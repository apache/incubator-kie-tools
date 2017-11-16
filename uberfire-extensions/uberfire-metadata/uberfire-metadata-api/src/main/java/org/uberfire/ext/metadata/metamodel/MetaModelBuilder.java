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

package org.uberfire.ext.metadata.metamodel;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.stream.StreamSupport;

import org.uberfire.ext.metadata.engine.MetaModelStore;
import org.uberfire.ext.metadata.model.KObject;
import org.uberfire.ext.metadata.model.KProperty;
import org.uberfire.ext.metadata.model.impl.MetaObjectImpl;
import org.uberfire.ext.metadata.model.impl.MetaPropertyImpl;
import org.uberfire.ext.metadata.model.schema.MetaObject;
import org.uberfire.ext.metadata.model.schema.MetaProperty;

public class MetaModelBuilder {

    private final MetaModelStore metaModelStore;

    public MetaModelBuilder(MetaModelStore metaModelStore) {
        this.metaModelStore = metaModelStore;
    }

    public void updateMetaModel(final KObject object) {
        final MetaObject metaObject = metaModelStore.getMetaObject(object.getType().getName());
        if (metaObject == null) {
            metaModelStore.add(newMetaObject(object));
        } else {
            Optional.ofNullable(object.getProperties())
                    .ifPresent(kProperties -> {
                        for (final KProperty property : object.getProperties()) {
                            final Optional<MetaProperty> metaProperty = metaObject.getProperty(property.getName());
                            if (!metaProperty.isPresent()) {
                                metaObject.addProperty(newMetaProperty(property));
                            } else {
                                metaProperty.get().addType(property.getValue().getClass());
                                if (property.isSearchable()) {
                                    metaProperty.get().setAsSearchable();
                                }
                            }
                        }
                        metaModelStore.update(metaObject);
                    });
        }
    }

    public MetaObject newMetaObject(final KObject object) {
        final Set<MetaProperty> properties = new HashSet<>();

        Optional.ofNullable(object.getProperties()).ifPresent(kProperties ->
                                                                      StreamSupport.stream(kProperties.spliterator(),
                                                                                           false).forEach(property ->
                                                                                                                  properties.add(newMetaProperty(property))));
        return new MetaObjectImpl(object.getType(),
                                  properties);
    }

    private MetaProperty newMetaProperty(final KProperty<?> property) {

        Set<Class<?>> types = new CopyOnWriteArraySet<Class<?>>() {{
            add(property.getValue().getClass());
        }};

        return new MetaPropertyImpl(property.getName(),
                                    property.isSearchable(),
                                    property.isSortable(),
                                    types);
    }
}
