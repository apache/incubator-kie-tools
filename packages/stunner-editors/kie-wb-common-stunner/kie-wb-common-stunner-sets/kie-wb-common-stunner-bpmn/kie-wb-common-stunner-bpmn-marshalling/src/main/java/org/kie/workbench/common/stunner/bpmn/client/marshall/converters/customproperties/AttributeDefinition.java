/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package org.kie.workbench.common.stunner.bpmn.client.marshall.converters.customproperties;

import java.util.Optional;

import org.eclipse.bpmn2.BaseElement;
import org.eclipse.emf.ecore.impl.EAttributeImpl;
import org.eclipse.emf.ecore.impl.EStructuralFeatureImpl;
import org.eclipse.emf.ecore.util.FeatureMap;

import static org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.Factories.metaData;

public abstract class AttributeDefinition<T> {

    protected final T defaultValue;
    private final String namespace;
    private final String name;

    public AttributeDefinition(String namespace, String name, T defaultValue) {
        this.namespace = namespace;
        this.name = name;
        this.defaultValue = defaultValue;
    }

    public String name() {
        return name;
    }

    public abstract T getValue(BaseElement element);

    public abstract void setValue(BaseElement element, T value);

    Optional<String> getStringValue(BaseElement element) {
        return element.getAnyAttribute().stream()
                .filter(e -> this.name().equals(e.getEStructuralFeature().getName()))
                .map(FeatureMap.Entry::getValue)
                .map(String::valueOf)
                .findFirst();
    }

    void setStringValue(BaseElement element, String value) {
        EAttributeImpl extensionAttribute = (EAttributeImpl) metaData.demandFeature(
                "http://www.jboss.org/drools",
                name,
                false,
                false);

        EStructuralFeatureImpl.SimpleFeatureMapEntry feature =
                new EStructuralFeatureImpl.SimpleFeatureMapEntry(extensionAttribute, value);

        element.getAnyAttribute().add(feature);
    }

    public CustomAttribute<T> of(BaseElement element) {
        return new CustomAttribute<>(this, element);
    }
}

class BooleanAttribute extends AttributeDefinition<Boolean> {

    BooleanAttribute(String namespace, String name, Boolean defaultValue) {
        super(namespace, name, defaultValue);
    }

    @Override
    public Boolean getValue(BaseElement element) {
        return getStringValue(element)
                .map(Boolean::parseBoolean)
                .orElse(defaultValue);
    }

    @Override
    public void setValue(BaseElement element, Boolean value) {
        setStringValue(element, String.valueOf(value));
    }
}

class StringAttribute extends AttributeDefinition<String> {

    StringAttribute(String namespace, String name, String defaultValue) {
        super(namespace, name, defaultValue);
    }

    @Override
    public String getValue(BaseElement element) {
        return getStringValue(element)
                .orElse(defaultValue);
    }

    @Override
    public void setValue(BaseElement element, String value) {
        setStringValue(element, value);
    }
}