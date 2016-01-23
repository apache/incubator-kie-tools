/*
   Copyright (c) 2014,2015,2016 Ahome' Innovation Technologies. All rights reserved.

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
import com.ait.lienzo.client.core.shape.json.validators.ValidationContext;
import com.ait.lienzo.client.core.shape.json.validators.ValidationException;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.core.types.PathPartList;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.types.Point2DArray;
import com.ait.lienzo.client.core.util.Geometry;
import com.ait.lienzo.shared.core.types.ShapeType;
import com.google.gwt.json.client.JSONObject;

public class SimpleArrow extends Decorator<SimpleArrow>
{
    public static final double DEFAULT_ARRROW_RATIO = 0.75;

    public SimpleArrow()
    {
        super(ShapeType.SIMPLE_ARROW);

        setArrowRatio(DEFAULT_ARRROW_RATIO);
    }

    public SimpleArrow(final double length, final double ratio)
    {
        super(ShapeType.SIMPLE_ARROW, length);

        setArrowRatio(ratio);
    }

    protected SimpleArrow(final JSONObject node, final ValidationContext ctx) throws ValidationException
    {
        super(ShapeType.SIMPLE_ARROW, node, ctx);
    }

    @Override
    public SimpleArrow setDecoratorPoints(final Point2D base, final Point2D head)
    {
        return setPoints(new Point2DArray(base, head));
    }

    public double getArrowRatio()
    {
        return getAttributes().getArrowRatio();
    }

    public SimpleArrow setArrowRatio(final double ratio)
    {
        getAttributes().setArrowRatio(ratio);

        return this;
    }

    @Override
    public BoundingBox getBoundingBox()
    {
        return new BoundingBox(getPoints());
    }

    @Override
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

                final Point2D dx = dv.unit().perpendicular().mul((dv.getLength() * getArrowRatio()) / 2);

                final PathPartList path = getPathPartList();

                path.M(hp);

                final double corner = getCornerRadius();

                if (corner <= 0)
                {
                    path.L(bp.add(dx));

                    path.L(bp.sub(dx));

                    path.Z();
                }
                else
                {
                    Geometry.drawArcJoinedLines(path, new Point2DArray(hp, bp.add(dx), bp.sub(dx), hp), corner);
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public SimpleArrow refresh()
    {
        getPathPartList().clear();

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
        return asAttributes(Attribute.POINTS, Attribute.CORNER_RADIUS, Attribute.ARROW_RATIO);
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

    public static class SimpleArrowFactory extends AbstractDecoratorFactory<SimpleArrow>
    {
        public SimpleArrowFactory()
        {
            super(ShapeType.SIMPLE_ARROW);

            addAttribute(Attribute.POINTS, true);

            addAttribute(Attribute.CORNER_RADIUS);

            addAttribute(Attribute.ARROW_RATIO);
        }

        @Override
        public SimpleArrow create(final JSONObject node, final ValidationContext ctx) throws ValidationException
        {
            return new SimpleArrow(node, ctx);
        }
    }
}