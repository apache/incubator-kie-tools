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

package org.kie.workbench.common.screens.datasource.management.util;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import org.appformer.maven.integration.Aether;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.installation.InstallRequest;
import org.eclipse.aether.installation.InstallResult;
import org.eclipse.aether.installation.InstallationException;
import org.eclipse.aether.resolution.ArtifactRequest;
import org.eclipse.aether.resolution.ArtifactResolutionException;
import org.eclipse.aether.resolution.ArtifactResult;

import org.junit.After;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class MavenArtifactResolverTest {

    @After
    public void tearDown() {
        deleteArtifactIFPresent();
    }

    private void deleteArtifactIFPresent() {
        ArtifactRequest artifactRequest = new ArtifactRequest();
        artifactRequest.setArtifact(getArtifact());
        try {
            ArtifactResult result = Aether.getAether().getSystem().resolveArtifact(Aether.getAether().getSession(),
                                                                                   artifactRequest);
            File artifactFile = result.getArtifact().getFile();
            assertThat(artifactFile.delete()).isTrue();
        } catch (Exception ex) {
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
    public void resolveArtifactNotOffline() throws Exception {
        final boolean[] executedOffline = {false};
        RepositorySystemSession session = Aether.getAether().getSession();
        assertThat(checksIfArtifactIsPresent(session)).isFalse();

        File file = new File("target/test-classes/uberfire-m2repo-editor-backend-100-SNAPSHOT.jar");
        assertThat(file).exists();

        Artifact artifact = getArtifact();
        artifact = artifact.setFile(file);
        ArtifactRequest artifactRequest = new ArtifactRequest();
        artifactRequest.setArtifact(artifact);

        ArtifactResult result;
        try {
            Aether.getAether().getSystem().resolveArtifact(session,
                                                           artifactRequest);
        } catch (ArtifactResolutionException ex) {
            assertThat(ex).isNotNull();
        }

        deployTestJar(artifact,
                      session);

        MavenArtifactResolver resolver = new MavenArtifactResolver() {
            public URI resolve(final String groupId,
                               final String artifactId,
                               final String version) throws Exception {
                return internalResolver(false, groupId, artifactId, version);
            }

            URI resolveEmbedded(final String groupId,
                                final String artifactId,
                                final String version) throws IOException {
                executedOffline[0] = false;
                return super.resolveEmbedded(groupId, artifactId, version);
            }
        };
        URI uri = resolver.resolve(artifact.getGroupId(),
                                   artifact.getArtifactId(),
                                   artifact.getVersion());
        assertThat(uri).isNotNull();
        assertThat(uri.getPath()).endsWith(File.separator + "uberfire-m2repo-editor-backend" + File.separator + "100-SNAPSHOT" + File.separator + "uberfire-m2repo-editor-backend-100-SNAPSHOT.jar");
        result = Aether.getAether().getSystem().resolveArtifact(session,
                                                                artifactRequest);
        assertThat(result.isMissing()).isFalse();
        assertThat(result.isResolved()).isTrue();
        assertThat(executedOffline[0]).isFalse();
    }

    @Test
    public void resolveArtifactOffline() throws Exception {
        final boolean[] executedOffline = {true};
        RepositorySystemSession session = Aether.getAether().getSession();
        assertThat(checksIfArtifactIsPresent(session)).isFalse();

        File file = new File("target/test-classes/uberfire-m2repo-editor-backend-100-SNAPSHOT.jar");
        assertThat(file).exists();

        Artifact artifact = getArtifact();
        artifact = artifact.setFile(file);
        ArtifactRequest artifactRequest = new ArtifactRequest();
        artifactRequest.setArtifact(artifact);

        ArtifactResult result;
        try {
            Aether.getAether().getSystem().resolveArtifact(session,
                                                           artifactRequest);
        } catch (ArtifactResolutionException ex) {
            assertThat(ex).isNotNull();
        }

        deployTestJar(artifact,
                      session);

        MavenArtifactResolver resolver = new MavenArtifactResolver() {
            public URI resolve(final String groupId,
                               final String artifactId,
                               final String version) throws Exception {
                return internalResolver(true, groupId, artifactId, version);
            }

            URI resolveEmbedded(final String groupId,
                                final String artifactId,
                                final String version) throws IOException {
                executedOffline[0] = false;
                return super.resolveEmbedded(groupId, artifactId, version);
            }
        };
        URI uri = resolver.resolve(artifact.getGroupId(),
                                   artifact.getArtifactId(),
                                   artifact.getVersion());
        assertThat(uri).isNotNull();
        assertThat(uri.getPath()).endsWith(File.separator + "uberfire-m2repo-editor-backend" + File.separator + "100-SNAPSHOT" + File.separator + "uberfire-m2repo-editor-backend-100-SNAPSHOT.jar");
        result = Aether.getAether().getSystem().resolveArtifact(session,
                                                                artifactRequest);
        assertThat(result.isMissing()).isFalse();
        assertThat(result.isResolved()).isTrue();
        assertThat(executedOffline[0]).isTrue();
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

    private boolean deployTestJar(Artifact jarArtifact,
                                  RepositorySystemSession session) throws InstallationException {
        final InstallRequest installRequest = new InstallRequest();
        installRequest.addArtifact(jarArtifact);
        InstallResult result = Aether.getAether().getSystem().install(session,
                                                                      installRequest);
        return result.getArtifacts().size() == 1;
    }
}
