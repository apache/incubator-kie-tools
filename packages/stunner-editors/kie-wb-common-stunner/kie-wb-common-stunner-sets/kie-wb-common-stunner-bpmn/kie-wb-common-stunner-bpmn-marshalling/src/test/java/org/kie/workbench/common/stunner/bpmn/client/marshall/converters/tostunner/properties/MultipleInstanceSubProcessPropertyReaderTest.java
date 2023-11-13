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
import org.eclipse.bpmn2.di.BPMNPlane;
import org.eclipse.bpmn2.di.BPMNShape;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.customproperties.CustomElement;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.DefinitionResolver;
import org.mockito.junit.MockitoJUnitRunner;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNull;
import static junit.framework.TestCase.assertTrue;
import static org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.Factories.bpmn2;
import static org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.Factories.di;

@RunWith(MockitoJUnitRunner.class)
public class MultipleInstanceSubProcessPropertyReaderTest {

    private DefinitionResolver definitionResolverReal;

    private MultipleInstanceSubProcessPropertyReader tested;

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
        SubProcess multipleInstanceSubProcess = bpmn2.createSubProcess();
        multipleInstanceSubProcess.setLoopCharacteristics(bpmn2.createMultiInstanceLoopCharacteristics());
        CustomElement.async.of(multipleInstanceSubProcess).set(Boolean.TRUE);

        tested = new MultipleInstanceSubProcessPropertyReader(multipleInstanceSubProcess,
                                                              definitionResolverReal.getDiagram(),
                                                              definitionResolverReal);

        assertTrue(tested.isAsync());
    }

    @Test
    public void defaultReturnValues() {
        MultipleInstanceSubProcessPropertyReader p =
                new MultipleInstanceSubProcessPropertyReader(bpmn2.createSubProcess(), null, new DummyDefinitionResolver());
        assertNull(p.getCollectionInput());
        assertNull(p.getCollectionOutput());
        assertEquals("", p.getDataInput());
        assertEquals("", p.getDataOutput());
        assertEquals("", p.getCompletionCondition());
        assertEquals("", p.getSlaDueDate());
    }

    // internal mock
    static class DummyDefinitionResolver extends DefinitionResolver {

        DummyDefinitionResolver() {
            super(makeDefinitions(), Collections.emptyList());
        }

        public BPMNShape getShape(String elementId) {
            return null;
        }

        static Definitions makeDefinitions() {
            Definitions d = bpmn2.createDefinitions();
            BPMNPlane bpmnPlane = di.createBPMNPlane();
            BPMNDiagram bpmnDiagram = di.createBPMNDiagram();
            bpmnDiagram.setPlane(bpmnPlane);
            d.getDiagrams().add(bpmnDiagram);
            d.getRootElements().add(bpmn2.createProcess());
            return d;
        }
    }

    @Test
    public void testGetSlaDueDate() {
        String rawSlaDueDate = "12/25/1983";

        SubProcess multipleInstanceSubProcess = bpmn2.createSubProcess();
        multipleInstanceSubProcess.setLoopCharacteristics(bpmn2.createMultiInstanceLoopCharacteristics());
        CustomElement.slaDueDate.of(multipleInstanceSubProcess).set(rawSlaDueDate);

        tested = new MultipleInstanceSubProcessPropertyReader(multipleInstanceSubProcess,
                                                              definitionResolverReal.getDiagram(),
                                                              definitionResolverReal);

        assertTrue(tested.getSlaDueDate().contains(rawSlaDueDate));
    }
}