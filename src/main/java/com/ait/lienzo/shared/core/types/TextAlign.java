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

import com.ait.lienzo.client.core.shape.Text;

import java.util.List;

/**
 * Enum to create a type safe set of values for {@link Text} Alignment.
 */
public enum TextAlign implements EnumWithValue
{
    START("start"), END("end"), LEFT("left"), CENTER("center"), RIGHT("right");

    private final String m_value;

    private static final EnumStringMap<TextAlign> LOOKUP_MAP = Statics.build(TextAlign.values());

    TextAlign(final String value)
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

    public static final TextAlign lookup(final String key)
    {
        return Statics.lookup(key, LOOKUP_MAP, START);
    }

    public static final List<String> getKeys()
    {
        return Statics.getKeys(TextAlign.values());
    }

    public static final List<TextAlign> getValues()
    {
        return Statics.getValues(TextAlign.values());
    }
}
