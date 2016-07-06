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
package org.drools.workbench.screens.guided.dtable.client.widget.analysis.cache;

import org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.util.Conflict;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.util.HumanReadable;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.util.IsConflicting;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.util.IsRedundant;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.util.IsSubsuming;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.keys.Key;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.keys.UUIDKey;

public class InspectorMultiMap<GroupBy extends Comparable, Value extends IsConflicting & IsRedundant & HumanReadable>
        extends RawMultiMap<GroupBy, Value, LeafInspectorList<Value>>
        implements HasConflicts,
                   HasRedundancy,
                   IsConflicting,
                   IsRedundant,
                   IsSubsuming,
                   HasKeys {

    private final UUIDKey uuidKey = new UUIDKey( this );

    @Override
    protected LeafInspectorList<Value> getNewSubMap() {
        return new LeafInspectorList<>();
    }

    @Override
    public Conflict hasConflicts() {
        for ( final GroupBy groupBy : keySet() ) {
            final Conflict result = get( groupBy ).hasConflicts();
            if ( result.foundIssue() ) {
                return result;
            }
        }

        return Conflict.EMPTY;
    }

    @Override
    public RedundancyResult<GroupBy, Value> hasRedundancy() {
        for ( final GroupBy groupBy : keySet() ) {
            final RedundancyResult result = get( groupBy ).hasRedundancy();
            if ( result.isTrue() ) {
                return new RedundancyResult<>( groupBy, result );
            }
        }

        return RedundancyResult.EMPTY;
    }

    @Override
    public boolean conflicts( final Object other ) {

        if ( other instanceof InspectorMultiMap ) {
            for ( final GroupBy groupBy : keySet() ) {
                final InspectorList list = ( InspectorList ) (( InspectorMultiMap ) other).get( groupBy );

                if ( list instanceof InspectorList && get( groupBy ).conflicts( list ) ) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public boolean isRedundant( final Object other ) {

        if ( other instanceof InspectorMultiMap ) {
            for ( final GroupBy groupBy : keySet() ) {

                if ( !get( groupBy ).isRedundant( (( InspectorMultiMap<GroupBy, Value> ) other).get( groupBy ) ) ) {
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

            for ( final Object groupBy : (( InspectorMultiMap ) other).keySet() ) {
                final InspectorList otherCollection = (( InspectorMultiMap<GroupBy, Value> ) other).get( ( GroupBy ) groupBy );
                final LeafInspectorList<Value> collection = get( ( GroupBy ) groupBy );
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
}
