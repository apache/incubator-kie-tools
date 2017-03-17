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

public enum Direction implements EnumWithValue
{
    NORTH("north"), SOUTH("south"), EAST("east"), WEST("west"), NONE("none"), NORTH_EAST("north_east"), SOUTH_EAST("south_east"), SOUTH_WEST("south_west"), NORTH_WEST("north_west");

    private final String m_value;

    private static final EnumStringMap<Direction> LOOKUP_MAP = Statics.build(Direction.values());

    private Direction(final String value)
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
    public static final Direction lookup(final String key)
    {
        return Statics.lookup(key, LOOKUP_MAP, NONE);
    }

    /**
     * Return list of enum keys.
     * 
     * @return
     */
    public static final List<String> getKeys()
    {
        return Statics.getKeys(Direction.values());
    }

    /**
     * Return list of enum values.
     * 
     * @return
     */
    public static final List<Direction> getValues()
    {
        return Statics.getValues(Direction.values());
    }
}
