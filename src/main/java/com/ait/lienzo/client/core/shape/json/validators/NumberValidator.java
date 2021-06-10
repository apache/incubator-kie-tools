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

import elemental2.core.JsNumber;

public class NumberValidator extends AbstractAttributeTypeValidator
{
    public static final NumberValidator INSTANCE = new NumberValidator();

    public NumberValidator()
    {
        super("Number");
    }

    @Override
    public void validate(final Object jval, final ValidationContext ctx) throws ValidationException
    {
        if (null == jval)
        {
            ctx.addBadTypeError(getTypeName());

            return;
        }

        // @FIXME serialization (mdp)
//        final JSONNumber numb = jval.isNumber();
//
//        if (null == numb)
//        {
//            ctx.addBadTypeError(getTypeName());
//
//            return;
//        }
//        if (false == isNumber(numb.doubleValue()))
//        {
//            ctx.addBadTypeError(getTypeName());
//        }
    }

    private final boolean isNumber(double number)
    {
		return JsNumber.isFinite(number);
    }
}
