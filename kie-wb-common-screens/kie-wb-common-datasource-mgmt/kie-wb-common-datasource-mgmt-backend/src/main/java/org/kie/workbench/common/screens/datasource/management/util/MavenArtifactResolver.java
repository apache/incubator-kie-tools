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

import java.net.URI;
import javax.enterprise.context.ApplicationScoped;

import org.apache.maven.repository.internal.MavenRepositorySystemUtils;
import org.appformer.maven.integration.Aether;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.connector.basic.BasicRepositoryConnectorFactory;
import org.eclipse.aether.impl.DefaultServiceLocator;
import org.eclipse.aether.repository.LocalRepository;
import org.eclipse.aether.resolution.ArtifactRequest;
import org.eclipse.aether.resolution.ArtifactResult;
import org.eclipse.aether.spi.connector.RepositoryConnectorFactory;
import org.eclipse.aether.spi.connector.transport.TransporterFactory;
import org.eclipse.aether.transport.file.FileTransporterFactory;
import org.eclipse.aether.transport.http.HttpTransporterFactory;
import org.guvnor.common.services.project.model.GAV;
import org.guvnor.m2repo.preferences.ArtifactRepositoryPreference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class MavenArtifactResolver {

    private static final Logger logger = LoggerFactory.getLogger(MavenArtifactResolver.class);
    private static final String JAR_ARTIFACT = "jar";

    public MavenArtifactResolver() {
    }

    public static RepositorySystemSession getAetherSessionWithGlobalRepo() {
        return newSession(newRepositorySystem());
    }

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

    public URI resolve(final String groupId,
                       final String artifactId,
                       final String version) throws Exception {
        try {
            GAV gav = new GAV(groupId,
                              artifactId,
                              version);
            org.eclipse.aether.artifact.Artifact jarArtifact = new DefaultArtifact(gav.getGroupId(),
                                                                                   gav.getArtifactId(),
                                                                                   JAR_ARTIFACT,
                                                                                   gav.getVersion());
            RepositorySystemSession session = newSession(newRepositorySystem());
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
}
