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


package org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.properties;

import java.util.Arrays;

import org.eclipse.bpmn2.ExtensionAttributeValue;
import org.eclipse.bpmn2.TextAnnotation;
import org.eclipse.bpmn2.di.BPMNDiagram;
import org.eclipse.bpmn2.di.BPMNShape;
import org.eclipse.emf.common.util.ECollections;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.jboss.drools.DroolsPackage;
import org.jboss.drools.MetaDataType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.customproperties.CustomElement;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TextAnnotationPropertyReaderTest {

    private TextAnnotationPropertyReader tested;

    @Mock
    private TextAnnotation element;

    @Mock
    private BPMNDiagram diagram;

    @Mock
    private BPMNShape shape;

    private static final double RESOLUTION_FACTOR = 1;

    @Mock
    private ExtensionAttributeValue extensionAttributeValue;

    @Mock
    private FeatureMap valueMap;

    @Mock
    private MetaDataType metaDataType;

    @Before
    public void setUp() {
        when(element.getName()).thenReturn("name");
        when(element.getText()).thenReturn("text");
        when(element.getExtensionValues()).thenReturn(ECollections.asEList(extensionAttributeValue));
        when(extensionAttributeValue.getValue()).thenReturn(valueMap);
        when(valueMap.get(DroolsPackage.Literals.DOCUMENT_ROOT__META_DATA, true)).thenReturn(Arrays.asList(metaDataType));
        when(metaDataType.getName()).thenReturn(CustomElement.name.name());
        when(metaDataType.getMetaValue()).thenReturn("custom");

        tested = new TextAnnotationPropertyReader(element, diagram, shape, RESOLUTION_FACTOR);
    }

    @Test
    public void getExtendedName() {
        String name = tested.getName();
        assertEquals("custom", name);
    }

    @Test
    public void getName() {
        when(element.getExtensionValues()).thenReturn(ECollections.emptyEList());
        String name = tested.getName();
        assertEquals("name", name);
    }

    @Test
    public void getTextName() {
        when(element.getExtensionValues()).thenReturn(ECollections.emptyEList());
        when(element.getName()).thenReturn(null);
        String name = tested.getName();
        assertEquals("text", name);
    }

    @Test
    public void getNameNull() {
        when(element.getExtensionValues()).thenReturn(ECollections.emptyEList());
        when(element.getName()).thenReturn(null);
        when(element.getText()).thenReturn(null);
        String name = tested.getName();
        assertEquals("", name);
    }
}