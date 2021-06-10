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

import com.ait.lienzo.shared.core.types.Color;

import elemental2.core.Uint8ClampedArray;
import elemental2.dom.ImageData;

/**
 * A simple Red-Blue-Green-Alpha color representation.
 */
public final class ImageDataPixelColor
{
    private final int m_r;

    private final int m_g;

    private final int m_b;

    private final int m_a;

    public ImageDataPixelColor(final ImageData source)
    {
        final Uint8ClampedArray data = source.data;

        m_r = (int) color(data, 0);

        m_g = (int) color(data, 1);

        m_b = (int) color(data, 2);

        m_a = (int) color(data, 3);
    }

    private final double color(Uint8ClampedArray data, int i)
    {
		return data.getAt(i);
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

    public final int getA()
    {
        return m_a;
    }

    public final String toBrowserRGB()
    {
        return Color.rgbToBrowserHexColor(m_r, m_g, m_b);
    }
}
