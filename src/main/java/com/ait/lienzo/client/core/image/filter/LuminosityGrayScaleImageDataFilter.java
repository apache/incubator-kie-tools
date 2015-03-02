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

import com.ait.lienzo.client.core.shape.json.IFactory;
import com.ait.lienzo.client.core.shape.json.validators.ValidationContext;
import com.ait.lienzo.client.core.shape.json.validators.ValidationException;
import com.ait.lienzo.client.core.types.ImageData;
import com.ait.lienzo.shared.core.types.ImageFilterType;
import com.google.gwt.canvas.dom.client.CanvasPixelArray;
import com.google.gwt.json.client.JSONObject;

/**
 * A class that allows for easy creation of a Luminosity Gray Scale based Image Filter.
 */
public class LuminosityGrayScaleImageDataFilter extends AbstractImageDataFilter<LuminosityGrayScaleImageDataFilter>
{
    public LuminosityGrayScaleImageDataFilter()
    {
    }

    protected LuminosityGrayScaleImageDataFilter(JSONObject node, ValidationContext ctx) throws ValidationException
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
        FilterCommonOps.dofilterLuminosity(data, FilterCommonOps.getLength(source));

        return source;
    }

    @Override
    public IFactory<LuminosityGrayScaleImageDataFilter> getFactory()
    {
        return new LuminosityGrayScaleImageDataFilterFactory();
    }

    public static class LuminosityGrayScaleImageDataFilterFactory extends ImageDataFilterFactory<LuminosityGrayScaleImageDataFilter>
    {
        public LuminosityGrayScaleImageDataFilterFactory()
        {
            super(ImageFilterType.LuminosityGrayScaleImageDataFilterType);
        }

        @Override
        public LuminosityGrayScaleImageDataFilter create(JSONObject node, ValidationContext ctx) throws ValidationException
        {
            return new LuminosityGrayScaleImageDataFilter(node, ctx);
        }
    }
}
