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

package org.kie.workbench.common.screens.library.client.screens.organizationalunit.contributors.tab;

import java.util.ArrayList;
import java.util.List;

import org.guvnor.common.services.project.context.ProjectContext;
import org.guvnor.structure.client.security.OrganizationalUnitController;
import org.guvnor.structure.events.AfterEditOrganizationalUnitEvent;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.library.client.screens.organizationalunit.contributors.edit.EditContributorsPopUpPresenter;
import org.kie.workbench.common.screens.library.client.util.LibraryPlaces;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ContributorsListPresenterTest {

    @Mock
    private ContributorsListPresenter.View view;

    @Mock
    private LibraryPlaces libraryPlaces;

    @Mock
    private ManagedInstance<ContributorsListItemPresenter> contributorsListItemPresenters;

    @Mock
    private ManagedInstance<EditContributorsPopUpPresenter> editContributorsPopUpPresenters;

    @Mock
    private ProjectContext projectContext;

    @Mock
    private OrganizationalUnitController organizationalUnitController;

    @Mock
    private ContributorsListItemPresenter contributorsListItemPresenter;

    @Mock
    private EditContributorsPopUpPresenter editContributorsPopUpPresenter;

    private ContributorsListPresenter presenter;

    @Before
    public void setup() {
        doReturn(true).when(organizationalUnitController).canReadOrgUnits();
        doReturn(true).when(organizationalUnitController).canReadOrgUnit(any());
        doReturn(true).when(organizationalUnitController).canUpdateOrgUnit(any());
        doReturn(true).when(organizationalUnitController).canCreateOrgUnits();
        doReturn(true).when(organizationalUnitController).canDeleteOrgUnit(any());

        doReturn(editContributorsPopUpPresenter).when(editContributorsPopUpPresenters).get();
        doReturn(contributorsListItemPresenter).when(contributorsListItemPresenters).get();
        doReturn(mock(ContributorsListItemPresenter.View.class)).when(contributorsListItemPresenter).getView();

        doReturn("Owner").when(view).getOwnerRoleLabel();
        doReturn("Contributor").when(view).getContributorRoleLabel();

        presenter = spy(new ContributorsListPresenter(view,
                                                      libraryPlaces,
                                                      contributorsListItemPresenters,
                                                      editContributorsPopUpPresenters,
                                                      projectContext,
                                                      organizationalUnitController));
    }

    @Test
    public void setupTest() {
        final InOrder order = inOrder(contributorsListItemPresenter);

        final List<String> contributors = new ArrayList<>();
        contributors.add("B");
        contributors.add("c");
        contributors.add("a");

        final OrganizationalUnit organizationalUnit = mock(OrganizationalUnit.class);
        doReturn("B").when(organizationalUnit).getOwner();
        doReturn(contributors).when(organizationalUnit).getContributors();
        doReturn(organizationalUnit).when(libraryPlaces).getSelectedOrganizationalUnit();

        presenter.setup();

        verify(view).init(presenter);
        verify(view).clearContributors();
        order.verify(contributorsListItemPresenter).setup("a",
                                                          "Contributor");
        order.verify(contributorsListItemPresenter).setup("B",
                                                          "Owner");
        order.verify(contributorsListItemPresenter).setup("c",
                                                          "Contributor");
        verify(view,
               times(3)).addContributor(any());
    }

    @Test
    public void filterContributorsTest() {
        final InOrder order = inOrder(contributorsListItemPresenter);

        presenter.contributors = new ArrayList<>();
        presenter.contributors.add("John");
        presenter.contributors.add("Mary");
        presenter.contributors.add("Jonathan");

        final OrganizationalUnit organizationalUnit = mock(OrganizationalUnit.class);
        doReturn("Mary").when(organizationalUnit).getOwner();
        doReturn(presenter.contributors).when(organizationalUnit).getContributors();
        doReturn(organizationalUnit).when(libraryPlaces).getSelectedOrganizationalUnit();

        presenter.filterContributors("h");

        verify(view).clearContributors();
        order.verify(contributorsListItemPresenter).setup("John",
                                                          "Contributor");
        order.verify(contributorsListItemPresenter).setup("Jonathan",
                                                          "Contributor");
        verify(view,
               times(2)).addContributor(any());
    }

    @Test
    public void editWithPermissionTest() {
        final OrganizationalUnit organizationalUnit = mock(OrganizationalUnit.class);
        doReturn(organizationalUnit).when(projectContext).getActiveOrganizationalUnit();

        presenter.edit();

        verify(editContributorsPopUpPresenter).show(organizationalUnit);
    }

    @Test
    public void editWithoutPermissionTest() {
        final OrganizationalUnit organizationalUnit = mock(OrganizationalUnit.class);
        doReturn(organizationalUnit).when(projectContext).getActiveOrganizationalUnit();

        doReturn(false).when(organizationalUnitController).canUpdateOrgUnit(organizationalUnit);

        presenter.edit();

        verify(editContributorsPopUpPresenter,
               never()).show(organizationalUnit);
    }

    @Test
    public void organizationalUnitEditedTest() {
        final OrganizationalUnit organizationalUnit = mock(OrganizationalUnit.class);
        final AfterEditOrganizationalUnitEvent afterEditOrganizationalUnitEvent = mock(AfterEditOrganizationalUnitEvent.class);
        doReturn(organizationalUnit).when(afterEditOrganizationalUnitEvent).getEditedOrganizationalUnit();

        presenter.organizationalUnitEdited(afterEditOrganizationalUnitEvent);

        verify(view).clearFilterText();
        verify(presenter).updateContributors(organizationalUnit);
    }

    @Test
    public void getContributorsCountTest() {
        final List<String> contributors = new ArrayList<>();
        contributors.add("B");
        contributors.add("c");
        contributors.add("a");

        final OrganizationalUnit organizationalUnit = mock(OrganizationalUnit.class);
        doReturn("B").when(organizationalUnit).getOwner();
        doReturn(contributors).when(organizationalUnit).getContributors();
        doReturn(organizationalUnit).when(libraryPlaces).getSelectedOrganizationalUnit();

        assertEquals(3,
                     presenter.getContributorsCount());
    }
}
