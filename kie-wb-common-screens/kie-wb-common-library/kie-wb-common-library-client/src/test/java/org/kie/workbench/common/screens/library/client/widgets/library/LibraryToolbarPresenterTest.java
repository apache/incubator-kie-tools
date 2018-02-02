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
import java.util.Optional;

import org.guvnor.common.services.project.client.context.WorkspaceProjectContext;
import org.guvnor.common.services.project.model.Module;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.guvnor.common.services.project.service.WorkspaceProjectService;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.repositories.Branch;
import org.guvnor.structure.repositories.Repository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.library.client.util.LibraryPlaces;
import org.kie.workbench.common.services.shared.project.KieModule;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mvp.Command;
import org.uberfire.spaces.Space;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class LibraryToolbarPresenterTest {

    @Mock
    private LibraryToolbarPresenter.View view;

    @Mock
    private PlaceManager placeManager;

    @Mock
    private WorkspaceProjectContext projectContext;

    @Mock
    private WorkspaceProjectService projectService;

    @Mock
    private LibraryPlaces libraryPlaces;

    private LibraryToolbarPresenter presenter;

    private OrganizationalUnit selectedOrganizationalUnit;

    private Repository selectedRepository;

    private Command callback;

    private ArrayList<Branch> selectedRepositoryBranches;

    @Before
    public void setup() {
        presenter = new LibraryToolbarPresenter(projectContext,
                                                new CallerMock<>(projectService),
                                                libraryPlaces,
                                                view,
                                                placeManager);

        selectedOrganizationalUnit = mock(OrganizationalUnit.class);

        final Branch masterBranch = new Branch("master",
                                               mock(Path.class));

        selectedRepository = mock(Repository.class);
        selectedRepositoryBranches = new ArrayList<>();
        selectedRepositoryBranches.add(masterBranch);
        when(selectedRepository.getDefaultBranch()).thenReturn(Optional.of(masterBranch));
        when(selectedRepository.getBranches()).thenReturn(selectedRepositoryBranches);
        doReturn("repository1").when(selectedRepository).getAlias();

        callback = mock(Command.class);

        doReturn(Optional.of(selectedOrganizationalUnit)).when(projectContext).getActiveOrganizationalUnit();
        doReturn(Optional.of(new WorkspaceProject(selectedOrganizationalUnit,
                                      selectedRepository,
                                      masterBranch,
                                      mock(Module.class)))).when(projectContext).getActiveWorkspaceProject();
        when(projectContext.getActiveModule()).thenReturn(Optional.empty());
        when(projectContext.getActiveRepositoryRoot()).thenReturn(Optional.empty());
        when(projectContext.getActivePackage()).thenReturn(Optional.empty());
    }

    @Test
    public void initTest() {
        presenter.init(callback);

        verify(view).init(presenter);
        verify(callback).execute();
    }

    @Test
    public void showBranchSelectorOnInit() throws Exception {
        presenter.init(callback);

        verify(view).setBranchSelectorVisibility(false);
    }

    @Test
    public void updateSelectedBranch() throws Exception {

        final Branch devBranch = new Branch("dev",
                                            mock(Path.class));

        final WorkspaceProject devBranchProject = new WorkspaceProject(selectedOrganizationalUnit,
                                                                       selectedRepository,
                                                                       devBranch,
                                                                       mock(Module.class));
        Space space = new Space("test-realm");
        doReturn(space).when(selectedRepository).getSpace();
        doReturn(devBranchProject).when(projectService).resolveProject(space, devBranch);

        selectedRepositoryBranches.add(devBranch);
        doReturn(Optional.of(devBranch)).when(selectedRepository).getBranch("dev");

        presenter.init(callback);
        reset(view);

        doReturn(true).when(placeManager).closeAllPlacesOrNothing();
        doReturn("dev").when(view).getSelectedBranch();

        presenter.onUpdateSelectedBranch();

        verify(libraryPlaces).goToProject(devBranchProject);
    }

    @Test
    public void noActiveProject() throws Exception {
        presenter.setUpBranches();

        verify(view).clearBranches();
        verify(view).setBranchSelectorVisibility(false);
    }

    @Test
    public void activeProjectHasOnlyOneBranch() throws Exception {

        final Repository repository = mock(Repository.class);
        final ArrayList<Branch> branches = new ArrayList<>();
        branches.add(new Branch());
        doReturn(branches).when(repository).getBranches();
        doReturn(Optional.of(new WorkspaceProject(mock(OrganizationalUnit.class),
                                      repository,
                                      mock(Branch.class),
                                      mock(KieModule.class)))).when(projectContext).getActiveWorkspaceProject();

        presenter.setUpBranches();

        verify(view).clearBranches();
        verify(view).setBranchSelectorVisibility(false);
    }

    @Test
    public void selectorVisibleWhenMoreThanOneBranch() throws Exception {

        final Repository repository = mock(Repository.class);
        final ArrayList<Branch> branches = new ArrayList<>();
        branches.add(new Branch());
        branches.add(new Branch());
        doReturn(branches).when(repository).getBranches();
        doReturn(Optional.of(new WorkspaceProject(mock(OrganizationalUnit.class),
                                      repository,
                                      mock(Branch.class),
                                      mock(KieModule.class)))).when(projectContext).getActiveWorkspaceProject();

        presenter.setUpBranches();

        verify(view).clearBranches();
        verify(view).setBranchSelectorVisibility(true);
    }

    @Test
    public void listBranches() throws Exception {

        final Repository repository = mock(Repository.class);
        final ArrayList<Branch> branches = new ArrayList<>();
        branches.add(new Branch("one", mock(Path.class)));
        branches.add(new Branch("two", mock(Path.class)));
        doReturn(branches).when(repository).getBranches();

        // Normally this is in the branch list, but I want to verify it gets set from the branch in the project
        final Branch selectedBranch = new Branch("selectedBranch", mock(Path.class));

        doReturn(Optional.of(new WorkspaceProject(mock(OrganizationalUnit.class),
                                      repository,
                                      selectedBranch,
                                      mock(KieModule.class)))).when(projectContext).getActiveWorkspaceProject();

        presenter.setUpBranches();

        verify(view).addBranch("one");
        verify(view).addBranch("two");

        view.setSelectedBranch("selectedBranch");
    }
}
