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

public abstract class AbstractBaseImageDataFilter<T extends AbstractBaseImageDataFilter<T>> implements IBaseImageDataFilter<T>
{
    private String  m_name   = null;

    private boolean m_native = true;

    private boolean m_active = true;

    @Override
    public final boolean isNative()
    {
        return m_native;
    }

    @SuppressWarnings("unchecked")
    @Override
    public final T setNative(boolean isnative)
    {
        m_native = isnative;

        return (T) this;
    }

    @Override
    public final int getLength(ImageData source)
    {
        return ((source.getWidth() * source.getHeight()) * PIXEL_SZ);
    }

    @Override
    public boolean isTransforming()
    {
        return false;
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
