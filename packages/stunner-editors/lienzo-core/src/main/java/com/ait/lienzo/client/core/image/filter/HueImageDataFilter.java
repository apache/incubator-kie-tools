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
public class HueImageDataFilter extends AbstractValueImageDataFilter<HueImageDataFilter> {

    public HueImageDataFilter() {
        super(ImageFilterType.HueImageDataFilterType, 0);
    }

    public HueImageDataFilter(double value) {
        super(ImageFilterType.HueImageDataFilterType, value);
    }

    protected HueImageDataFilter(Object node) {
        super(ImageFilterType.HueImageDataFilterType, node);
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
        filter_(data, source.width, source.height, getValue(), FilterCommonOps);

        return source;
    }

    private final void filter_(Uint8ClampedArray dataArray, int w, int h, double value, ImageDataFilterCommonOps fops) {
        int[] data = Js.uncheckedCast(dataArray);
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int p = (y * w + x) * 4;
                int[] hsv = fops.RGBtoHSV(data[p], data[p + 1], data[p + 2]);
                hsv[0] += value;
                while (hsv[0] < 0) {
                    hsv[0] += 360;
                }
                int[] rgb = fops.HSVtoRGB(hsv[0], hsv[1], hsv[2]);
                for (int i = 0; i < 3; i++) {
                    data[p + i] = rgb[i];
                }
            }
        }
    }
}
