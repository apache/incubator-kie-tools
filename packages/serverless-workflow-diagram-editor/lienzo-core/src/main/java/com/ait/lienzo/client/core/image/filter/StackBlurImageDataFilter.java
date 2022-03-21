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

public class StackBlurImageDataFilter extends AbstractValueImageDataFilter<StackBlurImageDataFilter> {

    public StackBlurImageDataFilter(int value) {
        super(ImageFilterType.StackBlurImageDataFilterType, value);
    }

    public StackBlurImageDataFilter() {
        super(ImageFilterType.StackBlurImageDataFilterType, 1);
    }

    protected StackBlurImageDataFilter(Object node) {
        super(ImageFilterType.StackBlurImageDataFilterType, node);
    }

    @Override
    public double getMinValue() {
        return 0;
    }

    @Override
    public double getMaxValue() {
        return 180;
    }

    @Override
    public double getRefValue() {
        return 1;
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
        filter_(data, source.width, source.height, (int) getValue(), FilterCommonOps);

        return source;
    }

    public static class BlurStack {

        public double r = 0;
        public double g = 0;
        public double b = 0;
        public double a = 0;
        public BlurStack next = null;
    }

    private final void filter_(Uint8ClampedArray dataArray, int width, int height, int radius, ImageDataFilterCommonOps fops) {
        int[] data = Js.uncheckedCast(dataArray);

        int[] mul_table = fops.mul_table;

        int[] shg_table = fops.shg_table;

        int x, y, i, p, yp, yi, yw, r_sum, g_sum, b_sum, r_in_sum, g_in_sum, b_in_sum, rbs;
        double pr, pg, pb, r_out_sum, g_out_sum, b_out_sum;

        int div = radius + radius + 1;
        int widthMinus1 = width - 1;
        int heightMinus1 = height - 1;
        int radiusPlus1 = radius + 1;
        double sumFactor = radiusPlus1 * (radiusPlus1 + 1) / 2;

        BlurStack stackStart = new BlurStack();

        BlurStack stack = stackStart;
        BlurStack stackEnd = null;
        for (i = 1; i < div; i++) {
            stack = stack.next = new BlurStack();
            if (i == radiusPlus1) {
                stackEnd = stack;
            }
        }
        stack.next = stackStart;
        BlurStack stackIn = null;
        BlurStack stackOut = null;

        yw = yi = 0;

        int mul_sum = mul_table[radius];
        int shg_sum = shg_table[radius];

        for (y = 0; y < height; y++) {
            r_in_sum = g_in_sum = b_in_sum = r_sum = g_sum = b_sum = 0;

            r_out_sum = radiusPlus1 * (pr = data[yi]);
            g_out_sum = radiusPlus1 * (pg = data[yi + 1]);
            b_out_sum = radiusPlus1 * (pb = data[yi + 2]);

            r_sum += sumFactor * pr;
            g_sum += sumFactor * pg;
            b_sum += sumFactor * pb;

            stack = stackStart;

            for (i = 0; i < radiusPlus1; i++) {
                stack.r = pr;
                stack.g = pg;
                stack.b = pb;
                stack = stack.next;
            }
            for (i = 1; i < radiusPlus1; i++) {
                p = yi + ((widthMinus1 < i ? widthMinus1 : i) << 2);
                r_sum += (stack.r = (pr = data[p])) * (rbs = radiusPlus1 - i);
                g_sum += (stack.g = (pg = data[p + 1])) * rbs;
                b_sum += (stack.b = (pb = data[p + 2])) * rbs;
                r_in_sum += pr;
                g_in_sum += pg;
                b_in_sum += pb;
                stack = stack.next;
            }
            stackIn = stackStart;
            stackOut = stackEnd;
            for (x = 0; x < width; x++) {
                data[yi] = (r_sum * mul_sum) >> shg_sum;
                data[yi + 1] = (g_sum * mul_sum) >> shg_sum;
                data[yi + 2] = (b_sum * mul_sum) >> shg_sum;
                r_sum -= r_out_sum;
                g_sum -= g_out_sum;
                b_sum -= b_out_sum;
                r_out_sum -= stackIn.r;
                g_out_sum -= stackIn.g;
                b_out_sum -= stackIn.b;

                p = (yw + ((p = x + radius + 1) < widthMinus1 ? p : widthMinus1)) << 2;

                r_in_sum += (stackIn.r = data[p]);
                g_in_sum += (stackIn.g = data[p + 1]);
                b_in_sum += (stackIn.b = data[p + 2]);

                r_sum += r_in_sum;
                g_sum += g_in_sum;
                b_sum += b_in_sum;

                stackIn = stackIn.next;

                r_out_sum += (pr = stackOut.r);
                g_out_sum += (pg = stackOut.g);
                b_out_sum += (pb = stackOut.b);

                r_in_sum -= pr;
                g_in_sum -= pg;
                b_in_sum -= pb;

                stackOut = stackOut.next;

                yi += 4;
            }
            yw += width;
        }
        for (x = 0; x < width; x++) {
            g_in_sum = b_in_sum = r_in_sum = g_sum = b_sum = r_sum = 0;

            yi = x << 2;
            r_out_sum = radiusPlus1 * (pr = data[yi]);
            g_out_sum = radiusPlus1 * (pg = data[yi + 1]);
            b_out_sum = radiusPlus1 * (pb = data[yi + 2]);

            r_sum += sumFactor * pr;
            g_sum += sumFactor * pg;
            b_sum += sumFactor * pb;

            stack = stackStart;

            for (i = 0; i < radiusPlus1; i++) {
                stack.r = pr;
                stack.g = pg;
                stack.b = pb;
                stack = stack.next;
            }
            yp = width;

            for (i = 1; i <= radius; i++) {
                yi = (yp + x) << 2;

                r_sum += (stack.r = (pr = data[yi])) * (rbs = radiusPlus1 - i);
                g_sum += (stack.g = (pg = data[yi + 1])) * rbs;
                b_sum += (stack.b = (pb = data[yi + 2])) * rbs;

                r_in_sum += pr;
                g_in_sum += pg;
                b_in_sum += pb;

                stack = stack.next;

                if (i < heightMinus1) {
                    yp += width;
                }
            }
            yi = x;
            stackIn = stackStart;
            stackOut = stackEnd;
            for (y = 0; y < height; y++) {
                p = yi << 2;
                data[p] = (r_sum * mul_sum) >> shg_sum;
                data[p + 1] = (g_sum * mul_sum) >> shg_sum;
                data[p + 2] = (b_sum * mul_sum) >> shg_sum;

                r_sum -= r_out_sum;
                g_sum -= g_out_sum;
                b_sum -= b_out_sum;

                r_out_sum -= stackIn.r;
                g_out_sum -= stackIn.g;
                b_out_sum -= stackIn.b;

                p = (x + (((p = y + radiusPlus1) < heightMinus1 ? p : heightMinus1) * width)) << 2;

                r_sum += (r_in_sum += (stackIn.r = data[p]));
                g_sum += (g_in_sum += (stackIn.g = data[p + 1]));
                b_sum += (b_in_sum += (stackIn.b = data[p + 2]));

                stackIn = stackIn.next;

                r_out_sum += (pr = stackOut.r);
                g_out_sum += (pg = stackOut.g);
                b_out_sum += (pb = stackOut.b);

                r_in_sum -= pr;
                g_in_sum -= pg;
                b_in_sum -= pb;

                stackOut = stackOut.next;

                yi += width;
            }
        }
    }
}
