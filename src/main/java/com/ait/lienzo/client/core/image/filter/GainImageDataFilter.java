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

import com.google.gwt.core.client.JavaScriptObject;

public class GainImageDataFilter extends AbstractTableImageDataFilter<GainImageDataFilter>
{
    private double m_gain = 0.5;

    private double m_bias = 0.5;

    public GainImageDataFilter()
    {
    }

    public GainImageDataFilter(double gain, double bias)
    {
        setGain(gain);

        setBias(bias);
    }

    public final GainImageDataFilter setGain(double gain)
    {
        m_gain = Math.max(Math.min(gain, 1.0), 0.0);

        return this;
    }

    public final double getGain()
    {
        return m_gain;
    }

    public final GainImageDataFilter setBias(double bias)
    {
        m_bias = Math.max(Math.min(bias, 1.0), 0.0);

        return this;
    }

    public final double getBias()
    {
        return m_bias;
    }

    @Override
    protected final native JavaScriptObject getTable()
    /*-{
        var gain = this.@com.ait.lienzo.client.core.image.filter.GainImageDataFilter::m_gain;
        
        var bias = this.@com.ait.lienzo.client.core.image.filter.GainImageDataFilter::m_bias;
        
        var table = [];
        
        for(var i = 0; i < 256; i++) {
            var v = i / 255;
            var k = (1 / gain - 2) * (1 - 2 * v);
            v = (v < 0.5) ? v / (k + 1) : (k - v) / (k - 1);
            v /= (1 / bias - 2) * (1 - v) + 1; 
            table[i] = (255 * v) | 0;
        }
        return table;
    }-*/;
}
