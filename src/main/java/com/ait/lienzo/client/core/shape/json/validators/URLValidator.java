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

public class URLValidator extends AbstractAttributeTypeValidator
{
    public static final URLValidator INSTANCE = new URLValidator();

    public URLValidator()
    {
        super("URL");
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
//        final JSONString str = jval.isString();
//
//        if (null == str)
//        {
//            ctx.addBadTypeError(getTypeName());
//
//            return;
//        }
//        String url = str.stringValue();
//
//        if ((null == url) || ((url = url.trim()).isEmpty()) || (url.startsWith("#")))
//        {
//            ctx.addBadTypeError(getTypeName());
//
//            return;
//        }
//        if (url.startsWith("data:"))
//        {
//            return;
//        }
//        url = UriUtils.fromString(url).asString();
//
//        if ((null == url) || ((url = url.trim()).isEmpty()) || (url.startsWith("#")))
//        {
//            ctx.addBadTypeError(getTypeName());
//
//            return;
//        }
    }
}
