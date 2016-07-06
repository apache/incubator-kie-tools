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

import java.util.Collection;

import org.drools.workbench.screens.guided.dtable.client.widget.analysis.cache.action.ActionInspector;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.cache.condition.ConditionInspector;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.util.Conflict;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.util.HumanReadable;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.util.IsConflicting;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.util.IsRedundant;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.util.IsSubsuming;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.Action;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.Condition;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.Field;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.ObjectField;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.keys.Key;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.keys.UUIDKey;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.select.AllListener;
import org.uberfire.commons.validation.PortablePreconditions;

public class FieldInspector
        implements HasConflicts,
                   IsConflicting,
                   IsSubsuming,
                   IsRedundant,
                   HumanReadable,
                   HasKeys {

    private final ObjectField      objectField;

    private final UpdatableInspectorList<ActionInspector, Action>       actionInspectorList    = new UpdatableInspectorList<>( new ActionInspectorFactory() );
    private final UpdatableInspectorList<ConditionInspector, Condition> conditionInspectorList = new UpdatableInspectorList<>( new ConditionInspectorFactory() );
    private final UUIDKey                                               uuidKey                = new UUIDKey( this );

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
        this.objectField = PortablePreconditions.checkNotNull( "field", field );
    }

    public ObjectField getObjectField() {
        return objectField;
    }

    private void updateConditionInspectors( final Collection<Condition> all ) {
        conditionInspectorList.update( all );
    }

    private void updateActionInspectors( final Collection<Action> all ) {
        actionInspectorList.update( all );
    }

    public InspectorList<ActionInspector> getActionInspectorList() {
        return actionInspectorList;
    }

    public InspectorList<ConditionInspector> getConditionInspectorList() {
        return conditionInspectorList;
    }

    @Override
    public Conflict hasConflicts() {
        int index = 1;
        for ( final ConditionInspector conditionInspector : conditionInspectorList ) {
            for ( int j = index; j < conditionInspectorList.size(); j++ ) {
                if ( conditionInspector.conflicts( conditionInspectorList.get( j ) ) ) {
                    return new Conflict( conditionInspector,
                                         conditionInspectorList.get( j ) );
                }
            }
            index++;
        }
        return Conflict.EMPTY;
    }

    @Override
    public boolean conflicts( final Object other ) {
        if ( other instanceof FieldInspector && objectField.equals( (( FieldInspector ) other).objectField ) ) {

            final boolean conflicting = actionInspectorList.conflicts( (( FieldInspector ) other).actionInspectorList );
            if ( conflicting ) {
                return true;
            } else {
                return conditionInspectorList.conflicts( (( FieldInspector ) other).conditionInspectorList );
            }
        } else {
        return false;
        }
    }

    @Override
    public boolean isRedundant( final Object other ) {
        if ( other instanceof FieldInspector && objectField.equals( (( FieldInspector ) other).objectField ) ) {
            return actionInspectorList.isRedundant( (( FieldInspector ) other).actionInspectorList )
                    && conditionInspectorList.isRedundant( (( FieldInspector ) other).conditionInspectorList );
        } else {
            return false;
        }
    }

    @Override
    public boolean subsumes( final Object other ) {
        if ( other instanceof FieldInspector && objectField.equals( (( FieldInspector ) other).objectField ) ) {
            return actionInspectorList.subsumes( (( FieldInspector ) other).actionInspectorList )
                    && conditionInspectorList.subsumes( (( FieldInspector ) other).conditionInspectorList );

        } else {
            return false;
        }
    }

    @Override
    public String toHumanReadableString() {
        return objectField.getName();
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
