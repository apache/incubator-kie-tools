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

public abstract class AbstractConvolveImageDataFilter<T extends AbstractConvolveImageDataFilter<T>> extends AbstractImageDataFilter<T>
{
    protected AbstractConvolveImageDataFilter(double... matrix)
    {
        setMatrix(matrix);
    }

    protected AbstractConvolveImageDataFilter(FilterConvolveMatrix matrix)
    {
        setMatrix(matrix);
    }

    protected AbstractConvolveImageDataFilter(JSONObject node, ValidationContext ctx) throws ValidationException
    {
        super(node, ctx);
    }

    public final T setMatrix(double... matrix)
    {
        getAttributes().setMatrix(matrix);

        return cast();
    }

    public final T setMatrix(FilterConvolveMatrix matrix)
    {
        getAttributes().setMatrix(matrix);

        return cast();
    }

    public final FilterConvolveMatrix getMatrix()
    {
        return getAttributes().getMatrix();
    }

    @Override
    public final boolean isTransforming()
    {
        return true;
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
        FilterConvolveMatrix matrix = getMatrix();

        if (matrix.size() < 1)
        {
            return source;
        }
        ImageData result = source.create();

        FilterCommonOps.doFilterConvolve(data, result.getData(), matrix, source.getWidth(), source.getHeight());

        return result;
    }

    protected static abstract class ConvolveImageDataFilterFactory<T extends AbstractConvolveImageDataFilter<T>> extends ImageDataFilterFactory<T>
    {
        protected ConvolveImageDataFilterFactory(String type)
        {
            super(type);

            addAttribute(Attribute.MATRIX, true);
        }
    }
}
