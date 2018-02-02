/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.kie.workbench.common.screens.explorer.client;

import java.util.Optional;

import org.guvnor.common.services.project.client.context.WorkspaceProjectContext;
import org.guvnor.common.services.project.model.Module;
import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.guvnor.structure.repositories.Branch;
import org.guvnor.structure.repositories.Repository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.explorer.client.widgets.ActiveContextOptions;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;
import org.uberfire.mvp.Command;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ExplorerMenuTest {

    @Mock
    private ExplorerMenuView view;

    @Mock
    private ActiveContextOptions activeOptions;

    @Mock
    private Command refreshCommand;

    @Mock
    private Command updateCommand;

    @Mock
    private WorkspaceProjectContext context;

    private ExplorerMenu menu;

    @Before
    public void setUp() throws Exception {
        menu = new ExplorerMenu(view,
                                activeOptions,
                                context);

        menu.addRefreshCommand(refreshCommand);
        menu.addUpdateCommand(updateCommand);
    }

    @Test
    public void testOnRefresh() throws Exception {
        menu.onRefresh();

        verify(refreshCommand).execute();
    }

    @Test
    public void testRefreshVersion1() throws Exception {

        when(activeOptions.isTechnicalViewActive()).thenReturn(true);
        when(activeOptions.isTreeNavigatorVisible()).thenReturn(true);
        when(activeOptions.canShowTag()).thenReturn(true);

        menu.refresh();

        verify(view).showTreeNav();
        verify(view).showTechViewIcon();
        verify(view).hideBusinessViewIcon();
        verify(view).showTagFilterIcon();

        verify(view,
               never()).showBreadcrumbNav();
        verify(view,
               never()).showBusinessViewIcon();
        verify(view,
               never()).hideTechViewIcon();
        verify(view,
               never()).hideTagFilterIcon();
    }

    @Test
    public void testRefreshVersion2() throws Exception {

        when(activeOptions.isTechnicalViewActive()).thenReturn(false);
        when(activeOptions.isTreeNavigatorVisible()).thenReturn(false);
        when(activeOptions.canShowTag()).thenReturn(false);

        menu.refresh();

        verify(view,
               never()).showTreeNav();
        verify(view,
               never()).showTechViewIcon();
        verify(view,
               never()).hideBusinessViewIcon();
        verify(view,
               never()).showTagFilterIcon();

        verify(view).showBreadcrumbNav();
        verify(view).showBusinessViewIcon();
        verify(view).hideTechViewIcon();
        verify(view).hideTagFilterIcon();
    }

    @Test
    public void testOnBreadCrumbExplorerSelected() throws Exception {
        menu.onBreadCrumbExplorerSelected();

        verify(activeOptions).activateBreadCrumbNavigation();
        verify(updateCommand).execute();
    }

    @Test
    public void testOnTreeExplorerSelected() throws Exception {
        menu.onTreeExplorerSelected();

        verify(activeOptions).activateTreeViewNavigation();
        verify(updateCommand).execute();
    }

    @Test
    public void testOnShowTagFilterSelectedOn() throws Exception {
        when(activeOptions.canShowTag()).thenReturn(false);

        menu.onShowTagFilterSelected();

        verify(activeOptions).activateTagFiltering();
        verify(updateCommand).execute();
    }

    @Test
    public void testOnShowTagFilterSelectedOff() throws Exception {
        when(activeOptions.canShowTag()).thenReturn(true);

        menu.onShowTagFilterSelected();

        verify(activeOptions).disableTagFiltering();
        verify(updateCommand).execute();
    }

    @Test
    public void testOnArchiveActiveModule() throws Exception {
        final Path rootModulePath = mock(Path.class);
        final POM pom = mock(POM.class);
        when(pom.getName()).thenReturn("my module");

        final Module module = new Module(rootModulePath,
                                         mock(Path.class),
                                         pom);
        when(context.getActiveModule()).thenReturn(Optional.of(module));

        menu.onArchiveActiveProject();

        verify(view).archive(rootModulePath);
    }

    @Test
    public void testOnArchiveActiveRepo() throws Exception {
        final Path rootRepoPath = mock(Path.class);
        final Repository repository = mock(Repository.class);
        when(repository.getDefaultBranch()).thenReturn(Optional.of(new Branch("master", rootRepoPath)));
        final WorkspaceProject project = mock(WorkspaceProject.class);
        when(project.getRepository()).thenReturn(repository);
        when(context.getActiveWorkspaceProject()).thenReturn(Optional.of(project));

        menu.onArchiveActiveRepository();

        verify(view).archive(rootRepoPath);
    }
}