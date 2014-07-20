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

public interface EnumWithValue
{
    public String getValue();

    public static final class Statics
    {
        public static final <T extends EnumWithValue> T lookup(String key, T[] values, T otherwise)
        {
            if ((null != key) && (false == (key = key.trim()).isEmpty()))
            {
                for (int i = 0; i < values.length; i++)
                {
                    T value = values[i];

                    if (value.getValue().equals(key))
                    {
                        return value;
                    }
                }
            }
            return otherwise;
        }

        public static final <T extends EnumWithValue> List<String> getKeys(T[] values)
        {
            ArrayList<String> keys = new ArrayList<String>(values.length);

            for (int i = 0; i < values.length; i++)
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
