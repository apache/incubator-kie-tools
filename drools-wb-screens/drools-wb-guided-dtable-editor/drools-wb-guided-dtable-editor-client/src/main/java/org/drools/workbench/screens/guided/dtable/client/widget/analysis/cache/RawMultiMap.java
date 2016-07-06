/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.workbench.screens.guided.dtable.client.widget.analysis.cache;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

public abstract class RawMultiMap<Key extends Comparable, Value, MapType extends List<Value>> {

    protected final TreeMap<Key, MapType> map;

    public RawMultiMap() {
        this.map = new TreeMap();
    }

    protected RawMultiMap( final SortedMap<Key, MapType> map ) {
        this.map = new TreeMap<>( map );
    }

    public boolean isEmpty() {
        return map.isEmpty();
    }

    public boolean containsKey( final Key key ) {
        return map.containsKey( key );
    }

    public void put( final Key key,
                     final int index,
                     final Value value ) {
        resolveInnerList( key ).add( index,
                                     value );
    }

    public boolean put( final Key key,
                        final Value value ) {
        return resolveInnerList( key ).add( value );
    }

    private MapType resolveInnerList( final Key key ) {
        if ( map.containsKey( key ) ) {
            return map.get( key );
        } else {
            final MapType list = getNewSubMap();
            map.put( key,
                     list );
            return list;
        }
    }

    protected abstract MapType getNewSubMap();

    public void putAllValues( final Key key,
                              final Collection<Value> values ) {
        final MapType newSubMap = getNewSubMap();
        newSubMap.addAll( values );
        map.put( key,
                 newSubMap );
    }

    public boolean addAllValues( final Key key,
                                 final Collection<Value> values ) {
        if ( map.containsKey( key ) ) {
            return map.get( key ).addAll( values );
        } else {
            final MapType set = getNewSubMap();
            set.addAll( values );
            map.put( key, set );
            return true;
        }
    }

    public Collection<Value> remove( final Key key ) {
        return map.remove( key );
    }

    public int size() {
        return map.size();
    }

    public Set<Key> keySet() {
        return map.keySet();
    }

    public MapType get( final Key key ) {
        return map.get( key );
    }

    public void clear() {
        map.clear();
    }

    public Collection<Value> allValues() {
        final MapType allValues = getNewSubMap();

        for ( final Key key : keySet() ) {
            final MapType collection = get( key );
            if ( collection != null ) {
                allValues.addAll( collection );
            }
        }

        return allValues;
    }

    public void removeValue( final Key key,
                             final Value value ) {
        get( key ).remove( value );
    }

    public Key firstKey() {
        return map.firstKey();
    }

    public Key lastKey() {
        return map.lastKey();
    }
}
