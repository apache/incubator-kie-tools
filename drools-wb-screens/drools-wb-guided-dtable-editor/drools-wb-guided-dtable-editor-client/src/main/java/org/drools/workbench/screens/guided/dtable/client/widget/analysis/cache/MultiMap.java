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

import java.util.ArrayList;
import java.util.SortedMap;

public class MultiMap<Key extends Comparable, Value>
        extends RawMultiMap<Key, Value, ArrayList<Value>> {

    public MultiMap() {
        super();
    }

    protected MultiMap( final SortedMap<Key, ArrayList<Value>> map ) {
        super( map );
    }

    @Override
    protected ArrayList<Value> getNewSubMap() {
        return new ArrayList<>();
    }

    public MultiMap<Key, Value> subMap( final Key fromKey,
                                        final Key toKey ) {
        return new MultiMap<>( map.subMap( fromKey,
                                           toKey ) );
    }

    public MultiMap<Key, Value> subMap( final Key fromKey,
                                  final boolean fromInclusive,
                                        final Key toKey,
                                  final boolean toInclusive ) {
        return new MultiMap<>( map.subMap( fromKey,
                                           fromInclusive,
                                           toKey,
                                           toInclusive ) );
    }

    public void merge( final MultiMap<Key, Value> other ) {
        for ( final Key key : other.keySet() ) {
            ArrayList<Value> values = other.get( key );
            putAllValues( key,
                          values );
        }

    }
}
