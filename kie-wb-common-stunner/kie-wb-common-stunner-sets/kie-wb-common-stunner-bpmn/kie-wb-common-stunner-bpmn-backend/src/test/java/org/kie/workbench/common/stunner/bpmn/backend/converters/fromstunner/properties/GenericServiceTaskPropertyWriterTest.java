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

import org.eclipse.bpmn2.ServiceTask;
import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.stunner.bpmn.backend.converters.customproperties.CustomAttribute;
import org.kie.workbench.common.stunner.bpmn.backend.converters.customproperties.CustomElement;

import static org.junit.Assert.assertEquals;
import static org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.Factories.bpmn2;

public class GenericServiceTaskPropertyWriterTest {

    private final static String SLA_DUE_DATE = "12/25/1983";
    private final static String SLA_DUE_DATE_CDATA = "<![CDATA[12/25/1983]]>";

    private GenericServiceTaskPropertyWriter w;

    private ServiceTask serviceTask = bpmn2.createServiceTask();

    @Before
    public void setUp() {
        PropertyWriterFactory writerFactory = new PropertyWriterFactory();
        w = writerFactory.of(serviceTask);
    }

    @Test
    public void setAndTestJava() {
        w.setServiceImplementation("Java");
        w.setServiceOperation("setServiceOperation");
        w.setServiceInterface("setServiceInterface");
        w.setSlaDueDate(SLA_DUE_DATE);

        assertEquals("Java", CustomAttribute.serviceImplementation.of(serviceTask).get());
        assertEquals("setServiceOperation", CustomAttribute.serviceOperation.of(serviceTask).get());
        assertEquals("setServiceInterface", CustomAttribute.serviceInterface.of(serviceTask).get());
        assertEquals(SLA_DUE_DATE_CDATA, CustomElement.slaDueDate.of(serviceTask).get());
    }

    @Test
    public void setAndTestWebService() {
        w.setServiceImplementation("WebService");
        w.setServiceOperation("setServiceOperation");
        w.setServiceInterface("setServiceInterface");
        w.setSlaDueDate(SLA_DUE_DATE);

        assertEquals("##WebService", CustomAttribute.serviceImplementation.of(serviceTask).get());
        assertEquals("setServiceOperation", CustomAttribute.serviceOperation.of(serviceTask).get());
        assertEquals("setServiceInterface", CustomAttribute.serviceInterface.of(serviceTask).get());
        assertEquals(SLA_DUE_DATE_CDATA, CustomElement.slaDueDate.of(serviceTask).get());
    }
}