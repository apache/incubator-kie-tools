/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.guvnor.m2repo.backend.server;

import java.io.File;

import org.apache.maven.repository.internal.MavenRepositorySystemUtils;
import org.appformer.maven.integration.Aether;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.connector.basic.BasicRepositoryConnectorFactory;
import org.eclipse.aether.impl.DefaultServiceLocator;
import org.eclipse.aether.repository.LocalRepository;
import org.eclipse.aether.resolution.ArtifactRequest;
import org.eclipse.aether.resolution.ArtifactResolutionException;
import org.eclipse.aether.resolution.ArtifactResult;
import org.eclipse.aether.spi.connector.RepositoryConnectorFactory;
import org.eclipse.aether.spi.connector.transport.TransporterFactory;
import org.eclipse.aether.transport.file.FileTransporterFactory;
import org.eclipse.aether.transport.http.HttpTransporterFactory;
import org.guvnor.common.services.project.model.GAV;
import org.guvnor.m2repo.preferences.ArtifactRepositoryPreference;
import org.junit.After;
import org.junit.Test;

import static org.assertj.core.api.Assertions.*;

public class M2ServletContextListenerTest {

    private static RepositorySystemSession newSession(RepositorySystem system) {
        DefaultRepositorySystemSession session = MavenRepositorySystemUtils.newSession();
        LocalRepository localRepo = new LocalRepository(ArtifactRepositoryPreference.getGlobalM2RepoDirWithFallback());
        session.setLocalRepositoryManager(system.newLocalRepositoryManager(session,
                                                                           localRepo));

        return session;
    }

    private static RepositorySystem newRepositorySystem() {
        DefaultServiceLocator locator = MavenRepositorySystemUtils.newServiceLocator();
        locator.addService(RepositoryConnectorFactory.class,
                           BasicRepositoryConnectorFactory.class);
        locator.addService(TransporterFactory.class,
                           FileTransporterFactory.class);
        locator.addService(TransporterFactory.class,
                           HttpTransporterFactory.class);
        return locator.getService(RepositorySystem.class);
    }

    @After
    public void tearDown() throws Exception {
        deleteArtifactIFPresent();
    }

    private void deleteArtifactIFPresent() throws ArtifactResolutionException {
        ArtifactRequest artifactRequest = new ArtifactRequest();
        artifactRequest.setArtifact(getArtifact());
        ArtifactResult result = Aether.getAether().getSystem().resolveArtifact(newSession(newRepositorySystem()),
                                                                               artifactRequest);
        if (!result.isMissing()) {
            File artifactFile = result.getArtifact().getFile();
            assertThat(artifactFile.delete()).isTrue();
        }
    }

    private Artifact getArtifact() {
        Artifact jarArtifact = new DefaultArtifact("org.uberfire",
                                                   "uberfire-m2repo-editor-backend",
                                                   "jar",
                                                   "100-SNAPSHOT");
        return jarArtifact;
    }

    @Test
    public void deployJarsFrowWar() throws Exception {
        RepositorySystemSession session = newSession(newRepositorySystem());
        assertThat(checksIfArtifactIsPresent(session)).isFalse();

        File file = new File("target/test-classes/org/guvnor/m2repo/backend/server/uberfire-m2repo-editor-backend-100-SNAPSHOT.jar");
        assertThat(file).exists();

        M2ServletContextListener listener = new M2ServletContextListener();
        GAV deployed = listener.deployJar(file.getAbsolutePath(),
                                          session);
        assertThat(deployed.getGroupId()).isEqualTo("org.uberfire");
        assertThat(deployed.getArtifactId()).isEqualTo("uberfire-m2repo-editor-backend");
        assertThat(deployed.getVersion()).isEqualTo("100-SNAPSHOT");

        ArtifactRequest artifactRequest = new ArtifactRequest();
        artifactRequest.setArtifact(getArtifact());
        ArtifactResult result = Aether.getAether().getSystem().resolveArtifact(session,
                                                                               artifactRequest);
        assertThat(result.isMissing()).isFalse();
        assertThat(result.isResolved()).isTrue();
        String absolutePath = result.getArtifact().getFile().toString();
        String folder = absolutePath.substring(0,
                                               absolutePath.lastIndexOf(File.separator));
        File remoteRepos = new File(folder + File.separator + "_remote.repositories");
        assertThat(remoteRepos.exists()).isTrue();
        File metadata = new File(folder + File.separator + "maven-metadata-local.xml");
        assertThat(metadata.exists()).isTrue();
        File pom = new File(folder + File.separator + "uberfire-m2repo-editor-backend-100-SNAPSHOT.pom");
        assertThat(pom.exists()).isTrue();
    }

    private boolean checksIfArtifactIsPresent(RepositorySystemSession session) {
        try {
            ArtifactRequest artifactRequest = new ArtifactRequest();
            artifactRequest.setArtifact(getArtifact());
            Aether.getAether().getSystem().resolveArtifact(session,
                                                           artifactRequest);
            return true;
        } catch (ArtifactResolutionException e) {
            return false;
        }
    }
}
