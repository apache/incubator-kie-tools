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
import java.util.List;
import javax.enterprise.event.Event;

import org.guvnor.common.services.project.model.Module;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.guvnor.structure.contributors.Contributor;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
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
import org.uberfire.client.promise.Promises;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.promise.SyncPromises;
import org.uberfire.spaces.Space;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

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
                                                      roleAccessListPresenter));
        mockLibraryPlaces();
    }

    @Test
    public void setupTest() {
        presenter.setup(mock(ProjectScreenModel.class));

        assertEquals("myBranch", presenter.selectedBranch);
        verify(view).init(presenter);
        verify(branchesSelect).setup(any(), any(), any(), any());
        verify(libraryService).loadBranchPermissions("mySpace", "myProject", "myBranch");
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
