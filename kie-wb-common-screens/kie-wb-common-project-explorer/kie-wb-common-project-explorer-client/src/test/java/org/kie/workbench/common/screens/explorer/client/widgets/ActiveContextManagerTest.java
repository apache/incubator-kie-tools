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

package org.kie.workbench.common.screens.explorer.client.widgets;

import org.guvnor.common.services.project.model.WorkspaceProject;
import org.guvnor.structure.repositories.Repository;
import org.jboss.errai.common.client.api.Caller;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.explorer.service.ExplorerService;
import org.kie.workbench.common.screens.explorer.service.ProjectExplorerContentQuery;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.mocks.CallerMock;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ActiveContextManagerTest {

    @Mock
    private View view;

    @Mock
    private ActiveContextItems activeContextItems;

    @Mock
    private ActiveContextOptions activeOptions;

    @Mock
    private ExplorerService explorerService;
    private Caller<ExplorerService> explorerServiceCaller;

    @Captor
    private ArgumentCaptor<ProjectExplorerContentQuery> queryCaptor;

    private ActiveContextManager activeContextManager;

    @Before
    public void setup() {
        explorerServiceCaller = new CallerMock<>(explorerService);
        activeContextManager = new ActiveContextManager(activeContextItems,
                                                        activeOptions,
                                                        explorerServiceCaller);
        activeContextManager.init(view,
                                  (content) -> {});
    }

    @Test
    public void refreshWithActiveProject() {
        final WorkspaceProject project = mock(WorkspaceProject.class);
        final Repository repository = mock(Repository.class);
        doReturn(repository).when(project).getRepository();
        doReturn(project).when(activeContextItems).getActiveProject();

        activeContextManager.refresh();

        verify(explorerService).getContent(queryCaptor.capture());
        assertSame(repository,
                   queryCaptor.getValue().getRepository());
    }

    @Test
    public void refreshWithoutActiveProject() {
        doReturn(null).when(activeContextItems).getActiveProject();

        activeContextManager.refresh();

        verify(explorerService,
               never()).getContent(any());
    }
}
