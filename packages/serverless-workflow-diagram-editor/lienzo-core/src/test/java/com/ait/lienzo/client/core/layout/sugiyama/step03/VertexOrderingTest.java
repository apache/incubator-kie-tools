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

package com.ait.lienzo.client.core.layout.sugiyama.step03;

import java.util.ArrayList;
import java.util.List;

import com.ait.lienzo.client.core.layout.OrientedEdgeImpl;
import com.ait.lienzo.client.core.layout.VertexPosition;
import com.ait.lienzo.client.core.layout.sugiyama.GraphLayer;
import com.ait.lienzo.client.core.layout.sugiyama.GraphLayerImpl;
import com.ait.lienzo.client.core.layout.sugiyama.LayeredGraph;
import com.ait.lienzo.client.core.layout.sugiyama.OrientedEdge;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class VertexOrderingTest {

    @Test
    public void testSimpleReorder() {
        final LayeredGraph graph = new LayeredGraph();
        graph.addEdge("A", "D");
        graph.addEdge("B", "C");

        final GraphLayerImpl layer01 = new GraphLayerImpl(1);
        layer01.addVertex(new VertexPosition("A"));
        layer01.addVertex(new VertexPosition("B"));
        graph.getLayers().add(layer01);

        final GraphLayerImpl layer02 = new GraphLayerImpl(2);
        layer02.addVertex(new VertexPosition("C"));
        layer02.addVertex(new VertexPosition("D"));
        graph.getLayers().add(layer02);

        final MedianVertexLayerPositioning median = new MedianVertexLayerPositioning();
        final LayerCrossingCount layersCount = new LayerCrossingCount();
        final VerticesTransposer verticesTransposer = new VerticesTransposer(layersCount);
        final DefaultVertexOrdering ordering = new DefaultVertexOrdering(median,
                                                                         layersCount,
                                                                         verticesTransposer);
        ordering.orderVertices(graph);

        Assertions.assertThat(graph.getLayers().get(0).getVertices())
                .extracting(VertexPosition::getId)
                .containsExactly("A", "B");
        Assertions.assertThat(graph.getLayers().get(1).getVertices())
                .extracting(VertexPosition::getId)
                .containsExactly("D", "C");
    }

    @Test
    public void testReorder() {
        final LayeredGraph graph = new LayeredGraph();
        graph.addEdge("A", "B");
        graph.addEdge("A", "C");
        graph.addEdge("F", "B");
        graph.addEdge("C", "E");
        graph.addEdge("G", "C");
        graph.addEdge("C", "H");
        graph.addEdge("D", "F");

        final GraphLayerImpl layer01 = new GraphLayerImpl(1);
        layer01.addVertex(new VertexPosition("A"));
        graph.getLayers().add(layer01);

        final GraphLayerImpl layer02 = new GraphLayerImpl(2);
        layer02.addVertex(new VertexPosition("B"));
        layer02.addVertex(new VertexPosition("C"));
        layer02.addVertex(new VertexPosition("D"));
        graph.getLayers().add(layer02);

        final GraphLayerImpl layer03 = new GraphLayerImpl(3);
        layer03.addVertex(new VertexPosition("E"));
        layer03.addVertex(new VertexPosition("F"));
        layer03.addVertex(new VertexPosition("G"));
        layer03.addVertex(new VertexPosition("H"));
        graph.getLayers().add(layer03);

        final MedianVertexLayerPositioning median = new MedianVertexLayerPositioning();
        final LayerCrossingCount layersCount = new LayerCrossingCount();
        final VerticesTransposer verticesTransposer = new VerticesTransposer(layersCount);
        final DefaultVertexOrdering ordering = new DefaultVertexOrdering(median,
                                                                         layersCount,
                                                                         verticesTransposer);
        ordering.orderVertices(graph);
        final List<GraphLayer> orderedLayers = graph.getLayers();

        Assertions.assertThat(orderedLayers.get(0).getVertices())
                .extracting(VertexPosition::getId)
                .containsExactly("A");
        Assertions.assertThat(orderedLayers.get(1).getVertices())
                .extracting(VertexPosition::getId)
                .containsExactly("D", "B", "C");
        Assertions.assertThat(orderedLayers.get(2).getVertices())
                .extracting(VertexPosition::getId)
                .containsExactly("F", "E", "G", "H");
    }

    @Test
    public void calculateMedianTest() {

        final List<OrientedEdge> edges = new ArrayList<>();
        edges.add(new OrientedEdgeImpl("G", "A"));
        edges.add(new OrientedEdgeImpl("G", "D"));
        edges.add(new OrientedEdgeImpl("G", "E"));

        final GraphLayerImpl layer00 = new GraphLayerImpl(0);
        layer00.addVertex(new VertexPosition("A"));
        layer00.addVertex(new VertexPosition("B"));
        layer00.addVertex(new VertexPosition("C"));
        layer00.addVertex(new VertexPosition("D"));
        layer00.addVertex(new VertexPosition("E"));
        final GraphLayerImpl layer01 = new GraphLayerImpl(1);
        layer01.addVertex(new VertexPosition("F"));
        layer01.addVertex(new VertexPosition("G"));
        layer01.addVertex(new VertexPosition("H"));
        layer01.addVertex(new VertexPosition("I"));
        layer01.addVertex(new VertexPosition("J"));

        final MedianVertexLayerPositioning vertexMedian = new MedianVertexLayerPositioning();
        final double median = vertexMedian.calculateMedianOfVerticesConnectedTo("G", layer00, edges);
        assertEquals(3.0, median, 0.0001);
    }

    @Test
    public void testSimpleCrossing() {

        final GraphLayerImpl top = new GraphLayerImpl(0);
        top.addVertex(new VertexPosition("A"));
        top.addVertex(new VertexPosition("B"));

        final GraphLayerImpl bottom = new GraphLayerImpl(1);
        bottom.addVertex(new VertexPosition("C"));
        bottom.addVertex(new VertexPosition("D"));

        final List<OrientedEdge> edges = new ArrayList<>();
        edges.add(new OrientedEdgeImpl("A", "D"));
        edges.add(new OrientedEdgeImpl("B", "C"));

        final LayerCrossingCount cc = new LayerCrossingCount();
        final int result = cc.crossing(edges, top, bottom);

        assertEquals(1, result);
    }

    @Test
    public void testSimpleNoCrossing() {

        final GraphLayerImpl top = new GraphLayerImpl(0);
        top.addVertex(new VertexPosition("A"));
        top.addVertex(new VertexPosition("B"));

        final GraphLayerImpl bottom = new GraphLayerImpl(1);
        bottom.addVertex(new VertexPosition("C"));
        bottom.addVertex(new VertexPosition("D"));

        final List<OrientedEdge> edges = new ArrayList<>();
        edges.add(new OrientedEdgeImpl("A", "C"));
        edges.add(new OrientedEdgeImpl("B", "D"));

        final LayerCrossingCount cc = new LayerCrossingCount();
        final int result = cc.crossing(edges, top, bottom);

        assertEquals(0, result);
    }

    @Test
    public void test1Crossing() {

        /*
         * 1 crossing
         * A   B   C
         *   \   /
         *     X
         *   /   \
         * D   E   F
         */
        final GraphLayerImpl top = new GraphLayerImpl(0);
        top.addVertex(new VertexPosition("A"));
        top.addVertex(new VertexPosition("B"));
        top.addVertex(new VertexPosition("C"));

        final GraphLayerImpl bottom = new GraphLayerImpl(1);
        bottom.addVertex(new VertexPosition("D"));
        bottom.addVertex(new VertexPosition("E"));
        bottom.addVertex(new VertexPosition("F"));

        final List<OrientedEdge> edges = new ArrayList<>();
        edges.add(new OrientedEdgeImpl("A", "F"));
        edges.add(new OrientedEdgeImpl("D", "C"));

        final LayerCrossingCount cc = new LayerCrossingCount();
        final int result = cc.crossing(edges, top, bottom);

        assertEquals(1, result);
    }

    @Test
    public void test2CrossingsUnevenLayers() {

        /*
         * 2 crossings
         *       A           B
         *      /\\         /
         *     /  \ -------+ --
         *    / /-- +-----/    \
         *   / /     \          \
         *  D         E          F
         * */
        final GraphLayerImpl top = new GraphLayerImpl(0);
        top.addVertex(new VertexPosition("A"));
        top.addVertex(new VertexPosition("B"));

        final GraphLayerImpl bottom = new GraphLayerImpl(1);
        bottom.addVertex(new VertexPosition("D"));
        bottom.addVertex(new VertexPosition("E"));
        bottom.addVertex(new VertexPosition("F"));

        final List<OrientedEdge> edges = new ArrayList<>();
        edges.add(new OrientedEdgeImpl("A", "D"));
        edges.add(new OrientedEdgeImpl("E", "A"));
        edges.add(new OrientedEdgeImpl("A", "F"));
        edges.add(new OrientedEdgeImpl("D", "B"));

        final LayerCrossingCount cc = new LayerCrossingCount();
        final int result = cc.crossing(edges, top, bottom);

        assertEquals(2, result);
    }

    @Test
    public void test2Crossings() {

        /*
         * 2 crossings
         * A   B   C
         *  \  | /
         *   x x
         *  / \|
         * D   E   F
         * */
        final GraphLayerImpl top = new GraphLayerImpl(0);
        top.addVertex(new VertexPosition("A"));
        top.addVertex(new VertexPosition("B"));
        top.addVertex(new VertexPosition("C"));

        final GraphLayerImpl bottom = new GraphLayerImpl(1);
        bottom.addVertex(new VertexPosition("D"));
        bottom.addVertex(new VertexPosition("E"));
        bottom.addVertex(new VertexPosition("F"));

        final List<OrientedEdge> edges = new ArrayList<>();
        edges.add(new OrientedEdgeImpl("A", "E"));
        edges.add(new OrientedEdgeImpl("B", "E"));
        edges.add(new OrientedEdgeImpl("C", "D"));

        final LayerCrossingCount cc = new LayerCrossingCount();
        final int result = cc.crossing(edges, top, bottom);

        assertEquals(2, result);
    }

    @Test
    public void test2Crossing8Vertex() {

        /*
         * 2 crossing
         *  A       B       C        D
         *  |\--\   |        \---\   |
         *  | \  \--+-------\     \  |
         *  |  \----+-----\  \     \ |
         *  |       |      \  \---\ \|
         *  E       F       G        H
         */
        final GraphLayerImpl top = new GraphLayerImpl(0);
        top.addVertex(new VertexPosition("A"));
        top.addVertex(new VertexPosition("B"));
        top.addVertex(new VertexPosition("C"));
        top.addVertex(new VertexPosition("D"));

        final GraphLayerImpl bottom = new GraphLayerImpl(1);
        bottom.addVertex(new VertexPosition("E"));
        bottom.addVertex(new VertexPosition("F"));
        bottom.addVertex(new VertexPosition("G"));
        bottom.addVertex(new VertexPosition("H"));

        final List<OrientedEdge> edges = new ArrayList<>();
        edges.add(new OrientedEdgeImpl("A", "E"));
        edges.add(new OrientedEdgeImpl("A", "G"));
        edges.add(new OrientedEdgeImpl("A", "H"));
        edges.add(new OrientedEdgeImpl("B", "F"));
        edges.add(new OrientedEdgeImpl("C", "H"));
        edges.add(new OrientedEdgeImpl("D", "H"));

        final LayerCrossingCount cc = new LayerCrossingCount();
        final int result = cc.crossing(edges, top, bottom);

        assertEquals(2, result);
    }

    @Test
    public void test3CrossingsInMiddle() {

        /*
         * 3 crossings
         * A   B   C
         *   \ |  /
         *     X
         *   / | \
         * D   E   F
         */
        final GraphLayerImpl top = new GraphLayerImpl(0);
        top.addVertex(new VertexPosition("A"));
        top.addVertex(new VertexPosition("B"));
        top.addVertex(new VertexPosition("C"));

        final GraphLayerImpl bottom = new GraphLayerImpl(1);
        bottom.addVertex(new VertexPosition("D"));
        bottom.addVertex(new VertexPosition("E"));
        bottom.addVertex(new VertexPosition("F"));

        final List<OrientedEdge> edges = new ArrayList<>();
        edges.add(new OrientedEdgeImpl("A", "F"));
        edges.add(new OrientedEdgeImpl("B", "E"));
        edges.add(new OrientedEdgeImpl("C", "D"));

        final LayerCrossingCount cc = new LayerCrossingCount();
        final int result = cc.crossing(edges, top, bottom);

        assertEquals(3, result);
    }

    @Test
    public void testK33GraphCrossing() {

        /*
         * k33 - every vertex from layer 1 connected to every vertex in layer 2
         */
        final GraphLayerImpl top = new GraphLayerImpl(0);
        top.addVertex(new VertexPosition("A"));
        top.addVertex(new VertexPosition("B"));
        top.addVertex(new VertexPosition("C"));

        final GraphLayerImpl bottom = new GraphLayerImpl(1);
        bottom.addVertex(new VertexPosition("D"));
        bottom.addVertex(new VertexPosition("E"));
        bottom.addVertex(new VertexPosition("F"));

        final List<OrientedEdge> edges = new ArrayList<>();
        edges.add(new OrientedEdgeImpl("A", "D"));
        edges.add(new OrientedEdgeImpl("A", "E"));
        edges.add(new OrientedEdgeImpl("A", "F"));

        edges.add(new OrientedEdgeImpl("B", "D"));
        edges.add(new OrientedEdgeImpl("B", "E"));
        edges.add(new OrientedEdgeImpl("B", "F"));

        edges.add(new OrientedEdgeImpl("C", "D"));
        edges.add(new OrientedEdgeImpl("C", "E"));
        edges.add(new OrientedEdgeImpl("C", "F"));

        final LayerCrossingCount cc = new LayerCrossingCount();
        final int result = cc.crossing(edges, top, bottom);

        assertEquals(9, result);
    }

    @Test
    public void test5VertexUnevenLayersNoCrossing() {

        /*
         * 3 crossings
         * A    B
         * |   /|\
         * |  / | \
         * | /  |  \
         * D    E   F
         */
        final GraphLayerImpl top = new GraphLayerImpl(0);
        top.addVertex(new VertexPosition("A"));
        top.addVertex(new VertexPosition("B"));

        final GraphLayerImpl bottom = new GraphLayerImpl(1);
        bottom.addVertex(new VertexPosition("D"));
        bottom.addVertex(new VertexPosition("E"));
        bottom.addVertex(new VertexPosition("F"));

        final List<OrientedEdge> edges = new ArrayList<>();
        edges.add(new OrientedEdgeImpl("A", "D"));
        edges.add(new OrientedEdgeImpl("B", "D"));
        edges.add(new OrientedEdgeImpl("E", "B"));
        edges.add(new OrientedEdgeImpl("F", "B"));

        final LayerCrossingCount cc = new LayerCrossingCount();
        final int result = cc.crossing(edges, top, bottom);

        assertEquals(0, result);
    }
}
