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
import com.google.gwt.json.client.JSONObject;

/**
 * Class to be used to bound the dragging area of a {@link Shape} 
 */
public final class DragBounds
{
    private final DragBoundsJSO m_jso;

    public DragBounds(DragBoundsJSO jso)
    {
        m_jso = jso;
    }

    public DragBounds()
    {
        this(DragBoundsJSO.make());
    }

    public DragBounds(double x1, double y1, double x2, double y2)
    {
        this(DragBoundsJSO.make());

        setX1(x1);

        setY1(y1);

        setX2(x2);

        setY2(y2);
    }

    /**
     * Sets the "x1" boundary coordinate for the Shape.
     * 
     * @param x1
     * @return
     */
    public final DragBounds setX1(double x1)
    {
        m_jso.setX1(x1);

        return this;
    }

    /**
     * Gets the "x1" boundary coordinate for the Shape.
     * 
     * @return
     */
    public final double getX1()
    {
        return m_jso.getX1();
    }

    /**
     * Sets the "x2" boundary coordinate for the Shape.
     * 
     * @param x2
     * @return
     */
    public final DragBounds setX2(double x2)
    {
        m_jso.setX2(x2);

        return this;
    }

    /**
     * Gets the "x2" boundary coordinate for the Shape.
     * 
     * @return
     */
    public final double getX2()
    {
        return m_jso.getX2();
    }

    /**
     * Sets the "y1" boundary coordinate for the Shape.
     * 
     * @param y1
     * @return
     */
    public final DragBounds setY1(double y1)
    {
        m_jso.setY1(y1);

        return this;
    }

    /**
     * Gets the "y1" boundary coordinate for the Shape.
     * 
     * @return
     */
    public final double getY1()
    {
        return m_jso.getY1();
    }

    /**
     * Sets the "y2" boundary coordinate for the Shape.
     * 
     * @param y2
     * @return
     */
    public final DragBounds setY2(double y2)
    {
        m_jso.setY2(y2);

        return this;
    }

    /**
     * Gets the "y1" boundary coordinate for the Shape.
     * 
     * @return
     */
    public final double getY2()
    {
        return m_jso.getY2();
    }

    /**
     * Returns true if the "x1" boundary coordinate for the Shape has been set.
     * 
     * @return
     */
    public final boolean isX1()
    {
        return m_jso.isX1();
    }

    /**
     * Returns true if the "x2" boundary coordinate for the Shape has been set.
     * 
     * @return
     */
    public final boolean isX2()
    {
        return m_jso.isX2();
    }

    /**
     * Returns true if the "y1" boundary coordinate for the Shape has been set.
     * 
     * @return
     */
    public final boolean isY1()
    {
        return m_jso.isY1();
    }

    /**
     * Returns true if the "y2" boundary coordinate for the Shape has been set.
     * 
     * @return
     */
    public final boolean isY2()
    {
        return m_jso.isY2();
    }

    public final DragBoundsJSO getJSO()
    {
        return m_jso;
    }

    public final String toJSONString()
    {
        return new JSONObject(m_jso).toString();
    }

    @Override
    public String toString()
    {
        return toJSONString();
    }

    @Override
    public boolean equals(Object other)
    {
        if ((other == null) || (false == (other instanceof DragBounds)))
        {
            return false;
        }
        if (this == other)
        {
            return true;
        }
        return ((DragBounds) other).toJSONString().equals(toJSONString());
    }

    @Override
    public int hashCode()
    {
        return toJSONString().hashCode();
    }

    public static final class DragBoundsJSO extends JavaScriptObject
    {
        static final DragBoundsJSO make()
        {
            return JavaScriptObject.createObject().cast();
        }

        protected DragBoundsJSO()
        {
        }

        final native boolean isX1()
        /*-{
        	if ((this.x1) && (this.x1 !== undefined)) {
        		return true;
        	}
        	return false;
        }-*/;

        final native boolean isX2()
        /*-{
        	if ((this.x2) && (this.x2 !== undefined)) {
        		return true;
        	}
        	return false;
        }-*/;

        final native boolean isY1()
        /*-{
        	if ((this.y1) && (this.y1 !== undefined)) {
        		return true;
        	}
        	return false;
        }-*/;

        final native boolean isY2()
        /*-{
        	if ((this.y2) && (this.y2 !== undefined)) {
        		return true;
        	}
        	return false;
        }-*/;

        final native void setX1(double x1)
        /*-{
        	this.x1 = x1;
        }-*/;

        final native double getX1()
        /*-{
        	return this.x1;
        }-*/;

        final native void setX2(double x2)
        /*-{
        	this.x2 = x2;
        }-*/;

        final native double getX2()
        /*-{
        	return this.x2;
        }-*/;

        final native void setY1(double y1)
        /*-{
        	this.y1 = y1;
        }-*/;

        final native double getY1()
        /*-{
        	return this.y1;
        }-*/;

        final native void setY2(double y2)
        /*-{
        	this.y2 = y2;
        }-*/;

        final native double getY2()
        /*-{
        	return this.y2;
        }-*/;
    }
}
