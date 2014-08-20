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

public abstract class AbstractConvolveImageDataFilter implements ImageDataFilter
{
    private String         m_name   = null;

    private boolean        m_active = true;

    private final double[] m_weights;      // changed back because sharpen filter stopped working, TODO: reinvestigate

    protected AbstractConvolveImageDataFilter(double[] weights)
    {
        m_weights = weights;
    }

    @Override
    public boolean isTransforming()
    {
        return true;
    }

    @Override
    public ImageData filter(ImageData source, boolean copy)
    {
        if (null == source)
        {
            return null;
        }
        if (false == isActive())
        {
            if (copy)
            {
                source = source.copy();
            }
            return source;
        }
        ImageData output = source.copy();

        final CanvasPixelArray srcd = source.getData();

        if (null == srcd)
        {
            return source;
        }
        final CanvasPixelArray dstd = output.getData();

        if (null == dstd)
        {
            return source;
        }
        int side = (int) (Math.sqrt(m_weights.length) + 0.5);

        int half = (int) (Math.floor(side / 2));

        int sw = source.getWidth();

        int sh = source.getHeight();

        int w = sw;

        int h = sh;

        for (int y = 0; y < h; y++)
        {
            for (int x = 0; x < w; x++)
            {
                int sy = y;

                int sx = x;

                int dsto = (y * w + x) * 4;

                int r = 0, g = 0, b = 0;

                for (int cy = 0; cy < side; cy++)
                {
                    for (int cx = 0; cx < side; cx++)
                    {
                        int scy = sy + cy - half;

                        int scx = sx + cx - half;

                        if (scy >= 0 && scy < sh && scx >= 0 && scx < sw)
                        {
                            int srco = (scy * sw + scx) * 4;

                            double wt = m_weights[cy * side + cx];

                            r += srcd.get(srco + R_OFFSET) * wt;

                            g += srcd.get(srco + G_OFFSET) * wt;

                            b += srcd.get(srco + B_OFFSET) * wt;
                        }
                    }
                }
                dstd.set(dsto + R_OFFSET, r);

                dstd.set(dsto + G_OFFSET, g);

                dstd.set(dsto + B_OFFSET, b);
            }
        }
        return output;
    }

    @Override
    public boolean isActive()
    {
        return m_active;
    }

    @Override
    public void setActive(boolean active)
    {
        m_active = active;
    }

    @Override
    public String getName()
    {
        if (null == m_name)
        {
            return getClass().getSimpleName();
        }
        return m_name;
    }

    @Override
    public void setName(String name)
    {
        m_name = name;
    }
}
