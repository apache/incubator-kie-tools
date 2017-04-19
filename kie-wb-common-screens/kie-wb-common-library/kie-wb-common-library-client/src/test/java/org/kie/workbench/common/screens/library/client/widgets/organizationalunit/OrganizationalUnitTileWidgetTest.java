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

package org.kie.workbench.common.screens.library.client.widgets.organizationalunit;

import java.util.ArrayList;

import org.guvnor.common.services.project.context.ProjectContextChangeEvent;
import org.guvnor.structure.client.security.OrganizationalUnitController;
import org.guvnor.structure.events.AfterDeleteOrganizationalUnitEvent;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.organizationalunit.OrganizationalUnitService;
import org.guvnor.structure.repositories.Repository;
import org.jboss.errai.common.client.api.Caller;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.library.api.LibraryService;
import org.kie.workbench.common.screens.library.api.OrganizationalUnitRepositoryInfo;
import org.kie.workbench.common.screens.library.api.preferences.LibraryInternalPreferences;
import org.kie.workbench.common.screens.library.client.screens.organizationalunit.popup.OrganizationalUnitPopUpPresenter;
import org.kie.workbench.common.screens.library.client.util.LibraryPlaces;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.workbench.events.NotificationEvent;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class OrganizationalUnitTileWidgetTest {

    @Mock
    private OrganizationalUnitTileWidget.View view;

    @Mock
    private LibraryPlaces libraryPlaces;

    @Mock
    private LibraryService libraryService;
    private Caller<LibraryService> libraryServiceCaller;

    @Mock
    private EventSourceMock<ProjectContextChangeEvent> projectContextChangeEvent;

    @Mock
    private OrganizationalUnitPopUpPresenter organizationalUnitPopUpPresenter;

    @Mock
    private OrganizationalUnitService organizationalUnitService;
    private Caller<OrganizationalUnitService> organizationalUnitServiceCaller;

    @Mock
    private EventSourceMock<AfterDeleteOrganizationalUnitEvent> afterDeleteOrganizationalUnitEvent;

    @Mock
    private EventSourceMock<NotificationEvent> notificationEvent;

    @Mock
    private OrganizationalUnitController organizationalUnitController;

    @Mock
    private LibraryInternalPreferences libraryInternalPreferences;

    private OrganizationalUnitTileWidget presenter;

    @Before
    public void setup() {
        organizationalUnitServiceCaller = new CallerMock<>(organizationalUnitService);
        libraryServiceCaller = new CallerMock<>(libraryService);

        doReturn(new OrganizationalUnitRepositoryInfo(new ArrayList<>(),
                                                      mock(OrganizationalUnit.class),
                                                      new ArrayList<>(),
                                                      mock(Repository.class)))
                .when(libraryService)
                .getOrganizationalUnitRepositoryInfo(any());

        presenter = spy(new OrganizationalUnitTileWidget(view,
                                                         libraryPlaces,
                                                         libraryServiceCaller,
                                                         projectContextChangeEvent,
                                                         organizationalUnitPopUpPresenter,
                                                         organizationalUnitServiceCaller,
                                                         afterDeleteOrganizationalUnitEvent,
                                                         notificationEvent,
                                                         organizationalUnitController,
                                                         libraryInternalPreferences));
    }

    @Test
    public void initTest() {
        final OrganizationalUnit organizationalUnit = mock(OrganizationalUnit.class);

        presenter.init(organizationalUnit);

        verify(view).setup(anyString(),
                           anyString(),
                           eq(organizationalUnit),
                           any(),
                           any(),
                           any());
    }

    @Test
    public void openTest() {
        final OrganizationalUnit organizationalUnit = mock(OrganizationalUnit.class);

        presenter.open(organizationalUnit);

        verify(projectContextChangeEvent).fire(any());
        verify(libraryPlaces).goToLibrary(any());
    }

    @Test
    public void editWithoutPermissionTest() {
        doReturn(false).when(organizationalUnitController).canUpdateOrgUnit(any());

        final OrganizationalUnit organizationalUnit = mock(OrganizationalUnit.class);
        presenter.edit(organizationalUnit);

        verify(organizationalUnitPopUpPresenter,
               never()).showEditPopUp(any());
    }

    @Test
    public void editWithPermissionTest() {
        doReturn(true).when(organizationalUnitController).canUpdateOrgUnit(any());

        final OrganizationalUnit organizationalUnit = mock(OrganizationalUnit.class);
        presenter.edit(organizationalUnit);

        verify(organizationalUnitPopUpPresenter).showEditPopUp(any());
    }

    @Test
    public void removeWithoutPermissionTest() {
        doReturn(false).when(organizationalUnitController).canDeleteOrgUnit(any());

        final OrganizationalUnit organizationalUnit = mock(OrganizationalUnit.class);
        presenter.remove(organizationalUnit);

        verify(organizationalUnitService,
               never()).removeOrganizationalUnit(anyString());
    }

    @Test
    public void removeWithoutConfirmationTest() {
        doReturn(true).when(organizationalUnitController).canDeleteOrgUnit(any());
        doReturn(false).when(presenter).confirmRemove(any());

        final OrganizationalUnit organizationalUnit = mock(OrganizationalUnit.class);
        presenter.remove(organizationalUnit);

        verify(organizationalUnitService,
               never()).removeOrganizationalUnit(anyString());
    }

    @Test
    public void removeTest() {
        doReturn(true).when(organizationalUnitController).canDeleteOrgUnit(any());
        doReturn(true).when(presenter).confirmRemove(any());

        final OrganizationalUnit organizationalUnit = mock(OrganizationalUnit.class);
        presenter.remove(organizationalUnit);

        verify(view).getRemovingBusyIndicatorMessage();
        verify(view).showBusyIndicator(anyString());
        verify(organizationalUnitService).removeOrganizationalUnit(anyString());
    }
}
