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

import java.util.Collections;

import org.eclipse.bpmn2.Definitions;
import org.eclipse.bpmn2.SubProcess;
import org.eclipse.bpmn2.di.BPMNDiagram;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.customproperties.CustomElement;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.DefinitionResolver;
import org.mockito.junit.MockitoJUnitRunner;

import static junit.framework.TestCase.assertTrue;
import static org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.Factories.bpmn2;
import static org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.Factories.di;

@RunWith(MockitoJUnitRunner.class)
public class EventSubprocessPropertyReaderTest {

    private DefinitionResolver definitionResolverReal;

    private SubProcessPropertyReader tested;

    @Before
    public void setUp() {
        Definitions definitions = bpmn2.createDefinitions();
        definitions.getRootElements().add(bpmn2.createProcess());
        BPMNDiagram bpmnDiagram = di.createBPMNDiagram();
        bpmnDiagram.setPlane(di.createBPMNPlane());
        definitions.getDiagrams().add(bpmnDiagram);

        definitionResolverReal = new DefinitionResolver(definitions, Collections.emptyList());
    }

    @Test
    public void testIsAsync() {
        SubProcess eventSubProcess = bpmn2.createSubProcess();
        CustomElement.async.of(eventSubProcess).set(Boolean.TRUE);

        tested = new SubProcessPropertyReader(eventSubProcess,
                                              definitionResolverReal.getDiagram(),
                                              definitionResolverReal);

        assertTrue(tested.isAsync());
    }

    @Test
    public void testGetSlaDueDate() {
        String rawSlaDueDate = "12/25/1983";

        SubProcess eventSubProcess = bpmn2.createSubProcess();
        CustomElement.slaDueDate.of(eventSubProcess).set(rawSlaDueDate);

        tested = new SubProcessPropertyReader(eventSubProcess,
                                              definitionResolverReal.getDiagram(),
                                              definitionResolverReal);

        assertTrue(tested.getSlaDueDate().contains(rawSlaDueDate));
    }
}
