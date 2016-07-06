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

import java.util.Arrays;
import java.util.Collection;

import org.drools.workbench.screens.guided.dtable.client.widget.analysis.cache.KeyTreeMap;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.cache.MultiMap;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.keys.Value;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.matchers.Matcher;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.select.Listen;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.select.Select;

public class ObjectTypes {

    public final KeyTreeMap<ObjectType> typesMap = new KeyTreeMap<>( ObjectType.keyDefinitions() );

    public ObjectTypes( final Collection<ObjectType> typesMap ) {
        for ( final ObjectType objectType : typesMap ) {
            add( objectType );
        }

    }

    public ObjectTypes( final ObjectType[] typesMap ) {
        this( Arrays.asList( typesMap ) );
    }

    public ObjectTypes() {

    }

    public void merge( final ObjectTypes patterns ) {
        this.typesMap.merge( patterns.typesMap );
    }

    public Where<ObjectTypesSelect, ObjectTypesListen> where( final Matcher matcher ) {
        return new Where<ObjectTypesSelect, ObjectTypesListen>() {
            @Override
            public ObjectTypesSelect select() {
                return new ObjectTypesSelect( matcher );
            }

            @Override
            public ObjectTypesListen listen() {
                return new ObjectTypesListen( matcher );
            }
        };
    }

    void add( final ObjectType... objectTypes ) {
        for ( final ObjectType objectType : objectTypes ) {
            this.typesMap.put( objectType );
        }
    }

    public class ObjectTypesSelect
            extends Select<ObjectType> {

        public ObjectTypesSelect( final Matcher matcher ) {
            super( typesMap.get( matcher.getKeyDefinition() ),
                   matcher );
        }

        public ObjectFields fields() {
            final ObjectFields fields = new ObjectFields();

            final MultiMap<Value, ObjectType> subMap = asMap();
            if ( subMap != null ) {
                final Collection<ObjectType> objectTypes = subMap.allValues();
                for ( final ObjectType objectType : objectTypes ) {
                    fields.merge( objectType.getFields() );
                }
            }

            return fields;
        }
    }

    public class ObjectTypesListen
            extends Listen<ObjectType> {

        public ObjectTypesListen( final Matcher matcher ) {
            super( typesMap.get( matcher.getKeyDefinition() ),
                   matcher );
        }
    }
}
