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

import java.util.Collection;
import javax.enterprise.event.Event;

import org.guvnor.structure.backend.backcompat.BackwardCompatibleUtil;
import org.guvnor.structure.organizationalunit.NewOrganizationalUnitEvent;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.organizationalunit.RemoveOrganizationalUnitEvent;
import org.guvnor.structure.organizationalunit.RepoAddedToOrganizationalUnitEvent;
import org.guvnor.structure.organizationalunit.RepoRemovedFromOrganizationalUnitEvent;
import org.guvnor.structure.organizationalunit.UpdatedOrganizationalUnitEvent;
import org.guvnor.structure.security.OrganizationalUnitAction;
import org.guvnor.structure.server.config.ConfigGroup;
import org.guvnor.structure.server.config.ConfigurationFactory;
import org.guvnor.structure.server.config.ConfigurationService;
import org.guvnor.structure.server.organizationalunit.OrganizationalUnitFactory;
import org.jboss.errai.security.shared.api.identity.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.security.Resource;
import org.uberfire.security.authz.AuthorizationManager;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class OrganizationalUnitServiceTest {

    @Mock
    private ConfigurationService configurationService;

    @Mock
    private ConfigurationFactory configurationFactory;

    @Mock
    private OrganizationalUnitFactory organizationalUnitFactory;

    @Mock
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

    @InjectMocks
    private OrganizationalUnitServiceImpl organizationalUnitService;

    @Before
    public void setUp() throws Exception {
        organizationalUnitService.registeredOrganizationalUnits.put("A",
                                                                    mock(OrganizationalUnit.class));
        when(authorizationManager.authorize(any(Resource.class),
                                            any(User.class))).thenReturn(false);
        doReturn(mock(ConfigGroup.class)).when(configurationFactory).newConfigGroup(any(),
                                                                                    any(),
                                                                                    any());
        final OrganizationalUnit organizationalUnit = mock(OrganizationalUnit.class);
        doReturn("name").when(organizationalUnit).getName();
        doReturn(organizationalUnit).when(organizationalUnitFactory).newOrganizationalUnit(any());
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
        setOUCreationPermission(true);

        final OrganizationalUnit ou = organizationalUnitService.createOrganizationalUnit("name",
                                                                                         "owner",
                                                                                         "default.group.id");

        assertNotNull(ou);
        verify(organizationalUnitFactory).newOrganizationalUnit(any());
    }

    private void setOUCreationPermission(final boolean hasPermission) {
        when(authorizationManager.authorize(eq(OrganizationalUnit.RESOURCE_TYPE),
                                            eq(OrganizationalUnitAction.CREATE),
                                            any(User.class))).thenReturn(hasPermission);
    }
}