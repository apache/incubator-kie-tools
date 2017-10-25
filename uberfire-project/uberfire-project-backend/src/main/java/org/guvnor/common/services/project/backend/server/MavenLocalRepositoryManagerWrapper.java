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

package org.guvnor.common.services.project.backend.server;

import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.metadata.Metadata;
import org.eclipse.aether.repository.LocalArtifactRegistration;
import org.eclipse.aether.repository.LocalArtifactRequest;
import org.eclipse.aether.repository.LocalArtifactResult;
import org.eclipse.aether.repository.LocalMetadataRegistration;
import org.eclipse.aether.repository.LocalMetadataRequest;
import org.eclipse.aether.repository.LocalMetadataResult;
import org.eclipse.aether.repository.LocalRepository;
import org.eclipse.aether.repository.LocalRepositoryManager;
import org.eclipse.aether.repository.RemoteRepository;
import org.kie.soup.commons.validation.PortablePreconditions;

/**
 * A wrapper around the normal LocalRepositoryManager to "fool" Maven into not checking local for remote artifacts
 */
public class MavenLocalRepositoryManagerWrapper implements LocalRepositoryManager {

    private final String tempLocalRepositoryBaseDir;
    private final LocalRepositoryManager delegate;

    public MavenLocalRepositoryManagerWrapper(final String tempLocalRepositoryBaseDir,
                                              final LocalRepositoryManager delegate) {
        this.tempLocalRepositoryBaseDir = PortablePreconditions.checkNotNull("tempLocalRepositoryBaseDir",
                                                                             tempLocalRepositoryBaseDir);
        this.delegate = PortablePreconditions.checkNotNull("delegate",
                                                           delegate);
    }

    @Override
    public LocalRepository getRepository() {
        return new LocalRepository(tempLocalRepositoryBaseDir);
    }

    @Override
    public String getPathForLocalArtifact(final Artifact artifact) {
        return delegate.getPathForLocalArtifact(artifact);
    }

    @Override
    public String getPathForRemoteArtifact(final Artifact artifact,
                                           final RemoteRepository repository,
                                           final String context) {
        return delegate.getPathForRemoteArtifact(artifact,
                                                 repository,
                                                 context);
    }

    @Override
    public String getPathForLocalMetadata(final Metadata metadata) {
        return delegate.getPathForLocalMetadata(metadata);
    }

    @Override
    public String getPathForRemoteMetadata(final Metadata metadata,
                                           final RemoteRepository repository,
                                           final String context) {
        return delegate.getPathForRemoteMetadata(metadata,
                                                 repository,
                                                 context);
    }

    @Override
    public LocalArtifactResult find(final RepositorySystemSession session,
                                    final LocalArtifactRequest request) {
        return new LocalArtifactResult(request);
    }

    @Override
    public void add(final RepositorySystemSession session,
                    final LocalArtifactRegistration request) {
        delegate.add(session,
                     request);
    }

    @Override
    public LocalMetadataResult find(final RepositorySystemSession session,
                                    final LocalMetadataRequest request) {
        return delegate.find(session,
                             request);
    }

    @Override
    public void add(final RepositorySystemSession session,
                    final LocalMetadataRegistration request) {
        delegate.add(session,
                     request);
    }
}
