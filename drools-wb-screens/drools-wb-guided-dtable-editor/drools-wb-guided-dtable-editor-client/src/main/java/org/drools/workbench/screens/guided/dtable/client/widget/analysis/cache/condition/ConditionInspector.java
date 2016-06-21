/*
 * Copyright 2011 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.workbench.screens.guided.dtable.client.widget.analysis.cache.condition;

import org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.util.HumanReadable;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.util.IsConflicting;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.util.IsOverlapping;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.util.IsRedundant;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.util.IsSubsuming;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.util.Operator;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.Field;

public abstract class ConditionInspector
        implements IsRedundant,
                   IsOverlapping,
                   IsSubsuming,
                   IsConflicting,
                   HumanReadable {

    protected final Field    field;
    protected final Operator operator;

    public ConditionInspector( final Field field,
                               final String operator ) {

        this.field = field;
        this.operator = Operator.resolve( operator );
    }

    public Field getField() {
        return field;
    }

    public abstract boolean hasValue();

    public abstract String toHumanReadableString();

    @Override
    public boolean equals( Object obj ) {
        if ( obj == null ) {
            return false;
        }
        if ( this == obj ) {
            return true;
        }
        if ( !obj.getClass().equals( this.getClass() ) ) {
            return false;
        }
        if ( this.toHumanReadableString().equals( (( ConditionInspector ) obj).toHumanReadableString() ) ) {
            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return toHumanReadableString().hashCode();
    }

}
