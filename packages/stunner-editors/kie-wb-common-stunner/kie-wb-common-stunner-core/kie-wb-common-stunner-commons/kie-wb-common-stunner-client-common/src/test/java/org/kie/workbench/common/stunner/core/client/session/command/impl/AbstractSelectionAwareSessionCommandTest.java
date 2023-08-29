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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.event.registration.CanvasElementsClearEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasClearSelectionEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasSelectionEvent;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AbstractSelectionAwareSessionCommandTest {

    private static final String CANVAS_ROOT_ID = "CANVAS_ROOT_ID";

    private AbstractSelectionAwareSessionCommand command;

    @Mock
    private ClientSession clientSession;

    @Mock
    private AbstractCanvasHandler canvasHandler;

    @Mock
    private AbstractCanvasHandler anotherCanvasHandler;

    @Mock
    private Diagram diagram;

    @Mock
    private Metadata metadata;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() {
        when(clientSession.getCanvasHandler()).thenReturn(canvasHandler);
        when(canvasHandler.getDiagram()).thenReturn(diagram);
        when(diagram.getMetadata()).thenReturn(metadata);
        when(metadata.getCanvasRootUUID()).thenReturn(CANVAS_ROOT_ID);

        command = spy(new AbstractSelectionAwareSessionCommand(true) {
            @Override
            protected void handleCanvasSelectionEvent(CanvasSelectionEvent event) {

            }

            @Override
            protected void handleCanvasClearSelectionEvent(CanvasClearSelectionEvent event) {

            }

            @Override
            protected void handleCanvasElementsClearEvent(CanvasElementsClearEvent event) {

            }

            @Override
            public void execute(Callback callback) {

            }

            @Override
            public boolean accepts(ClientSession session) {
                return true;
            }
        });
        command.bind(clientSession);
    }

    @Test
    public void testOnCanvasSelection() {
        CanvasSelectionEvent event = new CanvasSelectionEvent(canvasHandler,
                                                              "");

        command.onCanvasSelectionEvent(event);
        verify(command,
               times(1)).handleCanvasSelectionEvent(event);

        event = new CanvasSelectionEvent(anotherCanvasHandler,
                                         "");
        command.onCanvasSelectionEvent(event);
        verify(command,
               never()).handleCanvasSelectionEvent(event);
    }

    @Test
    public void testOnCanvasClearSelection() {
        CanvasClearSelectionEvent event = new CanvasClearSelectionEvent(canvasHandler);
        command.onCanvasClearSelectionEvent(event);
        verify(command,
               times(1)).handleCanvasClearSelectionEvent(event);

        event = new CanvasClearSelectionEvent(anotherCanvasHandler);
        command.onCanvasClearSelectionEvent(event);
        verify(command,
               never()).handleCanvasClearSelectionEvent(event);
    }

    @Test
    public void testOnCanvasElementsClearEvent() {
        CanvasElementsClearEvent event = new CanvasElementsClearEvent(canvasHandler);
        command.onCanvasElementsClearEvent(event);
        verify(command,
               times(1)).handleCanvasElementsClearEvent(event);

        event = new CanvasElementsClearEvent(anotherCanvasHandler);
        command.onCanvasElementsClearEvent(event);
        verify(command,
               never()).handleCanvasElementsClearEvent(event);
    }

    @Test
    public void testOnlyCanvasRootSelected() {
        List<String> selectedNodes = new ArrayList<>();
        assertFalse(command.onlyCanvasRootSelected(new CanvasSelectionEvent(canvasHandler,
                                                                            selectedNodes)));
        selectedNodes.clear();
        selectedNodes.add("one");
        selectedNodes.add("two");
        assertFalse(command.onlyCanvasRootSelected(new CanvasSelectionEvent(canvasHandler,
                                                                            selectedNodes)));
        selectedNodes.clear();
        selectedNodes.add("one");
        selectedNodes.add(CANVAS_ROOT_ID);
        assertFalse(command.onlyCanvasRootSelected(new CanvasSelectionEvent(canvasHandler,
                                                                            selectedNodes)));

        selectedNodes.clear();
        selectedNodes.add(CANVAS_ROOT_ID);
        assertTrue(command.onlyCanvasRootSelected(new CanvasSelectionEvent(canvasHandler,
                                                                           selectedNodes)));
    }
}
