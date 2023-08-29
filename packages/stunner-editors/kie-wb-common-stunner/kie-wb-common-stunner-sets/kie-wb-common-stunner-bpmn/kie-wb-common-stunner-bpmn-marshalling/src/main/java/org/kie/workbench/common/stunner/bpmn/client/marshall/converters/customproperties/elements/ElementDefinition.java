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


package org.kie.workbench.common.stunner.bpmn.client.marshall.converters.customproperties.elements;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import org.eclipse.bpmn2.BaseElement;
import org.eclipse.bpmn2.Bpmn2Factory;
import org.eclipse.bpmn2.ExtensionAttributeValue;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.jboss.drools.DroolsPackage;
import org.jboss.drools.MetaDataType;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.customproperties.CustomElement;

public abstract class ElementDefinition<T> {

    private final T defaultValue;
    private final String name;

    public ElementDefinition(String name, T defaultValue) {
        this.name = name;
        this.defaultValue = defaultValue;
    }

    public String name() {
        return name;
    }

    public final T getDefaultValue() {
        return defaultValue;
    }

    public abstract T getValue(BaseElement element);

    public abstract void setValue(BaseElement element, T value);

    protected Optional<String> getStringValue(BaseElement element) {
        return Optional.ofNullable(getMetaDataValue(element.getExtensionValues(), name));
    }

    protected abstract void setStringValue(BaseElement element, String value);

    public static FeatureMap getExtensionElements(BaseElement element) {
        if (element.getExtensionValues() == null || element.getExtensionValues().isEmpty()) {
            ExtensionAttributeValue eav = Bpmn2Factory.eINSTANCE.createExtensionAttributeValue();
            element.getExtensionValues().add(eav);
            return eav.getValue();
        } else {
            return element.getExtensionValues().get(0).getValue();
        }
    }

    public CustomElement<T> of(BaseElement element) {
        return new CustomElement<>(this, element);
    }

    @SuppressWarnings("unchecked")
    private static String getMetaDataValue(final List<ExtensionAttributeValue> extensionValues,
                                           final String metaDataName) {
        if (extensionValues != null) {
            return extensionValues.stream()
                    .map(ExtensionAttributeValue::getValue)
                    .flatMap((Function<FeatureMap, Stream<?>>) extensionElements -> {
                        List o = (List) extensionElements.get(DroolsPackage.Literals.DOCUMENT_ROOT__META_DATA, true);
                        return o.stream();
                    })
                    .filter(metaType -> isMetaType((MetaDataType) metaType, metaDataName))
                    .findFirst()
                    .map(m -> ((MetaDataType) m).getMetaValue())
                    .orElse(null);
        }
        return null;
    }

    private static boolean isMetaType(MetaDataType metaType,
                                      final String metaDataName) {
        return metaType.getName() != null &&
                metaType.getName().equals(metaDataName) &&
                metaType.getMetaValue() != null &&
                metaType.getMetaValue().length() > 0;
    }
}