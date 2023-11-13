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


package org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.properties.util;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.bpmn2.BaseElement;
import org.eclipse.bpmn2.Import;
import org.eclipse.dd.dc.Bounds;
import org.eclipse.dd.dc.Point;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.customproperties.CustomElement;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.DefinitionResolver;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.imports.WSDLImport;
import org.kie.workbench.common.stunner.core.graph.content.view.Point2D;

public class PropertyReaderUtils {

    public static Point2D getSourcePosition(DefinitionResolver definitionResolver,
                                            String edgeId,
                                            String sourceId) {
        final double resolutionFactor = definitionResolver.getResolutionFactor();
        final Bounds sourceBounds = definitionResolver.getShape(sourceId).getBounds();
        final List<Point> waypoint = definitionResolver.getEdge(edgeId).getWaypoint();

        return waypoint.isEmpty() ?
                sourcePosition(sourceBounds, resolutionFactor)
                : offsetPosition(sourceBounds,
                                 waypoint.get(0),
                                 resolutionFactor);
    }

    public static Point2D getTargetPosition(DefinitionResolver definitionResolver,
                                            String edgeId,
                                            String targetId) {
        final double resolutionFactor = definitionResolver.getResolutionFactor();
        final Bounds targetBounds = definitionResolver.getShape(targetId).getBounds();
        final List<Point> waypoint = definitionResolver.getEdge(edgeId).getWaypoint();

        return waypoint.isEmpty() ?
                targetPosition(targetBounds, resolutionFactor)
                : offsetPosition(targetBounds,
                                 waypoint.get(waypoint.size() - 1),
                                 resolutionFactor);
    }

    public static List<Point2D> getControlPoints(DefinitionResolver definitionResolver,
                                                 String edgeId) {
        final double resolutionFactor = definitionResolver.getResolutionFactor();
        final List<Point> waypoint = definitionResolver.getEdge(edgeId).getWaypoint();
        final List<Point2D> result = new ArrayList<>();
        if (waypoint.size() > 2) {
            List<Point> points = waypoint.subList(1,
                                                  waypoint.size() - 1);
            for (Point p : points) {
                result.add(createPoint2D(p, resolutionFactor));
            }
        }
        return result;
    }

    public static WSDLImport toWSDLImports(Import imp) {
        return new WSDLImport(imp.getLocation(), imp.getNamespace());
    }

    private static Point2D createPoint2D(Point p, double factor) {
        return Point2D.create(p.getX() * factor,
                              p.getY() * factor);
    }

    public static boolean isAutoConnectionSource(BaseElement element) {
        return CustomElement.autoConnectionSource.of(element).get();
    }

    public static boolean isAutoConnectionTarget(BaseElement element) {
        return CustomElement.autoConnectionTarget.of(element).get();
    }

    private static Point2D sourcePosition(Bounds sourceBounds,
                                          double factor) {
        return Point2D.create(sourceBounds.getWidth() * factor,
                              sourceBounds.getHeight() * factor / 2);
    }

    private static Point2D targetPosition(Bounds targetBounds,
                                          double factor) {
        return Point2D.create(0,
                              targetBounds.getHeight() * factor / 2);
    }

    private static Point2D offsetPosition(Bounds sourceBounds,
                                          Point wayPoint,
                                          double factor) {
        return Point2D.create((wayPoint.getX() * factor) - (sourceBounds.getX() * factor),
                              (wayPoint.getY() * factor) - (sourceBounds.getY() * factor));
    }
}