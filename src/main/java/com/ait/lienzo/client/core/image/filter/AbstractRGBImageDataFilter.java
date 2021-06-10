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

package com.ait.lienzo.client.core.image.filter;

import com.ait.lienzo.client.core.Attribute;
import com.ait.lienzo.client.core.shape.json.validators.ValidationContext;
import com.ait.lienzo.client.core.shape.json.validators.ValidationException;
import com.ait.lienzo.shared.core.types.Color;
import com.ait.lienzo.shared.core.types.ImageFilterType;
import com.ait.lienzo.shared.core.types.IColor;
import com.ait.lienzo.tools.client.StringOps;

import jsinterop.annotations.JsProperty;

public abstract class AbstractRGBImageDataFilter<T extends AbstractRGBImageDataFilter<T>> extends AbstractImageDataFilter<T>
{
    @JsProperty
    private String color;

    private int m_r;

    private int m_g;

    private int m_b;

    public AbstractRGBImageDataFilter(final ImageFilterType type)
    {
        super(type);

        m_r = 0;

        m_g = 0;

        m_b = 0;

        doUpdateColorFromRGB();
    }

    public AbstractRGBImageDataFilter(final ImageFilterType type, final int r, final int g, final int b)
    {
        super(type);

        m_r = fixc(r);

        m_g = fixc(g);

        m_b = fixc(b);

        doUpdateColorFromRGB();
    }

    protected AbstractRGBImageDataFilter(final ImageFilterType type, final Object node, final ValidationContext ctx) throws ValidationException
    {
        super(type, node, ctx);

        doUpdateRGBFromColor();
    }

    public AbstractRGBImageDataFilter(final ImageFilterType type, final IColor color)
    {
        this(type, color.getR(), color.getG(), color.getB());
    }

    public AbstractRGBImageDataFilter(final ImageFilterType type, final String color)
    {
        this(type, Color.fromColorString(color));
    }

    public final int getR()
    {
        return m_r;
    }

    public final int getG()
    {
        return m_g;
    }

    public final int getB()
    {
        return m_b;
    }

    public final T setR(final int r)
    {
        m_r = fixc(r);

        doUpdateColorFromRGB();

        return cast();
    }

    public final T setG(final int g)
    {
        m_g = fixc(g);

        doUpdateColorFromRGB();

        return cast();
    }

    public final T setB(final int b)
    {
        m_b = fixc(b);

        doUpdateColorFromRGB();

        return cast();
    }

    private final void doUpdateColorFromRGB()
    {
        this.color = new Color(getR(), getG(), getB()).getColorString();
    }

    private final void doUpdateRGBFromColor()
    {
        final String cstr = StringOps.toTrimOrNull(this.color);

        if (null == cstr)
        {
            m_r = 0;

            m_g = 0;

            m_b = 0;
        }
        else
        {
            final Color colr = Color.fromColorString(cstr);

            m_r = colr.getR();

            m_g = colr.getG();

            m_b = colr.getB();
        }
    }

    private final int fixc(final int color)
    {
        if (color < 0)
        {
            return 0;
        }
        else if (color > 255)
        {
            return 255;
        }
        return color;
    }

    protected abstract static class RGBImageDataFilterFactory<T extends AbstractRGBImageDataFilter<T>> extends ImageDataFilterFactory<T>
    {
        protected RGBImageDataFilterFactory(final ImageFilterType type)
        {
            super(type);

            addAttribute(Attribute.COLOR, true);
        }
    }
}
