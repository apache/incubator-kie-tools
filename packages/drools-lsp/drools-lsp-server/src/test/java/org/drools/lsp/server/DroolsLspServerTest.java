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
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.drools.completion.ClassIndex;
import org.drools.formatter.FormatterOptions;
import org.eclipse.lsp4j.ClientCapabilities;
import org.eclipse.lsp4j.ConfigurationItem;
import org.eclipse.lsp4j.ConfigurationParams;
import org.eclipse.lsp4j.DidChangeConfigurationCapabilities;
import org.eclipse.lsp4j.DidChangeConfigurationParams;
import org.eclipse.lsp4j.DidChangeWatchedFilesParams;
import org.eclipse.lsp4j.InitializeParams;
import org.eclipse.lsp4j.InitializedParams;
import org.eclipse.lsp4j.MessageActionItem;
import org.eclipse.lsp4j.MessageParams;
import org.eclipse.lsp4j.PublishDiagnosticsParams;
import org.eclipse.lsp4j.Registration;
import org.eclipse.lsp4j.RegistrationParams;
import org.eclipse.lsp4j.ShowMessageRequestParams;
import org.eclipse.lsp4j.WorkspaceClientCapabilities;
import org.eclipse.lsp4j.services.LanguageClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.assertj.core.api.Assertions.assertThat;

class DroolsLspServerTest {

    @TempDir
    Path tempDir;

    @Test
    void classpathEntriesEmptyBeforeInitialize() {
        DroolsLspServer server = new DroolsLspServer();
        assertThat(server.getClasspathEntries()).isEmpty();
    }

    @Test
    void rebuildClassIndexUpdatesDocumentService() throws IOException {
        DroolsLspServer server = TestHelperMethods.getDroolsLspServerForDocument("");

        Path classDir = createClassDir("com/example/Foo.class");
        server.setClasspathEntriesForTest(Set.of(classDir));

        server.rebuildClassIndex();

        ClassIndex index = server.getTextDocumentService().getClassIndexForTest();
        assertThat(index.getMatching("Foo")).contains("com.example.Foo");
    }

    @Test
    void didChangeWatchedFilesTriggersRebuild() throws Exception {
        DroolsLspServer server = TestHelperMethods.getDroolsLspServerForDocument("");

        Path classDir = createClassDir("com/example/Bar.class");
        server.setClasspathEntriesForTest(Set.of(classDir));

        DroolsLspWorkspaceService workspaceService = (DroolsLspWorkspaceService) server.getWorkspaceService();
        workspaceService.didChangeWatchedFiles(new DidChangeWatchedFilesParams());

        Thread.sleep(DroolsLspWorkspaceService.DEBOUNCE_DELAY_MS + 500);

        ClassIndex index = server.getTextDocumentService().getClassIndexForTest();
        assertThat(index.getMatching("Bar")).contains("com.example.Bar");
    }

    @Test
    void rapidFileChangesCoalesceIntoOneRebuild() throws Exception {
        DroolsLspServer server = TestHelperMethods.getDroolsLspServerForDocument("");

        Path classDir = createClassDir("com/example/Baz.class");
        server.setClasspathEntriesForTest(Set.of(classDir));

        DroolsLspWorkspaceService workspaceService = (DroolsLspWorkspaceService) server.getWorkspaceService();
        for (int i = 0; i < 100; i++) {
            workspaceService.didChangeWatchedFiles(new DidChangeWatchedFilesParams());
        }

        // Index should not be rebuilt yet (still within debounce window)
        ClassIndex indexBefore = server.getTextDocumentService().getClassIndexForTest();
        assertThat(indexBefore.getMatching("Baz")).isEmpty();

        Thread.sleep(DroolsLspWorkspaceService.DEBOUNCE_DELAY_MS + 500);

        ClassIndex indexAfter = server.getTextDocumentService().getClassIndexForTest();
        assertThat(indexAfter).isNotNull();
        assertThat(indexAfter.getMatching("Baz")).contains("com.example.Baz");
    }

    // Verifies the cached JAR index is used on rebuild, not that JARs are
    // literally not re-read — the latter would require a spy on ClassIndex.build.
    @Test
    void rebuildPreservesJarClassesFromCachedIndex() throws Exception {
        DroolsLspServer server = TestHelperMethods.getDroolsLspServerForDocument("");

        Path classDir = createClassDir("com/example/Foo.class");

        Path jarPath = tempDir.resolve("dep.jar");
        try (java.util.jar.JarOutputStream jos = new java.util.jar.JarOutputStream(
                Files.newOutputStream(jarPath))) {
            jos.putNextEntry(new java.util.jar.JarEntry("com/acme/Order.class"));
            jos.closeEntry();
        }

        server.setClasspathEntriesForTest(Set.of(classDir, jarPath));
        server.rebuildClassIndex();

        ClassIndex index = server.getTextDocumentService().getClassIndexForTest();
        assertThat(index.getMatching("Foo")).contains("com.example.Foo");
        assertThat(index.getMatching("Or")).contains("com.acme.Order");

        // Delete the JAR — rebuild should still have JAR classes from cached index
        Files.delete(jarPath);
        server.rebuildClassIndex();

        ClassIndex indexAfter = server.getTextDocumentService().getClassIndexForTest();
        assertThat(indexAfter.getMatching("Foo")).contains("com.example.Foo");
        assertThat(indexAfter.getMatching("Or")).contains("com.acme.Order");
    }

    @Test
    void resolveCustomMavenRootsAcceptsPomFile() throws IOException {
        Files.writeString(tempDir.resolve("pom.xml"), "<project/>");

        List<Path> roots = DroolsLspServer.resolveCustomMavenRoots(tempDir, "pom.xml");

        assertThat(roots).containsExactly(tempDir);
    }

    @Test
    void resolveCustomMavenRootsAcceptsDirectoryContainingPom() throws IOException {
        Path module = tempDir.resolve("module-a");
        Files.createDirectories(module);
        Files.writeString(module.resolve("pom.xml"), "<project/>");

        // Pointing at the directory (not the pom.xml itself) resolves to that
        // directory, not its parent.
        List<Path> roots = DroolsLspServer.resolveCustomMavenRoots(tempDir, "module-a");

        assertThat(roots).containsExactly(module);
    }

    @Test
    void resolveCustomMavenRootsSkipsMissingAndDirectoriesWithoutPom() throws IOException {
        Files.createDirectories(tempDir.resolve("empty-dir"));

        List<Path> roots = DroolsLspServer.resolveCustomMavenRoots(
            tempDir, "does-not-exist.xml" + File.pathSeparator + "empty-dir");

        assertThat(roots).isEmpty();
    }

    @Test
    void resolveCustomMavenRootsHandlesMultipleEntriesSkippingInvalidOnes() throws IOException {
        Path module = tempDir.resolve("module-a");
        Files.createDirectories(module);
        Files.writeString(module.resolve("pom.xml"), "<project/>");

        List<Path> roots = DroolsLspServer.resolveCustomMavenRoots(
            tempDir, "module-a/pom.xml" + File.pathSeparator + "missing/pom.xml");

        assertThat(roots).containsExactly(module);
    }

    @Test
    void initializedPullsFormatterOptionsAndRegistersForEmptyConfigurationChanges() throws Exception {
        DroolsLspServer server = new DroolsLspServer();
        CapturingClient client = new CapturingClient();
        client.configurationAnswer = List.of(jsonObject("{\"lineLength\":80}"));
        server.connect(client);

        server.initialize(initializeParams(new DidChangeConfigurationCapabilities(true), true)).get();
        server.initialized(new InitializedParams());

        assertThat(client.configurationRequests).hasSize(1);
        List<ConfigurationItem> items = client.configurationRequests.get(0).getItems();
        assertThat(items).hasSize(1);
        assertThat(items.get(0).getSection()).isEqualTo("drools.lsp.formatter");
        assertThat(items.get(0).getScopeUri()).isNull();
        assertThat(server.getTextDocumentService().formatterOptions().lineLength()).isEqualTo(80);

        assertThat(client.registrations).hasSize(1);
        List<Registration> registrations = client.registrations.get(0).getRegistrations();
        assertThat(registrations).hasSize(1);
        assertThat(registrations.get(0).getMethod()).isEqualTo("workspace/didChangeConfiguration");
        assertThat(registrations.get(0).getRegisterOptions()).isNull();
    }

    @Test
    void didChangeConfigurationPullsAgainWhenTheClientProvidesConfiguration() throws Exception {
        DroolsLspServer server = new DroolsLspServer();
        CapturingClient client = new CapturingClient();
        client.configurationAnswer = List.of(jsonObject("{\"lineLength\":80}"));
        server.connect(client);
        server.initialize(initializeParams(new DidChangeConfigurationCapabilities(true), true)).get();
        server.initialized(new InitializedParams());

        client.configurationAnswer = List.of(jsonObject("{\"parenPadding\":false}"));
        server.getWorkspaceService().didChangeConfiguration(new DidChangeConfigurationParams(JsonNull.INSTANCE));

        assertThat(client.configurationRequests).hasSize(2);
        assertThat(server.getTextDocumentService().formatterOptions().parenPadding()).isFalse();
    }

    @Test
    void nullConfigurationAnswerLeavesOptionsUntouched() throws Exception {
        DroolsLspServer server = new DroolsLspServer();
        CapturingClient client = new CapturingClient();
        client.configurationAnswer = List.of(jsonObject("{\"lineLength\":80}"));
        server.connect(client);
        server.initialize(initializeParams(new DidChangeConfigurationCapabilities(true), true)).get();
        server.pullFormatterOptions().join();

        client.configurationAnswer = List.of(JsonNull.INSTANCE);
        server.pullFormatterOptions().join();
        client.configurationAnswer = List.of();
        server.pullFormatterOptions().join();

        assertThat(server.getTextDocumentService().formatterOptions().lineLength()).isEqualTo(80);
    }

    @Test
    void clientWithoutConfigurationSupportGetsThePushFallback() throws Exception {
        DroolsLspServer server = new DroolsLspServer();
        CapturingClient client = new CapturingClient();
        server.connect(client);

        server.initialize(initializeParams(new DidChangeConfigurationCapabilities(true), false)).get();
        server.initialized(new InitializedParams());

        assertThat(client.configurationRequests).isEmpty();

        server.getWorkspaceService().didChangeConfiguration(new DidChangeConfigurationParams(
                jsonObject("{\"drools\":{\"lsp\":{\"formatter\":{\"parenPadding\":false}}}}")));

        assertThat(server.getTextDocumentService().formatterOptions().parenPadding()).isFalse();
    }

    @Test
    void initializedRegistersNothingWithoutDynamicRegistration() throws Exception {
        CapturingClient client = new CapturingClient();

        DroolsLspServer serverWithoutCapabilities = new DroolsLspServer();
        serverWithoutCapabilities.connect(client);
        serverWithoutCapabilities.initialize(new InitializeParams()).get();
        serverWithoutCapabilities.initialized(new InitializedParams());

        DroolsLspServer serverWithRegistrationOff = new DroolsLspServer();
        serverWithRegistrationOff.connect(client);
        serverWithRegistrationOff.initialize(
                initializeParams(new DidChangeConfigurationCapabilities(false), null)).get();
        serverWithRegistrationOff.initialized(new InitializedParams());

        assertThat(client.registrations).isEmpty();
    }

    private Path createClassDir(String classFilePath) throws IOException {
        Path classFile = tempDir.resolve(classFilePath);
        Files.createDirectories(classFile.getParent());
        Files.createFile(classFile);
        return tempDir;
    }

    private static JsonObject jsonObject(String json) {
        return JsonParser.parseString(json).getAsJsonObject();
    }

    private static InitializeParams initializeParams(DidChangeConfigurationCapabilities didChangeConfiguration,
                                                     Boolean configuration) {
        WorkspaceClientCapabilities workspace = new WorkspaceClientCapabilities();
        workspace.setDidChangeConfiguration(didChangeConfiguration);
        workspace.setConfiguration(configuration);
        ClientCapabilities capabilities = new ClientCapabilities();
        capabilities.setWorkspace(workspace);
        InitializeParams params = new InitializeParams();
        params.setCapabilities(capabilities);
        return params;
    }

    private static class CapturingClient implements LanguageClient {

        final List<RegistrationParams> registrations = new ArrayList<>();
        final List<ConfigurationParams> configurationRequests = new ArrayList<>();
        volatile List<Object> configurationAnswer = List.of(jsonObject("{}"));

        @Override
        public CompletableFuture<Void> registerCapability(RegistrationParams params) {
            registrations.add(params);
            return CompletableFuture.completedFuture(null);
        }

        @Override
        public CompletableFuture<List<Object>> configuration(ConfigurationParams params) {
            configurationRequests.add(params);
            return CompletableFuture.completedFuture(configurationAnswer);
        }

        @Override
        public void telemetryEvent(Object object) {
        }

        @Override
        public CompletableFuture<MessageActionItem> showMessageRequest(ShowMessageRequestParams requestParams) {
            return CompletableFuture.completedFuture(null);
        }

        @Override
        public void showMessage(MessageParams messageParams) {
        }

        @Override
        public void publishDiagnostics(PublishDiagnosticsParams diagnostics) {
        }

        @Override
        public void logMessage(MessageParams message) {
        }
    }
}
