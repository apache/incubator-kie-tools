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

import static com.ait.lienzo.shared.core.types.Direction.EAST;
import static com.ait.lienzo.shared.core.types.Direction.NONE;
import static com.ait.lienzo.shared.core.types.Direction.NORTH;
import static com.ait.lienzo.shared.core.types.Direction.SOUTH;
import static com.ait.lienzo.shared.core.types.Direction.WEST;

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
import com.ait.lienzo.shared.core.types.Direction;
import com.ait.lienzo.shared.core.types.ShapeType;
import com.google.gwt.json.client.JSONObject;

public class OrthogonalPolyLine extends AbstractDirectionalMultiPointShape<OrthogonalPolyLine>
{
    private final PathPartList m_list = new PathPartList();

    private Point2D            m_tailOffsetPoint;

    private Point2D            m_headOffsetPoint;

    public OrthogonalPolyLine(final Point2D start, final Point2D... points)
    {
        this(new Point2DArray(start, points));
    }

    public OrthogonalPolyLine(final Point2DArray points)
    {
        super(ShapeType.ORTHOGONAL_POLYLINE);

        setControlPoints(points);
    }

    public OrthogonalPolyLine(final JSONObject node, final ValidationContext ctx) throws ValidationException
    {
        super(ShapeType.ORTHOGONAL_POLYLINE, node, ctx);
    }

    public void correctTailWithOffset(Point2DArray points, double offset, Direction direction)
    {
        Point2D p0 = points.get(0);

        Point2D p1 = points.get(1);

        Point2D p = correctEndWithOffset(offset, direction, p0, p1, false);

        m_tailOffsetPoint = p;

        points.set(0, p);
    }

    public void correctHeadWithOffset(Point2DArray points, double offset, Direction direction)
    {
        int size = points.size();

        Point2D p0 = points.get(size - 2);

        Point2D p1 = points.get(size - 1);

        Point2D p = correctEndWithOffset(offset, direction, p0, p1, true);

        m_headOffsetPoint = p;

        points.set(size - 1, p);
    }

    private static final Point2D correctEndWithOffset(double offset, final Direction direction, final Point2D p0, final Point2D p1, final boolean reverse)
    {
        Point2D target;

        if (reverse)
        {
            target = p1;

            offset = -offset;
        }
        else
        {
            target = p0;
        }
        switch (direction)
        {
            case NORTH:
                return target.setY(target.getY() - offset);
            case EAST:
                return target.setX(target.getX() + offset);
            case SOUTH:
                return target.setY(target.getY() + offset);
            case WEST:
                return target.setX(target.getX() - offset);
            case NONE:
            default:
                return target.add(p1.sub(p0).unit().mul(offset)); // unit vector in the direction of SE
        }
    }

    private static final NFastDoubleArrayJSO getOrthogonalLinePoints(final Point2DArray points, Direction tailDirection, Direction headDirection, final double correction)
    {
        final NFastDoubleArrayJSO buffer = NFastDoubleArrayJSO.make();

        if (points.size() == 2)
        {
            Point2D p0 = points.get(0);

            Point2D p1 = points.get(1);

            double p0x = p0.getX();

            double p0y = p0.getY();

            switch (tailDirection)
            {
                case NORTH:
                    p0y = p0y - correction;
                    addPoint(buffer, p0x, p0y);
                    break;
                case SOUTH:
                    p0y = p0y + correction;
                    addPoint(buffer, p0x, p0y);
                    break;
                case EAST:
                    p0x = p0x + correction;
                    addPoint(buffer, p0x, p0y);
                    break;
                case WEST:
                    p0x = p0x - correction;
                    addPoint(buffer, p0x, p0y);
                    break;
                case NONE:
                    tailDirection = getTailDirection(points);
                    break;
            }
            addHead(buffer, tailDirection, headDirection, p0, p1, correction);

            return buffer;
        }
        Point2D p1;

        int i = 0;

        Direction direction = tailDirection;

        if (tailDirection == NONE)
        {
            direction = getTailDirection(points);

            p1 = points.get(0);

            i = 1;
        }
        else
        {
            boolean tailAdded = addTail(buffer, points, tailDirection, correction);

            if (tailAdded)
            {
                // need to get new direction, and update the start offset (i).

                int size = buffer.size();

                p1 = new Point2D(buffer.get(size - 2), buffer.get(size - 1));

                direction = getDirection(buffer.get(size - 4), buffer.get(size - 3), p1.getX(), p1.getY());

                i = 2;
            }
            else
            {
                p1 = points.get(0);

                i = 1;
            }
        }
        final int size = points.size();

        for (; i < size - 1; i++)
        {
            Point2D p2 = points.get(i);

            direction = getOrthogonalLinePointsAndDirection(buffer, direction, p1.getX(), p1.getY(), p2.getX(), p2.getY());

            if (null == direction)
            {
                return null;
            }
            p1 = p2;
        }
        p1 = points.get(size - 2);

        Point2D p2 = points.get(size - 1);

        addHead(buffer, direction, headDirection, p1, p2, correction);

        return buffer;
    }

    private static final boolean addTail(final NFastDoubleArrayJSO buffer, Point2DArray points, final Direction tailDirection, final double correction)
    {
        Point2D p0 = points.get(0);

        Point2D p1 = points.get(1);

        double p0x = p0.getX();

        double p0y = p0.getY();

        double p1x = p1.getX();

        double p1y = p1.getY();

        final double dx = (p1x - p0x);

        final double dy = (p1y - p0y);

        switch (tailDirection)
        {
            case SOUTH:
                if (dy > correction)
                {
                    return false;
                }
                break;
            case NORTH:
                if (dy < correction)
                {
                    return false;
                }
                break;
            case EAST:
                if (dx > correction)
                {
                    return false;
                }
                break;
            case WEST:
                if (dx < correction)
                {
                    return false;
                }
                break;
            default:
                return false;
        }
        double offset = correction;

        switch (tailDirection)
        {
            case NORTH:
                offset = -offset;
            case SOUTH:
            {
                double x = p0x;
                double y = p0y + offset;

                addPoint(buffer, x, y);
                x = p1x;
                addPoint(buffer, x, y);
                addPoint(buffer, p1x, p1y);
                return true;
            }
            case WEST:
                offset = -offset;
            case EAST:
            {
                double x = p0x + offset;
                double y = p0y;

                addPoint(buffer, x, y);
                y = p1y;
                addPoint(buffer, x, y);
                x = p1x;
                addPoint(buffer, p1x, p1y);
                return true;
            }
            default:
                return false;
        }
    }

    private static final void addHead(NFastDoubleArrayJSO buffer, Direction lastDirection, Direction headDirection, Point2D p0, Point2D p1, final double correction)
    {
        double p0x = p0.getX();

        double p0y = p0.getY();

        double p1x = p1.getX();

        double p1y = p1.getY();

        // the delta tells us the quadrant we need move to
        final double dx = (p1x - p0x);

        final double dy = (p1y - p0y);

        double x, y;

        double correctionOffset = correction;

        switch (headDirection)
        {
            case SOUTH:
                correctionOffset = -correction;
            case NORTH:
                if (((headDirection == SOUTH) && (dy < correction)) || ((headDirection == NORTH) && (dy > -correction)))
                {
                    // p1 located same side of the HEAD direction
                    if (lastDirection == headDirection)
                    {
                        x = p0x + (dx / 2);
                        y = p0y;
                        addPoint(buffer, x, y);
                    }
                    else
                    {
                        x = p0x;
                    }
                    y = p1y + correctionOffset;
                    addPoint(buffer, x, y);
                    x = p1x;
                    addPoint(buffer, x, y);
                }
                else
                {
                    // p1 is opposite  of p3, for the head direction
                    if (((dx > 0) && (lastDirection == WEST)) || ((dx < 0) && (lastDirection == EAST)))
                    {
                        x = p0x;
                        y = p0y + (dy / 2);
                        addPoint(buffer, x, y);
                        x = p1x;
                        addPoint(buffer, x, y);
                    }
                    else
                    {
                        x = p1x;
                        y = p0y;
                        addPoint(buffer, x, y);
                    }
                }
                break;
            case EAST:
                correctionOffset = -correction;
            case WEST:
                if (((headDirection == EAST) && (dx < correction)) || ((headDirection == WEST) && (dx > -correction)))
                {
                    // p1 located same side of the HEAD direction
                    if (lastDirection == headDirection)
                    {
                        x = p0x;
                        y = p0y + (dy / 2);
                        addPoint(buffer, x, y);
                    }
                    else
                    {
                        y = p0y;
                    }
                    x = p1x + correctionOffset;
                    addPoint(buffer, x, y);
                    y = p1y;
                    addPoint(buffer, x, y);
                }
                else
                {
                    // p1 is opposite  of p3, for the head direction
                    if (((dy > 0) && (lastDirection == NORTH)) || ((dy < 0) && (lastDirection == SOUTH)))
                    {
                        x = p0x + (dx / 2);
                        y = p0y;
                        addPoint(buffer, x, y);
                        y = p1y;
                        addPoint(buffer, x, y);
                    }
                    else
                    {
                        x = p0x;
                        y = p1y;
                        addPoint(buffer, x, y);
                    }
                }
                break;
            case NONE:
                getOrthogonalLinePointsAndDirection(buffer, lastDirection, p0x, p0y, p1x, p1y);
                return;
        }
        addPoint(buffer, p1x, p1y);
    }

    private static final Direction getTailDirection(final Point2DArray points)
    {
        Point2D p1 = points.get(0);

        Point2D p2 = points.get(1);

        double p1x = p1.getX();

        double p1y = p1.getY();

        double p2x = p2.getX();

        double p2y = p2.getY();

        final double dx = (p2x - p1x);

        final double dy = (p2y - p1y);

        if (dy < 0)
        {
            // if p2 is north, then always go north
            return NORTH;
        }
        else if (dx == 0)
        {
            // if it's directly south, with same x, go south.
            return SOUTH;
        }
        else if (dx > 0)
        {
            // if p2 is south and east, so go east
            return EAST;
        }
        else
        //if (dx > 0 )
        {
            // we know p2 is south and west, so go west.
            return WEST;
        }
    }

    private static final Direction getDirection(final double p1x, final double p1y, final double p2x, final double p2y)
    {
        final double dx = (p2x - p1x);

        final double dy = (p2y - p1y);

        if ((dx == 0) && (dy == 0))
        {
            return NONE;
        }
        if (dy < 0)
        {
            return NORTH;
        }
        else if (dy > 0)
        {
            return SOUTH;
        }
        else if (dx > 0)
        {
            return EAST;
        }
        else
        //if (dx < 0 )
        {
            return WEST;
        }
    }

    /**
     * Draws an orthogonal line between two points, it uses the previous direction to determine the new direction. It
     * will always attempt to continue the line in the same direction if it can do so, without requiring a corner.
     */
    private static final Direction getOrthogonalLinePointsAndDirection(final NFastDoubleArrayJSO buffer, final Direction direction, final double p1x, final double p1y, final double p2x, final double p2y)
    {
        Direction next_direction;

        switch (direction)
        {
            case NORTH:
                if (p2y < p1y)
                {
                    next_direction = NORTH;
                }
                else if (p2x > p1x)
                {
                    next_direction = EAST;
                }
                else
                {
                    next_direction = WEST;
                }
                break;
            case SOUTH:
                if (p2y > p1y)
                {
                    next_direction = SOUTH;
                }
                else if (p2x > p1x)
                {
                    next_direction = EAST;
                }
                else
                {
                    next_direction = WEST;
                }
                break;
            case EAST:
                if (p2x > p1x)
                {
                    next_direction = EAST;
                }
                else if (p2y < p1y)
                {
                    next_direction = NORTH;
                }
                else
                {
                    next_direction = SOUTH;
                }
                break;
            case WEST:
                if (p2x < p1x)
                {
                    next_direction = WEST;
                }
                else if (p2y < p1y)
                {
                    next_direction = NORTH;
                }
                else
                {
                    next_direction = SOUTH;
                }
                break;
            default:
                return null;
        }
        if ((next_direction == SOUTH) || (next_direction == NORTH))
        {
            buffer.push(p1x, p2y, p2x, p2y);

            if (p1x < p2x)
            {
                return EAST;
            }
            else if (p1x > p2x)
            {
                return WEST;
            }
            else
            {
                return next_direction;
            }
        }
        else
        {
            buffer.push(p2x, p1y, p2x, p2y);

            if (p1y > p2y)
            {
                return NORTH;
            }
            else if (p1y < p2y)
            {
                return SOUTH;
            }
            else
            {
                return next_direction;
            }
        }
    }

    private static final void addPoint(final NFastDoubleArrayJSO buffer, final double x, final double y)
    {
        buffer.push(x, y);
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

    @Override
    public OrthogonalPolyLine refresh()
    {
        m_list.clear();

        return this;
    }

    @Override
    protected void fill(Context2D context, Attributes attr, double alpha)
    {
    }

    private final boolean parse(final Attributes attr)
    {
        Point2DArray points = attr.getControlPoints();

        if (null != points)
        {
            points = points.noAdjacentPoints(); // this clones the points, so we are ok to mutate the elements (see head/tail offset)

            if (points.size() > 1)
            {
                final double tailOffset = attr.getTailOffset();

                final double headOffset = attr.getHeadOffset();

                final double correction = attr.getCorrectionOffset();

                final Direction headDirection = attr.getHeadDirection();

                final Direction tailDirection = attr.getTailDirection();

                if (tailOffset > 0)
                {
                    correctTailWithOffset(points, tailOffset, tailDirection);
                }
                if (headOffset > 0)
                {
                    correctHeadWithOffset(points, headOffset, headDirection);
                }
                final Point2D p1 = points.get(0);

                final NFastDoubleArrayJSO linept = getOrthogonalLinePoints(points, tailDirection, headDirection, correction);

                if (null != linept)
                {
                    m_list.M(p1.getX(), p1.getY());

                    addLinePoints(linept);

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

    /**
     * Returns this OrthogonalPolyLine's points.
     *
     * @return {@link Point2DArray}
     */
    public Point2DArray getControlPoints()
    {
        return getAttributes().getControlPoints();
    }

    /**
     * Sets this OrthogonalPolyLine's points.
     *
     * @param points {@link Point2DArray}
     * @return this OrthogonalPolyLine
     */
    public OrthogonalPolyLine setControlPoints(final Point2DArray points)
    {
        getAttributes().setControlPoints(points);

        return refresh();
    }

    @Override
    public OrthogonalPolyLine setPoint2DArray(final Point2DArray points)
    {
        return setControlPoints(points);
    }

    @Override
    public Point2DArray getPoint2DArray()
    {
        return getControlPoints();
    }

    @Override
    public boolean isControlPointShape()
    {
        return true;
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
    public IFactory<OrthogonalPolyLine> getFactory()
    {
        return new OrthogonaPolylLineFactory();
    }

    public static class OrthogonaPolylLineFactory extends AbstractDirectionalMultiPointShapeFactory<OrthogonalPolyLine>
    {
        public OrthogonaPolylLineFactory()
        {
            super(ShapeType.ORTHOGONAL_POLYLINE);

            addAttribute(Attribute.CONTROL_POINTS, true);
        }

        @Override
        public OrthogonalPolyLine create(final JSONObject node, final ValidationContext ctx) throws ValidationException
        {
            return new OrthogonalPolyLine(node, ctx);
        }
    }
}