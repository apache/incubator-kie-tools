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
import javax.enterprise.event.Event;

import org.guvnor.common.services.project.client.security.ProjectController;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.structure.repositories.Repository;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.library.api.LibraryInfo;
import org.kie.workbench.common.screens.library.api.LibraryService;
import org.kie.workbench.common.screens.library.api.ProjectInfo;
import org.kie.workbench.common.screens.library.client.events.ProjectDetailEvent;
import org.kie.workbench.common.screens.library.client.screens.project.AddProjectPopUpPresenter;
import org.kie.workbench.common.screens.library.client.util.LibraryPlaces;
import org.kie.workbench.common.screens.library.client.widgets.common.TileWidget;
import org.kie.workbench.common.screens.library.client.widgets.library.AddProjectButtonPresenter;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.mocks.CallerMock;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PopulatedLibraryScreenTest {

    @Mock
    private PopulatedLibraryScreen.View view;

    @Mock
    private LibraryPlaces libraryPlaces;

    @Mock
    private Event<ProjectDetailEvent> projectDetailEvent;

    @Mock
    private LibraryService libraryService;
    private Caller<LibraryService> libraryServiceCaller;

    @Mock
    private ProjectController projectController;

    @Mock
    private ManagedInstance<TileWidget> tileWidgets;

    @Mock
    private AddProjectButtonPresenter addProjectButtonPresenter;

    @Mock
    private TileWidget tileWidget;

    @Mock
    private AddProjectPopUpPresenter addProjectPopUpPresenter;

    private PopulatedLibraryScreen libraryScreen;

    private Project project1;
    private Project project2;
    private Project project3;

    @Before
    public void setup() {
        libraryServiceCaller = new CallerMock<>(libraryService);

        libraryScreen = spy(new PopulatedLibraryScreen(view,
                                                       libraryPlaces,
                                                       projectDetailEvent,
                                                       libraryServiceCaller,
                                                       projectController,
                                                       tileWidgets,
                                                       addProjectButtonPresenter));

        project1 = mock(Project.class);
        doReturn("project1Name").when(project1).getProjectName();
        project2 = mock(Project.class);
        doReturn("project2Name").when(project2).getProjectName();
        project3 = mock(Project.class);
        doReturn("project3Name").when(project3).getProjectName();

        final List<Project> projects = new ArrayList<>();
        projects.add(project1);
        projects.add(project2);
        projects.add(project3);

        final LibraryInfo libraryInfo = new LibraryInfo("master",
                                                        projects);
        doReturn(libraryInfo).when(libraryService).getLibraryInfo(any(Repository.class),
                                                                  anyString());

        doReturn(true).when(projectController).canCreateProjects();
        doReturn(true).when(projectController).canReadProjects();
        doReturn(true).when(projectController).canReadProject(any());
        doReturn(false).when(projectController).canReadProject(project2);

        doReturn(mock(TileWidget.View.class)).when(tileWidget).getView();
        doReturn(tileWidget).when(tileWidgets).get();
        doReturn(mock(AddProjectButtonPresenter.View.class)).when(addProjectButtonPresenter).getView();

        libraryScreen.setup();
    }

    @Test
    public void setupTest() {
        verify(addProjectButtonPresenter).getView();
        verify(view).addAction(any());

        verify(view).clearFilterText();
        verify(view).clearProjects();

        verify(tileWidget,
               times(2)).init(any(),
                              any(),
                              any(),
                              any(),
                              any());
        verify(tileWidget).init(eq("project1Name"),
                                any(),
                                any(),
                                any(),
                                any());
        verify(tileWidget).init(eq("project3Name"),
                                any(),
                                any(),
                                any(),
                                any());
        verify(view,
               times(2)).addProject(any());
    }

    @Test
    public void selectCommandTest() {
        libraryScreen.selectCommand(project1).execute();

        verify(libraryPlaces).goToProject(any(ProjectInfo.class));
    }

    @Test
    public void filterProjectsTest() {
        assertEquals(2,
                     libraryScreen.projects.size());
        assertEquals(1,
                     libraryScreen.filterProjects("project1").size());
        assertEquals(1,
                     libraryScreen.filterProjects("roject1").size());
        assertEquals(0,
                     libraryScreen.filterProjects("unexistent").size());
    }
}