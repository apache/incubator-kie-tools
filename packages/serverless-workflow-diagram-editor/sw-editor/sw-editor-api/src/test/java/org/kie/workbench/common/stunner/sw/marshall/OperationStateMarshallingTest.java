/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.sw.marshall;

import org.junit.Test;
import org.kie.workbench.common.stunner.sw.definition.ActionNode;
import org.kie.workbench.common.stunner.sw.definition.CallFunctionAction;
import org.kie.workbench.common.stunner.sw.definition.OperationState;
import org.kie.workbench.common.stunner.sw.definition.State;
import org.kie.workbench.common.stunner.sw.definition.Workflow;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class OperationStateMarshallingTest extends BaseMarshallingTest {

    @Override
    protected Workflow createWorkflow() {
        return new Workflow()
                .setId("workflow1")
                .setName("Workflow1")
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
        assertDefinitionReferencedInNode(workflow, "Workflow1");
        assertEquals(4, countChildren("Workflow1"));
        OperationState state = (OperationState) workflow.states[0];
        assertDefinitionReferencedInNode(state, "State1");
        assertParentOf("Workflow1", "State1");
        assertTrue(hasIncomingEdges("State1"));
        assertTrue(hasOutgoingEdges("State1"));
        assertTrue(hasIncomingEdgeFrom("State1", Marshaller.STATE_START));
        assertTrue(hasOutgoingEdgeTo("State1", Marshaller.STATE_END));
        assertDefinitionReferencedInNode(state.actions[0], "Call Function 1");
    }
}