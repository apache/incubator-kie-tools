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
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.enterprise.event.Event;

import org.guvnor.structure.backend.config.ConfigurationFactoryImpl;
import org.guvnor.structure.contributors.Contributor;
import org.guvnor.structure.contributors.ContributorType;
import org.guvnor.structure.repositories.Branch;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.repositories.RepositoryUpdatedEvent;
import org.guvnor.structure.repositories.impl.git.GitRepository;
import org.guvnor.structure.server.config.ConfigGroup;
import org.guvnor.structure.server.config.ConfigItem;
import org.guvnor.structure.server.config.ConfigurationService;
import org.guvnor.structure.server.repositories.RepositoryFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.spaces.Space;

import static org.guvnor.structure.server.config.ConfigType.REPOSITORY;
import static org.guvnor.structure.server.config.ConfigType.SPACE;
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
    public static final String REPO3S2 = "multibranchs3";

    @Mock
    ConfigurationService configurationService;

    @Mock
    RepositoryFactory repositoryFactory;

    @Mock
    Event<RepositoryUpdatedEvent> repositoryUpdatedEvent;

    @Spy
    ConfigurationFactoryImpl configurationFactory;

    private ConfiguredRepositories configuredRepositories;

    @Captor
    ArgumentCaptor<ConfigItem<List<Contributor>>> contributorsArgumentCaptor;

    @Before
    public void setUp() throws Exception {
        final Map<String, List<ConfigGroup>> repoConfigsBySpace = new HashMap<>();

        final List<ConfigGroup> space1RepoConfigs = new ArrayList<>();
        space1RepoConfigs.add(addRepository(SPACE1,
                                            REPO1S1,
                                            Collections.singletonList(new Contributor("admin1", ContributorType.OWNER)),
                                            "master"));
        space1RepoConfigs.add(addRepository(SPACE1,
                                            REPO2S1,
                                            Collections.singletonList(new Contributor("admin1", ContributorType.OWNER)),
                                            "master",
                                            "dev",
                                            "release"));
        repoConfigsBySpace.put(SPACE1.getName(),
                               space1RepoConfigs);

        final List<ConfigGroup> space2RepoConfigs = new ArrayList<>();
        space2RepoConfigs.add(addRepository(SPACE2,
                                            REPO1S2,
                                            Collections.emptyList(),
                                            "master"));
        space2RepoConfigs.add(addRepository(SPACE2,
                                            REPO2S2,
                                            Collections.emptyList(),
                                            "master",
                                            "dev",
                                            "release"));
        space2RepoConfigs.add(addRepository(SPACE2,
                                            REPO3S2,
                                            Collections.emptyList(),
                                            "master",
                                            "dev",
                                            "release"));
        repoConfigsBySpace.put(SPACE2.getName(),
                               space2RepoConfigs);

        when(configurationService.getConfigurationByNamespace(REPOSITORY)).thenReturn(repoConfigsBySpace);
        final List<ConfigGroup> spaces = Arrays.asList(createSpaceConfigGroup(SPACE1, Collections.singletonList(new Contributor("admin1", ContributorType.OWNER))),
                                                       createSpaceConfigGroup(SPACE2, Collections.singletonList(new Contributor("admin2", ContributorType.OWNER))));

        when(configurationService.getConfiguration(SPACE)).thenReturn(spaces);

        configuredRepositories = new ConfiguredRepositoriesImpl(configurationService,
                                                                repositoryFactory,
                                                                SystemRepository.SYSTEM_REPO,
                                                                repositoryUpdatedEvent,
                                                                configurationFactory);

        configuredRepositories.reloadRepositories();
    }

    private ConfigGroup createSpaceConfigGroup(final Space space,
                                               final List<Contributor> contributors) {
        final ConfigGroup configGroup = spy(new ConfigGroup());
        configGroup.setName(space.getName());
        configGroup.addConfigItem(configurationFactory.newConfigItem("space-contributors", contributors));

        return configGroup;
    }

    private ConfigGroup addRepository(final Space space,
                                      final String alias,
                                      final List<Contributor> contributors,
                                      final String... branches) {
        final ConfigGroup configGroup = spy(new ConfigGroup());
        if (contributors != null && !contributors.isEmpty()) {
            configGroup.addConfigItem(configurationFactory.newConfigItem("contributors", contributors));
        }

        final GitRepository repository = new GitRepository(alias, space);

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

    @Test
    public void testContributorsWereSetOnReload() {

        final Map<String, List<ConfigGroup>> repoBySpace = configurationService.getConfigurationByNamespace(REPOSITORY);
        repoBySpace.keySet().forEach(space -> {
            repoBySpace.get(space).forEach(repoConfigGroup -> {
                if (space.equals(SPACE1.getName())) {
                    verify(configurationService, never()).updateConfiguration(repoConfigGroup);
                } else {
                    verify(repoConfigGroup).addConfigItem(contributorsArgumentCaptor.capture());
                    verify(configurationService).updateConfiguration(repoConfigGroup);

                    final ConfigItem<List<Contributor>> configItem = contributorsArgumentCaptor.getValue();
                    assertEquals("contributors", configItem.getName());

                    final List<Contributor> contributors = configItem.getValue();
                }
            });
        });
    }
}