/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.definition;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class HashCodeAndEqualityTest {

    @Test
    public void testAdHocSubprocessEquals() {
        AdHocSubprocess.AdHocSubprocessBuilder builder = new AdHocSubprocess.AdHocSubprocessBuilder();
        AdHocSubprocess a = builder.build();
        builder = new AdHocSubprocess.AdHocSubprocessBuilder();
        AdHocSubprocess b = builder.build();
        assertEquals(a,
                     b);
        assertFalse(a.equals(19));
        assertFalse(a.equals(null));
    }

    @Test
    public void testAdHocSubprocessHashCode() {
        AdHocSubprocess.AdHocSubprocessBuilder builder = new AdHocSubprocess.AdHocSubprocessBuilder();
        AdHocSubprocess a = builder.build();
        builder = new AdHocSubprocess.AdHocSubprocessBuilder();
        AdHocSubprocess b = builder.build();
        assertTrue(a.hashCode() == b.hashCode());
    }

    @Test
    public void testBPMNDiagramImplEquals() {
        BPMNDiagramImpl.BPMNDiagramBuilder builder = new BPMNDiagramImpl.BPMNDiagramBuilder();
        BPMNDiagramImpl a = builder.build();
        builder = new BPMNDiagramImpl.BPMNDiagramBuilder();
        BPMNDiagramImpl b = builder.build();
        assertEquals(a,
                     b);
        assertFalse(a.equals(19));
        assertFalse(a.equals(null));
    }

    @Test
    public void testBPMNDiagramImplHashCode() {
        BPMNDiagramImpl.BPMNDiagramBuilder builder = new BPMNDiagramImpl.BPMNDiagramBuilder();
        BPMNDiagramImpl a = builder.build();
        builder = new BPMNDiagramImpl.BPMNDiagramBuilder();
        BPMNDiagramImpl b = builder.build();
        assertTrue(a.hashCode() == b.hashCode());
    }

    @Test
    public void testBusinessRuleTaskEquals() {
        BusinessRuleTask.BusinessRuleTaskBuilder builder = new BusinessRuleTask.BusinessRuleTaskBuilder();
        BusinessRuleTask a = builder.build();
        builder = new BusinessRuleTask.BusinessRuleTaskBuilder();
        BusinessRuleTask b = builder.build();
        assertEquals(a,
                     b);
        assertFalse(a.equals(19));
        assertFalse(a.equals(null));
    }

    @Test
    public void testBusinessRuleTaskHashCode() {
        BusinessRuleTask.BusinessRuleTaskBuilder builder = new BusinessRuleTask.BusinessRuleTaskBuilder();
        BusinessRuleTask a = builder.build();
        builder = new BusinessRuleTask.BusinessRuleTaskBuilder();
        BusinessRuleTask b = builder.build();
        assertTrue(a.hashCode() == b.hashCode());
    }

    @Test
    public void testEndNoneEventEquals() {
        EndNoneEvent.EndNoneEventBuilder builder = new EndNoneEvent.EndNoneEventBuilder();
        EndNoneEvent a = builder.build();
        builder = new EndNoneEvent.EndNoneEventBuilder();
        EndNoneEvent b = builder.build();
        assertEquals(a,
                     b);
        assertFalse(a.equals(19));
        assertFalse(a.equals(null));
    }

    @Test
    public void testEndNoneEventHashCode() {
        EndNoneEvent.EndNoneEventBuilder builder = new EndNoneEvent.EndNoneEventBuilder();
        EndNoneEvent a = builder.build();
        builder = new EndNoneEvent.EndNoneEventBuilder();
        EndNoneEvent b = builder.build();
        assertTrue(a.hashCode() == b.hashCode());
    }

    @Test
    public void testEndTerminateEventEquals() {
        EndTerminateEvent.EndTerminateEventBuilder builder = new EndTerminateEvent.EndTerminateEventBuilder();
        EndTerminateEvent a = builder.build();
        builder = new EndTerminateEvent.EndTerminateEventBuilder();
        EndTerminateEvent b = builder.build();
        assertEquals(a,
                     b);
        assertFalse(a.equals(19));
        assertFalse(a.equals(null));
    }

    @Test
    public void testEndTerminateEventHashCode() {
        EndTerminateEvent.EndTerminateEventBuilder builder = new EndTerminateEvent.EndTerminateEventBuilder();
        EndTerminateEvent a = builder.build();
        builder = new EndTerminateEvent.EndTerminateEventBuilder();
        EndTerminateEvent b = builder.build();
        assertTrue(a.hashCode() == b.hashCode());
    }

    @Test
    public void testExclusiveDatabasedGatewayEquals() {
        ExclusiveDatabasedGateway.ExclusiveDatabasedGatewayBuilder builder = new ExclusiveDatabasedGateway.ExclusiveDatabasedGatewayBuilder();
        ExclusiveDatabasedGateway a = builder.build();
        builder = new ExclusiveDatabasedGateway.ExclusiveDatabasedGatewayBuilder();
        ExclusiveDatabasedGateway b = builder.build();
        assertEquals(a,
                     b);
        assertFalse(a.equals(19));
        assertFalse(a.equals(null));
    }

    @Test
    public void testExclusiveDatabasedGatewayHashCode() {
        ExclusiveDatabasedGateway.ExclusiveDatabasedGatewayBuilder builder = new ExclusiveDatabasedGateway.ExclusiveDatabasedGatewayBuilder();
        ExclusiveDatabasedGateway a = builder.build();
        builder = new ExclusiveDatabasedGateway.ExclusiveDatabasedGatewayBuilder();
        ExclusiveDatabasedGateway b = builder.build();
        assertTrue(a.hashCode() == b.hashCode());
    }

    @Test
    public void testIntermediateTimerEventEquals() {
        IntermediateTimerEvent.IntermediateTimerEventBuilder builder = new IntermediateTimerEvent.IntermediateTimerEventBuilder();
        IntermediateTimerEvent a = builder.build();
        builder = new IntermediateTimerEvent.IntermediateTimerEventBuilder();
        IntermediateTimerEvent b = builder.build();
        assertEquals(a,
                     b);
        assertFalse(a.equals(19));
        assertFalse(a.equals(null));
    }

    @Test
    public void testIntermediateTimerEventHashCode() {
        IntermediateTimerEvent.IntermediateTimerEventBuilder builder = new IntermediateTimerEvent.IntermediateTimerEventBuilder();
        IntermediateTimerEvent a = builder.build();
        builder = new IntermediateTimerEvent.IntermediateTimerEventBuilder();
        IntermediateTimerEvent b = builder.build();
        assertTrue(a.hashCode() == b.hashCode());
    }

    @Test
    public void testLaneEquals() {
        Lane.LaneBuilder builder = new Lane.LaneBuilder();
        Lane a = builder.build();
        builder = new Lane.LaneBuilder();
        Lane b = builder.build();
        assertEquals(a,
                     b);
        assertFalse(a.equals(19));
        assertFalse(a.equals(null));
    }

    @Test
    public void testLaneHashCode() {
        Lane.LaneBuilder builder = new Lane.LaneBuilder();
        Lane a = builder.build();
        builder = new Lane.LaneBuilder();
        Lane b = builder.build();
        assertTrue(a.hashCode() == b.hashCode());
    }

    @Test
    public void testNoneTaskEquals() {
        NoneTask.NoneTaskBuilder builder = new NoneTask.NoneTaskBuilder();
        NoneTask a = builder.build();
        builder = new NoneTask.NoneTaskBuilder();
        NoneTask b = builder.build();
        assertEquals(a,
                     b);
        assertFalse(a.equals(19));
        assertFalse(a.equals(null));
    }

    @Test
    public void testNoneTaskHashCode() {
        NoneTask.NoneTaskBuilder builder = new NoneTask.NoneTaskBuilder();
        NoneTask a = builder.build();
        builder = new NoneTask.NoneTaskBuilder();
        NoneTask b = builder.build();
        assertTrue(a.hashCode() == b.hashCode());
    }

    @Test
    public void testParallelGatewayEquals() {
        ParallelGateway.ParallelGatewayBuilder builder = new ParallelGateway.ParallelGatewayBuilder();
        ParallelGateway a = builder.build();
        builder = new ParallelGateway.ParallelGatewayBuilder();
        ParallelGateway b = builder.build();
        assertEquals(a,
                     b);
        assertFalse(a.equals(19));
        assertFalse(a.equals(null));
    }

    @Test
    public void testParallelGatewayHashCode() {
        ParallelGateway.ParallelGatewayBuilder builder = new ParallelGateway.ParallelGatewayBuilder();
        ParallelGateway a = builder.build();
        builder = new ParallelGateway.ParallelGatewayBuilder();
        ParallelGateway b = builder.build();
        assertTrue(a.hashCode() == b.hashCode());
    }

    @Test
    public void testReusableSubprocessEquals() {
        ReusableSubprocess.ReusableSubprocessBuilder builder = new ReusableSubprocess.ReusableSubprocessBuilder();
        ReusableSubprocess a = builder.build();
        builder = new ReusableSubprocess.ReusableSubprocessBuilder();
        ReusableSubprocess b = builder.build();
        assertEquals(a,
                     b);
        assertFalse(a.equals(19));
        assertFalse(a.equals(null));
    }

    @Test
    public void testReusableSubprocessHashCode() {
        ReusableSubprocess.ReusableSubprocessBuilder builder = new ReusableSubprocess.ReusableSubprocessBuilder();
        ReusableSubprocess a = builder.build();
        builder = new ReusableSubprocess.ReusableSubprocessBuilder();
        ReusableSubprocess b = builder.build();
        assertTrue(a.hashCode() == b.hashCode());
    }

    @Test
    public void testScriptTaskEquals() {
        ScriptTask.ScriptTaskBuilder builder = new ScriptTask.ScriptTaskBuilder();
        ScriptTask a = builder.build();
        builder = new ScriptTask.ScriptTaskBuilder();
        ScriptTask b = builder.build();
        assertEquals(a,
                     b);
        assertFalse(a.equals(19));
        assertFalse(a.equals(null));
    }

    @Test
    public void testScriptTaskHashCode() {
        ScriptTask.ScriptTaskBuilder builder = new ScriptTask.ScriptTaskBuilder();
        ScriptTask a = builder.build();
        builder = new ScriptTask.ScriptTaskBuilder();
        ScriptTask b = builder.build();
        assertTrue(a.hashCode() == b.hashCode());
    }

    @Test
    public void testSequenceFlowEquals() {
        SequenceFlow.SequenceFlowBuilder builder = new SequenceFlow.SequenceFlowBuilder();
        SequenceFlow a = builder.build();
        builder = new SequenceFlow.SequenceFlowBuilder();
        SequenceFlow b = builder.build();
        assertEquals(a,
                     b);
        assertFalse(a.equals(19));
        assertFalse(a.equals(null));
    }

    @Test
    public void testSequenceFlowHashCode() {
        SequenceFlow.SequenceFlowBuilder builder = new SequenceFlow.SequenceFlowBuilder();
        SequenceFlow a = builder.build();
        builder = new SequenceFlow.SequenceFlowBuilder();
        SequenceFlow b = builder.build();
        assertTrue(a.hashCode() == b.hashCode());
    }

    @Test
    public void testStartNoneEventEquals() {
        StartNoneEvent.StartNoneEventBuilder builder = new StartNoneEvent.StartNoneEventBuilder();
        StartNoneEvent a = builder.build();
        builder = new StartNoneEvent.StartNoneEventBuilder();
        StartNoneEvent b = builder.build();
        assertEquals(a,
                     b);
        assertFalse(a.equals(19));
        assertFalse(a.equals(null));
    }

    @Test
    public void testStartNoneEventHashCode() {
        StartNoneEvent.StartNoneEventBuilder builder = new StartNoneEvent.StartNoneEventBuilder();
        StartNoneEvent a = builder.build();
        builder = new StartNoneEvent.StartNoneEventBuilder();
        StartNoneEvent b = builder.build();
        assertTrue(a.hashCode() == b.hashCode());
    }

    @Test
    public void testStartSignalEventEquals() {
        StartSignalEvent.StartSignalEventBuilder builder = new StartSignalEvent.StartSignalEventBuilder();
        StartSignalEvent a = builder.build();
        builder = new StartSignalEvent.StartSignalEventBuilder();
        StartSignalEvent b = builder.build();
        assertEquals(a,
                     b);
        assertFalse(a.equals(19));
        assertFalse(a.equals(null));
    }

    @Test
    public void testStartSignalEventHashCode() {
        StartSignalEvent.StartSignalEventBuilder builder = new StartSignalEvent.StartSignalEventBuilder();
        StartSignalEvent a = builder.build();
        builder = new StartSignalEvent.StartSignalEventBuilder();
        StartSignalEvent b = builder.build();
        assertTrue(a.hashCode() == b.hashCode());
    }

    @Test
    public void testStartTimerEventEquals() {
        StartTimerEvent.StartTimerEventBuilder builder = new StartTimerEvent.StartTimerEventBuilder();
        StartTimerEvent a = builder.build();
        builder = new StartTimerEvent.StartTimerEventBuilder();
        StartTimerEvent b = builder.build();
        assertEquals(a,
                     b);
        assertFalse(a.equals(19));
        assertFalse(a.equals(null));
    }

    @Test
    public void testStartTimerEventHashCode() {
        StartTimerEvent.StartTimerEventBuilder builder = new StartTimerEvent.StartTimerEventBuilder();
        StartTimerEvent a = builder.build();
        builder = new StartTimerEvent.StartTimerEventBuilder();
        StartTimerEvent b = builder.build();
        assertTrue(a.hashCode() == b.hashCode());
    }

    @Test
    public void testUserTaskEquals() {
        UserTask.UserTaskBuilder builder = new UserTask.UserTaskBuilder();
        UserTask a = builder.build();
        builder = new UserTask.UserTaskBuilder();
        UserTask b = builder.build();
        assertEquals(a,
                     b);
        assertFalse(a.equals(19));
        assertFalse(a.equals(null));
    }

    @Test
    public void testUserTaskHashCode() {
        UserTask.UserTaskBuilder builder = new UserTask.UserTaskBuilder();
        UserTask a = builder.build();
        builder = new UserTask.UserTaskBuilder();
        UserTask b = builder.build();
        assertTrue(a.hashCode() == b.hashCode());
    }

    @Test
    public void testEmbeddedSubprocessEquals() {
        EmbeddedSubprocess.EmbeddedSubprocessBuilder builder = new EmbeddedSubprocess.EmbeddedSubprocessBuilder();
        EmbeddedSubprocess a = builder.build();
        builder = new EmbeddedSubprocess.EmbeddedSubprocessBuilder();
        EmbeddedSubprocess b = builder.build();
        assertEquals(a,
                     b);
        assertFalse(a.equals(19));
        assertFalse(a.equals(null));
    }

    @Test
    public void testEmbeddedSubprocessHashCode() {
        EmbeddedSubprocess.EmbeddedSubprocessBuilder builder = new EmbeddedSubprocess.EmbeddedSubprocessBuilder();
        EmbeddedSubprocess a = builder.build();
        builder = new EmbeddedSubprocess.EmbeddedSubprocessBuilder();
        EmbeddedSubprocess b = builder.build();
        assertTrue(a.hashCode() == b.hashCode());
    }


}
