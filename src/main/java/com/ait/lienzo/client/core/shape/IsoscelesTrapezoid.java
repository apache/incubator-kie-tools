/*
   Copyright (c) 2014,2015 Ahome' Innovation Technologies. All rights reserved.

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

import java.util.Arrays;
import java.util.List;

import com.ait.lienzo.client.core.Attribute;
import com.ait.lienzo.client.core.Context2D;
import com.ait.lienzo.client.core.shape.json.validators.ValidationContext;
import com.ait.lienzo.client.core.shape.json.validators.ValidationException;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.core.types.PathPartList;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.types.Point2DArray;
import com.ait.lienzo.client.core.util.Geometry;
import com.ait.lienzo.shared.core.types.ShapeType;
import com.google.gwt.json.client.JSONObject;

public class IsoscelesTrapezoid extends Shape<IsoscelesTrapezoid>
{
    private final PathPartList m_list = new PathPartList();

    public IsoscelesTrapezoid(final double topwidth, final double bottomwidth, final double height)
    {
        super(ShapeType.ISOSCELES_TRAPEZOID);

        setTopWidth(topwidth).setBottomWidth(bottomwidth).setHeight(height);
    }

    protected IsoscelesTrapezoid(final JSONObject node, final ValidationContext ctx) throws ValidationException
    {
        super(ShapeType.ISOSCELES_TRAPEZOID, node, ctx);
    }

    @Override
    protected boolean prepare(final Context2D context, final Attributes attr, final double alpha)
    {
        if (m_list.size() < 1)
        {
            if (false == parse(attr))
            {
                return false;
            }
        }
        if (m_list.size() < 1)
        {
            return false;
        }
        context.path(m_list);

        return true;
    }

    private boolean parse(final Attributes attr)
    {
        final double hig = attr.getHeight();

        final double top = attr.getTopWidth();

        final double bot = attr.getBottomWidth();

        if ((hig > 0) && (top > 0) && (bot > 0))
        {
            final double sub = Math.abs(top - bot);

            final Point2DArray list = new Point2DArray();

            if (0 == sub)
            {
                list.push(0, 0);

                list.push(top, 0);

                list.push(top, hig);

                list.push(0, hig);
            }
            else
            {
                if (top > bot)
                {
                    list.push(0, 0);

                    list.push(top, 0);

                    list.push((sub / 2.0) + bot, hig);

                    list.push((sub / 2.0), hig);
                }
                else
                {
                    list.push((sub / 2.0), 0);

                    list.push((sub / 2.0) + top, 0);

                    list.push(bot, hig);

                    list.push(0, hig);
                }
            }
            final Point2D p0 = list.get(0);

            m_list.M(p0);

            final double corner = getCornerRadius();

            if (corner <= 0)
            {
                final int size = list.size();

                for (int i = 1; i < size; i++)
                {
                    m_list.L(list.get(i));
                }
                m_list.Z();
            }
            else
            {
                Geometry.drawArcJoinedLines(m_list, list.push(p0), corner);
            }
            return true;
        }
        return false;
    }

    @Override
    public BoundingBox getBoundingBox()
    {
        return new BoundingBox(0, 0, Math.max(getTopWidth(), getBottomWidth()), getHeight());
    }

    @Override
    public IsoscelesTrapezoid refresh()
    {
        m_list.clear();

        return this;
    }

    public IsoscelesTrapezoid setTopWidth(final double topwidth)
    {
        getAttributes().setTopWidth(topwidth);

        return refresh();
    }

    public double getTopWidth()
    {
        return getAttributes().getTopWidth();
    }

    public IsoscelesTrapezoid setBottomWidth(final double bottomwidth)
    {
        getAttributes().setBottomWidth(bottomwidth);

        return refresh();
    }

    public double getBottomWidth()
    {
        return getAttributes().getBottomWidth();
    }

    public IsoscelesTrapezoid setHeight(final double height)
    {
        getAttributes().setHeight(height);

        return refresh();
    }

    public double getHeight()
    {
        return getAttributes().getHeight();
    }

    @Override
    public List<Attribute> getBoundingBoxAttributes()
    {
        return Arrays.asList(Attribute.TOP_WIDTH, Attribute.BOTTOM_WIDTH, Attribute.HEIGHT);
    }

    public double getCornerRadius()
    {
        return getAttributes().getCornerRadius();
    }

    public IsoscelesTrapezoid setCornerRadius(final double radius)
    {
        getAttributes().setCornerRadius(radius);

        return refresh();
    }

    public static class IsoscelesTrapezoidFactory extends ShapeFactory<IsoscelesTrapezoid>
    {
        public IsoscelesTrapezoidFactory()
        {
            super(ShapeType.ISOSCELES_TRAPEZOID);

            addAttribute(Attribute.CORNER_RADIUS);

            addAttribute(Attribute.TOP_WIDTH, true);

            addAttribute(Attribute.BOTTOM_WIDTH, true);

            addAttribute(Attribute.HEIGHT, true);
        }

        @Override
        public IsoscelesTrapezoid create(final JSONObject node, final ValidationContext ctx) throws ValidationException
        {
            return new IsoscelesTrapezoid(node, ctx);
        }
    }
}