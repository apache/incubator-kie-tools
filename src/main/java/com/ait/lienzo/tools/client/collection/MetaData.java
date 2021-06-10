/*
   Copyright (c) 2014,2015,2016 Ahome' Innovation Technologies. All rights reserved.

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

package com.ait.lienzo.tools.client.collection;

import jsinterop.annotations.JsType;

// TODO: lienzo-to-native: remove this, not used anywhere...

@JsType
public final class MetaData //implements NHasJSO<NObjectJSO>, NJSONStringify
{
//    private final NObjectJSO m_jso;

    public MetaData()
    {
 //       m_jso = NObjectJSO.make2P();
    }

//    public MetaData(final NObjectJSO jso)
//    {
//        if (null != jso)
//        {
//            m_jso = jso;
//        }
//        else
//        {
//            m_jso = NObjectJSO.make2P();
//        }
//        throw new UnsupportedOperationException();
//    }

//    public MetaData(final JavaScriptObject jso)
//    {
//        if ((null != jso) && (JSONType.OBJECT == Native.getNativeTypeOfJSO(jso)))
//        {
//            m_jso = jso.cast();
//        }
//        else
//        {
//            m_jso = NObjectJSO.make2P();
//        }
//    }


//    @Override
//    public final NObjectJSO getJSO()
//    {
//        return m_jso;
//    }

    public final MetaData putString(final String name, final String value)
    {
//        m_jso.putString(name, value);
//        return this;
        throw new UnsupportedOperationException();
    }

    public final MetaData putInt(final String name, final int value)
    {
//        m_jso.putString(name, value);
//        return this;
        throw new UnsupportedOperationException();
    }

    public final MetaData putDouble(final String name, final double value)
    {
//        m_jso.putString(name, value);
//        return this;
        throw new UnsupportedOperationException();
    }

    public final MetaData putBoolean(final String name, final boolean value)
    {
//        m_jso.putString(name, value);
//        return this;
        throw new UnsupportedOperationException();
    }

    public final MetaData putMetaData(final String name, final MetaData value)
    {
//        m_jso.putString(name, value);
//        return this;
        throw new UnsupportedOperationException();
    }

    public final boolean isEmpty()
    {
//        return m_jso.isEmpty();
        return true;
    }

//    public final JSONType getNativeTypeOf(final String name)
//    {
//        //return Native.getNativeTypeOf(m_jso, NUtils.doKeyRepair(name));
//        throw new UnsupportedOperationException();
//    }

    public final boolean isString(final String name)
    {
        //return Native.isString(m_jso, NUtils.doKeyRepair(name));
        throw new UnsupportedOperationException();
    }

    public final boolean isBoolean(final String name)
    {
        //return Native.isBoolean(m_jso, NUtils.doKeyRepair(name));
        throw new UnsupportedOperationException();
    }

    public final boolean isObject(final String name)
    {

        //return Native.isObject(m_jso, NUtils.doKeyRepair(name));
        throw new UnsupportedOperationException();
    }

    public final boolean isArray(final String name)
    {
        //return Native.isArray(m_jso, NUtils.doKeyRepair(name));
        //return false;
        throw new UnsupportedOperationException();
    }

    public final boolean isNumber(final String name)
    {
//        return Native.isNumber(m_jso, NUtils.doKeyRepair(name));
        throw new UnsupportedOperationException();
    }

    public final boolean isInteger(final String name)
    {
//        return Native.isInteger(m_jso, NUtils.doKeyRepair(name));
        throw new UnsupportedOperationException();
    }

    public final int getAsInteger(final String name)
    {
//        if (isInteger(name))
//        {
//            return m_jso.getAsInteger(name);
//        }
//        return 0;
        throw new UnsupportedOperationException();
    }

    public final double getAsDouble(final String name)
    {
//        if (isNumber(name))
//        {
//            return m_jso.getAsDouble(name);
//        }
//        return 0;
        throw new UnsupportedOperationException();
    }

    public final String getAsString(final String name)
    {
//        if (isString(name))
//        {
//            return m_jso.getAsString(name);
//        }
//        return null;
        throw new UnsupportedOperationException();
    }

    public final boolean getAsBoolean(final String name)
    {
//        if (isBoolean(name))
//        {
//            return m_jso.getAsBoolean(name);
//        }
//        return false;
        throw new UnsupportedOperationException();
    }

    public final MetaData getAsMetaData(final String name)
    {
//        if (isObject(name))
//        {
//            return new MetaData(m_jso.getAsJSO(name));
//        }
        throw new UnsupportedOperationException();
    }

    public final boolean isDefined(final String name)
    {
//        return m_jso.isDefined(name);
        throw new UnsupportedOperationException();
    }

    public final MetaData remove(final String name)
    {
//        m_jso.remove(name);
        throw new UnsupportedOperationException();
    }

//    @Override
//    public final String toJSONString(final int indent)
//    {
//        return m_jso.toJSONString(indent);
//    }
//
//    @Override
//    public final String toJSONString(final String indent)
//    {
//        //return m_jso.toJSONString(indent);
//        return null;
//    }
//
//    @Override
//    public final String toJSONString(final NJSONReplacer replacer, final int indent)
//    {
//        return m_jso.toJSONString(replacer, indent);
//    }
//
//    @Override
//    public final String toJSONString(final NJSONReplacer replacer, final String indent)
//    {
//        return m_jso.toJSONString(replacer, indent);
//        return null;
//    }
//
//    @Override
//    public final String toJSONString()
//    {
//        return m_jso.toJSONString();
//    }
//
//    @Override
//    public final String toJSONString(final NJSONReplacer replacer)
//    {
//        return m_jso.toJSONString(replacer);
//    }

    @Override
    public final String toString()
    {
//        return toJSONString();
        return null;
    }

    @Override
    public final boolean equals(final Object other)
    {
        return false;
    }

    @Override
    public final int hashCode()
    {
        return 0;
    }

//    @Override
//    public final NObject onWire()
//    {
//        return new NObject(m_jso);
//    }
}
