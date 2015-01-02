/*
   Copyright (c) 2014 Ahome' Innovation Technologies. All rights reserved.

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
import java.util.List;

import com.ait.lienzo.client.core.types.NFastStringMap;

public interface EnumWithValue
{
    public String getValue();

    public static final class Statics
    {
        public static final <T extends EnumWithValue> NFastStringMap<T> build(final T[] values)
        {
            final NFastStringMap<T> make = new NFastStringMap<T>();

            final int size = values.length;

            for (int i = 0; i < size; i++)
            {
                T value = values[i];

                make.put(value.getValue(), value);
            }
            return make;
        }

        public static final <T extends EnumWithValue> T lookup(final String key, final NFastStringMap<T> map, final T otherwise)
        {
            if ((null != key) && (key.length() > 0))
            {
                T value = map.get(key);

                if (null != value)
                {
                    return value;
                }
            }
            return otherwise;
        }

        public static final <T extends EnumWithValue> List<String> getKeys(final T[] values)
        {
            final int size = values.length;

            final ArrayList<String> keys = new ArrayList<String>(size);

            for (int i = 0; i < size; i++)
            {
                keys.add(values[i].getValue());
            }
            return keys;
        }

        public static final <T extends EnumWithValue> List<T> getValues(T[] values)
        {
            return Arrays.asList(values);
        }
    }
}
