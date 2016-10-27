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

import java.util.Collection;
import java.util.Collections;
import java.util.List;

class ListRegistry<T> implements DynamicRegistry<T> {

    private final KeyProvider<T> keyProvider;
    private final List<T> items;

    public ListRegistry( final KeyProvider<T> keyProvider, final List<T> items ) {
        this.keyProvider = keyProvider;
        this.items = items;
    }

    public void add( final int pos, final T item ) {
        items.add( pos, item );
    }

    @Override
    public void register( final T item ) {
        items.add( item );
    }

    public boolean remove( final T item ) {
        return items.remove( item );
    }

    @Override
    public boolean contains( final T item ) {
        return items.contains( item );
    }

    public Collection<T> getItems() {
        return Collections.unmodifiableList( items );
    }

    public T getItemByKey( final String id ) {
        if ( null != id ) {
            for ( final T item : items ) {
                final String itemId = getItemKey( item );
                if ( id.equals( itemId ) ) {
                    return item;
                }
            }
        }
        return null;
    }

    public void clear() {
        items.clear();
    }

    public int indexOf( final T item ) {
        return items.indexOf( item );
    }

    private String getItemKey( final T item ) {
        return keyProvider.getKey( item );
    }

}
