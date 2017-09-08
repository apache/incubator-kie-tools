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
import javax.enterprise.event.Event;

import org.guvnor.common.services.project.client.repositories.ConflictingRepositoriesPopup;
import org.guvnor.common.services.project.events.NewProjectEvent;
import org.guvnor.common.services.project.service.DeploymentMode;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.repositories.Repository;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.library.api.LibraryInfo;
import org.kie.workbench.common.screens.library.api.LibraryService;
import org.kie.workbench.common.screens.library.api.ProjectInfo;
import org.kie.workbench.common.screens.library.api.preferences.LibraryPreferences;
import org.kie.workbench.common.screens.library.client.util.LibraryPlaces;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.ext.widgets.common.client.common.BusyIndicatorView;
import org.uberfire.java.nio.file.FileAlreadyExistsException;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mocks.SessionInfoMock;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.workbench.events.NotificationEvent;

import static org.jgroups.util.Util.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class NewProjectScreenTest {

    @Mock
    private LibraryService libraryService;
    private CallerMock<LibraryService> libraryServiceCaller;

    @Mock
    private PlaceManager placeManager;

    @Mock
    private BusyIndicatorView busyIndicatorView;

    @Mock
    private Event<NotificationEvent> notificationEvent;

    @Mock
    private LibraryPlaces libraryPlaces;

    @Mock
    private NewProjectScreen.View view;

    private SessionInfo sessionInfo;

    @Mock
    private Event<NewProjectEvent> newProjectEvent;

    @Mock
    private LibraryPreferences libraryPreferences;

    @Mock
    private ConflictingRepositoriesPopup conflictingRepositoriesPopup;

    private NewProjectScreen newProjectScreen;

    private LibraryInfo libraryInfo;

    @Before
    public void setup() {
        libraryServiceCaller = new CallerMock<>(libraryService);
        sessionInfo = new SessionInfoMock();

        final OrganizationalUnit selectedOrganizationalUnit = mock(OrganizationalUnit.class);
        doReturn("selectedOrganizationalUnit").when(selectedOrganizationalUnit).getIdentifier();
        doReturn(selectedOrganizationalUnit).when(libraryPlaces).getSelectedOrganizationalUnit();

        newProjectScreen = spy(new NewProjectScreen(libraryServiceCaller,
                                                    placeManager,
                                                    busyIndicatorView,
                                                    notificationEvent,
                                                    libraryPlaces,
                                                    view,
                                                    sessionInfo,
                                                    newProjectEvent,
                                                    libraryPreferences,
                                                    conflictingRepositoriesPopup));

        doReturn("baseUrl").when(newProjectScreen).getBaseURL();

        doReturn("emptyNameMessage").when(view).getEmptyNameMessage();
        doReturn("invalidNameMessage").when(view).getInvalidNameMessage();
        doReturn("duplicatedProjectMessage").when(view).getDuplicatedProjectMessage();

        libraryInfo = new LibraryInfo("master",
                                      new ArrayList<>());
        doReturn(libraryInfo).when(libraryService).getLibraryInfo(any(Repository.class),
                                                                  anyString());

        newProjectScreen.load();
    }

    @Test
    public void loadTest() {
        assertEquals(libraryInfo,
                     newProjectScreen.libraryInfo);
    }

    @Test
    public void cancelTest() {
        newProjectScreen.cancel();

        verify(libraryPlaces).goToLibrary();
        verify(placeManager).closePlace(LibraryPlaces.NEW_PROJECT_SCREEN);
    }

    @Test
    public void newProjectIsCreatedIntoSelectedRepository() throws Exception {
        final OrganizationalUnit organizationalUnit = mock(OrganizationalUnit.class);
        when(libraryPlaces.getSelectedOrganizationalUnit()).thenReturn(organizationalUnit);

        final Repository repository = mock(Repository.class);
        when(libraryPlaces.getSelectedRepository()).thenReturn(repository);

        newProjectScreen.createProject("test",
                                       "description");

        verify(libraryService).createProject("test",
                                             organizationalUnit,
                                             repository,
                                             "baseUrl",
                                             "description",
                                             DeploymentMode.VALIDATED);
    }

    @Test
    public void createProjectSuccessfullyTest() {
        newProjectScreen.createProject("projectName",
                                       "description");

        verify(busyIndicatorView).showBusyIndicator(anyString());
        verify(newProjectEvent).fire(any(NewProjectEvent.class));
        verify(busyIndicatorView).hideBusyIndicator();
        verify(notificationEvent).fire(any(NotificationEvent.class));
        verify(libraryPlaces).goToProject(any(ProjectInfo.class));
        verify(placeManager).closePlace(LibraryPlaces.NEW_PROJECT_SCREEN);
    }

    @Test
    public void createProjectWithEmptyNameFailedTest() {
        newProjectScreen.createProject("",
                                       "description");

        verify(busyIndicatorView).showBusyIndicator(anyString());
        verify(newProjectEvent,
               never()).fire(any(NewProjectEvent.class));
        verify(busyIndicatorView).hideBusyIndicator();
        verify(notificationEvent).fire(new NotificationEvent(view.getEmptyNameMessage(),
                                                             NotificationEvent.NotificationType.ERROR));
        verify(libraryPlaces,
               never()).goToProject(any(ProjectInfo.class));
        verify(placeManager,
               never()).closePlace(LibraryPlaces.NEW_PROJECT_SCREEN);
    }

    @Test
    public void createProjectWithDuplicatedNameTest() {
        doThrow(new FileAlreadyExistsException()).when(libraryService).createProject(anyString(),
                                                                                     any(),
                                                                                     any(Repository.class),
                                                                                     anyString(),
                                                                                     anyString(),
                                                                                     any());
        doAnswer(invocationOnMock -> ((Throwable) invocationOnMock.getArguments()[0]).getCause() instanceof FileAlreadyExistsException)
                .when(newProjectScreen).isDuplicatedProjectName(any());

        newProjectScreen.createProject("projectName",
                                       "description");

        verify(busyIndicatorView).showBusyIndicator(anyString());
        verify(newProjectEvent,
               never()).fire(any(NewProjectEvent.class));
        verify(busyIndicatorView).hideBusyIndicator();
        verify(notificationEvent).fire(new NotificationEvent(view.getDuplicatedProjectMessage(),
                                                             NotificationEvent.NotificationType.ERROR));
        verify(libraryPlaces,
               never()).goToProject(any(ProjectInfo.class));
        verify(placeManager,
               never()).closePlace(LibraryPlaces.NEW_PROJECT_SCREEN);
    }
}