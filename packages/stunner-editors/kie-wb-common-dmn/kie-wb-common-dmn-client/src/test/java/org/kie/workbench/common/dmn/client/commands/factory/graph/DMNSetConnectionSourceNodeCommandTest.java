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

package org.kie.workbench.common.dmn.client.commands.factory.graph;

import java.util.Optional;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.docks.navigator.drds.DMNGraphsProvider;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.content.HasContentDefinitionId;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.graph.content.view.Connection;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class DMNSetConnectionSourceNodeCommandTest {

    private static final String EDGE_UUID = "edge uuid";
    private static final String NODE_DIAGRAM_UUID = "the id";
    private static final String SOURCE_NODE_UUID = "source node uuid";

    @Mock
    private Node<? extends View<?>, Edge> sourceNode;

    @Mock
    private Edge<? extends View, Node> edge;

    @Mock
    private Connection connection;

    @Mock
    private DMNGraphsProvider graphsProvider;

    @Mock
    private GraphCommandExecutionContext context;

    private DMNSetConnectionSourceNodeCommand command;

    @Before
    public void setup() {

        final Node edgeSourceNode = createNode(NODE_DIAGRAM_UUID);

        when(sourceNode.getUUID()).thenReturn(SOURCE_NODE_UUID);
        when(edge.getUUID()).thenReturn(EDGE_UUID);
        when(edge.getSourceNode()).thenReturn(edgeSourceNode);

        command = spy(new DMNSetConnectionSourceNodeCommand(sourceNode,
                                                            edge,
                                                            connection,
                                                            graphsProvider));
    }

    @Test
    public void testGetSourceNodeWhenCommandBelongsToAnotherGraph() {

        final Node node = mock(Node.class);
        final Graph graph = mock(Graph.class);

        doReturn(true).when(command).commandBelongsToAnotherGraph();
        doReturn(graph).when(command).getEdgesGraph();

        when(graph.getNode(SOURCE_NODE_UUID)).thenReturn(node);

        final Node currentSourceNode = command.getSourceNode(context);

        assertEquals(node, currentSourceNode);
        verify(command, never()).superGetSourceNode(context);
    }

    @Test
    public void testGetSourceNodeWhenCommandBelongsToCurrentGraph() {

        final Node node = mock(Node.class);

        doReturn(false).when(command).commandBelongsToAnotherGraph();
        doReturn(node).when(command).superGetSourceNode(context);

        final Node currentSourceNode = command.getSourceNode(context);

        assertEquals(node, currentSourceNode);
        verify(command).superGetSourceNode(context);
    }

    @Test
    public void testGetNodeWhenCommandBelongsToAnotherGraph() {

        final String uuid = "uuid";
        final Graph graph = mock(Graph.class);
        final Node node = mock(Node.class);

        doReturn(true).when(command).commandBelongsToAnotherGraph();
        doReturn(graph).when(command).getEdgesGraph();
        when(graph.getNode(uuid)).thenReturn(node);

        final Node currentNode = command.getNode(context, uuid);

        assertEquals(node, currentNode);
    }

    @Test
    public void testGetNodeWhenCommandBelongsToCurrentGraph() {

        final String uuid = "uuid";
        final Node node = mock(Node.class);

        doReturn(false).when(command).commandBelongsToAnotherGraph();
        doReturn(node).when(command).superGetNode(context, uuid);

        final Node currentNode = command.getNode(context, uuid);

        assertEquals(node, currentNode);
    }

    @Test
    public void testCommandBelongsToAnotherGraphWhenItIsNot() {

        final String diagramId = "diagramId";
        final Optional<String> commandDiagramId = Optional.of(diagramId);

        when(graphsProvider.getCurrentDiagramId()).thenReturn(diagramId);
        doReturn(commandDiagramId).when(command).getDiagramId();

        assertFalse(command.commandBelongsToAnotherGraph());
    }

    @Test
    public void testCommandBelongsToAnotherGraph() {

        final String diagramId = "diagramId";
        final String anotherDiagramId = "another diagram id";
        final Optional<String> commandDiagramId = Optional.of(diagramId);

        when(graphsProvider.getCurrentDiagramId()).thenReturn(anotherDiagramId);
        doReturn(commandDiagramId).when(command).getDiagramId();

        assertTrue(command.commandBelongsToAnotherGraph());
    }

    @Test
    public void testCommandBelongsToAnotherGraphWhenDiagramIdIsNotPresent() {

        final String anotherDiagramId = "another diagram id";

        when(graphsProvider.getCurrentDiagramId()).thenReturn(anotherDiagramId);
        doReturn(Optional.empty()).when(command).getDiagramId();

        assertFalse(command.commandBelongsToAnotherGraph());
    }

    @Test
    public void testGetEdgesGraph() {

        final String diagramId = "diagram id";
        final Diagram diagram = mock(Diagram.class);
        final Graph graph = mock(Graph.class);

        doReturn(Optional.of(diagramId)).when(command).getDiagramId();
        when(diagram.getGraph()).thenReturn(graph);
        when(graphsProvider.getDiagram(diagramId)).thenReturn(diagram);

        final Graph actualGraph = command.getEdgesGraph();

        assertEquals(graph, actualGraph);
    }

    @Test(expected=IllegalStateException.class)
    public void testGetEdgesGraphWhenDiagramIdIsNotSet() {

        doReturn(Optional.empty()).when(command).getDiagramId();

        command.getEdgesGraph();
    }

    private Node createNode(final String diagramId) {

        final Node node = mock(Node.class);
        final Definition definition = mock(Definition.class);
        final HasContentDefinitionId hasContentDefinitionId = mock(HasContentDefinitionId.class);

        when(node.getContent()).thenReturn(definition);
        when(hasContentDefinitionId.getDiagramId()).thenReturn(diagramId);
        when(definition.getDefinition()).thenReturn(hasContentDefinitionId);

        return node;
    }
}
