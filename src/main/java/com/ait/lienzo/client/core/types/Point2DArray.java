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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import com.ait.lienzo.client.core.types.Point2D.Point2DJSO;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.json.client.JSONArray;

/**
 * Point2DArray represents an array (or List) with {@link Point2D} objects.
 */
public class Point2DArray implements Iterable<Point2D>
{
    private final Point2DArrayJSO m_jso;

    Point2DArray(Point2DArrayJSO jso)
    {
        m_jso = jso;
    }

    public Point2DArray(JsArray<JavaScriptObject> jso)
    {
        m_jso = jso.cast();
    }

    public Point2DArray()
    {
        this(Point2DArrayJSO.make());
    }

    public Point2DArray(double x, double y)
    {
        this(Point2DArrayJSO.make());

        push(x, y);
    }

    public Point2DArray(Point2D point)
    {
        this(Point2DArrayJSO.make());

        push(point);
    }

    public Point2DArray(Point2D point, Point2D... points)
    {
        this(Point2DArrayJSO.make());

        push(point, points);
    }

    public final BoundingBox getBoundingBox()
    {
        double minx = 0;

        double miny = 0;

        double maxx = 0;

        double maxy = 0;

        final int size = size();

        if (size > 0)
        {
            Point2DJSO point = m_jso.get(0);

            minx = maxx = point.getX();

            miny = maxy = point.getY();

            for (int i = 1; i < size; i++)
            {
                point = m_jso.get(i);

                minx = Math.min(minx, point.getX());

                miny = Math.min(miny, point.getY());

                maxx = Math.max(maxx, point.getX());

                maxy = Math.max(maxy, point.getY());
            }
        }
        return new BoundingBox(minx, miny, maxx - minx, maxy - miny);
    }

    /**
     * Creates a Point2DArray from an array of X coordinates and an array of correspondng 
     * Y coordinates. The arrays should be of the same length.
     * <code>
     * double[] x = {0, 5, 0, -5};
     * double[] y = {10, -6, -2, -6};
     * Point2DArray a = new Point2DArray(x, y);
     * </code>
     * 
     * @param x double[]
     * @param y double[]
     */
    public Point2DArray(double[] x, double[] y)
    {
        this(Point2DArrayJSO.make());

        if (x.length != y.length)
        {
            throw new IllegalArgumentException("x and y array should have the same length");
        }
        for (int i = 0; i < x.length; i++)
        {
            push(x[i], y[i]);
        }
    }

    /**
     * Creates a Point2DArray for an array with {x,y} pairs, e.g.
     * <code>
     * double[][] points = {{0, 10}, {5, -6}, {0, -2}, {-5, -6}};
     * Point2DArray a = new Point2DArray(points);
     * </code>
     * 
     * @param points Array with double[] arrays of length 2
     */
    public Point2DArray(double[][] points)
    {
        this(Point2DArrayJSO.make());

        for (int i = 0; i < points.length; i++)
        {
            double[] xy = points[i];

            if (xy.length != 2)
            {
                throw new IllegalArgumentException("points[" + i + "] does not have length of 2");
            }
            push(xy[0], xy[1]);
        }
    }

    public final Point2DArray push(Point2D point)
    {
        m_jso.push(point.getJSO());

        return this;
    }

    public final Point2DArray push(double x, double y)
    {
        m_jso.push(Point2DJSO.make(x, y));

        return this;
    }

    public final Point2DArray push(Point2D point, Point2D... points)
    {
        m_jso.push(point.getJSO());

        if (points != null)
        {
            for (int i = 0; i < points.length; i++)
            {
                m_jso.push(points[i].getJSO());
            }
        }
        return this;
    }

    public final int size()
    {
        return m_jso.length();
    }

    public final Point2D get(int i)
    {
        return new Point2D(m_jso.get(i));
    }

    public final Point2DArray set(int i, Point2D p)
    {
        m_jso.set(i, p.getJSO());

        return this;
    }

    public Point2DArray shift()
    {
        m_jso.shift();

        return this;
    }

    public Point2DArray pop()
    {
        m_jso.pop();

        return this;
    }

    public final Collection<Point2D> getPoints()
    {
        int leng = size();

        ArrayList<Point2D> list = new ArrayList<Point2D>(leng);

        for (int i = 0; i < leng; i++)
        {
            list.add(get(i));
        }
        return Collections.unmodifiableCollection(list);
    }

    public final Point2DArrayJSO getJSO()
    {
        return m_jso;
    }

    public final String toJSONString()
    {
        return new JSONArray(m_jso).toString();
    }

    @Override
    public String toString()
    {
        return toJSONString();
    }

    @Override
    public Iterator<Point2D> iterator()
    {
        return getPoints().iterator();
    }

    public static final class Point2DArrayJSO extends JsArray<Point2DJSO>
    {
        protected Point2DArrayJSO()
        {
        }

        public final native void pop()
        /*-{
        	this.pop();
        }-*/;

        public static final Point2DArrayJSO make()
        {
            return JsArray.createArray().cast();
        }
    }
}
