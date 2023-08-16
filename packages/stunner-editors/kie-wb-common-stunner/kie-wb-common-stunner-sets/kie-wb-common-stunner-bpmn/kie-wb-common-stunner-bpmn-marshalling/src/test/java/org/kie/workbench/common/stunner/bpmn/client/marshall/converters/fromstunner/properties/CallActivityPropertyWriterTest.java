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

import org.junit.Test;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.customproperties.CustomElement;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.SLADueDate;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.Factories.bpmn2;

public class CallActivityPropertyWriterTest {

    private CallActivityPropertyWriter tested =
            new CallActivityPropertyWriter(bpmn2.createCallActivity(),
                                           new FlatVariableScope(),
                                           new HashSet<>());

    @Test
    public void testSetCase_true() throws Exception {
        tested.setCase(Boolean.TRUE);

        assertTrue(CustomElement.isCase.of(tested.getFlowElement()).get());
    }

    @Test
    public void testSetCase_false() throws Exception {
        tested.setCase(Boolean.FALSE);

        assertFalse(CustomElement.isCase.of(tested.getFlowElement()).get());
    }

    @Test
    public void testSetAdHocAutostart_true() throws Exception {
        tested.setAdHocAutostart(Boolean.TRUE);

        assertTrue(CustomElement.autoStart.of(tested.getFlowElement()).get());
    }

    @Test
    public void testSetAdHocAutostart_false() throws Exception {
        tested.setAdHocAutostart(Boolean.FALSE);

        assertFalse(CustomElement.autoStart.of(tested.getFlowElement()).get());
    }

    @Test
    public void testAbortParentTrue() {
        tested.setAbortParent(true);
    }

    @Test
    public void testAbortParentFalse() {
        tested.setAbortParent(false);
    }

    @Test
    public void testSetIsAsync() {
        tested.setAsync(Boolean.TRUE);
        assertTrue(CustomElement.async.of(tested.getFlowElement()).get());
    }

    @Test
    public void testSetSlaDueDate() {
        String slaDueDate = "12/25/1983";
        tested.setSlaDueDate(new SLADueDate(slaDueDate));

        assertTrue(CustomElement.slaDueDate.of(tested.getFlowElement()).get().contains(slaDueDate));
    }
}