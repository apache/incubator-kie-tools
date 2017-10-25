/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.guvnor.ala.wildfly.executor.tests;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.maven.project.MavenProject;
import org.appformer.maven.integration.embedder.MavenProjectLoader;
import org.arquillian.cube.CubeController;
import org.arquillian.cube.HostIp;
import org.arquillian.cube.requirement.ArquillianConditionalRunner;
import org.guvnor.ala.build.maven.model.impl.MavenProjectImpl;
import org.guvnor.ala.build.maven.util.MavenBuildExecutor;
import org.guvnor.ala.build.maven.util.RepositoryVisitor;
import org.guvnor.ala.registry.inmemory.InMemorySourceRegistry;
import org.guvnor.ala.source.Source;
import org.guvnor.ala.source.git.GitHub;
import org.guvnor.ala.source.git.GitRepository;
import org.guvnor.ala.source.git.config.impl.GitConfigImpl;
import org.guvnor.ala.source.git.executor.GitConfigExecutor;
import org.guvnor.ala.wildfly.access.WildflyAppState;
import org.guvnor.ala.wildfly.access.WildflyClient;
import org.guvnor.ala.wildfly.executor.tests.requirement.RequiresNotWindows;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.uberfire.java.nio.file.Path;

import static org.guvnor.ala.runtime.RuntimeState.RUNNING;
import static org.guvnor.ala.runtime.RuntimeState.UNKNOWN;
import static org.junit.Assert.*;

/**
 * Test the Wildfly Provider by starting a docker image of wildfly and deploying
 * an application there.
 */
@RunWith(ArquillianConditionalRunner.class)
@RequiresNotWindows
public class WildflyRuntimeTest {

    private static final String CONTAINER = "swarm";
    private static File tempPath;

    @HostIp
    private String ip;

    @ArquillianResource
    private CubeController cc;

    @BeforeClass
    public static void setUp() {
        try {
            tempPath = Files.createTempDirectory("ooo").toFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @AfterClass
    public static void tearDown() {
        FileUtils.deleteQuietly(tempPath);
    }

    @Test
    @InSequence(1)
    public void shouldBeAbleToCreateAndStartTest() {
        cc.create(CONTAINER);
        cc.start(CONTAINER);
    }

    @Test
    @InSequence(2)
    public void waitForAppBuildTest() {
        final GitHub gitHub = new GitHub();
        final GitRepository repository = (GitRepository) gitHub.getRepository("salaboy/drools-workshop",
                                                                              new HashMap<String, String>() {
                                                                                  {
                                                                                      put("out-dir",
                                                                                          tempPath.getAbsolutePath());
                                                                                  }
                                                                              });
        final Optional<Source> _source = new GitConfigExecutor(new InMemorySourceRegistry()).apply(new GitConfigImpl(tempPath.getAbsolutePath(),
                                                                                                                     "master",
                                                                                                                     "https://github.com/salaboy/drools-workshop",
                                                                                                                     "drools-workshop-build",
                                                                                                                     "true"));

        assertTrue(_source.isPresent());
        final Source source = _source.get();
        assertNotNull(source);

        List<String> goals = new ArrayList<>();
        goals.add("package");
        Properties properties = new Properties();
        properties.setProperty("failIfNoTests",
                               "false");
        final Path projectRoot = source.getPath().resolve("drools-webapp-example");
        final InputStream pomStream = org.uberfire.java.nio.file.Files.newInputStream(projectRoot.resolve("pom.xml"));
        final MavenProject project = MavenProjectLoader.parseMavenPom(pomStream);
        RepositoryVisitor repositoryVisitor = new RepositoryVisitor(projectRoot,
                                                                    project.getName());
        final String expectedBinary = project.getArtifact().getArtifactId() + "-" + project.getArtifact().getVersion() + "." + project.getArtifact().getType();
        final org.guvnor.ala.build.maven.model.MavenProject mavenProject = new MavenProjectImpl(project.getId(),
                                                                                                project.getArtifact().getType(),
                                                                                                project.getName(),
                                                                                                expectedBinary,
                                                                                                source.getPath(),
                                                                                                source.getPath().resolve("drools-webapp-example"),
                                                                                                source.getPath().resolve("target").resolve(expectedBinary).toAbsolutePath(),
                                                                                                repositoryVisitor.getRoot().getAbsolutePath(),
                                                                                                null);

        final File pom = new File(mavenProject.getTempDir(),
                                  "pom.xml");
        MavenBuildExecutor.executeMaven(pom,
                                        properties,
                                        goals.toArray(new String[0]));

        final File file = new File(repositoryVisitor.getRoot().getAbsolutePath() + "/target/" + mavenProject.getExpectedBinary());

        WildflyClient wildflyClient = new WildflyClient("",
                                                        "admin",
                                                        "Admin#70365",
                                                        ip,
                                                        8080,
                                                        9990);

        wildflyClient.deploy(file);

        final String id = file.getName();

        WildflyAppState appState = wildflyClient.getAppState(id);

        assertNotNull(appState);

        assertTrue(appState.getState().equals(RUNNING));

        wildflyClient.undeploy(id);

        appState = wildflyClient.getAppState(id);
        assertNotNull(appState);

        assertTrue(appState.getState().equals(UNKNOWN));
        wildflyClient.deploy(file);

        appState = wildflyClient.getAppState(id);
        assertNotNull(appState);

        assertTrue(appState.getState().equals(RUNNING));
    }

    @Test
    @InSequence(3)
    public void shouldBeAbleToStopAndDestroyTest() {
        cc.stop(CONTAINER);
        cc.destroy(CONTAINER);
    }
}
