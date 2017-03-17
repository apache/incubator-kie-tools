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

package com.ait.lienzo.shared.core.types;

import java.util.List;

import com.ait.lienzo.client.core.shape.Arrow;

/**
 * ArrowType defines the style of the arrow heads for an 
 * {@link Arrow}.
 * See the {@link Arrow} 
 * class for a detailed description.
 */
public enum ArrowType implements EnumWithValue
{
    AT_END("at-end"), AT_START("at-start"), AT_BOTH_ENDS("at-both-ends"), AT_END_TAPERED("at-end-tapered"), AT_START_TAPERED("at-start-tapered");

    private final String m_value;

    private static final EnumStringMap<ArrowType> LOOKUP_MAP = Statics.build(ArrowType.values());

    private ArrowType(final String value)
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

    public static final ArrowType lookup(final String key)
    {
        return Statics.lookup(key, LOOKUP_MAP, AT_END);
    }

    public static final List<String> getKeys()
    {
        return Statics.getKeys(ArrowType.values());
    }

    public static final List<ArrowType> getValues()
    {
        return Statics.getValues(ArrowType.values());
    }
}
