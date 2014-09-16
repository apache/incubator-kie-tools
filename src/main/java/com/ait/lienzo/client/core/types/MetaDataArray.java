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
import com.google.gwt.json.client.JSONArray;

public class MetaDataArray
{
    private final MetaDataArrayJSO m_jso;

    public MetaDataArray()
    {
        this(MetaDataArrayJSO.make());
    }

    public MetaDataArray(MetaDataArrayJSO jso)
    {
        if (null != jso)
        {
            m_jso = jso;
        }
        else
        {
            m_jso = MetaDataArrayJSO.make();
        }
    }

    public final int size()
    {
        return m_jso.length();
    }

    public final MetaDataArray add(String value)
    {
        m_jso.push(value);

        return this;
    }

    public final MetaDataArray add(int value)
    {
        m_jso.push(value);

        return this;
    }

    public final MetaDataArray add(double value)
    {
        m_jso.push(value);

        return this;
    }

    public final MetaDataArray add(boolean value)
    {
        m_jso.push(value);

        return this;
    }

    public final MetaDataArray add(MetaData value)
    {
        m_jso.push(value.getJSO());

        return this;
    }

    public final MetaDataArray add(MetaDataArray value)
    {
        m_jso.push(value.getJSO());

        return this;
    }

    public final MetaDataArray set(int index, String value)
    {
        m_jso.set(index, value);

        return this;
    }

    public final MetaDataArray set(int index, int value)
    {
        m_jso.set(index, value);

        return this;
    }

    public final MetaDataArray set(int index, double value)
    {
        m_jso.set(index, value);

        return this;
    }

    public final MetaDataArray set(int index, boolean value)
    {
        m_jso.set(index, value);

        return this;
    }

    public final MetaDataArray set(int index, MetaData value)
    {
        m_jso.set(index, value.getJSO());

        return this;
    }

    public final MetaDataArray set(int index, MetaDataArray value)
    {
        m_jso.set(index, value.getJSO());

        return this;
    }

    public final int getInteger(int index)
    {
        if (m_jso.typeOf(index) == NativeInternalType.NUMBER)
        {
            return m_jso.getInteger(index);
        }
        return 0;
    }

    public final double getDouble(int index)
    {
        if (m_jso.typeOf(index) == NativeInternalType.NUMBER)
        {
            return m_jso.getNumber(index);
        }
        return 0;
    }

    public final String getString(int index)
    {
        if (m_jso.typeOf(index) == NativeInternalType.STRING)
        {
            return m_jso.getString(index);
        }
        return null;
    }

    public final boolean getBoolean(int index)
    {
        if (m_jso.typeOf(index) == NativeInternalType.BOOLEAN)
        {
            return m_jso.getBoolean(index);
        }
        return false;
    }

    public final MetaData getMetaData(int index)
    {
        if (m_jso.typeOf(index) == NativeInternalType.OBJECT)
        {
            NFastStringMapMixedJSO jso = m_jso.getObject(index).cast();

            return new MetaData(jso);
        }
        return null;
    }

    public final MetaDataArray getMetaDataArray(int index)
    {
        if (m_jso.typeOf(index) == NativeInternalType.ARRAY)
        {
            MetaDataArrayJSO jso = m_jso.getArray(index).cast();

            return new MetaDataArray(jso);
        }
        return null;
    }

    public final NativeInternalType typeOf(int index)
    {
        return m_jso.typeOf(index);
    }

    public final MetaDataArrayJSO getJSO()
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

    public final static class MetaDataArrayJSO extends JsArrayMixed
    {
        static final MetaDataArrayJSO make()
        {
            return JsArrayMixed.createArray().cast();
        }

        final native NativeInternalType typeOf(int index)
        /*-{
            if ((index >= 0) && (index < this.length) {

                var valu = this[index];

                var type = typeof valu;

                switch (type) {
                case 'string': {
                    return @com.ait.lienzo.client.core.types.NativeInternalType::STRING;
                }
                case 'boolean': {
                    return @com.ait.lienzo.client.core.types.NativeInternalType::BOOLEAN;
                }
                case 'number': {
                    if (isFinite(valu)) {
                        return @com.ait.lienzo.client.core.types.NativeInternalType::NUMBER;
                    }
                    return @com.ait.lienzo.client.core.types.NativeInternalType::UNDEFINED;
                }
                case 'object': {
                    if ((valu instanceof Array) || (valu instanceof $wnd.Array)) {
                        return @com.ait.lienzo.client.core.types.NativeInternalType::ARRAY;
                    }
                    return @com.ait.lienzo.client.core.types.NativeInternalType::OBJECT;
                }
                case 'function': {
                    return @com.ait.lienzo.client.core.types.NativeInternalType::FUNCTION;
                }
                }
            }
            return @com.ait.lienzo.client.core.types.NativeInternalType::UNDEFINED;
        }-*/;

        final native int getInteger(int index)
        /*-{
            return Math.round(this[index]);
        }-*/;

        final native JsArrayMixed getArray(int index)
        /*-{
            returnthis[index];
        }-*/;
    }
}
