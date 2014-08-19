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

import java.util.Set;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;

public class BehaviorMapValidator implements IAttributeTypeValidator
{
    public static BehaviorMapValidator INSTANCE = new BehaviorMapValidator();

    public BehaviorMapValidator()
    {
    }

    @Override
    public void validate(JSONValue jval, ValidationContext ctx) throws ValidationException
    {
        if (null == jval)
        {
            ctx.addBadTypeError("BoundingBox");

            return;
        }
        JSONObject jobj = jval.isObject();

        if (null == jobj)
        {
            ctx.addBadTypeError("BoundingBox");
        }
        else
        {
            Set<String> keys = jobj.keySet();

            if (keys.isEmpty())
            {
                ctx.addBadTypeError("BoundingBox no keys");

                return;
            }
            for (String ikey : keys)
            {
                if ((null == ikey) || (ikey.trim().isEmpty()))
                {
                    ctx.addBadTypeError("BoundingBox bad key");

                    return;
                }
                jval = jobj.get(ikey);

                if (null == jval)
                {
                    ctx.addBadTypeError("BoundingBox no array");

                    return;
                }
                JSONArray jarr = jval.isArray();

                if (null == jarr)
                {
                    ctx.addBadTypeError("BoundingBox not array");

                    return;
                }
                if (jarr.size() < 2)
                {
                    ctx.addBadArraySizeError(2, jarr.size());

                    return;
                }
                BoundingBoxArrayValidator.INSTANCE.validate(jarr, ctx);
            }
        }
    }
}
