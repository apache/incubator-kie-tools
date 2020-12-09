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
package org.kie.workbench.common.screens.library.client.perspective;

import java.util.Collections;

import javax.enterprise.event.Event;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.guvnor.common.services.project.context.WorkspaceProjectContextChangeEvent;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.library.client.util.LibraryPlaces;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.VFSService;
import org.uberfire.client.workbench.events.PerspectiveChange;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.workbench.events.NotificationEvent;
import org.uberfire.workbench.model.PanelDefinition;

import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class LibraryPerspectiveTest {

    @Mock
    private LibraryPlaces libraryPlaces;

    @Mock
    private EventSourceMock<WorkspaceProjectContextChangeEvent> projectContextChangeEvent;

    @Mock
    private VFSService vfsService;
    private CallerMock<VFSService> vfsServiceCaller;

    @Mock
    private EventSourceMock<NotificationEvent> notificationEvent;

    @Mock
    private TranslationService ts;

    @Captor
    private ArgumentCaptor<Command> commandCaptor;

    private LibraryPerspective perspective;

    @Mock
    private PerspectiveChange perspectiveChangeEvent;

    @Before
    public void setup() {
        vfsServiceCaller = new CallerMock<>(vfsService);
        perspective = spy(new LibraryPerspective(libraryPlaces,
                                                 projectContextChangeEvent,
                                                 vfsServiceCaller,
                                                 notificationEvent,
                                                 ts));
        when(perspectiveChangeEvent.getIdentifier()).thenReturn(LibraryPlaces.LIBRARY_PERSPECTIVE);
    }

    @Test
    public void testLibraryPlacesIsInitialized() throws Exception {
        perspective.onStartup(mock(PlaceRequest.class));
        verify(libraryPlaces).init(any(LibraryPerspective.class));
    }

    @Test
    public void libraryRefreshesPlacesOnPerspectiveChangeEventWithRootPanelTest() {
        doReturn(mock(PanelDefinition.class)).when(perspective).getRootPanel();

        perspective.perspectiveChangeEvent(perspectiveChangeEvent);

        verify(libraryPlaces).refresh(commandCaptor.capture());

        commandCaptor.getValue().execute();

        verify(libraryPlaces).goToLibrary();
    }

    @Test
    public void libraryOpensProjectFromPathOnPerspectiveChangeEventWithRootPanelTest() {
        final Path projectPath = mock(Path.class);

        doReturn(mock(PanelDefinition.class)).when(perspective).getRootPanel();
        doReturn(Collections.singletonMap("path", Collections.singletonList("projectPath"))).when(perspective).getWindowParameterMap();
        doReturn(projectPath).when(vfsService).get("projectPath");

        perspective.onStartup(new DefaultPlaceRequest(LibraryPlaces.LIBRARY_PERSPECTIVE));
        perspective.perspectiveChangeEvent(perspectiveChangeEvent);

        verify(libraryPlaces).refresh(commandCaptor.capture());

        commandCaptor.getValue().execute();

        verify(libraryPlaces).goToProject(projectPath);
        verify(notificationEvent, never()).fire(any());
    }

    @Test
    public void libraryFailsToOpenProjectFromInvalidPathOnPerspectiveChangeEventWithRootPanelTest() {
        final Path projectPath = mock(Path.class);

        doReturn(mock(PanelDefinition.class)).when(perspective).getRootPanel();
        doReturn(Collections.singletonMap("path", Collections.singletonList("projectPath"))).when(perspective).getWindowParameterMap();
        doThrow(RuntimeException.class).when(vfsService).get("projectPath");

        perspective.onStartup(new DefaultPlaceRequest(LibraryPlaces.LIBRARY_PERSPECTIVE));
        perspective.perspectiveChangeEvent(perspectiveChangeEvent);

        verify(libraryPlaces).refresh(commandCaptor.capture());

        commandCaptor.getValue().execute();

        verify(libraryPlaces, never()).goToProject(projectPath);
        verify(notificationEvent).fire(any());
    }

    @Test
    public void libraryDoesNotLoadOnPerspectiveChangeEventWithoutRootPanelTest() {
        doReturn(null).when(perspective).getRootPanel();

        perspective.perspectiveChangeEvent(perspectiveChangeEvent);

        verify(libraryPlaces).refresh(commandCaptor.capture());

        commandCaptor.getValue().execute();

        verify(libraryPlaces,
               never()).goToLibrary();
    }

    @Test
    public void libraryDoesNotLoadOnPerspectiveChangeEventFromOtherPerspectives() {

        when(perspectiveChangeEvent.getIdentifier()).thenReturn("dora");

        perspective.perspectiveChangeEvent(perspectiveChangeEvent);

        verify(libraryPlaces, never()).refresh(any());
    }

    @Test
    public void libraryResetsContextOnCloseTest() {
        perspective.onClose();

        verify(projectContextChangeEvent).fire(new WorkspaceProjectContextChangeEvent());
    }
}