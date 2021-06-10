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

/**
 * A class that allows for easy creation of a Sharpen Image Filter.
 */
public class BumpImageDataFilter extends AbstractConvolveImageDataFilter<BumpImageDataFilter>
{
    public BumpImageDataFilter()
    {
        super(ImageFilterType.BumpImageDataFilterType, -1, -1, 0, -1, 1, 1, 0, 1, 1);
    }

    protected BumpImageDataFilter(Object node, ValidationContext ctx) throws ValidationException
    {
        super(ImageFilterType.BumpImageDataFilterType, node, ctx);
    }

    @Override
    public IFactory<BumpImageDataFilter> getFactory()
    {
        return new BumpImageDataFilterFactory();
    }

    public static class BumpImageDataFilterFactory extends ConvolveImageDataFilterFactory<BumpImageDataFilter>
    {
        public BumpImageDataFilterFactory()
        {
            super(ImageFilterType.BumpImageDataFilterType);
        }
    }
}
