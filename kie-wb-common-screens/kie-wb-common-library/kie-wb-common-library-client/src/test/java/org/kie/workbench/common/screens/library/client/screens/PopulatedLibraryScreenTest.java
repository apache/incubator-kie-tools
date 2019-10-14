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
package org.kie.workbench.common.screens.library.client.screens;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.enterprise.event.Event;

import org.guvnor.common.services.project.client.context.WorkspaceProjectContext;
import org.guvnor.common.services.project.client.security.ProjectController;
import org.guvnor.common.services.project.events.NewProjectEvent;
import org.guvnor.common.services.project.model.Module;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.repositories.RepositoryRemovedEvent;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.library.api.LibraryInfo;
import org.kie.workbench.common.screens.library.api.LibraryService;
import org.kie.workbench.common.screens.library.api.ProjectAssetListUpdated;
import org.kie.workbench.common.screens.library.api.ProjectCountUpdate;
import org.kie.workbench.common.screens.library.client.util.LibraryPlaces;
import org.kie.workbench.common.screens.library.client.widgets.common.TileWidget;
import org.kie.workbench.common.screens.library.client.widgets.library.AddProjectButtonPresenter;
import org.mockito.Answers;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.uberfire.mocks.CallerMock;
import org.uberfire.spaces.Space;

import static org.junit.Assert.assertEquals;
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
public class PopulatedLibraryScreenTest {

    private static final String SPACE_NAME = "dora";
    private static final String PROJECT_1 = "project1Name";
    private static final String PROJECT_2 = "project2Name";
    private static final String PROJECT_3 = "project3Name";

    @Mock
    private PopulatedLibraryScreen.View view;

    @Mock
    private LibraryPlaces libraryPlaces;

    @Mock
    private LibraryService libraryService;
    private Caller<LibraryService> libraryServiceCaller;

    @Mock
    private ProjectController projectController;

    @Mock
    private ManagedInstance<TileWidget<WorkspaceProject>> tileWidgets;

    @Mock
    private AddProjectButtonPresenter addProjectButtonPresenter;

    @Mock
    private TileWidget tileWidget;

    @Mock
    private OrganizationalUnit organizationalUnit;

    @Mock
    private WorkspaceProjectContext projectContext;

    private PopulatedLibraryScreen libraryScreen;

    @Mock
    private Event<ProjectCountUpdate> projectCountUpdateEvent;

    private WorkspaceProject project1;
    private WorkspaceProject project2;
    private WorkspaceProject project3;

    @Before
    public void setup() {
        libraryServiceCaller = new CallerMock<>(libraryService);

        libraryScreen = spy(new PopulatedLibraryScreen(view,
                                                       libraryPlaces,
                                                       libraryServiceCaller,
                                                       projectController,
                                                       projectContext,
                                                       tileWidgets,
                                                       addProjectButtonPresenter,
                                                       projectCountUpdateEvent));

        doReturn(true).when(projectController).canCreateProjects(any());

        project1 = mockProject(PROJECT_1);
        project2 = mockProject(PROJECT_2);
        project3 = mockProject(PROJECT_3);

        final List<WorkspaceProject> projects = new ArrayList<>();
        projects.add(project1);
        projects.add(project2);
        projects.add(project3);

        when(projectContext.getActiveOrganizationalUnit()).thenReturn(Optional.of(organizationalUnit));
        when(projectContext.getActiveWorkspaceProject()).thenReturn(Optional.empty());
        when(projectContext.getActiveModule()).thenReturn(Optional.empty());
        when(projectContext.getActiveRepositoryRoot()).thenReturn(Optional.empty());
        when(projectContext.getActivePackage()).thenReturn(Optional.empty());

        final LibraryInfo libraryInfo = new LibraryInfo(projects);
        doReturn(libraryInfo).when(libraryService).getLibraryInfo(organizationalUnit);

        doReturn(mock(TileWidget.View.class)).when(tileWidget).getView();

        when(tileWidgets.get()).thenAnswer((Answer<TileWidget<WorkspaceProject>>) invocationOnMock -> {
            TileWidget tile = mock(TileWidget.class);

            doReturn(mock(TileWidget.View.class)).when(tile).getView();

            doAnswer((Answer<Void>) invocationOnMock1 -> {
                when(tile.getContent()).thenReturn(invocationOnMock1.getArgumentAt(0, WorkspaceProject.class));
                return null;
            }).when(tile).setContent(any());

            return tile;
        });
        doReturn(mock(AddProjectButtonPresenter.View.class)).when(addProjectButtonPresenter).getView();

        libraryScreen.setup();
    }

    private WorkspaceProject mockProject(final String projectName) {
        final WorkspaceProject result = mock(WorkspaceProject.class,
                                             Answers.RETURNS_DEEP_STUBS.get());
        doReturn(projectName).when(result).getName();
        doReturn(mock(Module.class)).when(result).getMainModule();
        Repository repo = mock(Repository.class);
        doReturn(projectName).when(repo).getIdentifier();
        doReturn(repo).when(result).getRepository();
        return result;
    }

    @Test
    public void setupTest() {
        verify(addProjectButtonPresenter).getView();
        verify(view).addAction(any());

        verify(view).clearFilterText();
        verify(view).clearProjects();

        verify(tileWidgets, times(3)).get();

        TileWidget<WorkspaceProject> first = libraryScreen.libraryTiles.first();
        verify(first).init(eq(PROJECT_1), any(), any());

        TileWidget<WorkspaceProject> second = libraryScreen.libraryTiles.higher(first);
        verify(second).init(eq(PROJECT_2), any(), any());

        TileWidget<WorkspaceProject> last = libraryScreen.libraryTiles.last();
        verify(last).init(eq(PROJECT_3), any(), any());

        verify(view, times(3)).addProject(any());
    }

    @Test
    public void selectCommandTest() {
        libraryScreen.selectCommand(project1).execute();

        verify(libraryPlaces).goToProject(any(WorkspaceProject.class));
    }

    @Test
    public void filterProjectsTest() {
        assertEquals(3,
                     libraryScreen.libraryTiles.size());
        assertEquals(1,
                     libraryScreen.filterProjects("project1").size());
        assertEquals(1,
                     libraryScreen.filterProjects("roject1").size());
        assertEquals(0,
                     libraryScreen.filterProjects("unexistent").size());
    }

    @Test
    public void onNewProjectMiddleWithAssetCountUpdate() {

        setupProjectContext(SPACE_NAME);

        libraryScreen.onNewProjectEvent(setupNewProjectEvent(SPACE_NAME, "doraProject"));

        Assert.assertEquals(4, libraryScreen.getProjectsCount());

        verify(tileWidgets, times(4)).get();

        verify(view).addProject(any(), any());

        verify(projectCountUpdateEvent, times(2)).fire(any());
    }

    @Test
    public void onNewProjectLastWithAssetCountUpdate() {

        setupProjectContext(SPACE_NAME);

        libraryScreen.onNewProjectEvent(setupNewProjectEvent(SPACE_NAME, "zzzz"));

        Assert.assertEquals(4, libraryScreen.getProjectsCount());

        verify(tileWidgets, times(4)).get();

        verify(view, times(4)).addProject(any());

        verify(projectCountUpdateEvent, times(2)).fire(any());
    }

    @Test
    public void onNewProjectAlreadyPresent() {

        setupProjectContext(SPACE_NAME);

        libraryScreen.onNewProjectEvent(new NewProjectEvent(project1));

        Assert.assertEquals(3, libraryScreen.getProjectsCount());

        verify(tileWidgets, times(3)).get();

        verify(view, never()).addProject(any(), any());

        verify(projectCountUpdateEvent).fire(any());
    }

    @Test
    public void onNewProjectFromDifferentSpace() {

        setupProjectContext(SPACE_NAME);

        doNothing().when(libraryScreen).setup();

        libraryScreen.onNewProjectEvent(setupNewProjectEvent("bento", "bento"));

        // Checking if the view has been rendered only one time (before the new project event)
        Assert.assertEquals(3, libraryScreen.getProjectsCount());

        verify(view, times(1)).clearProjects();
        verify(projectCountUpdateEvent, times(1)).fire(any());
        verify(view, times(3)).addProject(any());
    }

    @Test
    public void onRepositoryRemovedShouldCallShowProjects() {
        setupProjectContext(SPACE_NAME);

        libraryScreen.onRepositoryRemovedEvent(createRepositoryRemovedEvent(SPACE_NAME, PROJECT_1));

        Assert.assertEquals(2, libraryScreen.getProjectsCount());

        verify(tileWidgets, times(3)).get();

        verify(view).removeProject(any());
        verify(projectCountUpdateEvent, times(2)).fire(any());
    }


    @Test
    public void onRepositoryRemovedShouldNeverCallShowProjectsForDifferentSpaces() {

        setupProjectContext(SPACE_NAME);

        libraryScreen.onRepositoryRemovedEvent(createRepositoryRemovedEvent("bento", "bento"));

        // Checking if the view has been rendered only one time (before the project removed event)
        Assert.assertEquals(3, libraryScreen.getProjectsCount());

        verify(view, times(1)).clearProjects();
        verify(projectCountUpdateEvent, times(1)).fire(any());
        verify(view, times(3)).addProject(any());
    }

    @Test
    public void testOnAssetListUpdated() {

        setupProjectContext(SPACE_NAME);

        libraryScreen.onAssetListUpdated(new ProjectAssetListUpdated(project1));

        ArgumentCaptor<WorkspaceProject> projectCaptor = ArgumentCaptor.forClass(WorkspaceProject.class);

        verify(libraryService, times(4)).getNumberOfAssets(projectCaptor.capture());

        Assert.assertSame(project1, projectCaptor.getValue());

        verify(libraryScreen.libraryTiles.first(), times(2)).setNumberOfAssets(anyInt());
    }

    @Test
    public void testOnAssetListUpdatedDifferentSpace() {

        libraryScreen.onAssetListUpdated(createAssetListUpdatedEvent(SPACE_NAME, "project4Name"));

        verify(libraryService, times(3)).getNumberOfAssets(any(WorkspaceProject.class));
    }

    private ProjectAssetListUpdated createAssetListUpdatedEvent(String spaceName, String projectName) {
        ProjectAssetListUpdated projectAssetListUpdated = mock(ProjectAssetListUpdated.class);
        WorkspaceProject project = this.mockProject(projectName);
        when(project.getSpace()).thenReturn(new Space(spaceName));
        when(projectAssetListUpdated.getProject()).thenReturn(project);
        return projectAssetListUpdated;
    }

    private void setupProjectContext(String spaceName) {
        OrganizationalUnit ouMock = mock(OrganizationalUnit.class);
        final Space space = new Space(spaceName);
        when(ouMock.getSpace()).thenReturn(space);
        Optional<OrganizationalUnit> ou = Optional.of(ouMock);

        when(project1.getSpace()).thenReturn(space);
        when(project2.getSpace()).thenReturn(space);
        when(project3.getSpace()).thenReturn(space);

        when(projectContext.getActiveOrganizationalUnit()).thenReturn(ou);
    }

    private NewProjectEvent setupNewProjectEvent(String spaceName, String projectName) {
        NewProjectEvent newProjectEvent = mock(NewProjectEvent.class);
        WorkspaceProject workspaceProject = mock(WorkspaceProject.class);

        when(workspaceProject.getSpace()).thenReturn(new Space(spaceName));
        when(workspaceProject.getName()).thenReturn(projectName);

        when(newProjectEvent.getWorkspaceProject()).thenReturn(workspaceProject);
        return newProjectEvent;
    }

    private RepositoryRemovedEvent createRepositoryRemovedEvent(String spaceName, String repositoryIdentifier) {

        RepositoryRemovedEvent repositoryRemovedEvent = mock(RepositoryRemovedEvent.class);
        Repository repository = mock(Repository.class);
        when(repository.getSpace()).thenReturn(new Space(spaceName));
        when(repository.getIdentifier()).thenReturn(repositoryIdentifier);

        when(repositoryRemovedEvent.getRepository()).thenReturn(repository);
        return repositoryRemovedEvent;
    }
}
