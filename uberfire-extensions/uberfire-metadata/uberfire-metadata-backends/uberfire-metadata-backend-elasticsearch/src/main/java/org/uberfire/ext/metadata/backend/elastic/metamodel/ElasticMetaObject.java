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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.uberfire.ext.metadata.backend.elastic.util.MappingException;
import org.uberfire.ext.metadata.model.schema.MetaObject;
import org.uberfire.ext.metadata.model.schema.MetaProperty;
import org.uberfire.ext.metadata.model.schema.MetaType;

import static org.kie.soup.commons.validation.PortablePreconditions.checkNotEmpty;
import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;

public class ElasticMetaObject implements MetaObject {

    private MetaType type;
    private List<MetaProperty> properties;

    public ElasticMetaObject(MetaType type) {
        checkNotNull("type",
                     type);
        checkNotEmpty("type.name",
                      type.getName());
        this.type = type;
        this.properties = new ArrayList<>();
    }

    @Override
    public MetaType getType() {
        return this.type;
    }

    @Override
    public Collection<MetaProperty> getProperties() {
        return this.properties;
    }

    @Override
    public Optional<MetaProperty> getProperty(String name) {
        checkNotEmpty("name",
                      name);
        return this.properties.stream().filter(metaProperty -> metaProperty.getName().equals(name)).findAny();
    }

    @Override
    public void addProperty(MetaProperty metaProperty) {
        this.properties.add(metaProperty);
    }
}
