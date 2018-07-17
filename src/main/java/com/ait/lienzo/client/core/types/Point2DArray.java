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

import com.ait.lienzo.client.core.types.Point2D.Point2DJSO;
import com.ait.tooling.nativetools.client.collection.NFastDoubleArrayJSO;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.json.client.JSONArray;

/**
 * Point2DArray represents an array (or List) with {@link Point2D} objects.
 */
public class Point2DArray implements IBoundedList<Point2D>
{
    private final Point2DArrayJSO m_jso;

    public static final Point2DArray fromArrayOfDouble(final double[] array)
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
            points.push(array[i], array[i + 1]);
        }
        return points;
    }

    public static final Point2DArray fromNFastDoubleArrayJSO(final NFastDoubleArrayJSO array)
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
            points.push(array.get(i), array.get(i + 1));
        }
        return points;
    }

    public Point2DArray(final Point2DArrayJSO jso)
    {
        m_jso = jso;
    }

    public Point2DArray(final JsArray<JavaScriptObject> jso)
    {
        m_jso = jso.cast();
    }

    public Point2DArray()
    {
        this(Point2DArrayJSO.make());
    }

    public Point2DArray(final double x, final double y)
    {
        this();

        push(x, y);
    }

    public Point2DArray(final Point2D point)
    {
        this();

        push(point);
    }

    public Point2DArray(final Point2D point, final Point2D... points)
    {
        this();

        push(point, points);
    }

    public Point2DArray(final Point2DArray points)
    {
        this(points.getJSO().copy());
    }

    public final BoundingBox getBoundingBox()
    {
        return new BoundingBox(this);
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
    public Point2DArray(final double[] x, final double[] y)
    {
        this();

        if ((null != x) && (null != y))
        {
            final int size = x.length;

            if (size != y.length)
            {
                throw new IllegalArgumentException("x and y array should have the same length");
            }
            for (int i = 0; i < size; i++)
            {
                push(x[i], y[i]);
            }
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
    public Point2DArray(final double[][] points)
    {
        this();

        if (null != points)
        {
            final int size = points.length;

            for (int i = 0; i < size; i++)
            {
                final double[] xy = points[i];

                if ((null == xy) || (xy.length != 2))
                {
                    throw new IllegalArgumentException("points[" + i + "] does not have length of 2");
                }
                push(xy[0], xy[1]);
            }
        }
    }

    public final Point2DArray push(final Point2D point)
    {
        m_jso.push(point.getJSO());

        return this;
    }

    public final Point2DArray push(final double x, final double y)
    {
        m_jso.push(Point2DJSO.make(x, y));

        return this;
    }

    public final Point2DArray push(final Point2D point, final Point2D... points)
    {
        m_jso.push(point.getJSO());

        if (points != null)
        {
            final int size = points.length;

            for (int i = 0; i < size; i++)
            {
                m_jso.push(points[i].getJSO());
            }
        }
        return this;
    }

    @Override
    public final int size()
    {
        return m_jso.length();
    }

    @Override
    public final boolean isEmpty()
    {
        return (size() == 0);
    }

    @Override
    public final Point2D get(final int i)
    {
        return new Point2D(m_jso.get(i));
    }

    public final Point2DArray set(final int i, final Point2D p)
    {
        m_jso.set(i, p.getJSO());

        return this;
    }

    public final Point2DArray set(final int i, final double x, final double y)
    {
        m_jso.set(i, Point2DJSO.make(x, y));

        return this;
    }

    public Point2DArray shift()
    {
        m_jso.shift();

        return this;
    }

    public Point2DArray unshift(final Point2D p)
    {
        m_jso.unshift(p.getJSO());

        return this;
    }

    public Point2DArray unshift(final double x, final double y)
    {
        m_jso.unshift(Point2DJSO.make(x, y));

        return this;
    }

    public Point2DArray pop()
    {
        m_jso.pop();

        return this;
    }

    public final Point2DArray noAdjacentPoints()
    {
        return new Point2DArray(m_jso.noAdjacentPoints());
    }

    public final Point2DArray copy()
    {
        return new Point2DArray(m_jso.copy());
    }

    public final Collection<Point2D> getPoints()
    {
        final int size = size();

        final ArrayList<Point2D> list = new ArrayList<Point2D>(size);

        for (int i = 0; i < size; i++)
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
    public boolean equals(final Object other)
    {
        if ((other == null) || (false == (other instanceof Point2DArray)))
        {
            return false;
        }
        if (this == other)
        {
            return true;
        }
        Point2DArray that = ((Point2DArray) other);

        final int size = size();

        if (that.size() != size)
        {
            return false;
        }
        for (int i = 0; i < size; i++)
        {
            if (false == get(i).equals(that.get(i)))
            {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode()
    {
        return toJSONString().hashCode();
    }

    @Override
    public Iterator<Point2D> iterator()
    {
        return new BoundedListIterator<Point2D>(this);
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

        public final native Point2DArrayJSO noAdjacentPoints()
        /*-{
			var no = [];
			var sz = this.length;
			if (sz < 1) {
				return no;
			}
			var p1 = this[0];
			no.push({
				x : p1.x,
				y : p1.y
			});
			if (sz < 2) {
				return no;
			}
			for (var i = 1; i < sz; i++) {
				var p2 = this[i];
				if (!((p1.x == p2.x) && (p1.y == p2.y))) {
					no.push({
						x : p2.x,
						y : p2.y
					});
				}
				p1 = p2;
			}
			return no;
        }-*/;

        public final native Point2DArrayJSO copy()
        /*-{
			var no = [];
			var sz = this.length;
			if (sz < 1) {
				return no;
			}
			for (var i = 0; i < sz; i++) {
				var p = this[i];
				no.push({
					x : p.x,
					y : p.y
				});
			}
			return no;
        }-*/;
    }
}
