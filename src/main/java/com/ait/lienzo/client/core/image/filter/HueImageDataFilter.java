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
import com.google.gwt.canvas.dom.client.CanvasPixelArray;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.json.client.JSONObject;

/**
 * A class that allows for easy creation of Brightness Filters.
 */
public class HueImageDataFilter extends AbstractValueImageDataFilter<HueImageDataFilter>
{
    public HueImageDataFilter()
    {
        super(0);
    }

    public HueImageDataFilter(double value)
    {
        super(value);
    }

    protected HueImageDataFilter(JSONObject node, ValidationContext ctx) throws ValidationException
    {
        super(node, ctx);
    }

    @Override
    public double getMinValue()
    {
        return -1;
    }

    @Override
    public double getMaxValue()
    {
        return 1;
    }

    @Override
    public double getRefValue()
    {
        return 0;
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
        filter_(data, source.getWidth(), source.getHeight(), getValue(), FilterCommonOps);

        return source;
    }

    private final native void filter_(JavaScriptObject data, int w, int h, double value, ImageDataFilterCommonOps fops)
    /*-{
    	 for (var y = 0; y < h; y++) {
            for (var x = 0; x < w; x++) {
                var p = (y * w + x) * 4;
                var hsv = fops.RGBtoHSV(data[p + 0], data[p + 1], data[p + 2]);
                hsv[0] += value;
                while(hsv[0] < 0) {
                    hsv[0] += 360;
                }
                var rgb = fops.HSVtoRGB(hsv[0] ,hsv[1], hsv[2]);
                for(var i = 0; i < 3; i++) {
                    data[p + i] = rgb[i];
                }
            }   
        }
    }-*/;

    @Override
    public IFactory<HueImageDataFilter> getFactory()
    {
        return new HueImageDataFilterFactory();
    }

    public static class HueImageDataFilterFactory extends ValueImageDataFilterFactory<HueImageDataFilter>
    {
        public HueImageDataFilterFactory()
        {
            super(HueImageDataFilter.class.getSimpleName());
        }

        @Override
        public HueImageDataFilter create(JSONObject node, ValidationContext ctx) throws ValidationException
        {
            return new HueImageDataFilter(node, ctx);
        }
    }
}
