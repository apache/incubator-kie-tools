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
import org.kie.workbench.common.stunner.bpmn.definition.property.simulation.SimulationAttributeSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.AdHocAutostart;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.AdHocCompletionCondition;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.AdHocOrdering;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.AdHocSubprocessTaskExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.BusinessRuleTaskExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.CreatedBy;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.Description;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.IsAsync;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.MITrigger;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.MultipleInstanceCollectionInput;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.MultipleInstanceCollectionOutput;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.MultipleInstanceCompletionCondition;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.MultipleInstanceDataInput;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.MultipleInstanceDataOutput;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.MultipleInstanceSubprocessTaskExecutionSet;
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
import org.kie.workbench.common.stunner.bpmn.definition.property.variables.ProcessData;
import org.kie.workbench.common.stunner.bpmn.definition.property.variables.ProcessVariables;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class HashCodeAndEqualityTest {

    @Test
    public void testAdHocSubprocessEquals() {
        AdHocSubprocess a = new AdHocSubprocess();
        AdHocSubprocess b = new AdHocSubprocess();;
        assertEquals(a,
                     b);
        assertEquals(new AdHocSubprocess(),
                     new AdHocSubprocess());
        assertFalse(a.equals(19));
        assertFalse(a.equals(null));
    }

    @Test
    public void testAdHocSubprocessHashCode() {
        AdHocSubprocess a = new AdHocSubprocess();
        AdHocSubprocess b = new AdHocSubprocess();;
        assertTrue(a.hashCode() == b.hashCode());
        assertTrue(new AdHocSubprocess().hashCode() == new AdHocSubprocess().hashCode());
    }

    @Test
    public void testBPMNDiagramImplEquals() {
        BPMNDiagramImpl a = new BPMNDiagramImpl();
        BPMNDiagramImpl b = new BPMNDiagramImpl();
        assertEquals(a,
                     b);
        assertFalse(a.equals(19));
        assertFalse(a.equals(null));
    }

    @Test
    public void testBPMNDiagramImplHashCode() {
        BPMNDiagramImpl a = new BPMNDiagramImpl();
        BPMNDiagramImpl b = new BPMNDiagramImpl();
        assertEquals(a.hashCode(),
                     b.hashCode());
    }

    @Test
    public void testBusinessRuleTaskEquals() {
        BusinessRuleTask a = new BusinessRuleTask();
        BusinessRuleTask b = new BusinessRuleTask();
        assertEquals(a,
                     b);
        assertFalse(a.equals(19));
        assertFalse(a.equals(null));
    }

    @Test
    public void testBusinessRuleTaskHashCode() {
        BusinessRuleTask a = new BusinessRuleTask();
        BusinessRuleTask b = new BusinessRuleTask();
        assertEquals(a.hashCode(),
                     b.hashCode());
    }

    @Test
    public void testEndNoneEventEquals() {
        BusinessRuleTask a = new BusinessRuleTask();
        BusinessRuleTask b = new BusinessRuleTask();
        assertEquals(a,
                     b);
        assertFalse(a.equals(19));
        assertFalse(a.equals(null));
    }

    @Test
    public void testEndNoneEventHashCode() {
        BusinessRuleTask a = new BusinessRuleTask();
        BusinessRuleTask b = new BusinessRuleTask();

        assertEquals(a.hashCode(),
                     b.hashCode());
    }

    @Test
    public void testEndTerminateEventEquals() {
        EndTerminateEvent a = new EndTerminateEvent();
        EndTerminateEvent b = new EndTerminateEvent();
        assertEquals(a,
                     b);
        assertFalse(a.equals(19));
        assertFalse(a.equals(null));
    }

    @Test
    public void testEndTerminateEventHashCode() {
        EndTerminateEvent a = new EndTerminateEvent();
        EndTerminateEvent b = new EndTerminateEvent();
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

        EndSignalEvent a = new EndSignalEvent();
        a.setExecutionSet(A_EXECUTION_SET);
        a.setDataIOSet(A_DATA_SET);

        EndSignalEvent b = new EndSignalEvent();
        b.setExecutionSet(B_EXECUTION_SET);
        b.setDataIOSet(B_DATA_SET);

        EndSignalEvent c = new EndSignalEvent();
        c.setExecutionSet(C_EXECUTION_SET);
        c.setDataIOSet(C_DATA_SET);

        EndSignalEvent d = new EndSignalEvent();
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

        EndMessageEvent e = new EndMessageEvent();
        assertNotEquals(a,
                        e);
    }

    @Test
    public void testEndSignalEventHashCode() {
        EndSignalEvent a = new EndSignalEvent();
        EndSignalEvent b = new EndSignalEvent();
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

        EndMessageEvent a = new EndMessageEvent();
        a.setExecutionSet(A_EXECUTION_SET);
        a.setDataIOSet(A_DATA_SET);

        EndMessageEvent b = new EndMessageEvent();
        b.setExecutionSet(B_EXECUTION_SET);
        b.setDataIOSet(B_DATA_SET);

        EndMessageEvent c = new EndMessageEvent();
        c.setExecutionSet(C_EXECUTION_SET);
        c.setDataIOSet(C_DATA_SET);

        EndMessageEvent d = new EndMessageEvent();
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

        EndSignalEvent e = new EndSignalEvent();
        assertNotEquals(a,
                        e);
    }

    @Test
    public void testEndMessageEventHashCode() {
        EndMessageEvent a = new EndMessageEvent();
        EndMessageEvent b = new EndMessageEvent();
        assertEquals(a.hashCode(),
                     b.hashCode());
    }

    @Test
    public void testExclusiveDatabasedGatewayEquals() {
        ExclusiveGateway a = new ExclusiveGateway();
        ExclusiveGateway b = new ExclusiveGateway();
        assertEquals(a,
                     b);
        assertFalse(a.equals(19));
        assertFalse(a.equals(null));
    }

    @Test
    public void testExclusiveDatabasedGatewayHashCode() {
        ExclusiveGateway a = new ExclusiveGateway();
        ExclusiveGateway b = new ExclusiveGateway();
        assertEquals(a.hashCode(),
                     b.hashCode());
    }

    @Test
    public void testIntermediateTimerEventEquals() {
        IntermediateTimerEvent a = new IntermediateTimerEvent();
        IntermediateTimerEvent b = new IntermediateTimerEvent();
        assertEquals(a,
                     b);
        assertFalse(a.equals(19));
        assertFalse(a.equals(null));
    }

    @Test
    public void IntermediateMessageEventThrowingHashCode() {
        IntermediateMessageEventThrowing a = new IntermediateMessageEventThrowing();
        IntermediateMessageEventThrowing b = new IntermediateMessageEventThrowing();
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

        IntermediateMessageEventThrowing a = new IntermediateMessageEventThrowing();
        a.setExecutionSet(A_EXECUTION_SET);
        a.setDataIOSet(A_DATA_SET);

        IntermediateMessageEventThrowing b = new IntermediateMessageEventThrowing();
        b.setExecutionSet(B_EXECUTION_SET);
        b.setDataIOSet(B_DATA_SET);

        IntermediateMessageEventThrowing c = new IntermediateMessageEventThrowing();
        c.setExecutionSet(C_EXECUTION_SET);
        c.setDataIOSet(C_DATA_SET);

        IntermediateMessageEventThrowing d = new IntermediateMessageEventThrowing();
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

        EndSignalEvent e = new EndSignalEvent();
        assertNotEquals(a,
                        e);
    }

    @Test
    public void IntermediateMessageEventCatchingHashCode() {
        IntermediateMessageEventCatching a = new IntermediateMessageEventCatching();
        IntermediateMessageEventCatching b = new IntermediateMessageEventCatching();
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

        IntermediateMessageEventCatching a = new IntermediateMessageEventCatching();
        a.setExecutionSet(A_EXECUTION_SET);
        a.setDataIOSet(A_DATA_SET);

        IntermediateMessageEventCatching b = new IntermediateMessageEventCatching();
        b.setExecutionSet(B_EXECUTION_SET);
        b.setDataIOSet(B_DATA_SET);

        IntermediateMessageEventCatching c = new IntermediateMessageEventCatching();
        c.setExecutionSet(C_EXECUTION_SET);
        c.setDataIOSet(C_DATA_SET);

        IntermediateMessageEventCatching d = new IntermediateMessageEventCatching();
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

        EndSignalEvent e = new EndSignalEvent();
        assertNotEquals(a,
                        e);
    }

    @Test
    public void testIntermediateTimerEventHashCode() {
        IntermediateTimerEvent a = new IntermediateTimerEvent();
        IntermediateTimerEvent b = new IntermediateTimerEvent();
        assertEquals(a.hashCode(),
                     b.hashCode());
    }

    @Test
    public void testLaneEquals() {
        Lane a = new Lane();
        Lane b = new Lane();
        assertEquals(a,
                     b);
        assertFalse(a.equals(19));
        assertFalse(a.equals(null));
    }

    @Test
    public void testLaneHashCode() {
        Lane a = new Lane();
        Lane b = new Lane();
        assertEquals(a.hashCode(),
                     b.hashCode());
    }

    @Test
    public void testNoneTaskEquals() {
        NoneTask a = new NoneTask();
        NoneTask b = new NoneTask();
        assertEquals(a,
                     b);
        assertFalse(a.equals(19));
        assertFalse(a.equals(null));
    }

    @Test
    public void testNoneTaskHashCode() {
        NoneTask a = new NoneTask();
        NoneTask b = new NoneTask();
        assertEquals(a.hashCode(),
                     b.hashCode());
    }

    @Test
    public void testParallelGatewayEquals() {
        ParallelGateway a = new ParallelGateway();
        ParallelGateway b = new ParallelGateway();
        assertEquals(a,
                     b);
        assertFalse(a.equals(19));
        assertFalse(a.equals(null));
    }

    @Test
    public void testParallelGatewayHashCode() {
        ParallelGateway a = new ParallelGateway();
        ParallelGateway b = new ParallelGateway();
        assertEquals(a.hashCode(),
                     b.hashCode());
    }

    @Test
    public void testReusableSubprocessEquals() {
        ReusableSubprocess a = new ReusableSubprocess();
        ReusableSubprocess b = new ReusableSubprocess();
        assertEquals(a,
                     b);
        assertFalse(a.equals(19));
        assertFalse(a.equals(null));
    }

    @Test
    public void testReusableSubprocessHashCode() {
        ReusableSubprocess a = new ReusableSubprocess();
        ReusableSubprocess b = new ReusableSubprocess();
        assertEquals(a.hashCode(),
                     b.hashCode());
    }

    @Test
    public void testEventSubprocessEquals() {
        EventSubprocess a = new EventSubprocess();
        EventSubprocess b = new EventSubprocess();
        assertEquals(a,
                     b);
        assertFalse(a.equals(19));
        assertFalse(a.equals(null));
    }

    @Test
    public void testEventSubprocessHashCode() {
        EventSubprocess a = new EventSubprocess();
        EventSubprocess b = new EventSubprocess();
        assertEquals(a.hashCode(),
                     b.hashCode());
    }

    @Test
    public void testScriptTaskEquals() {
        ScriptTask a = new ScriptTask();
        ScriptTask b = new ScriptTask();
        assertEquals(a,
                     b);
        assertFalse(a.equals(19));
        assertFalse(a.equals(null));
    }

    @Test
    public void testScriptTaskHashCode() {
        ScriptTask a = new ScriptTask();
        ScriptTask b = new ScriptTask();
        assertEquals(a.hashCode(),
                     b.hashCode());
    }

    @Test
    public void testSequenceFlowEquals() {
        SequenceFlow a = new SequenceFlow();
        SequenceFlow b = new SequenceFlow();
        assertEquals(a,
                     b);
        assertFalse(a.equals(19));
        assertFalse(a.equals(null));
    }

    @Test
    public void testSequenceFlowHashCode() {
        SequenceFlow a = new SequenceFlow();
        SequenceFlow b = new SequenceFlow();
        assertEquals(a.hashCode(),
                     b.hashCode());
    }

    @Test
    public void testStartNoneEventEqualsAndHasCode() {
        TestCaseBuilder.newTestCase()
                .addTrueCase(new StartNoneEvent(),
                             new StartNoneEvent())
                .addTrueCase(new StartNoneEvent(new BPMNGeneralSet(),
                                                new BackgroundSet(),
                                                new FontSet(),
                                                new CircleDimensionSet(),
                                                new SimulationAttributeSet(),
                                                new IsInterrupting()),
                             new StartNoneEvent(new BPMNGeneralSet(),
                                                new BackgroundSet(),
                                                new FontSet(),
                                                new CircleDimensionSet(),
                                                new SimulationAttributeSet(),
                                                new IsInterrupting()))
                .addTrueCase(new StartNoneEvent(),
                              new StartNoneEvent(new BPMNGeneralSet(),
                                                 new BackgroundSet(),
                                                 new FontSet(),
                                                 new CircleDimensionSet(),
                                                 new SimulationAttributeSet(),
                                                 new IsInterrupting()))
                .addTrueCase(new StartNoneEvent(new BPMNGeneralSet(),
                                                 new BackgroundSet(),
                                                 new FontSet(),
                                                 new CircleDimensionSet(),
                                                 new SimulationAttributeSet(),
                                                 new IsInterrupting()),
                              new StartNoneEvent())
                .test();
    }

    @Test
    public void testStartSignalEventEquals() {
        StartSignalEvent a = new StartSignalEvent();
        StartSignalEvent b = new StartSignalEvent();
        assertEquals(a,
                     b);
        assertFalse(a.equals(19));
        assertFalse(a.equals(null));
    }

    @Test
    public void testStartSignalEventHashCode() {
        StartSignalEvent a = new StartSignalEvent();
        StartSignalEvent b = new StartSignalEvent();
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

        StartMessageEvent a = new StartMessageEvent();
        a.setExecutionSet(A_EXECUTION_SET);
        a.setDataIOSet(A_DATA_SET);

        StartMessageEvent b = new StartMessageEvent();
        b.setExecutionSet(B_EXECUTION_SET);
        b.setDataIOSet(B_DATA_SET);

        StartMessageEvent c = new StartMessageEvent();
        c.setExecutionSet(C_EXECUTION_SET);
        c.setDataIOSet(C_DATA_SET);

        StartMessageEvent d = new StartMessageEvent();
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

        EndSignalEvent e = new EndSignalEvent();
        assertNotEquals(a,
                        e);
    }

    @Test
    public void testStartMessageEventHashCode() {
        StartMessageEvent a = new StartMessageEvent();
        StartMessageEvent b = new StartMessageEvent();
        assertEquals(a.hashCode(),
                     b.hashCode());
    }

    @Test
    public void testStartTimerEventEquals() {
        StartTimerEvent a = new StartTimerEvent();
        StartTimerEvent b = new StartTimerEvent();
        assertEquals(a,
                     b);
        assertFalse(a.equals(19));
        assertFalse(a.equals(null));
    }

    @Test
    public void testStartTimerEventHashCode() {
        StartTimerEvent a = new StartTimerEvent();
        StartTimerEvent b = new StartTimerEvent();
        assertEquals(a.hashCode(),
                     b.hashCode());
    }

    @Test
    public void testUserTaskEquals() {
        UserTask a = new UserTask();
        UserTask b = new UserTask();
        assertEquals(a,
                     b);
        assertFalse(a.equals(19));
        assertFalse(a.equals(null));
    }

    @Test
    public void testUserTaskHashCode() {
        UserTask a = new UserTask();
        UserTask b = new UserTask();
        assertEquals(a.hashCode(),
                     b.hashCode());
    }

    @Test
    public void testEmbeddedSubprocessEquals() {
        EmbeddedSubprocess a = new EmbeddedSubprocess();
        EmbeddedSubprocess b = new EmbeddedSubprocess();
        assertEquals(a,
                     b);
        assertEquals(new EmbeddedSubprocess(),
                     new EmbeddedSubprocess());
        assertFalse(a.equals(19));
        assertFalse(a.equals(null));
    }

    @Test
    public void testEmbeddedSubprocessHashCode() {
        EmbeddedSubprocess a = new EmbeddedSubprocess();
        EmbeddedSubprocess b = new EmbeddedSubprocess();
        assertEquals(a.hashCode(),
                     b.hashCode());
        assertEquals(new EmbeddedSubprocess().hashCode(),
                     new EmbeddedSubprocess().hashCode());
    }

    @Test
    public void testMultipleInstanceSubprocessEquals() {
        final String MULTIPLE_INSTANCE_COLLECTION_INPUT = "multiple Instance collection input";
        final String MULTIPLE_INSTANCE_COLLECTION_OUTPUT = "multiple Instance collection output";
        final String MULTIPLE_INSTANCE_DATA_INPUT = "multiple Instance collection input";
        final String MULTIPLE_INSTANCE_DATA_OUTPUT = "multiple Instance collection output";
        final String MULTIPLE_INSTANCE_COMPLETION_CONDITION = "multiple Instance completion condition";
        final String OTHER_VALUE = "other value";

        final ScriptTypeListValue ON_ENTRY_ACTION = new ScriptTypeListValue().addValue(new ScriptTypeValue("java",
                                                                                                           ""));
        final ScriptTypeListValue ON_EXIT_ACTION = new ScriptTypeListValue().addValue(new ScriptTypeValue("java",
                                                                                                          ""));
        final Boolean IS_ASYNC = true;

        final MultipleInstanceSubprocessTaskExecutionSet A_EXECUTION_SET
                = new MultipleInstanceSubprocessTaskExecutionSet
                (new MultipleInstanceCollectionInput(MULTIPLE_INSTANCE_COLLECTION_INPUT),
                 new MultipleInstanceCollectionOutput(MULTIPLE_INSTANCE_COLLECTION_OUTPUT),
                 new MultipleInstanceDataInput(MULTIPLE_INSTANCE_DATA_INPUT),
                 new MultipleInstanceDataOutput(MULTIPLE_INSTANCE_DATA_OUTPUT),
                 new MultipleInstanceCompletionCondition(MULTIPLE_INSTANCE_COMPLETION_CONDITION),
                 new OnEntryAction(ON_ENTRY_ACTION),
                 new OnExitAction(ON_EXIT_ACTION),
                 new MITrigger("true"),
                 new IsAsync(IS_ASYNC));

        final MultipleInstanceSubprocessTaskExecutionSet B_EXECUTION_SET = new MultipleInstanceSubprocessTaskExecutionSet(
                new MultipleInstanceCollectionInput(MULTIPLE_INSTANCE_COLLECTION_INPUT),
                new MultipleInstanceCollectionOutput(MULTIPLE_INSTANCE_COLLECTION_OUTPUT),
                new MultipleInstanceDataInput(MULTIPLE_INSTANCE_DATA_INPUT),
                new MultipleInstanceDataOutput(MULTIPLE_INSTANCE_DATA_OUTPUT),
                new MultipleInstanceCompletionCondition(MULTIPLE_INSTANCE_COMPLETION_CONDITION),
                new OnEntryAction(ON_ENTRY_ACTION),
                new OnExitAction(ON_EXIT_ACTION),
                new MITrigger("true"),
                new IsAsync(IS_ASYNC));

        final MultipleInstanceSubprocessTaskExecutionSet C_EXECUTION_SET = new MultipleInstanceSubprocessTaskExecutionSet(
                new MultipleInstanceCollectionInput(OTHER_VALUE),
                new MultipleInstanceCollectionOutput(MULTIPLE_INSTANCE_COLLECTION_OUTPUT),
                new MultipleInstanceDataInput(MULTIPLE_INSTANCE_DATA_INPUT),
                new MultipleInstanceDataOutput(MULTIPLE_INSTANCE_DATA_OUTPUT),
                new MultipleInstanceCompletionCondition(MULTIPLE_INSTANCE_COMPLETION_CONDITION),
                new OnEntryAction(ON_ENTRY_ACTION),
                new OnExitAction(ON_EXIT_ACTION),
                new MITrigger("true"),
                new IsAsync(IS_ASYNC));

        final MultipleInstanceSubprocessTaskExecutionSet D_EXECUTION_SET = new MultipleInstanceSubprocessTaskExecutionSet(
                new MultipleInstanceCollectionInput(MULTIPLE_INSTANCE_COLLECTION_INPUT),
                new MultipleInstanceCollectionOutput(OTHER_VALUE),
                new MultipleInstanceDataInput(MULTIPLE_INSTANCE_DATA_INPUT),
                new MultipleInstanceDataOutput(MULTIPLE_INSTANCE_DATA_OUTPUT),
                new MultipleInstanceCompletionCondition(MULTIPLE_INSTANCE_COMPLETION_CONDITION),
                new OnEntryAction(ON_ENTRY_ACTION),
                new OnExitAction(ON_EXIT_ACTION),
                new MITrigger("true"),
                new IsAsync(IS_ASYNC));

        final MultipleInstanceSubprocessTaskExecutionSet E_EXECUTION_SET = new MultipleInstanceSubprocessTaskExecutionSet(
                new MultipleInstanceCollectionInput(MULTIPLE_INSTANCE_COLLECTION_INPUT),
                new MultipleInstanceCollectionOutput(MULTIPLE_INSTANCE_COLLECTION_OUTPUT),
                new MultipleInstanceDataInput(OTHER_VALUE),
                new MultipleInstanceDataOutput(MULTIPLE_INSTANCE_DATA_OUTPUT),
                new MultipleInstanceCompletionCondition(MULTIPLE_INSTANCE_COMPLETION_CONDITION),
                new OnEntryAction(ON_ENTRY_ACTION),
                new OnExitAction(ON_EXIT_ACTION),
                new MITrigger("true"),
                new IsAsync(IS_ASYNC));

        final MultipleInstanceSubprocessTaskExecutionSet F_EXECUTION_SET = new MultipleInstanceSubprocessTaskExecutionSet(
                new MultipleInstanceCollectionInput(MULTIPLE_INSTANCE_COLLECTION_INPUT),
                new MultipleInstanceCollectionOutput(MULTIPLE_INSTANCE_COLLECTION_OUTPUT),
                new MultipleInstanceDataInput(MULTIPLE_INSTANCE_DATA_INPUT),
                new MultipleInstanceDataOutput(OTHER_VALUE),
                new MultipleInstanceCompletionCondition(MULTIPLE_INSTANCE_COMPLETION_CONDITION),
                new OnEntryAction(ON_ENTRY_ACTION),
                new OnExitAction(ON_EXIT_ACTION),
                new MITrigger("true"),
                new IsAsync(IS_ASYNC));

        final MultipleInstanceSubprocessTaskExecutionSet G_EXECUTION_SET = new MultipleInstanceSubprocessTaskExecutionSet(
                new MultipleInstanceCollectionInput(MULTIPLE_INSTANCE_COLLECTION_INPUT),
                new MultipleInstanceCollectionOutput(MULTIPLE_INSTANCE_COLLECTION_OUTPUT),
                new MultipleInstanceDataInput(MULTIPLE_INSTANCE_DATA_INPUT),
                new MultipleInstanceDataOutput(MULTIPLE_INSTANCE_DATA_OUTPUT),
                new MultipleInstanceCompletionCondition(OTHER_VALUE),
                new OnEntryAction(ON_ENTRY_ACTION),
                new OnExitAction(ON_EXIT_ACTION),
                new MITrigger("true"),
                new IsAsync(IS_ASYNC));

        final MultipleInstanceSubprocessTaskExecutionSet H_EXECUTION_SET
                = new MultipleInstanceSubprocessTaskExecutionSet
                (new MultipleInstanceCollectionInput(MULTIPLE_INSTANCE_COLLECTION_INPUT),
                 new MultipleInstanceCollectionOutput(MULTIPLE_INSTANCE_COLLECTION_OUTPUT),
                 new MultipleInstanceDataInput(MULTIPLE_INSTANCE_DATA_INPUT),
                 new MultipleInstanceDataOutput(MULTIPLE_INSTANCE_DATA_OUTPUT),
                 new MultipleInstanceCompletionCondition(MULTIPLE_INSTANCE_COMPLETION_CONDITION),
                 new OnEntryAction(new ScriptTypeListValue().addValue(new ScriptTypeValue("other language",
                                                                                          ""))),
                 new OnExitAction(ON_EXIT_ACTION),
                 new MITrigger("true"),
                 new IsAsync(IS_ASYNC));

        final MultipleInstanceSubprocessTaskExecutionSet I_EXECUTION_SET
                = new MultipleInstanceSubprocessTaskExecutionSet
                (new MultipleInstanceCollectionInput(MULTIPLE_INSTANCE_COLLECTION_INPUT),
                 new MultipleInstanceCollectionOutput(MULTIPLE_INSTANCE_COLLECTION_OUTPUT),
                 new MultipleInstanceDataInput(MULTIPLE_INSTANCE_DATA_INPUT),
                 new MultipleInstanceDataOutput(MULTIPLE_INSTANCE_DATA_OUTPUT),
                 new MultipleInstanceCompletionCondition(MULTIPLE_INSTANCE_COMPLETION_CONDITION),
                 new OnEntryAction(ON_ENTRY_ACTION),
                 new OnExitAction(new ScriptTypeListValue().addValue(new ScriptTypeValue("other language",
                                                                                         ""))),
                 new MITrigger("true"),
                 new IsAsync(IS_ASYNC));

        final MultipleInstanceSubprocessTaskExecutionSet J_EXECUTION_SET
                = new MultipleInstanceSubprocessTaskExecutionSet
                (new MultipleInstanceCollectionInput(MULTIPLE_INSTANCE_COLLECTION_INPUT),
                 new MultipleInstanceCollectionOutput(MULTIPLE_INSTANCE_COLLECTION_OUTPUT),
                 new MultipleInstanceDataInput(MULTIPLE_INSTANCE_DATA_INPUT),
                 new MultipleInstanceDataOutput(MULTIPLE_INSTANCE_DATA_OUTPUT),
                 new MultipleInstanceCompletionCondition(MULTIPLE_INSTANCE_COMPLETION_CONDITION),
                 new OnEntryAction(ON_ENTRY_ACTION),
                 new OnExitAction(ON_EXIT_ACTION),
                 new MITrigger("true"),
                 new IsAsync(false));

        final String PROCESS_DATA = "some value";
        final ProcessData A_PROCESS_DATA = new ProcessData(new ProcessVariables(PROCESS_DATA));
        final ProcessData B_PROCESS_DATA = new ProcessData(new ProcessVariables("Other value"));

        MultipleInstanceSubprocess a = new MultipleInstanceSubprocess();
        a.setExecutionSet(A_EXECUTION_SET);
        a.setProcessData(A_PROCESS_DATA);

        MultipleInstanceSubprocess b = new MultipleInstanceSubprocess();
        b.setExecutionSet(B_EXECUTION_SET);
        b.setProcessData(A_PROCESS_DATA);

        MultipleInstanceSubprocess c = new MultipleInstanceSubprocess();
        c.setExecutionSet(C_EXECUTION_SET);

        MultipleInstanceSubprocess d = new MultipleInstanceSubprocess();
        d.setExecutionSet(D_EXECUTION_SET);

        MultipleInstanceSubprocess e = new MultipleInstanceSubprocess();
        e.setExecutionSet(E_EXECUTION_SET);

        MultipleInstanceSubprocess f = new MultipleInstanceSubprocess();
        f.setExecutionSet(F_EXECUTION_SET);

        MultipleInstanceSubprocess g = new MultipleInstanceSubprocess();
        g.setExecutionSet(G_EXECUTION_SET);

        MultipleInstanceSubprocess h = new MultipleInstanceSubprocess();
        h.setExecutionSet(H_EXECUTION_SET);

        MultipleInstanceSubprocess i = new MultipleInstanceSubprocess();
        i.setExecutionSet(I_EXECUTION_SET);

        MultipleInstanceSubprocess j = new MultipleInstanceSubprocess();
        j.setExecutionSet(J_EXECUTION_SET);

        assertEquals(a,
                     a);
        assertEquals(a,
                     b);

        assertNotEquals(a,
                        c);
        assertNotEquals(a,
                        d);
        assertNotEquals(a,
                        e);
        assertNotEquals(a,
                        f);
        assertNotEquals(a,
                        g);
        assertNotEquals(a,
                        h);
        assertNotEquals(a,
                        i);
        assertNotEquals(a,
                        j);

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

        a.setProcessData(null);
        assertNotEquals(a,
                        b);
        assertNotEquals(b,
                        a);

        a.setExecutionSet(A_EXECUTION_SET);
        a.setProcessData(A_PROCESS_DATA);
        assertEquals(a,
                     b);

        a.setProcessData(B_PROCESS_DATA);

        assertNotEquals(a,
                        b);
        assertNotEquals(b,
                        a);

        MultipleInstanceSubprocess k = new MultipleInstanceSubprocess();
        assertNotEquals(a,
                        k);
    }

    @Test
    public void testMultipleInstanceSubprocessHashCode() {
        MultipleInstanceSubprocess a = new MultipleInstanceSubprocess();
        MultipleInstanceSubprocess b = new MultipleInstanceSubprocess();
        assertEquals(a.hashCode(),
                     b.hashCode());
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

    @Test
    public void testIsInterruptingEqualsAndHashCode() {
        TestCaseBuilder.newTestCase()
                .addTrueCase(new IsInterrupting(),
                             new IsInterrupting())
                .addTrueCase(new IsInterrupting(true),
                             new IsInterrupting(true))
                .addTrueCase(new IsInterrupting(false),
                             new IsInterrupting(false))
                .addFalseCase(new IsInterrupting(true),
                              new IsInterrupting(false))
                .addFalseCase(new IsInterrupting(false),
                              new IsInterrupting(true))
                .test();
    }

    @Test
    public void testBaseStartEventEqualsAndHashCode() {
        TestCaseBuilder.newTestCase()
                .addTrueCase(new BaseStartEventStub(),
                             new BaseStartEventStub())
                .addTrueCase(new BaseStartEventStub(new BPMNGeneralSet(),
                                                    new BackgroundSet(),
                                                    new FontSet(),
                                                    new CircleDimensionSet(),
                                                    new SimulationAttributeSet()),
                             new BaseStartEventStub(new BPMNGeneralSet(),
                                                    new BackgroundSet(),
                                                    new FontSet(),
                                                    new CircleDimensionSet(),
                                                    new SimulationAttributeSet()))
                .addFalseCase(new BaseStartEventStub(),
                              new BaseStartEventStub(new BPMNGeneralSet(),
                                                     new BackgroundSet(),
                                                     new FontSet(),
                                                     new CircleDimensionSet(),
                                                     new SimulationAttributeSet()))

                .addFalseCase(new BaseStartEventStub(new BPMNGeneralSet(),
                                                     new BackgroundSet(),
                                                     new FontSet(),
                                                     new CircleDimensionSet(),
                                                     new SimulationAttributeSet()),
                              new BaseStartEventStub())
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

    private class BaseStartEventStub extends BaseStartEvent {

        public BaseStartEventStub() {
            super();
        }

        public BaseStartEventStub(BPMNGeneralSet general,
                                  BackgroundSet backgroundSet,
                                  FontSet fontSet,
                                  CircleDimensionSet dimensionsSet,
                                  SimulationAttributeSet simulationSet) {
            super(general,
                  backgroundSet,
                  fontSet,
                  dimensionsSet,
                  simulationSet);
        }
    }
}
