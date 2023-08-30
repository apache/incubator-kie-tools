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


package org.kie.workbench.common.stunner.bpmn.definition;

import org.junit.Test;
import org.kie.workbench.common.stunner.bpmn.definition.property.assignee.Actors;
import org.kie.workbench.common.stunner.bpmn.definition.property.assignee.Groupid;
import org.kie.workbench.common.stunner.bpmn.definition.property.background.BackgroundSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.background.BgColor;
import org.kie.workbench.common.stunner.bpmn.definition.property.background.BorderColor;
import org.kie.workbench.common.stunner.bpmn.definition.property.background.BorderSize;
import org.kie.workbench.common.stunner.bpmn.definition.property.common.ConditionExpression;
import org.kie.workbench.common.stunner.bpmn.definition.property.connectors.Priority;
import org.kie.workbench.common.stunner.bpmn.definition.property.connectors.SequenceFlowExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.dataio.AssignmentsInfo;
import org.kie.workbench.common.stunner.bpmn.definition.property.dataio.DataIOSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.dimensions.CircleDimensionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.dimensions.Radius;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.BaseCancellingEventExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.BaseStartEventExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.CancelActivity;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.IsInterrupting;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.compensation.ActivityRef;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.compensation.CompensationEventExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.conditional.CancellingConditionalEventExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.conditional.InterruptingConditionalEventExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.error.CancellingErrorEventExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.error.ErrorRef;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.escalation.CancellingEscalationEventExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.escalation.EscalationEventExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.escalation.EscalationRef;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.escalation.InterruptingEscalationEventExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.link.LinkEventExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.link.LinkRef;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.message.CancellingMessageEventExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.message.InterruptingMessageEventExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.message.MessageEventExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.message.MessageRef;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.signal.CancellingSignalEventExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.signal.ScopedSignalEventExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.signal.SignalRef;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.signal.SignalScope;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.timer.CancellingTimerEventExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.timer.TimerSettings;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.timer.TimerSettingsValue;
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
import org.kie.workbench.common.stunner.bpmn.definition.property.general.SLADueDate;
import org.kie.workbench.common.stunner.bpmn.definition.property.notification.NotificationsInfo;
import org.kie.workbench.common.stunner.bpmn.definition.property.reassignment.ReassignmentsInfo;
import org.kie.workbench.common.stunner.bpmn.definition.property.simulation.SimulationAttributeSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.subProcess.IsCase;
import org.kie.workbench.common.stunner.bpmn.definition.property.subProcess.execution.EmbeddedSubprocessExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.subProcess.execution.EventSubprocessExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.AbortParent;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.AdHocActivationCondition;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.AdHocAutostart;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.AdHocCompletionCondition;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.AdHocOrdering;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.AdHocSubprocessTaskExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.BusinessRuleTaskExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.CalledElement;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.Content;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.CreatedBy;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.DecisionName;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.Description;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.DmnModelName;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.FileName;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.Independent;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.IsAsync;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.IsMultipleInstance;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.MultipleInstanceCollectionInput;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.MultipleInstanceCollectionOutput;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.MultipleInstanceCompletionCondition;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.MultipleInstanceDataInput;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.MultipleInstanceDataOutput;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.MultipleInstanceExecutionMode;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.MultipleInstanceSubprocessTaskExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.Namespace;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.OnEntryAction;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.OnExitAction;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.ReusableSubprocessTaskExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.RuleFlowGroup;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.RuleLanguage;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.Script;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.ScriptTaskExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.ScriptTypeListValue;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.ScriptTypeValue;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.Skippable;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.Subject;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.TaskName;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.TaskPriority;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.UserTaskExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.WaitForCompletion;
import org.kie.workbench.common.stunner.bpmn.definition.property.variables.AdvancedData;
import org.kie.workbench.common.stunner.bpmn.definition.property.variables.ProcessData;
import org.kie.workbench.common.stunner.bpmn.definition.property.variables.ProcessVariables;
import org.kie.workbench.common.stunner.bpmn.workitem.CustomTask;
import org.kie.workbench.common.stunner.bpmn.workitem.CustomTaskExecutionSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.kie.workbench.common.stunner.bpmn.util.EqualsAndHashCodeTestUtils.TestCaseBuilder;

public class HashCodeAndEqualityTest {

    @Test
    public void testAdHocSubprocessEquals() {
        AdHocSubprocess a = new AdHocSubprocess();
        AdHocSubprocess b = new AdHocSubprocess();
        assertEquals(a, b);
        assertEquals(new AdHocSubprocess(), new AdHocSubprocess());
        assertFalse(a.equals(19));
        assertFalse(a.equals(null));
    }

    @Test
    public void testAdHocSubprocessHashCode() {
        AdHocSubprocess a = new AdHocSubprocess();
        AdHocSubprocess b = new AdHocSubprocess();
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
    public void testServiceTaskHashCode() {
        CustomTask a = new CustomTask();
        CustomTask b = new CustomTask();
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    public void testServiceTaskEquals() {
        CustomTask a = new CustomTask();
        CustomTask b = new CustomTask();
        assertEquals(a, b);
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
    public void testIntermediateLinkCatchEventEquals() {
        IntermediateLinkEventCatching linkEvent = new IntermediateLinkEventCatching();
        IntermediateLinkEventCatching linkEvent2 = new IntermediateLinkEventCatching();
        assertNotEquals(linkEvent, null);
        assertNotEquals(linkEvent, "");
        assertEquals(linkEvent, linkEvent);
        assertEquals(linkEvent, linkEvent2);

        BackgroundSet backgroundSet = new BackgroundSet();
        backgroundSet.setBgColor(new BgColor("black"));
        linkEvent.setBackgroundSet(backgroundSet);
        assertNotEquals(linkEvent, linkEvent2);

        linkEvent2.setBackgroundSet(backgroundSet);
        assertEquals(linkEvent, linkEvent2);

        linkEvent.setBackgroundSet(backgroundSet);
        linkEvent.setExecutionSet(new LinkEventExecutionSet(new LinkRef("value")));
        assertNotEquals(linkEvent, linkEvent2);
    }

    @Test
    public void testIntermediateLinkThrowingEventEquals() {
        IntermediateLinkEventThrowing linkEvent = new IntermediateLinkEventThrowing();
        IntermediateLinkEventThrowing linkEvent2 = new IntermediateLinkEventThrowing();
        assertNotEquals(linkEvent, null);
        assertNotEquals(linkEvent, "");
        assertEquals(linkEvent, linkEvent);
        assertEquals(linkEvent, linkEvent2);

        BackgroundSet backgroundSet = new BackgroundSet();
        backgroundSet.setBgColor(new BgColor("black"));
        linkEvent.setBackgroundSet(backgroundSet);
        assertNotEquals(linkEvent, linkEvent2);

        linkEvent2.setBackgroundSet(backgroundSet);
        assertEquals(linkEvent, linkEvent2);

        linkEvent.setBackgroundSet(backgroundSet);
        linkEvent.setExecutionSet(new LinkEventExecutionSet(new LinkRef("value")));
        assertNotEquals(linkEvent, linkEvent2);
    }

    @Test
    public void testLinkRef() {
        LinkRef link = new LinkRef("link");
        assertNotEquals(link, null);
        assertNotEquals(link.getValue(), "");
        assertEquals(link, link);

        LinkRef link2 = new LinkRef("link");
        assertEquals(link, link2);

        link2.setValue("link2");
        assertNotEquals(link, link2);
    }

    @Test
    public void testLinkEventExecutionSet() {
        LinkRef link = new LinkRef("link");
        LinkEventExecutionSet executionSet = new LinkEventExecutionSet(link);
        assertNotEquals(executionSet, null);
        assertNotEquals(executionSet.getLinkRef().getValue(), "");
        assertEquals(executionSet, executionSet);

        LinkRef link2 = new LinkRef("link");
        LinkEventExecutionSet executionSet2 = new LinkEventExecutionSet(link2);
        assertEquals(executionSet, executionSet2);

        link2.setValue("link2");
        assertNotEquals(executionSet, executionSet2);
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
        final MessageEventExecutionSet A_EXECUTION_SET = new MessageEventExecutionSet(new MessageRef(MESSAGE_REF, ""));
        final MessageEventExecutionSet B_EXECUTION_SET = new MessageEventExecutionSet(new MessageRef(MESSAGE_REF, ""));
        final MessageEventExecutionSet C_EXECUTION_SET = new MessageEventExecutionSet(new MessageRef("Other value", ""));
        final MessageEventExecutionSet D_EXECUTION_SET = new MessageEventExecutionSet(new MessageRef(MESSAGE_REF, ""));

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
        final MessageEventExecutionSet A_EXECUTION_SET = new MessageEventExecutionSet(new MessageRef(MESSAGE_REF, ""));
        final MessageEventExecutionSet B_EXECUTION_SET = new MessageEventExecutionSet(new MessageRef(MESSAGE_REF, ""));
        final MessageEventExecutionSet C_EXECUTION_SET = new MessageEventExecutionSet(new MessageRef("Other value", ""));
        final MessageEventExecutionSet D_EXECUTION_SET = new MessageEventExecutionSet(new MessageRef(MESSAGE_REF, ""));

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
                                                                                                          new SLADueDate(),
                                                                                                          new MessageRef(MESSAGE_REF, ""));
        final CancellingMessageEventExecutionSet B_EXECUTION_SET = new CancellingMessageEventExecutionSet(new CancelActivity(true),
                                                                                                          new SLADueDate(),
                                                                                                          new MessageRef(MESSAGE_REF, ""));
        final CancellingMessageEventExecutionSet C_EXECUTION_SET = new CancellingMessageEventExecutionSet(new CancelActivity(true),
                                                                                                          new SLADueDate(),
                                                                                                          new MessageRef("Other value", ""));
        final CancellingMessageEventExecutionSet D_EXECUTION_SET = new CancellingMessageEventExecutionSet(new CancelActivity(true),
                                                                                                          new SLADueDate(),
                                                                                                          new MessageRef(MESSAGE_REF, ""));

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
    public void testEventSubprocessTaskExecutionSetEqualsAndHashCode() {
        TestCaseBuilder.newTestCase()
                .addTrueCase(new EventSubprocessExecutionSet(),
                             new EventSubprocessExecutionSet())
                .addTrueCase(new EventSubprocessExecutionSet(new IsAsync(),
                                                             new SLADueDate()),
                             new EventSubprocessExecutionSet(new IsAsync(),
                                                             new SLADueDate()))
                .test();
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
    public void testGenericServiceTaskEquals() {
        GenericServiceTask a = new GenericServiceTask();
        GenericServiceTask b = new GenericServiceTask();
        assertEquals(a,
                     b);
        assertFalse(a.equals(19));
        assertFalse(a.equals(null));
    }

    @Test
    public void testGenericServiceTaskHashCode() {
        GenericServiceTask a = new GenericServiceTask();
        GenericServiceTask b = new GenericServiceTask();
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
                                                new AdvancedData(),
                                                new BaseStartEventExecutionSet()),
                             new StartNoneEvent(new BPMNGeneralSet(),
                                                new BackgroundSet(),
                                                new FontSet(),
                                                new CircleDimensionSet(),
                                                new SimulationAttributeSet(),
                                                new AdvancedData(),
                                                new BaseStartEventExecutionSet()))
                .addTrueCase(new StartNoneEvent(),
                             new StartNoneEvent(new BPMNGeneralSet(),
                                                new BackgroundSet(),
                                                new FontSet(),
                                                new CircleDimensionSet(),
                                                new SimulationAttributeSet(),
                                                new AdvancedData(),
                                                new BaseStartEventExecutionSet()))
                .addTrueCase(new StartNoneEvent(new BPMNGeneralSet(),
                                                new BackgroundSet(),
                                                new FontSet(),
                                                new CircleDimensionSet(),
                                                new SimulationAttributeSet(),
                                                new AdvancedData(),
                                                new BaseStartEventExecutionSet()),
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
                                                                                                              new SLADueDate(),
                                                                                                              new MessageRef(MESSAGE_REF, ""));
        final InterruptingMessageEventExecutionSet B_EXECUTION_SET = new InterruptingMessageEventExecutionSet(new IsInterrupting(true),
                                                                                                              new SLADueDate(),
                                                                                                              new MessageRef(MESSAGE_REF, ""));
        final InterruptingMessageEventExecutionSet C_EXECUTION_SET = new InterruptingMessageEventExecutionSet(new IsInterrupting(true),
                                                                                                              new SLADueDate(),
                                                                                                              new MessageRef("Other value", ""));
        final InterruptingMessageEventExecutionSet D_EXECUTION_SET = new InterruptingMessageEventExecutionSet(new IsInterrupting(true),
                                                                                                              new SLADueDate(),
                                                                                                              new MessageRef(MESSAGE_REF, ""));

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
    public void testEmbeddedSubprocessTaskExecutionSetEqualsAndHashCode() {
        TestCaseBuilder.newTestCase()
                .addTrueCase(new EmbeddedSubprocessExecutionSet(),
                             new EmbeddedSubprocessExecutionSet())
                .addTrueCase(new EmbeddedSubprocessExecutionSet(new OnEntryAction(),
                                                                new OnExitAction(),
                                                                new IsAsync(),
                                                                new SLADueDate()),
                             new EmbeddedSubprocessExecutionSet(new OnEntryAction(),
                                                                new OnExitAction(),
                                                                new IsAsync(),
                                                                new SLADueDate()))
                .test();
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
                (new MultipleInstanceExecutionMode(),
                 new MultipleInstanceCollectionInput(MULTIPLE_INSTANCE_COLLECTION_INPUT),
                 new MultipleInstanceCollectionOutput(MULTIPLE_INSTANCE_COLLECTION_OUTPUT),
                 new MultipleInstanceDataInput(MULTIPLE_INSTANCE_DATA_INPUT),
                 new MultipleInstanceDataOutput(MULTIPLE_INSTANCE_DATA_OUTPUT),
                 new MultipleInstanceCompletionCondition(MULTIPLE_INSTANCE_COMPLETION_CONDITION),
                 new OnEntryAction(ON_ENTRY_ACTION),
                 new OnExitAction(ON_EXIT_ACTION),
                 new IsMultipleInstance(true),
                 new IsAsync(IS_ASYNC),
                 new SLADueDate());

        final MultipleInstanceSubprocessTaskExecutionSet B_EXECUTION_SET = new MultipleInstanceSubprocessTaskExecutionSet(
                new MultipleInstanceExecutionMode(),
                new MultipleInstanceCollectionInput(MULTIPLE_INSTANCE_COLLECTION_INPUT),
                new MultipleInstanceCollectionOutput(MULTIPLE_INSTANCE_COLLECTION_OUTPUT),
                new MultipleInstanceDataInput(MULTIPLE_INSTANCE_DATA_INPUT),
                new MultipleInstanceDataOutput(MULTIPLE_INSTANCE_DATA_OUTPUT),
                new MultipleInstanceCompletionCondition(MULTIPLE_INSTANCE_COMPLETION_CONDITION),
                new OnEntryAction(ON_ENTRY_ACTION),
                new OnExitAction(ON_EXIT_ACTION),
                new IsMultipleInstance(true),
                new IsAsync(IS_ASYNC),
                new SLADueDate());

        final MultipleInstanceSubprocessTaskExecutionSet C_EXECUTION_SET = new MultipleInstanceSubprocessTaskExecutionSet(
                new MultipleInstanceExecutionMode(),
                new MultipleInstanceCollectionInput(OTHER_VALUE),
                new MultipleInstanceCollectionOutput(MULTIPLE_INSTANCE_COLLECTION_OUTPUT),
                new MultipleInstanceDataInput(MULTIPLE_INSTANCE_DATA_INPUT),
                new MultipleInstanceDataOutput(MULTIPLE_INSTANCE_DATA_OUTPUT),
                new MultipleInstanceCompletionCondition(MULTIPLE_INSTANCE_COMPLETION_CONDITION),
                new OnEntryAction(ON_ENTRY_ACTION),
                new OnExitAction(ON_EXIT_ACTION),
                new IsMultipleInstance(true),
                new IsAsync(IS_ASYNC),
                new SLADueDate());

        final MultipleInstanceSubprocessTaskExecutionSet D_EXECUTION_SET = new MultipleInstanceSubprocessTaskExecutionSet(
                new MultipleInstanceExecutionMode(),
                new MultipleInstanceCollectionInput(MULTIPLE_INSTANCE_COLLECTION_INPUT),
                new MultipleInstanceCollectionOutput(OTHER_VALUE),
                new MultipleInstanceDataInput(MULTIPLE_INSTANCE_DATA_INPUT),
                new MultipleInstanceDataOutput(MULTIPLE_INSTANCE_DATA_OUTPUT),
                new MultipleInstanceCompletionCondition(MULTIPLE_INSTANCE_COMPLETION_CONDITION),
                new OnEntryAction(ON_ENTRY_ACTION),
                new OnExitAction(ON_EXIT_ACTION),
                new IsMultipleInstance(true),
                new IsAsync(IS_ASYNC),
                new SLADueDate());

        final MultipleInstanceSubprocessTaskExecutionSet E_EXECUTION_SET = new MultipleInstanceSubprocessTaskExecutionSet(
                new MultipleInstanceExecutionMode(),
                new MultipleInstanceCollectionInput(MULTIPLE_INSTANCE_COLLECTION_INPUT),
                new MultipleInstanceCollectionOutput(MULTIPLE_INSTANCE_COLLECTION_OUTPUT),
                new MultipleInstanceDataInput(OTHER_VALUE),
                new MultipleInstanceDataOutput(MULTIPLE_INSTANCE_DATA_OUTPUT),
                new MultipleInstanceCompletionCondition(MULTIPLE_INSTANCE_COMPLETION_CONDITION),
                new OnEntryAction(ON_ENTRY_ACTION),
                new OnExitAction(ON_EXIT_ACTION),
                new IsMultipleInstance(true),
                new IsAsync(IS_ASYNC),
                new SLADueDate());

        final MultipleInstanceSubprocessTaskExecutionSet F_EXECUTION_SET = new MultipleInstanceSubprocessTaskExecutionSet(
                new MultipleInstanceExecutionMode(),
                new MultipleInstanceCollectionInput(MULTIPLE_INSTANCE_COLLECTION_INPUT),
                new MultipleInstanceCollectionOutput(MULTIPLE_INSTANCE_COLLECTION_OUTPUT),
                new MultipleInstanceDataInput(MULTIPLE_INSTANCE_DATA_INPUT),
                new MultipleInstanceDataOutput(OTHER_VALUE),
                new MultipleInstanceCompletionCondition(MULTIPLE_INSTANCE_COMPLETION_CONDITION),
                new OnEntryAction(ON_ENTRY_ACTION),
                new OnExitAction(ON_EXIT_ACTION),
                new IsMultipleInstance(true),
                new IsAsync(IS_ASYNC),
                new SLADueDate());

        final MultipleInstanceSubprocessTaskExecutionSet G_EXECUTION_SET = new MultipleInstanceSubprocessTaskExecutionSet(
                new MultipleInstanceExecutionMode(),
                new MultipleInstanceCollectionInput(MULTIPLE_INSTANCE_COLLECTION_INPUT),
                new MultipleInstanceCollectionOutput(MULTIPLE_INSTANCE_COLLECTION_OUTPUT),
                new MultipleInstanceDataInput(MULTIPLE_INSTANCE_DATA_INPUT),
                new MultipleInstanceDataOutput(MULTIPLE_INSTANCE_DATA_OUTPUT),
                new MultipleInstanceCompletionCondition(OTHER_VALUE),
                new OnEntryAction(ON_ENTRY_ACTION),
                new OnExitAction(ON_EXIT_ACTION),
                new IsMultipleInstance(true),
                new IsAsync(IS_ASYNC),
                new SLADueDate());

        final MultipleInstanceSubprocessTaskExecutionSet H_EXECUTION_SET
                = new MultipleInstanceSubprocessTaskExecutionSet
                (new MultipleInstanceExecutionMode(),
                 new MultipleInstanceCollectionInput(MULTIPLE_INSTANCE_COLLECTION_INPUT),
                 new MultipleInstanceCollectionOutput(MULTIPLE_INSTANCE_COLLECTION_OUTPUT),
                 new MultipleInstanceDataInput(MULTIPLE_INSTANCE_DATA_INPUT),
                 new MultipleInstanceDataOutput(MULTIPLE_INSTANCE_DATA_OUTPUT),
                 new MultipleInstanceCompletionCondition(MULTIPLE_INSTANCE_COMPLETION_CONDITION),
                 new OnEntryAction(new ScriptTypeListValue().addValue(new ScriptTypeValue("other language",
                                                                                          ""))),
                 new OnExitAction(ON_EXIT_ACTION),
                 new IsMultipleInstance(true),
                 new IsAsync(IS_ASYNC),
                 new SLADueDate());

        final MultipleInstanceSubprocessTaskExecutionSet I_EXECUTION_SET
                = new MultipleInstanceSubprocessTaskExecutionSet
                (new MultipleInstanceExecutionMode(),
                 new MultipleInstanceCollectionInput(MULTIPLE_INSTANCE_COLLECTION_INPUT),
                 new MultipleInstanceCollectionOutput(MULTIPLE_INSTANCE_COLLECTION_OUTPUT),
                 new MultipleInstanceDataInput(MULTIPLE_INSTANCE_DATA_INPUT),
                 new MultipleInstanceDataOutput(MULTIPLE_INSTANCE_DATA_OUTPUT),
                 new MultipleInstanceCompletionCondition(MULTIPLE_INSTANCE_COMPLETION_CONDITION),
                 new OnEntryAction(ON_ENTRY_ACTION),
                 new OnExitAction(new ScriptTypeListValue().addValue(new ScriptTypeValue("other language",
                                                                                         ""))),
                 new IsMultipleInstance(true),
                 new IsAsync(IS_ASYNC),
                 new SLADueDate());

        final MultipleInstanceSubprocessTaskExecutionSet J_EXECUTION_SET
                = new MultipleInstanceSubprocessTaskExecutionSet
                (new MultipleInstanceExecutionMode(),
                 new MultipleInstanceCollectionInput(MULTIPLE_INSTANCE_COLLECTION_INPUT),
                 new MultipleInstanceCollectionOutput(MULTIPLE_INSTANCE_COLLECTION_OUTPUT),
                 new MultipleInstanceDataInput(MULTIPLE_INSTANCE_DATA_INPUT),
                 new MultipleInstanceDataOutput(MULTIPLE_INSTANCE_DATA_OUTPUT),
                 new MultipleInstanceCompletionCondition(MULTIPLE_INSTANCE_COMPLETION_CONDITION),
                 new OnEntryAction(ON_ENTRY_ACTION),
                 new OnExitAction(ON_EXIT_ACTION),
                 new IsMultipleInstance(true),
                 new IsAsync(false),
                 new SLADueDate());

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
    public void testAdHocSubprocessTaskExecutionSetEqualsAndHashCode() {
        TestCaseBuilder.newTestCase()
                .addTrueCase(new AdHocSubprocessTaskExecutionSet(),
                             new AdHocSubprocessTaskExecutionSet())

                .addTrueCase(new AdHocSubprocessTaskExecutionSet(new AdHocActivationCondition(),
                                                                 new AdHocCompletionCondition(),
                                                                 new AdHocOrdering(),
                                                                 new AdHocAutostart(),
                                                                 new OnEntryAction(),
                                                                 new OnExitAction(),
                                                                 new IsAsync(),
                                                                 new SLADueDate()),
                             new AdHocSubprocessTaskExecutionSet(new AdHocActivationCondition(),
                                                                 new AdHocCompletionCondition(),
                                                                 new AdHocOrdering(),
                                                                 new AdHocAutostart(),
                                                                 new OnEntryAction(),
                                                                 new OnExitAction(),
                                                                 new IsAsync(),
                                                                 new SLADueDate()))
                .test();
    }

    @Test
    public void testReusableSubprocessTaskExecutionSetEqualsAndHashCode() {
        TestCaseBuilder.newTestCase()
                .addTrueCase(new ReusableSubprocessTaskExecutionSet(),
                             new ReusableSubprocessTaskExecutionSet())

                .addTrueCase(new ReusableSubprocessTaskExecutionSet(new CalledElement(),
                                                                    new IsCase(),
                                                                    new Independent(),
                                                                    new AbortParent(),
                                                                    new WaitForCompletion(),
                                                                    new IsAsync(),
                                                                    new AdHocAutostart(),
                                                                    new IsMultipleInstance(),
                                                                    new MultipleInstanceExecutionMode(),
                                                                    new MultipleInstanceCollectionInput(),
                                                                    new MultipleInstanceDataInput(),
                                                                    new MultipleInstanceCollectionOutput(),
                                                                    new MultipleInstanceDataOutput(),
                                                                    new MultipleInstanceCompletionCondition(),
                                                                    new OnEntryAction(),
                                                                    new OnExitAction(),
                                                                    new SLADueDate()),
                             new ReusableSubprocessTaskExecutionSet(new CalledElement(),
                                                                    new IsCase(),
                                                                    new Independent(),
                                                                    new AbortParent(),
                                                                    new WaitForCompletion(),
                                                                    new IsAsync(),
                                                                    new AdHocAutostart(),
                                                                    new IsMultipleInstance(),
                                                                    new MultipleInstanceExecutionMode(),
                                                                    new MultipleInstanceCollectionInput(),
                                                                    new MultipleInstanceDataInput(),
                                                                    new MultipleInstanceCollectionOutput(),
                                                                    new MultipleInstanceDataOutput(),
                                                                    new MultipleInstanceCompletionCondition(),
                                                                    new OnEntryAction(),
                                                                    new OnExitAction(),
                                                                    new SLADueDate()))
                .test();
    }

    @Test
    public void testBusinessRuleTaskExecutionSetEqualsAndHashCode() {
        TestCaseBuilder.newTestCase()
                .addTrueCase(new BusinessRuleTaskExecutionSet(),
                             new BusinessRuleTaskExecutionSet())

                .addTrueCase(new BusinessRuleTaskExecutionSet(new RuleLanguage(),
                                                              new RuleFlowGroup(),
                                                              new FileName(),
                                                              new Namespace(),
                                                              new DecisionName(),
                                                              new DmnModelName(),
                                                              new OnEntryAction(),
                                                              new OnExitAction(),
                                                              new IsAsync(),
                                                              new AdHocAutostart(),
                                                              new SLADueDate()),
                             new BusinessRuleTaskExecutionSet(new RuleLanguage(),
                                                              new RuleFlowGroup(),
                                                              new FileName(),
                                                              new Namespace(),
                                                              new DecisionName(),
                                                              new DmnModelName(),
                                                              new OnEntryAction(),
                                                              new OnExitAction(),
                                                              new IsAsync(),
                                                              new AdHocAutostart(),
                                                              new SLADueDate()))
                .test();
    }

    @Test
    public void testServiceTaskExecutionSetEqualsAndHashCode() {
        TestCaseBuilder.newTestCase()
                .addTrueCase(new CustomTaskExecutionSet(),
                             new CustomTaskExecutionSet())

                .addTrueCase(new CustomTaskExecutionSet(new TaskName(),
                                                        new IsAsync(),
                                                        new AdHocAutostart(),
                                                        new OnEntryAction(),
                                                        new OnExitAction(),
                                                        new SLADueDate()),
                             new CustomTaskExecutionSet(new TaskName(),
                                                        new IsAsync(),
                                                        new AdHocAutostart(),
                                                        new OnEntryAction(),
                                                        new OnExitAction(),
                                                        new SLADueDate()))
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
                                                        new IsAsync(),
                                                        new AdHocAutostart()),

                             new ScriptTaskExecutionSet(new Script(),
                                                        new IsAsync(),
                                                        new AdHocAutostart()))
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
                                                      new NotificationsInfo(),
                                                      new ReassignmentsInfo(),
                                                      new IsAsync(),
                                                      new Skippable(),
                                                      new TaskPriority(),
                                                      new Subject(),
                                                      new Description(),
                                                      new CreatedBy(),
                                                      new AdHocAutostart(),
                                                      new IsMultipleInstance(),
                                                      new MultipleInstanceExecutionMode(),
                                                      new MultipleInstanceCollectionInput(),
                                                      new MultipleInstanceDataInput(),
                                                      new MultipleInstanceCollectionOutput(),
                                                      new MultipleInstanceDataOutput(),
                                                      new MultipleInstanceCompletionCondition(),
                                                      new OnEntryAction(),
                                                      new OnExitAction(),
                                                      new Content(),
                                                      new SLADueDate()),

                             new UserTaskExecutionSet(new TaskName(),
                                                      new Actors(),
                                                      new Groupid(),
                                                      new AssignmentsInfo(),
                                                      new NotificationsInfo(),
                                                      new ReassignmentsInfo(),
                                                      new IsAsync(),
                                                      new Skippable(),
                                                      new TaskPriority(),
                                                      new Subject(),
                                                      new Description(),
                                                      new CreatedBy(),
                                                      new AdHocAutostart(),
                                                      new IsMultipleInstance(),
                                                      new MultipleInstanceExecutionMode(),
                                                      new MultipleInstanceCollectionInput(),
                                                      new MultipleInstanceDataInput(),
                                                      new MultipleInstanceCollectionOutput(),
                                                      new MultipleInstanceDataOutput(),
                                                      new MultipleInstanceCompletionCondition(),
                                                      new OnEntryAction(),
                                                      new OnExitAction(),
                                                      new Content(),
                                                      new SLADueDate()))
                .test();
    }

    @Test
    public void testMultipleInstanceSubprocessTaskExecutionSetAndHashCode() {
        TestCaseBuilder.newTestCase()
                .addTrueCase(new MultipleInstanceSubprocessTaskExecutionSet(),
                             new MultipleInstanceSubprocessTaskExecutionSet())
                .addTrueCase(new MultipleInstanceSubprocessTaskExecutionSet(new MultipleInstanceExecutionMode(),
                                                                            new MultipleInstanceCollectionInput(),
                                                                            new MultipleInstanceCollectionOutput(),
                                                                            new MultipleInstanceDataInput(),
                                                                            new MultipleInstanceDataOutput(),
                                                                            new MultipleInstanceCompletionCondition(),
                                                                            new OnEntryAction(),
                                                                            new OnExitAction(),
                                                                            new IsMultipleInstance(),
                                                                            new IsAsync(),
                                                                            new SLADueDate()),
                             new MultipleInstanceSubprocessTaskExecutionSet(new MultipleInstanceExecutionMode(),
                                                                            new MultipleInstanceCollectionInput(),
                                                                            new MultipleInstanceCollectionOutput(),
                                                                            new MultipleInstanceDataInput(),
                                                                            new MultipleInstanceDataOutput(),
                                                                            new MultipleInstanceCompletionCondition(),
                                                                            new OnEntryAction(),
                                                                            new OnExitAction(),
                                                                            new IsMultipleInstance(),
                                                                            new IsAsync(),
                                                                            new SLADueDate()))
                .addFalseCase(new MultipleInstanceSubprocessTaskExecutionSet(new MultipleInstanceExecutionMode(true),
                                                                             new MultipleInstanceCollectionInput(),
                                                                             new MultipleInstanceCollectionOutput(),
                                                                             new MultipleInstanceDataInput(),
                                                                             new MultipleInstanceDataOutput(),
                                                                             new MultipleInstanceCompletionCondition(),
                                                                             new OnEntryAction(),
                                                                             new OnExitAction(),
                                                                             new IsMultipleInstance(),
                                                                             new IsAsync(),
                                                                             new SLADueDate()),
                              new MultipleInstanceSubprocessTaskExecutionSet(new MultipleInstanceExecutionMode(false),
                                                                             new MultipleInstanceCollectionInput(),
                                                                             new MultipleInstanceCollectionOutput(),
                                                                             new MultipleInstanceDataInput(),
                                                                             new MultipleInstanceDataOutput(),
                                                                             new MultipleInstanceCompletionCondition(),
                                                                             new OnEntryAction(),
                                                                             new OnExitAction(),
                                                                             new IsMultipleInstance(),
                                                                             new IsAsync(),
                                                                             new SLADueDate()))
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
                                                  new AdvancedData(),
                                                  new GatewayExecutionSet()),
                             new InclusiveGateway(new BPMNGeneralSet(),
                                                  new BackgroundSet(),
                                                  new FontSet(),
                                                  new CircleDimensionSet(),
                                                  new AdvancedData(),
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
                                                    new SimulationAttributeSet(),
                                                    new AdvancedData()),
                             new BaseStartEventStub(new BPMNGeneralSet(),
                                                    new BackgroundSet(),
                                                    new FontSet(),
                                                    new CircleDimensionSet(),
                                                    new SimulationAttributeSet(),
                                                    new AdvancedData()))
                .addFalseCase(new BaseStartEventStub(),
                              new BaseStartEventStub(new BPMNGeneralSet(),
                                                     new BackgroundSet(),
                                                     new FontSet(),
                                                     new CircleDimensionSet(),
                                                     new SimulationAttributeSet(),
                                                     new AdvancedData()))

                .addFalseCase(new BaseStartEventStub(new BPMNGeneralSet(),
                                                     new BackgroundSet(),
                                                     new FontSet(),
                                                     new CircleDimensionSet(),
                                                     new SimulationAttributeSet(),
                                                     new AdvancedData()),
                              new BaseStartEventStub())
                .test();
    }

    @Test
    public void testBaseCancellingEventExecutionSetEqualsAndHashCode() {
        TestCaseBuilder.newTestCase()
                .addTrueCase(new BaseCancellingEventExecutionSet(),
                             new BaseCancellingEventExecutionSet())

                .addTrueCase(new BaseCancellingEventExecutionSet(new CancelActivity(false), new SLADueDate()),
                             new BaseCancellingEventExecutionSet(new CancelActivity(false), new SLADueDate()))

                .addTrueCase(new BaseCancellingEventExecutionSet(new CancelActivity(false), new SLADueDate("12/25/1983")),
                             new BaseCancellingEventExecutionSet(new CancelActivity(false), new SLADueDate("12/25/1983")))

                .addTrueCase(new BaseCancellingEventExecutionSet(new CancelActivity(true), new SLADueDate()),
                             new BaseCancellingEventExecutionSet(new CancelActivity(true), new SLADueDate()))

                .addTrueCase(new BaseCancellingEventExecutionSet(new CancelActivity(true), new SLADueDate("12/25/1983")),
                             new BaseCancellingEventExecutionSet(new CancelActivity(true), new SLADueDate("12/25/1983")))

                .addFalseCase(new BaseCancellingEventExecutionSet(),
                              null)

                .addFalseCase(new BaseCancellingEventExecutionSet(new CancelActivity(false), new SLADueDate()),
                              new BaseCancellingEventExecutionSet(new CancelActivity(true), new SLADueDate()))

                .addFalseCase(new BaseCancellingEventExecutionSet(new CancelActivity(false), new SLADueDate("12/25/1983")),
                              new BaseCancellingEventExecutionSet(new CancelActivity(true), new SLADueDate("12/25/1983")))

                .addFalseCase(new BaseCancellingEventExecutionSet(new CancelActivity(false), new SLADueDate()),
                              new BaseCancellingEventExecutionSet(new CancelActivity(false), new SLADueDate("12/25/1983")))

                .addFalseCase(new BaseCancellingEventExecutionSet(new CancelActivity(false), new SLADueDate("07/12/2017")),
                              new BaseCancellingEventExecutionSet(new CancelActivity(false), new SLADueDate("12/25/1983")))

                .addFalseCase(new BaseCancellingEventExecutionSet(new CancelActivity(false), new SLADueDate("17/02/2013")),
                              new BaseCancellingEventExecutionSet(new CancelActivity(false), new SLADueDate("12/25/1983")))

                .addFalseCase(new BaseCancellingEventExecutionSet(new CancelActivity(true), new SLADueDate()),
                              new BaseCancellingEventExecutionSet(new CancelActivity(true), new SLADueDate("12/25/1983")))

                .addFalseCase(new BaseCancellingEventExecutionSet(new CancelActivity(true), new SLADueDate("07/12/2017")),
                              new BaseCancellingEventExecutionSet(new CancelActivity(true), new SLADueDate("12/25/1983")))

                .addFalseCase(new BaseCancellingEventExecutionSet(new CancelActivity(true), new SLADueDate("17/02/2013")),
                              new BaseCancellingEventExecutionSet(new CancelActivity(true), new SLADueDate("12/25/1983")))

                .test();
    }

    @Test
    public void testCancellingConditionalEventExecutionSetEqualsAndHashCode() {
        TestCaseBuilder.newTestCase()
                .addTrueCase(new CancellingConditionalEventExecutionSet(),
                             new CancellingConditionalEventExecutionSet())

                .addTrueCase(new CancellingConditionalEventExecutionSet(new CancelActivity(false), new SLADueDate(), new ConditionExpression()),
                             new CancellingConditionalEventExecutionSet(new CancelActivity(false), new SLADueDate(), new ConditionExpression()))

                .addTrueCase(new CancellingConditionalEventExecutionSet(new CancelActivity(false), new SLADueDate(), new ConditionExpression(new ScriptTypeValue("drools", "script"))),
                             new CancellingConditionalEventExecutionSet(new CancelActivity(false), new SLADueDate(), new ConditionExpression(new ScriptTypeValue("drools", "script"))))

                .addFalseCase(new CancellingConditionalEventExecutionSet(),
                              null)

                .addFalseCase(new CancellingConditionalEventExecutionSet(new CancelActivity(false), new SLADueDate(), new ConditionExpression(new ScriptTypeValue("drools", "script"))),
                              new CancellingConditionalEventExecutionSet(new CancelActivity(true), new SLADueDate(), new ConditionExpression(new ScriptTypeValue("drools", "script"))))

                .addFalseCase(new CancellingConditionalEventExecutionSet(new CancelActivity(false), new SLADueDate(), new ConditionExpression(new ScriptTypeValue("drools", "script"))),
                              new CancellingConditionalEventExecutionSet(new CancelActivity(false), new SLADueDate(), new ConditionExpression(new ScriptTypeValue("drools1", "script"))))

                .addFalseCase(new CancellingConditionalEventExecutionSet(new CancelActivity(false), new SLADueDate(), new ConditionExpression(new ScriptTypeValue("drools", "script"))),
                              new CancellingConditionalEventExecutionSet(new CancelActivity(false), new SLADueDate(), new ConditionExpression(new ScriptTypeValue("drools", "script1"))))

                .addFalseCase(new CancellingConditionalEventExecutionSet(new CancelActivity(false), new SLADueDate(), new ConditionExpression(new ScriptTypeValue("drools", "script"))),
                              new CancellingConditionalEventExecutionSet(new CancelActivity(false), new SLADueDate(), new ConditionExpression(new ScriptTypeValue("drools1", "script"))))

                .addFalseCase(new CancellingConditionalEventExecutionSet(new CancelActivity(false), new SLADueDate(), new ConditionExpression(new ScriptTypeValue("drools", "script"))),
                              new CancellingConditionalEventExecutionSet(new CancelActivity(false), new SLADueDate(), null))

                .test();
    }

    @Test
    public void testInterruptingConditionalEventExecutionSetEqualsAndHashCode() {
        TestCaseBuilder.newTestCase()
                .addTrueCase(new InterruptingConditionalEventExecutionSet(),
                             new InterruptingConditionalEventExecutionSet())

                .addTrueCase(new InterruptingConditionalEventExecutionSet(new IsInterrupting(false), new SLADueDate(), new ConditionExpression()),
                             new InterruptingConditionalEventExecutionSet(new IsInterrupting(false), new SLADueDate(), new ConditionExpression()))

                .addTrueCase(new InterruptingConditionalEventExecutionSet(new IsInterrupting(false), new SLADueDate(), new ConditionExpression(new ScriptTypeValue("drools", "script"))),
                             new InterruptingConditionalEventExecutionSet(new IsInterrupting(false), new SLADueDate(), new ConditionExpression(new ScriptTypeValue("drools", "script"))))

                .addFalseCase(new InterruptingConditionalEventExecutionSet(),
                              null)

                .addFalseCase(new InterruptingConditionalEventExecutionSet(new IsInterrupting(false), new SLADueDate(), new ConditionExpression(new ScriptTypeValue("drools", "script"))),
                              new InterruptingConditionalEventExecutionSet(new IsInterrupting(true), new SLADueDate(), new ConditionExpression(new ScriptTypeValue("drools", "script"))))

                .addFalseCase(new InterruptingConditionalEventExecutionSet(new IsInterrupting(false), new SLADueDate(), new ConditionExpression(new ScriptTypeValue("drools", "script"))),
                              new InterruptingConditionalEventExecutionSet(new IsInterrupting(false), new SLADueDate(), new ConditionExpression(new ScriptTypeValue("drools1", "script"))))

                .addFalseCase(new InterruptingConditionalEventExecutionSet(new IsInterrupting(false), new SLADueDate(), new ConditionExpression(new ScriptTypeValue("drools", "script"))),
                              new InterruptingConditionalEventExecutionSet(new IsInterrupting(false), new SLADueDate(), new ConditionExpression(new ScriptTypeValue("drools", "script1"))))

                .addFalseCase(new InterruptingConditionalEventExecutionSet(new IsInterrupting(false), new SLADueDate(), new ConditionExpression(new ScriptTypeValue("drools", "script"))),
                              new InterruptingConditionalEventExecutionSet(new IsInterrupting(false), new SLADueDate(), new ConditionExpression(new ScriptTypeValue("drools1", "script"))))

                .addFalseCase(new InterruptingConditionalEventExecutionSet(new IsInterrupting(false), new SLADueDate(), new ConditionExpression(new ScriptTypeValue("drools", "script"))),
                              new InterruptingConditionalEventExecutionSet(new IsInterrupting(false), null, null))

                .test();
    }

    @Test
    public void testStartConditionalEventEqualsAndHashCode() {
        TestCaseBuilder.newTestCase()
                .addTrueCase(new StartConditionalEvent(),
                             new StartConditionalEvent())

                .addTrueCase(new StartConditionalEvent(new BPMNGeneralSet(),
                                                       new BackgroundSet(),
                                                       new FontSet(),
                                                       new CircleDimensionSet(),
                                                       new SimulationAttributeSet(),
                                                       new AdvancedData(),
                                                       new InterruptingConditionalEventExecutionSet()),
                             new StartConditionalEvent(new BPMNGeneralSet(),
                                                       new BackgroundSet(),
                                                       new FontSet(),
                                                       new CircleDimensionSet(),
                                                       new SimulationAttributeSet(),
                                                       new AdvancedData(),
                                                       new InterruptingConditionalEventExecutionSet()))

                .addFalseCase(new StartConditionalEvent(),
                              null)

                .addFalseCase(new StartConditionalEvent(new BPMNGeneralSet("name", "doc"),
                                                        new BackgroundSet(),
                                                        new FontSet(),
                                                        new CircleDimensionSet(),
                                                        new SimulationAttributeSet(),
                                                        new AdvancedData(),
                                                        new InterruptingConditionalEventExecutionSet()),
                              new StartConditionalEvent(new BPMNGeneralSet("name1", "doc1"),
                                                        new BackgroundSet(),
                                                        new FontSet(),
                                                        new CircleDimensionSet(),
                                                        new SimulationAttributeSet(),
                                                        new AdvancedData(),
                                                        new InterruptingConditionalEventExecutionSet()))

                .addFalseCase(new StartConditionalEvent(new BPMNGeneralSet("name", "doc"),
                                                        new BackgroundSet(),
                                                        new FontSet(),
                                                        new CircleDimensionSet(),
                                                        new SimulationAttributeSet(),
                                                        new AdvancedData(),
                                                        new InterruptingConditionalEventExecutionSet(
                                                                new IsInterrupting(false),
                                                                new SLADueDate(),
                                                                new ConditionExpression(
                                                                        new ScriptTypeValue("drools", "script")))),
                              new StartConditionalEvent(new BPMNGeneralSet("name", "doc"),
                                                        new BackgroundSet(),
                                                        new FontSet(),
                                                        new CircleDimensionSet(),
                                                        new SimulationAttributeSet(),
                                                        new AdvancedData(),
                                                        new InterruptingConditionalEventExecutionSet()))

                .addFalseCase(new StartConditionalEvent(new BPMNGeneralSet("name", "doc"),
                                                        new BackgroundSet(),
                                                        new FontSet(),
                                                        new CircleDimensionSet(),
                                                        new SimulationAttributeSet(),
                                                        new AdvancedData(),
                                                        new InterruptingConditionalEventExecutionSet(
                                                                new IsInterrupting(false),
                                                                new SLADueDate(),
                                                                new ConditionExpression(
                                                                        new ScriptTypeValue("drools", "script")))),
                              new StartConditionalEvent(new BPMNGeneralSet("name", "doc"),
                                                        new BackgroundSet(),
                                                        new FontSet(),
                                                        new CircleDimensionSet(),
                                                        new SimulationAttributeSet(),
                                                        new AdvancedData(),
                                                        new InterruptingConditionalEventExecutionSet(
                                                                new IsInterrupting(true),
                                                                new SLADueDate(),
                                                                new ConditionExpression(
                                                                        new ScriptTypeValue("drools", "script")))))

                .addFalseCase(new StartConditionalEvent(new BPMNGeneralSet("name", "doc"),
                                                        new BackgroundSet(),
                                                        new FontSet(),
                                                        new CircleDimensionSet(),
                                                        new SimulationAttributeSet(),
                                                        new AdvancedData(),
                                                        new InterruptingConditionalEventExecutionSet(
                                                                new IsInterrupting(false),
                                                                new SLADueDate(),
                                                                new ConditionExpression(
                                                                        new ScriptTypeValue("drools", "script")))),
                              new StartConditionalEvent(new BPMNGeneralSet("name", "doc"),
                                                        new BackgroundSet(),
                                                        new FontSet(),
                                                        new CircleDimensionSet(),
                                                        new SimulationAttributeSet(),
                                                        new AdvancedData(),
                                                        new InterruptingConditionalEventExecutionSet(
                                                                new IsInterrupting(false),
                                                                new SLADueDate(),
                                                                new ConditionExpression(
                                                                        new ScriptTypeValue("drools1", "script")))))

                .addFalseCase(new StartConditionalEvent(new BPMNGeneralSet("name", "doc"),
                                                        new BackgroundSet(),
                                                        new FontSet(),
                                                        new CircleDimensionSet(),
                                                        new SimulationAttributeSet(),
                                                        new AdvancedData(),
                                                        new InterruptingConditionalEventExecutionSet(
                                                                new IsInterrupting(false),
                                                                new SLADueDate(), new ConditionExpression(
                                                                        new ScriptTypeValue("drools", "script")))),
                              new StartConditionalEvent(new BPMNGeneralSet("name", "doc"),
                                                        new BackgroundSet(),
                                                        new FontSet(),
                                                        new CircleDimensionSet(),
                                                        new SimulationAttributeSet(),
                                                        new AdvancedData(),
                                                        new InterruptingConditionalEventExecutionSet(
                                                                new IsInterrupting(false),
                                                                new SLADueDate(),
                                                                new ConditionExpression(
                                                                        new ScriptTypeValue("drools", "script1")))))

                .test();
    }

    @Test
    public void testIntermediateConditionalEventEqualsAndHashCode() {

        TestCaseBuilder.newTestCase()
                .addTrueCase(new IntermediateConditionalEvent(),
                             new IntermediateConditionalEvent())

                .addTrueCase(new IntermediateConditionalEvent(new BPMNGeneralSet(),
                                                              new BackgroundSet(),
                                                              new FontSet(),
                                                              new CircleDimensionSet(),
                                                              new DataIOSet(),
                                                              new AdvancedData(),
                                                              new CancellingConditionalEventExecutionSet()),
                             new IntermediateConditionalEvent(new BPMNGeneralSet(),
                                                              new BackgroundSet(),
                                                              new FontSet(),
                                                              new CircleDimensionSet(),
                                                              new DataIOSet(),
                                                              new AdvancedData(),
                                                              new CancellingConditionalEventExecutionSet()))

                .addFalseCase(new IntermediateConditionalEvent(),
                              null)

                .addFalseCase(new IntermediateConditionalEvent(new BPMNGeneralSet("name", "doc"),
                                                               new BackgroundSet(), new FontSet(),
                                                               new CircleDimensionSet(),
                                                               new DataIOSet(),
                                                               new AdvancedData(),
                                                               new CancellingConditionalEventExecutionSet()),
                              new IntermediateConditionalEvent(new BPMNGeneralSet("name1", "doc1"),
                                                               new BackgroundSet(), new FontSet(),
                                                               new CircleDimensionSet(),
                                                               new DataIOSet(),
                                                               new AdvancedData(),
                                                               new CancellingConditionalEventExecutionSet()))

                .addFalseCase(new IntermediateConditionalEvent(new BPMNGeneralSet("name", "doc"),
                                                               new BackgroundSet(), new FontSet(),
                                                               new CircleDimensionSet(),
                                                               new DataIOSet(),
                                                               new AdvancedData(),
                                                               new CancellingConditionalEventExecutionSet(
                                                                       new CancelActivity(false),
                                                                       new SLADueDate(), new ConditionExpression(
                                                                               new ScriptTypeValue("drools", "script")))),
                              new IntermediateConditionalEvent(new BPMNGeneralSet("name", "doc"),
                                                               new BackgroundSet(), new FontSet(),
                                                               new CircleDimensionSet(),
                                                               new DataIOSet(),
                                                               new AdvancedData(),
                                                               new CancellingConditionalEventExecutionSet()))

                .addFalseCase(new IntermediateConditionalEvent(new BPMNGeneralSet("name", "doc"),
                                                               new BackgroundSet(),
                                                               new FontSet(),
                                                               new CircleDimensionSet(),
                                                               new DataIOSet(),
                                                               new AdvancedData(),
                                                               new CancellingConditionalEventExecutionSet(
                                                                       new CancelActivity(false),
                                                                       new SLADueDate(),
                                                                       new ConditionExpression(
                                                                               new ScriptTypeValue("drools", "script")))),
                              new IntermediateConditionalEvent(new BPMNGeneralSet("name", "doc"),
                                                               new BackgroundSet(),
                                                               new FontSet(),
                                                               new CircleDimensionSet(),
                                                               new DataIOSet(),
                                                               new AdvancedData(),
                                                               new CancellingConditionalEventExecutionSet(
                                                                       new CancelActivity(true),
                                                                       new SLADueDate(),
                                                                       new ConditionExpression(
                                                                               new ScriptTypeValue("drools", "script")))))

                .addFalseCase(new IntermediateConditionalEvent(new BPMNGeneralSet("name", "doc"),
                                                               new BackgroundSet(),
                                                               new FontSet(),
                                                               new CircleDimensionSet(),
                                                               new DataIOSet(),
                                                               new AdvancedData(),
                                                               new CancellingConditionalEventExecutionSet(
                                                                       new CancelActivity(false),
                                                                       new SLADueDate(),
                                                                       new ConditionExpression(
                                                                               new ScriptTypeValue("drools", "script")))),
                              new IntermediateConditionalEvent(new BPMNGeneralSet("name", "doc"),
                                                               new BackgroundSet(),
                                                               new FontSet(),
                                                               new CircleDimensionSet(),
                                                               new DataIOSet(),
                                                               new AdvancedData(),
                                                               new CancellingConditionalEventExecutionSet(
                                                                       new CancelActivity(false),
                                                                       new SLADueDate(),
                                                                       new ConditionExpression(
                                                                               new ScriptTypeValue("drools1", "script")))))

                .addFalseCase(new IntermediateConditionalEvent(new BPMNGeneralSet("name", "doc"),
                                                               new BackgroundSet(),
                                                               new FontSet(),
                                                               new CircleDimensionSet(),
                                                               new DataIOSet(),
                                                               new AdvancedData(),
                                                               new CancellingConditionalEventExecutionSet(
                                                                       new CancelActivity(false),
                                                                       new SLADueDate(),
                                                                       new ConditionExpression(
                                                                               new ScriptTypeValue("drools", "script")))),
                              new IntermediateConditionalEvent(new BPMNGeneralSet("name", "doc"),
                                                               new BackgroundSet(),
                                                               new FontSet(),
                                                               new CircleDimensionSet(),
                                                               new DataIOSet(),
                                                               new AdvancedData(),
                                                               new CancellingConditionalEventExecutionSet(
                                                                       new CancelActivity(false),
                                                                       new SLADueDate(),
                                                                       new ConditionExpression(
                                                                               new ScriptTypeValue("drools", "script1")))))

                .test();
    }

    @Test
    public void testEscalationRefEqualsAndHashCode() {
        TestCaseBuilder.newTestCase()
                .addTrueCase(new EscalationRef(), new EscalationRef())
                .addTrueCase(new EscalationRef("ref"), new EscalationRef("ref"))
                .addTrueCase(new EscalationRef(null), new EscalationRef(null))
                .addFalseCase(new EscalationRef("ref"), null)
                .addFalseCase(new EscalationRef("ref"), new EscalationRef("ref1"))
                .test();
    }

    @Test
    public void testCancellingTimerEventExecutionSetEqualsAndHashCode() {
        TimerSettingsValue TIMER_REF = new TimerSettingsValue("a1",
                                                              "b1",
                                                              "c1",
                                                              "d1");
        TimerSettingsValue TIMER_REF_1 = new TimerSettingsValue("a2",
                                                                "b2",
                                                                "c2",
                                                                "d2");
        TestCaseBuilder.newTestCase()
                .addTrueCase(new CancellingTimerEventExecutionSet(),
                             new CancellingTimerEventExecutionSet())

                .addTrueCase(new CancellingTimerEventExecutionSet(new CancelActivity(false), new SLADueDate(), new TimerSettings()),
                             new CancellingTimerEventExecutionSet(new CancelActivity(false), new SLADueDate(), new TimerSettings()))

                .addTrueCase(new CancellingTimerEventExecutionSet(new CancelActivity(false), new SLADueDate(), new TimerSettings(TIMER_REF)),
                             new CancellingTimerEventExecutionSet(new CancelActivity(false), new SLADueDate(), new TimerSettings(TIMER_REF)))

                .addFalseCase(new CancellingTimerEventExecutionSet(),
                              null)

                .addFalseCase(new CancellingTimerEventExecutionSet(new CancelActivity(false), new SLADueDate(), new TimerSettings(TIMER_REF)),
                              new CancellingTimerEventExecutionSet(new CancelActivity(false), new SLADueDate(), new TimerSettings(TIMER_REF_1)))
                .test();
    }

    @Test
    public void testCancellingMessageEventExecutionSetEqualsAndHashCode() {
        String MESSAGE_REF = "MESSAGE_REF";
        String MESSAGE_REF_1 = "MESSAGE_REF_1";
        TestCaseBuilder.newTestCase()
                .addTrueCase(new CancellingMessageEventExecutionSet(),
                             new CancellingMessageEventExecutionSet())

                .addTrueCase(new CancellingMessageEventExecutionSet(new CancelActivity(false), new SLADueDate(), new MessageRef()),
                             new CancellingMessageEventExecutionSet(new CancelActivity(false), new SLADueDate(), new MessageRef()))

                .addTrueCase(new CancellingMessageEventExecutionSet(new CancelActivity(false), new SLADueDate(), new MessageRef(MESSAGE_REF, "")),
                             new CancellingMessageEventExecutionSet(new CancelActivity(false), new SLADueDate(), new MessageRef(MESSAGE_REF, "")))

                .addFalseCase(new CancellingMessageEventExecutionSet(),
                              null)

                .addFalseCase(new CancellingMessageEventExecutionSet(new CancelActivity(false), new SLADueDate(), new MessageRef(MESSAGE_REF, "")),
                              new CancellingMessageEventExecutionSet(new CancelActivity(false), new SLADueDate(), new MessageRef(MESSAGE_REF_1, "")))
                .test();
    }

    @Test
    public void testCancellingSignalEventExecutionSetEqualsAndHashCode() {
        String SIGNAL_REF = "SIGNAL_REF";
        String SIGNAL_REF_1 = "SIGNAL_REF_1";
        TestCaseBuilder.newTestCase()
                .addTrueCase(new CancellingSignalEventExecutionSet(),
                             new CancellingSignalEventExecutionSet())

                .addTrueCase(new CancellingSignalEventExecutionSet(new CancelActivity(false), new SLADueDate(), new SignalRef()),
                             new CancellingSignalEventExecutionSet(new CancelActivity(false), new SLADueDate(), new SignalRef()))

                .addTrueCase(new CancellingSignalEventExecutionSet(new CancelActivity(false), new SLADueDate(), new SignalRef(SIGNAL_REF)),
                             new CancellingSignalEventExecutionSet(new CancelActivity(false), new SLADueDate(), new SignalRef(SIGNAL_REF)))

                .addFalseCase(new CancellingSignalEventExecutionSet(),
                              null)

                .addFalseCase(new CancellingSignalEventExecutionSet(new CancelActivity(false), new SLADueDate(), new SignalRef(SIGNAL_REF)),
                              new CancellingSignalEventExecutionSet(new CancelActivity(false), new SLADueDate(), new SignalRef(SIGNAL_REF_1)))

                .test();
    }

    @Test
    public void testCancellingErrorEventExecutionSetEqualsAndHashCode() {
        String ERROR_REF = "ERROR_REF";
        String ERROR_REF_1 = "ERROR_REF_1";
        TestCaseBuilder.newTestCase()
                .addTrueCase(new CancellingErrorEventExecutionSet(),
                             new CancellingErrorEventExecutionSet())

                .addTrueCase(new CancellingErrorEventExecutionSet(new CancelActivity(false), new SLADueDate(), new ErrorRef()),
                             new CancellingErrorEventExecutionSet(new CancelActivity(false), new SLADueDate(), new ErrorRef()))

                .addTrueCase(new CancellingErrorEventExecutionSet(new CancelActivity(false), new SLADueDate(), new ErrorRef(ERROR_REF)),
                             new CancellingErrorEventExecutionSet(new CancelActivity(false), new SLADueDate(), new ErrorRef(ERROR_REF)))

                .addFalseCase(new CancellingErrorEventExecutionSet(),
                              null)

                .addFalseCase(new CancellingErrorEventExecutionSet(new CancelActivity(false), new SLADueDate(), new ErrorRef(ERROR_REF)),
                              new CancellingErrorEventExecutionSet(new CancelActivity(false), new SLADueDate(), new ErrorRef(ERROR_REF_1)))

                .test();
    }

    @Test
    public void testCancellingEscalationEventExecutionSetEqualsAndHashCode() {
        String ESCALATION_REF = "ESCALATION_REF";
        String ESCALATION_REF_1 = "ESCALATION_REF_1";
        TestCaseBuilder.newTestCase()
                .addTrueCase(new CancellingEscalationEventExecutionSet(),
                             new CancellingEscalationEventExecutionSet())

                .addTrueCase(new CancellingEscalationEventExecutionSet(new CancelActivity(false), new SLADueDate(), new EscalationRef()),
                             new CancellingEscalationEventExecutionSet(new CancelActivity(false), new SLADueDate(), new EscalationRef()))

                .addTrueCase(new CancellingEscalationEventExecutionSet(new CancelActivity(false), new SLADueDate(), new EscalationRef(ESCALATION_REF)),
                             new CancellingEscalationEventExecutionSet(new CancelActivity(false), new SLADueDate(), new EscalationRef(ESCALATION_REF)))

                .addFalseCase(new CancellingEscalationEventExecutionSet(),
                              null)

                .addFalseCase(new CancellingEscalationEventExecutionSet(new CancelActivity(false), new SLADueDate(), new EscalationRef(ESCALATION_REF)),
                              new CancellingEscalationEventExecutionSet(new CancelActivity(false), new SLADueDate(), new EscalationRef(ESCALATION_REF_1)))

                .test();
    }

    @Test
    public void testInterruptingEscalationEventExecutionSetEqualsAndHashCode() {
        String ESCALATION_REF = "ESCALATION_REF";
        String ESCALATION_REF_1 = "ESCALATION_REF_1";
        TestCaseBuilder.newTestCase()
                .addTrueCase(new InterruptingEscalationEventExecutionSet(),
                             new InterruptingEscalationEventExecutionSet())

                .addTrueCase(new InterruptingEscalationEventExecutionSet(new IsInterrupting(false), new SLADueDate(), new EscalationRef()),
                             new InterruptingEscalationEventExecutionSet(new IsInterrupting(false), new SLADueDate(), new EscalationRef()))

                .addTrueCase(new InterruptingEscalationEventExecutionSet(new IsInterrupting(false), new SLADueDate(), new EscalationRef(ESCALATION_REF)),
                             new InterruptingEscalationEventExecutionSet(new IsInterrupting(false), new SLADueDate(), new EscalationRef(ESCALATION_REF)))

                .addFalseCase(new InterruptingEscalationEventExecutionSet(),
                              null)

                .addFalseCase(new InterruptingEscalationEventExecutionSet(new IsInterrupting(false), new SLADueDate(), new EscalationRef(ESCALATION_REF)),
                              new InterruptingEscalationEventExecutionSet(new IsInterrupting(true), new SLADueDate(), new EscalationRef(ESCALATION_REF)))

                .addFalseCase(new InterruptingEscalationEventExecutionSet(new IsInterrupting(false), new SLADueDate(), new EscalationRef(ESCALATION_REF)),
                              new InterruptingEscalationEventExecutionSet(new IsInterrupting(false), new SLADueDate(), new EscalationRef(ESCALATION_REF_1)))

                .test();
    }

    @Test
    public void testEscalationEventExecutionSetEqualsAndHashCode() {
        String ESCALATION_REF = "ESCALATION_REF";
        String ESCALATION_REF_1 = "ESCALATION_REF_1";
        TestCaseBuilder.newTestCase()
                .addTrueCase(new EscalationEventExecutionSet(),
                             new EscalationEventExecutionSet())

                .addTrueCase(new EscalationEventExecutionSet(new EscalationRef()),
                             new EscalationEventExecutionSet(new EscalationRef()))

                .addTrueCase(new EscalationEventExecutionSet(new EscalationRef(ESCALATION_REF)),
                             new EscalationEventExecutionSet(new EscalationRef(ESCALATION_REF)))

                .addFalseCase(new EscalationEventExecutionSet(),
                              null)

                .addFalseCase(new EscalationEventExecutionSet(new EscalationRef(ESCALATION_REF)),
                              new EscalationEventExecutionSet(new EscalationRef(ESCALATION_REF_1)))

                .test();
    }

    @Test
    public void testStartEscalationEventEqualsAndHashCode() {
        String ESCALATION_REF = "ESCALATION_REF";
        String ESCALATION_REF_1 = "ESCALATION_REF_1";
        TestCaseBuilder.newTestCase()
                .addTrueCase(new StartEscalationEvent(),
                             new StartEscalationEvent())

                .addTrueCase(new StartEscalationEvent(new BPMNGeneralSet(),
                                                      new BackgroundSet(),
                                                      new FontSet(),
                                                      new CircleDimensionSet(),
                                                      new SimulationAttributeSet(),
                                                      new AdvancedData(),
                                                      new DataIOSet(),
                                                      new InterruptingEscalationEventExecutionSet()),
                             new StartEscalationEvent(new BPMNGeneralSet(),
                                                      new BackgroundSet(),
                                                      new FontSet(),
                                                      new CircleDimensionSet(),
                                                      new SimulationAttributeSet(),
                                                      new AdvancedData(),
                                                      new DataIOSet(),
                                                      new InterruptingEscalationEventExecutionSet()))

                .addFalseCase(new StartEscalationEvent(),
                              null)

                .addFalseCase(new StartEscalationEvent(new BPMNGeneralSet("name", "doc"),
                                                       new BackgroundSet(),
                                                       new FontSet(),
                                                       new CircleDimensionSet(),
                                                       new SimulationAttributeSet(),
                                                       new AdvancedData(),
                                                       new DataIOSet(),
                                                       new InterruptingEscalationEventExecutionSet()),
                              new StartEscalationEvent(new BPMNGeneralSet("name1", "doc1"),
                                                       new BackgroundSet(),
                                                       new FontSet(),
                                                       new CircleDimensionSet(),
                                                       new SimulationAttributeSet(),
                                                       new AdvancedData(),
                                                       new DataIOSet(),
                                                       new InterruptingEscalationEventExecutionSet()))

                .addFalseCase(new StartEscalationEvent(new BPMNGeneralSet("name", "doc"),
                                                       new BackgroundSet(),
                                                       new FontSet(),
                                                       new CircleDimensionSet(),
                                                       new SimulationAttributeSet(),
                                                       new AdvancedData(),
                                                       new DataIOSet(),
                                                       new InterruptingEscalationEventExecutionSet(
                                                               new IsInterrupting(true),
                                                               new SLADueDate(),
                                                               new EscalationRef(ESCALATION_REF))),
                              new StartEscalationEvent(new BPMNGeneralSet("name", "doc"),
                                                       new BackgroundSet(),
                                                       new FontSet(),
                                                       new CircleDimensionSet(),
                                                       new SimulationAttributeSet(),
                                                       new AdvancedData(),
                                                       new DataIOSet(),
                                                       new InterruptingEscalationEventExecutionSet(
                                                               new IsInterrupting(false),
                                                               new SLADueDate(),
                                                               new EscalationRef(ESCALATION_REF))))

                .addFalseCase(new StartEscalationEvent(new BPMNGeneralSet("name", "doc"),
                                                       new BackgroundSet(),
                                                       new FontSet(),
                                                       new CircleDimensionSet(),
                                                       new SimulationAttributeSet(),
                                                       new AdvancedData(),
                                                       new DataIOSet(),
                                                       new InterruptingEscalationEventExecutionSet(
                                                               new IsInterrupting(true),
                                                               new SLADueDate(),
                                                               new EscalationRef(ESCALATION_REF))),
                              new StartEscalationEvent(new BPMNGeneralSet("name", "doc"),
                                                       new BackgroundSet(),
                                                       new FontSet(),
                                                       new CircleDimensionSet(),
                                                       new SimulationAttributeSet(),
                                                       new AdvancedData(),
                                                       new DataIOSet(),
                                                       new InterruptingEscalationEventExecutionSet(
                                                               new IsInterrupting(true),
                                                               new SLADueDate(),
                                                               new EscalationRef(ESCALATION_REF_1))))

                .addFalseCase(new StartEscalationEvent(new BPMNGeneralSet("name", "doc"),
                                                       new BackgroundSet(),
                                                       new FontSet(),
                                                       new CircleDimensionSet(),
                                                       new SimulationAttributeSet(),
                                                       new AdvancedData(),
                                                       new DataIOSet(),
                                                       new InterruptingEscalationEventExecutionSet(
                                                               new IsInterrupting(false),
                                                               new SLADueDate(),
                                                               new EscalationRef(ESCALATION_REF))),
                              new StartEscalationEvent(new BPMNGeneralSet("name", "doc"),
                                                       new BackgroundSet(),
                                                       new FontSet(),
                                                       new CircleDimensionSet(),
                                                       new SimulationAttributeSet(),
                                                       new AdvancedData(),
                                                       new DataIOSet("data"),
                                                       new InterruptingEscalationEventExecutionSet(
                                                               new IsInterrupting(false),
                                                               new SLADueDate(),
                                                               new EscalationRef(ESCALATION_REF))))

                .addFalseCase(new StartEscalationEvent(new BPMNGeneralSet("name", "doc"),
                                                       new BackgroundSet(),
                                                       new FontSet(),
                                                       new CircleDimensionSet(),
                                                       new SimulationAttributeSet(),
                                                       new AdvancedData(),
                                                       new DataIOSet("data"),
                                                       new InterruptingEscalationEventExecutionSet(
                                                               new IsInterrupting(false),
                                                               new SLADueDate(),
                                                               new EscalationRef(ESCALATION_REF))),
                              new StartEscalationEvent(new BPMNGeneralSet("name", "doc"),
                                                       new BackgroundSet(),
                                                       new FontSet(),
                                                       new CircleDimensionSet(),
                                                       new SimulationAttributeSet(),
                                                       new AdvancedData(),
                                                       new DataIOSet(),
                                                       new InterruptingEscalationEventExecutionSet(
                                                               null,
                                                               null,
                                                               new EscalationRef(ESCALATION_REF))))

                .addFalseCase(new StartEscalationEvent(new BPMNGeneralSet("name", "doc"),
                                                       new BackgroundSet(),
                                                       new FontSet(),
                                                       new CircleDimensionSet(),
                                                       new SimulationAttributeSet(),
                                                       new AdvancedData(),
                                                       new DataIOSet("data"),
                                                       new InterruptingEscalationEventExecutionSet(
                                                               new IsInterrupting(false),
                                                               new SLADueDate(),
                                                               new EscalationRef(ESCALATION_REF))),
                              new StartEscalationEvent(new BPMNGeneralSet("name", "doc"),
                                                       new BackgroundSet(),
                                                       new FontSet(),
                                                       new CircleDimensionSet(),
                                                       new SimulationAttributeSet(),
                                                       new AdvancedData(),
                                                       new DataIOSet(),
                                                       new InterruptingEscalationEventExecutionSet(
                                                               new IsInterrupting(false),
                                                               null,
                                                               null)))

                .addFalseCase(new StartEscalationEvent(new BPMNGeneralSet("name", "doc"),
                                                       new BackgroundSet(),
                                                       new FontSet(),
                                                       new CircleDimensionSet(),
                                                       new SimulationAttributeSet(),
                                                       new AdvancedData(),
                                                       new DataIOSet("data"),
                                                       new InterruptingEscalationEventExecutionSet(
                                                               new IsInterrupting(false),
                                                               new SLADueDate(),
                                                               new EscalationRef(ESCALATION_REF))),
                              new StartEscalationEvent(new BPMNGeneralSet("name", "doc"),
                                                       new BackgroundSet(),
                                                       new FontSet(),
                                                       new CircleDimensionSet(),
                                                       new SimulationAttributeSet(),
                                                       new AdvancedData(),
                                                       null,
                                                       new InterruptingEscalationEventExecutionSet(
                                                               new IsInterrupting(false),
                                                               new SLADueDate(),
                                                               new EscalationRef(ESCALATION_REF))))

                .test();
    }

    @Test
    public void testIntermediateEscalationCatchingEventEqualsAndHashCode() {
        String ESCALATION_REF = "ESCALATION_REF";
        String ESCALATION_REF_1 = "ESCALATION_REF_1";
        TestCaseBuilder.newTestCase()
                .addTrueCase(new IntermediateEscalationEvent(),
                             new IntermediateEscalationEvent())

                .addTrueCase(new IntermediateEscalationEvent(new BPMNGeneralSet(),
                                                             new BackgroundSet(),
                                                             new FontSet(),
                                                             new CircleDimensionSet(),
                                                             new DataIOSet(),
                                                             new AdvancedData(),
                                                             new CancellingEscalationEventExecutionSet()),
                             new IntermediateEscalationEvent(new BPMNGeneralSet(),
                                                             new BackgroundSet(),
                                                             new FontSet(),
                                                             new CircleDimensionSet(),
                                                             new DataIOSet(),
                                                             new AdvancedData(),
                                                             new CancellingEscalationEventExecutionSet()))

                .addFalseCase(new IntermediateEscalationEvent(),
                              null)

                .addFalseCase(new IntermediateEscalationEvent(new BPMNGeneralSet("name", "doc"),
                                                              new BackgroundSet(),
                                                              new FontSet(),
                                                              new CircleDimensionSet(),
                                                              new DataIOSet(),
                                                              new AdvancedData(),
                                                              new CancellingEscalationEventExecutionSet()),
                              new IntermediateEscalationEvent(new BPMNGeneralSet("name1", "doc1"),
                                                              new BackgroundSet(),
                                                              new FontSet(),
                                                              new CircleDimensionSet(),
                                                              new DataIOSet(),
                                                              new AdvancedData(),
                                                              new CancellingEscalationEventExecutionSet()))

                .addFalseCase(new IntermediateEscalationEvent(new BPMNGeneralSet("name", "doc"),
                                                              new BackgroundSet(),
                                                              new FontSet(),
                                                              new CircleDimensionSet(),
                                                              new DataIOSet(),
                                                              new AdvancedData(),
                                                              new CancellingEscalationEventExecutionSet(
                                                                      new CancelActivity(true),
                                                                      new SLADueDate(),
                                                                      new EscalationRef(ESCALATION_REF))),
                              new IntermediateEscalationEvent(new BPMNGeneralSet("name", "doc"),
                                                              new BackgroundSet(),
                                                              new FontSet(),
                                                              new CircleDimensionSet(),
                                                              new DataIOSet(),
                                                              new AdvancedData(),
                                                              new CancellingEscalationEventExecutionSet(
                                                                      new CancelActivity(false),
                                                                      new SLADueDate(),
                                                                      new EscalationRef(ESCALATION_REF))))

                .addFalseCase(new IntermediateEscalationEvent(new BPMNGeneralSet("name", "doc"),
                                                              new BackgroundSet(),
                                                              new FontSet(),
                                                              new CircleDimensionSet(),
                                                              new DataIOSet(),
                                                              new AdvancedData(),
                                                              new CancellingEscalationEventExecutionSet(
                                                                      new CancelActivity(true),
                                                                      new SLADueDate(),
                                                                      new EscalationRef(ESCALATION_REF))),
                              new IntermediateEscalationEvent(new BPMNGeneralSet("name", "doc"),
                                                              new BackgroundSet(),
                                                              new FontSet(),
                                                              new CircleDimensionSet(),
                                                              new DataIOSet(),
                                                              new AdvancedData(),
                                                              new CancellingEscalationEventExecutionSet(
                                                                      new CancelActivity(true),
                                                                      new SLADueDate(),
                                                                      new EscalationRef(ESCALATION_REF_1))))

                .addFalseCase(new IntermediateEscalationEvent(new BPMNGeneralSet("name", "doc"),
                                                              new BackgroundSet(),
                                                              new FontSet(),
                                                              new CircleDimensionSet(),
                                                              new DataIOSet(),
                                                              new AdvancedData(),
                                                              new CancellingEscalationEventExecutionSet(
                                                                      new CancelActivity(false),
                                                                      new SLADueDate(),
                                                                      new EscalationRef(ESCALATION_REF))),
                              new IntermediateEscalationEvent(new BPMNGeneralSet("name", "doc"),
                                                              new BackgroundSet(),
                                                              new FontSet(),
                                                              new CircleDimensionSet(),
                                                              new DataIOSet("data"),
                                                              new AdvancedData(),
                                                              new CancellingEscalationEventExecutionSet(
                                                                      new CancelActivity(false),
                                                                      new SLADueDate(),
                                                                      new EscalationRef(ESCALATION_REF))))

                .addFalseCase(new IntermediateEscalationEvent(new BPMNGeneralSet("name", "doc"),
                                                              new BackgroundSet(),
                                                              new FontSet(),
                                                              new CircleDimensionSet(),
                                                              new DataIOSet("data"),
                                                              new AdvancedData(),
                                                              new CancellingEscalationEventExecutionSet(
                                                                      new CancelActivity(false),
                                                                      new SLADueDate(),
                                                                      new EscalationRef(ESCALATION_REF))),
                              new IntermediateEscalationEvent(new BPMNGeneralSet("name", "doc"),
                                                              new BackgroundSet(),
                                                              new FontSet(),
                                                              new CircleDimensionSet(),
                                                              new DataIOSet(),
                                                              new AdvancedData(),
                                                              new CancellingEscalationEventExecutionSet(
                                                                      null,
                                                                      new SLADueDate(),
                                                                      new EscalationRef(ESCALATION_REF))))

                .addFalseCase(new IntermediateEscalationEvent(new BPMNGeneralSet("name", "doc"),
                                                              new BackgroundSet(),
                                                              new FontSet(),
                                                              new CircleDimensionSet(),
                                                              new DataIOSet("data"),
                                                              new AdvancedData(),
                                                              new CancellingEscalationEventExecutionSet(
                                                                      new CancelActivity(false),
                                                                      new SLADueDate(),
                                                                      new EscalationRef(ESCALATION_REF))),
                              new IntermediateEscalationEvent(new BPMNGeneralSet("name", "doc"),
                                                              new BackgroundSet(),
                                                              new FontSet(),
                                                              new CircleDimensionSet(),
                                                              new DataIOSet(),
                                                              new AdvancedData(),
                                                              new CancellingEscalationEventExecutionSet(
                                                                      new CancelActivity(false),
                                                                      new SLADueDate(),
                                                                      null)))

                .addFalseCase(new IntermediateEscalationEvent(new BPMNGeneralSet("name", "doc"),
                                                              new BackgroundSet(),
                                                              new FontSet(),
                                                              new CircleDimensionSet(),
                                                              new DataIOSet("data"),
                                                              new AdvancedData(),
                                                              new CancellingEscalationEventExecutionSet(
                                                                      new CancelActivity(false),
                                                                      new SLADueDate(),
                                                                      new EscalationRef(ESCALATION_REF))),
                              new IntermediateEscalationEvent(new BPMNGeneralSet("name", "doc"),
                                                              new BackgroundSet(),
                                                              new FontSet(),
                                                              new CircleDimensionSet(),
                                                              null,
                                                              new AdvancedData(),
                                                              new CancellingEscalationEventExecutionSet(
                                                                      new CancelActivity(false),
                                                                      new SLADueDate(),
                                                                      new EscalationRef(ESCALATION_REF))))

                .test();
    }

    @Test
    public void testIntermediateEscalationThrowingEventEqualsAndHashCode() {
        String ESCALATION_REF = "ESCALATION_REF";
        String ESCALATION_REF_1 = "ESCALATION_REF_1";
        TestCaseBuilder.newTestCase()
                .addTrueCase(new IntermediateEscalationEventThrowing(),
                             new IntermediateEscalationEventThrowing())

                .addTrueCase(new IntermediateEscalationEventThrowing(new BPMNGeneralSet(),
                                                                     new BackgroundSet(),
                                                                     new FontSet(),
                                                                     new CircleDimensionSet(),
                                                                     new DataIOSet(),
                                                                     new AdvancedData(),
                                                                     new EscalationEventExecutionSet()),
                             new IntermediateEscalationEventThrowing(new BPMNGeneralSet(),
                                                                     new BackgroundSet(),
                                                                     new FontSet(),
                                                                     new CircleDimensionSet(),
                                                                     new DataIOSet(),
                                                                     new AdvancedData(),
                                                                     new EscalationEventExecutionSet()))

                .addFalseCase(new IntermediateEscalationEventThrowing(),
                              null)

                .addTrueCase(
                        new IntermediateEscalationEventThrowing(new BPMNGeneralSet("name", "doc"),
                                                                new BackgroundSet(),
                                                                new FontSet(),
                                                                new CircleDimensionSet(),
                                                                new DataIOSet(),
                                                                new AdvancedData(),
                                                                new EscalationEventExecutionSet()),
                        new IntermediateEscalationEventThrowing(new BPMNGeneralSet("name", "doc"),
                                                                new BackgroundSet(),
                                                                new FontSet(),
                                                                new CircleDimensionSet(),
                                                                new DataIOSet(),
                                                                new AdvancedData(),
                                                                new EscalationEventExecutionSet()))

                .addTrueCase(
                        new IntermediateEscalationEventThrowing(new BPMNGeneralSet("name", "doc"),
                                                                new BackgroundSet(),
                                                                new FontSet(),
                                                                new CircleDimensionSet(),
                                                                new DataIOSet(), new AdvancedData(),
                                                                new EscalationEventExecutionSet(
                                                                        new EscalationRef(ESCALATION_REF))),
                        new IntermediateEscalationEventThrowing(new BPMNGeneralSet("name", "doc"),
                                                                new BackgroundSet(),
                                                                new FontSet(),
                                                                new CircleDimensionSet(),
                                                                new DataIOSet(),
                                                                new AdvancedData(),
                                                                new EscalationEventExecutionSet(
                                                                        new EscalationRef(ESCALATION_REF))))

                .addFalseCase(
                        new IntermediateEscalationEventThrowing(new BPMNGeneralSet("name", "doc"),
                                                                new BackgroundSet(),
                                                                new FontSet(),
                                                                new CircleDimensionSet(),
                                                                new DataIOSet(),
                                                                new AdvancedData(),
                                                                new EscalationEventExecutionSet(
                                                                        new EscalationRef(ESCALATION_REF))),
                        new IntermediateEscalationEventThrowing(
                                new BPMNGeneralSet("name1", "doc1"),
                                new BackgroundSet(),
                                new FontSet(),
                                new CircleDimensionSet(),
                                new DataIOSet(), new AdvancedData(),
                                new EscalationEventExecutionSet(
                                        new EscalationRef(ESCALATION_REF))))

                .addFalseCase(
                        new IntermediateEscalationEventThrowing(
                                new BPMNGeneralSet("name", "doc"),
                                new BackgroundSet(),
                                new FontSet(),
                                new CircleDimensionSet(),
                                new DataIOSet(),
                                new AdvancedData(),
                                new EscalationEventExecutionSet(new EscalationRef(ESCALATION_REF))),
                        new IntermediateEscalationEventThrowing(new BPMNGeneralSet("name", "doc"),
                                                                new BackgroundSet(),
                                                                new FontSet(),
                                                                new CircleDimensionSet(),
                                                                new DataIOSet(),
                                                                new AdvancedData(),
                                                                new EscalationEventExecutionSet(
                                                                        new EscalationRef(ESCALATION_REF_1))))

                .addFalseCase(
                        new IntermediateEscalationEventThrowing(new BPMNGeneralSet("name", "doc"),
                                                                new BackgroundSet(),
                                                                new FontSet(),
                                                                new CircleDimensionSet(),
                                                                new DataIOSet(),
                                                                new AdvancedData(),

                                                                new EscalationEventExecutionSet(
                                                                        new EscalationRef(ESCALATION_REF))),
                        new IntermediateEscalationEventThrowing(new BPMNGeneralSet("name", "doc"),
                                                                new BackgroundSet(),
                                                                new FontSet(),
                                                                new CircleDimensionSet(),
                                                                new DataIOSet(),
                                                                new AdvancedData(),
                                                                new EscalationEventExecutionSet(null)))

                .addFalseCase(
                        new IntermediateEscalationEventThrowing(new BPMNGeneralSet("name", "doc"),
                                                                new BackgroundSet(),
                                                                new FontSet(),
                                                                new CircleDimensionSet(),
                                                                new DataIOSet(),
                                                                new AdvancedData(),
                                                                new EscalationEventExecutionSet(
                                                                        new EscalationRef(ESCALATION_REF))),
                        new IntermediateEscalationEventThrowing(new BPMNGeneralSet("name", "doc"),
                                                                new BackgroundSet(),
                                                                new FontSet(),
                                                                new CircleDimensionSet(),
                                                                new DataIOSet(),
                                                                new AdvancedData(),
                                                                null))

                .test();
    }

    @Test
    public void testEndEscalationEventEqualsAndHashCode() {
        String ESCALATION_REF = "ESCALATION_REF";
        String ESCALATION_REF_1 = "ESCALATION_REF_1";
        TestCaseBuilder.newTestCase()
                .addTrueCase(new EndEscalationEvent(),
                             new EndEscalationEvent())

                .addTrueCase(new EndEscalationEvent(new BPMNGeneralSet(),
                                                    new BackgroundSet(),
                                                    new FontSet(),
                                                    new CircleDimensionSet(),
                                                    new EscalationEventExecutionSet(),
                                                    new AdvancedData(),
                                                    new DataIOSet()),
                             new EndEscalationEvent(new BPMNGeneralSet(),
                                                    new BackgroundSet(),
                                                    new FontSet(),
                                                    new CircleDimensionSet(),
                                                    new EscalationEventExecutionSet(),
                                                    new AdvancedData(),
                                                    new DataIOSet()))

                .addFalseCase(new EndEscalationEvent(),
                              null)

                .addTrueCase(new EndEscalationEvent(new BPMNGeneralSet("name", "doc"),
                                                    new BackgroundSet(),
                                                    new FontSet(),
                                                    new CircleDimensionSet(),
                                                    new EscalationEventExecutionSet(),
                                                    new AdvancedData(),
                                                    new DataIOSet()),
                             new EndEscalationEvent(new BPMNGeneralSet("name", "doc"),
                                                    new BackgroundSet(),
                                                    new FontSet(),
                                                    new CircleDimensionSet(),
                                                    new EscalationEventExecutionSet(),
                                                    new AdvancedData(),
                                                    new DataIOSet()))

                .addTrueCase(new EndEscalationEvent(new BPMNGeneralSet("name", "doc"),
                                                    new BackgroundSet(),
                                                    new FontSet(),
                                                    new CircleDimensionSet(),
                                                    new EscalationEventExecutionSet(new EscalationRef(ESCALATION_REF)),
                                                    new AdvancedData(),
                                                    new DataIOSet()),
                             new EndEscalationEvent(new BPMNGeneralSet("name", "doc"),
                                                    new BackgroundSet(),
                                                    new FontSet(),
                                                    new CircleDimensionSet(),
                                                    new EscalationEventExecutionSet(new EscalationRef(ESCALATION_REF)),
                                                    new AdvancedData(),
                                                    new DataIOSet()))

                .addFalseCase(new EndEscalationEvent(new BPMNGeneralSet("name", "doc"),
                                                     new BackgroundSet(),
                                                     new FontSet(),
                                                     new CircleDimensionSet(),
                                                     new EscalationEventExecutionSet(new EscalationRef(ESCALATION_REF)),
                                                     new AdvancedData(),
                                                     new DataIOSet()),
                              new EndEscalationEvent(new BPMNGeneralSet("name1", "doc1"),
                                                     new BackgroundSet(),
                                                     new FontSet(),
                                                     new CircleDimensionSet(),
                                                     new EscalationEventExecutionSet(new EscalationRef(ESCALATION_REF)),
                                                     new AdvancedData(),
                                                     new DataIOSet()))

                .addFalseCase(new EndEscalationEvent(new BPMNGeneralSet("name", "doc"),
                                                     new BackgroundSet(),
                                                     new FontSet(),
                                                     new CircleDimensionSet(),
                                                     new EscalationEventExecutionSet(new EscalationRef(ESCALATION_REF)),
                                                     new AdvancedData(),
                                                     new DataIOSet()),
                              new EndEscalationEvent(new BPMNGeneralSet("name", "doc"),
                                                     new BackgroundSet(),
                                                     new FontSet(),
                                                     new CircleDimensionSet(),
                                                     new EscalationEventExecutionSet(new EscalationRef(ESCALATION_REF_1)),
                                                     new AdvancedData(),
                                                     new DataIOSet()))

                .addFalseCase(new EndEscalationEvent(new BPMNGeneralSet("name", "doc"),
                                                     new BackgroundSet(),
                                                     new FontSet(),
                                                     new CircleDimensionSet(),
                                                     new EscalationEventExecutionSet(new EscalationRef(ESCALATION_REF)),
                                                     new AdvancedData(),
                                                     new DataIOSet()),
                              new EndEscalationEvent(new BPMNGeneralSet("name", "doc"),
                                                     new BackgroundSet(),
                                                     new FontSet(),
                                                     new CircleDimensionSet(),
                                                     new EscalationEventExecutionSet(new EscalationRef(ESCALATION_REF)),
                                                     new AdvancedData(),
                                                     new DataIOSet("data")))

                .addFalseCase(new EndEscalationEvent(new BPMNGeneralSet("name", "doc"),
                                                     new BackgroundSet(),
                                                     new FontSet(),
                                                     new CircleDimensionSet(),
                                                     new EscalationEventExecutionSet(new EscalationRef(ESCALATION_REF)),
                                                     new AdvancedData(),
                                                     new DataIOSet()),
                              new EndEscalationEvent(new BPMNGeneralSet("name", "doc"),
                                                     new BackgroundSet(),
                                                     new FontSet(),
                                                     new CircleDimensionSet(),
                                                     new EscalationEventExecutionSet(null),
                                                     new AdvancedData(),
                                                     new DataIOSet()))

                .addFalseCase(new EndEscalationEvent(new BPMNGeneralSet("name", "doc"),
                                                     new BackgroundSet(),
                                                     new FontSet(),
                                                     new CircleDimensionSet(),
                                                     new EscalationEventExecutionSet(new EscalationRef(ESCALATION_REF)),
                                                     new AdvancedData(),
                                                     new DataIOSet()),
                              new EndEscalationEvent(new BPMNGeneralSet("name", "doc"),
                                                     new BackgroundSet(),
                                                     new FontSet(),
                                                     new CircleDimensionSet(),
                                                     null,
                                                     new AdvancedData(),
                                                     new DataIOSet()))

                .test();
    }

    @Test
    public void testStartCompensationEventAndHashCode() {
        TestCaseBuilder.newTestCase()
                .addTrueCase(new StartCompensationEvent(),
                             new StartCompensationEvent())

                .addTrueCase(new StartCompensationEvent(new BPMNGeneralSet(),
                                                        new BackgroundSet(),
                                                        new FontSet(),
                                                        new CircleDimensionSet(),
                                                        new SimulationAttributeSet(),
                                                        new AdvancedData(),
                                                        new BaseStartEventExecutionSet()),
                             new StartCompensationEvent(new BPMNGeneralSet(),
                                                        new BackgroundSet(),
                                                        new FontSet(),
                                                        new CircleDimensionSet(),
                                                        new SimulationAttributeSet(),
                                                        new AdvancedData(),
                                                        new BaseStartEventExecutionSet()))

                .addTrueCase(new StartCompensationEvent(new BPMNGeneralSet("name", "doc"),
                                                        new BackgroundSet(),
                                                        new FontSet(),
                                                        new CircleDimensionSet(),
                                                        new SimulationAttributeSet(),
                                                        new AdvancedData(),
                                                        new BaseStartEventExecutionSet()),
                             new StartCompensationEvent(new BPMNGeneralSet("name", "doc"),
                                                        new BackgroundSet(),
                                                        new FontSet(),
                                                        new CircleDimensionSet(),
                                                        new SimulationAttributeSet(),
                                                        new AdvancedData(),
                                                        new BaseStartEventExecutionSet()))

                .addFalseCase(new StartCompensationEvent(), null)

                .addFalseCase(new StartCompensationEvent(new BPMNGeneralSet("name", "doc"),
                                                         new BackgroundSet(),
                                                         new FontSet(),
                                                         new CircleDimensionSet(),
                                                         new SimulationAttributeSet(),
                                                         new AdvancedData(),
                                                         new BaseStartEventExecutionSet()),
                              new StartCompensationEvent(new BPMNGeneralSet("name1", "doc1"),
                                                         new BackgroundSet(),
                                                         new FontSet(),
                                                         new CircleDimensionSet(),
                                                         new SimulationAttributeSet(),
                                                         new AdvancedData(),
                                                         new BaseStartEventExecutionSet()))

                .test();
    }

    @Test
    public void testIntermediateCompensationEventAndHashCode() {
        TestCaseBuilder.newTestCase()
                .addTrueCase(new IntermediateCompensationEvent(),
                             new IntermediateCompensationEvent())

                .addTrueCase(new IntermediateCompensationEvent(new BPMNGeneralSet(),
                                                               new BackgroundSet(),
                                                               new FontSet(),
                                                               new CircleDimensionSet(),
                                                               new DataIOSet(),
                                                               new AdvancedData(),
                                                               new BaseCancellingEventExecutionSet()),
                             new IntermediateCompensationEvent(new BPMNGeneralSet(),
                                                               new BackgroundSet(),
                                                               new FontSet(),
                                                               new CircleDimensionSet(),
                                                               new DataIOSet(),
                                                               new AdvancedData(),
                                                               new BaseCancellingEventExecutionSet()))

                .addTrueCase(new IntermediateCompensationEvent(new BPMNGeneralSet("name", "doc"),
                                                               new BackgroundSet(),
                                                               new FontSet(),
                                                               new CircleDimensionSet(),
                                                               new DataIOSet(),
                                                               new AdvancedData(),
                                                               new BaseCancellingEventExecutionSet()),
                             new IntermediateCompensationEvent(new BPMNGeneralSet("name", "doc"),
                                                               new BackgroundSet(),
                                                               new FontSet(),
                                                               new CircleDimensionSet(),
                                                               new DataIOSet(),
                                                               new AdvancedData(),
                                                               new BaseCancellingEventExecutionSet()))

                .addFalseCase(new IntermediateCompensationEvent(), null)

                .addFalseCase(new IntermediateCompensationEvent(new BPMNGeneralSet("name", "doc"),
                                                                new BackgroundSet(),
                                                                new FontSet(),
                                                                new CircleDimensionSet(),
                                                                new DataIOSet(),
                                                                new AdvancedData(),
                                                                new BaseCancellingEventExecutionSet()),
                              new IntermediateCompensationEvent(new BPMNGeneralSet("name1", "doc1"),
                                                                new BackgroundSet(),
                                                                new FontSet(),
                                                                new CircleDimensionSet(),
                                                                new DataIOSet(),
                                                                new AdvancedData(),
                                                                new BaseCancellingEventExecutionSet()))

                .test();
    }

    @Test
    public void testIntermediateCompensationThrowingEventEqualsAndHashCode() {
        String ACTIVITY_REF = "ACTIVITY_REF";
        String ACTIVITY_REF_1 = "ACTIVITY_REF_1";
        TestCaseBuilder.newTestCase()
                .addTrueCase(new IntermediateCompensationEventThrowing(),
                             new IntermediateCompensationEventThrowing())

                .addTrueCase(new IntermediateCompensationEventThrowing(new BPMNGeneralSet(),
                                                                       new BackgroundSet(),
                                                                       new FontSet(),
                                                                       new CircleDimensionSet(),
                                                                       new DataIOSet(),
                                                                       new AdvancedData(),
                                                                       new CompensationEventExecutionSet()),
                             new IntermediateCompensationEventThrowing(new BPMNGeneralSet(),
                                                                       new BackgroundSet(),
                                                                       new FontSet(),
                                                                       new CircleDimensionSet(),
                                                                       new DataIOSet(),
                                                                       new AdvancedData(),
                                                                       new CompensationEventExecutionSet()))

                .addFalseCase(new IntermediateCompensationEventThrowing(),
                              null)

                .addTrueCase(
                        new IntermediateCompensationEventThrowing(new BPMNGeneralSet("name", "doc"),
                                                                  new BackgroundSet(),
                                                                  new FontSet(),
                                                                  new CircleDimensionSet(),
                                                                  new DataIOSet(),
                                                                  new AdvancedData(),
                                                                  new CompensationEventExecutionSet()),
                        new IntermediateCompensationEventThrowing(new BPMNGeneralSet("name", "doc"),
                                                                  new BackgroundSet(),
                                                                  new FontSet(),
                                                                  new CircleDimensionSet(),
                                                                  new DataIOSet(),
                                                                  new AdvancedData(),
                                                                  new CompensationEventExecutionSet()))

                .addTrueCase(
                        new IntermediateCompensationEventThrowing(new BPMNGeneralSet("name", "doc"),
                                                                  new BackgroundSet(),
                                                                  new FontSet(),
                                                                  new CircleDimensionSet(),
                                                                  new DataIOSet(),
                                                                  new AdvancedData(),
                                                                  new CompensationEventExecutionSet(
                                                                          new ActivityRef(ACTIVITY_REF))),
                        new IntermediateCompensationEventThrowing(new BPMNGeneralSet("name", "doc"),
                                                                  new BackgroundSet(),
                                                                  new FontSet(),
                                                                  new CircleDimensionSet(),
                                                                  new DataIOSet(),
                                                                  new AdvancedData(),
                                                                  new CompensationEventExecutionSet(
                                                                          new ActivityRef(ACTIVITY_REF))))

                .addFalseCase(
                        new IntermediateCompensationEventThrowing(new BPMNGeneralSet("name", "doc"),
                                                                  new BackgroundSet(),
                                                                  new FontSet(),
                                                                  new CircleDimensionSet(),
                                                                  new DataIOSet(),
                                                                  new AdvancedData(),
                                                                  new CompensationEventExecutionSet(
                                                                          new ActivityRef(ACTIVITY_REF))),
                        new IntermediateCompensationEventThrowing(new BPMNGeneralSet("name1", "doc1"),
                                                                  new BackgroundSet(),
                                                                  new FontSet(),
                                                                  new CircleDimensionSet(),
                                                                  new DataIOSet(),
                                                                  new AdvancedData(),
                                                                  new CompensationEventExecutionSet(
                                                                          new ActivityRef(ACTIVITY_REF))))

                .addFalseCase(
                        new IntermediateCompensationEventThrowing(new BPMNGeneralSet("name", "doc"),
                                                                  new BackgroundSet(),
                                                                  new FontSet(),
                                                                  new CircleDimensionSet(),
                                                                  new DataIOSet(),
                                                                  new AdvancedData(),
                                                                  new CompensationEventExecutionSet(
                                                                          new ActivityRef(ACTIVITY_REF))),
                        new IntermediateCompensationEventThrowing(new BPMNGeneralSet("name", "doc"),
                                                                  new BackgroundSet(),
                                                                  new FontSet(),
                                                                  new CircleDimensionSet(),
                                                                  new DataIOSet(),
                                                                  new AdvancedData(),
                                                                  new CompensationEventExecutionSet(
                                                                          new ActivityRef(ACTIVITY_REF_1))))

                .addFalseCase(
                        new IntermediateCompensationEventThrowing(new BPMNGeneralSet("name", "doc"),
                                                                  new BackgroundSet(),
                                                                  new FontSet(),
                                                                  new CircleDimensionSet(),
                                                                  new DataIOSet(),
                                                                  new AdvancedData(),
                                                                  new CompensationEventExecutionSet(
                                                                          new ActivityRef(ACTIVITY_REF))),
                        new IntermediateCompensationEventThrowing(new BPMNGeneralSet("name", "doc"),
                                                                  new BackgroundSet(),
                                                                  new FontSet(),
                                                                  new CircleDimensionSet(),
                                                                  new DataIOSet(),
                                                                  new AdvancedData(),
                                                                  new CompensationEventExecutionSet(null)))

                .addFalseCase(
                        new IntermediateCompensationEventThrowing(new BPMNGeneralSet("name", "doc"),
                                                                  new BackgroundSet(),
                                                                  new FontSet(),
                                                                  new CircleDimensionSet(),
                                                                  new DataIOSet(),
                                                                  new AdvancedData(),
                                                                  new CompensationEventExecutionSet(
                                                                          new ActivityRef(ACTIVITY_REF))),
                        new IntermediateCompensationEventThrowing(new BPMNGeneralSet("name", "doc"),
                                                                  new BackgroundSet(),
                                                                  new FontSet(),
                                                                  new CircleDimensionSet(),
                                                                  new DataIOSet(),
                                                                  new AdvancedData(),
                                                                  null))

                .test();
    }

    @Test
    public void testEndCompensationEventEqualsAndHashCode() {
        String ACTIVITY_REF = "ACTIVITY_REF";
        String ACTIVITY_REF_1 = "ACTIVITY_REF_1";
        TestCaseBuilder.newTestCase()
                .addTrueCase(new EndCompensationEvent(),
                             new EndCompensationEvent())

                .addTrueCase(new EndCompensationEvent(new BPMNGeneralSet(),
                                                      new BackgroundSet(),
                                                      new FontSet(),
                                                      new CircleDimensionSet(),
                                                      new AdvancedData(),
                                                      new CompensationEventExecutionSet()),
                             new EndCompensationEvent(new BPMNGeneralSet(),
                                                      new BackgroundSet(),
                                                      new FontSet(),
                                                      new CircleDimensionSet(),
                                                      new AdvancedData(),
                                                      new CompensationEventExecutionSet()))

                .addFalseCase(new EndCompensationEvent(),
                              null)

                .addTrueCase(new EndCompensationEvent(new BPMNGeneralSet("name", "doc"),
                                                      new BackgroundSet(),
                                                      new FontSet(),
                                                      new CircleDimensionSet(),
                                                      new AdvancedData(),
                                                      new CompensationEventExecutionSet()),
                             new EndCompensationEvent(new BPMNGeneralSet("name", "doc"),
                                                      new BackgroundSet(),
                                                      new FontSet(),
                                                      new CircleDimensionSet(),
                                                      new AdvancedData(),
                                                      new CompensationEventExecutionSet()))

                .addTrueCase(new EndCompensationEvent(new BPMNGeneralSet("name", "doc"),
                                                      new BackgroundSet(),
                                                      new FontSet(),
                                                      new CircleDimensionSet(),
                                                      new AdvancedData(),
                                                      new CompensationEventExecutionSet(new ActivityRef(ACTIVITY_REF))),
                             new EndCompensationEvent(new BPMNGeneralSet("name", "doc"),
                                                      new BackgroundSet(),
                                                      new FontSet(),
                                                      new CircleDimensionSet(),
                                                      new AdvancedData(),
                                                      new CompensationEventExecutionSet(new ActivityRef(ACTIVITY_REF))))

                .addFalseCase(new EndCompensationEvent(new BPMNGeneralSet("name", "doc"),
                                                       new BackgroundSet(),
                                                       new FontSet(),
                                                       new CircleDimensionSet(),
                                                       new AdvancedData(),
                                                       new CompensationEventExecutionSet(new ActivityRef(ACTIVITY_REF))),
                              new EndCompensationEvent(new BPMNGeneralSet("name1", "doc1"),
                                                       new BackgroundSet(),
                                                       new FontSet(),
                                                       new CircleDimensionSet(),
                                                       new AdvancedData(),
                                                       new CompensationEventExecutionSet(new ActivityRef(ACTIVITY_REF))))

                .addFalseCase(new EndCompensationEvent(new BPMNGeneralSet("name", "doc"),
                                                       new BackgroundSet(),
                                                       new FontSet(),
                                                       new CircleDimensionSet(),
                                                       new AdvancedData(),
                                                       new CompensationEventExecutionSet(new ActivityRef(ACTIVITY_REF))),
                              new EndCompensationEvent(new BPMNGeneralSet("name", "doc"),
                                                       new BackgroundSet(),
                                                       new FontSet(),
                                                       new CircleDimensionSet(),
                                                       new AdvancedData(),
                                                       new CompensationEventExecutionSet(new ActivityRef(ACTIVITY_REF_1))))

                .addFalseCase(new EndCompensationEvent(new BPMNGeneralSet("name", "doc"),
                                                       new BackgroundSet(),
                                                       new FontSet(),
                                                       new CircleDimensionSet(),
                                                       new AdvancedData(),
                                                       new CompensationEventExecutionSet(new ActivityRef(ACTIVITY_REF))),
                              new EndCompensationEvent(new BPMNGeneralSet("name", "doc"),
                                                       new BackgroundSet(),
                                                       new FontSet(),
                                                       new CircleDimensionSet(),
                                                       new AdvancedData(),
                                                       new CompensationEventExecutionSet(new ActivityRef(ACTIVITY_REF_1))))

                .addFalseCase(new EndCompensationEvent(new BPMNGeneralSet("name", "doc"),
                                                       new BackgroundSet(),
                                                       new FontSet(),
                                                       new CircleDimensionSet(),
                                                       new AdvancedData(),
                                                       new CompensationEventExecutionSet(new ActivityRef(ACTIVITY_REF))),
                              new EndCompensationEvent(new BPMNGeneralSet("name", "doc"),
                                                       new BackgroundSet(),
                                                       new FontSet(),
                                                       new CircleDimensionSet(),
                                                       new AdvancedData(),
                                                       new CompensationEventExecutionSet(null)))

                .addFalseCase(new EndCompensationEvent(new BPMNGeneralSet("name", "doc"),
                                                       new BackgroundSet(),
                                                       new FontSet(),
                                                       new CircleDimensionSet(),
                                                       new AdvancedData(),
                                                       new CompensationEventExecutionSet(new ActivityRef(ACTIVITY_REF))),
                              new EndCompensationEvent(new BPMNGeneralSet("name", "doc"),
                                                       new BackgroundSet(),
                                                       new FontSet(),
                                                       new CircleDimensionSet(),
                                                       new AdvancedData(),
                                                       null))

                .test();
    }

    @Test
    public void testActivityRefEqualsAndHashCode() {
        TestCaseBuilder.newTestCase()
                .addTrueCase(new ActivityRef(), new ActivityRef())
                .addTrueCase(new ActivityRef(null), new ActivityRef(null))
                .addTrueCase(new ActivityRef("a"), new ActivityRef("a"))
                .addFalseCase(new ActivityRef(), new ActivityRef("b"))
                .addFalseCase(new ActivityRef("a"), new ActivityRef("b"))
                .test();
    }

    @Test
    public void testCompensationEventExecutionSetEqualsAndHashCode() {
        TestCaseBuilder.newTestCase()
                .addTrueCase(new CompensationEventExecutionSet(), new CompensationEventExecutionSet())
                .addTrueCase(new CompensationEventExecutionSet(null), new CompensationEventExecutionSet(null))
                .addTrueCase(new CompensationEventExecutionSet(new ActivityRef()), new CompensationEventExecutionSet(new ActivityRef()))
                .addTrueCase(new CompensationEventExecutionSet(new ActivityRef("a")), new CompensationEventExecutionSet(new ActivityRef("a")))
                .addFalseCase(new CompensationEventExecutionSet(new ActivityRef("a")), new CompensationEventExecutionSet(new ActivityRef("b")))
                .addFalseCase(new CompensationEventExecutionSet(new ActivityRef("a")), new CompensationEventExecutionSet(new ActivityRef(null)))
                .addFalseCase(new CompensationEventExecutionSet(new ActivityRef("a")), new CompensationEventExecutionSet(new ActivityRef()))
                .test();
    }

    @Test
    public void testSignalScopeEqualsAndHashCode() {
        TestCaseBuilder.newTestCase()
                .addTrueCase(new SignalScope(), new SignalScope())
                .addTrueCase(new SignalScope("scope"), new SignalScope("scope"))
                .addTrueCase(new SignalScope(null), new SignalScope(null))
                .addFalseCase(new SignalScope("scope"), null)
                .addFalseCase(new SignalScope("scope"), new SignalScope("scope1"))
                .test();
    }

    @Test
    public void testSignalRefEqualsAndHashCode() {
        TestCaseBuilder.newTestCase()
                .addTrueCase(new SignalRef(), new SignalRef())
                .addTrueCase(new SignalRef(null), new SignalRef(null))
                .addFalseCase(new SignalRef("ref"), null)
                .addFalseCase(new SignalRef("ref"), new SignalRef("ref1"))
                .test();
    }

    @Test
    public void testDirectionalAssociationEqualsAndHashCode() {
        TestCaseBuilder.newTestCase()
                .addTrueCase(new DirectionalAssociation(), new DirectionalAssociation())
                .addTrueCase(new DirectionalAssociation(new BPMNGeneralSet(), new BackgroundSet(), new FontSet()),
                             new DirectionalAssociation(new BPMNGeneralSet(), new BackgroundSet(), new FontSet()))
                .addFalseCase(new DirectionalAssociation(),
                              new DirectionalAssociation(new BPMNGeneralSet(), new BackgroundSet(), new FontSet()))
                .addFalseCase(new DirectionalAssociation(),
                              new DirectionalAssociation(null, null, null))
                .test();
    }

    @Test
    public void testNonDirectionalAssociationEqualsAndHashCode() {
        TestCaseBuilder.newTestCase()
                .addTrueCase(new DirectionalAssociation(), new DirectionalAssociation())
                .addTrueCase(new DirectionalAssociation(new BPMNGeneralSet(), new BackgroundSet(), new FontSet()),
                             new DirectionalAssociation(new BPMNGeneralSet(), new BackgroundSet(), new FontSet()))
                .addFalseCase(new DirectionalAssociation(),
                              new DirectionalAssociation(new BPMNGeneralSet(), new BackgroundSet(), new FontSet()))
                .addFalseCase(new DirectionalAssociation(),
                              new DirectionalAssociation(null, null, null))
                .test();
    }

    @Test
    public void testEventGatewayEqualsAndHashCode() {
        TestCaseBuilder.newTestCase()
                .addTrueCase(new EventGateway(), new EventGateway())
                .addTrueCase(new EventGateway(new BPMNGeneralSet("name", "documentation"),
                                              new BackgroundSet(),
                                              new FontSet(),
                                              new CircleDimensionSet(),
                                              new AdvancedData()),
                             new EventGateway(new BPMNGeneralSet("name", "documentation"),
                                              new BackgroundSet(),
                                              new FontSet(),
                                              new CircleDimensionSet(),
                                              new AdvancedData()))
                .addFalseCase(new EventGateway(new BPMNGeneralSet("name", "documentation"),
                                               new BackgroundSet(),
                                               new FontSet(),
                                               new CircleDimensionSet(),
                                               new AdvancedData()),
                              new EventGateway(new BPMNGeneralSet("name1", "documentation"),
                                               new BackgroundSet(),
                                               new FontSet(),
                                               new CircleDimensionSet(),
                                               new AdvancedData()))
                .addFalseCase(new EventGateway(new BPMNGeneralSet("name", "documentation"),
                                               new BackgroundSet(),
                                               new FontSet(),
                                               new CircleDimensionSet(),
                                               new AdvancedData()),
                              new EventGateway(new BPMNGeneralSet("name", "documentation1"),
                                               new BackgroundSet(),
                                               new FontSet(),
                                               new CircleDimensionSet(),
                                               new AdvancedData()))
                .test();
    }

    @Test
    public void testAdHocActivationConditionEqualsAndHashCode() {
        TestCaseBuilder.newTestCase()
                .addTrueCase(new AdHocActivationCondition(), new AdHocActivationCondition())
                .addTrueCase(new AdHocActivationCondition(null), new AdHocActivationCondition(null))
                .addTrueCase(new AdHocActivationCondition("value1"), new AdHocActivationCondition("value1"))
                .addFalseCase(new AdHocActivationCondition("value1"), new AdHocActivationCondition("value2"))
                .addFalseCase(new AdHocActivationCondition("value1"), new AdHocActivationCondition(null))
                .test();
    }

    @Test
    public void testAbortParentEqualsAndHashCode() {
        TestCaseBuilder.newTestCase()
                .addTrueCase(new AbortParent(), new AbortParent())
                .addTrueCase(new AbortParent(true), new AbortParent(true))
                .addTrueCase(new AbortParent(false), new AbortParent(false))
                .addTrueCase(new AbortParent(true), new AbortParent())
                .addFalseCase(new AbortParent(false), new AbortParent())
                .addFalseCase(new AbortParent(true), new AbortParent(false))
                .test();
    }

    private class BaseStartEventStub extends BaseStartEvent {

        public BaseStartEventStub() {
            super();
        }

        public BaseStartEventStub(BPMNGeneralSet general,
                                  BackgroundSet backgroundSet,
                                  FontSet fontSet,
                                  CircleDimensionSet dimensionsSet,
                                  SimulationAttributeSet simulationSet,
                                  AdvancedData advancedData) {
            super(general,
                  backgroundSet,
                  fontSet,
                  dimensionsSet,
                  simulationSet,
                  advancedData);
        }
    }
}
