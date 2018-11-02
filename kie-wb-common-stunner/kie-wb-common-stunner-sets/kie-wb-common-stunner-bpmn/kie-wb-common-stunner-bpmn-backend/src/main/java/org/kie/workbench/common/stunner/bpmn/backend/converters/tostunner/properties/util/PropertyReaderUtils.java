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

package org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.properties.util;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.bpmn2.BaseElement;
import org.eclipse.dd.dc.Bounds;
import org.eclipse.dd.dc.Point;
import org.kie.workbench.common.stunner.bpmn.backend.converters.customproperties.CustomElement;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.DefinitionResolver;
import org.kie.workbench.common.stunner.core.graph.content.view.Point2D;

public class PropertyReaderUtils {

    public static Point2D getSourcePosition(DefinitionResolver definitionResolver,
                                            String edgeId,
                                            String sourceId) {
        Bounds sourceBounds = definitionResolver.getShape(sourceId).getBounds();
        List<Point> waypoint = definitionResolver.getEdge(edgeId).getWaypoint();

        return waypoint.isEmpty() ?
                sourcePosition(sourceBounds)
                : offsetPosition(sourceBounds,
                                 waypoint.get(0));
    }

    public static Point2D getTargetPosition(DefinitionResolver definitionResolver,
                                            String edgeId,
                                            String targetId) {
        Bounds targetBounds = definitionResolver.getShape(targetId).getBounds();
        List<Point> waypoint = definitionResolver.getEdge(edgeId).getWaypoint();

        return waypoint.isEmpty() ?
                targetPosition(targetBounds)
                : offsetPosition(targetBounds,
                                 waypoint.get(waypoint.size() - 1));
    }

    public static List<Point2D> getControlPoints(DefinitionResolver definitionResolver,
                                                 String edgeId) {
        List<Point> waypoint = definitionResolver.getEdge(edgeId).getWaypoint();
        List<Point2D> result = new ArrayList<>();
        if (waypoint.size() > 2) {
            List<Point> points = waypoint.subList(1,
                                                  waypoint.size() - 1);
            for (Point p : points) {
                result.add(Point2D.create(p.getX(),
                                          p.getY()));
            }
        }
        return result;
    }

    public static boolean isAutoConnectionSource(BaseElement element) {
        return CustomElement.autoConnectionSource.of(element).get();
    }

    public static boolean isAutoConnectionTarget(BaseElement element) {
        return CustomElement.autoConnectionTarget.of(element).get();
    }

    private static Point2D sourcePosition(Bounds sourceBounds) {
        return Point2D.create(sourceBounds.getWidth(),
                              sourceBounds.getHeight() / 2);
    }

    private static Point2D targetPosition(Bounds targetBounds) {
        return Point2D.create(0,
                              targetBounds.getHeight() / 2);
    }

    private static Point2D offsetPosition(Bounds sourceBounds,
                                          Point wayPoint) {
        return Point2D.create(wayPoint.getX() - sourceBounds.getX(),
                              wayPoint.getY() - sourceBounds.getY());
    }
}