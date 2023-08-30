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

import org.eclipse.bpmn2.ServiceTask;
import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.customproperties.CustomAttribute;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.customproperties.CustomElement;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.properties.Scripts;
import org.kie.workbench.common.stunner.bpmn.definition.property.dataio.AssignmentsInfo;
import org.kie.workbench.common.stunner.bpmn.definition.property.service.GenericServiceTaskValue;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.Factories.bpmn2;

public class GenericServiceTaskPropertyWriterTest {

    private final static String SLA_DUE_DATE = "12/25/1983";
    private final static String SLA_DUE_DATE_CDATA = "<![CDATA[12/25/1983]]>";

    private GenericServiceTaskPropertyWriter w;

    private ServiceTask serviceTask = bpmn2.createServiceTask();

    private GenericServiceTaskValue value;

    @Before
    public void setUp() {
        PropertyWriterFactory writerFactory = new PropertyWriterFactory();
        w = writerFactory.of(serviceTask);
        value = new GenericServiceTaskValue("Java",
                                            "serviceInterface",
                                            "serviceOperation",
                                            "inMessageStructure",
                                            "outMessagetructure");
    }

    @Test
    public void setAndTestJava() {
        w.setValue(value);
        w.setSLADueDate(SLA_DUE_DATE);
        w.setAsync(false);
        w.setAdHocAutostart(false);
        w.setAssignmentsInfo(new AssignmentsInfo());

        assertServiceTaskProperties("Java");
        assertEquals(SLA_DUE_DATE_CDATA, CustomElement.slaDueDate.of(serviceTask).get());
        assertEquals(false, CustomElement.async.of(serviceTask).get());
        assertEquals(false, CustomElement.autoStart.of(serviceTask).get());
        assertNotNull(Scripts.onEntry(serviceTask));
    }

    private void assertServiceTaskProperties(String serviceImplementation) {
        assertEquals(serviceImplementation, CustomAttribute.serviceImplementation.of(serviceTask).get());
        assertEquals("serviceOperation", CustomAttribute.serviceOperation.of(serviceTask).get());
        assertEquals("serviceInterface", CustomAttribute.serviceInterface.of(serviceTask).get());
        assertEquals("serviceOperation", serviceTask.getOperationRef().getName());
        assertEquals("inMessageStructure", serviceTask.getOperationRef().getInMessageRef().getItemRef().getStructureRef());
        assertEquals("outMessagetructure", serviceTask.getOperationRef().getOutMessageRef().getItemRef().getStructureRef());
    }

    @Test
    public void setAndTestWebService() {
        value.setServiceImplementation("WebServicee");
        w.setValue(value);
        w.setSLADueDate(SLA_DUE_DATE);
        w.setAsync(false);
        w.setAdHocAutostart(false);

        assertServiceTaskProperties("WebServicee");
        assertEquals(SLA_DUE_DATE_CDATA, CustomElement.slaDueDate.of(serviceTask).get());
        assertEquals(false, CustomElement.async.of(serviceTask).get());
        assertEquals(false, CustomElement.autoStart.of(serviceTask).get());
    }
}