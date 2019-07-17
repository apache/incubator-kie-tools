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
import java.util.Set;

import org.guvnor.structure.contributors.Contributor;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.uberfire.backend.vfs.Path;
import org.uberfire.java.nio.base.version.VersionRecord;
import org.uberfire.spaces.Space;

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
        public RepositoryInfo getRepositoryInfo(Space space,
                                                String alias) {
            RepositoryInfo result = repositoryService.getRepositoryInfo(space,
                                                                        alias);
            remoteCallback.callback(result);
            return result;
        }

        @Override
        public List<VersionRecord> getRepositoryHistory(Space space,
                                                        String alias,
                                                        int startIndex) {
            List<VersionRecord> result = repositoryService.getRepositoryHistory(space,
                                                                                alias,
                                                                                startIndex);
            remoteCallback.callback(result);
            return result;
        }

        @Override
        public List<VersionRecord> getRepositoryHistory(Space space,
                                                        String alias,
                                                        int startIndex,
                                                        int endIndex) {
            List<VersionRecord> result = repositoryService.getRepositoryHistory(space,
                                                                                alias,
                                                                                startIndex,
                                                                                endIndex);
            remoteCallback.callback(result);
            return result;
        }

        @Override
        public List<VersionRecord> getRepositoryHistoryAll(Space space,
                                                           String alias) {
            List<VersionRecord> result = repositoryService.getRepositoryHistoryAll(space,
                                                                                   alias);
            remoteCallback.callback(result);
            return result;
        }

        @Override
        public Repository getRepositoryFromSpace(Space currentSpace,
                                                 String alias) {
            Repository result = repositoryService.getRepositoryFromSpace(currentSpace,
                                                                         alias);
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
        public Repository getRepository(Space space,
                                        Path root) {
            Repository result = repositoryService.getRepository(space,
                                                                root);
            remoteCallback.callback(result);
            return result;
        }

        @Override
        public Collection<Repository> getAllRepositories(Space space) {
            Collection<Repository> result = repositoryService.getAllRepositories(space);
            remoteCallback.callback(result);
            return result;
        }

        @Override
        public Collection<Repository> getAllRepositories(Space space,
                                                         boolean includeDeleted) {
            Collection<Repository> result = repositoryService.getAllRepositories(space,
                                                                                 includeDeleted);
            remoteCallback.callback(result);
            return result;
        }

        @Override
        public Collection<Repository> getAllDeletedRepositories(Space space) {
            Collection<Repository> result = repositoryService.getAllDeletedRepositories(space);
            remoteCallback.callback(result);
            return result;
        }

        @Override
        public Collection<Repository> getAllRepositoriesFromAllUserSpaces() {
            Collection<Repository> result = repositoryService.getAllRepositoriesFromAllUserSpaces();
            remoteCallback.callback(result);
            return result;
        }

        @Override
        public Collection<Repository> getRepositories(Space space) {
            Collection<Repository> result = repositoryService.getRepositories(space);
            remoteCallback.callback(result);
            return result;
        }

        @Override
        public Repository createRepository(final OrganizationalUnit organizationalUnit,
                                           final String scheme,
                                           final String alias,
                                           final RepositoryEnvironmentConfigurations configurations) throws RepositoryAlreadyExistsException {
            Repository result = repositoryService.createRepository(organizationalUnit,
                                                                   scheme,
                                                                   alias,
                                                                   configurations);
            remoteCallback.callback(result);
            return result;
        }

        @Override
        public Repository createRepository(OrganizationalUnit organizationalUnit,
                                           String scheme,
                                           String alias,
                                           RepositoryEnvironmentConfigurations configurations,
                                           Collection<Contributor> contributors) throws RepositoryAlreadyExistsException {

            Repository result = repositoryService.createRepository(organizationalUnit,
                                                                   scheme,
                                                                   alias,
                                                                   configurations,
                                                                   contributors);
            remoteCallback.callback(result);
            return null;
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
        public void updateContributors(Repository repository,
                                       List<Contributor> contributors) {
            repositoryService.updateContributors(repository,
                                                 contributors);
        }

        @Override
        public void removeRepository(Space space,
                                     String alias) {
            repositoryService.removeRepository(space,
                                               alias);
        }

        @Override
        public void removeRepositories(Space space,
                                       Set<String> aliases) {
            repositoryService.removeRepositories(space,
                                                 aliases);
        }
    }
}