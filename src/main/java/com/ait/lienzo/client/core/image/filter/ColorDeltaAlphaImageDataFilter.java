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
public class ColorDeltaAlphaImageDataFilter extends AbstractRGBImageDataFilter<ColorDeltaAlphaImageDataFilter>
{
    public ColorDeltaAlphaImageDataFilter(int r, int g, int b, int value)
    {
        super(r, g, b);

        setValue(value);
    }

    public ColorDeltaAlphaImageDataFilter(IColor color, int value)
    {
        super(color);
    }

    public ColorDeltaAlphaImageDataFilter(String color, int value)
    {
        super(color);

        setValue(value);
    }

    protected ColorDeltaAlphaImageDataFilter(JSONObject node, ValidationContext ctx) throws ValidationException
    {
        super(node, ctx);
    }

    public final ColorDeltaAlphaImageDataFilter setValue(double value)
    {
        getAttributes().setValue(value);

        return this;
    }

    public final double getValue()
    {
        return getAttributes().getValue();
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
        filter_(data, FilterCommonOps.getLength(source), getR(), getG(), getB(), getValue());

        return source;
    }

    private final native void filter_(JavaScriptObject data, int length, int r, int g, int b, double v)
    /*-{
        var rmin = Math.max(r - v,   0) | 0;
        var rmax = Math.min(r + v, 255) | 0;
        var gmin = Math.max(g - v,   0) | 0;
        var gmax = Math.min(g + v, 255) | 0;
        var bmin = Math.max(b - v,   0) | 0;
        var bmax = Math.min(b + v, 255) | 0;
    	for (var i = 0; i < length; i += 4) {
            var rval = data[i + 0];
            var gval = data[i + 1];
            var bval = data[i + 2];
            if((rval <= rmax) && (rval >= rmin) && (gval <= gmax) && (gval >= gmin) && (bval <= bmax) && (bval >= bmin)) {
                data[i + 3] = 0;
            }
    	}
    }-*/;

    @Override
    public IFactory<ColorDeltaAlphaImageDataFilter> getFactory()
    {
        return new ColorDeltaAlphaImageDataFilterFactory();
    }

    public static class ColorDeltaAlphaImageDataFilterFactory extends RGBImageDataFilterFactory<ColorDeltaAlphaImageDataFilter>
    {
        public ColorDeltaAlphaImageDataFilterFactory()
        {
            super(ColorDeltaAlphaImageDataFilter.class.getSimpleName());

            addAttribute(Attribute.VALUE, true);
        }

        @Override
        public ColorDeltaAlphaImageDataFilter create(JSONObject node, ValidationContext ctx) throws ValidationException
        {
            return new ColorDeltaAlphaImageDataFilter(node, ctx);
        }
    }
}
