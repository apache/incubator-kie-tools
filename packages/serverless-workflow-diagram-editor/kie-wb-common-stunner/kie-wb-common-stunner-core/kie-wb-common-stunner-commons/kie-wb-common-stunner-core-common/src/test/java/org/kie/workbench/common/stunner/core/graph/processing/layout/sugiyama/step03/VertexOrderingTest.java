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


package org.kie.workbench.common.stunner.core.graph.processing.layout.sugiyama.step03;

import java.util.ArrayList;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.graph.processing.layout.OrientedEdgeImpl;
import org.kie.workbench.common.stunner.core.graph.processing.layout.Vertex;
import org.kie.workbench.common.stunner.core.graph.processing.layout.sugiyama.GraphLayer;
import org.kie.workbench.common.stunner.core.graph.processing.layout.sugiyama.GraphLayerImpl;
import org.kie.workbench.common.stunner.core.graph.processing.layout.sugiyama.LayeredGraph;
import org.kie.workbench.common.stunner.core.graph.processing.layout.sugiyama.OrientedEdge;
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
        layer01.addNewVertex("A");
        layer01.addNewVertex("B");
        graph.getLayers().add(layer01);

        final GraphLayerImpl layer02 = new GraphLayerImpl(2);
        layer02.addNewVertex("C");
        layer02.addNewVertex("D");
        graph.getLayers().add(layer02);

        final MedianVertexLayerPositioning median = new MedianVertexLayerPositioning();
        final LayerCrossingCount layersCount = new LayerCrossingCount();
        final VerticesTransposer verticesTransposer = new VerticesTransposer(layersCount);
        final DefaultVertexOrdering ordering = new DefaultVertexOrdering(median,
                                                                         layersCount,
                                                                         verticesTransposer);
        ordering.orderVertices(graph);

        Assertions.assertThat(graph.getLayers().get(0).getVertices())
                .extracting(Vertex::getId)
                .containsExactly("A", "B");
        Assertions.assertThat(graph.getLayers().get(1).getVertices())
                .extracting(Vertex::getId)
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
        layer01.addNewVertex("A");
        graph.getLayers().add(layer01);

        final GraphLayerImpl layer02 = new GraphLayerImpl(2);
        layer02.addNewVertex("B");
        layer02.addNewVertex("C");
        layer02.addNewVertex("D");
        graph.getLayers().add(layer02);

        final GraphLayerImpl layer03 = new GraphLayerImpl(3);
        layer03.addNewVertex("E");
        layer03.addNewVertex("F");
        layer03.addNewVertex("G");
        layer03.addNewVertex("H");
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
                .extracting(Vertex::getId)
                .containsExactly("A");
        Assertions.assertThat(orderedLayers.get(1).getVertices())
                .extracting(Vertex::getId)
                .containsExactly("D", "B", "C");
        Assertions.assertThat(orderedLayers.get(2).getVertices())
                .extracting(Vertex::getId)
                .containsExactly("F", "E", "G", "H");
    }

    @Test
    public void calculateMedianTest() {

        final List<OrientedEdge> edges = new ArrayList<>();
        edges.add(new OrientedEdgeImpl("G", "A"));
        edges.add(new OrientedEdgeImpl("G", "D"));
        edges.add(new OrientedEdgeImpl("G", "E"));

        final GraphLayerImpl layer00 = new GraphLayerImpl(0);
        layer00.addNewVertex("A");
        layer00.addNewVertex("B");
        layer00.addNewVertex("C");
        layer00.addNewVertex("D");
        layer00.addNewVertex("E");
        final GraphLayerImpl layer01 = new GraphLayerImpl(1);
        layer01.addNewVertex("F");
        layer01.addNewVertex("G");
        layer01.addNewVertex("H");
        layer01.addNewVertex("I");
        layer01.addNewVertex("J");

        final MedianVertexLayerPositioning vertexMedian = new MedianVertexLayerPositioning();
        final double median = vertexMedian.calculateMedianOfVerticesConnectedTo("G", layer00, edges);
        assertEquals(3.0, median, 0.0001);
    }

    @Test
    public void testSimpleCrossing() {

        final GraphLayerImpl top = new GraphLayerImpl(0);
        top.addNewVertex("A");
        top.addNewVertex("B");

        final GraphLayerImpl bottom = new GraphLayerImpl(1);
        bottom.addNewVertex("C");
        bottom.addNewVertex("D");

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
        top.addNewVertex("A");
        top.addNewVertex("B");

        final GraphLayerImpl bottom = new GraphLayerImpl(1);
        bottom.addNewVertex("C");
        bottom.addNewVertex("D");

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
        top.addNewVertex("A");
        top.addNewVertex("B");
        top.addNewVertex("C");

        final GraphLayerImpl bottom = new GraphLayerImpl(1);
        bottom.addNewVertex("D");
        bottom.addNewVertex("E");
        bottom.addNewVertex("F");

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
        top.addNewVertex("A");
        top.addNewVertex("B");

        final GraphLayerImpl bottom = new GraphLayerImpl(1);
        bottom.addNewVertex("D");
        bottom.addNewVertex("E");
        bottom.addNewVertex("F");

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
        top.addNewVertex("A");
        top.addNewVertex("B");
        top.addNewVertex("C");

        final GraphLayerImpl bottom = new GraphLayerImpl(1);
        bottom.addNewVertex("D");
        bottom.addNewVertex("E");
        bottom.addNewVertex("F");

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
        top.addNewVertex("A");
        top.addNewVertex("B");
        top.addNewVertex("C");
        top.addNewVertex("D");

        final GraphLayerImpl bottom = new GraphLayerImpl(1);
        bottom.addNewVertex("E");
        bottom.addNewVertex("F");
        bottom.addNewVertex("G");
        bottom.addNewVertex("H");

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
        top.addNewVertex("A");
        top.addNewVertex("B");
        top.addNewVertex("C");

        final GraphLayerImpl bottom = new GraphLayerImpl(1);
        bottom.addNewVertex("D");
        bottom.addNewVertex("E");
        bottom.addNewVertex("F");

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
        top.addNewVertex("A");
        top.addNewVertex("B");
        top.addNewVertex("C");

        final GraphLayerImpl bottom = new GraphLayerImpl(1);
        bottom.addNewVertex("D");
        bottom.addNewVertex("E");
        bottom.addNewVertex("F");

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
        top.addNewVertex("A");
        top.addNewVertex("B");

        final GraphLayerImpl bottom = new GraphLayerImpl(1);
        bottom.addNewVertex("D");
        bottom.addNewVertex("E");
        bottom.addNewVertex("F");

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