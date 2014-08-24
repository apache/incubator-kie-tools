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
 * An Image filter to convert all pixels in the CanvasPixelArray to an RGB color.
 * 
 * <ul>
 * 	<li>
 * 		Alpha is always set to 255 in this filter.
 *  </li>
 * </ui>
 */
public class RGBIgnoreAlphaImageDataFilter extends AbstractRGBImageDataFilter<RGBIgnoreAlphaImageDataFilter>
{
    public RGBIgnoreAlphaImageDataFilter()
    {
    }

    public RGBIgnoreAlphaImageDataFilter(int r, int g, int b)
    {
        super(r, g, b);
    }

    public RGBIgnoreAlphaImageDataFilter(IColor color)
    {
        super(color);
    }

    public RGBIgnoreAlphaImageDataFilter(String color)
    {
        super(color);
    }

    protected RGBIgnoreAlphaImageDataFilter(JSONObject node, ValidationContext ctx) throws ValidationException
    {
        super(node, ctx);
    }

    /**
     * Returns an {@link ImageData} that is transformed based on the passed in RGB color, setting alpha to 255
     */
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

    		if (data[i + 3] > 0) {

    			data[i + 0] = r;

    			data[i + 1] = g;

    			data[i + 2] = b;

    			data[i + 3] = 255;
    		}
    	}
    }-*/;

    @Override
    public IFactory<RGBIgnoreAlphaImageDataFilter> getFactory()
    {
        // TODO Auto-generated method stub
        return null;
    }
}
