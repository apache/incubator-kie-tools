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

import java.util.Collection;

public interface ImageDataFilterable<T extends ImageDataFilterable<T>>
{
    public T setFilters(ImageDataFilter<?> filter, ImageDataFilter<?>... filters);

    public T addFilters(ImageDataFilter<?> filter, ImageDataFilter<?>... filters);

    public T removeFilters(ImageDataFilter<?> filter, ImageDataFilter<?>... filters);

    public T setFilters(Iterable<ImageDataFilter<?>> filters);

    public T addFilters(Iterable<ImageDataFilter<?>> filters);

    public T removeFilters(Iterable<ImageDataFilter<?>> filters);

    public T clearFilters();

    public T setFiltersActive(boolean active);

    public boolean areFiltersActive();

    public Collection<ImageDataFilter<?>> getFilters();
}
