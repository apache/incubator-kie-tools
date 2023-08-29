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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.bpmn2.ItemDefinition;
import org.eclipse.bpmn2.Property;
import org.eclipse.bpmn2.impl.Bpmn2FactoryImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.customproperties.CustomElement;
import org.kie.workbench.common.stunner.bpmn.definition.property.cm.CaseFileVariables;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ProcessVariableReaderTest {

    private List<Property> properties;

    protected Property property1;
    protected Property property2;
    protected Property property3;
    protected Property property4;
    protected Property property5;

    @Mock
    protected ItemDefinition definition;

    @Before
    public void setup() {
        when(definition.getStructureRef()).thenReturn("Boolean");

        Bpmn2FactoryImpl bpmn2Factory = new Bpmn2FactoryImpl();

        property1 = bpmn2Factory.createProperty();
        property1.setName("PV1");
        property1.setId("PV1");
        property1.setItemSubjectRef(definition);
        CustomElement.customTags.of(property1).set("internal;input;customTag");

        property2 = bpmn2Factory.createProperty();
        property2.setName(null);
        property2.setId("PV2");

        property3 = bpmn2Factory.createProperty();
        property3.setName("");
        property3.setId("PV3");

        property4 = bpmn2Factory.createProperty();
        property4.setName(CaseFileVariables.CASE_FILE_PREFIX + "CV4");
        property4.setId(CaseFileVariables.CASE_FILE_PREFIX + "CV4");

        property5 = bpmn2Factory.createProperty();
        property5.setName(null);
        property5.setId(CaseFileVariables.CASE_FILE_PREFIX + "CV5");

        properties = new ArrayList<>();
        properties.add(property1);
        properties.add(property2);
        properties.add(property3);
        properties.add(property4);
        properties.add(property5);
    }

    @Test
    public void getProcessVariables() {
        String result = ProcessVariableReader.getProcessVariables(properties);
        assertEquals("PV1:Boolean:<![CDATA[internal;input;customTag]]>,PV2::[],PV3::[]", result);
    }

    @Test
    public void getProcessVariableName() {
        assertEquals("PV1", ProcessVariableReader.getProcessVariableName(property1));
        assertEquals("PV2", ProcessVariableReader.getProcessVariableName(property2));
        assertEquals("PV3", ProcessVariableReader.getProcessVariableName(property3));
        assertEquals("caseFile_CV4", ProcessVariableReader.getProcessVariableName(property4));
        assertEquals("caseFile_CV5", ProcessVariableReader.getProcessVariableName(property5));
    }

    @Test
    public void isProcessVariable() {
        assertTrue(ProcessVariableReader.isProcessVariable(property1));
        assertTrue(ProcessVariableReader.isProcessVariable(property2));
        assertTrue(ProcessVariableReader.isProcessVariable(property3));
        assertFalse(ProcessVariableReader.isProcessVariable(property4));
        assertFalse(ProcessVariableReader.isProcessVariable(property5));
    }
}