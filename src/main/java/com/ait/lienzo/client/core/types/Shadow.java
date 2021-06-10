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

import com.ait.lienzo.shared.core.types.IColor;

import elemental2.core.Global;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

/**
 * Wraps a Shadow JSO providing access to color, blur and coordinates offset.
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

    private static final String normalizeShadowColor(final IColor color)
    {
        if (null == color)
        {
            return "black";
        }
        return normalizeShadowColor(color.getColorString());
    }

    public Shadow(final ShadowJSO jso)
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
    public Shadow(final String color, final int blur, final double offx, final double offy)
    {
        this(ShadowJSO.make(normalizeShadowColor(color), blur, true, new Point2D(offx, offy)));
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
    public Shadow(final IColor color, final int blur, final double offx, final double offy)
    {
        this(ShadowJSO.make(normalizeShadowColor(color), blur, true, new Point2D(offx, offy)));
    }

    /**
     * Constructs a Shadow from a color (as a String), a blur and an offset (offx, offy).
     * 
     * @param color String
     * @param blur
     * @param offx
     * @param offy
     */
    public Shadow(final String color, final int blur, final double offx, final double offy, final boolean onfill)
    {
        this(ShadowJSO.make(normalizeShadowColor(color), blur, onfill, new Point2D(offx, offy)));
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
    public Shadow(final IColor color, final int blur, final double offx, final double offy, final boolean onfill)
    {
        this(ShadowJSO.make(normalizeShadowColor(color), blur, onfill, new Point2D(offx, offy)));
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
    public final Shadow setColor(final String color)
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
    public final Shadow setColor(final IColor color)
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
    public final Shadow setBlur(final int blur)
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
        return new Point2D(m_jso.getOffset().getX(), m_jso.getOffset().getY());
    }

    /**
     * Sets the offset with a Point2D
     * 
     * @param offset Point2D
     * @return this Shadow
     */
    public final Shadow setOffset(final Point2D offset)
    {
        m_jso.setOffset(offset);

        return this;
    }

    /**
     * Returns the boolean onFill
     * @return Point2D
     */
    public final boolean getOnFill()
    {
        return m_jso.getOnFill();
    }

    /**
     * Sets the boolean onFill
     * 
     * @param offset Point2D
     * @return this Shadow
     */
    public final Shadow setOnFill(final boolean onfill)
    {
        m_jso.setOnFill(onfill);

        return this;
    }

    public final Shadow copy()
    {
        return new Shadow(getJSO().copy());
    }

    public final ShadowJSO getJSO()
    {
        return m_jso;
    }

    public final String toJSONString()
    {
        return Global.JSON.stringify(m_jso);
    }

    @Override
    public String toString()
    {
        return toJSONString();
    }

    @Override
    public boolean equals(final Object other)
    {
        if ((other == null) || (!(other instanceof Shadow)))
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

    @JsType
    public static final class ShadowJSO
    {
        @JsProperty
        private String color;

        @JsProperty
        private int blur;

        @JsProperty
        private boolean onfill;

        @JsProperty
        private Point2D offset;

        static final ShadowJSO make(String color, int blur, boolean onfill, Point2D offset)
        {
            ShadowJSO jso = new ShadowJSO();
            jso.color = color;
            jso.blur = blur;
            jso.offset = offset;
            jso.onfill = onfill;

            return jso;
        }

        protected ShadowJSO()
        {
        }

        public final String getColor()
        {
			return this.color;
        }

        public final void setColor(String color)
        {
			this.color = color;
        }

        public final int getBlur()
        {
			return this.blur;
        }

        public final void setBlur(int blur)
        {
			this.blur = blur;
        }

        public final boolean getOnFill()
        {
			return this.onfill ;
        }

        public final void setOnFill(boolean onfill)
        {
			this.onfill = onfill;
        }

        public final Point2D getOffset()
        {
			return this.offset;
        }

        public final void setOffset(Point2D offset)
        {
			this.offset = offset;
        };

        public ShadowJSO copy() {
            return make(color, blur, onfill, offset);
        }
    }
}