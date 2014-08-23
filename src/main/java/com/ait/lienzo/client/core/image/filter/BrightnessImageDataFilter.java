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
import com.ait.lienzo.client.core.types.ImageData;
import com.google.gwt.canvas.dom.client.CanvasPixelArray;
import com.google.gwt.core.client.JavaScriptObject;

/**
 * A class that allows for easy creation of Brightness Filters.
 */
public class BrightnessImageDataFilter extends AbstractImageDataFilter<BrightnessImageDataFilter>
{
    private double m_brightness;

    public BrightnessImageDataFilter(double brightness)
    {
        setBrightness(brightness);
    }

    public BrightnessImageDataFilter setBrightness(double brightness)
    {
        if (brightness < -1)
        {
            brightness = -1;
        }
        if (brightness > 1)
        {
            brightness = 1;
        }
        m_brightness = brightness;

        return this;
    }

    public double getBrightness()
    {
        return m_brightness;
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
        filter_(data, FilterCommonOps.getLength(source), m_brightness);

        return source;
    }

    private final native void filter_(JavaScriptObject pixa, int length, double brightness)
    /*-{
    	var data = pixa;

    	function calculate(v) {
    		return Math.max(Math.min((v + (brightness * 255) + 0.5), 255), 0) | 0;
    	}
    	for (var i = 0; i < length; i += 4) {

    		data[i + 0] = calculate(data[i + 0]);

    		data[i + 1] = calculate(data[i + 1]);

    		data[i + 2] = calculate(data[i + 2]);
    	}
    }-*/;

    @Override
    public IFactory<BrightnessImageDataFilter> getFactory()
    {
        // TODO Auto-generated method stub
        return null;
    }
}
