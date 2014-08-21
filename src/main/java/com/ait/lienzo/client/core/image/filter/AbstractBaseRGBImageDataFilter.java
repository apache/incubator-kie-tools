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

package com.ait.lienzo.client.core.image.filter;

import com.ait.lienzo.shared.core.types.Color;
import com.ait.lienzo.shared.core.types.IColor;

public abstract class AbstractBaseRGBImageDataFilter<T extends AbstractBaseRGBImageDataFilter<T>> extends AbstractBaseImageDataFilter<T>
{
    private int m_r;

    private int m_g;

    private int m_b;

    public AbstractBaseRGBImageDataFilter()
    {
        m_r = 0;

        m_g = 0;

        m_b = 0;
    }

    public AbstractBaseRGBImageDataFilter(int r, int g, int b)
    {
        m_r = fixc(r);

        m_g = fixc(g);

        m_b = fixc(b);
    }

    public AbstractBaseRGBImageDataFilter(IColor color)
    {
        this(color.getR(), color.getG(), color.getB());
    }

    public AbstractBaseRGBImageDataFilter(String color)
    {
        this(Color.fromColorString(color));
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

    public final T setR(int r)
    {
        m_r = fixc(r);

        return cast();
    }

    public final T setG(int g)
    {
        m_g = fixc(g);

        return cast();
    }

    public final T setB(int b)
    {
        m_b = fixc(b);

        return cast();
    }

    private final int fixc(int color)
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
}
