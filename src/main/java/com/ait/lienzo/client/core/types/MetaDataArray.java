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

public final class MetaDataArray
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
        return m_jso.size();
    }

    public final MetaDataArray push(String value)
    {
        m_jso.push(value);

        return this;
    }

    public final MetaDataArray push(int value)
    {
        m_jso.push(value);

        return this;
    }

    public final MetaDataArray push(double value)
    {
        m_jso.push(value);

        return this;
    }

    public final MetaDataArray push(boolean value)
    {
        m_jso.push(value);

        return this;
    }

    public final MetaDataArray push(MetaData value)
    {
        m_jso.push(value.getJSO());

        return this;
    }

    public final MetaDataArray push(MetaDataArray value)
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
        if (m_jso.getNativeTypeOf(index) == NativeInternalType.NUMBER)
        {
            return m_jso.getInteger(index);
        }
        return 0;
    }

    public final double getDouble(int index)
    {
        if (m_jso.getNativeTypeOf(index) == NativeInternalType.NUMBER)
        {
            return m_jso.getDouble(index);
        }
        return 0;
    }

    public final String getString(int index)
    {
        if (m_jso.getNativeTypeOf(index) == NativeInternalType.STRING)
        {
            return m_jso.getString(index);
        }
        return null;
    }

    public final boolean getBoolean(int index)
    {
        if (m_jso.getNativeTypeOf(index) == NativeInternalType.BOOLEAN)
        {
            return m_jso.getBoolean(index);
        }
        return false;
    }

    public final MetaData getMetaData(int index)
    {
        if (m_jso.getNativeTypeOf(index) == NativeInternalType.OBJECT)
        {
            NFastStringMapMixedJSO jso = m_jso.getObject(index).cast();

            return new MetaData(jso);
        }
        return null;
    }

    public final MetaDataArray getMetaDataArray(int index)
    {
        if (m_jso.getNativeTypeOf(index) == NativeInternalType.ARRAY)
        {
            MetaDataArrayJSO jso = m_jso.getArray(index).cast();

            return new MetaDataArray(jso);
        }
        return null;
    }

    public final boolean is(int index, NativeInternalType type)
    {
        return (type == getNativeTypeOf(index));
    }

    public final NativeInternalType getNativeTypeOf(int index)
    {
        return m_jso.getNativeTypeOf(index);
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

    @Override
    public boolean equals(Object other)
    {
        if ((other == null) || (false == (other instanceof MetaDataArray)))
        {
            return false;
        }
        if (this == other)
        {
            return true;
        }
        return ((MetaDataArray) other).toJSONString().equals(toJSONString());
    }

    @Override
    public int hashCode()
    {
        return toJSONString().hashCode();
    }

    public final static class MetaDataArrayJSO extends NBaseNativeArrayJSO<MetaDataArrayJSO>
    {
        public static final MetaDataArrayJSO make()
        {
            return NBaseNativeArrayJSO.make().cast();
        }

        protected MetaDataArrayJSO()
        {
        }

        final void push(String value)
        {
            if (null != value)
            {
                push0(value.substring(0));
            }
            else
            {
                push0(value);
            }
        }

        final native void push0(String value)
        /*-{
            this[this.length] = value;
        }-*/;

        final native void push(int value)
        /*-{
            this[this.length] = value;
        }-*/;

        final native void push(double value)
        /*-{
            this[this.length] = value;
        }-*/;

        final native void push(boolean value)
        /*-{
            this[this.length] = value;
        }-*/;

        final native void set(int index, int value)
        /*-{
            this[index] = value;
        }-*/;

        final native void set(int index, double value)
        /*-{
            this[index] = value;
        }-*/;

        final native void set(int index, boolean value)
        /*-{
            this[index] = value;
        }-*/;

        final void set(int index, String value)
        {
            if (null != value)
            {
                set0(index, value.substring(0));
            }
            else
            {
                set0(index, value);
            }
        };

        final native void set0(int index, String value)
        /*-{
            this[index] = value;
        }-*/;

        final native void set(int index, MetaDataArrayJSO value)
        /*-{
            this[index] = value;
        }-*/;

        final native void set(int index, NFastStringMapMixedJSO value)
        /*-{
            this[index] = value;
        }-*/;

        final native void push(MetaDataArrayJSO value)
        /*-{
            this[this.length] = value;
        }-*/;

        final native void push(NFastStringMapMixedJSO value)
        /*-{
            this[this.length] = value;
        }-*/;

        final native int getInteger(int index)
        /*-{
            return Math.round(this[index]);
        }-*/;

        final native MetaDataArrayJSO getArray(int index)
        /*-{
            returnthis[index];
        }-*/;

        final native double getDouble(int index)
        /*-{
            return this[index];
        }-*/;

        final native String getString(int index)
        /*-{
            return this[index];
        }-*/;

        final native boolean getBoolean(int index)
        /*-{
            return this[index];
        }-*/;

        final native NFastStringMapMixedJSO getObject(int index)
        /*-{
            return this[index];
        }-*/;

    }
}
