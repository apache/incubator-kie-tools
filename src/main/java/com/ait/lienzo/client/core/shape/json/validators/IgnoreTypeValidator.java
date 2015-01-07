/*
   Copyright (c) 2014,2015 Ahome' Innovation Technologies. All rights reserved.

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

import com.google.gwt.json.client.JSONValue;

public class IgnoreTypeValidator implements IAttributeTypeValidator
{
    public static final IgnoreTypeValidator INSTANCE = new IgnoreTypeValidator();

    @Override
    public void validate(JSONValue jval, ValidationContext ctx) throws ValidationException
    {
        // TODO Auto-generated method stub
    }
}