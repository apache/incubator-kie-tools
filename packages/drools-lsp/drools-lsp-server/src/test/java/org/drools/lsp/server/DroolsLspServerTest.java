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
import java.util.List;
import java.util.Set;

import org.drools.completion.ClassIndex;
import org.eclipse.lsp4j.DidChangeWatchedFilesParams;
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

    private Path createClassDir(String classFilePath) throws IOException {
        Path classFile = tempDir.resolve(classFilePath);
        Files.createDirectories(classFile.getParent());
        Files.createFile(classFile);
        return tempDir;
    }
}
