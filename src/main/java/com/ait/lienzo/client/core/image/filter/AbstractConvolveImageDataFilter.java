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

public abstract class AbstractConvolveImageDataFilter<T extends AbstractConvolveImageDataFilter<T>> extends AbstractBaseImageDataFilter<T>
{
    private final FilterConvolveMatrix m_matrix = FilterConvolveMatrix.make();

    protected AbstractConvolveImageDataFilter()
    {
    }

    protected AbstractConvolveImageDataFilter(double... matrix)
    {
        setMatrix(matrix);
    }

    public final T setMatrix(double... matrix)
    {
        m_matrix.clear();

        for (int i = 0; i < matrix.length; i++)
        {
            m_matrix.push(matrix[i]);
        }
        return cast();
    }

    @Override
    public final boolean isTransforming()
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
        if (m_matrix.size() < 1)
        {
            return source;
        }
        FilterCommonOps.doFilterConvolve(data, m_matrix, source.getWidth(), source.getHeight());

        return source;
    }
}
