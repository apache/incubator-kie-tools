/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.ext.metadata.backend.elastic.metamodel;

import java.util.Set;

import org.uberfire.ext.metadata.model.schema.MetaProperty;

import static org.kie.soup.commons.validation.PortablePreconditions.checkNotEmpty;
import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;

public class ElasticMetaProperty implements MetaProperty {

    private String name;
    private String value;
    private boolean sortable;
    private boolean searchable;
    private Set<Class<?>> types;

    public ElasticMetaProperty(String name,
                               String value,
                               Set<Class<?>> types) {

        this(name,
             value,
             types,
             false,
             false);
    }

    public ElasticMetaProperty(String name,
                               String value,
                               Set<Class<?>> types,
                               boolean sortable,
                               boolean searchable) {
        this.name = checkNotEmpty("name",
                                  name);
        this.value = value;
        this.types = checkNotNull("types",
                                  types);
        this.sortable = sortable;
        this.searchable = searchable;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Set<Class<?>> getTypes() {
        return this.types;
    }

    @Override
    public boolean isSearchable() {
        return this.searchable;
    }

    @Override
    public void setAsSearchable() {
        this.searchable = true;
    }

    @Override
    public boolean isSortable() {
        return this.sortable;
    }

    @Override
    public void setAsSortable() {
        this.sortable = true;
    }

    @Override
    public void addType(Class<?> aClass) {
        this.types.add(aClass);
    }

    public String getValue() {
        return value;
    }

    public boolean isBoolean() {
        return this.getTypes().contains(Boolean.class);
    }
}
