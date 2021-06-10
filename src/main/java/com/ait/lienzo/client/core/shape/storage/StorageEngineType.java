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

package com.ait.lienzo.client.core.shape.storage;

import com.ait.lienzo.tools.common.api.types.IStringValued;

public class StorageEngineType implements IStringValued
{
    public static final StorageEngineType VIEWPORT_FAST_ARRAY_STORAGE_ENGINE  = new StorageEngineType("ViewportFastArrayStorageEngine");

    public static final StorageEngineType SCENE_FAST_ARRAY_STORAGE_ENGINE     = new StorageEngineType("SceneFastArrayStorageEngine");

    public static final StorageEngineType PRIMITIVE_FAST_ARRAY_STORAGE_ENGINE = new StorageEngineType("PrimitiveFastArrayStorageEngine");

    private final String                  m_value;

    protected StorageEngineType(final String value)
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
        if ((other == null) || (!(other instanceof StorageEngineType)))
        {
            return false;
        }
        if (this == other)
        {
            return true;
        }
        StorageEngineType that = ((StorageEngineType) other);

        return that.getValue().equals(getValue());
    }

    @Override
    public int hashCode()
    {
        return getValue().hashCode();
    }
}
