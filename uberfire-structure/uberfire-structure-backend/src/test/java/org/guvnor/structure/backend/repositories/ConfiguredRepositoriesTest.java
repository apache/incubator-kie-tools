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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.guvnor.structure.contributors.Contributor;
import org.guvnor.structure.contributors.ContributorType;
import org.guvnor.structure.organizationalunit.config.RepositoryConfiguration;
import org.guvnor.structure.organizationalunit.config.RepositoryInfo;
import org.guvnor.structure.organizationalunit.config.SpaceConfigStorage;
import org.guvnor.structure.organizationalunit.config.SpaceConfigStorageRegistry;
import org.guvnor.structure.organizationalunit.config.SpaceInfo;
import org.guvnor.structure.repositories.Branch;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.repositories.impl.git.GitRepository;
import org.guvnor.structure.server.repositories.RepositoryFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.spaces.Space;

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
    private RepositoryFactory repositoryFactory;

    private ConfiguredRepositories configuredRepositories;

    @Mock
    private SpaceConfigStorageRegistry spaceConfigStorageRegistry;

    @Before
    public void setUp() throws Exception {

        ArrayList<RepositoryInfo> repositoriesSpace1 = new ArrayList<>();
        repositoriesSpace1.add(addRepository(SPACE1,
                                             REPO1S1,
                                             createAdminContributors("admin1"),
                                             "master"));
        repositoriesSpace1.add(addRepository(SPACE1,
                                             REPO2S1,
                                             createAdminContributors("admin1"),
                                             "master",
                                             "dev",
                                             "release"));

        SpaceInfo space1 = new SpaceInfo(SPACE1.getName(),
                                         "Test Space",
                                         "com.space1",
                                         createAdminContributors("admin1"),
                                         repositoriesSpace1,
                                         Collections.emptyList());

        ArrayList<RepositoryInfo> repositoriesSpace2 = new ArrayList<>();
        repositoriesSpace2.add(addRepository(SPACE2,
                                             REPO1S2,
                                             Collections.emptyList(),
                                             "master"));
        repositoriesSpace2.add(addRepository(SPACE2,
                                             REPO2S2,
                                             Collections.emptyList(),
                                             "master",
                                             "dev",
                                             "release"));
        repositoriesSpace2.add(addRepository(SPACE2,
                                             REPO3S2,
                                             Collections.emptyList(),
                                             "master",
                                             "dev",
                                             "release"));

        SpaceInfo space2 = new SpaceInfo(SPACE2.getName(),
                                         "Test space",
                                         "com.space2",
                                         createAdminContributors("admin2"),
                                         repositoriesSpace2,
                                         Collections.emptyList());

        Map<String, SpaceInfo> spaces = new HashMap<>();
        spaces.put(SPACE1.getName(),
                   space1);
        spaces.put(SPACE2.getName(),
                   space2);

        doAnswer(invocationOnMock -> {
            final SpaceConfigStorage spaceConfigStorage = mock(SpaceConfigStorage.class);
            String spaceName = (String) invocationOnMock.getArguments()[0];
            doReturn(spaces.get(spaceName)).when(spaceConfigStorage).loadSpaceInfo();
            doReturn(true)
                    .when(spaceConfigStorage).isInitialized();
            return spaceConfigStorage;
        }).when(spaceConfigStorageRegistry).get(any());

        configuredRepositories = new ConfiguredRepositoriesImpl(repositoryFactory,
                                                                spaceConfigStorageRegistry);
    }

    private List<Contributor> createAdminContributors(String... username) {
        List<Contributor> contributors = new ArrayList<>();
        for (int i = 0; i < username.length; i++) {
            contributors.add(new Contributor(username[i],
                                             ContributorType.OWNER));
        }
        return contributors;
    }

    private RepositoryInfo addRepository(final Space space,
                                         final String alias,
                                         final List<Contributor> contributors,
                                         final String... branches) {

        RepositoryConfiguration config = new RepositoryConfiguration();
        RepositoryInfo repositoryInfo = new RepositoryInfo(alias,
                                                           false,
                                                           config);
        config.add("contributors",
                   contributors);
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
        when(repositoryFactory.newRepository(repositoryInfo)).thenReturn(repository);
        return repositoryInfo;
    }

    @Test
    public void testLoadRepositories() {
        assertEquals(2,
                     configuredRepositories.getAllConfiguredRepositories(SPACE1).size());
        assertEquals(3,
                     configuredRepositories.getAllConfiguredRepositories(SPACE2).size());
    }

    @Test
    public void testLoadSingle() {
        final Repository single = configuredRepositories.getRepositoryByRepositoryAlias(SPACE1,
                                                                                        REPO1S1);
        assertEquals(1,
                     single.getBranches().size());
        assertNotNull(single.getBranch("master"));
    }

    @Test
    public void testLoadMultiBranch() {
        final Repository single = configuredRepositories.getRepositoryByRepositoryAlias(SPACE1,
                                                                                        REPO2S1);
        assertEquals(3,
                     single.getBranches().size());
        assertNotNull(single.getBranch("master"));
        assertNotNull(single.getBranch("dev"));
        assertNotNull(single.getBranch("release"));
    }
}