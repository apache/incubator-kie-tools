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

package org.kie.workbench.common.screens.datasource.management.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;

import javax.enterprise.context.ApplicationScoped;

import org.apache.maven.project.MavenProject;
import org.appformer.maven.integration.Aether;
import org.appformer.maven.integration.embedder.MavenProjectLoader;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.resolution.ArtifactRequest;
import org.eclipse.aether.resolution.ArtifactResult;
import org.guvnor.common.services.project.backend.server.utils.POMContentHandler;
import org.guvnor.common.services.project.model.Dependency;
import org.guvnor.common.services.project.model.GAV;
import org.guvnor.common.services.project.model.POM;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class MavenArtifactResolver {

    private static final Logger logger = LoggerFactory.getLogger(MavenArtifactResolver.class);
    private static final String JAR_ARTIFACT = "jar";
    private POMContentHandler pomContentHandler = new POMContentHandler();

    public MavenArtifactResolver() {
    }

    public URI resolve(final String groupId,
                       final String artifactId,
                       final String version) throws Exception {
        return internalResolver(MavenProjectLoader.IS_FORCE_OFFLINE,
                                groupId,
                                artifactId,
                                version);
    }

    URI internalResolver(final boolean isOffline,
                         final String groupId,
                         final String artifactId,
                         final String version) throws Exception {
        try {
            if (!isOffline) {
                return resolveEmbedded(groupId,
                                       artifactId,
                                       version);
            }
            GAV gav = new GAV(groupId,
                              artifactId,
                              version);
            org.eclipse.aether.artifact.Artifact jarArtifact = new DefaultArtifact(gav.getGroupId(),
                                                                                   gav.getArtifactId(),
                                                                                   JAR_ARTIFACT,
                                                                                   gav.getVersion());
            RepositorySystemSession session = Aether.getAether().getSession();
            ArtifactRequest artifactReq = new ArtifactRequest();
            artifactReq.setArtifact(jarArtifact);
            ArtifactResult result = Aether.getAether().getSystem().resolveArtifact(session,
                                                                                   artifactReq);
            return result.getArtifact().getFile().toURI();
        } catch (Exception e) {
            logger.error("Unable to get artifact: " + groupId + ":" + artifactId + ":" + version +
                                 " from maven repository",
                         e);
            throw new Exception("Unable to get artifact: " + groupId + ":" + artifactId + ":" + version +
                                        " from maven repository",
                                e);
        }
    }

    URI resolveEmbedded(final String groupId,
                        final String artifactId,
                        final String version) throws IOException {
        final POM projectPom = new POM(new GAV("resolver-dummy-group",
                                               "resolver-dummy-artifact",
                                               "resolver-dummy-version"));
        projectPom.getDependencies().add(new Dependency(new GAV(groupId, artifactId, version)));

        final String pomXML = pomContentHandler.toString(projectPom);

        final InputStream pomStream = new ByteArrayInputStream(pomXML.getBytes(StandardCharsets.UTF_8));
        final MavenProject mavenProject = MavenProjectLoader.parseMavenPom(pomStream);

        for (org.apache.maven.artifact.Artifact mavenArtifact : mavenProject.getArtifacts()) {
            if (groupId.equals(mavenArtifact.getGroupId()) &&
                    artifactId.equals(mavenArtifact.getArtifactId()) &&
                    version.equals(mavenArtifact.getVersion()) &&
                    mavenArtifact.getFile().exists()) {
                return mavenArtifact.getFile().toURI();
            }
        }
        return null;
    }
}
