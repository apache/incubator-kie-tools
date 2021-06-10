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
 * Enum to be used to constrain the Dragging Layer of a {@link Shape} or {@link Group}
 */
public enum DragMode implements EnumWithValue
{
    DRAG_LAYER("drag-layer"), SAME_LAYER("same-layer");

    private final String m_value;

    private static final EnumStringMap<DragMode> LOOKUP_MAP = Statics.build(DragMode.values());

    DragMode(final String value)
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
    public static final DragMode lookup(final String key)
    {
        return Statics.lookup(key, LOOKUP_MAP, DRAG_LAYER);
    }

    /**
     * Return list of enum keys.
     * 
     * @return
     */
    public static final List<String> getKeys()
    {
        return Statics.getKeys(DragMode.values());
    }

    /**
     * Return list of enum values.
     * 
     * @return
     */
    public static final List<DragMode> getValues()
    {
        return Statics.getValues(DragMode.values());
    }
}
