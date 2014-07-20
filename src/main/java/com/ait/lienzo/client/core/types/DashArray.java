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

package com.ait.lienzo.client.core.types;

import com.google.gwt.core.client.JsArrayMixed;

/**
 * A native implementation of an array wrapped by a {@link DashArrayJSO}
 * This class is used in {@link Line} to create Dashed Lines.
 * 
 */
public final class DashArray
{
    private final DashArrayJSO m_jso;

    public DashArray(DashArrayJSO jso)
    {
        m_jso = jso;
    }

    public DashArray()
    {
        this(DashArrayJSO.makeDashArrayJSO());
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
        getJSO().push(dash);

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
        int leng = m_jso.length();

        if ((leng % 2) == 1)
        {
            double[] dashes = new double[leng * 2];

            for (int i = 0; i < leng; i++)
            {
                dashes[i] = dashes[i + leng] = m_jso.getNumber(i);
            }
            return dashes;
        }
        double[] dashes = new double[leng];

        for (int i = 0; i < leng; i++)
        {
            dashes[i] = m_jso.getNumber(i);
        }
        return dashes;
    }

    public final int getLength()
    {
        return getJSO().length();
    }

    public final DashArrayJSO getJSO()
    {
        return m_jso;
    }

    public static final class DashArrayJSO extends JsArrayMixed
    {
        protected DashArrayJSO()
        {

        }

        public static final native DashArrayJSO makeDashArrayJSO()
        /*-{
			return [];
        }-*/;
    }
}
