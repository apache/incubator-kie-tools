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
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.cache.action.FieldActionInspector;
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
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.FieldAction;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.FieldCondition;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.ObjectField;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.select.AllListener;

public class FieldInspector
        implements HasConflicts,
                   IsConflicting,
                   IsSubsuming,
                   IsRedundant,
                   HumanReadable {

    private final ObjectField objectField;

    private final List<ActionInspector> actionInspectorList = new ArrayList<>();
    private final List<ConditionInspector> conditionInspectorList = new ArrayList<>();

    public FieldInspector( final Field field ) {
        this( field.getObjectField() );

        updateActionInspectors( field.getActions()
                                     .where( Action.value().any() )
                                     .select().all() );
        updateConditionInspectors( field.getConditions()
                                        .where( Condition.value().any() )
                                        .select().all() );

        field.getActions()
             .where( Condition.value().any() )
             .listen().all( new AllListener<Action>() {
            @Override
            public void onAllChanged( final Collection<Action> all ) {
                updateActionInspectors( all );
            }
        } );

        field.getConditions()
             .where( Condition.value().any() )
             .listen().all( new AllListener<Condition>() {
            @Override
            public void onAllChanged( final Collection<Condition> all ) {
                updateConditionInspectors( all );
            }
        } );
    }

    public FieldInspector( final ObjectField field ) {
        this.objectField = field;
    }

    public ObjectField getObjectField() {
        return objectField;
    }

    private void updateConditionInspectors( final Collection<Condition> all ) {
        conditionInspectorList.clear();
        for ( final Condition condition : all ) {
            if ( condition instanceof FieldCondition ) {
                conditionInspectorList.add( buildConditionInspector( ( FieldCondition ) condition ) );
            }
        }
    }

    private void updateActionInspectors( final Collection<Action> all ) {
        actionInspectorList.clear();
        for ( final Action action : all ) {
            actionInspectorList.add( new FieldActionInspector( ( FieldAction ) action ) );
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
        if ( other instanceof FieldInspector && objectField.equals( (( FieldInspector ) other).objectField ) ) {

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
        if ( other instanceof FieldInspector && objectField.equals( (( FieldInspector ) other).objectField ) ) {
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
        if ( other instanceof FieldInspector && objectField.equals( (( FieldInspector ) other).objectField ) ) {
            return Redundancy.subsumes( actionInspectorList,
                                        (( FieldInspector ) other).actionInspectorList )
                    && Redundancy.subsumes( conditionInspectorList,
                                            (( FieldInspector ) other).conditionInspectorList );

        } else {
            return false;
        }
    }

    private ConditionInspector buildConditionInspector( final FieldCondition condition ) {

        if ( !condition.getValues().isEmpty() && condition.getValues().get( 0 ) instanceof String ) {
            return new StringConditionInspector( condition );

        } else if ( !condition.getValues().isEmpty() && condition.getValues().get( 0 ) instanceof Boolean ) {
            return new BooleanConditionInspector( condition );

        } else if ( !condition.getValues().isEmpty() && condition.getValues().get( 0 ) instanceof Integer ) {
            return new NumericIntegerConditionInspector( condition );

        } else {
            return new ComparableConditionInspector<>( condition );
        }
    }

    @Override
    public String toHumanReadableString() {
        return objectField.getName();
    }
}
