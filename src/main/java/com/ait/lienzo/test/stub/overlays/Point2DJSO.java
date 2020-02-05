/*
 * Copyright (c) 2017 Ahome' Innovation Technologies. All rights reserved.
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

package com.ait.lienzo.test.stub.overlays;

import com.ait.lienzo.test.annotation.StubClass;
import com.google.gwt.core.client.JavaScriptObject;

/**
 * Stub for class <code>com.ait.lienzo.client.core.types.Point2D$Point2DJSO</code>.
 *
 * @author Roger Martinez
 * @since 1.0
 *
 */
@StubClass("com.ait.lienzo.client.core.types.Point2D$Point2DJSO")
public class Point2DJSO extends JavaScriptObject
{
    private double x;

    private double y;

    protected Point2DJSO()
    {
    }

    protected Point2DJSO(final double x, final double y)
    {
        this.x = x;
        this.y = y;
    }

    public static Point2DJSO make(final double xval, final double yval)
    {
        return new Point2DJSO(xval, yval);
    }

    public static double distance(final Point2DJSO a, final Point2DJSO b)
    {
        final double dx = b.x - a.x;

        final double dy = b.y - a.y;

        return Math.sqrt((dx * dx) + (dy * dy));
    }

    public static double length(final Point2DJSO a)
    {
        final double dx = a.x;

        final double dy = a.y;

        return Math.sqrt((dx * dx) + (dy * dy));
    }

    public double getX()
    {
        return x;
    }

    public void setX(final double x)
    {
        this.x = x;
    }

    public double getY()
    {
        return y;
    }

    public void setY(final double y)
    {
        this.y = y;
    }

    public void set(final Point2DJSO o)
    {
        this.x = o.getX();
        this.y = o.getY();
    }

    public void set(final double x, final double y)
    {
        this.x = x;
        this.y = y;
    }

    public Point2DJSO copy()
    {
        return new Point2DJSO(x, y);
    }

    public double distance(final Point2DJSO other)
    {
        return distance(this, other);
    }

    public double getLength()
    {
        return length(this);
    }

    public Point2DJSO add(final Point2DJSO jso)
    {
        this.x += jso.x;
        this.y += jso.y;
        return new Point2DJSO(this.x, this.y);
    }

    public void offset(final double x, final double y)
    {
        this.x += x;
        this.y += y;
    }

    public void offset(final Point2DJSO jso)
    {
        this.x += jso.x;
        this.y += jso.y;
    }

    public void minus(final double x, final double y)
    {
        this.x -= x;
        this.y -= y;
    }

    public void minus(final Point2DJSO jso)
    {
        this.x -= jso.x;
        this.y -= jso.y;
    }

    public Point2DJSO sub(final Point2DJSO jso)
    {
        return new Point2DJSO(this.x - jso.x, this.y - jso.y);
    }

    public Point2DJSO scale(final double d)
    {
        return new Point2DJSO(this.x * d, this.y * d);
    }

    public Point2DJSO perpendicular()
    {
        return new Point2DJSO(-this.y, this.x);
    }

    public Point2DJSO rotate(final double angle)
    {
        final double s = Math.sin(angle);
        final double c = Math.cos(angle);
        return new Point2DJSO((c * this.x) - (s * this.y), (s * this.x) + (c * this.y));
    }

    public double dot(final Point2DJSO p)
    {
        return (this.x * p.x) + (this.y * p.y);
    }

    public double crossScalar(final Point2DJSO p)
    {
        return (this.x * p.y) - (this.y * p.x);
    }

    public boolean isNullVector()
    {
        return ((this.x == 0) && (this.y == 0));
    }

    public double theta()
    {
        if ((this.x == 0) && (this.y == 0))
        {
            return 0.0;
        }
        final double a = Math.atan2(this.y, this.x);

        return (a >= 0.0) ? a : (a + (Math.PI * 2));
    }

    public double thetaTo(final Point2DJSO p)
    {
        if ((this.x == p.x) && (this.y == p.y))
        {
            return 0.0;
        }
        final double a = Math.atan2(p.y, p.x) - Math.atan2(this.y, this.x);

        return (a >= 0.0) ? a : (a + (Math.PI * 2));
    }
}