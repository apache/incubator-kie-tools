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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.enterprise.inject.Default;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.Bound;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.HasBounds;
import org.kie.workbench.common.stunner.core.graph.content.view.Point2D;
import org.kie.workbench.common.stunner.core.graph.processing.layout.AbstractLayoutService;
import org.kie.workbench.common.stunner.core.graph.processing.layout.GraphProcessor;
import org.kie.workbench.common.stunner.core.graph.processing.layout.Layout;
import org.kie.workbench.common.stunner.core.graph.processing.layout.Vertex;
import org.kie.workbench.common.stunner.core.graph.processing.layout.VertexPosition;
import org.kie.workbench.common.stunner.core.graph.processing.layout.VertexPositionImpl;
import org.kie.workbench.common.stunner.core.graph.processing.layout.sugiyama.step01.CycleBreaker;
import org.kie.workbench.common.stunner.core.graph.processing.layout.sugiyama.step02.VertexLayerer;
import org.kie.workbench.common.stunner.core.graph.processing.layout.sugiyama.step03.VertexOrdering;
import org.kie.workbench.common.stunner.core.graph.processing.layout.sugiyama.step04.LayerArrangement;
import org.kie.workbench.common.stunner.core.graph.processing.layout.sugiyama.step04.VertexPositioning;

import static org.kie.workbench.common.stunner.core.graph.processing.layout.sugiyama.step04.VertexPositioning.DEFAULT_VERTEX_WIDTH;

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
public class SugiyamaLayoutService extends AbstractLayoutService {

    public static final int DEFAULT_INNER_HORIZONTAL_PADDING = 20;
    public static final int DEFAULT_INNER_VERTICAL_PADDING = 20;
    public static final int DEFAULT_PARENT_NODE_HEIGHT = 200;

    private final CycleBreaker cycleBreaker;
    private final VertexLayerer vertexLayerer;
    private final VertexOrdering vertexOrdering;
    private final VertexPositioning vertexPositioning;
    private final GraphProcessor graphProcessor;
    static final LayerArrangement DEFAULT_LAYER_ARRANGEMENT = LayerArrangement.BottomUp;

    /**
     * Default constructor.
     *
     * @param cycleBreaker      The strategy used to break cycles in cycle graphs.
     * @param vertexLayerer     The strategy used to choose the layer for each vertex.
     * @param vertexOrdering    The strategy used to order vertices inside each layer.
     * @param vertexPositioning The strategy used to position vertices on screen (x,y coordinates).
     * @param graphProcessor    Applies some pre-process in the graph to extract the nodes to be used.
     */
    @Inject
    public SugiyamaLayoutService(final CycleBreaker cycleBreaker,
                                 final VertexLayerer vertexLayerer,
                                 final VertexOrdering vertexOrdering,
                                 final VertexPositioning vertexPositioning,
                                 final GraphProcessor graphProcessor) {
        this.cycleBreaker = cycleBreaker;
        this.vertexLayerer = vertexLayerer;
        this.vertexOrdering = vertexOrdering;
        this.vertexPositioning = vertexPositioning;
        this.graphProcessor = graphProcessor;
    }

    /**
     * Performs the automatic layout in graph using Sugiyama method,
     * putting vertices in layers in order to reduce edges crossing.
     *
     * @param graph The graph.
     * @return The Layout for the vertices.
     * @see Layout
     */
    @Override
    public Layout createLayout(final Graph<?, ?> graph) {

        final Iterable<? extends Node> nodes = graphProcessor.getNodes(graph);
        final HashMap<String, Node> indexByUuid = createIndex(nodes);
        final LayeredGraph layeredGraph = createLayeredGraph(indexByUuid.values());

        this.cycleBreaker.breakCycle(layeredGraph);
        this.vertexLayerer.createLayers(layeredGraph);
        this.vertexOrdering.orderVertices(layeredGraph);
        this.vertexPositioning.calculateVerticesPositions(layeredGraph,
                                                          DEFAULT_LAYER_ARRANGEMENT,
                                                          graphProcessor,
                                                          graph);

        final List<GraphLayer> orderedLayers = layeredGraph.getLayers();
        return buildLayout(indexByUuid, orderedLayers, graph);
    }

    HashMap<String, Node> createIndex(final Iterable<? extends Node> nodes) {

        final HashMap<String, Node> indexByUuid = new HashMap<>();
        for (final Node n : nodes) {
            if (!(n.getContent() instanceof HasBounds)) {
                continue;
            }

            indexByUuid.put(n.getUUID(), n);
        }

        return indexByUuid;
    }

    LayeredGraph createLayeredGraph(final Iterable<? extends Node> nodes) {

        final LayeredGraph layeredGraph = getLayeredGraph();
        for (final Node n : nodes) {
            addInEdges(layeredGraph, n);
            addOutEdges(layeredGraph, n);
        }

        return layeredGraph;
    }

    LayeredGraph getLayeredGraph() {
        return new LayeredGraph();
    }

    void addOutEdges(final LayeredGraph layeredGraph, final Node n) {
        for (final Object e : n.getOutEdges()) {
            if (e instanceof Edge) {
                final Edge edge = (Edge) e;
                final String to = getId(edge.getTargetNode());
                final String from = getId(n);
                layeredGraph.addEdge(from, to);
                layeredGraph.setVertexSize(getId(n), getWidth(n), getHeight(n));
            }
        }
    }

    void addInEdges(final LayeredGraph layeredGraph, final Node n) {
        for (final Object e : n.getInEdges()) {
            if (e instanceof Edge) {
                final Edge edge = (Edge) e;
                final String from = getId(edge.getSourceNode());
                final String to = getId(n);
                layeredGraph.addEdge(from, to);
                layeredGraph.setVertexSize(getId(n), getWidth(n), getHeight(n));
            }
        }
    }

    int getHeight(final Node n) {
        return (int) ((HasBounds) n.getContent()).getBounds().getHeight();
    }

    int getWidth(final Node n) {
        return (int) ((HasBounds) n.getContent()).getBounds().getWidth();
    }

    String getId(final Node node) {
        if (graphProcessor.isReplacedByAnotherNode(node.getUUID())) {
            return graphProcessor.getReplaceNodeId(node.getUUID());
        }

        return node.getUUID();
    }

    Layout buildLayout(final HashMap<String, Node> indexByUuid,
                       final List<GraphLayer> layers,
                       final Graph<?, ?> graph) {

        final Layout layout = new Layout();

        for (int i = layers.size() - 1; i >= 0; i--) {
            final GraphLayer layer = layers.get(i);
            for (final Vertex v : layer.getVertices()) {
                final Node n = indexByUuid.get(v.getId());

                final int x = v.getX();
                final int y = v.getY();

                final Bounds currentBounds = ((HasBounds) n.getContent()).getBounds();
                final Bound lowerRight = currentBounds.getLowerRight();
                final int x2;
                if (isCloseToZero(lowerRight.getX())) {
                    x2 = x + DEFAULT_VERTEX_WIDTH;
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

        positionReplacedNodes(graph, layout);

        return layout;
    }

    private void positionReplacedNodes(final Graph<?, ?> graph,
                                       final Layout layout) {

        final Map<String, String> replacedNodes = graphProcessor.getReplacedNodes();
        final Map<String, Double> parentNodesWidth = new HashMap<>();

        for (final Map.Entry<String, String> entry : replacedNodes.entrySet()) {
            final String childId = entry.getKey();
            final String parentId = entry.getValue();
            final Optional<Node> childNode = graphProcessor.getNodeFromGraph(childId, graph);

            // We only set position for inner nodes that doesn't have a position yet
            // because if the inner node has position, it moves with its parent.
            if (childNode.isPresent()
                    && !hasPositionSet(childNode.get())) {

                final Optional<VertexPosition> parentPosition = layout.getNodePositions()
                        .stream()
                        .filter(p -> p.getId() == parentId)
                        .findFirst();

                if (parentPosition.isPresent()) {

                    final Optional<Node> parentNode = graphProcessor.getNodeFromGraph(parentId, graph);
                    parentNode.ifPresent(parent -> graphProcessor.connect(parent, childNode.get()));
                    final double padding = parentNodesWidth.getOrDefault(parentId, 0.0)
                            + DEFAULT_INNER_HORIZONTAL_PADDING;
                    final VertexPosition position = graphProcessor.getChildVertexPosition(parentId,
                                                                                          childId,
                                                                                          padding,
                                                                                          graph);
                    layout.getNodePositions().add(position);

                    parentNodesWidth.put(parentId, position.getUpperLeft().getX() + DEFAULT_VERTEX_WIDTH);
                }
            }
        }

        parentNodesWidth.forEach((parentId, width) -> {
            final Optional<VertexPosition> parentPosition = layout.getNodePositions()
                    .stream()
                    .filter(p -> p.getId() == parentId)
                    .findFirst();

            parentPosition.ifPresent(position -> {
                final Point2D upperLeft = position.getUpperLeft();
                position.getBottomRight().setX(upperLeft.getX() + width + DEFAULT_INNER_HORIZONTAL_PADDING);
                position.getBottomRight().setY(upperLeft.getY() + DEFAULT_PARENT_NODE_HEIGHT);
            });
        });
    }

    boolean hasPositionSet(final Node node) {
        if (node.getContent() instanceof HasBounds) {
            final Bounds bounds = ((HasBounds) node.getContent()).getBounds();
            if (bounds.hasUpperLeft()
                    && (isXSet(bounds.getUpperLeft()) || isYSet(bounds.getUpperLeft()))) {
                return true;
            }
        }
        return false;
    }

    boolean isXSet(final Bound bound) {
        return bound.hasX() && !isCloseToZero(bound.getX());
    }

    boolean isYSet(final Bound bound) {
        return bound.hasY() && !isCloseToZero(bound.getY());
    }
}