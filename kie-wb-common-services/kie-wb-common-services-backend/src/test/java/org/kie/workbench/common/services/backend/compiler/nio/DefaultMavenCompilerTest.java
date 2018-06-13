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

package org.kie.workbench.common.services.backend.compiler.nio;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PullCommand;
import org.eclipse.jgit.api.PullResult;
import org.eclipse.jgit.api.RebaseCommand;
import org.eclipse.jgit.api.RebaseResult;
import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.services.backend.compiler.CompilationResponse;
import org.kie.workbench.common.services.backend.compiler.TestUtil;
import org.kie.workbench.common.services.backend.compiler.configuration.Decorator;
import org.kie.workbench.common.services.backend.compiler.configuration.MavenCLIArgs;
import org.kie.workbench.common.services.backend.compiler.nio.impl.DefaultCompilationRequest;
import org.kie.workbench.common.services.backend.compiler.nio.impl.MavenCompilerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.Files;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.Paths;
import org.uberfire.java.nio.fs.jgit.JGitFileSystem;
import org.uberfire.mocks.FileSystemTestingUtils;

import static org.junit.Assert.*;

public class DefaultMavenCompilerTest {

    private FileSystemTestingUtils fileSystemTestingUtils = new FileSystemTestingUtils();
    private IOService ioService;
    private static final Logger logger = LoggerFactory.getLogger(DefaultMavenCompilerTest.class);

    private Path mavenRepo;

    @Before
    public void setUp() throws Exception {
        fileSystemTestingUtils.setup();
        ioService = fileSystemTestingUtils.getIoService();

        mavenRepo = Paths.get(System.getProperty("user.home"),
                              "/.m2/repository");

        if (!Files.exists(mavenRepo)) {
            logger.info("Creating a m2_repo into " + mavenRepo);
            if (!Files.exists(Files.createDirectories(mavenRepo))) {
                throw new Exception("Folder not writable in the project");
            }
        }
    }

    @After
    public void tearDown() throws IOException {
        fileSystemTestingUtils.cleanup();
        TestUtil.rm(new File("src/../.security/"));
    }

    @Test
    public void buildWithCloneTest() throws Exception {
        final String repoName = "myrepo";
        final JGitFileSystem fs = (JGitFileSystem) ioService.newFileSystem(URI.create("git://" + repoName),
                                                                           new HashMap<String, Object>() {{
                                                                               put("init",
                                                                                   Boolean.TRUE);
                                                                               put("internal",
                                                                                   Boolean.TRUE);
                                                                           }});

        ioService.startBatch(fs);

        ioService.write(fs.getPath("/dummy/pom.xml"),
                        new String(java.nio.file.Files.readAllBytes(new File("src/test/projects/dummy_multimodule_untouched/pom.xml").toPath())));
        ioService.write(fs.getPath("/dummy/dummyA/src/main/java/dummy/DummyA.java"),
                        new String(java.nio.file.Files.readAllBytes(new File("src/test/projects/dummy_multimodule_untouched/dummyA/src/main/java/dummy/DummyA.java").toPath())));
        ioService.write(fs.getPath("/dummy/dummyB/src/main/java/dummy/DummyB.java"),
                        new String(java.nio.file.Files.readAllBytes(new File("src/test/projects/dummy_multimodule_untouched/dummyB/src/main/java/dummy/DummyB.java").toPath())));
        ioService.write(fs.getPath("/dummy/dummyA/pom.xml"),
                        new String(java.nio.file.Files.readAllBytes(new File("src/test/projects/dummy_multimodule_untouched/dummyA/pom.xml").toPath())));
        ioService.write(fs.getPath("/dummy/dummyB/pom.xml"),
                        new String(java.nio.file.Files.readAllBytes(new File("src/test/projects/dummy_multimodule_untouched/dummyB/pom.xml").toPath())));
        ioService.endBatch();

        Path tmpRootCloned = Files.createTempDirectory("cloned");

        Path tmpCloned = Files.createDirectories(Paths.get(tmpRootCloned.toString(),
                                                           "dummy"));

        final File gitClonedFolder = new File(tmpCloned.toFile(),
                                              ".clone.git");

        final Git cloned = Git.cloneRepository().setURI(fs.getGit().getRepository().getDirectory().toURI().toString()).setBare(false).setDirectory(gitClonedFolder).call();

        assertNotNull(cloned);

        //Compile the repo
        AFCompiler compiler = MavenCompilerFactory.getCompiler(Decorator.LOG_OUTPUT_AFTER);
        Path prjFolder = Paths.get(gitClonedFolder + "/dummy/");
        byte[] encoded = Files.readAllBytes(Paths.get(prjFolder + "/pom.xml"));
        String pomAsAstring = new String(encoded,
                                         StandardCharsets.UTF_8);
        Assert.assertFalse(pomAsAstring.contains("<artifactId>takari-lifecycle-plugin</artifactId>"));

        WorkspaceCompilationInfo info = new WorkspaceCompilationInfo(prjFolder);
        CompilationRequest req = new DefaultCompilationRequest(mavenRepo.toAbsolutePath().toString(),
                                                               info,
                                                               new String[]{MavenCLIArgs.COMPILE, MavenCLIArgs.OFFLINE},
                                                               new HashMap<>(),
                                                               Boolean.TRUE);

        CompilationResponse res = compiler.compileSync(req);
        if (res.getMavenOutput().isPresent() && !res.isSuccessful()) {
            TestUtil.writeMavenOutputIntoTargetFolder(res.getMavenOutput().get(),
                                                      "KieDefaultMavenCompilerOnInMemoryFSTest.buildWithCloneTest");
        }
        assertTrue(res.getMavenOutput().isPresent());
        assertTrue(res.isSuccessful());

        Path incrementalConfiguration = Paths.get(prjFolder + "/target/incremental/io.takari.maven.plugins_takari-lifecycle-plugin_compile_compile");
        assertTrue(incrementalConfiguration.toFile().exists());

        encoded = Files.readAllBytes(Paths.get(prjFolder + "/pom.xml"));
        pomAsAstring = new String(encoded,
                                  StandardCharsets.UTF_8);
        assertTrue(pomAsAstring.contains("<artifactId>takari-lifecycle-plugin</artifactId>"));

        TestUtil.rm(tmpRootCloned.toFile());
    }

    @Test
    public void buildWithPullRebaseUberfireTest() throws Exception {

        //Setup origin in memory
        final URI originRepo = URI.create("git://repo");
        final JGitFileSystem origin = (JGitFileSystem) ioService.newFileSystem(originRepo,
                                                                               new HashMap<String, Object>() {{
                                                                                   put("init",
                                                                                       Boolean.TRUE);
                                                                                   put("internal",
                                                                                       Boolean.TRUE);
                                                                                   put("listMode",
                                                                                       "ALL");
                                                                               }});
        ioService.startBatch(origin);

        ioService.write(origin.getPath("/dummy/pom.xml"),
                        new String(java.nio.file.Files.readAllBytes(new File("src/test/projects/dummy_multimodule_untouched/pom.xml").toPath())));
        ioService.write(origin.getPath("/dummy/dummyA/src/main/java/dummy/DummyA.java"),
                        new String(java.nio.file.Files.readAllBytes(new File("src/test/projects/dummy_multimodule_untouched/dummyA/src/main/java/dummy/DummyA.java").toPath())));
        ioService.write(origin.getPath("/dummy/dummyB/src/main/java/dummy/DummyB.java"),
                        new String(java.nio.file.Files.readAllBytes(new File("src/test/projects/dummy_multimodule_untouched/dummyB/src/main/java/dummy/DummyB.java").toPath())));
        ioService.write(origin.getPath("/dummy/dummyA/pom.xml"),
                        new String(java.nio.file.Files.readAllBytes(new File("src/test/projects/dummy_multimodule_untouched/dummyA/pom.xml").toPath())));
        ioService.write(origin.getPath("/dummy/dummyB/pom.xml"),
                        new String(java.nio.file.Files.readAllBytes(new File("src/test/projects/dummy_multimodule_untouched/dummyB/pom.xml").toPath())));
        ioService.endBatch();

        // clone into a regularfs
        Path tmpRootCloned = Files.createTempDirectory("cloned");
        Path tmpCloned = Files.createDirectories(Paths.get(tmpRootCloned.toString(),
                                                           ".clone"));

        final Git cloned = Git.cloneRepository().setURI(origin.getGit().getRepository().getDirectory().toURI().toString()).setBare(false).setDirectory(tmpCloned.toFile()).call();

        assertNotNull(cloned);

        PullCommand pc = cloned.pull().setRemote("origin").setRebase(Boolean.TRUE);
        PullResult pullRes = pc.call();
        assertTrue(pullRes.getRebaseResult().getStatus().equals(RebaseResult.Status.UP_TO_DATE));// nothing changed yet

        RebaseCommand rb = cloned.rebase().setUpstream("origin/master");
        RebaseResult rbResult = rb.setPreserveMerges(true).call();
        assertTrue(rbResult.getStatus().isSuccessful());

        //Compile the repo
        AFCompiler compiler = MavenCompilerFactory.getCompiler(Decorator.LOG_OUTPUT_AFTER);

        byte[] encoded = Files.readAllBytes(Paths.get(tmpCloned + "/dummy/pom.xml"));
        String pomAsAstring = new String(encoded,
                                         StandardCharsets.UTF_8);
        Assert.assertFalse(pomAsAstring.contains("<artifactId>takari-lifecycle-plugin</artifactId>"));

        Path prjFolder = Paths.get(tmpCloned + "/dummy/");

        WorkspaceCompilationInfo info = new WorkspaceCompilationInfo(prjFolder);
        CompilationRequest req = new DefaultCompilationRequest(mavenRepo.toAbsolutePath().toString(),
                                                               info,
                                                               new String[]{MavenCLIArgs.CLEAN, MavenCLIArgs.COMPILE, MavenCLIArgs.OFFLINE},
                                                               new HashMap<>(),
                                                               Boolean.TRUE);

        CompilationResponse res = compiler.compileSync(req);
        if (res.getMavenOutput().isPresent() && !res.isSuccessful()) {
            TestUtil.writeMavenOutputIntoTargetFolder(res.getMavenOutput().get(),
                                                      "KieDefaultMavenCompilerOnInMemoryFSTest.buildWithPullRebaseUberfireTest");
        }

        assertTrue(res.isSuccessful());

        Path incrementalConfiguration = Paths.get(prjFolder + "/target/incremental/io.takari.maven.plugins_takari-lifecycle-plugin_compile_compile");
        assertTrue(incrementalConfiguration.toFile().exists());

        encoded = Files.readAllBytes(Paths.get(prjFolder + "/pom.xml"));
        pomAsAstring = new String(encoded,
                                  StandardCharsets.UTF_8);
        assertTrue(pomAsAstring.contains("<artifactId>takari-lifecycle-plugin</artifactId>"));

        TestUtil.rm(tmpRootCloned.toFile());
    }

    @Test
    public void buildWithJGitDecoratorTest() throws Exception {
        AFCompiler compiler = MavenCompilerFactory.getCompiler(Decorator.JGIT_BEFORE);

        String MASTER_BRANCH = "master";

        //Setup origin in memory
        final URI originRepo = URI.create("git://repo");
        final JGitFileSystem origin = (JGitFileSystem) ioService.newFileSystem(originRepo,
                                                                               new HashMap<String, Object>() {{
                                                                                   put("init",
                                                                                       Boolean.TRUE);
                                                                                   put("internal",
                                                                                       Boolean.TRUE);
                                                                                   put("listMode",
                                                                                       "ALL");
                                                                               }});
        assertNotNull(origin);

        ioService.startBatch(origin);

        ioService.write(origin.getPath("/dummy/pom.xml"),
                        new String(java.nio.file.Files.readAllBytes(new File("src/test/projects/dummy_multimodule_untouched/pom.xml").toPath())));
        ioService.write(origin.getPath("/dummy/dummyA/src/main/java/dummy/DummyA.java"),
                        new String(java.nio.file.Files.readAllBytes(new File("src/test/projects/dummy_multimodule_untouched/dummyA/src/main/java/dummy/DummyA.java").toPath())));
        ioService.write(origin.getPath("/dummy/dummyB/src/main/java/dummy/DummyB.java"),
                        new String(java.nio.file.Files.readAllBytes(new File("src/test/projects/dummy_multimodule_untouched/dummyB/src/main/java/dummy/DummyB.java").toPath())));
        ioService.write(origin.getPath("/dummy/dummyA/pom.xml"),
                        new String(java.nio.file.Files.readAllBytes(new File("src/test/projects/dummy_multimodule_untouched/dummyA/pom.xml").toPath())));
        ioService.write(origin.getPath("/dummy/dummyB/pom.xml"),
                        new String(java.nio.file.Files.readAllBytes(new File("src/test/projects/dummy_multimodule_untouched/dummyB/pom.xml").toPath())));
        ioService.endBatch();

        RevCommit lastCommit = origin.getGit().resolveRevCommit(origin.getGit().getRef(MASTER_BRANCH).getObjectId());

        assertNotNull(lastCommit);

        //@TODO refactor and use only one between the URI or Git
        //@TODO find a way to resolve the problem of the prjname inside .git folder
        WorkspaceCompilationInfo info = new WorkspaceCompilationInfo(origin.getPath("/dummy/"));
        CompilationRequest req = new DefaultCompilationRequest(mavenRepo.toAbsolutePath().toString(),
                                                               info,
                                                               new String[]{MavenCLIArgs.CLEAN, MavenCLIArgs.COMPILE, MavenCLIArgs.OFFLINE},
                                                               new HashMap<>(),
                                                               Boolean.FALSE);
        CompilationResponse res = compiler.compileSync(req);
        if (res.getMavenOutput().isPresent() && !res.isSuccessful()) {
            TestUtil.writeMavenOutputIntoTargetFolder(res.getMavenOutput().get(),
                                                      "KieDefaultMavenCompilerOnInMemoryFSTest.buildWithJGitDecoratorTest");
        }
        assertTrue(res.isSuccessful());

        lastCommit = origin.getGit().resolveRevCommit(origin.getGit().getRef(MASTER_BRANCH).getObjectId());
        ;
        assertNotNull(lastCommit);

        ioService.write(origin.getPath("/dummy/dummyA/src/main/java/dummy/DummyA.java"),
                        new String(java.nio.file.Files.readAllBytes(new File("src/test/projects/DummyA.java").toPath())));

        RevCommit commitBefore = origin.getGit().resolveRevCommit(origin.getGit().getRef(MASTER_BRANCH).getObjectId());
        assertNotNull(commitBefore);
        assertFalse(lastCommit.getId().toString().equals(commitBefore.getId().toString()));

        //recompile
        res = compiler.compileSync(req);
//        assert commits
        assertTrue(res.isSuccessful());
    }

    @Test
    public void buildWithAllDecoratorsTest() throws Exception {
        AFCompiler compiler = MavenCompilerFactory.getCompiler(Decorator.JGIT_BEFORE_AND_LOG_AFTER);

        String MASTER_BRANCH = "master";

        //Setup origin in memory
        final URI originRepo = URI.create("git://repo");
        final JGitFileSystem origin = (JGitFileSystem) ioService.newFileSystem(originRepo,
                                                                               new HashMap<String, Object>() {{
                                                                                   put("init",
                                                                                       Boolean.TRUE);
                                                                                   put("internal",
                                                                                       Boolean.TRUE);
                                                                                   put("listMode",
                                                                                       "ALL");
                                                                               }});
        assertNotNull(origin);

        ioService.startBatch(origin);

        ioService.write(origin.getPath("/dummy/pom.xml"),
                        new String(java.nio.file.Files.readAllBytes(new File("target/test-classes/kjar-2-single-resources/pom.xml").toPath())));
        ioService.write(origin.getPath("/dummy/src/main/java/org/kie/maven/plugin/test/Person.java"),
                        new String(java.nio.file.Files.readAllBytes(new File("target/test-classes/kjar-2-single-resources/src/main/java/org/kie/maven/plugin/test/Person.java").toPath())));
        ioService.write(origin.getPath("/dummy/src/main/resources/AllResourceTypes/simple-rules.drl"),
                        new String(java.nio.file.Files.readAllBytes(new File("target/test-classes/kjar-2-single-resources/src/main/resources/AllResourceTypes/simple-rules.drl").toPath())));
        ioService.write(origin.getPath("/dummy/src/main/resources/META-INF/kmodule.xml"),
                        new String(java.nio.file.Files.readAllBytes(new File("target/test-classes/kjar-2-single-resources/src/main/resources/META-INF/kmodule.xml").toPath())));
        ioService.endBatch();

        RevCommit lastCommit = origin.getGit().resolveRevCommit(origin.getGit().getRef(MASTER_BRANCH).getObjectId());
        assertNotNull(lastCommit);

        // clone into a regularfs
        Path tmpRootCloned = Files.createTempDirectory("cloned");
        Path tmpCloned = Files.createDirectories(Paths.get(tmpRootCloned.toString(),
                                                           ".clone.git"));

        final Git cloned = Git.cloneRepository().setURI(origin.getGit().getRepository().getDirectory().toURI().toString()).setBare(false).setDirectory(tmpCloned.toFile()).call();

        assertNotNull(cloned);

        WorkspaceCompilationInfo info = new WorkspaceCompilationInfo(Paths.get(tmpCloned + "/dummy"));
        CompilationRequest req = new DefaultCompilationRequest(mavenRepo.toAbsolutePath().toString(),
                                                               info,
                                                               new String[]{MavenCLIArgs.COMPILE, MavenCLIArgs.OFFLINE},
                                                               new HashMap<>(),
                                                               Boolean.TRUE);
        CompilationResponse res = compiler.compileSync(req);
        if (res.getMavenOutput().isPresent() && !res.isSuccessful()) {
            TestUtil.writeMavenOutputIntoTargetFolder(res.getMavenOutput().get(),
                                                      "KieDefaultMavenCompilerOnInMemoryFSTest.buildWithAllDecoratorsTest");
        }
        assertTrue(res.getMavenOutput().isPresent());
        assertTrue(res.isSuccessful());

        lastCommit = origin.getGit().resolveRevCommit(origin.getGit().getRef(MASTER_BRANCH).getObjectId());
        assertNotNull(lastCommit);

        //change one file and commit on the origin repo
        ioService.write(origin.getPath("/dummy/src/main/java/org/kie/maven/plugin/test/Person.java"),
                        new String(java.nio.file.Files.readAllBytes(new File("src/test/projects/Person.java").toPath())));

        RevCommit commitBefore = origin.getGit().resolveRevCommit(origin.getGit().getRef(MASTER_BRANCH).getObjectId());
        assertNotNull(commitBefore);
        assertFalse(lastCommit.getId().toString().equals(commitBefore.getId().toString()));

        //recompile
        res = compiler.compileSync(req);
        if (res.getMavenOutput().isPresent() && !res.isSuccessful()) {
            TestUtil.writeMavenOutputIntoTargetFolder(res.getMavenOutput().get(),
                                                      "KieDefaultMavenCompilerOnInMemoryFSTest.buildWithAllDecoratorsTest");
        }
        assertTrue(res.isSuccessful());
        assertTrue(res.getMavenOutput().isPresent());

        TestUtil.rm(tmpRootCloned.toFile());
    }
}
