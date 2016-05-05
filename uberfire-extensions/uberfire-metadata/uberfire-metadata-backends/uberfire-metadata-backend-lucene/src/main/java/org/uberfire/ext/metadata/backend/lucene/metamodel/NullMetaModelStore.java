/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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

package org.uberfire.ext.metadata.backend.lucene.metamodel;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import org.uberfire.ext.metadata.engine.MetaModelStore;
import org.uberfire.ext.metadata.model.schema.MetaObject;
import org.uberfire.ext.metadata.model.schema.MetaProperty;
import org.uberfire.ext.metadata.model.schema.MetaType;

public class NullMetaModelStore implements MetaModelStore {

    private static MetaObject EMPTY = new MetaObject() {
        @Override
        public MetaType getType() {
            return new MetaType() {
                @Override
                public String getName() {
                    return "";
                }
            };
        }

        @Override
        public Collection<MetaProperty> getProperties() {
            return Collections.emptyList();
        }

        @Override
        public MetaProperty getProperty( String name ) {
            return new MetaProperty() {
                @Override
                public String getName() {
                    return "";
                }

                @Override
                public Set<Class<?>> getTypes() {
                    return Collections.emptySet();
                }

                @Override
                public boolean isSearchable() {
                    return false;
                }

                @Override
                public void setAsSearchable() {

                }

                @Override
                public void addType( Class<?> aClass ) {

                }
            };
        }

        @Override
        public void addProperty( MetaProperty metaProperty ) {

        }
    };

    @Override
    public void add( final MetaObject metaObject ) {
    }

    @Override
    public void update( final MetaObject metaObject ) {
    }

    @Override
    public MetaObject getMetaObject( final String type ) {
        return EMPTY;
    }

    @Override
    public void dispose() {
    }
}
