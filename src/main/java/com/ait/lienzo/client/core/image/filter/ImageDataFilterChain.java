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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import com.ait.lienzo.client.core.types.ImageData;

public class ImageDataFilterChain implements ImageDataFilter, ImageDataFilterable<ImageDataFilterChain>, Iterable<ImageDataFilter>
{
    private String                     m_name    = null;

    private boolean                    m_active  = true;

    private ArrayList<ImageDataFilter> m_filters = new ArrayList<ImageDataFilter>();

    public ImageDataFilterChain()
    {
    }

    public ImageDataFilterChain(ImageDataFilter filter, ImageDataFilter... filters)
    {
        addFilters(filter, filters);
    }

    public int size()
    {
        return m_filters.size();
    }

    @Override
    public ImageDataFilterChain clearFilters()
    {
        m_filters.clear();

        return this;
    }

    @Override
    public boolean isTransforming()
    {
        if (isActive())
        {
            int size = size();

            for (int i = 0; i < size; i++)
            {
                ImageDataFilter filter = m_filters.get(i);

                if ((null != filter) && (filter.isTransforming()) && (filter.isActive()))
                {
                    return true;
                }
            }
        }
        return false;
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
            return source;
        }
        if (copy)
        {
            source = source.copy();
        }
        int size = size();

        for (int i = 0; i < size; i++)
        {
            ImageDataFilter filter = m_filters.get(i);

            if ((null != filter) && (filter.isActive()))
            {
                ImageData imdata = filter.filter(source, false);

                if (null != imdata)
                {
                    source = imdata;
                }
            }
        }
        return source;
    }

    private final void add(ImageDataFilter filter)
    {
        if (null != filter)
        {
            if (false == m_filters.contains(filter))
            {
                m_filters.add(filter);
            }
        }
    }

    @Override
    public ImageDataFilterChain addFilters(ImageDataFilter filter, ImageDataFilter... filters)
    {
        add(filter);

        if (null != filters)
        {
            for (int i = 0; i < filters.length; i++)
            {
                add(filters[i]);
            }
        }
        return this;
    }

    @Override
    public ImageDataFilterChain setFilters(ImageDataFilter filter, ImageDataFilter... filters)
    {
        clearFilters();

        add(filter);

        if (null != filters)
        {
            for (int i = 0; i < filters.length; i++)
            {
                add(filters[i]);
            }
        }
        return this;
    }

    private final void remove(ImageDataFilter filter)
    {
        if (null != filter)
        {
            m_filters.remove(filter);
        }
    }

    @Override
    public ImageDataFilterChain removeFilters(ImageDataFilter filter, ImageDataFilter... filters)
    {
        remove(filter);

        if (null != filters)
        {
            for (int i = 0; i < filters.length; i++)
            {
                remove(filters[i]);
            }
        }
        return this;
    }

    @Override
    public ImageDataFilterChain setFiltersActive(boolean active)
    {
        setActive(active);

        return this;
    }

    @Override
    public boolean areFiltersActive()
    {
        return isActive();
    }

    @Override
    public ImageDataFilterChain setFilters(Iterable<ImageDataFilter> filters)
    {
        clearFilters();

        for (ImageDataFilter filter : filters)
        {
            add(filter);
        }
        return this;
    }

    @Override
    public ImageDataFilterChain addFilters(Iterable<ImageDataFilter> filters)
    {
        for (ImageDataFilter filter : filters)
        {
            add(filter);
        }
        return this;
    }

    @Override
    public ImageDataFilterChain removeFilters(Iterable<ImageDataFilter> filters)
    {
        for (ImageDataFilter filter : filters)
        {
            remove(filter);
        }
        return this;
    }

    @Override
    public boolean isActive()
    {
        if ((m_active) && (m_filters.size() > 0))
        {
            for (int i = 0; i < m_filters.size(); i++)
            {
                ImageDataFilter filter = m_filters.get(i);

                if ((null != filter) && (filter.isActive()))
                {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void setActive(boolean active)
    {
        m_active = active;
    }

    @Override
    public Collection<ImageDataFilter> getFilters()
    {
        return Collections.unmodifiableCollection(m_filters);
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

    @Override
    public Iterator<ImageDataFilter> iterator()
    {
        return getFilters().iterator();
    }
}
