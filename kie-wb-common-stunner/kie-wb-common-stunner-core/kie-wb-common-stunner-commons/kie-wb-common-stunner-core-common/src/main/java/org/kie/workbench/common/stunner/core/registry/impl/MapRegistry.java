/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.core.registry.impl;

import org.kie.workbench.common.stunner.core.registry.DynamicRegistry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

class MapRegistry<T> implements DynamicRegistry<T> {

    private final KeyProvider<T> keyProvider;
    private final java.util.Map<String, T> items;

    MapRegistry( final KeyProvider<T> keyProvider,
                 final Map<String, T> items ) {
        this.keyProvider = keyProvider;
        this.items = items;
    }

    @Override
    public void register( final T item ) {
        items.put( getItemId( item ), item );
    }

    public boolean remove( final T item ) {
        return null != items.remove( getItemId( item ) );
    }

    @Override
    public boolean contains( final T item ) {
        return items.containsValue( item );
    }

    public Collection<T> getItems() {
        return Collections.unmodifiableList( new ArrayList<T>( items.values() ) );
    }

    public void clear() {
        items.clear();
    }

    public T getItemByKey( final String key ) {
        return items.get( key );
    }

    private String getItemId( final T item ) {
        return keyProvider.getKey( item );
    }

}
