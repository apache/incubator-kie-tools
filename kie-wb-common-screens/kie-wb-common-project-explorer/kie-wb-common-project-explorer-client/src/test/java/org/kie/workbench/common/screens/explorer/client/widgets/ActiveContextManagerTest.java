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

import javax.enterprise.event.Event;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.guvnor.common.services.project.context.WorkspaceProjectContextChangeEvent;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.guvnor.structure.repositories.Repository;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.explorer.model.ProjectExplorerContent;
import org.kie.workbench.common.screens.explorer.service.ActiveOptions;
import org.kie.workbench.common.screens.explorer.service.ExplorerService;
import org.kie.workbench.common.screens.explorer.service.ProjectExplorerContentQuery;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mocks.EventSourceMock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
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

    @Captor
    private ArgumentCaptor<WorkspaceProjectContextChangeEvent> eventArgumentCaptor;

    private ActiveContextManager activeContextManager;
    private Event<WorkspaceProjectContextChangeEvent> contextChangeEvent;
    private RemoteCallback<ProjectExplorerContent> contentCallback;

    @Before
    public void setup() {
        explorerServiceCaller = new CallerMock<>(explorerService);
        this.contentCallback = mock(RemoteCallback.class);
        this.contextChangeEvent = mock(EventSourceMock.class);
        activeContextManager = new ActiveContextManager(activeContextItems,
                                                        activeOptions,
                                                        explorerServiceCaller,
                                                        contextChangeEvent);
        activeContextManager.init(view,
                                  contentCallback);
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

    @Test
    public void initActiveContextWithPathString() throws Exception {

        final WorkspaceProject workspaceProject = mock(WorkspaceProject.class);
        doReturn(workspaceProject).when(explorerService).resolveProject("path");

        final ProjectExplorerContent projectExplorerContent = mock(ProjectExplorerContent.class);
        doReturn(projectExplorerContent).when(explorerService).getContent(eq("path"),
                                                                          any(ActiveOptions.class));

        activeContextManager.initActiveContext("path");

        verify(view).showBusyIndicator("Loading");

        verify(contextChangeEvent).fire(eventArgumentCaptor.capture());
        assertEquals(workspaceProject, eventArgumentCaptor.getValue().getWorkspaceProject());

        verify(contentCallback).callback(projectExplorerContent);
    }
}
