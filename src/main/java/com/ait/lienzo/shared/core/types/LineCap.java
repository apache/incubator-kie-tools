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

/**
 * Enum to type safe the usage of Canvas Line Caps.
 */
public enum LineCap implements EnumWithValue
{
    BUTT("butt"), ROUND("round"), SQUARE("square");

    private final String m_value;

    private static final EnumStringMap<LineCap> LOOKUP_MAP = Statics.build(LineCap.values());

    private LineCap(final String value)
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

    public static final LineCap lookup(final String key)
    {
        return Statics.lookup(key, LOOKUP_MAP, BUTT);
    }

    public static final List<String> getKeys()
    {
        return Statics.getKeys(LineCap.values());
    }

    public static final List<LineCap> getValues()
    {
        return Statics.getValues(LineCap.values());
    }
}
