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


package org.kie.workbench.common.stunner.sw.client.editor;

import java.util.Collections;

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.appformer.kogito.bridge.client.diagramApi.DiagramApi;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasSelectionEvent;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.sw.definition.State;
import org.mockito.Mock;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class ShapeEventsHandlerTest {

    private final String NODE_NAME = "TEST";

    private ShapeEventsHandler tested = new ShapeEventsHandler();

    @Mock
    private AbstractCanvasHandler canvasHandler;

    @Mock
    private DiagramApi diagramApi;

    @Mock
    private Diagram diagram;

    @Mock
    Graph graph;

    @Mock
    Node node;

    @Mock
    View view;

    State state = new State();

    @Before
    public void init() {
        when(canvasHandler.getDiagram()).thenReturn(diagram);
        when(diagram.getGraph()).thenReturn(graph);

        when(node.getContent()).thenReturn(view);
        when(view.getDefinition()).thenReturn(state);
        state.setName(NODE_NAME);
    }

    @Test
    public void nullCanvasHandlerTest() {
        tested.setDiagramApi(diagramApi);
        CanvasSelectionEvent selectionEvent = new CanvasSelectionEvent(null, "TEST");

        tested.onCanvasSelectionEvent(selectionEvent);

        verify(diagramApi, times(0)).onNodeSelected(any());
    }

    @Test
    public void noIdentifiersToSelectTest() {
        tested.setDiagramApi(diagramApi);
        CanvasSelectionEvent selectionEvent = new CanvasSelectionEvent(canvasHandler, Collections.emptyList());

        tested.onCanvasSelectionEvent(selectionEvent);

        verify(diagramApi, times(0)).onNodeSelected(any());
    }

    @Test
    public void identifierNotFoundTest() {
        tested.setDiagramApi(diagramApi);
        CanvasSelectionEvent selectionEvent = new CanvasSelectionEvent(canvasHandler, "TEST");

        tested.onCanvasSelectionEvent(selectionEvent);

        verify(diagramApi).onNodeSelected(isNull());
    }

    @Test
    public void identifierFoundTest() {
        when(graph.getNode(NODE_NAME)).thenReturn(node);

        tested.setDiagramApi(diagramApi);
        CanvasSelectionEvent selectionEvent = new CanvasSelectionEvent(canvasHandler, NODE_NAME);

        tested.onCanvasSelectionEvent(selectionEvent);

        verify(diagramApi).onNodeSelected(NODE_NAME);
    }
}
