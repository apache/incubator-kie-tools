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


package org.kie.workbench.common.stunner.sw.marshall;

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.sw.definition.ActionNode;
import org.kie.workbench.common.stunner.sw.definition.CallFunctionAction;
import org.kie.workbench.common.stunner.sw.definition.OperationState;
import org.kie.workbench.common.stunner.sw.definition.State;
import org.kie.workbench.common.stunner.sw.definition.Workflow;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(LienzoMockitoTestRunner.class)
public class OperationStateMarshallingTest extends BaseMarshallingTest {

    private static final String WORKFLOW_ID = "workflow1";
    private static final String WORKFLOW_NAME = "Workflow1";

    @Override
    protected Workflow createWorkflow() {
        return new Workflow()
                .setId(WORKFLOW_ID)
                .setName(WORKFLOW_NAME)
                .setStart("State1")
                .setStates(new State[]{
                        new OperationState()
                                .setActions(new ActionNode[]{
                                        new CallFunctionAction()
                                                .setId("callFunction1")
                                                .setName("Call Function 1")
                                                .setFunctionRef("function1")
                                })
                                .setName("State1")
                                .setEnd(true)
                });
    }

    @Test
    public void testUnmarshallWorkflow() {
        unmarshallWorkflow();
        assertDefinitionReferencedInNode(workflow, WORKFLOW_ID);
        assertEquals(4, countChildren(WORKFLOW_ID));
        OperationState state = (OperationState) workflow.getStates()[0];
        assertDefinitionReferencedInNode(state, "State1");
        assertParentOf(WORKFLOW_ID, "State1");
        assertTrue(hasIncomingEdges("State1"));
        assertTrue(hasOutgoingEdges("State1"));
        assertTrue(hasIncomingEdgeFrom("State1", Marshaller.STATE_START));
        assertTrue(isConnectedToEnd("State1"));
        assertDefinitionReferencedInNode(state.getActions()[0], "Call Function 1");
    }
}