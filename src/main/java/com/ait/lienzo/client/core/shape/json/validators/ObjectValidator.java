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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;

public class ObjectValidator implements IAttributeTypeValidator
{
    private final String                               m_typeName;

    private final List<String>                         m_requiredAttributes = new ArrayList<String>();

    private final Map<String, IAttributeTypeValidator> m_attributes         = new HashMap<String, IAttributeTypeValidator>();

    public ObjectValidator(String typeName)
    {
        m_typeName = typeName;
    }

    public void addAttribute(String attrName, IAttributeTypeValidator type, boolean required)
    {
        m_attributes.put(attrName, type);

        if (required)
        {
            m_requiredAttributes.add(attrName);
        }
    }

    @Override
    public void validate(JSONValue jval, ValidationContext ctx) throws ValidationException
    {
        if (null == jval)
        {
            ctx.addBadTypeError("Object");

            return;
        }
        JSONObject jobj = jval.isObject();

        if (null == jobj)
        {
            ctx.addBadTypeError("Object");
        }
        else
        {
            Set<String> keys = jobj.keySet();

            // Check required attributes

            for (String attrName : m_requiredAttributes)
            {
                ctx.push(attrName);

                if (false == keys.contains(attrName))
                {
                    ctx.addRequiredError(); // value is missing
                }
                else
                {
                    JSONValue aval = jobj.get(attrName);

                    if ((null == aval) || (null != aval.isNull()))
                    {
                        ctx.addRequiredError(); // value is null
                    }
                }
                ctx.pop(); // attrName
            }
            // Now check the attribute values

            for (String attrName : keys)
            {
                ctx.push(attrName);

                IAttributeTypeValidator validator = m_attributes.get(attrName);

                if (null == validator)
                {
                    ctx.addInvalidAttributeError(m_typeName);
                }
                else
                {
                    JSONValue aval = jobj.get(attrName);

                    validator.validate(aval, ctx);
                }
                ctx.pop(); // attrName
            }
        }
    }

    protected void checkHardcodedAttribute(String attrName, String requiredAttrValue, JSONValue jval, ValidationContext ctx) throws ValidationException
    {
        // ASSUMPTION: requiredness was already checked and reported on

        JSONObject jobj = jval.isObject();

        if (null != jobj)
        {
            JSONValue aval = jobj.get(attrName);

            if (null != aval)
            {
                JSONString s = aval.isString();

                if ((null == s) || (false == requiredAttrValue.equals(s.stringValue())))
                {
                    ctx.push(attrName);

                    ctx.addRequiredAttributeValueError(requiredAttrValue);

                    ctx.pop(); // attrName
                }
            }
        }
    }
}
