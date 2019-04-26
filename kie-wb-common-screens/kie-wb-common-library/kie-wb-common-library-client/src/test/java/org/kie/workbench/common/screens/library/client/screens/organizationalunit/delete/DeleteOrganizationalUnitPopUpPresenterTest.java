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

package org.kie.workbench.common.screens.library.client.screens.organizationalunit.delete;

import org.guvnor.structure.client.security.OrganizationalUnitController;
import org.guvnor.structure.events.AfterDeleteOrganizationalUnitEvent;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.organizationalunit.OrganizationalUnitService;
import org.jboss.errai.common.client.api.Caller;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.library.client.util.LibraryPlaces;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.workbench.events.NotificationEvent;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DeleteOrganizationalUnitPopUpPresenterTest {

    @Mock
    private DeleteOrganizationalUnitPopUpPresenter.View view;

    @Mock
    private OrganizationalUnitService organizationalUnitService;
    private Caller<OrganizationalUnitService> organizationalUnitServiceCaller;

    @Mock
    private OrganizationalUnitController organizationalUnitController;

    @Mock
    private EventSourceMock<AfterDeleteOrganizationalUnitEvent> afterDeleteOrganizationalUnitEvent;

    @Mock
    private EventSourceMock<NotificationEvent> notificationEvent;

    @Mock
    private LibraryPlaces libraryPlaces;

    private DeleteOrganizationalUnitPopUpPresenter presenter;

    @Before
    public void setup() {
        doReturn(true).when(organizationalUnitController).canReadOrgUnits();
        doReturn(true).when(organizationalUnitController).canReadOrgUnit(any());
        doReturn(true).when(organizationalUnitController).canUpdateOrgUnit(any());
        doReturn(true).when(organizationalUnitController).canCreateOrgUnits();
        doReturn(true).when(organizationalUnitController).canDeleteOrgUnit(any());

        organizationalUnitServiceCaller = new CallerMock<>(organizationalUnitService);
        doReturn(null).when(organizationalUnitService).getOrganizationalUnit(anyString());

        presenter = spy(new DeleteOrganizationalUnitPopUpPresenter(view,
                                                                   organizationalUnitServiceCaller,
                                                                   organizationalUnitController,
                                                                   afterDeleteOrganizationalUnitEvent,
                                                                   notificationEvent,
                                                                   libraryPlaces));
    }

    @Test
    public void showWithPermissionTest() {
        final OrganizationalUnit organizationalUnit = mock(OrganizationalUnit.class);
        doReturn("ou-name").when(organizationalUnit).getName();

        presenter.show(organizationalUnit);

        verify(view).show(organizationalUnit.getName());
        assertEquals(organizationalUnit,
                     presenter.organizationalUnit);
    }

    @Test
    public void showWithoutPermissionTest() {
        final OrganizationalUnit organizationalUnit = mock(OrganizationalUnit.class);
        doReturn("ou-name").when(organizationalUnit).getName();
        doReturn(false).when(organizationalUnitController).canDeleteOrgUnit(organizationalUnit);

        presenter.show(organizationalUnit);

        verify(view,
               never()).show(organizationalUnit.getName());
        assertNull(presenter.organizationalUnit);
    }

    @Test
    public void deleteWithWrongConfirmedNameTest() {
        presenter.organizationalUnit = mock(OrganizationalUnit.class);
        doReturn("ou-name").when(presenter.organizationalUnit).getName();
        doReturn("other").when(view).getConfirmedName();

        presenter.delete();

        verify(view).getWrongConfirmedNameValidationMessage();
        verify(view).showError(anyString());

        verify(organizationalUnitService,
               never()).removeOrganizationalUnit(anyString());
    }

    @Test
    public void deleteTest() {
        presenter.organizationalUnit = mock(OrganizationalUnit.class);
        doReturn("ou-name").when(presenter.organizationalUnit).getName();
        doReturn("ou-name").when(view).getConfirmedName();

        presenter.delete();

        verify(view).showBusyIndicator(anyString());
        verify(afterDeleteOrganizationalUnitEvent).fire(any());
        verify(view).hideBusyIndicator();
        verify(notificationEvent).fire(any());
        verify(view).hide();
        verify(organizationalUnitService).removeOrganizationalUnit(presenter.organizationalUnit.getName());
    }

    @Test
    public void cancelTest() {
        presenter.cancel();

        verify(view).hide();
    }
}
