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


package org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.bpmn2.di.BPMNEdge;
import org.eclipse.dd.dc.Point;
import org.kie.workbench.common.stunner.bpmn.client.marshall.MarshallingMessage;
import org.kie.workbench.common.stunner.bpmn.client.marshall.MarshallingMessageKeys;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.Result;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.properties.EdgePropertyReader;
import org.kie.workbench.common.stunner.bpmn.definition.AdHocSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNBaseInfo;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNViewDefinition;
import org.kie.workbench.common.stunner.bpmn.definition.BaseSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.EmbeddedSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.EventSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.Lane;
import org.kie.workbench.common.stunner.bpmn.definition.property.dimensions.Height;
import org.kie.workbench.common.stunner.bpmn.definition.property.dimensions.RectangleDimensionsSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.dimensions.Width;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.Name;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.Bound;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.view.Connection;
import org.kie.workbench.common.stunner.core.graph.content.view.MagnetConnection;
import org.kie.workbench.common.stunner.core.graph.content.view.Point2D;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.validation.Violation;

public class ProcessPostConverter {

    private static double PRECISION = 1;
    private PostConverterContext context;

    ProcessPostConverter() {
    }

    public Result<BpmnNode> postConvert(BpmnNode rootNode, DefinitionResolver definitionResolver) {
        if (definitionResolver.getResolutionFactor() != 1) {
            context = PostConverterContext.of(rootNode, definitionResolver.isJbpm());
            adjustAllEdgeConnections(rootNode, true);
            if (context.hasCollapsedNodes()) {

                List<LaneInfo> laneInfos = new ArrayList<>();
                new ArrayList<>(rootNode.getChildren()).stream()
                        .filter(ProcessPostConverter::isLane)
                        .filter(BpmnNode::hasChildren)
                        .forEach(lane -> {
                            LaneInfo laneInfo = new LaneInfo(lane, Padding.of(lane), new ArrayList<>(lane.getChildren()));
                            laneInfos.add(laneInfo);
                            laneInfo.getChildren().forEach(child -> child.setParent(rootNode));
                            rootNode.removeChild(lane);
                        });

                rootNode.getChildren().stream()
                        .filter(ProcessPostConverter::isSubProcess)
                        .forEach(this::postConvertSubProcess);

                List<BpmnNode> resizedChildren = context.getResizedChildren(rootNode);
                resizedChildren.forEach(resizedChild -> applyNodeResize(rootNode, resizedChild));

                laneInfos.forEach(laneInfo -> {
                    laneInfo.getLane().setParent(rootNode);
                    laneInfo.getChildren().forEach(node -> node.setParent(laneInfo.getLane()));
                    adjustLane(laneInfo.getLane(), laneInfo.getPadding());
                });
                adjustAllEdgeConnections(rootNode, false);

                return Result.success(rootNode, resizedChildren.stream()
                        .map(n -> MarshallingMessage.builder()
                                .message("Collapsed node was resized " + n.value().getContent().getDefinition())
                                .messageKey(MarshallingMessageKeys.collapsedElementExpanded)
                                .messageArguments(n.value().getUUID(),
                                                  Optional.ofNullable(n.value())
                                                          .map(Node::getContent)
                                                          .map(View::getDefinition)
                                                          .map(BPMNViewDefinition::getGeneral)
                                                          .map(BPMNBaseInfo::getName)
                                                          .map(Name::getValue)
                                                          .orElse(""))
                                .type(Violation.Type.WARNING)
                                .build())
                        .toArray(MarshallingMessage[]::new));
            }
        }
        return Result.success(rootNode);
    }

    private static class PostConverterContext {

        private HashMap<BpmnNode, Boolean> collapsedNodes;
        private HashMap<BpmnNode, Boolean> resizedNodes = new HashMap<>();

        private PostConverterContext(HashMap<BpmnNode, Boolean> collapsedNodes) {
            this.collapsedNodes = collapsedNodes;
        }

        public boolean isCollapsed(BpmnNode node) {
            return collapsedNodes.getOrDefault(node, false);
        }

        public void setCollapsed(BpmnNode node, boolean collapsed) {
            collapsedNodes.put(node, collapsed);
        }

        public boolean hasCollapsedNodes() {
            return collapsedNodes.values().stream()
                    .filter(value -> value).findFirst()
                    .orElse(false);
        }

        public boolean isResized(BpmnNode node) {
            return resizedNodes.getOrDefault(node, false);
        }

        public void setResized(BpmnNode node, boolean resized) {
            resizedNodes.put(node, resized);
        }

        public List<BpmnNode> getResizedChildren(BpmnNode node) {
            return node.getChildren().stream()
                    .filter(this::isResized)
                    .collect(Collectors.toList());
        }

        public static PostConverterContext of(BpmnNode rootNode, boolean jbpmnModel) {
            HashMap<BpmnNode, Boolean> collapsedNodes = new HashMap<>();
            calculateCollapsedNodes(rootNode, jbpmnModel, collapsedNodes);
            return new PostConverterContext(collapsedNodes);
        }

        private static void calculateCollapsedNodes(BpmnNode rootNode,
                                                    boolean jbpmnModel,
                                                    HashMap<BpmnNode, Boolean> collapsedNodes) {
            if (jbpmnModel) {
                return;
            }
            for (BpmnNode child : rootNode.getChildren()) {
                if (isLane(child)) {
                    calculateCollapsedNodes(child, jbpmnModel, collapsedNodes);
                } else if (isSubProcess(child)) {
                    //this calculation can't be done for jBPM processes since this expanded attribute wasn't properly
                    //set for jBPM Designer or initial Stunner versions.
                    boolean collapsed = !child.getPropertyReader().isExpanded();
                    collapsedNodes.put(child, collapsed);
                    calculateCollapsedNodes(child, jbpmnModel, collapsedNodes);
                }
            }
        }
    }

    private void postConvertSubProcess(BpmnNode subProcess) {
        subProcess.getChildren().stream()
                .filter(ProcessPostConverter::isSubProcess)
                .forEach(this::postConvertSubProcess);

        List<BpmnNode> resizedChildren = context.getResizedChildren(subProcess);
        resizedChildren.forEach(resizedChild -> applyNodeResize(subProcess, resizedChild));
        if ((context.isCollapsed(subProcess) && subProcess.hasChildren()) || !resizedChildren.isEmpty()) {
            resizeSubProcess(subProcess);
        }
        if (context.isCollapsed(subProcess)) {
            Bound subProcessUl = subProcess.value().getContent().getBounds().getUpperLeft();
            //boundary elements are relative to the target node, translation is no applied for this elements.
            subProcess.getChildren().stream()
                    .filter(child -> !child.isDocked())
                    .forEach(child -> translate(child, subProcessUl.getX(), subProcessUl.getY()));
            translate(subProcess.getEdges(), subProcessUl.getX(), subProcessUl.getY());
            context.setCollapsed(subProcess, false);
        }
    }

    private void resizeSubProcess(BpmnNode subProcess) {
        if (subProcess.hasChildren()) {
            ViewPort viewPort = ViewPort.of(subProcess, true);
            double leftPadding = viewPort.getUpperLeftX();
            double topPadding = viewPort.getUpperLeftY();
            double width = viewPort.getLowerRightX() + leftPadding;
            double height = viewPort.getLowerRightY() + topPadding;

            Bounds subProcessBounds = subProcess.value().getContent().getBounds();
            Bound subProcessUl = subProcessBounds.getUpperLeft();
            Bound subProcessLr = subProcessBounds.getLowerRight();
            Bounds subProcessOriginalBounds = Bounds.create(subProcessUl.getX(), subProcessUl.getY(), subProcessLr.getX(), subProcessLr.getY());
            double originalWidth = subProcessBounds.getWidth();
            double originalHeight = subProcessBounds.getHeight();
            subProcessLr.setX(subProcessUl.getX() + width);
            subProcessLr.setY(subProcessUl.getY() + height);

            RectangleDimensionsSet subProcessRectangle = ((BaseSubprocess) subProcess.value().getContent().getDefinition()).getDimensionsSet();
            subProcessRectangle.setWidth(new Width(width));
            subProcessRectangle.setHeight(new Height(height));
            context.setResized(subProcess, true);

            double widthFactor = width / originalWidth;
            double heightFactor = height / originalHeight;
            //incoming connections has the target point relative to subProcess so they needs to be scaled.
            inEdges(subProcess.getParent(), subProcess).forEach(edge -> scale(edge.getTargetConnection().getLocation(), widthFactor, heightFactor));
            //outgoing connections has source point relative to the subProcess so they needs to be scaled.
            outEdges(subProcess.getParent(), subProcess).forEach(edge -> scale(edge.getSourceConnection().getLocation(), widthFactor, heightFactor));
            //boundary elements are relative to the target subProcess so they needs to be scaled.
            dockedNodes(subProcess.getParent(), subProcess).forEach(node -> scaleBoundaryEventPosition(node, subProcessOriginalBounds, subProcessBounds, widthFactor, heightFactor));
        }
    }

    /**
     * Scales a boundary event position accordingly with his positioning on the target node.
     */
    private static void scaleBoundaryEventPosition(BpmnNode boundaryEvent, Bounds subProcessOriginalBounds, Bounds subProcessCurrentBounds, double widthFactor, double heightFactor) {
        Bounds bounds = boundaryEvent.value().getContent().getBounds();
        Bound ul = bounds.getUpperLeft();
        Bound lr = bounds.getLowerRight();
        double width = bounds.getWidth();
        double height = bounds.getHeight();
        if (ul.getX() > 0 && ul.getY() <= 0) {
            ul.setX(ul.getX() * widthFactor);
        } else if (ul.getX() >= (subProcessOriginalBounds.getWidth() - width / 2) && ul.getY() > 0) {
            ul.setX(ul.getX() + subProcessCurrentBounds.getWidth() - subProcessOriginalBounds.getWidth());
            ul.setY(ul.getY() * heightFactor);
        } else if (ul.getX() > 0 && ul.getY() >= (subProcessOriginalBounds.getHeight() - height / 2)) {
            ul.setX(ul.getX() * widthFactor);
            ul.setY(ul.getY() + subProcessCurrentBounds.getHeight() - subProcessOriginalBounds.getHeight());
        } else if (ul.getX() <= 0 && ul.getY() > 0) {
            ul.setY(ul.getY() * heightFactor);
        }
        lr.setX(ul.getX() + width);
        lr.setY(ul.getY() + height);
    }

    private static void adjustLane(BpmnNode lane, Padding padding) {
        if (lane.hasChildren()) {
            ViewPort viewPort = ViewPort.of(lane, false);
            Bounds laneBounds = lane.value().getContent().getBounds();
            Bound laneUl = laneBounds.getUpperLeft();
            Bound laneLr = laneBounds.getLowerRight();
            laneUl.setX(viewPort.getUpperLeftX() - padding.getLeft());
            laneUl.setY(viewPort.getUpperLeftY() - padding.getTop());
            laneLr.setX(viewPort.getLowerRightX() + padding.getRight());
            laneLr.setY(viewPort.getLowerRightY() + padding.getBottom());

            RectangleDimensionsSet laneRectangle = ((Lane) lane.value().getContent().getDefinition()).getDimensionsSet();
            laneRectangle.setWidth(new Width(laneBounds.getWidth()));
            laneRectangle.setHeight(new Height(laneBounds.getHeight()));
        }
    }

    private static void adjustEdgeConnections(BpmnEdge.Simple edge, boolean includeMagnets) {
        if (includeMagnets) {
            adjustMagnet(edge, true);
            adjustMagnet(edge, false);
        }
        adjustEdgeConnection(edge, true);
        adjustEdgeConnection(edge, false);
    }

    private static void adjustMagnet(BpmnEdge.Simple edge, boolean targetConnection) {
        EdgePropertyReader propertyReader = (EdgePropertyReader) edge.getPropertyReader();
        BPMNEdge bpmnEdge = propertyReader.getDefinitionResolver().getEdge(propertyReader.getElement().getId());

        if (bpmnEdge.getWaypoint().size() >= 2) {
            Point wayPoint;
            org.eclipse.dd.dc.Bounds bounds;
            Bounds nodeBounds;
            Connection magnetConnection;
            Point2D magnetLocation;

            if (targetConnection) {
                wayPoint = bpmnEdge.getWaypoint().get(bpmnEdge.getWaypoint().size() - 1);
                bounds = edge.getTarget().getPropertyReader().getShape().getBounds();
                magnetConnection = edge.getTargetConnection();
                magnetLocation = magnetConnection.getLocation();
                nodeBounds = edge.getTarget().value().getContent().getBounds();
            } else {
                wayPoint = bpmnEdge.getWaypoint().get(0);
                bounds = edge.getSource().getPropertyReader().getShape().getBounds();
                magnetConnection = edge.getSourceConnection();
                magnetLocation = magnetConnection.getLocation();
                nodeBounds = edge.getSource().value().getContent().getBounds();
            }
            //establish the magnet location using original elements for safety, since elements like events and gateways have
            //fixed sizes in Stunner.
            double wayPointX = wayPoint.getX();
            double wayPointY = wayPoint.getY();
            double boundX = bounds.getX();
            double boundY = bounds.getY();
            double width = bounds.getWidth();
            double height = bounds.getHeight();

            if (equals(wayPointY, boundY, PRECISION)) {
                //magnet is on top in the aris node
                magnetLocation.setX(nodeBounds.getWidth() / 2);
                magnetLocation.setY(0);
            } else if (equals(wayPointY, boundY + height, PRECISION)) {
                //magnet is on bottom in the aris node
                magnetLocation.setX(nodeBounds.getWidth() / 2);
                magnetLocation.setY(nodeBounds.getHeight());
            } else if (equals(wayPointX, boundX, PRECISION)) {
                //magnet is on the left in the aris node
                magnetLocation.setX(0);
                magnetLocation.setY(nodeBounds.getHeight() / 2);
            } else if (equals(wayPointX, boundX + width, PRECISION)) {
                //magnet is on the right the aris node
                magnetLocation.setX(nodeBounds.getWidth());
                magnetLocation.setY(nodeBounds.getHeight() / 2);
            } else {
                if (magnetConnection instanceof MagnetConnection) {
                    ((MagnetConnection) magnetConnection).setAuto(true);
                }
            }
        }
    }

    private static void adjustAllEdgeConnections(BpmnNode parentNode, boolean includeMagnets) {
        parentNode.getChildren().stream()
                .filter(child -> !child.isDocked())
                .forEach(node -> adjustAllEdgeConnections(node, includeMagnets));
        simpleEdges(parentNode.getEdges()).forEach(edge -> adjustEdgeConnections(edge, includeMagnets));
    }

    private static void adjustEdgeConnection(BpmnEdge.Simple edge, boolean targetConnection) {
        Point2D siblingPoint = null;
        Connection magnetConnection;
        Point2D magnetPoint;
        BpmnNode connectionPointNode;
        List<Point2D> controlPoints = edge.getControlPoints();
        if (targetConnection) {
            magnetConnection = edge.getTargetConnection();
            magnetPoint = magnetConnection.getLocation();
            connectionPointNode = edge.getTarget();
            if (controlPoints.size() >= 1) {
                siblingPoint = controlPoints.get(controlPoints.size() - 1);
            }
        } else {
            magnetConnection = edge.getSourceConnection();
            magnetPoint = magnetConnection.getLocation();
            connectionPointNode = edge.getSource();
            if (controlPoints.size() >= 1) {
                siblingPoint = controlPoints.get(0);
            }
        }
        if (siblingPoint != null) {
            Bounds bounds = connectionPointNode.value().getContent().getBounds();
            Bound nodeUl = bounds.getUpperLeft();
            if (connectionPointNode.isDocked()) {
                //boundary nodes coordinates are relative to the target node, so we need to de-relativize the coordinates.
                BpmnNode dockedNodeTarget = findDockedNodeTarget(connectionPointNode);
                if (dockedNodeTarget != null) {
                    double width = bounds.getWidth();
                    double height = bounds.getHeight();
                    Bounds dockedNodeTargetBounds = dockedNodeTarget.value().getContent().getBounds();
                    Bound dockingNodeUL = dockedNodeTargetBounds.getUpperLeft();
                    //translate to absolute coordinates
                    nodeUl = new Bound(nodeUl.getX() + dockingNodeUL.getX(),
                                       nodeUl.getY() + dockingNodeUL.getY());
                    Bound nodeLr = new Bound(nodeUl.getX() + width, nodeUl.getY() + height);
                    bounds = Bounds.create(nodeUl, nodeLr);
                }
            }
            if (!(magnetConnection instanceof MagnetConnection && ((MagnetConnection) magnetConnection).isAuto())) {
                if (equals(magnetPoint.getY(), 0, PRECISION) || equals(magnetPoint.getY(), bounds.getHeight(), PRECISION)) {
                    //magnet point is on top or bottom
                    if (siblingPoint.getY() != (magnetPoint.getY() + nodeUl.getY())) {
                        siblingPoint.setX(nodeUl.getX() + (bounds.getWidth() / 2));
                    }
                } else {
                    //magnet point is on left or right
                    if (siblingPoint.getX() != (magnetPoint.getX() + nodeUl.getX())) {
                        siblingPoint.setY(nodeUl.getY() + (bounds.getHeight() / 2));
                    }
                }
            }
        }
    }

    /**
     * @return The node to which a docked/boundary node is attached.
     */
    private static BpmnNode findDockedNodeTarget(BpmnNode dockedNode) {
        BpmnNode parent = dockedNode.getParent();
        while (parent != null && isLane(parent)) {
            //lanes has no edges, so we need to reach first non lane parent to get the edges.
            parent = parent.getParent();
        }
        if (parent != null) {
            return parent.getEdges().stream()
                    .filter(BpmnEdge::isDocked)
                    .filter(edge -> edge.getTarget() == dockedNode)
                    .map(BpmnEdge::getSource)
                    .findFirst().orElse(null);
        }
        return null;
    }

    private void applyNodeResize(BpmnNode container, BpmnNode resizedChild) {
        Bounds originalBounds = resizedChild.getPropertyReader().getBounds();
        Bounds currentBounds = resizedChild.value().getContent().getBounds();
        double deltaX = currentBounds.getWidth() - originalBounds.getWidth();
        double deltaY = currentBounds.getHeight() - originalBounds.getHeight();
        //boundary elements are relative to the target node, translation is no applied for this elements.
        container.getChildren().stream()
                .filter(child -> child != resizedChild)
                .filter(child -> !child.isDocked())
                .forEach(child -> applyTranslationIfRequired(currentBounds.getX(), currentBounds.getY(), deltaX, deltaY, child));

        simpleEdges(container.getEdges()).forEach(edge -> {
            applyTranslationIfRequired(currentBounds.getX(), currentBounds.getY(), deltaX, deltaY, edge);
            adjustEdgeConnections(edge, false);
        });
    }

    private void translate(BpmnNode node, double deltaX, double deltaY) {
        Bounds childBounds = node.value().getContent().getBounds();
        translate(childBounds.getUpperLeft(), deltaX, deltaY);
        translate(childBounds.getLowerRight(), deltaX, deltaY);
        if (!context.isCollapsed(node)) {
            node.getChildren().forEach(child -> translate(child, deltaX, deltaY));
            translate(node.getEdges(), deltaX, deltaY);
        }
    }

    private static void translate(List<BpmnEdge> edges, double deltaX, double deltaY) {
        simpleEdges(edges).forEach(edge -> translate(edge, deltaX, deltaY));
    }

    private static void translate(BpmnEdge.Simple edge, double deltaX, double deltaY) {
        //source and target connections points are relative to the respective source and target node, no translation is required for them,
        //only the control points are translated.
        edge.getControlPoints().forEach(controlPoint -> translate(controlPoint, deltaX, deltaY));
    }

    private static void translate(Point2D point, double deltaX, double deltaY) {
        point.setX(point.getX() + deltaX);
        point.setY(point.getY() + deltaY);
    }

    private static void translate(Bound bound, double deltaX, double deltaY) {
        bound.setX(bound.getX() + deltaX);
        bound.setY(bound.getY() + deltaY);
    }

    private static void scale(Point2D point2D, double widthFactor, double heightFactor) {
        point2D.setX(point2D.getX() * widthFactor);
        point2D.setY(point2D.getY() * heightFactor);
    }

    private void applyTranslationIfRequired(double x, double y, double deltaX, double deltaY, BpmnNode node) {
        Bounds bounds = node.value().getContent().getBounds();
        Bound ul = bounds.getUpperLeft();
        if (ul.getX() >= x && ul.getY() >= y) {
            translate(node, deltaX, deltaY);
        } else if (ul.getX() >= x && ul.getY() < y) {
            translate(node, deltaX, 0);
        } else if (ul.getX() < x && ul.getY() >= y) {
            translate(node, 0, deltaY);
        }
    }

    private static void applyTranslationIfRequired(double x, double y, double deltaX, double deltaY, BpmnEdge.Simple edge) {
        edge.getControlPoints().forEach(point -> applyTranslationIfRequired(x, y, deltaX, deltaY, point));
    }

    private static void applyTranslationIfRequired(double x, double y, double deltaX, double deltaY, Point2D point) {
        if (point.getX() >= x && point.getY() >= y) {
            translate(point, deltaX, deltaY);
        } else if (point.getX() >= x && point.getY() < y) {
            translate(point, deltaX, 0);
        } else if (point.getX() < x && point.getY() >= y) {
            translate(point, 0, deltaY);
        }
    }

    private static boolean isSubProcess(BpmnNode node) {
        return node.value().getContent().getDefinition() instanceof EmbeddedSubprocess ||
                node.value().getContent().getDefinition() instanceof EventSubprocess ||
                node.value().getContent().getDefinition() instanceof AdHocSubprocess;
    }

    private static boolean isLane(BpmnNode node) {
        return node.value().getContent().getDefinition() instanceof Lane;
    }

    /**
     * @return The list of incoming edges for the targetNode.
     */
    private static List<BpmnEdge.Simple> inEdges(BpmnNode container, BpmnNode targetNode) {
        return simpleEdges(container.getEdges())
                .filter(edge -> edge.getTarget() == targetNode)
                .collect(Collectors.toList());
    }

    /**
     * @return The list of outgoing edges for the sourceNode.
     */
    private static List<BpmnEdge.Simple> outEdges(BpmnNode container, BpmnNode sourceNode) {
        return simpleEdges(container.getEdges())
                .filter(edge -> edge.getSource() == sourceNode)
                .collect(Collectors.toList());
    }

    /**
     * @return The list of nodes that are docked, typically the boundary events, on the given node.
     */
    private static Stream<BpmnNode> dockedNodes(BpmnNode container, BpmnNode node) {
        return container.getEdges().stream()
                .filter(BpmnEdge::isDocked)
                .filter(edge -> edge.getSource() == node)
                .map(BpmnEdge::getTarget);
    }

    private static Stream<BpmnEdge.Simple> simpleEdges(List<BpmnEdge> edges) {
        return edges.stream()
                .filter(edge -> edge instanceof BpmnEdge.Simple)
                .map(edge -> (BpmnEdge.Simple) edge);
    }

    private static <X, T extends Object & Comparable<? super T>> T min(List<X> values, Function<X, T> mapper) {
        return Collections.min(values.stream().map(mapper).collect(Collectors.toList()));
    }

    private static <X, T extends Object & Comparable<? super T>> T max(List<X> values, Function<X, T> mapper) {
        return Collections.max(values.stream().map(mapper).collect(Collectors.toList()));
    }

    private static boolean equals(double a, double b, double delta) {
        if (Double.compare(a, b) == 0) {
            return true;
        } else {
            return Math.abs(a - b) < delta;
        }
    }

    private static class LaneInfo {

        private BpmnNode lane;
        private Padding padding;
        private List<BpmnNode> children;

        public LaneInfo(BpmnNode lane, Padding padding, List<BpmnNode> children) {
            this.lane = lane;
            this.padding = padding;
            this.children = children;
        }

        public BpmnNode getLane() {
            return lane;
        }

        public Padding getPadding() {
            return padding;
        }

        public List<BpmnNode> getChildren() {
            return children;
        }
    }

    private static class Padding {

        private double top;
        private double right;
        private double bottom;
        private double left;

        public Padding() {
        }

        public Padding(double top, double right, double bottom, double left) {
            this.top = top;
            this.right = right;
            this.bottom = bottom;
            this.left = left;
        }

        public double getTop() {
            return top;
        }

        public double getRight() {
            return right;
        }

        public double getBottom() {
            return bottom;
        }

        public double getLeft() {
            return left;
        }

        public static Padding of(BpmnNode node) {
            if (!node.hasChildren()) {
                return new Padding();
            }
            ViewPort viewPort = ViewPort.of(node, false);
            Bounds bounds = node.value().getContent().getBounds();
            double topPadding = Math.abs(viewPort.getUpperLeftY() - bounds.getUpperLeft().getY());
            double rightPadding = Math.abs(viewPort.getLowerRightX() - bounds.getLowerRight().getX());
            double bottomPadding = Math.abs(viewPort.getLowerRightY() - bounds.getLowerRight().getY());
            double leftPadding = Math.abs(viewPort.getUpperLeftX() - bounds.getUpperLeft().getX());
            return new Padding(topPadding, rightPadding, bottomPadding, leftPadding);
        }
    }

    private static class ViewPort {

        private double ulx;
        private double uly;
        private double lrx;
        private double lry;

        public ViewPort(double ulx, double uly, double lrx, double lry) {
            this.ulx = ulx;
            this.uly = uly;
            this.lrx = lrx;
            this.lry = lry;
        }

        public double getUpperLeftX() {
            return ulx;
        }

        public double getUpperLeftY() {
            return uly;
        }

        public double getLowerRightX() {
            return lrx;
        }

        public double getLowerRightY() {
            return lry;
        }

        public static ViewPort of(BpmnNode node, boolean includeEdges) {
            final List<Bound> ulBounds = node.getChildren().stream()
                    .filter(child -> !child.isDocked())
                    .map(child -> child.value().getContent().getBounds().getUpperLeft())
                    .collect(Collectors.toList());
            final List<Bound> lrBounds = node.getChildren().stream()
                    .filter(child -> !child.isDocked())
                    .map(child -> child.value().getContent().getBounds().getLowerRight())
                    .collect(Collectors.toList());
            List<Point2D> controlPoints;
            if (includeEdges) {
                controlPoints = simpleEdges(node.getEdges())
                        .map(BpmnEdge.Simple::getControlPoints)
                        .flatMap(Collection::stream)
                        .collect(Collectors.toList());
            } else {
                controlPoints = Collections.emptyList();
            }

            double ulx = min(ulBounds, Bound::getX);
            double uly = min(ulBounds, Bound::getY);
            double lrx = max(lrBounds, Bound::getX);
            double lry = max(lrBounds, Bound::getY);

            if (!controlPoints.isEmpty()) {
                ulx = Math.min(ulx, min(controlPoints, Point2D::getX));
                uly = Math.min(uly, min(controlPoints, Point2D::getY));
                lrx = Math.max(lrx, max(controlPoints, Point2D::getX));
                lry = Math.max(lry, max(controlPoints, Point2D::getY));
            }
            return new ViewPort(ulx, uly, lrx, lry);
        }
    }
}
