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
 *
 */

package org.guvnor.m2repo.backend.server.repositories;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.maven.model.DeploymentRepository;
import org.apache.maven.model.DistributionManagement;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectBuildingException;
import org.apache.maven.settings.Server;
import org.apache.maven.settings.Settings;
import org.appformer.maven.integration.Aether;
import org.appformer.maven.integration.embedder.MavenEmbedder;
import org.appformer.maven.integration.embedder.MavenEmbedderException;
import org.appformer.maven.integration.embedder.MavenProjectLoader;
import org.appformer.maven.integration.embedder.MavenSettings;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.deployment.DeployRequest;
import org.eclipse.aether.deployment.DeploymentException;
import org.eclipse.aether.repository.Authentication;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.repository.RepositoryPolicy;
import org.guvnor.common.services.project.model.GAV;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DistributionManagementArtifactRepository implements ArtifactRepository {

    private String name;
    private Logger logger = LoggerFactory.getLogger(DistributionManagementArtifactRepository.class);

    public DistributionManagementArtifactRepository() {
    }

    public DistributionManagementArtifactRepository(final String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getRootDir() {
        return null;
    }

    @Override
    public Collection<File> listFiles(final List<String> wildcards) {
        return Collections.EMPTY_LIST;
    }

    @Override
    public Collection<Artifact> listArtifacts(final List<String> wildcards) {
        return Collections.EMPTY_LIST;
    }

    @Override
    public boolean containsArtifact(final GAV gav) {
        return false;
    }

    @Override
    public File getArtifactFileFromRepository(final GAV gav) {
        return null;
    }

    @Override
    public boolean isRepository() {
        return true;
    }

    @Override
    public boolean isPomRepository() {
        return false;
    }

    @Override
    public void deploy(final String pom,
                       final Artifact... artifacts) {
        try {

            MavenEmbedder embedder = MavenProjectLoader.newMavenEmbedder(false);
            DistributionManagement distributionManagement = getDistributionManagement(pom,
                                                                                      embedder);

            if (distributionManagement != null) {

                final boolean isSnapshot = Arrays.stream(artifacts).anyMatch(artifact -> artifact.isSnapshot());
                DeploymentRepository remoteRepository = null;
                if (isSnapshot) {
                    remoteRepository = distributionManagement.getSnapshotRepository();

                    //Maven documentation states use of the regular repository if the SNAPSHOT repository is undefined
                    //See https://maven.apache.org/pom.html#Repository and https://bugzilla.redhat.com/show_bug.cgi?id=1129573
                    if (remoteRepository == null) {
                        remoteRepository = distributionManagement.getRepository();
                    }
                } else {
                    remoteRepository = distributionManagement.getRepository();
                }

                //If the user has configured a distribution management module in the pom then we will attempt to deploy there.
                //If credentials are required those credentials must be provisioned in the user's settings.xml file
                if (remoteRepository != null) {
                    DeployRequest remoteRequest = new DeployRequest();

                    for (Artifact artifact : artifacts) {
                        remoteRequest.addArtifact(artifact);
                    }

                    remoteRequest.setRepository(getRemoteRepoFromDeployment(remoteRepository,
                                                                            embedder));

                    Aether.getAether().getSystem().deploy(Aether.getAether().getSession(),
                                                          remoteRequest);
                }
            }
        } catch (DeploymentException e) {
            throw new RuntimeException(e);
        }
    }

    private DistributionManagement getDistributionManagement(final String pomXML,
                                                             final MavenEmbedder embedder) {
        final InputStream is = new ByteArrayInputStream(pomXML.getBytes(Charset.forName("UTF-8")));
        MavenProject project = null;
        try {
            project = embedder.readProject(is);
        } catch (ProjectBuildingException e) {
            logger.error("Unable to build Maven project from POM",
                         e);
            throw new RuntimeException(e);
        } catch (MavenEmbedderException e) {
            logger.error("Unable to build Maven project from POM",
                         e);
            throw new RuntimeException(e);
        } finally {
            try {
                is.close();
            } catch (IOException ioe) {
                //Swallow
            }
        }
        return project.getDistributionManagement();
    }

    private RemoteRepository getRemoteRepoFromDeployment(final DeploymentRepository repo,
                                                         final MavenEmbedder embedder) {
        RemoteRepository.Builder remoteRepoBuilder = new RemoteRepository.Builder(repo.getId(),
                                                                                  repo.getLayout(),
                                                                                  repo
                                                                                          .getUrl())
                .setSnapshotPolicy(new RepositoryPolicy(true,
                                                        RepositoryPolicy.UPDATE_POLICY_DAILY,
                                                        RepositoryPolicy.CHECKSUM_POLICY_WARN))
                .setReleasePolicy(new RepositoryPolicy(true,
                                                       RepositoryPolicy.UPDATE_POLICY_ALWAYS,
                                                       RepositoryPolicy.CHECKSUM_POLICY_WARN));

        Settings settings = MavenSettings.getSettings();
        Server server = settings.getServer(repo.getId());

        if (server != null) {
            Authentication authentication = embedder.getMavenSession().getRepositorySession()
                    .getAuthenticationSelector()
                    .getAuthentication(remoteRepoBuilder.build());
            remoteRepoBuilder.setAuthentication(authentication);
        }

        return remoteRepoBuilder.build();
    }

    @Override
    public void delete(final GAV gav) {

    }
}
