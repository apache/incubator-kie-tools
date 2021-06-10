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

import com.ait.lienzo.tools.common.api.types.IStringValued;

/**
 * GroupType is an extensible enumeration of all GroupOf types.
 */
public class GroupType implements IStringValued
{
    public static final GroupType GROUP = new GroupType("Group");

    private final String          m_value;

    protected GroupType(final String value)
    {
        m_value = value;
    }

    @Override
    public final String toString()
    {
        return m_value;
    }

    @Override
    public final String getValue()
    {
        return m_value;
    }

    @Override
    public boolean equals(final Object other)
    {
        if (!(other instanceof GroupType))
        {
            return false;
        }
        if (this == other)
        {
            return true;
        }
        return ((GroupType) other).getValue().equals(getValue());
    }

    @Override
    public int hashCode()
    {
        return getValue().hashCode();
    }
}
