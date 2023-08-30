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

import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.TestingGraphInstanceBuilder;
import org.kie.workbench.common.stunner.core.TestingGraphMockHandler;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class UpdateDockNodeCommandTest {

    public static final String LANE_UUID = "lane1";
    public static final String DOCK_UUID = "dock1";
    public static final String SHAPE_SET_ID = "shapeSetId";

    @Mock
    private AbstractCanvasHandler canvasHandler;
    @Mock
    private AbstractCanvas canvas;
    @Mock
    private Diagram diagram;
    @Mock
    private Metadata metadata;

    private TestingGraphMockHandler graphHandler;
    private TestingGraphInstanceBuilder.TestGraph2 graphHolder;
    private UpdateDockNodeCommand tested;
    private Node<View<?>, Edge> dockNode;
    private Node<View<?>, Edge> laneNode;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() throws Exception {
        this.graphHandler = new TestingGraphMockHandler();
        this.graphHolder = TestingGraphInstanceBuilder.newGraph2(graphHandler);
        this.laneNode = graphHandler.newViewNode(LANE_UUID,
                                                 Optional.empty(),
                                                 50,
                                                 59,
                                                 500,
                                                 500);
        graphHandler.setChild(graphHolder.parentNode,
                              laneNode);
        graphHandler.removeChild(graphHolder.parentNode,
                                 graphHolder.endNode);
        graphHandler.setChild(laneNode,
                              graphHolder.endNode);
        this.dockNode = graphHandler.newViewNode(DOCK_UUID,
                                                 Optional.empty(),
                                                 0,
                                                 0,
                                                 15,
                                                 15);
        graphHandler.setChild(graphHolder.parentNode,
                              dockNode);
        when(canvasHandler.getDiagram()).thenReturn(diagram);
        when(canvasHandler.getCanvas()).thenReturn(canvas);
        when(canvasHandler.getGraphIndex()).thenReturn(graphHandler.graphIndex);
        when(diagram.getMetadata()).thenReturn(metadata);
        when(diagram.getGraph()).thenReturn(graphHandler.graph);
        when(metadata.getDefinitionSetId()).thenReturn(TestingGraphMockHandler.DEF_SET_ID);
        when(metadata.getShapeSetId()).thenReturn(SHAPE_SET_ID);
        when(metadata.getCanvasRootUUID()).thenReturn(graphHolder.parentNode.getUUID());
        this.tested = new UpdateDockNodeCommand(graphHolder.endNode,
                                                dockNode);
    }

    @Test
    public void testDock() {
        graphHandler.dockTo(graphHolder.startNode,
                            dockNode);
        this.tested = new UpdateDockNodeCommand(graphHolder.intermNode,
                                                dockNode);
        CommandResult<CanvasViolation> result = tested.allow(canvasHandler);
        final List<Command<AbstractCanvasHandler, CanvasViolation>> commands = tested.getCommands();
        assertTrue(2 == commands.size());
        final UnDockNodeCommand c1 = (UnDockNodeCommand) commands.get(0);
        assertNotNull(c1);
        assertEquals(graphHolder.startNode,
                     c1.getParent());
        assertEquals(dockNode,
                     c1.getCandidate());
        final DockNodeCommand c2 = (DockNodeCommand) commands.get(1);
        assertNotNull(c2);
        assertEquals(graphHolder.intermNode,
                     c2.getParent());
        assertEquals(dockNode,
                     c2.getCandidate());
        assertEquals(CommandResult.Type.INFO,
                     result.getType());
    }

    @Test
    public void testDockUsingDifferentParents() {
        this.tested = new UpdateDockNodeCommand(graphHolder.endNode,
                                                dockNode);
        CommandResult<CanvasViolation> result = tested.allow(canvasHandler);
        final List<Command<AbstractCanvasHandler, CanvasViolation>> commands = tested.getCommands();
        assertEquals(commands.size(), 2);
        final UpdateChildrenCommand c2 = (UpdateChildrenCommand) commands.get(0);
        assertNotNull(c2);
        assertEquals(laneNode,
                     c2.getParent());
        assertEquals(dockNode,
                     c2.getCandidates().iterator().next());
        final DockNodeCommand c6 = (DockNodeCommand) commands.get(1);
        assertNotNull(c6);
        assertEquals(graphHolder.endNode,
                     c6.getParent());
        assertEquals(dockNode,
                     c6.getCandidate());
        assertEquals(CommandResult.Type.INFO,
                     result.getType());
    }
}
