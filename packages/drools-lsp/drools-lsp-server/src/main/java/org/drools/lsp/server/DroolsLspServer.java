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

import java.io.File;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.drools.completion.ClassIndex;
import org.drools.completion.ClassMemberIndex;
import org.drools.completion.DRLDeclaredTypeParser;
import org.drools.completion.JavaSourceRoots;
import org.drools.completion.JavaSourceTypeIndex;
import org.drools.completion.WorkspaceSiblingResolver;
import org.drools.completion.WorkspaceSiblingResolvers;
import org.eclipse.lsp4j.jsonrpc.Endpoint;
import org.eclipse.lsp4j.jsonrpc.services.JsonNotification;
import org.eclipse.lsp4j.jsonrpc.services.JsonRequest;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.eclipse.lsp4j.CodeLensOptions;
import org.eclipse.lsp4j.CompletionOptions;
import org.eclipse.lsp4j.DiagnosticRegistrationOptions;
import org.eclipse.lsp4j.InitializeParams;
import org.eclipse.lsp4j.InitializeResult;
import org.eclipse.lsp4j.RenameOptions;
import org.eclipse.lsp4j.ServerCapabilities;
import org.eclipse.lsp4j.SetTraceParams;
import org.eclipse.lsp4j.TextDocumentSyncKind;
import org.eclipse.lsp4j.services.LanguageClient;
import org.eclipse.lsp4j.services.LanguageClientAware;
import org.eclipse.lsp4j.services.LanguageServer;
import org.eclipse.lsp4j.services.WorkspaceService;

public class DroolsLspServer implements LanguageServer, LanguageClientAware {

    private static final Logger logger = Logger.getLogger(DroolsLspServer.class.getName());

    private final DroolsLspDocumentService textService;
    private final WorkspaceService workspaceService;

    private LanguageClient client;
    private volatile Set<Path> classpathEntries = Set.of();
    private volatile Set<Path> buildOutputDirs = Set.of();
    private volatile ClassIndex jarClassIndex = ClassIndex.empty();
    private volatile ClassMemberIndex classMemberIndex = ClassMemberIndex.empty();
    private volatile JavaSourceTypeIndex javaSourceIndex = JavaSourceTypeIndex.empty();

    /** The LSP workspace root, once {@code initialize} has parsed a usable {@code rootUri}. */
    private volatile Path workspaceRootPath;

    /** {@code drools.lsp.java.sourcePaths}, parsed once at {@code initialize}. */
    private volatile List<String> javaSourcePathsSetting = List.of();

    /** {@code drools.lsp.java.packageFilters}, parsed once at {@code initialize}. */
    private volatile List<String> javaPackageFiltersSetting = List.of();

    /** Tracks whether {@code shutdown} preceded {@code exit} (LSP spec). */
    private volatile boolean shutdownReceived = false;

    public DroolsLspServer() {
        textService = new DroolsLspDocumentService(this);
        workspaceService = new DroolsLspWorkspaceService(this);
    }


    @Override
    public void connect(LanguageClient client) {
        this.client = client;
    }

    public LanguageClient getClient() {
        return client;
    }

    public Set<Path> getClasspathEntries() {
        return classpathEntries;
    }

    public Set<Path> getBuildOutputDirs() {
        return buildOutputDirs;
    }

    public void rebuildClassIndex() {
        try {
            // Refreshed inside the try: an escaped exception here (e.g. an
            // unreadable source root) must not silently abort the rebuild
            // with no log — the debounce executor never inspects the
            // resulting future.
            if (workspaceRootPath != null) {
                refreshJavaSourceIndex(workspaceRootPath);
            }
            Set<Path> dirs = buildOutputDirs;
            if (dirs.isEmpty() && jarClassIndex.size() == 0 && javaSourceIndex.classNames().isEmpty()) {
                return;
            }
            publishClassIndex();
            // Fresh loader so recompiled classes aren't served from the old one's cache.
            swapMemberIndex(ClassMemberIndex.of(classpathEntries));
            // Drop the declared-type parse cache so edited sibling files re-parse
            // and the cache doesn't grow unbounded over the server's lifetime.
            DRLDeclaredTypeParser.clearCache();
        } catch (Exception e) {
            logger.log(Level.WARNING, "Failed to rebuild class index", e);
        }
    }

    /**
     * Recomputes {@link #javaSourceIndex} from {@code root} using the
     * current {@link #javaSourcePathsSetting}/{@link #javaPackageFiltersSetting}.
     * The single choke point for the discover+build pair — every site that
     * (re)builds the source index (initialize's fast path,
     * {@link #rebuildClassIndex}, the test hook) calls this rather than
     * repeating it inline.
     */
    private void refreshJavaSourceIndex(Path root) {
        List<Path> roots = JavaSourceRoots.discover(root, javaSourcePathsSetting);
        javaSourceIndex = JavaSourceTypeIndex.build(new LinkedHashSet<>(roots), javaPackageFiltersSetting);
    }

    /**
     * Publishes the merged class index — compiled classpath classes, this
     * workspace's own build output, and Java-source-derived type names —
     * to the document service. The single choke point for every publish
     * site (initialize's fast/post-mvn phases, {@link #rebuildClassIndex}),
     * so source names are never missing from one path but not another.
     * Nudges the client to re-pull diagnostics afterward (same pull-model
     * nudge the build-diagnostics tier uses) so unknown-type squiggles
     * against a type this publish just resolved clear promptly rather than
     * waiting for the next edit/save.
     */
    private void publishClassIndex() {
        ClassIndex outputIndex = ClassIndex.build(buildOutputDirs);
        ClassIndex merged = ClassIndex.merge(ClassIndex.merge(jarClassIndex, outputIndex),
                ClassIndex.of(javaSourceIndex.classNames()));
        textService.setClassIndex(merged);
        textService.setJavaSourceIndex(javaSourceIndex);
        if (client != null) {
            try {
                // Pull model: nudge the client to re-request diagnostics, same
                // as the build-diagnostics tier's refresh — some clients (and
                // test doubles) don't implement this LSP4J capability method.
                client.refreshDiagnostics();
            } catch (Exception e) {
                logger.log(Level.FINE, "Failed to refresh diagnostics after publishing class index", e);
            }
        }
    }

    private void setResolvedClasspath(Set<Path> entries) {
        this.classpathEntries = entries;
        this.buildOutputDirs = filterDirectories(entries);
        Set<Path> jars = filterJars(entries);
        this.jarClassIndex = jars.isEmpty() ? ClassIndex.empty() : ClassIndex.build(jars);
        // Member lookup reflects over the full classpath (jars + class dirs)
        // lazily — building the index itself loads no classes.
        swapMemberIndex(ClassMemberIndex.of(entries));

        if (entries.isEmpty()) {
            logger.warning("Classpath resolution returned 0 entries — type member hover "
                    + "and field completion will not be available. Ensure "
                    + "'mvn dependency:build-classpath' succeeds in the workspace root.");
        } else {
            logger.fine(() -> "Classpath resolved: " + entries.size() + " entries ("
                    + jars.size() + " JARs, " + buildOutputDirs.size() + " class dirs)");
        }
    }

    private synchronized void swapMemberIndex(ClassMemberIndex next) {
        ClassMemberIndex previous = this.classMemberIndex;
        this.classMemberIndex = next;
        next.setSourceFallback(javaSourceIndex);
        textService.setClassMemberIndex(next);
        if (previous != next) {
            try {
                previous.close();
            } catch (Exception e) {
                logger.log(Level.FINE, "Failed to close previous class member index", e);
            }
        }
    }

    void setClasspathEntriesForTest(Set<Path> entries) {
        setResolvedClasspath(entries);
    }

    /**
     * Test-only entry point: runs the same fast-path source-typing steps
     * {@code initialize} runs before the mvn phase (re-read the
     * {@code drools.lsp.java.*} settings, refresh the source index via
     * {@link #refreshJavaSourceIndex}, install it as the member-index
     * fallback, publish the merged class index) without the async plumbing
     * or a real LSP client — so a test exercising the settings path doesn't
     * need to duplicate {@code initialize}'s sys-prop parsing.
     */
    void initializeJavaSourceTypingForTest(Path workspaceRoot) {
        this.workspaceRootPath = workspaceRoot;
        this.javaSourcePathsSetting = parseSemicolonSetting(
                System.getProperty("drools.lsp.java.sourcePaths"));
        this.javaPackageFiltersSetting = parseSemicolonSetting(
                System.getProperty("drools.lsp.java.packageFilters"));
        refreshJavaSourceIndex(workspaceRoot);
        swapMemberIndex(ClassMemberIndex.of(classpathEntries));
        publishClassIndex();
    }

    /**
     * Splits a {@code drools.lsp.java.*} system property on {@code ;},
     * trimming and dropping empty entries. {@code null}/blank yields an
     * empty list.
     */
    private static List<String> parseSemicolonSetting(String raw) {
        if (raw == null || raw.isBlank()) {
            return List.of();
        }
        List<String> out = new ArrayList<>();
        for (String part : raw.split(";")) {
            String trimmed = part.trim();
            if (!trimmed.isEmpty()) {
                out.add(trimmed);
            }
        }
        return out;
    }

    private static Set<Path> filterDirectories(Set<Path> entries) {
        Set<Path> dirs = new LinkedHashSet<>();
        for (Path entry : entries) {
            if (Files.isDirectory(entry)) {
                dirs.add(entry);
            }
        }
        return dirs;
    }

    private static Set<Path> filterJars(Set<Path> entries) {
        Set<Path> jars = new LinkedHashSet<>();
        for (Path entry : entries) {
            if (!Files.isDirectory(entry)) {
                jars.add(entry);
            }
        }
        return jars;
    }

    /**
     * Maps the {@code drools.lsp.maven.pomPath} setting to the Maven root
     * directories whose classpath should be resolved.
     *
     * <p>Each path-separator-delimited entry may point at a {@code pom.xml} file
     * or at the directory that contains one; relative entries are resolved
     * against {@code rootPath}. Entries that do not resolve to an existing
     * {@code pom.xml} are skipped with a warning rather than silently falling
     * back to scanning an unrelated parent tree.
     */
    static List<Path> resolveCustomMavenRoots(Path rootPath, String pomPathProp) {
        List<Path> roots = new ArrayList<>();
        for (String entry : pomPathProp.split(File.pathSeparator)) {
            String trimmed = entry.trim();
            if (trimmed.isEmpty()) {
                continue;
            }
            Path configured = Path.of(trimmed);
            if (!configured.isAbsolute()) {
                configured = rootPath.resolve(configured);
            }
            configured = configured.normalize();
            Path pomFile = Files.isDirectory(configured) ? configured.resolve("pom.xml") : configured;
            if (!Files.isRegularFile(pomFile)) {
                logger.warning("Configured drools.lsp.maven.pomPath does not point to an existing pom.xml, skipping: " + configured);
                continue;
            }
            roots.add(pomFile.getParent());
        }
        return roots;
    }

    @Override
    public CompletableFuture<InitializeResult> initialize(InitializeParams params) {
        final InitializeResult initializeResult = new InitializeResult(new ServerCapabilities());

        initializeResult.getCapabilities().setTextDocumentSync(TextDocumentSyncKind.Full);
        CompletionOptions completionOptions = new CompletionOptions();
        initializeResult.getCapabilities().setCompletionProvider(completionOptions);
        initializeResult.getCapabilities().setDefinitionProvider(true);
        initializeResult.getCapabilities().setReferencesProvider(true);
        initializeResult.getCapabilities().setRenameProvider(new RenameOptions(true));
        initializeResult.getCapabilities().setCodeLensProvider(new CodeLensOptions(false));
        initializeResult.getCapabilities().setHoverProvider(true);
        initializeResult.getCapabilities().setCodeActionProvider(true);
        initializeResult.getCapabilities().setInlayHintProvider(true);
        initializeResult.getCapabilities().setDocumentSymbolProvider(true);
        initializeResult.getCapabilities().setFoldingRangeProvider(true);
        initializeResult.getCapabilities().setDiagnosticProvider(
                new DiagnosticRegistrationOptions(false, false));
        initializeResult.getCapabilities().setTypeHierarchyProvider(true);

        final String rootUri = params.getRootUri();
        if (rootUri != null) {
            WorkspaceSiblingResolver resolver = WorkspaceSiblingResolvers.active();

            // Java source typing needs the root and its settings before either
            // async block runs: a rebuild triggered by a watch event reads them
            // to refresh the source index. Guarded, because a non-file rootUri
            // must not fail initialize.
            try {
                this.workspaceRootPath = Paths.get(URI.create(rootUri));
            } catch (Exception e) {
                logger.log(Level.WARNING, "Failed to read the workspace root for Java source typing", e);
            }
            this.javaSourcePathsSetting = parseSemicolonSetting(
                    System.getProperty("drools.lsp.java.sourcePaths"));
            this.javaPackageFiltersSetting = parseSemicolonSetting(
                    System.getProperty("drools.lsp.java.packageFilters"));

            // Applied before returning, so that a notification the client sends
            // once initialize completes is necessarily the newer value. Deferring
            // these would let a startup value captured here land afterwards and
            // overwrite it. Neither call scans: the workspace root is still
            // unset, so the reload each triggers resolves to nothing.
            String groupingSettings = groupingSettingsOf(params.getInitializationOptions());
            if (groupingSettings != null) {
                resolver.setSettingsConfig(groupingSettings);
            }
            List<Path> workspaceFiles = workspaceFilesOf(params.getInitializationOptions());
            if (workspaceFiles != null) {
                resolver.setWorkspaceFiles(workspaceFiles);
            }

            // Only the root is set asynchronously, because only it scans.
            CompletableFuture.runAsync(() -> {
                try {
                    resolver.setWorkspaceRoot(Paths.get(URI.create(rootUri)));
                } catch (Exception e) {
                    logger.log(Level.WARNING, "Failed to initialize DRL file grouping", e);
                } finally {
                    // Scanning a workspace takes long enough that a client asking
                    // once at startup can easily ask too early. Tell it when the
                    // answer is actually ready rather than leaving it stale.
                    notifyFileGroupsChanged();
                }
            });
            CompletableFuture.runAsync(() -> {
                try {
                    Path rootPath = Paths.get(URI.create(rootUri));

                    // Build the Java-source type index first. Like the build-output
                    // scan below, this only touches the filesystem (no mvn), so
                    // source-derived type and member data is available within
                    // milliseconds — the works-before-build case for a fresh
                    // checkout with no compiled output yet.
                    refreshJavaSourceIndex(rootPath);
                    // classpathEntries is still Set.of() here (setResolvedClasspath
                    // hasn't run yet) — of() yields a loader-less but
                    // fallback-capable index, so member data from sources works
                    // even before the classpath resolves below.
                    swapMemberIndex(ClassMemberIndex.of(classpathEntries));

                    // Resolve against the configured custom POM root(s) when set,
                    // otherwise the workspace root.
                    String pomPathProp = System.getProperty("drools.lsp.maven.pomPath");
                    List<Path> mavenRoots;
                    if (pomPathProp != null && !pomPathProp.isBlank()) {
                        mavenRoots = resolveCustomMavenRoots(rootPath, pomPathProp);
                        mavenRoots.forEach(root -> logger.fine(() -> "Using custom Maven root: " + root));
                    } else {
                        logger.fine(() -> "Resolving Maven classpath from: " + rootPath);
                        mavenRoots = List.of(rootPath);
                    }

                    // Publish the project's own compiled classes first. This only
                    // scans the filesystem (no mvn), so type-name completion is
                    // available within milliseconds rather than waiting on the
                    // dependency-JAR resolution below — which shells out to mvn and
                    // can take many seconds.
                    Set<Path> outputDirs = new LinkedHashSet<>();
                    for (Path mavenRoot : mavenRoots) {
                        outputDirs.addAll(MavenClasspathResolver.resolveBuildOutputDirs(mavenRoot));
                    }
                    buildOutputDirs = outputDirs;
                    publishClassIndex();

                    // Then resolve the full classpath (dependency JARs via mvn) and
                    // merge, so member hover and field completion over dependencies
                    // become available too.
                    Set<Path> resolved = new LinkedHashSet<>();
                    for (Path mavenRoot : mavenRoots) {
                        resolved.addAll(MavenClasspathResolver.resolve(mavenRoot));
                    }
                    setResolvedClasspath(resolved);
                    publishClassIndex();
                } catch (Exception e) {
                    logger.log(Level.WARNING, "Failed to build class index at startup", e);
                }
            });
        }

        return CompletableFuture.supplyAsync(() -> initializeResult);
    }

    /**
     * Returns the workspace's DRL file groups, keyed by name, so a client can
     * show which group the open file is in and offer the rest.
     *
     * <p>The client asks rather than reading the config files itself: the server
     * already resolves kmodule descriptors, config files and adopted manifests,
     * and a second implementation of that in the client is a second place for it
     * to be wrong.
     */
    @JsonRequest("drools/fileGroups")
    public CompletableFuture<Map<String, FileGroupingProtocol.FileGroup>> fileGroups() {
        return CompletableFuture.supplyAsync(() -> {
            Map<String, FileGroupingProtocol.FileGroup> groups = new LinkedHashMap<>();
            WorkspaceSiblingResolvers.active().resolveAllGroups().forEach((name, group) -> {
                List<String> uris = new ArrayList<>(group.files().size());
                for (Path file : group.files()) {
                    uris.add(file.toUri().toString());
                }
                Path declaredIn = group.declaredIn();
                groups.put(name, new FileGroupingProtocol.FileGroup(uris, group.kind(),
                        declaredIn == null ? null : declaredIn.toUri().toString()));
            });
            return groups;
        });
    }

    /**
     * Pins a document to a named group, overriding what the configuration
     * resolves it to. A file can belong to several groups, so this is how the
     * user settles which one the editor works in.
     */
    @JsonNotification("drools/setFileGroup")
    public void setFileGroup(FileGroupingProtocol.FileGroupParams params) {
        if (params == null || params.getUri() == null) {
            return;
        }
        try {
            WorkspaceSiblingResolvers.active()
                    .setGroupOverride(Paths.get(URI.create(params.getUri())), params.getGroup());
            // Pinning changes what is in scope, and diagnostics here are pulled
            // rather than pushed, so nothing would re-ask on its own.
            refreshDiagnostics();
        } catch (Exception e) {
            logger.log(Level.WARNING, "Failed to pin " + params.getUri() + " to a DRL file group", e);
        }
    }

    /** Re-reads the workspace's grouping configuration after a config file changes. */
    @JsonNotification("drools/reloadFileGroups")
    public void reloadFileGroups() {
        WorkspaceSiblingResolvers.active().reload();
        notifyFileGroupsChanged();
    }

    /**
     * Replaces the grouping declared in the editor's settings, so a user editing
     * {@code drools.lsp.grouping} sees the effect without restarting the server.
     */
    @JsonNotification("drools/setGroupingConfig")
    public void setGroupingConfig(FileGroupingProtocol.GroupingConfigParams params) {
        JsonObject config = (params == null) ? null : params.getConfig();
        WorkspaceSiblingResolvers.active().setSettingsConfig(config == null ? null : config.toString());
        notifyFileGroupsChanged();
    }

    /**
     * Tells the client the group map changed, so it can re-read it.
     *
     * <p>Sent through the raw endpoint because this is a custom method the
     * {@link LanguageClient} interface does not declare. A client that is not an
     * lsp4j proxy — a test double, say — simply does not get told.
     */
    private void notifyFileGroupsChanged() {
        if (client instanceof Endpoint endpoint) {
            endpoint.notify("drools/fileGroupsChanged", null);
        }
        refreshDiagnostics();
    }

    /**
     * Asks the client to re-request diagnostics for its open documents.
     *
     * <p>Needed because this server publishes diagnostics on pull, not push: when
     * grouping changes, the set of files in scope changes with it, but nothing
     * would prompt the client to ask again.
     */
    private void refreshDiagnostics() {
        LanguageClient target = client;
        if (target == null) {
            return;
        }
        try {
            target.refreshDiagnostics();
        } catch (Exception e) {
            logger.log(Level.FINE, "Client did not accept a diagnostics refresh", e);
        }
    }

    /**
     * Replaces the client's view of which files the workspace contains, after it
     * has seen one created, deleted or renamed.
     */
    @JsonNotification("drools/setWorkspaceFiles")
    public void setWorkspaceFiles(FileGroupingProtocol.WorkspaceFilesParams params) {
        List<String> uris = (params == null) ? null : params.getUris();
        WorkspaceSiblingResolvers.active().setWorkspaceFiles(uris == null ? null : toPaths(uris));
        notifyFileGroupsChanged();
    }

    /**
     * Pulls {@code workspaceFiles} out of the client's
     * {@code initializationOptions}. Absent means the client is not enumerating,
     * and the resolver should discover files itself.
     */
    private static List<Path> workspaceFilesOf(Object initializationOptions) {
        if (!(initializationOptions instanceof JsonObject options)) {
            return null;
        }
        JsonElement files = options.get("workspaceFiles");
        if (files == null || !files.isJsonArray()) {
            return null;
        }
        List<String> uris = new ArrayList<>();
        for (JsonElement uri : files.getAsJsonArray()) {
            if (uri.isJsonPrimitive()) {
                uris.add(uri.getAsString());
            }
        }
        return toPaths(uris);
    }

    /** Converts document URIs to paths, dropping any the platform cannot represent. */
    private static List<Path> toPaths(List<String> uris) {
        List<Path> paths = new ArrayList<>(uris.size());
        for (String uri : uris) {
            try {
                paths.add(Paths.get(URI.create(uri)));
            } catch (Exception e) {
                logger.log(Level.FINE, "Ignoring unusable workspace file URI: " + uri, e);
            }
        }
        return paths;
    }

    /**
     * Pulls {@code grouping} out of the client's {@code initializationOptions},
     * as raw JSON. Delivered this way rather than as a JVM argument because the
     * setting is a structured object, and JSON on a command line runs into
     * quoting rules and length limits that differ per platform.
     */
    private static String groupingSettingsOf(Object initializationOptions) {
        if (!(initializationOptions instanceof JsonObject options)) {
            return null;
        }
        JsonElement grouping = options.get("grouping");
        // Only an object is meaningful; toString() on anything else would yield
        // a quoted literal that no longer parses as the config.
        return (grouping == null || !grouping.isJsonObject()) ? null : grouping.toString();
    }

    @Override
    public void setTrace(SetTraceParams params) {
        // No-op: this server emits no LSP trace notifications. Overriding avoids
        // the LanguageServer default, which throws UnsupportedOperationException
        // when the client sends "$/setTrace" (vscode-languageclient v8+ does).
    }

    @Override
    public CompletableFuture<Object> shutdown() {
        shutdownReceived = true;
        try {
            classMemberIndex.close();
        } catch (Exception e) {
            logger.log(Level.FINE, "Failed to close class member index on shutdown", e);
        }
        DRLDeclaredTypeParser.clearCache();
        JavaSourceTypeIndex.clearCache();
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public void exit() {
        // LSP spec: exit code 0 only when a shutdown request was received first.
        System.exit(shutdownReceived ? 0 : 1);
    }

    @Override
    public DroolsLspDocumentService getTextDocumentService() {
        return textService;
    }

    @Override
    public WorkspaceService getWorkspaceService() {
        return workspaceService;
    }
}
