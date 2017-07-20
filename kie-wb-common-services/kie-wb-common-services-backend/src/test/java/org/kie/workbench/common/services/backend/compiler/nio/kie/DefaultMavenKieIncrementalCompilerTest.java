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
package org.kie.workbench.common.services.backend.compiler.nio.kie;

import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.services.backend.compiler.CompilationResponse;
import org.kie.workbench.common.services.backend.compiler.TestUtil;
import org.kie.workbench.common.services.backend.compiler.configuration.KieDecorator;
import org.kie.workbench.common.services.backend.compiler.configuration.MavenCLIArgs;
import org.kie.workbench.common.services.backend.compiler.nio.NIOCompilationRequest;
import org.kie.workbench.common.services.backend.compiler.nio.NIOKieMavenCompiler;
import org.kie.workbench.common.services.backend.compiler.nio.impl.NIODefaultCompilationRequest;
import org.kie.workbench.common.services.backend.compiler.nio.impl.NIOWorkspaceCompilationInfo;
import org.kie.workbench.common.services.backend.compiler.nio.impl.kie.NIOKieMavenCompilerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultMavenKieIncrementalCompilerTest {

    private Path mavenRepo;
    private Logger logger = LoggerFactory.getLogger(DefaultMavenKieIncrementalCompilerTest.class);

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
        TestUtil.copyTree(Paths.get("src/test/projects/dummy"),
                          tmp);

        NIOKieMavenCompiler compiler = NIOKieMavenCompilerFactory.getCompiler(
                KieDecorator.NONE);

        NIOWorkspaceCompilationInfo info = new NIOWorkspaceCompilationInfo(tmp);
        NIOCompilationRequest req = new NIODefaultCompilationRequest(mavenRepo.toAbsolutePath().toString(),
                                                                     info,
                                                                     new String[]{MavenCLIArgs.VERSION},
                                                                     new HashMap(),
                                                                     Boolean.FALSE);
        CompilationResponse res = compiler.compileSync(req);
        Assert.assertTrue(res.isSuccessful());

        TestUtil.rm(tmpRoot.toFile());
    }

    @Test
    public void testIncrementalWithPluginEnabled() throws Exception {
        Path tmpRoot = Files.createTempDirectory("repo");
        Path tmp = Files.createDirectories(Paths.get(tmpRoot.toString(),
                                                     "dummy"));
        TestUtil.copyTree(Paths.get("src/test/projects/dummy"),
                          tmp);

        NIOKieMavenCompiler compiler = NIOKieMavenCompilerFactory.getCompiler(KieDecorator.NONE);

        NIOWorkspaceCompilationInfo info = new NIOWorkspaceCompilationInfo(tmp);
        NIOCompilationRequest req = new NIODefaultCompilationRequest(mavenRepo.toAbsolutePath().toString(),
                                                                     info,
                                                                     new String[]{MavenCLIArgs.CLEAN, MavenCLIArgs.COMPILE},
                                                                     new HashMap<>(),
                                                                     Boolean.FALSE);
        CompilationResponse res = compiler.compileSync(req);
        Assert.assertTrue(res.isSuccessful());

        Path incrementalConfiguration = Paths.get(tmp.toAbsolutePath().toString(),
                                                  "/target/incremental/io.takari.maven.plugins_takari-lifecycle-plugin_compile_compile");
        Assert.assertTrue(incrementalConfiguration.toFile().exists());

        TestUtil.rm(tmpRoot.toFile());
    }

    @Test
    public void testIncrementalWithPluginEnabledThreeBuild() throws Exception {
        Path tmpRoot = Files.createTempDirectory("repo");
        Path tmp = Files.createDirectories(Paths.get(tmpRoot.toString(),
                                                     "dummy"));
        TestUtil.copyTree(Paths.get("src/test/projects/dummy"),
                          tmp);

        NIOKieMavenCompiler compiler = NIOKieMavenCompilerFactory.getCompiler(
                KieDecorator.NONE);

        NIOWorkspaceCompilationInfo info = new NIOWorkspaceCompilationInfo(tmp);
        NIOCompilationRequest req = new NIODefaultCompilationRequest(mavenRepo.toAbsolutePath().toString(),
                                                                     info,
                                                                     new String[]{MavenCLIArgs.CLEAN, MavenCLIArgs.COMPILE},
                                                                     new HashMap<>(),
                                                                     Boolean.FALSE);
        CompilationResponse res = compiler.compileSync(req);
        Assert.assertTrue(res.isSuccessful());

        res = compiler.compileSync(req);
        Assert.assertTrue(res.isSuccessful());

        res = compiler.compileSync(req);
        Assert.assertTrue(res.isSuccessful());

        Path incrementalConfiguration = Paths.get(tmp.toAbsolutePath().toString(),
                                                  "/target/incremental/io.takari.maven.plugins_takari-lifecycle-plugin_compile_compile");
        Assert.assertTrue(incrementalConfiguration.toFile().exists());

        TestUtil.rm(tmpRoot.toFile());
    }

    @Test
    public void testCheckIncrementalWithChanges() throws Exception {
        Path tmpRoot = Files.createTempDirectory("repo");
        Path tmp = Files.createDirectories(Paths.get(tmpRoot.toString(),
                                                     "dummy"));
        TestUtil.copyTree(Paths.get("src/test/projects/dummy_kie_incremental"),
                          tmp);

        //compiler
        NIOKieMavenCompiler compiler = NIOKieMavenCompilerFactory.getCompiler(KieDecorator.LOG_OUTPUT_AFTER);
        NIOWorkspaceCompilationInfo info = new NIOWorkspaceCompilationInfo(tmp);
        NIOCompilationRequest req = new NIODefaultCompilationRequest(mavenRepo.toAbsolutePath().toString(),
                                                                     info,
                                                                     new String[]{MavenCLIArgs.COMPILE},
                                                                     new HashMap<>(),
                                                                     Boolean.TRUE);
        CompilationResponse res = compiler.compileSync(req);

        //checks
        Assert.assertTrue(res.isSuccessful());
        List<String> fileNames = new ArrayList<>();
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(tmp + "/target/classes/dummy"))) {
            for (Path path : directoryStream) {
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
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(tmp + "/target/classes/dummy"))) {
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

        TestUtil.rm(tmpRoot.toFile());
    }

    private boolean isPresent(List<String> output,
                              String text) {
        return output.stream().anyMatch(s -> s.contains(text));
    }
}
