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

package org.kie.workbench.common.dmn.backend.definition.v1_1.dd;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.kie.dmn.model.api.dmndi.DMNShape;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.Bounds.Bound;
import org.kie.workbench.common.stunner.core.graph.content.relationship.Child;
import org.kie.workbench.common.stunner.core.graph.content.view.BoundImpl;
import org.kie.workbench.common.stunner.core.graph.content.view.BoundsImpl;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

public class PointUtils {

    private PointUtils() {
        // util class.
    }

    public static org.kie.dmn.model.api.dmndi.Point point2dToDMNDIPoint(org.kie.workbench.common.stunner.core.graph.content.view.Point2D point2d) {
        org.kie.dmn.model.api.dmndi.Point result = new org.kie.dmn.model.v1_2.dmndi.Point();
        result.setX(point2d.getX());
        result.setY(point2d.getY());
        return result;
    }

    public static org.kie.workbench.common.stunner.core.graph.content.view.Point2D dmndiPointToPoint2D(org.kie.dmn.model.api.dmndi.Point dmndiPoint) {
        org.kie.workbench.common.stunner.core.graph.content.view.Point2D result = new org.kie.workbench.common.stunner.core.graph.content.view.Point2D(dmndiPoint.getX(), dmndiPoint.getY());
        return result;
    }

    // In Stunner terms the location of a child (target) is always relative to the
    // Parent (source) location however DMN requires all locations to be absolute.
    public static void convertToAbsoluteBounds(final Node<?, ?> targetNode) {
        convertBounds(targetNode,
                      (base, delta) -> base + delta);
    }

    // In DMN terms the location of a Node is always absolute however Stunner requires
    // children (target) to have a relative location to their Parent (source).
    public static void convertToRelativeBounds(final Node<?, ?> targetNode) {
        convertBounds(targetNode,
                      (base, delta) -> base - delta);
    }

    @SuppressWarnings("unchecked")
    private static void convertBounds(final Node<?, ?> targetNode,
                                      final BiFunction<Double, Double, Double> convertor) {
        if (targetNode.getContent() instanceof View<?>) {
            final View<?> targetNodeView = (View<?>) targetNode.getContent();
            double boundsX = xOfBound(upperLeftBound(targetNodeView));
            double boundsY = yOfBound(upperLeftBound(targetNodeView));
            final double boundsWidth = xOfBound(lowerRightBound(targetNodeView)) - boundsX;
            final double boundsHeight = yOfBound(lowerRightBound(targetNodeView)) - boundsY;
            final List<Edge<?, ?>> inEdges = (List<Edge<?, ?>>) targetNode.getInEdges();
            for (Edge<?, ?> e : inEdges) {
                if (e.getContent() instanceof Child) {
                    final Node<?, ?> sourceNode = e.getSourceNode();
                    final View<?> sourceView = (View<?>) sourceNode.getContent();
                    final Bound sourceViewULBound = sourceView.getBounds().getUpperLeft();
                    final double dx = sourceViewULBound.getX();
                    final double dy = sourceViewULBound.getY();
                    boundsX = convertor.apply(boundsX, dx);
                    boundsY = convertor.apply(boundsY, dy);
                    targetNodeView.setBounds(new BoundsImpl(new BoundImpl(boundsX, boundsY),
                                                            new BoundImpl(boundsX + boundsWidth, boundsY + boundsHeight)));
                    break;
                }
            }
        }
    }

    public static double xOfShape(final DMNShape shape) {
        return extractValue(shape, org.kie.dmn.model.api.dmndi.Bounds::getX);
    }

    public static double yOfShape(final DMNShape shape) {
        return extractValue(shape, org.kie.dmn.model.api.dmndi.Bounds::getY);
    }

    public static double widthOfShape(final DMNShape shape) {
        return extractValue(shape, org.kie.dmn.model.api.dmndi.Bounds::getWidth);
    }

    public static double heightOfShape(final DMNShape shape) {
        return extractValue(shape, org.kie.dmn.model.api.dmndi.Bounds::getHeight);
    }

    public static Bound upperLeftBound(final View view) {
        return extractBounds(view, Bounds::getUpperLeft);
    }

    public static Bound lowerRightBound(final View view) {
        return extractBounds(view, Bounds::getLowerRight);
    }

    public static double xOfBound(final Bound bound) {
        return extractBound(bound, Bound::getX);
    }

    public static double yOfBound(final Bound bound) {
        return extractBound(bound, Bound::getY);
    }

    private static double extractValue(final DMNShape shape,
                                       final Function<org.kie.dmn.model.api.dmndi.Bounds, Double> extractor) {
        if (shape != null) {
            if (shape.getBounds() != null) {
                return extractor.apply(shape.getBounds());
            }
        }
        return 0.0;
    }

    private static Bound extractBounds(final View view,
                                       final Function<Bounds, Bound> extractor) {
        if (view != null) {
            if (view.getBounds() != null) {
                return extractor.apply(view.getBounds());
            }
        }
        return null;
    }

    private static double extractBound(final Bound bound,
                                       final Function<Bound, Double> extractor) {
        if (bound != null) {
            return extractor.apply(bound);
        }
        return 0.0;
    }
}
