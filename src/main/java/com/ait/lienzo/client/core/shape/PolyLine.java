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

/**
 * PolyLine is a continuous line composed of one or more line segments.
 * To create a dashed PolyLine, use one of the setDashArray() methods. 
 */
public class PolyLine extends AbstractOffsetMultiPointShape<PolyLine>
{
    private Point2D            m_tailOffsetPoint = null;

    private Point2D            m_headOffsetPoint = null;

    private final PathPartList m_list            = new PathPartList();

    /**
     * Constructor. Creates an instance of a polyline.
     * 
     * @param points a {@link Point2DArray} containing 2 or more points.
     */
    public PolyLine(final Point2DArray points)
    {
        super(ShapeType.POLYLINE);

        setPoints(points);
    }

    public PolyLine(final Point2D point, final Point2D... points)
    {
        this(new Point2DArray(point, points));
    }

    public PolyLine(double... array)
    {
        this(Point2DArray.fromArrayOfDouble(array));
    }

    protected PolyLine(final JSONObject node, final ValidationContext ctx) throws ValidationException
    {
        super(ShapeType.POLYLINE, node, ctx);

        refresh();
    }

    @Override
    public BoundingBox getBoundingBox()
    {
        return new BoundingBox(getPoints());
    }

    /**
     * Draws this polyline.
     * 
     * @param context
     */
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

    private boolean parse(Attributes attr)
    {
        Point2DArray list = attr.getPoints();

        if (null != list)
        {
            list = list.noAdjacentPoints();

            final int size = list.size();

            if (size > 1)
            {
                Point2D point = list.get(0);

                m_list.M(point.getX(), point.getY());

                final double corner = getCornerRadius();

                if (corner <= 0)
                {
                    for (int i = 1; i < size; i++)
                    {
                        point = list.get(i);

                        m_list.L(point.getX(), point.getY());
                    }
                }
                else
                {
                    Geometry.drawArcJoinedLines(m_list, list, corner);
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public PolyLine refresh()
    {
        m_list.clear();

        return this;
    }

    @Override
    protected void fill(Context2D context, Attributes attr, double alpha)
    {
    }

    /**
     * Returns this PolyLine's points.
     * @return {@link Point2DArray}
     */
    public Point2DArray getPoints()
    {
        return getAttributes().getPoints();
    }

    /**
     * Sets this PolyLine's points.
     * @param points {@link Point2DArray}
     * @return this PolyLine
     */
    public PolyLine setPoints(final Point2DArray points)
    {
        getAttributes().setPoints(points);

        return refresh();
    }

    public double getCornerRadius()
    {
        return getAttributes().getCornerRadius();
    }

    public PolyLine setCornerRadius(final double radius)
    {
        getAttributes().setCornerRadius(radius);

        return refresh();
    }

    @Override
    public PolyLine setPoint2DArray(Point2DArray points)
    {
        return setPoints(points);
    }

    @Override
    public Point2DArray getPoint2DArray()
    {
        return getPoints();
    }

    @Override
    public Point2D getTailOffsetPoint()
    {
        return m_tailOffsetPoint;
    }

    @Override
    public Point2D getHeadOffsetPoint()
    {
        return m_headOffsetPoint;
    }

    @Override
    public List<Attribute> getBoundingBoxAttributes()
    {
        return getBoundingBoxAttributesComposed(Arrays.asList(Attribute.POINTS));
    }

    public static class PolyLineFactory extends AbstractOffsetMultiPointShapeFactory<PolyLine>
    {
        public PolyLineFactory()
        {
            super(ShapeType.POLYLINE);

            addAttribute(Attribute.POINTS, true);

            addAttribute(Attribute.CORNER_RADIUS);
        }

        @Override
        public PolyLine create(final JSONObject node, final ValidationContext ctx) throws ValidationException
        {
            return new PolyLine(node, ctx);
        }
    }
}
