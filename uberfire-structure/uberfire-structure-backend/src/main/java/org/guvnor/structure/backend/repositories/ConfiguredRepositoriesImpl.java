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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Named;

import org.guvnor.structure.config.SystemRepositoryChangedEvent;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.server.config.ConfigGroup;
import org.guvnor.structure.server.config.ConfigurationService;
import org.guvnor.structure.server.repositories.RepositoryFactory;
import org.uberfire.backend.vfs.Path;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.spaces.Space;

import static org.guvnor.structure.server.config.ConfigType.REPOSITORY;
import static org.uberfire.backend.server.util.Paths.convert;

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

    private ConfigurationService configurationService;
    private RepositoryFactory repositoryFactory;
    private Repository systemRepository;
    private Map<Space, ConfiguredRepositoriesBySpace> repositoriesBySpace = new HashMap<>();

    public ConfiguredRepositoriesImpl() {
    }

    @Inject
    public ConfiguredRepositoriesImpl(final ConfigurationService configurationService,
                                  final RepositoryFactory repositoryFactory,
                                  final @Named("system") Repository systemRepository) {
        this.configurationService = configurationService;
        this.repositoryFactory = repositoryFactory;
        this.systemRepository = systemRepository;
    }

    @SuppressWarnings("unchecked")
    @PostConstruct
    public void reloadRepositories() {
        repositoriesBySpace.values().forEach(r -> r.clear());

        final Map<String, List<ConfigGroup>> repoConfigsBySpace = configurationService.getConfigurationByNamespace(REPOSITORY);

        for (final Map.Entry<String, List<ConfigGroup>> entry : repoConfigsBySpace.entrySet()) {
            final Space space = new Space(entry.getKey());
            final ConfiguredRepositoriesBySpace configuredRepositoriesBySpace = getConfiguredRepositoriesBySpace(space);

            for (ConfigGroup repoConfig : entry.getValue()) {
                final Repository repository = repositoryFactory.newRepository(repoConfig);
                configuredRepositoriesBySpace.add(repository);
            }
        }
    }

    /**
     * @param space Space of the repository.
     * @param alias Name of the repository.
     * @return Repository that has a random branch as a root, usually master if master exists.
     */
    public Repository getRepositoryByRepositoryAlias(final Space space,
                                                     final String alias) {
        ConfiguredRepositoriesBySpace configuredRepositoriesBySpace = getConfiguredRepositoriesBySpace(space);

        return configuredRepositoriesBySpace.get(alias);
    }

    private ConfiguredRepositoriesBySpace getConfiguredRepositoriesBySpace(Space space) {
        repositoriesBySpace.putIfAbsent(space,
                                        new ConfiguredRepositoriesBySpace());
        return repositoriesBySpace.get(space);
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

        if (systemRepository.getDefaultBranch().isPresent()
                && convert(systemRepository.getDefaultBranch().get().getPath()).getFileSystem().equals(fs)) {
            return systemRepository;
        }

        for (ConfiguredRepositoriesBySpace configuredRepositoriesBySpace : repositoriesBySpace.values()) {
            for (final Repository repository : configuredRepositoriesBySpace.getAllConfiguredRepositories()) {
                if (repository.getDefaultBranch().isPresent()
                        && convert(repository.getDefaultBranch().get().getPath()).getFileSystem().equals(fs)) {
                    return repository;
                }
            }
        }
        return null;
    }

    /**
     * @param space Space of the repository.
     * @param root Path to the repository root in any branch.
     * @return Repository root branch is still the default, usually master.
     */
    public Repository getRepositoryByRootPath(final Space space,
                                              final Path root) {
        ConfiguredRepositoriesBySpace configuredRepositoriesBySpace = getConfiguredRepositoriesBySpace(space);
        return configuredRepositoriesBySpace.get(root);
    }

    /**
     * @return Does not include system repository.
     */
   public List<Repository> getAllConfiguredRepositories(final Space space) {
        ConfiguredRepositoriesBySpace configuredRepositoriesBySpace = getConfiguredRepositoriesBySpace(space);
        return new ArrayList<>(configuredRepositoriesBySpace.getAllConfiguredRepositories());
    }

    public boolean containsAlias(final Space space,
                                 final String alias) {
        ConfiguredRepositoriesBySpace configuredRepositoriesBySpace = getConfiguredRepositoriesBySpace(space);
        return configuredRepositoriesBySpace.containsRepository(alias) || SystemRepository.SYSTEM_REPO.getAlias().equals(alias);
    }

    public Repository remove(final Space space,
                             final String alias) {
        ConfiguredRepositoriesBySpace configuredRepositoriesBySpace = getConfiguredRepositoriesBySpace(space);
        return configuredRepositoriesBySpace.remove(alias);
    }

    public void add(final Space space,
                    final Repository alias) {
        ConfiguredRepositoriesBySpace configuredRepositoriesBySpace = getConfiguredRepositoriesBySpace(space);

        configuredRepositoriesBySpace.add(alias);
    }

    public void update(final Space space,
                       final Repository updatedRepo) {
        ConfiguredRepositoriesBySpace configuredRepositoriesBySpace = getConfiguredRepositoriesBySpace(space);
        configuredRepositoriesBySpace.remove(updatedRepo.getAlias());
        configuredRepositoriesBySpace.add(updatedRepo);
    }

    public void flush(final @Observes
                      @org.guvnor.structure.backend.config.Repository
                      SystemRepositoryChangedEvent changedEvent) {
        reloadRepositories();
    }
}
