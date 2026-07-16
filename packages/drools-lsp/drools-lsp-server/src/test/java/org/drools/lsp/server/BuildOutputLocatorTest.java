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
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.assertj.core.api.Assertions.assertThat;

class BuildOutputLocatorTest {

    @TempDir
    Path moduleDir;

    @Test
    void findsMavenTargetClasses() throws IOException {
        Path classes = createDirWithClass(moduleDir.resolve("target/classes"), "com/example/Foo.class");
        // a source directory must never be reported as compiled output
        Files.createDirectories(moduleDir.resolve("src/main/java/com/example"));

        List<Path> dirs = BuildOutputLocator.findClassDirs(moduleDir);

        assertThat(dirs).containsExactly(classes.normalize());
    }

    @Test
    void findsMavenMainAndTestClasses() throws IOException {
        Path main = createDirWithClass(moduleDir.resolve("target/classes"), "com/example/Foo.class");
        Path test = createDirWithClass(moduleDir.resolve("target/test-classes"), "com/example/FooTest.class");

        List<Path> dirs = BuildOutputLocator.findClassDirs(moduleDir);

        assertThat(dirs).contains(main.normalize(), test.normalize());
    }

    @Test
    void findsGradleOutput() throws IOException {
        Path gradle = createDirWithClass(moduleDir.resolve("build/classes/java/main"), "com/example/Foo.class");

        List<Path> dirs = BuildOutputLocator.findClassDirs(moduleDir);

        assertThat(dirs).contains(gradle.normalize());
    }

    @Test
    void prefersConventionalOverFallbackScan() throws IOException {
        Path conventional = createDirWithClass(moduleDir.resolve("target/classes"), "com/example/Foo.class");
        // a stray custom 'classes' dir that must be ignored because a conventional dir exists
        createDirWithClass(moduleDir.resolve("custom-out/classes"), "com/other/Bar.class");

        List<Path> dirs = BuildOutputLocator.findClassDirs(moduleDir);

        assertThat(dirs).containsExactly(conventional.normalize());
    }

    @Test
    void fallbackFindsCustomClassesDir() throws IOException {
        Path custom = createDirWithClass(moduleDir.resolve("custom-out/classes"), "com/example/Foo.class");

        List<Path> dirs = BuildOutputLocator.findClassDirs(moduleDir);

        assertThat(dirs).containsExactly(custom.normalize());
    }

    @Test
    void fallbackIgnoresEmptyClassesDir() throws IOException {
        // a 'classes' dir with no .class files beneath it must not be reported
        Files.createDirectories(moduleDir.resolve("weird/classes/empty"));

        List<Path> dirs = BuildOutputLocator.findClassDirs(moduleDir);

        assertThat(dirs).isEmpty();
    }

    @Test
    void fallbackIgnoresGradleIntermediateClassesDir() throws IOException {
        // 'build/classes' is a build-tool intermediate (has a 'java' subdir), not a
        // package root -- it must not be reported as a class directory itself.
        createDirWithClass(moduleDir.resolve("build/classes/java/custom"), "com/example/Foo.class");

        List<Path> dirs = BuildOutputLocator.findClassDirs(moduleDir);

        assertThat(dirs).noneMatch(p -> p.endsWith("build/classes") || p.endsWith(Path.of("build", "classes")));
    }

    @Test
    void returnsEmptyWhenNoOutput() {
        List<Path> dirs = BuildOutputLocator.findClassDirs(moduleDir);

        assertThat(dirs).isEmpty();
    }

    private static Path createDirWithClass(Path dir, String classFileRelative) throws IOException {
        Path classFile = dir.resolve(classFileRelative);
        Files.createDirectories(classFile.getParent());
        Files.createFile(classFile);
        return dir;
    }
}
