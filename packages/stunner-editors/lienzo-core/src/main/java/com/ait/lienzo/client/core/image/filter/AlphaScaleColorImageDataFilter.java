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

public class AlphaScaleColorImageDataFilter extends AbstractRGBImageDataFilter<AlphaScaleColorImageDataFilter> {

    @JsProperty
    private boolean inverted;

    public AlphaScaleColorImageDataFilter(int r, int g, int b) {
        super(ImageFilterType.AlphaScaleColorImageDataFilterType, r, g, b);
    }

    public AlphaScaleColorImageDataFilter(int r, int g, int b, boolean invert) {
        super(ImageFilterType.AlphaScaleColorImageDataFilterType, r, g, b);

        setInverted(invert);
    }

    public AlphaScaleColorImageDataFilter(IColor color) {
        super(ImageFilterType.AlphaScaleColorImageDataFilterType, color);
    }

    public AlphaScaleColorImageDataFilter(IColor color, boolean invert) {
        super(ImageFilterType.AlphaScaleColorImageDataFilterType, color);

        setInverted(invert);
    }

    public AlphaScaleColorImageDataFilter(String color) {
        super(ImageFilterType.AlphaScaleColorImageDataFilterType, color);
    }

    public AlphaScaleColorImageDataFilter(String color, boolean invert) {
        super(ImageFilterType.AlphaScaleColorImageDataFilterType, color);

        setInverted(invert);
    }

    protected AlphaScaleColorImageDataFilter(Object node) {
        super(ImageFilterType.AlphaScaleColorImageDataFilterType, node);
    }

    public AlphaScaleColorImageDataFilter setInverted(boolean inverted) {
        this.inverted = inverted;

        return this;
    }

    public boolean isInverted() {
        return this.inverted;
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
        filter_(data, FilterCommonOps.getLength(source), getR(), getG(), getB(), isInverted());

        return source;
    }

    private final void filter_(Uint8ClampedArray dataArray, int length, int r, int g, int b, boolean invert) {
        int[] data = Js.uncheckedCast(dataArray);
        for (int i = 0; i < length; i += 4) {
            double v = ((data[i] * 0.21) + (data[i + 1] * 0.72) + (data[i + 2] * 0.07));
            data[i] = r;
            data[i + 1] = g;
            data[i + 2] = b;
            v = Js.coerceToInt(v + 0.5);
            if (invert) {
                data[i + 3] = (int) v;
            } else {
                data[i + 3] = (int) (255 - v);
            }
        }
    }
}
