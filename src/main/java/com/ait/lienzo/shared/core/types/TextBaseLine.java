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
public enum TextBaseLine implements EnumWithValue
{
    ALPHABETIC("alphabetic"), TOP("top"), HANGING("hanging"), MIDDLE("middle"), IDEOGRAPHIC("ideographic"), BOTTOM("bottom");

    private final String m_value;

    private static final EnumStringMap<TextBaseLine> LOOKUP_MAP = Statics.build(TextBaseLine.values());

    private TextBaseLine(final String value)
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

    public static final TextBaseLine lookup(final String key)
    {
        return Statics.lookup(key, LOOKUP_MAP, ALPHABETIC);
    }

    public static final List<String> getKeys()
    {
        return Statics.getKeys(TextBaseLine.values());
    }

    public static final List<TextBaseLine> getValues()
    {
        return Statics.getValues(TextBaseLine.values());
    }
}
