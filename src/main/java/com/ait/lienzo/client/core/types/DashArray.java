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

import com.google.gwt.json.client.JSONArray;

/**
 * A native implementation of an array wrapped by a {@link DashArrayJSO}
 * This class is used in {@link Line} to create Dashed Lines.
 * 
 */
public final class DashArray
{
    private final NFastDoubleArrayJSO m_jso;

    public DashArray(NFastDoubleArrayJSO jso)
    {
        m_jso = jso;
    }

    public DashArray()
    {
        this(NFastDoubleArrayJSO.make());
    }

    public DashArray(double dash, double... dashes)
    {
        this();

        push(dash, dashes);
    }

    public final DashArray push(double dash)
    {
        if (dash < 0)
        {
            dash = Math.abs(dash);
        }
        m_jso.push(dash);

        return this;
    }

    public final DashArray push(double dash, double... dashes)
    {
        push(dash);

        if (dashes != null)
        {
            for (int i = 0; i < dashes.length; i++)
            {
                push(dashes[i]);
            }
        }
        return this;
    }

    public final double[] getNormalizedArray()
    {
        final int leng = m_jso.size();

        if ((leng % 2) == 1)
        {
            double[] dashes = new double[leng * 2];

            for (int i = 0; i < leng; i++)
            {
                dashes[i] = dashes[i + leng] = m_jso.get(i);
            }
            return dashes;
        }
        return m_jso.toArray();
    }

    public final int size()
    {
        return m_jso.size();
    }

    public final NFastDoubleArrayJSO getJSO()
    {
        return m_jso;
    }

    public final String toJSONString()
    {
        return new JSONArray(m_jso).toString();
    }

    @Override
    public String toString()
    {
        return toJSONString();
    }

    @Override
    public boolean equals(Object other)
    {
        if ((other == null) || (false == (other instanceof DashArray)))
        {
            return false;
        }
        if (this == other)
        {
            return true;
        }
        DashArray that = ((DashArray) other);

        final int leng = size();

        if (that.size() != leng)
        {
            return false;
        }
        NFastDoubleArrayJSO o_jso = that.getJSO();

        for (int i = 0; i < leng; i++)
        {
            if (o_jso.get(i) != m_jso.get(i))
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
