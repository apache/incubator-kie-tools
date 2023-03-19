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
 * An Image filter to convert all pixels in the CanvasPixelArray to an RGB color.
 *
 * <ul>
 * 	<li>
 * 		Alpha is always set to 255 in this filter.
 *  </li>
 * </ui>
 */
public class RGBIgnoreAlphaImageDataFilter extends AbstractRGBImageDataFilter<RGBIgnoreAlphaImageDataFilter> {

    public RGBIgnoreAlphaImageDataFilter() {
        super(ImageFilterType.RGBIgnoreAlphaImageDataFilterType);
    }

    public RGBIgnoreAlphaImageDataFilter(int r, int g, int b) {
        super(ImageFilterType.RGBIgnoreAlphaImageDataFilterType, r, g, b);
    }

    public RGBIgnoreAlphaImageDataFilter(IColor color) {
        super(ImageFilterType.RGBIgnoreAlphaImageDataFilterType, color);
    }

    public RGBIgnoreAlphaImageDataFilter(String color) {
        super(ImageFilterType.RGBIgnoreAlphaImageDataFilterType, color);
    }

    protected RGBIgnoreAlphaImageDataFilter(Object node) {
        super(ImageFilterType.RGBIgnoreAlphaImageDataFilterType, node);
    }

    /**
     * Returns an {@link ImageData} that is transformed based on the passed in RGB color, setting alpha to 255
     */
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
            if (data[i + 3] > 0) {
                data[i] = r;
                data[i + 1] = g;
                data[i + 2] = b;
                data[i + 3] = 255;
            }
        }
    }
}
