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
import jsinterop.base.Js;

/**
 * A class that allows for easy creation of Brightness Filters.
 */
public class BrightnessImageDataFilter extends AbstractValueImageDataFilter<BrightnessImageDataFilter> {

    public BrightnessImageDataFilter() {
        super(ImageFilterType.BrightnessImageDataFilterType, 0);
    }

    public BrightnessImageDataFilter(double value) {
        super(ImageFilterType.BrightnessImageDataFilterType, value);
    }

    protected BrightnessImageDataFilter(Object node) {
        super(ImageFilterType.BrightnessImageDataFilterType, node);
    }

    @Override
    public double getMinValue() {
        return -1;
    }

    @Override
    public double getMaxValue() {
        return 1;
    }

    @Override
    public double getRefValue() {
        return 0;
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
        filter_(data, FilterCommonOps.getLength(source), getValue());

        return source;
    }

    private final void filter_(Uint8ClampedArray dataArray, int length, double value) {
        int[] data = Js.uncheckedCast(dataArray);
        double v = (value * 255) + 0.5;
        for (int i = 0; i < length; i += 4) {
            data[i] = Js.coerceToInt(Math.max(Math.min(data[i] + v, 255), 0));
            data[i + 1] = Js.coerceToInt(Math.max(Math.min(data[i + 1] + v, 255), 0));
            data[i + 2] = Js.coerceToInt(Math.max(Math.min(data[i + 2] + v, 255), 0));
        }
    }
}
