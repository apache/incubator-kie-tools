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
 * Enum to be used to constrain the Dragging Area of a {@link ShapeType}
 */
public enum DragConstraint implements EnumWithValue
{
    HORIZONTAL("horizontal"), VERTICAL("vertical"), NONE("none");

    private final String m_value;

    private static final EnumStringMap<DragConstraint> LOOKUP_MAP = Statics.build(DragConstraint.values());

    private DragConstraint(final String value)
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
    public static final DragConstraint lookup(final String key)
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
        return Statics.getKeys(DragConstraint.values());
    }

    /**
     * Return list of enum values.
     * 
     * @return
     */
    public static final List<DragConstraint> getValues()
    {
        return Statics.getValues(DragConstraint.values());
    }
}
