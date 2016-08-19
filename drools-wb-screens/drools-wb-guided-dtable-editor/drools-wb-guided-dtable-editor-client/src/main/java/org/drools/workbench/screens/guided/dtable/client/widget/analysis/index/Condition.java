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


import java.util.Iterator;

import org.drools.workbench.screens.guided.dtable.client.widget.analysis.cache.util.HasKeys;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.cache.util.maps.KeyDefinition;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.keys.Key;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.keys.UUIDKey;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.keys.UpdatableKey;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.keys.Value;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.keys.Values;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.matchers.ComparableMatchers;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.matchers.UUIDMatchers;
import org.uberfire.commons.validation.PortablePreconditions;

public abstract class Condition<T extends Comparable>
        implements HasKeys {

    private final static KeyDefinition SUPER_TYPE  = KeyDefinition.newKeyDefinition()
                                                                  .withId( "superType" )
                                                                  .updatable()
                                                                  .build();
    private final static KeyDefinition COLUMN_UUID = KeyDefinition.newKeyDefinition()
                                                                  .withId( "columnUUID" )
                                                                  .build();
    private final static KeyDefinition VALUE       = KeyDefinition.newKeyDefinition()
                                                                  .withId( "value" )
                                                                  .updatable()
                                                                  .build();

    protected final UUIDKey uuidKey = new UUIDKey( this );
    protected final Column                  column;
    private final   ConditionSuperType      superType;
    private UpdatableKey<Condition<T>> valueKey;

    private final Values<Comparable> values = new Values<>();

    public Condition( final Column column,
                      final ConditionSuperType superType,
                      final Values<T> values ) {
        this.column = PortablePreconditions.checkNotNull( "column", column );
        this.superType = PortablePreconditions.checkNotNull( "superType", superType );
        this.valueKey = new UpdatableKey<>( VALUE,
                                            PortablePreconditions.checkNotNull( "values", values ) );
        resetValues();
    }

    private void resetValues() {
        values.clear();

        for ( final Object o : valueKey.getValues() ) {
            values.add( (( Value ) o).getComparable() );
        }
    }

    public Column getColumn() {
        return column;
    }

    @Override
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
        final Iterator<Value> iterator = valueKey.getValues().iterator();
        if ( iterator.hasNext() ) {
            return ( T ) iterator.next().getComparable();
        } else {
            return null;
        }
    }

    public Values<Comparable> getValues() {
        return values;
    }

    public void setValue( final Values<T> values ) {
        if ( !valueKey.getValues().isThereChanges( values ) ) {
            return;
        } else {
            final UpdatableKey<Condition<T>> oldKey = valueKey;

            final UpdatableKey<Condition<T>> newKey = new UpdatableKey<>( VALUE,
                                                                          values );

            valueKey = newKey;

            oldKey.update( newKey,
                           this );
            resetValues();
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
