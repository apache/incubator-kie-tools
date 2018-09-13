/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.library.client.screens.project.branch.delete;

import java.util.Optional;
import javax.enterprise.event.Event;

import org.guvnor.common.services.project.model.WorkspaceProject;
import org.guvnor.structure.repositories.Branch;
import org.guvnor.structure.repositories.Repository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.library.api.LibraryService;
import org.kie.workbench.common.screens.library.client.util.LibraryPlaces;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.mocks.CallerMock;
import org.uberfire.workbench.events.NotificationEvent;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DeleteBranchPopUpScreenTest {

    private DeleteBranchPopUpScreen presenter;

    @Mock
    private DeleteBranchPopUpScreen.View view;

    @Mock
    private LibraryService libraryService;

    @Mock
    private LibraryPlaces libraryPlaces;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private WorkspaceProject project;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private Branch branch;

    @Before
    public void setUp() {
        when(libraryPlaces.getActiveWorkspace()).thenReturn(project);
        when(project.getRepository().getDefaultBranch()).thenReturn(Optional.of(branch));
        when(branch.getName()).thenReturn("myBranch");

        presenter = spy(new DeleteBranchPopUpScreen(view,
                                                    new CallerMock<>(libraryService),
                                                    libraryPlaces));
    }

    @Test
    public void testShowDeleteIfIsAbleTo() {
        this.presenter.show(branch);

        verify(this.view).show(eq(branch.getName()));
    }

    @Test
    public void testDeleteActionWithDifferentConfirmationName() {
        when(this.view.getConfirmedName()).thenReturn("anotherName");
        this.presenter.show(branch);
        this.presenter.delete();

        verify(this.view).showError(anyString());
    }

    @Test
    public void testDeleteAction() {
        when(this.view.getConfirmedName()).thenReturn("myBranch");
        this.presenter.show(branch);
        this.presenter.delete();

        verify(this.view, never()).showError(anyString());
        verify(this.libraryService).removeBranch(project, branch);
        verify(libraryPlaces).goToProject(project, branch);
        verify(libraryPlaces, never()).goToLibrary();
    }

    @Test
    public void testDeleteActionWithNoDefaultBranch() {
        when(project.getRepository().getDefaultBranch()).thenReturn(Optional.empty());
        when(this.view.getConfirmedName()).thenReturn("myBranch");
        this.presenter.show(branch);
        this.presenter.delete();

        verify(this.view, never()).showError(anyString());
        verify(this.libraryService).removeBranch(project, branch);
        verify(libraryPlaces, never()).goToProject(project, branch);
        verify(libraryPlaces).goToLibrary();
    }

    @Test
    public void cancelTest() {
        presenter.cancel();

        verify(view).hide();
    }
}