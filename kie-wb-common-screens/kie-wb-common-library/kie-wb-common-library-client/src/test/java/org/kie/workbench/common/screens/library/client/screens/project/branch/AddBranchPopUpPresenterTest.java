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
package org.kie.workbench.common.screens.library.client.screens.project.branch;

import java.util.Arrays;
import java.util.List;
import javax.enterprise.event.Event;

import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.guvnor.structure.repositories.Branch;
import org.guvnor.structure.repositories.Repository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.library.api.LibraryService;
import org.kie.workbench.common.screens.library.client.util.LibraryPlaces;
import org.kie.workbench.common.services.shared.validation.ValidationService;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.widgets.common.client.common.BusyIndicatorView;
import org.uberfire.java.nio.file.FileAlreadyExistsException;
import org.uberfire.mocks.CallerMock;
import org.uberfire.workbench.events.NotificationEvent;

import static org.jgroups.util.Util.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AddBranchPopUpPresenterTest {

    @Mock
    private LibraryService libraryService;
    private CallerMock<LibraryService> libraryServiceCaller;

    @Mock
    private BusyIndicatorView busyIndicatorView;

    @Mock
    private Event<NotificationEvent> notificationEvent;

    @Mock
    private LibraryPlaces libraryPlaces;

    @Mock
    private AddBranchPopUpPresenter.View view;

    @Mock
    private ValidationService validationService;
    private CallerMock<ValidationService> validationServiceCaller;

    private AddBranchPopUpPresenter presenter;

    @Mock
    private Repository repository;

    @Mock
    private WorkspaceProject project;

    @Before
    public void setup() {
        libraryServiceCaller = new CallerMock<>(libraryService);
        validationServiceCaller = new CallerMock<>(validationService);

        when(libraryPlaces.getActiveWorkspace()).thenReturn(project);

        presenter = spy(new AddBranchPopUpPresenter(libraryServiceCaller,
                                                    busyIndicatorView,
                                                    notificationEvent,
                                                    libraryPlaces,
                                                    view,
                                                    validationServiceCaller));

        doReturn("emptyNameMessage").when(view).getEmptyNameMessage();
        doReturn("invalidNameMessage").when(view).getInvalidNameMessage();
        doReturn("duplicatedBranchMessage").when(view).getDuplicatedBranchMessage();

        doReturn(true).when(validationService).isBranchNameValid(anyString());

        when(repository.getAlias()).thenReturn("repository");
        final List<Branch> repositoryBranches = Arrays.asList(makeBranch("branch1", repository.getAlias()),
                                                              makeBranch("branch2", repository.getAlias()));
        when(repository.getBranches()).thenReturn(repositoryBranches);
        when(project.getRepository()).thenReturn(repository);
        final Branch branch = makeBranch("master", repository.getAlias());
        when(project.getBranch()).thenReturn(branch);

        presenter.setup();
    }

    @Test
    public void loadTest() {
        verify(view).init(presenter);
        assertEquals(project, presenter.project);
    }

    @Test
    public void cancelTest() {
        presenter.cancel();

        view.hide();
    }

    @Test
    public void newBranchIsCreated() throws Exception {
        doReturn("new-branch").when(view).getName();
        doReturn("master").when(view).getBranchFrom();

        presenter.add();

        final ArgumentCaptor<POM> pomArgumentCaptor = ArgumentCaptor.forClass(POM.class);

        verify(view).setAddButtonEnabled(false);
        verify(view).showBusyIndicator(anyString());
        verify(libraryService).addBranch("new-branch", "master",
                                          project);
        verify(view).setAddButtonEnabled(true);
        verify(view).hideBusyIndicator();
    }

    @Test
    public void createProjectWithDuplicatedNameTest() {
        doReturn("new-branch").when(view).getName();
        doReturn("master").when(view).getBranchFrom();

        doThrow(new FileAlreadyExistsException()).when(libraryService).addBranch(any(), any(), any());

        presenter.add();

        verify(view).setAddButtonEnabled(false);
        verify(view).showBusyIndicator(anyString());
        verify(view).hideBusyIndicator();
        verify(view, never()).hide();
        verify(view).showError(anyString());
        verify(view).setAddButtonEnabled(true);
    }

    @Test
    public void createProjectWithEmptyNameFailedTest() {
        doReturn("").when(view).getName();
        doReturn("master").when(view).getBranchFrom();

        presenter.add();

        verify(view).setAddButtonEnabled(false);
        verify(view).showBusyIndicator(anyString());
        verify(view).hideBusyIndicator();
        verify(view, never()).hide();
        verify(view).showError(anyString());
        verify(view).setAddButtonEnabled(true);
    }

    @Test
    public void createProjectWithInvalidNameFailedTest() {
        doReturn("name").when(view).getName();

        doReturn(false).when(validationService).isBranchNameValid(any());

        presenter.add();

        verify(view).setAddButtonEnabled(false);
        verify(view).showBusyIndicator(anyString());
        verify(view).hideBusyIndicator();
        verify(view, never()).hide();
        verify(view).showError(anyString());
        verify(view).setAddButtonEnabled(true);
    }

    private Branch makeBranch(final String branchName,
                              final String repoName) {
        final Path path = mock(Path.class);
        doReturn("default://" + branchName + "@" + repoName + "/").when(path).toURI();
        return new Branch(branchName, path);
    }
}
