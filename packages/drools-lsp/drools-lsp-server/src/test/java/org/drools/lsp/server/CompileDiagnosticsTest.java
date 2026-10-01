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

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BooleanSupplier;

import org.drools.completion.WorkspaceSiblingResolver;
import org.drools.completion.WorkspaceSiblingResolvers;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.DiagnosticSeverity;
import org.eclipse.lsp4j.DidChangeTextDocumentParams;
import org.eclipse.lsp4j.DidOpenTextDocumentParams;
import org.eclipse.lsp4j.DidSaveTextDocumentParams;
import org.eclipse.lsp4j.DocumentDiagnosticParams;
import org.eclipse.lsp4j.DocumentDiagnosticReport;
import org.eclipse.lsp4j.MessageActionItem;
import org.eclipse.lsp4j.MessageParams;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.PublishDiagnosticsParams;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.ShowMessageRequestParams;
import org.eclipse.lsp4j.TextDocumentContentChangeEvent;
import org.eclipse.lsp4j.TextDocumentIdentifier;
import org.eclipse.lsp4j.TextDocumentItem;
import org.eclipse.lsp4j.VersionedTextDocumentIdentifier;
import org.eclipse.lsp4j.services.LanguageClient;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.assertj.core.api.Assertions.assertThat;

class CompileDiagnosticsTest {

    private static final String VALID = """
            package com.example;
            declare Order
                total : int
            end
            rule "Large order"
                when
                    Order( total > 100 )
                then
                end
            """;

    private static final String BROKEN_TYPE = """
            package com.example;
            rule "R"
                when
                    NoSuchType( field == 1 )
                then
                end
            """;

    private static final String DECLARES_ORDER = """
            package com.example;
            declare Order
                total : int
            end
            """;

    private static final String USES_ORDER = """
            package com.example;
            rule "R"
                when
                    Order( total > 1 )
                then
                end
            """;

    private static ProjectEngine realEngine;

    @BeforeAll
    static void loadTheEngineOnce() {
        realEngine = ProjectEngine.over(TestClasspath.withoutTheBridge());
        assertThat(realEngine.available()).isTrue();
    }

    @AfterAll
    static void closeTheEngine() {
        realEngine.close();
    }

    @AfterEach
    void restoreDefaultResolver() {
        WorkspaceSiblingResolvers.setActive(null);
    }

    private record Fixture(DroolsLspServer server, DroolsLspDocumentService service, CompileDiagnostics compile,
                           RecordingClient client) {
    }

    private static Fixture fixture(Engine engine) {
        RecordingClient client = new RecordingClient();
        DroolsLspServer server = new DroolsLspServer();
        server.connect(client);
        DroolsLspDocumentService service = server.getTextDocumentService();
        CompileDiagnostics compile = service.compileDiagnostics();
        compile.setEngine(engine);
        return new Fixture(server, service, compile, client);
    }

    /** A real engine that is never closed by a fixture: {@code setEngine} closes the previous one. */
    private static Engine shared() {
        return new Engine() {
            @Override
            public boolean available() {
                return realEngine.available();
            }

            @Override
            public List<Map<String, Object>> build(Map<String, String> drlByPath) throws Exception {
                return realEngine.build(drlByPath);
            }

            @Override
            public void close() {
            }
        };
    }

    @Test
    void saveCompilesTheGroupAndCachesErrors(@TempDir Path tmp) throws Exception {
        Path drl = write(tmp, "A.drl", BROKEN_TYPE);
        Fixture f = fixture(shared());
        open(f.service(), drl, BROKEN_TYPE);

        f.service().didSave(new DidSaveTextDocumentParams(new TextDocumentIdentifier(uri(drl))));

        awaitUntil(() -> !f.compile().cachedFor(drl).isEmpty());
        DocumentDiagnosticReport report = f.service().diagnostic(
                new DocumentDiagnosticParams(new TextDocumentIdentifier(uri(drl)))).get();
        assertThat(report.getRelatedFullDocumentDiagnosticReport().getItems()).anySatisfy(d -> {
            assertThat(d.getSource()).isEqualTo("drools");
            assertThat(d.getSeverity()).isEqualTo(DiagnosticSeverity.Error);
        });
        assertThat(f.client().refreshes.get()).isGreaterThanOrEqualTo(1);
        assertThat(f.client().messages).isEmpty();
    }

    @Test
    void editDropsTheCachedResult(@TempDir Path tmp) throws Exception {
        Path drl = write(tmp, "A.drl", BROKEN_TYPE);
        Fixture f = fixture(shared());
        open(f.service(), drl, BROKEN_TYPE);
        f.service().didSave(new DidSaveTextDocumentParams(new TextDocumentIdentifier(uri(drl))));
        awaitUntil(() -> !f.compile().cachedFor(drl).isEmpty());

        f.service().didChange(new DidChangeTextDocumentParams(
                new VersionedTextDocumentIdentifier(uri(drl), 2),
                List.of(new TextDocumentContentChangeEvent(VALID))));

        assertThat(f.compile().cachedFor(drl)).isEmpty();
        DocumentDiagnosticReport report = f.service().diagnostic(
                new DocumentDiagnosticParams(new TextDocumentIdentifier(uri(drl)))).get();
        assertThat(report.getRelatedFullDocumentDiagnosticReport().getItems())
                .noneSatisfy(d -> assertThat(d.getSource()).isEqualTo("drools"));
    }

    @Test
    void staleResultIsDiscarded(@TempDir Path tmp) throws Exception {
        Path drl = write(tmp, "A.drl", BROKEN_TYPE);
        Fixture f = fixture(shared());
        open(f.service(), drl, BROKEN_TYPE);
        f.service().didChange(new DidChangeTextDocumentParams(
                new VersionedTextDocumentIdentifier(uri(drl), 2),
                List.of(new TextDocumentContentChangeEvent(VALID))));

        f.compile().triggerNow(drl, BROKEN_TYPE, false);
        f.compile().triggerNow(drl, VALID, false);

        awaitUntil(() -> f.client().refreshes.get() >= 1);
        Thread.sleep(500);
        assertThat(f.client().refreshes.get()).isEqualTo(1);
        assertThat(f.compile().cachedFor(drl)).isEmpty();
    }

    @Test
    void syntaxErrorsSkipTheCompile(@TempDir Path tmp) throws Exception {
        String broken = "rule broken";
        Path drl = write(tmp, "A.drl", broken);
        Fixture f = fixture(shared());
        open(f.service(), drl, broken);

        f.compile().triggerNow(drl, broken, false);

        Thread.sleep(300);
        assertThat(f.compile().cachedFor(drl)).isEmpty();
        assertThat(f.client().refreshes.get()).isZero();
        assertThat(f.client().messages).isEmpty();
    }

    @Test
    void siblingDeclaresAreVisibleToTheScopedCompile(@TempDir Path tmp) throws Exception {
        write(tmp, "Types.drl", DECLARES_ORDER);
        Path usage = write(tmp, "Usage.drl", USES_ORDER);
        Fixture f = fixture(shared());
        open(f.service(), usage, USES_ORDER);

        f.service().didSave(new DidSaveTextDocumentParams(new TextDocumentIdentifier(uri(usage))));

        awaitUntil(() -> f.client().refreshes.get() >= 1);
        assertThat(f.compile().cachedFor(usage)).isEmpty();
    }

    @Test
    void siblingErrorsAreServedOnTheSiblingsPull(@TempDir Path tmp) throws Exception {
        Path good = write(tmp, "A.drl", VALID);
        Path bad = write(tmp, "B.drl", BROKEN_TYPE);
        Fixture f = fixture(shared());
        open(f.service(), good, VALID);

        f.service().didSave(new DidSaveTextDocumentParams(new TextDocumentIdentifier(uri(good))));

        awaitUntil(() -> !f.compile().cachedFor(bad).isEmpty());
        assertThat(f.compile().cachedFor(good)).isEmpty();
        assertThat(f.compile().cachedFor(bad)).anySatisfy(d -> assertThat(d.getSource()).isEqualTo("drools"));
    }

    @Test
    void rebuildWorkspaceCompilesEveryDrlUnderTheRoot(@TempDir Path tmp) throws Exception {
        Path a = Files.createDirectories(tmp.resolve("a"));
        Path b = Files.createDirectories(tmp.resolve("b"));
        write(a, "Types.drl", DECLARES_ORDER);
        Path usage = write(b, "Usage.drl", USES_ORDER);
        Fixture f = fixture(shared());
        f.server().initializeJavaSourceTypingForTest(tmp);
        open(f.service(), usage, USES_ORDER);

        f.service().didSave(new DidSaveTextDocumentParams(new TextDocumentIdentifier(uri(usage))));
        awaitUntil(() -> !f.compile().cachedFor(usage).isEmpty());

        f.compile().rebuildWorkspace(usage);
        awaitUntil(() -> f.compile().cachedFor(usage).isEmpty());
        assertThat(f.client().messages).hasSizeGreaterThanOrEqualTo(2);
    }

    @Test
    void saveDoesNothingWhenOnSaveIsOff(@TempDir Path tmp) throws Exception {
        Path drl = write(tmp, "A.drl", BROKEN_TYPE);
        Fixture f = fixture(shared());
        f.compile().setOnSave(false);
        open(f.service(), drl, BROKEN_TYPE);

        f.service().didSave(new DidSaveTextDocumentParams(new TextDocumentIdentifier(uri(drl))));
        Thread.sleep(500);
        assertThat(f.compile().cachedFor(drl)).isEmpty();
        assertThat(f.client().refreshes.get()).isZero();

        f.compile().rebuildWorkspace(drl);
        awaitUntil(() -> !f.compile().cachedFor(drl).isEmpty());
    }

    @Test
    void noEngineMeansNoCompile(@TempDir Path tmp) throws Exception {
        Path drl = write(tmp, "A.drl", BROKEN_TYPE);
        Fixture f = fixture(ProjectEngine.over(List.of()));
        open(f.service(), drl, BROKEN_TYPE);

        f.service().didSave(new DidSaveTextDocumentParams(new TextDocumentIdentifier(uri(drl))));

        Thread.sleep(500);
        assertThat(f.compile().cachedFor(drl)).isEmpty();
        assertThat(f.client().refreshes.get()).isZero();
    }

    @Test
    void cacheIsKeyedByPathNotByUriSpelling(@TempDir Path tmp) throws Exception {
        Path drl = write(tmp, "A.drl", BROKEN_TYPE);
        String clientSpelling = drl.toFile().toURI().toString();
        assertThat(clientSpelling).isNotEqualTo(uri(drl));
        Fixture f = fixture(shared());
        TextDocumentItem item = new TextDocumentItem();
        item.setUri(clientSpelling);
        item.setLanguageId("drools");
        item.setVersion(1);
        item.setText(BROKEN_TYPE);
        f.service().didOpen(new DidOpenTextDocumentParams(item));

        f.service().didSave(new DidSaveTextDocumentParams(new TextDocumentIdentifier(clientSpelling)));

        awaitUntil(() -> !f.compile().cachedFor(drl).isEmpty());
        DocumentDiagnosticReport report = f.service().diagnostic(
                new DocumentDiagnosticParams(new TextDocumentIdentifier(clientSpelling))).get();
        assertThat(report.getRelatedFullDocumentDiagnosticReport().getItems())
                .anySatisfy(d -> assertThat(d.getSource()).isEqualTo("drools"));
    }

    @Test
    void messageWithoutAPathLandsOnTheSavedFile(@TempDir Path tmp) throws Exception {
        Path drl = write(tmp, "A.drl", VALID);
        FakeEngine engine = new FakeEngine(sources -> List.of(message(null, "ERROR", 0, 0, "engine says no")));
        Fixture f = fixture(engine);
        open(f.service(), drl, VALID);

        f.service().didSave(new DidSaveTextDocumentParams(new TextDocumentIdentifier(uri(drl))));

        awaitUntil(() -> !f.compile().cachedFor(drl).isEmpty());
        assertThat(f.compile().cachedFor(drl)).singleElement()
                .satisfies(d -> assertThat(d.getMessage()).isEqualTo("engine says no"));
    }

    @Test
    void aThrowingEngineYieldsOneErrorAndReleasesTheGate(@TempDir Path tmp) throws Exception {
        Path drl = write(tmp, "A.drl", VALID);
        FakeEngine throwing = new FakeEngine(sources -> {
            throw new IllegalStateException("boom");
        });
        Fixture f = fixture(throwing);
        open(f.service(), drl, VALID);

        f.service().didSave(new DidSaveTextDocumentParams(new TextDocumentIdentifier(uri(drl))));
        awaitUntil(() -> !f.compile().cachedFor(drl).isEmpty());
        assertThat(f.compile().cachedFor(drl)).singleElement().satisfies(d -> {
            assertThat(d.getSeverity()).isEqualTo(DiagnosticSeverity.Error);
            assertThat(d.getMessage()).contains("boom");
        });

        f.compile().setEngine(shared());
        f.service().didSave(new DidSaveTextDocumentParams(new TextDocumentIdentifier(uri(drl))));
        awaitUntil(() -> f.compile().cachedFor(drl).isEmpty());
    }

    @Test
    void aMissingProjectClassIsReportedByName(@TempDir Path tmp) throws Exception {
        Path drl = write(tmp, "A.drl", VALID);
        FakeEngine missingClass = new FakeEngine(sources -> {
            NoClassDefFoundError linkage = new NoClassDefFoundError("com/example/Parcel");
            linkage.initCause(new ClassNotFoundException("com.example.Parcel"));
            throw new RuntimeException("Rules compilation failed or interrupted",
                    new java.util.concurrent.ExecutionException(linkage));
        });
        Fixture f = fixture(missingClass);
        open(f.service(), drl, VALID);

        f.service().didSave(new DidSaveTextDocumentParams(new TextDocumentIdentifier(uri(drl))));

        awaitUntil(() -> !f.compile().cachedFor(drl).isEmpty());
        assertThat(f.compile().cachedFor(drl)).singleElement().satisfies(d -> {
            assertThat(d.getSeverity()).isEqualTo(DiagnosticSeverity.Warning);
            assertThat(d.getMessage()).contains("com.example.Parcel").contains("Build the project");
        });
    }

    @Test
    void javaSourcesWithoutBuildOutputSkipTheCompileWithOneNotice(@TempDir Path tmp) throws Exception {
        Path sources = Files.createDirectories(tmp.resolve("src/main/java/com/example"));
        Files.writeString(sources.resolve("Parcel.java"), "package com.example;\npublic class Parcel {\n}\n");
        Path drl = write(tmp, "A.drl", VALID);
        FakeEngine engine = new FakeEngine(s -> List.of());
        Fixture f = fixture(engine);
        f.server().initializeJavaSourceTypingForTest(tmp);
        open(f.service(), drl, VALID);

        f.service().didSave(new DidSaveTextDocumentParams(new TextDocumentIdentifier(uri(drl))));
        f.service().didSave(new DidSaveTextDocumentParams(new TextDocumentIdentifier(uri(drl))));

        awaitUntil(() -> !f.client().messages.isEmpty());
        Thread.sleep(300);
        assertThat(engine.calls.get()).isZero();
        assertThat(f.client().messages).singleElement()
                .satisfies(m -> assertThat(m.getMessage()).contains("Build the project"));

        f.compile().rebuildWorkspace(drl);
        awaitUntil(() -> f.client().messages.size() == 2);
        assertThat(f.client().messages.get(1).getMessage()).startsWith("Workspace compile skipped");
        assertThat(engine.calls.get()).isZero();
    }

    @Test
    void replacingTheEngineDuringABuildClosesTheOldOneAfterwards(@TempDir Path tmp) throws Exception {
        Path drl = write(tmp, "A.drl", VALID);
        CountDownLatch release = new CountDownLatch(1);
        FakeEngine blocking = new FakeEngine(sources -> {
            release.await(10, TimeUnit.SECONDS);
            return List.of();
        });
        FakeEngine replacement = new FakeEngine(sources -> List.of());
        Fixture f = fixture(blocking);
        open(f.service(), drl, VALID);

        f.service().didSave(new DidSaveTextDocumentParams(new TextDocumentIdentifier(uri(drl))));
        awaitUntil(() -> blocking.calls.get() == 1);
        f.compile().setEngine(replacement);
        assertThat(blocking.closed).isFalse();

        release.countDown();
        awaitUntil(() -> blocking.closed);
        assertThat(f.client().refreshes.get()).isEqualTo(1);
    }

    @Test
    void fileOutsideTheWorkspaceRootStillCompiles(@TempDir Path tmp) throws Exception {
        Path root = Files.createDirectories(tmp.resolve("ws"));
        Path elsewhere = Files.createDirectories(tmp.resolve("elsewhere"));
        Path drl = write(elsewhere, "A.drl", BROKEN_TYPE);
        Fixture f = fixture(shared());
        f.server().initializeJavaSourceTypingForTest(root);
        open(f.service(), drl, BROKEN_TYPE);

        f.service().didSave(new DidSaveTextDocumentParams(new TextDocumentIdentifier(uri(drl))));

        awaitUntil(() -> !f.compile().cachedFor(drl).isEmpty());
    }

    @Test
    void noisyOnlyErrorsSurviveTheCascadeFilter(@TempDir Path tmp) throws Exception {
        Path drl = write(tmp, "A.drl", VALID);
        FakeEngine engine = new FakeEngine(sources -> List.of(
                message("src/main/resources/A.drl", "ERROR", 0, 0, "Unknown type 'Foo'")));
        Fixture f = fixture(engine);
        open(f.service(), drl, VALID);

        f.service().didSave(new DidSaveTextDocumentParams(new TextDocumentIdentifier(uri(drl))));

        awaitUntil(() -> !f.compile().cachedFor(drl).isEmpty());
        assertThat(f.compile().cachedFor(drl)).singleElement()
                .satisfies(d -> assertThat(d.getMessage()).isEqualTo("Unknown type 'Foo'"));
    }

    @Test
    void aSaveDoesNotReplaceAQueuedRebuild(@TempDir Path tmp) throws Exception {
        Path drl = write(tmp, "A.drl", VALID);
        CountDownLatch release = new CountDownLatch(1);
        List<Integer> callSizes = new CopyOnWriteArrayList<>();
        FakeEngine blocking = new FakeEngine(sources -> {
            callSizes.add(sources.size());
            release.await(10, TimeUnit.SECONDS);
            return List.of();
        });
        Fixture f = fixture(blocking);
        open(f.service(), drl, VALID);

        // Both requests are queued synchronously so the merge happens while the first build is still blocked.
        f.compile().triggerNow(drl, VALID, false);
        awaitUntil(() -> blocking.calls.get() == 1);

        f.compile().triggerNow(drl, VALID, true);
        f.compile().triggerNow(drl, VALID, false);
        release.countDown();

        awaitUntil(() -> blocking.calls.get() == 2);
        assertThat(callSizes).hasSize(2);
        awaitUntil(() -> f.client().messages.stream()
                .anyMatch(m -> m.getMessage().startsWith("Workspace compile finished")));
        assertThat(f.client().messages).anySatisfy(
                m -> assertThat(m.getMessage()).isEqualTo("Compiling every DRL file in the workspace"));
        assertThat(f.client().messages).anySatisfy(
                m -> assertThat(m.getMessage()).contains("Workspace compile finished"));
    }

    @Test
    void rebuildLeavesABrokenActiveFileOutAndStillRuns(@TempDir Path tmp) throws Exception {
        String broken = "rule broken";
        Path drl = write(tmp, "A.drl", broken);
        write(tmp, "B.drl", VALID);
        Fixture f = fixture(shared());
        open(f.service(), drl, broken);

        f.compile().rebuildWorkspace(drl);

        awaitUntil(() -> f.client().messages.stream()
                .anyMatch(m -> m.getMessage().startsWith("Workspace compile finished")));
        assertThat(f.compile().cachedFor(drl)).isEmpty();
    }

    @Test
    void rebuildFromABrokenActiveFileMapsUnmappedMessagesToACompiledFile(@TempDir Path tmp) throws Exception {
        String broken = "rule broken";
        Path brokenFile = write(tmp, "A.drl", broken);
        Path types = write(tmp, "Types.drl", VALID);
        FakeEngine engine = new FakeEngine(sources -> List.of(message(null, "ERROR", 0, 0, "engine says no")));
        Fixture f = fixture(engine);
        open(f.service(), brokenFile, broken);

        f.compile().rebuildWorkspace(brokenFile);

        awaitUntil(() -> f.client().messages.stream()
                .anyMatch(m -> m.getMessage().startsWith("Workspace compile finished")));
        assertThat(f.compile().cachedFor(brokenFile)).isEmpty();
        assertThat(f.compile().cachedFor(types)).singleElement()
                .satisfies(d -> assertThat(d.getMessage()).isEqualTo("engine says no"));
    }

    @Test
    void siblingsWithSyntaxErrorsAreLeftOutOfTheCompile(@TempDir Path tmp) throws Exception {
        Path types = write(tmp, "Types.drl", "rule broken");
        Path saved = write(tmp, "A.drl", VALID);
        Fixture f = fixture(shared());
        open(f.service(), saved, VALID);

        f.service().didSave(new DidSaveTextDocumentParams(new TextDocumentIdentifier(uri(saved))));

        awaitUntil(() -> f.client().refreshes.get() >= 1);
        assertThat(f.compile().cachedFor(saved)).isEmpty();
        assertThat(f.compile().cachedFor(types)).isEmpty();
    }

    @Test
    void aSiblingThatBrokeOnDiskLosesItsCachedResult(@TempDir Path tmp) throws Exception {
        Path a = write(tmp, "A.drl", VALID);
        Path b = write(tmp, "B.drl", BROKEN_TYPE);
        Fixture f = fixture(shared());
        open(f.service(), a, VALID);

        f.service().didSave(new DidSaveTextDocumentParams(new TextDocumentIdentifier(uri(a))));
        awaitUntil(() -> !f.compile().cachedFor(b).isEmpty());

        Files.writeString(b, "rule broken");
        f.service().didSave(new DidSaveTextDocumentParams(new TextDocumentIdentifier(uri(a))));

        awaitUntil(() -> f.compile().cachedFor(b).isEmpty());
    }

    @Test
    void rebuildSummaryNamesTheFilesWithErrors(@TempDir Path tmp) throws Exception {
        Path a = write(tmp, "A.drl", VALID);
        write(tmp, "B.drl", VALID);
        FakeEngine engine = new FakeEngine(sources -> List.of(
                message("src/main/resources/A.drl", "ERROR", 0, 0, "bad A"),
                message("src/main/resources/B.drl", "ERROR", 0, 0, "bad B")));
        Fixture f = fixture(engine);
        f.server().initializeJavaSourceTypingForTest(tmp);
        open(f.service(), a, VALID);

        f.compile().rebuildWorkspace(a);

        awaitUntil(() -> f.client().messages.stream()
                .anyMatch(m -> m.getMessage().startsWith("Workspace compile finished")));
        assertThat(f.client().messages).anySatisfy(m -> {
            assertThat(m.getMessage()).contains("2 error(s)");
            assertThat(m.getMessage()).contains("A.drl");
            assertThat(m.getMessage()).contains("B.drl");
        });
    }

    @Test
    void fullScopeTakesTheResolversFileSet(@TempDir Path tmp) throws Exception {
        Path a = Files.createDirectories(tmp.resolve("a"));
        Path b = Files.createDirectories(tmp.resolve("b"));
        Path types = write(a, "Types.drl", DECLARES_ORDER);
        Path usage = write(b, "Usage.drl", USES_ORDER);
        WorkspaceSiblingResolvers.setActive(new WorkspaceSiblingResolver() {
            @Override
            public List<Path> resolveSiblings(Path currentFile) {
                return List.of();
            }

            @Override
            public List<Path> workspaceDrlFiles() {
                return List.of(types, usage);
            }
        });
        Fixture f = fixture(shared());
        open(f.service(), usage, USES_ORDER);

        f.compile().rebuildWorkspace(usage);

        awaitUntil(() -> f.client().messages.stream()
                .anyMatch(m -> m.getMessage().startsWith("Workspace compile finished")));
        assertThat(f.compile().cachedFor(usage)).isEmpty();
    }

    @Test
    void fullScopeWalkKeepsAPackageDirectoryNamedBuild(@TempDir Path tmp) throws Exception {
        write(Files.createDirectories(tmp.resolve("src/main/resources/com/acme/build")), "Types.drl", DECLARES_ORDER);
        Path usage = write(Files.createDirectories(tmp.resolve("src/main/resources")), "Usage.drl", USES_ORDER);
        Fixture f = fixture(shared());
        f.server().initializeJavaSourceTypingForTest(tmp);
        open(f.service(), usage, USES_ORDER);

        f.compile().rebuildWorkspace(usage);

        awaitUntil(() -> f.client().messages.stream()
                .anyMatch(m -> m.getMessage().startsWith("Workspace compile finished")));
        assertThat(f.compile().cachedFor(usage)).isEmpty();
    }

    @Test
    void setEngineAfterShutdownClosesTheNewEngine() throws Exception {
        Fixture f = fixture(shared());
        f.compile().shutdown();

        FakeEngine fake = new FakeEngine(sources -> List.of());
        f.compile().setEngine(fake);

        assertThat(fake.closed).isTrue();
        assertThat(f.compile().engine()).isNull();
    }

    @Test
    void mergeKeepsFastDiagnosticsAndAppendsNewCompileOnes() {
        Diagnostic fast = diagnostic("Unknown type 'X'", 1, "drools-type");
        Diagnostic sameAsFast = diagnostic("Unknown type 'X'", 1, "drools");
        Diagnostic compiled = diagnostic("Unable to resolve ObjectType 'X'", 1, "drools");

        List<Diagnostic> merged = CompileDiagnostics.merge(List.of(fast), List.of(sameAsFast, compiled));

        assertThat(merged).containsExactly(fast, compiled);
    }

    private static Diagnostic diagnostic(String message, int line, String source) {
        Diagnostic d = new Diagnostic();
        d.setMessage(message);
        d.setSeverity(DiagnosticSeverity.Error);
        d.setSource(source);
        d.setRange(new Range(new Position(line, 0), new Position(line, 1)));
        return d;
    }

    private static Map<String, Object> message(String path, String level, int line, int column, String text) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("path", path);
        m.put("level", level);
        m.put("line", line);
        m.put("column", column);
        m.put("text", text);
        return m;
    }

    private static Path write(Path dir, String name, String text) throws Exception {
        Path file = dir.resolve(name);
        Files.writeString(file, text);
        return file;
    }

    private static String uri(Path path) {
        return path.toUri().toString();
    }

    private static void open(DroolsLspDocumentService service, Path path, String text) {
        TextDocumentItem item = new TextDocumentItem();
        item.setUri(uri(path));
        item.setLanguageId("drools");
        item.setVersion(1);
        item.setText(text);
        service.didOpen(new DidOpenTextDocumentParams(item));
    }

    private static void awaitUntil(BooleanSupplier condition) throws InterruptedException {
        long deadline = System.currentTimeMillis() + 30_000L;
        while (System.currentTimeMillis() < deadline) {
            if (condition.getAsBoolean()) {
                return;
            }
            Thread.sleep(100);
        }
        throw new AssertionError("Condition not met within 30 seconds");
    }

    static final class FakeEngine implements Engine {
        final AtomicInteger calls = new AtomicInteger();
        volatile boolean closed;
        private final Answer answer;

        interface Answer {
            List<Map<String, Object>> apply(Map<String, String> sources) throws Exception;
        }

        FakeEngine(Answer answer) {
            this.answer = answer;
        }

        @Override
        public boolean available() {
            return !closed;
        }

        @Override
        public List<Map<String, Object>> build(Map<String, String> drlByPath) throws Exception {
            calls.incrementAndGet();
            return answer.apply(drlByPath);
        }

        @Override
        public void close() {
            closed = true;
        }
    }

    static final class RecordingClient implements LanguageClient {
        final AtomicInteger refreshes = new AtomicInteger();
        final List<MessageParams> messages = new CopyOnWriteArrayList<>();

        @Override
        public CompletableFuture<Void> refreshDiagnostics() {
            refreshes.incrementAndGet();
            return CompletableFuture.completedFuture(null);
        }

        @Override
        public void telemetryEvent(Object object) {
        }

        @Override
        public void publishDiagnostics(PublishDiagnosticsParams diagnostics) {
        }

        @Override
        public void showMessage(MessageParams messageParams) {
            messages.add(messageParams);
        }

        @Override
        public CompletableFuture<MessageActionItem> showMessageRequest(ShowMessageRequestParams requestParams) {
            return CompletableFuture.completedFuture(null);
        }

        @Override
        public void logMessage(MessageParams message) {
        }
    }
}
