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

package org.guvnor.structure.backend.repositories;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.guvnor.structure.organizationalunit.config.RepositoryInfo;
import org.guvnor.structure.organizationalunit.config.SpaceConfigStorageRegistry;
import org.guvnor.structure.repositories.Branch;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.server.repositories.RepositoryFactory;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.spaces.Space;

/**
 * Cache for configured repositories.
 * <p>
 * If you plan to use this outside of ProjectService make sure you know what you are doing.
 * <p>
 * It is safe to get data from this class, but any editing should be done through ProjectService.
 * Still if possible use ProjectService for accessing the repositories. It is part of a public API
 * and this is hidden in the -backend on purpose.
 */
@ApplicationScoped
public class ConfiguredRepositoriesImpl implements ConfiguredRepositories {

    private RepositoryFactory repositoryFactory;
    private SpaceConfigStorageRegistry spaceConfigStorage;

    public ConfiguredRepositoriesImpl() {
    }

    @Inject
    public ConfiguredRepositoriesImpl(final RepositoryFactory repositoryFactory,
                                      final SpaceConfigStorageRegistry spaceConfigStorage) {
        this.repositoryFactory = repositoryFactory;
        this.spaceConfigStorage = spaceConfigStorage;
    }

    /**
     * @param space Space of the repository.
     * @param alias Name of the repository.
     * @return Repository that has a random branch as a root, usually master if master exists.
     */
    public Repository getRepositoryByRepositoryAlias(final Space space,
                                                     final String alias) {

        return this.getRepositoryByRepositoryAlias(space,
                                                   alias,
                                                   false);
    }

    /**
     * @param alias Name of the repository.
     * @param space Space of the repository.
     * @return Repository that has a random branch as a root, usually master if master exists.
     */
    public Repository getRepositoryByRepositoryAlias(final Space space,
                                                     final String alias,
                                                     final boolean includeDeleted) {

        List<RepositoryInfo> repositories = getAllRepositoryInfo(space);

        return repositories.stream()
                .filter(this.getRepository(alias,
                                           includeDeleted))
                .findAny()
                .map(repo -> repositoryFactory.newRepository(repo))
                .orElse(null);
    }

    private List<RepositoryInfo> getAllRepositoryInfo(Space space) {
        try {
            return this.spaceConfigStorage.get(space.getName())
                    .loadSpaceInfo()
                    .getRepositories();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    private Predicate<RepositoryInfo> getRepository(String alias,
                                                    boolean includeDeleted) {
        return (RepositoryInfo repositoryInfo) -> repositoryInfo.getName().equals(alias) && (!repositoryInfo.isDeleted() || includeDeleted);
    }

    /**
     * @param space Space of the repository.
     * @param root Path to the repository root in any branch.
     * @return Repository root branch is still the default, usually master.
     */
    public Repository getRepositoryByRootPath(final Space space,
                                              final Path root) {

        return this.getAllConfiguredRepositories(space).stream().filter(r -> {
            if (r.getBranches() != null) {
                for (final Branch branch : r.getBranches()) {
                    Path rootPath = Paths.normalizePath(branch.getPath());
                    if (root.equals(rootPath)) {
                        return true;
                    }
                }
                return false;
            } else {
                return false;
            }
        }).findFirst()
                .orElse(null);
    }

    /**
     * @return Does not include system repository.
     */
    public List<Repository> getAllConfiguredRepositories(final Space space) {
        return this.getConfiguredRepositories(space,
                                              repositoryInfo -> !repositoryInfo.isDeleted());
    }

    @Override
    public List<Repository> getAllConfiguredRepositories(Space space,
                                                         boolean includeDeleted) {

        return this.getConfiguredRepositories(space,
                                              r -> includeDeleted || !r.isDeleted());
    }

    @Override
    public List<Repository> getAllDeletedConfiguredRepositories(Space space) {
        return this.getConfiguredRepositories(space,
                                              repositoryInfo -> repositoryInfo.isDeleted());
    }

    private List<Repository> getConfiguredRepositories(Space space,
                                                       Predicate<RepositoryInfo> predicate) {
        List<RepositoryInfo> repositories = getAllRepositoryInfo(space);

        return repositories.stream()
                .filter(predicate)
                .map(repo -> repositoryFactory.newRepository(repo))
                .collect(Collectors.toList());
    }

    public boolean containsAlias(final Space space,
                                 final String alias) {
        List<RepositoryInfo> repositories = this.getAllRepositoryInfo(space);
        return repositories.stream()
                .anyMatch(r -> !r.isDeleted() && r.getName().equals(alias)) &&
                SystemRepository.SYSTEM_REPO.getAlias().equals(alias);
    }
}
