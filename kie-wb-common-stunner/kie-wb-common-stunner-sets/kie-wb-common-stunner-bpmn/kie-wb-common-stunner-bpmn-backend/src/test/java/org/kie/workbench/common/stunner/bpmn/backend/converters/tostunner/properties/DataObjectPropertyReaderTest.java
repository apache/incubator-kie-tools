/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.properties;

import java.util.Arrays;
import java.util.Collections;

import org.eclipse.bpmn2.DataObject;
import org.eclipse.bpmn2.DataObjectReference;
import org.eclipse.bpmn2.ExtensionAttributeValue;
import org.eclipse.bpmn2.di.BPMNDiagram;
import org.eclipse.bpmn2.di.BPMNShape;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.jboss.drools.DroolsPackage;
import org.jboss.drools.MetaDataType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.backend.converters.customproperties.CustomElement;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.Factories.bpmn2;
import static org.mockito.Mockito.spy;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DataObjectPropertyReaderTest {

    private DataObjectPropertyReader tested;

    @Mock
    private DataObjectReference ref;

    @Mock
    private DataObject dataObject;

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

        dataObject = spy(bpmn2.createDataObject());

        when(ref.getDataObjectRef()).thenReturn(dataObject);

        when(dataObject.getName()).thenReturn("name");
        when(dataObject.getExtensionValues()).thenReturn(Arrays.asList(extensionAttributeValue));
        when(extensionAttributeValue.getValue()).thenReturn(valueMap);
        when(valueMap.get(DroolsPackage.Literals.DOCUMENT_ROOT__META_DATA, true)).thenReturn(Arrays.asList(metaDataType));
        when(metaDataType.getName()).thenReturn(CustomElement.name.name());
        when(metaDataType.getMetaValue()).thenReturn("custom");

        tested = new DataObjectPropertyReader(ref, diagram, shape, RESOLUTION_FACTOR);
    }

    @Test
    public void getExtendedName() {
        String name = tested.getName();
        assertEquals("custom", name);
    }

    @Test
    public void getName() {
        when(dataObject.getExtensionValues()).thenReturn(Collections.emptyList());
        String name = tested.getName();
        assertEquals("name", name);
    }

}