/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UpdateChildNodeCommandTest {

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

    private TestingGraphInstanceBuilder.TestGraph2 graphHolder;
    private UpdateChildNodeCommand tested;
    private Node<View<?>, Edge> dockNode;
    private Node<View<?>, Edge> laneNode;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() throws Exception {
        TestingGraphMockHandler graphHandler = new TestingGraphMockHandler();
        this.graphHolder = TestingGraphInstanceBuilder.newGraph2(graphHandler);
        this.laneNode = graphHandler.newViewNode(LANE_UUID,
                                                 Optional.empty(),
                                                 50,
                                                 59,
                                                 500,
                                                 500);
        graphHandler.setChild(graphHolder.parentNode,
                              laneNode);
        this.dockNode = graphHandler.newViewNode(DOCK_UUID,
                                                 Optional.empty(),
                                                 0,
                                                 0,
                                                 15,
                                                 15);
        graphHandler.setChild(graphHolder.parentNode,
                              dockNode);
        graphHandler.dockTo(graphHolder.endNode,
                            dockNode);
        when(canvasHandler.getDiagram()).thenReturn(diagram);
        when(canvasHandler.getCanvas()).thenReturn(canvas);
        when(canvasHandler.getGraphIndex()).thenReturn(graphHandler.graphIndex);
        when(diagram.getMetadata()).thenReturn(metadata);
        when(diagram.getGraph()).thenReturn(graphHandler.graph);
        when(metadata.getDefinitionSetId()).thenReturn(TestingGraphMockHandler.DEF_SET_ID);
        when(metadata.getShapeSetId()).thenReturn(SHAPE_SET_ID);
        when(metadata.getCanvasRootUUID()).thenReturn(graphHolder.parentNode.getUUID());
        this.tested = new UpdateChildNodeCommand(laneNode,
                                                 graphHolder.endNode);
    }

    @Test
    public void testCommands() {
        CommandResult<CanvasViolation> result = tested.allow(canvasHandler);
        final List<Command<AbstractCanvasHandler, CanvasViolation>> commands = tested.getCommands();
        assertTrue(6 == commands.size());

        final RemoveChildCommand c1 = (RemoveChildCommand) commands.get(0);
        assertNotNull(c1);
        assertEquals(graphHolder.parentNode,
                     c1.getParent());

        assertEquals(graphHolder.endNode,
                     c1.getCandidate());

        final SetChildNodeCommand c2 = (SetChildNodeCommand) commands.get(1);
        assertNotNull(c2);
        assertEquals(laneNode,
                     c2.getParent());
        assertEquals(graphHolder.endNode,
                     c2.getCandidate());

        final RemoveChildCommand c4 = (RemoveChildCommand) commands.get(2);
        assertNotNull(c4);
        assertEquals(graphHolder.parentNode,
                     c4.getParent());
        assertEquals(dockNode,
                     c4.getCandidate());

        final UnDockNodeCommand c3 = (UnDockNodeCommand) commands.get(3);
        assertNotNull(c3);
        assertEquals(graphHolder.endNode,
                     c3.getParent());
        assertEquals(dockNode,
                     c3.getCandidate());

        final SetChildNodeCommand c5 = (SetChildNodeCommand) commands.get(4);
        assertNotNull(c5);
        assertEquals(laneNode,
                     c5.getParent());
        assertEquals(dockNode,
                     c5.getCandidate());
        final DockNodeCommand c6 = (DockNodeCommand) commands.get(5);
        assertNotNull(c6);
        assertEquals(graphHolder.endNode,
                     c6.getParent());
        assertEquals(dockNode,
                     c6.getCandidate());
        assertEquals(CommandResult.Type.INFO,
                     result.getType());
    }
}
