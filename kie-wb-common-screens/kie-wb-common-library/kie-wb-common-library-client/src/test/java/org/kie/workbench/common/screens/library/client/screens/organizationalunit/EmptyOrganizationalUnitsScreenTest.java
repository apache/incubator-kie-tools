/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.screens.library.client.screens.organizationalunit;

import org.guvnor.structure.client.security.OrganizationalUnitController;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.library.client.screens.organizationalunit.popup.OrganizationalUnitPopUpPresenter;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class EmptyOrganizationalUnitsScreenTest {

    @Mock
    private EmptyOrganizationalUnitsScreen.View view;

    @Mock
    private ManagedInstance<OrganizationalUnitPopUpPresenter> organizationalUnitPopUpPresenters;

    @Mock
    private OrganizationalUnitController organizationalUnitController;

    @Mock
    private OrganizationalUnitPopUpPresenter organizationalUnitPopUpPresenter;

    private EmptyOrganizationalUnitsScreen emptyOrganizationalUnitsScreen;

    @Before
    public void setup() {
        doReturn(organizationalUnitPopUpPresenter).when(organizationalUnitPopUpPresenters).get();

        doReturn(true).when(organizationalUnitController).canCreateOrgUnits();

        emptyOrganizationalUnitsScreen = new EmptyOrganizationalUnitsScreen(view,
                                                                            organizationalUnitPopUpPresenters,
                                                                            organizationalUnitController);
    }

    @Test
    public void setupTest() {
        emptyOrganizationalUnitsScreen.setup();

        verify(view).init(emptyOrganizationalUnitsScreen);
    }

    @Test
    public void addProjectWithPermissionTest() {
        emptyOrganizationalUnitsScreen.createOrganizationalUnit();

        verify(organizationalUnitPopUpPresenter).show();
    }

    @Test
    public void addProjectWithoutPermissionTest() {
        doReturn(false).when(organizationalUnitController).canCreateOrgUnits();

        emptyOrganizationalUnitsScreen.createOrganizationalUnit();

        verify(organizationalUnitPopUpPresenter,
               never()).show();
    }
}