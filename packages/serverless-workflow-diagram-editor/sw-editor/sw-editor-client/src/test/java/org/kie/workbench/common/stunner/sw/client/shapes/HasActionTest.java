/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.stunner.sw.client.shapes;

import org.junit.Test;
import org.kie.workbench.common.stunner.sw.definition.ActionEventRef;
import org.kie.workbench.common.stunner.sw.definition.ActionNode;
import org.kie.workbench.common.stunner.sw.definition.FunctionRef;
import org.kie.workbench.common.stunner.sw.definition.SubFlowRef;

import static org.junit.Assert.assertEquals;
import static org.kie.workbench.common.stunner.sw.client.shapes.HasAction.ACTIONS_ARE_NULL;
import static org.kie.workbench.common.stunner.sw.client.shapes.HasAction.ACTION_IS_EVENT;
import static org.kie.workbench.common.stunner.sw.client.shapes.HasAction.ACTION_IS_FUNC;
import static org.kie.workbench.common.stunner.sw.client.shapes.HasAction.ACTION_IS_NULL;
import static org.kie.workbench.common.stunner.sw.client.shapes.HasAction.ACTION_IS_SUBFLOW;
import static org.kie.workbench.common.stunner.sw.client.shapes.HasAction.ACTION_NAME;
import static org.kie.workbench.common.stunner.sw.client.shapes.HasAction.getActionString;
import static org.kie.workbench.common.stunner.sw.client.shapes.HasAction.getActionStringFromArray;

public class HasActionTest {

    @Test
    public void actionIsNullTest() {
        assertEquals(ACTION_IS_NULL, getActionString(null));
    }

    @Test
    public void actionIsEmptyTest() {
        ActionNode action = new ActionNode();

        assertEquals(ACTION_IS_NULL, getActionString(action));
    }

    @Test
    public void actionNameIsPresentTest() {
        ActionNode action = new ActionNode();
        String testName = "TEST NAME";
        action.setName(testName);

        assertEquals(ACTION_NAME + testName, getActionString(action));
    }

    @Test
    public void functionRefIsStringTest() {
        ActionNode action = new ActionNode();
        String funcRef = "FUNC REF NAME";
        action.setFunctionRef(funcRef);

        assertEquals(ACTION_IS_FUNC + funcRef, getActionString(action));
    }

    @Test
    public void functionRefIsEmptyObjectTest() {
        ActionNode action = new ActionNode();
        FunctionRef functionRef = new FunctionRef();
        action.setFunctionRef(functionRef);

        assertEquals(ACTION_IS_FUNC + "null", getActionString(action));
    }

    @Test
    public void functionRefIsObjectWithProperNameTest() {
        ActionNode action = new ActionNode();
        FunctionRef functionRef = new FunctionRef();
        String refName = "PROPER REF NAME";
        functionRef.setRefName(refName);
        action.setFunctionRef(functionRef);

        assertEquals(ACTION_IS_FUNC + refName, getActionString(action));
    }

    @Test
    public void eventRefIsEmptyObjectTest() {
        ActionNode action = new ActionNode();
        ActionEventRef eventRef = new ActionEventRef();
        action.setEventRef(eventRef);

        assertEquals(ACTION_IS_EVENT + "null", getActionString(action));
    }

    @Test
    public void eventRefConsumeEventIsProperlySetObjectTest() {
        ActionNode action = new ActionNode();
        ActionEventRef eventRef = new ActionEventRef();
        String consumeEventRef = "PROPER CONSUME EVENT REF";
        eventRef.setConsumeEventRef(consumeEventRef);
        action.setEventRef(eventRef);

        assertEquals(ACTION_IS_EVENT + consumeEventRef, getActionString(action));
    }

    @Test
    public void subFlowRefIsStringTest() {
        ActionNode action = new ActionNode();
        String subFlowRef = "SUBFLOW REF NAME";
        action.setSubFlowRef(subFlowRef);

        assertEquals(ACTION_IS_SUBFLOW + subFlowRef, getActionString(action));
    }

    @Test
    public void subFlowIsEmptyObjectTest() {
        ActionNode action = new ActionNode();
        SubFlowRef subFlowRef = new SubFlowRef();
        action.setSubFlowRef(subFlowRef);

        assertEquals(ACTION_IS_SUBFLOW + "null", getActionString(action));
    }

    @Test
    public void subFlowIsObjectWithProperNameTest() {
        ActionNode action = new ActionNode();
        SubFlowRef subFlowRef = new SubFlowRef();
        String workflowId = "PROPER WORKFLOW ID";
        subFlowRef.setWorkflowId(workflowId);
        action.setSubFlowRef(subFlowRef);

        assertEquals(ACTION_IS_SUBFLOW + workflowId, getActionString(action));
    }

    @Test
    public void actionsArrayNullTest() {
        assertEquals(ACTIONS_ARE_NULL, getActionStringFromArray(null));
    }

    @Test
    public void actionsArrayEmptyTest() {
        ActionNode[] actions = new ActionNode[0];

        assertEquals(ACTIONS_ARE_NULL, getActionStringFromArray(actions));
    }

    @Test
    public void actionsArrayTest() {
        ActionNode[] actions = new ActionNode[2];

        ActionNode action1 = new ActionNode();
        SubFlowRef subFlowRef = new SubFlowRef();
        String workflowId = "PROPER WORKFLOW ID";
        subFlowRef.setWorkflowId(workflowId);
        action1.setSubFlowRef(subFlowRef);
        actions[0] = action1;

        ActionNode action2 = new ActionNode();
        String funcRef = "FUNC REF NAME";
        action2.setFunctionRef(funcRef);
        actions[1] = action2;

        assertEquals(ACTION_IS_SUBFLOW + workflowId + "\r\n" + ACTION_IS_FUNC + funcRef + "\r\n",
                     getActionStringFromArray(actions));
    }
}
