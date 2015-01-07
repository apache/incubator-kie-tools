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

import com.ait.lienzo.shared.core.types.Color;

/**
 * A simple Red-Blue-Green-Alpha color representation.
 */
public final class ImageDataPixelColor
{
    private final ImageData m_data;

    public ImageDataPixelColor(ImageData data)
    {
        m_data = data;
    }

    public final int getR()
    {
        return m_data.getRedAt(0, 0);
    }

    public final int getG()
    {
        return m_data.getGreenAt(0, 0);
    }

    public final int getB()
    {
        return m_data.getBlueAt(0, 0);
    }

    public final int getA()
    {
        return m_data.getAlphaAt(0, 0);
    }

    public final String toBrowserRGB()
    {
        return Color.rgbToBrowserHexColor(getR(), getG(), getB());
    }
}
