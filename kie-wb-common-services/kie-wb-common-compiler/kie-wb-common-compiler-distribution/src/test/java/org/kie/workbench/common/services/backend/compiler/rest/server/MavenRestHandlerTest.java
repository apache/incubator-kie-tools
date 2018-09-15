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
package org.kie.workbench.common.services.backend.compiler.rest.server;

import java.io.File;
import java.net.URI;
import java.nio.file.Path;
import java.util.HashMap;

import org.eclipse.jgit.api.Git;
import org.jboss.resteasy.core.AsynchronousDispatcher;
import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.core.SynchronousDispatcher;
import org.jboss.resteasy.core.SynchronousExecutionContext;
import org.jboss.resteasy.mock.MockDispatcherFactory;
import org.jboss.resteasy.mock.MockHttpRequest;
import org.jboss.resteasy.mock.MockHttpResponse;
import org.jboss.resteasy.plugins.providers.RegisterBuiltin;
import org.jboss.resteasy.plugins.server.resourcefactory.POJOResourceFactory;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kie.workbench.common.services.backend.compiler.HttpCompilationResponse;
import org.kie.workbench.common.services.backend.compiler.TestUtil;
import org.kie.workbench.common.services.backend.compiler.rest.RestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.fs.jgit.JGitFileSystem;
import org.uberfire.mocks.FileSystemTestingUtils;

import static org.assertj.core.api.Assertions.assertThat;

public class MavenRestHandlerTest {

    private static Path tmpRoot;
    private static Path mavenRepoPath;
    private static FileSystemTestingUtils fileSystemTestingUtils = new FileSystemTestingUtils();
    private static IOService ioService;
    /**
     * Maven use as current dir the current module, arquillian w/junit the top level module kie-wb-common
     */
    private static Boolean runIntoMavenCLI = null;
    private Logger logger = LoggerFactory.getLogger(MavenRestHandlerTest.class);

    @BeforeClass
    public static void setup() throws Exception {
        setRunIntoMavenCLI();
        fileSystemTestingUtils.setup();
        ioService = fileSystemTestingUtils.getIoService();
    }

    public static void tearDown() {
        fileSystemTestingUtils.cleanup();
        File sec = new File("src/../.security/");
        if (sec.exists()) {
            TestUtil.rm(sec);
        }
    }

    private static void setRunIntoMavenCLI() {
        if (runIntoMavenCLI == null) {
            File currentDir = new File(".");
            if (currentDir.getAbsolutePath().endsWith("kie-wb-common-compiler-distribution/.")) {
                runIntoMavenCLI = Boolean.TRUE; // Run into MavenCLI
            } else {
                runIntoMavenCLI = Boolean.FALSE; //RUn into IDE
            }
        }
    }

    @Test
    public void get() {

        //Start preparation FS
        try {
            fileSystemTestingUtils.setup();
            ioService = fileSystemTestingUtils.getIoService();

            final String repoName = "myrepo";
            final JGitFileSystem fs = (JGitFileSystem) ioService.newFileSystem(URI.create("git://" + repoName),
                                                                               new HashMap<String, Object>() {{
                                                                                   put("init",
                                                                                       Boolean.TRUE);
                                                                                   put("internal",
                                                                                       Boolean.TRUE);
                                                                               }});

            ioService.startBatch(fs);
            String pom = "target/test-classes/kjar-2-single-resources/pom.xml";
            if (!runIntoMavenCLI) {
                pom = "kie-wb-common-services/kie-wb-common-compiler/kie-wb-common-compiler-distribution/target/test-classes/kjar-2-single-resources/pom.xml";
            }
            ioService.write(fs.getPath("/kjar-2-single-resources/pom.xml"), new String(java.nio.file.Files.readAllBytes(new File(pom).toPath())));

            String personDotJava = "target/test-classes/kjar-2-single-resources/src/main/java/org/kie/maven/plugin/test/Person.java";
            if (!runIntoMavenCLI) {
                personDotJava = "kie-wb-common-services/kie-wb-common-compiler/kie-wb-common-compiler-distribution/target/test-classes/kjar-2-single-resources/src/main/java/org/kie/maven/plugin/test/Person.java";
            }
            ioService.write(fs.getPath("/kjar-2-single-resources/src/main/java/org/kie/maven/plugin/test/Person.java"), new String(java.nio.file.Files.readAllBytes(new File(personDotJava).toPath())));

            String simpleRulesDotDRL = "target/test-classes/kjar-2-single-resources/src/main/resources/AllResourcesTypes/simple-rules.drl";
            if (!runIntoMavenCLI) {
                simpleRulesDotDRL = "kie-wb-common-services/kie-wb-common-compiler/kie-wb-common-compiler-distribution/target/test-classes/kjar-2-single-resources/src/main/resources/AllResourceTypes/simple-rules.drl";
            }
            ioService.write(fs.getPath("/kjar-2-single-resources/src/main/resources/AllResourcesTypes/simple-rules.drl"), new String(java.nio.file.Files.readAllBytes(new File(simpleRulesDotDRL).toPath())));

            String kmodule = "target/test-classes/kjar-2-single-resources/src/main/resources/META-INF/kmodule.xml";
            if (!runIntoMavenCLI) {
                kmodule = "kie-wb-common-services/kie-wb-common-compiler/kie-wb-common-compiler-distribution/target/test-classes/kjar-2-single-resources/src/main/resources/META-INF/kmodule.xml";
            }
            ioService.write(fs.getPath("/kjar-2-single-resources/src/main/resources/META-INF/kmodule.xml"), new String(java.nio.file.Files.readAllBytes(new File(kmodule).toPath())));

            ioService.endBatch();

            java.nio.file.Path tmpRootCloned = java.nio.file.Files.createTempDirectory("cloned");
            java.nio.file.Path tmpCloned = java.nio.file.Files.createDirectories(java.nio.file.Paths.get(tmpRootCloned.toString(), "dummy"));

            final File gitClonedFolder = new File(tmpCloned.toFile(), ".clone.git");

            final Git cloned = Git.cloneRepository().setURI(fs.getGit().getRepository().getDirectory().toURI().toString()).setBare(false).setDirectory(gitClonedFolder).call();

            assertThat(cloned).isNotNull();
            mavenRepoPath = java.nio.file.Paths.get(System.getProperty("user.home"), ".m2", "repository");
            tmpRoot = java.nio.file.Paths.get(gitClonedFolder + "/dummy/");

            //END preparation FS

            //Test
            Dispatcher dispatcher = MockDispatcherFactory.createDispatcher();
            POJOResourceFactory noDefaults = new POJOResourceFactory(MavenRestHandler.class);
            dispatcher.getRegistry().addResourceFactory(noDefaults);
            MockHttpRequest request = MockHttpRequest.get("build/maven/");
            MockHttpResponse response = new MockHttpResponse();
            dispatcher.invoke(request, response);
            assertThat(response.getStatus()).isEqualTo(200);
            assertThat(response.getContentAsString()).isEqualTo("Apache Maven");
            //end test

            tearDown();
        } catch (Exception e) {
            logger.error(e.getMessage());
        } finally {
            fileSystemTestingUtils.cleanup();
        }
    }

    @Test
    public void post() {
        //Start preparation FS
        try {
            fileSystemTestingUtils.setup();
            ioService = fileSystemTestingUtils.getIoService();

            final String repoName = "myrepo";
            final JGitFileSystem fs = (JGitFileSystem) ioService.newFileSystem(URI.create("git://" + repoName),
                                                                               new HashMap<String, Object>() {{
                                                                                   put("init",
                                                                                       Boolean.TRUE);
                                                                                   put("internal",
                                                                                       Boolean.TRUE);
                                                                               }});

            ioService.startBatch(fs);
            String pom = "target/test-classes/kjar-2-single-resources/pom.xml";
            if (!runIntoMavenCLI) {
                pom = "kie-wb-common-services/kie-wb-common-compiler/kie-wb-common-compiler-distribution/target/test-classes/kjar-2-single-resources/pom.xml";
            }
            ioService.write(fs.getPath("/kjar-2-single-resources/pom.xml"), new String(java.nio.file.Files.readAllBytes(new File(pom).toPath())));

            String personDotJava = "target/test-classes/kjar-2-single-resources/src/main/java/org/kie/maven/plugin/test/Person.java";
            if (!runIntoMavenCLI) {
                personDotJava = "kie-wb-common-services/kie-wb-common-compiler/kie-wb-common-compiler-distribution/target/test-classes/kjar-2-single-resources/src/main/java/org/kie/maven/plugin/test/Person.java";
            }
            ioService.write(fs.getPath("/kjar-2-single-resources/src/main/java/org/kie/maven/plugin/test/Person.java"), new String(java.nio.file.Files.readAllBytes(new File(personDotJava).toPath())));

            String simpleRulesDotDRL = "target/test-classes/kjar-2-single-resources/src/main/resources/AllResourcesTypes/simple-rules.drl";
            if (!runIntoMavenCLI) {
                simpleRulesDotDRL = "kie-wb-common-services/kie-wb-common-compiler/kie-wb-common-compiler-distribution/target/test-classes/kjar-2-single-resources/src/main/resources/AllResourceTypes/simple-rules.drl";
            }
            ioService.write(fs.getPath("/kjar-2-single-resources/src/main/resources/AllResourcesTypes/simple-rules.drl"), new String(java.nio.file.Files.readAllBytes(new File(simpleRulesDotDRL).toPath())));

            String kmodule = "target/test-classes/kjar-2-single-resources/src/main/resources/META-INF/kmodule.xml";
            if (!runIntoMavenCLI) {
                kmodule = "kie-wb-common-services/kie-wb-common-compiler/kie-wb-common-compiler-distribution/target/test-classes/kjar-2-single-resources/src/main/resources/META-INF/kmodule.xml";
            }
            ioService.write(fs.getPath("/kjar-2-single-resources/src/main/resources/META-INF/kmodule.xml"), new String(java.nio.file.Files.readAllBytes(new File(kmodule).toPath())));

            ioService.endBatch();

            java.nio.file.Path tmpRootCloned = java.nio.file.Files.createTempDirectory("cloned");
            java.nio.file.Path tmpCloned = java.nio.file.Files.createDirectories(java.nio.file.Paths.get(tmpRootCloned.toString(), "dummy"));

            final File gitClonedFolder = new File(tmpCloned.toFile(), ".clone.git");

            final Git cloned = Git.cloneRepository().setURI(fs.getGit().getRepository().getDirectory().toURI().toString()).setBare(false).setDirectory(gitClonedFolder).call();

            assertThat(cloned).isNotNull();
            mavenRepoPath = java.nio.file.Paths.get(System.getProperty("user.home"), ".m2", "repository");
            tmpRoot = java.nio.file.Paths.get(gitClonedFolder + "/dummy/");

            //END preparation FS

            Dispatcher dispatcher = new AsynchronousDispatcher(new ResteasyProviderFactory());
            ResteasyProviderFactory.setInstance(dispatcher.getProviderFactory());
            RegisterBuiltin.register(dispatcher.getProviderFactory());

            POJOResourceFactory noDefaults = new POJOResourceFactory(MavenRestHandler.class);
            dispatcher.getRegistry().addResourceFactory(noDefaults);

            MockHttpRequest request = MockHttpRequest.create("POST", "build/maven/");
            request.header("project", tmpRoot.toAbsolutePath().toString() + "/dummy").header("mavenrepo", mavenRepoPath.toAbsolutePath().toString());
            MockHttpResponse response = new MockHttpResponse();

            SynchronousExecutionContext synchronousExecutionContext = new SynchronousExecutionContext((SynchronousDispatcher) dispatcher, request, response);
            request.setAsynchronousContext(synchronousExecutionContext);

            dispatcher.invoke(request, response);
            assertThat(response.getStatus()).isEqualTo(200);
            byte[] serializedCompilationResponse = response.getOutput();
            HttpCompilationResponse res = RestUtils.readDefaultCompilationResponseFromBytes(serializedCompilationResponse);
            assertThat(res).isNotNull();
            assertThat(res.getDependencies()).hasSize(4);
            assertThat(res.getTargetContent()).hasSize(3);

            tearDown();
        } catch (Exception e) {
            logger.error(e.getMessage());
        } finally {
            fileSystemTestingUtils.cleanup();
        }
    }
}