/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ait.lienzo.client.core.shape;

import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.types.Point2DArray;
import com.ait.lienzo.shared.core.types.Direction;
import com.ait.lienzo.tools.client.collection.NFastDoubleArray;

import static com.ait.lienzo.shared.core.types.Direction.EAST;
import static com.ait.lienzo.shared.core.types.Direction.NORTH;
import static com.ait.lienzo.shared.core.types.Direction.SOUTH;
import static com.ait.lienzo.shared.core.types.Direction.WEST;

public class OrthogonalLineUtils {

    public static Point2D correctEndWithOffset(double offset, Direction direction, final Point2D target) {
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

    public static Point2D correctP0(Direction headDirection, double correction, final double headOffset, boolean write, NFastDoubleArray buffer, Point2D p0) {
        if (!write) {
            p0 = p0.copy();
        }

        // correct for headOffset
        if (headOffset > 0) {
            OrthogonalLineUtils.correctEndWithOffset(headOffset, headDirection, p0);
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
            OrthogonalLineUtils.correctEndWithOffset(correction, headDirection, p0);
            // addBoundingBox another point of the correction, to ensure the line is always visible at the tip of the arrow
            addPoint(buffer, p0.getX(), p0.getY(), write);
        }
        return p0;
    }

    /**
     * Draws an orthogonal line between two points, it uses the previous direction to determine the new direction. It
     * will always attempt to continue the line in the same direction if it can do so, without requiring a corner.
     * If the line goes back on itself, it'll go 50% of the way  and then go perpendicular, so that it no longer goes back on itself.
     */
    public static Direction drawOrthogonalLineSegment(final NFastDoubleArray buffer, final Direction direction, Direction nextDirection, double p1x, double p1y, final double p2x, final double p2y, boolean write) {
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
     */
    public static Direction getNextDirection(Direction direction, double p1x, double p1y, double p2x, double p2y) {
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
     */
    public static Direction getTailDirection(Point2DArray points, NFastDoubleArray buffer, Direction lastDirection, Direction tailDirection, double correction, double headOffset, double tailOffset, double p0x, double p0y, double p1x, double p1y) {
        double offset = headOffset + correction;
        switch (tailDirection) {
            case NONE: {
                final double dx = (p1x - p0x);
                final double dy = (p1y - p0y);

                int bestPoints = 0;

                if (dx > offset) {
                    tailDirection = WEST;
                    bestPoints = drawTail(points, buffer, lastDirection, WEST, correction, tailOffset, p0x, p0y, p1x, p1y, false);
                } else {
                    tailDirection = EAST;
                    bestPoints = drawTail(points, buffer, lastDirection, EAST, correction, tailOffset, p0x, p0y, p1x, p1y, false);
                }

                if (dy > 0) {
                    int points3 = drawTail(points, buffer, lastDirection, NORTH, correction, tailOffset, p0x, p0y, p1x, p1y, false);

                    if (points3 < bestPoints) {
                        tailDirection = NORTH;
                        bestPoints = points3;
                    }
                } else {
                    int points4 = drawTail(points, buffer, lastDirection, SOUTH, correction, tailOffset, p0x, p0y, p1x, p1y, false);
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

    public static void drawTail(Point2DArray points, NFastDoubleArray buffer, Direction lastDirection, Direction tailDirection, Point2D p0, Point2D p1, final double correction, final double headoffset, final double tailOffset) {
        double p0x = p0.getX();

        double p0y = p0.getY();

        double p1x = p1.getX();

        double p1y = p1.getY();

        // This returns an array, as drawTail needs both the direction and the number of corner points.
        tailDirection = getTailDirection(points, buffer, lastDirection, tailDirection, correction, headoffset, tailOffset, p0x, p0y, p1x, p1y);

        drawTail(points, buffer, lastDirection, tailDirection, correction, tailOffset, p0x, p0y, p1x, p1y, true);
    }

    /**
     * Draws the last segment of the line to the tail.
     * It will take into account the correction and arrow.
     * Logic is applied to help draw an attractive line. Under certain conditions it will attempt to addBoundingBox an extra mid point. For example if you have directions
     * going opposite to each other, it will create a mid point so that the line goes back on itseld through this mid point.
     */
    public static int drawTail(Point2DArray points, NFastDoubleArray buffer, Direction lastDirection, Direction tailDirection, double correction, double tailOffset, double p0x, double p0y, double p1x, double p1y, boolean write) {
        Point2D p1 = points.get(points.size() - 1);

        // correct for tailOffset
        if (tailOffset > 0) {
            if (!write) {
                p1 = p1.copy();
            }
            OrthogonalLineUtils.correctEndWithOffset(tailOffset, tailDirection, p1);
            p1x = p1.getX();
            p1y = p1.getY();
        }

        // correct for correction
        if (correction > 0) {
            // must do this off a cloned Point2D, as we still need the p1, for the last part of the line at the end.
            Point2D p1Copy = p1.copy();
            OrthogonalLineUtils.correctEndWithOffset(correction, tailDirection, p1Copy);
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

    public static void addPoint(final NFastDoubleArray buffer, final double x, final double y, boolean write) {
        if (write) {
            addPoint(buffer, x, y);
        }
    }

    public static void addPoint(final NFastDoubleArray buffer, final double x0, final double y0, double x1, double y1, boolean write) {
        if (write) {
            buffer.push(x0, y0, x1, y1);
        }
    }

    public static void addPoint(final NFastDoubleArray buffer, final double x, final double y) {
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

}
