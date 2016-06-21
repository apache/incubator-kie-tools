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
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.keys.Key;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.keys.UUIDKey;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.keys.UpdatableKey;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.matchers.ComparableMatchers;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.matchers.FieldMatchers;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.matchers.UUIDMatchers;

public class Condition<T extends Comparable>
        implements HasKeys {

    private final UUIDKey uuidKey = new UUIDKey( this );
    private final Field                   field;
    private final Column                  column;
    private final String                  operator;
    private       UpdatableKey<Condition> valueKey;

    public Condition( final Field field,
                      final Column column,
                      final String operator,
                      final T value ) {
        this.field = field;
        this.column = column;
        this.operator = operator;
        this.valueKey = new UpdatableKey<>( "value",
                                            value );
    }

    public UUIDKey getUuidKey() {
        return uuidKey;
    }

    public static ComparableMatchers value() {
        return new ComparableMatchers( "value" );
    }

    public static Matchers columnUUID() {
        return new Matchers( "columnUUID" );
    }

    public static Matchers operator() {
        return new Matchers( "operator" );
    }

    public static FieldMatchers field() {
        return new FieldMatchers( "factType.fieldName" );
    }

    public Field getField() {
        return field;
    }

    public T getValue() {
        return ( T ) valueKey.getValue().getComparable();
    }

    public void setValue( final T value ) {
        if ( valueKey.getValue().equals( value ) ) {
            return;
        } else {
            final UpdatableKey<Condition> oldKey = valueKey;

            final UpdatableKey<Condition> newKey = new UpdatableKey<>( "value",
                                                                       value );

            valueKey = newKey;

            oldKey.update( newKey,
                             this );

        }

    }

    public String getOperator() {
        return operator;
    }

    public static Matchers uuid() {
        return new UUIDMatchers();
    }

    @Override
    public Key[] keys() {
        return new Key[]{
                uuidKey,
                new Key( "field",
                         field ),
                new Key( "factType.fieldName",
                         field.getFactType() + "." + field.getName() ),
                new Key( "columnUUID",
                         column.getUuidKey() ),
                valueKey,
                new Key( "operator",
                         operator )
        };
    }

    public static String[] keyIds() {
        return new String[]{
                UUIDKey.UNIQUE_UUID,
                "field",
                "factType.fieldName",
                "columnUUID",
                "value",
                "operator"
        };
    }
}
