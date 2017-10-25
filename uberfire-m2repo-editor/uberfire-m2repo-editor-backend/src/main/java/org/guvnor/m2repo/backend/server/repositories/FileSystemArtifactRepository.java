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

import java.io.File;
import java.net.MalformedURLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOCase;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.appformer.maven.integration.Aether;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.deployment.DeployRequest;
import org.eclipse.aether.deployment.DeploymentException;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.repository.RepositoryPolicy;
import org.eclipse.aether.resolution.ArtifactRequest;
import org.eclipse.aether.resolution.ArtifactResolutionException;
import org.eclipse.aether.resolution.ArtifactResult;
import org.guvnor.common.services.project.model.GAV;
import org.guvnor.m2repo.backend.server.ArtifactImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.apache.commons.io.FilenameUtils;

public class FileSystemArtifactRepository implements ArtifactRepository {

    private String name;
    private Logger logger = LoggerFactory.getLogger(FileSystemArtifactRepository.class);

    private RemoteRepository repository;
    private String repositoryDirectory;

    public FileSystemArtifactRepository() {
    }

    public FileSystemArtifactRepository(final String name,
                                        final String dir) {
        this.name = name;
        final String m2RepoDir = FilenameUtils.normalize(dir.trim() + File.separatorChar);
        logger.info("Maven Repository root set to: " + m2RepoDir);

        //Ensure repository root has been created
        final File root = new File(m2RepoDir);
        if (!root.exists()) {
            logger.info("Creating Maven Repository root: " + m2RepoDir);
            root.mkdirs();
        }
        this.repositoryDirectory = dir;
        this.repository = this.createRepository(dir);
        Aether.getAether().getRepositories().add(this.getRepository());
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getRootDir() {
        return this.getRepositoryDirectory();
    }

    @Override
    public Collection<File> listFiles(final List<String> wildcards) {
        return FileUtils.listFiles(new File(this.getRepositoryDirectory()),
                                   new WildcardFileFilter(wildcards,
                                                          IOCase.INSENSITIVE),
                                   DirectoryFileFilter.DIRECTORY);
    }

    @Override
    public Collection<Artifact> listArtifacts(final List<String> wildcards) {
        final Collection<File> files = this.listFiles(wildcards);

        return files.stream().map(file -> {
            final HashMap<String, String> map = new HashMap<String, String>();
            map.put("repository",
                    this.getName());
            final ArtifactImpl artifact = new ArtifactImpl(file);
            artifact.setProperties(map);
            return artifact;
        }).collect(Collectors.toList());
    }

    @Override
    public boolean containsArtifact(final GAV gav) {
        ArtifactRequest request = createArtifactRequest(gav);
        try {
            Aether aether = Aether.getAether();
            aether.getSystem().resolveArtifact(aether.getSession(),
                                               request);
        } catch (ArtifactResolutionException e) {
            logger.trace("Artifact {} not found.",
                         gav,
                         e);
            return false;
        }
        logger.trace("Artifact {} found.",
                     gav);
        return true;
    }

    @Override
    public File getArtifactFileFromRepository(final GAV gav) {
        ArtifactRequest request = createArtifactRequest(gav);
        ArtifactResult result = null;
        try {
            result = Aether.getAether().getSystem().resolveArtifact(
                    Aether.getAether().getSession(),
                    request);
        } catch (ArtifactResolutionException e) {
            logger.warn(e.getMessage(),
                        e);
        }

        if (result == null) {
            return null;
        }

        File artifactFile = null;
        if (result.isResolved() && !result.isMissing()) {
            artifactFile = result.getArtifact().getFile();
        }

        return artifactFile;
    }

    @Override
    public boolean isRepository() {
        return true;
    }

    @Override
    public boolean isPomRepository() {
        return true;
    }

    @Override
    public void deploy(final String pom,
                       final Artifact... artifacts) {
        try {
            final DeployRequest deployRequest = new DeployRequest();

            for (Artifact artifact : artifacts) {
                deployRequest.addArtifact(artifact);
            }

            deployRequest.setRepository(getRepository());

            Aether.getAether().getSystem().deploy(Aether.getAether().getSession(),
                                                  deployRequest);
        } catch (DeploymentException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(final GAV gav) {

    }

    private ArtifactRequest createArtifactRequest(final GAV gav) {
        ArtifactRequest request = new ArtifactRequest();
        request.addRepository(this.getRepository());
        DefaultArtifact artifact = new DefaultArtifact(gav.getGroupId(),
                                                       gav.getArtifactId(),
                                                       "jar",
                                                       gav.getVersion());
        request.setArtifact(artifact);
        return request;
    }

    protected RemoteRepository getRepository() {
        return this.repository;
    }

    private RemoteRepository createRepository(final String dir) {
        File m2RepoDir = new File(dir);
        if (!m2RepoDir.exists()) {
            logger.error("Repository root does not exist: " + dir);
            throw new IllegalArgumentException("Repository root does not exist: " + dir);
        }

        try {
            String localRepositoryUrl = m2RepoDir.toURI().toURL().toExternalForm();
            return new RemoteRepository.Builder(this.getName(),
                                                "default",
                                                localRepositoryUrl)
                    .setSnapshotPolicy(new RepositoryPolicy(true,
                                                            RepositoryPolicy.UPDATE_POLICY_DAILY,
                                                            RepositoryPolicy.CHECKSUM_POLICY_WARN))
                    .setReleasePolicy(new RepositoryPolicy(true,
                                                           RepositoryPolicy.UPDATE_POLICY_ALWAYS,
                                                           RepositoryPolicy.CHECKSUM_POLICY_WARN))
                    .build();
        } catch (MalformedURLException e) {
            logger.error(e.getMessage(),
                         e);
            throw new RuntimeException(e);
        }
    }

    private String getRepositoryDirectory() {
        return repositoryDirectory;
    }
}
