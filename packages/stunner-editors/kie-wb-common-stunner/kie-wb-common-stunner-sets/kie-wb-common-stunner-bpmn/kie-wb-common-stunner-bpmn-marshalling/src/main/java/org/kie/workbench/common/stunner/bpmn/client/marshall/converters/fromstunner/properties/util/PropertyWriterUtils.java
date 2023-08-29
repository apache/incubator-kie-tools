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


package org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.properties.util;

import java.util.List;
import java.util.Optional;

import org.eclipse.bpmn2.Bpmn2Factory;
import org.eclipse.bpmn2.Import;
import org.eclipse.bpmn2.di.BPMNEdge;
import org.eclipse.dd.dc.Bounds;
import org.eclipse.dd.dc.Point;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.Ids;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.properties.BasePropertyWriter;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.imports.WSDLImport;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.relationship.Dock;
import org.kie.workbench.common.stunner.core.graph.content.view.Connection;
import org.kie.workbench.common.stunner.core.graph.content.view.ControlPoint;
import org.kie.workbench.common.stunner.core.graph.content.view.Point2D;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

import static org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.Factories.dc;
import static org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.Factories.di;

public class PropertyWriterUtils {

    public static BPMNEdge createBPMNEdge(BasePropertyWriter source,
                                          BasePropertyWriter target,
                                          Connection sourceConnection,
                                          ControlPoint[] mid,
                                          Connection targetConnection) {
        BPMNEdge bpmnEdge = di.createBPMNEdge();
        bpmnEdge.setId(Ids.bpmnEdge(source.getShape().getId(),
                                    target.getShape().getId()));

        Point2D sourcePt = getSourceLocation(source,
                                             sourceConnection);
        Point2D targetPt = getTargetLocation(target,
                                             targetConnection);

        Point sourcePoint = pointOf(
                source.getShape().getBounds().getX() + sourcePt.getX(),
                source.getShape().getBounds().getY() + sourcePt.getY());

        Point targetPoint = pointOf(
                target.getShape().getBounds().getX() + targetPt.getX(),
                target.getShape().getBounds().getY() + targetPt.getY());

        List<Point> waypoints = bpmnEdge.getWaypoint();
        waypoints.add(sourcePoint);

        if (null != mid) {
            for (ControlPoint controlPoint : mid) {
                waypoints.add(pointOf(controlPoint.getLocation().getX(),
                                      controlPoint.getLocation().getY()));
            }
        }

        waypoints.add(targetPoint);

        return bpmnEdge;
    }

    @SuppressWarnings("unchecked")
    public static Optional<Node<View, Edge>> getDockSourceNode(final Node<? extends View, ?> node) {
        return node.getInEdges().stream()
                .filter(PropertyWriterUtils::isDockEdge)
                .map(Edge::getSourceNode)
                .map(n -> (Node<View, Edge>) n)
                .findFirst();
    }

    private static boolean isDockEdge(final Edge edge) {
        return edge.getContent() instanceof Dock;
    }

    private static Point2D getSourceLocation(BasePropertyWriter source,
                                             Connection sourceConnection) {
        Point2D location = sourceConnection.getLocation();
        if (location == null) {
            Bounds bounds = source.getShape().getBounds();
            location = Point2D.create(
                    bounds.getWidth(),
                    bounds.getHeight() / 2
            );
        }
        return location;
    }

    private static Point2D getTargetLocation(BasePropertyWriter target,
                                             Connection targetConnection) {
        Point2D location = targetConnection.getLocation();
        if (location == null) {
            Bounds bounds = target.getShape().getBounds();
            location = Point2D.create(
                    0,
                    bounds.getHeight() / 2
            );
        }
        return location;
    }

    private static Point pointOf(double x,
                                 double y) {
        Point pt = dc.createPoint();
        pt.setX((float) x);
        pt.setY((float) y);
        return pt;
    }

    public static Import toImport(WSDLImport wsdlImport) {
        Import imp = Bpmn2Factory.eINSTANCE.createImport();
        imp.setImportType("http://schemas.xmlsoap.org/wsdl/");
        imp.setLocation(wsdlImport.getLocation());
        imp.setNamespace(wsdlImport.getNamespace());
        return imp;
    }
}