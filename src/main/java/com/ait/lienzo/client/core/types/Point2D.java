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

package com.ait.lienzo.client.core.types;

import java.util.Objects;

import com.ait.lienzo.client.core.util.GeometryException;

import elemental2.core.Global;
import jsinterop.annotations.JsProperty;

/**
 * Point2D can be used to represent a point or vector in 2D.
 * 
 * <p>
 * Some of the methods related to linear algebra come in two flavors, e.g. plus() and plusInSitu().
 * The first one does not modify the object that it is invoked on, but the second one does.
 * The second one is a little faster as it does not need to create a new object.
 * Think of them as the "+" and the "+=" operators respectively.
 */
public final class Point2D
{
//    private Point2DJSO m_jso;

    @JsProperty
    private double x;

    @JsProperty
    private double y;

    /**
     * Constructs a Point2D at (x,y)
     * 
     * @param x double
     * @param y double
     */
    public Point2D(final double x, final double y)
    {
        this.x = x;
        this.y = y;
    }

    public final Point2D copy()
    {
        return new Point2D(x, y);
    }

    /**
     * Returns the x coordinate
     * @return double
     */
    public final double getX()
    {
        return this.x;
    }

    /**
     * Sets the x coordinate
     * @param x double
     * @return this Point2D
     */
    public final Point2D setX(final double x)
    {
        this.x = x;

        return this;
    }

    /**
     * Returns the y coordinate
     * @return double
     */
    public final double getY()
    {
        return this.y;
    }

    /**
     * Sets the y coordinate
     * @param y double
     * @return this Point2D
     */
    public final Point2D setY(final double y)
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
    public final Point2D set(final Point2D p)
    {
        x = p.getX();
        y = p.getY();

        return this;
    }

    /**
     * Returns the length of the vector from (0,0) to this Point2D.
     * 
     * @return double
     */
    public final double getLength()
    {
        double dx = this.x;

        double dy = this.y;

        return Math.sqrt((dx * dx) + (dy * dy));
    }

    /**
     * Returns the distance from this Point2D to the other Point2D.
     * 
     * @param other Point2D
     * @return double
     */
    public final double distance(final Point2D other)
    {
        double dx = other.x - this.x;

        double dy = other.y - this.y;

        return Math.sqrt((dx * dx) + (dy * dy));
    }


    /**
     * Returns a new point by adding the coordinates of this point and point P,
     * i.e. (this.x + p.x, this.y + p.y)
     * <p>
     * This Point2D is not modified.
     * 
     * @param p Point2D
     * @return a new Point2D
     */
    public final Point2D add(final Point2D p)
    {
        return new Point2D(this.x + p.x, this.y + p.y);
    }

    public final Point2D offset(final double x, final double y)
    {
        this.x += x;
        this.y += y;

        return this;
    }

    public final Point2D normalize(final double length)
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
    public final Point2D sub(final Point2D p)
    {
        return new Point2D(this.x - p.x, this.y - p.y);
    }

    public final Point2D subXY(double x, double y)
    {
        return new Point2D(this.x - x, this.y - y);
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
    public final Point2D div(final double d) throws GeometryException
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
    public final Point2D scale(final double d)
    {
        return new Point2D(this.x * d, this.y * d);
    }

    public final Point2D mul(final double d)
    {
        return scale(d);
    }

    /**
     * Returns a new Point2D perpendicular to this vector by rotating this Point2D 
     * 90 degrees counterclockwise around (0,0)
     * 
     * @return Point2D
     * @see http://mathworld.wolfram.com/PerpendicularVector.html
     */
    public final Point2D perpendicular()
    {
        return new Point2D(-this.y, this.x);
    }

    /**
     * Returns a new Point2D by rotating this Point2D counterclockwise 
     * over the angle (in radians, not degrees!)
     * <p>
     * This Point2D is not modified.
     * 
     * @param angleInRadians
     * 
     * @return Point2D
     */
    public final Point2D rotate(final double angle)
    {
        //return new Point2D(m_jso.rotate(angle));
        double s = Math.sin(angle);
        double c = Math.cos(angle);
        return new Point2D(c * x - s * y,
                           s * x + c * y);
    }

    /**
     * Returns a new Point2D in the same direction as this Point2D
     * with a length of 1.
     * 
     * @return Point2D
     */
    public final Point2D unit() throws GeometryException
    {
        double len = getLength();

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
    public final boolean isNullVector()
    {
        return ((this.x == 0) && (this.y == 0));
    }

    /**
     * Returns the angle of the vector thru (0,0) and this Point2D, 
     * and the positive x-axis. Returns 0 for the null vector (0,0).
     * 
     * @return double
     */
    public final double theta()
    {
        if ((this.x == 0) && (this.y == 0)) {
            return 0.0; // not sure if check is needed
        }
        double a = Math.atan2(this.y, this.x); // between [-PI,PI]

        return (a >= 0.0) ? a : (a + Math.PI * 2);
    }

    public final double thetaTo(final Point2D p)
    {
        if ((this.x == p.x) && (this.y == p.y)) {
            return 0.0;
        }
        double a = Math.atan2(p.y, p.x) - Math.atan2(this.y, this.x);

        return (a >= 0.0) ? a : (a + Math.PI * 2);
    }

    public final String toJSONString()
    {
        return Global.JSON.stringify(this);
    }

    @Override
    public boolean equals(final Object other)
    {
        if ((other == null) || (!(other instanceof Point2D)))
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

//    public boolean closeEnough(final Point2D p)
//    {
//        return (Geometry.closeEnough(getX(), p.getX()) && Geometry.closeEnough(getY(), p.getY()));
//    }
//
//    public boolean closeEnough(final Point2D p, final double precision)
//    {
//        return (Geometry.closeEnough(getX(), p.getX(), precision) && Geometry.closeEnough(getY(), p.getY(), precision));
//    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    public final double dot(final Point2D p)
    {
        return this.x * p.x + this.y * p.y;
    }

    public final double crossScalar(final Point2D p)
    {
        return this.x * p.y - this.y * p.x;
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
    public static final double det(final Point2D p, final Point2D q)
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
    public static final Point2D polar(final double radius, final double angle)
    {
        return new Point2D(radius * Math.cos(angle), radius * Math.sin(angle));
    }

    @Override
    public String toString() {
        return "Point2D{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }

//    @JsType()
//    public static final class Point2DJSO
//    {
//        private double x;
//        private double y;
//
//        public Point2DJSO(final double x, final double y)
//        {
//            this.x = x;
//            this.y = y;
//        }
//
//        public static Point2DJSO makeFromValues(final double x, final double y)
//        {
//            return new Point2DJSO(x,y);
//        }
//
//        public static double distance(Point2DJSO a, Point2DJSO b)
//        {
//			double dx = b.x - a.x;
//
//			double dy = b.y - a.y;
//
//			return Math.sqrt((dx * dx) + (dy * dy));
//        };
//
//        public static double length(Point2DJSO a)
//        {
//			double dx = a.x;
//
//            double dy = a.y;
//
//			return Math.sqrt((dx * dx) + (dy * dy));
//        };
//
//        public double getX()
//        {
//			return this.x;
//        };
//
//        public void setX(double x)
//        {
//			this.x = x;
//        };
//
//        public double getY()
//        {
//			return this.y;
//        };
//
//        public void setY(double y)
//        {
//			this.y = y;
//        };
//
//
//        public void set(double x, double y)
//        {
//			this.x = x;
//			this.y = y;
//        };
//
//        public Point2DJSO copy()
//        {
//			return new Point2DJSO(x,y);
//        };
//
//        public double distance(final Point2DJSO other)
//        {
//            return distance(this, other);
//        }
//
//        public double getLength()
//        {
//            return length(this);
//        }
//
//        public Point2DJSO addBoundingBox(Point2DJSO jso)
//        {
//			return new Point2DJSO(this.x + jso.x, this.y + jso.y);
//        };
//
//        public void offset(double x, double y)
//        {
//			this.x += x;
//			this.y += y;
//        };
//
//        public Point2DJSO subXY(double x, double y)
//        {
//            return new Point2DJSO(this.x - x, this.y - y);
//        };
//
//        public Point2DJSO sub(Point2DJSO jso)
//        {
//            return new Point2DJSO(this.x - jso.x, this.y - jso.y);
//        };
//
//        public Point2DJSO scaleWithXY(double d)
//        {
//            return new Point2DJSO(this.x * d, this.y * d);
//        };
//
//        public Point2DJSO perpendicular()
//        {
//            return new Point2DJSO(-this.y, this.x);
//        };
//
//        public Point2DJSO rotate(double angle)
//        {
//			double s = Math.sin(angle);
//            double c = Math.cos(angle);
//            return new Point2DJSO(c * x - s * y,
//                                  s * x + c * y);
//
//        };
//
//        public double dot(Point2DJSO p)
//        {
//			return this.x * p.x + this.y * p.y;
//        };
//
//        public final double crossScalar(Point2DJSO p)
//        {
//			return this.x * p.y - this.y * p.x;
//        };
//
//        public final boolean isNullVector()
//        {
//			return ((this.x == 0) && (this.y == 0));
//        };
//
//        public final double theta()
//        {
//			if ((this.x == 0) && (this.y == 0)) {
//				return 0.0; // not sure if check is needed
//			}
//			double a = Math.atan2(this.y, this.x); // between [-PI,PI]
//
//			return (a >= 0.0) ? a : (a + Math.PI * 2);
//        };
//
//        public final double thetaTo(Point2DJSO p)
//        {
//			if ((this.x == p.x) && (this.y == p.y)) {
//				return 0.0;
//			}
//			double a = Math.atan2(p.y, p.x) - Math.atan2(this.y, this.x);
//
//			return (a >= 0.0) ? a : (a + Math.PI * 2);
//        };
//    }
}
