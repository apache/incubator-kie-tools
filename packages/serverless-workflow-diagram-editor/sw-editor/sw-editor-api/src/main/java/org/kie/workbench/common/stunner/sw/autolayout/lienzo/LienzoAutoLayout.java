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


package org.kie.workbench.common.stunner.sw.autolayout.lienzo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.ait.lienzo.client.core.layout.AbstractLayoutService;
import com.ait.lienzo.client.core.layout.Layout;
import com.ait.lienzo.client.core.layout.VertexPosition;
import com.ait.lienzo.client.core.layout.graph.Vertex;
import com.ait.lienzo.client.core.layout.sugiyama.SugiyamaLayoutService;
import com.ait.lienzo.client.core.layout.sugiyama.step01.ReverseEdgesCycleBreaker;
import com.ait.lienzo.client.core.layout.sugiyama.step02.LongestPathVertexLayerer;
import com.ait.lienzo.client.core.layout.sugiyama.step02.VertexLayerer;
import com.ait.lienzo.client.core.layout.sugiyama.step03.DefaultVertexOrdering;
import com.ait.lienzo.client.core.layout.sugiyama.step03.LayerCrossingCount;
import com.ait.lienzo.client.core.layout.sugiyama.step03.MedianVertexLayerPositioning;
import com.ait.lienzo.client.core.layout.sugiyama.step03.VertexLayerPositioning;
import com.ait.lienzo.client.core.layout.sugiyama.step03.VertexOrdering;
import com.ait.lienzo.client.core.layout.sugiyama.step03.VerticesTransposer;
import com.ait.lienzo.client.core.layout.sugiyama.step04.DefaultVertexPositioning;
import com.ait.lienzo.client.core.layout.sugiyama.step04.VertexPositioning;
import elemental2.promise.Promise;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.HasBounds;

public class LienzoAutoLayout {

    public Promise<Layout> layout(final Graph graph,
                                  final Map<String, Vertex> vertices,
                                  final String startingNodeId,
                                  final String endingNodeId) {
        return Promise.resolve(processGraph(graph, vertices, startingNodeId, endingNodeId));
    }

    public Layout processGraph(final Graph graph,
                               final Map<String, Vertex> vertices,
                               final String startingNodeId,
                               final String endingNodeId) {

        final AbstractLayoutService layoutService = createLayoutService();
        final Layout layout = layoutService.createLayout(new ArrayList<>(vertices.values()), startingNodeId, endingNodeId);

        applyLayout(layout, graph);

        return layout;
    }

    private void applyLayout(final Layout layout,
                             final Graph graph) {

        if (layout.getVerticesPositions().isEmpty()) {
            return;
        }

        final HashMap<String, Node> indexByUuid = new HashMap<>();
        for (final Object n : graph.nodes()) {

            if (n instanceof Node) {
                final Node node = (Node) n;
                indexByUuid.put(node.getUUID(), node);
            }
        }

        for (final VertexPosition position : layout.getVerticesPositions()) {

            final Node indexed = indexByUuid.get(position.getId());

            if (indexed.getContent() instanceof HasBounds) {
                ((HasBounds) indexed.getContent()).setBounds(Bounds.create(
                        position.getX(),
                        position.getY(),
                        position.getBottomRightX(),
                        position.getBottomRightY()
                ));
            }
        }
    }

    private AbstractLayoutService createLayoutService() {

        final ReverseEdgesCycleBreaker cycleBreaker = new ReverseEdgesCycleBreaker();
        final VertexLayerer vertexLayerer = new LongestPathVertexLayerer();
        final VertexOrdering vertexOrdering = createVertexOrdering();
        final VertexPositioning vertexPositioning = new DefaultVertexPositioning();

        return new SugiyamaLayoutService(cycleBreaker,
                                         vertexLayerer,
                                         vertexOrdering,
                                         vertexPositioning);
    }

    private VertexOrdering createVertexOrdering() {

        final VertexLayerPositioning vertexLayerPositioning = new MedianVertexLayerPositioning();
        final LayerCrossingCount crossingCount = new LayerCrossingCount();
        final VerticesTransposer verticesTranspose = new VerticesTransposer(crossingCount);
        return new DefaultVertexOrdering(vertexLayerPositioning,
                                         crossingCount,
                                         verticesTranspose);
    }
}
