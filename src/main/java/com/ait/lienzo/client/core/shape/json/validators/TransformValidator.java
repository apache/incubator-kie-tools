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

package com.ait.lienzo.client.core.shape.json.validators;

public class TransformValidator extends ArrayValidator
{
    public static final TransformValidator INSTANCE = new TransformValidator();

    public TransformValidator()
    {
        super(NumberValidator.INSTANCE);
    }

    @Override
    public void validate(final Object jval, final ValidationContext ctx) throws ValidationException
    {
        super.validate(jval, ctx);

        // @FIXME serialization (mdp)
//        if (null != jval)
//        {
//            final JSONArray jarr = jval.isArray();
//
//            if (null != jarr)
//            {
//                if (jarr.size() != 6)
//                {
//                    ctx.addBadArraySizeError(6, jarr.size());
//                }
//                else
//                {
//                    for (int i = 0; i < 6; i++)
//                    {
//                        ctx.pushIndex(i);
//
//                        final JSONValue val = jarr.get(i);
//
//                        if ((val == null) || (val.isNumber() == null))
//                        {
//                            ctx.addBadTypeError(NumberValidator.INSTANCE.getTypeName());
//                        }
//                        ctx.pop();// i
//                    }
//                }
//            }
//        }
    }
}
