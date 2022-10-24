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

package org.kie.workbench.common.stunner.sw.autolayout;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import com.ait.lienzo.client.core.layout.Layout;
import com.ait.lienzo.client.core.layout.VertexPosition;
import com.ait.lienzo.client.core.layout.graph.OutgoingEdge;
import com.ait.lienzo.client.core.layout.graph.Vertex;
import com.ait.lienzo.client.core.types.Point2D;
import elemental2.dom.DomGlobal;
import elemental2.promise.Promise;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.command.impl.CompositeCommand;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.command.DirectGraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.command.impl.AddControlPointCommand;
import org.kie.workbench.common.stunner.core.graph.command.impl.SetConnectionSourceNodeCommand;
import org.kie.workbench.common.stunner.core.graph.command.impl.SetConnectionTargetNodeCommand;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.definition.DefinitionSet;
import org.kie.workbench.common.stunner.core.graph.content.relationship.Child;
import org.kie.workbench.common.stunner.core.graph.content.view.ControlPoint;
import org.kie.workbench.common.stunner.core.graph.content.view.MagnetConnection;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.content.ChildrenTraverseCallback;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.content.ChildrenTraverseProcessorImpl;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.content.ContentTraverseCallback;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.content.ViewTraverseProcessorImpl;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.tree.TreeWalkTraverseProcessorImpl;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.kie.workbench.common.stunner.sw.autolayout.lienzo.LienzoAutoLayout;
import org.uberfire.client.promise.Promises;

public class AutoLayout {

    private AutoLayout() {
        // Private constructor to prevent instantiation
    }

    @SuppressWarnings("all")
    public static Promise<Node> applyLayout(final Graph graph,
                                            final Node parentNode,
                                            final Promises promises,
                                            final DirectGraphCommandExecutionContext context,
                                            final boolean isSubset,
                                            final String startingNodeId,
                                            final String endingNodeId) {

        final Map<String, Vertex> vertices = loadVertices(graph, parentNode, false);
        final Promise<Layout> autoLayoutPromise = new LienzoAutoLayout().layout(graph, vertices, startingNodeId, endingNodeId);

        return promises.create((resolve, reject) -> autoLayoutPromise
                .then(layout -> {

                    final CompositeCommand.Builder layoutCommands = new CompositeCommand.Builder();

                    updateEdgesDirection(layout, layoutCommands);
                    createControlPoints(layout, layoutCommands);
                    hideNodeIfIsNotConnected(layout, startingNodeId, graph, parentNode);
                    hideNodeIfIsNotConnected(layout, endingNodeId, graph, parentNode);

                    final CompositeCommand<GraphCommandExecutionContext, RuleViolation> all =
                            new CompositeCommand.Builder<>()
                                    .addCommand(layoutCommands.build())
                                    .build();

                    all.execute(context);
                    resolve.onInvoke(parentNode);
                    return null;
                }, error -> {
                    DomGlobal.console.error("Error while performing layout: " + error);
                    resolve.onInvoke(parentNode);
                    return null;
                }));
    }

    static void hideNodeIfIsNotConnected(final Layout layout,
                                         final String nodeId,
                                         final Graph graph,
                                         final Node parentNode) {

        final Optional<VertexPosition> vertexPosition = layout.getVerticesPositions()
                .stream()
                .filter(p -> Objects.equals(p.getId(), nodeId)).findFirst();

        if (!vertexPosition.isPresent()
                || vertexPosition.get().getOutgoingEdges().isEmpty()
                && !hasIncomingConnection(nodeId, layout)) {
            deleteNode(parentNode, nodeId, graph);
        }
    }

    private static void deleteNode(Node parentNode, String nodeId, Graph graph) {
        final Optional outParent = parentNode.getOutEdges().stream()
                .filter(outEdge -> Objects.equals(((Edge) outEdge).getTargetNode().getUUID(), nodeId))
                .findFirst();
        if (outParent.isPresent()) {
            parentNode.getOutEdges().remove(outParent.get());
        }
        graph.removeNode(nodeId);
    }

    static boolean hasIncomingConnection(final String nodeId, final Layout layout) {

        return layout.getVerticesPositions().stream()
                .anyMatch(p -> p.getOutgoingEdges().stream().anyMatch(edge -> Objects.equals(edge.getTarget(), nodeId)));
    }

    static void updateEdgesDirection(final Layout layout,
                                     final CompositeCommand.Builder layoutCommands) {

        final Map<String, VertexPosition> index = layout.getVerticesPositions()
                .stream()
                .collect(Collectors.toMap(VertexPosition::getId, verticesPosition -> verticesPosition, (a, b) -> b));

        for (final VertexPosition verticesPosition : layout.getVerticesPositions()) {
            for (com.ait.lienzo.client.core.layout.Edge outgoingEdge : verticesPosition.getOutgoingEdges()) {

                final Position position = getTargetPositionRelativeToSource(outgoingEdge, index);
                final String edgeIUd = outgoingEdge.getId();
                switch (position) {
                    case ABOVE:
                        updateTargetMagnet(layoutCommands, outgoingEdge, edgeIUd, 3);
                        updateSourceMagnet(layoutCommands, outgoingEdge, edgeIUd, 1);
                        break;

                    case LEFT:
                        updateTargetMagnet(layoutCommands, outgoingEdge, edgeIUd, 2);
                        updateSourceMagnet(layoutCommands, outgoingEdge, edgeIUd, 4);
                        break;

                    case RIGHT:
                        updateTargetMagnet(layoutCommands, outgoingEdge, edgeIUd, 4);
                        updateSourceMagnet(layoutCommands, outgoingEdge, edgeIUd, 2);
                        break;

                    case BELOW:
                        // Do nothing because it's already in the right direction
                        break;
                }
            }
        }
    }

    private static void updateTargetMagnet(final CompositeCommand.Builder layoutCommands,
                                           final com.ait.lienzo.client.core.layout.Edge outgoingEdge,
                                           final String edgeIUd,
                                           final int magnetIndex) {
        layoutCommands.addCommand(new SetConnectionTargetNodeCommand(outgoingEdge.getTarget(),
                                                                     edgeIUd,
                                                                     MagnetConnection.Builder.at(0, 0)) {
            public CommandResult<RuleViolation> execute(GraphCommandExecutionContext context) {
                final Node<? extends View<?>, Edge> targetNode = getTargetNode(context);
                if (null != targetNode) {
                    asMagnetConnection().setIndex(magnetIndex);
                }
                return super.execute(context);
            }

            private MagnetConnection asMagnetConnection() {
                return (MagnetConnection) getConnection();
            }
        });
    }

    private static void updateSourceMagnet(final CompositeCommand.Builder layoutCommands,
                                           final com.ait.lienzo.client.core.layout.Edge outgoingEdge,
                                           final String edgeIUd,
                                           final int magnetIndex) {
        layoutCommands.addCommand(new SetConnectionSourceNodeCommand(outgoingEdge.getSource(),
                                                                     edgeIUd,
                                                                     MagnetConnection.Builder.at(0, 0)) {
            public CommandResult<RuleViolation> execute(GraphCommandExecutionContext context) {
                final Node<? extends View<?>, Edge> sourceNode = getSourceNode(context);
                if (null != sourceNode) {
                    asMagnetConnection().setIndex(magnetIndex);
                }
                return super.execute(context);
            }

            private MagnetConnection asMagnetConnection() {
                return (MagnetConnection) getConnection();
            }
        });
    }

    static Position getTargetPositionRelativeToSource(final com.ait.lienzo.client.core.layout.Edge outgoingEdge,
                                                      final Map<String, VertexPosition> index) {
        final VertexPosition source = index.get(outgoingEdge.getSource());
        final VertexPosition target = index.get(outgoingEdge.getTarget());
        if (target.getY() < source.getY()) {
            return Position.ABOVE;
        }

        if (target.getY() > source.getY()) {
            return Position.BELOW;
        }

        if (target.getX() < source.getX()) {
            return Position.LEFT;
        }

        if (target.getX() > source.getX()) {
            return Position.RIGHT;
        }

        return Position.BELOW;
    }

    public static void createControlPoints(final Layout layout,
                                           final CompositeCommand.Builder layoutCommands) {

        for (final VertexPosition verticesPosition : layout.getVerticesPositions()) {
            for (com.ait.lienzo.client.core.layout.Edge outgoingEdge : verticesPosition.getOutgoingEdges()) {
                int index = -1;
                for (final Point2D bendingPoint : outgoingEdge.getBendingPoints()) {
                    final org.kie.workbench.common.stunner.core.graph.content.view.Point2D point = new org.kie.workbench.common.stunner.core.graph.content.view.Point2D(bendingPoint.getX(), bendingPoint.getY());
                    layoutCommands.addCommand(new AddControlPointCommand(outgoingEdge.getId(), new ControlPoint(point), ++index));
                }
            }
        }
    }

    @SuppressWarnings("all")
    public static Map<String, Vertex> loadVertices(final Graph graph,
                                                   final Node parentNode,
                                                   boolean isSubset) {
        final String rootUUID = parentNode.getUUID();
        final Map<String, Vertex> vertices = new HashMap<>();
        final Map<String, String> nodeContainment = new HashMap<>();

        new ChildrenTraverseProcessorImpl(new TreeWalkTraverseProcessorImpl())
                .setRootUUID(rootUUID)
                .traverse(graph, new ChildrenTraverseCallback<Node<View, Edge>, Edge<Child, Node>>() {

                    private Map<String, Boolean> processedNodes = new HashMap<>();

                    @Override
                    public boolean startNodeTraversal(List<Node<View, Edge>> parents, Node<View, Edge> node) {
                        final Node parent = parents.get(parents.size() - 1);

                        if (parent.getUUID().equals(rootUUID)) {
                            addNode(node);
                        } else {
                            //Containment
                            addChildNode(parent, node);
                        }

                        return true;
                    }

                    private void addNode(final Node<View, Edge> node) {
                        //skip in case the node was already processed
                        if (processedNodes.containsKey(node.getUUID())) {
                            return;
                        }

                        addVertex(node, vertices);

                        addProcessedNode(node);
                    }

                    //Containment
                    private void addChildNode(final Node<View, Edge> parent,
                                              final Node<View, Edge> node) {
                        //skip in case the node was already processed
                        if (processedNodes.containsKey(node.getUUID()) || parent.getUUID().equals(rootUUID)) {
                            return;
                        }

                        //check whether the parent was processed, is must be processed before child node
                        if (!processedNodes.containsKey(parent.getUUID())) {
                            addNode(parent);
                        }

                        addVertex(node, vertices);

                        addNodeParentReference(node, parent);
                        addProcessedNode(node);
                    }

                    private void addProcessedNode(Node<View, Edge> node) {
                        processedNodes.put(node.getUUID(), true);
                    }

                    private void addNodeParentReference(Node<View, Edge> node, Node<View, Edge> parent) {
                        nodeContainment.put(node.getUUID(), parent.getUUID());
                    }

                    @Override
                    public void startGraphTraversal(Graph<DefinitionSet, Node<View, Edge>> graph) {
                    }

                    @Override
                    public void startEdgeTraversal(Edge<Child, Node> edge) {
                    }

                    @Override
                    public void endEdgeTraversal(Edge<Child, Node> edge) {
                    }

                    @Override
                    public void startNodeTraversal(Node<View, Edge> node) {
                    }

                    @Override
                    public void endNodeTraversal(Node<View, Edge> node) {
                    }

                    @Override
                    public void endGraphTraversal() {
                    }
                });

        new ViewTraverseProcessorImpl(new TreeWalkTraverseProcessorImpl()).traverse(graph, new ContentTraverseCallback<View<?>, Node<View, Edge>, Edge<View<?>, Node>>() {
            @Override
            public void startGraphTraversal(Graph<DefinitionSet, Node<View, Edge>> graph) {

            }

            @Override
            public void startEdgeTraversal(Edge<View<?>, Node> edge) {
                if (edge.getSourceNode() == null || edge.getTargetNode() == null) {
                    return;
                }
                final String sourceUuid = edge.getSourceNode().getUUID();
                final String targetUuid = edge.getTargetNode().getUUID();

                final Vertex source = vertices.get(sourceUuid);
                final Vertex target = vertices.get(targetUuid);
                final String edgeUuid = edge.getUUID();

                final OutgoingEdge outgoingEdge = new OutgoingEdge(edgeUuid, target);
                source.getOutgoingEdges().add(outgoingEdge);
            }

            @Override
            public void endEdgeTraversal(Edge<View<?>, Node> edge) {

            }

            @Override
            public void startNodeTraversal(Node<View, Edge> node) {

            }

            @Override
            public void endNodeTraversal(Node<View, Edge> node) {

            }

            @Override
            public void endGraphTraversal() {

            }
        });

        return vertices;
    }

    static void addVertex(final Node<View, Edge> node,
                          final Map<String, Vertex> vertices) {
        final Bounds bounds = node.getContent().getBounds();
        final Vertex v = new Vertex(node.getUUID());
        v.setWidth((int) bounds.getWidth());
        v.setHeight((int) bounds.getHeight());
        vertices.put(v.getId(), v);
    }
}
