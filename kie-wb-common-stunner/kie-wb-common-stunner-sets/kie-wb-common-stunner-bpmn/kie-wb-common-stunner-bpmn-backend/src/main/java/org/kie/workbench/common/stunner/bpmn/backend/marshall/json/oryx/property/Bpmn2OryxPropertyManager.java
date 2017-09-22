/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.backend.marshall.json.oryx.property;

import java.util.LinkedList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.core.definition.property.PropertyType;

/**
 * Provides the Property Serializers for the serialization expected by oryx/jbpmdesigner marshallers.
 */
@Dependent
public class Bpmn2OryxPropertyManager {

    Instance<Bpmn2OryxPropertySerializer<?>> propertySerializerInstances;
    private final List<Bpmn2OryxPropertySerializer<?>> propertySerializers = new LinkedList<>();

    protected Bpmn2OryxPropertyManager() {
    }

    public Bpmn2OryxPropertyManager(final List<Bpmn2OryxPropertySerializer<?>> propertySerializers) {
        this.propertySerializers.addAll(propertySerializers);
    }

    @Inject
    public Bpmn2OryxPropertyManager(final Instance<Bpmn2OryxPropertySerializer<?>> propertySerializerInstances) {
        this.propertySerializerInstances = propertySerializerInstances;
    }

    @PostConstruct
    public void init() {
        initPropertySerializers();
    }

    private void initPropertySerializers() {
        for (Bpmn2OryxPropertySerializer<?> serializerInstance : propertySerializerInstances) {
            propertySerializers.add(serializerInstance);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T parse(final Object property,
                       final PropertyType propertyType,
                       final String value) {
        Bpmn2OryxPropertySerializer<T> serializer = (Bpmn2OryxPropertySerializer<T>) getSerializer(propertyType);
        return serializer.parse(property,
                                value);
    }

    @SuppressWarnings("unchecked")
    public <T> String serialize(final Object property,
                                final PropertyType propertyType,
                                final T value) {
        Bpmn2OryxPropertySerializer<T> serializer = (Bpmn2OryxPropertySerializer<T>) getSerializer(propertyType);
        return serializer.serialize(property,
                                    value);
    }

    protected Bpmn2OryxPropertySerializer<?> getSerializer(final PropertyType type) {
        for (Bpmn2OryxPropertySerializer<?> serializer : propertySerializers) {
            if (serializer.accepts(type)) {
                return serializer;
            }
        }
        throw new RuntimeException("No property serializer found for type [" + type + "]");
    }
}
