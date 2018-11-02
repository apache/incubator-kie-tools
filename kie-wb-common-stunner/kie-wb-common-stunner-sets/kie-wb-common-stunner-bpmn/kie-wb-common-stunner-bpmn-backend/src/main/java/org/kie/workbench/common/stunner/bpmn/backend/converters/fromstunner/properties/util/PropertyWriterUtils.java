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

package org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.properties.util;

import java.util.List;

import org.eclipse.bpmn2.di.BPMNEdge;
import org.eclipse.dd.dc.Bounds;
import org.eclipse.dd.dc.Point;
import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.Ids;
import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.properties.BasePropertyWriter;
import org.kie.workbench.common.stunner.core.graph.content.view.Connection;
import org.kie.workbench.common.stunner.core.graph.content.view.ControlPoint;
import org.kie.workbench.common.stunner.core.graph.content.view.Point2D;

import static org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.Factories.dc;
import static org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.Factories.di;

public class PropertyWriterUtils {

    public static BPMNEdge createBPMNEdge(BasePropertyWriter source,
                                          BasePropertyWriter target,
                                          Connection sourceConnection,
                                          List<ControlPoint> mid,
                                          Connection targetConnection) {
        BPMNEdge bpmnEdge = di.createBPMNEdge();
        bpmnEdge.setId(Ids.bpmnEdge(source.getShape().getId(),
                                    target.getShape().getId()));

        Point2D sourcePt = getSourceLocation(source,
                                             sourceConnection);
        Point2D targetPt = getTargetLocation(target,
                                             targetConnection);

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

        return bpmnEdge;
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

    private static org.eclipse.dd.dc.Point pointOf(double x,
                                                   double y) {
        org.eclipse.dd.dc.Point pt = dc.createPoint();
        pt.setX((float) x);
        pt.setY((float) y);
        return pt;
    }
}