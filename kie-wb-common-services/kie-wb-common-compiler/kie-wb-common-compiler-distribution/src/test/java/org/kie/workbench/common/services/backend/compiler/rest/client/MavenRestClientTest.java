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
package org.kie.workbench.common.services.backend.compiler.rest.client;

import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.concurrent.Future;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.apache.commons.io.IOUtils;
import org.eclipse.jgit.api.Git;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.services.backend.compiler.HttpCompilationResponse;
import org.kie.workbench.common.services.backend.compiler.TestUtil;
import org.kie.workbench.common.services.backend.compiler.TestUtilMaven;
import org.kie.workbench.common.services.backend.compiler.rest.MVELEvaluatorProducer;
import org.kie.workbench.common.services.backend.compiler.rest.RestUtils;
import org.kie.workbench.common.services.backend.compiler.rest.server.MavenRestHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.fs.jgit.JGitFileSystem;
import org.uberfire.mocks.FileSystemTestingUtils;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Arquillian.class)
public class MavenRestClientTest {

    private static Path tmpRoot;
    private static Path mavenRepoPath;
    private static FileSystemTestingUtils fileSystemTestingUtils = new FileSystemTestingUtils();
    private static IOService ioService;
    private static String maven = "Apache Maven";
    private static String mavenSettingsPath;
    /**
     * Maven use as current dir the current module, arquillian w/junit the top level module kie-wb-common
     */
    private static Boolean runIntoMavenCLI = null;
    private static Logger logger = LoggerFactory.getLogger(MavenRestClientTest.class);

    @ArquillianResource
    private URL deploymentUrl;

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

    public static void setup() throws Exception {
        setRunIntoMavenCLI();
        fileSystemTestingUtils.setup();
        ioService = fileSystemTestingUtils.getIoService();
        mavenSettingsPath = TestUtilMaven.getSettingsFile();
    }

    public static void tearDown() {
        fileSystemTestingUtils.cleanup();
        File sec = new File("src/../.security/");
        if (sec.exists()) {
            TestUtil.rm(sec);
        }
    }

    @Deployment
    public static Archive getDeployment() throws Exception {
        setup();
        setRunIntoMavenCLI();
        final WebArchive war = ShrinkWrap.create(WebArchive.class, "compiler.war");
        final File[] metaInfFilesFiles;
        File pom;
        if (runIntoMavenCLI) {
            war.setWebXML(new File("target/test-classes/web.xml"));
            metaInfFilesFiles = new File("target/test-classes/META-INF").listFiles();
            pom = new File("pom.xml");
        } else {
            war.addAsResource(new File("kie-wb-common-services/kie-wb-common-compiler/kie-wb-common-compiler-distribution/target/test-classes/IncrementalCompiler.properties"));
            war.setWebXML(new File("kie-wb-common-services/kie-wb-common-compiler/kie-wb-common-compiler-distribution/target/test-classes/web.xml"));
            metaInfFilesFiles = new File("kie-wb-common-services/kie-wb-common-compiler/kie-wb-common-compiler-distribution/target/test-classes/META-INF").listFiles();
            pom = new File("kie-wb-common-services/kie-wb-common-compiler/kie-wb-common-compiler-distribution/pom.xml");
        }

        war.addClasses(MavenRestHandler.class);
        war.addClass(MVELEvaluatorProducer.class);
        war.addClass(TestUtil.class);
        war.addPackages(true, "org.kie.workbench.common.services.backend.compiler.rest");
        for (final File file : metaInfFilesFiles) {
            war.addAsManifestResource(file);
        }
        
        final File[] files = Maven.configureResolver().
                fromFile(mavenSettingsPath).
                loadPomFromFile(pom)
                .resolve(
                        "org.assertj:assertj-core:?",
                        "org.kie.workbench.services:kie-wb-common-compiler-core:?",
                        "org.kie.workbench.services:kie-wb-common-compiler-service:?",
                        "org.kie.workbench.services:kie-wb-common-services-backend:?",
                        "org.kie.soup:kie-soup-project-datamodel-commons:?",

                        "org.jboss.errai:errai-bus:?",
                        "org.jboss.errai:errai-jboss-as-support:?",
                        "org.jboss.errai:errai-marshalling:?",

                        "org.uberfire:uberfire-nio2-api:?",
                        "org.uberfire:uberfire-nio2-model:?",
                        "org.uberfire:uberfire-nio2-jgit:?",
                        "org.uberfire:uberfire-nio2-fs:?",
                        "org.uberfire:uberfire-servlet-security:?",
                        "org.uberfire:uberfire-testing-utils:?",
                        "org.uberfire:uberfire-metadata-backend-elasticsearch:?",
                        "org.uberfire:uberfire-metadata-backend-lucene:?",
                        "org.uberfire:uberfire-io:?",
                        "org.uberfire:uberfire-ssh-api:?",
                        "org.uberfire:uberfire-ssh-backend:?",

                        "org.eclipse.jgit:org.eclipse.jgit:?",

                        "org.jboss.resteasy:resteasy-jaxrs:?",
                        "org.jboss.resteasy:resteasy-multipart-provider:?").withTransitivity()
                .asFile();

        for (final File file : files) {
            war.addAsLibrary(file);
        }
        System.out.println(war.toString(true));
        return war;
    }

    @Test
    public void getTest() {
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(deploymentUrl.toString() + "rest/build/maven/");
        Invocation invocation = target.request().buildGet();
        Response response = invocation.invoke();
        assertThat(response.getStatusInfo().getStatusCode()).isEqualTo(200);
        assertThat(response.readEntity(String.class)).isEqualTo(maven);
        tearDown();
    }

    @Test
    public void postTest() {
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

            Path tmpRootCloned = Files.createTempDirectory("cloned");
            Path tmpCloned = Files.createDirectories(Paths.get(tmpRootCloned.toString(), "dummy"));

            final File gitClonedFolder = new File(tmpCloned.toFile(), ".clone.git");

            final Git cloned = Git.cloneRepository().setURI(fs.getGit().getRepository().getDirectory().toURI().toString()).setBare(false).setDirectory(gitClonedFolder).call();

            assertThat(cloned).isNotNull();
            mavenRepoPath = Paths.get(System.getProperty("user.home"), ".m2", "repository");
            tmpRoot = Paths.get(gitClonedFolder + "/dummy/");

            Client client = ClientBuilder.newClient();
            WebTarget target = client.target(deploymentUrl.toString() + "rest/maven/");
            MultivaluedMap headersMap = new MultivaluedHashMap();
            headersMap.add("project", tmpRoot.toAbsolutePath().toString() + "/dummy");
            headersMap.add("mavenrepo", mavenRepoPath.toAbsolutePath().toString());
            headersMap.add("settings_xml",
                           mavenSettingsPath);
            Future<Response> responseFuture = target.request().headers(headersMap).async().post(Entity.entity(String.class, MediaType.TEXT_PLAIN));
            Response response = responseFuture.get();
            assertThat(response.getStatusInfo().getStatusCode()).isEqualTo(200);
            InputStream is = response.readEntity(InputStream.class);
            byte[] serializedCompilationResponse = IOUtils.toByteArray(is);

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