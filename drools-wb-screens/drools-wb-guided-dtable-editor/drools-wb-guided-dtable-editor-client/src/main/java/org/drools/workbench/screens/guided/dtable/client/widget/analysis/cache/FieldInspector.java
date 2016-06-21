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
import java.util.Collection;
import java.util.List;

import org.drools.workbench.screens.guided.dtable.client.widget.analysis.cache.action.ActionInspector;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.cache.condition.BooleanConditionInspector;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.cache.condition.ComparableConditionInspector;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.cache.condition.ConditionInspector;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.cache.condition.NumericIntegerConditionInspector;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.cache.condition.StringConditionInspector;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.util.Conflict;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.util.HumanReadable;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.util.IsConflicting;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.util.IsRedundant;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.util.IsSubsuming;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.util.Redundancy;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.Action;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.Condition;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.Field;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.select.AllListener;

public class FieldInspector
        implements HasConflicts,
                   IsConflicting,
                   IsSubsuming,
                   IsRedundant,
                   HumanReadable {

    private final Field field;

    private final List<ActionInspector> actionInspectorList = new ArrayList<>();
    private final List<ConditionInspector> conditionInspectorList = new ArrayList<>();

    public FieldInspector( final Field field ) {
        this.field = field;

        updateActionInspectors( field.getActions()
                                     .where( Action.value().isNot( null ) )
                                     .select().all() );
        updateConditionInspectors( field.getConditions()
                                        .where( Condition.value().isNot( null ) )
                                        .select().all() );

        field.getActions()
             .where( Condition.value().isNot( null ) )
             .listen().all( new AllListener<Action>() {
            @Override
            public void onAllChanged( final Collection<Action> all ) {
                updateActionInspectors( all );
            }
        } );

        field.getConditions()
             .where( Condition.value().isNot( null ) )
             .listen().all( new AllListener<Condition>() {
            @Override
            public void onAllChanged( final Collection<Condition> all ) {
                updateConditionInspectors( all );
            }
        } );
    }

    public Field getField() {
        return field;
    }

    private void updateConditionInspectors( final Collection<Condition> all ) {
        conditionInspectorList.clear();
        for ( final Condition condition : all ) {
            conditionInspectorList.add( buildConditionInspector( condition ) );
        }
    }

    private void updateActionInspectors( final Collection<Action> all ) {
        actionInspectorList.clear();
        for ( final Action action : all ) {
            actionInspectorList.add( new ActionInspector( action ) );
        }
    }

    public List<ActionInspector> getActionInspectorList() {
        return actionInspectorList;
    }

    public List<ConditionInspector> getConditionInspectorList() {
        return conditionInspectorList;
    }

    @Override
    public ArrayList<ConditionInspector> hasConflicts() {
        int index = 1;
        for ( final ConditionInspector conditionInspector : conditionInspectorList ) {
            for ( int j = index; j < conditionInspectorList.size(); j++ ) {
                if ( conditionInspector.conflicts( conditionInspectorList.get( j ) ) ) {
                    final ArrayList<ConditionInspector> result = new ArrayList<>();
                    result.add( conditionInspector );
                    result.add( conditionInspectorList.get( j ) );
                    return result;
                }
            }
            index++;
        }
        return new ArrayList<>();
    }

    @Override
    public boolean conflicts( final Object other ) {
        if ( other instanceof FieldInspector && field.equals( (( FieldInspector ) other).field ) ) {

            final boolean conflicting = Conflict.isConflicting( actionInspectorList,
                                                                (( FieldInspector ) other).actionInspectorList );
            if ( conflicting ) {
                return true;
            } else {
                return Conflict.isConflicting( conditionInspectorList,
                                               (( FieldInspector ) other).conditionInspectorList );
            }
        } else {
        return false;
        }
    }

    @Override
    public boolean isRedundant( final Object other ) {
        if ( other instanceof FieldInspector && field.equals( (( FieldInspector ) other).field ) ) {
            return Redundancy.isRedundant( actionInspectorList,
                                           (( FieldInspector ) other).actionInspectorList )
                    && Redundancy.isRedundant( conditionInspectorList,
                                               (( FieldInspector ) other).conditionInspectorList );
        } else {
            return false;
        }
    }

    @Override
    public boolean subsumes( final Object other ) {
        if ( other instanceof FieldInspector && field.equals( (( FieldInspector ) other).field ) ) {
            return Redundancy.subsumes( actionInspectorList,
                                        (( FieldInspector ) other).actionInspectorList )
                    && Redundancy.subsumes( conditionInspectorList,
                                            (( FieldInspector ) other).conditionInspectorList );

        } else {
            return false;
        }
    }

    private ConditionInspector buildConditionInspector( final Condition condition ) {

        if ( condition.getValue() instanceof String ) {
            return new StringConditionInspector( field,
                                                 condition.getValue().toString(),
                                                 condition.getOperator() );

        } else if ( condition.getValue() instanceof Boolean ) {
            return new BooleanConditionInspector( field,
                                                  ( Boolean ) condition.getValue(),
                                                  condition.getOperator() );

        } else if ( condition.getValue() instanceof Integer ) {
            return new NumericIntegerConditionInspector( field,
                                                         ( Integer ) condition.getValue(),
                                                         condition.getOperator() );

        } else {
            return new ComparableConditionInspector<>( field,
                                                       condition.getValue(),
                                                       condition.getOperator() );
        }
    }

    @Override
    public String toHumanReadableString() {
        return field.getName();
    }
}
