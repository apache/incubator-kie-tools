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
 * A class that allows for easy creation of a Light Gray Scale Image Filter.
 */
public class LightnessGrayScaleImageDataFilter extends AbstractImageDataFilter<LightnessGrayScaleImageDataFilter>
{
    public LightnessGrayScaleImageDataFilter()
    {
    }

    protected LightnessGrayScaleImageDataFilter(JSONObject node, ValidationContext ctx) throws ValidationException
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
        filter_(data, FilterCommonOps.getLength(source));

        return source;
    }

    private final native void filter_(JavaScriptObject data, int length)
    /*-{
    	for (var i = 0; i < length; i += 4) {
    		var r = data[i + 0];
    		var g = data[i + 1];
    		var b = data[i + 2];
    		var v = ((((Math.max(Math.max(r, g), b) + Math.min(Math.min(r, g), b))) / 2.0) + 0.5) | 0;
    		data[i + 0] = v;
    		data[i + 1] = v;
    		data[i + 2] = v;
    	}
    }-*/;

    @Override
    public IFactory<LightnessGrayScaleImageDataFilter> getFactory()
    {
        return new LightnessGrayScaleImageDataFilterFactory();
    }

    public static class LightnessGrayScaleImageDataFilterFactory extends ImageDataFilterFactory<LightnessGrayScaleImageDataFilter>
    {
        public LightnessGrayScaleImageDataFilterFactory()
        {
            super(LightnessGrayScaleImageDataFilter.class.getSimpleName());
        }

        @Override
        public LightnessGrayScaleImageDataFilter create(JSONObject node, ValidationContext ctx) throws ValidationException
        {
            return new LightnessGrayScaleImageDataFilter(node, ctx);
        }
    }
}
