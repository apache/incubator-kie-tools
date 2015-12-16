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

import org.drools.workbench.screens.guided.dtable.client.widget.analysis.action.ActionInspector;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.action.ActionInspectorKey;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.util.Conflict;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.util.IsConflicting;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.util.IsRedundant;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.util.IsSubsuming;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.util.Redundancy;

public class Actions
        extends MultiMap<ActionInspectorKey, ActionInspector>
        implements IsRedundant,
                   IsSubsuming,
                   IsConflicting {

    @Override
    public boolean isRedundant( Object other ) {
        if ( other instanceof IsSubsuming ) {
            return ( subsumes( other ) && ( (IsSubsuming) other ).subsumes( this ) );
        } else {
            return false;
        }
    }

    @Override
    public boolean subsumes( Object other ) {
        if ( other instanceof Actions ) {
            for ( ActionInspectorKey key : keys() ) {
                if ( !Redundancy.subsumes( get( key ),
                                           ( (Actions) other ).get( key ) ) ) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean conflicts( Object other ) {
        if ( other instanceof Actions ) {
            for ( ActionInspectorKey key : keys() ) {
                if ( Conflict.isConflicting( get( key ),
                                             ( (Actions) other ).get( key ) ) ) {
                    return true;
                }
            }
            return false;
        } else {
            return false;
        }
    }

    public boolean hasValues() {
        for ( ActionInspector action : allValues() ) {
            if ( action.hasValue() ) {
                return true;
            }
        }
        return false;
    }
}
