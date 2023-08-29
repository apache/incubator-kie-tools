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


package org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.properties;

import java.util.HashSet;

import org.eclipse.bpmn2.DataObjectReference;
import org.eclipse.bpmn2.ExtensionAttributeValue;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.jboss.drools.MetaDataType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.customproperties.CustomElement;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.Factories.bpmn2;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class DataObjectPropertyWriterTest {

    public static final String NAME = "name";

    private DataObjectPropertyWriter tested;

    private final DataObjectReference reference = bpmn2.createDataObjectReference();

    @Mock
    private VariableScope variableScope;

    @Mock
    private ExtensionAttributeValue extensionAttributeValue;

    @Mock
    private FeatureMap valueMap;

    @Mock
    private MetaDataType metaDataType;

    @Before
    public void setUp() {
        when(extensionAttributeValue.getValue()).thenReturn(valueMap);
        when(metaDataType.getName()).thenReturn(CustomElement.name.name());
        tested = new DataObjectPropertyWriter(reference, variableScope, new HashSet<>());
    }

    @Test
    public void setName() {
        tested.setName(NAME);
        assertEquals(NAME, reference.getDataObjectRef().getName());
    }

    @Test
    public void setType() {
        tested.setType(NAME);
        assertEquals(NAME, reference.getDataObjectRef().getItemSubjectRef().getStructureRef());
    }

    @Test
    public void getDataObjects() {
        assertEquals(0, tested.getDataObjects().size());
    }

    @Test
    public void getElement() {
        assertEquals(reference, tested.getElement());
    }
}