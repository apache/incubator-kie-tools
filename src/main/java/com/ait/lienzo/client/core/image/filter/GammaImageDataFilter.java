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

public class GammaImageDataFilter extends AbstractTableImageDataFilter<GammaImageDataFilter>
{
    private double m_value = 1.0;

    public GammaImageDataFilter()
    {
    }

    public GammaImageDataFilter(double value)
    {
        setValue(value);
    }

    public final GammaImageDataFilter setValue(double value)
    {
        m_value = Math.max(Math.min(value, 2.0), 0.0);

        return this;
    }

    public final double getValue()
    {
        return m_value;
    }

    @Override
    protected final native FilterTableArray getTable()
    /*-{
        var value = this.@com.ait.lienzo.client.core.image.filter.GammaImageDataFilter::m_value;
        
        var table = [];
        
        for(var i = 0; i < 256; i++) {
        
            table[i] = 255 * Math.pow(i / 255, 1 / value) + 0.5;
        }
        return table;
    }-*/;

    @Override
    public IFactory<GammaImageDataFilter> getFactory()
    {
        // TODO Auto-generated method stub
        return null;
    }
}
