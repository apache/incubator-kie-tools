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
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.Field;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.Pattern;

public class PatternInspector
        implements HasConflicts,
                   IsConflicting,
                   IsSubsuming,
                   IsRedundant,
                   HumanReadable {

    private final Pattern pattern;

    private final InspectorList<FieldInspector> inspectorList       = new InspectorList<>();

    public PatternInspector( final Pattern pattern ) {
        this.pattern = pattern;
        for ( final Field field : pattern.getFields()
                                         .where( Field.uuid().any() )
                                         .select().all() ) {
            inspectorList.add( new FieldInspector( field ) );
        }
    }

    public Pattern getPattern() {
        return pattern;
    }

    @Override
    public boolean conflicts( final Object other ) {
        if ( other instanceof PatternInspector ) {
            if ( pattern.getObjectType().getType().equals( (( PatternInspector ) other).getPattern().getObjectType().getType() ) ) {
                return Conflict.isConflicting( inspectorList,
                                           (( PatternInspector ) other).inspectorList );
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    @Override
    public boolean isRedundant( final Object other ) {
        if ( other instanceof PatternInspector ) {
            if ( pattern.getObjectType().getType().equals( (( PatternInspector ) other).getPattern().getObjectType().getType() ) ) {
                return Redundancy.isRedundant( inspectorList,
                                           (( PatternInspector ) other).inspectorList );
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    @Override
    public boolean subsumes( final Object other ) {
        if ( other instanceof PatternInspector ) {
            if ( pattern.getObjectType().getType().equals( (( PatternInspector ) other).getPattern().getObjectType().getType() ) ) {
                return Redundancy.subsumes( inspectorList,
                                            (( PatternInspector ) other).inspectorList );
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    @Override
    public ArrayList<FieldInspector> hasConflicts() {
        return inspectorList.hasConflicts();
    }

    public ActionsInspector getActionsInspector() {
        final ActionsInspector actionsInspector = new ActionsInspector();

        for ( final FieldInspector fieldInspector : inspectorList ) {
            actionsInspector.addAllValues( fieldInspector.getField(),
                                           fieldInspector.getActionInspectorList() );
        }

        return actionsInspector;
    }

    public ConditionsInspector getConditionsInspector() {
        final ConditionsInspector conditionsInspector = new ConditionsInspector();

        for ( final FieldInspector fieldInspector : inspectorList ) {
            conditionsInspector.addAllValues( fieldInspector.getField(),
                                              fieldInspector.getConditionInspectorList() );
        }

        return conditionsInspector;
    }

    @Override
    public String toHumanReadableString() {
        return pattern.getName();
    }
}
