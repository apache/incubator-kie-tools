/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.library.client.settings.sections.branchmanagement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.guvnor.common.services.project.client.security.ProjectController;
import org.guvnor.common.services.project.model.Module;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.guvnor.structure.contributors.Contributor;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.organizationalunit.config.BranchPermissions;
import org.guvnor.structure.organizationalunit.config.RolePermissions;
import org.guvnor.structure.repositories.Branch;
import org.guvnor.structure.repositories.Repository;
import org.jboss.errai.common.client.api.Caller;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.library.api.LibraryService;
import org.kie.workbench.common.screens.library.client.settings.SettingsSectionChange;
import org.kie.workbench.common.screens.library.client.settings.util.sections.MenuItem;
import org.kie.workbench.common.screens.library.client.util.LibraryPlaces;
import org.kie.workbench.common.screens.projecteditor.model.ProjectScreenModel;
import org.kie.workbench.common.widgets.client.widget.KieSelectElement;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.promise.Promises;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.promise.SyncPromises;
import org.uberfire.spaces.Space;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class BranchManagementPresenterTest {

    @Mock
    private BranchManagementPresenter.View view;

    private Promises promises = new SyncPromises();

    @Mock
    private MenuItem<ProjectScreenModel> menuItem;

    @Mock
    private EventSourceMock<SettingsSectionChange<ProjectScreenModel>> settingsSectionChangeEvent;

    @Mock
    private LibraryService libraryService;
    private Caller<LibraryService> libraryServiceCaller;

    @Mock
    private LibraryPlaces libraryPlaces;

    @Mock
    private KieSelectElement branchesSelect;

    @Mock
    private BranchManagementPresenter.RoleAccessListPresenter roleAccessListPresenter;

    @Mock
    private ProjectController projectController;

    private BranchManagementPresenter presenter;

    @Before
    public void setup() {
        promises = spy(new SyncPromises());
        libraryServiceCaller = new CallerMock<>(libraryService);
        presenter = spy(new BranchManagementPresenter(view,
                                                      promises,
                                                      menuItem,
                                                      settingsSectionChangeEvent,
                                                      libraryServiceCaller,
                                                      libraryPlaces,
                                                      branchesSelect,
                                                      roleAccessListPresenter,
                                                      projectController));
        mockLibraryPlaces();
    }

    @Test
    public void setupWithNoUpdatableBranchesTest() {
        doReturn(promises.resolve(Collections.emptyList())).when(projectController).getUpdatableBranches(any());

        presenter.setup(mock(ProjectScreenModel.class)).then(v -> {
            verify(view).init(presenter);
            verify(view).showEmptyState();
            verify(branchesSelect, never()).setup(any(), any(), any());
            verify(libraryService, never()).loadBranchPermissions(anyString(), anyString(), anyString());

            return promises.resolve();
        }).catch_(error -> {
            fail();
            return promises.resolve();
        });
    }

    @Test
    public void setupWithUpdatableBranchesTest() {
        final Map<String, RolePermissions> permissionsByRole = new HashMap<>();
        permissionsByRole.put("CONTRIBUTOR", new RolePermissions("CONTRIBUTOR", true, false, true, true));
        doReturn(new BranchPermissions("myBranch", permissionsByRole)).when(libraryService).loadBranchPermissions("mySpace", "myProject", "myBranch");

        doReturn(promises.resolve(Arrays.asList(new Branch("master", mock(Path.class)),
                                                new Branch("myBranch", mock(Path.class))))).when(projectController).getUpdatableBranches(any());

        presenter.setup(mock(ProjectScreenModel.class)).then(v -> {
            assertEquals("myBranch", presenter.selectedBranch);
            verify(view).init(presenter);
            verify(view, never()).showEmptyState();
            verify(branchesSelect).setup(any(), any(), any());
            verify(libraryService).loadBranchPermissions("mySpace", "myProject", "myBranch");

            return promises.resolve();
        }).catch_(error -> {
            fail();
            return promises.resolve();
        });
    }

    @Test
    public void validateTest() {
        presenter.validate();

        verify(view).hideError();
        verify(promises).resolve();
    }

    @Test
    public void setBranchTest() {
        presenter.setBranch("otherBranch");

        assertEquals("otherBranch", presenter.selectedBranch);
        verify(presenter).fireChangeEvent();
    }

    @Test
    public void saveTest() {
        presenter.setup("myBranch");
        presenter.save("comment", null);

        verify(libraryService).saveBranchPermissions(eq("mySpace"),
                                                     eq("myProject"),
                                                     eq("myBranch"),
                                                     any());
    }

    private void mockLibraryPlaces() {
        final Branch branch = mock(Branch.class);
        doReturn("myBranch").when(branch).getName();

        final Repository repository = mock(Repository.class);
        doReturn(Arrays.asList(branch)).when(repository).getBranches();
        doReturn("myProject").when(repository).getIdentifier();

        final OrganizationalUnit organizationalUnit = mock(OrganizationalUnit.class);
        final List<Contributor> organizationalUnitContributors = new ArrayList<>();
        doReturn(organizationalUnitContributors).when(organizationalUnit).getContributors();
        doReturn("mySpace").when(organizationalUnit).getName();

        final Space space = mock(Space.class);

        final WorkspaceProject project = mock(WorkspaceProject.class);
        doReturn(repository).when(project).getRepository();
        doReturn(organizationalUnit).when(project).getOrganizationalUnit();
        doReturn(space).when(project).getSpace();
        doReturn(branch).when(project).getBranch();
        doReturn(mock(Module.class)).when(project).getMainModule();

        doReturn(project).when(libraryPlaces).getActiveWorkspace();
        doReturn(organizationalUnit).when(libraryPlaces).getActiveSpace();
    }
}
