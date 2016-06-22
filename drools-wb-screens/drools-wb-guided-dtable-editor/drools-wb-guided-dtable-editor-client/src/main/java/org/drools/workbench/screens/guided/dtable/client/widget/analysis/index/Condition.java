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
package org.drools.workbench.screens.guided.dtable.client.widget.analysis.index;


import org.drools.workbench.screens.guided.dtable.client.widget.analysis.cache.HasKeys;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.cache.KeyDefinition;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.keys.Key;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.keys.UUIDKey;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.keys.UpdatableKey;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.keys.Values;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.matchers.ComparableMatchers;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.matchers.UUIDMatchers;
import org.uberfire.commons.validation.PortablePreconditions;

public abstract class Condition<T extends Comparable>
        implements HasKeys {

    private final static KeyDefinition SUPER_TYPE  = KeyDefinition.newKeyDefinition().withId( "superType" ).build();
    private final static KeyDefinition COLUMN_UUID = KeyDefinition.newKeyDefinition().withId( "columnUUID" ).build();
    private final static KeyDefinition VALUE = KeyDefinition.newKeyDefinition()
                                                            .withId( "value" )
                                                            .valueList()
                                                            .canBeEmpty().build();

    protected final UUIDKey uuidKey = new UUIDKey( this );
    protected final Column                  column;
    private final   ConditionSuperType      superType;
    private UpdatableKey<Condition<T>> valueKey;

    public Condition( final Column column,
                      final ConditionSuperType superType,
                      final Values<T> values ) {
        this.column = PortablePreconditions.checkNotNull( "column", column );
        this.superType = PortablePreconditions.checkNotNull( "superType", superType );
        this.valueKey = new UpdatableKey<>( VALUE,
                                            PortablePreconditions.checkNotNull( "values", values ) );
    }

    public UUIDKey getUuidKey() {
        return uuidKey;
    }

    public static ComparableMatchers value() {
        return new ComparableMatchers( VALUE );
    }

    public static Matchers columnUUID() {
        return new Matchers( COLUMN_UUID );
    }

    public static Matchers superType() {
        return new Matchers( SUPER_TYPE );
    }

    public static Matchers uuid() {
        return new UUIDMatchers();
    }

    public T getFirstValue() {
        return ( T ) valueKey.getValue().iterator().next();
    }

    public Values getValues() {
        return Values.toValues( valueKey.getValue() );
    }

    public void setValue( final Values<T> values ) {
        if ( !Values.toValues( valueKey.getValue() ).isThereChanges( values ) ) {
            return;
        } else {
            final UpdatableKey<Condition<T>> oldKey = valueKey;

            final UpdatableKey<Condition<T>> newKey = new UpdatableKey<>( VALUE,
                                                                          values );

            valueKey = newKey;

            oldKey.update( newKey,
                           this );
        }
    }

    @Override
    public Key[] keys() {
        return new Key[]{
                uuidKey,
                valueKey,
                new Key( SUPER_TYPE,
                         superType ),
                new Key( COLUMN_UUID,
                         column.getUuidKey() ),
        };
    }

    public static KeyDefinition[] keyDefinitions() {
        return new KeyDefinition[]{
                UUIDKey.UNIQUE_UUID,
                VALUE,
                SUPER_TYPE,
                COLUMN_UUID
        };
    }
}
