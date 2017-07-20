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

package org.kie.workbench.common.services.backend.compiler.internalNIO.kie;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PullCommand;
import org.eclipse.jgit.api.PullResult;
import org.eclipse.jgit.api.RebaseCommand;
import org.eclipse.jgit.api.RebaseResult;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.eclipse.jgit.util.FileUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.services.backend.compiler.CompilationResponse;
import org.kie.workbench.common.services.backend.compiler.TestUtil;
import org.kie.workbench.common.services.backend.compiler.configuration.KieDecorator;
import org.kie.workbench.common.services.backend.compiler.configuration.MavenCLIArgs;
import org.kie.workbench.common.services.backend.compiler.internalNIO.InternalNIOCompilationRequest;
import org.kie.workbench.common.services.backend.compiler.internalNIO.InternalNIOKieMavenCompiler;
import org.kie.workbench.common.services.backend.compiler.internalNIO.InternalNIOTestUtil;
import org.kie.workbench.common.services.backend.compiler.internalNIO.InternalNIOWorkspaceCompilationInfo;
import org.kie.workbench.common.services.backend.compiler.internalNIO.impl.InternalNIODefaultCompilationRequest;
import org.kie.workbench.common.services.backend.compiler.internalNIO.impl.kie.InternalNIOKieMavenCompilerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.java.nio.file.Files;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.Paths;
import org.uberfire.java.nio.fs.jgit.JGitFileSystem;
import org.uberfire.java.nio.fs.jgit.JGitFileSystemProvider;
import org.uberfire.java.nio.fs.jgit.util.JGitUtil;
import org.uberfire.java.nio.security.FileSystemAuthenticator;
import org.uberfire.java.nio.security.FileSystemUser;

import static org.junit.Assert.*;

public class InternalNIOKieDefaultMavenCompilerOnInMemoryFSTest {

    private static final Logger logger = LoggerFactory.getLogger(InternalNIOKieDefaultMavenCompilerOnInMemoryFSTest.class);

    private Path mavenRepo;
    private int gitSSHPort;
    private JGitFileSystemProvider provider;

    public static int findFreePort() {
        int port = 0;
        try {
            ServerSocket server =
                    new ServerSocket(0);
            port = server.getLocalPort();
            server.close();
        } catch (IOException e) {
            Assert.fail("Can't find free port!");
        }
        logger.debug("Found free port " + port);
        return port;
    }

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

    @After
    public void tearDown() throws IOException {
        if (provider == null) {
            // this would mean that setup failed. no need to clean up.
            return;
        }

        provider.shutdown();

        if (provider.getGitRepoContainerDir() != null && provider.getGitRepoContainerDir().exists()) {
            FileUtils.delete(provider.getGitRepoContainerDir(),
                             FileUtils.RECURSIVE);
        }
        TestUtil.rm(new File("src/../.security/"));
    }

    @Test
    public void buildWithCloneTest() throws Exception {

        Path tmpRoot = Files.createTempDirectory("repo");

        //NIO creation and copy content
        java.nio.file.Path temp = java.nio.file.Files.createDirectories(java.nio.file.Paths.get(tmpRoot.toString(),
                                                                                                "dummy"));
        TestUtil.copyTree(java.nio.file.Paths.get("src/test/projects/dummy_multimodule_untouched"),
                          temp);
        //end NIO

        File gitFolder = new File(temp.toFile(),
                                  ".repo.git");//@TODO why is mandatory use a .git folder name ?

        Git origin = JGitUtil.newRepository(gitFolder,
                                            false);
        assertNotNull(origin);

        JGitUtil.commit(origin,
                        "master",
                        "name",
                        "name@example.com",
                        "master-1",
                        null,
                        null,
                        false,
                        getFilesToCommit(temp.toFile())
        );

        assertEquals(JGitUtil.branchList(origin).size(),
                     1);

        Path tmpRootCloned = Files.createTempDirectory("cloned");
        Path tmpCloned = Files.createDirectories(Paths.get(tmpRootCloned.toString(),
                                                           "dummy"));

        final File gitClonedFolder = new File(tmpCloned.toFile(),
                                              ".clone.git");
        //clone the repo
        Git cloned = JGitUtil.cloneRepository(gitClonedFolder,
                                              origin.getRepository().getDirectory().toString(),
                                              false,
                                              CredentialsProvider.getDefault());
        assertNotNull(cloned);

        //Compile the repo
        InternalNIOKieMavenCompiler compiler = InternalNIOKieMavenCompilerFactory.getCompiler(
                KieDecorator.LOG_OUTPUT_AFTER);
        Path prjFolder = Paths.get(gitClonedFolder + "/dummy/");
        byte[] encoded = Files.readAllBytes(Paths.get(prjFolder + "/pom.xml"));
        String pomAsAstring = new String(encoded,
                                         StandardCharsets.UTF_8);
        Assert.assertFalse(pomAsAstring.contains("<artifactId>takari-lifecycle-plugin</artifactId>"));

        InternalNIOWorkspaceCompilationInfo info = new InternalNIOWorkspaceCompilationInfo(prjFolder);
        InternalNIOCompilationRequest req = new InternalNIODefaultCompilationRequest(mavenRepo.toAbsolutePath().toString(),
                                                                                     info,
                                                                                     new String[]{MavenCLIArgs.COMPILE},
                                                                                     new HashMap<>(),
                                                                                     Boolean.TRUE);

        CompilationResponse res = compiler.compileSync(req);
        if (res.getMavenOutput().isPresent() && !res.isSuccessful()) {
            TestUtil.writeMavenOutputIntoTargetFolder(res.getMavenOutput().get(),
                                                      "InternalNIOKieDefaultMavenCompilerOnInMemoryFSTest.buildWithCloneTest");
        }
        assertTrue(res.getMavenOutput().isPresent());
        assertTrue(res.isSuccessful());

        Path incrementalConfiguration = Paths.get(prjFolder + "/target/incremental/io.takari.maven.plugins_takari-lifecycle-plugin_compile_compile");
        assertTrue(incrementalConfiguration.toFile().exists());

        encoded = Files.readAllBytes(Paths.get(prjFolder + "/pom.xml"));
        pomAsAstring = new String(encoded,
                                  StandardCharsets.UTF_8);
        assertTrue(pomAsAstring.contains("<artifactId>takari-lifecycle-plugin</artifactId>"));

        cloned.close();
        origin.close();

        InternalNIOTestUtil.rm(tmpRootCloned.toFile());
        InternalNIOTestUtil.rm(tmpRoot.toFile());
    }

    private Map<String, File> getKieFilesToCommit(File temp) {
        Map<String, File> map = new HashMap<>();
        map.put("/dummy/pom.xml",
                new File(temp.toString() + "/pom.xml"));
        map.put("/dummy/src/main/java/org/kie/maven/plugin/test/Person.java",
                new File(temp.toString() + "/src/main/java/org/kie/maven/plugin/test/Person.java"));
        map.put("/dummy/src/main/resources/AllResourceTypes/simple-rules.drl",
                new File(temp.toString() + "/src/main/resources/AllResourceTypes/simple-rules.drl"));
        map.put("/dummy/src/main/resources/META-INF/kmodule.xml",
                new File(temp.toString() + "/src/main/resources/META-INF/kmodule.xml"));
        return map;
    }

    private Map<String, File> getFilesToCommit(File temp) {
        Map<String, File> map = new HashMap<>();
        map.put("/dummy/pom.xml",
                new File(temp.toString() + "/pom.xml"));
        map.put("/dummy/dummyA/src/main/java/dummy/DummyA.java",
                new File(temp.toString() + "/dummyA/src/main/java/dummy/DummyA.java"));
        map.put("/dummy/dummyB/src/main/java/dummy/DummyB.java",
                new File(temp.toString() + "/dummyB/src/main/java/dummy/DummyB.java"));
        map.put("/dummy/dummyA/pom.xml",
                new File(temp.toString() + "/dummyA/pom.xml"));
        map.put("/dummy/dummyB/pom.xml",
                new File(temp.toString() + "/dummyB/pom.xml"));
        return map;
    }

    @Test
    public void buildWithPullRebaseUberfireTest() throws Exception {

        provider = new JGitFileSystemProvider(getGitPreferences());
        provider.setAuthenticator(getAuthenticator());
        provider.setAuthorizer((fs, fileSystemUser) -> true);

        CredentialsProvider.setDefault(new UsernamePasswordCredentialsProvider("admin",
                                                                               ""));

        //Setup origin in memory
        final URI originRepo = URI.create("git://repo");
        final JGitFileSystem origin = (JGitFileSystem) provider.newFileSystem(originRepo,
                                                                              new HashMap<String, Object>() {{
                                                                                  put("listMode",
                                                                                      "ALL");
                                                                              }});
        assertNotNull(origin);

        Path tmpRoot = Files.createTempDirectory("repo");
        Path tmp = Files.createDirectories(Paths.get(tmpRoot.toString(),
                                                     "dummy"));

        //NIO creation and copy content
        java.nio.file.Path temp = java.nio.file.Files.createDirectories(java.nio.file.Paths.get(tmpRoot.toString(),
                                                                                                "dummy"));
        TestUtil.copyTree(java.nio.file.Paths.get("src/test/projects/dummy_multimodule_untouched"),
                          temp);
        //end NIO

        JGitUtil.commit(origin.gitRepo(),
                        "master",
                        "name",
                        "name@example.com",
                        "master",
                        null,
                        null,
                        false,
                        getFilesToCommit(temp.toFile())
        );

        // clone into a regularfs
        Path tmpRootCloned = Files.createTempDirectory("cloned");
        Path tmpCloned = Files.createDirectories(Paths.get(tmpRootCloned.toString(),
                                                           ".clone.git"));
        Git cloned = JGitUtil.cloneRepository(tmpCloned.toFile(),
                                              "git://localhost:9418/repo"/*origin.gitRepo().toString()*/,
                                              false,
                                              CredentialsProvider.getDefault());
        assertNotNull(cloned);

        PullCommand pc = cloned.pull().setRemote("origin").setRebase(Boolean.TRUE);
        PullResult pullRes = pc.call();
        assertTrue(pullRes.getRebaseResult().getStatus().equals(RebaseResult.Status.UP_TO_DATE));// nothing changed yet

        RebaseCommand rb = cloned.rebase().setUpstream("origin/master");
        RebaseResult rbResult = rb.setPreserveMerges(true).call();
        assertTrue(rbResult.getStatus().isSuccessful());

        //Compile the repo
        InternalNIOKieMavenCompiler compiler = InternalNIOKieMavenCompilerFactory.getCompiler(
                KieDecorator.LOG_OUTPUT_AFTER);

        byte[] encoded = Files.readAllBytes(Paths.get(tmpCloned + "/dummy/pom.xml"));
        String pomAsAstring = new String(encoded,
                                         StandardCharsets.UTF_8);
        Assert.assertFalse(pomAsAstring.contains("<artifactId>takari-lifecycle-plugin</artifactId>"));

        Path prjFolder = Paths.get(tmpCloned + "/dummy/");

        InternalNIOWorkspaceCompilationInfo info = new InternalNIOWorkspaceCompilationInfo(prjFolder);
        InternalNIOCompilationRequest req = new InternalNIODefaultCompilationRequest(mavenRepo.toAbsolutePath().toString(),
                                                                                     info,
                                                                                     new String[]{MavenCLIArgs.CLEAN, MavenCLIArgs.COMPILE},
                                                                                     new HashMap<>(),
                                                                                     Boolean.TRUE);

        CompilationResponse res = compiler.compileSync(req);
        if (res.getMavenOutput().isPresent() && !res.isSuccessful()) {
            TestUtil.writeMavenOutputIntoTargetFolder(res.getMavenOutput().get(),
                                                      "InternalNIOKieDefaultMavenCompilerOnInMemoryFSTest.buildWithPullRebaseUberfireTest");
        }

        assertTrue(res.isSuccessful());

        Path incrementalConfiguration = Paths.get(prjFolder + "/target/incremental/io.takari.maven.plugins_takari-lifecycle-plugin_compile_compile");
        assertTrue(incrementalConfiguration.toFile().exists());

        encoded = Files.readAllBytes(Paths.get(prjFolder + "/pom.xml"));
        pomAsAstring = new String(encoded,
                                  StandardCharsets.UTF_8);
        assertTrue(pomAsAstring.contains("<artifactId>takari-lifecycle-plugin</artifactId>"));

        InternalNIOTestUtil.rm(tmpRoot.toFile());
        InternalNIOTestUtil.rm(tmpRootCloned.toFile());
    }

    @Test
    public void buildWithJGitDecoratorTest() throws Exception {
        InternalNIOKieMavenCompiler compiler = InternalNIOKieMavenCompilerFactory.getCompiler(
                KieDecorator.JGIT_BEFORE);

        String MASTER_BRANCH = "master";

        provider = new JGitFileSystemProvider(getGitPreferences());
        provider.setAuthenticator(getAuthenticator());
        provider.setAuthorizer((fs, fileSystemUser) -> true);

        CredentialsProvider.setDefault(new UsernamePasswordCredentialsProvider("admin",
                                                                               ""));

        //Setup origin in memory
        final URI originRepo = URI.create("git://repo");
        final JGitFileSystem origin = (JGitFileSystem) provider.newFileSystem(originRepo,
                                                                              new HashMap<String, Object>() {{
                                                                                  put("listMode",
                                                                                      "ALL");
                                                                              }}
        );
        assertNotNull(origin);

        Path tmpRoot = Files.createTempDirectory("repo");
        Path tmp = Files.createDirectories(Paths.get(tmpRoot.toString(),
                                                     "dummy"));

        //NIO creation and copy content
        java.nio.file.Path temp = java.nio.file.Files.createDirectories(java.nio.file.Paths.get(tmpRoot.toString(),
                                                                                                "dummy"));
        TestUtil.copyTree(java.nio.file.Paths.get("src/test/projects/dummy_multimodule_untouched"),
                          temp);
        //end NIO

        JGitUtil.commit(origin.gitRepo(),
                        MASTER_BRANCH,
                        "name",
                        "name@example.com",
                        "master",
                        null,
                        null,
                        false,
                        getFilesToCommit(temp.toFile())
        );

        RevCommit lastCommit = JGitUtil.getLastCommit(origin.gitRepo(),
                                                      MASTER_BRANCH);
        assertNotNull(lastCommit);

        // clone into a regularfs
        Path tmpRootCloned = Files.createTempDirectory("cloned");
        Path tmpCloned = Files.createDirectories(Paths.get(tmpRootCloned.toString(),
                                                           ".clone.git"));
        //@TODO find a way to retrieve the address git://... of the repo
        Git cloned = JGitUtil.cloneRepository(tmpCloned.toFile(),
                                              "git://localhost:9418/repo",
                                              false,
                                              CredentialsProvider.getDefault());
        assertNotNull(cloned);

        //@TODO refactor and use only one between the URI or Git
        //@TODO find a way to resolve the problem of the prjname inside .git folder
        InternalNIOWorkspaceCompilationInfo info = new InternalNIOWorkspaceCompilationInfo(Paths.get(tmpCloned + "/dummy"),
                                                                                           URI.create("git://localhost:9418/repo"),
                                                                                           cloned);
        InternalNIOCompilationRequest req = new InternalNIODefaultCompilationRequest(mavenRepo.toAbsolutePath().toString(),
                                                                                     info,
                                                                                     new String[]{MavenCLIArgs.CLEAN, MavenCLIArgs.COMPILE},
                                                                                     new HashMap<>(),
                                                                                     Boolean.FALSE);
        CompilationResponse res = compiler.compileSync(req);
        if (res.getMavenOutput().isPresent() && !res.isSuccessful()) {
            TestUtil.writeMavenOutputIntoTargetFolder(res.getMavenOutput().get(),
                                                      "InternalNIOKieDefaultMavenCompilerOnInMemoryFSTest.buildWithJGitDecoratorTest");
        }
        assertTrue(res.isSuccessful());

        lastCommit = JGitUtil.getLastCommit(origin.gitRepo(),
                                            MASTER_BRANCH);
        assertNotNull(lastCommit);

        //change one file and commit on the origin repo
        Map<String, File> map = new HashMap<>();
        map.put("/dummy/dummyA/src/main/java/dummy/DummyA.java",
                new File("src/test/projects/DummyA.java"));

        JGitUtil.commit(origin.gitRepo(),
                        MASTER_BRANCH,
                        "name",
                        "name@example.com",
                        "master",
                        null,
                        null,
                        false,
                        map
        );

        RevCommit commitBefore = JGitUtil.getLastCommit(origin.gitRepo(),
                                                        MASTER_BRANCH);
        assertNotNull(commitBefore);
        assertFalse(lastCommit.getId().toString().equals(commitBefore.getId().toString()));

        //recompile
        res = compiler.compileSync(req);
        assertTrue(res.isSuccessful());

        InternalNIOTestUtil.rm(tmpRoot.toFile());
        InternalNIOTestUtil.rm(tmpRootCloned.toFile());
    }

    @Test
    public void buildWithAllDecoratorsTest() throws Exception {
        InternalNIOKieMavenCompiler compiler = InternalNIOKieMavenCompilerFactory.getCompiler(
                KieDecorator.JGIT_BEFORE_AND_LOG_AFTER);

        String MASTER_BRANCH = "master";

        provider = new JGitFileSystemProvider(getGitPreferences());
        provider.setAuthenticator(getAuthenticator());
        provider.setAuthorizer((fs, fileSystemUser) -> true);

        CredentialsProvider.setDefault(new UsernamePasswordCredentialsProvider("admin",
                                                                               ""));

        //Setup origin in memory
        final URI originRepo = URI.create("git://repo");
        final JGitFileSystem origin = (JGitFileSystem) provider.newFileSystem(originRepo,
                                                                              new HashMap<String, Object>() {{
                                                                                  put("listMode",
                                                                                      "ALL");
                                                                              }}
        );
        assertNotNull(origin);

        Path tmpRoot = Files.createTempDirectory("repo");
        Path tmp = Files.createDirectories(Paths.get(tmpRoot.toString(),
                                                     "dummy"));

        //NIO creation and copy content
        java.nio.file.Path temp = java.nio.file.Files.createDirectories(java.nio.file.Paths.get(tmpRoot.toString(),
                                                                                                "dummy"));
        TestUtil.copyTree(java.nio.file.Paths.get("src/test/projects/kjar-2-single-resources"),
                          temp);
        //end NIO

        JGitUtil.commit(origin.gitRepo(),
                        MASTER_BRANCH,
                        "name",
                        "name@example.com",
                        "master",
                        null,
                        null,
                        false,
                        getKieFilesToCommit(temp.toFile())
        );

        RevCommit lastCommit = JGitUtil.getLastCommit(origin.gitRepo(),
                                                      MASTER_BRANCH);
        assertNotNull(lastCommit);

        // clone into a regularfs
        Path tmpRootCloned = Files.createTempDirectory("cloned");
        Path tmpCloned = Files.createDirectories(Paths.get(tmpRootCloned.toString(),
                                                           ".clone.git"));
        //@TODO find a way to retrieve the address git://... of the repo
        Git cloned = JGitUtil.cloneRepository(tmpCloned.toFile(),
                                              "git://localhost:9418/repo",
                                              false,
                                              CredentialsProvider.getDefault());

        assertNotNull(cloned);

        //@TODO refactor and use only one between the URI or Git
        //@TODO find a way to resolve the problem of the prjname inside .git folder
        InternalNIOWorkspaceCompilationInfo info = new InternalNIOWorkspaceCompilationInfo(Paths.get(tmpCloned + "/dummy"),
                                                                                           URI.create("git://localhost:9418/repo"),
                                                                                           cloned);
        InternalNIOCompilationRequest req = new InternalNIODefaultCompilationRequest(mavenRepo.toAbsolutePath().toString(),
                                                                                     info,
                                                                                     new String[]{MavenCLIArgs.COMPILE},
                                                                                     new HashMap<>(),
                                                                                     Boolean.TRUE);
        CompilationResponse res = compiler.compileSync(req);
        if (res.getMavenOutput().isPresent() && !res.isSuccessful()) {
            TestUtil.writeMavenOutputIntoTargetFolder(res.getMavenOutput().get(),
                                                      "InternalNIOKieDefaultMavenCompilerOnInMemoryFSTest.buildWithAllDecoratorsTest");
        }
        assertTrue(res.getMavenOutput().isPresent());
        assertTrue(res.isSuccessful());

        lastCommit = JGitUtil.getLastCommit(origin.gitRepo(),
                                            MASTER_BRANCH);
        assertNotNull(lastCommit);

        //change one file and commit on the origin repo
        Map<String, File> map = new HashMap<>();
        map.put("/dummy/src/main/java/org/kie/maven/plugin/test/Person.java",
                new File("src/test/projects/Person.java"));

        JGitUtil.commit(origin.gitRepo(),
                        MASTER_BRANCH,
                        "name",
                        "name@example.com",
                        "master",
                        null,
                        null,
                        false,
                        map
        );

        RevCommit commitBefore = JGitUtil.getLastCommit(origin.gitRepo(),
                                                        MASTER_BRANCH);
        assertNotNull(commitBefore);
        assertFalse(lastCommit.getId().toString().equals(commitBefore.getId().toString()));

        //recompile
        res = compiler.compileSync(req);
        if (res.getMavenOutput().isPresent() && !res.isSuccessful()) {
            TestUtil.writeMavenOutputIntoTargetFolder(res.getMavenOutput().get(),
                                                      "InternalNIOKieDefaultMavenCompilerOnInMemoryFSTest.buildWithAllDecoratorsTest");
        }
        assertTrue(res.isSuccessful());
        assertTrue(res.getMavenOutput().isPresent());

        InternalNIOTestUtil.rm(tmpRoot.toFile());
        InternalNIOTestUtil.rm(tmpRootCloned.toFile());
    }

    private FileSystemAuthenticator getAuthenticator() {
        return new FileSystemAuthenticator() {
            @Override
            public FileSystemUser authenticate(final String username,
                                               final String password) {
                return new FileSystemUser() {
                    @Override
                    public String getName() {
                        return "admin";
                    }
                };
            }
        };
    }

    public Map<String, String> getGitPreferences() {
        Map<String, String> gitPrefs = new HashMap<>();

        gitPrefs.put("org.uberfire.nio.git.ssh.enabled",
                     "true");
        gitSSHPort = findFreePort();
        gitPrefs.put("org.uberfire.nio.git.ssh.port",
                     String.valueOf(gitSSHPort));
        gitPrefs.put("org.uberfire.nio.git.ssh.idle.timeout",
                     "10001");

        return gitPrefs;
    }
}
