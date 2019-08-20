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

package org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.properties;

import java.util.Arrays;

import org.eclipse.bpmn2.ExtensionAttributeValue;
import org.eclipse.bpmn2.TextAnnotation;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.jboss.drools.MetaDataType;
import org.jboss.drools.impl.MetaDataTypeImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.backend.converters.customproperties.CustomElement;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TextAnnotationPropertyWriterTest {

    public static final String NAME = "name";

    private TextAnnotationPropertyWriter tested;

    @Mock
    private TextAnnotation element;

    @Mock
    private VariableScope variableScope;

    @Mock
    private ExtensionAttributeValue extensionAttributeValue;

    @Mock
    private FeatureMap valueMap;

    @Mock
    private MetaDataType metaDataType;

    @Captor
    private ArgumentCaptor<FeatureMap.Entry> entryArgumentCaptor;

    @Before
    public void setUp() {
        when(element.getExtensionValues()).thenReturn(Arrays.asList(extensionAttributeValue));
        when(extensionAttributeValue.getValue()).thenReturn(valueMap);
        when(metaDataType.getName()).thenReturn(CustomElement.name.name());
        tested = new TextAnnotationPropertyWriter(element, variableScope);
    }

    @Test
    public void setName() {
        tested.setName(NAME);
        verify(element).setText(NAME);
        verify(element).setName(NAME);
        verify(valueMap).add(entryArgumentCaptor.capture());
        final MetaDataTypeImpl value = (MetaDataTypeImpl) entryArgumentCaptor.getValue().getValue();
        assertEquals(NAME, CustomElement.name.stripCData(value.getMetaValue()));
    }

    @Test
    public void getElement() {
        assertEquals(element, tested.getElement());
    }
}