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

package com.ait.lienzo.client.core.image.filter;

import com.ait.lienzo.client.core.Attribute;
import com.ait.lienzo.client.core.shape.json.validators.ValidationContext;
import com.ait.lienzo.client.core.shape.json.validators.ValidationException;
import com.ait.lienzo.client.core.types.ImageData;
import com.google.gwt.canvas.dom.client.CanvasPixelArray;
import com.google.gwt.json.client.JSONObject;

public abstract class AbstractValueTransformImageDataFilter<T extends AbstractValueTransformImageDataFilter<T>> extends AbstractValueImageDataFilter<T>
{
    protected AbstractValueTransformImageDataFilter(double value)
    {
        super(value);
    }

    protected AbstractValueTransformImageDataFilter(JSONObject node, ValidationContext ctx) throws ValidationException
    {
        super(node, ctx);
    }

    @Override
    public ImageData filter(ImageData source, boolean copy)
    {
        if (null == source)
        {
            return null;
        }
        if (copy)
        {
            source = source.copy();
        }
        if (false == isActive())
        {
            return source;
        }
        final CanvasPixelArray data = source.getData();

        if (null == data)
        {
            return source;
        }
        ImageData result = source.create();

        FilterCommonOps.doFilterTransform(data, result.getData(), getTransform(getValue()), source.getWidth(), source.getHeight());

        return result;
    }

    @Override
    public final boolean isTransforming()
    {
        return true;
    }

    protected abstract FilterTransformFunction getTransform(double value);

    protected static abstract class ValueTransformImageDataFilterFactory<T extends AbstractValueTransformImageDataFilter<T>> extends ImageDataFilterFactory<T>
    {
        protected ValueTransformImageDataFilterFactory(String type)
        {
            super(type);

            addAttribute(Attribute.VALUE, true);
        }
    }
}
