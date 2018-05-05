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

package org.kie.workbench.common.stunner.core.graph.processing.index.bounds;

import java.util.List;
import java.util.Optional;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.relationship.Child;
import org.kie.workbench.common.stunner.core.graph.content.view.Point2D;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.content.AbstractChildrenTraverseCallback;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.content.ChildrenTraverseProcessor;
import org.kie.workbench.common.stunner.core.graph.util.GraphUtils;

@Dependent
public class GraphBoundsIndexerImpl implements GraphBoundsIndexer {

    ChildrenTraverseProcessor childrenTraverseProcessor;
    private Graph<View, Node<View, Edge>> graph;
    private String rootUUID = null;

    @Inject
    public GraphBoundsIndexerImpl(final ChildrenTraverseProcessor childrenTraverseProcessor) {
        this.childrenTraverseProcessor = childrenTraverseProcessor;
    }

    @Override
    public GraphBoundsIndexerImpl build(final Graph<View, Node<View, Edge>> graph) {
        this.graph = graph;
        return this;
    }

    @Override
    public Node<View<?>, Edge> getAt(final double x,
                                     final double y) {
        return findElementAt(x,
                             y);
    }

    @Override
    public Node<View<?>, Edge> getAt(final double x,
                                     final double y,
                                     final double width,
                                     final double height,
                                     final Element parentNode) {
        Point2D parentNodePosition;
        double xToCheck = 0;
        double yToCheck = 0;
        if (parentNode != null) {
            parentNodePosition = GraphUtils.getPosition((View) parentNode.asNode().getContent());
            xToCheck = x + parentNodePosition.getX();
            yToCheck = y + parentNodePosition.getY();
        }
        Node<View<?>, Edge> element;
        Point2D[] pointsToCheck = new Point2D[5];
        pointsToCheck[0] = new Point2D(xToCheck,
                                       yToCheck);
        pointsToCheck[1] = new Point2D(xToCheck + width,
                                       yToCheck);
        pointsToCheck[2] = new Point2D(xToCheck + (width / 2),
                                       yToCheck + (height / 2));
        pointsToCheck[3] = new Point2D(xToCheck,
                                       yToCheck + height);
        pointsToCheck[4] = new Point2D(xToCheck + width,
                                       yToCheck + height);
        for (Point2D point : pointsToCheck) {
            element = findElementAt(point.getX(),
                                    point.getY());
            if (element != null) {
                if (element != parentNode) {
                    return element;
                }
            }
        }
        return null;
    }

    @Override
    public double[] getTrimmedBounds() {
        final double[] result = new double[]{Double.MAX_VALUE, Double.MAX_VALUE, 0, 0};
        childrenTraverseProcessor
                .setRootUUID(this.rootUUID)
                .traverse(graph,
                          new GraphBoundIndexerTraverseCallback(new NodeBoundsTraverseCallback() {

                              @Override
                              public void onNodeTraverse(final Node<View, Edge> node,
                                                         final double parentX,
                                                         final double parentY) {
                                  final String uuid = node.getUUID();
                                  final boolean isRoot = null != GraphBoundsIndexerImpl.this.rootUUID
                                          && GraphBoundsIndexerImpl.this.rootUUID.equals(uuid);
                                  if (!isRoot) {
                                      final double[] absCoords = getNodeAbsoluteCoordinates(node,
                                                                                            parentX,
                                                                                            parentY);
                                      final double x = absCoords[0];
                                      final double y = absCoords[1];
                                      final double w = absCoords[2];
                                      final double h = absCoords[3];
                                      if (x < result[0]) {
                                          result[0] = x;
                                      }
                                      if (y < result[1]) {
                                          result[1] = y;
                                      }
                                      if (w > result[2]) {
                                          result[2] = w;
                                      }
                                      if (h > result[3]) {
                                          result[3] = h;
                                      }
                                  }
                              }
                          }));
        return result;
    }

    @SuppressWarnings("unchecked")
    public Node<View<?>, Edge> findElementAt(final double x,
                                             final double y) {
        final Node[] result = new Node[1];
        childrenTraverseProcessor.traverse(graph,
                                           new GraphBoundIndexerTraverseCallback(new NodeBoundsTraverseCallback() {

                                               @Override
                                               public void onNodeTraverse(final Node<View, Edge> node,
                                                                          final double parentX,
                                                                          final double parentY) {
                                                   if (isNodeAt(node,
                                                                parentX,
                                                                parentY,
                                                                x,
                                                                y)) {
                                                       result[0] = node;
                                                   }
                                               }
                                           }));
        return result[0];
    }

    private Point2D getNodeCoordinates(final Node node) {
        if (null != node) {
            final Object content = node.getContent();
            if (content instanceof View) {
                final View viewContent = (View) content;
                return GraphUtils.getPosition(viewContent);
            }
        }
        return null;
    }

    private double[] getNodeAbsoluteCoordinates(final Node node,
                                                final double parentX,
                                                final double parentY) {
        final View content = (View) node.getContent();
        final Bounds bounds = content.getBounds();
        final Bounds.Bound ulBound = bounds.getUpperLeft();
        final Bounds.Bound lrBound = bounds.getLowerRight();
        final double ulX = ulBound.getX() + parentX;
        final double ulY = ulBound.getY() + parentY;
        final double lrX = lrBound.getX() + parentX;
        final double lrY = lrBound.getY() + parentY;
        return new double[]{ulX, ulY, lrX, lrY};
    }

    private boolean isNodeAt(final Node node,
                             final double parentX,
                             final double parentY,
                             final double mouseX,
                             final double mouseY) {
        if (null != rootUUID && node.getUUID().equals(rootUUID)) {
            return true;
        }
        final double[] absoluteCoords = getNodeAbsoluteCoordinates(node,
                                                                   parentX,
                                                                   parentY);
        final double ulX = absoluteCoords[0];
        final double ulY = absoluteCoords[1];
        final double lrX = absoluteCoords[2];
        final double lrY = absoluteCoords[3];
        if (mouseX >= ulX && mouseX <= lrX &&
                mouseY >= ulY && mouseY <= lrY) {
            return true;
        }
        return false;
    }

    @Override
    public GraphBoundsIndexer setRootUUID(final String uuid) {
        this.rootUUID = uuid;
        return this;
    }

    @Override
    public void destroy() {
        this.graph = null;
        this.rootUUID = null;
        this.childrenTraverseProcessor = null;
    }

    private abstract class NodeBoundsTraverseCallback {

        public abstract void onNodeTraverse(final Node<View, Edge> node,
                                            final double parentX,
                                            final double parentY);
    }

    private class GraphBoundIndexerTraverseCallback extends AbstractChildrenTraverseCallback<Node<View, Edge>, Edge<Child, Node>> {

        private final NodeBoundsTraverseCallback callback;

        private GraphBoundIndexerTraverseCallback(final NodeBoundsTraverseCallback callback) {
            this.callback = callback;
        }

        @Override
        public void startNodeTraversal(final Node<View, Edge> node) {
            super.startNodeTraversal(node);
            onStartNodeTraversal(Optional.empty(),
                                 node);
        }

        @Override
        public boolean startNodeTraversal(final List<Node<View, Edge>> parents,
                                          final Node<View, Edge> node) {
            super.startNodeTraversal(parents,
                                     node);
            onStartNodeTraversal(Optional.ofNullable(parents),
                                 node);
            return true;
        }

        private void onStartNodeTraversal(final Optional<List<Node<View, Edge>>> parents,
                                          final Node<View, Edge> node) {
            final double[] parentLocation = {0, 0};
            if (parents.isPresent()) {
                parents.get().forEach(parent -> {
                    final Point2D nodeCoordinates = getNodeCoordinates(parent);
                    if (null != nodeCoordinates) {
                        parentLocation[0] += nodeCoordinates.getX();
                        parentLocation[1] += nodeCoordinates.getY();
                    }
                });
            }
            callback.onNodeTraverse(node,
                                    parentLocation[0],
                                    parentLocation[1]);
        }
    }
}
