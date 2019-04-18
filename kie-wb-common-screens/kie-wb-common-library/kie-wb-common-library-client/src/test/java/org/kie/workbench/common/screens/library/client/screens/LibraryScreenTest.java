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
package org.kie.workbench.common.screens.library.client.screens;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.guvnor.common.services.project.client.context.WorkspaceProjectContext;
import org.guvnor.common.services.project.client.security.ProjectController;
import org.guvnor.common.services.project.events.NewProjectEvent;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.guvnor.structure.client.security.OrganizationalUnitController;
import org.guvnor.structure.events.AfterEditOrganizationalUnitEvent;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.organizationalunit.OrganizationalUnitService;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.repositories.RepositoryRemovedEvent;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.library.api.LibraryService;
import org.kie.workbench.common.screens.library.client.screens.importrepository.ImportRepositoryPopUpPresenter;
import org.kie.workbench.common.screens.library.client.screens.organizationalunit.contributors.tab.ContributorsListPresenter;
import org.kie.workbench.common.screens.library.client.screens.organizationalunit.contributors.tab.SpaceContributorsListServiceImpl;
import org.kie.workbench.common.screens.library.client.screens.organizationalunit.delete.DeleteOrganizationalUnitPopUpPresenter;
import org.kie.workbench.common.screens.library.client.util.LibraryPermissions;
import org.kie.workbench.common.screens.library.client.util.LibraryPlaces;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mvp.Command;
import org.uberfire.spaces.Space;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class LibraryScreenTest {

    @Mock
    private LibraryScreen.View view;

    @Mock
    private ManagedInstance<DeleteOrganizationalUnitPopUpPresenter> deleteOrganizationalUnitPopUpPresenters;

    @Mock
    private LibraryPermissions libraryPermissions;

    @Mock
    private EmptyLibraryScreen emptyLibraryScreen;

    @Mock
    private PopulatedLibraryScreen populatedLibraryScreen;

    @Mock
    private OrgUnitsMetricsScreen orgUnitsMetricsScreen;

    @Mock
    private OrgUnitsMetricsScreen.View orgUnitsMetricsView;

    @Mock
    private ContributorsListPresenter contributorsListPresenter;

    @Mock
    private LibraryService libraryService;

    @Mock
    private LibraryPlaces libraryPlaces;

    @Mock
    private SpaceContributorsListServiceImpl spaceContributorsListService;

    @Mock
    private DeleteOrganizationalUnitPopUpPresenter deleteOrganizationalUnitPopUpPresenter;

    @Mock
    private ImportRepositoryPopUpPresenter importRepositoryPopUpPresenter;

    @Mock
    private WorkspaceProjectContext projectContext;

    private LibraryScreen libraryScreen;

    @Before
    public void setup() {

        doReturn(deleteOrganizationalUnitPopUpPresenter).when(deleteOrganizationalUnitPopUpPresenters).get();
        doReturn(orgUnitsMetricsView).when(orgUnitsMetricsScreen).getView();

        doReturn(true).when(libraryPermissions).userCanCreateProject(any());
        doReturn(true).when(libraryPermissions).userCanUpdateOrganizationalUnit(any());
        doReturn(true).when(libraryPermissions).userCanDeleteOrganizationalUnit(any());

        doReturn(mock(PopulatedLibraryScreen.View.class)).when(populatedLibraryScreen).getView();
        doReturn(mock(EmptyLibraryScreen.View.class)).when(emptyLibraryScreen).getView();

        final OrganizationalUnit organizationalUnit = mock(OrganizationalUnit.class);
        doReturn(Optional.of(organizationalUnit)).when(projectContext).getActiveOrganizationalUnit();
        when(projectContext.getActiveWorkspaceProject()).thenReturn(Optional.empty());
        when(projectContext.getActiveModule()).thenReturn(Optional.empty());
        when(projectContext.getActiveRepositoryRoot()).thenReturn(Optional.empty());
        when(projectContext.getActivePackage()).thenReturn(Optional.empty());

        doAnswer(invocationOnMock -> {
            ((Command) invocationOnMock.getArguments()[0]).execute();
            return null;
        }).when(libraryPlaces).closeAllPlacesOrNothing(any());

        libraryScreen = spy(new LibraryScreen(view,
                                              deleteOrganizationalUnitPopUpPresenters,
                                              projectContext,
                                              libraryPermissions,
                                              emptyLibraryScreen,
                                              populatedLibraryScreen,
                                              orgUnitsMetricsScreen,
                                              contributorsListPresenter,
                                              new CallerMock<>(libraryService),
                                              libraryPlaces,
                                              spaceContributorsListService));
    }

    @Test
    public void setupTest() {
        final OrganizationalUnit organizationalUnit = mock(OrganizationalUnit.class);
        doReturn("name").when(organizationalUnit).getName();
        doReturn(Optional.of(organizationalUnit)).when(projectContext).getActiveOrganizationalUnit();

        libraryScreen.init();

        verify(view).init(libraryScreen);
        verify(view).setTitle("name");
        verify(contributorsListPresenter).setup(eq(spaceContributorsListService), any());
    }

    @Test
    public void trySamplesWithPermissionTest() {
        libraryScreen.trySamples();

        verify(libraryPlaces).goToTrySamples();
    }

    @Test
    public void trySamplesWithoutPermissionTest() {
        doReturn(false).when(libraryPermissions).userCanCreateProject(any());

        libraryScreen.trySamples();

        verify(libraryPlaces,
               never()).goToTrySamples();
    }

    @Test
    public void importProjectWithPermissionTest() {
        libraryScreen.importProject();

        verify(libraryPlaces).goToImportRepositoryPopUp();
    }

    @Test
    public void importProjectWithoutPermissionTest() {
        doReturn(false).when(libraryPermissions).userCanCreateProject(any());

        libraryScreen.importProject();

        verify(libraryPlaces,
               never()).goToImportRepositoryPopUp();
    }

    @Test
    public void deleteWithPermissionTest() {
        libraryScreen.delete();

        verify(deleteOrganizationalUnitPopUpPresenter).show(any());
    }

    @Test
    public void deleteWithoutPermissionTest() {
        doReturn(false).when(libraryPermissions).userCanDeleteOrganizationalUnit(any());

        libraryScreen.delete();

        verify(deleteOrganizationalUnitPopUpPresenter,
               never()).show(any());
    }

    @Test
    public void showProjectsTest() {
        doReturn(true).when(libraryService).hasProjects(any());
        final HTMLElement populatedLibraryScreenElement = mock(HTMLElement.class);
        when(populatedLibraryScreen.getView().getElement()).thenReturn(populatedLibraryScreenElement);
        doReturn(3).when(populatedLibraryScreen).getProjectsCount();

        libraryScreen.showProjects();

        verify(view).updateContent(populatedLibraryScreenElement);
        verify(view).setProjectsCount(3);
    }

    @Test
    public void onNewProjectShouldCallShowProjects() {

        setupProjectContext("dora");

        doNothing().when(libraryScreen).showProjects();

        libraryScreen.onNewProject(setupNewProjectEvent("dora"));

        verify(libraryScreen).showProjects();
    }

    @Test
    public void onNewProjectShouldNeverCallShowProjectsForDifferentSpaces() {

        setupProjectContext("dora");

        doNothing().when(libraryScreen).showProjects();

        libraryScreen.onNewProject(setupNewProjectEvent("bento"));

        verify(libraryScreen,
               never()).showProjects();
    }

    @Test
    public void onRepositoryRemovedShouldCallShowProjects() {

        setupProjectContext("dora");

        doNothing().when(libraryScreen).showProjects();

        libraryScreen.onRepositoryRemovedEvent(createRepositoryRemovedEvent("dora"));

        verify(libraryScreen).showProjects();
    }

    @Test
    public void onRepositoryRemovedShouldNeverCallShowProjectsForDifferentSpaces() {

        setupProjectContext("dora");

        doNothing().when(libraryScreen).showProjects();

        libraryScreen.onRepositoryRemovedEvent(createRepositoryRemovedEvent("bento"));

        verify(libraryScreen,
               never()).showProjects();
    }

    @Test
    public void showNoProjectsTest() {
        doReturn(false).when(libraryService).hasProjects(any());
        final HTMLElement emptyLibraryScreenElement = mock(HTMLElement.class);
        when(emptyLibraryScreen.getView().getElement()).thenReturn(emptyLibraryScreenElement);

        libraryScreen.showProjects();

        verify(view).updateContent(emptyLibraryScreenElement);
        verify(view).setProjectsCount(0);
    }

    @Test
    public void showMetrics() {
        libraryScreen.showMetrics();
        verify(orgUnitsMetricsScreen).refresh();
        verify(view).updateContent(any());
    }

    private void setupProjectContext(String spaceName) {
        OrganizationalUnit ouMock = mock(OrganizationalUnit.class);
        when(ouMock.getSpace()).thenReturn(new Space(spaceName));
        Optional<OrganizationalUnit> ou = Optional.of(ouMock);

        when(projectContext.getActiveOrganizationalUnit()).thenReturn(ou);
    }

    private NewProjectEvent setupNewProjectEvent(String spaceName) {
        NewProjectEvent newProjectEvent = mock(NewProjectEvent.class);
        WorkspaceProject context = mock(WorkspaceProject.class);
        when(context.getSpace()).thenReturn(new Space(spaceName));

        when(newProjectEvent.getWorkspaceProject()).thenReturn(context);
        return newProjectEvent;
    }

    private RepositoryRemovedEvent createRepositoryRemovedEvent(String spaceName) {
        RepositoryRemovedEvent repositoryRemovedEvent = mock(RepositoryRemovedEvent.class);
        Repository repository = mock(Repository.class);
        when(repository.getSpace()).thenReturn(new Space(spaceName));

        when(repositoryRemovedEvent.getRepository()).thenReturn(repository);
        return repositoryRemovedEvent;
    }
}

