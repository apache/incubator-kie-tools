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

package org.kie.workbench.common.screens.library.client.widgets.library;

import java.util.HashSet;
import java.util.Set;

import org.guvnor.common.services.project.client.security.ProjectController;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.examples.model.ExampleProject;
import org.kie.workbench.common.screens.library.client.util.ExamplesUtils;
import org.kie.workbench.common.screens.library.client.util.LibraryPlaces;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ImportProjectButtonWidgetTest {

    @Mock
    private ImportProjectButtonWidget.View view;

    @Mock
    private LibraryPlaces libraryPlaces;

    @Mock
    private ProjectController projectController;

    @Mock
    private ExamplesUtils examplesUtils;

    private ImportProjectButtonWidget presenter;

    private ExampleProject exampleProject1;
    private ExampleProject exampleProject2;

    @Before
    public void setup() {
        doReturn(true).when(projectController).canCreateProjects();

        doReturn("Advanced Import").when(view).getAdvancedImportDescription();

        exampleProject1 = mock(ExampleProject.class);
        doReturn("exampleProject1").when(exampleProject1).getName();
        doReturn("exampleProject1Description").when(exampleProject1).getDescription();
        exampleProject2 = mock(ExampleProject.class);
        doReturn("exampleProject2").when(exampleProject2).getName();
        doReturn("exampleProject2Description").when(exampleProject2).getDescription();

        final Set<ExampleProject> exampleProjects = new HashSet<>();
        exampleProjects.add(exampleProject1);
        exampleProjects.add(exampleProject2);

        doAnswer(invocationOnMock -> {
            final ParameterizedCommand<Set<ExampleProject>> callback = (ParameterizedCommand<Set<ExampleProject>>) invocationOnMock.getArguments()[0];
            callback.execute(exampleProjects);
            return null;
        }).when(examplesUtils).getExampleProjects(any(ParameterizedCommand.class));

        presenter = spy(new ImportProjectButtonWidget(view,
                                                      libraryPlaces,
                                                      projectController,
                                                      examplesUtils));
    }

    @Test
    public void initTest() {
        presenter.init();

        verify(view,
               times(1)).clearDropdown();

        verify(view,
               times(2)).addHeader(anyString());
        verify(view,
               times(1)).addSeparator();

        verify(view,
               times(1)).addOption(anyString(),
                                   any());
        verify(view,
               times(2)).addOption(anyString(),
                                   anyString(),
                                   any());

        verify(view).addOption(eq(view.getAdvancedImportDescription()),
                               any(Command.class));

        String projectName = exampleProject1.getName();
        String projectDescription = exampleProject1.getDescription();
        verify(view).addOption(eq(projectName),
                               eq(projectDescription),
                               any(Command.class));

        projectName = exampleProject2.getName();
        projectDescription = exampleProject2.getDescription();
        verify(view).addOption(eq(projectName),
                               eq(projectDescription),
                               any(Command.class));
    }

    @Test
    public void initWithoutProjectCreationPermissionTest() {
        doReturn(false).when(projectController).canCreateProjects();

        presenter.init();

        verify(view,
               never()).clearDropdown();
        verify(view,
               never()).addHeader(anyString());
        verify(view,
               never()).addSeparator();
        verify(view,
               never()).addOption(anyString(),
                                  any());
    }
}
