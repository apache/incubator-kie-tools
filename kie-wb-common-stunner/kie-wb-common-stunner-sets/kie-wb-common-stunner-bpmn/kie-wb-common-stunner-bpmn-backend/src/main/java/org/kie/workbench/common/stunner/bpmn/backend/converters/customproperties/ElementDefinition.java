/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.backend.converters.customproperties;

import java.util.Optional;

import org.eclipse.bpmn2.BaseElement;
import org.eclipse.bpmn2.Bpmn2Factory;
import org.eclipse.bpmn2.ExtensionAttributeValue;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.impl.EStructuralFeatureImpl;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.jboss.drools.DroolsFactory;
import org.jboss.drools.MetaDataType;
import org.kie.workbench.common.stunner.bpmn.backend.legacy.util.Utils;

import static org.jboss.drools.DroolsPackage.Literals.DOCUMENT_ROOT__META_DATA;
import static org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.properties.Scripts.asCData;

public abstract class ElementDefinition<T> {

    protected final T defaultValue;
    private final String name;

    public ElementDefinition(String name, T defaultValue) {
        this.name = name;
        this.defaultValue = defaultValue;
    }

    public String name() {
        return name;
    }

    public abstract T getValue(BaseElement element);

    public abstract void setValue(BaseElement element, T value);

    protected Optional<java.lang.String> getStringValue(BaseElement element) {
        return Optional.ofNullable(Utils.getMetaDataValue(element.getExtensionValues(), name));
    }

    protected void setStringValue(BaseElement element, String value) {
        FeatureMap.Entry extension = extensionOf(
                DOCUMENT_ROOT__META_DATA, metaDataOf(value));
        getExtensionElements(element).add(extension);
    }

    private FeatureMap.Entry extensionOf(EReference eref, MetaDataType eleMetadata) {
        return new EStructuralFeatureImpl.SimpleFeatureMapEntry(
                (EStructuralFeature.Internal) eref,
                eleMetadata);
    }

    private MetaDataType metaDataOf(String value) {
        MetaDataType eleMetadata = DroolsFactory.eINSTANCE.createMetaDataType();
        eleMetadata.setName(name);
        eleMetadata.setMetaValue(asCData(value));
        return eleMetadata;
    }

    protected FeatureMap getExtensionElements(BaseElement element) {
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

    public String stripCData(String s) {
        String BEGIN_CDATA = "<![CDATA[";
        String END_CDATA = "]]>";
        return s.startsWith(BEGIN_CDATA) && s.endsWith(END_CDATA) ? s.substring(BEGIN_CDATA.length(), s.length() - END_CDATA.length()) : s;
    }
}

class BooleanElement extends ElementDefinition<Boolean> {

    BooleanElement(String name, java.lang.Boolean defaultValue) {
        super(name, defaultValue);
    }

    @Override
    public java.lang.Boolean getValue(BaseElement element) {
        return getStringValue(element)
                .map(this::stripCData)
                .map(java.lang.Boolean::parseBoolean)
                .orElse(defaultValue);
    }

    @Override
    public void setValue(BaseElement element, Boolean value) {
        setStringValue(element, String.valueOf(value));
    }
}

class StringElement extends ElementDefinition<String> {

    StringElement(String name, java.lang.String defaultValue) {
        super(name, defaultValue);
    }

    @Override
    public java.lang.String getValue(BaseElement element) {
        return getStringValue(element)
                .orElse(defaultValue);
    }

    @Override
    public void setValue(BaseElement element, String value) {
        setStringValue(element, value);
    }
}