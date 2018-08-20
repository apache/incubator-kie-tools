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
package org.kie.workbench.common.services.backend.compiler;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.kie.workbench.common.services.backend.compiler.configuration.KieDecorator;
import org.kie.workbench.common.services.backend.compiler.configuration.MavenCLIArgs;
import org.kie.workbench.common.services.backend.compiler.impl.DefaultCompilationRequest;
import org.kie.workbench.common.services.backend.compiler.impl.WorkspaceCompilationInfo;
import org.kie.workbench.common.services.backend.compiler.impl.kie.KieMavenCompilerFactory;
import org.kie.workbench.common.services.backend.constants.ResourcesConstants;
import org.kie.workbench.common.services.backend.utils.TestUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.java.nio.file.DirectoryStream;
import org.uberfire.java.nio.file.Files;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;

public class DefaultMavenIncrementalCompilerTest {

    private String mavenRepo;
    private Path tmpRoot;
    private Logger logger = LoggerFactory.getLogger(DefaultMavenIncrementalCompilerTest.class);

    @Rule
    public TestName testName = new TestName();

    @Before
    public void setUp() throws Exception {
        mavenRepo = TestUtilMaven.getMavenRepo();
        tmpRoot = Files.createTempDirectory("repo");
    }

    @After
    public void cleanUp() {
        if (tmpRoot != null) {
            TestUtil.rm(tmpRoot.toFile());
        }
    }

    @Test
    public void testIsValidMavenHome() throws Exception {
        Path temp = TestUtil.createAndCopyToDirectory(tmpRoot, "dummy", ResourcesConstants.DUMMY_DIR);

        AFCompiler compiler = KieMavenCompilerFactory.getCompiler(KieDecorator.NONE);
        WorkspaceCompilationInfo info = new WorkspaceCompilationInfo(temp);
        CompilationRequest req = new DefaultCompilationRequest(mavenRepo,
                                                               info,
                                                               new String[]{MavenCLIArgs.VERSION},
                                                               Boolean.TRUE);
        CompilationResponse res = compiler.compile(req);
        TestUtil.saveMavenLogIfCompilationResponseNotSuccessfull(temp, res, this.getClass(), testName);
        assertThat(res.isSuccessful()).isTrue();
    }

    @Test
    public void testIncrementalWithPluginEnabled() throws Exception {
        Path temp = TestUtil.createAndCopyToDirectory(tmpRoot, "dummy", ResourcesConstants.DUMMY_DIR);

        AFCompiler compiler = KieMavenCompilerFactory.getCompiler(KieDecorator.NONE);
        WorkspaceCompilationInfo info = new WorkspaceCompilationInfo(temp);
        CompilationRequest req = new DefaultCompilationRequest(mavenRepo,
                                                               info,
                                                               new String[]{MavenCLIArgs.COMPILE},
                                                               Boolean.TRUE);
        CompilationResponse res = compiler.compile(req);
        TestUtil.saveMavenLogIfCompilationResponseNotSuccessfull(temp, res, this.getClass(), testName);
        assertThat(res.isSuccessful()).isTrue();

        Path incrementalConfiguration = Paths.get(temp.toAbsolutePath().toString(),
                                                  "/target/incremental/io.takari.maven.plugins_takari-lifecycle-plugin_compile_compile");
        assertThat(incrementalConfiguration.toFile()).exists();
    }

    @Test
    public void testIncrementalWithPluginEnabledThreeTime() throws Exception {
        Path temp = TestUtil.createAndCopyToDirectory(tmpRoot, "dummy", ResourcesConstants.DUMMY_DIR);

        AFCompiler compiler = KieMavenCompilerFactory.getCompiler(KieDecorator.NONE);

        WorkspaceCompilationInfo info = new WorkspaceCompilationInfo(temp);
        CompilationRequest req = new DefaultCompilationRequest(mavenRepo,
                                                               info,
                                                               new String[]{MavenCLIArgs.COMPILE},
                                                               Boolean.TRUE);
        CompilationResponse res = compiler.compile(req);
        TestUtil.saveMavenLogIfCompilationResponseNotSuccessfull(temp, res, this.getClass(), testName);
        assertThat(res.isSuccessful()).isTrue();

        res = compiler.compile(req);
        assertThat(res.isSuccessful()).isTrue();

        res = compiler.compile(req);
        assertThat(res.isSuccessful()).isTrue();

        Path incrementalConfiguration = Paths.get(temp.toAbsolutePath().toString(),
                                                  "/target/incremental/io.takari.maven.plugins_takari-lifecycle-plugin_compile_compile");
        assertThat(incrementalConfiguration.toFile()).exists();
    }

    @Test
    public void testCheckIncrementalWithChanges() throws Exception {
        Path temp = TestUtil.createAndCopyToDirectory(tmpRoot, "dummy", ResourcesConstants.DUMMY_INCREMENTAL_DIR);

        //compiler
        AFCompiler compiler = KieMavenCompilerFactory.getCompiler(KieDecorator.LOG_OUTPUT_AFTER);
        WorkspaceCompilationInfo info = new WorkspaceCompilationInfo(temp);
        CompilationRequest req = new DefaultCompilationRequest(mavenRepo,
                                                               info,
                                                               new String[]{MavenCLIArgs.COMPILE},
                                                               Boolean.TRUE);
        CompilationResponse res = compiler.compile(req);
        TestUtil.saveMavenLogIfCompilationResponseNotSuccessfull(temp, res, this.getClass(), testName);

        //checks
        assertThat(res.isSuccessful()).isTrue();

        List<String> fileNames = new ArrayList<>();
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(temp + "/target/classes/dummy"))) {
            for (Path path : directoryStream) {
                fileNames.add(path.toString());
            }
        }
        assertThat(fileNames).hasSize(2);
        String dummyJava;
        if (fileNames.get(0).endsWith("Dummy.class")) {
            dummyJava = fileNames.get(0);
        } else {
            dummyJava = fileNames.get(1);
        }
        long dummyJavaSize = Paths.get(dummyJava).toFile().length();

        List<String> output = res.getMavenOutput();
        assertThat(isTextPresent(output,
                                 "Previous incremental build state does not exist, performing full build")).isTrue();
        assertThat(isTextPresent(output,
                                 "Previous incremental build state does not exist, performing full build")).isTrue();
        assertThat(isTextPresent(output,
                                 "Compiled 2 out of 2 sources ")).isTrue();

        Files.delete(Paths.get(temp + "/src/main/java/dummy/DummyA.java"));
        //overwrite the class with a new version with two additional methods and one int variable
        Files.write(Paths.get(temp + "/src/main/java/dummy/Dummy.java"),
                    Files.readAllBytes(Paths.get("src/test/projects/Dummy.java")));

        //second compilation
        res = compiler.compile(req);

        //checks
        assertThat(res.isSuccessful()).isTrue();

        fileNames = new ArrayList<>();
        //impl
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(temp + "/target/classes/dummy"))) {
            for (Path path : directoryStream) {
                fileNames.add(path.toString());
            }
        }

        assertThat(fileNames).hasSize(1);
        assertThat(fileNames.get(0)).endsWith("Dummy.class");
        long dummyJavaSizeAfterChanges = Paths.get(dummyJava).toFile().length();
        assertThat(dummyJavaSize).isLessThan(dummyJavaSizeAfterChanges);

        output = res.getMavenOutput();
        assertThat(isTextPresent(output,
                                 "Performing incremental build")).isTrue();
        assertThat(isTextPresent(output,
                                 "Compiled 1 out of 1 sources ")).isTrue();
    }

    @Test
    public void testError() throws Exception {
        Path temp = TestUtil.createAndCopyToDirectory(tmpRoot, "dummy", ResourcesConstants.DUMMY_KIE_MULTIMODULE_UNTOUCHED_WITH_ERROR_DIR);

        //compiler
        AFCompiler compiler = KieMavenCompilerFactory.getCompiler(KieDecorator.LOG_OUTPUT_AFTER);
        WorkspaceCompilationInfo info = new WorkspaceCompilationInfo(temp);
        CompilationRequest req = new DefaultCompilationRequest(mavenRepo,
                                                               info,
                                                               new String[]{MavenCLIArgs.COMPILE, MavenCLIArgs.FAIL_NEVER},
                                                               Boolean.TRUE);
        CompilationResponse res = compiler.compile(req);
        TestUtil.saveMavenLogIfCompilationResponseNotSuccessfull(temp, res, this.getClass(), testName);
        //checks
        assertThat(res.isSuccessful()).isTrue();

        List<String> fileNames = new ArrayList<>();
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(temp + "/dummyA/target/classes/dummy"))) {
            for (Path path : directoryStream) {
                fileNames.add(path.toString());
            }
        }
        assertThat(fileNames).hasSize(1);
        String dummyAJava = null;
        if (fileNames.get(0).endsWith("DummyA.class")) {
            dummyAJava = fileNames.get(0);
        }
        assertThat(dummyAJava).isNotNull();

        fileNames = new ArrayList<>();
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(temp + "/dummyB/target/classes/dummy"))) {
            for (Path path : directoryStream) {
                fileNames.add(path.toString());
            }
        }

        assertThat(fileNames).hasSize(1);
        String dummyBJava = null;
        if (fileNames.get(0).endsWith("DummyB.class")) {
            dummyBJava = fileNames.get(0);
        }
        assertThat(dummyBJava).isNotNull();

        List<String> output = res.getMavenOutput();
        assertThat(isTextPresent(output,
                                 "Previous incremental build state does not exist, performing full build")).isTrue();
        assertThat(isTextPresent(output,
                                 "Compiled 2 out of 2 sources ")).isTrue();

        assertThat(isTextPresent(output,
                                 "Compiled 1 out of 1 sources ")).isTrue();
    }

    private boolean isTextPresent(List<String> output,
                                  String text) {
        return output.stream().anyMatch(s -> s.contains(text));
    }
}
