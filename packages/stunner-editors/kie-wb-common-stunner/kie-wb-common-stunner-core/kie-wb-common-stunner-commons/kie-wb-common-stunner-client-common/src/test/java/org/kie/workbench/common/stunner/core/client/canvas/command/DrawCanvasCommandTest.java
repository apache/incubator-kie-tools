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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.StreamSupport;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.TestingGraphInstanceBuilder;
import org.kie.workbench.common.stunner.core.TestingGraphMockHandler;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.client.shape.impl.ConnectorShape;
import org.kie.workbench.common.stunner.core.client.shape.view.ShapeView;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.command.impl.CompositeCommand;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.processing.index.Index;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.content.ChildrenTraverseProcessorImpl;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.content.ViewTraverseProcessorImpl;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.tree.TreeWalkTraverseProcessorImpl;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class DrawCanvasCommandTest {

    private static final String SHAPE_SET_ID = "shapeSet";

    private DrawCanvasCommand tested;

    @Mock
    private Shape shape;

    @Mock
    private AbstractCanvasHandler context;

    @Mock
    private Index graphIndex;

    private TestingGraphInstanceBuilder.TestGraph4 graphHolder;

    @Mock
    private Diagram diagram;

    @Mock
    private Metadata metadata;

    @Mock
    private AbstractCanvas canvas;

    @Captor
    private ArgumentCaptor<CompositeCommand.Builder<AbstractCanvasHandler, CanvasViolation>> commandsCapture;

    @Mock
    private ConnectorShape connectorShape;

    @Mock
    private ShapeView shapeView;

    @Before
    public void setUp() throws Exception {
        this.graphHolder = TestingGraphInstanceBuilder.newGraph4(new TestingGraphMockHandler());

        when(context.getGraphIndex()).thenReturn(graphIndex);
        when(graphIndex.getGraph()).thenReturn(graphHolder.graph);
        when(context.getDiagram()).thenReturn(diagram);
        when(diagram.getMetadata()).thenReturn(metadata);
        when(metadata.getShapeSetId()).thenReturn(SHAPE_SET_ID);
        when(context.getAbstractCanvas()).thenReturn(canvas);
        when(context.getCanvas()).thenReturn(canvas);
        StreamSupport
                .stream(graphHolder.graph.nodes().spliterator(), false)
                .forEach(node -> when(canvas.getShape(((Node) node).getUUID())).thenReturn(shape));
        when(canvas.getShape(graphHolder.edge1.getUUID())).thenReturn(connectorShape);
        when(canvas.getShape(graphHolder.edge2.getUUID())).thenReturn(connectorShape);
        when(connectorShape.getShapeView()).thenReturn(shapeView);
        when(shape.getShapeView()).thenReturn(shapeView);

        tested = spy(new DrawCanvasCommand(new ChildrenTraverseProcessorImpl(new TreeWalkTraverseProcessorImpl()),
                                           new ViewTraverseProcessorImpl(new TreeWalkTraverseProcessorImpl())));
    }

    @Test
    public void execute() {
        tested.execute(context);
        verify(tested).executeCommands(eq(context), commandsCapture.capture());
        final CompositeCommand.Builder<AbstractCanvasHandler, CanvasViolation> commands = commandsCapture.getValue();
        assertEquals(commands.size(), 7);

        final List<Command<AbstractCanvasHandler, CanvasViolation>> commandList = new ArrayList<>();
        for (int i = 0; i < commands.size(); i++) {
            Command<AbstractCanvasHandler, CanvasViolation> cmd = commands.get(i);
            commandList.add(cmd);
        }
        assertEquals(commandList.stream().filter(cmd -> cmd instanceof AddCanvasNodeCommand).count(), 1);
        assertEquals(commandList.stream().filter(cmd -> cmd instanceof AddCanvasChildNodeCommand).count(), 3);
        assertEquals(commandList.stream().filter(cmd -> cmd instanceof AddCanvasDockedNodeCommand).count(), 1);
        assertEquals(commandList.stream().filter(cmd -> cmd instanceof AddCanvasConnectorCommand).count(), 2);
    }
}