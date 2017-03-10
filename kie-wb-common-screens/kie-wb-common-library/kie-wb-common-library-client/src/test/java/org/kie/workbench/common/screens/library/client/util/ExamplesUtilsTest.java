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

package org.kie.workbench.common.screens.library.client.util;

import java.util.HashSet;
import java.util.Set;
import javax.enterprise.event.Event;

import org.guvnor.common.services.project.events.NewProjectEvent;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.repositories.Repository;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.examples.model.ExampleProject;
import org.kie.workbench.common.screens.library.api.LibraryService;
import org.kie.workbench.common.screens.library.api.ProjectInfo;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.ext.widgets.common.client.common.BusyIndicatorView;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mocks.SessionInfoMock;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.workbench.events.NotificationEvent;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ExamplesUtilsTest {

    private SessionInfo sessionInfo = new SessionInfoMock();

    @Mock
    private TranslationService ts;

    @Mock
    private LibraryPlaces libraryPlaces;

    @Mock
    private LibraryService libraryService;
    private Caller<LibraryService> libraryServiceCaller;

    @Mock
    private BusyIndicatorView busyIndicatorView;

    @Mock
    private Event<NotificationEvent> notificationEvent;

    @Mock
    private Event<NewProjectEvent> newProjectEvent;

    private ExamplesUtils examplesUtils;

    @Before
    public void setup() {
        libraryServiceCaller = new CallerMock<>(libraryService);
        examplesUtils = new ExamplesUtils(sessionInfo,
                                          ts,
                                          libraryPlaces,
                                          libraryServiceCaller,
                                          busyIndicatorView,
                                          notificationEvent,
                                          newProjectEvent);
    }

    @Test
    public void getExampleProjectsTest() {
        final ParameterizedCommand<Set<ExampleProject>> callback = mock(ParameterizedCommand.class);
        final Set<ExampleProject> exampleProjects = new HashSet<>();

        doReturn(exampleProjects).when(libraryService).getExampleProjects();

        examplesUtils.getExampleProjects(callback);

        verify(libraryService,
               times(1)).getExampleProjects();
        verify(callback,
               times(1)).execute(anySet());

        examplesUtils.getExampleProjects(callback);

        verify(libraryService,
               times(1)).getExampleProjects();
        verify(callback,
               times(2)).execute(anySet());
    }

    @Test
    public void importProjectSuccessfullyTest() {
        final ArgumentCaptor<ProjectInfo> projectInfoArgumentCaptor = ArgumentCaptor.forClass(ProjectInfo.class);

        final OrganizationalUnit organizationalUnit = mock(OrganizationalUnit.class);
        final Repository repository = mock(Repository.class);
        final String branch = "master";
        final ExampleProject exampleProject = mock(ExampleProject.class);
        final Project project = mock(Project.class);

        doReturn(organizationalUnit).when(libraryPlaces).getSelectedOrganizationalUnit();
        doReturn(repository).when(libraryPlaces).getSelectedRepository();
        doReturn(branch).when(libraryPlaces).getSelectedBranch();
        doReturn(project).when(libraryService).importProject(organizationalUnit,
                                                             repository,
                                                             "master",
                                                             exampleProject);

        examplesUtils.importProject(exampleProject);

        verify(busyIndicatorView).showBusyIndicator(anyString());
        verify(busyIndicatorView).hideBusyIndicator();
        verify(notificationEvent).fire(any(NotificationEvent.class));
        verify(newProjectEvent).fire(any(NewProjectEvent.class));
        verify(libraryPlaces).goToProject(projectInfoArgumentCaptor.capture());

        final ProjectInfo projectInfo = projectInfoArgumentCaptor.getValue();

        assertEquals(organizationalUnit,
                     projectInfo.getOrganizationalUnit());
        assertEquals(repository,
                     projectInfo.getRepository());
        assertEquals(branch,
                     projectInfo.getBranch());
        assertEquals(project,
                     projectInfo.getProject());
    }

    @Test
    public void importProjectFailTest() {
        final ArgumentCaptor<ProjectInfo> projectInfoArgumentCaptor = ArgumentCaptor.forClass(ProjectInfo.class);

        final OrganizationalUnit organizationalUnit = mock(OrganizationalUnit.class);
        final Repository repository = mock(Repository.class);
        final String branch = "master";
        final ExampleProject exampleProject = mock(ExampleProject.class);
        final Project project = mock(Project.class);

        doReturn(organizationalUnit).when(libraryPlaces).getSelectedOrganizationalUnit();
        doReturn(repository).when(libraryPlaces).getSelectedRepository();
        doReturn(branch).when(libraryPlaces).getSelectedBranch();
        doThrow(new RuntimeException()).when(libraryService).importProject(organizationalUnit,
                                                                           repository,
                                                                           "master",
                                                                           exampleProject);

        examplesUtils.importProject(exampleProject);

        verify(busyIndicatorView).showBusyIndicator(anyString());
        verify(busyIndicatorView).hideBusyIndicator();
        verify(notificationEvent).fire(any(NotificationEvent.class));
        verify(newProjectEvent,
               never()).fire(any(NewProjectEvent.class));
        verify(libraryPlaces,
               never()).goToProject(any(ProjectInfo.class));
    }
}
