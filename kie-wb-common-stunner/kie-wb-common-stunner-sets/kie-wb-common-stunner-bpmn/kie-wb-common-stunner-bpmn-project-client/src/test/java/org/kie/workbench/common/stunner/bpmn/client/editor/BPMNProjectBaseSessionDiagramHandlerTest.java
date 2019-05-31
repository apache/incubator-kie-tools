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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.project.client.editor.BPMNProjectBaseSessionDiagramHandler;
import org.kie.workbench.common.stunner.bpmn.project.client.type.BPMNDiagramResourceType;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.SelectionControl;
import org.kie.workbench.common.stunner.core.client.canvas.util.CanvasFileExport;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
import org.kie.workbench.common.stunner.core.client.service.ClientDiagramService;
import org.kie.workbench.common.stunner.core.client.service.ClientRuntimeError;
import org.kie.workbench.common.stunner.core.client.service.ServiceCallback;
import org.kie.workbench.common.stunner.core.client.session.impl.EditorSession;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.project.diagram.ProjectDiagram;
import org.kie.workbench.common.stunner.project.diagram.ProjectMetadata;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.workbench.events.NotificationEvent;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.kie.workbench.common.stunner.bpmn.project.client.resources.BPMNClientConstants.EditorGenerateSvgFileError;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public abstract class BPMNProjectBaseSessionDiagramHandlerTest<H extends BPMNProjectBaseSessionDiagramHandler> {

    private static final String SVG = "SVG";

    @Mock
    protected BPMNDiagramResourceType bpmnDiagramResourceType;

    @Mock
    protected ClientDiagramService diagramService;

    @Mock
    protected CanvasFileExport canvasExport;

    @Mock
    protected EventSourceMock<NotificationEvent> notificationEvent;

    @Mock
    protected ClientTranslationService translationService;

    @Mock
    protected ProjectDiagram projectDiagram;

    @Mock
    protected ProjectMetadata projectMetadata;

    @Mock
    protected Path path;

    @Mock
    protected EditorSession editorSession;

    @Mock
    protected AbstractCanvasHandler canvasHandler;

    @Mock
    protected SelectionControl selectionControl;

    @Captor
    protected ArgumentCaptor<ServiceCallback<Path>> serviceCallbackCaptor;

    protected H handler;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() {
        when(projectDiagram.getMetadata()).thenReturn(projectMetadata);
        when(projectMetadata.getPath()).thenReturn(path);
        when(editorSession.getCanvasHandler()).thenReturn(canvasHandler);
        when(canvasHandler.getDiagram()).thenReturn(projectDiagram);
        when(editorSession.getSelectionControl()).thenReturn(selectionControl);
        when(canvasExport.exportToSvg(canvasHandler)).thenReturn(SVG);
        handler = createHandler();
    }

    protected abstract H createHandler();

    @Test
    public void testAcceptsProjectDiagramBPMNResource() {
        when(bpmnDiagramResourceType.accept(path)).thenReturn(true);
        assertTrue(handler.accepts(projectDiagram));
    }

    @Test
    public void testAcceptsProjectDiagramNonBPMNResource() {
        when(bpmnDiagramResourceType.accept(path)).thenReturn(false);
        assertFalse(handler.accepts(projectDiagram));
    }

    @Test
    public void testAcceptsNonProjectDiagram() {
        assertFalse(handler.accepts(mock(Diagram.class)));
    }

    protected void verifySaveOrUpdateSuccessful() {
        verify(selectionControl).clearSelection();
        verify(diagramService).saveOrUpdateSvg(eq(path), eq(SVG), serviceCallbackCaptor.capture());
        serviceCallbackCaptor.getValue().onSuccess(path);
    }

    protected void verifySaveOrUpdateWithErrors() {
        verify(selectionControl).clearSelection();
        when(translationService.getValue(EditorGenerateSvgFileError)).thenReturn(EditorGenerateSvgFileError);
        verify(diagramService).saveOrUpdateSvg(eq(path), eq(SVG), serviceCallbackCaptor.capture());
        serviceCallbackCaptor.getValue().onError(new ClientRuntimeError("some error"));
        verify(notificationEvent).fire(new NotificationEvent(EditorGenerateSvgFileError, NotificationEvent.NotificationType.ERROR));
    }

    @SuppressWarnings("unchecked")
    protected void verifyDoNothing() {
        verify(selectionControl, never()).clearSelection();
        verify(canvasExport, never()).exportToSvg(any(AbstractCanvasHandler.class));
        verify(diagramService, never()).saveOrUpdateSvg(any(Path.class), any(String.class), any(ServiceCallback.class));
    }
}
