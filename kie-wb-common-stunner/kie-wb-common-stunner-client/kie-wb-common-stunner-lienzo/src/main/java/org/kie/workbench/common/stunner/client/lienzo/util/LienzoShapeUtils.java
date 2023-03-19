/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.client.lienzo.util;

import com.ait.lienzo.client.core.Attribute;
import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.lienzo.client.core.shape.Shape;
import com.ait.lienzo.client.core.shape.wires.OptionalBounds;
import com.ait.lienzo.client.core.shape.wires.WiresLayoutContainer;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.core.types.LinearGradient;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import org.kie.workbench.common.stunner.core.client.shape.HasChildren;
import org.kie.workbench.common.stunner.core.graph.content.Bound;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;

public class LienzoShapeUtils {

    private static final String WIDTH = Attribute.WIDTH.getProperty();
    private static final String HEIGHT = Attribute.HEIGHT.getProperty();

    public static double getWidth(final IPrimitive<?> prim) {
        return getDoubleFromPrimitive(WIDTH, prim);
    }

    public static void setWidth(final IPrimitive<?> prim,
                                final double value) {
        setDoubleFromPrimitive(WIDTH, prim, value);
    }

    public static double getHeight(final IPrimitive<?> prim) {
        return getDoubleFromPrimitive(HEIGHT, prim);
    }

    public static void setHeight(final IPrimitive<?> prim,
                                 final double value) {
        setDoubleFromPrimitive(HEIGHT, prim, value);
    }

    public static void scale(final Shape shape,
                             final double width,
                             final double height) {
        final BoundingBox bb = shape.getBoundingBox();
        final double[] scale = getScaleFactor(bb.getWidth(),
                                              bb.getHeight(),
                                              width,
                                              height);
        shape.setScale(scale[0],
                       scale[1]);
    }

    public static double[] getScaleFactor(final double width,
                                          final double height,
                                          final double targetWidth,
                                          final double targetHeight) {
        return new double[]{
                width > 0 ? targetWidth / width : 1,
                height > 0 ? targetHeight / height : 1};
    }

    public static WiresLayoutContainer.Layout getWiresLayout(final HasChildren.Layout layout) {
        switch (layout) {
            case CENTER:
                return WiresLayoutContainer.Layout.CENTER;
            case LEFT:
                return WiresLayoutContainer.Layout.LEFT;
            case RIGHT:
                return WiresLayoutContainer.Layout.RIGHT;
            case TOP:
                return WiresLayoutContainer.Layout.TOP;
            case BOTTOM:
                return WiresLayoutContainer.Layout.BOTTOM;
        }
        throw new UnsupportedOperationException("Unsupported layout [" + layout.name() + "]");
    }

    public static LinearGradient getLinearGradient(final String startColor,
                                                   final String endColor,
                                                   final Double width,
                                                   final Double height) {
        final LinearGradient linearGradient = new LinearGradient(0,
                                                                 width,
                                                                 0,
                                                                 -height / 2);
        linearGradient.addColorStop(1,
                                    endColor);
        linearGradient.addColorStop(0,
                                    startColor);
        return linearGradient;
    }

    public static OptionalBounds translateBounds(final Bounds bounds) {
        final OptionalBounds result = OptionalBounds.createEmptyBounds();
        if (bounds.hasUpperLeft()) {
            final Bound upperLeft = bounds.getUpperLeft();
            if (upperLeft.hasX()) {
                result.setMinX(upperLeft.getX());
            }
            if (upperLeft.hasY()) {
                result.setMinY(upperLeft.getY());
            }
        }
        if (bounds.hasLowerRight()) {
            final Bound lowerRight = bounds.getLowerRight();
            if (lowerRight.hasX()) {
                result.setMaxX(lowerRight.getX());
            }
            if (lowerRight.hasY()) {
                result.setMaxY(lowerRight.getY());
            }
        }
        return result;
    }

    private static double getDoubleFromPrimitive(final String field,
                                                 final IPrimitive<?> prim) {
        JsPropertyMap<Object> nodeMap = Js.uncheckedCast(prim);
        return nodeMap.has(field) ? Js.coerceToDouble(nodeMap.get(field)) : 0d;
    }

    private static void setDoubleFromPrimitive(final String field,
                                               final IPrimitive<?> prim,
                                               final double value) {
        JsPropertyMap<Object> nodeMap = Js.uncheckedCast(prim);
        nodeMap.set(field, value);
    }
}
