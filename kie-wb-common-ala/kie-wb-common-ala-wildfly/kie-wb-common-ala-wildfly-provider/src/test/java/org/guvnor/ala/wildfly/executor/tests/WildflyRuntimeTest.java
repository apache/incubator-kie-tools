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
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.maven.project.MavenProject;
import org.appformer.maven.integration.embedder.MavenProjectLoader;
import org.arquillian.cube.HostIp;
import org.arquillian.cube.requirement.ArquillianConditionalRunner;
import org.guvnor.ala.build.maven.executor.MavenTestUtils;
import org.guvnor.ala.build.maven.model.impl.MavenProjectImpl;
import org.guvnor.ala.build.maven.util.MavenBuildExecutor;
import org.guvnor.ala.build.maven.util.RepositoryVisitor;
import org.guvnor.ala.registry.inmemory.InMemorySourceRegistry;
import org.guvnor.ala.source.Source;
import org.guvnor.ala.source.git.config.impl.GitConfigImpl;
import org.guvnor.ala.source.git.executor.GitConfigExecutor;
import org.guvnor.ala.wildfly.access.WildflyAppState;
import org.guvnor.ala.wildfly.access.WildflyClient;
import org.guvnor.ala.wildfly.executor.tests.requirement.RequiresNotWindows;
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

    private static File tempPath;
    private static String gitUrl;

    @HostIp
    private String ip;

    @BeforeClass
    public static void setUp() throws Exception {
        tempPath = Files.createTempDirectory("ooo").toFile();
        gitUrl = MavenTestUtils.createGitRepoWithPom(tempPath);
    }

    @AfterClass
    public static void tearDown() {
        FileUtils.deleteQuietly(tempPath);
    }

    @Test
    public void waitForAppBuildTest() {
        final Optional<Source> _source = new GitConfigExecutor(new InMemorySourceRegistry()).apply(new GitConfigImpl(tempPath.getAbsolutePath(),
                                                                                                                     "master",
                                                                                                                     gitUrl,
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
        final Path projectRoot = source.getPath();
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
                                                                                                source.getPath(),
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

}
