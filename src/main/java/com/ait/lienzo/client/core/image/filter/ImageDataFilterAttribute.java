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

package com.ait.lienzo.client.core.image.filter;

import com.ait.lienzo.client.core.Attribute;
import com.ait.lienzo.client.core.AttributeType;

public class ImageDataFilterAttribute extends Attribute
{
    public static final ImageDataFilterAttribute ACTIVE = new ImageDataFilterAttribute("active", "", "", AttributeType.BOOLEAN_TYPE);

    public static final ImageDataFilterAttribute VALUE  = new ImageDataFilterAttribute("value", "", "", AttributeType.NUMBER_TYPE);

    public static final ImageDataFilterAttribute COLOR  = new ImageDataFilterAttribute("color", "", "", AttributeType.COLOR_TYPE);

    public static final ImageDataFilterAttribute MATRIX = new ImageDataFilterAttribute("matrix", "", "", AttributeType.NUMBER_ARRAY_TYPE);

    protected ImageDataFilterAttribute(String property, String label, String description, AttributeType type)
    {
        super(property, label, description, type);
    }
}
