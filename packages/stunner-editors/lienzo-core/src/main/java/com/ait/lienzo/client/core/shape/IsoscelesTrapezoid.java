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

package com.ait.lienzo.client.core.shape;

import java.util.List;

import com.ait.lienzo.client.core.Attribute;
import com.ait.lienzo.client.core.Context2D;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.core.types.PathPartList;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.types.Point2DArray;
import com.ait.lienzo.client.core.util.Geometry;
import com.ait.lienzo.shared.core.types.ShapeType;
import jsinterop.annotations.JsProperty;

public class IsoscelesTrapezoid extends Shape<IsoscelesTrapezoid> {

    @JsProperty
    private double height;

    @JsProperty
    private double topWidth;

    @JsProperty
    private double bottomWidth;

    @JsProperty
    private double cornerRadius;

    private final PathPartList m_list = new PathPartList();

    public IsoscelesTrapezoid(final double topwidth, final double bottomwidth, final double height) {
        super(ShapeType.ISOSCELES_TRAPEZOID);

        setTopWidth(topwidth).setBottomWidth(bottomwidth).setHeight(height);
    }

    public IsoscelesTrapezoid(final double topwidth, final double bottomwidth, final double height, final double corner) {
        this(topwidth, bottomwidth, height);

        setCornerRadius(corner);
    }

    @Override
    protected boolean prepare(final Context2D context, final double alpha) {
        if (m_list.size() < 1) {
            if (!parse()) {
                return false;
            }
        }
        if (m_list.size() < 1) {
            return false;
        }
        context.path(m_list);

        return true;
    }

    private boolean parse() {
        final double hig = getHeight();

        final double top = getTopWidth();

        final double bot = getBottomWidth();

        if ((hig > 0) && (top > 0) && (bot > 0)) {
            final double sub = Math.abs(top - bot);

            final Point2DArray list = new Point2DArray();

            if (0 == sub) {
                list.pushXY(0, 0);

                list.pushXY(top, 0);

                list.pushXY(top, hig);

                list.pushXY(0, hig);
            } else {
                if (top > bot) {
                    list.pushXY(0, 0);

                    list.pushXY(top, 0);

                    list.pushXY((sub / 2.0) + bot, hig);

                    list.pushXY((sub / 2.0), hig);
                } else {
                    list.pushXY((sub / 2.0), 0);

                    list.pushXY((sub / 2.0) + top, 0);

                    list.pushXY(bot, hig);

                    list.pushXY(0, hig);
                }
            }
            final Point2D p0 = list.get(0);

            m_list.M(p0);

            final double corner = getCornerRadius();

            if (corner <= 0) {
                final int size = list.size();

                for (int i = 1; i < size; i++) {
                    m_list.L(list.get(i));
                }
                m_list.Z();
            } else {
                list.push(p0);
                Geometry.drawArcJoinedLines(m_list, list, corner);
            }
            return true;
        }
        return false;
    }

    @Override
    public BoundingBox getBoundingBox() {
        return BoundingBox.fromDoubles(0, 0, Math.max(getTopWidth(), getBottomWidth()), getHeight());
    }

    @Override
    public IsoscelesTrapezoid refresh() {
        m_list.clear();

        return this;
    }

    public IsoscelesTrapezoid setTopWidth(final double topWidth) {
        this.topWidth = topWidth;

        return refresh();
    }

    public double getTopWidth() {
        return this.topWidth;
    }

    public IsoscelesTrapezoid setBottomWidth(final double bottomWidth) {
        this.bottomWidth = bottomWidth;

        return refresh();
    }

    public double getBottomWidth() {
        return this.bottomWidth;
    }

    public IsoscelesTrapezoid setHeight(final double height) {
        this.height = height;

        return refresh();
    }

    public double getHeight() {
        return this.height;
    }

    @Override
    public List<Attribute> getBoundingBoxAttributes() {
        return asAttributes(Attribute.TOP_WIDTH, Attribute.BOTTOM_WIDTH, Attribute.HEIGHT, Attribute.CORNER_RADIUS);
    }

    public double getCornerRadius() {
        return this.cornerRadius;
    }

    public IsoscelesTrapezoid setCornerRadius(final double radius) {
        this.cornerRadius = radius;

        return refresh();
    }
}