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

import org.guvnor.structure.repositories.NewBranchEvent;
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

import static org.guvnor.structure.server.config.ConfigType.REPOSITORY;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ConfiguredRepositoriesTest {

    @Mock
    ConfigurationService configurationService;

    @Mock
    RepositoryFactory repositoryFactory;

    private ConfiguredRepositories configuredRepositories;

    @Before
    public void setUp() throws Exception {

        final ArrayList<ConfigGroup> configGroups = new ArrayList<>();

        configGroups.add(addRepository("single",
                                       "master"));
        configGroups.add(addRepository("multibranch",
                                       "master",
                                       "dev",
                                       "release"));

        when(configurationService.getConfiguration(REPOSITORY)).thenReturn(configGroups);

        configuredRepositories = new ConfiguredRepositories(configurationService,
                                                            repositoryFactory,
                                                            SystemRepository.SYSTEM_REPO);

        configuredRepositories.loadRepositories();
    }

    private ConfigGroup addRepository(final String alias,
                                      final String... branches) {
        final ConfigGroup configGroup = new ConfigGroup();
        final GitRepository repository = new GitRepository(alias);

        final HashMap<String, Path> branchMap = new HashMap<>();
        for (String branch : branches) {
            branchMap.put(branch,
                          mock(Path.class));
        }
        repository.setBranches(branchMap);
        repository.setRoot(branchMap.get("master"));

        when(repositoryFactory.newRepository(configGroup)).thenReturn(repository);

        return configGroup;
    }

    @Test
    public void testLoadRepositories() throws Exception {
        assertEquals(2,
                     configuredRepositories.getAllConfiguredRepositories().size());
    }

    @Test
    public void testLoadSingle() throws Exception {
        final Repository single = configuredRepositories.getRepositoryByRepositoryAlias("single");
        assertEquals(1,
                     single.getBranches().size());
        assertNotNull(single.getBranchRoot("master"));
    }

    @Test
    public void testLoadMultiBranch() throws Exception {
        final Repository single = configuredRepositories.getRepositoryByRepositoryAlias("multibranch");
        assertEquals(3,
                     single.getBranches().size());
        assertNotNull(single.getBranchRoot("master"));
        assertNotNull(single.getBranchRoot("dev"));
        assertNotNull(single.getBranchRoot("release"));
    }

    @Test
    public void testRemoveSingle() throws Exception {
        final Path root = configuredRepositories.getRepositoryByRepositoryAlias("single").getRoot();

        assertNotNull(configuredRepositories.getRepositoryByRootPath(root));

        assertNotNull(configuredRepositories.remove("single"));

        assertFalse(configuredRepositories.containsAlias("single"));

        assertNull(configuredRepositories.getRepositoryByRootPath(root));
    }

    @Test
    public void testRemoveMultiBranch() throws Exception {
        final Path devRoot = configuredRepositories.getRepositoryByRepositoryAlias("multibranch").getBranchRoot("dev");

        assertNotNull(configuredRepositories.getRepositoryByRootPath(devRoot));

        assertNotNull(configuredRepositories.remove("multibranch"));

        assertFalse(configuredRepositories.containsAlias("multibranch"));

        assertNull(configuredRepositories.getRepositoryByRootPath(devRoot));
    }

    @Test
    public void testNewBranch() throws Exception {
        final Path branchPath = mock(Path.class);
        final NewBranchEvent changedEvent = new NewBranchEvent("single",
                                                               "mybranch",
                                                               branchPath,
                                                               System.currentTimeMillis());
        configuredRepositories.onNewBranch(changedEvent);

        // Root for both is the default root
        assertEquals(configuredRepositories.getRepositoryByRepositoryAlias("single").getRoot(),
                     configuredRepositories.getRepositoryByRootPath(branchPath).getRoot());

        final Repository single = configuredRepositories.getRepositoryByRepositoryAlias("single");

        assertEquals(2,
                     single.getBranches().size());

        assertEquals(branchPath,
                     single.getBranchRoot("mybranch"));
    }
}