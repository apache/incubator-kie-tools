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

package org.guvnor.structure.backend.organizationalunit;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.guvnor.structure.backend.backcompat.BackwardCompatibleUtil;
import org.guvnor.structure.backend.config.ConfigurationFactoryImpl;
import org.guvnor.structure.contributors.Contributor;
import org.guvnor.structure.contributors.ContributorType;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.repositories.RepositoryService;
import org.guvnor.structure.server.config.ConfigGroup;
import org.guvnor.structure.server.config.ConfigType;
import org.guvnor.structure.server.config.ConfigurationFactory;
import org.guvnor.structure.server.config.ConfigurationService;
import org.guvnor.structure.server.config.PasswordService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.spaces.Space;
import org.uberfire.spaces.SpacesAPI;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class OrganizationalUnitFactoryImplTest {

    @Mock
    private RepositoryService repositoryService;

    @Mock
    private BackwardCompatibleUtil backward;

    @Mock
    private SpacesAPI spacesAPI;

    @Mock
    private ConfigurationService configurationService;

    @Mock
    private PasswordService secureService;

    private ConfigurationFactory configurationFactory;

    private OrganizationalUnitFactoryImpl factory;

    @Before
    public void setup() {
        doAnswer(invocationOnMock -> invocationOnMock.getArgumentAt(0, ConfigGroup.class)).when(backward).compat(any());
        doAnswer(invocationOnMock -> {
            final Repository repository = mock(Repository.class);
            doReturn(invocationOnMock.getArgumentAt(0, Space.class)).when(repository).getSpace();
            doReturn(invocationOnMock.getArgumentAt(1, String.class)).when(repository).getAlias();
            return repository;
        }).when(repositoryService).getRepositoryFromSpace(any(Space.class), anyString());


        configurationFactory = new ConfigurationFactoryImpl(secureService);
        factory = spy(new OrganizationalUnitFactoryImpl(repositoryService,
                                                        backward,
                                                        spacesAPI,
                                                        configurationService,
                                                        configurationFactory));
    }

    @Test
    public void newOrganizationalUnitTest() {
        final ConfigGroup configGroup = generateOUConfigGroup("ou",
                                                              "groupId",
                                                              Collections.singletonList("repo1"),
                                                              Collections.singletonList("group1"));
        final List<Contributor> contributors = Arrays.asList(new Contributor("c1", ContributorType.OWNER),
                                                             new Contributor("c2", ContributorType.ADMIN));
        configGroup.addConfigItem(configurationFactory.newConfigItem("space-contributors",
                                                                     contributors));

        final OrganizationalUnit organizationalUnit = factory.newOrganizationalUnit(configGroup);

        verify(configurationService, never()).updateConfiguration(any());

        assertEquals("ou", organizationalUnit.getName());
        assertEquals("groupId", organizationalUnit.getDefaultGroupId());

        assertEquals(1, organizationalUnit.getRepositories().size());
        assertEquals("repo1", ((List<Repository>) organizationalUnit.getRepositories()).get(0).getAlias());

        assertEquals(1, organizationalUnit.getGroups().size());
        assertEquals("group1", ((List<String>) organizationalUnit.getGroups()).get(0));

        assertEquals(2, organizationalUnit.getContributors().size());
        assertEquals("c1", ((List<Contributor>) organizationalUnit.getContributors()).get(0).getUsername());
        assertEquals(ContributorType.OWNER, ((List<Contributor>) organizationalUnit.getContributors()).get(0).getType());
        assertEquals("c2", ((List<Contributor>) organizationalUnit.getContributors()).get(1).getUsername());
        assertEquals(ContributorType.ADMIN, ((List<Contributor>) organizationalUnit.getContributors()).get(1).getType());
    }

    @Test
    public void newOrganizationalUnitWithOldContributorsTest() {
        final ConfigGroup configGroup = generateOUConfigGroup("ou",
                                                              "groupId",
                                                              Collections.singletonList("repo1"),
                                                              Collections.singletonList("group1"));
        configGroup.addConfigItem(configurationFactory.newConfigItem("owner", "owner"));
        final List<String> contributors = Arrays.asList("c1", "c2");
        configGroup.addConfigItem(configurationFactory.newConfigItem("contributors", contributors));

        final OrganizationalUnit organizationalUnit = factory.newOrganizationalUnit(configGroup);

        verify(configurationService).updateConfiguration(any());

        assertEquals("ou", organizationalUnit.getName());
        assertEquals("groupId", organizationalUnit.getDefaultGroupId());

        assertEquals(1, organizationalUnit.getRepositories().size());
        assertEquals("repo1", ((List<Repository>) organizationalUnit.getRepositories()).get(0).getAlias());

        assertEquals(1, organizationalUnit.getGroups().size());
        assertEquals("group1", ((List<String>) organizationalUnit.getGroups()).get(0));

        assertEquals(3, organizationalUnit.getContributors().size());
        assertEquals("owner", ((List<Contributor>) organizationalUnit.getContributors()).get(0).getUsername());
        assertEquals(ContributorType.OWNER, ((List<Contributor>) organizationalUnit.getContributors()).get(0).getType());
        assertEquals("c1", ((List<Contributor>) organizationalUnit.getContributors()).get(1).getUsername());
        assertEquals(ContributorType.CONTRIBUTOR, ((List<Contributor>) organizationalUnit.getContributors()).get(1).getType());
        assertEquals("c2", ((List<Contributor>) organizationalUnit.getContributors()).get(2).getUsername());
        assertEquals(ContributorType.CONTRIBUTOR, ((List<Contributor>) organizationalUnit.getContributors()).get(2).getType());
    }

    private ConfigGroup generateOUConfigGroup(final String name,
                                              final String defaultGroupId,
                                              final List<String> repositories,
                                              final List<String> groups) {
        final ConfigGroup configGroup = configurationFactory.newConfigGroup(ConfigType.SPACE, name, "");
        configGroup.addConfigItem(configurationFactory.newConfigItem("defaultGroupId", defaultGroupId));
        configGroup.addConfigItem(configurationFactory.newConfigItem("repositories", repositories));
        configGroup.addConfigItem(configurationFactory.newConfigItem("security:groups", groups));

        return configGroup;
    }
}
