/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.services.verifier.api.client.cache.util.maps;

import java.util.Collection;
import java.util.Set;

import org.drools.workbench.services.verifier.api.client.cache.util.HasConflicts;
import org.drools.workbench.services.verifier.api.client.cache.util.HasRedundancy;
import org.drools.workbench.services.verifier.api.client.cache.util.RedundancyResult;
import org.drools.workbench.services.verifier.api.client.checks.util.Conflict;
import org.drools.workbench.services.verifier.api.client.checks.util.IsConflicting;
import org.drools.workbench.services.verifier.api.client.checks.util.IsRedundant;
import org.drools.workbench.services.verifier.api.client.checks.util.IsSubsuming;
import org.drools.workbench.services.verifier.api.client.index.keys.Key;
import org.drools.workbench.services.verifier.api.client.index.keys.UUIDKey;
import org.drools.workbench.services.verifier.api.client.cache.util.HasKeys;
import org.drools.workbench.services.verifier.api.client.checks.util.HumanReadable;
import org.drools.workbench.services.verifier.api.client.configuration.AnalyzerConfiguration;

public class InspectorMultiMap<GroupBy extends Comparable, Value extends IsConflicting & IsRedundant & HumanReadable>
        implements HasConflicts,
                   HasRedundancy,
                   IsConflicting,
                   IsRedundant,
                   IsSubsuming,
                   HasKeys {

    private final UUIDKey uuidKey;

    private MultiMap<GroupBy, Value, LeafInspectorList<Value>> map;

    public InspectorMultiMap( final AnalyzerConfiguration configuration ) {
        uuidKey = configuration.getUUID( this );
        map = MultiMapFactory.make( true,
                                    new NewSubMapProvider<Value, LeafInspectorList<Value>>() {
                                        @Override
                                        public LeafInspectorList<Value> getNewSubMap() {
                                            return new LeafInspectorList<>( configuration );
                                        }
                                    } );

    }

    @Override
    public Conflict hasConflicts() {
        for ( final GroupBy groupBy : map.keySet() ) {
            final Conflict result = map.get( groupBy )
                    .hasConflicts();
            if ( result.foundIssue() ) {
                return result;
            }
        }

        return Conflict.EMPTY;
    }

    @Override
    public RedundancyResult<GroupBy, Value> hasRedundancy() {
        for ( final GroupBy groupBy : map.keySet() ) {
            final RedundancyResult result = map.get( groupBy )
                    .hasRedundancy();
            if ( result.isTrue() ) {
                return new RedundancyResult<>( groupBy,
                                               result );
            }
        }

        return RedundancyResult.EMPTY;
    }

    @Override
    public boolean conflicts( final Object other ) {

        if ( other instanceof InspectorMultiMap ) {
            for ( final GroupBy groupBy : map.keySet() ) {
                final InspectorList list = (InspectorList) ( (InspectorMultiMap) other ).map.get( groupBy );

                if ( list instanceof InspectorList && map.get( groupBy )
                        .conflicts( list ) ) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public boolean isRedundant( final Object other ) {

        if ( other instanceof InspectorMultiMap ) {
            for ( final GroupBy groupBy : map.keySet() ) {

                if ( !map.get( groupBy )
                        .isRedundant( ( (InspectorMultiMap<GroupBy, Value>) other ).map.get( groupBy ) ) ) {
                    return false;
                }
            }
            return true;
        }

        return false;
    }

    @Override
    public boolean subsumes( final Object other ) {

        if ( other instanceof InspectorMultiMap ) {

            for ( final Object groupBy : ( (InspectorMultiMap) other ).map.keySet() ) {
                final InspectorList otherCollection = ( (InspectorMultiMap<GroupBy, Value>) other ).map.get( (GroupBy) groupBy );
                final LeafInspectorList<Value> collection = map.get( (GroupBy) groupBy );
                if ( !otherCollection.subsumes( collection ) ) {
                    return false;
                }
            }
            return true;
        }

        return false;
    }

    @Override
    public UUIDKey getUuidKey() {
        return uuidKey;
    }

    @Override
    public Key[] keys() {
        return new Key[]{
                uuidKey
        };
    }

    public Set<GroupBy> keySet() {
        return map.keySet();
    }

    public LeafInspectorList<Value> get( final GroupBy groupBy ) {
        return map.get( groupBy );
    }

    public void putAllValues( final GroupBy groupBy,
                              final LeafInspectorList<Value> values ) {
        map.putAllValues( groupBy,
                          values );
    }

    public void put( final GroupBy groupBy,
                     final Value value ) {
        map.put( groupBy,
                 value );
    }

    public void addAllValues( final GroupBy groupBy,
                              final InspectorList<Value> list ) {
        map.addAllValues( groupBy,
                          list );
    }

    public Collection<Value> allValues() {
        return map.allValues();
    }

}
