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
import java.util.List;

import org.dashbuilder.displayer.DisplayerSettings;
import org.dashbuilder.displayer.client.Displayer;
import org.dashbuilder.displayer.client.DisplayerLocator;
import org.guvnor.structure.client.security.OrganizationalUnitController;
import org.guvnor.structure.events.AfterCreateOrganizationalUnitEvent;
import org.guvnor.structure.events.AfterDeleteOrganizationalUnitEvent;
import org.guvnor.structure.events.AfterEditOrganizationalUnitEvent;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.organizationalunit.impl.OrganizationalUnitImpl;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.library.api.LibraryService;
import org.kie.workbench.common.screens.library.api.search.FilterUpdateEvent;
import org.kie.workbench.common.screens.library.client.screens.organizationalunit.popup.OrganizationalUnitPopUpPresenter;
import org.kie.workbench.common.screens.library.client.util.LibraryPlaces;
import org.kie.workbench.common.screens.library.client.util.OrgUnitsMetricsFactory;
import org.kie.workbench.common.screens.library.client.util.TranslationUtils;
import org.kie.workbench.common.screens.library.client.widgets.organizationalunit.OrganizationalUnitTileWidget;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.mocks.CallerMock;

import static org.kie.workbench.common.screens.contributors.model.ContributorsDataSetColumns.*;
import static org.junit.Assert.*;
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
    private ManagedInstance<OrganizationalUnitTileWidget> organizationalUnitTileWidgets;

    @Mock
    private OrganizationalUnitTileWidget organizationalUnitTileWidget;

    @Mock
    private TranslationService translationService;

    @Mock
    private TranslationUtils translationUtils;

    @Mock
    private DisplayerLocator displayerLocator;

    @Mock
    private Displayer commmitsDisplayer;

    private OrgUnitsMetricsFactory orgUnitsMetricsFactory;
    private OrganizationalUnitsScreen presenter;

    private OrganizationalUnit organizationalUnit1;
    private OrganizationalUnit organizationalUnit2;
    private OrganizationalUnit organizationalUnit3;

    @Before
    public void setup() {
        when(translationService.getTranslation(anyString())).thenReturn("");

        orgUnitsMetricsFactory = new OrgUnitsMetricsFactory(translationService,
                translationUtils,
                displayerLocator);

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

        when(displayerLocator.lookupDisplayer(any())).thenReturn(commmitsDisplayer);

        presenter = spy(new OrganizationalUnitsScreen(view,
                                                      libraryPlaces,
                                                      libraryServiceCaller,
                                                      organizationalUnitPopUpPresenter,
                                                      organizationalUnitController,
                                                      organizationalUnitTileWidgets,
                                                      orgUnitsMetricsFactory));

        doReturn(true).when(organizationalUnitController).canCreateOrgUnits();
        doReturn(true).when(organizationalUnitController).canReadOrgUnits();
        doReturn(true).when(organizationalUnitController).canReadOrgUnit(any());

        doReturn("").when(view).getFilterName();

        doReturn(organizationalUnitTileWidget).when(organizationalUnitTileWidgets).get();
    }

    @Test
    public void initWithoutReadOrgUnitsPermissionTest() {
        doReturn(false).when(organizationalUnitController).canReadOrgUnits();

        presenter.init();

        verify(view,
               never()).clearOrganizationalUnits();
        verify(view,
               never()).addOrganizationalUnit(any());

        verify(commmitsDisplayer).draw();
        verify(view).updateContributionsMetric(commmitsDisplayer);
    }

    @Test
    public void initWithAllPermissionsTest() {
        List<OrganizationalUnit> organizationalUnits = new ArrayList<>();
        organizationalUnits.add(organizationalUnit1);
        organizationalUnits.add(organizationalUnit2);
        organizationalUnits.add(organizationalUnit3);

        presenter.init();

        verify(view).clearOrganizationalUnits();
        verify(organizationalUnitTileWidget,
               times(3)).init(any());
        verify(organizationalUnitTileWidget).init(organizationalUnit1);
        verify(organizationalUnitTileWidget).init(organizationalUnit2);
        verify(organizationalUnitTileWidget).init(organizationalUnit3);
        verify(view,
               times(3)).addOrganizationalUnit(any());

        verify(commmitsDisplayer).draw();
        verify(view).updateContributionsMetric(commmitsDisplayer);
    }

    @Test
    public void createOrganizationalUnitTest() {
        presenter.createOrganizationalUnit();

        verify(organizationalUnitPopUpPresenter).showAddPopUp();
    }

    @Test
    public void organizationalUnitCreatedTest() {
        final OrganizationalUnit newOrganizationalUnit = new OrganizationalUnitImpl("newOu",
                                                                                    "newOwner",
                                                                                    "newDefaultGroupId");
        List<OrganizationalUnit> organizationalUnits = new ArrayList<>();
        organizationalUnits.add(organizationalUnit1);
        organizationalUnits.add(organizationalUnit2);
        organizationalUnits.add(organizationalUnit3);
        organizationalUnits.add(newOrganizationalUnit);

        presenter.init();
        presenter.organizationalUnitCreated(new AfterCreateOrganizationalUnitEvent(newOrganizationalUnit));

        assertEquals(organizationalUnits,
                     presenter.organizationalUnits);
    }

    @Test
    public void organizationalUnitEditedTest() {
        final OrganizationalUnit editedOrganizationalUnit = new OrganizationalUnitImpl("editedOu",
                                                                                       "editedOwner",
                                                                                       "editedDefaultGroupId");
        List<OrganizationalUnit> organizationalUnits = new ArrayList<>();
        organizationalUnits.add(organizationalUnit1);
        organizationalUnits.add(organizationalUnit2);
        organizationalUnits.add(editedOrganizationalUnit);

        presenter.init();
        presenter.organizationalUnitEdited(new AfterEditOrganizationalUnitEvent(organizationalUnit3,
                                                                                editedOrganizationalUnit));

        assertEquals(organizationalUnits,
                     presenter.organizationalUnits);
    }

    @Test
    public void organizationalUnitDeletedTest() {
        List<OrganizationalUnit> organizationalUnits = new ArrayList<>();
        organizationalUnits.add(organizationalUnit1);
        organizationalUnits.add(organizationalUnit2);

        presenter.init();
        presenter.organizationalUnitDeleted(new AfterDeleteOrganizationalUnitEvent(organizationalUnit3));

        assertEquals(organizationalUnits,
                     presenter.organizationalUnits);
    }

    @Test
    public void refreshTest() {
        doReturn("u3").when(view).getFilterName();
        presenter.organizationalUnits = new ArrayList<>();
        presenter.organizationalUnits.add(organizationalUnit1);
        presenter.organizationalUnits.add(organizationalUnit2);
        presenter.organizationalUnits.add(organizationalUnit3);

        presenter.refresh();

        verify(view,
               times(1)).clearOrganizationalUnits();
        verify(organizationalUnitTileWidget,
               times(1)).init(any());
        verify(organizationalUnitTileWidget).init(organizationalUnit3);
        verify(view,
               times(1)).addOrganizationalUnit(any());
    }

    @Test
    public void filterUpdateTest() {
        presenter.organizationalUnits = new ArrayList<>();

        presenter.filterUpdate(new FilterUpdateEvent("name"));

        verify(view).setFilterName("name");
        verify(presenter).refresh();
    }

    @Test
    public void dateSelectorFormatTest() {
        DisplayerSettings settings = orgUnitsMetricsFactory.buildDateSelectorSettings();
        assertEquals(settings.getColumnSettings(COLUMN_DATE).getValuePattern(), "dd MMM, yyyy HH:mm");
    }

    @Test
    public void displayersListenOthersTest() {
        assertTrue(orgUnitsMetricsFactory.buildAllCommitsSettings().isFilterListeningEnabled());
        assertTrue(orgUnitsMetricsFactory.buildCommitsByDayOfWeekSettings().isFilterListeningEnabled());
        assertTrue(orgUnitsMetricsFactory.buildCommitsByQuarterSettings().isFilterListeningEnabled());
        assertTrue(orgUnitsMetricsFactory.buildCommitsByYearSettings().isFilterListeningEnabled());
        assertTrue(orgUnitsMetricsFactory.buildCommitsOverTimeSettings().isFilterListeningEnabled());
        assertTrue(orgUnitsMetricsFactory.buildDateSelectorSettings().isFilterListeningEnabled());
        assertTrue(orgUnitsMetricsFactory.buildOrgUnitSelectorSettings().isFilterListeningEnabled());
        assertTrue(orgUnitsMetricsFactory.buildProjectSelectorSettings().isFilterListeningEnabled());
        assertTrue(orgUnitsMetricsFactory.buildTopContributorSelectorSettings().isFilterListeningEnabled());
        assertTrue(orgUnitsMetricsFactory.buildCommitsPerProjectSettings().isFilterListeningEnabled());
        assertTrue(orgUnitsMetricsFactory.buildCommitsPerAuthorSettings().isFilterListeningEnabled());
    }
}
