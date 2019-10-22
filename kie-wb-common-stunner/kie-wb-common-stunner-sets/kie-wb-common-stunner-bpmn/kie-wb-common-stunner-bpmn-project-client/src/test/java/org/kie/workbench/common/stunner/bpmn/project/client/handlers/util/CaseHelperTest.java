/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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
 */

package org.kie.workbench.common.stunner.bpmn.project.client.handlers.util;

import java.util.Optional;

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.google.gwt.core.client.Callback;
import org.guvnor.common.services.project.client.context.WorkspaceProjectContext;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.project.service.BPMNDiagramService;
import org.kie.workbench.common.stunner.bpmn.service.ProjectType;
import org.mockito.Mock;
import org.uberfire.backend.vfs.Path;
import org.uberfire.mocks.CallerMock;

import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class CaseHelperTest {

    @Mock
    private BPMNDiagramService bpmnDiagramService;

    @Mock
    private WorkspaceProjectContext projectContext;

    @Mock
    private WorkspaceProject workspaceProject;

    @Mock
    private Path rootPath;

    @Mock
    private Callback<Boolean, Void> callback;

    private CaseHelper tested;

    @Before
    public void setUp() throws Exception {
        when(projectContext.getActiveWorkspaceProject()).thenReturn(Optional.of(workspaceProject));
        when(workspaceProject.getRootPath()).thenReturn(rootPath);

        tested = new CaseHelper(new CallerMock<>(bpmnDiagramService), projectContext);
    }

    @Test
    public void acceptContext_true() {
        when(bpmnDiagramService.getProjectType(rootPath)).thenReturn(ProjectType.CASE);

        tested.acceptContext(callback);

        verify(bpmnDiagramService).getProjectType(rootPath);
        verify(callback).onSuccess(true);
    }

    @Test
    public void acceptContext_false() {
        when(bpmnDiagramService.getProjectType(rootPath)).thenReturn(ProjectType.BPMN);

        tested.acceptContext(callback);

        verify(bpmnDiagramService).getProjectType(rootPath);
        verify(callback, never()).onSuccess(anyBoolean());
    }
}