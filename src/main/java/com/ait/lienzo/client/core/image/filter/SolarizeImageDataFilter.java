/*
   Copyright (c) 2017 Ahome' Innovation Technologies. All rights reserved.

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
import com.ait.lienzo.shared.core.types.ImageFilterType;

import jsinterop.base.Js;

public class SolarizeImageDataFilter extends AbstractTableImageDataFilter<SolarizeImageDataFilter>
{
    private static final FilterTableArray CONSTANT_TABLE = table();

    private static final FilterTableArray table()
    {
        int[] table = new int[256];
        for(int i = 0; i < 256; i++) {
            int v = Js.coerceToInt(((i / 255 > 0.5) ? 2 * (i / 255 - 0.5) : 2 * (0.5 - i / 255)) * 255);
            table[i] = v;
        }
        return new FilterTableArray(table);
    }

    public SolarizeImageDataFilter()
    {
        super(ImageFilterType.SolarizeImageDataFilterType);
    }

    protected SolarizeImageDataFilter(Object node, ValidationContext ctx) throws ValidationException
    {
        super(ImageFilterType.SolarizeImageDataFilterType, node, ctx);
    }

    @Override
    protected final FilterTableArray getTable()
    {
        return CONSTANT_TABLE;
    }

    @Override
    public IFactory<SolarizeImageDataFilter> getFactory()
    {
        return new SolarizeImageDataFilterFactory();
    }

    public static class SolarizeImageDataFilterFactory extends TableImageDataFilterFactory<SolarizeImageDataFilter>
    {
        public SolarizeImageDataFilterFactory()
        {
            super(ImageFilterType.SolarizeImageDataFilterType);
        }
    }
}
