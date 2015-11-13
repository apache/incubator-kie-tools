/*
 * Copyright 2015 JBoss, by Red Hat, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.uberfire.ext.wires.core.client.util;

/**
 * Useful geometry functions taken from Java's Line2D class
 */
public class GeometryUtil {

    /**
     * Returns the square of the distance from a point to a line segment.
     * The distance measured is the distance between the specified
     * point and the closest point between the specified end points.
     * If the specified point intersects the line segment in between the
     * end points, this method returns 0.0.
     * <p/>
     * See http://docs.oracle.com/javase/6/docs/api/java/awt/geom/Line2D.html#ptSegDist%28double,%20double,%20double,%20double,%20double,%20double%29
     * @param x1 the X coordinate of the start point of the
     * specified line segment
     * @param y1 the Y coordinate of the start point of the
     * specified line segment
     * @param x2 the X coordinate of the end point of the
     * specified line segment
     * @param y2 the Y coordinate of the end point of the
     * specified line segment
     * @param px the X coordinate of the specified point being
     * measured against the specified line segment
     * @param py the Y coordinate of the specified point being
     * measured against the specified line segment
     * @return a double value that is the square of the distance from the
     *         specified point to the specified line segment.
     */
    public static double ptSegDistSq( double x1,
                                      double y1,
                                      double x2,
                                      double y2,
                                      double px,
                                      double py ) {
        // Adjust vectors relative to x1,y1
        // x2,y2 becomes relative vector from x1,y1 to end of segment
        x2 -= x1;
        y2 -= y1;
        // px,py becomes relative vector from x1,y1 to test point
        px -= x1;
        py -= y1;
        double dotprod = px * x2 + py * y2;
        double projlenSq;
        if ( dotprod <= 0.0 ) {
            // px,py is on the side of x1,y1 away from x2,y2
            // distance to segment is length of px,py vector
            // "length of its (clipped) projection" is now 0.0
            projlenSq = 0.0;
        } else {
            // switch to backwards vectors relative to x2,y2
            // x2,y2 are already the negative of x1,y1=>x2,y2
            // to get px,py to be the negative of px,py=>x2,y2
            // the dot product of two negated vectors is the same
            // as the dot product of the two normal vectors
            px = x2 - px;
            py = y2 - py;
            dotprod = px * x2 + py * y2;
            if ( dotprod <= 0.0 ) {
                // px,py is on the side of x2,y2 away from x1,y1
                // distance to segment is length of (backwards) px,py vector
                // "length of its (clipped) projection" is now 0.0
                projlenSq = 0.0;
            } else {
                // px,py is between x1,y1 and x2,y2
                // dotprod is the length of the px,py vector
                // projected on the x2,y2=>x1,y1 vector times the
                // length of the x2,y2=>x1,y1 vector
                projlenSq = dotprod * dotprod / ( x2 * x2 + y2 * y2 );
            }
        }
        // Distance to line is now the length of the relative point
        // vector minus the length of its projection onto the line
        // (which is zero if the projection falls outside the range
        //  of the line segment).
        double lenSq = px * px + py * py - projlenSq;
        if ( lenSq < 0 ) {
            lenSq = 0;
        }
        return lenSq;
    }

}
