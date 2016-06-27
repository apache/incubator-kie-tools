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


import java.util.ArrayList;

import org.drools.workbench.screens.guided.dtable.client.widget.analysis.cache.KeyDefinition;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.keys.Key;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.keys.Values;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.matchers.FieldMatchers;
import org.uberfire.commons.validation.PortablePreconditions;

public class FieldCondition<T extends Comparable>
        extends Condition {

    private final static KeyDefinition OPERATOR              = KeyDefinition.newKeyDefinition().withId( "operator" ).build();
    private final static KeyDefinition FACT_TYPE__FIELD_NAME = KeyDefinition.newKeyDefinition().withId( "factType.fieldName" ).build();
    private final static KeyDefinition FIELD                 = KeyDefinition.newKeyDefinition().withId( "field" ).build();
    private final static KeyDefinition FIELD_TYPE = KeyDefinition.newKeyDefinition().withId( "fieldType" ).build();

    private final Field  field;
    private final String operator;

    public FieldCondition( final Field field,
                           final Column column,
                           final String operator,
                           final Values<T> values ) {
        super( column,
               ConditionSuperType.FIELD_CONDITION,
               resolveValues( PortablePreconditions.checkNotNull( "operator", operator ),
                              PortablePreconditions.checkNotNull( "values", values ) ) );
        this.field = PortablePreconditions.checkNotNull( "field", field );
        this.operator = resolveOperator( operator );
    }

    private static Values resolveValues( final String operator,
                                         final Values values ) {
        if ( "!= null".equals( operator ) ) {
            return Values.nullValue();
        } else if ( "== null".equals( operator ) ) {
            return Values.nullValue();
        } else {
            return values;
        }
    }

    private String resolveOperator( final String operator ) {

        if ( "!= null".equals( operator ) ) {
            return "!=";
        } else if ( "== null".equals( operator ) ) {
            return "==";
        } else {
            return operator;
        }
    }


    public static Matchers operator() {
        return new Matchers( OPERATOR );
    }

    public static FieldMatchers field() {
        return new FieldMatchers( FACT_TYPE__FIELD_NAME );
    }

    public static Matchers fieldType() {
        return new Matchers( FIELD_TYPE );
    }

    public Field getField() {
        return field;
    }

    public String getOperator() {
        return operator;
    }

    @Override
    public Key[] keys() {
        final ArrayList<Key> keys = new ArrayList<>();
        for ( final Key key : super.keys() ) {
            keys.add( key );
        }

        keys.add( new Key( OPERATOR,
                           operator ) );
        keys.add( new Key( FIELD,
                           field ) );
        keys.add( new Key( FIELD_TYPE,
                           field.getFieldType() ) );
        keys.add( new Key( FACT_TYPE__FIELD_NAME,
                           field.getFactType() + "." + field.getName() ) );

        return keys.toArray( new Key[keys.size()] );
    }

    public static KeyDefinition[] keyDefinitions() {
        final ArrayList<KeyDefinition> keyDefinitions = new ArrayList<>();
        for ( final KeyDefinition keyDefinition : Condition.keyDefinitions() ) {
            keyDefinitions.add( keyDefinition );
        }

        keyDefinitions.add( OPERATOR );
        keyDefinitions.add( FIELD );
        keyDefinitions.add( FACT_TYPE__FIELD_NAME );
        keyDefinitions.add( FIELD_TYPE );

        return keyDefinitions.toArray( new KeyDefinition[keyDefinitions.size()] );
    }
}
