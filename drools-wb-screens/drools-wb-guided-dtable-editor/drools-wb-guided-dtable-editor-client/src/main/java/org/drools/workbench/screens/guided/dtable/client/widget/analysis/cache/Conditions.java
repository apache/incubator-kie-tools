/*
 * Copyright 2015 JBoss Inc
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

import org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.util.IsRedundant;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.util.IsSubsuming;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.util.Redundancy;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.condition.ConditionInspector;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.condition.ConditionInspectorKey;

public class Conditions
        extends MultiMap<ConditionInspectorKey, ConditionInspector>
        implements IsRedundant,
                   IsSubsuming {

    public void addAll( Collection<ConditionInspector> conditionInspectorList ) {
        for ( ConditionInspector inspector : conditionInspectorList ) {
            put( inspector.getKey(), inspector );
        }
    }

    @Override
    public boolean isRedundant( Object other ) {
        if ( other instanceof IsSubsuming ) {
            if ( subsumes( other ) && ( (IsSubsuming) other ).subsumes( this ) ) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    @Override
    public boolean subsumes( Object other ) {
        if ( other instanceof Conditions ) {
            for ( ConditionInspectorKey key : keys() ) {
                if ( !Redundancy.isSubsumptant( get( key ), ( (Conditions) other ).get( key ) ) ) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }
}
