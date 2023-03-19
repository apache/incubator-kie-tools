/*
   Copyright (c) 2017 Ahome' Innovation Technologies. All rights reserved.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

package com.ait.lienzo.shared.core.types;

import com.ait.lienzo.tools.common.api.types.IStringValued;

/**
 * ShapeType is an extensible enumeration of all Shape types.
 */
public class ShapeType implements IStringValued {

    public static final ShapeType ARC = new ShapeType("Arc");

    public static final ShapeType ARROW = new ShapeType("Arrow");

    public static final ShapeType BEZIER_CURVE = new ShapeType("BezierCurve");

    public static final ShapeType CIRCLE = new ShapeType("Circle");

    public static final ShapeType ELLIPTICAL_ARC = new ShapeType("EllipticalArc");

    public static final ShapeType ELLIPSE = new ShapeType("Ellipse");

    public static final ShapeType LINE = new ShapeType("Line");

    public static final ShapeType MOVIE = new ShapeType("Movie");

    public static final ShapeType PARALLELOGRAM = new ShapeType("Parallelogram");

    public static final ShapeType PICTURE = new ShapeType("Picture");

    public static final ShapeType IMAGE = new ShapeType("Image");

    public static final ShapeType POLYGON = new ShapeType("Polygon");

    public static final ShapeType POLYLINE = new ShapeType("Polyline");

    public static final ShapeType ORTHOGONAL_POLYLINE = new ShapeType("OrthogonalPolyline");

    public static final ShapeType QUADRATIC_CURVE = new ShapeType("QuadraticCurve");

    public static final ShapeType RECTANGLE = new ShapeType("Rectangle");

    public static final ShapeType REGULAR_POLYGON = new ShapeType("RegularPolygon");

    public static final ShapeType SLICE = new ShapeType("Slice");

    public static final ShapeType STAR = new ShapeType("Star");

    public static final ShapeType TEXT = new ShapeType("Text");

    public static final ShapeType TRIANGLE = new ShapeType("Triangle");

    public static final ShapeType SPLINE = new ShapeType("Spline");

    public static final ShapeType BOW = new ShapeType("Bow");

    public static final ShapeType RING = new ShapeType("Ring");

    public static final ShapeType CHORD = new ShapeType("Chord");

    public static final ShapeType ISOSCELES_TRAPEZOID = new ShapeType("IsoscelesTrapezoid");

    public static final ShapeType SVG_PATH = new ShapeType("SVGPath");

    public static final ShapeType MULTI_PATH = new ShapeType("MultiPath");

    public static final ShapeType SPRITE = new ShapeType("Sprite");

    private final String m_value;

    protected ShapeType(final String value) {
        m_value = value;
    }

    @Override
    public final String toString() {
        return m_value;
    }

    @Override
    public final String getValue() {
        return m_value;
    }

    @Override
    public boolean equals(final Object other) {
        if ((other == null) || (!(other instanceof ShapeType))) {
            return false;
        }
        if (this == other) {
            return true;
        }
        ShapeType that = ((ShapeType) other);

        return that.getValue().equals(getValue());
    }

    @Override
    public int hashCode() {
        return getValue().hashCode();
    }

    public ShapeType copy() {
        return new ShapeType(m_value);
    }
}
