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
package org.guvnor.ala.build.maven.executor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.maven.repository.internal.MavenRepositorySystemUtils;
import org.appformer.maven.integration.embedder.MavenSettings;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.impl.DefaultServiceLocator;
import org.eclipse.aether.installation.InstallRequest;
import org.eclipse.aether.repository.LocalRepository;
import org.guvnor.ala.build.Binary;
import org.guvnor.ala.build.maven.config.impl.MavenDependencyConfigImpl;
import org.guvnor.ala.build.maven.model.MavenBinary;
import org.guvnor.ala.pipeline.Input;
import org.guvnor.ala.pipeline.Pipeline;
import org.guvnor.ala.pipeline.PipelineFactory;
import org.guvnor.ala.pipeline.execution.PipelineExecutor;
import org.guvnor.ala.registry.BuildRegistry;
import org.guvnor.ala.registry.inmemory.InMemoryBuildRegistry;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static java.util.Collections.singletonList;
import static org.appformer.maven.integration.embedder.MavenSettings.CUSTOM_SETTINGS_PROPERTY;
import static org.junit.Assert.*;

public class MavenDependencyConfigExecutorTest {

    private File m2Folder;

    @Before
    public void setUp() throws IOException {
        m2Folder = Files.createTempDirectory("temp-m2").toFile();
    }

    @After
    public void tearDown() {
        FileUtils.deleteQuietly(m2Folder);
    }

    @Test
    public void testMavenDependencyAPI() throws Exception {
        final String groupId = "org.guvnor.ala";
        final String artifactId = "maven-ala-artifact-test";
        final String version = "1";
        final String artifactPath = "/org/guvnor/ala/maven-ala-artifact-test/1/maven-ala-artifact-test-1.pom";

        final String oldSettingsXmlPath = System.getProperty(CUSTOM_SETTINGS_PROPERTY);
        try {
            final Path settingsXmlPath = generateSettingsXml();
            System.setProperty(CUSTOM_SETTINGS_PROPERTY,
                               settingsXmlPath.toString());
            MavenSettings.reinitSettings();

            installArtifactLocally(groupId,
                                   artifactId,
                                   version);

            final BuildRegistry buildRegistry = new InMemoryBuildRegistry();

            final Pipeline pipe = PipelineFactory
                    .newBuilder()
                    .addConfigStage("Maven Artifact",
                                    new MavenDependencyConfigImpl())
                    .buildAs("my pipe");

            final PipelineExecutor executor = new PipelineExecutor(singletonList(new MavenDependencyConfigExecutor(buildRegistry)));

            executor.execute(new Input() {
                                 {
                                     put("artifact",
                                         groupId + ":" + artifactId + ":pom:" + version);
                                 }
                             },
                             pipe,
                             System.out::println);

            final List<Binary> allBinaries = buildRegistry.getAllBinaries();
            assertNotNull(allBinaries);
            assertEquals(1,
                         allBinaries.size());
            assertTrue(allBinaries.get(0) instanceof MavenBinary);
            final MavenBinary binary = (MavenBinary) allBinaries.get(0);
            assertEquals("Maven",
                         binary.getType());
            assertEquals(artifactId,
                         binary.getName());
            assertEquals(groupId,
                         binary.getGroupId());
            assertEquals(artifactId,
                         binary.getArtifactId());
            assertEquals(version,
                         binary.getVersion());
            assertEquals(Paths.get(m2Folder.getPath(), artifactPath).toString(),
                         binary.getPath().toString());
        } finally {
            if (oldSettingsXmlPath == null) {
                System.clearProperty(CUSTOM_SETTINGS_PROPERTY);
            } else {
                System.setProperty(CUSTOM_SETTINGS_PROPERTY,
                        oldSettingsXmlPath);
            }
            MavenSettings.reinitSettings();
        }
    }

    private void installArtifactLocally(final String groupId,
                                        final String artifactId,
                                        final String version) throws Exception {
        Artifact pomArtifact = new DefaultArtifact(groupId,
                                                   artifactId,
                                                   "pom",
                                                   version);
        final Path pom = getPom(groupId,
                                artifactId,
                                version);
        pomArtifact = pomArtifact.setFile(pom.toFile());

        final InstallRequest installRequest = new InstallRequest();
        installRequest.addArtifact(pomArtifact);

        final DefaultServiceLocator locator = MavenRepositorySystemUtils.newServiceLocator();
        RepositorySystem system = locator.getService(RepositorySystem.class);

        final DefaultRepositorySystemSession session = MavenRepositorySystemUtils.newSession();

        final LocalRepository localRepo = new LocalRepository(m2Folder);
        session.setLocalRepositoryManager(system.newLocalRepositoryManager(session,
                                                                           localRepo));

        system.install(session,
                       installRequest);
    }

    private Path generateSettingsXml() throws IOException {
        final String localRepositoryUrl = m2Folder.getAbsolutePath();
        String settingsXml =
                "<settings xmlns=\"http://maven.apache.org/SETTINGS/1.0.0\"\n" +
                        "      xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                        "      xsi:schemaLocation=\"http://maven.apache.org/SETTINGS/1.0.0\n" +
                        "                          http://maven.apache.org/xsd/settings-1.0.0.xsd\">\n" +
                        "  <localRepository>" + localRepositoryUrl + "</localRepository>\n" +
                        "  <offline>true</offline>\n" +
                        "</settings>\n";

        final Path settingsXmlPath = Files.createTempFile(m2Folder.toPath(),
                                                          "settings",
                                                          ".xml");
        Files.write(settingsXmlPath,
                    settingsXml.getBytes());
        return settingsXmlPath;
    }

    protected Path getPom(final String groupId,
                          final String artifactId,
                          final String version) throws IOException {
        String pom =
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "<project xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                        "         xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd\">\n" +
                        "  <modelVersion>4.0.0</modelVersion>\n" +
                        "\n" +
                        "  <groupId>" + groupId + "</groupId>\n" +
                        "  <artifactId>" + artifactId + "</artifactId>\n" +
                        "  <version>" + version + "</version>\n" +
                        "  <packaging>pom</packaging>\n" +
                        "\n";
        pom += "</project>";

        final Path pomXmlPath = Files.createTempFile(m2Folder.toPath(),
                                                     "pom",
                                                     ".xml");
        Files.write(pomXmlPath,
                    pom.getBytes());
        return pomXmlPath;
    }
}
