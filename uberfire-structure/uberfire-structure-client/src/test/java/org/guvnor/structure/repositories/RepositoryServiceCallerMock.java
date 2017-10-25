/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.guvnor.structure.repositories;

import java.util.Collection;
import java.util.List;

import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.uberfire.backend.vfs.Path;
import org.uberfire.java.nio.base.version.VersionRecord;

/**
 * Utility class for client side testing.
 * (analogous usage as OrganizationalUnitServiceCallerMock)
 */
public class RepositoryServiceCallerMock
        implements Caller<RepositoryService> {

    protected RepositoryServiceWrapper repositoryServiceWrapper;

    protected RemoteCallback remoteCallback;

    public RepositoryServiceCallerMock(RepositoryService repositoryService) {
        repositoryServiceWrapper = new RepositoryServiceWrapper(repositoryService);
    }

    @Override
    public RepositoryService call() {
        return repositoryServiceWrapper;
    }

    @Override
    public RepositoryService call(RemoteCallback<?> remoteCallback) {
        return call(remoteCallback,
                    null);
    }

    @Override
    public RepositoryService call(RemoteCallback<?> remoteCallback,
                                  ErrorCallback<?> errorCallback) {
        this.remoteCallback = remoteCallback;
        return repositoryServiceWrapper;
    }

    private class RepositoryServiceWrapper
            implements RepositoryService {

        RepositoryService repositoryService;

        public RepositoryServiceWrapper(RepositoryService repositoryService) {
            this.repositoryService = repositoryService;
        }

        @Override
        public RepositoryInfo getRepositoryInfo(String alias) {
            RepositoryInfo result = repositoryService.getRepositoryInfo(alias);
            remoteCallback.callback(result);
            return result;
        }

        @Override
        public List<VersionRecord> getRepositoryHistory(String alias,
                                                        int startIndex) {
            List<VersionRecord> result = repositoryService.getRepositoryHistory(alias,
                                                                                startIndex);
            remoteCallback.callback(result);
            return result;
        }

        @Override
        public List<VersionRecord> getRepositoryHistory(String alias,
                                                        int startIndex,
                                                        int endIndex) {
            List<VersionRecord> result = repositoryService.getRepositoryHistory(alias,
                                                                                startIndex,
                                                                                endIndex);
            remoteCallback.callback(result);
            return result;
        }

        @Override
        public List<VersionRecord> getRepositoryHistoryAll(String alias) {
            List<VersionRecord> result = repositoryService.getRepositoryHistoryAll(alias);
            remoteCallback.callback(result);
            return result;
        }

        @Override
        public Repository getRepository(String alias) {
            Repository result = repositoryService.getRepository(alias);
            remoteCallback.callback(result);
            return result;
        }

        @Override
        public Repository getRepository(Path root) {
            Repository result = repositoryService.getRepository(root);
            remoteCallback.callback(result);
            return result;
        }

        @Override
        public Collection<Repository> getAllRepositories() {
            Collection<Repository> result = repositoryService.getAllRepositories();
            remoteCallback.callback(result);
            return result;
        }

        @Override
        public Collection<Repository> getRepositories() {
            Collection<Repository> result = repositoryService.getRepositories();
            remoteCallback.callback(result);
            return result;
        }

        @Override
        public Repository createRepository(final OrganizationalUnit organizationalUnit,
                                           final String scheme,
                                           final String alias,
                                           final RepositoryEnvironmentConfigurations configuration) throws RepositoryAlreadyExistsException {
            Repository result = repositoryService.createRepository(organizationalUnit,
                                                                   scheme,
                                                                   alias,
                                                                   configuration);
            remoteCallback.callback(result);
            return result;
        }

        @Override
        public Repository createRepository(final String scheme,
                                           final String alias,
                                           final RepositoryEnvironmentConfigurations configuration) throws RepositoryAlreadyExistsException {
            Repository result = repositoryService.createRepository(scheme,
                                                                   alias,
                                                                   configuration);
            remoteCallback.callback(result);
            return result;
        }

        @Override
        public String normalizeRepositoryName(String name) {
            String result = repositoryService.normalizeRepositoryName(name);
            remoteCallback.callback(result);
            return result;
        }

        @Override
        public boolean validateRepositoryName(String name) {
            boolean result = repositoryService.validateRepositoryName(name);
            remoteCallback.callback(result);
            return result;
        }

        @Override
        public void addGroup(Repository repository,
                             String group) {
            repositoryService.addGroup(repository,
                                       group);
        }

        @Override
        public void removeGroup(Repository repository,
                                String group) {
            repositoryService.removeGroup(repository,
                                          group);
        }

        @Override
        public void removeRepository(String alias) {
            repositoryService.removeRepository(alias);
        }

        @Override
        public Repository updateRepositoryConfiguration(final Repository repository,
                                                        final RepositoryEnvironmentConfigurations config) {
            Repository result = repositoryService.updateRepositoryConfiguration(repository,
                                                                                config);
            remoteCallback.callback(result);
            return result;
        }
    }
}