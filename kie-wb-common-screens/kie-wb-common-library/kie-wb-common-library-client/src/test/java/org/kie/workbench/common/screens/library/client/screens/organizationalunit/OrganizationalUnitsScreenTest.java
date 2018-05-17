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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.enterprise.event.Event;

import org.guvnor.common.services.project.client.context.WorkspaceProjectContext;
import org.guvnor.common.services.project.context.WorkspaceProjectContextChangeEvent;
import org.guvnor.structure.client.security.OrganizationalUnitController;
import org.guvnor.structure.organizationalunit.NewOrganizationalUnitEvent;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.organizationalunit.RemoveOrganizationalUnitEvent;
import org.guvnor.structure.organizationalunit.impl.OrganizationalUnitImpl;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.library.api.LibraryService;
import org.kie.workbench.common.screens.library.api.preferences.LibraryInternalPreferences;
import org.kie.workbench.common.screens.library.api.sync.ClusterLibraryEvent;
import org.kie.workbench.common.screens.library.client.screens.organizationalunit.popup.OrganizationalUnitPopUpPresenter;
import org.kie.workbench.common.screens.library.client.util.LibraryPlaces;
import org.kie.workbench.common.screens.library.client.util.TranslationUtils;
import org.kie.workbench.common.screens.library.client.widgets.common.TileWidget;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.mocks.CallerMock;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class OrganizationalUnitsScreenTest {

    @Mock
    private OrganizationalUnitsScreen.View view;

    @Mock
    private LibraryPlaces libraryPlaces;

    @Mock
    private LibraryService libraryService;
    private Caller<LibraryService> libraryServiceCaller;

    @Mock
    private OrganizationalUnitPopUpPresenter organizationalUnitPopUpPresenter;

    @Mock
    private OrganizationalUnitController organizationalUnitController;

    @Mock
    private ManagedInstance<TileWidget> organizationalUnitTileWidgets;

    @Mock
    private Event<WorkspaceProjectContextChangeEvent> projectContextChangeEvent;

    @Mock
    private LibraryInternalPreferences libraryInternalPreferences;

    @Mock
    private TileWidget tileWidget;

    @Mock
    private TranslationService translationService;

    @Mock
    private TranslationUtils translationUtils;

    @Mock
    private EmptyOrganizationalUnitsScreen emptyOrganizationalUnitsScreen;

    private OrganizationalUnitsScreen presenter;

    private OrganizationalUnit organizationalUnit1;
    private OrganizationalUnit organizationalUnit2;
    private OrganizationalUnit organizationalUnit3;

    @Before
    public void setup() {
        when(translationService.getTranslation(anyString())).thenReturn("");

        libraryServiceCaller = new CallerMock<>(libraryService);

        organizationalUnit1 = new OrganizationalUnitImpl("ou1",
                                                         "owner1",
                                                         "defaultGroupId1");
        organizationalUnit2 = new OrganizationalUnitImpl("ou2",
                                                         "owner2",
                                                         "defaultGroupId2");
        organizationalUnit3 = new OrganizationalUnitImpl("ou3",
                                                         "owner3",
                                                         "defaultGroupId3");

        List<OrganizationalUnit> organizationalUnits = new ArrayList<>();
        organizationalUnits.add(organizationalUnit1);
        organizationalUnits.add(organizationalUnit2);
        organizationalUnits.add(organizationalUnit3);
        doReturn(organizationalUnits).when(libraryService).getOrganizationalUnits();

        presenter = spy(new OrganizationalUnitsScreen(view,
                                                      libraryPlaces,
                                                      libraryServiceCaller,
                                                      organizationalUnitPopUpPresenter,
                                                      organizationalUnitController,
                                                      organizationalUnitTileWidgets,
                                                      projectContextChangeEvent,
                                                      mock(WorkspaceProjectContext.class),
                                                      libraryInternalPreferences,
                                                      emptyOrganizationalUnitsScreen));

        doReturn(true).when(organizationalUnitController).canCreateOrgUnits();
        doReturn(true).when(organizationalUnitController).canReadOrgUnits();
        doReturn(true).when(organizationalUnitController).canReadOrgUnit(any());

        doReturn(tileWidget).when(organizationalUnitTileWidgets).get();

        doReturn(mock(EmptyOrganizationalUnitsScreen.View.class)).when(emptyOrganizationalUnitsScreen).getView();
    }

    @Test
    public void initWithoutReadAllOrgUnitsPermissionTest() {
        doReturn(false).when(organizationalUnitController).canReadOrgUnits();

        presenter.init();

        verify(view).clearOrganizationalUnits();
        verify(view,
               times(3)).addOrganizationalUnit(any());
    }

    @Test
    public void initWithAllPermissionsTest() {
        presenter.init();

        verify(view).clearOrganizationalUnits();
        verify(tileWidget,
               times(3)).init(any(),
                              any(),
                              any(),
                              any(),
                              any());
        verify(tileWidget).init(eq(organizationalUnit1.getName()),
                                any(),
                                any(),
                                any(),
                                any());
        verify(tileWidget).init(eq(organizationalUnit2.getName()),
                                any(),
                                any(),
                                any(),
                                any());
        verify(tileWidget).init(eq(organizationalUnit3.getName()),
                                any(),
                                any(),
                                any(),
                                any());
        verify(view,
               times(3)).addOrganizationalUnit(any());
    }

    @Test
    public void initWithNoOrganizationalUnitsTest() {
        doReturn(Collections.EMPTY_LIST).when(libraryService).getOrganizationalUnits();

        presenter.init();

        verify(view).showNoOrganizationalUnits(any());
    }

    @Test
    public void createOrganizationalUnitTest() {
        presenter.createOrganizationalUnit();

        verify(organizationalUnitPopUpPresenter).show();
    }

    @Test
    public void ouEventsShouldReloadOUs() {

        presenter.onNewOrganizationalUnitEvent(new NewOrganizationalUnitEvent());

        presenter.onClusterLibraryEvent(new ClusterLibraryEvent());

        presenter.onRemoveOrganizationalUnitEvent(new RemoveOrganizationalUnitEvent());

        verify(presenter, times(3)).setupOrganizationalUnits();
        verify(libraryService, times(3)).getOrganizationalUnits();
    }

    @Test
    public void refreshTest() {
        presenter.organizationalUnits = new ArrayList<>();
        presenter.organizationalUnits.add(organizationalUnit1);
        presenter.organizationalUnits.add(organizationalUnit2);
        presenter.organizationalUnits.add(organizationalUnit3);

        presenter.refresh();

        verify(view,
               times(1)).clearOrganizationalUnits();
        verify(tileWidget,
               times(3)).init(any(),
                              any(),
                              any(),
                              any(),
                              any());
        verify(tileWidget).init(eq(organizationalUnit1.getName()),
                                any(),
                                any(),
                                any(),
                                any());
        verify(tileWidget).init(eq(organizationalUnit2.getName()),
                                any(),
                                any(),
                                any(),
                                any());
        verify(tileWidget).init(eq(organizationalUnit3.getName()),
                                any(),
                                any(),
                                any(),
                                any());
        verify(view,
               times(3)).addOrganizationalUnit(any());
    }
}
