/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

package com.ait.lienzo.client.core.layout.sugiyama.step04;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import com.ait.lienzo.client.core.layout.OrientedEdgeImpl;
import com.ait.lienzo.client.core.layout.VertexPosition;
import com.ait.lienzo.client.core.layout.sugiyama.GraphLayer;
import com.ait.lienzo.client.core.layout.sugiyama.LayeredGraph;
import com.ait.lienzo.client.core.layout.sugiyama.OrientedEdge;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.junit.MockitoJUnitRunner;

import static com.ait.lienzo.client.core.layout.sugiyama.step04.DefaultVertexPositioning.DEFAULT_LAYER_HORIZONTAL_PADDING;
import static com.ait.lienzo.client.core.layout.sugiyama.step04.DefaultVertexPositioning.DEFAULT_LAYER_SPACE;
import static com.ait.lienzo.client.core.layout.sugiyama.step04.DefaultVertexPositioning.DEFAULT_LAYER_VERTICAL_PADDING;
import static com.ait.lienzo.client.core.layout.sugiyama.step04.DefaultVertexPositioning.DEFAULT_VERTEX_SPACE;
import static com.ait.lienzo.tools.common.api.java.util.UUID.uuid;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
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
        final LayeredGraph graph = mock(LayeredGraph.class);
        final HashMap hash = mock(HashMap.class);
        final HashMap layersStartX = mock(HashMap.class);
        final int largestWidth = 100;
        final int newY = DEFAULT_LAYER_SPACE + DEFAULT_VERTEX_SPACE;
        when(layersStartX.get(any())).thenReturn(0);
        doReturn(hash).when(tested).createHashForLayersWidth();
        doReturn(largestWidth).when(tested).calculateLayersWidth(layers, hash);
        doReturn(layersStartX).when(tested).getLayersStartX(graph, hash, largestWidth);

        final InOrder inOrder = inOrder(tested);

        tested.arrangeVertices(layers,
                               LayerArrangement.TopDown,
                               graph);

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
    public void testArrangeVerticesBottomUp() {

        final GraphLayer layer1 = mock(GraphLayer.class);
        final GraphLayer layer2 = mock(GraphLayer.class);
        final List<GraphLayer> layers = Arrays.asList(layer1, layer2);
        final LayeredGraph graph = mock(LayeredGraph.class);
        final HashMap hash = mock(HashMap.class);
        final HashMap layersStartX = mock(HashMap.class);
        final int largestWidth = 100;
        final int newY = 17;

        doReturn(hash).when(tested).createHashForLayersWidth();
        doReturn(largestWidth).when(tested).calculateLayersWidth(layers, hash);
        doReturn(layersStartX).when(tested).getLayersStartX(graph, hash, largestWidth);

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
                               LayerArrangement.BottomUp,
                               graph);

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
    public void testCalculateVerticesPositions() {
        final DefaultVertexPositioning tested = mock(DefaultVertexPositioning.class);
        final LayeredGraph graph = mock(LayeredGraph.class);
        final List<OrientedEdge> edges = mock(List.class);
        final LayerArrangement arrangement = LayerArrangement.BottomUp;
        final Set<VertexPosition> vertices = mock(Set.class);
        when(tested.getVertices(graph)).thenReturn(vertices);
        final List<GraphLayer> layers = mock(List.class);
        when(graph.getLayers()).thenReturn(layers);

        when(graph.getEdges()).thenReturn(edges);
        doCallRealMethod().when(tested).calculateVerticesPositions(graph, arrangement);

        tested.calculateVerticesPositions(graph, arrangement);

        final InOrder inOrder = inOrder(tested);

        inOrder.verify(tested).deReverseEdges(graph);
        inOrder.verify(tested).getVertices(graph);

        inOrder.verify(tested).arrangeVertices(layers, arrangement, graph);

        inOrder.verify(tested).convertVirtualVerticesToBendingPoints(edges, vertices);

        inOrder.verify(tested).removeVirtualVertices(edges, vertices);
        inOrder.verify(tested).removeVirtualVerticesFromLayers(layers, vertices);
    }

    @Test
    public void testGetVertices() {
        final LayeredGraph graph = new LayeredGraph();
        final GraphLayer layer1 = mock(GraphLayer.class);
        final GraphLayer layer2 = mock(GraphLayer.class);
        final List<VertexPosition> vertices1 = new ArrayList<>();
        final List<VertexPosition> vertices2 = new ArrayList<>();
        final VertexPosition v1 = mock(VertexPosition.class);
        final VertexPosition v2 = mock(VertexPosition.class);
        final VertexPosition v3 = mock(VertexPosition.class);
        vertices1.add(v1);
        vertices1.add(v2);
        vertices1.add(v3);
        when(layer1.getVertices()).thenReturn(vertices1);
        when(layer2.getVertices()).thenReturn(vertices2);
        graph.getLayers().add(layer1);
        graph.getLayers().add(layer2);

        final Set<VertexPosition> actual = tested.getVertices(graph);

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

        final GraphLayer startLayer = mock(GraphLayer.class);
        final List<GraphLayer> layers = Arrays.asList(mock(GraphLayer.class),
                                                      mock(GraphLayer.class),
                                                      startLayer);
        final LayeredGraph graph = mock(LayeredGraph.class);
        final HashMap<Integer, Integer> layersWidth = new HashMap<>();
        layersWidth.put(0, 200);
        layersWidth.put(1, 600);
        layersWidth.put(2, 300);

        final VertexPosition startVertex = mock(VertexPosition.class);
        final Integer startVertexWidth = 44;
        when(startVertex.getWidth()).thenReturn(startVertexWidth);

        final List<VertexPosition> topVertices = new ArrayList<>();
        topVertices.add(startVertex);

        when(startLayer.getVertices()).thenReturn(topVertices);

        when(graph.getLayers()).thenReturn(layers);

        final HashMap<Integer, Integer> startX = tested.getLayersStartX(graph, layersWidth, largestWidth);

        assertEquals(200 + DEFAULT_LAYER_HORIZONTAL_PADDING, (int) startX.get(0));
        assertEquals(0 + DEFAULT_LAYER_HORIZONTAL_PADDING, (int) startX.get(1));
        assertEquals(largestWidth / 2 - startVertexWidth/2 + DEFAULT_LAYER_HORIZONTAL_PADDING, (int) startX.get(2));
    }

    @Test
    public void testGetLargestWidth() {

        final int vertexWidth = 100;
        final VertexPosition layer1Vertex1 = createVertexWithWidth(vertexWidth);

        final VertexPosition layer2Vertex1 = createVertexWithWidth(vertexWidth);
        final VertexPosition layer2Vertex2 = createVertexWithWidth(vertexWidth);
        final VertexPosition layer2Vertex3 = createVertexWithWidth(vertexWidth);
        final VertexPosition layer2Vertex4 = createVertexWithWidth(vertexWidth);

        final VertexPosition layer3Vertex1 = createVertexWithWidth(vertexWidth);
        final VertexPosition layer3Vertex2 = createVertexWithWidth(vertexWidth);
        final VertexPosition layer3Vertex3 = createVertexWithWidth(vertexWidth);

        final GraphLayer layer1 = mock(GraphLayer.class);
        final GraphLayer layer2 = mock(GraphLayer.class);
        final GraphLayer layer3 = mock(GraphLayer.class);

        final int expectedLayer1Size = vertexWidth;
        final int expectedLayer2Size = vertexWidth * 4 + DEFAULT_VERTEX_SPACE * 3;
        final int expectedLayer3Size = vertexWidth * 3 + DEFAULT_VERTEX_SPACE * 2;

        when(layer1.getVertices()).thenReturn(Arrays.asList(layer1Vertex1));
        when(layer2.getVertices()).thenReturn(Arrays.asList(layer2Vertex1,
                                                            layer2Vertex2,
                                                            layer2Vertex3,
                                                            layer2Vertex4));

        when(layer3.getVertices()).thenReturn(Arrays.asList(layer3Vertex1,
                                                            layer3Vertex2,
                                                            layer3Vertex3));

        final List<GraphLayer> layers = Arrays.asList(layer1, layer2, layer3);
        final HashMap<Integer, Integer> layersWidth = new HashMap<>();

        final int largest = tested.calculateLayersWidth(layers, layersWidth);

        assertEquals(expectedLayer2Size, largest);
        assertEquals(expectedLayer1Size, (int) layersWidth.get(0));
        assertEquals(expectedLayer2Size, (int) layersWidth.get(1));
        assertEquals(expectedLayer3Size, (int) layersWidth.get(2));
    }

    private VertexPosition createVertexWithWidth(final int width) {
        final VertexPosition vertexPosition = new VertexPosition(uuid());
        vertexPosition.setWidth(width);
        return vertexPosition;
    }
}
