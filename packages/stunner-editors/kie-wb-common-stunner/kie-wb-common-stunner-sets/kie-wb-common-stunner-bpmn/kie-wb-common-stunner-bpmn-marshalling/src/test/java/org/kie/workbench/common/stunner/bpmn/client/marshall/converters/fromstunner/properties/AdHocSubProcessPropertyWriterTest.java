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

import org.eclipse.bpmn2.AdHocSubProcess;
import org.eclipse.bpmn2.FormalExpression;
import org.junit.Test;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.customproperties.CustomElement;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.properties.Scripts;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.util.FormalExpressionBodyHandler;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.SLADueDate;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.AdHocCompletionCondition;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.AdHocOrdering;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.ScriptTypeValue;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.Factories.bpmn2;
import static org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.properties.Scripts.asCData;

public class AdHocSubProcessPropertyWriterTest {

    private AdHocSubProcessPropertyWriter tested = new AdHocSubProcessPropertyWriter(bpmn2.createAdHocSubProcess(),
                                                                                     new FlatVariableScope(), new HashSet<>());

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

    /* TODO: Kogito
    @Test
    public void testSetAdHocActivationCondition() {
        tested.setAdHocActivationCondition(new AdHocActivationCondition("condition expression"));
        assertEquals(asCData("condition expression"), CustomElement.customActivationCondition.of(tested.getFlowElement()).get());
    }

    @Test
    public void testSetAdHocActivationConditionNull() {
        tested.setAdHocActivationCondition(new AdHocActivationCondition(null));
        assertEquals("", CustomElement.customActivationCondition.of(tested.getFlowElement()).get());
    }

    @Test
    public void testSetAdHocActivationConditionEmpty() {
        tested.setAdHocActivationCondition(new AdHocActivationCondition(""));
        assertEquals("", CustomElement.customActivationCondition.of(tested.getFlowElement()).get());
    }
    */

    @Test
    public void testSetAdHocOrderingSequential() {
        tested.setAdHocOrdering(new AdHocOrdering("Sequential"));
        assertEquals(org.eclipse.bpmn2.AdHocOrdering.SEQUENTIAL, ((AdHocSubProcess) tested.getFlowElement()).getOrdering());
    }

    @Test
    public void testSetAdHocOrderingParallel() {
        tested.setAdHocOrdering(new AdHocOrdering("Parallel"));
        assertEquals(org.eclipse.bpmn2.AdHocOrdering.PARALLEL, ((AdHocSubProcess) tested.getFlowElement()).getOrdering());
    }

    @Test
    public void testSetAdHocCompletionCondition() {
        AdHocCompletionCondition condition = new AdHocCompletionCondition(new ScriptTypeValue("java", "some code"));
        tested.setAdHocCompletionCondition(condition);
        FormalExpression expression = (FormalExpression) ((AdHocSubProcess) tested.getFlowElement()).getCompletionCondition();
        assertEquals(condition.getValue().getLanguage(), Scripts.scriptLanguageFromUri(expression.getLanguage()));
        assertEquals(asCData(condition.getValue().getScript()), FormalExpressionBodyHandler.of(expression).getBody());
    }

    @Test
    public void testSetAsync() {
        tested.setAsync(true);
        assertTrue(CustomElement.async.of(tested.getFlowElement()).get());
    }

    @Test
    public void testSetSlaDueDate() {
        String slaDueDate = "12/25/1983";
        tested.setSlaDueDate(new SLADueDate(slaDueDate));

        assertTrue(CustomElement.slaDueDate.of(tested.getFlowElement()).get().contains(slaDueDate));
    }
}