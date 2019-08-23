/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.backend.converters.customproperties.elements;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import org.eclipse.bpmn2.BaseElement;
import org.eclipse.bpmn2.ExtensionAttributeValue;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.impl.EStructuralFeatureImpl;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.jboss.drools.DroolsFactory;
import org.jboss.drools.DroolsPackage;
import org.jboss.drools.MetaDataType;

import static org.jboss.drools.DroolsPackage.Literals.DOCUMENT_ROOT__META_DATA;
import static org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.properties.Scripts.asCData;

public abstract class MetadataTypeDefinition<T> extends ElementDefinition<T> {

    public MetadataTypeDefinition(String name, T defaultValue) {
        super(name, defaultValue);
    }

    @Override
    public abstract T getValue(BaseElement element);

    @Override
    public abstract void setValue(BaseElement element, T value);

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

    protected void setStringValue(BaseElement element, String value) {
        FeatureMap.Entry extension = extensionOf(
                DOCUMENT_ROOT__META_DATA, metaDataOf(value));
        getExtensionElements(element).add(extension);
    }

    private FeatureMap.Entry extensionOf(EReference eref, MetaDataType eleMetadata) {
        return new EStructuralFeatureImpl.SimpleFeatureMapEntry((EStructuralFeature.Internal) eref, eleMetadata);
    }

    private MetaDataType metaDataOf(String value) {
        MetaDataType eleMetadata = DroolsFactory.eINSTANCE.createMetaDataType();
        eleMetadata.setName(this.name());
        eleMetadata.setMetaValue(asCData(value));
        return eleMetadata;
    }

    private static boolean isMetaType(MetaDataType metaType,
                                      final String metaDataName) {
        return metaType.getName() != null &&
                metaType.getName().equals(metaDataName) &&
                metaType.getMetaValue() != null &&
                metaType.getMetaValue().length() > 0;
    }

    protected Optional<String> getStringValue(BaseElement element) {
        return Optional.ofNullable(getMetaDataValue(element.getExtensionValues(), name()));
    }

    public String stripCData(String s) {
        String BEGIN_CDATA = "<![CDATA[";
        String END_CDATA = "]]>";
        return s.startsWith(BEGIN_CDATA) && s.endsWith(END_CDATA) ? s.substring(BEGIN_CDATA.length(), s.length() - END_CDATA.length()) : s;
    }
}