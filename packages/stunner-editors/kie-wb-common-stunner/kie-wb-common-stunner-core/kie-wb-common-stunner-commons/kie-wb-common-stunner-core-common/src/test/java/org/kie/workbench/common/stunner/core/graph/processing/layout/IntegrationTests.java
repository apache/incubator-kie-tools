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

package org.kie.workbench.common.stunner.core.graph.processing.layout;

import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.processing.layout.sugiyama.LayeredGraph;
import org.kie.workbench.common.stunner.core.graph.processing.layout.sugiyama.step01.ReverseEdgesCycleBreaker;
import org.kie.workbench.common.stunner.core.graph.processing.layout.sugiyama.step02.LongestPathVertexLayerer;
import org.kie.workbench.common.stunner.core.graph.processing.layout.sugiyama.step03.DefaultVertexOrdering;
import org.kie.workbench.common.stunner.core.graph.processing.layout.sugiyama.step03.LayerCrossingCount;
import org.kie.workbench.common.stunner.core.graph.processing.layout.sugiyama.step03.MedianVertexLayerPositioning;
import org.kie.workbench.common.stunner.core.graph.processing.layout.sugiyama.step03.VerticesTransposer;
import org.kie.workbench.common.stunner.core.graph.processing.layout.sugiyama.step04.DefaultVertexPositioning;
import org.kie.workbench.common.stunner.core.graph.processing.layout.sugiyama.step04.LayerArrangement;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class IntegrationTests {

    @Test
    public void testRealCase1() {
        final GraphProcessor graphProcessor = mock(GraphProcessor.class);
        final Graph<?, ?> stunnerGraph = mock(Graph.class);
        final LayeredGraph graph = new LayeredGraph(Graphs.REAL_CASE_1);

        final ReverseEdgesCycleBreaker s01 = new ReverseEdgesCycleBreaker();
        s01.breakCycle(graph);
        final LongestPathVertexLayerer s02 = new LongestPathVertexLayerer();
        s02.createLayers(graph);

        final MedianVertexLayerPositioning vertexPositioning = new MedianVertexLayerPositioning();
        final LayerCrossingCount crossingCount = new LayerCrossingCount();
        final VerticesTransposer verticesTransposer = new VerticesTransposer(crossingCount);

        final DefaultVertexOrdering s03 = new DefaultVertexOrdering(vertexPositioning,
                                                                    crossingCount,
                                                                    verticesTransposer);
        s03.orderVertices(graph);

        Assert.assertEquals(6, graph.getLayers().size());

        final DefaultVertexPositioning defaultVertexPositioning = new DefaultVertexPositioning();
        defaultVertexPositioning.calculateVerticesPositions(graph,
                                                            LayerArrangement.TopDown,
                                                            graphProcessor,
                                                            stunnerGraph);
    }

    @Test
    public void testTwoSeparateTrees() {
        final LayeredGraph graph = new LayeredGraph(Graphs.TwoSeparateTreesFromRoots);
        final ReverseEdgesCycleBreaker s01 = new ReverseEdgesCycleBreaker();
        s01.breakCycle(graph);
        final LongestPathVertexLayerer s02 = new LongestPathVertexLayerer();
        s02.createLayers(graph);
        final MedianVertexLayerPositioning vertexPositioning = new MedianVertexLayerPositioning();
        final LayerCrossingCount crossingCount = new LayerCrossingCount();
        final VerticesTransposer verticesTransposer = new VerticesTransposer(crossingCount);
        final DefaultVertexOrdering s03 = new DefaultVertexOrdering(vertexPositioning,
                                                                    crossingCount,
                                                                    verticesTransposer);
        s03.orderVertices(graph);
        Assertions.assertThat(graph.getLayers())
                .as("TwoSeparateTreesFromRoots graph vertices should be placed into three layers")
                .hasSize(3);
    }

    @Test
    public void testFullGraph() {
        final LayeredGraph graph = new LayeredGraph(Graphs.Full);
        final ReverseEdgesCycleBreaker s01 = new ReverseEdgesCycleBreaker();
        s01.breakCycle(graph);
        final LongestPathVertexLayerer s02 = new LongestPathVertexLayerer();
        s02.createLayers(graph);
        final MedianVertexLayerPositioning vertexPositioning = new MedianVertexLayerPositioning();
        final LayerCrossingCount crossingCount = new LayerCrossingCount();
        final VerticesTransposer verticesTransposer = new VerticesTransposer(crossingCount);
        final DefaultVertexOrdering s03 = new DefaultVertexOrdering(vertexPositioning,
                                                                    crossingCount,
                                                                    verticesTransposer);
        s03.orderVertices(graph);
        Assertions.assertThat(graph.getLayers())
                .as("Full graph vertices should be in 4 layers")
                .hasSize(4);
    }
}