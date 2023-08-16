/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package org.kie.workbench.common.stunner.core.client.session.command.impl;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.event.registration.CanvasElementsClearEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasClearSelectionEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasSelectionEvent;
import org.kie.workbench.common.stunner.core.client.session.impl.EditorSession;
import org.kie.workbench.common.stunner.core.client.session.impl.ViewerSession;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.mockito.Mock;
import org.uberfire.mvp.Command;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public abstract class BaseSessionCommandKeyboardSelectionAwareTest extends BaseSessionCommandKeyboardTest {

    protected static final String CANVAS_ROOT_ID = "CANVAS_ROOT_ID";

    @Mock
    protected AbstractCanvasHandler canvasHandler;

    @Mock
    protected Diagram diagram;

    @Mock
    protected Metadata metadata;

    @Mock
    protected Command statusCallback;

    @Override
    public void setup() {
        super.setup();
        when(session.getCanvasHandler()).thenReturn(canvasHandler);
        when(canvasHandler.getDiagram()).thenReturn(diagram);
        when(diagram.getMetadata()).thenReturn(metadata);
        when(metadata.getCanvasRootUUID()).thenReturn(CANVAS_ROOT_ID);
        command.listen(statusCallback);
    }

    @Test
    public void testHandleCanvasSelectionEventWhenElementsAreSelected() {
        command.bind(session);

        List<String> selectedIds = new ArrayList<>();
        selectedIds.add("id1");
        selectedIds.add("id2");
        CanvasSelectionEvent selectionEvent = new CanvasSelectionEvent(canvasHandler,
                                                                       selectedIds);
        ((AbstractSelectionAwareSessionCommand) command).handleCanvasSelectionEvent(selectionEvent);
        assertTrue(command.isEnabled());
        verify(statusCallback,
               times(1)).execute();
    }

    @Test
    public void testHandleCanvasSelectionEventWhenCanvasRootIsSelected() {
        command.bind(session);

        List<String> selectedIds = new ArrayList<>();
        selectedIds.add(CANVAS_ROOT_ID);
        CanvasSelectionEvent selectionEvent = new CanvasSelectionEvent(canvasHandler,
                                                                       selectedIds);
        ((AbstractSelectionAwareSessionCommand) command).handleCanvasSelectionEvent(selectionEvent);
        assertFalse(command.isEnabled());
        verify(statusCallback,
               times(1)).execute();
    }

    @Test
    public void testHandleCanvasClearSelectionEvent() {
        command.bind(session);
        ((AbstractSelectionAwareSessionCommand) command).handleCanvasClearSelectionEvent(new CanvasClearSelectionEvent(canvasHandler));
        assertFalse(command.isEnabled());
        verify(statusCallback,
               times(1)).execute();
    }

    @Test
    public void testHandleCanvasElementsClearEvent() {
        command.bind(session);
        ((AbstractSelectionAwareSessionCommand) command).handleCanvasElementsClearEvent(new CanvasElementsClearEvent(canvasHandler));
        assertFalse(command.isEnabled());
        verify(statusCallback,
               times(1)).execute();
    }

    @Test
    public void testAcceptsSession() {
        assertTrue(command.accepts(mock(EditorSession.class)));
        assertFalse(command.accepts(mock(ViewerSession.class)));
    }
}
