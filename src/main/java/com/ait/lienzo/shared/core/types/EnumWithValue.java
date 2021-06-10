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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import com.ait.lienzo.tools.common.api.types.IStringValued;

public interface EnumWithValue extends IStringValued
{
    public static final class EnumStringMap<T extends EnumWithValue> extends LinkedHashMap<String, T>
    {
        private static final long serialVersionUID = -8637592993705769824L;

        public EnumStringMap()
        {
        }

        public EnumStringMap(final T[] values)
        {
            final int size = values.length;

            for (int i = 0; i < size; i++)
            {
                final T value = values[i];

                put(value.getValue(), value);
            }
        }

        public T lookup(final String key, final T otherwise)
        {
            if ((null != key) && (key.length() > 0))
            {
                final T value = get(key);

                if (null != value)
                {
                    return value;
                }
            }
            return otherwise;
        }

        public List<String> getKeys()
        {
            return new ArrayList<>(keySet());
        }

        public List<T> getValues()
        {
            return new ArrayList<>(values());
        }
        
        public Iterator<T> iterator()
        {
            return getValues().iterator();
        }
    }

    final class Statics
    {
        private Statics()
        {

        }

        public static final <T extends EnumWithValue> EnumStringMap<T> build(final T[] values)
        {
            return new EnumStringMap<>(values);
        }

        public static final <T extends EnumWithValue> T lookup(final String key, final EnumStringMap<T> map, final T otherwise)
        {
            return map.lookup(key, otherwise);
        }

        public static final <T extends EnumWithValue> List<String> getKeys(final T[] values)
        {
            final int size = values.length;

            final ArrayList<String> keys = new ArrayList<>(size);

            for (int i = 0; i < size; i++)
            {
                keys.add(values[i].getValue());
            }
            return keys;
        }

        public static final <T extends EnumWithValue> List<T> getValues(final T[] values)
        {
            return Arrays.asList(values);
        }
    }
}
