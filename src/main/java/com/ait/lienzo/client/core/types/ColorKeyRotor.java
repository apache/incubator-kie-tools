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

import java.io.Serializable;

import com.ait.lienzo.shared.core.types.Color;

public final class ColorKeyRotor implements Serializable
{
    private static final long serialVersionUID    = 3354900887588406643L;

    public static final int   COLOR_SPACE_MAXIMUM = PhosphorRotor.COLOR_SPACE_MAXIMUM;

    private int               m_r_color           = 0;

    private int               m_g_color           = 0;

    private int               m_b_color           = 0;

    private PhosphorRotor     m_r_rotor           = new PhosphorRotor();

    private PhosphorRotor     m_g_rotor           = new PhosphorRotor();

    private PhosphorRotor     m_b_rotor           = new PhosphorRotor();

    private PhosphorRotor     m_c_rotor           = m_r_rotor;

    public ColorKeyRotor()
    {
    }

    public String next()
    {
        if (m_c_rotor == m_r_rotor)
        {
            int color = m_r_rotor.next();

            if (PhosphorRotor.COLOR_ROTOR_BOUNDRY != color)
            {
                m_r_color = color;

                return Color.rgbToBrowserHexColor(m_r_color, m_g_color, m_b_color);
            }
            m_c_rotor = m_g_rotor;
        }
        if (m_c_rotor == m_g_rotor)
        {
            int color = m_g_rotor.next();

            if (PhosphorRotor.COLOR_ROTOR_BOUNDRY != color)
            {
                m_g_color = color;

                return Color.rgbToBrowserHexColor(m_r_color, m_g_color, m_b_color);
            }
            m_c_rotor = m_b_rotor;
        }
        if (m_c_rotor == m_b_rotor)
        {
            int color = m_b_rotor.next();

            if (PhosphorRotor.COLOR_ROTOR_BOUNDRY != color)
            {
                m_b_color = color;

                return Color.rgbToBrowserHexColor(m_r_color, m_g_color, m_b_color);
            }
            m_c_rotor = m_r_rotor;
        }
        return next();
    }

    private static final class PhosphorRotor implements Serializable
    {
        private static final long serialVersionUID    = 3854157795554756689L;

        public static final int   COLOR_ROTOR_BOUNDRY = 256;

        public static final int   COLOR_SPACE_MAXIMUM = COLOR_ROTOR_BOUNDRY * COLOR_ROTOR_BOUNDRY * COLOR_ROTOR_BOUNDRY;

        int                       m_n                 = 0;

        int                       m_c                 = 0;

        public PhosphorRotor()
        {
        }

        public int next()
        {
            if (m_n < COLOR_ROTOR_BOUNDRY)
            {
                m_n = m_n + 1;

                m_c = m_c + 16;

                if (m_c >= COLOR_ROTOR_BOUNDRY)
                {
                    m_c = (m_c - COLOR_ROTOR_BOUNDRY) + 1;

                    return COLOR_ROTOR_BOUNDRY;
                }
            }
            else
            {
                m_n = 0;

                m_c = 0;
            }
            return m_c;
        }
    }
}
