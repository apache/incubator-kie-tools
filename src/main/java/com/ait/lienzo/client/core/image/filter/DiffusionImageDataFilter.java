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
import com.google.gwt.json.client.JSONObject;

public class DiffusionImageDataFilter extends AbstractValueTransformImageDataFilter<DiffusionImageDataFilter>
{
    public DiffusionImageDataFilter()
    {
        super(4);
    }

    public DiffusionImageDataFilter(double value)
    {
        super(value);
    }

    protected DiffusionImageDataFilter(JSONObject node, ValidationContext ctx) throws ValidationException
    {
        super(node, ctx);
    }

    @Override
    public double getMinValue()
    {
        return 1;
    }

    @Override
    public double getMaxValue()
    {
        return 100;
    }

    @Override
    public double getRefValue()
    {
        return 4;
    }

    @Override
    protected final native FilterTransformFunction getTransform(double value)
    /*-{
        var stabl = [];
        var ctabl = [];
        for(var i = 0; i < 256; i++) {
            var a = Math.PI *2 * i / 256;
            stabl[i] = value * Math.sin(a);
            ctabl[i] = value * Math.cos(a);
        }
        return function(x, y, out) {
            var a = (Math.random() * 255) | 0;
            var d = Math.random();
            out[0] = x + d * stabl[a];
            out[1] = y + d * ctabl[a];
        };
    }-*/;

    @Override
    public IFactory<DiffusionImageDataFilter> getFactory()
    {
        return new DiffusionImageDataFilterFactory();
    }

    public static class DiffusionImageDataFilterFactory extends ValueTransformImageDataFilterFactory<DiffusionImageDataFilter>
    {
        public DiffusionImageDataFilterFactory()
        {
            super(DiffusionImageDataFilter.class.getSimpleName());
        }

        @Override
        public DiffusionImageDataFilter create(JSONObject node, ValidationContext ctx) throws ValidationException
        {
            return new DiffusionImageDataFilter(node, ctx);
        }
    }
}
