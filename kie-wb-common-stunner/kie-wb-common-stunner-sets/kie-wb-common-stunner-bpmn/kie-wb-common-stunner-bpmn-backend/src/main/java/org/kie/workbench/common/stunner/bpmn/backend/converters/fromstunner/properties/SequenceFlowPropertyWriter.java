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

package org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.properties;

import java.util.List;

import org.eclipse.bpmn2.FlowNode;
import org.eclipse.bpmn2.SequenceFlow;
import org.eclipse.bpmn2.di.BPMNEdge;
import org.eclipse.dd.dc.Bounds;
import org.eclipse.dd.dc.Point;
import org.kie.workbench.common.stunner.bpmn.backend.converters.customproperties.CustomElement;
import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.Ids;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNViewDefinition;
import org.kie.workbench.common.stunner.core.graph.content.view.Connection;
import org.kie.workbench.common.stunner.core.graph.content.view.ControlPoint;
import org.kie.workbench.common.stunner.core.graph.content.view.DiscreteConnection;
import org.kie.workbench.common.stunner.core.graph.content.view.Point2D;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewConnector;

import static org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.Factories.dc;
import static org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.Factories.di;

public class SequenceFlowPropertyWriter extends PropertyWriter {

    private final SequenceFlow sequenceFlow;
    private BasePropertyWriter source;
    private BasePropertyWriter target;
    private BPMNEdge bpmnEdge;

    public SequenceFlowPropertyWriter(SequenceFlow sequenceFlow, VariableScope variableScope) {
        super(sequenceFlow, variableScope);
        this.sequenceFlow = sequenceFlow;
    }

    public void setAutoConnectionSource(Connection connection) {
        DiscreteConnection c = (DiscreteConnection) connection;
        CustomElement.autoConnectionSource.of(sequenceFlow).set(c.isAuto());
    }

    public void setAutoConnectionTarget(Connection connection) {
        DiscreteConnection c = (DiscreteConnection) connection;
        CustomElement.autoConnectionTarget.of(sequenceFlow).set(c.isAuto());
    }

    public void setConnection(ViewConnector<? extends BPMNViewDefinition> connector) {
        Connection sourceConnection = connector.getSourceConnection().get();
        Connection targetConnection = connector.getTargetConnection().get();

        setAutoConnectionSource(sourceConnection);
        setAutoConnectionTarget(targetConnection);

        List<ControlPoint> controlPoints = connector.getControlPoints();
        setWaypoints(sourceConnection, controlPoints, targetConnection);
    }

    public void setSource(BasePropertyWriter pSrc) {
        this.source = pSrc;
        sequenceFlow.setSourceRef((FlowNode) pSrc.getElement());
        pSrc.setTarget(this);
    }

    public void setTarget(BasePropertyWriter pTgt) {
        this.target = pTgt;
        sequenceFlow.setTargetRef((FlowNode) pTgt.getElement());
        pTgt.setSource(this);
    }

    private void setWaypoints(Connection sourceConnection, List<ControlPoint> mid, Connection targetConnection) {
        BPMNEdge bpmnEdge = di.createBPMNEdge();
        bpmnEdge.setId(Ids.bpmnEdge(source.getShape().getId(), target.getShape().getId()));
        bpmnEdge.setBpmnElement(sequenceFlow);

        Point2D sourcePt = getSourceLocation(sourceConnection);
        Point2D targetPt = getTargetLocation(targetConnection);

        org.eclipse.dd.dc.Point sourcePoint = pointOf(
                source.getShape().getBounds().getX() + sourcePt.getX(),
                source.getShape().getBounds().getY() + sourcePt.getY());

        org.eclipse.dd.dc.Point targetPoint = pointOf(
                target.getShape().getBounds().getX() + targetPt.getX(),
                target.getShape().getBounds().getY() + targetPt.getY());

        List<Point> waypoints = bpmnEdge.getWaypoint();
        waypoints.add(sourcePoint);

        mid.stream()
                .map(pt -> pointOf(
                        pt.getLocation().getX(),
                        pt.getLocation().getY()))
                .forEach(waypoints::add);

        waypoints.add(targetPoint);

        this.bpmnEdge = bpmnEdge;
    }

    private Point2D getSourceLocation(Connection sourceConnection) {
        Point2D location = sourceConnection.getLocation();
        if (location == null) {
            location = getDefaultLocation(source.getShape().getBounds());
        }
        return location;
    }

    private Point2D getTargetLocation(Connection targetConnection) {
        Point2D location = targetConnection.getLocation();
        if (location == null) {
            location = getDefaultLocation(target.getShape().getBounds());
        }
        return location;
    }

    private Point2D getDefaultLocation(Bounds bounds) {
        return Point2D.create(bounds.getWidth() / 2, bounds.getHeight() / 2);
    }

    private org.eclipse.dd.dc.Point pointOf(double x, double y) {
        org.eclipse.dd.dc.Point pt = dc.createPoint();
        pt.setX((float) x);
        pt.setY((float) y);
        return pt;
    }

    public BPMNEdge getEdge() {
        return bpmnEdge;
    }
}
