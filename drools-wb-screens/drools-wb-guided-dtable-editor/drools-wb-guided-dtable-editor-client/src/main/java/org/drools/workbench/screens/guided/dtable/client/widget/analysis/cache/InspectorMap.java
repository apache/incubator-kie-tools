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

import java.util.ArrayList;

import org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.util.Conflict;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.util.HumanReadable;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.util.IsConflicting;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.util.IsRedundant;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.util.IsSubsuming;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.util.Redundancy;

public class InspectorMap<GroupBy extends Comparable, Value extends IsConflicting & IsRedundant & HumanReadable>
        extends RawMultiMap<GroupBy, Value, LeafInspectorList<Value>>
        implements HasConflicts,
                   HasRedundancy,
                   IsConflicting,
                   IsRedundant,
                   IsSubsuming {

    @Override
    protected LeafInspectorList<Value> getNewSubMap() {
        return new LeafInspectorList<>();
    }

    @Override
    public ArrayList<Value> hasConflicts() {
        for ( final GroupBy groupBy : keys() ) {
            final ArrayList<Value> result = get( groupBy ).hasConflicts();
            if ( !result.isEmpty() ) {
                return result;
            }
        }

        return new ArrayList<>();
    }

    @Override
    public RedundancyResult<GroupBy, Value> hasRedundancy() {
        for ( final GroupBy groupBy : keys() ) {
            final RedundancyResult result = get( groupBy ).hasRedundancy();
            if ( result.isTrue() ) {
                return new RedundancyResult<GroupBy, Value>( groupBy, result );
            }
        }

        return RedundancyResult.EMPTY;
    }

    @Override
    public boolean conflicts( final Object other ) {

        if ( other instanceof InspectorMap ) {
            for ( final GroupBy groupBy : keys() ) {
                if ( Conflict.isConflicting( get( groupBy ),
                                             (( InspectorMap ) other).get( groupBy ) ) ) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public boolean isRedundant( final Object other ) {

        if ( other instanceof InspectorMap ) {
            for ( final GroupBy groupBy : keys() ) {
                if ( !Redundancy.isRedundant( get( groupBy ),
                                              (( InspectorMap ) other).get( groupBy ) ) ) {
                    return false;
                }
            }
            return true;
        }

        return false;
    }

    @Override
    public boolean subsumes( final Object other ) {

        if ( other instanceof InspectorMap ) {

            for ( final Object groupBy : (( InspectorMap ) other).keys() ) {
                if ( !Redundancy.subsumes( (( InspectorMap ) other).get( groupBy ),
                                           get( groupBy ) ) ) {
                    return false;
                }
            }
            return true;
        }

        return false;
    }

}
