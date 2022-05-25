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
import org.kie.workbench.common.stunner.sw.definition.State;
import org.kie.workbench.common.stunner.sw.definition.Workflow;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class StateMarshallingTest extends BaseMarshallingTest {

    @Override
    protected Workflow createWorkflow() {
        return new Workflow()
                .setId("workflow1")
                .setName("Workflow1")
                .setStates(new State[]{
                        new State()
                                .setName("State1")
                });
    }

    @Test
    public void testUnmarshallWorkflow() {
        unmarshallWorkflow();
        assertDefinitionReferencedInNode(workflow, "Workflow1");
        assertDefinitionReferencedInNode(workflow.states[0], "State1");
        assertEquals(2, countChildren("Workflow1"));
        assertParentOf("Workflow1", "State1");
        assertTrue(hasIncomingEdges("State1"));
        assertFalse(hasOutgoingEdges("State1"));
    }

    @Test
    public void testUnmarshallStartState() {
        workflow.setStart("State1");
        unmarshallWorkflow();
        assertEquals(3, countChildren("Workflow1"));
        assertTrue(hasIncomingEdgeFrom("State1", Marshaller.STATE_START));
    }

    @Test
    public void testUnmarshallEndState() {
        workflow.states[0].setEnd(true);
        unmarshallWorkflow();
        assertTrue(hasOutgoingEdges("State1"));
        assertTrue(hasOutgoingEdgeTo("State1", Marshaller.STATE_END));
    }

    @Test
    public void testMarshallGraph() {
        unmarshallWorkflow();
        Workflow workflow = marshallWorkflow();
        assertEquals(1, workflow.states.length);
        State state1 = workflow.states[0];
        assertNull(state1.transition);
        assertFalse(DefinitionTypeUtils.getEnd(state1.end));
        assertNull(state1.compensatedBy);
        assertNull(state1.eventTimeout);
        assertNull(state1.onErrors);
    }

    @Test
    public void testMarshallStateError() {
        // Unmarshall the graph for the workflow example.
        unmarshallWorkflow();
        // Create an error transition from State1 to end.
        ErrorTransition errorTransition = new ErrorTransition();
        errorTransition.setErrorRef("error1");
        graphHandler.addEdgeTo(graphHandler.newEdge("error1", Optional.of(errorTransition)),
                               getNodeByName("State1"),
                               graphHandler.newNode(Marshaller.STATE_END, Optional.of(new End())));
        // Assert the domain object gets properly updated once marshalling.
        Workflow workflow = marshallWorkflow();
        State state1 = workflow.states[0];
        assertNull(state1.transition);
        assertFalse(DefinitionTypeUtils.getEnd(state1.end));
        assertNull(state1.compensatedBy);
        assertNull(state1.eventTimeout);
        assertNotNull(state1.onErrors);
        assertEquals(1, state1.onErrors.length);
        ErrorTransition onError = state1.onErrors[0];
        assertNull(onError.transition);
        assertTrue(DefinitionTypeUtils.getEnd(onError.end));
    }
}