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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import com.ait.lienzo.tools.client.collection.NFastDoubleArray;

import elemental2.core.Global;
import elemental2.core.JsIterable;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import jsinterop.base.Js;
import jsinterop.base.JsArrayLike;

/**
 * Point2DArray represents an array (or List) with {@link Point2D} objects.
 */
@JsType(isNative = true, name = "Array", namespace = JsPackage.GLOBAL)
public class Point2DArray implements JsIterable<Point2D>, JsArrayLike<Point2D>
{
    @JsOverlay
    public static final Point2DArray fromArrayOfDouble(final double... array)
    {
        final Point2DArray points = new Point2DArray();

        if (null == array)
        {
            return points;
        }
        final int size = Math.abs(array.length);

        if (0 == size)
        {
            return points;
        }
        if ((size % 2) == 1)
        {
            throw new IllegalArgumentException("size of array is not a multiple of 2");
        }
        for (int i = 0; i < size; i += 2)
        {
            points.pushXY(array[i], array[i + 1]);
        }
        return points;
    }

    @JsOverlay
    public static final Point2DArray fromNFastDoubleArray(final NFastDoubleArray array)
    {
        final Point2DArray points = new Point2DArray();

        if (null == array)
        {
            return points;
        }
        final int size = Math.abs(array.size());

        if (size < 1)
        {
            return points;
        }
        if ((size % 2) == 1)
        {
            throw new IllegalArgumentException("size of array is not a multiple of 2");
        }
        for (int i = 0; i < size; i += 2)
        {
            points.pushXY(array.get(i), array.get(i + 1));
        }
        return points;
    }

    @JsOverlay
    public static final Point2DArray fromArrayOfPoint2D(final Point2D... inArray)
    {
        final Point2DArray points = new Point2DArray();

        if (null == inArray)
        {
            return points;
        }
        final int size = Math.abs(inArray.length);

        if (0 == size)
        {
            return points;
        }
        points.push(inArray);
        return points;
    }

//    public Point2DArray(final double x, final double y)
//    {
//        this();
//
//        pushXY(x, y);
//    }

//    public Point2DArray(final Point2D point)
//    {
//        this();
//
//        push(point);
//    }

//    public Point2DArray(final Point2D... points)
//    {
//        push(points);
//    }

    @JsOverlay
    public final BoundingBox getBoundingBox()
    {
        return BoundingBox.fromPoint2DArray(this);
    }

//    /**
//     * Creates a Point2DArray from an array of X coordinates and an array of correspondng
//     * Y coordinates. The arrays should be of the same length.
//     * <code>
//     * double[] x = {0, 5, 0, -5};
//     * double[] y = {10, -6, -2, -6};
//     * Point2DArray a = new Point2DArray(x, y);
//     * </code>
//     *
//     * @param x double[]
//     * @param y double[]
//     */
//    public Point2DArray(final double[] x, final double[] y)
//    {
//        this();
//
//        if ((null != x) && (null != y))
//        {
//            final int size = x.length;
//
//            if (size != y.length)
//            {
//                throw new IllegalArgumentException("x and y array should have the same length");
//            }
//            for (int i = 0; i < size; i++)
//            {
//                push(x[i], y[i]);
//            }
//        }
//    }
//
//    /**
//     * Creates a Point2DArray for an array with {x,y} pairs, e.g.
//     * <code>
//     * double[][] points = {{0, 10}, {5, -6}, {0, -2}, {-5, -6}};
//     * Point2DArray a = new Point2DArray(points);
//     * </code>
//     *
//     * @param points Array with double[] arrays of length 2
//     */
//    public Point2DArray(final double[][] points)
//    {
//        this();
//
//        if (null != points)
//        {
//            final int size = points.length;
//
//            for (int i = 0; i < size; i++)
//            {
//                final double[] xy = points[i];
//
//                if ((null == xy) || (xy.length != 2))
//                {
//                    throw new IllegalArgumentException("points[" + i + "] does not have length of 2");
//                }
//                push(xy[0], xy[1]);
//            }
//        }
//    }

    @JsOverlay
    public final Point2DArray pushXY(final double x, final double y)
    {
        push(new Point2D(x, y));

        return this;
    }

    public native int push(Point2D... var_args);

//    public final Point2DArray push(final Point2D point, final Point2D... points)
//    {
//        m_jso.push(point);
//
//        if (points != null)
//        {
//            final int size = points.length;
//
//            for (int i = 0; i < size; i++)
//            {
//                m_jso.push(points[i]);
//            }
//        }
//        return this;
//    }

    @JsOverlay
    public final int size()
    {
        return getLength();
    }

    @JsOverlay
    public final boolean isEmpty()
    {
        return (size() == 0);
    }

    @JsOverlay
    public final Point2D get(final int i)
    {
        return getAt(i);
    }

    @JsOverlay
    public final Point2DArray set(final int i, final Point2D p)
    {
        setAt(i, p);

        return this;
    }

    public native Point2DArray shift();

    public native Point2DArray unshift(final Point2D p);


    public native Point2DArray pop();

    @JsOverlay
    public final Point2DArray noAdjacentPoints()
    {
        Point2DArray no = new Point2DArray();

        int sz = getLength();
        if (sz < 1) {
            return no;
        }

        Point2D p1 = getAt(0);
        no.push(p1.copy());
        if (sz < 2) {
            return no;
        }


        for (int i = 1; i < sz; i++) {
            Point2D p2 = getAt(i);

            if (!((p1.getX() == p2.getX()) && (p1.getY() == p2.getY()))) {
                no.push(p2.copy());
            }
            p1 = p2;
        }

        return no;
    }

    @JsOverlay
    public final Point2DArray copy()
    {
        Point2DArray no = new Point2DArray();
        int sz = getLength();
        if (sz < 1) {
            return no;
        }
        for (int i = 0; i < sz; i++) {
            Point2D p = getAt(i);
            no.push(p.copy());
        }
        return no;
    }

    @JsOverlay
    public final Collection<Point2D> getPoints()
    {
        final int size = size();

        final ArrayList<Point2D> list = new ArrayList<>(size);

        for (int i = 0; i < size; i++)
        {
            list.add(get(i));
        }
        return Collections.unmodifiableCollection(list);
    }

    @JsOverlay
    public final String toJSONString()
    {
        return Global.JSON.stringify(this);
    }

//    @JsOverlay
//    public final String toString()
//    {
//        return toJSONString();
//    }
//
//    @JsOverlay
//    public final boolean equals(final Object other)
//    {
//        if ((other == null) || (false == (other instanceof Point2DArray)))
//        {
//            return false;
//        }
//        if (this == other)
//        {
//            return true;
//        }
//        Point2DArray that = ((Point2DArray) other);
//
//        final int size = size();
//
//        if (that.size() != size)
//        {
//            return false;
//        }
//        for (int i = 0; i < size; i++)
//        {
//            if (false == get(i).equals(that.get(i)))
//            {
//                return false;
//            }
//        }
//        return true;
//    }
//
//    @JsOverlay
//    public final int hashCode()
//    {
//        return toJSONString().hashCode();
//    }


    @JsOverlay
    public final Iterator<Point2D> iterator()
    {
        return asList().iterator();
    }

    @JsOverlay
    public final Point2D[] asArray()
    {
        return Js.<Point2D[]>uncheckedCast(this);
    }
}
