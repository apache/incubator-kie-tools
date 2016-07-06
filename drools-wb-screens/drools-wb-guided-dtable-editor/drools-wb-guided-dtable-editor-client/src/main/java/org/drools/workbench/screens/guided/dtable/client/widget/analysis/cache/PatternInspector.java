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
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.util.RelationResolver;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.Field;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.ObjectField;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.Pattern;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.keys.Key;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.keys.UUIDKey;
import org.uberfire.commons.validation.PortablePreconditions;

public class PatternInspector
        implements HasConflicts,
                   IsConflicting,
                   IsSubsuming,
                   IsRedundant,
                   HumanReadable, HasKeys {

    private final UUIDKey uuidKey = new UUIDKey( this );

    private final Pattern          pattern;

    private final InspectorList<FieldInspector> inspectorList       = new InspectorList<>();
    private final RelationResolver relationResolver;

    private final ActionsInspectorMultiMap actionsInspector;

    public PatternInspector( final Pattern pattern ) {
        this.pattern = PortablePreconditions.checkNotNull( "pattern", pattern );
        this.actionsInspector = new ActionsInspectorMultiMap();

        relationResolver = new RelationResolver( inspectorList );

        makeFieldInspectors();
        makeActionsInspectors();
    }

    private void makeActionsInspectors() {
        for ( final FieldInspector fieldInspector : inspectorList ) {
            actionsInspector.addAllValues( fieldInspector.getObjectField(),
                                           fieldInspector.getActionInspectorList() );
        }
    }

    private void makeFieldInspectors() {
        final ArrayList<ObjectField> alreadyAdded = new ArrayList<>();

        for ( final Field field : pattern.getFields()
                                         .where( Field.uuid().any() )
                                         .select().all() ) {
            inspectorList.add( new FieldInspector( field ) );
            alreadyAdded.add( field.getObjectField() );
        }
        for ( final ObjectField field : pattern.getObjectType()
                                               .getFields()
                                               .where( Field.uuid().any() )
                                               .select().all() ) {
            if ( alreadyAdded.contains( field ) ) {
                inspectorList.add( new FieldInspector( field ) );
            }
        }
    }

    public Pattern getPattern() {
        return pattern;
    }

    @Override
    public boolean conflicts( final Object other ) {
        if ( other instanceof PatternInspector ) {
            if ( pattern.getObjectType().getType().equals( (( PatternInspector ) other).getPattern().getObjectType().getType() ) ) {
                return inspectorList.conflicts( (( PatternInspector ) other).inspectorList );
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
                return inspectorList.isRedundant( (( PatternInspector ) other).inspectorList );
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
                return inspectorList.subsumes( (( PatternInspector ) other).inspectorList );
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    @Override
    public Conflict hasConflicts() {
        return relationResolver.resolveConflict( inspectorList );
    }

    public ActionsInspectorMultiMap getActionsInspector() {
        return actionsInspector;
    }

    public ConditionsInspectorMultiMap getConditionsInspector() {
        final ConditionsInspectorMultiMap conditionsInspector = new ConditionsInspectorMultiMap();

        for ( final FieldInspector fieldInspector : inspectorList ) {
            conditionsInspector.addAllValues( fieldInspector.getObjectField(),
                                              fieldInspector.getConditionInspectorList() );
        }

        return conditionsInspector;
    }

    @Override
    public String toHumanReadableString() {
        return pattern.getName();
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
