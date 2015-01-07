/*
   Copyright (c) 2014,2015 Ahome' Innovation Technologies. All rights reserved.

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

import com.ait.lienzo.client.core.image.filter.ImageDataFilter.FilterConvolveMatrix;
import com.ait.lienzo.client.core.image.filter.ImageDataFilter.FilterTableArray;
import com.ait.lienzo.client.core.image.filter.ImageDataFilter.FilterTransformFunction;
import com.ait.lienzo.client.core.types.ImageData;
import com.google.gwt.canvas.dom.client.CanvasPixelArray;
import com.google.gwt.core.client.JavaScriptObject;

public class ImageDataFilterCommonOps extends JavaScriptObject
{
    public static final ImageDataFilterCommonOps make()
    {
        ImageDataFilterCommonOps self = JavaScriptObject.createObject().cast();

        self.initialize();

        return self;
    }

    protected ImageDataFilterCommonOps()
    {
    }

    private native final void initialize()
    /*-{
        this.mul_table = [ 512, 512, 456, 512, 328, 456, 335, 512, 405, 328,
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
        this.shg_table = [ 9, 11, 12, 13, 13, 14, 14, 15, 15, 15, 15, 16, 16,
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
        this.HSVtoRGB = function(h, s, v) {
            var r, g, b;
            var i = Math.floor(h * 6);
            var f = h * 6 - i;
            var p = v * (1 - s);
            var q = v * (1 - f * s);
            var t = v * (1 - (1 - f) * s);
            switch(i % 6) {
                case 0: r = v, g = t, b = p; break;
                case 1: r = q, g = v, b = p; break;
                case 2: r = p, g = v, b = t; break;
                case 3: r = p, g = q, b = v; break;
                case 4: r = t, g = p, b = v; break;
                case 5: r = v, g = p, b = q; break;
            }
            return [r * 255, g * 255, b * 255];
        };
        this.RGBtoHSV = function(r, g, b) {
            r = r/255, g = g/255, b = b/255;
            var max = Math.max(r, g, b), min = Math.min(r, g, b);
            var h, s, v = max;
            var d = max - min;
            s = max == 0 ? 0 : d / max;
            if(max == min) {
                h = 0;
            } else {
                switch(max) {
                    case r: h = (g - b) / d + (g < b ? 6 : 0); break;
                    case g: h = (b - r) / d + 2; break;
                    case b: h = (r - g) / d + 4; break;
                }
                h /= 6;
            }
            return [h, s, v];
        };
        this.filterTable = function(data, table, w, h) {
            var length = w * h * 4;
            for(var i = 0; i < length; i += 4) {
                data[i + 0] = table[data[i + 0]];
                data[i + 1] = table[data[i + 1]];
                data[i + 2] = table[data[i + 2]];
            }
        };
        this.filterConvolve = function(data, buff, matrix, w, h) {
            var rows, cols;
            rows = cols = Math.sqrt(matrix.length);
            var row2 = (rows / 2) | 0;
            var col2 = (cols / 2) | 0;
            for(var y = 0; y < h; y++) {
                for (var x = 0; x < w; x++) {
                    var p = (y * w + x) * 4;
                    var r = 0, g = 0, b = 0;
                    for(var row = -row2; row <= row2; row++) {
                        var iy = y + row;
                        var ioff;
                        if ((0 <= iy) && (iy < h)) {
                            ioff = iy * w;
                        } else {
                            ioff = y * w;
                        }
                        var moff = cols * (row + row2) + col2;
                        for (var col = -col2; col <= col2; col++) {
                            var f = matrix[moff + col];
                            if (f != 0) {
                                var ix = x + col;
                                if (!((0 <= ix) && (ix < w))) {
                                        ix = x;
                                }
                                var ipix = (ioff + ix) * 4;
                                r += f * data[ipix + 0];
                                g += f * data[ipix + 1];
                                b += f * data[ipix + 2];
                            }
                        }
                    }
                    buff[p + 0] = (r + 0.5) | 0;
                    buff[p + 1] = (g + 0.5) | 0;
                    buff[p + 2] = (b + 0.5) | 0;
                    buff[p + 3] = data[p + 3];
                }
            }
        };
        this.linearInterpolate = function(t, a, b) {
            return a + t * (b - a);
        };
        this.bilinearInterpolate = function(x, y, nw, ne, sw, se) {
            var m0, m1;
            var r0 = nw[0]; var g0 = nw[1]; var b0 = nw[2]; var a0 = nw[3];
            var r1 = ne[0]; var g1 = ne[1]; var b1 = ne[2]; var a1 = ne[3];
            var r2 = sw[0]; var g2 = sw[1]; var b2 = sw[2]; var a2 = sw[3];
            var r3 = se[0]; var g3 = se[1]; var b3 = se[2]; var a3 = se[3];
            var cx = 1.0 - x; var cy = 1.0 - y;
            m0 = cx * a0 + x * a1;
            m1 = cx * a2 + x * a3;
            var a = cy * m0 + y * m1;
            m0 = cx * r0 + x * r1;
            m1 = cx * r2 + x * r3;
            var r = cy * m0 + y * m1;
            m0 = cx * g0 + x * g1;
            m1 = cx * g2 + x * g3;
            var g = cy * m0 + y * m1;
            m0 = cx * b0 + x * b1;
            m1 = cx * b2 + x * b3;
            var b =cy * m0 + y * m1;
            return [r, g, b, a];
        };
        this.mixColors = function(t, rgb1, rgb2) {
            var r = this.linearInterpolate(t, rgb1[0], rgb2[0]);
            var g = this.linearInterpolate(t, rgb1[1], rgb2[1]);
            var b = this.linearInterpolate(t, rgb1[2], rgb2[2]);
            var a = this.linearInterpolate(t, rgb1[3], rgb2[3]);
            return [r, g, b, a];
        };
        this.clamp = function(val, min, max) {
            return (val < min) ? min : (val > max) ? max : val;
        };
        this.luminocity = function(r, g, b) {
            return (r * 0.21) + (g * 0.72) + (b * 0.07);
        };
        this.getPixel = function(data, x, y, w, h) {
            var p = (y * w + x) * 4;
            if ((x < 0) || (x >= w) || (y < 0) || (y >= h)) {
                return [
                    data[((this.clamp(y, 0, h - 1) * w) + this.clamp(x, 0, w - 1)) * 4 + 0],
                    data[((this.clamp(y, 0, h - 1) * w) + this.clamp(x, 0, w - 1)) * 4 + 1],
                    data[((this.clamp(y, 0, h - 1) * w) + this.clamp(x, 0, w - 1)) * 4 + 2],
                    data[((this.clamp(y, 0, h - 1) * w) + this.clamp(x, 0, w - 1)) * 4 + 3]
                ];
            }
            return [data[p + 0], data[p + 1], data[p + 2], data[p + 3]]
        };
        this.filterTransform = function(data, buff, transform, w, h) {
            var xfrm = [];
            for(var y = 0; y < h; y++) {
                for (var x = 0; x < w; x++) {
                    var p = (y * w + x) * 4;
                    transform(x, y, xfrm);
                    var srcx = Math.floor(xfrm[0]);
                    var srcy = Math.floor(xfrm[1]);
                    var xwht = xfrm[0] - srcx;
                    var ywht = xfrm[1] - srcy;
                    var nw, ne, sw, se;
                    if(srcx >= 0 && srcx < w - 1 && srcy >= 0 && srcy < h - 1) {
                        var i = (w * srcy + srcx) * 4;
                        nw = [data[i + 0], data[i + 1], data[i + 2], data[i + 3]];
                        ne = [data[i + 4], data[i + 5], data[i + 6], data[i + 7]];
                        sw = [data[i + w * 4], data[i + w * 4 + 1], data[i + w * 4 + 2],data[i + w * 4 + 3]];
                        se = [data[i + (w + 1) *4], data[i + (w + 1) * 4 + 1], data[i + (w + 1) * 4 + 2], data[i + (w + 1) * 4 + 3]];
                    } else {
                        nw = this.getPixel(data, srcx + 0, srcy + 0, w, h);
                        ne = this.getPixel(data, srcx + 1, srcy + 0, w, h);
                        sw = this.getPixel(data, srcx + 0, srcy + 1, w, h);
                        se = this.getPixel(data, srcx + 1, srcy + 1, w, h);
                    }
                    var rgba = this.bilinearInterpolate(xwht, ywht, nw, ne, sw, se);
                    buff[p + 0] = rgba[0];
                    buff[p + 1] = rgba[1];
                    buff[p + 2] = rgba[2];
                    buff[p + 3] = rgba[3];
                }
            }
        };
    }-*/;

    public final int getLength(ImageData source)
    {
        return ((source.getWidth() * source.getHeight()) * 4);
    }

    public final native void doFilterTable(CanvasPixelArray data, FilterTableArray table, int w, int h)
    /*-{
        this.filterTable(data, table, w, h);
    }-*/;

    public final native void doFilterConvolve(CanvasPixelArray data, CanvasPixelArray buff, FilterConvolveMatrix matrix, int w, int h)
    /*-{
        this.filterConvolve(data, buff, matrix, w, h);
    }-*/;

    public final native void doFilterTransform(CanvasPixelArray data, CanvasPixelArray buff, FilterTransformFunction transform, int w, int h)
    /*-{
        this.filterTransform(data, buff, transform, w, h);
    }-*/;
}
