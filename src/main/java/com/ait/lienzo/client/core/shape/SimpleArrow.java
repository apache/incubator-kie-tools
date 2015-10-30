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
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.types.Point2DArray;
import com.ait.lienzo.client.core.util.Geometry;
import com.ait.lienzo.shared.core.types.ShapeType;
import com.google.gwt.json.client.JSONObject;

public class SimpleArrow extends EndDecorator<SimpleArrow>
{
    private double m_ratio = 0.75;

    private double m_length = 30;

    public SimpleArrow()
    {
        super(ShapeType.SIMPLE_ARROW);
    }

    public SimpleArrow(double length, double ratio)
    {
        super(ShapeType.SIMPLE_ARROW);
        m_length = length;
        m_ratio = ratio;
    }

    protected SimpleArrow(final JSONObject node, final ValidationContext ctx) throws ValidationException
    {
        super(ShapeType.SIMPLE_ARROW, node, ctx);
    }

    public void set(final Point2D base, final Point2D head)
    {
        setPoints(new Point2DArray(base, head));
    }

    public double getRatio()
    {
        return m_ratio;
    }

    public SimpleArrow setRatio(double ratio)
    {
        m_ratio = ratio;
        return this;
    }

    public double getLength()
    {
        return m_length;
    }

    public SimpleArrow setLength(double length)
    {
        m_length = length;
        return this;
    }

    @Override
    public BoundingBox getBoundingBox()
    {
        return new BoundingBox(getPoints());
    }


    public final boolean parse(final Attributes attr)
    {
        Point2DArray points = attr.getPoints();

        if (null != points)
        {
            points = points.noAdjacentPoints();

            if (points.size() > 1)
            {
                final Point2D bp = points.get(0);

                final Point2D hp = points.get(1);

                final Point2D dv = bp.sub(hp);

                final Point2D dx = dv.unit().perpendicular().mul((dv.getLength() * m_ratio) / 2);

                m_list.M(hp);

                final double corner = getCornerRadius();

                if (corner <= 0)
                {
                    m_list.L(bp.add(dx));

                    m_list.L(bp.sub(dx));

                    m_list.Z();
                }
                else
                {
                    Geometry.drawArcJoinedLines(m_list, new Point2DArray(hp, bp.add(dx), bp.sub(dx), hp), corner);
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public SimpleArrow refresh()
    {
        m_list.clear();

        return this;
    }

    public Point2DArray getPoints()
    {
        return getAttributes().getPoints();
    }

    public SimpleArrow setPoints(final Point2DArray points)
    {
        while (points.size() > 2)
        {
            points.pop();
        }
        getAttributes().setPoints(points);

        return refresh();
    }

    @Override
    public SimpleArrow setPoint2DArray(final Point2DArray points)
    {
        return setPoints(points);
    }

    @Override
    public Point2DArray getPoint2DArray()
    {
        return getPoints();
    }

    @Override
    public List<Attribute> getBoundingBoxAttributes()
    {
        return Arrays.asList(Attribute.POINTS);
    }

    public double getCornerRadius()
    {
        return getAttributes().getCornerRadius();
    }

    public SimpleArrow setCornerRadius(final double radius)
    {
        getAttributes().setCornerRadius(radius);

        return refresh();
    }

    public static class SimpleArrowFactory extends ShapeFactory<SimpleArrow>
    {
        public SimpleArrowFactory()
        {
            super(ShapeType.SIMPLE_ARROW);

            addAttribute(Attribute.POINTS, true);

            addAttribute(Attribute.CORNER_RADIUS);
        }

        @Override
        public SimpleArrow create(final JSONObject node, final ValidationContext ctx) throws ValidationException
        {
            return new SimpleArrow(node, ctx);
        }
    }
}