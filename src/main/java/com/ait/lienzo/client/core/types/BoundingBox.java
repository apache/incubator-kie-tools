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

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;

public final class BoundingBox
{
    private final BoundingBoxJSO m_jso;

    public BoundingBox()
    {
        this(Double.MAX_VALUE, Double.MAX_VALUE, -Double.MAX_VALUE, -Double.MAX_VALUE);
    }

    public BoundingBox(final BoundingBox bbox)
    {
        this();

        add(bbox);
    }

    public BoundingBox(final double minx, final double miny, final double maxx, final double maxy)
    {
        this(BoundingBoxJSO.make(minx, miny, maxx, maxy));
    }

    public BoundingBox(final Point2D point, final Point2D... points)
    {
        this();

        add(point, points);
    }

    public BoundingBox(final Point2DArray points)
    {
        this();

        add(points);
    }

    public BoundingBox(final BoundingBoxJSO jso)
    {
        m_jso = Objects.requireNonNull(jso);
    }

    public final boolean isValid()
    {
        final double minx = m_jso.getMinX();

        final double maxx = m_jso.getMaxX();

        if ((maxx <= minx) || (maxx == -Double.MAX_VALUE) || (minx == Double.MAX_VALUE))
        {
            return false;
        }
        final double miny = m_jso.getMinY();

        final double maxy = m_jso.getMaxY();

        if ((maxy <= miny) || (maxy == -Double.MAX_VALUE) || (miny == Double.MAX_VALUE))
        {
            return false;
        }
        return true;
    }

    public final BoundingBox addX(final double x)
    {
        m_jso.addX(x);

        return this;
    }

    public final BoundingBox addY(final double y)
    {
        m_jso.addY(y);

        return this;
    }

    public final BoundingBox add(final double x, final double y)
    {
        m_jso.addX(x);

        m_jso.addY(y);

        return this;
    }

    public final BoundingBox add(final BoundingBox bbox)
    {
        if (null != bbox)
        {
            m_jso.addX(bbox.m_jso.getMinX());

            m_jso.addY(bbox.m_jso.getMinY());

            m_jso.addX(bbox.m_jso.getMaxX());

            m_jso.addY(bbox.m_jso.getMaxY());
        }
        return this;
    }

    public final BoundingBox add(final Point2D point, final Point2D... points)
    {
        if (null != point)
        {
            m_jso.addX(point.getX());

            m_jso.addY(point.getY());
        }
        if (null != points)
        {
            final int size = points.length;

            for (int i = 0; i < size; i++)
            {
                final Point2D p = points[i];

                if (null != p)
                {
                    m_jso.addX(p.getX());

                    m_jso.addY(p.getY());
                }
            }
        }
        return this;
    }

    public final BoundingBox add(final Point2DArray points)
    {
        if (null != points)
        {
            final int size = points.size();

            for (int i = 0; i < size; i++)
            {
                final Point2D p = points.get(i);

                if (null != p)
                {
                    m_jso.addX(p.getX());

                    m_jso.addY(p.getY());
                }
            }
        }
        return this;
    }

    public final BoundingBox add(final Point2D point)
    {
        if (null != point)
        {
            m_jso.addX(point.getX());

            m_jso.addY(point.getY());
        }
        return this;
    }

    public final double getX()
    {
        return m_jso.getMinX();
    }

    public final double getY()
    {
        return m_jso.getMinY();
    }

    public final double getWidth()
    {
        return Math.abs(m_jso.getMaxX() - m_jso.getMinX());
    }

    public final double getHeight()
    {
        return Math.abs(m_jso.getMaxY() - m_jso.getMinY());
    }

    public final double getMinX()
    {
        return m_jso.getMinX();
    }

    public final double getMaxX()
    {
        return m_jso.getMaxX();
    }

    public final double getMinY()
    {
        return m_jso.getMinY();
    }

    public final double getMaxY()
    {
        return m_jso.getMaxY();
    }

    public final boolean intersects(BoundingBox other)
    {
        if (getMaxX() < other.getMinX())
        {
            return false; // this is left of other
        }
        if (getMinX() > other.getMaxX())
        {
            return false; // this is right of other
        }
        if (getMaxY() < other.getMinY())
        {
            return false; // this is above other
        }
        if (getMinY() > other.getMaxY())
        {
            return false; // this is below other
        }
        return true; // boxes overlap
    }

    public final boolean contains(BoundingBox other)
    {
        if (getMinX() <= other.getMinX() && getMaxX() >= other.getMaxX() && getMinY() <= other.getMinY() && getMaxY() >= other.getMaxY())
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public final boolean contains(Point2D p)
    {
        return getMinX() <= p.getX() && getMaxX() >= p.getX() &&
               getMinY() <= p.getY() && getMaxY() >= p.getY();
    }

    public final BoundingBoxJSO getJSO()
    {
        return m_jso;
    }

    public final String toJSONString()
    {
        JSONObject object = new JSONObject();

        object.put("x", new JSONNumber(getX()));

        object.put("y", new JSONNumber(getY()));

        object.put("width", new JSONNumber(getWidth()));

        object.put("height", new JSONNumber(getHeight()));

        return object.toString();
    }

    @Override
    public final String toString()
    {
        return toJSONString();
    }

    @Override
    public final boolean equals(final Object other)
    {
        if ((other == null) || (false == (other instanceof BoundingBox)))
        {
            return false;
        }
        if (this == other)
        {
            return true;
        }
        final BoundingBox that = ((BoundingBox) other);

        return ((that.getX() == getX()) && (that.getY() == getY()) && (that.getWidth() == getWidth()) && (that.getHeight() == getHeight()));
    }

    @Override
    public final int hashCode()
    {
        return toJSONString().hashCode();
    }

    public void offset(int dx, int dy)
    {
        m_jso.offset(dx, dy);
    }

    public final static class BoundingBoxJSO extends JavaScriptObject
    {
        protected BoundingBoxJSO()
        {
        }

        final static native BoundingBoxJSO make(double minx, double miny, double maxx, double maxy)
        /*-{
			return {
				minx : minx,
				miny : miny,
				maxx : maxx,
				maxy : maxy
			};
        }-*/;

        final native double getMinX()
        /*-{
			return this.minx;
        }-*/;

        final native double getMinY()
        /*-{
			return this.miny;
        }-*/;

        final native double getMaxX()
        /*-{
			return this.maxx;
        }-*/;

        final native double getMaxY()
        /*-{
			return this.maxy;
        }-*/;

        final native void addX(double x)
        /*-{
			if (x < this.minx) {
				this.minx = x;
			}
			if (x > this.maxx) {
				this.maxx = x;
			}
        }-*/;

        final native void addY(double y)
        /*-{
			if (y < this.miny) {
				this.miny = y;
			}
			if (y > this.maxy) {
				this.maxy = y;
			}
        }-*/;
        final native void offset(double dx, double dy)
        /*-{
            this.minx = this.minx + dx;
            this.maxx = this.maxx + dx;
            this.miny = this.miny + dy;
            this.maxy = this.maxy + dy;
        }-*/;
    }
}
