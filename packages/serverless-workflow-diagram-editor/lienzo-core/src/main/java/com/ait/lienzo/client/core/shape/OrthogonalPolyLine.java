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
import com.ait.lienzo.shared.core.types.Direction;
import com.ait.lienzo.shared.core.types.ShapeType;
import com.ait.lienzo.tools.client.collection.NFastDoubleArray;
import jsinterop.annotations.JsProperty;

import static com.ait.lienzo.shared.core.types.Direction.EAST;
import static com.ait.lienzo.shared.core.types.Direction.NONE;
import static com.ait.lienzo.shared.core.types.Direction.NORTH;
import static com.ait.lienzo.shared.core.types.Direction.NORTH_WEST;
import static com.ait.lienzo.shared.core.types.Direction.SOUTH;
import static com.ait.lienzo.shared.core.types.Direction.SOUTH_WEST;
import static com.ait.lienzo.shared.core.types.Direction.WEST;

/**
 * Draws Orthogonal lines based on Head and Tail Directions.
 * When the HEAD direction is NONE it bases the direction on the position of the next point. If the point is above it goes vertical up and then along.
 * If the point is below it goes horizontally first and then down. If Tail is NONE it actualy attempts to draw for multiple directions and picks the one
 * with the lowest corner counts.
 * Outsie of this it is expecting that the directions have alread been resolved to N, E, S and W. Corner directions on the left go W and on the right go E.
 */
public class OrthogonalPolyLine extends AbstractDirectionalMultiPointShape<OrthogonalPolyLine> {

    private Point2D m_headOffsetPoint;

    private Point2D m_tailOffsetPoint;

    private Point2DArray m_computedPoint2DArray;

    private double m_breakDistance;

    @JsProperty
    private double cornerRadius;

    public OrthogonalPolyLine(final Point2D... points) {
        this(Point2DArray.fromArrayOfPoint2D(points));
    }

    public OrthogonalPolyLine(final Point2DArray points) {
        super(ShapeType.ORTHOGONAL_POLYLINE);

        setControlPoints(points);
    }

    public OrthogonalPolyLine(final Point2DArray points, final double corner) {
        this(points);

        setCornerRadius(corner);
    }

    @Override
    public boolean parse() {
        Point2DArray points = correctBreakDistance(getControlPoints(), m_breakDistance);

        if (points.size() > 1) {
            final double headOffset = getHeadOffset();
            final double correction = getCorrectionOffset();
            Direction headDirection = getHeadDirection();
            Direction tailDirection = getTailDirection();

            if (headDirection == NONE) {
                Point2D p0 = points.get(0);
                Point2D p1 = points.get(1);
                double headOffsetAndCorrect = headOffset + correction;
                headDirection = getHeadDirection(points, null, headDirection, tailDirection, p0, p1, headOffsetAndCorrect, correction, this);
            }

            final NFastDoubleArray opoint = drawOrthogonalLinePoints(points, headDirection, tailDirection, correction, this, m_breakDistance, true);

            m_headOffsetPoint = points.get(0);
            m_tailOffsetPoint = points.get(points.size() - 1);

            if (null != opoint) {
                final PathPartList list = getPathPartList();
                list.M(m_headOffsetPoint.getX(), m_headOffsetPoint.getY());
                final double radius = getCornerRadius();

                m_computedPoint2DArray = Point2DArray.fromNFastDoubleArray(opoint);

                if (radius > 0) {
                    Geometry.drawArcJoinedLines(list, m_computedPoint2DArray, radius);
                } else {
                    final int size = opoint.size();
                    // start at 2, as M is for opoint[0]
                    for (int i = 2; i < size; i += 2) {
                        list.L(opoint.get(i), opoint.get(i + 1));
                    }
                }
            }

            return true;
        }

        m_computedPoint2DArray = null;
        return false;
    }

    public final Point2DArray correctBreakDistance(Point2DArray points, double breakDistance) {
        Point2DArray cPoints = points.copy();

        Point2D p1, p2;

        final int size = cPoints.size();

        for (int i = 0; i < size - 1; i++) {
            p1 = cPoints.get(i);
            p2 = cPoints.get(i + 1);

            if (Geometry.closeEnough(p1.getX(), p2.getX(), breakDistance)) {
                p2.setX(p1.getX());
            }

            if (Geometry.closeEnough(p1.getY(), p2.getY(), breakDistance)) {
                p2.setY(p1.getY());
            }
        }

        return cPoints;
    }

    private static final Direction getHeadDirection(Point2DArray points, NFastDoubleArray buffer, Direction headDirection, Direction tailDirection, Point2D p0, Point2D p1, double headOffsetAndCorrection, final double correction, final OrthogonalPolyLine pline) {
        double p0x = p0.getX();
        double p0y = p0.getY();
        double p1x = p1.getX();
        double p1y = p1.getY();
        final double dx = (p1x - p0x);
        final double dy = (p1y - p0y);

        boolean verticalOverlap = (dx > -headOffsetAndCorrection && dx < headOffsetAndCorrection);
        boolean horizontalOverlap = (dy > -headOffsetAndCorrection && dy < headOffsetAndCorrection);

        switch (headDirection) {
            case NONE: {
                Direction p0ToP1Direction = Geometry.getQuadrant(p0x, p0y, p1x, p1y);
                switch (p0ToP1Direction) {
                    case SOUTH_WEST:
                    case SOUTH_EAST:
                        headDirection = (p0ToP1Direction == SOUTH_WEST) ? WEST : EAST;
                        if (verticalOverlap) {
                            headDirection = SOUTH;
                        }
                        break;
                    case NORTH_WEST:
                    case NORTH_EAST:
                        headDirection = (p0ToP1Direction == NORTH_WEST) ? WEST : EAST;
                        if (!horizontalOverlap) {
                            headDirection = NORTH;
                        }
                        break;
                }
                break;
            }
            default:
                // return head and tail, as is.
                break;
        }

        return headDirection;
    }

    public static final Point2D correctEndWithOffset(double offset, Direction direction, final Point2D target) {
        switch (direction) {
            case NORTH:
                return target.setY(target.getY() - offset);
            case EAST:
                return target.setX(target.getX() + offset);
            case SOUTH:
                return target.setY(target.getY() + offset);
            case WEST:
                return target.setX(target.getX() - offset);
            case NONE:
                return target;
            default:
                throw new IllegalStateException("Invalid Direction " + direction);
        }
    }

    private static Point2D correctP0(Direction headDirection, double correction, OrthogonalPolyLine pline, boolean write, NFastDoubleArray buffer, Point2D p0) {
        if (!write) {
            p0 = p0.copy();
        }

        final double headOffset = pline.getHeadOffset();

        // correct for headOffset
        if (headOffset > 0) {
            correctEndWithOffset(headOffset, headDirection, p0);
        }

        // addBoundingBox starting point, that may have head offset
        addPoint(buffer, p0.getX(), p0.getY(), write);

        // correct for correction
        if (correction > 0) {
            // must do this off a cloned Point2D, as points[0] is used for M operation, during line drawing.
            if (write) {
                // if !write, we are already working on a copy
                p0 = p0.copy();
            }
            correctEndWithOffset(correction, headDirection, p0);
            // addBoundingBox another point of the correction, to ensure the line is always visible at the tip of the arrow
            addPoint(buffer, p0.getX(), p0.getY(), write);
        }
        return p0;
    }

    private static final NFastDoubleArray drawOrthogonalLinePoints(final Point2DArray points, Direction headDirection, Direction tailDirection, final double correction, final OrthogonalPolyLine pline, double breakDistance, boolean write) {
        final NFastDoubleArray buffer = new NFastDoubleArray();

        Point2D p0 = points.get(0);
        p0 = correctP0(headDirection, correction, pline, write, buffer, p0);

        int i = 1;
        Direction direction = headDirection;
        final int size = points.size();
        Point2D p1;
        Point2D p2;

        for (; i < size - 1; i++) {
            p1 = points.get(i);

            p2 = points.get(i + 1);

            direction = drawOrthogonalLineSegment(buffer, direction, null, p0.getX(), p0.getY(), p1.getX(), p1.getY(), p2.getX(), p2.getY(), write);

            if (null == direction) {
                return null;
            }
            p0 = p1;
        }
        p1 = points.get(size - 1);

        drawTail(points, buffer, direction, tailDirection, p0, p1, correction, pline);

        return buffer;
    }

    /**
     * Draws an orthogonal line between two points, it uses the previous direction to determine the new direction. It
     * will always attempt to continue the line in the same direction if it can do so, without requiring a corner.
     * If the line goes back on itself, it'll go 50% of the way  and then go perpendicular, so that it no longer goes back on itself.
     */
    private static final Direction drawOrthogonalLineSegment(final NFastDoubleArray buffer, final Direction direction, Direction nextDirection, double p1x, double p1y, final double p2x, final double p2y, final double p3x, final double p3y, boolean write) {
        if (nextDirection == null) {
            nextDirection = getNextDirection(direction, p1x, p1y, p2x, p2y);
        }

        if ((nextDirection == SOUTH) || (nextDirection == NORTH)) {
            if (p1x == p2x) {
                // points are already on a straight line, so don't try and apply an orthogonal line
                addPoint(buffer, p2x, p2y, write);
            } else {
                addPoint(buffer, p1x, p2y, p2x, p2y, write);
            }
            if (p1x < p2x) {
                return EAST;
            } else if (p1x > p2x) {
                return WEST;
            } else {
                return nextDirection;
            }
        } else {
            if (p1y != p2y) {
                addPoint(buffer, p2x, p1y, p2x, p2y, write);
            } else {
                // points are already on a straight line, so don't try and apply an orthogonal line
                addPoint(buffer, p2x, p2y, write);
            }
            if (p1y > p2y) {
                return NORTH;
            } else if (p1y < p2y) {
                return SOUTH;
            } else {
                return nextDirection;
            }
        }
    }

    /**
     * looks at the current and target points and based on the current direction returns the next direction. This drives the orthogonal line drawing.
     *
     * @param direction
     * @param p1x
     * @param p1y
     * @param p2x
     * @param p2y
     * @return
     */
    private static Direction getNextDirection(Direction direction, double p1x, double p1y, double p2x, double p2y) {
        Direction next_direction;

        switch (direction) {
            case NORTH:
                if (p2y < p1y) {
                    next_direction = NORTH;
                } else if (p2x > p1x) {
                    next_direction = EAST;
                } else {
                    next_direction = WEST;
                }
                break;
            case SOUTH:
                if (p2y > p1y) {
                    next_direction = SOUTH;
                } else if (p2x > p1x) {
                    next_direction = EAST;
                } else {
                    next_direction = WEST;
                }
                break;
            case EAST:
                if (p2x > p1x) {
                    next_direction = EAST;
                } else if (p2y < p1y) {
                    next_direction = NORTH;
                } else {
                    next_direction = SOUTH;
                }
                break;
            case WEST:
                if (p2x < p1x) {
                    next_direction = WEST;
                } else if (p2y < p1y) {
                    next_direction = NORTH;
                } else {
                    next_direction = SOUTH;
                }
                break;
            default:
                throw new IllegalStateException("This should not be reached (Defensive Code)");
        }
        return next_direction;
    }

    /**
     * When tail is NONE it needs to try multiple directions to determine which gives the least number of corners, and then selects that as the final direction.
     *
     * @param points
     * @param buffer
     * @param lastDirection
     * @param tailDirection
     * @param correction
     * @param pline
     * @param p0x
     * @param p0y
     * @param p1x
     * @param p1y
     * @return
     */
    private static Direction getTailDirection(Point2DArray points, NFastDoubleArray buffer, Direction lastDirection, Direction tailDirection, double correction, OrthogonalPolyLine pline, double p0x, double p0y, double p1x, double p1y) {
        double offset = pline.getHeadOffset() + correction;
        switch (tailDirection) {
            case NONE: {
                final double dx = (p1x - p0x);
                final double dy = (p1y - p0y);

                int bestPoints = 0;

                if (dx > offset) {
                    tailDirection = WEST;
                    bestPoints = drawTail(points, buffer, lastDirection, WEST, correction, pline, p0x, p0y, p1x, p1y, false);
                } else {
                    tailDirection = EAST;
                    bestPoints = drawTail(points, buffer, lastDirection, EAST, correction, pline, p0x, p0y, p1x, p1y, false);
                }

                if (dy > 0) {
                    int points3 = drawTail(points, buffer, lastDirection, NORTH, correction, pline, p0x, p0y, p1x, p1y, false);

                    if (points3 < bestPoints) {
                        tailDirection = NORTH;
                        bestPoints = points3;
                    }
                } else {
                    int points4 = drawTail(points, buffer, lastDirection, SOUTH, correction, pline, p0x, p0y, p1x, p1y, false);
                    if (points4 < bestPoints) {
                        tailDirection = SOUTH;
                        bestPoints = points4;
                    }
                }

                break;
            }
            default:
                break;
        }
        return tailDirection;
    }

    private static final void drawTail(Point2DArray points, NFastDoubleArray buffer, Direction lastDirection, Direction tailDirection, Point2D p0, Point2D p1, final double correction, final OrthogonalPolyLine pline) {
        double p0x = p0.getX();

        double p0y = p0.getY();

        double p1x = p1.getX();

        double p1y = p1.getY();

        // This returns an array, as drawTail needs both the direction and the number of corner points.
        tailDirection = getTailDirection(points, buffer, lastDirection, tailDirection, correction, pline, p0x, p0y, p1x, p1y);

        drawTail(points, buffer, lastDirection, tailDirection, correction, pline, p0x, p0y, p1x, p1y, true);
    }

    /**
     * Draws the last segment of the line to the tail.
     * It will take into account the correction and arrow.
     * Logic is applied to help draw an attractive line. Under certain conditions it will attempt to addBoundingBox an extra mid point. For example if you have directions
     * going opposite to each other, it will create a mid point so that the line goes back on itseld through this mid point.
     *
     * @param points
     * @param buffer
     * @param lastDirection
     * @param tailDirection
     * @param correction
     * @param pline
     * @param p0x
     * @param p0y
     * @param p1x
     * @param p1y
     * @param write
     * @return
     */
    private static int drawTail(Point2DArray points, NFastDoubleArray buffer, Direction lastDirection, Direction tailDirection, double correction, final OrthogonalPolyLine pline, double p0x, double p0y, double p1x, double p1y, boolean write) {
        double tailOffset = pline.getTailOffset();

        double distance = 0;
        Point2D p1 = points.get(points.size() - 1);

        // correct for tailOffset
        if (tailOffset > 0) {
            if (!write) {
                p1 = p1.copy();
            }
            correctEndWithOffset(tailOffset, tailDirection, p1);
            p1x = p1.getX();
            p1y = p1.getY();
        }

        // correct for correction
        if (correction > 0) {
            // must do this off a cloned Point2D, as we still need the p1, for the last part of the line at the end.
            Point2D p1Copy = p1.copy();
            correctEndWithOffset(correction, tailDirection, p1Copy);
            p1x = p1Copy.getX();
            p1y = p1Copy.getY();
        }

        final double dx = (p1x - p0x);
        final double dy = (p1y - p0y);

        int corners = 0;

        boolean behind = false;

        switch (tailDirection) {
            case NORTH:
                behind = dy < 0;
                break;
            case SOUTH:
                behind = dy > 0;
                break;
            case WEST:
                behind = dx < 0;
                break;
            case EAST:
                behind = dx > 0;
                break;
            case NONE:
                // do nothing as NONE is explicitey handled at the end
                break;
            default:
                throw new IllegalStateException("Invalid Direction " + tailDirection);
        }
        double x = p0x;

        double y = p0y;

        if (behind) {
            // means p0 is behind.
            switch (tailDirection) {
                case NORTH:
                case SOUTH:
                    if ((lastDirection == NORTH && tailDirection == SOUTH) ||
                            (lastDirection == SOUTH && tailDirection == NORTH) ||
                            (dx > 0 && lastDirection == EAST) ||
                            (dx < 0 && lastDirection == WEST)) {
                        // A mid point is needed to ensure an attractive line is drawn.
                        x = p0x + (dx / 2);
                        addPoint(buffer, x, y, write);

                        if (lastDirection == NORTH || lastDirection == SOUTH) {
                            corners++;
                        }
                    }

                    y = p1y;
                    addPoint(buffer, x, y, write);
                    if (lastDirection != tailDirection) {
                        corners++;
                    }

                    x = p1x;
                    addPoint(buffer, x, y, write);
                    corners++;

                    y = p1.getY();
                    addPoint(buffer, x, y, write);
                    corners++;
                    break;
                case WEST:
                case EAST:
                    if ((lastDirection == WEST && tailDirection == EAST) ||
                            (lastDirection == EAST && tailDirection == WEST) ||
                            (dy > 0 && lastDirection == SOUTH) ||
                            (dy < 0 && lastDirection == NORTH)) {
                        // A mid point is needed to ensure an attrictive line is drawn.
                        y = p0y + (dy / 2);
                        addPoint(buffer, x, y, write);

                        if (lastDirection == EAST || lastDirection == WEST) {
                            corners++;
                        }
                    }

                    x = p1x;
                    addPoint(buffer, x, y, write);
                    if (lastDirection != tailDirection) {
                        corners++;
                    }

                    y = p1y;
                    addPoint(buffer, x, y, write);
                    corners++;

                    x = p1.getX();
                    addPoint(buffer, x, y, write);
                    corners++;
                    break;
                default:
                    throw new IllegalStateException("Invalid Direction " + tailDirection);
            }
        } else {
            // means p0 is in front
            switch (tailDirection) {
                case NORTH:
                case SOUTH:
                    if ((lastDirection == NORTH && tailDirection == SOUTH) ||
                            (lastDirection == SOUTH && tailDirection == NORTH) ||
                            (dx > 0 && lastDirection == WEST) ||
                            (dx < 0 && lastDirection == EAST)) {
                        // A mid point is needed to ensure an attrictive line is drawn.
                        y = p0y + (dy / 2);
                        addPoint(buffer, x, y, write);

                        if (lastDirection == EAST || lastDirection == WEST) {
                            lastDirection = (dy < 0) ? NORTH : SOUTH;
                            corners++;
                        }
                    }

                    x = p1x;
                    addPoint(buffer, x, y, write);
                    if (lastDirection == NORTH || lastDirection == SOUTH) {
                        corners++;
                    }

                    y = p1.getY();
                    addPoint(buffer, x, y, write);
                    corners++;
                    break;
                case WEST:
                case EAST:
                    if ((lastDirection == WEST && tailDirection == EAST) ||
                            (lastDirection == EAST && tailDirection == WEST) ||
                            (dy > 0 && lastDirection == NORTH) ||
                            (dy < 0 && lastDirection == SOUTH)) {
                        // A mid point is needed to ensure an attrictive line is drawn.
                        x = p0x + (dx / 2);
                        addPoint(buffer, x, y, write);

                        if (lastDirection == NORTH || lastDirection == SOUTH) {
                            lastDirection = (dx < 0) ? WEST : EAST;
                            corners++;
                        }
                    }

                    y = p1y;
                    addPoint(buffer, x, y, write);
                    if (lastDirection == EAST || lastDirection == WEST) {
                        corners++;
                    }

                    x = p1.getX();
                    addPoint(buffer, x, y, write);
                    corners++;
                    break;
                default:
                    throw new IllegalStateException("Invalid Direction " + tailDirection);
            }
        }

        return corners;
    }

    private static final void addPoint(final NFastDoubleArray buffer, final double x, final double y, boolean write) {
        if (write == true) {
            addPoint(buffer, x, y);
        }
    }

    private static final void addPoint(final NFastDoubleArray buffer, final double x0, final double y0, double x1, double y1, boolean write) {
        if (write == true) {
            buffer.push(x0, y0, x1, y1);
        }
    }

    private static final void addPoint(final NFastDoubleArray buffer, final double x, final double y) {
        // always attempt to normalise
        if (!buffer.isEmpty()) {
            double x1 = buffer.get(buffer.size() - 2);
            double y1 = buffer.get(buffer.size() - 1);

            if (x == x1 && y == y1) {
                // New point is the same as old point. The code should probably be changed, so that situation didn't occur.
                // But at the moment not entirely sure how to do that, so fixing sympton that than cause (mdp).
                return;
            }
        }

        buffer.push(x, y);
    }

    @Override
    public BoundingBox getBoundingBox() {
        if (getPathPartList().size() < 1) {
            if (false == parse()) {
                return BoundingBox.fromDoubles(0, 0, 0, 0);
            }
        }
        return getPathPartList().getBoundingBox();
    }

    @Override
    protected boolean fill(Context2D context, double alpha) {
        return false;
    }

    public double getCornerRadius() {
        return this.cornerRadius;
    }

    public OrthogonalPolyLine setCornerRadius(final double radius) {
        this.cornerRadius = radius;

        return refresh();
    }

    public double getBreakDistance() {
        return m_breakDistance;
    }

    public OrthogonalPolyLine setBreakDistance(double distance) {
        m_breakDistance = distance;

        return refresh();
    }

    @Override
    public OrthogonalPolyLine setPoint2DArray(final Point2DArray points) {
        return setControlPoints(points);
    }

    @Override
    public Point2DArray getPoint2DArray() {
        return getControlPoints();
    }

    public Point2DArray getComputedPoint2DArray() {
        return m_computedPoint2DArray;
    }

    @Override
    public boolean isControlPointShape() {
        return true;
    }

    @Override
    public Point2D getHeadOffsetPoint() {
        return m_headOffsetPoint;
    }

    @Override
    public Point2D getTailOffsetPoint() {
        return m_tailOffsetPoint;
    }

    @Override
    public List<Attribute> getBoundingBoxAttributes() {
        return getBoundingBoxAttributesComposed(Attribute.CONTROL_POINTS, Attribute.CORNER_RADIUS);
    }

    @Override
    public Shape<OrthogonalPolyLine> copyTo(Shape<OrthogonalPolyLine> other) {
        super.copyTo(other);
        ((OrthogonalPolyLine) other).m_headOffsetPoint = m_headOffsetPoint.copy();
        ((OrthogonalPolyLine) other).m_tailOffsetPoint = m_tailOffsetPoint.copy();
        ((OrthogonalPolyLine) other).m_computedPoint2DArray = m_computedPoint2DArray.copy();
        ((OrthogonalPolyLine) other).m_breakDistance = m_breakDistance;
        ((OrthogonalPolyLine) other).cornerRadius = cornerRadius;

        return other;
    }

    @Override
    public OrthogonalPolyLine cloneLine() {
        OrthogonalPolyLine orthogonalPolyLine = new OrthogonalPolyLine(this.getControlPoints().copy(), cornerRadius);
        return (OrthogonalPolyLine) copyTo(orthogonalPolyLine);
    }
}