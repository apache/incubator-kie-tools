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

import java.util.List;

import com.ait.lienzo.shared.core.types.EnumWithValue;

/**
 * Used internally by the toolkit to map JSON types.
 */
public enum NativeInternalType implements EnumWithValue
{
    STRING("string"), NUMBER("number"), BOOLEAN("boolean"), FUNCTION("function"), ARRAY("array"), OBJECT("object"), UNDEFINED("undefined");

    private final String m_value;

    private NativeInternalType(String value)
    {
        m_value = value;
    }

    @Override
    public final String getValue()
    {
        return m_value;
    }

    @Override
    public final String toString()
    {
        return m_value;
    }

    public static final NativeInternalType lookup(String key)
    {
        return Statics.lookup(key, NativeInternalType.values(), null);
    }

    public static final List<String> getKeys()
    {
        return Statics.getKeys(NativeInternalType.values());
    }

    public static final List<NativeInternalType> getValues()
    {
        return Statics.getValues(NativeInternalType.values());
    }
}
