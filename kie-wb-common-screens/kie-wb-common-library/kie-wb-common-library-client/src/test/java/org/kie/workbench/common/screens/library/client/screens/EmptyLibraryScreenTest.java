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

import java.util.HashSet;
import java.util.Set;

import org.guvnor.common.services.project.client.security.ProjectController;
import org.jboss.errai.security.shared.api.identity.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.examples.model.ExampleProject;
import org.kie.workbench.common.screens.library.client.util.ExamplesUtils;
import org.kie.workbench.common.screens.library.client.util.LibraryPlaces;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.mvp.ParameterizedCommand;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class EmptyLibraryScreenTest {

    @Mock
    private EmptyLibraryScreen.View view;

    @Mock
    private User user;

    @Mock
    private LibraryPlaces libraryPlaces;

    @Mock
    private ExamplesUtils examplesUtils;

    @Mock
    private ProjectController projectController;

    private EmptyLibraryScreen emptyLibraryScreen;

    @Before
    public void setup() {
        emptyLibraryScreen = new EmptyLibraryScreen(view,
                                                    user,
                                                    libraryPlaces,
                                                    examplesUtils,
                                                    projectController);

        doReturn("user").when(user).getIdentifier();
    }

    @Test
    public void setupTest() {
        final ExampleProject exampleProject1 = mock(ExampleProject.class);
        final ExampleProject exampleProject2 = mock(ExampleProject.class);

        final Set<ExampleProject> exampleProjects = new HashSet<>();
        exampleProjects.add(exampleProject1);
        exampleProjects.add(exampleProject2);

        doAnswer(invocationOnMock -> {
            final ParameterizedCommand<Set<ExampleProject>> callback = (ParameterizedCommand<Set<ExampleProject>>) invocationOnMock.getArguments()[0];
            callback.execute(exampleProjects);
            return null;
        }).when(examplesUtils).getExampleProjects(any(ParameterizedCommand.class));

        emptyLibraryScreen.setup();

        verify(view).init(emptyLibraryScreen);
        verify(view).setup("user");
        verify(view).clearImportProjectsContainer();

        verify(view,
               times(2)).addProjectToImport(any(ExampleProject.class));
        verify(view).addProjectToImport(exampleProject1);
        verify(view).addProjectToImport(exampleProject2);
    }

    @Test
    public void canCreateNewProjectTest() {
        doReturn(true).when(projectController).canCreateProjects();

        emptyLibraryScreen.newProject();

        verify(libraryPlaces).goToNewProject();
    }

    @Test
    public void cannotCreateNewProjectTest() {
        doReturn(false).when(projectController).canCreateProjects();

        emptyLibraryScreen.newProject();

        verify(libraryPlaces,
               never()).goToNewProject();
    }

    @Test
    public void canImportProjectTest() {
        doReturn(true).when(projectController).canCreateProjects();

        final ExampleProject exampleProject = mock(ExampleProject.class);

        emptyLibraryScreen.importProject(exampleProject);

        verify(examplesUtils).importProject(exampleProject);
    }

    @Test
    public void cannotImportProjectTest() {
        doReturn(false).when(projectController).canCreateProjects();

        final ExampleProject exampleProject = mock(ExampleProject.class);

        emptyLibraryScreen.importProject(exampleProject);

        verify(examplesUtils,
               never()).importProject(exampleProject);
    }
}