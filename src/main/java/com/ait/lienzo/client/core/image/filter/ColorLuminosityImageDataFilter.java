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

import com.ait.lienzo.client.core.shape.json.IFactory;
import com.ait.lienzo.client.core.shape.json.validators.ValidationContext;
import com.ait.lienzo.client.core.shape.json.validators.ValidationException;
import com.ait.lienzo.client.core.types.ImageData;
import com.ait.lienzo.shared.core.types.IColor;
import com.google.gwt.canvas.dom.client.CanvasPixelArray;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.json.client.JSONObject;

/**
 * A class that allows for easy creation of a Color Luminosity based Image Filter.
 */
public class ColorLuminosityImageDataFilter extends AbstractRGBImageDataFilter<ColorLuminosityImageDataFilter>
{
    public ColorLuminosityImageDataFilter(int r, int g, int b)
    {
        super(r, g, b);
    }

    public ColorLuminosityImageDataFilter(IColor color)
    {
        super(color);
    }

    public ColorLuminosityImageDataFilter(String color)
    {
        super(color);
    }

    protected ColorLuminosityImageDataFilter(JSONObject node, ValidationContext ctx) throws ValidationException
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
        filter_(data, FilterCommonOps.getLength(source), getR(), getG(), getB());

        return source;
    }

    private final native void filter_(JavaScriptObject data, int length, int r, int g, int b)
    /*-{
    	for (var i = 0; i < length; i += 4) {
            var v = (((data[i + 0] * 0.21) + (data[i + 1] * 0.72) + (data[i + 2] * 0.07)) / 255.0);
    		data[i + 0] = ((r * v) + 0.5) | 0;
    		data[i + 1] = ((g * v) + 0.5) | 0;
    		data[i + 2] = ((b * v) + 0.5) | 0;
    	}
    }-*/;

    @Override
    public IFactory<ColorLuminosityImageDataFilter> getFactory()
    {
        return new ColorLuminosityImageDataFilterFactory();
    }

    public static class ColorLuminosityImageDataFilterFactory extends RGBImageDataFilterFactory<ColorLuminosityImageDataFilter>
    {
        public ColorLuminosityImageDataFilterFactory()
        {
            super(ColorLuminosityImageDataFilter.class.getSimpleName());
        }

        @Override
        public ColorLuminosityImageDataFilter create(JSONObject node, ValidationContext ctx) throws ValidationException
        {
            return new ColorLuminosityImageDataFilter(node, ctx);
        }
    }
}
