/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.workbench.common.services.backend.compiler.internalNIO;

import java.nio.file.DirectoryStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.services.backend.compiler.CompilationResponse;
import org.kie.workbench.common.services.backend.compiler.TestUtil;
import org.kie.workbench.common.services.backend.compiler.configuration.Decorator;
import org.kie.workbench.common.services.backend.compiler.configuration.MavenCLIArgs;
import org.kie.workbench.common.services.backend.compiler.internalNIO.impl.InternalNIODefaultCompilationRequest;
import org.kie.workbench.common.services.backend.compiler.internalNIO.impl.InternalNIOMavenCompilerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.java.nio.file.Files;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.Paths;

public class InternalNIODefaultMavenIncrementalCompilerTest {

    private Path mavenRepo;
    private Logger logger = LoggerFactory.getLogger(InternalNIODefaultMavenIncrementalCompilerTest.class);

    @Before
    public void setUp() throws Exception {

        mavenRepo = Paths.get(System.getProperty("user.home"),
                              "/.m2/repository");

        if (!Files.exists(mavenRepo)) {
            logger.info("Creating a m2_repo into " + mavenRepo);
            if (!Files.exists(Files.createDirectories(mavenRepo))) {
                throw new Exception("Folder not writable in the project");
            }
        }
    }

    @Test
    public void testIsValidMavenHome() throws Exception {
        Path tmpRoot = Files.createTempDirectory("repo");
        Path tmp = Files.createDirectories(Paths.get(tmpRoot.toString(),
                                                     "dummy"));
        //NIO creation and copy content
        java.nio.file.Path temp = java.nio.file.Files.createDirectories(java.nio.file.Paths.get(tmpRoot.toString(),
                                                                                                "dummy"));
        TestUtil.copyTree(java.nio.file.Paths.get("src/test/projects/dummy"),
                          temp);
        //end NIO

        InternalNIOMavenCompiler compiler = InternalNIOMavenCompilerFactory.getCompiler(
                Decorator.NONE);

        InternalNIOWorkspaceCompilationInfo info = new InternalNIOWorkspaceCompilationInfo(tmp);
        InternalNIOCompilationRequest req = new InternalNIODefaultCompilationRequest(mavenRepo.toAbsolutePath().toString(),
                                                                                     info,
                                                                                     new String[]{MavenCLIArgs.VERSION},
                                                                                     new HashMap<>(),
                                                                                     Boolean.FALSE);
        CompilationResponse res = compiler.compileSync(req);
        if (res.getMavenOutput().isPresent() && !res.isSuccessful()) {
            TestUtil.writeMavenOutputIntoTargetFolder(res.getMavenOutput().get(),
                                                      "InternalNIODefaultMavenIncrementalCompilerTest.testIsValidMavenHome");
        }
        Assert.assertTrue(res.isSuccessful());

        InternalNIOTestUtil.rm(tmpRoot.toFile());
    }

    @Test
    public void testIncrementalWithPluginEnabled() throws Exception {
        Path tmpRoot = Files.createTempDirectory("repo");
        Path tmp = Files.createDirectories(Paths.get(tmpRoot.toString(),
                                                     "dummy"));
        //NIO creation and copy content
        java.nio.file.Path temp = java.nio.file.Files.createDirectories(java.nio.file.Paths.get(tmpRoot.toString(),
                                                                                                "dummy"));
        TestUtil.copyTree(java.nio.file.Paths.get("src/test/projects/dummy"),
                          temp);
        //end NIO

        InternalNIOMavenCompiler compiler = InternalNIOMavenCompilerFactory.getCompiler(
                Decorator.NONE);

        InternalNIOWorkspaceCompilationInfo info = new InternalNIOWorkspaceCompilationInfo(tmp);
        InternalNIOCompilationRequest req = new InternalNIODefaultCompilationRequest(mavenRepo.toAbsolutePath().toString(),
                                                                                     info,
                                                                                     new String[]{MavenCLIArgs.CLEAN, MavenCLIArgs.COMPILE},
                                                                                     new HashMap<>(),
                                                                                     Boolean.FALSE);
        CompilationResponse res = compiler.compileSync(req);
        if (res.getMavenOutput().isPresent() && !res.isSuccessful()) {
            TestUtil.writeMavenOutputIntoTargetFolder(res.getMavenOutput().get(),
                                                      "InternalNIODefaultMavenIncrementalCompilerTest.testIncrementalWithPluginEnabled");
        }
        Assert.assertTrue(res.isSuccessful());

        Path incrementalConfiguration = Paths.get(tmp.toAbsolutePath().toString(),
                                                  "/target/incremental/io.takari.maven.plugins_takari-lifecycle-plugin_compile_compile");
        Assert.assertTrue(incrementalConfiguration.toFile().exists());

        InternalNIOTestUtil.rm(tmpRoot.toFile());
    }

    @Test
    public void testIncrementalWithPluginEnabledThreeTime() throws Exception {
        Path tmpRoot = Files.createTempDirectory("repo");
        Path tmp = Files.createDirectories(Paths.get(tmpRoot.toString(),
                                                     "dummy"));
        //NIO creation and copy content
        java.nio.file.Path temp = java.nio.file.Files.createDirectories(java.nio.file.Paths.get(tmpRoot.toString(),
                                                                                                "dummy"));
        TestUtil.copyTree(java.nio.file.Paths.get("src/test/projects/dummy"),
                          temp);
        //end NIO

        InternalNIOMavenCompiler compiler = InternalNIOMavenCompilerFactory.getCompiler(
                Decorator.NONE);

        InternalNIOWorkspaceCompilationInfo info = new InternalNIOWorkspaceCompilationInfo(tmp);
        InternalNIOCompilationRequest req = new InternalNIODefaultCompilationRequest(mavenRepo.toAbsolutePath().toString(),
                                                                                     info,
                                                                                     new String[]{MavenCLIArgs.CLEAN, MavenCLIArgs.COMPILE},
                                                                                     new HashMap<>(),
                                                                                     Boolean.FALSE);
        CompilationResponse res = compiler.compileSync(req);
        if (res.getMavenOutput().isPresent() && !res.isSuccessful()) {
            TestUtil.writeMavenOutputIntoTargetFolder(res.getMavenOutput().get(),
                                                      "InternalNIODefaultMavenIncrementalCompilerTest.testIncrementalWithPluginEnabledThreeTime");
        }
        Assert.assertTrue(res.isSuccessful());

        res = compiler.compileSync(req);
        Assert.assertTrue(res.isSuccessful());

        res = compiler.compileSync(req);
        Assert.assertTrue(res.isSuccessful());

        Path incrementalConfiguration = Paths.get(tmp.toAbsolutePath().toString(),
                                                  "/target/incremental/io.takari.maven.plugins_takari-lifecycle-plugin_compile_compile");
        Assert.assertTrue(incrementalConfiguration.toFile().exists());

        InternalNIOTestUtil.rm(tmpRoot.toFile());
    }

    @Test
    public void testCheckIncrementalWithChanges() throws Exception {
        Path tmpRoot = Files.createTempDirectory("repo");
        Path tmp = Files.createDirectories(Paths.get(tmpRoot.toString(),
                                                     "dummy"));
        //NIO creation and copy content
        java.nio.file.Path temp = java.nio.file.Files.createDirectories(java.nio.file.Paths.get(tmpRoot.toString(),
                                                                                                "dummy"));
        TestUtil.copyTree(java.nio.file.Paths.get("src/test/projects/dummy_incremental"),
                          temp);
        //end NIO

        //compiler
        InternalNIOMavenCompiler compiler = InternalNIOMavenCompilerFactory.getCompiler(Decorator.LOG_OUTPUT_AFTER);
        InternalNIOWorkspaceCompilationInfo info = new InternalNIOWorkspaceCompilationInfo(tmp);
        InternalNIOCompilationRequest req = new InternalNIODefaultCompilationRequest(mavenRepo.toAbsolutePath().toString(),
                                                                                     info,
                                                                                     new String[]{MavenCLIArgs.COMPILE},
                                                                                     new HashMap<>(),
                                                                                     Boolean.TRUE);
        CompilationResponse res = compiler.compileSync(req);
        if (res.getMavenOutput().isPresent() && !res.isSuccessful()) {
            TestUtil.writeMavenOutputIntoTargetFolder(res.getMavenOutput().get(),
                                                      "InternalNIODefaultMavenIncrementalCompilerTest.testCheckIncrementalWithChanges");
        }

        //checks
        Assert.assertTrue(res.isSuccessful());

        List<String> fileNames = new ArrayList<>();
        try (DirectoryStream<java.nio.file.Path> directoryStream = java.nio.file.Files.newDirectoryStream(java.nio.file.Paths.get(tmp + "/target/classes/dummy"))) {
            for (java.nio.file.Path path : directoryStream) {
                fileNames.add(path.toString());
            }
        }
        Assert.assertTrue(fileNames.size() == 2);
        String dummyJava;
        if (fileNames.get(0).endsWith("Dummy.class")) {
            dummyJava = fileNames.get(0);
        } else {
            dummyJava = fileNames.get(1);
        }
        long dummyJavaSize = Paths.get(dummyJava).toFile().length();

        Assert.assertTrue(res.getMavenOutput().isPresent());
        List<String> output = res.getMavenOutput().get();
        Assert.assertTrue(isPresent(output,
                                    "Previous incremental build state does not exist, performing full build"));
        Assert.assertTrue(isPresent(output,
                                    "Compiled 2 out of 2 sources "));

        Files.delete(Paths.get(tmp + "/src/main/java/dummy/DummyA.java"));
        //overwrite the class with a new version with two additional methods and one int variable
        Files.write(Paths.get(tmp + "/src/main/java/dummy/Dummy.java"),
                    Files.readAllBytes(Paths.get("src/test/projects/Dummy.java")));

        //second compilation
        res = compiler.compileSync(req);

        //checks
        Assert.assertTrue(res.isSuccessful());

        fileNames = new ArrayList<>();
        //nio
        try (org.uberfire.java.nio.file.DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(tmp + "/target/classes/dummy"))) {
            for (Path path : directoryStream) {
                fileNames.add(path.toString());
            }
        }

        Assert.assertTrue(fileNames.size() == 1);
        Assert.assertTrue(fileNames.get(0).endsWith("Dummy.class"));
        long dummyJavaSizeAfterChanges = Paths.get(dummyJava).toFile().length();
        Assert.assertTrue(dummyJavaSize < dummyJavaSizeAfterChanges);

        Assert.assertTrue(res.getMavenOutput().isPresent());
        output = res.getMavenOutput().get();
        Assert.assertTrue(isPresent(output,
                                    "Performing incremental build"));
        Assert.assertTrue(isPresent(output,
                                    "Compiled 1 out of 1 sources "));

        InternalNIOTestUtil.rm(tmpRoot.toFile());
    }

    private boolean isPresent(List<String> output,
                              String text) {
        return output.stream().anyMatch(s -> s.contains(text));
    }
}
