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
 * An Image Filter than can be used to set the brightness levels for each of the RGB channels of the {@link Picture} 
 */
public class ChannelBrightnessImageDataFilter extends AbstractImageDataFilter<ChannelBrightnessImageDataFilter>
{
    private double m_r_brightness;

    private double m_g_brightness;

    private double m_b_brightness;

    public ChannelBrightnessImageDataFilter(double r_brightness, double g_brightness, double b_brightness)
    {
        setRBrightness(r_brightness);

        setGBrightness(g_brightness);

        setBBrightness(b_brightness);
    }

    /**
     * Sets the RED brightness value
     * 
     * @param brightness
     * @return {@link ChannelBrightnessImageDataFilter}
     */
    public ChannelBrightnessImageDataFilter setRBrightness(double brightness)
    {
        m_r_brightness = checkBrightnessValue(brightness);

        return this;
    }

    /**
     * Sets the GREEN brightness value.
     * 
     * @param brightness
     * @return {@link ChannelBrightnessImageDataFilter}
     */
    public ChannelBrightnessImageDataFilter setGBrightness(double brightness)
    {
        m_g_brightness = checkBrightnessValue(brightness);

        return this;
    }

    /**
     * Sets the BLUE brightness value.
     * 
     * @param brightness
     * @return {@link ChannelBrightnessImageDataFilter}
     */
    public ChannelBrightnessImageDataFilter setBBrightness(double brightness)
    {
        m_b_brightness = checkBrightnessValue(brightness);

        return this;
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
        filter_(data, FilterCommonOps.getLength(source), m_r_brightness, m_g_brightness, m_b_brightness);

        return source;
    }

    private final double checkBrightnessValue(double brightness)
    {
        if (brightness < -1)
        {
            brightness = -1;
        }
        if (brightness > 1)
        {
            brightness = 1;
        }
        return brightness;
    }

    private final native void filter_(JavaScriptObject pixa, int length, double r, double g, double b)
    /*-{
    	var data = pixa;

    	function calculate(v, brightness) {
    		return Math.max(Math.min((v + (brightness * 255) + 0.5), 255), 0) | 0;
    	}
    	for (var i = 0; i < length; i += 4) {

    		data[i + 0] = calculate(data[i + 0], r);

    		data[i + 1] = calculate(data[i + 1], g);

    		data[i + 2] = calculate(data[i + 2], b);
    	}
    }-*/;

    @Override
    public IFactory<ChannelBrightnessImageDataFilter> getFactory()
    {
        // TODO Auto-generated method stub
        return null;
    }
}
