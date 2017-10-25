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

import java.util.Map;

import org.eclipse.aether.RepositoryCache;
import org.eclipse.aether.RepositoryListener;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.SessionData;
import org.eclipse.aether.artifact.ArtifactTypeRegistry;
import org.eclipse.aether.collection.DependencyGraphTransformer;
import org.eclipse.aether.collection.DependencyManager;
import org.eclipse.aether.collection.DependencySelector;
import org.eclipse.aether.collection.DependencyTraverser;
import org.eclipse.aether.collection.VersionFilter;
import org.eclipse.aether.repository.AuthenticationSelector;
import org.eclipse.aether.repository.LocalRepository;
import org.eclipse.aether.repository.LocalRepositoryManager;
import org.eclipse.aether.repository.MirrorSelector;
import org.eclipse.aether.repository.ProxySelector;
import org.eclipse.aether.repository.WorkspaceReader;
import org.eclipse.aether.resolution.ArtifactDescriptorPolicy;
import org.eclipse.aether.resolution.ResolutionErrorPolicy;
import org.eclipse.aether.transfer.TransferListener;
import org.kie.soup.commons.validation.PortablePreconditions;

/**
 * A wrapper around the normal RepositorySystemSession to "fool" Maven into not checking local for remote artifacts
 */
public class MavenRepositorySystemSessionWrapper implements RepositorySystemSession {

    private final String tempLocalRepositoryBaseDir;
    private final RepositorySystemSession delegate;

    public MavenRepositorySystemSessionWrapper(final String tempLocalRepositoryBaseDir,
                                               final RepositorySystemSession delegate) {
        this.tempLocalRepositoryBaseDir = PortablePreconditions.checkNotNull("tempLocalRepositoryBaseDir",
                                                                             tempLocalRepositoryBaseDir);
        this.delegate = PortablePreconditions.checkNotNull("delegate",
                                                           delegate);
    }

    @Override
    public boolean isOffline() {
        return delegate.isOffline();
    }

    @Override
    public boolean isIgnoreArtifactDescriptorRepositories() {
        return delegate.isIgnoreArtifactDescriptorRepositories();
    }

    @Override
    public ResolutionErrorPolicy getResolutionErrorPolicy() {
        return delegate.getResolutionErrorPolicy();
    }

    @Override
    public ArtifactDescriptorPolicy getArtifactDescriptorPolicy() {
        return delegate.getArtifactDescriptorPolicy();
    }

    @Override
    public String getChecksumPolicy() {
        return delegate.getChecksumPolicy();
    }

    @Override
    public String getUpdatePolicy() {
        return delegate.getUpdatePolicy();
    }

    @Override
    public LocalRepository getLocalRepository() {
        return delegate.getLocalRepository();
    }

    @Override
    public LocalRepositoryManager getLocalRepositoryManager() {
        return new MavenLocalRepositoryManagerWrapper(tempLocalRepositoryBaseDir,
                                                      delegate.getLocalRepositoryManager());
    }

    @Override
    public WorkspaceReader getWorkspaceReader() {
        return delegate.getWorkspaceReader();
    }

    @Override
    public RepositoryListener getRepositoryListener() {
        return delegate.getRepositoryListener();
    }

    @Override
    public TransferListener getTransferListener() {
        return delegate.getTransferListener();
    }

    @Override
    public Map<String, String> getSystemProperties() {
        return delegate.getSystemProperties();
    }

    @Override
    public Map<String, String> getUserProperties() {
        return delegate.getUserProperties();
    }

    @Override
    public Map<String, Object> getConfigProperties() {
        return delegate.getConfigProperties();
    }

    @Override
    public MirrorSelector getMirrorSelector() {
        return delegate.getMirrorSelector();
    }

    @Override
    public ProxySelector getProxySelector() {
        return delegate.getProxySelector();
    }

    @Override
    public AuthenticationSelector getAuthenticationSelector() {
        return delegate.getAuthenticationSelector();
    }

    @Override
    public ArtifactTypeRegistry getArtifactTypeRegistry() {
        return delegate.getArtifactTypeRegistry();
    }

    @Override
    public DependencyTraverser getDependencyTraverser() {
        return delegate.getDependencyTraverser();
    }

    @Override
    public DependencyManager getDependencyManager() {
        return delegate.getDependencyManager();
    }

    @Override
    public DependencySelector getDependencySelector() {
        return delegate.getDependencySelector();
    }

    @Override
    public VersionFilter getVersionFilter() {
        return delegate.getVersionFilter();
    }

    @Override
    public DependencyGraphTransformer getDependencyGraphTransformer() {
        return delegate.getDependencyGraphTransformer();
    }

    @Override
    public SessionData getData() {
        return delegate.getData();
    }

    @Override
    public RepositoryCache getCache() {
        return delegate.getCache();
    }
}
