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

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.Optional;

import org.guvnor.common.services.project.client.context.WorkspaceProjectContext;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.repositories.Branch;
import org.guvnor.structure.repositories.Repository;
import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.library.api.LibraryService;
import org.kie.workbench.common.screens.library.client.events.ProjectDetailEvent;
import org.kie.workbench.common.screens.library.client.util.LibraryPlaces;
import org.kie.workbench.common.services.shared.project.KieModule;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mocks.EventSourceMock;

@RunWith(MockitoJUnitRunner.class)
public class ProjectScreenTest {

    @Mock
    private ProjectScreen.View view;
    @Mock
    private LibraryPlaces libraryPlaces;
    @Mock
    private LibraryService libraryService;
    @Mock
    private EmptyWorkspaceProjectPresenter emptyWorkspaceProjectPresenter;
    @Mock
    private WorkspaceProjectListAssetsPresenter workspaceProjectListAssetsPresenter;
    @Mock
    private WorkspaceProjectContext projectContext;
    @Mock
    private EventSourceMock<ProjectDetailEvent> projectDetailEvent;

    @Captor
    private ArgumentCaptor<ProjectDetailEvent> projectDetailEventArgumentCaptor;

    private ProjectScreen screen;

    @Before
    public void setUp() throws Exception {
        screen = new ProjectScreen(view,
                                   libraryPlaces,
                                   new CallerMock<>(libraryService),
                                   emptyWorkspaceProjectPresenter,
                                   workspaceProjectListAssetsPresenter,
                                   projectContext,
                                   projectDetailEvent);
    }

    @Test
    public void setUpBranches() throws Exception {

        final WorkspaceProject project = new WorkspaceProject(mock(OrganizationalUnit.class),
                                                              mock(Repository.class),
                                                              mock(Branch.class),
                                                              null);
        doReturn(Optional.of(project)).when(projectContext).getActiveWorkspaceProject();

        IsElement view = mock(IsElement.class);
        HTMLElement element = mock(HTMLElement.class);
        doReturn(element).when(view).getElement();
        doReturn(view).when(emptyWorkspaceProjectPresenter).getView();

        screen.onStartup();

        verify(libraryPlaces).setUpBranches();
    }

    @Test
    public void showEmptyProject() throws Exception {

        final WorkspaceProject project = new WorkspaceProject(mock(OrganizationalUnit.class),
                                                              mock(Repository.class),
                                                              mock(Branch.class),
                                                              mock(KieModule.class));
        doReturn(Optional.of(project)).when(projectContext).getActiveWorkspaceProject();

        doReturn(false).when(libraryService).hasAssets(project);

        IsElement view = mock(IsElement.class);
        HTMLElement element = mock(HTMLElement.class);
        doReturn(element).when(view).getElement();
        doReturn(view).when(emptyWorkspaceProjectPresenter).getView();

        screen.onStartup();

        verify(projectDetailEvent).fire(projectDetailEventArgumentCaptor.capture());
        assertEquals(project, projectDetailEventArgumentCaptor.getValue().getProject());

        verify(emptyWorkspaceProjectPresenter).show(project);
    }

    @Test
    public void showList() throws Exception {

        final WorkspaceProject project = new WorkspaceProject(mock(OrganizationalUnit.class),
                                                              mock(Repository.class),
                                                              mock(Branch.class),
                                                              mock(KieModule.class));

        doReturn(Optional.of(project)).when(projectContext).getActiveWorkspaceProject();

        doReturn(true).when(libraryService).hasAssets(project);

        IsElement view = mock(IsElement.class);
        HTMLElement element = mock(HTMLElement.class);
        doReturn(element).when(view).getElement();
        doReturn(view).when(workspaceProjectListAssetsPresenter).getView();

        screen.onStartup();

        verify(projectDetailEvent).fire(projectDetailEventArgumentCaptor.capture());
        assertEquals(project, projectDetailEventArgumentCaptor.getValue().getProject());

        verify(workspaceProjectListAssetsPresenter).show(project);
    }

//    @Test
//    public void goToSettingsTest() {
//        screen.goToSettings();
//
//        verify(assetDetailEvent).fire(new AssetDetailEvent(pr project,
//                                                           null));
//    }
//
//    @Test
//    public void getProjectNameTest() {
//        assertEquals("projectName",
//                     screen.getProjectName());
//    }
//
//    @Test
//    public void selectCommandTest() {
//        final Path assetPath = mock(Path.class);
//
//        screen.selectCommand(assetPath).execute();
//
//        verify(libraryPlaces).goToAsset(projectInfo,
//                                        assetPath);
//    }
//
//    @Test
//    public void detailsCommandTest() {
//        final Path assetPath = mock(Path.class);
//
//        screen.detailsCommand(assetPath).execute();
//
//        verify(libraryPlaces).goToAsset(projectInfo,
//                                        assetPath);
//    }
}