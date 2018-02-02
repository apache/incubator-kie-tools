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

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
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
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.workbench.events.PlaceGainFocusEvent;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.mvp.PlaceRequest;

@RunWith(MockitoJUnitRunner.class)
public class ProjectScreenRefreshTest {

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

        final WorkspaceProject project = new WorkspaceProject(mock(OrganizationalUnit.class),
                                                              mock(Repository.class),
                                                              new Branch("master", mock(Path.class)),
                                                              null);
        doReturn(Optional.of(project)).when(projectContext).getActiveWorkspaceProject();
    }

    @Test
    public void refreshOnlyIfSetupIsDone() throws Exception {

        final PlaceGainFocusEvent placeGainFocusEvent = getPlaceGainFocusEvent(LibraryPlaces.PROJECT_SCREEN);

        screen.refreshOnFocus(placeGainFocusEvent);

        verify(this.view, never()).setContent(any(HTMLElement.class));
    }

    @Test
    public void refreshProjectChanged() throws Exception {

        IsElement view = mock(IsElement.class);
        doReturn(view).when(emptyWorkspaceProjectPresenter).getView();
        doReturn(mock(HTMLElement.class)).when(view).getElement();

        final PlaceGainFocusEvent placeGainFocusEvent = getPlaceGainFocusEvent(LibraryPlaces.PROJECT_SCREEN);

        screen.onStartup();

        doReturn(Optional.of(new WorkspaceProject(mock(OrganizationalUnit.class),
                                                  mock(Repository.class),
                                                  new Branch("other", mock(Path.class)),
                                                  null))).when(projectContext).getActiveWorkspaceProject();

        reset(this.view);

        screen.refreshOnFocus(placeGainFocusEvent);

        verify(this.view).setContent(any(HTMLElement.class));
    }

    @Test
    public void onlyRefreshIfWeTargetProjectScreen() throws Exception {

        final PlaceGainFocusEvent placeGainFocusEvent = getPlaceGainFocusEvent("nothing");
        IsElement view = mock(IsElement.class);
        HTMLElement element = mock(HTMLElement.class);
        doReturn(element).when(view).getElement();
        doReturn(view).when(emptyWorkspaceProjectPresenter).getView();

        screen.onStartup();

        reset(this.view);

        screen.refreshOnFocus(placeGainFocusEvent);

        verify(this.view, never()).setContent(any(HTMLElement.class));
    }

    private PlaceGainFocusEvent getPlaceGainFocusEvent(String identifier) {
        final PlaceGainFocusEvent placeGainFocusEvent = mock(PlaceGainFocusEvent.class);
        final PlaceRequest placeRequest = mock(PlaceRequest.class);
        doReturn(identifier).when(placeRequest).getIdentifier();
        doReturn(placeRequest).when(placeGainFocusEvent).getPlace();
        return placeGainFocusEvent;
    }
}