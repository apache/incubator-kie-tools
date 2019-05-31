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

package org.kie.workbench.common.stunner.bpmn.client.editor;

import org.junit.Test;
import org.kie.workbench.common.stunner.bpmn.project.client.editor.BPMNProjectSessionDiagramOpenedHandler;
import org.kie.workbench.common.stunner.core.client.session.impl.ViewerSession;
import org.kie.workbench.common.stunner.project.diagram.ProjectMetadata;
import org.mockito.Mock;
import org.uberfire.backend.vfs.Path;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BPMNProjectSessionDiagramOpenedHandlerTest extends BPMNProjectBaseSessionDiagramHandlerTest<BPMNProjectSessionDiagramOpenedHandler> {

    @Mock
    private Path svgPath;

    @Override
    protected BPMNProjectSessionDiagramOpenedHandler createHandler() {
        return new BPMNProjectSessionDiagramOpenedHandler(bpmnDiagramResourceType,
                                                          diagramService,
                                                          canvasExport,
                                                          notificationEvent,
                                                          translationService);
    }

    @Test
    public void testOnSessionDiagramOpenedForEditorSessionSVGNotExistsSuccessful() {
        when(projectMetadata.getDiagramSVGPath()).thenReturn(null);
        handler.onSessionDiagramOpened(editorSession);
        verifySaveOrUpdateSuccessful();
    }

    @Test
    public void testOnSessionDiagramOpenedForEditorSessionSVGNotExistsWithErrors() {
        when(projectMetadata.getDiagramSVGPath()).thenReturn(null);
        handler.onSessionDiagramOpened(editorSession);
        verifySaveOrUpdateWithErrors();
    }

    @Test
    public void testOnSessionDiagramOpenedForEditorSessionSVGExistsForStunner() {
        when(projectMetadata.getDiagramSVGPath()).thenReturn(svgPath);
        when(projectMetadata.getDiagramSVGGenerator()).thenReturn(ProjectMetadata.SVGGenerator.STUNNER);
        handler.onSessionDiagramOpened(editorSession);
        verifyDoNothing();
    }

    @Test
    public void testOnSessionDiagramOpenedForEditorSessionSVGExistsForJBPMDesignerSuccessful() {
        when(projectMetadata.getDiagramSVGPath()).thenReturn(svgPath);
        when(projectMetadata.getDiagramSVGGenerator()).thenReturn(ProjectMetadata.SVGGenerator.JBPM_DESIGNER);
        handler.onSessionDiagramOpened(editorSession);
        verifySaveOrUpdateSuccessful();
    }

    @Test
    public void testOnSessionDiagramOpenedForEditorSessionSVGExistsForJBPMDesignerWithErrors() {
        when(projectMetadata.getDiagramSVGPath()).thenReturn(svgPath);
        when(projectMetadata.getDiagramSVGGenerator()).thenReturn(ProjectMetadata.SVGGenerator.JBPM_DESIGNER);
        handler.onSessionDiagramOpened(editorSession);
        verifySaveOrUpdateWithErrors();
    }

    @Test
    public void testOnSessionDiagramOpenedSessionForReadonlySession() {
        handler.onSessionDiagramOpened(mock(ViewerSession.class));
        verifyDoNothing();
    }
}
