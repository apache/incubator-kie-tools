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

public class ExposureImageDataFilter extends AbstractValueTableImageDataFilter<ExposureImageDataFilter>
{
    public ExposureImageDataFilter()
    {
        super(1);
    }

    public ExposureImageDataFilter(double value)
    {
        super(value);
    }

    protected ExposureImageDataFilter(JSONObject node, ValidationContext ctx) throws ValidationException
    {
        super(node, ctx);
    }

    @Override
    public double getMinValue()
    {
        return 0;
    }

    @Override
    public double getMaxValue()
    {
        return 5;
    }

    @Override
    public double getRefValue()
    {
        return 1;
    }

    @Override
    protected final native FilterTableArray getTable(double value)
    /*-{        
        var table = [];
        
        for(var i = 0; i < 256; i++) {
        
            table[i] = (255 * (1 - Math.exp(-(i / 255) * value))) | 0;
        }
        return table;
    }-*/;

    @Override
    public IFactory<ExposureImageDataFilter> getFactory()
    {
        return new ExposureImageDataFilterFactory();
    }

    public static class ExposureImageDataFilterFactory extends ValueTableImageDataFilterFactory<ExposureImageDataFilter>
    {
        public ExposureImageDataFilterFactory()
        {
            super(ExposureImageDataFilter.class.getSimpleName());
        }

        @Override
        public ExposureImageDataFilter create(JSONObject node, ValidationContext ctx) throws ValidationException
        {
            return new ExposureImageDataFilter(node, ctx);
        }
    }
}
