/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.guvnor.structure.client.security;

import org.guvnor.structure.contributors.ContributorType;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.jboss.errai.security.shared.api.identity.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.security.authz.AuthorizationManager;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

@RunWith(MockitoJUnitRunner.class)
public class OrganizationalUnitControllerTest {

    @Mock
    private AuthorizationManager authorizationManager;

    @Mock
    private User user;

    private OrganizationalUnitController organizationalUnitController;

    @Before
    public void setup() {
        organizationalUnitController = spy(new OrganizationalUnitController(authorizationManager,
                                                                            user));
    }


    @Test
    public void userCanReadOrganizationalUnitsTest() {
        doReturn(true).when(organizationalUnitController).canReadOrgUnits();
        assertTrue(organizationalUnitController.canReadOrgUnits());
    }

    @Test
    public void userCanNotReadOrganizationalUnitsTest() {
        doReturn(false).when(organizationalUnitController).canReadOrgUnits();
        assertFalse(organizationalUnitController.canReadOrgUnits());
    }

    @Test
    public void userCanReadOrganizationalUnitTest() {
        doReturn(true).when(organizationalUnitController).canReadOrgUnit(any());
        assertTrue(organizationalUnitController.canReadOrgUnit(mock(OrganizationalUnit.class)));
    }

    @Test
    public void userCanNotReadOrganizationalUnitTest() {
        doReturn(false).when(organizationalUnitController).canReadOrgUnit(any());
        assertFalse(organizationalUnitController.canReadOrgUnit(mock(OrganizationalUnit.class)));
    }

    @Test
    public void contributorCanReadOrganizationalUnitTest() {
        doReturn(true).when(organizationalUnitController).userIsAtLeast(eq(ContributorType.CONTRIBUTOR), any());
        assertTrue(organizationalUnitController.canReadOrgUnit(mock(OrganizationalUnit.class)));
    }

    @Test
    public void contributorCanNotReadOrganizationalUnitTest() {
        doReturn(false).when(organizationalUnitController).userIsAtLeast(eq(ContributorType.CONTRIBUTOR), any());
        assertFalse(organizationalUnitController.canReadOrgUnit(mock(OrganizationalUnit.class)));
    }

    @Test
    public void userCanDeleteOrganizationalUnitTest() {
        doReturn(true).when(organizationalUnitController).canDeleteOrgUnit(any());
        assertTrue(organizationalUnitController.canDeleteOrgUnit(mock(OrganizationalUnit.class)));
    }

    @Test
    public void userCanNotDeleteOrganizationalUnitTest() {
        doReturn(false).when(organizationalUnitController).canDeleteOrgUnit(any());
        assertFalse(organizationalUnitController.canDeleteOrgUnit(mock(OrganizationalUnit.class)));
    }

    @Test
    public void contributorCanDeleteOrganizationalUnitTest() {
        doReturn(true).when(organizationalUnitController).userIsAtLeast(eq(ContributorType.OWNER), any());
        assertTrue(organizationalUnitController.canDeleteOrgUnit(mock(OrganizationalUnit.class)));
    }

    @Test
    public void contributorCanNotDeleteOrganizationalUnitTest() {
        doReturn(false).when(organizationalUnitController).userIsAtLeast(eq(ContributorType.OWNER), any());
        assertFalse(organizationalUnitController.canDeleteOrgUnit(mock(OrganizationalUnit.class)));
    }

    @Test
    public void userCanUpdateOrganizationalUnitTest() {
        doReturn(true).when(organizationalUnitController).canUpdateOrgUnit(any());
        assertTrue(organizationalUnitController.canUpdateOrgUnit(mock(OrganizationalUnit.class)));
    }

    @Test
    public void userCanNotUpdateOrganizationalUnitTest() {
        doReturn(false).when(organizationalUnitController).canUpdateOrgUnit(any());
        assertFalse(organizationalUnitController.canUpdateOrgUnit(mock(OrganizationalUnit.class)));
    }

    @Test
    public void contributorCanUpdateOrganizationalUnitTest() {
        doReturn(true).when(organizationalUnitController).userIsAtLeast(eq(ContributorType.ADMIN), any());
        assertTrue(organizationalUnitController.canUpdateOrgUnit(mock(OrganizationalUnit.class)));
    }

    @Test
    public void contributorCanNotUpdateOrganizationalUnitTest() {
        doReturn(false).when(organizationalUnitController).userIsAtLeast(eq(ContributorType.ADMIN), any());
        assertFalse(organizationalUnitController.canUpdateOrgUnit(mock(OrganizationalUnit.class)));
    }

    @Test
    public void userCanCreateOrganizationalUnitTest() {
        doReturn(true).when(organizationalUnitController).canCreateOrgUnits();
        assertTrue(organizationalUnitController.canCreateOrgUnits());
    }

    @Test
    public void userCanNotCreateOrganizationalUnitsTest() {
        doReturn(false).when(organizationalUnitController).canCreateOrgUnits();
        assertFalse(organizationalUnitController.canCreateOrgUnits());
    }

}
