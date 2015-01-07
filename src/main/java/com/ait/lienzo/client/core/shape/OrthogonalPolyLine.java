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

import com.ait.lienzo.client.core.Attribute;
import com.ait.lienzo.client.core.Context2D;
import com.ait.lienzo.client.core.shape.json.IFactory;
import com.ait.lienzo.client.core.shape.json.validators.ValidationContext;
import com.ait.lienzo.client.core.shape.json.validators.ValidationException;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.core.types.NFastDoubleArrayJSO;
import com.ait.lienzo.client.core.types.PathPartList;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.types.Point2DArray;
import com.ait.lienzo.shared.core.types.ShapeType;
import com.google.gwt.json.client.JSONObject;

public class OrthogonalPolyLine extends Shape<OrthogonalPolyLine>
{
    private static final boolean ALTERNATE = true;

    private static final boolean NORMALIZE = false;

    private static final int     ERROR     = 0;

    private static final int     DIR_N     = 1;

    private static final int     DIR_S     = 2;

    private static final int     DIR_E     = 3;

    private static final int     DIR_W     = 4;

    private final PathPartList   m_list    = new PathPartList();

    public OrthogonalPolyLine(Point2D start, Point2D... points)
    {
        this(new Point2DArray(start, points));
    }

    public OrthogonalPolyLine(Point2DArray points)
    {
        super(ShapeType.ORTHOGONAL_POLYLINE);

        setControlPoints(points);
    }

    public OrthogonalPolyLine(JSONObject node, ValidationContext ctx) throws ValidationException
    {
        super(ShapeType.ORTHOGONAL_POLYLINE, node, ctx);
    }

    @Override
    public BoundingBox getBoundingBox()
    {
        if (m_list.size() < 1)
        {
            if (false == parse(getAttributes()))
            {
                return new BoundingBox(0, 0, 0, 0);
            }
        }
        return m_list.getBoundingBox();
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

    private final boolean parse(final Attributes attr)
    {
        Point2DArray points = attr.getControlPoints();

        if (null != points)
        {
            points = points.noAdjacentPoints();

            if (points.size() >= 2)
            {
                final Point2D p1 = points.get(0);

                final Point2D p2 = points.get(1);

                if (points.size() == 2)
                {
                    if ((p1.getX() == p2.getX()) || (p1.getY() == p2.getY()))
                    {
                        m_list.M(p1.getX(), p1.getY()).L(p2.getX(), p2.getY());

                        return true;
                    }
                    else
                    {
                        points = new Point2DArray(p1, new Point2D(((p2.getX() + p1.getX()) / 2), ((p2.getY() + p1.getY()) / 2)), p2);
                    }
                }
                final NFastDoubleArrayJSO normalize = getOrthonalLinePoints(points, NORMALIZE);

                final NFastDoubleArrayJSO alternate = getOrthonalLinePoints(points, ALTERNATE);

                if ((null == alternate) || ((null != normalize) && (normalize.size() < alternate.size())))
                {
                    m_list.M(p1.getX(), p1.getY());

                    addLinePoints(normalize);

                    return true;
                }
                if ((null == normalize) || ((null != alternate) && (alternate.size() < normalize.size())))
                {
                    m_list.M(p1.getX(), p1.getY());

                    addLinePoints(alternate);

                    return true;
                }
                if ((null != normalize) && (null != alternate) && (normalize.size() == alternate.size()))
                {
                    m_list.M(p1.getX(), p1.getY());

                    addLinePoints(normalize);

                    return true;
                }
            }
        }
        return false;
    }

    private final void addLinePoints(final NFastDoubleArrayJSO points)
    {
        final int size = points.size();

        for (int i = 0; i < size; i += 2)
        {
            m_list.L(points.get(i), points.get(i + 1));
        }
    }

    private final NFastDoubleArrayJSO getOrthonalLinePoints(final Point2DArray points, final boolean alternative)
    {
        final NFastDoubleArrayJSO buffer = NFastDoubleArrayJSO.make();

        Point2D p1 = points.get(0);

        Point2D p2 = points.get(1);

        int direction = getOrthonalLinePoints(buffer, p1, p2, points.get(2), alternative);

        p1 = p2;

        final int size = points.size();

        for (int i = 2; i < size; i++)
        {
            p2 = points.get(i);

            direction = getOrthonalLinePoints(buffer, direction, p1, p2);

            if (direction == ERROR)
            {
                return null;
            }
            p1 = p2;
        }
        return buffer;
    }

    private final int getOrthonalLinePoints(final NFastDoubleArrayJSO buffer, final Point2D p1, final Point2D p2, final Point2D p3, final boolean alternative)
    {
        final int direction = direction(p1, p2, p3, alternative);

        int next_direction;

        final double p1x = p1.getX();

        final double p2x = p2.getX();

        final double p1y = p1.getY();

        final double p2y = p2.getY();

        if (direction <= DIR_S)
        {
            buffer.push(p1x);

            buffer.push(p2y);

            if (p1x == p2x)
            {
                next_direction = direction;
            }
            else if (p1x < p2x)
            {
                next_direction = DIR_E;
            }
            else
            {
                next_direction = DIR_W;
            }
        }
        else
        {
            buffer.push(p2x);

            buffer.push(p1y);

            if (p1y == p2y)
            {
                next_direction = direction;
            }
            else if (p1y > p2y)
            {
                next_direction = DIR_N;
            }
            else
            {
                next_direction = DIR_S;
            }
        }
        buffer.push(p2x);

        buffer.push(p2y);

        return next_direction;
    }

    /**
     * Based on the initial three points it determines the direction, and alternative direction,
     * to ensure the orthgonal line can be drawn between the three points.
     *
     * @param p1
     * @param p2
     * @param p3
     * @param alternative
     * @return
     */
    private final int direction(final Point2D p1, final Point2D p2, final Point2D p3, final boolean alternative)
    {
        final double px = p2.getX();

        final double dx = (px - p1.getX());

        final double dy = (p2.getY() - p1.getY());

        if ((dx > 0) && (dy < 0))
        {
            if ((NORMALIZE == alternative) && (p3.getX() > px))
            {
                return DIR_N;
            }
            else
            {
                return DIR_E;
            }
        }
        else if ((dx > 0) && (dy > 0))
        {
            if ((NORMALIZE == alternative) && (p3.getX() > px))
            {
                return DIR_S;
            }
            else
            {
                return DIR_E;
            }
        }
        else if ((dx < 0) && (dy > 0))
        {
            if ((NORMALIZE == alternative) && (p3.getX() < px))
            {
                return DIR_S;
            }
            else
            {
                return DIR_W;
            }
        }
        else
        {
            if ((NORMALIZE == alternative) && (p3.getX() < px))
            {
                return DIR_N;
            }
            else
            {
                return DIR_W;
            }
        }
    }

    /**
     * Draws an orthogonal line between two points, it uses the previous direction to determine the new direction.
     * It will always attempt to continue the line in the same direction if it can do so, without requiring a corner.
     * This helps ensure to ensure the orthgonal line can be drawn between the three points.
     * @param points
     * @param prev_direction
     * @param p1
     * @param p2
     * @return
     */
    private final int getOrthonalLinePoints(final NFastDoubleArrayJSO points, final int prev_direction, final Point2D p1, final Point2D p2)
    {
        int temp_direction;

        final double p1x = p1.getX();

        final double p2x = p2.getX();

        final double p1y = p1.getY();

        final double p2y = p2.getY();

        switch (prev_direction)
        {
            case DIR_N:
                if ((p2y > p1y) && (p2x == p1x))
                {
                    return ERROR; // a line cannot go back on itself
                }
                else if (p2y < p1y)
                {
                    temp_direction = DIR_N;
                }
                else if (p2x > p1x)
                {
                    temp_direction = DIR_E;
                }
                else
                {
                    temp_direction = DIR_W;
                }
                break;
            case DIR_E:
                if ((p2x < p1x) && (p2y == p1y))
                {
                    return ERROR; // a line cannot go back on itself
                }
                else if (p2x > p1x)
                {
                    temp_direction = DIR_E;
                }
                else if (p2y < p1y)
                {
                    temp_direction = DIR_N;
                }
                else
                {
                    temp_direction = DIR_S;
                }
                break;
            case DIR_S:
                if ((p2y < p1y) && (p2x == p1x))
                {
                    return ERROR; // a line cannot go back on itself
                }
                else if (p2y > p1y)
                {
                    temp_direction = DIR_S;
                }
                else if (p2x > p1x)
                {
                    temp_direction = DIR_E;
                }
                else
                {
                    temp_direction = DIR_W;
                }
                break;
            case DIR_W:
                if ((p2x > p1x) && (p2y == p1y))
                {
                    return ERROR; // a line cannot go back on itself
                }
                else if (p2x < p1x)
                {
                    temp_direction = DIR_W;
                }
                else if (p2y < p1y)
                {
                    temp_direction = DIR_N;
                }
                else
                {
                    temp_direction = DIR_S;
                }
                break;
            default:
                return ERROR;
        }
        int next_direction;

        if (temp_direction <= DIR_S)
        {
            points.push(p1x);

            points.push(p2y);

            if (p1x == p2x)
            {
                next_direction = temp_direction;
            }
            else if (p1x < p2x)
            {
                next_direction = DIR_E;
            }
            else
            {
                next_direction = DIR_W;
            }
        }
        else
        {
            points.push(p2x);

            points.push(p1y);

            if (p1y == p2y)
            {
                next_direction = temp_direction;
            }
            else if (p1y > p2y)
            {
                next_direction = DIR_N;
            }
            else
            {
                next_direction = DIR_S;
            }
        }
        points.push(p2x);

        points.push(p2y);

        return next_direction;
    }

    @Override
    public void fill(Context2D context, Attributes attr, double alpha)
    {
    }

    /**
     * Returns this OrthogonalPolyLine's points.
     * @return {@link Point2DArray}
     */
    public Point2DArray getControlPoints()
    {
        return getAttributes().getControlPoints();
    }

    /**
     * Sets this OrthogonalPolyLine's points.
     * @param points {@link Point2DArray}
     * @return this OrthogonalPolyLine
     */
    public OrthogonalPolyLine setControlPoints(Point2DArray points)
    {
        getAttributes().setControlPoints(points);

        m_list.clear();

        return this;
    }

    @Override
    public IFactory<OrthogonalPolyLine> getFactory()
    {
        return new OrthogonaPolylLineFactory();
    }

    public static class OrthogonaPolylLineFactory extends ShapeFactory<OrthogonalPolyLine>
    {
        public OrthogonaPolylLineFactory()
        {
            super(ShapeType.ORTHOGONAL_POLYLINE);

            addAttribute(Attribute.CONTROL_POINTS, true);
        }

        @Override
        public OrthogonalPolyLine create(JSONObject node, ValidationContext ctx) throws ValidationException
        {
            return new OrthogonalPolyLine(node, ctx);
        }
    }
}
