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
import java.util.UUID;

import org.eclipse.bpmn2.CallActivity;
import org.eclipse.bpmn2.Definitions;
import org.eclipse.bpmn2.di.BPMNDiagram;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.customproperties.CustomElement;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.DefinitionResolver;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.Factories.bpmn2;
import static org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.Factories.di;

@RunWith(MockitoJUnitRunner.class)
public class CallActivityPropertyReaderTest {

    private DefinitionResolver definitionResolver;

    private CallActivityPropertyReader tested;

    @Before
    public void setUp() {
        Definitions definitions = bpmn2.createDefinitions();
        definitions.getRootElements().add(bpmn2.createProcess());
        BPMNDiagram bpmnDiagram = di.createBPMNDiagram();
        bpmnDiagram.setPlane(di.createBPMNPlane());
        definitions.getDiagrams().add(bpmnDiagram);

        definitionResolver = new DefinitionResolver(definitions, Collections.emptyList());
    }

    @Test
    public void testIsCase_true() {
        String id = UUID.randomUUID().toString();

        CallActivity callActivity = bpmn2.createCallActivity();
        callActivity.setId(id);
        CustomElement.isCase.of(callActivity).set(Boolean.TRUE);

        tested = new CallActivityPropertyReader(callActivity,
                                                definitionResolver.getDiagram(),
                                                definitionResolver);

        assertTrue(tested.isCase());
    }

    @Test
    public void testIsCase_false() {
        String id = UUID.randomUUID().toString();

        CallActivity callActivity = bpmn2.createCallActivity();
        callActivity.setId(id);
        CustomElement.isCase.of(callActivity).set(Boolean.FALSE);

        tested = new CallActivityPropertyReader(callActivity,
                                                definitionResolver.getDiagram(),
                                                definitionResolver);

        assertFalse(tested.isCase());
    }

    @Test
    public void testIsAdHocAutostart_true() {
        String id = UUID.randomUUID().toString();

        CallActivity callActivity = bpmn2.createCallActivity();
        callActivity.setId(id);
        CustomElement.autoStart.of(callActivity).set(Boolean.TRUE);

        tested = new CallActivityPropertyReader(callActivity,
                                                definitionResolver.getDiagram(),
                                                definitionResolver);

        assertTrue(tested.isAdHocAutostart());
    }

    @Test
    public void testIsAdHocAutostart_false() {
        String id = UUID.randomUUID().toString();

        CallActivity callActivity = bpmn2.createCallActivity();
        callActivity.setId(id);
        CustomElement.autoStart.of(callActivity).set(Boolean.FALSE);

        tested = new CallActivityPropertyReader(callActivity,
                                                definitionResolver.getDiagram(),
                                                definitionResolver);

        assertFalse(tested.isAdHocAutostart());
    }

    @Test
    public void testAbortParentTrue() {
        testAbortParent(true);
    }

    @Test
    public void testAbortParentFalse() {
        testAbortParent(false);
    }

    private void testAbortParent(boolean value) {
        CallActivity callActivity = bpmn2.createCallActivity();
        CustomElement.abortParent.of(callActivity).set(value);

        tested = new CallActivityPropertyReader(callActivity,
                                                definitionResolver.getDiagram(),
                                                definitionResolver);
        assertEquals(value, tested.isAbortParent());
    }

    @Test
    public void testIsAsync() {
        CallActivity callActivity = bpmn2.createCallActivity();
        CustomElement.async.of(callActivity).set(Boolean.TRUE);

        tested = new CallActivityPropertyReader(callActivity,
                                                definitionResolver.getDiagram(),
                                                definitionResolver);

        assertTrue(tested.isAsync());
    }

    @Test
    public void testGetSlaDueDate() {
        String rawSlaDueDate = "12/25/1983";

        CallActivity callActivity = bpmn2.createCallActivity();
        CustomElement.slaDueDate.of(callActivity).set(rawSlaDueDate);

        tested = new CallActivityPropertyReader(callActivity,
                                                definitionResolver.getDiagram(),
                                                definitionResolver);

        assertTrue(tested.getSlaDueDate().contains(rawSlaDueDate));
    }
}