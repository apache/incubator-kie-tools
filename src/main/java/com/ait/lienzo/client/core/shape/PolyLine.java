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
// TODO - review DSJ

package com.ait.lienzo.client.core.shape;

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
public class PolyLine extends AbstractDirectionalMultiPointShape<PolyLine>
{
    private static final double SEGMENT_SNAP_DISTANCE = 5d;

    private Point2D      m_headOffsetPoint;

    private Point2D      m_tailOffsetPoint;

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

    public PolyLine(final Point2DArray points, final double corner)
    {
        this(points);

        setCornerRadius(corner);
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
    }

    @Override
    public BoundingBox getBoundingBox()
    {
        return new BoundingBox(getPoints());
    }

    @Override
    public boolean parse(final Attributes attr)
    {
        Point2DArray list = attr.getPoints();

        list = list.noAdjacentPoints();

        final int size = list.size();
        final PathPartList path = getPathPartList();

        final double headOffset = attr.getHeadOffset();
        final double tailOffset = attr.getTailOffset();

        if (size > 1)
        {
            m_headOffsetPoint = Geometry.getProjection(list.get(0), list.get(1), headOffset);
            m_tailOffsetPoint = Geometry.getProjection(list.get(size - 1), list.get(size - 2), tailOffset);

            path.M(m_headOffsetPoint);

            final double corner = getCornerRadius();
            if (corner <= 0)
            {
                for (int i = 1; i < size - 1; i++)
                {
                    path.L(list.get(i));
                }

                path.L(m_tailOffsetPoint);
            }
            else
            {
                list = list.copy();
                list.set(size - 1, m_tailOffsetPoint);

                Geometry.drawArcJoinedLines(path, list, corner);
            }
        }
        else if (size == 1)
        {
            m_headOffsetPoint = list.get(0).copy().offset(headOffset, headOffset);
            m_tailOffsetPoint = list.get(0).copy().offset(tailOffset, tailOffset);

            path.M(m_headOffsetPoint);

            final double corner = getCornerRadius();
            if (corner <= 0)
            {
                path.L(m_tailOffsetPoint);
            }
            else
            {
                list = new Point2DArray(list.get(0).copy(), list.get(0).copy());

                Geometry.drawArcJoinedLines(path, list, corner);
            }
        }

        return true;
    }

    @Override
    protected boolean fill(Context2D context, Attributes attr, double alpha)
    {
        return false;
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
        return getBoundingBoxAttributesComposed(Attribute.POINTS, Attribute.CORNER_RADIUS);
    }

    @Override
    public Point2D adjustPoint(double x, double y, double deltaX, double deltaY) {
        Point2DArray points = getPoint2DArray();

        Point2D before = null;
        Point2D target = null;
        Point2D after = null;

        for (Point2D point: points) {
            after = point;

            if (target != null) {
                if (target.getX() == x && target.getY() == y) {
                    break;
                }
            }

            before = target;
            target = after;
        }

        if (target == after) {
            after = null;
        }

        double xDiffBefore = Double.MAX_VALUE;
        double yDiffBefore = Double.MAX_VALUE;

        if (before != null) {
            xDiffBefore = target.getX() - before.getX();
            yDiffBefore = target.getY() - before.getY();
        }

        double xDiffAfter = Double.MAX_VALUE;
        double yDiffAfter = Double.MAX_VALUE;

        if (after != null) {
            xDiffAfter = target.getX() - after.getX();
            yDiffAfter = target.getY() - after.getY();
        }

        if (Math.abs(xDiffBefore) < Math.abs(xDiffAfter) && Math.abs(xDiffBefore) <= SEGMENT_SNAP_DISTANCE) {
            target.setX(target.getX() - xDiffBefore);
        } else if (Math.abs(xDiffAfter) <= SEGMENT_SNAP_DISTANCE) {
            target.setX(target.getX() - xDiffAfter);
        }

        if (Math.abs(yDiffBefore) < Math.abs(yDiffAfter) && Math.abs(yDiffBefore) <= SEGMENT_SNAP_DISTANCE) {
            target.setY(target.getY() - yDiffBefore);
        } else if (Math.abs(yDiffAfter) <= SEGMENT_SNAP_DISTANCE) {
            target.setY(target.getY() - yDiffAfter);
        }

        return new Point2D(target.getX(), target.getY());
    }

    @Override
    public boolean isControlPointShape()
    {
        return true;
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
