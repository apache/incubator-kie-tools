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


package org.kie.workbench.common.stunner.core.graph.processing.layout.sugiyama.step04;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.Bound;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.HasBounds;
import org.kie.workbench.common.stunner.core.graph.processing.layout.GraphProcessor;
import org.kie.workbench.common.stunner.core.graph.processing.layout.OrientedEdgeImpl;
import org.kie.workbench.common.stunner.core.graph.processing.layout.ReorderedGraph;
import org.kie.workbench.common.stunner.core.graph.processing.layout.Vertex;
import org.kie.workbench.common.stunner.core.graph.processing.layout.sugiyama.GraphLayer;
import org.kie.workbench.common.stunner.core.graph.processing.layout.sugiyama.LayeredGraph;
import org.kie.workbench.common.stunner.core.graph.processing.layout.sugiyama.OrientedEdge;
import org.mockito.InOrder;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.kie.workbench.common.stunner.core.graph.processing.layout.sugiyama.SugiyamaLayoutService.DEFAULT_INNER_HORIZONTAL_PADDING;
import static org.kie.workbench.common.stunner.core.graph.processing.layout.sugiyama.SugiyamaLayoutService.DEFAULT_PARENT_NODE_HEIGHT;
import static org.kie.workbench.common.stunner.core.graph.processing.layout.sugiyama.step04.DefaultVertexPositioning.DEFAULT_LAYER_HORIZONTAL_PADDING;
import static org.kie.workbench.common.stunner.core.graph.processing.layout.sugiyama.step04.DefaultVertexPositioning.DEFAULT_LAYER_VERTICAL_PADDING;
import static org.kie.workbench.common.stunner.core.graph.processing.layout.sugiyama.step04.DefaultVertexPositioning.DEFAULT_VERTEX_SPACE;
import static org.kie.workbench.common.stunner.core.graph.processing.layout.sugiyama.step04.VertexPositioning.DEFAULT_VERTEX_WIDTH;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DefaultVertexPositioningTest {

    private DefaultVertexPositioning tested;

    @Before
    public void setup() {
        tested = spy(new DefaultVertexPositioning());
    }

    @Test
    public void testArrangeVertices() {

        final GraphLayer layer1 = mock(GraphLayer.class);
        final GraphLayer layer2 = mock(GraphLayer.class);
        final List<GraphLayer> layers = Arrays.asList(layer1, layer2);
        final ReorderedGraph graph = mock(ReorderedGraph.class);
        final HashMap hash = mock(HashMap.class);
        final HashMap layersStartX = mock(HashMap.class);
        final int largestWidth = 100;
        final int newY = 17;
        final GraphProcessor graphProcessor = mock(GraphProcessor.class);
        final Graph<?, ?> stunnerGraph = mock(Graph.class);
        doReturn(hash).when(tested).createHashForLayersWidth();
        doReturn(largestWidth).when(tested).calculateLayersWidth(layers, hash);
        doReturn(layersStartX).when(tested).getLayersStartX(layers.size(), hash, largestWidth);

        doReturn(newY).when(tested).distributeVertices(layers,
                                                       layersStartX,
                                                       DEFAULT_LAYER_VERTICAL_PADDING,
                                                       0,
                                                       graph);

        doReturn(newY).when(tested).distributeVertices(layers,
                                                       layersStartX,
                                                       newY,
                                                       1,
                                                       graph);
        tested.arrangeVertices(layers,
                               LayerArrangement.TopDown,
                               graph,
                               stunnerGraph,
                               graphProcessor);

        final InOrder inOrder = inOrder(tested);

        inOrder.verify(tested).distributeVertices(layers,
                                                  layersStartX,
                                                  DEFAULT_LAYER_VERTICAL_PADDING,
                                                  0,
                                                  graph);

        inOrder.verify(tested).distributeVertices(layers,
                                                  layersStartX,
                                                  newY,
                                                  1,
                                                  graph);
    }

    @Test
    public void testArrangeVerticesBottomUp() {

        final GraphLayer layer1 = mock(GraphLayer.class);
        final GraphLayer layer2 = mock(GraphLayer.class);
        final List<GraphLayer> layers = Arrays.asList(layer1, layer2);
        final ReorderedGraph graph = mock(ReorderedGraph.class);
        final HashMap hash = mock(HashMap.class);
        final HashMap layersStartX = mock(HashMap.class);
        final int largestWidth = 100;
        final int newY = 17;
        final GraphProcessor graphProcessor = mock(GraphProcessor.class);
        final Graph<?, ?> stunnerGraph = mock(Graph.class);
        doReturn(hash).when(tested).createHashForLayersWidth();
        doReturn(largestWidth).when(tested).calculateLayersWidth(layers, hash);
        doReturn(layersStartX).when(tested).getLayersStartX(layers.size(), hash, largestWidth);

        doReturn(newY).when(tested).distributeVertices(layers,
                                                       layersStartX,
                                                       newY,
                                                       0,
                                                       graph);

        doReturn(newY).when(tested).distributeVertices(layers,
                                                       layersStartX,
                                                       DEFAULT_LAYER_VERTICAL_PADDING,
                                                       1,
                                                       graph);
        tested.arrangeVertices(layers,
                               LayerArrangement.BottomUp,
                               graph,
                               stunnerGraph,
                               graphProcessor);

        final InOrder inOrder = inOrder(tested);

        inOrder.verify(tested).distributeVertices(layers,
                                                  layersStartX,
                                                  DEFAULT_LAYER_VERTICAL_PADDING,
                                                  1,
                                                  graph);

        inOrder.verify(tested).distributeVertices(layers,
                                                  layersStartX,
                                                  newY,
                                                  0,
                                                  graph);
    }

    @Test
    public void testCalculateVerticesPositions() {
        final DefaultVertexPositioning tested = mock(DefaultVertexPositioning.class);
        final LayeredGraph graph = mock(LayeredGraph.class);
        final List<OrientedEdge> edges = mock(List.class);
        final LayerArrangement arrangement = LayerArrangement.BottomUp;
        final Set<Vertex> vertices = mock(Set.class);
        final GraphProcessor graphProcessor = mock(GraphProcessor.class);
        final Graph<?, ?> stunnerGraph = mock(Graph.class);
        when(tested.getVertices(graph)).thenReturn(vertices);
        final List<GraphLayer> layers = mock(List.class);
        when(graph.getLayers()).thenReturn(layers);

        when(graph.getEdges()).thenReturn(edges);

        doCallRealMethod().when(tested).calculateVerticesPositions(graph, arrangement, graphProcessor, stunnerGraph);

        tested.calculateVerticesPositions(graph, arrangement, graphProcessor, stunnerGraph);

        final InOrder inOrder = inOrder(tested);

        inOrder.verify(tested).deReverseEdges(graph);
        inOrder.verify(tested).getVertices(graph);
        inOrder.verify(tested).removeVirtualVertices(edges, vertices);
        inOrder.verify(tested).removeVirtualVerticesFromLayers(layers, vertices);
        inOrder.verify(tested).arrangeVertices(layers, arrangement, graph, stunnerGraph, graphProcessor);
    }

    @Test
    public void testGetVertices() {
        final LayeredGraph graph = new LayeredGraph();
        final GraphLayer layer1 = mock(GraphLayer.class);
        final GraphLayer layer2 = mock(GraphLayer.class);
        final List<Vertex> vertices1 = new ArrayList<>();
        final List<Vertex> vertices2 = new ArrayList<>();
        final Vertex v1 = mock(Vertex.class);
        final Vertex v2 = mock(Vertex.class);
        final Vertex v3 = mock(Vertex.class);
        vertices1.add(v1);
        vertices1.add(v2);
        vertices1.add(v3);
        when(layer1.getVertices()).thenReturn(vertices1);
        when(layer2.getVertices()).thenReturn(vertices2);
        graph.getLayers().add(layer1);
        graph.getLayers().add(layer2);

        final Set<Vertex> actual = tested.getVertices(graph);

        assertTrue(actual.contains(v1));
        assertTrue(actual.contains(v2));
        assertTrue(actual.contains(v3));
        assertEquals(3, actual.size());
    }

    @Test
    public void testDeReverseEdges() {

        final LayeredGraph graph = new LayeredGraph();
        final OrientedEdgeImpl e1 = new OrientedEdgeImpl("1", "2", true);
        final OrientedEdgeImpl e2 = new OrientedEdgeImpl("2", "3", false);
        final OrientedEdgeImpl e3 = new OrientedEdgeImpl("2", "4", true);

        graph.addEdge(e1);
        graph.addEdge(e2);
        graph.addEdge(e3);

        tested.deReverseEdges(graph);

        assertFalse(e1.isReversed());
        assertFalse(e2.isReversed());
        assertFalse(e3.isReversed());
    }

    @Test
    public void testGetLayersStartX() {

        final int largestWidth = 600;
        final int layersCount = 3;
        final HashMap<Integer, Integer> layersWidth = new HashMap<>();
        layersWidth.put(0, 200);
        layersWidth.put(1, 600);
        layersWidth.put(2, 300);
        final HashMap<Integer, Integer> startX = tested.getLayersStartX(layersCount, layersWidth, largestWidth);

        assertEquals(200 + DEFAULT_LAYER_HORIZONTAL_PADDING, (int) startX.get(0));
        assertEquals(0 + DEFAULT_LAYER_HORIZONTAL_PADDING, (int) startX.get(1));
        assertEquals(150 + DEFAULT_LAYER_HORIZONTAL_PADDING, (int) startX.get(2));
    }

    @Test
    public void testGetLargestWidth() {

        final GraphLayer layer1 = createGraphLayer(2);
        final int expectedSize1 = getExpectSize(2);
        final GraphLayer layer2 = createGraphLayer(4);
        final int expectedSize2 = getExpectSize(4);
        final GraphLayer layer3 = createGraphLayer(1);
        final int expectedSize3 = getExpectSize(1);
        final List<GraphLayer> layers = Arrays.asList(layer1, layer2, layer3);
        final HashMap<Integer, Integer> layersWidth = new HashMap<>();

        final int largest = tested.calculateLayersWidth(layers, layersWidth);

        assertEquals(expectedSize2, largest);
        assertEquals((int) layersWidth.get(0), expectedSize1);
        assertEquals((int) layersWidth.get(1), expectedSize2);
        assertEquals((int) layersWidth.get(2), expectedSize3);
    }

    @Test
    public void testCalculateParentVerticesSize() {

        final GraphProcessor graphProcessor = mock(GraphProcessor.class);
        final ReorderedGraph reorderedGraph = mock(ReorderedGraph.class);
        final Graph<?, ?> graph = mock(Graph.class);
        final Map<String, String> replacedNodes = mock(Map.class);
        final String parent1 = "parent1";
        final String parent2 = "parent2";
        final String parent3 = "parent3";
        final Integer parentWidth1 = 11;
        final Integer parentWidth2 = 22;
        final Integer parentWidth3 = 33;
        final Map<String, Integer> parentVertexWidth = new HashMap() {{
            put(parent1, parentWidth1);
            put(parent2, parentWidth2);
            put(parent3, parentWidth3);
        }};

        final Node node1 = mock(Node.class);
        final Node node2 = mock(Node.class);
        final Node node3 = mock(Node.class);
        final Optional<Node> optionalNode1 = Optional.of(node1);
        final Optional<Node> optionalNode2 = Optional.of(node2);
        final Optional<Node> optionalNode3 = Optional.of(node3);

        when(graphProcessor.getReplacedNodes()).thenReturn(replacedNodes);
        when(graphProcessor.getNodeFromGraph(parent1, graph)).thenReturn(optionalNode1);
        when(graphProcessor.getNodeFromGraph(parent2, graph)).thenReturn(optionalNode2);
        when(graphProcessor.getNodeFromGraph(parent3, graph)).thenReturn(optionalNode3);

        doReturn(true).when(tested).hasPositionSet(node3);
        doReturn(false).when(tested).hasPositionSet(node2);
        doReturn(false).when(tested).hasPositionSet(node1);
        doReturn(parentVertexWidth).when(tested).calculateParentVerticesWidth(replacedNodes);

        tested.calculateParentVerticesSize(graph, graphProcessor, reorderedGraph);

        verify(reorderedGraph).setVertexSize(parent1, parentWidth1, DEFAULT_PARENT_NODE_HEIGHT);
        verify(reorderedGraph).setVertexSize(parent2, parentWidth2, DEFAULT_PARENT_NODE_HEIGHT);
        verify(reorderedGraph, never()).setVertexSize(parent3, parentWidth3, DEFAULT_PARENT_NODE_HEIGHT);
    }

    @Test
    public void testCalculateParentVerticesWidth() {

        final String replacedNode1 = "replacedNode1";
        final String replacedNode2 = "replacedNode2";
        final String replacedNode3 = "replacedNode3";
        final String replacedNode4 = "replacedNode4";
        final String parent1 = "parent1";
        final String parent2 = "parent2";

        final Map<String, String> replacedNodes = new HashMap<>();

        // Parent1 is parent of nodes 1-3
        replacedNodes.put(replacedNode1, parent1);
        replacedNodes.put(replacedNode2, parent1);
        replacedNodes.put(replacedNode3, parent1);

        // Parent2 is parent of node 4
        replacedNodes.put(replacedNode4, parent2);

        final Map<String, Integer> widths = tested.calculateParentVerticesWidth(replacedNodes);

        // We want the width of Parent1 and Parent2 only
        assertEquals(2, widths.size());
        assertTrue(widths.containsKey(parent1));
        assertTrue(widths.containsKey(parent2));

        assertEquals(DEFAULT_VERTEX_WIDTH + DEFAULT_INNER_HORIZONTAL_PADDING, widths.get(parent2), 0.01);

        final double expectedWidthFor3Children = (DEFAULT_INNER_HORIZONTAL_PADDING + DEFAULT_VERTEX_WIDTH) * 3;
        assertEquals(expectedWidthFor3Children, widths.get(parent1), 0.01);
    }

    @Test
    public void testHasPositionSet_WhenThereIsNoBounds() {

        final Node node = mock(Node.class);
        doReturn(Optional.empty()).when(tested).getBounds(node);

        assertFalse(tested.hasPositionSet(node));
    }

    @Test
    public void testHasPositionSet_WhenThereIsUpperLeft() {

        final Node node = mock(Node.class);
        final Bounds bounds = mock(Bounds.class);

        when(bounds.hasUpperLeft()).thenReturn(false);
        doReturn(Optional.of(bounds)).when(tested).getBounds(node);

        assertFalse(tested.hasPositionSet(node));
    }

    @Test
    public void testHasPositionSet_WhenXisSet() {

        final Node node = mock(Node.class);
        final Bounds bounds = mock(Bounds.class);

        when(bounds.hasUpperLeft()).thenReturn(true);
        final Bound upperLeft = mock(Bound.class);
        when(bounds.getUpperLeft()).thenReturn(upperLeft);
        doReturn(Optional.of(bounds)).when(tested).getBounds(node);
        doReturn(true).when(tested).isXSet(upperLeft);

        assertTrue(tested.hasPositionSet(node));
    }

    @Test
    public void testHasPositionSet_WhenYisSet() {

        final Node node = mock(Node.class);
        final Bounds bounds = mock(Bounds.class);

        when(bounds.hasUpperLeft()).thenReturn(true);
        final Bound upperLeft = mock(Bound.class);
        when(bounds.getUpperLeft()).thenReturn(upperLeft);
        doReturn(Optional.of(bounds)).when(tested).getBounds(node);
        doReturn(false).when(tested).isXSet(upperLeft);
        doReturn(true).when(tested).isYSet(upperLeft);

        assertTrue(tested.hasPositionSet(node));
    }

    @Test
    public void testHasPositionSet_WhenIsNotSet() {

        final Node node = mock(Node.class);
        final Bounds bounds = mock(Bounds.class);

        when(bounds.hasUpperLeft()).thenReturn(true);
        final Bound upperLeft = mock(Bound.class);
        when(bounds.getUpperLeft()).thenReturn(upperLeft);
        doReturn(Optional.of(bounds)).when(tested).getBounds(node);
        doReturn(false).when(tested).isXSet(upperLeft);
        doReturn(false).when(tested).isYSet(upperLeft);

        assertFalse(tested.hasPositionSet(node));
    }

    @Test
    public void testGetBounds() {

        final Node node = mock(Node.class);
        final HasBounds hasBounds = mock(HasBounds.class);
        final Bounds bounds = mock(Bounds.class);
        when(hasBounds.getBounds()).thenReturn(bounds);
        when(node.getContent()).thenReturn(hasBounds);

        final Optional<Bounds> returnedBounds = tested.getBounds(node);

        assertTrue(returnedBounds.isPresent());
        assertEquals(bounds, returnedBounds.get());
    }

    @Test
    public void testGetBounds_WhenThereIsNoBounds() {

        final Node node = mock(Node.class);

        when(node.getContent()).thenReturn(new Object());

        final Optional<Bounds> returnedBounds = tested.getBounds(node);

        assertFalse(returnedBounds.isPresent());
    }

    private int getExpectSize(final int totalOfVertices) {
        return (totalOfVertices * DEFAULT_VERTEX_WIDTH) + ((totalOfVertices - 1) * DEFAULT_VERTEX_SPACE);
    }

    private GraphLayer createGraphLayer(final int verticesSize) {
        final GraphLayer layer = mock(GraphLayer.class);
        final List<Vertex> vertices = mock(List.class);
        when(vertices.size()).thenReturn(verticesSize);
        when(layer.getVertices()).thenReturn(vertices);

        return layer;
    }
}
