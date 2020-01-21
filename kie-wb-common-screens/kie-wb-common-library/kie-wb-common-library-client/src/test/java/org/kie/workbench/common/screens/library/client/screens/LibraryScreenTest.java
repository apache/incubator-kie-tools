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

import java.util.Optional;

import javax.enterprise.event.Event;

import org.guvnor.common.services.project.client.context.WorkspaceProjectContext;
import org.guvnor.common.services.project.client.security.ProjectController;
import org.guvnor.common.services.project.events.NewProjectEvent;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.guvnor.structure.client.security.OrganizationalUnitController;
import org.guvnor.structure.contributors.SpaceContributorsUpdatedEvent;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.repositories.RepositoryRemovedEvent;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.library.api.LibraryService;
import org.kie.workbench.common.screens.library.client.screens.importrepository.ImportRepositoryPopUpPresenter;
import org.kie.workbench.common.screens.library.client.screens.organizationalunit.contributors.tab.ContributorsListPresenter;
import org.kie.workbench.common.screens.library.client.screens.organizationalunit.contributors.tab.SpaceContributorsListServiceImpl;
import org.kie.workbench.common.screens.library.client.screens.organizationalunit.delete.DeleteOrganizationalUnitPopUpPresenter;
import org.kie.workbench.common.screens.library.client.screens.organizationalunit.settings.SettingsScreenPresenter;
import org.kie.workbench.common.screens.library.client.util.LibraryPlaces;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.client.promise.Promises;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mvp.Command;
import org.uberfire.promise.SyncPromises;
import org.uberfire.spaces.Space;
import org.uberfire.workbench.events.NotificationEvent;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class LibraryScreenTest {

    @Mock
    private LibraryScreen.View view;

    @Mock
    private ManagedInstance<DeleteOrganizationalUnitPopUpPresenter> deleteOrganizationalUnitPopUpPresenters;

    @Mock
    private ProjectController projectController;

    @Mock
    private OrganizationalUnitController organizationalUnitController;

    @Mock
    private EmptyLibraryScreen emptyLibraryScreen;

    @Mock
    private PopulatedLibraryScreen populatedLibraryScreen;

    @Mock
    private OrgUnitsMetricsScreen orgUnitsMetricsScreen;

    @Mock
    private OrgUnitsMetricsScreen.View orgUnitsMetricsView;

    @Mock
    private SettingsScreenPresenter settingsScreenPresenter;

    @Mock
    private SettingsScreenPresenter.View settingsScreenView;

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

    @Mock
    private TranslationService translationService;

    @Mock
    private Event<NotificationEvent> notificationEvent;

    private LibraryScreen libraryScreen;

    private Promises promises;

    @Before
    public void setup() {
        promises = new SyncPromises();

        doReturn(deleteOrganizationalUnitPopUpPresenter).when(deleteOrganizationalUnitPopUpPresenters).get();
        doReturn(orgUnitsMetricsView).when(orgUnitsMetricsScreen).getView();
        doReturn(settingsScreenView).when(settingsScreenPresenter).getView();

        doReturn(true).when(projectController).canCreateProjects(any());
        doReturn(true).when(organizationalUnitController).canUpdateOrgUnit(any());
        doReturn(true).when(organizationalUnitController).canDeleteOrgUnit(any());
        doReturn(true).when(organizationalUnitController).canReadOrgUnit(any());

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
                                              projectController,
                                              organizationalUnitController,
                                              emptyLibraryScreen,
                                              populatedLibraryScreen,
                                              orgUnitsMetricsScreen,
                                              settingsScreenPresenter,
                                              contributorsListPresenter,
                                              new CallerMock<>(libraryService),
                                              libraryPlaces,
                                              spaceContributorsListService,
                                              notificationEvent,
                                              translationService,
                                              promises));
    }

    @Test
    public void setupTest() {
        when(view.isProjectsTabActive()).thenReturn(true);
        final OrganizationalUnit organizationalUnit = mock(OrganizationalUnit.class);
        doReturn("name").when(organizationalUnit).getName();
        doReturn(Optional.of(organizationalUnit)).when(projectContext).getActiveOrganizationalUnit();

        libraryScreen.init();

        verify(view).init(libraryScreen);
        verify(view).setTitle("name");
        verify(contributorsListPresenter).setup(eq(spaceContributorsListService), any());

        verify(view).setProjectsCount(0);
        verify(view).isProjectsTabActive();
        verify(view).updateContent(any());
    }

    @Test
    public void trySamplesWithPermissionTest() {
        libraryScreen.trySamples();

        verify(libraryPlaces).goToTrySamples();
    }

    @Test
    public void trySamplesWithoutPermissionTest() {
        doReturn(false).when(projectController).canCreateProjects(any());

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
        doReturn(false).when(projectController).canCreateProjects(any());

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
        doReturn(false).when(organizationalUnitController).canDeleteOrgUnit(any());

        libraryScreen.delete();

        verify(deleteOrganizationalUnitPopUpPresenter,
               never()).show(any());
    }

    @Test
    public void showProjectsTest() {
        doReturn(true).when(view).isProjectsTabActive();
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
        doReturn(true).when(view).isProjectsTabActive();
        doReturn(false).when(libraryService).hasProjects(any());
        final HTMLElement emptyLibraryScreenElement = mock(HTMLElement.class);
        when(emptyLibraryScreen.getView().getElement()).thenReturn(emptyLibraryScreenElement);

        libraryScreen.showProjects();

        verify(view).updateContent(emptyLibraryScreenElement);
        verify(view, times(1)).setProjectsCount(0);
    }

    @Test
    public void showMetrics() {
        doReturn(true).when(view).isMetricsTabActive();
        libraryScreen.showMetrics();
        verify(orgUnitsMetricsScreen).refresh();
        verify(view).updateContent(any());
    }

    @Test
    public void showSettingsTabWhenUserAllowed() {
        libraryScreen.init();

        verify(view).showSettingsTab(true);
    }

    @Test
    public void hideSettingsTabWhenUserNotAllowed() {
        doReturn(false).when(organizationalUnitController).canUpdateOrgUnit(any());

        libraryScreen.init();

        verify(view).showSettingsTab(false);
    }

    @Test
    public void showSettingsTest() {
        doReturn(true).when(view).isSettingsTabActive();
        doReturn(promises.resolve()).when(settingsScreenPresenter).setupUsingCurrentSection();

        libraryScreen.showSettings();

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

    @Test
    public void testOnSpaceContributorsUpdatedSameSpace() {
        String spaceName = "ou1";

        OrganizationalUnit ou1 = mock(OrganizationalUnit.class, Answers.RETURNS_DEEP_STUBS.get());
        when(ou1.getSpace().getName()).thenReturn(spaceName);

        doReturn(Optional.of(ou1)).when(projectContext).getActiveOrganizationalUnit();

        OrganizationalUnit ou2 = mock(OrganizationalUnit.class, Answers.RETURNS_DEEP_STUBS.get());
        when(ou2.getSpace().getName()).thenReturn(spaceName);

        SpaceContributorsUpdatedEvent event = new SpaceContributorsUpdatedEvent(ou2);
        this.libraryScreen.onSpaceContributorsUpdated(event);

        verify(this.libraryScreen, times(1)).showProjects(any());
        verify(this.view, times(1)).setContributorsCount(anyInt());
    }

    @Test
    public void testOnSpaceContributorsUpdatedDifferentSpace() {
        OrganizationalUnit ou1 = mock(OrganizationalUnit.class, Answers.RETURNS_DEEP_STUBS.get());
        when(ou1.getSpace().getName()).thenReturn("ou1");

        doReturn(Optional.of(ou1)).when(projectContext).getActiveOrganizationalUnit();

        OrganizationalUnit ou2 = mock(OrganizationalUnit.class, Answers.RETURNS_DEEP_STUBS.get());
        when(ou2.getSpace().getName()).thenReturn("ou2");

        SpaceContributorsUpdatedEvent event = new SpaceContributorsUpdatedEvent(ou2);
        this.libraryScreen.onSpaceContributorsUpdated(event);

        verify(this.libraryScreen, times(0)).showProjects(any());
        verify(this.view, times(0)).setContributorsCount(anyInt());
    }

    @Test
    public void testRedirectIfNoRightPermissions() {
        doReturn(false).when(organizationalUnitController).canReadOrgUnit(any());
        this.libraryScreen.showProjects();
        verify(this.libraryService, times(0)).hasProjects(any());
        verify(this.libraryPlaces).goToOrganizationalUnits();
    }
}

