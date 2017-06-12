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

import java.util.ArrayList;
import java.util.List;

import org.guvnor.common.services.project.client.security.ProjectController;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.library.client.util.ExamplesUtils;
import org.kie.workbench.common.screens.library.client.util.LibraryPlaces;
import org.kie.workbench.common.widgets.client.handlers.NewProjectHandler;
import org.kie.workbench.common.widgets.client.handlers.NewResourceHandler;
import org.kie.workbench.common.widgets.client.handlers.NewResourcePresenter;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.mvp.Command;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class NewProjectButtonWidgetTest {

    @Mock
    private NewProjectButtonWidget.View view;

    @Mock
    private ManagedInstance<NewProjectHandler> newProjectHandlers;

    @Mock
    private org.kie.workbench.common.screens.projecteditor.client.handlers.NewProjectHandler newDefaultProjectHandler;

    @Mock
    private NewResourcePresenter newResourcePresenter;

    @Mock
    private LibraryPlaces libraryPlaces;

    @Mock
    private ProjectController projectController;

    @Mock
    private ExamplesUtils examplesUtils;

    private NewProjectButtonWidget presenter;

    @Before
    public void setup() {
        doReturn(true).when(projectController).canCreateProjects();

        presenter = spy(new NewProjectButtonWidget(view,
                                                   newProjectHandlers,
                                                   newDefaultProjectHandler,
                                                   newResourcePresenter,
                                                   libraryPlaces,
                                                   projectController));
    }

    @Test
    public void initTest() {
        NewProjectHandler otherNewProjectHandler1 = mock(NewProjectHandler.class);
        doReturn(true).when(otherNewProjectHandler1).canCreate();
        NewProjectHandler otherNewProjectHandler2 = mock(NewProjectHandler.class);
        doReturn(false).when(otherNewProjectHandler2).canCreate();

        List<NewResourceHandler> handlers = new ArrayList<>();
        handlers.add(newDefaultProjectHandler);
        handlers.add(otherNewProjectHandler1);
        handlers.add(otherNewProjectHandler2);
        doReturn(handlers).when(presenter).getNewProjectHandlers();

        presenter.init();

        verify(view,
               times(1)).addOption(anyString(),
                                   any(Command.class));
        verify(view,
               times(2)).addOption(anyString(),
                                   any(NewProjectHandler.class));
        verify(view).addOption(anyString(),
                               any(Command.class));
        verify(view).addOption(anyString(),
                               eq(newDefaultProjectHandler));
        verify(view).addOption(anyString(),
                               eq(otherNewProjectHandler1));
    }
}
