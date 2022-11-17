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
import org.kie.workbench.common.stunner.sw.definition.State;
import org.kie.workbench.common.stunner.sw.definition.Workflow;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class StatesFlowMarshallingTest extends BaseMarshallingTest {

    private static final String WORKFLOW_ID = "workflow1";
    private static final String WORKFLOW_NAME = "Workflow1";

    @Override
    protected Workflow createWorkflow() {
        return new Workflow()
                .setId(WORKFLOW_ID)
                .setName(WORKFLOW_NAME)
                .setStart("State1")
                .setStates(new State[]{
                        new State()
                                .setName("State1")
                                .setTransition("State2"),
                        new State()
                                .setName("State2")
                                .setTransition("State3"),
                        new State()
                                .setName("State3")
                                .setEnd(true)
                });
    }

    @Test
    public void testUnmarshallWorkflow() {
        unmarshallWorkflow();
        assertDefinitionReferencedInNode(workflow, WORKFLOW_ID);
        assertEquals(5, countChildren(WORKFLOW_ID));
        assertDefinitionReferencedInNode(workflow.getStates()[0], "State1");
        assertParentOf(WORKFLOW_ID, "State1");
        assertTrue(hasIncomingEdges("State1"));
        assertTrue(hasIncomingEdgeFrom("State1", Marshaller.STATE_START));
        assertTrue(hasOutgoingEdges("State1"));
        assertDefinitionReferencedInNode(workflow.getStates()[1], "State2");
        assertParentOf(WORKFLOW_ID, "State2");
        assertTrue(hasIncomingEdges("State2"));
        assertTrue(hasIncomingEdgeFrom("State2", "State1"));
        assertTrue(hasOutgoingEdges("State2"));
        assertDefinitionReferencedInNode(workflow.getStates()[2], "State3");
        assertParentOf(WORKFLOW_ID, "State3");
        assertTrue(hasIncomingEdges("State3"));
        assertTrue(hasIncomingEdgeFrom("State3", "State2"));
        assertTrue(hasOutgoingEdges("State3"));
        assertTrue(hasOutgoingEdgeTo("State3", Marshaller.STATE_END));
    }
}