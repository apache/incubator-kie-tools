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


package org.kie.workbench.common.stunner.core.graph.processing.layout.sugiyama;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.HasBounds;
import org.kie.workbench.common.stunner.core.graph.processing.layout.GraphProcessor;
import org.kie.workbench.common.stunner.core.graph.processing.layout.sugiyama.step01.CycleBreaker;
import org.kie.workbench.common.stunner.core.graph.processing.layout.sugiyama.step02.VertexLayerer;
import org.kie.workbench.common.stunner.core.graph.processing.layout.sugiyama.step03.VertexOrdering;
import org.kie.workbench.common.stunner.core.graph.processing.layout.sugiyama.step04.VertexPositioning;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.kie.workbench.common.stunner.core.graph.processing.layout.sugiyama.SugiyamaLayoutService.DEFAULT_LAYER_ARRANGEMENT;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class SugiyamaLayoutServiceTest {

    @Mock
    private CycleBreaker cycleBreaker;

    @Mock
    private VertexLayerer vertexLayerer;

    @Mock
    private VertexOrdering vertexOrdering;

    @Mock
    private VertexPositioning vertexPositioning;

    @Mock
    private GraphProcessor graphProcessor;

    private SugiyamaLayoutService layoutService;

    @Before
    public void setup() {

        layoutService = spy(new SugiyamaLayoutService(cycleBreaker,
                                                      vertexLayerer,
                                                      vertexOrdering,
                                                      vertexPositioning,
                                                      graphProcessor));
    }

    @Test
    public void testCreateLayout() {

        final Graph<?, ?> graph = mock(Graph.class);
        final Iterable nodes = mock(Iterable.class);
        final HashMap indexByUuid = mock(HashMap.class);
        final Collection values = mock(Collection.class);
        final LayeredGraph layeredGraph = mock(LayeredGraph.class);
        final List layers = mock(List.class);

        when(graphProcessor.getNodes(graph)).thenReturn(nodes);
        when(indexByUuid.values()).thenReturn(values);
        when(layeredGraph.getLayers()).thenReturn(layers);
        doReturn(indexByUuid).when(layoutService).createIndex(nodes);
        doReturn(layeredGraph).when(layoutService).createLayeredGraph(values);

        layoutService.createLayout(graph);
        final InOrder inOrder = inOrder(graphProcessor, cycleBreaker, vertexLayerer,
                                        vertexOrdering, vertexPositioning);

        inOrder.verify(graphProcessor).getNodes(graph);
        inOrder.verify(cycleBreaker).breakCycle(layeredGraph);
        inOrder.verify(vertexLayerer).createLayers(layeredGraph);
        inOrder.verify(vertexOrdering).orderVertices(layeredGraph);
        inOrder.verify(vertexPositioning).calculateVerticesPositions(layeredGraph,
                                                                     DEFAULT_LAYER_ARRANGEMENT,
                                                                     graphProcessor,
                                                                     graph);
        verify(layoutService).buildLayout(indexByUuid, layers, graph);
    }

    @Test
    public void testCreateIndex() {

        final Node nodeWithoutBounds = mock(Node.class);
        final String noBoundsId = "not";
        final String id1 = "id1";
        final String id2 = "id2";
        final Object notHasBoundsInstance = mock(Object.class);
        final Node n1 = createNode(id1);
        final Node n2 = createNode(id2);
        final List<Node> nodes = Arrays.asList(n1, n2, nodeWithoutBounds);

        when(nodeWithoutBounds.getUUID()).thenReturn(noBoundsId);
        when(nodeWithoutBounds.getContent()).thenReturn(notHasBoundsInstance);

        final HashMap<String, Node> index = layoutService.createIndex(nodes);

        assertTrue(index.containsKey(id1));
        assertTrue(index.containsKey(id2));
        assertFalse(index.containsKey(noBoundsId));

        assertEquals(n1, index.get(id1));
        assertEquals(n2, index.get(id2));
    }

    private Node createNode(final String uuid) {
        final Node node = mock(Node.class);
        final HasBounds hasBounds = mock(HasBounds.class);
        when(node.getUUID()).thenReturn(uuid);
        when(node.getContent()).thenReturn(hasBounds);
        return node;
    }

    @Test
    public void testCreateLayeredGraph() {

        final Node n1 = createNode("id1");
        final Node n2 = createNode("id2");
        final List<Node> nodes = Arrays.asList(n1, n2);
        final LayeredGraph layeredGraph = mock(LayeredGraph.class);
        doReturn(layeredGraph).when(layoutService).getLayeredGraph();
        doNothing().when(layoutService).addInEdges(layeredGraph, n1);
        doNothing().when(layoutService).addOutEdges(layeredGraph, n1);
        doNothing().when(layoutService).addInEdges(layeredGraph, n2);
        doNothing().when(layoutService).addOutEdges(layeredGraph, n2);

        layoutService.createLayeredGraph(nodes);

        verify(layoutService).addInEdges(layeredGraph, n1);
        verify(layoutService).addInEdges(layeredGraph, n2);
        verify(layoutService).addOutEdges(layeredGraph, n1);
        verify(layoutService).addOutEdges(layeredGraph, n2);
    }

    @Test
    public void testAddInEdges() {
        final LayeredGraph layeredGraph = mock(LayeredGraph.class);
        final Node node = mock(Node.class);
        final Edge edge = mock(Edge.class);
        final Node sourceNode = mock(Node.class);
        final List inEdges = Arrays.asList(edge);
        final String fromId = "from";
        final String toId = "to";
        final int width = 10;
        final int height = 20;

        when(node.getInEdges()).thenReturn(inEdges);
        when(edge.getSourceNode()).thenReturn(sourceNode);
        doReturn(fromId).when(layoutService).getId(sourceNode);
        doReturn(toId).when(layoutService).getId(node);
        doReturn(width).when(layoutService).getWidth(node);
        doReturn(height).when(layoutService).getHeight(node);

        layoutService.addInEdges(layeredGraph, node);

        verify(layeredGraph).addEdge(fromId, toId);
        verify(layeredGraph).setVertexSize(toId, width, height);
    }

    @Test
    public void testAddOutEdges() {
        final LayeredGraph layeredGraph = mock(LayeredGraph.class);
        final Node node = mock(Node.class);
        final Edge edge = mock(Edge.class);
        final Node targetNode = mock(Node.class);
        final List outEdges = Arrays.asList(edge);
        final String fromId = "from";
        final String toId = "to";
        final int width = 10;
        final int height = 20;

        when(node.getOutEdges()).thenReturn(outEdges);
        when(edge.getTargetNode()).thenReturn(targetNode);
        doReturn(fromId).when(layoutService).getId(node);
        doReturn(toId).when(layoutService).getId(targetNode);
        doReturn(width).when(layoutService).getWidth(node);
        doReturn(height).when(layoutService).getHeight(node);

        layoutService.addOutEdges(layeredGraph, node);

        verify(layeredGraph).addEdge(fromId, toId);
        verify(layeredGraph).setVertexSize(fromId, width, height);
    }

    @Test
    public void testGetHeight() {

        final Node n = mock(Node.class);
        final HasBounds hasBounds = mock(HasBounds.class);
        final Bounds bounds = mock(Bounds.class);
        final double height = 15;
        final int expected = (int) height;
        when(bounds.getHeight()).thenReturn(height);
        when(hasBounds.getBounds()).thenReturn(bounds);
        when(n.getContent()).thenReturn(hasBounds);

        int actual = layoutService.getHeight(n);

        assertEquals(expected, actual);
    }

    @Test
    public void testGetWidth() {

        final Node n = mock(Node.class);
        final HasBounds hasBounds = mock(HasBounds.class);
        final Bounds bounds = mock(Bounds.class);
        final double width = 17;
        final int expected = (int) width;
        when(bounds.getWidth()).thenReturn(width);
        when(hasBounds.getBounds()).thenReturn(bounds);
        when(n.getContent()).thenReturn(hasBounds);

        int actual = layoutService.getWidth(n);

        assertEquals(expected, actual);
    }
}