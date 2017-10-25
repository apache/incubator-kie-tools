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

package org.guvnor.structure.backend.repositories;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Named;

import org.guvnor.structure.config.SystemRepositoryChangedEvent;
import org.guvnor.structure.repositories.NewBranchEvent;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.repositories.impl.git.GitRepository;
import org.guvnor.structure.server.config.ConfigGroup;
import org.guvnor.structure.server.config.ConfigurationService;
import org.guvnor.structure.server.repositories.RepositoryFactory;
import org.uberfire.backend.vfs.Path;
import org.uberfire.java.nio.file.FileSystem;

import static org.guvnor.structure.server.config.ConfigType.REPOSITORY;
import static org.uberfire.backend.server.util.Paths.convert;

/**
 * Cache for configured repositories.
 */
@ApplicationScoped
public class ConfiguredRepositories {

    private ConfigurationService configurationService;
    private RepositoryFactory repositoryFactory;
    private Repository systemRepository;

    private Map<String, Repository> repositoriesByAlias = new HashMap<>();
    private Map<Path, Repository> repositoriesByBranchRoot = new HashMap<>();

    public ConfiguredRepositories() {
    }

    @Inject
    public ConfiguredRepositories(final ConfigurationService configurationService,
                                  final RepositoryFactory repositoryFactory,
                                  final @Named("system") Repository systemRepository) {
        this.configurationService = configurationService;
        this.repositoryFactory = repositoryFactory;
        this.systemRepository = systemRepository;
    }

    @SuppressWarnings("unchecked")
    @PostConstruct
    public void loadRepositories() {
        repositoriesByAlias.clear();
        repositoriesByBranchRoot.clear();

        final List<ConfigGroup> repoConfigs = configurationService.getConfiguration(REPOSITORY);
        if (!(repoConfigs == null || repoConfigs.isEmpty())) {
            for (final ConfigGroup configGroup : repoConfigs) {
                final Repository repository = repositoryFactory.newRepository(configGroup);

                add(repository);
            }
        }
    }

    /**
     * @param alias Name of the repository.
     * @return Repository that has a random branch as a root, usually master if master exists.
     */
    public Repository getRepositoryByRepositoryAlias(final String alias) {
        return repositoriesByAlias.get(alias);
    }

    /**
     * This can also return System Repository.
     * @param fs
     * @return
     */
    public Repository getRepositoryByRepositoryFileSystem(final FileSystem fs) {
        if (fs == null) {
            return null;
        }

        if (convert(systemRepository.getRoot()).getFileSystem().equals(fs)) {
            return systemRepository;
        }

        for (final Repository repository : repositoriesByAlias.values()) {
            if (convert(repository.getRoot()).getFileSystem().equals(fs)) {
                return repository;
            }
        }

        return null;
    }

    /**
     * @param root Path to the repository root in any branch.
     * @return Repository root branch is still the default, usually master.
     */
    public Repository getRepositoryByRootPath(final Path root) {
        return repositoriesByBranchRoot.get(root);
    }

    /**
     * @return Does not include system repository.
     */
    public List<Repository> getAllConfiguredRepositories() {
        return new ArrayList<>(repositoriesByAlias.values());
    }

    public boolean containsAlias(final String alias) {
        return repositoriesByAlias.containsKey(alias) || SystemRepository.SYSTEM_REPO.getAlias().equals(alias);
    }

    public void add(final Repository repository) {
        repositoriesByAlias.put(repository.getAlias(),
                                repository);

        if (repository instanceof GitRepository &&
                repository.getBranches() != null) {
            for (String branch : repository.getBranches()) {
                repositoriesByBranchRoot.put(repository.getBranchRoot(branch),
                                             repository);
            }
        } else {
            repositoriesByBranchRoot.put(repository.getRoot(),
                                         repository);
        }
    }

    public void update(final Repository updatedRepo) {
        add(updatedRepo);
    }

    public Repository remove(final String alias) {

        final Repository removed = repositoriesByAlias.remove(alias);

        removeFromRootByAlias(alias);

        return removed;
    }

    private void removeFromRootByAlias(final String alias) {
        for (Path path : findFromRootMapByAlias(alias)) {
            repositoriesByBranchRoot.remove(path);
        }
    }

    private List<Path> findFromRootMapByAlias(final String alias) {
        List<Path> result = new ArrayList<>();
        for (Path path : repositoriesByBranchRoot.keySet()) {
            if (repositoriesByBranchRoot.get(path).getAlias().equals(alias)) {
                result.add(path);
            }
        }
        return result;
    }

    public void onNewBranch(final @Observes NewBranchEvent changedEvent) {

        if (repositoriesByAlias.containsKey(changedEvent.getRepositoryAlias())) {

            final Repository repository = getRepositoryByRepositoryAlias(changedEvent.getRepositoryAlias());
            if (repository instanceof GitRepository) {
                ((GitRepository) repository).addBranch(changedEvent.getBranchName(),
                                                       changedEvent.getBranchPath());
                repositoriesByBranchRoot.put(changedEvent.getBranchPath(),
                                             repository);
            }
        }
    }

    public void flush(final @Observes @org.guvnor.structure.backend.config.Repository SystemRepositoryChangedEvent changedEvent) {
        loadRepositories();
    }
}
