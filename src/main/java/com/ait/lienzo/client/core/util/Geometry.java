/*
   Copyright (c) 2014 Ahome' Innovation Technologies. All rights reserved.

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

package com.ait.lienzo.client.core.util;

import com.ait.lienzo.client.core.types.Point2D;

/**
 * Static utility methods related to geometry and other math.
 *
 */
public class Geometry
{
    /**
     * Converts angle from degrees to radians.
     * 
     * @param angdeg
     * 
     * @return Angle converted from degrees to radians.
     */
    public static double toRadians(double angdeg)
    {
        return Math.toRadians(angdeg);
    }

    /**
     * Converts angle from radians to degrees.
     * 
     * @param angrad
     * 
     * @return Angle converted from radians to degrees.
     */
    public static double toDegrees(double angrad)
    {
        return Math.toDegrees(angrad);
    }

    public static double slope(Point2D a, Point2D b)
    {
        return slope(b.getX(), a.getX(), b.getY(), a.getY());
    }

    public static double slope(double x1, double y1, double x2, double y2)
    {
        final double dx = (x2 - x1);

        final double dy = (y2 - y1);

        boolean xbig = (Math.abs(dx) > Math.abs(dy));

        return (xbig) ? (dy / dx) : (dx / dy);
    }

    public static final double distance(double dx, double dy)
    {
        return Math.sqrt((dx * dx) + (dy * dy));
    }

    public static final double getVectorRatio(double[] u, double[] v)
    {
        return ((u[0] * v[0]) + (u[1] * v[1])) / (distance(u[0], u[1]) * distance(v[0], v[1]));
    }

    public static final double getVectorAngle(double[] u, double[] v)
    {
        return (((u[0] * v[1]) < (u[1] * v[0])) ? -1 : 1) * Math.acos(getVectorRatio(u, v));
    }
}
