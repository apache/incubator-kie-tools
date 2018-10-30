/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.kie.workbench.common.screens.library.client.screens.project.delete;

import org.guvnor.common.services.project.model.WorkspaceProject;
import org.guvnor.structure.repositories.RepositoryService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.library.client.util.LibraryPlaces;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mvp.Command;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DeleteProjectPopUpScreenTest {

    private DeleteProjectPopUpScreen presenter;

    @Mock
    private DeleteProjectPopUpScreen.View view;

    @Mock
    private LibraryPlaces libraryPlaces;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private WorkspaceProject project;

    @Before
    public void setUp() {
        doAnswer(invocationOnMock -> {
            invocationOnMock.getArgumentAt(0, Command.class).execute();
            return null;
        }).when(libraryPlaces).closeAllPlacesOrNothing(any());

        when(project.getName()).thenReturn("kieProject");

        presenter = spy(new DeleteProjectPopUpScreen(view,
                                                     libraryPlaces));
    }

    @Test
    public void testShowDeleteIfIsAbleTo() {

        this.presenter.show(project);

        verify(this.view).show(eq(project.getName()));
    }

    @Test
    public void testDeleteActionWithDifferentConfirmationName() {
        when(this.view.getConfirmedName()).thenReturn("anotherName");
        this.presenter.show(project);
        this.presenter.delete();

        verify(this.view).showError(anyString());
        verify(this.libraryPlaces, never()).deleteProject(any(), any());
    }

    @Test
    public void testDeleteAction() {
        when(this.view.getConfirmedName()).thenReturn("kieProject");
        this.presenter.show(project);
        this.presenter.delete();

        verify(this.view, never()).showError(anyString());
        verify(this.libraryPlaces).closeAllPlacesOrNothing(any());
        verify(this.libraryPlaces).deleteProject(project, view);
    }
}