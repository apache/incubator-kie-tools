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

import java.util.Collection;

import com.ait.lienzo.client.core.types.ImageDataUtil;
import com.ait.lienzo.shared.core.types.ImageFilterType;
import com.ait.lienzo.tools.client.collection.NFastArrayList;
import elemental2.dom.ImageData;

public class ImageDataFilterChain extends AbstractImageDataFilter<ImageDataFilterChain> implements ImageDataFilterable<ImageDataFilterChain> {

    private NFastArrayList<ImageDataFilter<?>> m_filters = new NFastArrayList<>();

    public ImageDataFilterChain() {
        super(ImageFilterType.ImageDataFilterChainType);
    }

    public ImageDataFilterChain(ImageDataFilter<?>... filters) {
        super(ImageFilterType.ImageDataFilterChainType);

        addFilters(filters);
    }

    protected ImageDataFilterChain(Object node) {
        super(ImageFilterType.ImageDataFilterChainType, node);
    }

    public int size() {
        return m_filters.size();
    }

    @Override
    public ImageDataFilterChain clearFilters() {
        m_filters.clear();

        return this;
    }

    @Override
    public boolean isTransforming() {
        if (isActive()) {
            int size = size();

            for (int i = 0; i < size; i++) {
                ImageDataFilter<?> filter = m_filters.get(i);

                if ((null != filter) && (filter.isTransforming()) && (filter.isActive())) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public ImageData filter(ImageData source, boolean copy) {
        if (null == source) {
            return null;
        }
        if (!isActive()) {
            return source;
        }
        if (copy) {
            source = ImageDataUtil.copy(source);
        }
        int size = size();

        for (int i = 0; i < size; i++) {
            ImageDataFilter<?> filter = m_filters.get(i);

            if ((null != filter) && (filter.isActive())) {
                ImageData imdata = filter.filter(source, false);

                if (null != imdata) {
                    source = imdata;
                }
            }
        }
        return source;
    }

    private final void add(ImageDataFilter<?> filter) {
        if (null != filter) {
            if (!m_filters.contains(filter)) {
                m_filters.add(filter);
            }
        }
    }

    @Override
    public ImageDataFilterChain addFilters(ImageDataFilter<?>... filters) {
        if (null != filters) {
            for (int i = 0; i < filters.length; i++) {
                add(filters[i]);
            }
        }
        return this;
    }

    @Override
    public ImageDataFilterChain setFilters(ImageDataFilter<?>... filters) {
        clearFilters();

        if (null != filters) {
            for (int i = 0; i < filters.length; i++) {
                add(filters[i]);
            }
        }
        return this;
    }

    private final void remove(ImageDataFilter<?> filter) {
        if (null != filter) {
            m_filters.remove(filter);
        }
    }

    @Override
    public ImageDataFilterChain removeFilters(ImageDataFilter<?>... filters) {
        if (null != filters) {
            for (int i = 0; i < filters.length; i++) {
                remove(filters[i]);
            }
        }
        return this;
    }

    @Override
    public ImageDataFilterChain setFiltersActive(boolean active) {
        setActive(active);

        return this;
    }

    @Override
    public boolean areFiltersActive() {
        return isActive();
    }

    @Override
    public ImageDataFilterChain setFilters(Iterable<ImageDataFilter<?>> filters) {
        clearFilters();

        for (ImageDataFilter<?> filter : filters) {
            add(filter);
        }
        return this;
    }

    @Override
    public ImageDataFilterChain addFilters(Iterable<ImageDataFilter<?>> filters) {
        for (ImageDataFilter<?> filter : filters) {
            add(filter);
        }
        return this;
    }

    @Override
    public ImageDataFilterChain removeFilters(Iterable<ImageDataFilter<?>> filters) {
        for (ImageDataFilter<?> filter : filters) {
            remove(filter);
        }
        return this;
    }

    @Override
    public boolean isActive() {
        if ((super.isActive()) && (m_filters.size() > 0)) {
            for (int i = 0; i < m_filters.size(); i++) {
                ImageDataFilter<?> filter = m_filters.get(i);

                if ((null != filter) && (filter.isActive())) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public Collection<ImageDataFilter<?>> getFilters() {
        return m_filters.toList();
    }
}
