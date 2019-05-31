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

package org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.properties;

import org.eclipse.bpmn2.ServiceTask;
import org.eclipse.bpmn2.di.BPMNDiagram;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.properties.GenericServiceTaskPropertyWriter;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.DefinitionResolver;
import org.kie.workbench.common.stunner.bpmn.definition.property.service.GenericServiceTaskValue;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.Factories.bpmn2;

@RunWith(MockitoJUnitRunner.class)
public class GenericServiceTaskPropertyReaderTest {

    private GenericServiceTaskPropertyReader reader;

    @Mock
    private BPMNDiagram diagram;

    @Mock
    private DefinitionResolver definitionResolver;

    @Before
    public void setUp() {
        ServiceTask serviceTask = bpmn2.createServiceTask();
        GenericServiceTaskPropertyWriter writer = new GenericServiceTaskPropertyWriter(serviceTask, null);
        writer.setServiceImplementation("WebService");
        writer.setServiceInterface("setServiceInterface");
        writer.setServiceOperation("setServiceOperation");
        reader = new GenericServiceTaskPropertyReader(serviceTask, diagram, definitionResolver);
    }

    @Test
    public void getGenericServiceTask() {
        GenericServiceTaskValue task = reader.getGenericServiceTask();
        assertEquals("setServiceOperation", task.getServiceOperation());
        assertEquals("setServiceInterface", task.getServiceInterface());
        assertEquals("WebService", task.getServiceImplementation());
    }
}