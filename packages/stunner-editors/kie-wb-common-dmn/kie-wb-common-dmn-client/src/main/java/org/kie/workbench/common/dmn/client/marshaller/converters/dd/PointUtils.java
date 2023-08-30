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

package org.kie.workbench.common.dmn.client.marshaller.converters.dd;

import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

import jsinterop.base.Js;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dc.JSIBounds;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dc.JSIPoint;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmndi12.JSIDMNShape;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.Bound;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.relationship.Child;
import org.kie.workbench.common.stunner.core.graph.content.view.Point2D;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

public class PointUtils {

    private PointUtils() {
        // util class.
    }

    public static JSIPoint point2dToDMNDIPoint(final Point2D point2d) {
        final JSIPoint result = JSIPoint.newInstance();
        result.setX(point2d.getX());
        result.setY(point2d.getY());
        return result;
    }

    public static Point2D dmndiPointToPoint2D(final JSIPoint dmndiPoint) {
        return new Point2D(dmndiPoint.getX(), dmndiPoint.getY());
    }

    // In Stunner terms the location of a child (target) is always relative to the
    // Parent (source) location however DMN requires all locations to be absolute.
    public static void convertToAbsoluteBounds(final Node<?, ?> targetNode) {
        convertBounds(targetNode, Double::sum);
    }

    // In DMN terms the location of a Node is always absolute however Stunner requires
    // children (target) to have a relative location to their Parent (source).
    public static void convertToRelativeBounds(final Node<?, ?> targetNode) {
        convertBounds(targetNode,
                      (base, delta) -> base - delta);
    }

    @SuppressWarnings("unchecked")
    private static void convertBounds(final Node<?, ?> targetNode,
                                      final BiFunction<Double, Double, Double> converter) {
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
                    boundsX = converter.apply(boundsX, dx);
                    boundsY = converter.apply(boundsY, dy);
                    targetNodeView.setBounds(Bounds.create(boundsX, boundsY, boundsX + boundsWidth, boundsY + boundsHeight));
                    break;
                }
            }
        }
    }

    public static double xOfShape(final JSIDMNShape shape) {
        return extractValue(shape,
                            bounds -> {
                                final JSIBounds _bounds = Js.uncheckedCast(bounds);
                                return _bounds.getX();
                            });
    }

    public static double yOfShape(final JSIDMNShape shape) {
        return extractValue(shape,
                            bounds -> {
                                final JSIBounds _bounds = Js.uncheckedCast(bounds);
                                return _bounds.getY();
                            });
    }

    public static double widthOfShape(final JSIDMNShape shape) {
        return extractValue(shape,
                            bounds -> {
                                final JSIBounds _bounds = Js.uncheckedCast(bounds);
                                return _bounds.getWidth();
                            });
    }

    public static double heightOfShape(final JSIDMNShape shape) {
        return extractValue(shape,
                            bounds -> {
                                final JSIBounds _bounds = Js.uncheckedCast(bounds);
                                return _bounds.getHeight();
                            });
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

    private static double extractValue(final JSIDMNShape shape,
                                       final Function<JSIBounds, Double> extractor) {
        if (Objects.nonNull(shape)) {
            final JSIDMNShape _shape = Js.uncheckedCast(shape);
            if (Objects.nonNull(_shape.getBounds())) {
                final JSIBounds bounds = Js.uncheckedCast(_shape.getBounds());
                return extractor.apply(bounds);
            }
        }
        return 0.0;
    }

    private static Bound extractBounds(final View view,
                                       final Function<Bounds, Bound> extractor) {
        if (Objects.nonNull(view)) {
            if (Objects.nonNull(view.getBounds())) {
                return extractor.apply(view.getBounds());
            }
        }
        return null;
    }

    private static double extractBound(final Bound bound,
                                       final Function<Bound, Double> extractor) {
        if (Objects.nonNull(bound)) {
            return extractor.apply(bound);
        }
        return 0.0;
    }
}
