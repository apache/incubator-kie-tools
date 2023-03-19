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

import com.ait.lienzo.client.core.types.ImageDataUtil;
import com.ait.lienzo.shared.core.types.ImageFilterType;
import elemental2.core.Uint8ClampedArray;
import elemental2.dom.ImageData;

/**
 * A class that allows for easy creation of a Luminosity Gray Scale based Image Filter.
 */
public class LuminosityGrayScaleImageDataFilter extends AbstractImageDataFilter<LuminosityGrayScaleImageDataFilter> {

    public LuminosityGrayScaleImageDataFilter() {
        super(ImageFilterType.LuminosityGrayScaleImageDataFilterType);
    }

    protected LuminosityGrayScaleImageDataFilter(Object node) {
        super(ImageFilterType.LuminosityGrayScaleImageDataFilterType, node);
    }

    @Override
    public ImageData filter(ImageData source, boolean copy) {
        if (null == source) {
            return null;
        }
        if (copy) {
            source = ImageDataUtil.copy(source);
        }
        if (!isActive()) {
            return source;
        }
        final Uint8ClampedArray data = source.data;

        if (null == data) {
            return source;
        }
        FilterCommonOps.dofilterLuminosity(data, FilterCommonOps.getLength(source));

        return source;
    }
}
