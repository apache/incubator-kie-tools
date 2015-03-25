/*
   Copyright (c) 2014,2015 Ahome' Innovation Technologies. All rights reserved.

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

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;

public final class BoundingBox
{
    private final BoundingBoxJSO m_jso;

    public BoundingBox()
    {
        this(Double.MAX_VALUE, Double.MAX_VALUE, Double.MIN_VALUE, Double.MIN_VALUE);
    }

    public BoundingBox(BoundingBox bbox)
    {
        this();

        add(bbox);
    }

    public BoundingBox(double minx, double miny, double maxx, double maxy)
    {
        this(BoundingBoxJSO.make(minx, miny, maxx, maxy));
    }

    public BoundingBox(Point2D point, Point2D... points)
    {
        this();

        add(point, points);
    }

    public BoundingBox(Point2DArray points)
    {
        this();

        add(points);
    }

    public BoundingBox(BoundingBoxJSO jso)
    {
        m_jso = jso;
    }

    public final BoundingBox addX(double x)
    {
        m_jso.addX(x);

        return this;
    }

    public final BoundingBox addY(double y)
    {
        m_jso.addY(y);

        return this;
    }

    public final BoundingBox add(double x, double y)
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

    public final BoundingBox add(Point2D point, Point2D... points)
    {
        m_jso.addX(point.getX());

        m_jso.addY(point.getY());

        final int size = points.length;

        for (int i = 0; i < size; i++)
        {
            final Point2D p = points[i];

            m_jso.addX(p.getX());

            m_jso.addY(p.getY());
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

                m_jso.addX(p.getX());

                m_jso.addY(p.getY());
            }
        }
        return this;
    }

    public final BoundingBox add(Point2D point)
    {
        m_jso.addX(point.getX());

        m_jso.addY(point.getY());

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
        return m_jso.getMaxX() - m_jso.getMinX();
    }

    public final double getHeight()
    {
        return m_jso.getMaxY() - m_jso.getMinY();
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
    public String toString()
    {
        return toJSONString();
    }

    @Override
    public boolean equals(Object other)
    {
        if ((other == null) || (false == (other instanceof BoundingBox)))
        {
            return false;
        }
        if (this == other)
        {
            return true;
        }
        BoundingBox that = ((BoundingBox) other);

        return ((that.getX() == getX()) && (that.getY() == getY()) && (that.getWidth() == getWidth()) && (that.getHeight() == getHeight()));
    }

    @Override
    public int hashCode()
    {
        return toJSONString().hashCode();
    }

    public final static class BoundingBoxJSO extends JavaScriptObject
    {
        protected BoundingBoxJSO()
        {
        }

        final static native BoundingBoxJSO make(double minx, double miny, double maxx, double maxy)
        /*-{
            return {minx: minx, miny: miny, maxx: maxx, maxy: maxy};
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
    }
}
