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

public class ContrastImageDataFilter extends AbstractValueTableImageDataFilter<ContrastImageDataFilter>
{
    public ContrastImageDataFilter()
    {
        super(1);
    }

    public ContrastImageDataFilter(double value)
    {
        super(value);
    }

    protected ContrastImageDataFilter(JSONObject node, ValidationContext ctx) throws ValidationException
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
        return 2;
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
        
            table[i] = (255 * (((i / 255) - 0.5) * value + 0.5)) | 0;
        }
        return table;
    }-*/;

    @Override
    public IFactory<ContrastImageDataFilter> getFactory()
    {
        return new ContrastImageDataFilterFactory();
    }

    public static class ContrastImageDataFilterFactory extends ValueTableImageDataFilterFactory<ContrastImageDataFilter>
    {
        public ContrastImageDataFilterFactory()
        {
            super(ContrastImageDataFilter.class.getSimpleName());
        }

        @Override
        public ContrastImageDataFilter create(JSONObject node, ValidationContext ctx) throws ValidationException
        {
            return new ContrastImageDataFilter(node, ctx);
        }
    }
}
