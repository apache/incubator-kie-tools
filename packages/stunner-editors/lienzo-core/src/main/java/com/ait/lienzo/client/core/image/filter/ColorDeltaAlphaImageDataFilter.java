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
import jsinterop.annotations.JsProperty;
import jsinterop.base.Js;

/**
 * A class that allows for easy creation of a Color Luminosity based Image Filter.
 */
public class ColorDeltaAlphaImageDataFilter extends AbstractRGBImageDataFilter<ColorDeltaAlphaImageDataFilter> {

    @JsProperty
    private double value;

    public ColorDeltaAlphaImageDataFilter(int r, int g, int b, int value) {
        super(ImageFilterType.ColorDeltaAlphaImageDataFilterType, r, g, b);

        setValue(value);
    }

    public ColorDeltaAlphaImageDataFilter(IColor color, int value) {
        super(ImageFilterType.ColorDeltaAlphaImageDataFilterType, color);
    }

    public ColorDeltaAlphaImageDataFilter(String color, int value) {
        super(ImageFilterType.ColorDeltaAlphaImageDataFilterType, color);

        setValue(value);
    }

    protected ColorDeltaAlphaImageDataFilter(Object node) {
        super(ImageFilterType.ColorDeltaAlphaImageDataFilterType, node);
    }

    public final ColorDeltaAlphaImageDataFilter setValue(double value) {
        this.value = value;

        return this;
    }

    public final double getValue() {
        return this.value;
    }

    @Override
    public ImageData filter(ImageData source, boolean copy) {
        if (null == source) {
            return null;
        }
        if (copy) {
            source = ImageDataUtil.copy(source);
        }
        if (false == isActive()) {
            return source;
        }
        final Uint8ClampedArray data = source.data;

        if (null == data) {
            return source;
        }
        filter_(data, FilterCommonOps.getLength(source), getR(), getG(), getB(), getValue());

        return source;
    }

    private final void filter_(Uint8ClampedArray dataArray, int length, int r, int g, int b, double v) {
        int[] data = Js.uncheckedCast(dataArray);
        int rmin = Js.coerceToInt(Math.max(r - v, 0));
        int rmax = Js.coerceToInt(Math.min(r + v, 255));
        int gmin = Js.coerceToInt(Math.max(g - v, 0));
        int gmax = Js.coerceToInt(Math.min(g + v, 255));
        int bmin = Js.coerceToInt(Math.max(b - v, 0));
        int bmax = Js.coerceToInt(Math.min(b + v, 255));
        for (int i = 0; i < length; i += 4) {
            double rval = data[i];
            double gval = data[i + 1];
            double bval = data[i + 2];
            if ((rval <= rmax) && (rval >= rmin) && (gval <= gmax) && (gval >= gmin) && (bval <= bmax) && (bval >= bmin)) {
                data[i + 3] = 0;
            }
        }
    }
}
