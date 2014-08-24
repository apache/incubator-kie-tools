/*
   Copyright (c) 2014 Ahome' Innovation Technologies. All rights reserved.

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
import com.ait.lienzo.client.core.types.ImageData;
import com.google.gwt.canvas.dom.client.CanvasPixelArray;
import com.google.gwt.core.client.JavaScriptObject;

public class StackBlurImageDataFilter extends AbstractImageDataFilter<StackBlurImageDataFilter>
{
    public int m_radius;

    public StackBlurImageDataFilter(int radius)
    {
        setRadius(radius);
    }

    public StackBlurImageDataFilter()
    {
        setRadius(1);
    }

    public final int getRadius()
    {
        return m_radius;
    }

    public final StackBlurImageDataFilter setRadius(int radius)
    {
        m_radius = Math.max(Math.min(radius, 180), 1);

        return this;
    }

    @Override
    public ImageData filter(ImageData source, boolean copy)
    {
        if (null == source)
        {
            return null;
        }
        if (copy)
        {
            source = source.copy();
        }
        if (false == isActive())
        {
            return source;
        }
        final CanvasPixelArray data = source.getData();

        if (null == data)
        {
            return source;
        }
        filter_(data, source.getWidth(), source.getHeight(), getRadius());

        return source;
    }

    private final native void filter_(JavaScriptObject data, int width, int height, int radius)
    /*-{
        function BlurStack() {
            this.r = 0;
            this.g = 0;
            this.b = 0;
            this.a = 0;
            this.next = null;
        }
        var mul_table = [ 512, 512, 456, 512, 328, 456, 335, 512, 405, 328,
                271, 456, 388, 335, 292, 512, 454, 405, 364, 328, 298, 271,
                496, 456, 420, 388, 360, 335, 312, 292, 273, 512, 482, 454,
                428, 405, 383, 364, 345, 328, 312, 298, 284, 271, 259, 496,
                475, 456, 437, 420, 404, 388, 374, 360, 347, 335, 323, 312,
                302, 292, 282, 273, 265, 512, 497, 482, 468, 454, 441, 428,
                417, 405, 394, 383, 373, 364, 354, 345, 337, 328, 320, 312,
                305, 298, 291, 284, 278, 271, 265, 259, 507, 496, 485, 475,
                465, 456, 446, 437, 428, 420, 412, 404, 396, 388, 381, 374,
                367, 360, 354, 347, 341, 335, 329, 323, 318, 312, 307, 302,
                297, 292, 287, 282, 278, 273, 269, 265, 261, 512, 505, 497,
                489, 482, 475, 468, 461, 454, 447, 441, 435, 428, 422, 417,
                411, 405, 399, 394, 389, 383, 378, 373, 368, 364, 359, 354,
                350, 345, 341, 337, 332, 328, 324, 320, 316, 312, 309, 305,
                301, 298, 294, 291, 287, 284, 281, 278, 274, 271, 268, 265,
                262, 259, 257, 507, 501, 496, 491, 485, 480, 475, 470, 465,
                460, 456, 451, 446, 442, 437, 433, 428, 424, 420, 416, 412,
                408, 404, 400, 396, 392, 388, 385, 381, 377, 374, 370, 367,
                363, 360, 357, 354, 350, 347, 344, 341, 338, 335, 332, 329,
                326, 323, 320, 318, 315, 312, 310, 307, 304, 302, 299, 297,
                294, 292, 289, 287, 285, 282, 280, 278, 275, 273, 271, 269,
                267, 265, 263, 261, 259 ];

        var shg_table = [ 9, 11, 12, 13, 13, 14, 14, 15, 15, 15, 15, 16, 16,
                16, 16, 17, 17, 17, 17, 17, 17, 17, 18, 18, 18, 18, 18, 18, 18,
                18, 18, 19, 19, 19, 19, 19, 19, 19, 19, 19, 19, 19, 19, 19, 19,
                20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20,
                20, 20, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21,
                21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 22, 22, 22,
                22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22,
                22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22,
                22, 22, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23,
                23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23,
                23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23,
                23, 23, 23, 23, 23, 23, 23, 23, 24, 24, 24, 24, 24, 24, 24, 24,
                24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24,
                24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24,
                24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24,
                24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24,
                24, 24 ];

        var x, y, i, p, yp, yi, yw, r_sum, g_sum, b_sum, r_out_sum, g_out_sum, b_out_sum, r_in_sum, g_in_sum, b_in_sum, pr, pg, pb, rbs;

        var div = radius + radius + 1;
        var widthMinus1 = width - 1;
        var heightMinus1 = height - 1;
        var radiusPlus1 = radius + 1;
        var sumFactor = radiusPlus1 * (radiusPlus1 + 1) / 2;

        var stackStart = new BlurStack();
        var stack = stackStart;
        for (i = 1; i < div; i++) {
            stack = stack.next = new BlurStack();
            if (i == radiusPlus1)
                var stackEnd = stack;
        }
        stack.next = stackStart;
        var stackIn = null;
        var stackOut = null;

        yw = yi = 0;

        var mul_sum = mul_table[radius];
        var shg_sum = shg_table[radius];

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

                p = (x + (((p = y + radiusPlus1) < heightMinus1 ? p
                        : heightMinus1) * width)) << 2;

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
    }-*/;

    @Override
    public IFactory<StackBlurImageDataFilter> getFactory()
    {
        // TODO Auto-generated method stub
        return null;
    }
}
