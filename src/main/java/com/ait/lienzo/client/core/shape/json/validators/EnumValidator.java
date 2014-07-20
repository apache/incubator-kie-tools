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

package com.ait.lienzo.client.core.shape.json.validators;

import com.ait.lienzo.shared.core.types.EnumWithValue;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;

public class EnumValidator<T extends Enum<T> & EnumWithValue> implements IAttributeTypeValidator
{
    private final T[]    m_values;

    private final String m_type;

    public EnumValidator(String type, T[] values)
    {
        m_type = type;

        m_values = values;
    }

    @Override
    public void validate(JSONValue jval, ValidationContext ctx) throws ValidationException
    {
        if (null == jval)
        {
            ctx.addBadTypeError(m_type);

            return;
        }
        JSONString sval = jval.isString();

        if (null == sval)
        {
            ctx.addBadTypeError(m_type);
        }
        else
        {
            String string = sval.stringValue();

            if (null != string)
            {
                for (T value : m_values)
                {
                    if (string.equals(value.getValue()))
                    {
                        return;
                    }
                }
            }
            ctx.addBadValueError(m_type, jval);
        }
    }
}