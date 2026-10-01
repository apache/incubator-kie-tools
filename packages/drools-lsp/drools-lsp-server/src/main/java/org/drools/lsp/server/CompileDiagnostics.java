/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.drools.lsp.server;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import org.drools.completion.DRLDiagnosticHelper;
import org.drools.completion.WorkspaceScan;
import org.drools.completion.WorkspaceSiblingResolvers;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.DiagnosticSeverity;
import org.eclipse.lsp4j.MessageType;
import org.eclipse.lsp4j.Range;

/**
 * Compiles a saved DRL file together with its group through the project's own
 * engine and keeps the engine's messages per file until the file is edited,
 * saved again or regrouped. One compile runs at a time; a save during a compile
 * queues one more and the latest wins.
 */
final class CompileDiagnostics {

    static final String SOURCE = "drools";
    static final String KFS_PREFIX = "src/main/resources/";
    static final int MAX_DIAGNOSTICS_PER_FILE = 500;
    static final String NOT_BUILT =
            "Compile diagnostics need the project's compiled classes. Build the project, then save again.";

    private static final Pattern NOISY_CASCADE = Pattern.compile(
            "(?i)(unknown\\s*type|cannot\\s*resolve\\s*type|unresolved\\s*type|unable\\s*to\\s*resolve\\s*type|class\\s*not\\s*found)");
    private static final Logger logger = Logger.getLogger(CompileDiagnostics.class.getName());

    private final DroolsLspServer server;
    private final Function<Path, String> openText;
    private final Map<Path, List<Diagnostic>> cached = new ConcurrentHashMap<>();
    private final AtomicBoolean buildInFlight = new AtomicBoolean(false);
    private final AtomicReference<PendingBuild> pending = new AtomicReference<>();
    private final ExecutorService gateExecutor = Executors.newSingleThreadExecutor(daemon("drools-lsp-compile-gate"));
    private final ExecutorService buildExecutor = Executors.newSingleThreadExecutor(daemon("drools-lsp-compile"));
    private volatile boolean onSave = true;
    private final AtomicReference<Engine> engine = new AtomicReference<>();
    private final AtomicBoolean notBuiltNoticeShown = new AtomicBoolean(false);

    CompileDiagnostics(DroolsLspServer server, Function<Path, String> openText) {
        this.server = server;
        this.openText = openText;
    }

    boolean onSave() {
        return onSave;
    }

    void setOnSave(boolean onSave) {
        this.onSave = onSave;
    }

    Engine engine() {
        return engine.get();
    }

    /** Installs the engine; the previous one is closed once the build in flight, if any, has finished. */
    synchronized void setEngine(Engine next) {
        if (buildExecutor.isShutdown()) {
            next.close();
            return;
        }
        Engine previous = engine.getAndSet(next);
        if (previous == null || previous.available() != next.available()) {
            logger.info(next.available()
                    ? "Drools engine found on the project classpath; compile diagnostics are on"
                    : "No usable Drools engine on the project classpath (needs drools-compiler and drools-mvel); "
                            + "compile diagnostics are off");
        }
        if (previous != null && previous != next) {
            buildExecutor.submit(previous::close);
        }
    }

    /** Releases the engine and stops both executors; the close runs after any build in flight. */
    synchronized void shutdown() {
        Engine current = engine.getAndSet(null);
        if (current != null) {
            buildExecutor.submit(current::close);
        }
        gateExecutor.shutdown();
        buildExecutor.shutdown();
    }

    List<Diagnostic> cachedFor(Path path) {
        return path == null ? List.of() : cached.getOrDefault(normalize(path), List.of());
    }

    void invalidate(Path path) {
        if (path != null) {
            cached.remove(normalize(path));
        }
    }

    void invalidateAll() {
        cached.clear();
    }

    void onDidSave(Path path, String text) {
        if (!onSave || path == null || text == null) {
            return;
        }
        trigger(path, text, false);
    }

    void rebuildWorkspace(Path path) {
        if (path == null) {
            return;
        }
        String text = openText.apply(normalize(path));
        if (text == null) {
            text = readQuietly(path);
        }
        if (text == null) {
            server.showMessage(MessageType.Warning, "Workspace compile skipped: cannot read " + path);
            return;
        }
        trigger(path, text, true);
    }

    private void trigger(Path path, String text, boolean fullScope) {
        gateExecutor.submit(() -> triggerNow(path, text, fullScope));
    }

    void triggerNow(Path path, String text, boolean fullScope) {
        Engine current = engine.get();
        if (current == null || !current.available()) {
            logger.fine(() -> "No Drools engine on the project classpath; not compiling " + path);
            if (fullScope) {
                server.showMessage(MessageType.Warning, "Workspace compile skipped: no Drools engine on the project classpath");
            }
            return;
        }
        if (server.projectClassesMissing()) {
            if (notBuiltNoticeShown.compareAndSet(false, true)) {
                logger.info(NOT_BUILT);
                server.showMessage(MessageType.Info, NOT_BUILT);
            }
            if (fullScope) {
                server.showMessage(MessageType.Warning, "Workspace compile skipped: the project has not been built");
            }
            return;
        }
        notBuiltNoticeShown.set(false);
        if (!fullScope && hasErrors(DRLDiagnosticHelper.parse(text).diagnostics)) {
            logger.info("Compile skipped, " + path + " has syntax errors");
            return;
        }
        if (!buildInFlight.compareAndSet(false, true)) {
            pending.accumulateAndGet(new PendingBuild(path, text, fullScope),
                    (old, next) -> old != null && old.fullScope && !next.fullScope
                            ? new PendingBuild(next.path, next.text, true)
                            : next);
            drainPendingIfUnclaimed();
            return;
        }
        submit(path, text, fullScope);
    }

    private void drainPendingIfUnclaimed() {
        while (pending.get() != null) {
            if (!buildInFlight.compareAndSet(false, true)) {
                return;
            }
            PendingBuild queued = pending.getAndSet(null);
            if (queued != null) {
                submit(queued.path, queued.text, queued.fullScope);
                return;
            }
            buildInFlight.set(false);
        }
    }

    private void submit(Path path, String text, boolean fullScope) {
        buildExecutor.submit(() -> {
            Engine target = engine.get();
            boolean updated = false;
            boolean ran = false;
            boolean noEngine = false;
            int errors = 0;
            int warnings = 0;
            Compilation result = null;
            try {
                if (target == null || !target.available()) {
                    noEngine = true;
                    logger.fine(() -> "No Drools engine on the project classpath; not compiling " + path);
                    return;
                }
                if (fullScope) {
                    server.showMessage(MessageType.Info, "Compiling every DRL file in the workspace");
                }
                result = compile(normalize(path), text, fullScope, target);
                updated = commit(result);
                errors = result.count(DiagnosticSeverity.Error);
                warnings = result.count(DiagnosticSeverity.Warning);
                ran = true;
            } catch (Throwable t) {
                logger.log(Level.WARNING, "Drools compile failed for " + path, t);
                result = failure(normalize(path), text, t);
                updated = commit(result);
                errors = 1;
                ran = true;
            } finally {
                if (updated) {
                    server.refreshDiagnostics();
                }
                if (fullScope) {
                    if (noEngine) {
                        server.showMessage(MessageType.Warning,
                                "Workspace compile skipped: no Drools engine on the project classpath");
                    } else if (ran) {
                        server.showMessage(errors > 0 ? MessageType.Warning : MessageType.Info,
                                summaryMessage(errors, warnings, result));
                    }
                }
                buildInFlight.set(false);
                PendingBuild queued = pending.getAndSet(null);
                if (queued != null) {
                    trigger(queued.path, queued.text, queued.fullScope);
                }
            }
        });
    }

    private static String summaryMessage(int errors, int warnings, Compilation result) {
        String base = "Workspace compile finished: " + errors + " error(s), " + warnings + " warning(s)";
        if (errors == 0 || result == null) {
            return base;
        }
        return base + " in " + joinFileNames(result.filesWithErrors());
    }

    private static String joinFileNames(List<String> files) {
        StringBuilder names = new StringBuilder();
        int shown = Math.min(3, files.size());
        for (int i = 0; i < shown; i++) {
            if (i > 0) {
                names.append(", ");
            }
            names.append(files.get(i));
        }
        if (files.size() > 3) {
            names.append(" and ").append(files.size() - 3).append(" more");
        }
        return names.toString();
    }

    private Compilation compile(Path saved, String text, boolean fullScope, Engine target) throws Exception {
        Path root = rootFor(saved);
        Map<Path, String> sources = new LinkedHashMap<>();
        Set<Path> skipped = new LinkedHashSet<>();
        for (Path sibling : scope(saved, fullScope)) {
            Path normalized = normalize(sibling);
            if (normalized.equals(saved)) {
                continue;
            }
            String siblingText = openText.apply(normalized);
            if (siblingText == null) {
                siblingText = readQuietly(normalized);
            }
            if (siblingText == null) {
                skipped.add(normalized);
                continue;
            }
            if (hasErrors(DRLDiagnosticHelper.parse(siblingText).diagnostics)) {
                logger.fine(() -> "Leaving " + normalized + " out of the compile, it has syntax errors");
                skipped.add(normalized);
                continue;
            }
            sources.put(normalized, siblingText);
        }
        if (fullScope && hasErrors(DRLDiagnosticHelper.parse(text).diagnostics)) {
            logger.fine(() -> "Leaving " + saved + " out of the compile, it has syntax errors");
            skipped.add(saved);
        } else {
            sources.put(saved, text);
        }

        Map<String, Path> fileByKfsPath = new LinkedHashMap<>();
        Map<String, String> drlByKfsPath = new LinkedHashMap<>();
        for (Map.Entry<Path, String> entry : sources.entrySet()) {
            String kfsPath = kfsPathFor(root, entry.getKey(), fileByKfsPath.size());
            fileByKfsPath.put(kfsPath, entry.getKey());
            drlByKfsPath.put(kfsPath, entry.getValue());
        }
        logger.info(() -> "Compiling " + sources.size() + " DRL file(s) for " + saved
                + (fullScope ? " (whole workspace)" : ""));

        List<Map<String, Object>> messages = target.build(drlByKfsPath);

        Map<Path, List<Diagnostic>> perFile = new LinkedHashMap<>();
        for (Path file : sources.keySet()) {
            perFile.put(file, new ArrayList<>());
        }
        Path fallback = sources.containsKey(saved) ? saved : sources.keySet().stream().findFirst().orElse(null);
        if (fallback != null) {
            for (Map<String, Object> m : messages) {
                Path file = fileFor(m.get("path"), fileByKfsPath, fallback);
                List<Diagnostic> bucket = perFile.get(file);
                if (bucket == null) {
                    file = fallback;
                    bucket = perFile.get(fallback);
                }
                String messageText = m.get("text") == null ? "" : String.valueOf(m.get("text"));
                Diagnostic d = new Diagnostic();
                d.setSeverity(severityOf(m.get("level")));
                d.setSource(SOURCE);
                d.setMessage(messageText);
                d.setRange(BuildMessageRanges.rangeFor(messageText, intOf(m.get("line")), intOf(m.get("column")),
                        sources.get(file)));
                bucket.add(d);
            }
        }
        perFile.replaceAll((file, list) -> cap(dedupe(dropCascades(list))));
        return new Compilation(sources, perFile, skipped);
    }

    private Compilation failure(Path current, String text, Throwable t) {
        Diagnostic d = new Diagnostic();
        d.setSource(SOURCE);
        String missing = missingClass(t);
        if (missing != null) {
            d.setSeverity(DiagnosticSeverity.Warning);
            d.setMessage("The Drools compile needs the project's compiled classes: " + missing
                    + " is not on the classpath. Build the project, then save again.");
        } else {
            d.setSeverity(DiagnosticSeverity.Error);
            d.setMessage("Drools compile failed: " + summarize(t));
        }
        d.setRange(BuildMessageRanges.firstRuleRange(text));
        Map<Path, List<Diagnostic>> perFile = new LinkedHashMap<>();
        perFile.put(current, List.of(d));
        return new Compilation(Map.of(current, text), perFile, Set.of());
    }

    private boolean commit(Compilation result) {
        boolean[] updated = {false};
        result.perFile.forEach((file, list) -> {
            String compiled = result.sources.get(file);
            cached.compute(file, (k, previous) -> {
                String now = openText.apply(file);
                if (now != null && !now.equals(compiled)) {
                    return previous;
                }
                updated[0] = true;
                return list.isEmpty() ? null : List.copyOf(list);
            });
        });
        for (Path file : result.skipped()) {
            if (cached.remove(file) != null) {
                updated[0] = true;
            }
        }
        return updated[0];
    }

    private List<Path> scope(Path current, boolean fullScope) {
        List<Path> files;
        if (!fullScope) {
            files = WorkspaceSiblingResolvers.active().resolveSiblings(current);
        } else {
            files = WorkspaceSiblingResolvers.active().workspaceDrlFiles();
            if (files.isEmpty()) {
                files = WorkspaceScan.of(rootFor(current)).drlFiles();
            }
        }
        return withoutBuildOutput(files);
    }

    /** Build output holds copies of the rules; compiling them next to their sources only duplicates every rule. */
    private List<Path> withoutBuildOutput(List<Path> files) {
        List<Path> outputDirs = new ArrayList<>();
        for (Path dir : server.getBuildOutputDirs()) {
            outputDirs.add(normalize(dir));
        }
        if (outputDirs.isEmpty()) {
            return files;
        }
        List<Path> kept = new ArrayList<>(files.size());
        for (Path file : files) {
            Path normalized = normalize(file);
            if (outputDirs.stream().noneMatch(normalized::startsWith)) {
                kept.add(file);
            }
        }
        return kept;
    }

    private Path rootFor(Path current) {
        Path root = server.workspaceRoot();
        return root != null ? normalize(root) : current.getParent();
    }

    static String kfsPathFor(Path root, Path file, int index) {
        Path abs = normalize(file);
        if (root != null) {
            Path r = normalize(root);
            if (abs.startsWith(r)) {
                return KFS_PREFIX + r.relativize(abs).toString().replace('\\', '/');
            }
        }
        return KFS_PREFIX + "external/" + index + "/" + abs.getFileName();
    }

    private static Path fileFor(Object pathValue, Map<String, Path> fileByKfsPath, Path fallback) {
        if (pathValue == null) {
            return fallback;
        }
        String p = String.valueOf(pathValue).replace('\\', '/');
        if (p.isBlank()) {
            return fallback;
        }
        Path exact = fileByKfsPath.get(p);
        if (exact != null) {
            return exact;
        }
        for (Map.Entry<String, Path> entry : fileByKfsPath.entrySet()) {
            String key = entry.getKey();
            if (p.endsWith("/" + key) || key.endsWith("/" + p) || p.equals(key)) {
                return entry.getValue();
            }
        }
        return fallback;
    }

    static List<Diagnostic> merge(List<Diagnostic> fast, List<Diagnostic> compiled) {
        if (compiled == null || compiled.isEmpty()) {
            return fast == null ? List.of() : fast;
        }
        List<Diagnostic> merged = new ArrayList<>();
        Set<String> seen = new HashSet<>();
        if (fast != null) {
            for (Diagnostic d : fast) {
                merged.add(d);
                seen.add(mergeKey(d));
            }
        }
        for (Diagnostic d : compiled) {
            if (seen.add(mergeKey(d))) {
                merged.add(d);
            }
        }
        return merged;
    }

    private static String mergeKey(Diagnostic d) {
        Range r = d.getRange();
        String where = r == null ? "" : r.getStart().getLine() + ":" + r.getStart().getCharacter() + "-"
                + r.getEnd().getLine() + ":" + r.getEnd().getCharacter();
        return where + "|" + d.getSeverity() + "|" + d.getMessage();
    }

    private static List<Diagnostic> dropCascades(List<Diagnostic> in) {
        boolean hasRootError = in.stream().anyMatch(d -> d.getSeverity() == DiagnosticSeverity.Error
                && (d.getMessage() == null || !NOISY_CASCADE.matcher(d.getMessage()).find()));
        if (!hasRootError) {
            return in;
        }
        List<Diagnostic> out = new ArrayList<>(in.size());
        for (Diagnostic d : in) {
            if (d.getMessage() == null || !NOISY_CASCADE.matcher(d.getMessage()).find()) {
                out.add(d);
            }
        }
        return out;
    }

    private static List<Diagnostic> dedupe(List<Diagnostic> in) {
        Map<String, Diagnostic> unique = new LinkedHashMap<>();
        for (Diagnostic d : in) {
            unique.putIfAbsent(key(d), d);
        }
        return new ArrayList<>(unique.values());
    }

    private static List<Diagnostic> cap(List<Diagnostic> in) {
        return in.size() <= MAX_DIAGNOSTICS_PER_FILE ? in : new ArrayList<>(in.subList(0, MAX_DIAGNOSTICS_PER_FILE));
    }

    private static String key(Diagnostic d) {
        Range r = d.getRange();
        String where = r == null ? "" : r.getStart().getLine() + ":" + r.getStart().getCharacter() + "-"
                + r.getEnd().getLine() + ":" + r.getEnd().getCharacter();
        return where + "|" + d.getSeverity() + "|" + d.getSource() + "|" + d.getMessage();
    }

    private static DiagnosticSeverity severityOf(Object level) {
        String name = level == null ? "" : String.valueOf(level);
        switch (name) {
            case "ERROR":
                return DiagnosticSeverity.Error;
            case "INFO":
                return DiagnosticSeverity.Information;
            default:
                return DiagnosticSeverity.Warning;
        }
    }

    private static int intOf(Object value) {
        return value instanceof Number number ? number.intValue() : 0;
    }

    private static boolean hasErrors(List<Diagnostic> diagnostics) {
        if (diagnostics == null) {
            return false;
        }
        for (Diagnostic d : diagnostics) {
            if (d != null && d.getSeverity() == DiagnosticSeverity.Error) {
                return true;
            }
        }
        return false;
    }

    /**
     * The class the engine could not load, when that is why the build threw. The
     * parallel rule build re-creates the worker's error without its message, so
     * the name is only found further down the cause chain.
     */
    static String missingClass(Throwable t) {
        String linkage = null;
        int depth = 0;
        for (Throwable cur = t; cur != null && depth < 16; cur = cur.getCause(), depth++) {
            if (cur instanceof ClassNotFoundException && cur.getMessage() != null) {
                return cur.getMessage();
            }
            if (linkage == null && cur instanceof NoClassDefFoundError && cur.getMessage() != null) {
                linkage = cur.getMessage().replace('/', '.');
            }
        }
        return linkage;
    }

    static String summarize(Throwable t) {
        StringBuilder sb = new StringBuilder();
        String last = null;
        int depth = 0;
        for (Throwable cur = t; cur != null && depth < 8; cur = cur.getCause(), depth++) {
            String msg = cur.getMessage();
            String part = msg != null && !msg.isBlank() ? msg.trim() : cur.getClass().getSimpleName();
            if (last == null || !(part.equals(last) || last.contains(part))) {
                if (sb.length() > 0) {
                    sb.append(" <- ");
                }
                sb.append(part);
            }
            last = part;
        }
        return sb.toString();
    }

    private static String readQuietly(Path path) {
        try {
            return Files.readString(path);
        } catch (IOException e) {
            return null;
        }
    }

    private static Path normalize(Path path) {
        return path.toAbsolutePath().normalize();
    }

    private static ThreadFactory daemon(String name) {
        return runnable -> {
            Thread t = new Thread(runnable, name);
            t.setDaemon(true);
            return t;
        };
    }

    private record PendingBuild(Path path, String text, boolean fullScope) {
    }

    private record Compilation(Map<Path, String> sources, Map<Path, List<Diagnostic>> perFile, Set<Path> skipped) {
        int count(DiagnosticSeverity severity) {
            int n = 0;
            for (List<Diagnostic> list : perFile.values()) {
                for (Diagnostic d : list) {
                    if (Objects.equals(d.getSeverity(), severity)) {
                        n++;
                    }
                }
            }
            return n;
        }

        List<String> filesWithErrors() {
            List<String> names = new ArrayList<>();
            for (Map.Entry<Path, List<Diagnostic>> entry : perFile.entrySet()) {
                boolean hasError = entry.getValue().stream()
                        .anyMatch(d -> d.getSeverity() == DiagnosticSeverity.Error);
                if (hasError) {
                    names.add(entry.getKey().getFileName().toString());
                }
            }
            return names;
        }
    }
}
