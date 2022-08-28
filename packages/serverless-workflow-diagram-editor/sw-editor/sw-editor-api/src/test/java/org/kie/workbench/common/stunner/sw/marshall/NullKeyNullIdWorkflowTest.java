package org.kie.workbench.common.stunner.sw.marshall;

import org.junit.Test;
import org.kie.workbench.common.stunner.sw.definition.OperationState;
import org.kie.workbench.common.stunner.sw.definition.State;
import org.kie.workbench.common.stunner.sw.definition.Workflow;

import static org.junit.Assert.assertTrue;

public class NullKeyNullIdWorkflowTest extends BaseMarshallingTest {

    @Override
    protected Workflow createWorkflow() {
        return new Workflow()
                .setStart("State1")
                .setStates(new State[]{
                        new OperationState()
                                .setName("State1")
                                .setEnd(true)
                });
    }

    @Test
    public void testUnmarshallWorkflow() {
        unmarshallWorkflow();
        OperationState state = (OperationState) workflow.getStates()[0];
        assertDefinitionReferencedInNode(state, "State1");
        assertTrue(hasIncomingEdges("State1"));
        assertTrue(hasOutgoingEdges("State1"));
        assertTrue(hasIncomingEdgeFrom("State1", Marshaller.STATE_START));
        assertTrue(hasOutgoingEdgeTo("State1", Marshaller.STATE_END));
    }
}
