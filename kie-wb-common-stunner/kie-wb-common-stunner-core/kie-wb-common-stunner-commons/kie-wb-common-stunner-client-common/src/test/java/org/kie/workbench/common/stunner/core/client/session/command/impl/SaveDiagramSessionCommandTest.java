/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.core.client.session.command.impl;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.select.SelectionControl;
import org.kie.workbench.common.stunner.core.client.canvas.util.CanvasFileExport;
import org.kie.workbench.common.stunner.core.client.service.ClientDiagramService;
import org.kie.workbench.common.stunner.core.client.service.ServiceCallback;
import org.kie.workbench.common.stunner.core.client.session.command.event.SaveDiagramSessionCommandExecutedEvent;
import org.kie.workbench.common.stunner.core.client.session.impl.EditorSession;
import org.kie.workbench.common.stunner.core.client.session.impl.ViewerSession;
import org.kie.workbench.common.stunner.core.client.util.TimerUtils;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.util.UUID;
import org.mockito.Mock;
import org.uberfire.backend.vfs.Path;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class SaveDiagramSessionCommandTest {

    private static final String DIAGRAM_UUID = UUID.uuid();
    private static final String RAW_DIAGRAM = "";

    private SaveDiagramSessionCommand command;

    @Mock
    private ClientDiagramService clientDiagramService;

    @Mock
    protected CanvasFileExport canvasFileExport;

    @Mock
    protected EditorSession session;

    @Mock
    protected AbstractCanvasHandler canvasHandler;

    private SaveDiagramSessionCommandExecutedEvent saveDiagramSessionCommandExecutedEvent;

    @Mock
    private Diagram diagram;

    @Mock
    private Metadata metadata;

    @Mock
    private Path path;

    @Mock
    private SelectionControl selectionControl;

    @Before
    public void setUp() throws Exception {
        when(session.getCanvasHandler()).thenReturn(canvasHandler);
        when(canvasHandler.getDiagram()).thenReturn(diagram);
        when(diagram.getMetadata()).thenReturn(metadata);
        when(metadata.getPath()).thenReturn(path);
        when(metadata.getCanvasRootUUID()).thenReturn(DIAGRAM_UUID);
        when(session.getSelectionControl()).thenReturn(selectionControl);
        when(canvasFileExport.exportToSvg(canvasHandler)).thenReturn(RAW_DIAGRAM);

        saveDiagramSessionCommandExecutedEvent = new SaveDiagramSessionCommandExecutedEvent(DIAGRAM_UUID);
        command = new SaveDiagramSessionCommand(clientDiagramService, canvasFileExport);
        command.bind(session);

        command.setTimer(new TimerUtils() {
            @Override
            public void executeWithDelay(Runnable executeFunction, int delayMillis) {
                executeFunction.run();
            }
        });
    }

    @Test
    public void onSaveDiagram() {
        command.onSaveDiagram(saveDiagramSessionCommandExecutedEvent);
        verify(selectionControl).clearSelection();
        verify(clientDiagramService).saveOrUpdateSvg(eq(path), eq(RAW_DIAGRAM), any(ServiceCallback.class));
    }

    @Test
    public void testAcceptsSession() {
        assertTrue(command.accepts(mock(EditorSession.class)));
        assertFalse(command.accepts(mock(ViewerSession.class)));
    }
}