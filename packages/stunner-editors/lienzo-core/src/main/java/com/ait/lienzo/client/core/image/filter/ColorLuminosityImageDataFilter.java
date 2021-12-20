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
import com.ait.lienzo.shared.core.types.IColor;
import com.ait.lienzo.shared.core.types.ImageFilterType;
import elemental2.core.Uint8ClampedArray;
import elemental2.dom.ImageData;
import jsinterop.base.Js;

/**
 * A class that allows for easy creation of a Color Luminosity based Image Filter.
 */
public class ColorLuminosityImageDataFilter extends AbstractRGBImageDataFilter<ColorLuminosityImageDataFilter> {

    public ColorLuminosityImageDataFilter(int r, int g, int b) {
        super(ImageFilterType.ColorLuminosityImageDataFilterType, r, g, b);
    }

    public ColorLuminosityImageDataFilter(IColor color) {
        super(ImageFilterType.ColorLuminosityImageDataFilterType, color);
    }

    public ColorLuminosityImageDataFilter(String color) {
        super(ImageFilterType.ColorLuminosityImageDataFilterType, color);
    }

    protected ColorLuminosityImageDataFilter(Object node) {
        super(ImageFilterType.ColorLuminosityImageDataFilterType, node);
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
        filter_(data, FilterCommonOps.getLength(source), getR(), getG(), getB());

        return source;
    }

    private final void filter_(Uint8ClampedArray dataArray, int length, int r, int g, int b) {
        int[] data = Js.uncheckedCast(dataArray);
        for (int i = 0; i < length; i += 4) {
            double v = (((data[i] * 0.21) + (data[i + 1] * 0.72) + (data[i + 2] * 0.07)) / 255.0);
            data[i] = Js.coerceToInt(((r * v) + 0.5));
            data[i + 1] = Js.coerceToInt(((g * v) + 0.5));
            data[i + 2] = Js.coerceToInt(((b * v) + 0.5));
        }
    }
}
