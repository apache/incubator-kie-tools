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

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.bpmn2.BaseElement;
import org.eclipse.bpmn2.ExtensionAttributeValue;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.impl.EStructuralFeatureImpl;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.jboss.drools.DroolsFactory;
import org.jboss.drools.MetaDataType;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.customproperties.CustomElement;

import static org.jboss.drools.DroolsPackage.Literals.DOCUMENT_ROOT__META_DATA;
import static org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.properties.Scripts.asCData;

public class MetaDataAttributesElement extends ElementDefinition<String> {

    public static final String DELIMITER = "Ø";
    public static final String SEPARATOR = "ß";

    public MetaDataAttributesElement(String name) {
        super(name, "");
    }

    @Override
    public String getValue(BaseElement element) {
        return getStringValue(element)
                .orElse(getDefaultValue());
    }

    @Override
    public void setValue(BaseElement element, String value) {
        setStringValue(element, value);
    }

    protected void setStringValue(BaseElement element, String value) {
        Stream.of(value.split(DELIMITER))
                .map(this::extensionOf)
                .forEach(getExtensionElements(element)::add);
    }

    @Override
    protected Optional<String> getStringValue(BaseElement element) {
        List<ExtensionAttributeValue> extValues = element.getExtensionValues();

        List<FeatureMap> extElementsList = extValues.stream()
                .map(ExtensionAttributeValue::getValue)
                .collect(Collectors.toList());

        List<MetaDataType> metadataExtensions = extElementsList.stream()
                .map(extAttrVal -> (List<MetaDataType>) extAttrVal.get(DOCUMENT_ROOT__META_DATA, true))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        String metaDataAttributes = metadataExtensions.stream()
                .filter(metaDataType -> metaDataType.getName() != null)
                .filter(metaDataType -> isMetaDataAttribute(metaDataType.getName()))
                .map(metaDataType -> metaDataType.getName()
                        + SEPARATOR
                        + ((null != metaDataType.getMetaValue() && metaDataType.getMetaValue().length() > 0)
                        ? metaDataType.getMetaValue() : ""))
                .collect(Collectors.joining(DELIMITER));

        return Optional.ofNullable(metaDataAttributes);
    }

    protected FeatureMap.Entry extensionOf(String metaData) {
        return new EStructuralFeatureImpl.SimpleFeatureMapEntry(
                (EStructuralFeature.Internal) DOCUMENT_ROOT__META_DATA,
                metaDataTypeDataOf(metaData));
    }

    protected MetaDataType metaDataTypeDataOf(String metaData) {
        MetaDataType metaDataType = DroolsFactory.eINSTANCE.createMetaDataType();
        String[] properties = metaData.split(SEPARATOR);
        metaDataType.setName(properties[0]);
        metaDataType.setMetaValue(properties.length > 1 ? asCData(properties[1]) : null);

        return metaDataType;
    }

    protected boolean isMetaDataAttribute(String metaDataTypeName) {
        if (metaDataTypeName.length() <= 0) {
            return false;
        }

        return !metaDataTypeName.equals(CustomElement.async.name()) &&
                !metaDataTypeName.equals(CustomElement.autoStart.name()) &&
                !metaDataTypeName.equals(CustomElement.autoConnectionSource.name()) &&
                !metaDataTypeName.equals(CustomElement.autoConnectionTarget.name()) &&
                !metaDataTypeName.equals(CustomElement.customTags.name()) &&
                !metaDataTypeName.equals(CustomElement.description.name()) &&
                !metaDataTypeName.equals(CustomElement.scope.name()) &&
                !metaDataTypeName.equals(CustomElement.name.name()) &&
                !metaDataTypeName.equals(CustomElement.caseIdPrefix.name()) &&
                !metaDataTypeName.equals(CustomElement.caseRole.name()) &&
                !metaDataTypeName.equals(CustomElement.slaDueDate.name()) &&
                !metaDataTypeName.equals(CustomElement.isCase.name()) &&
                !metaDataTypeName.equals(CustomElement.customActivationCondition.name()) &&
                !metaDataTypeName.equals(CustomElement.abortParent.name());
    }
}