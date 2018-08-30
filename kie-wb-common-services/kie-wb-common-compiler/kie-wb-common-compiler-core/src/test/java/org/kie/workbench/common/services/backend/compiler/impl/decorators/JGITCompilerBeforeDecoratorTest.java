/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.services.backend.compiler.impl.decorators;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jgit.api.Git;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.kie.workbench.common.services.backend.compiler.BaseCompilerTest;
import org.kie.workbench.common.services.backend.compiler.CompilationRequest;
import org.kie.workbench.common.services.backend.compiler.CompilationResponse;
import org.kie.workbench.common.services.backend.compiler.TestUtilGit;
import org.kie.workbench.common.services.backend.compiler.TestUtilMaven;
import org.kie.workbench.common.services.backend.compiler.configuration.MavenCLIArgs;
import org.kie.workbench.common.services.backend.compiler.impl.BaseMavenCompiler;
import org.kie.workbench.common.services.backend.compiler.impl.DefaultCompilationRequest;
import org.kie.workbench.common.services.backend.compiler.impl.WorkspaceCompilationInfo;
import org.kie.workbench.common.services.backend.utils.TestUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.Paths;
import org.uberfire.java.nio.fs.jgit.JGitFileSystem;
import org.uberfire.mocks.FileSystemTestingUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.workbench.common.services.backend.constants.TestConstants.TARGET_TAKARI_PLUGIN;

public class JGITCompilerBeforeDecoratorTest {

    private FileSystemTestingUtils fileSystemTestingUtils = new FileSystemTestingUtils();
    private IOService ioService;
    private String mavenRepo;
    private static Logger logger = LoggerFactory.getLogger(JGITCompilerBeforeDecoratorTest.class);

    @Rule
    public TestName testName = new TestName();


    @BeforeClass
    public static void setupSystemProperties() {
        //These are not needed for the tests
        System.setProperty("org.uberfire.nio.git.daemon.enabled",
                           "false");
        System.setProperty("org.uberfire.nio.git.ssh.enabled",
                           "false");
        System.setProperty("org.uberfire.sys.repo.monitor.disabled",
                           "true");
    }

    @AfterClass
    public static void restoreSystemProperties() {
        System.clearProperty("org.uberfire.sys.repo.monitor.disabled");
        System.clearProperty("org.uberfire.nio.git.daemon.enabled");
        System.clearProperty("org.uberfire.nio.git.ssh.enabled");
        System.clearProperty("org.uberfire.sys.repo.monitor.disabled");
    }

    @Before
    public void setUp() throws Exception {
        fileSystemTestingUtils.setup();
        ioService = fileSystemTestingUtils.getIoService();

        mavenRepo = TestUtilMaven.getMavenRepo();
    }

    @After
    public void tearDown() {
        fileSystemTestingUtils.cleanup();
        TestUtil.rm(new File("src/../.security/"));
    }

    @Test
    public void compileTestTwo() throws Exception {
        final FileSystem fileSystem = createFileSystem("myrepodecorator");

        //Compile the repo
        JGITCompilerBeforeDecorator compiler = new JGITCompilerBeforeDecorator(new BaseMavenCompiler());
        WorkspaceCompilationInfo info = new WorkspaceCompilationInfo(fileSystem.getPath("/"));
        CompilationRequest req = new DefaultCompilationRequest(mavenRepo,
                                                               info,
                                                               new String[]{MavenCLIArgs.COMPILE},
                                                               Boolean.TRUE);
        CompilationResponse res = compiler.compile(req);

        final java.nio.file.Path tempPath = ((Git) compiler.getGitMap().get(fileSystem)).getRepository().getDirectory().toPath().getParent();
        TestUtil.saveMavenLogIfCompilationResponseNotSuccessfull(tempPath, res, this.getClass(), testName);

        assertThat(res.isSuccessful()).isTrue();

        final Path incrementalConfiguration = Paths.get(tempPath.toUri() + TARGET_TAKARI_PLUGIN);
        assertThat(incrementalConfiguration.toFile()).exists();

        TestUtil.rm(tempPath.toFile());
    }

    @Test
    public void compileWithOverrideTest() throws Exception {
        final FileSystem fileSystem = createFileSystem("myrepo");

        //Compile the repo
        WorkspaceCompilationInfo info = new WorkspaceCompilationInfo(fileSystem.getPath("/"));
        CompilationRequest req = new DefaultCompilationRequest(mavenRepo,
                                                               info,
                                                               new String[]{MavenCLIArgs.COMPILE},
                                                               Boolean.TRUE);

        Map<Path, InputStream> override = new HashMap<>();
        org.uberfire.java.nio.file.Path path = fileSystem.getPath("/", "/dummyA/src/main/java/dummy/Person.java");
        InputStream input = new FileInputStream(new File("target/test-classes/kjar-2-single-resources_override/src/main/java/dummy/PersonOverride.java"));
        override.put(path, input);

        JGITCompilerBeforeDecorator compiler = new JGITCompilerBeforeDecorator(new BaseMavenCompiler());
        CompilationResponse res = compiler.compile(req, override);

        final java.nio.file.Path tempPath = ((Git) compiler.getGitMap().get(fileSystem)).getRepository().getDirectory().toPath().getParent();
        TestUtil.saveMavenLogIfCompilationResponseNotSuccessfull(tempPath, res, this.getClass(), testName);

        assertThat(res.isSuccessful()).isTrue();

        final Path incrementalConfiguration = Paths.get(tempPath.toUri() + TARGET_TAKARI_PLUGIN);
        assertThat(incrementalConfiguration.toFile().exists()).isTrue();

        TestUtil.rm(tempPath.toFile());
    }

    @Test
    public void compileWithEmptyOverrideTest() throws Exception {
        final FileSystem fileSystem = createFileSystem("myrepo");

        //Compile the repo

        WorkspaceCompilationInfo info = new WorkspaceCompilationInfo(fileSystem.getPath("/"));
        CompilationRequest req = new DefaultCompilationRequest(mavenRepo,
                                                               info,
                                                               new String[]{MavenCLIArgs.COMPILE},
                                                               Boolean.TRUE);

        Map<Path, InputStream> override = new HashMap<>();

        JGITCompilerBeforeDecorator compiler = new JGITCompilerBeforeDecorator(new BaseMavenCompiler());
        CompilationResponse res = compiler.compile(req, override);

        final java.nio.file.Path tempPath = ((Git) compiler.getGitMap().get(fileSystem)).getRepository().getDirectory().toPath().getParent();

        TestUtil.saveMavenLogIfCompilationResponseNotSuccessfull(tempPath, res, this.getClass(), testName);
        assertThat(res.isSuccessful()).isTrue();

        Path incrementalConfiguration = Paths.get(tempPath.toUri() + TARGET_TAKARI_PLUGIN);
        assertThat(incrementalConfiguration.toFile()).exists();

        TestUtil.rm(tempPath.toFile());
    }

    private FileSystem createFileSystem(String repoName) throws Exception {
        HashMap<String, Object> env = new HashMap<>();
        env.put("init", Boolean.TRUE);
        env.put("internal", Boolean.TRUE);

        final JGitFileSystem fs = (JGitFileSystem) ioService.newFileSystem(URI.create("git://" + repoName), env);

        ioService.startBatch(fs);

        ioService.write(fs.getPath("/pom.xml"),
                        new String(java.nio.file.Files.readAllBytes(new File("src/test/projects/dummy_multimodule_untouched/pom.xml").toPath())));
        ioService.write(fs.getPath("/dummyA/src/main/java/dummy/DummyA.java"),
                        new String(java.nio.file.Files.readAllBytes(new File("src/test/projects/dummy_multimodule_untouched/dummyA/src/main/java/dummy/DummyA.java").toPath())));
        ioService.write(fs.getPath("/dummyB/src/main/java/dummy/DummyB.java"),
                        new String(java.nio.file.Files.readAllBytes(new File("src/test/projects/dummy_multimodule_untouched/dummyB/src/main/java/dummy/DummyB.java").toPath())));
        ioService.write(fs.getPath("/dummyA/pom.xml"),
                        new String(java.nio.file.Files.readAllBytes(new File("src/test/projects/dummy_multimodule_untouched/dummyA/pom.xml").toPath())));
        ioService.write(fs.getPath("/dummyB/pom.xml"),
                        new String(java.nio.file.Files.readAllBytes(new File("src/test/projects/dummy_multimodule_untouched/dummyB/pom.xml").toPath())));
        ioService.endBatch();

        return fs;
    }
}
