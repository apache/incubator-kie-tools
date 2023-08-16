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


package org.kie.workbench.common.stunner.core.client.canvas.command;

import java.util.stream.StreamSupport;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.TestingGraphInstanceBuilder;
import org.kie.workbench.common.stunner.core.TestingGraphMockHandler;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.client.shape.EdgeShape;
import org.kie.workbench.common.stunner.core.client.shape.MutationContext;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.client.shape.view.ShapeView;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.command.util.CommandUtils;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewConnector;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertFalse;
import static org.mockito.ArgumentMatchers.anyObject;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class MorphCanvasNodeCommandTest extends AbstractCanvasCommandTest {

    static final String NEW_SHAPE_SET_ID = "ssid2";

    @Mock
    private ViewConnector viewConnector;

    @Mock
    private EdgeShape edge1Shape;

    @Mock
    private ShapeView edge1ShapeView;

    @Mock
    private EdgeShape edge2Shape;

    @Mock
    private ShapeView edge2ShapeView;

    private MorphCanvasNodeCommand tested;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        when(edge1Shape.getShapeView()).thenReturn(edge1ShapeView);
        when(edge2Shape.getShapeView()).thenReturn(edge2ShapeView);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testMorphDockedNode() {
        TestingGraphInstanceBuilder.TestGraph4 graphInstance = mockGraph4();
        this.tested = new MorphCanvasNodeCommand(graphInstance.dockedNode, NEW_SHAPE_SET_ID);
        CommandResult<CanvasViolation> result = tested.execute(canvasHandler);
        assertFalse(CommandUtils.isError(result));
        verify(canvasHandler, times(1)).deregister(graphInstance.dockedNode);
        verify(canvasHandler, times(1)).register(NEW_SHAPE_SET_ID, graphInstance.dockedNode);
        verify(canvasHandler, times(1)).undock(graphInstance.intermNode, graphInstance.dockedNode);
        verify(canvasHandler, times(1)).dock(graphInstance.intermNode, graphInstance.dockedNode);
        verify(canvasHandler, times(1)).applyElementMutation(graphInstance.dockedNode, MutationContext.STATIC);
        verify(canvasHandler, never()).removeChild(anyObject(), anyObject());
        verify(canvasHandler, never()).addChild(anyObject(), anyObject());
        verify(edge1ShapeView, never()).moveToTop();
        verify(edge2ShapeView, never()).moveToTop();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testMorphSomeIntermediateNode() {
        TestingGraphInstanceBuilder.TestGraph4 graphInstance = mockGraph4();
        this.tested = new MorphCanvasNodeCommand(graphInstance.intermNode, NEW_SHAPE_SET_ID);
        CommandResult<CanvasViolation> result = tested.execute(canvasHandler);
        assertFalse(CommandUtils.isError(result));
        verify(canvasHandler, times(1)).undock(eq(graphInstance.intermNode), eq(graphInstance.dockedNode));
        verify(canvasHandler, times(1)).removeChild(graphInstance.parentNode, graphInstance.intermNode);
        verify(canvasHandler, times(1)).deregister(graphInstance.intermNode);
        verify(canvasHandler, times(1)).register(NEW_SHAPE_SET_ID, graphInstance.intermNode);
        verify(canvasHandler, times(1)).addChild(graphInstance.parentNode, graphInstance.intermNode);
        verify(canvasHandler, times(1)).dock(eq(graphInstance.intermNode), eq(graphInstance.dockedNode));
        verify(canvasHandler, times(1)).applyElementMutation(graphInstance.intermNode, MutationContext.STATIC);
        verify(edge1ShapeView, times(1)).moveToTop();
        verify(edge2ShapeView, times(1)).moveToTop();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testMorphSomeContainerNode() {
        TestingGraphInstanceBuilder.TestGraph3 graphInstance = mockGraph3();
        this.tested = new MorphCanvasNodeCommand(graphInstance.containerNode, NEW_SHAPE_SET_ID);
        CommandResult<CanvasViolation> result = tested.execute(canvasHandler);
        assertFalse(CommandUtils.isError(result));
        verify(canvasHandler, times(1)).deregister(graphInstance.containerNode);
        verify(canvasHandler, times(1)).removeChild(graphInstance.parentNode, graphInstance.containerNode);
        verify(canvasHandler, times(1)).removeChild(graphInstance.containerNode, graphInstance.startNode);
        verify(canvasHandler, times(1)).removeChild(graphInstance.containerNode, graphInstance.intermNode);
        verify(canvasHandler, times(1)).removeChild(graphInstance.containerNode, graphInstance.endNode);
        verify(canvasHandler, times(1)).register(NEW_SHAPE_SET_ID, graphInstance.containerNode);
        verify(canvasHandler, times(1)).addChild(graphInstance.parentNode, graphInstance.containerNode);
        verify(canvasHandler, times(1)).addChild(graphInstance.containerNode, graphInstance.startNode);
        verify(canvasHandler, times(1)).addChild(graphInstance.containerNode, graphInstance.intermNode);
        verify(canvasHandler, times(1)).addChild(graphInstance.containerNode, graphInstance.endNode);
        verify(canvasHandler, times(1)).applyElementMutation(graphInstance.containerNode, MutationContext.STATIC);
        verify(edge1ShapeView, times(1)).moveToTop();
        verify(edge2ShapeView, times(1)).moveToTop();
    }

    private TestingGraphInstanceBuilder.TestGraph3 mockGraph3() {
        TestingGraphInstanceBuilder.TestGraph3 graph3 = TestingGraphInstanceBuilder.newGraph3(new TestingGraphMockHandler());
        when(canvas.getShape(eq(graph3.edge1.getUUID()))).thenReturn(edge1Shape);
        when(canvas.getShape(eq(graph3.edge2.getUUID()))).thenReturn(edge2Shape);
        return mockGraph(graph3);
    }

    private TestingGraphInstanceBuilder.TestGraph4 mockGraph4() {
        TestingGraphInstanceBuilder.TestGraph4 graph4 = TestingGraphInstanceBuilder.newGraph4(new TestingGraphMockHandler());
        when(canvas.getShape(eq(graph4.edge1.getUUID()))).thenReturn(edge1Shape);
        when(canvas.getShape(eq(graph4.edge2.getUUID()))).thenReturn(edge2Shape);
        return mockGraph(graph4);
    }

    @SuppressWarnings("unchecked")
    private <T extends TestingGraphInstanceBuilder.TestGraph> T mockGraph(T graph) {
        when(diagram.getGraph()).thenReturn(graph.graph);
        when(graphIndex.getGraph()).thenReturn(graph.graph);
        //mocking shapes
        StreamSupport.<Node>stream(graph.graph.nodes().spliterator(), true)
                .map(node -> ((Node) node).getUUID())
                .forEach(uuid -> when(canvas.getShape(eq((String) uuid))).thenReturn(mock(Shape.class)));
        return graph;
    }
}