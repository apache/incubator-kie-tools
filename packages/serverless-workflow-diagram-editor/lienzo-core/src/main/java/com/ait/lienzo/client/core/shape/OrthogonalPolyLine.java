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

    private static NFastDoubleArray drawOrthogonalLinePoints(final Point2DArray points, Direction headDirection, Direction tailDirection, final double correction, final OrthogonalPolyLine pline, double breakDistance, boolean write) {
        final NFastDoubleArray buffer = new NFastDoubleArray();

        Point2D p0 = points.get(0);
        p0 = OrthogonalLineUtils.correctP0(headDirection, correction, pline.getHeadOffset(), write, buffer, p0);

        int i = 1;
        Direction direction = headDirection;
        final int size = points.size();
        Point2D p1;
        Point2D p2;

        for (; i < size - 1; i++) {
            p1 = points.get(i);

            p2 = points.get(i + 1);

            direction = OrthogonalLineUtils.drawOrthogonalLineSegment(buffer, direction, null, p0.getX(), p0.getY(), p1.getX(), p1.getY(), write);

            if (null == direction) {
                return null;
            }
            p0 = p1;
        }
        p1 = points.get(size - 1);

        OrthogonalLineUtils.drawTail(points, buffer, direction, tailDirection, p0, p1, correction, pline.getHeadOffset(), pline.getTailOffset());

        return buffer;
    }

    @Override
    public BoundingBox getBoundingBox() {
        if (getPathPartList().size() < 1) {
            if (!parse()) {
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