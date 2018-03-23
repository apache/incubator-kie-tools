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

package org.guvnor.structure.backend.organizationalunit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import javax.enterprise.event.Event;

import org.guvnor.structure.backend.backcompat.BackwardCompatibleUtil;
import org.guvnor.structure.backend.config.ConfigurationFactoryImpl;
import org.guvnor.structure.organizationalunit.NewOrganizationalUnitEvent;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.organizationalunit.RemoveOrganizationalUnitEvent;
import org.guvnor.structure.organizationalunit.RepoAddedToOrganizationalUnitEvent;
import org.guvnor.structure.organizationalunit.RepoRemovedFromOrganizationalUnitEvent;
import org.guvnor.structure.organizationalunit.UpdatedOrganizationalUnitEvent;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.repositories.RepositoryService;
import org.guvnor.structure.security.OrganizationalUnitAction;
import org.guvnor.structure.server.config.ConfigGroup;
import org.guvnor.structure.server.config.ConfigType;
import org.guvnor.structure.server.config.ConfigurationService;
import org.guvnor.structure.server.organizationalunit.OrganizationalUnitFactory;
import org.jboss.errai.security.shared.api.identity.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.security.Resource;
import org.uberfire.security.authz.AuthorizationManager;
import org.uberfire.spaces.Space;
import org.uberfire.spaces.SpacesAPI;

@RunWith(MockitoJUnitRunner.class)
public class OrganizationalUnitServiceTest {

    @Mock
    private RepositoryService repositoryService;

    @Mock
    private SpacesAPI spacesAPI;

    @Mock
    private ConfigurationService configurationService;

    @Spy
    @InjectMocks
    private ConfigurationFactoryImpl configurationFactory;

    private OrganizationalUnitFactory organizationalUnitFactory;

    private BackwardCompatibleUtil backward;

    @Mock
    private Event<NewOrganizationalUnitEvent> newOrganizationalUnitEvent;

    @Mock
    private Event<RemoveOrganizationalUnitEvent> removeOrganizationalUnitEvent;

    @Mock
    private Event<RepoAddedToOrganizationalUnitEvent> repoAddedToOrgUnitEvent;

    @Mock
    private Event<RepoRemovedFromOrganizationalUnitEvent> repoRemovedFromOrgUnitEvent;

    @Mock
    private Event<UpdatedOrganizationalUnitEvent> updatedOrganizationalUnitEvent;

    @Mock
    private AuthorizationManager authorizationManager;

    @Mock
    private SessionInfo sessionInfo;

    @Mock
    private RepositoryService repoService;

    @Mock
    private OrganizationalUnit orgUnit;

    private OrganizationalUnitServiceImpl organizationalUnitService;

    @Before
    public void setUp() throws Exception {
        backward = new BackwardCompatibleUtil(configurationFactory);
        organizationalUnitFactory = spy(new OrganizationalUnitFactoryImpl(repositoryService,
                                                                          backward,
                                                                          spacesAPI));
        organizationalUnitService = new OrganizationalUnitServiceImpl(configurationService,
                                                                      configurationFactory,
                                                                      organizationalUnitFactory,
                                                                      repoService,
                                                                      backward,
                                                                      newOrganizationalUnitEvent,
                                                                      removeOrganizationalUnitEvent,
                                                                      repoAddedToOrgUnitEvent,
                                                                      repoRemovedFromOrgUnitEvent,
                                                                      updatedOrganizationalUnitEvent,
                                                                      authorizationManager,
                                                                      spacesAPI,
                                                                      sessionInfo);

        organizationalUnitService.registeredOrganizationalUnits.put("A", orgUnit);
        when(authorizationManager.authorize(any(Resource.class),
                                            any(User.class))).thenReturn(false);
    }

    @Test
    public void testAllOrgUnits() throws Exception {
        Collection<OrganizationalUnit> orgUnits = organizationalUnitService.getAllOrganizationalUnits();
        assertEquals(orgUnits.size(),
                     1);
    }

    @Test
    public void testSecuredOrgUnits() throws Exception {
        Collection<OrganizationalUnit> orgUnits = organizationalUnitService.getOrganizationalUnits();
        assertEquals(orgUnits.size(),
                     0);
    }

    @Test
    public void createOrganizationalUnitWithDuplicatedNameTest() {
        setOUCreationPermission(true);

        final OrganizationalUnit ou = organizationalUnitService.createOrganizationalUnit("a",
                                                                                         "owner",
                                                                                         "default.group.id");

        assertNull(ou);

        verify(organizationalUnitFactory,
               never()).newOrganizationalUnit(any());
    }

    @Test
    public void createValidOrganizationalUnitTest() {
        List<String> contributors = new ArrayList<>();
        contributors.add("admin");

        setOUCreationPermission(true);

        final OrganizationalUnit ou = organizationalUnitService.createOrganizationalUnit("name",
                                                                                         "owner",
                                                                                         "default.group.id",
                                                                                         new ArrayList<>(),
                                                                                         contributors);

        assertNotNull(ou);
        verify(organizationalUnitFactory).newOrganizationalUnit(any());
        assertEquals("name",
                     ou.getName());
        assertEquals("owner",
                     ou.getOwner());
        assertEquals("default.group.id",
                     ou.getDefaultGroupId());
        assertEquals(contributors,
                     ou.getContributors());
    }

    @Test
    public void removeOrganizationalUnitRemovesRepositories() throws Exception {
        Repository repoA = mock(Repository.class);
        Repository repoB = mock(Repository.class);
        List<Repository> repos = Arrays.asList(repoA, repoB);
        when(repoA.getAlias()).thenReturn("A");
        when(repoB.getAlias()).thenReturn("B");

        Space space = new Space("A");
        when(orgUnit.getRepositories()).thenReturn(repos);
        when(orgUnit.getSpace()).thenReturn(space);

        ConfigGroup configGroup = new ConfigGroup();
        configGroup.setName("A");
        when(configurationService.getConfiguration(ConfigType.SPACE)).thenReturn(Collections.singletonList(configGroup));

        organizationalUnitService.removeOrganizationalUnit("A");

        verify(repoService).removeRepositories(eq(space), eq(new HashSet<>(Arrays.asList("A", "B"))));
        ArgumentCaptor<RemoveOrganizationalUnitEvent> eventCaptor = ArgumentCaptor.forClass(RemoveOrganizationalUnitEvent.class);
        verify(removeOrganizationalUnitEvent).fire(eventCaptor.capture());
        RemoveOrganizationalUnitEvent event = eventCaptor.getValue();
        assertEquals(repos, event.getOrganizationalUnit().getRepositories());

    }

    private void setOUCreationPermission(final boolean hasPermission) {
        when(authorizationManager.authorize(eq(OrganizationalUnit.RESOURCE_TYPE),
                                            eq(OrganizationalUnitAction.CREATE),
                                            any(User.class))).thenReturn(hasPermission);
    }
}