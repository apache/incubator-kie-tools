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

import com.ait.lienzo.client.core.types.PatternGradient;

/**
 * Enum to type safe the usage of Canvas fill patterns. See {@link PatternGradient}.
 */
public enum FillRepeat implements EnumWithValue
{
    REPEAT("repeat"), REPEAT_X("repeat-x"), REPEAT_Y("repeat-y"), NO_REPEAT("no-repeat");

    private final String m_value;

    private static final EnumStringMap<FillRepeat> LOOKUP_MAP = Statics.build(FillRepeat.values());

    FillRepeat(final String value)
    {
        m_value = value;
    }

    /**
     * Return String representation.
     * 
     * @return
     */

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

    /**
     * Used to safely convert from a String to an enum.
     * 
     * @param key
     * @return
     */
    public static final FillRepeat lookup(final String key)
    {
        return Statics.lookup(key, LOOKUP_MAP, NO_REPEAT);
    }

    /**
     * Return list of enum keys.
     * 
     * @return
     */
    public static final List<String> getKeys()
    {
        return Statics.getKeys(FillRepeat.values());
    }

    /**
     * Return list of enum values.
     * 
     * @return
     */
    public static final List<FillRepeat> getValues()
    {
        return Statics.getValues(FillRepeat.values());
    }
}
