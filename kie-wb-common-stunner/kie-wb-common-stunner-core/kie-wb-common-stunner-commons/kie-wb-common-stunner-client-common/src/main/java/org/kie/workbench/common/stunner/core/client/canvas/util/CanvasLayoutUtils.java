/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.core.client.canvas.util;

import java.util.LinkedList;
import java.util.List;

import javax.enterprise.context.Dependent;

import org.kie.workbench.common.stunner.core.client.canvas.CanvasHandler;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.definition.DefinitionSet;
import org.kie.workbench.common.stunner.core.graph.content.relationship.Child;
import org.kie.workbench.common.stunner.core.graph.content.view.Point2D;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.util.GraphUtils;

import static org.uberfire.commons.validation.PortablePreconditions.checkNotNull;

/**
 * This class is a basic implementation for achieving a simple layout mechanism.
 * It finds an empty area on the canvas where new elements could be place as:
 * - Calling <code>getNextLayoutPosition( CanvasHandler canvasHandler )</code> returns the
 * cartesian coordinates for an empty area found in the diagram's graph that is being managed by the
 * canvas handler instance.
 * - Calling <code>getNextLayoutPosition( CanvasHandler canvasHandler, Element<View<?>> source )</code> returns the
 * cartesian coordinates for an empty area found in the diagram's graph but relative to the given <code>source</code>
 * argument and its parent, if any.
 * In both cases the resulting coordinates are given from the coordinates of the visible element in the graph, which
 * is position is on bottom right rather than the others, plus a given <code>PADDING</code> anb some
 * error margin given by the <code>MARGIN</code> floating point.
 * <p/>
 * TODO: This has to be refactored by the use of a good impl that achieve good dynamic layouts. Probably each
 * Definition Set / Diagram will require a different layout manager as well.
 */
@Dependent
public class CanvasLayoutUtils {

    private static final int PADDING_X = 40;
    private static final int PADDING_Y = 40;

    public static boolean isCanvasRoot(final Diagram diagram,
                                       final Element parent) {
        return null != parent && isCanvasRoot(diagram,
                                              parent.getUUID());
    }

    public static boolean isCanvasRoot(final Diagram diagram,
                                       final String pUUID) {
        final String canvasRoot = diagram.getMetadata().getCanvasRootUUID();
        return (null != canvasRoot && null != pUUID && canvasRoot.equals(pUUID));
    }

    @SuppressWarnings("unchecked")
    public double[] getNext(final CanvasHandler canvasHandler,
                            final double height) {
        Point2D point2D = new Point2D(0,
                                      0);
        return getNext(canvasHandler,
                       height,
                       point2D
        );
    }

    @SuppressWarnings("unchecked")
    public double[] getNext(final CanvasHandler canvasHandler,
                            final double height,
                            final Point2D offset
    ) {

        checkNotNull("canvasHandler",
                     canvasHandler);
        final Bounds bounds = getGraphBounds(canvasHandler);
        final Bounds.Bound ul = bounds.getUpperLeft();
        final String ruuid = canvasHandler.getDiagram().getMetadata().getCanvasRootUUID();
        Point2D min = new Point2D(ul.getX(),
                                  ul.getY());
        if (null != ruuid) {
            Node root = canvasHandler.getDiagram().getGraph().getNode(ruuid);
            return getNext(canvasHandler,
                           root,
                           height,
                           offset,
                           min);
        }
        final Iterable<Node> nodes = canvasHandler.getDiagram().getGraph().nodes();
        if (null != nodes) {
            final Bounds.Bound lr = bounds.getLowerRight();
            final List<Node<View<?>, Edge>> nodeList = new LinkedList<>();
            nodes.forEach(nodeList::add);
            return getNext(canvasHandler,
                           nodeList,
                           height,
                           offset,
                           min,
                           lr.getX() - PADDING_X);
        }
        return new double[]{ul.getX(), ul.getY()};
    }

    @SuppressWarnings("unchecked")
    public double[] getNext(final CanvasHandler canvasHandler,
                            final double h,
                            final Point2D offset,
                            final Point2D min
    ) {
        checkNotNull("canvasHandler",
                     canvasHandler);
        final String ruuid = canvasHandler.getDiagram().getMetadata().getCanvasRootUUID();
        if (null != ruuid) {
            Node root = canvasHandler.getDiagram().getGraph().getNode(ruuid);
            return getNext(canvasHandler,
                           root,
                           h,
                           offset,
                           min
            );
        }
        final Bounds bounds = getGraphBounds(canvasHandler);
        final Bounds.Bound lr = bounds.getLowerRight();
        final Iterable<Node> nodes = canvasHandler.getDiagram().getGraph().nodes();
        if (null != nodes) {
            final List<Node<View<?>, Edge>> nodeList = new LinkedList<>();
            nodes.forEach(nodeList::add);
            return getNext(canvasHandler,
                           nodeList,
                           h,
                           offset,
                           min,
                           lr.getX() - PADDING_X);
        }
        return new double[]{min.getX(), min.getY()};
    }

    @SuppressWarnings("unchecked")
    public double[] getNext(final CanvasHandler canvasHandler,
                            final Node<View<?>, Edge> root,
                            final Node<View<?>, Edge> newNode) {
        final double[] rootBounds = getBoundCoordinates(root.getContent());
        final double[] rootSize = GraphUtils.getNodeSize(root.getContent());
        final double[] newNodeSize = GraphUtils.getNodeSize(newNode.getContent());
        final Point2D newNodePosition = GraphUtils.getPosition(newNode.getContent());
        Point2D[] offset = {new Point2D(0,
                                        0)};
        Point2D[] parentOffset = {new Point2D(0,
                                              0)};
        final Node<View<?>, Edge> parent = (Node<View<?>, Edge>) GraphUtils.getParent(root);
        if ((parent != null) && (GraphUtils.hasChildren(parent))) {
            final Point2D pos = GraphUtils.getPosition(parent.getContent());
            parentOffset[0] = new Point2D(pos.getX() + newNodePosition.getX() + PADDING_X,
                                          pos.getY() + newNodePosition.getY());
            offset[0].setX(parentOffset[0].getX());
        }
        double maxNodeY[] = {0};
        if (root.getOutEdges().size() > 0) {
            root.getOutEdges().forEach(n -> {
                final Node<View<?>, Edge> node = n.getTargetNode();
                final Point2D nodePos = GraphUtils.getPosition(node.getContent());
                final Point2D rootPos = GraphUtils.getPosition(root.getContent());
                if (nodePos.getY() > maxNodeY[0]) {
                    maxNodeY[0] = nodePos.getY();
                    final double[] nodeSize = GraphUtils.getNodeSize(node.getContent());
                    offset[0].setY(maxNodeY[0] + nodeSize[1] - rootPos.getY());
                }
            });
            offset[0].setY(offset[0].getY() + parentOffset[0].getY() + PADDING_Y);
        } else {
            offset[0].setY(parentOffset[0].getY() - (newNodeSize[1] - rootSize[1]) / 2);
        }
        offset[0].setX(offset[0].getX() + PADDING_X);
        final Point2D offsetCoordinates = new Point2D(offset[0].getX(),
                                                      offset[0].getY());

        final Point2D rootBoundsCoordinates = new Point2D(rootBounds[0],
                                                          rootBounds[1]);

        return getNext(canvasHandler,
                       root,
                       rootSize[1],
                       offsetCoordinates,
                       rootBoundsCoordinates
        );
    }

    public double[] getNext(final CanvasHandler canvasHandler,
                            final Node<View<?>, Edge> root,
                            final double h,
                            final Point2D offset,
                            final Point2D min
    ) {
        checkNotNull("canvasHandler",
                     canvasHandler);
        checkNotNull("root",
                     root);
        final List<Edge> outEdges = root.getOutEdges();
        final double[] totHeight = new double[1];
        totHeight[0] = 0;
        if (null != outEdges) {
            final List<Node<View<?>, Edge>> nodes = new LinkedList<>();
            outEdges.stream().forEach(edge -> {
                if (edge instanceof Child
                        && edge.getTargetNode().getContent() instanceof View) {
                    nodes.add(edge.getTargetNode());
                }
            });
            if (!nodes.isEmpty()) {
                final double[] rootBounds = getBoundCoordinates(root.getContent());
                final double[] n = getNext(canvasHandler,
                                           nodes,
                                           h,
                                           offset,
                                           min,
                                           rootBounds[0] - PADDING_X);
                return new double[]{n[0] + PADDING_X, n[1]};
            }
        }
        double[] nextPosition = getNextPositionWithOffset(min,
                                                          offset
        );
        return nextPosition;
    }

    private double[] getNext(final CanvasHandler canvasHandler,
                             final List<Node<View<?>, Edge>> nodes,
                             final double width,
                             final Point2D offset,
                             final Point2D min,
                             final double maxX) {
        checkNotNull("canvasHandler",
                     canvasHandler);
        checkNotNull("nodes",
                     nodes);
        final double[] result = new double[]{min.getX(), min.getY()};
        nodes.stream().forEach(node -> {
            final double[] coordinates = getAbsolute(node);
            result[0] = coordinates[0] >= result[0] ? coordinates[0] : result[0];
            result[1] = coordinates[1] >= result[1] ? coordinates[1] : result[1];
            result[0] = result[0] + offset.getX();
            result[1] = result[1] + offset.getY() + PADDING_Y;
            final Point2D coordinatesPoint = new Point2D(coordinates[0],
                                                         coordinates[1]);
            final double[] r = getNextPositionWithOffset(coordinatesPoint,
                                                         offset
            );
            if ((coordinates[0] + width) >= maxX) {
                result[0] = r[0];
                result[1] = r[1];
            }
        });
        return result;
    }

    private double[] getNextPositionWithOffset(final Point2D nextPosition,
                                               final Point2D offset) {
        final double[] result = new double[]{nextPosition.getX(), nextPosition.getY()};
        double[] res = new double[]{result[0] + offset.getX(), result[1] + offset.getY()};
        return res;
    }

    @SuppressWarnings("unchecked")
    private double[] getAbsolute(final Node<View<?>, Edge> root) {
        final double[] pos = getBoundCoordinates(root.getContent());
        return getAbsolute(root,
                           pos[0],
                           pos[1]);
    }

    @SuppressWarnings("unchecked")
    private double[] getAbsolute(final Node<View<?>, Edge> root,
                                 final double x,
                                 final double y) {
        Element parent = GraphUtils.getParent(root);
        if (null != parent
                && parent instanceof Node
                && parent.getContent() instanceof View) {
            final double[] pos = getBoundCoordinates((View) parent.getContent());
            return getAbsolute((Node<View<?>, Edge>) parent,
                               x + pos[0],
                               y + pos[1]);
        }
        return new double[]{x, y};
    }

    private double[] getBoundCoordinates(final View view) {
        final Bounds bounds = view.getBounds();
        final Bounds.Bound ulBound = bounds.getUpperLeft();
        final Bounds.Bound lrBound = bounds.getLowerRight();
        final double lrX = lrBound.getX();
        final double lrY = ulBound.getY();
        return new double[]{lrX, lrY};
    }

    @SuppressWarnings("unchecked")
    private Bounds getGraphBounds(final CanvasHandler canvasHandler) {
        final Graph<DefinitionSet, ?> graph = canvasHandler.getDiagram().getGraph();
        return graph.getContent().getBounds();
    }
}
