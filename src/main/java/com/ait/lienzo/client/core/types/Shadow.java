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

import com.ait.lienzo.client.core.types.Point2D.Point2DJSO;
import com.ait.lienzo.shared.core.types.IColor;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.json.client.JSONObject;

/**
 * Wraps a Shadow JSO providing JS native access to color, blur and coordinates offset.
 */
public final class Shadow
{
    private final ShadowJSO m_jso;

    private static final String normalizeShadowColor(String color)
    {
        if ((null == color) || ((color = color.trim()).isEmpty()))
        {
            return "black";
        }
        return color;
    }

    private static final String normalizeShadowColor(IColor color)
    {
        if (null == color)
        {
            return "black";
        }
        return normalizeShadowColor(color.getColorString());
    }

    public Shadow(ShadowJSO jso)
    {
        m_jso = jso;
    }

    /**
     * Constructs a Shadow from a color (as a String), a blur and an offset (offx, offy).
     * 
     * @param color String
     * @param blur
     * @param offx
     * @param offy
     */
    public Shadow(String color, int blur, double offx, double offy)
    {
        this(ShadowJSO.make(normalizeShadowColor(color), blur, true, new Point2D(offx, offy).getJSO()));
    }

    /**
     * Constructs a Shadow from a color (i.e. {@link Color} or {@link ColorName}), 
     * a blur and an offset (offx, offy).
     * 
     * @param color {@link Color} or {@link ColorName}
     * @param blur
     * @param offx
     * @param offy
     */
    public Shadow(IColor color, int blur, double offx, double offy)
    {
        this(ShadowJSO.make(normalizeShadowColor(color), blur, true, new Point2D(offx, offy).getJSO()));
    }

    /**
     * Constructs a Shadow from a color (as a String), a blur and an offset (offx, offy).
     * 
     * @param color String
     * @param blur
     * @param offx
     * @param offy
     */
    public Shadow(String color, int blur, double offx, double offy, boolean onfill)
    {
        this(ShadowJSO.make(normalizeShadowColor(color), blur, onfill, new Point2D(offx, offy).getJSO()));
    }

    /**
     * Constructs a Shadow from a color (i.e. {@link Color} or {@link ColorName}), 
     * a blur and an offset (offx, offy).
     * 
     * @param color {@link Color} or {@link ColorName}
     * @param blur
     * @param offx
     * @param offy
     */
    public Shadow(IColor color, int blur, double offx, double offy, boolean onfill)
    {
        this(ShadowJSO.make(normalizeShadowColor(color), blur, onfill, new Point2D(offx, offy).getJSO()));
    }

    /**
     * Returns the color as a string.
     * @return String
     */
    public final String getColor()
    {
        return normalizeShadowColor(m_jso.getColor());
    }

    /**
     * Sets the color as a string.
     * 
     * @param color String
     * @return this Shadow
     */
    public final Shadow setColor(String color)
    {
        m_jso.setColor(normalizeShadowColor(color));

        return this;
    }

    /**
     * Sets the color as a {@link Color} or {@link ColorName}.
     * 
     * @param color {@link Color} or {@link ColorName}
     * @return this Shadow
     */
    public final Shadow setColor(IColor color)
    {
        m_jso.setColor(normalizeShadowColor(color));

        return this;
    }

    /**
     * Returns the blur.
     * @return String
     */
    public final int getBlur()
    {
        return m_jso.getBlur();
    }

    /**
     * Sets the blur.
     * 
     * @param blur int
     * @return this Shadow
     */
    public final Shadow setBlur(int blur)
    {
        m_jso.setBlur(blur);

        return this;
    }

    /**
     * Returns the offset as a Point2D.
     * @return Point2D
     */
    public final Point2D getOffset()
    {
        return new Point2D(m_jso.getOffset());
    }

    /**
     * Sets the color as a string.
     * 
     * @param offset Point2D
     * @return this Shadow
     */
    public final Shadow setOffset(Point2D offset)
    {
        m_jso.setOffset(offset.getJSO());

        return this;
    }

    /**
     * Returns the offset as a Point2D.
     * @return Point2D
     */
    public final boolean getOnFill()
    {
        return m_jso.getOnFill();
    }

    /**
     * Sets the color as a string.
     * 
     * @param offset Point2D
     * @return this Shadow
     */
    public final Shadow setOnFill(boolean onfill)
    {
        m_jso.setOnFill(onfill);

        return this;
    }

    public final ShadowJSO getJSO()
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
        if ((other == null) || (false == (other instanceof Shadow)))
        {
            return false;
        }
        if (this == other)
        {
            return true;
        }
        return ((Shadow) other).toJSONString().equals(toJSONString());
    }

    @Override
    public int hashCode()
    {
        return toJSONString().hashCode();
    }

    public static final class ShadowJSO extends JavaScriptObject
    {
        static final native ShadowJSO make(String color, int blur, boolean onfill, Point2DJSO offset)
        /*-{
        	return {
        		color : color,
        		blur : blur,
        		offset : offset,
        		onfill : onfill
        	};
        }-*/;

        protected ShadowJSO()
        {
        }

        public final native String getColor()
        /*-{
        	return this.color;
        }-*/;

        public final native void setColor(String color)
        /*-{
        	this.color = color;
        }-*/;

        public final native int getBlur()
        /*-{
        	return this.blur;
        }-*/;

        public final native void setBlur(int blur)
        /*-{
        	this.blur = blur;
        }-*/;

        public final native boolean getOnFill()
        /*-{
        	if (this.onfill != undefined) {
        		return this.onfill;
        	}
        	return true;
        }-*/;

        public final native void setOnFill(boolean onfill)
        /*-{
        	this.onfill = onfill;
        }-*/;

        public final native Point2DJSO getOffset()
        /*-{
        	return this.offset;
        }-*/;

        public final native void setOffset(Point2DJSO offset)
        /*-{
        	this.offset = offset;
        }-*/;
    }
}