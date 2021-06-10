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

import elemental2.core.Global;

/**
 * A native implementation of an array wrapped by a {@link DashArrayJSO}
 * This class is used in {@link Line} to create Dashed Lines.
 * 
 */
public final class DashArray
{
    private double[] m_jso;

    public DashArray(final double... jso)
    {
        m_jso = jso;
    }

    public DashArray()
    {
        m_jso = new double[0];
    }

//    public final DashArray push(double dash)
//    {
//        if (dash < 0)
//        {
//            dash = Math.abs(dash);
//        }
//        m_jso.push(dash);
//
//        return this;
//    }
//
//    public final DashArray push(final double dash, final double... dashes)
//    {
//        push(dash);
//
//        if (dashes != null)
//        {
//            for (int i = 0; i < dashes.length; i++)
//            {
//                push(dashes[i]);
//            }
//        }
//        return this;
//    }

    public final double[] getNormalizedArray()
    {
        final int leng = Math.abs(m_jso.length);

        if (leng < 1)
        {
            return new double[0];
        }
        if ((leng % 2) == 1)
        {
            final double[] dashes = new double[leng * 2];

            for (int i = 0; i < leng; i++)
            {
                dashes[i] = dashes[i + leng] = m_jso[i];
            }
            return dashes;
        }
        return m_jso;
    }

    public final int size()
    {
        return m_jso.length;
    }

    public final double[] getJSO()
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
        if ((other == null) || (!(other instanceof DashArray)))
        {
            return false;
        }
        if (this == other)
        {
            return true;
        }
        final DashArray that = ((DashArray) other);

        final int leng = size();

        if (that.size() != leng)
        {
            return false;
        }
        final double[] o_jso = that.getJSO();

        for (int i = 0; i < leng; i++)
        {
            if (o_jso[i] != m_jso[i])
            {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode()
    {
        return toJSONString().hashCode();
    }
}
