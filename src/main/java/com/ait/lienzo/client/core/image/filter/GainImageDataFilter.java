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
import com.google.gwt.json.client.JSONObject;

public class GainImageDataFilter extends AbstractTableImageDataFilter<GainImageDataFilter>
{
    public GainImageDataFilter()
    {
        this(0.5, 0.5);
    }

    public GainImageDataFilter(double gain, double bias)
    {
        setGain(gain);

        setBias(bias);
    }

    protected GainImageDataFilter(JSONObject node, ValidationContext ctx) throws ValidationException
    {
        super(node, ctx);
    }

    public final GainImageDataFilter setGain(double gain)
    {
        getAttributes().setGain(Math.max(Math.min(gain, getMaxGain()), getMinGain()));

        return this;
    }

    public final double getGain()
    {
        return Math.max(Math.min(getAttributes().getGain(), getMaxGain()), getMinGain());
    }

    public final double getMinGain()
    {
        return 0;
    }

    public final double getMaxGain()
    {
        return 1;
    }

    public final GainImageDataFilter setBias(double bias)
    {
        getAttributes().setBias(Math.max(Math.min(bias, getMaxBias()), getMinBias()));

        return this;
    }

    public final double getBias()
    {
        return Math.max(Math.min(getAttributes().getBias(), getMaxBias()), getMinBias());
    }

    public final double getMinBias()
    {
        return 0;
    }

    public final double getMaxBias()
    {
        return 1;
    }

    @Override
    protected final native FilterTableArray getTable()
    /*-{
        var table = [];
        var gain = this.@com.ait.lienzo.client.core.image.filter.GainImageDataFilter::getGain()();
        var bias = this.@com.ait.lienzo.client.core.image.filter.GainImageDataFilter::getBias()();
        for(var i = 0; i < 256; i++) {
            var v = i / 255;
            var k = (1 / gain - 2) * (1 - 2 * v);
            v = (v < 0.5) ? v / (k + 1) : (k - v) / (k - 1);
            v /= (1 / bias - 2) * (1 - v) + 1; 
            table[i] = (255 * v) | 0;
        }
        return table;
    }-*/;

    @Override
    public IFactory<GainImageDataFilter> getFactory()
    {
        return new GainImageDataFilterFactory();
    }

    public static class GainImageDataFilterFactory extends TableImageDataFilterFactory<GainImageDataFilter>
    {
        public GainImageDataFilterFactory()
        {
            super(GainImageDataFilter.class.getSimpleName());

            addAttribute(Attribute.GAIN, true);

            addAttribute(Attribute.BIAS, true);
        }

        @Override
        public GainImageDataFilter create(JSONObject node, ValidationContext ctx) throws ValidationException
        {
            return new GainImageDataFilter(node, ctx);
        }
    }
}
