/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.core.graph.processing.layout.sugiyama;

import java.util.HashMap;
import java.util.List;

import javax.enterprise.inject.Default;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.HasBounds;
import org.kie.workbench.common.stunner.core.graph.content.view.Point2D;
import org.kie.workbench.common.stunner.core.graph.processing.layout.AbstractLayoutService;
import org.kie.workbench.common.stunner.core.graph.processing.layout.Layout;
import org.kie.workbench.common.stunner.core.graph.processing.layout.Vertex;
import org.kie.workbench.common.stunner.core.graph.processing.layout.VertexPosition;
import org.kie.workbench.common.stunner.core.graph.processing.layout.VertexPositionImpl;
import org.kie.workbench.common.stunner.core.graph.processing.layout.sugiyama.step01.CycleBreaker;
import org.kie.workbench.common.stunner.core.graph.processing.layout.sugiyama.step02.VertexLayerer;
import org.kie.workbench.common.stunner.core.graph.processing.layout.sugiyama.step03.VertexOrdering;
import org.kie.workbench.common.stunner.core.graph.processing.layout.sugiyama.step04.LayerArrangement;
import org.kie.workbench.common.stunner.core.graph.processing.layout.sugiyama.step04.VertexPositioning;

/**
 * Implementation of the Sugiyama automatic layout method.
 * This method is developed by Kozo Sugiyama, and draws the graph in a layered way.
 * This method is divided in 4 steps:
 * 1. Cycle Breaker: removes cycles in a cyclic graph.
 * 2. Vertex Layering: puts each vertex (node) in a layer.
 * 3. Vertex positioning: positions each vertex inside its layer.
 * 4. Vertex arrangement: sets the x and y position of the vertex.
 * <p>
 * The method used in each step could be changed in order to achieve better results,
 * with a better node disposition and less edges crossing.
 */
@Default
public final class SugiyamaLayoutService extends AbstractLayoutService {

    private final CycleBreaker cycleBreaker;
    private final VertexLayerer vertexLayerer;
    private final VertexOrdering vertexOrdering;
    private final VertexPositioning vertexPositioning;

    /**
     * Default constructor.
     * @param cycleBreaker The strategy used to break cycles in cycle graphs.
     * @param vertexLayerer The strategy used to choose the layer for each vertex.
     * @param vertexOrdering The strategy used to order vertices inside each layer.
     * @param vertexPositioning The strategy used to position vertices on screen (x,y coordinates).
     */
    @Inject
    public SugiyamaLayoutService(final CycleBreaker cycleBreaker,
                                 final VertexLayerer vertexLayerer,
                                 final VertexOrdering vertexOrdering,
                                 final VertexPositioning vertexPositioning) {
        this.cycleBreaker = cycleBreaker;
        this.vertexLayerer = vertexLayerer;
        this.vertexOrdering = vertexOrdering;
        this.vertexPositioning = vertexPositioning;
    }

    /**
     * Performs the automatic layout in graph using Sugiyama method,
     * putting vertices in layers in order to reduce edges crossing.
     * @param graph The graph.
     * @return The Layout for the vertices.
     * @see Layout
     */
    @Override
    public Layout createLayout(final Graph<?, ?> graph) {
        final HashMap<String, Node> indexByUuid = new HashMap<>();
        final LayeredGraph reorderedGraph = new LayeredGraph();

        for (final Node n : graph.nodes()) {

            if (!(n.getContent() instanceof HasBounds)) {
                continue;
            }

            indexByUuid.put(n.getUUID(), n);

            for (final Object e : n.getInEdges()) {
                if (e instanceof Edge) {
                    final Edge edge = (Edge) e;
                    final String from = edge.getSourceNode().getUUID();
                    final String to = n.getUUID();
                    reorderedGraph.addEdge(from, to);
                }
            }

            for (final Object e : n.getOutEdges()) {
                if (e instanceof Edge) {
                    final Edge edge = (Edge) e;
                    final String to = edge.getTargetNode().getUUID();
                    final String from = n.getUUID();
                    reorderedGraph.addEdge(from, to);
                }
            }
        }

        this.cycleBreaker.breakCycle(reorderedGraph);
        this.vertexLayerer.createLayers(reorderedGraph);
        this.vertexOrdering.orderVertices(reorderedGraph);
        this.vertexPositioning.calculateVerticesPositions(reorderedGraph,
                                                          LayerArrangement.BottomUp);

        final List<GraphLayer> orderedLayers = reorderedGraph.getLayers();
        return buildLayout(indexByUuid, orderedLayers);
    }

    private Layout buildLayout(final HashMap<String, Node> indexByUuid,
                               final List<GraphLayer> layers) {

        final Layout layout = new Layout();

        for (int i = layers.size() - 1; i >= 0; i--) {
            final GraphLayer layer = layers.get(i);
            for (final Vertex v : layer.getVertices()) {
                final Node n = indexByUuid.get(v.getId());

                final int x = v.getX();
                final int y = v.getY();

                final Bounds currentBounds = ((HasBounds) n.getContent()).getBounds();
                final Bounds.Bound lowerRight = currentBounds.getLowerRight();
                final int x2;
                if (isCloseToZero(lowerRight.getX())) {
                    x2 = x + VertexPositioning.DEFAULT_VERTEX_WIDTH;
                } else {
                    x2 = (int) (x + lowerRight.getX());
                }

                final int y2;
                if (isCloseToZero(lowerRight.getY())) {
                    y2 = y + VertexPositioning.DEFAULT_VERTEX_HEIGHT;
                } else {
                    y2 = (int) (y + lowerRight.getY());
                }

                final VertexPosition position = new VertexPositionImpl(v.getId(),
                                                                       new Point2D(x, y),
                                                                       new Point2D(x2, y2));

                layout.getNodePositions().add(position);
            }
        }

        return layout;
    }
}