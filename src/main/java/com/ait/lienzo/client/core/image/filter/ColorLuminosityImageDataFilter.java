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

import com.ait.lienzo.client.core.types.ImageData;
import com.ait.lienzo.shared.core.types.IColor;
import com.google.gwt.canvas.dom.client.CanvasPixelArray;
import com.google.gwt.core.client.JavaScriptObject;

/**
 * A class that allows for easy creation of a Color Luminosity based Image Filter.
 */
public class ColorLuminosityImageDataFilter extends AbstractBaseRGBImageDataFilter<ColorLuminosityImageDataFilter>
{
    public ColorLuminosityImageDataFilter(int r, int g, int b)
    {
        super(r, g, b);
    }

    public ColorLuminosityImageDataFilter(IColor color)
    {
        super(color);
    }

    public ColorLuminosityImageDataFilter(String color)
    {
        super(color);
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
        final int length = getLength(source);

        final CanvasPixelArray data = source.getData();

        if (null == data)
        {
            return source;
        }
        if (isNative())
        {
            filter0(data, length, getR(), getG(), getB());
        }
        else
        {
            for (int i = 0; i < length; i += PIXEL_SZ)
            {
                double m = (((0.21 * data.get(i + R_OFFSET)) + (0.72 * data.get(i + G_OFFSET)) + (0.07 * data.get(i + B_OFFSET))) / 255.0);

                data.set(i + R_OFFSET, (int) ((getR() * m) + 0.5));

                data.set(i + G_OFFSET, (int) ((getG() * m) + 0.5));

                data.set(i + B_OFFSET, (int) ((getB() * m) + 0.5));
            }
        }
        return source;
    }

    private final native void filter0(JavaScriptObject pixa, int length, int r, int g, int b)
    /*-{
		var data = pixa;

		function luminocity(rv, gv, bv) {
			return (rv * 0.21) + (gv * 0.72) + (bv * 0.07);
		}
		for (var i = 0; i < length; i += 4) {

			var v = (luminocity(data[i + 0], data[i + 1], data[i + 2]) / 255.0);

			data[i + 0] = ((r * v) + 0.5) | 0;

			data[i + 1] = ((g * v) + 0.5) | 0;

			data[i + 2] = ((b * v) + 0.5) | 0;
		}
    }-*/;
}
