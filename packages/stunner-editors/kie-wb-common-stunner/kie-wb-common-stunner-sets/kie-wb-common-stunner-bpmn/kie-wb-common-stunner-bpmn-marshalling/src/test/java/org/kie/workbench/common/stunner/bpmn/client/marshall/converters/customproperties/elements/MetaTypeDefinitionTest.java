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

import org.eclipse.bpmn2.BaseElement;
import org.eclipse.bpmn2.Bpmn2Factory;
import org.eclipse.bpmn2.ExtensionAttributeValue;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.impl.EStructuralFeatureImpl;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.jboss.drools.DroolsFactory;
import org.jboss.drools.DroolsPackage;
import org.jboss.drools.MetaDataType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.jboss.drools.DroolsPackage.Literals.DOCUMENT_ROOT__META_DATA;
import static org.junit.Assert.assertEquals;
import static org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.Factories.bpmn2;
import static org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.properties.Scripts.asCData;

@RunWith(MockitoJUnitRunner.class)
public abstract class MetaTypeDefinitionTest<T> {

    protected BaseElement baseElement;

    public abstract MetadataTypeDefinition<T> getMetaTypeDefinition();

    protected abstract T getTestValue();

    protected abstract T formattedResult(T testValue);

    protected abstract String formattedValue(T testValue);

    protected String asStringValue(T testValue) {
        return testValue.toString();
    }

    @Before
    public void setUp() {
        baseElement = bpmn2.createTask();
    }

    @Test
    public void testGetValue() {
        MetadataTypeDefinition<T> metadataTypeDefinition = getMetaTypeDefinition();
        T expectedValue = getTestValue();
        prepareBaseElementAttribute(metadataTypeDefinition.name(), asStringValue(expectedValue));
        assertEquals(formattedResult(expectedValue), metadataTypeDefinition.getValue(baseElement));
    }

    @Test
    public void testSetValue() throws Exception {
        MetadataTypeDefinition<T> metadataTypeDefinition = getMetaTypeDefinition();
        T expectedValue = getTestValue();
        metadataTypeDefinition.setValue(baseElement, expectedValue);
        ExtensionAttributeValue attributeValue = baseElement.getExtensionValues().get(0);
        FeatureMap extensionElements = attributeValue.getValue();
        List<MetaDataType> metadataExtensions = (List<MetaDataType>) extensionElements
                .get(DroolsPackage.Literals.DOCUMENT_ROOT__META_DATA, true);
        MetaDataType metaDataType = metadataExtensions.stream().filter(metaData -> metadataTypeDefinition.name().equals(metaData.getName())).findFirst().orElseThrow(() -> new Exception("expected metadata element:" + metadataTypeDefinition.name() + " was not found"));
        assertEquals(formattedValue(expectedValue), metaDataType.getMetaValue());
    }

    private void prepareBaseElementAttribute(String name, String value) {
        ExtensionAttributeValue eav = Bpmn2Factory.eINSTANCE.createExtensionAttributeValue();
        baseElement.getExtensionValues().add(eav);
        eav.getValue().add(new EStructuralFeatureImpl.SimpleFeatureMapEntry((EStructuralFeature.Internal) DOCUMENT_ROOT__META_DATA, metaDataOf(name, value)));
    }

    private MetaDataType metaDataOf(String name, String value) {
        MetaDataType eleMetadata = DroolsFactory.eINSTANCE.createMetaDataType();
        eleMetadata.setName(name);
        eleMetadata.setMetaValue(asCData(value));
        return eleMetadata;
    }
}
