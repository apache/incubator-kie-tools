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
import static com.ait.lienzo.shared.core.types.Direction.NORTH_EAST;
import static com.ait.lienzo.shared.core.types.Direction.NORTH_WEST;
import static com.ait.lienzo.shared.core.types.Direction.SOUTH;
import static com.ait.lienzo.shared.core.types.Direction.SOUTH_EAST;
import static com.ait.lienzo.shared.core.types.Direction.SOUTH_WEST;
import static com.ait.lienzo.shared.core.types.Direction.WEST;

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
import com.ait.lienzo.shared.core.types.Direction;
import com.ait.lienzo.shared.core.types.ShapeType;
import com.ait.tooling.nativetools.client.collection.NFastDoubleArrayJSO;
import com.google.gwt.json.client.JSONObject;

public class OrthogonalPolyLine extends AbstractDirectionalMultiPointShape<OrthogonalPolyLine>
{
    private Point2D            m_headOffsetPoint;

    private Point2D            m_tailOffsetPoint;

    public OrthogonalPolyLine(final Point2D start, final Point2D... points)
    {
        this(new Point2DArray(start, points));
    }

    public OrthogonalPolyLine(final Point2DArray points)
    {
        super(ShapeType.ORTHOGONAL_POLYLINE);

        setControlPoints(points);
    }

    public OrthogonalPolyLine(final Point2DArray points, final double corner)
    {
        this(points);

        setCornerRadius(corner);
    }

    protected OrthogonalPolyLine(final JSONObject node, final ValidationContext ctx) throws ValidationException
    {
        super(ShapeType.ORTHOGONAL_POLYLINE, node, ctx);
    }

    public void correctHeadWithOffset(Point2DArray points, double offset, Direction direction)
    {
        Point2D p0 = points.get(0);

        Point2D p1 = points.get(1);

        Point2D p = correctEndWithOffset(offset, direction, p0, p1, false);

        m_headOffsetPoint = p;

        points.set(0, p);
    }

    public void correctTailWithOffset(Point2DArray points, double offset, Direction direction)
    {
        int size = points.size();

        Point2D p0 = points.get(size - 2);

        Point2D p1 = points.get(size - 1);

        Point2D p = correctEndWithOffset(offset, direction, p0, p1, true);

        m_tailOffsetPoint = p;

        points.set(size - 1, p);
    }

    private static final Point2D correctEndWithOffset(double offset, Direction direction, final Point2D p0, final Point2D p1, final boolean reverse)
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

    private static Direction correctHeadDirection(Direction direction, Point2D p0, Point2D p1)
    {
        switch (direction)
        {
            case NORTH_WEST:
                if ( p0.getX() <= p1.getX() )
                {
                    direction = Direction.NORTH;
                }
                else
                {
                    direction = Direction.WEST;
                }
                break;
            case NORTH_EAST:
                if ( p0.getX() >= p1.getX() )
                {
                    direction = Direction.NORTH;
                }
                else
                {
                    direction = Direction.EAST;
                }
                break;
            case SOUTH_EAST:
                if ( p0.getX() >= p1.getX() )
                {
                    direction = Direction.SOUTH;
                }
                else
                {
                    direction = Direction.EAST;
                }
                break;
            case SOUTH_WEST:
                if ( p0.getX() <= p1.getX() )
                {
                    direction = Direction.SOUTH;
                }
                else
                {
                    direction = Direction.WEST;
                }
                break;
        }
        return direction;
    }

    private static final NFastDoubleArrayJSO getOrthogonalLinePoints(final Point2DArray points, Direction headDirection, Direction tailDirection, final double correction)
    {
        final NFastDoubleArrayJSO buffer = NFastDoubleArrayJSO.make();

        Point2D p0 = points.get(0);
        Point2D p1 = points.get(1);

        int i = 1;

        Direction direction = headDirection;

        Direction nextDirection = null;

        if (headDirection == NONE)
        {
            direction = getHeadDirection(points);
        }
        else
        {
            double p0x = p0.getX();

            double p0y = p0.getY();

            double p1x = p1.getX();

            double p1y = p1.getY();

            final double dx = (p1x - p0x);

            final double dy = (p1y - p0y);

            boolean p1BehindP0 = false;
            switch (headDirection)
            {
                case NORTH:
                    p1BehindP0 = dy > -correction;
                    break;
                case SOUTH:
                    p1BehindP0 = dy < correction;
                    break;
                case WEST:
                    p1BehindP0 = dx > -correction;
                    break;
                case EAST:
                    p1BehindP0 = dx < correction;
                    break;
            }

            if ( p1BehindP0 )
            {
                double x = p0.getX();
                double y = p0.getY();

                double correctionOffset = correction;
                switch (headDirection)
                {
                    case NORTH:
                        correctionOffset = -correction;
                    case SOUTH:
                        y = y +  correctionOffset;
                        break;
                    case WEST:
                        correctionOffset = -correction;
                    case EAST:
                        x = x +  correctionOffset;
                        break;
                }
                addPoint(buffer, x, y);
                p0 = new Point2D(x, y);
            }
            else
            {
                // this is used to force a drawing direction
                nextDirection = headDirection;
            }

        }

        if (points.size() == 2)
        {
            // always inject a midpoint, otherwise it cannot draw both a head and a tail, with forced directions.
            double  x = p1.getX()+((p0.getX()-p1.getX())/2);
            double y = p1.getY()+((p0.getY()-p1.getY())/2);
            direction = getOrthogonalLinePointsAndDirection(buffer, direction, nextDirection,
                                                            p0.getX(), p0.getY(),
                                                            x, y,
                                                            p1.getX(), p1.getY(), true );
            p0 = new Point2D(x, y);
        }

        final int size = points.size();

        Point2D p2;
        for (; i < size - 1; i++)
        {
            p1 = points.get(i);
            p2 = points.get(i+1);

            direction = getOrthogonalLinePointsAndDirection(buffer, direction, nextDirection,
                                                            p0.getX(), p0.getY(),
                                                            p1.getX(), p1.getY(),
                                                            p2.getX(), p2.getY(), true );

            nextDirection = null; // this becomes null as only the first point can have  forced nextDirection

            if (null == direction)
            {
                return null;
            }
            p0 = p1;
        }
        p1 = points.get(size - 1);

        addTail(buffer, direction, tailDirection, p0, p1, correction);

        return buffer;
    }

    private static final void addTail(NFastDoubleArrayJSO buffer, Direction lastDirection, Direction tailDirection, Point2D p0, Point2D p1, final double correction)
    {
        double p0x = p0.getX();

        double p0y = p0.getY();

        double p1x = p1.getX();

        double p1y = p1.getY();

        switch ( tailDirection ) {
            // Corner directions can go either way, so try both and pick the one that results in the least number of corners.
            case NORTH_EAST:
            {
                tailDirection = EAST;
                int points1 = addTail(buffer, lastDirection, NORTH, correction, p0x, p0y, p1x, p1y, false);
                int points2 = addTail(buffer, lastDirection, EAST, correction, p0x, p0y, p1x, p1y, false);
                if ( points1 < points2 )
                {
                    tailDirection = NORTH;
                }
                break;
            }
            case SOUTH_EAST:
            {
                tailDirection = EAST;
                int points1 = addTail(buffer, lastDirection, SOUTH, correction, p0x, p0y, p1x, p1y, false);
                int points2 = addTail(buffer, lastDirection, EAST, correction, p0x, p0y, p1x, p1y, false);
                if ( points1 < points2 )
                {
                    tailDirection = SOUTH;
                }
                break;
            }
            case SOUTH_WEST:
            {
                tailDirection = WEST;
                int points1 = addTail(buffer, lastDirection, SOUTH, correction, p0x, p0y, p1x, p1y, false);
                int points2 = addTail(buffer, lastDirection, WEST, correction, p0x, p0y, p1x, p1y, false);
                if ( points1 < points2 )
                {
                    tailDirection = SOUTH;
                }
                break;
            }
            case NORTH_WEST:
            {
                tailDirection = WEST;
                int points1 = addTail(buffer, lastDirection, NORTH, correction, p0x, p0y, p1x, p1y, false);
                int points2 = addTail(buffer, lastDirection, WEST, correction, p0x, p0y, p1x, p1y, false);
                if ( points1 < points2 )
                {
                    tailDirection = NORTH;
                }
                break;
            }
        }

        addTail(buffer, lastDirection, tailDirection, correction, p0x, p0y, p1x, p1y, true);
    }

    private static int addTail(NFastDoubleArrayJSO buffer, Direction lastDirection, Direction tailDirection, double correction, double p0x, double p0y, double p1x, double p1y, boolean write)
    {
        final double dx = (p1x - p0x);

        final double dy = (p1y - p0y);
        double offset = 0;
        switch (tailDirection)
        {
            case NORTH:
                p1y = p1y - offset;
            case EAST:
                p1x = p1x + offset;
            case SOUTH:
                p1y = p1y + offset;
            case WEST:
                p1x = p1x - offset;
            case NONE:
            default:
                //return target.add(p1.sub(p0).unit().mul(offset)); // unit vector in the direction of SE
        }


        int corners = 0;
        boolean p0BehindP1 = false;
        switch (tailDirection)
        {
            case NORTH:
                p0BehindP1 = dy < correction;
                break;
            case SOUTH:
                p0BehindP1 = dy > -correction;
                break;
            case WEST:
                p0BehindP1 = dx < correction;
                break;
            case EAST:
                p0BehindP1 = dx > -correction;
                break;
        }

        double x = p0x;
        double y = p0y;

        if(p0BehindP1)
        {
            // addCorrection means p0 is behind.
            // If p0 is in behind and lastDirection points opposite direction p1's tail direction, it must first be corrected.
            // This means going perpendicular to the current direction, in the direction of p1.
            // It uses half the distance, so the lines look better spaced.
            switch (tailDirection)
            {
                case NORTH:
                case SOUTH:
                    if ((lastDirection == NORTH && tailDirection == SOUTH) || (lastDirection == SOUTH && tailDirection == NORTH))
                    {
                        x = p0x + (dx / 2);
                        addPoint(buffer, x, y, write);
                        lastDirection = (dx > 0) ? EAST : WEST;
                        corners++;
                    }
                    break;
                case WEST:
                case EAST:
                    if ((lastDirection == WEST && tailDirection == EAST) || (lastDirection == EAST && tailDirection == WEST))
                    {
                        y = p0y + (dy / 2);
                        addPoint(buffer, x, y, write);
                        lastDirection = (dy > 0) ? SOUTH : NORTH;
                        corners++;
                    }
                    break;
            }
        }
        else
        {
            // !addCorrection means p0 is in front
            // If p0 is in front and lastDirection points perpendicular away from p1, it must first be corrected.
            // This means going perpendicular to the current direction, in the direction of p1.
            // It uses half the distance, so the lines look better spaced.
            switch (tailDirection)
            {
                case NORTH:
                case SOUTH:
                    if ((dx > 0 && lastDirection == WEST) || (dx < 0 && lastDirection == EAST))
                    {
                        y = p0y + (dy / 2);
                        addPoint(buffer, x, y, write);
                        lastDirection = (tailDirection == NORTH) ? SOUTH : NORTH;
                        corners++;
                    }
                    break;
                case WEST:
                case EAST:
                    if ((dy > 0 && lastDirection == NORTH) || (dy < 0 && lastDirection == SOUTH))
                    {
                        x = p0x + (dx / 2);
                        addPoint(buffer, x, y, write);
                        lastDirection = (tailDirection == WEST) ? EAST : WEST;
                        corners++;
                    }
                    break;
            }
        }

        if(p0BehindP1)
        {
            double correctionOffset = correction;
            switch (tailDirection)
            {
                case NORTH:
                    correctionOffset = -correction;
                    if (lastDirection != NORTH )
                    {
                        corners++;
                    }
                case SOUTH:
                    if (tailDirection == SOUTH && lastDirection != SOUTH)
                    {
                        corners++;
                    }
                    y = p1y + correctionOffset;
                    addPoint(buffer, x, y, write);

                    if ( x != p1x)
                    {
                        x = p1x;
                        addPoint(buffer, x, y, write);
                        corners++;
                    }

                    y = p1y;
                    addPoint(buffer, x, y, write);
                    corners++;
                    break;
                case WEST:
                    correctionOffset = -correction;
                    if (lastDirection != WEST )
                    {
                        corners++;
                    }
                case EAST:
                    if (tailDirection == EAST && lastDirection != EAST)
                    {
                        corners++;
                    }
                    x = p1x + correctionOffset;
                    addPoint(buffer, x, y, write);

                    if ( y != p1y )
                    {
                        y = p1y;
                        addPoint(buffer, x, y, write);
                        corners++;
                    }

                    x = p1x;
                    addPoint(buffer, x, y, write);
                    corners++;
                    break;
            }
        }
        else
        {

            switch (tailDirection)
            {
                case NORTH:
                case SOUTH:
                    if ( x != p1x)
                    {
                        if (!(lastDirection == EAST || lastDirection == WEST))
                        {
                            corners++;
                        }
                        x = p1x;
                        addPoint(buffer, x, y, write);
                    }
                    y = p1y;
                    addPoint(buffer, x, y, write);
                    corners++;
                    break;
                case WEST:
                case EAST:
                    if ( y != p1y )
                    {
                        if (!(lastDirection == NORTH || lastDirection == SOUTH))
                        {
                            corners++;
                        }
                        y = p1y;
                        addPoint(buffer, x, y, write);
                    }
                    x = p1x;
                    addPoint(buffer, x, y, write);
                    corners++;
                    break;
                case NONE:
                    // getOrthogonalLinePointsAndDirection doesn't yet calculate corners, so determine that here.
                    if ( getNextDirection(lastDirection, x, y, p1x, p1y) == lastDirection)
                    {
                        if ( x != p1x || y != p1y )
                        {
                            // the line is in the same direction, so only one corner needs to be added
                            corners++;
                        }
                    }
                    else
                    {
                        if ( x != p1x || y != p1y )
                        {
                            // the line is not in the same diretion, so it'll end up with two cornes.
                            corners = corners + 2;
                        }
                    }

                    getOrthogonalLinePointsAndDirection(buffer, lastDirection, null,
                                                        x, y,
                                                        p1x, p1y,
                                                        x + 1, y + 1,
                                                        write); // last arguments are arbitrary, as not used, but must not be the same.
            }
        }

        return corners;
    }

    private static final Direction getHeadDirection(final Point2DArray points)
    {
        Point2D p0 = points.get(0);

        Point2D p1 = points.get(1);

        double p0x = p0.getX();

        double p0y = p0.getY();

        double p1x = p1.getX();

        double p1y = p1.getY();

        final double dx = (p1x - p0x);

        final double dy = (p1y - p0y);

        if (dy < 0)
        {
            // if p1 is north, then always go north
            return NORTH;
        }
        else if (dx == 0)
        {
            // if it's directly south, with same x, go south.
            return SOUTH;
        }
        else if (dx > 0)
        {
            // if p1 is south and east, so go east
            return EAST;
        }
        else
        //if (dx > 0 )
        {
            // we know p2 is south and west, so go west.
            return WEST;
        }
    }

    /**
     * Draws an orthogonal line between two points, it uses the previous direction to determine the new direction. It
     * will always attempt to continue the line in the same direction if it can do so, without requiring a corner.
     * If the line goes back on itself, it'll go 50% of the way  and then go perpendicular, so that it no longer goes back on itself.
     */
    private static final Direction getOrthogonalLinePointsAndDirection(final NFastDoubleArrayJSO buffer, final Direction direction, Direction next_direction, double p1x, double p1y, final double p2x, final double p2y, final double p3x, final double p3y, boolean write)
    {
        if ( next_direction == null)
        {
            next_direction = getNextDirection(direction, p1x, p1y, p2x, p2y);
        }

        switch ( next_direction )
        {
            // If the direction ends going back up on itself, it must be split to avoid this.
            case WEST:
            case EAST:
                if ( p2x == p3x && ((p1y > p2y && p3y > p2y)||(p1y < p2y && p3y < p2y) ) )
                {
                    p1x = p1x + ((p2x - p1x)/2);
                    addPoint(buffer, p1x, p1y, write);
                    next_direction = (p2y < p1y) ? NORTH : SOUTH;
                }
                break;
            case NORTH:
            case SOUTH:
                if ( p2y == p3y && ((p1x > p2x && p3x > p2x)||(p1x < p2x && p3x < p2x) ))
                {
                    p1y = p1y + ((p2y - p1y)/2);
                    addPoint(buffer, p1x, p1y, write);
                    next_direction = (p2x < p1x) ? WEST : EAST;
                }
                break;
            default:
                throw new IllegalStateException("This should not be reached (Defensive Code)");
        }


        if ((next_direction == SOUTH) || (next_direction == NORTH))
        {
            if (p1x != p2x)
            {
                addPoint(buffer, p1x, p2y, p2x, p2y, write);
            }
            else
            {
                // points are already on a straight line, so don't try and apply an orthogonal line
                addPoint(buffer, p2x, p2y, write);
            }

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
            if (p1y != p2y)
            {
                addPoint(buffer, p2x, p1y, p2x, p2y, write);
            }
            else
            {
                // points are already on a straight line, so don't try and apply an orthogonal line
                addPoint(buffer, p2x, p2y, write);
            }

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

    private static Direction getNextDirection(Direction direction, double p1x, double p1y, double p2x, double p2y)
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
                throw new IllegalStateException("This should not be reached (Defensive Code)");
        }
        return next_direction;
    }

    private static final void addPoint(final NFastDoubleArrayJSO buffer, final double x, final double y, boolean write)
    {
        if ( write == true )
        {
            addPoint( buffer, x, y);
        }
    }

    private static final void addPoint(final NFastDoubleArrayJSO buffer, final double x0, final double y0, double x1, double y1, boolean write)
    {
        if ( write == true )
        {
            addPoint( buffer, x0, y0,x1, y1);
        }
    }

    private static final int addPoint(final NFastDoubleArrayJSO buffer, final double x, final double y, boolean write, int points)
    {
        if ( write == true )
        {
            addPoint( buffer, x, y);
        }
        points++;
        return points;
    }

    private static final void addPoint(final NFastDoubleArrayJSO buffer, final double x, final double y)
    {
        buffer.push(x, y);
    }

    private static final void addPoint(final NFastDoubleArrayJSO buffer, final double x0, final double y0, double x1, double y1 )
    {
        buffer.push(x0, y0, x1, y1);
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
            points = points.noAdjacentPoints(); // this clones the points, so we are ok to mutate the elements (see tail/head offset)

            if (points.size() > 1)
            {
                final double headOffset = attr.getHeadOffset();

                final double tailOffset = attr.getTailOffset();

                final double correction = attr.getCorrectionOffset();

                Direction headDirection = attr.getHeadDirection();

                Direction tailDirection = attr.getTailDirection();

                headDirection = correctHeadDirection(headDirection, points.get(0), points.get(1));
                if (headOffset > 0)
                {
                    correctHeadWithOffset(points, headOffset, headDirection);
                }

                final Point2D p0 = points.get(0);
                double x0 = p0.getX();
                double y0 = p0.getY();

                final NFastDoubleArrayJSO linept = getOrthogonalLinePoints(points, headDirection, tailDirection, correction);

                if (null != linept)
                {
                    m_list.M(x0, y0);

                    final double radius = getCornerRadius();

                    if (radius > 0)
                    {
                        Geometry.drawArcJoinedLines(m_list, Geometry.getPoints(linept, new Point2D(x0, y0)), radius);
                    }
                    else
                    {
                        drawLines(linept);
                    }
                    return true;
                }
            }
        }
        return false;
    }

    private final void drawLines(final NFastDoubleArrayJSO points)
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

    public double getCornerRadius()
    {
        return getAttributes().getCornerRadius();
    }

    public OrthogonalPolyLine setCornerRadius(final double radius)
    {
        getAttributes().setCornerRadius(radius);

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
    public Point2D getHeadOffsetPoint()
    {
        return m_headOffsetPoint;
    }

    @Override
    public Point2D getTailOffsetPoint()
    {
        return m_tailOffsetPoint;
    }

    @Override
    public List<Attribute> getBoundingBoxAttributes()
    {
        return getBoundingBoxAttributesComposed(Arrays.asList(Attribute.CONTROL_POINTS));
    }

    public static class OrthogonaPolylLineFactory extends AbstractDirectionalMultiPointShapeFactory<OrthogonalPolyLine>
    {
        public OrthogonaPolylLineFactory()
        {
            super(ShapeType.ORTHOGONAL_POLYLINE);

            addAttribute(Attribute.CORNER_RADIUS);

            addAttribute(Attribute.CONTROL_POINTS, true);
        }

        @Override
        public OrthogonalPolyLine create(final JSONObject node, final ValidationContext ctx) throws ValidationException
        {
            return new OrthogonalPolyLine(node, ctx);
        }
    }
}