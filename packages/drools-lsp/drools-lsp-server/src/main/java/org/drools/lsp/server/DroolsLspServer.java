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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.drools.completion.ClassIndex;
import org.drools.completion.ClassMemberIndex;
import org.drools.completion.DRLDeclaredTypeParser;
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
        Set<Path> dirs = buildOutputDirs;
        if (dirs.isEmpty() && jarClassIndex.size() == 0) {
            return;
        }
        try {
            ClassIndex outputIndex = ClassIndex.build(dirs);
            textService.setClassIndex(ClassIndex.merge(jarClassIndex, outputIndex));
            // Fresh loader so recompiled classes aren't served from the old one's cache.
            swapMemberIndex(ClassMemberIndex.of(classpathEntries));
            // Drop the declared-type parse cache so edited sibling files re-parse
            // and the cache doesn't grow unbounded over the server's lifetime.
            DRLDeclaredTypeParser.clearCache();
        } catch (Exception e) {
            logger.log(Level.WARNING, "Failed to rebuild class index", e);
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

        String rootUri = params.getRootUri();
        if (rootUri != null) {
            CompletableFuture.runAsync(() -> {
                try {
                    Path rootPath = Paths.get(URI.create(rootUri));

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
                    textService.setClassIndex(ClassIndex.build(outputDirs));

                    // Then resolve the full classpath (dependency JARs via mvn) and
                    // merge, so member hover and field completion over dependencies
                    // become available too.
                    Set<Path> resolved = new LinkedHashSet<>();
                    for (Path mavenRoot : mavenRoots) {
                        resolved.addAll(MavenClasspathResolver.resolve(mavenRoot));
                    }
                    setResolvedClasspath(resolved);
                    ClassIndex outputIndex = ClassIndex.build(buildOutputDirs);
                    textService.setClassIndex(ClassIndex.merge(jarClassIndex, outputIndex));
                } catch (Exception e) {
                    logger.log(Level.WARNING, "Failed to build class index at startup", e);
                }
            });
        }

        return CompletableFuture.supplyAsync(() -> initializeResult);
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
