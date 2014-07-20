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

package com.ait.lienzo.client.core.types;

import com.ait.lienzo.client.core.util.GeometryException;
import com.google.gwt.core.client.JavaScriptObject;

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
    private final Point2DJSO m_jso;

    public Point2D(Point2DJSO jso)
    {
        m_jso = jso;
    }

    /**
     * Constructs a Point2D at (0,0)
     */
    public Point2D()
    {
        this(Point2DJSO.make(0, 0));
    }

    /**
     * Constructs a Point2D at (x,y)
     * 
     * @param x double
     * @param y double
     */
    public Point2D(double x, double y)
    {
        this(Point2DJSO.make(x, y));
    }

    /**
     * Returns the x coordinate
     * @return double
     */
    public final double getX()
    {
        return m_jso.getX();
    }

    /**
     * Sets the x coordinate
     * @param x double
     * @return this Point2D
     */
    public final Point2D setX(double x)
    {
        m_jso.setX(x);

        return this;
    }

    /**
     * Returns the y coordinate
     * @return double
     */
    public final double getY()
    {
        return m_jso.getY();
    }

    /**
     * Sets the y coordinate
     * @param y double
     * @return this Point2D
     */
    public final Point2D setY(double y)
    {
        m_jso.setY(y);

        return this;
    }

    /**
     * Sets the x and y coordinates to those of point P.
     * 
     * @param p Point2D
     * @return this Point2D
     */
    public final Point2D set(Point2D p)
    {
        m_jso.setX(p.getX());

        m_jso.setY(p.getY());

        return this;
    }

    /**
     * Returns the length of the vector from (0,0) to this Point2D.
     * 
     * @return double
     */
    public final double getLength()
    {
        return getJSO().getLength();
    };

    /**
     * Returns the distance from this Point2D to the other Point2D.
     * 
     * @param other Point2D
     * @return double
     */
    public final double distance(Point2D other)
    {
        return getJSO().distance(other.getJSO());
    };

    /**
     * Returns the distance from point A to point B.
     * 
     * @param a Point2D
     * @param b Point2D
     * @return double
     */
    public static final double distance(Point2D a, Point2D b)
    {
        return Point2DJSO.distance(a.getJSO(), b.getJSO());
    };

    /**
     * Returns a new point by adding the coordinates of this point and point P,
     * i.e. (this.x + p.x, this.y + p.y)
     * <p>
     * This Point2D is not modified.
     * 
     * @param p Point2D
     * @return a new Point2D
     */
    public final Point2D plus(Point2D p)
    {
        return new Point2D(m_jso.plus(p.m_jso));
    }

    /**
     * Adds the coordinates of point P to this point,
     * i.e. this.x += p.x; this.y += p.y;
     * 
     * @param p Point2D
     * @return this Point2D
     */
    public final Point2D plusInSitu(Point2D p)
    {
        m_jso.plusInSitu(p.m_jso);

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
    public final Point2D minus(Point2D p)
    {
        return new Point2D(m_jso.minus(p.m_jso));
    }

    /**
     * Subtracts the coordinates of point P from this point,
     * i.e. this.x -= p.x; this.y -= p.y;
     * 
     * @param p Point2D
     * @return this Point2D
     */
    public final Point2D minusInSitu(Point2D p)
    {
        m_jso.minusInSitu(p.m_jso);

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
    public final Point2D div(double d) throws GeometryException
    {
        if (d == 0.0)
        {
            throw new GeometryException("can't divide by 0");
        }
        return times(1.0 / d);
    }

    /**
     * Divides the coordinates this Point2D by 'd',
     * i.e. this.x /= p.x; this.y /= p.y;
     * 
     * @param d double
     * @return this Point2D
     */
    public final Point2D divInSitu(double d) throws GeometryException
    {
        if (d == 0.0)
        {
            throw new GeometryException("can't divide by 0");
        }
        timesInSitu(d);

        return this;
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
    public final Point2D times(double d)
    {
        return new Point2D(m_jso.times(d));
    }

    /**
     * Multiplies the coordinates this Point2D by 'd',
     * i.e. this.x *= p.x; this.y *= p.y;
     * 
     * @param d double
     * @return this Point2D
     */
    public final Point2D timesInSitu(double d)
    {
        m_jso.timesInSitu(d);

        return this;
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
        return new Point2D(m_jso.perpendicular());
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
    public final Point2D rotate(double angleInRadians)
    {
        return new Point2D(m_jso.rotate(angleInRadians));
    }

    /**
     * Rotates this Point2D counterclockwise 
     * over the angle (in radians, not degrees!)
     * 
     * @param angleInRadians
     * 
     * @return this Point2D
     */
    public final Point2D rotateInSitu(double angleInRadians)
    {
        m_jso.rotateInSitu(angleInRadians);

        return this;
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
     * Sets the length of the vector from (0,0) to this Point2D to 1
     * while maintaining its direction.
     * 
     * @return this Point2D
     * @throws GeometryException
     */
    public final Point2D unitInSitu() throws GeometryException
    {
        double len = getLength();

        if (len == 0)
        {
            throw new GeometryException("can't normalize (0,0)");
        }
        return divInSitu(len);
    }

    /**
     * Returns whether this point is the Null vector (0,0)
     * 
     * @return boolean
     */
    public final boolean isNullVector()
    {
        return m_jso.isNullVector();
    }

    /**
     * Returns the angle of the vector thru (0,0) and this Point2D, 
     * and the positive x-axis. Returns 0 for the null vector (0,0).
     * 
     * @return double
     */
    public final double getAngle()
    {
        return m_jso.getAngle();
    }

    /**
     * Returns the underlying JavaScriptObject
     * @return Point2DJSO
     */
    public final Point2DJSO getJSO()
    {
        return m_jso;
    }

    /**
     * Returns a string representation for debugging purposes, 
     * e.g. "(1.1, 2.2)"
     * 
     * @return String
     */
    public String toString()
    {
        return "(" + getX() + ", " + getY() + ")";
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
    public static final boolean isColinear(Point2D p1, Point2D p2, Point2D p3)
    {
        return (p1.getX() * (p2.getY() - p3.getY()) + p2.getX() * (p3.getY() - p1.getY()) + p3.getX() * (p1.getY() - p2.getY())) == 0;
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
    public static final double det(Point2D p, Point2D q)
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
    public static final Point2D fromPolar(double radius, double angle)
    {
        return new Point2D(radius * Math.cos(angle), radius * Math.sin(angle));
    }

    public static class Point2DJSO extends JavaScriptObject
    {
        protected Point2DJSO()
        {

        }

        public static native Point2DJSO make(double xval, double yval)
        /*-{
			return {
				x : xval,
				y : yval
			};
        }-*/;

        public final native static double distance(Point2DJSO a, Point2DJSO b)
        /*-{
			var dx = b.x - a.x;

			var dy = b.y - a.y;

			return Math.sqrt((dx * dx) + (dy * dy));
        }-*/;

        public final native static double length(Point2DJSO a)
        /*-{
			var dx = a.x;

			var dy = a.y;

			return Math.sqrt((dx * dx) + (dy * dy));
        }-*/;

        public final native double getX()
        /*-{
			return this.x;
        }-*/;

        public final native void setX(double x)
        /*-{
			this.x = x;
        }-*/;

        public final native double getY()
        /*-{
			return this.y;
        }-*/;

        public final native void setY(double y)
        /*-{
			this.y = y;
        }-*/;

        public final double distance(Point2DJSO other)
        {
            return distance(this, other);
        };

        public final double getLength()
        {
            return length(this);
        };

        public final native Point2DJSO plus(Point2DJSO jso)
        /*-{
			return {
				x : this.x + jso.x,
				y : this.y + jso.y
			};
        }-*/;

        public final native void plusInSitu(Point2DJSO jso)
        /*-{
			this.x += jso.x;

			this.y += jso.y;
        }-*/;

        public final native Point2DJSO minus(Point2DJSO jso)
        /*-{
			return {
				x : this.x - jso.x,
				y : this.y - jso.y
			};
        }-*/;

        public final native void minusInSitu(Point2DJSO jso)
        /*-{
			this.x -= jso.x;

			this.y -= jso.y;
        }-*/;

        public final native Point2DJSO times(double d)
        /*-{
			return {
				x : this.x * d,
				y : this.y * d
			};
        }-*/;

        public final native void timesInSitu(double d)
        /*-{
			this.x *= d;

			this.y *= d;
        }-*/;

        public final native Point2DJSO perpendicular()
        /*-{
			return {
				x : -this.y,
				y : this.x
			};
        }-*/;

        public final native Point2DJSO rotate(double angleInRadians)
        /*-{
			var s = Math.sin(alpha);

			var c = Math.cos(alpha);

			return {
				x : c * this.x - s * this.y,
				y : s * this.x + c * this.y
			};
        }-*/;

        public final native void rotateInSitu(double angleInRadians)
        /*-{
			var s = Math.sin(alpha);

			var c = Math.cos(alpha);

			var x = c * this.x - s * this.y;

			this.y = s * this.x + c * this.y;

			this.x = x;
        }-*/;

        public final native boolean isNullVector()
        /*-{
			return ((this.x == 0) && (this.y == 0));
        }-*/;

        public final native double getAngle()
        /*-{
			if ((this.x == 0) && (this.y == 0)) {
				return 0.0; // not sure if check is needed
			}
			var a = Math.atan2(this.y, this.x); // between [-PI,PI]

			return (a >= 0.0) ? a : (a + Math.PI * 2);
        }-*/;
    }
}
