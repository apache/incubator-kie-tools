/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
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
import java.util.Set;
import java.util.stream.Stream;

import com.google.gwt.core.client.GWT;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.Canvas;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasHandler;
import org.kie.workbench.common.stunner.core.client.shape.EdgeShape;
import org.kie.workbench.common.stunner.core.client.shape.MutationContext;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.relationship.Child;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewConnector;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.content.AbstractChildrenTraverseCallback;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.content.ChildrenTraverseProcessorImpl;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.tree.TreeWalkTraverseProcessorImpl;

public class ShapeUtils {

    public static String getModuleAbsolutePath(final String path) {
        return GWT.getModuleBaseURL() + path;
    }

    @SuppressWarnings("unchecked")
    public static void applyConnections(final Edge<?, ?> edge,
                                        final CanvasHandler canvasHandler,
                                        final MutationContext mutationContext) {
        final Canvas<?> canvas = canvasHandler.getCanvas();
        final Node sourceNode = edge.getSourceNode();
        final Node targetNode = edge.getTargetNode();
        final Shape<?> source = sourceNode != null ? canvas.getShape(sourceNode.getUUID()) : null;
        final Shape<?> target = targetNode != null ? canvas.getShape(targetNode.getUUID()) : null;
        EdgeShape connector = (EdgeShape) canvas.getShape(edge.getUUID());
        connector.applyConnections(edge,
                                   source != null ? source.getShapeView() : null,
                                   target != null ? target.getShapeView() : null,
                                   mutationContext);
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
        connectorIds.stream()
                .forEach(id -> moveShapeToTop(canvasHandler,
                                              id));
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