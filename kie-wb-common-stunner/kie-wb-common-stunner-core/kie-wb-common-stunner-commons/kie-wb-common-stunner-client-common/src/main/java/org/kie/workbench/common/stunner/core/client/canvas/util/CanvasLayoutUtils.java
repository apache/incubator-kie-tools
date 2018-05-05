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

package org.kie.workbench.common.stunner.core.client.canvas.util;

import javax.annotation.PreDestroy;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasHandler;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.view.Point2D;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewConnector;
import org.kie.workbench.common.stunner.core.graph.processing.index.bounds.GraphBoundsIndexer;
import org.kie.workbench.common.stunner.core.graph.util.GraphUtils;
import org.kie.workbench.common.stunner.core.rule.RuleManager;
import org.kie.workbench.common.stunner.core.rule.RuleSet;
import org.kie.workbench.common.stunner.core.rule.RuleViolations;
import org.kie.workbench.common.stunner.core.rule.context.NodeContainmentContext;
import org.kie.workbench.common.stunner.core.rule.context.impl.RuleContextBuilder;
import org.kie.workbench.common.stunner.core.validation.Violation;

import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;

@Dependent
public class CanvasLayoutUtils {

    private static final int PADDING_X = 40;
    private static final int PADDING_Y = 40;
    private static final int CANVAS_BOTTOM_MARGIN = 15;

    @Inject
    private GraphBoundsIndexer graphBoundsIndexer;

    @Inject
    private RuleManager ruleManager;

    @Inject
    private DefinitionManager definitionManager;

    public CanvasLayoutUtils() {
    }

    CanvasLayoutUtils(GraphBoundsIndexer graphBoundsIndexer,
                      RuleManager ruleManager,
                      DefinitionManager definitionManager) {
        this.graphBoundsIndexer = graphBoundsIndexer;
        this.ruleManager = ruleManager;
        this.definitionManager = definitionManager;
    }

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

    public static int getPaddingX() {
        return PADDING_X;
    }

    public static int getPaddingY() {
        return PADDING_Y;
    }

    @PreDestroy
    public void destroy() {
        graphBoundsIndexer.destroy();
    }

    @SuppressWarnings("unchecked")
    public Point2D getNext(final CanvasHandler canvasHandler,
                           final Node<View<?>, Edge> root,
                           final Node<View<?>, Edge> newNode) {
        final double[] rootBounds = getBoundCoordinates(root.getContent());
        final double[] rootSize = GraphUtils.getNodeSize(root.getContent());
        final double[] newNodeSize = GraphUtils.getNodeSize(newNode.getContent());
        Point2D[] offset = {new Point2D(PADDING_X,
                                        0)};
        Point2D[] parentOffset = {new Point2D(0,
                                              0)};
        double maxNodeY[] = {0};
        if (root.getOutEdges().size() > 0) {
            root.getOutEdges().stream()
                    .filter(e -> e.getContent() instanceof ViewConnector)
                    .filter(e -> null != e.getTargetNode() && !e.getTargetNode().equals(newNode))
                    .forEach(n -> {
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

        final Point2D rootNodeCoordinates = new Point2D(rootBounds[0],
                                                        rootBounds[1]);

        return getNext(canvasHandler,
                       root,
                       rootSize[0],
                       rootSize[1],
                       newNodeSize[0],
                       newNodeSize[1],
                       offsetCoordinates,
                       rootNodeCoordinates
        );
    }

    public Point2D getNext(final CanvasHandler canvasHandler,
                           final Node<View<?>, Edge> root,
                           final double rootNodeWidth,
                           final double rootNodeHeight,
                           final double newNodeWidth,
                           final double newNodeHeight,
                           Point2D offset,
                           Point2D rootNodeCoordinates
    ) {
        checkNotNull("canvasHandler",
                     canvasHandler);
        checkNotNull("root",
                     root);

        final int canvasHeight = canvasHandler.getCanvas().getHeight();
        final int canvasWidth = canvasHandler.getCanvas().getWidth();

        Point2D newPositionUL = getNextPositionWithOffset(rootNodeCoordinates,
                                                          offset);

        graphBoundsIndexer.build(canvasHandler.getDiagram().getGraph());

        Element parentNode = GraphUtils.getParent(root.asNode());

        boolean checkParent = false;
        if (parentNode != null) {
            if (!(isCanvasRoot(canvasHandler.getDiagram(),
                               parentNode.getUUID()))) {
                checkParent = true;
            }
        }

        Node targetNodeContainer = graphBoundsIndexer.getAt(newPositionUL.getX(),
                                                            newPositionUL.getY(),
                                                            newNodeWidth,
                                                            newNodeHeight,
                                                            parentNode);
        boolean canContain = false;
        if (targetNodeContainer != null) {
            canContain = canContain(canvasHandler,
                                    targetNodeContainer,
                                    root);
        }
        if ((!canContain) || isOutOfCanvas(newPositionUL,
                                           newNodeHeight,
                                           canvasHeight) || checkParent) {

            if (checkParent) {
                newPositionUL = getNextPositionFromParent(rootNodeCoordinates,
                                                          offset,
                                                          parentNode,
                                                          rootNodeHeight,
                                                          rootNodeWidth,
                                                          newNodeWidth);
            }
            while (((!isCanvasPositionAvailable(graphBoundsIndexer,
                                                newPositionUL,
                                                newNodeWidth,
                                                newNodeHeight,
                                                parentNode)) &&
                    !canContain)
                    &&
                    (newPositionUL.getY() < canvasHeight) && (newPositionUL.getX() < canvasWidth)
                    ||
                    isOutOfCanvas(newPositionUL,
                                  newNodeHeight,
                                  canvasHeight)) {

                parentNode = GraphUtils.getParent(root.asNode());
                checkParent = false;

                if (parentNode != null) {
                    if (!(isCanvasRoot(canvasHandler.getDiagram(),
                                       parentNode.getUUID()))) {
                        checkParent = true;
                    }
                }

                double[] nodeAtPositionSize;

                nodeAtPositionSize = getNodeSizeAt(graphBoundsIndexer,
                                                   newPositionUL,
                                                   newNodeWidth,
                                                   newNodeHeight,
                                                   parentNode);

                if (checkParent) {
                    final Node targetNodeNewPos = graphBoundsIndexer.getAt(newPositionUL.getX(),
                                                                           newPositionUL.getY(),
                                                                           newNodeWidth,
                                                                           newNodeHeight,
                                                                           parentNode);
                    if (parentNode != targetNodeNewPos) {
                        offset.setY(offset.getY() + PADDING_Y);
                    }
                } else {

                    if (nodeAtPositionSize == null) {
                        nodeAtPositionSize = new double[]{0.0d};
                        offset.setY(offset.getY() + PADDING_Y);
                    } else {
                        offset.setY(offset.getY() + nodeAtPositionSize[1] + PADDING_Y);
                    }
                }
                newPositionUL = getNextPositionWithOffset(rootNodeCoordinates,
                                                          offset);

                if (isOutOfCanvas(newPositionUL,
                                  newNodeHeight,
                                  canvasHeight)) {
                    rootNodeCoordinates.setY(0);
                    offset.setY(PADDING_Y);
                    offset.setX(offset.getX() + nodeAtPositionSize[0] + PADDING_X);
                    newPositionUL = getNextPositionWithOffset(rootNodeCoordinates,
                                                              offset);
                }

                if (checkParent) {
                    newPositionUL = getNextPositionFromParent(rootNodeCoordinates,
                                                              offset,
                                                              parentNode,
                                                              rootNodeHeight,
                                                              rootNodeWidth,
                                                              newNodeWidth);
                }

                targetNodeContainer = graphBoundsIndexer.getAt(newPositionUL.getX(),
                                                               newPositionUL.getY(),
                                                               newNodeWidth,
                                                               newNodeHeight,
                                                               parentNode);
                canContain = targetNodeContainer == null || canContain(canvasHandler, targetNodeContainer, root);
            }
        } else {
            if (checkParent) {
                newPositionUL = getNextPositionFromParent(rootNodeCoordinates,
                                                          offset,
                                                          parentNode,
                                                          rootNodeHeight,
                                                          rootNodeWidth,
                                                          newNodeWidth);
            } else {
                newPositionUL = getNextPositionWithOffset(rootNodeCoordinates,
                                                          offset);
            }
        }

        return newPositionUL;
    }

    private Point2D getNextPositionFromParent(final Point2D rootNodeCoordinates,
                                              final Point2D offset,
                                              final Element parentNode,
                                              final double rootNodeHeight,
                                              final double rootNodeWidth,
                                              final double nodeWidth) {

        final Point2D nextPosition = getNextPositionWithOffset(rootNodeCoordinates,
                                                               offset);
        final double[] parentNodeSize = GraphUtils.getNodeSize((View) parentNode.getContent());
        final Point2D parentPosition = GraphUtils.getPosition((View) parentNode.getContent());
        double newPositionToCheckX = (rootNodeCoordinates.getX() + rootNodeWidth + PADDING_X + nodeWidth);
        double parentPositionToCheckX = parentNodeSize[0] + parentPosition.getX();

        if (newPositionToCheckX > parentPositionToCheckX) {
            nextPosition.setX(parentPosition.getX() + PADDING_X);
            nextPosition.setY(rootNodeCoordinates.getY() + rootNodeHeight + PADDING_Y + offset.getY());
        }

        return nextPosition;
    }

    private boolean canContain(final CanvasHandler canvasHandler,
                               final Node container,
                               final Node candidate) {
        boolean canContain = true;
        NodeContainmentContext containmentContext = RuleContextBuilder.GraphContexts.containment(canvasHandler.getDiagram().getGraph(),
                                                                                                 container,
                                                                                                 candidate);
        String definitionSetId = canvasHandler.getDiagram().getMetadata().getDefinitionSetId();
        Object definitionSet = definitionManager.definitionSets().getDefinitionSetById(definitionSetId);
        RuleSet ruleSet = definitionManager.adapters().forRules().getRuleSet(definitionSet);

        RuleViolations violations = ruleManager.evaluate(ruleSet,
                                                         containmentContext);

        if (violations.violations(Violation.Type.ERROR).iterator().hasNext()) {
            canContain = false;
        }

        return canContain;
    }

    private boolean isOutOfCanvas(Point2D newPositionUL,
                                  double newNodeHeight,
                                  double canvasHeight) {
        return newPositionUL.getY() + newNodeHeight > canvasHeight - CANVAS_BOTTOM_MARGIN;
    }

    private Point2D getNextPositionWithOffset(final Point2D nextPosition,
                                              final Point2D offset) {
        return new Point2D(nextPosition.getX() + offset.getX(),
                           nextPosition.getY() + offset.getY());
    }

    private double[] getNodeSizeAt(final GraphBoundsIndexer graphBoundsIndexer,
                                   final Point2D position,
                                   final double w,
                                   final double h,
                                   final Element parentNode) {

        final Node targetNode = graphBoundsIndexer.getAt(position.getX(),
                                                         position.getY(),
                                                         w,
                                                         h,
                                                         parentNode);
        if (targetNode != null) {
            return GraphUtils.getNodeSize((View) targetNode.getContent());
        } else {
            return null;
        }
    }

    private boolean isCanvasPositionAvailable(final GraphBoundsIndexer graphBoundsIndexer,
                                              final Point2D positionUL,
                                              final double width,
                                              final double height,
                                              final Element parentNode
    ) {
        final Node targetNode = graphBoundsIndexer.getAt(positionUL.getX(),
                                                         positionUL.getY(),
                                                         width,
                                                         height,
                                                         parentNode);
        return targetNode == null;
    }

    private double[] getBoundCoordinates(final View view) {
        final Bounds bounds = view.getBounds();
        final Bounds.Bound ulBound = bounds.getUpperLeft();
        final Bounds.Bound lrBound = bounds.getLowerRight();
        final double lrX = lrBound.getX();
        final double lrY = ulBound.getY();
        return new double[]{lrX, lrY};
    }
}