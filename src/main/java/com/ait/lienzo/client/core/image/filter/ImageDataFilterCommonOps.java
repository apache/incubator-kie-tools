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

import com.ait.lienzo.client.core.image.filter.ImageDataFilter.FilterConvolveMatrix;
import com.ait.lienzo.client.core.image.filter.ImageDataFilter.FilterTableArray;
import com.ait.lienzo.client.core.image.filter.ImageDataFilter.FilterTransformFunction;

import elemental2.core.Uint8ClampedArray;
import elemental2.dom.ImageData;
import jsinterop.base.Js;

/**
 * this class has lots of "wierd' coercion, this is mostly in an attempt to preserve
 * the original logic of the JS original code. Until it can be correctly audited and improved.
 */
public class ImageDataFilterCommonOps
{
        int[] mul_table = new int[] { 512, 512, 456, 512, 328, 456, 335, 512, 405, 328,
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
        267, 265, 263, 261, 259 };

        int[] shg_table = new int[] {  9, 11, 12, 13, 13, 14, 14, 15, 15, 15, 15, 16, 16,
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
        24, 24 };

    public static final ImageDataFilterCommonOps make()
    {
        ImageDataFilterCommonOps self = new ImageDataFilterCommonOps();

        return self;
    }

    protected ImageDataFilterCommonOps()
    {
    }

     public int[] HSVtoRGB(int h, int s, int v) {
         double r=0, g=0, b=0;
         int i = (int) Math.floor(h * 6);
         double f = h * 6 - i;
         double p = v * (1 - s);
         double q = v * (1 - f * s);
         double t = v * (1 - (1 - f) * s);
         switch(i % 6) {
             case 0:
                 r = v;
                 g = t;
                 b = p;
                 break;
             case 1:
                 r = q;
                 g = v;
                 b = p;
                 break;
             case 2:
                 r = p;
                 g = v;
                 b = t;
                 break;
             case 3:
                 r = p;
                 g = q;
                 b = v;
                 break;
             case 4:
                 r = t;
                 g = p;
                 b = v;
                 break;
             case 5:
                 r = v;
                 g = p;
                 b = q;
                 break;
        }
        return new int[] {Js.coerceToInt(r * 255), Js.coerceToInt(g * 255), Js.coerceToInt(b * 255)};
    }

     public int[] RGBtoHSV(int r, int g, int b) {
         r = r/255;
         g = g/255;
         b = b/255;
         double max = Math.max(r, Math.max(g, b));
         double min = Math.min(r, Math.max(g, b));
         double h = 0, s = 0, v = max;
         double d = max - min;
         s = max == 0 ? 0 : d / max;
         if(max == min)
         {
             h = 0;
         }
         else
         {
             if (max==r)
             {
                 h = (g - b) / d + (g < b ? 6 : 0);
             }
             else if(max==g)
             {
                 h = (b - r) / d + 2;
             }
             else if(max==b)
             {
                    h = (r - g) / d + 4;
             }
            h /= 6;
        }
        return new int[] {Js.coerceToInt(h), Js.coerceToInt(s), Js.coerceToInt(v)};
     }

     // MDP set correct parameters....
     public void filterTable(Uint8ClampedArray dataArray, FilterTableArray tableArray, int w, int h) {
         //int[] t1 = Uint8ClampedArray.ConstructorLengthUnionType.of(data).asIntArray();
         int[] data = Js.uncheckedCast(dataArray);
         // It's dirty, but should be no difference in JS from int[] and Integer[]
         int[] table = Js.uncheckedCast(tableArray);

        int length = w * h * 4;
        for(int i = 0; i < length; i += 4) {
            data[i] = table[data[i]];
            data[i + 1] = table[ data[ i + 1]];
            data[i + 2] = table[ data[ i + 2]];
        }
     }

     public void filterLuminosity(Uint8ClampedArray dataArray, int length) {

         int[] data = Js.uncheckedCast(dataArray); //Uint8ClampedArray.ConstructorLengthUnionType.of(dataArray).asIntArray();
         for (int j = 0; j < length; j += 4) {
             int v = Js.coerceToInt((((data[j] * 0.21) + (data[j + 1] * 0.72) + (data[j + 2] * 0.07)) + 0.5));
             data[j] = data[j + 1] = data[j + 2] = v;
         }
     }

     public int[] getPixel(Uint8ClampedArray dataArray, double x, double y, double w, double h) {
         //int[] data = Uint8ClampedArray.ConstructorLengthUnionType.of(dataArray).asIntArray();
         int[] data = Js.uncheckedCast(dataArray);
         int[] ret;
         double p = (y * w + x) * 4;
         if ((x < 0) || (x >= w) || (y < 0) || (y >= h)) {
             double c = ((clamp(y, 0, h - 1) * w) + clamp(x, 0, w - 1)) * 4;
             ret = new int[] {data[(int)c], data[(int)c + 1],
                              data[(int)c + 2], data[(int)c + 3]};
         }
         else
         {
             ret = new int[] {data[(int)p], data[(int)p + 1],
                              data[(int)p + 2], data[(int)p + 3]};
         }

        return ret;
     };

     public double clamp(double val, double min, double max) {
         return (val < min) ? min : (val > max) ? max : val;
     }

    public void filterTransform(Uint8ClampedArray dataArray, Uint8ClampedArray buffArray, FilterTransformFunction transformer, int w, int h) {
//        int[] data = Uint8ClampedArray.ConstructorLengthUnionType.of(dataArray).asIntArray();
//        int[] buff = Uint8ClampedArray.ConstructorLengthUnionType.of(buffArray).asIntArray();
        int[] data = Js.uncheckedCast(dataArray);
        int[] buff = Js.uncheckedCast(buffArray);

        int[] xfrm = new int[]{};
        for(int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++)
            {
                int p = (y * w + x) * 4;
                transformer.transform(x, y, xfrm);
                double srcx = Math.floor(xfrm[0]);
                double srcy = Math.floor(xfrm[1]);
                double xwht = xfrm[0] - srcx;
                double ywht = xfrm[1] - srcy;
                int[] nw, ne, sw, se;
                if(srcx >= 0 && srcx < w - 1 && srcy >= 0 && srcy < h - 1) {
                    int i = (int) (w * srcy + srcx) * 4;
                    int s = i + w * 4;
                    int e = i + (w + 1) * 4;
                    nw = new int[] {data[  i  ], data[i + 1], data[i + 2], data[i + 3]};
                    ne = new int[] {data[i + 4], data[i + 5], data[i + 6], data[i + 7]};
                    sw = new int[] {data[  s  ], data[s + 1], data[s + 2], data[s + 3]};
                    se = new int[] {data[  e  ], data[e + 1], data[e + 2], data[e + 3]};
                } else {
                    nw = getPixel(dataArray, srcx + 0, srcy + 0, w, h);
                    ne = getPixel(dataArray, srcx + 1, srcy + 0, w, h);
                    sw = getPixel(dataArray, srcx + 0, srcy + 1, w, h);
                    se = getPixel(dataArray, srcx + 1, srcy + 1, w, h);
                }
                int[] rgba = bilinearInterpolate(xwht, ywht, nw, ne, sw, se);
                buff[  p  ] = rgba[0];
                buff[p + 1] = rgba[1];
                buff[p + 2] = rgba[2];
                buff[p + 3] = rgba[3];
            }
        }
    }

    public void filterConvolve(Uint8ClampedArray dataArray, Uint8ClampedArray buffArray,
                               FilterConvolveMatrix matrix, double w, double h) {
        int[] data = Js.uncheckedCast(dataArray); //Uint8ClampedArray dataArray
        int[] buff = Js.uncheckedCast(buffArray);

        double rows, cols;
        rows = cols = Math.sqrt(matrix.getLength());
        int row2 = Js.coerceToInt(rows / 2) ;
        int col2 = Js.coerceToInt(cols / 2) ;
        for(int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                double p = (y * w + x) * 4;
                double r = 0, g = 0, b = 0;
                for(int row = -row2; row <= row2; row++) {
                    double iy = y + row;
                    double ioff;
                    if ((0 <= iy) && (iy < h)) {
                        ioff = iy * w;
                    } else {
                        ioff = y * w;
                    }
                    double moff = cols * (row + row2) + col2;
                    for (int col = -col2; col <= col2; col++) {
                        double f = matrix.getAt((int) moff + col);
                        if (f != 0) {
                            int ix = x + col;
                            if (!((0 <= ix) && (ix < w))) {
                                ix = x;
                            }
                            double ipix = (ioff + ix) * 4;
                            r += f * data[(int) ipix ];
                            g += f * data[(int)ipix + 1];
                            b += f * data[(int)ipix + 2];
                        }
                    }
                }
                buff[ (int) p  ] = (int) Js.coerceToInt(r + 0.5);
                buff[ (int) p + 1] =  (int) Js.coerceToInt(g + 0.5);
                buff[ (int) p + 2] =  (int) Js.coerceToInt(b + 0.5);
                buff[ (int) p + 3] =  (int) data[(int) p + 3];
            }
        }
    }

    public double[] mixColors(double t, double[] rgb1, double[] rgb2) {
        double r = this.linearInterpolate(t, rgb1[0], rgb2[0]);
        double g = this.linearInterpolate(t, rgb1[1], rgb2[1]);
        double b = this.linearInterpolate(t, rgb1[2], rgb2[2]);
        double a = this.linearInterpolate(t, rgb1[3], rgb2[3]);
        return new double[] {r, g, b, a};
    }

    public double luminocity(double r, double g, double b) {
        return (r * 0.21) + (g * 0.72) + (b * 0.07);
    };

    public boolean hasAlphaChannel(Uint8ClampedArray dataArray, int length) {
        //int[] data = Uint8ClampedArray.ConstructorLengthUnionType.of(dataArray).asIntArray();
        int[] data = Js.uncheckedCast(dataArray);
        for (int j = 0; j < length; j += 4) {
            if (data[j+3] < 255) {
                return true;
            }
        }
        return false;
    }

    public static int[] bilinearInterpolate(double x, double y, int[] nw, int[] ne, int[] sw, int[] se) {
        double m0, m1;
        int r0 = nw[0];
        int g0 = nw[1];
        int b0 = nw[2];
        int a0 = nw[3];

        int r1 = ne[0];
        int g1 = ne[1];
        int b1 = ne[2];
        int a1 = ne[3];

        int r2 = sw[0];
        int g2 = sw[1];
        int b2 = sw[2];
        int a2 = sw[3];

        int r3 = se[0];
        int g3 = se[1];
        int b3 = se[2];
        int a3 = se[3];

        double cx = 1.0 - x;
        double cy = 1.0 - y;
        m0 = cx * a0 + x * a1;
        m1 = cx * a2 + x * a3;
        int a = Js.coerceToInt(cy * m0 + y * m1);
        m0 = cx * r0 + x * r1;
        m1 = cx * r2 + x * r3;
        int r = Js.coerceToInt(cy * m0 + y * m1);
        m0 = cx * g0 + x * g1;
        m1 = cx * g2 + x * g3;
        int g = Js.coerceToInt(cy * m0 + y * m1);
        m0 = cx * b0 + x * b1;
        m1 = cx * b2 + x * b3;
        int b = Js.coerceToInt(cy * m0 + y * m1);
        return new int[] {r, g, b, a};
    }


    public static double linearInterpolate(double t, double a, double b) {
        return a + t * (b - a);
    };

    public final int getLength(ImageData source)
    {
        return ((source.width * source.height) * 4);
    }

    public final void dofilterLuminosity(Uint8ClampedArray data, int length)
    {
        filterLuminosity(data, length);
    };

    public final void doFilterTable(Uint8ClampedArray data, FilterTableArray table, int w, int h)
    {
        filterTable(data, table, w, h);
    }

    public final void doFilterConvolve(Uint8ClampedArray data, Uint8ClampedArray buff, FilterConvolveMatrix matrix, int w, int h)
    {
        filterConvolve(data, buff, matrix, w, h);
    }

    public final void doFilterTransform(Uint8ClampedArray data, Uint8ClampedArray buff, FilterTransformFunction transform, int w, int h)
    {
        filterTransform(data, buff, transform, w, h);
    }
}
