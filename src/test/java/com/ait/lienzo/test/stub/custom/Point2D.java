/*
 *
 *    Copyright (c) 2017 Ahome' Innovation Technologies. All rights reserved.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 */

package com.ait.lienzo.test.stub.custom;

import com.ait.lienzo.client.core.util.Geometry;
import com.ait.lienzo.client.core.util.GeometryException;
import com.ait.lienzo.test.annotation.StubClass;

/**
 * This is an example of a custom stub for class <code>com.ait.lienzo.client.core.types.Point2D</code>. This library already provides
 * an stub class for it, but this can be used as an example if you need to overrade the behavior from the build-in one and provide
 * your own for a concrete unit test class.
 *
 * This class overrides the implementation for the <code>add</code> and <code>toString</code> methods, used as an example.
 *
 * @author Roger Martinez
 * @since 1.0
 *
 */
@StubClass("com.ait.lienzo.client.core.types.Point2D")
public class Point2D
{
    private double x;

    private double y;

    /** Stubbed wih custom impl. **/
    @Override
    public String toString()
    {
        return "{x=" + getX() + "} {y=" + getY() + "}";
    }

    /** Stubbed wih custom impl. **/
    public Point2D add(final Point2D p)
    {
        return new Point2D(this.x + p.getY(), this.y + p.getX());
    }

    public Point2D(final com.ait.lienzo.client.core.types.Point2D.Point2DJSO jso)
    {
        this();
    }

    /**
     * Constructs a Point2D at (0,0)
     */
    public Point2D()
    {
    }

    public final String toJSONString()
    {
        return toString();
    }

    /**
     * Constructs a Point2D at (x,y)
     *
     * @param x double
     * @param y double
     */
    public Point2D(final double x, final double y)
    {
        this();
        this.x = x;
        this.y = y;
    }

    public Point2D(final Point2D p)
    {
        this(p.getX(), p.getY());
    }

    public final Point2D copy()
    {
        return new Point2D(this.getX(), this.getY());
    }

    /**
     * Returns the x coordinate
     * @return double
     */
    public double getX()
    {
        return x;
    }

    /**
     * Sets the x coordinate
     * @param x double
     * @return this Point2D
     */
    public Point2D setX(final double x)
    {
        this.x = x;

        return this;
    }

    /**
     * Returns the y coordinate
     * @return double
     */
    public double getY()
    {
        return y;
    }

    /**
     * Sets the y coordinate
     * @param y double
     * @return this Point2D
     */
    public Point2D setY(final double y)
    {
        this.y = y;

        return this;
    }

    /**
     * Sets the x and y coordinates to those of point P.
     *
     * @param p Point2D
     * @return this Point2D
     */
    public Point2D set(final Point2D p)
    {
        this.x = p.getX();
        this.y = p.getY();

        return this;
    }

    /**
     * Returns the length of the vector from (0,0) to this Point2D.
     *
     * @return double
     */
    public double getLength()
    {
        // TODO
        return 0;
    };

    /**
     * Returns the distance from this Point2D to the other Point2D.
     *
     * @param other Point2D
     * @return double
     */
    public double distance(final Point2D other)
    {
        // TODO
        return 0;
    };

    /**
     * Returns the distance from point A to point B.
     *
     * @param a Point2D
     * @param b Point2D
     * @return double
     */
    public static double distance(final Point2D a, final Point2D b)
    {
        // TODO
        return 0;
    };

    public Point2D offset(final double x, final double y)
    {
        // TODO

        return this;
    }

    public Point2D offset(final Point2D p)
    {
        // TODO

        return this;
    }

    public Point2D minus(final double x, final double y)
    {
        // TODO

        return this;
    }

    public final Point2D minus(final Point2D p)
    {
        // TODO

        return this;
    }

    public Point2D normalize(final double length)
    {
        final double x = getX();

        final double y = getY();

        if (((x == 0) && (y == 0)) || (length == 0))
        {
            return this;
        }
        final double angle = Math.atan2(y, x);

        this.x = Math.cos(angle) * length;
        this.y = Math.sin(angle) * length;

        return this;
    }

    /**
     * Returns a new point by subtracting the coordinates of this point and point P,
     * i.e. (this.x - p.x, this.y - p.y)
     * <p>
     * This Point2D is not modified.
     *
     * @param p Point2D
     * @return a new Point2D
     */
    public Point2D sub(final Point2D p)
    {
        // TODO
        return this;
    }

    /**
     * Returns a new point by diving the coordinates of this point by 'd',
     * i.e. (this.x / d, this.y / d)
     * <p>
     * This Point2D is not modified.
     *
     * @param d double
     * @return a new Point2D
     */
    public Point2D div(final double d) throws GeometryException
    {
        if (d == 0.0)
        {
            throw new GeometryException("can't divide by 0");
        }
        return mul(1.0 / d);
    }

    /**
     * Returns a new point by multiplying the coordinates of this point by 'd',
     * i.e. (this.x * d, this.y * d)
     * <p>
     * This Point2D is not modified.
     *
     * @param d double
     * @return a new Point2D
     */
    public Point2D scale(final double d)
    {
        // TODO
        return this;
    }

    public Point2D mul(final double d)
    {
        // TODO
        return this;
    }

    /**
     * Returns a new Point2D perpendicular to this vector by rotating this Point2D
     * 90 degrees counterclockwise around (0,0)
     *
     * @return Point2D
    public Point2D perpendicular()
    {
        // TODO
        return this;

    }

    /**
     * Returns a new Point2D by rotating this Point2D counterclockwise
     * over the angle (in radians, not degrees!)
     * <p>
     * This Point2D is not modified.
     *
     * @return Point2D
     */
    public Point2D rotate(final double angle)
    {
        // TODO
        return this;
    }

    /**
     * Returns a new Point2D in the same direction as this Point2D
     * with a length of 1.
     *
     * @return Point2D
     */
    public Point2D unit() throws GeometryException
    {
        final double len = getLength();

        if (len == 0)
        {
            throw new GeometryException("can't normalize (0,0)");
        }
        return div(len);
    }

    /**
     * Returns whether this point is the Null vector (0,0)
     *
     * @return boolean
     */
    public boolean isNullVector()
    {
        // TODO
        return false;
    }

    /**
     * Returns the angle of the vector thru (0,0) and this Point2D,
     * and the positive x-axis. Returns 0 for the null vector (0,0).
     *
     * @return double
     */
    public double theta()
    {
        // TODO
        return 0;
    }

    public double thetaTo(final Point2D p)
    {
        // TODO
        return 0;
    }

    /**
     * Returns the underlying JavaScriptObject
     * @return Point2DJSO
     */
    public com.ait.lienzo.client.core.types.Point2D.Point2DJSO getJSO()
    {
        return null;
    }

    @Override
    public boolean equals(final Object other)
    {
        if ((other == null) || (false == (other instanceof Point2D)))
        {
            return false;
        }
        if (this == other)
        {
            return true;
        }
        final Point2D p = ((Point2D) other);

        return ((p.getX() == getX()) && (p.getY() == getY()));
    }

    public boolean closeEnough(final Point2D p)
    {
        return (Geometry.closeEnough(getX(), p.getX()) && Geometry.closeEnough(getY(), p.getY()));
    }

    public boolean closeEnough(final Point2D p, final double precision)
    {
        return (Geometry.closeEnough(getX(), p.getX(), precision) && Geometry.closeEnough(getY(), p.getY(), precision));
    }

    @Override
    public int hashCode()
    {
        return toJSONString().hashCode();
    }

    public double dot(final Point2D p)
    {
        // TODO
        return 0;
    }

    public double crossScalar(final Point2D p)
    {
        // TODO
        return 0;
    }

    /**
     * Returns whether the 3 points are colinear, i.e. whether they lie on a single straight line.
     *
     * @param p1
     * @param p2
     * @param p3
     * @return
     *
     * @see <a href="http://mathworld.wolfram.com/Collinear.html">Collinear in Wolfram MathWorld</a>
     */
    public static boolean collinear(final Point2D p1, final Point2D p2, final Point2D p3)
    {
        // TODO
        return false;
    }

    public boolean collinear(final Point2D p2, final Point2D p3)
    {
        return collinear(this, p2, p3);
    }

    /**
     * Returns the determinant of vectors P and Q. By definition:
     * <ul>
     *  <li>det(P, Q) = -det(Q, P)
     *  <li>det(P, -Q) = -det(P, Q)
     *  <li>if P == alpha * Q   &lt;=&gt;  det(P, Q) = 0 (P and Q on same line)
     *  <li>det(P, Origin) = 0
     *  <li>OQ parallel OP  &lt;=&gt;  det(P,Q) = 0
     * </ul>
     * Imagine standing on the Origin, facing point P:
     * <ul>
     *  <li>if Q is on your left, det(P,Q) &gt; 0
     *  <li>if Q is on your right, det(P,Q) &lt; 0
     *  <li>if Q is on the line thru P and the Origin, det(P,Q) = 0
     * </ul>
     *
     * @param p Point2D
     * @param q Point2D
     * @return the determinant of vectors P and Q
     */
    public static double det(final Point2D p, final Point2D q)
    {
        return (p.getX() * q.getY()) - (p.getY() * q.getX());
    }

    /**
     * Construct a Point2D from polar coordinates, i.e. a radius and an angle.
     *
     * @param radius
     * @param angle in radians
     * @return Point2D
     */
    public static Point2D polar(final double radius, final double angle)
    {
        return new Point2D(radius * Math.cos(angle), radius * Math.sin(angle));
    }
}
