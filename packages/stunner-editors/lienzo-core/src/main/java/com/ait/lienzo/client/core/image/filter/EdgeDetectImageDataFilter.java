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
 * A class that allows for easy creation of a Light Gray Scale Image Filter.
 */
public class EdgeDetectImageDataFilter extends AbstractImageDataFilter<EdgeDetectImageDataFilter> {

    public EdgeDetectImageDataFilter() {
        super(ImageFilterType.EdgeDetectImageDataFilterType);
    }

    protected EdgeDetectImageDataFilter(Object node) {
        super(ImageFilterType.EdgeDetectImageDataFilterType, node);
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
        ImageData result = ImageDataUtil.create(source);

        filter_(data, result.data, source.width, source.height);

        return result;
    }

    private final void filter_(Uint8ClampedArray dataArray, Uint8ClampedArray buffArray, int w, int h) {
        int[] data = Js.uncheckedCast(dataArray);
        int[] buff = Js.uncheckedCast(buffArray);

        int[] hmap = new int[]{-1, -2, -1, 0, 0, 0, 1, 2, 1};
        int[] vmap = new int[]{-1, 0, 1, -2, 0, 2, -1, 0, 1};
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int p = (y * w + x) * 4;
                int rh = 0;
                int gh = 0;
                int bh = 0;
                int rv = 0;
                int gv = 0;
                int bv = 0;
                for (int irow = -1; irow <= 1; irow++) {
                    int iy = y + irow;
                    int ioff;
                    if ((iy >= 0) && (iy < h)) {
                        ioff = iy * w * 4;
                    } else {
                        ioff = y * w * 4;
                    }
                    int moff = 3 * (irow + 1) + 1;
                    for (int icol = -1; icol <= 1; icol++) {
                        int ix = x + icol;
                        if (!((ix >= 0) && (ix < w))) {
                            ix = x;
                        }
                        ix *= 4;
                        int f = ioff + ix;
                        double r = data[f];
                        double g = data[f + 1];
                        double b = data[f + 2];
                        int m = moff + icol;
                        int z = hmap[m];
                        int v = vmap[m];
                        rh += Js.coerceToInt(z * r);
                        bh += Js.coerceToInt(z * g);
                        gh += Js.coerceToInt(z * b);
                        rv += Js.coerceToInt(v * r);
                        gv += Js.coerceToInt(v * g);
                        bv += Js.coerceToInt(v * b);
                    }
                }
                buff[p] = Js.coerceToInt(Math.sqrt(rh * rh + rv * rv) / 1.8);
                buff[p + 1] = Js.coerceToInt((Math.sqrt(gh * gh + gv * gv) / 1.8));
                buff[p + 2] = Js.coerceToInt((Math.sqrt(bh * bh + bv * bv) / 1.8));
                buff[p + 3] = data[p + 3];
            }
        }
    }
}
