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

import java.util.Optional;

import org.junit.Test;
import org.kie.workbench.common.stunner.sw.definition.End;
import org.kie.workbench.common.stunner.sw.definition.ErrorTransition;
import org.kie.workbench.common.stunner.sw.definition.InjectState;
import org.kie.workbench.common.stunner.sw.definition.State;
import org.kie.workbench.common.stunner.sw.definition.StateEnd;
import org.kie.workbench.common.stunner.sw.definition.Workflow;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.kie.workbench.common.stunner.sw.marshall.Marshaller.unmarshallNode;

public class StateMarshallingTest extends BaseMarshallingTest {

    private static final String WORKFLOW_ID = "workflow1";
    private static final String WORKFLOW_NAME = "Workflow1";

    @Override
    protected Workflow createWorkflow() {
        return new Workflow()
                .setId(WORKFLOW_ID)
                .setName(WORKFLOW_NAME)
                .setStates(new State[]{
                        new State()
                                .setName("State1")
                });
    }

    @Test
    public void testEndObject() {
        StateEnd endObject = new StateEnd();
        endObject.setTerminate(true);
        endObject.setContinueAs("{}");
        endObject.setCompensate(false);

        workflow.getStates()[0].setEnd(endObject);
        unmarshallWorkflow();
        assertTrue(hasOutgoingEdges("State1"));
        assertTrue(hasOutgoingEdgeTo("State1", Marshaller.STATE_END));
        assertTrue(workflow.getStates()[0].getEnd() instanceof StateEnd);
        final StateEnd end = (StateEnd) workflow.getStates()[0].getEnd();
        assertTrue(end.getTerminate());
        assertEquals("{}",end.getContinueAs());
        assertFalse(end.getCompensate());
        //assertTrue(end.get("produceEvents").equals("[]"));
    }

    @Test
    public void testUnsetCompensatedBy() {
        InjectState injectState = new InjectState();

        injectState.setName("Inject State");
        injectState.setUsedForCompensation(true);

        Workflow workflow = new Workflow()
                .setId(WORKFLOW_ID)
                .setName(WORKFLOW_NAME)
                .setStates(new State[]{
                        injectState
                });

        unmarshallNode(builderContext, workflow);
        assertEquals(injectState.getUsedForCompensation(), true);
        // specific case when usedForCompensation is equals to Js.undefined cannot be tested since value defaults to false
    }

    @Test
    public void testUnmarshallWorkflow() {
        unmarshallWorkflow();
        assertDefinitionReferencedInNode(workflow, WORKFLOW_ID);
        assertDefinitionReferencedInNode(workflow.getStates()[0], "State1");
        assertEquals(2, countChildren(WORKFLOW_ID));
        assertParentOf(WORKFLOW_ID, "State1");
        assertTrue(hasIncomingEdges("State1"));
        assertFalse(hasOutgoingEdges("State1"));
    }

    @Test
    public void testUnmarshallStartState() {
        workflow.setStart("State1");
        unmarshallWorkflow();
        assertEquals(3, countChildren(WORKFLOW_ID));
        assertTrue(hasIncomingEdgeFrom("State1", Marshaller.STATE_START));
    }

    @Test
    public void testUnmarshallEndState() {
        workflow.getStates()[0].setEnd(true);
        unmarshallWorkflow();
        assertTrue(hasOutgoingEdges("State1"));
        assertTrue(hasOutgoingEdgeTo("State1", Marshaller.STATE_END));
    }

    @Test
    public void testMarshallGraph() {
        unmarshallWorkflow();
        Workflow workflow = marshallWorkflow();
        assertEquals(1, workflow.getStates().length);
        State state1 = workflow.getStates()[0];
        assertNull(state1.getTransition());
        assertFalse(DefinitionTypeUtils.getEnd(state1.getEnd()));
        assertNull(state1.getCompensatedBy());
        assertNull(state1.getEventTimeout());
        assertNull(state1.getOnErrors());
    }

    @Test
    public void testMarshallStateError() {
        // Unmarshall the graph for the workflow example.
        unmarshallWorkflow();
        // Create an error transition from State1 to end.
        ErrorTransition errorTransition = new ErrorTransition();
        errorTransition.setErrorRef("error1");
        errorTransition.setEnd(true);
        graphHandler.addEdgeTo(graphHandler.newEdge("error1", Optional.of(errorTransition)),
                               getNodeByName("State1"),
                               graphHandler.newNode(Marshaller.STATE_END, Optional.of(new End())));
        // Assert the domain object gets properly updated once marshalling.
        Workflow workflow = marshallWorkflow();
        State state1 = workflow.getStates()[0];
        assertNull(state1.getTransition());
        assertFalse(DefinitionTypeUtils.getEnd(state1.getEnd()));
        assertNull(state1.getCompensatedBy());
        assertNull(state1.getEventTimeout());
        assertNotNull(state1.getOnErrors());
        assertEquals(1, state1.getOnErrors().length);
        ErrorTransition onError = state1.getOnErrors()[0];
        assertNull(onError.getTransition());
        assertTrue(DefinitionTypeUtils.getEnd(onError.getEnd()));
    }
}