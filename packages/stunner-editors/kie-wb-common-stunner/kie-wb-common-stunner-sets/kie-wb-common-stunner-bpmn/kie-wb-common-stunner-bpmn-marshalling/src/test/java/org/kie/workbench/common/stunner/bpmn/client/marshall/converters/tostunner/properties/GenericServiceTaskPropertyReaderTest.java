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

import org.eclipse.bpmn2.ServiceTask;
import org.eclipse.bpmn2.di.BPMNDiagram;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.properties.GenericServiceTaskPropertyWriter;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.DefinitionResolver;
import org.kie.workbench.common.stunner.bpmn.definition.property.dataio.AssignmentsInfo;
import org.kie.workbench.common.stunner.bpmn.definition.property.service.GenericServiceTaskValue;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.OnEntryAction;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.OnExitAction;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.ScriptTypeListValue;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.Factories.bpmn2;

@RunWith(MockitoJUnitRunner.class)
public class GenericServiceTaskPropertyReaderTest {

    private final static String SLA_DUE_DATE = "12/25/1983";
    private final static String SLA_DUE_DATE_CDATA = "<![CDATA[12/25/1983]]>";

    private GenericServiceTaskPropertyReader reader;

    @Mock
    private BPMNDiagram diagram;

    @Mock
    private DefinitionResolver definitionResolver;

    private GenericServiceTaskValue value;

    @Before
    public void setUp() {
        ServiceTask serviceTask = bpmn2.createServiceTask();
        value = new GenericServiceTaskValue("java",
                                            "serviceInterface",
                                            "serviceOperation",
                                            "inMessageStructure",
                                            "outMessageStructure");

        GenericServiceTaskPropertyWriter writer = new GenericServiceTaskPropertyWriter(serviceTask, null, null);
        writer.setValue(value);
        writer.setSLADueDate(SLA_DUE_DATE);
        writer.setAsync(false);
        writer.setAdHocAutostart(true);

        OnEntryAction onEntryAction = new OnEntryAction();
        onEntryAction.setValue(new ScriptTypeListValue());
        writer.setOnEntryAction(onEntryAction);

        OnExitAction onExitAction = new OnExitAction();
        onExitAction.setValue(new ScriptTypeListValue());
        writer.setOnExitAction(onExitAction);

        writer.setAssignmentsInfo(new AssignmentsInfo());

        reader = new GenericServiceTaskPropertyReader(serviceTask, diagram, definitionResolver);
    }

    @Test
    public void getGenericServiceTask() {
        GenericServiceTaskValue task = reader.getGenericServiceTask();
        assertEquals("Java", task.getServiceImplementation());
        assertEquals("serviceOperation", task.getServiceOperation());
        assertEquals("serviceInterface", task.getServiceInterface());
        assertEquals("inMessageStructure", task.getInMessageStructure());
        assertEquals("outMessageStructure", task.getOutMessagetructure());
        assertEquals(SLA_DUE_DATE_CDATA, reader.getSLADueDate());
        assertEquals(false, reader.isAsync());
        assertEquals(true, reader.isAdHocAutostart());
        assertNotNull(reader.getOnEntryAction());
        assertNotNull(reader.getOnExitAction());
        assertNotNull(reader.getAssignmentsInfo());
    }
}