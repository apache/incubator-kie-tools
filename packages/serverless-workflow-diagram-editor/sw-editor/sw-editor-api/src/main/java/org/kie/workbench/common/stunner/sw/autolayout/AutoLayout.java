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
import org.kie.workbench.common.stunner.core.graph.content.view.ViewConnector;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.content.ChildrenTraverseCallback;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.content.ChildrenTraverseProcessorImpl;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.content.ContentTraverseCallback;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.content.ViewTraverseProcessorImpl;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.tree.TreeWalkTraverseProcessorImpl;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.kie.workbench.common.stunner.sw.autolayout.lienzo.LienzoAutoLayout;
import org.kie.workbench.common.stunner.sw.definition.End;
import org.uberfire.client.promise.Promises;

public class AutoLayout {

    public static final double X_DEVIATION = 102d;

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

                    moveEndNodesX(graph, X_DEVIATION);

                    applyOrthogonalLinesBehaviour(graph);

                    resolve.onInvoke(parentNode);
                    return null;
                }, error -> {
                    resolve.onInvoke(parentNode);
                    return null;
                }));
    }

    @SuppressWarnings("all")
    static void moveEndNodesX(Graph graph, final double x) {
        Iterable<Node> nodes = graph.nodes();
        nodes.forEach(node -> {
            if (node.getContent() instanceof View) {
                final View content = (View) node.getContent();
                if (content.getDefinition() instanceof End) {
                    final Bounds bounds = content.getBounds();
                    final Bounds newBounds = Bounds.create(bounds.getUpperLeft().getX() + x,
                                                           bounds.getUpperLeft().getY(),
                                                           bounds.getLowerRight().getX() + x,
                                                           bounds.getLowerRight().getY());
                    content.setBounds(newBounds);
                }
            }
        });
    }

    @SuppressWarnings("all")
    static void applyOrthogonalLinesBehaviour(Graph graph) {
        Iterable<Node> nodes = graph.nodes();
        nodes.forEach(node -> {
            if (node.getContent() instanceof View) {
                final List<Edge> inEdges = (List<Edge>) node.getInEdges().stream()
                        .filter(e -> ((Edge) e).getContent() instanceof ViewConnector)
                        .collect(Collectors.toList());
                final List<Edge> outEdges = (List<Edge>) node.getOutEdges().stream()
                        .filter(e -> ((Edge) e).getContent() instanceof ViewConnector)
                        .collect(Collectors.toList());

                // Incoming connections
                adjustIncomingConnections(node, inEdges);

                // Outgoing connections
                adjustOutgoingConnections(node, inEdges, outEdges);
            }
        });
    }

    @SuppressWarnings("all")
    static void adjustIncomingConnections(final Node node, final List<Edge> inEdges) {
        if (inEdges.size() >= 1) {
            for (int i = 0; i < inEdges.size(); i++) {
                Edge edge = inEdges.get(i);
                ViewConnector content = (ViewConnector) edge.getContent();
                MagnetConnection targetConnection = (MagnetConnection) content.getTargetConnection().get();
                Node sourceNode = edge.getSourceNode();

                adjustConnectionWithoutControlPoints(i,
                                                     sourceNode,
                                                     targetConnection,
                                                     inEdges);
            }
        }
    }

    @SuppressWarnings("all")
    static void adjustOutgoingConnections(final Node node,
                                          final List<Edge> inEdges,
                                          final List<Edge> outEdges) {
        if (node.getContent() instanceof View) {
            if (outEdges.size() >= 1) {
                for (int i = 0; i < outEdges.size(); i++) {
                    Edge edge = outEdges.get(i);
                    ViewConnector content = (ViewConnector) edge.getContent();
                    MagnetConnection sourceConnection = (MagnetConnection) content.getSourceConnection().get();
                    MagnetConnection targetConnection = (MagnetConnection) content.getTargetConnection().get();
                    org.kie.workbench.common.stunner.core.graph.content.view.Point2D sourceLocation = sourceConnection.getLocation();
                    org.kie.workbench.common.stunner.core.graph.content.view.Point2D targetLocation = targetConnection.getLocation();
                    ControlPoint[] controlPoints = content.getControlPoints();
                    Node sourceNode = edge.getSourceNode();
                    View sourceContent = (View) sourceNode.getContent();
                    Bounds sourceBounds = sourceContent.getBounds();
                    Node targetNode = edge.getTargetNode();
                    View targetContent = (View) targetNode.getContent();
                    Bounds targetBounds = targetContent.getBounds();

                    // Handle connection / magnet default settings.
                    sourceConnection.setAuto(false);
                    sourceConnection.setIndex(MagnetConnection.MAGNET_CENTER);

                    if (isBackwards(sourceBounds.getY(), targetBounds.getY())) {
                        // single backward connections on top
                        if (outEdges.size() == 1) {
                            sourceConnection.setAuto(false);
                            sourceConnection.setIndex(MagnetConnection.MAGNET_TOP);
                        }

                        adjustBackwardConnections(sourceConnection,
                                                  targetConnection,
                                                  controlPoints,
                                                  outEdges.size());

                        // Handle backward connections crossing several (>1) layers.
                        adjustBackwardConnectionsCrossingMultipleLayers(i,
                                                                        controlPoints,
                                                                        sourceBounds,
                                                                        targetBounds,
                                                                        sourceConnection,
                                                                        targetConnection);
                    } else {
                        // single connections on bottom
                        if (outEdges.size() == 1) {
                            sourceConnection.setAuto(false);
                            sourceConnection.setIndex(MagnetConnection.MAGNET_BOTTOM);
                        }

                        // handle connections without CPs
                        adjustConnectionWithoutControlPoints(i,
                                                             sourceNode,
                                                             targetConnection,
                                                             inEdges);

                        // Handle connections crossing several (>1) layers.
                        adjustConnectionsCrossingMultipleLayers(i,
                                                                content,
                                                                controlPoints,
                                                                sourceBounds,
                                                                targetBounds,
                                                                sourceConnection,
                                                                targetConnection);
                    }
                }
            }
        }
    }

    // Handle connectors crossing several (>1) layers.
    static void adjustConnectionsCrossingMultipleLayers(final int edgeIndex,
                                                        final ViewConnector content,
                                                        final ControlPoint[] controlPoints,
                                                        final Bounds sourceBounds,
                                                        final Bounds targetBounds,
                                                        MagnetConnection sourceConnection,
                                                        MagnetConnection targetConnection) {
        if (controlPoints.length > 0) {
            double padding = 40d;
            double maxx = 0;
            for (int j = 0; j < controlPoints.length; j++) {
                if (j == 0) {
                    boolean isTopBottom = sourceBounds.getY() < controlPoints[j].getLocation().getY();
                    double ty = sourceBounds.getY() +
                            ((padding / 2) * edgeIndex) +
                            (isTopBottom ? sourceBounds.getHeight() + padding : -padding);
                    controlPoints[j].getLocation().setY(ty);

                    sourceConnection.setIndex(MagnetConnection.MAGNET_BOTTOM);
                    sourceConnection.setAuto(false);
                } else if (j == controlPoints.length - 1) {
                    boolean isTopBottom = targetBounds.getY() < controlPoints[j].getLocation().getY();
                    double ty = targetBounds.getY() +
                            -(padding * edgeIndex) +
                            (isTopBottom ? targetBounds.getHeight() + padding : -padding);
                    controlPoints[j].getLocation().setY(ty);
                    targetConnection.setIndex(MagnetConnection.MAGNET_CENTER);
                    targetConnection.setAuto(false);
                }

                ControlPoint cp = controlPoints[j];
                if (cp.getLocation().getX() > maxx) {
                    maxx = cp.getLocation().getX();
                    for (int k = j; k >= 0; k--) {
                        controlPoints[k].getLocation().setX(maxx);
                    }
                }
                cp.getLocation().setX(maxx);
            }

            content.setControlPoints(new ControlPoint[]{
                    controlPoints[0].copy(),
                    controlPoints[controlPoints.length - 1].copy()
            });
        }
    }

    // Handle backward connectors crossing several (>1) layers.
    static void adjustBackwardConnectionsCrossingMultipleLayers(final int edgeIndex,
                                                                final ControlPoint[] controlPoints,
                                                                final Bounds sourceBounds,
                                                                final Bounds targetBounds,
                                                                MagnetConnection sourceConnection,
                                                                MagnetConnection targetConnection) {
        if (controlPoints.length > 0) {
            double padding = 40d;
            double maxx = 0;
            for (int j = 0; j < controlPoints.length; j++) {
                if (j == 0) {
                    // Figure out if it goes to the left, to the right or up
                    // Source and target are horizontally aligned
                    if (Math.abs(controlPoints[j].getLocation().getX() - ((sourceBounds.getX() + sourceBounds.getWidth())) / 2) > padding &&
                            Math.abs(sourceBounds.getX() - targetBounds.getX()) < targetBounds.getWidth()) {
                        targetConnection.setAuto(false);
                        targetConnection.setIndex(MagnetConnection.MAGNET_CENTER);

                        controlPoints[j].getLocation().setY(sourceBounds.getUpperLeft().getY() - (padding * 2));
                    } else {
                        boolean isBottomTop = targetBounds.getY() < controlPoints[j].getLocation().getY();
                        double ty = targetBounds.getY() +
                                (isBottomTop ? targetBounds.getHeight() + padding : -padding);

                        if (Math.abs(controlPoints[0].getLocation().getX() - sourceBounds.getX()) < padding) {
                            sourceConnection.setAuto(false);
                            sourceConnection.setIndex(MagnetConnection.MAGNET_LEFT);
                        } else {
                            sourceConnection.setAuto(false);
                            sourceConnection.setIndex(MagnetConnection.MAGNET_CENTER);
                            targetConnection.setAuto(false);
                            targetConnection.setIndex(MagnetConnection.MAGNET_LEFT);
                            ty = sourceBounds.getY() - padding;
                            controlPoints[j].getLocation().setX(controlPoints[j].getLocation().getX() - (padding * 2));
                        }

                        controlPoints[j].getLocation().setY(ty);
                    }
                } else if (j == controlPoints.length - 1) {
                    boolean isTopBottom = targetBounds.getY() < controlPoints[j].getLocation().getY();
                    double ty = targetBounds.getY() +
                            -(padding * edgeIndex) +
                            (isTopBottom ? targetBounds.getHeight() + padding : -padding);
                    controlPoints[j].getLocation().setY(ty);
                    sourceConnection.setAuto(false);
                    sourceConnection.setIndex(MagnetConnection.MAGNET_CENTER);
                    targetConnection.setAuto(false);
                    targetConnection.setIndex(MagnetConnection.MAGNET_CENTER);
                }

                ControlPoint cp = controlPoints[j];
                if (cp.getLocation().getX() > maxx) {
                    maxx = cp.getLocation().getX();
                    for (int k = j; k >= 0; k--) {
                        controlPoints[k].getLocation().setX(maxx);
                    }
                }

                cp.getLocation().setX(maxx);
            }
        }
    }

    // check if the connector points up
    static boolean isBackwards(final double sourceY, final double targetY) {
        return sourceY > targetY;
    }

    // Handle backward connections to upper layer (may overlap with incoming connectors, if any).
    static void adjustBackwardConnections(final MagnetConnection sourceConnection,
                                          final MagnetConnection targetConnection,
                                          final ControlPoint[] controlPoints,
                                          final int edgesCount) {
        if (edgesCount > 0) {
            if (controlPoints.length == 0) {
                sourceConnection.setAuto(false);
                sourceConnection.setIndex(MagnetConnection.MAGNET_LEFT);
            }
            targetConnection.setAuto(false);
            targetConnection.setIndex(MagnetConnection.MAGNET_CENTER);
        }
    }

    static void adjustConnectionWithoutControlPoints(final int edgeIndex,
                                                     final Node sourceNode,
                                                     final MagnetConnection targetConnection,
                                                     final List<Edge> inEdges) {
        if (inEdges.size() == 1) {
            targetConnection.setAuto(false);
            targetConnection.setIndex(MagnetConnection.MAGNET_TOP);
        } else if (isSameSource(sourceNode, inEdges, edgeIndex)) {
            // Node with more than one connector with same target
            targetConnection.setAuto(false);
            targetConnection.setIndex(MagnetConnection.MAGNET_TOP);
        } else {
            targetConnection.setAuto(false);
            targetConnection.setIndex(MagnetConnection.MAGNET_CENTER);
        }
    }

    @SuppressWarnings("all")
    static boolean isSameSource(final Node sourceNode, final List<Edge> inEdges, final int currentIndex) {
        for (int k = currentIndex; k < inEdges.size(); k++) {
            if (k != currentIndex) {
                Node tempNode = inEdges.get(k).getSourceNode();
                if (sourceNode == tempNode) {
                    return true;
                }
            }
        }
        return false;
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

    @SuppressWarnings("all")
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

    @SuppressWarnings("all")
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

    @SuppressWarnings("all")
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

    @SuppressWarnings("all")
    public static void createControlPoints(final Layout layout,
                                           final CompositeCommand.Builder layoutCommands) {

        for (final VertexPosition verticesPosition : layout.getVerticesPositions()) {
            for (com.ait.lienzo.client.core.layout.Edge outgoingEdge : verticesPosition.getOutgoingEdges()) {
                int index = -1;
                for (final Point2D bendingPoint : outgoingEdge.getBendingPoints()) {
                    final org.kie.workbench.common.stunner.core.graph.content.view.Point2D point = new org.kie.workbench.common.stunner.core.graph.content.view.Point2D(bendingPoint.getX(), bendingPoint.getY());
                    ControlPoint cp = new ControlPoint(point);
                    AddControlPointCommand command = new AddControlPointCommand(outgoingEdge.getId(), cp, ++index);
                    layoutCommands.addCommand(command);
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
