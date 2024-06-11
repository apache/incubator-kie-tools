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

package org.kie.workbench.common.stunner.sw.client.shapes;

import org.junit.Test;
import org.kie.workbench.common.stunner.sw.definition.ActionEventRef;
import org.kie.workbench.common.stunner.sw.definition.ActionNode;
import org.kie.workbench.common.stunner.sw.definition.FunctionRef;
import org.kie.workbench.common.stunner.sw.definition.SubFlowRef;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class HasActionsTest extends HasTranslationGeneralTest {

    private final HasActions hasAction = HasActionsTest.super::getTranslation;

    @Test
    public void actionIsNullTest() {
        assertTranslations(TEST_STRING, hasAction.getActionString(null), "Action.null");
    }

    @Test
    public void actionIsEmptyTest() {
        ActionNode action = new ActionNode();

        assertTranslations(TEST_STRING, hasAction.getActionString(action), "Action.null");
    }

    @Test
    public void actionNameIsPresentTest() {
        ActionNode action = new ActionNode();
        String testName = "TEST NAME";
        action.setName(testName);

        assertTranslations(TEST_STRING + ": " + testName,
                           hasAction.getActionString(action),
                           "Action.name");
    }

    @Test
    public void functionRefIsStringTest() {
        ActionNode action = new ActionNode();
        String funcRef = "FUNC REF NAME";
        action.setFunctionRef(funcRef);

        assertTranslations(TEST_STRING + ": " + funcRef,
                           hasAction.getActionString(action),
                           "Action.function");
    }

    @Test
    public void functionRefIsEmptyObjectTest() {
        ActionNode action = new ActionNode();
        FunctionRef functionRef = new FunctionRef();
        action.setFunctionRef(functionRef);

        assertTranslations(TEST_STRING + ": " + "undefined",
                           hasAction.getActionString(action),
                           "Action.function");
    }

    @Test
    public void functionRefIsObjectWithProperNameTest() {
        ActionNode action = new ActionNode();
        FunctionRef functionRef = new FunctionRef();
        String refName = "PROPER REF NAME";
        functionRef.setRefName(refName);
        action.setFunctionRef(functionRef);

        assertTranslations(TEST_STRING + ": " + refName,
                           hasAction.getActionString(action),
                           "Action.function");
    }

    @Test
    public void eventRefIsEmptyObjectTest() {
        ActionNode action = new ActionNode();
        ActionEventRef eventRef = new ActionEventRef();
        action.setEventRef(eventRef);

        assertTranslations(TEST_STRING + ": " + "undefined",
                           hasAction.getActionString(action),
                           "Action.event");
    }

    @Test
    public void eventRefConsumeEventIsProperlySetObjectTest() {
        ActionNode action = new ActionNode();
        ActionEventRef eventRef = new ActionEventRef();
        String consumeEventRef = "PROPER CONSUME EVENT REF";
        eventRef.setConsumeEventRef(consumeEventRef);
        action.setEventRef(eventRef);

        assertTranslations(TEST_STRING + ": " + consumeEventRef,
                           hasAction.getActionString(action),
                           "Action.event");
    }

    @Test
    public void subFlowRefIsStringTest() {
        ActionNode action = new ActionNode();
        String subFlowRef = "SUBFLOW REF NAME";
        action.setSubFlowRef(subFlowRef);

        assertTranslations(TEST_STRING + ": " + subFlowRef,
                           hasAction.getActionString(action),
                           "Action.subflow");
    }

    @Test
    public void subFlowIsEmptyObjectTest() {
        ActionNode action = new ActionNode();
        SubFlowRef subFlowRef = new SubFlowRef();
        action.setSubFlowRef(subFlowRef);

        assertTranslations(TEST_STRING + ": " + "undefined",
                           hasAction.getActionString(action),
                           "Action.subflow");
    }

    @Test
    public void subFlowIsObjectWithProperNameTest() {
        ActionNode action = new ActionNode();
        SubFlowRef subFlowRef = new SubFlowRef();
        String workflowId = "PROPER WORKFLOW ID";
        subFlowRef.setWorkflowId(workflowId);
        action.setSubFlowRef(subFlowRef);

        assertTranslations(TEST_STRING + ": " + workflowId,
                           hasAction.getActionString(action),
                           "Action.subflow");
    }

    @Test
    public void actionsArrayNullTest() {
        assertTranslations(TEST_STRING + "\r\n",
                           hasAction.getActionStringFromArray(null),
                           "Actions.null");
    }

    @Test
    public void actionsArrayEmptyTest() {
        ActionNode[] actions = new ActionNode[0];

        assertTranslations(TEST_STRING + "\r\n",
                           hasAction.getActionStringFromArray(actions),
                           "Actions.null");
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

        assertTranslations(TEST_STRING + ": " + workflowId + "\r\n" + TEST_STRING + ": " + funcRef + "\r\n",
                           hasAction.getActionStringFromArray(actions),
                           "Action.subflow",
                           "Action.function");
    }

    @Test
    public void hasSubflowsTrueTest() {
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

        assertTrue(hasAction.hasSubflows(actions));
    }

    @Test
    public void hasSubflowsFalseTest() {
        ActionNode[] actions = new ActionNode[1];
        ActionNode action = new ActionNode();
        String funcRef = "FUNC REF NAME";
        action.setFunctionRef(funcRef);
        actions[0] = action;

        assertFalse(hasAction.hasSubflows(actions));
    }

}
