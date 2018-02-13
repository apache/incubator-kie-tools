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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import org.junit.Test;
import org.kie.workbench.common.stunner.bpmn.definition.property.assignee.Actors;
import org.kie.workbench.common.stunner.bpmn.definition.property.assignee.Groupid;
import org.kie.workbench.common.stunner.bpmn.definition.property.background.BackgroundSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.background.BgColor;
import org.kie.workbench.common.stunner.bpmn.definition.property.background.BorderColor;
import org.kie.workbench.common.stunner.bpmn.definition.property.background.BorderSize;
import org.kie.workbench.common.stunner.bpmn.definition.property.connectors.ConditionExpression;
import org.kie.workbench.common.stunner.bpmn.definition.property.connectors.Priority;
import org.kie.workbench.common.stunner.bpmn.definition.property.connectors.SequenceFlowExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.dataio.AssignmentsInfo;
import org.kie.workbench.common.stunner.bpmn.definition.property.dataio.DataIOSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.dimensions.CircleDimensionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.dimensions.Radius;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.CancelActivity;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.IsInterrupting;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.message.CancellingMessageEventExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.message.InterruptingMessageEventExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.message.MessageEventExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.message.MessageRef;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.signal.ScopedSignalEventExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.signal.SignalRef;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.signal.SignalScope;
import org.kie.workbench.common.stunner.bpmn.definition.property.font.FontBorderColor;
import org.kie.workbench.common.stunner.bpmn.definition.property.font.FontBorderSize;
import org.kie.workbench.common.stunner.bpmn.definition.property.font.FontColor;
import org.kie.workbench.common.stunner.bpmn.definition.property.font.FontFamily;
import org.kie.workbench.common.stunner.bpmn.definition.property.font.FontSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.font.FontSize;
import org.kie.workbench.common.stunner.bpmn.definition.property.gateway.DefaultRoute;
import org.kie.workbench.common.stunner.bpmn.definition.property.gateway.GatewayExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.BPMNGeneralSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.Documentation;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.Name;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.AdHocAutostart;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.AdHocCompletionCondition;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.AdHocOrdering;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.AdHocSubprocessTaskExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.BusinessRuleTaskExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.CreatedBy;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.Description;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.IsAsync;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.OnEntryAction;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.OnExitAction;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.RuleFlowGroup;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.Script;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.ScriptTaskExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.ScriptTypeListValue;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.ScriptTypeValue;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.Skippable;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.Subject;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.TaskName;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.UserTaskExecutionSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
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
        assertEquals(new AdHocSubprocess(),
                     new AdHocSubprocess());
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
        assertTrue(new AdHocSubprocess().hashCode() == new AdHocSubprocess().hashCode());
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
        assertEquals(a.hashCode(),
                     b.hashCode());
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
        assertEquals(a.hashCode(),
                     b.hashCode());
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
        assertEquals(a.hashCode(),
                     b.hashCode());
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
        assertEquals(a.hashCode(),
                     b.hashCode());
    }

    @Test
    public void testEndSignalEventEquals() {
        final String SIGNAL_REF = "signal ref";
        final String SIGNAL_SCOPE = "signal scope";
        final ScopedSignalEventExecutionSet A_EXECUTION_SET = new ScopedSignalEventExecutionSet(new SignalRef(SIGNAL_REF),
                                                                                                new SignalScope(SIGNAL_SCOPE));
        final ScopedSignalEventExecutionSet B_EXECUTION_SET = new ScopedSignalEventExecutionSet(new SignalRef(SIGNAL_REF),
                                                                                                new SignalScope(SIGNAL_SCOPE));
        final ScopedSignalEventExecutionSet C_EXECUTION_SET = new ScopedSignalEventExecutionSet(new SignalRef("Other value"),
                                                                                                new SignalScope(SIGNAL_SCOPE));
        final ScopedSignalEventExecutionSet D_EXECUTION_SET = new ScopedSignalEventExecutionSet(new SignalRef(SIGNAL_REF),
                                                                                                new SignalScope(SIGNAL_SCOPE));

        final String ASSIGNMENT_INFO = "some value";
        final DataIOSet A_DATA_SET = new DataIOSet(new AssignmentsInfo(ASSIGNMENT_INFO));
        final DataIOSet B_DATA_SET = new DataIOSet(new AssignmentsInfo(ASSIGNMENT_INFO));
        final DataIOSet C_DATA_SET = new DataIOSet(new AssignmentsInfo(ASSIGNMENT_INFO));
        final DataIOSet D_DATA_SET = new DataIOSet(new AssignmentsInfo("Other value"));

        EndSignalEvent.EndSignalEventBuilder builder = new EndSignalEvent.EndSignalEventBuilder();
        EndSignalEvent a = builder.build();
        a.setExecutionSet(A_EXECUTION_SET);
        a.setDataIOSet(A_DATA_SET);

        builder = new EndSignalEvent.EndSignalEventBuilder();
        EndSignalEvent b = builder.build();
        b.setExecutionSet(B_EXECUTION_SET);
        b.setDataIOSet(B_DATA_SET);

        builder = new EndSignalEvent.EndSignalEventBuilder();
        EndSignalEvent c = builder.build();
        c.setExecutionSet(C_EXECUTION_SET);
        c.setDataIOSet(C_DATA_SET);

        builder = new EndSignalEvent.EndSignalEventBuilder();
        EndSignalEvent d = builder.build();
        d.setExecutionSet(D_EXECUTION_SET);
        d.setDataIOSet(D_DATA_SET);

        assertEquals(a,
                     a);
        assertEquals(a,
                     b);
        assertNotEquals(a,
                        c);
        assertNotEquals(a,
                        d);
        assertNotEquals(a,
                        19);
        assertNotEquals(a,
                        null);

        a.setExecutionSet(null);
        assertNotEquals(a,
                        b);
        assertNotEquals(b,
                        a);

        a.setExecutionSet(A_EXECUTION_SET);
        assertEquals(a,
                     b);

        a.setDataIOSet(null);
        assertNotEquals(a,
                        b);
        assertNotEquals(b,
                        a);

        EndMessageEvent.EndMessageEventBuilder builderMessage = new EndMessageEvent.EndMessageEventBuilder();
        EndMessageEvent e = builderMessage.build();
        assertNotEquals(a,
                        e);
    }

    @Test
    public void testEndSignalEventHashCode() {
        EndSignalEvent.EndSignalEventBuilder builder = new EndSignalEvent.EndSignalEventBuilder();
        EndSignalEvent a = builder.build();
        builder = new EndSignalEvent.EndSignalEventBuilder();
        EndSignalEvent b = builder.build();
        assertEquals(a.hashCode(),
                     b.hashCode());
    }

    @Test
    public void testEndMessageEventEquals() {
        final String MESSAGE_REF = "message ref";
        final MessageEventExecutionSet A_EXECUTION_SET = new MessageEventExecutionSet(new MessageRef(MESSAGE_REF));
        final MessageEventExecutionSet B_EXECUTION_SET = new MessageEventExecutionSet(new MessageRef(MESSAGE_REF));
        final MessageEventExecutionSet C_EXECUTION_SET = new MessageEventExecutionSet(new MessageRef("Other value"));
        final MessageEventExecutionSet D_EXECUTION_SET = new MessageEventExecutionSet(new MessageRef(MESSAGE_REF));

        final String ASSIGNMENT_INFO = "some value";
        final DataIOSet A_DATA_SET = new DataIOSet(new AssignmentsInfo(ASSIGNMENT_INFO));
        final DataIOSet B_DATA_SET = new DataIOSet(new AssignmentsInfo(ASSIGNMENT_INFO));
        final DataIOSet C_DATA_SET = new DataIOSet(new AssignmentsInfo(ASSIGNMENT_INFO));
        final DataIOSet D_DATA_SET = new DataIOSet(new AssignmentsInfo("Other value"));

        EndMessageEvent.EndMessageEventBuilder builder = new EndMessageEvent.EndMessageEventBuilder();
        EndMessageEvent a = builder.build();
        a.setExecutionSet(A_EXECUTION_SET);
        a.setDataIOSet(A_DATA_SET);

        builder = new EndMessageEvent.EndMessageEventBuilder();
        EndMessageEvent b = builder.build();
        b.setExecutionSet(B_EXECUTION_SET);
        b.setDataIOSet(B_DATA_SET);

        builder = new EndMessageEvent.EndMessageEventBuilder();
        EndMessageEvent c = builder.build();
        c.setExecutionSet(C_EXECUTION_SET);
        c.setDataIOSet(C_DATA_SET);

        builder = new EndMessageEvent.EndMessageEventBuilder();
        EndMessageEvent d = builder.build();
        d.setExecutionSet(D_EXECUTION_SET);
        d.setDataIOSet(D_DATA_SET);

        assertEquals(a,
                     a);
        assertEquals(a,
                     b);
        assertNotEquals(a,
                        c);
        assertNotEquals(a,
                        d);
        assertNotEquals(a,
                        19);
        assertNotEquals(a,
                        null);

        a.setExecutionSet(null);
        assertNotEquals(a,
                        b);
        assertNotEquals(b,
                        a);

        a.setExecutionSet(A_EXECUTION_SET);
        assertEquals(a,
                     b);
        assertNotEquals(a,
                        c);
        assertNotEquals(a,
                        d);

        a.setDataIOSet(null);
        assertNotEquals(a,
                        b);
        assertNotEquals(b,
                        a);
        assertNotEquals(a,
                        c);
        assertNotEquals(a,
                        d);

        EndSignalEvent.EndSignalEventBuilder builderSignal = new EndSignalEvent.EndSignalEventBuilder();
        EndSignalEvent e = builderSignal.build();
        assertNotEquals(a,
                        e);
    }

    @Test
    public void testEndMessageEventHashCode() {
        EndMessageEvent.EndMessageEventBuilder builder = new EndMessageEvent.EndMessageEventBuilder();
        EndMessageEvent a = builder.build();
        builder = new EndMessageEvent.EndMessageEventBuilder();
        EndMessageEvent b = builder.build();
        assertEquals(a.hashCode(),
                     b.hashCode());
    }

    @Test
    public void testExclusiveDatabasedGatewayEquals() {
        ExclusiveGateway.ExclusiveGatewayBuilder builder = new ExclusiveGateway.ExclusiveGatewayBuilder();
        ExclusiveGateway a = builder.build();
        builder = new ExclusiveGateway.ExclusiveGatewayBuilder();
        ExclusiveGateway b = builder.build();
        assertEquals(a,
                     b);
        assertFalse(a.equals(19));
        assertFalse(a.equals(null));
    }

    @Test
    public void testExclusiveDatabasedGatewayHashCode() {
        ExclusiveGateway.ExclusiveGatewayBuilder builder = new ExclusiveGateway.ExclusiveGatewayBuilder();
        ExclusiveGateway a = builder.build();
        builder = new ExclusiveGateway.ExclusiveGatewayBuilder();
        ExclusiveGateway b = builder.build();
        assertEquals(a.hashCode(),
                     b.hashCode());
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
    public void IntermediateMessageEventThrowingHashCode() {
        IntermediateMessageEventThrowing.IntermediateMessageEventThrowingBuilder builder = new IntermediateMessageEventThrowing.IntermediateMessageEventThrowingBuilder();
        IntermediateMessageEventThrowing a = builder.build();
        builder = new IntermediateMessageEventThrowing.IntermediateMessageEventThrowingBuilder();
        IntermediateMessageEventThrowing b = builder.build();
        assertEquals(a.hashCode(),
                     b.hashCode());
    }

    @Test
    public void IntermediateMessageEventThrowingEquals() {
        final String MESSAGE_REF = "message ref";
        final MessageEventExecutionSet A_EXECUTION_SET = new MessageEventExecutionSet(new MessageRef(MESSAGE_REF));
        final MessageEventExecutionSet B_EXECUTION_SET = new MessageEventExecutionSet(new MessageRef(MESSAGE_REF));
        final MessageEventExecutionSet C_EXECUTION_SET = new MessageEventExecutionSet(new MessageRef("Other value"));
        final MessageEventExecutionSet D_EXECUTION_SET = new MessageEventExecutionSet(new MessageRef(MESSAGE_REF));

        final String ASSIGNMENT_INFO = "some value";
        final DataIOSet A_DATA_SET = new DataIOSet(new AssignmentsInfo(ASSIGNMENT_INFO));
        final DataIOSet B_DATA_SET = new DataIOSet(new AssignmentsInfo(ASSIGNMENT_INFO));
        final DataIOSet C_DATA_SET = new DataIOSet(new AssignmentsInfo(ASSIGNMENT_INFO));
        final DataIOSet D_DATA_SET = new DataIOSet(new AssignmentsInfo("Other value"));

        IntermediateMessageEventThrowing.IntermediateMessageEventThrowingBuilder builder = new IntermediateMessageEventThrowing.IntermediateMessageEventThrowingBuilder();
        IntermediateMessageEventThrowing a = builder.build();
        a.setExecutionSet(A_EXECUTION_SET);
        a.setDataIOSet(A_DATA_SET);

        builder = new IntermediateMessageEventThrowing.IntermediateMessageEventThrowingBuilder();
        IntermediateMessageEventThrowing b = builder.build();
        b.setExecutionSet(B_EXECUTION_SET);
        b.setDataIOSet(B_DATA_SET);

        builder = new IntermediateMessageEventThrowing.IntermediateMessageEventThrowingBuilder();
        IntermediateMessageEventThrowing c = builder.build();
        c.setExecutionSet(C_EXECUTION_SET);
        c.setDataIOSet(C_DATA_SET);

        builder = new IntermediateMessageEventThrowing.IntermediateMessageEventThrowingBuilder();
        IntermediateMessageEventThrowing d = builder.build();
        d.setExecutionSet(D_EXECUTION_SET);
        d.setDataIOSet(D_DATA_SET);

        assertEquals(a,
                     a);
        assertEquals(a,
                     b);
        assertNotEquals(a,
                        c);
        assertNotEquals(a,
                        d);
        assertNotEquals(a,
                        19);
        assertNotEquals(a,
                        null);

        a.setExecutionSet(null);
        assertNotEquals(a,
                        b);
        assertNotEquals(b,
                        a);
        assertNotEquals(a,
                        c);
        assertNotEquals(a,
                        d);

        a.setExecutionSet(A_EXECUTION_SET);
        assertEquals(a,
                     b);
        assertNotEquals(a,
                        c);
        assertNotEquals(a,
                        d);

        a.setDataIOSet(null);
        assertNotEquals(a,
                        b);
        assertNotEquals(b,
                        a);
        assertNotEquals(a,
                        c);
        assertNotEquals(a,
                        d);

        EndSignalEvent.EndSignalEventBuilder builderSignal = new EndSignalEvent.EndSignalEventBuilder();
        EndSignalEvent e = builderSignal.build();
        assertNotEquals(a,
                        e);
    }

    @Test
    public void IntermediateMessageEventCatchingHashCode() {
        IntermediateMessageEventCatching.IntermediateMessageEventCatchingBuilder builder = new IntermediateMessageEventCatching.IntermediateMessageEventCatchingBuilder();
        IntermediateMessageEventCatching a = builder.build();
        builder = new IntermediateMessageEventCatching.IntermediateMessageEventCatchingBuilder();
        IntermediateMessageEventCatching b = builder.build();
        assertEquals(a.hashCode(),
                     b.hashCode());
    }

    @Test
    public void IntermediateMessageEventCatchingEquals() {
        final String MESSAGE_REF = "message ref";
        final CancellingMessageEventExecutionSet A_EXECUTION_SET = new CancellingMessageEventExecutionSet(new CancelActivity(true),
                                                                                                          new MessageRef(MESSAGE_REF));
        final CancellingMessageEventExecutionSet B_EXECUTION_SET = new CancellingMessageEventExecutionSet(new CancelActivity(true),
                                                                                                          new MessageRef(MESSAGE_REF));
        final CancellingMessageEventExecutionSet C_EXECUTION_SET = new CancellingMessageEventExecutionSet(new CancelActivity(true),
                                                                                                          new MessageRef("Other value"));
        final CancellingMessageEventExecutionSet D_EXECUTION_SET = new CancellingMessageEventExecutionSet(new CancelActivity(true),
                                                                                                          new MessageRef(MESSAGE_REF));

        final String ASSIGNMENT_INFO = "some value";
        final DataIOSet A_DATA_SET = new DataIOSet(new AssignmentsInfo(ASSIGNMENT_INFO));
        final DataIOSet B_DATA_SET = new DataIOSet(new AssignmentsInfo(ASSIGNMENT_INFO));
        final DataIOSet C_DATA_SET = new DataIOSet(new AssignmentsInfo(ASSIGNMENT_INFO));
        final DataIOSet D_DATA_SET = new DataIOSet(new AssignmentsInfo("Other value"));

        IntermediateMessageEventCatching.IntermediateMessageEventCatchingBuilder builder = new IntermediateMessageEventCatching.IntermediateMessageEventCatchingBuilder();
        IntermediateMessageEventCatching a = builder.build();
        a.setExecutionSet(A_EXECUTION_SET);
        a.setDataIOSet(A_DATA_SET);

        builder = new IntermediateMessageEventCatching.IntermediateMessageEventCatchingBuilder();
        IntermediateMessageEventCatching b = builder.build();
        b.setExecutionSet(B_EXECUTION_SET);
        b.setDataIOSet(B_DATA_SET);

        builder = new IntermediateMessageEventCatching.IntermediateMessageEventCatchingBuilder();
        IntermediateMessageEventCatching c = builder.build();
        c.setExecutionSet(C_EXECUTION_SET);
        c.setDataIOSet(C_DATA_SET);

        builder = new IntermediateMessageEventCatching.IntermediateMessageEventCatchingBuilder();
        IntermediateMessageEventCatching d = builder.build();
        d.setExecutionSet(D_EXECUTION_SET);
        d.setDataIOSet(D_DATA_SET);

        assertEquals(a,
                     a);
        assertEquals(a,
                     b);
        assertNotEquals(a,
                        c);
        assertNotEquals(a,
                        d);
        assertNotEquals(a,
                        19);
        assertNotEquals(a,
                        null);

        a.setExecutionSet(null);
        assertNotEquals(a,
                        b);
        assertNotEquals(b,
                        a);
        assertNotEquals(a,
                        c);
        assertNotEquals(a,
                        d);

        a.setExecutionSet(A_EXECUTION_SET);
        assertEquals(a,
                     b);
        assertNotEquals(a,
                        c);
        assertNotEquals(a,
                        d);

        a.setDataIOSet(null);
        assertNotEquals(a,
                        b);
        assertNotEquals(b,
                        a);
        assertNotEquals(a,
                        c);
        assertNotEquals(a,
                        d);

        EndSignalEvent.EndSignalEventBuilder builderSignal = new EndSignalEvent.EndSignalEventBuilder();
        EndSignalEvent e = builderSignal.build();
        assertNotEquals(a,
                        e);
    }

    @Test
    public void testIntermediateTimerEventHashCode() {
        IntermediateTimerEvent.IntermediateTimerEventBuilder builder = new IntermediateTimerEvent.IntermediateTimerEventBuilder();
        IntermediateTimerEvent a = builder.build();
        builder = new IntermediateTimerEvent.IntermediateTimerEventBuilder();
        IntermediateTimerEvent b = builder.build();
        assertEquals(a.hashCode(),
                     b.hashCode());
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
        assertEquals(a.hashCode(),
                     b.hashCode());
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
        assertEquals(a.hashCode(),
                     b.hashCode());
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
        assertEquals(a.hashCode(),
                     b.hashCode());
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
        assertEquals(a.hashCode(),
                     b.hashCode());
    }

    @Test
    public void testEventSubprocessEquals() {
        EventSubprocess.EventSubprocessBuilder builder = new EventSubprocess.EventSubprocessBuilder();
        EventSubprocess a = builder.build();
        builder = new EventSubprocess.EventSubprocessBuilder();
        EventSubprocess b = builder.build();
        assertEquals(a,
                     b);
        assertFalse(a.equals(19));
        assertFalse(a.equals(null));
    }

    @Test
    public void testEventSubprocessHashCode() {
        EventSubprocess.EventSubprocessBuilder builder = new EventSubprocess.EventSubprocessBuilder();
        EventSubprocess a = builder.build();
        builder = new EventSubprocess.EventSubprocessBuilder();
        EventSubprocess b = builder.build();
        assertEquals(a.hashCode(),
                     b.hashCode());
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
        assertEquals(a.hashCode(),
                     b.hashCode());
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
        assertEquals(a.hashCode(),
                     b.hashCode());
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
        assertEquals(a.hashCode(),
                     b.hashCode());
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
        assertEquals(a.hashCode(),
                     b.hashCode());
    }

    @Test
    public void testStartMessageEventEquals() {
        final String MESSAGE_REF = "message ref";
        final InterruptingMessageEventExecutionSet A_EXECUTION_SET = new InterruptingMessageEventExecutionSet(new IsInterrupting(true),
                                                                                                              new MessageRef(MESSAGE_REF));
        final InterruptingMessageEventExecutionSet B_EXECUTION_SET = new InterruptingMessageEventExecutionSet(new IsInterrupting(true),
                                                                                                              new MessageRef(MESSAGE_REF));
        final InterruptingMessageEventExecutionSet C_EXECUTION_SET = new InterruptingMessageEventExecutionSet(new IsInterrupting(true),
                                                                                                              new MessageRef("Other value"));
        final InterruptingMessageEventExecutionSet D_EXECUTION_SET = new InterruptingMessageEventExecutionSet(new IsInterrupting(true),
                                                                                                              new MessageRef(MESSAGE_REF));

        final String ASSIGNMENT_INFO = "some value";
        final DataIOSet A_DATA_SET = new DataIOSet(new AssignmentsInfo(ASSIGNMENT_INFO));
        final DataIOSet B_DATA_SET = new DataIOSet(new AssignmentsInfo(ASSIGNMENT_INFO));
        final DataIOSet C_DATA_SET = new DataIOSet(new AssignmentsInfo(ASSIGNMENT_INFO));
        final DataIOSet D_DATA_SET = new DataIOSet(new AssignmentsInfo("Other value"));

        StartMessageEvent.StartMessageEventBuilder builder = new StartMessageEvent.StartMessageEventBuilder();
        StartMessageEvent a = builder.build();
        a.setExecutionSet(A_EXECUTION_SET);
        a.setDataIOSet(A_DATA_SET);

        builder = new StartMessageEvent.StartMessageEventBuilder();
        StartMessageEvent b = builder.build();
        b.setExecutionSet(B_EXECUTION_SET);
        b.setDataIOSet(B_DATA_SET);

        builder = new StartMessageEvent.StartMessageEventBuilder();
        StartMessageEvent c = builder.build();
        c.setExecutionSet(C_EXECUTION_SET);
        c.setDataIOSet(C_DATA_SET);

        builder = new StartMessageEvent.StartMessageEventBuilder();
        StartMessageEvent d = builder.build();
        d.setExecutionSet(D_EXECUTION_SET);
        d.setDataIOSet(D_DATA_SET);

        assertEquals(a,
                     a);
        assertEquals(a,
                     b);
        assertNotEquals(a,
                        c);
        assertNotEquals(a,
                        d);
        assertNotEquals(a,
                        19);
        assertNotEquals(a,
                        null);

        a.setExecutionSet(null);
        assertNotEquals(a,
                        b);
        assertNotEquals(b,
                        a);
        assertNotEquals(a,
                        c);
        assertNotEquals(a,
                        d);

        a.setExecutionSet(A_EXECUTION_SET);
        assertEquals(a,
                     b);
        assertNotEquals(a,
                        c);
        assertNotEquals(a,
                        d);

        a.setDataIOSet(null);
        assertNotEquals(a,
                        b);
        assertNotEquals(b,
                        a);
        assertNotEquals(a,
                        c);
        assertNotEquals(a,
                        d);

        EndSignalEvent.EndSignalEventBuilder builderSignal = new EndSignalEvent.EndSignalEventBuilder();
        EndSignalEvent e = builderSignal.build();
        assertNotEquals(a,
                        e);
    }

    @Test
    public void testStartMessageEventHashCode() {
        StartMessageEvent.StartMessageEventBuilder builder = new StartMessageEvent.StartMessageEventBuilder();
        StartMessageEvent a = builder.build();
        builder = new StartMessageEvent.StartMessageEventBuilder();
        StartMessageEvent b = builder.build();
        assertEquals(a.hashCode(),
                     b.hashCode());
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
        assertEquals(a.hashCode(),
                     b.hashCode());
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
        assertEquals(a.hashCode(),
                     b.hashCode());
    }

    @Test
    public void testEmbeddedSubprocessEquals() {
        EmbeddedSubprocess.EmbeddedSubprocessBuilder builder = new EmbeddedSubprocess.EmbeddedSubprocessBuilder();
        EmbeddedSubprocess a = builder.build();
        builder = new EmbeddedSubprocess.EmbeddedSubprocessBuilder();
        EmbeddedSubprocess b = builder.build();
        assertEquals(a,
                     b);
        assertEquals(new EmbeddedSubprocess(),
                     new EmbeddedSubprocess());
        assertFalse(a.equals(19));
        assertFalse(a.equals(null));
    }

    @Test
    public void testEmbeddedSubprocessHashCode() {
        EmbeddedSubprocess.EmbeddedSubprocessBuilder builder = new EmbeddedSubprocess.EmbeddedSubprocessBuilder();
        EmbeddedSubprocess a = builder.build();
        builder = new EmbeddedSubprocess.EmbeddedSubprocessBuilder();
        EmbeddedSubprocess b = builder.build();
        assertEquals(a.hashCode(),
                     b.hashCode());
        assertEquals(new EmbeddedSubprocess().hashCode(),
                     new EmbeddedSubprocess().hashCode());
    }

    @Test
    public void testConditionExpressionEqualsAndHashCode() {
        TestCaseBuilder.newTestCase()
                .addTrueCase(new ConditionExpression(),
                             new ConditionExpression())

                .addTrueCase(new ConditionExpression(new ScriptTypeValue("a",
                                                                         "b")),
                             new ConditionExpression(new ScriptTypeValue("a",
                                                                         "b")))
                .addTrueCase(new ConditionExpression(null),
                             new ConditionExpression(null))

                .addFalseCase(new ConditionExpression(new ScriptTypeValue("a",
                                                                          "b")),
                              new ConditionExpression(new ScriptTypeValue("a",
                                                                          "X")))

                .addFalseCase(new ConditionExpression(new ScriptTypeValue("a",
                                                                          "b")),
                              new ConditionExpression(new ScriptTypeValue("Y",
                                                                          "b")))

                .addFalseCase(new ConditionExpression(new ScriptTypeValue("a",
                                                                          "b")),
                              new ConditionExpression(new ScriptTypeValue("Y",
                                                                          "X")))
                .test();
    }

    @Test
    public void testScriptTypeValueEqualsAndHashCode() {
        TestCaseBuilder.newTestCase()
                .addTrueCase(new ScriptTypeValue(),
                             new ScriptTypeValue())

                .addTrueCase(new ScriptTypeValue(null,
                                                 null),
                             new ScriptTypeValue(null,
                                                 null))

                .addTrueCase(new ScriptTypeValue("a",
                                                 "b"),
                             new ScriptTypeValue("a",
                                                 "b"))

                .addTrueCase(new ScriptTypeValue("a",
                                                 null),
                             new ScriptTypeValue("a",
                                                 null))

                .addTrueCase(new ScriptTypeValue(null,
                                                 "b"),
                             new ScriptTypeValue(null,
                                                 "b"))

                .addFalseCase(new ScriptTypeValue("a",
                                                  "b"),
                              new ScriptTypeValue("X",
                                                  "b"))

                .addFalseCase(new ScriptTypeValue("a",
                                                  "b"),
                              new ScriptTypeValue("a",
                                                  "Y"))

                .addFalseCase(new ScriptTypeValue("a",
                                                  "b"),
                              new ScriptTypeValue("X",
                                                  "Y"))
                .test();
    }

    @Test
    public void testSequenceFlowExecutionSetEqualsAndHashCode() {
        TestCaseBuilder.newTestCase()
                .addTrueCase(new SequenceFlowExecutionSet(),
                             new SequenceFlowExecutionSet())

                .addTrueCase(new SequenceFlowExecutionSet(null,
                                                          null),
                             new SequenceFlowExecutionSet(null,
                                                          null))

                .addTrueCase(new SequenceFlowExecutionSet(new Priority(),
                                                          new ConditionExpression()),
                             new SequenceFlowExecutionSet(new Priority(),
                                                          new ConditionExpression()))

                .addTrueCase(new SequenceFlowExecutionSet(new Priority("1"),
                                                          new ConditionExpression(new ScriptTypeValue("a",
                                                                                                      "b"))),
                             new SequenceFlowExecutionSet(new Priority("1"),
                                                          new ConditionExpression(new ScriptTypeValue("a",
                                                                                                      "b"))))

                .addFalseCase(new SequenceFlowExecutionSet(new Priority("1"),
                                                           new ConditionExpression(new ScriptTypeValue("a",
                                                                                                       "b"))),
                              new SequenceFlowExecutionSet(new Priority("2"),
                                                           new ConditionExpression(new ScriptTypeValue("a",
                                                                                                       "b"))))

                .addFalseCase(new SequenceFlowExecutionSet(new Priority("1"),
                                                           new ConditionExpression(new ScriptTypeValue("a",
                                                                                                       "b"))),
                              new SequenceFlowExecutionSet(new Priority("1"),
                                                           new ConditionExpression(new ScriptTypeValue("X",
                                                                                                       "Y"))))

                .addFalseCase(new SequenceFlowExecutionSet(new Priority("1"),
                                                           new ConditionExpression(new ScriptTypeValue("a",
                                                                                                       "b"))),
                              new SequenceFlowExecutionSet(new Priority("2"),
                                                           new ConditionExpression(new ScriptTypeValue("X",
                                                                                                       "Y"))))
                .test();
    }

    @Test
    public void testAdHocCompletionConditionEqualsAndHashCode() {
        TestCaseBuilder.newTestCase()
                .addTrueCase(new AdHocCompletionCondition(),
                             new AdHocCompletionCondition())

                .addTrueCase(new AdHocCompletionCondition(new ScriptTypeValue()),
                             new AdHocCompletionCondition(new ScriptTypeValue()))

                .addTrueCase(new AdHocCompletionCondition(new ScriptTypeValue(null,
                                                                              null)),
                             new AdHocCompletionCondition(new ScriptTypeValue(null,
                                                                              null)))

                .addTrueCase(new AdHocCompletionCondition(new ScriptTypeValue("a",
                                                                              "b")),
                             new AdHocCompletionCondition(new ScriptTypeValue("a",
                                                                              "b")))

                .addTrueCase(new AdHocCompletionCondition(new ScriptTypeValue("a",
                                                                              null)),
                             new AdHocCompletionCondition(new ScriptTypeValue("a",
                                                                              null)))

                .addTrueCase(new AdHocCompletionCondition(new ScriptTypeValue(null,
                                                                              "b")),
                             new AdHocCompletionCondition(new ScriptTypeValue(null,
                                                                              "b")))

                .addFalseCase(new AdHocCompletionCondition(new ScriptTypeValue("a",
                                                                               "b")),
                              new AdHocCompletionCondition(new ScriptTypeValue("X",
                                                                               "b")))

                .addFalseCase(new AdHocCompletionCondition(new ScriptTypeValue("a",
                                                                               "b")),
                              new AdHocCompletionCondition(new ScriptTypeValue("a",
                                                                               "Y")))

                .addFalseCase(new AdHocCompletionCondition(new ScriptTypeValue("a",
                                                                               "b")),
                              new AdHocCompletionCondition(new ScriptTypeValue("X",
                                                                               "Y")))
                .test();
    }

    @Test
    public void testAdHocOrderingEqualsAndHash() {
        TestCaseBuilder.newTestCase()
                .addTrueCase(new AdHocOrdering(),
                             new AdHocOrdering())

                .addTrueCase(new AdHocOrdering(null),
                             new AdHocOrdering(null))

                .addTrueCase(new AdHocOrdering("a"),
                             new AdHocOrdering("a"))

                .addFalseCase(new AdHocOrdering("a"),
                              new AdHocOrdering(null))

                .addFalseCase(new AdHocOrdering(null),
                              new AdHocOrdering("a"))

                .test();
    }

    @Test
    public void testAdHocSubprocessTaskExecutionSEtSetEqualsAndHashCode() {
        TestCaseBuilder.newTestCase()
                .addTrueCase(new AdHocSubprocessTaskExecutionSet(),
                             new AdHocSubprocessTaskExecutionSet())

                .addTrueCase(new AdHocSubprocessTaskExecutionSet(new AdHocCompletionCondition(),
                                                                 new AdHocOrdering(),
                                                                 new OnEntryAction(),
                                                                 new OnExitAction()),
                             new AdHocSubprocessTaskExecutionSet(new AdHocCompletionCondition(),
                                                                 new AdHocOrdering(),
                                                                 new OnEntryAction(),
                                                                 new OnExitAction()))
                .test();
    }

    @Test
    public void testBusinessRuleTaskExecutionSetEqualsAndHashCode() {
        TestCaseBuilder.newTestCase()
                .addTrueCase(new BusinessRuleTaskExecutionSet(),
                             new BusinessRuleTaskExecutionSet())

                .addTrueCase(new BusinessRuleTaskExecutionSet(new RuleFlowGroup(),
                                                              new OnEntryAction(),
                                                              new OnExitAction(),
                                                              new IsAsync(),
                                                              new AdHocAutostart()),
                             new BusinessRuleTaskExecutionSet(new RuleFlowGroup(),
                                                              new OnEntryAction(),
                                                              new OnExitAction(),
                                                              new IsAsync(),
                                                              new AdHocAutostart()))
                .test();
    }

    @Test
    public void testOnEntryActionEqualsAndHashCode() {
        TestCaseBuilder.newTestCase()
                .addTrueCase(new OnEntryAction(),
                             new OnEntryAction())

                .addTrueCase(new OnEntryAction(new ScriptTypeListValue()),
                             new OnEntryAction(new ScriptTypeListValue()))
                .test();
    }

    @Test
    public void testOnExitActionEqualsAndHashCode() {
        TestCaseBuilder.newTestCase()
                .addTrueCase(new OnExitAction(),
                             new OnExitAction())

                .addTrueCase(new OnExitAction(new ScriptTypeListValue()),
                             new OnExitAction(new ScriptTypeListValue()))
                .test();
    }

    @Test
    public void testScriptEqualsAndHashCode() {
        TestCaseBuilder.newTestCase()
                .addTrueCase(new Script(),
                             new Script())

                .addTrueCase(new Script(new ScriptTypeValue()),
                             new Script(new ScriptTypeValue()))
                .test();
    }

    @Test
    public void testScriptTaskExecutionSetEqualsAndHashCode() {
        TestCaseBuilder.newTestCase()
                .addTrueCase(new ScriptTaskExecutionSet(),
                             new ScriptTaskExecutionSet())

                .addTrueCase(new ScriptTaskExecutionSet(new Script(),
                                                        new IsAsync()),

                             new ScriptTaskExecutionSet(new Script(),
                                                        new IsAsync()))
                .test();
    }

    @Test
    public void testScriptTypeListValueEqualsAndHashCode() {
        TestCaseBuilder.newTestCase()
                .addTrueCase(new ScriptTypeListValue(),
                             new ScriptTypeListValue())

                .addTrueCase(new ScriptTypeListValue().addValue(new ScriptTypeValue()),
                             new ScriptTypeListValue().addValue(new ScriptTypeValue()))

                .addTrueCase(new ScriptTypeListValue().addValue(new ScriptTypeValue(null,
                                                                                    null)),
                             new ScriptTypeListValue().addValue(new ScriptTypeValue(null,
                                                                                    null)))

                .addTrueCase(new ScriptTypeListValue().addValue(new ScriptTypeValue("a",
                                                                                    "b")),
                             new ScriptTypeListValue().addValue(new ScriptTypeValue("a",
                                                                                    "b")))

                .addTrueCase(new ScriptTypeListValue().addValue(new ScriptTypeValue("a",
                                                                                    null)),
                             new ScriptTypeListValue().addValue(new ScriptTypeValue("a",
                                                                                    null)))

                .addTrueCase(new ScriptTypeListValue().addValue(new ScriptTypeValue(null,
                                                                                    "b")),
                             new ScriptTypeListValue().addValue(new ScriptTypeValue(null,
                                                                                    "b")))

                .addFalseCase(new ScriptTypeListValue().addValue(new ScriptTypeValue("a",
                                                                                     "b")),
                              new ScriptTypeListValue().addValue(new ScriptTypeValue("X",
                                                                                     "b")))

                .addFalseCase(new ScriptTypeListValue().addValue(new ScriptTypeValue("a",
                                                                                     "b")),
                              new ScriptTypeListValue().addValue(new ScriptTypeValue("a",
                                                                                     "Y")))

                .addFalseCase(new ScriptTypeListValue().addValue(new ScriptTypeValue("a",
                                                                                     "b")),
                              new ScriptTypeListValue().addValue(new ScriptTypeValue("X",
                                                                                     "Y")))
                .test();
    }

    @Test
    public void testUserTaskExecutionSetEqualsAndHashCode() {
        TestCaseBuilder.newTestCase()
                .addTrueCase(new UserTaskExecutionSet(),
                             new UserTaskExecutionSet())

                .addTrueCase(new UserTaskExecutionSet(new TaskName(),
                                                      new Actors(),
                                                      new Groupid(),
                                                      new AssignmentsInfo(),
                                                      new IsAsync(),
                                                      new Skippable(),
                                                      new Priority(),
                                                      new Subject(),
                                                      new Description(),
                                                      new CreatedBy(),
                                                      new AdHocAutostart(),
                                                      new OnEntryAction(),
                                                      new OnExitAction()),

                             new UserTaskExecutionSet(new TaskName(),
                                                      new Actors(),
                                                      new Groupid(),
                                                      new AssignmentsInfo(),
                                                      new IsAsync(),
                                                      new Skippable(),
                                                      new Priority(),
                                                      new Subject(),
                                                      new Description(),
                                                      new CreatedBy(),
                                                      new AdHocAutostart(),
                                                      new OnEntryAction(),
                                                      new OnExitAction()))
                .test();
    }

    @Test
    public void testInclusiveGatewayEqualsAndHashCode() {
        TestCaseBuilder.newTestCase()
                .addTrueCase(new InclusiveGateway(),
                             new InclusiveGateway())
                .addTrueCase(new InclusiveGateway(new BPMNGeneralSet(),
                                                  new BackgroundSet(),
                                                  new FontSet(),
                                                  new CircleDimensionSet(),
                                                  new GatewayExecutionSet()),
                             new InclusiveGateway(new BPMNGeneralSet(),
                                                  new BackgroundSet(),
                                                  new FontSet(),
                                                  new CircleDimensionSet(),
                                                  new GatewayExecutionSet()))
                .test();
    }

    @Test
    public void testGatewayExecutionSetEqualsAndHashCode() {
        TestCaseBuilder.newTestCase()
                .addTrueCase(new GatewayExecutionSet(),
                             new GatewayExecutionSet())
                .addTrueCase(new GatewayExecutionSet(new DefaultRoute()),
                             new GatewayExecutionSet(new DefaultRoute()))
                .test();
    }

    @Test
    public void testDefaultRouteEqualsAndHashCode() {
        TestCaseBuilder.newTestCase()
                .addTrueCase(new DefaultRoute(),
                             new DefaultRoute())
                .addTrueCase(new DefaultRoute("A"),
                             new DefaultRoute("A"))
                .test();
    }

    @Test
    public void testCircleDimensionSetEqualsAndHashCode() {
        TestCaseBuilder.newTestCase()
                .addTrueCase(new CircleDimensionSet(),
                             new CircleDimensionSet())
                .addTrueCase(new CircleDimensionSet(new Radius()),
                             new CircleDimensionSet(new Radius()))
                .test();
    }

    @Test
    public void testBPMNGeneralSetEqualsAndHashCode() {
        TestCaseBuilder.newTestCase()
                .addTrueCase(new BPMNGeneralSet(),
                             new BPMNGeneralSet())
                .addTrueCase(new BPMNGeneralSet(new Name(),
                                                new Documentation()),
                             new BPMNGeneralSet(new Name(),
                                                new Documentation()))
                .test();
    }

    @Test
    public void testBackgroundSetEqualsAndHashCode() {
        TestCaseBuilder.newTestCase()
                .addTrueCase(new BackgroundSet(),
                             new BackgroundSet())
                .addTrueCase(new BackgroundSet(new BgColor(),
                                               new BorderColor(),
                                               new BorderSize()),
                             new BackgroundSet(new BgColor(),
                                               new BorderColor(),
                                               new BorderSize()))
                .test();
    }

    @Test
    public void testFontSetEqualsAndHashCode() {
        TestCaseBuilder.newTestCase()
                .addTrueCase(new FontSet(),
                             new FontSet())
                .addTrueCase(new FontSet(new FontFamily(),
                                         new FontColor(),
                                         new FontSize(),
                                         new FontBorderSize(),
                                         new FontBorderColor()),
                             new FontSet(new FontFamily(),
                                         new FontColor(),
                                         new FontSize(),
                                         new FontBorderSize(),
                                         new FontBorderColor()))
                .test();
    }

    public static class HashCodeAndEqualityTestCase {

        private Object a;
        private Object b;
        private boolean expectedResult = true;

        public HashCodeAndEqualityTestCase(Object a,
                                           Object b,
                                           boolean expectedResult) {
            this.a = a;
            this.b = b;
            this.expectedResult = expectedResult;
        }

        public Object getA() {
            return a;
        }

        public Object getB() {
            return b;
        }

        public boolean isExpectedResult() {
            return expectedResult;
        }
    }

    public static class TestCaseBuilder {

        private List<HashCodeAndEqualityTestCase> testCases = new ArrayList<>();

        private TestCaseBuilder() {
        }

        public static TestCaseBuilder newTestCase() {
            return new TestCaseBuilder();
        }

        public TestCaseBuilder addTrueCase(Object a,
                                           Object b) {
            testCases.add(new HashCodeAndEqualityTestCase(a,
                                                          b,
                                                          true));
            return this;
        }

        public TestCaseBuilder addFalseCase(Object a,
                                            Object b) {
            testCases.add(new HashCodeAndEqualityTestCase(a,
                                                          b,
                                                          false));
            return this;
        }

        public void test() {
            testHashCodeAndEquality(testCases);
        }
    }

    public static void testHashCodeAndEquality(Collection<HashCodeAndEqualityTestCase> testCases) {
        int index = 0;
        for (HashCodeAndEqualityTestCase testCase : testCases) {
            if (testCase.isExpectedResult()) {
                assertEquals("Equality check failed for test case element: " + index + " expected result is: " + testCase.isExpectedResult(),
                             testCase.getA(),
                             testCase.getB());
                assertEquals("HashCode check failed for test case element: " + index + " expected result is: " + testCase.isExpectedResult(),
                             Objects.hashCode(testCase.getA()),
                             Objects.hashCode(testCase.getB()));
            } else {
                assertNotEquals("Equality check failed for test case element: " + index + " expected result is: " + testCase.isExpectedResult(),
                                testCase.getA(),
                                testCase.getB());
                assertNotEquals("HashCode check failed for test case element: " + index + " expected result is: " + testCase.isExpectedResult(),
                                Objects.hashCode(testCase.getA()),
                                Objects.hashCode(testCase.getB()));
            }
            index++;
        }
    }
}
