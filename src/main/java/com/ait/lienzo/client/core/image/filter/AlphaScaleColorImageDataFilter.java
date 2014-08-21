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

public class AlphaScaleColorImageDataFilter extends AbstractBaseRGBImageDataFilter<AlphaScaleColorImageDataFilter>
{
    private boolean m_invert = false;

    public AlphaScaleColorImageDataFilter(int r, int g, int b)
    {
        super(r, g, b);
    }

    public AlphaScaleColorImageDataFilter(int r, int g, int b, boolean invert)
    {
        super(r, g, b);

        setInverted(invert);
    }

    public AlphaScaleColorImageDataFilter(IColor color)
    {
        super(color);
    }

    public AlphaScaleColorImageDataFilter(IColor color, boolean invert)
    {
        super(color);

        setInverted(invert);
    }

    public AlphaScaleColorImageDataFilter(String color)
    {
        super(color);
    }

    public AlphaScaleColorImageDataFilter(String color, boolean invert)
    {
        super(color);

        setInverted(invert);
    }

    public AlphaScaleColorImageDataFilter setInverted(boolean invert)
    {
        m_invert = invert;

        return this;
    }

    public boolean isInverted()
    {
        return m_invert;
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
        filter_(data, FilterCommonOps.getLength(source), getR(), getG(), getB(), isInverted());

        return source;
    }

    private final native void filter_(JavaScriptObject pixa, int length, int r, int g, int b, boolean invert)
    /*-{
        var data = pixa;

        function luminocity(rv, gv, bv) {
            return (rv * 0.21) + (gv * 0.72) + (bv * 0.07);
        };
        for (var i = 0; i < length; i += 4) {

            var v = luminocity(data[i + 0], data[i + 1], data[i + 2]);

            data[i + 0] = r;

            data[i + 1] = g;

            data[i + 2] = b;
            
            if (true == invert)
            {
                data[i + 3] = (v + 0.5) | 0;
            }
            else
            {
                data[i + 3] = 255 - ((v + 0.5) | 0);
            }
        }
    }-*/;
}
