/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.core.client.util;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.OptionalInt;
import java.util.Set;
import java.util.stream.Stream;

import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.Canvas;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasHandler;
import org.kie.workbench.common.stunner.core.client.shape.EdgeShape;
import org.kie.workbench.common.stunner.core.client.shape.MutationContext;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.client.shape.impl.ConnectorShape;
import org.kie.workbench.common.stunner.core.client.shape.view.BoundingBox;
import org.kie.workbench.common.stunner.core.client.shape.view.HasDragBounds;
import org.kie.workbench.common.stunner.core.client.shape.view.HasRadius;
import org.kie.workbench.common.stunner.core.client.shape.view.HasSize;
import org.kie.workbench.common.stunner.core.client.shape.view.ShapeView;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.relationship.Child;
import org.kie.workbench.common.stunner.core.graph.content.view.Connection;
import org.kie.workbench.common.stunner.core.graph.content.view.MagnetConnection;
import org.kie.workbench.common.stunner.core.graph.content.view.Point2D;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewConnector;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.content.AbstractChildrenTraverseCallback;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.content.ChildrenTraverseProcessorImpl;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.tree.TreeWalkTraverseProcessorImpl;

public class ShapeUtils {

    @SuppressWarnings("unchecked")
    public static void applyConnections(final Edge<? extends ViewConnector<?>, Node> edge,
                                        final AbstractCanvasHandler canvasHandler,
                                        final MutationContext mutationContext) {
        final Canvas<?> canvas = canvasHandler.getCanvas();
        final Node sourceNode = edge.getSourceNode();
        final Node targetNode = edge.getTargetNode();
        final Shape<?> source = sourceNode != null ? canvas.getShape(sourceNode.getUUID()) : null;
        final Shape<?> target = targetNode != null ? canvas.getShape(targetNode.getUUID()) : null;
        EdgeShape connector = (EdgeShape) canvas.getShape(edge.getUUID());
        if (connector != null) {
            connector.applyConnections(edge,
                                       source != null ? source.getShapeView() : null,
                                       target != null ? target.getShapeView() : null,
                                       mutationContext);
        }
        updateEdgeConnections(edge, canvasHandler);
    }

    public static ConnectorShape getConnectorShape(Edge edge, CanvasHandler canvasHandler) {
        validateConnector(edge);
        return (ConnectorShape) canvasHandler.getCanvas().getShape(edge.getUUID());
    }

    private static void validateConnector(Edge edge) {
        Objects.requireNonNull(edge, "Edge should not be null");
        if (!(edge.getContent() instanceof ViewConnector)) {
            throw new IllegalArgumentException("Edge content should be a ViewConnector. Edge UUID: " + edge.getUUID());
        }
    }

    @SuppressWarnings("unchecked")
    public static void updateEdgeConnections(final Edge<? extends ViewConnector<?>, Node> edge,
                                             final AbstractCanvasHandler context) {
        final Node source = edge.getSourceNode();
        final Node target = edge.getTargetNode();
        edge.getContent().getSourceConnection()
                .ifPresent(connection -> updateEdgeConnection(context,
                                                              connection,
                                                              source));
        edge.getContent().getTargetConnection()
                .ifPresent(connection -> updateEdgeConnection(context,
                                                              connection,
                                                              target));
    }

    @SuppressWarnings("unchecked")
    public static void updateEdgeConnection(final AbstractCanvasHandler context,
                                            final Connection connection,
                                            final Node<? extends View<?>, Edge> node) {
        if (null != node &&
                null == connection.getLocation() &&
                connection instanceof MagnetConnection) {
            final MagnetConnection magnetConnection = (MagnetConnection) connection;
            final OptionalInt magnetIndex = magnetConnection.getMagnetIndex();
            if (magnetIndex.orElse(-1) == MagnetConnection.MAGNET_CENTER) {
                final Shape<ShapeView<?>> nodeShape = context.getCanvas().getShape(node.getUUID());
                final BoundingBox boundingBox = nodeShape.getShapeView().getBoundingBox();
                magnetConnection.setLocation(new Point2D(boundingBox.getWidth() / 2,
                                                         boundingBox.getHeight() / 2));
            }
        }
    }

    public static void enforceLocationConstraints(final ShapeView shape,
                                                  final Bounds bounds) {
        if (shape instanceof HasDragBounds) {
            ((HasDragBounds) shape).setDragBounds(bounds);
        }
    }

    @SuppressWarnings("unchecked")
    public static void moveViewConnectorsToTop(final AbstractCanvasHandler canvasHandler,
                                               final Node<?, Edge> node) {
        final Set<String> connectorIds = new HashSet<>();
        // Obtain all view connectors for the node and its children.
        appendViewConnectorIds(connectorIds,
                               node);
        new ChildrenTraverseProcessorImpl(new TreeWalkTraverseProcessorImpl())
                .setRootUUID(node.getUUID())
                .traverse(canvasHandler.getGraphIndex().getGraph(),
                          new AbstractChildrenTraverseCallback<Node<View, Edge>, Edge<Child, Node>>() {
                              @Override
                              public boolean startNodeTraversal(final List<Node<View, Edge>> parents,
                                                                final Node<View, Edge> childNode) {
                                  appendViewConnectorIds(connectorIds,
                                                         childNode);
                                  return true;
                              }
                          });
        // Update connector's view.
        connectorIds.forEach(id -> moveShapeToTop(canvasHandler,
                                                  id));
    }

    public static double getRadiusForBoundingBox(final double width,
                                                 final double height) {
        return (width > height ?
                width / 2 :
                height / 2);
    }

    public static void setSizeFromBoundingBox(final ShapeView view,
                                              final double boundingBoxWidth,
                                              final double boundingBoxHeight) {
        if (view instanceof HasSize) {
            ((HasSize) view).setSize(boundingBoxWidth, boundingBoxHeight);
        } else if (view instanceof HasRadius) {
            final double radius = getRadiusForBoundingBox(boundingBoxWidth, boundingBoxHeight);
            ((HasRadius) view).setRadius(radius);
        }
    }

    /**
     * Returns the distance between two points in a dual axis cartesian graph.
     */
    public static double dist(final double x0,
                              final double y0,
                              final double x1,
                              final double y1) {
        final double dx = Math.abs(x1 - x0);
        final double dy = Math.abs(y1 - y0);
        return (Math.sqrt((dx * dx) + (dy * dy)));
    }

    private static void appendViewConnectorIds(final Set<String> result,
                                               final Node<?, Edge> node) {
        Stream.concat(node.getInEdges().stream(),
                      node.getOutEdges().stream())
                .filter(e -> e.getContent() instanceof ViewConnector)
                .forEach(e -> result.add(e.getUUID()));
    }

    private static void moveShapeToTop(final AbstractCanvasHandler canvasHandler,
                                       final String uuid) {
        final Shape shape = canvasHandler.getCanvas().getShape(uuid);
        if (null != shape) {
            shape.getShapeView().moveToTop();
        }
    }
}
