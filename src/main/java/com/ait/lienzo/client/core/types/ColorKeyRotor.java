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

public final class ColorKeyRotor
{
    public static final int COLOR_SPACE_MAXIMUM = 256 * 256 * 256;

    private int             m_r_color           = 0;

    private int             m_g_color           = 0;

    private int             m_b_color           = 0;

    public ColorKeyRotor()
    {
    }

    public String next()
    {
        m_r_color += 16;

        if (m_r_color >= 256)
        {
            m_r_color = m_r_color - 255;

            m_g_color += 16;

            if (m_g_color >= 256)
            {
                m_g_color = m_g_color - 255;

                m_b_color += 16;

                if (m_b_color >= 256)
                {
                    m_b_color = m_b_color - 255;

                    return next();
                }
            }
        }
        return Color.rgbToBrowserHexColor(m_r_color, m_g_color, m_b_color);
    }
}
