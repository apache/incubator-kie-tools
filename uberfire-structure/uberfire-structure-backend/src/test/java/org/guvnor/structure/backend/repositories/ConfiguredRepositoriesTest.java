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

import org.guvnor.structure.repositories.Branch;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.repositories.impl.git.GitRepository;
import org.guvnor.structure.server.config.ConfigGroup;
import org.guvnor.structure.server.config.ConfigurationService;
import org.guvnor.structure.server.repositories.RepositoryFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.spaces.Space;

import static org.guvnor.structure.server.config.ConfigType.REPOSITORY;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ConfiguredRepositoriesTest {

    public static final Space SPACE1 = new Space("space1");
    public static final String REPO1S1 = "single";
    public static final String REPO2S1 = "multibranch";

    public static final Space SPACE2 = new Space("space2");
    public static final String REPO1S2 = "singles2";
    public static final String REPO2S2 = "multibranchs2";
    public static final String REPO2S3 = "multibranchs3";
    @Mock
    ConfigurationService configurationService;

    @Mock
    RepositoryFactory repositoryFactory;

    private ConfiguredRepositories configuredRepositories;

    @Before
    public void setUp() throws Exception {
        final Map<String, List<ConfigGroup>> repoConfigsBySpace = new HashMap<>();

        final List<ConfigGroup> space1RepoConfigs = new ArrayList<>();
        space1RepoConfigs.add(addRepository(SPACE1,
                                            REPO1S1,
                                            "master"));
        space1RepoConfigs.add(addRepository(SPACE1,
                                            REPO2S1,
                                            "master",
                                            "dev",
                                            "release"));
        repoConfigsBySpace.put(SPACE1.getName(),
                               space1RepoConfigs);

        final List<ConfigGroup> space2RepoConfigs = new ArrayList<>();
        space2RepoConfigs.add(addRepository(SPACE2,
                                            REPO1S2,
                                            "master"));
        space2RepoConfigs.add(addRepository(SPACE2,
                                            REPO2S2,
                                            "master",
                                            "dev",
                                            "release"));
        space2RepoConfigs.add(addRepository(SPACE2,
                                            REPO2S3,
                                            "master",
                                            "dev",
                                            "release"));
        repoConfigsBySpace.put(SPACE2.getName(),
                               space2RepoConfigs);

        when(configurationService.getConfigurationByNamespace(REPOSITORY)).thenReturn(repoConfigsBySpace);

        configuredRepositories = new ConfiguredRepositoriesImpl(configurationService,
                                                                repositoryFactory,
                                                                SystemRepository.SYSTEM_REPO);

        configuredRepositories.reloadRepositories();
    }

    private ConfigGroup addRepository(final Space space,
                                      final String alias,
                                      final String... branches) {
        final ConfigGroup configGroup = new ConfigGroup();
        final GitRepository repository = new GitRepository(alias,
                                                           space);

        final HashMap<String, Branch> branchMap = new HashMap<>();

        for (String branch : branches) {

            Path path = PathFactory.newPath(alias + ".txt",
                                            "default://master@myteam/mortgages/" + alias + ".txt");
            branchMap.put(branch,
                          new Branch(branch,
                                     path));
        }
        repository.setBranches(branchMap);

        when(repositoryFactory.newRepository(configGroup)).thenReturn(repository);

        return configGroup;
    }

    @Test
    public void testLoadRepositories() throws Exception {
        assertEquals(2,
                     configuredRepositories.getAllConfiguredRepositories(SPACE1).size());
        assertEquals(3,
                     configuredRepositories.getAllConfiguredRepositories(SPACE2).size());
    }

    @Test
    public void testLoadSingle() throws Exception {
        final Repository single = configuredRepositories.getRepositoryByRepositoryAlias(SPACE1,
                                                                                        REPO1S1);
        assertEquals(1,
                     single.getBranches().size());
        assertNotNull(single.getBranch("master"));
    }

    @Test
    public void testLoadMultiBranch() throws Exception {
        final Repository single = configuredRepositories.getRepositoryByRepositoryAlias(SPACE1,
                                                                                        REPO2S1);
        assertEquals(3,
                     single.getBranches().size());
        assertNotNull(single.getBranch("master"));
        assertNotNull(single.getBranch("dev"));
        assertNotNull(single.getBranch("release"));
    }

    @Test
    public void testRemoveSingle() throws Exception {
        final Path root = configuredRepositories.getRepositoryByRepositoryAlias(SPACE1,
                                                                                REPO1S1).getDefaultBranch().get().getPath();

        assertNotNull(configuredRepositories.getRepositoryByRootPath(SPACE1,
                                                                     root));

        assertNotNull(configuredRepositories.remove(SPACE1,
                                                    REPO1S1));

        assertFalse(configuredRepositories.containsAlias(SPACE1,
                                                         REPO1S1));

        assertNull(configuredRepositories.getRepositoryByRootPath(SPACE1,
                                                                  root));
    }

    @Test
    public void testRemoveMultiBranch() throws Exception {
        final Branch devBranch = configuredRepositories.getRepositoryByRepositoryAlias(SPACE1,
                                                                                       REPO2S1).getBranch("dev").get();

        assertNotNull(configuredRepositories.getRepositoryByRootPath(SPACE1,
                                                                     devBranch.getPath()));

        assertNotNull(configuredRepositories.remove(SPACE1,
                                                    REPO2S1));

        assertFalse(configuredRepositories.containsAlias(SPACE1,
                                                         REPO2S1));

        assertNull(configuredRepositories.getRepositoryByRootPath(SPACE1,
                                                                  devBranch.getPath()));
    }
}