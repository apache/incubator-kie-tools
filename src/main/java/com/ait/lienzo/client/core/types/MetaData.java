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

import com.ait.lienzo.client.core.types.MetaDataArray.MetaDataArrayJSO;
import com.google.gwt.json.client.JSONObject;

public final class MetaData
{
    private final NFastStringMapMixedJSO m_jso;

    public MetaData()
    {
        m_jso = NFastStringMapMixedJSO.make();
    }

    public MetaData(NFastStringMapMixedJSO valu)
    {
        if (null != valu)
        {
            m_jso = valu.cast();
        }
        else
        {
            m_jso = NFastStringMapMixedJSO.make();
        }
    }

    public final NFastStringMapMixedJSO getJSO()
    {
        return m_jso;
    }

    public final MetaData put(String name, String value)
    {
        m_jso.put(name, value);

        return this;
    }

    public final MetaData put(String name, int value)
    {
        m_jso.put(name, value);

        return this;
    }

    public final MetaData put(String name, double value)
    {
        m_jso.put(name, value);

        return this;
    }

    public final MetaData put(String name, boolean value)
    {
        m_jso.put(name, value);

        return this;
    }

    public final MetaData put(String name, MetaData value)
    {
        if (null != value)
        {
            m_jso.put(name, value.getJSO());
        }
        else
        {
            m_jso.delete(name);
        }
        return this;
    }

    public final MetaData put(String name, MetaDataArray value)
    {
        if (null != value)
        {
            m_jso.put(name, value.getJSO());
        }
        else
        {
            m_jso.delete(name);
        }
        return this;
    }

    public final boolean isEmpty()
    {
        return m_jso.isEmpty();
    }

    public final int getInteger(String name)
    {
        if (m_jso.typeOf(name) == NativeInternalType.NUMBER)
        {
            return m_jso.getInteger(name);
        }
        return 0;
    }

    public final double getDouble(String name)
    {
        if (m_jso.typeOf(name) == NativeInternalType.NUMBER)
        {
            return m_jso.getDouble(name);
        }
        return 0;
    }

    public final String getString(String name)
    {
        if (m_jso.typeOf(name) == NativeInternalType.STRING)
        {
            return m_jso.getString(name);
        }
        return null;
    }

    public final boolean getBoolean(String name)
    {
        if (m_jso.typeOf(name) == NativeInternalType.BOOLEAN)
        {
            return m_jso.getBoolean(name);
        }
        return false;
    }

    public final MetaData getMetaData(String name)
    {
        if (m_jso.typeOf(name) == NativeInternalType.OBJECT)
        {
            NFastStringMapMixedJSO jso = m_jso.getObject(name).cast();

            return new MetaData(jso);
        }
        return null;
    }

    public final MetaDataArray getMetaDataArray(String name)
    {
        if (m_jso.typeOf(name) == NativeInternalType.ARRAY)
        {
            MetaDataArrayJSO jso = m_jso.getArray(name).cast();

            return new MetaDataArray(jso);
        }
        return null;
    }

    public final boolean isDefined(String name)
    {
        if (null == name)
        {
            return false;
        }
        return m_jso.isDefined(name);
    }

    public final MetaData delete(String name)
    {
        m_jso.delete(name);

        return this;
    }

    public final NativeInternalType typeOf(String name)
    {
        if (null != name)
        {
            return m_jso.typeOf(name);
        }
        return NativeInternalType.UNDEFINED;
    }

    public final String toJSONString()
    {
        return new JSONObject(m_jso).toString();
    }

    @Override
    public String toString()
    {
        return toJSONString();
    }

    @Override
    public boolean equals(Object other)
    {
        if ((other == null) || (false == (other instanceof MetaData)))
        {
            return false;
        }
        if (this == other)
        {
            return true;
        }
        return ((MetaData) other).toJSONString().equals(toJSONString());
    }

    @Override
    public int hashCode()
    {
        return toJSONString().hashCode();
    }
}
