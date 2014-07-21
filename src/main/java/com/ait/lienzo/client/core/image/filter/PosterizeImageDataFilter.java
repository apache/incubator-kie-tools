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
import com.google.gwt.canvas.dom.client.CanvasPixelArray;
import com.google.gwt.core.client.JavaScriptObject;

/**
 * A class that allows for easy creation of a Posterize Image Filter.
 */
public class PosterizeImageDataFilter extends AbstractBaseImageDataFilter<PosterizeImageDataFilter>
{
    private final int m_level;

    public PosterizeImageDataFilter(int level)
    {
        m_level = Math.max(Math.min(level, 256), 2);
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
        final double areas = 256.0 / m_level;

        final double valus = 256.0 / (m_level - 1);

        final int w = source.getWidth();

        final int h = source.getHeight();

        final int m = w * 4;

        filter_(data, areas, valus, w, h, m);

        return source;
    }

    private final native void filter_(JavaScriptObject pixa, double areas, double valus, int w, int h, int m)
    /*-{
    	var data = pixa;

    	var y = h;

    	do {
    		var offsety = (y - 1) * m;
    		var x = w;
    		do {
    			var offset = offsety + (x - 1) * 4;

    			var r = (valus * ((data[offset + 0] / areas))) | 0;
    			var g = (valus * ((data[offset + 1] / areas))) | 0;
    			var b = (valus * ((data[offset + 2] / areas))) | 0;

    			if (r > 255) {
    				r = 255;
    			}
    			if (g > 255) {
    				g = 255;
    			}
    			if (b > 255) {
    				b = 255;
    			}
    			data[offset + 0] = r;
    			data[offset + 1] = g;
    			data[offset + 2] = b;

    		} while (--x);
    	} while (--y);
    }-*/;
}
