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
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.util.HumanReadable;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.keys.Key;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.keys.UUIDKey;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.matchers.UUIDMatchers;

public class Field
        implements Comparable<Field>,
                   HasKeys,
                   HumanReadable{

    private final UUIDKey uuidKey = new UUIDKey( this );
    private final String factType;
    private final String fieldType;
    private final String name;
    private final Conditions conditions = new Conditions();
    private final Actions    actions    = new Actions();

    public Field( final String factType,
                  final String fieldType,
                  final String name ) {
        this.factType = factType;
        this.fieldType = fieldType;
        this.name = name;
    }

    public UUIDKey getUuidKey() {
        return uuidKey;
    }

    public static Matchers name() {
        return new Matchers( "name" );
    }

    public String getFactType() {
        return factType;
    }

    public String getFieldType() {
        return fieldType;
    }

    public String getName() {
        return name;
    }

    public Conditions getConditions() {
        return conditions;
    }

    public Actions getActions() {
        return actions;
    }

    public static Matchers uuid() {
        return new UUIDMatchers();
    }

    public void remove( final Column column ) {
        this.conditions.remove( column );
        this.actions.remove( column );
    }

    @Override
    public int compareTo( final Field field ) {
        if ( factType.equals( field.factType )
                && name.equals( field.name ) ) {
            return 0;
        } else if ( factType.equals( field.factType ) ) {
            return name.compareTo( field.name );
        } else {
            return factType.compareTo( field.factType );
        }
    }

    @Override
    public int hashCode() {
        int result = ~~factType.hashCode();
        result = 31 * result + ~~name.hashCode();
        return ~~result;
    }

    @Override
    public boolean equals( final Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( o == null || getClass() != o.getClass() ) {
            return false;
        }

        final Field field = ( Field ) o;

        if ( !factType.equals( field.factType ) ) {
            return false;
        }
        return name.equals( field.name );

    }

    @Override
    public Key[] keys() {
        return new Key[]{
                uuidKey,
                new Key( "factTypeName",
                         factType ),
                new Key( "name",
                         name )
        };
    }

    public static String[] keyIDs() {
        return new String[]{
                UUIDKey.UNIQUE_UUID,
                "factTypeName",
                "name"
        };
    }

    @Override
    public String toHumanReadableString() {
        return null;
    }

}
