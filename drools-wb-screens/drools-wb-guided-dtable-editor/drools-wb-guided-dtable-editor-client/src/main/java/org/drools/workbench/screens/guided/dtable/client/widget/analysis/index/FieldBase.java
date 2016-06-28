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
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.util.HumanReadable;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.keys.Key;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.keys.UUIDKey;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.matchers.UUIDMatchers;
import org.uberfire.commons.validation.PortablePreconditions;

public abstract class FieldBase
        implements Comparable<FieldBase>,
                   HasKeys,
                   HumanReadable{

    private static KeyDefinition NAME           = KeyDefinition.newKeyDefinition().withId( "name" ).build();
    private static KeyDefinition FACT_TYPE_NAME = KeyDefinition.newKeyDefinition().withId( "factTypeName" ).build();

    private final UUIDKey uuidKey = new UUIDKey( this );
    private final String factType;
    private final String fieldType;
    private final String name;

    public FieldBase( final String factType,
                      final String fieldType,
                      final String name ) {
        this.factType = PortablePreconditions.checkNotNull( "factType", factType );
        this.fieldType = PortablePreconditions.checkNotNull( "fieldType", fieldType );
        this.name = PortablePreconditions.checkNotNull( "name", name );
    }

    public UUIDKey getUuidKey() {
        return uuidKey;
    }

    public static Matchers name() {
        return new Matchers( NAME );
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


    public static Matchers uuid() {
        return new UUIDMatchers();
    }

    @Override
    public int compareTo( final FieldBase field ) {
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

        final FieldBase field = ( FieldBase ) o;

        if ( !factType.equals( field.factType ) ) {
            return false;
        }
        return name.equals( field.name );

    }

    @Override
    public Key[] keys() {
        return new Key[]{
                uuidKey,
                new Key( FACT_TYPE_NAME,
                         factType ),
                new Key( NAME,
                         name )
        };
    }

    public static KeyDefinition[] keyDefinitions() {
        return new KeyDefinition[]{
                UUIDKey.UNIQUE_UUID,
                FACT_TYPE_NAME,
                NAME
        };
    }

    @Override
    public String toHumanReadableString() {
        return null;
    }

}
