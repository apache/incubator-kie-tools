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


package org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.events;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import org.eclipse.bpmn2.BoundaryEvent;
import org.eclipse.bpmn2.CatchEvent;
import org.eclipse.bpmn2.Event;
import org.eclipse.bpmn2.FlowElement;
import org.eclipse.bpmn2.IntermediateCatchEvent;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.properties.BoundaryEventPropertyWriter;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.properties.CatchEventPropertyWriter;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.properties.FlatVariableScope;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.properties.PropertyWriter;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.properties.PropertyWriterFactory;
import org.kie.workbench.common.stunner.bpmn.definition.BaseCatchingIntermediateEvent;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateCompensationEvent;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateConditionalEvent;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateErrorEventCatching;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateEscalationEvent;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateLinkEventCatching;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateMessageEventCatching;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateSignalEventCatching;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateTimerEvent;
import org.kie.workbench.common.stunner.bpmn.definition.property.background.BackgroundSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.collaboration.events.CorrelationSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.common.ConditionExpression;
import org.kie.workbench.common.stunner.bpmn.definition.property.dataio.AssignmentsInfo;
import org.kie.workbench.common.stunner.bpmn.definition.property.dataio.DataIOSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.dimensions.CircleDimensionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.BaseCancellingEventExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.CancelActivity;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.conditional.CancellingConditionalEventExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.error.CancellingErrorEventExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.error.ErrorRef;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.escalation.CancellingEscalationEventExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.escalation.EscalationRef;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.link.LinkEventExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.link.LinkRef;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.message.CancellingMessageEventExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.message.MessageRef;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.signal.CancellingSignalEventExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.signal.SignalRef;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.timer.CancellingTimerEventExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.timer.TimerSettings;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.timer.TimerSettingsValue;
import org.kie.workbench.common.stunner.bpmn.definition.property.font.FontSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.BPMNGeneralSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.SLADueDate;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.ScriptTypeValue;
import org.kie.workbench.common.stunner.bpmn.definition.property.variables.AdvancedData;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.Bound;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.relationship.Dock;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.Factories.bpmn2;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class IntermediateCatchEventConverterTest {

    private static final String UUID = "CATCHING EVENT UUID";
    private static final String NAME = "CATCHING EVENT NAME";
    private static final String DOCUMENTATION = "CATCHING EVENT DOCUMENTATION";
    private static final String ASSIGNMENTS_INFO = "Years of Service:Integer||Data Test:Boolean||[din]Years of Service=35,[dout]Data Test->BooleanTest";
    private static final boolean CANCEL_ACTIVITY = false;

    private BoundaryEventPropertyWriter boundaryEventPropertyWriter;
    private CatchEventPropertyWriter catchEventPropertyWriter;

    private PropertyWriterFactory propertyWriterFactory;

    private BPMNGeneralSet generalSet;
    private AssignmentsInfo assignmentsInfo;
    private DataIOSet dataIOSet;
    private CorrelationSet correlationSet;
    private AdvancedData advancedData;
    private SLADueDate slaDueDate;

    private ErrorRef errorRef;
    private SignalRef signalRef;
    private LinkRef linkRef;
    private TimerSettingsValue timerSettingsValue;
    private TimerSettings timerSettings;
    private MessageRef messageRef;
    private ScriptTypeValue scriptTypeValue;
    private ConditionExpression conditionExpression;
    private EscalationRef escalationRef;

    private IntermediateCatchEventConverter tested;

    @Before
    public void setUp() {
        Event boundaryEvent = bpmn2.createBoundaryEvent();
        boundaryEvent.setId("boundaryEventID");
        boundaryEventPropertyWriter = spy(new BoundaryEventPropertyWriter((BoundaryEvent) boundaryEvent,
                                                                          new FlatVariableScope(),
                                                                          new HashSet<>()));
        when(boundaryEventPropertyWriter.getFlowElement()).thenReturn(spy(FlowElement.class));

        Event catchEvent = bpmn2.createIntermediateCatchEvent();
        catchEvent.setId("catchEventID");
        catchEventPropertyWriter = spy(new CatchEventPropertyWriter((CatchEvent) catchEvent,
                                                                    new FlatVariableScope(),
                                                                    new HashSet<>()));

        propertyWriterFactory = spy(PropertyWriterFactory.class);
        when(propertyWriterFactory.of(any(BoundaryEvent.class))).thenReturn(boundaryEventPropertyWriter);
        when(propertyWriterFactory.of(any(CatchEvent.class))).thenReturn(catchEventPropertyWriter);

        generalSet = new BPMNGeneralSet(NAME, DOCUMENTATION);

        assignmentsInfo = new AssignmentsInfo(ASSIGNMENTS_INFO);
        dataIOSet = new DataIOSet(assignmentsInfo);
        correlationSet = new CorrelationSet();
        advancedData = new AdvancedData();

        slaDueDate = mock(SLADueDate.class);
        errorRef = mock(ErrorRef.class);
        signalRef = mock(SignalRef.class);
        linkRef = mock(LinkRef.class);
        timerSettingsValue = mock(TimerSettingsValue.class);
        timerSettings = new TimerSettings(timerSettingsValue);
        messageRef = new MessageRef();
        scriptTypeValue = mock(ScriptTypeValue.class);
        conditionExpression = new ConditionExpression(scriptTypeValue);
        escalationRef = mock(EscalationRef.class);

        tested = spy(new IntermediateCatchEventConverter(propertyWriterFactory));
    }

    @Test
    public void constructor() {
        assertEquals(propertyWriterFactory, tested.propertyWriterFactory);
    }

    @Test
    public void toFlowElement() {
        Node intermediateErrorEventCatchingNode = createIntermediateErrorEventCatchingNode();
        Node intermediateSignalEventCatchingNode = createIntermediateSignalEventCatchingNode();
        Node intermediateLinkEventCatchingNode = createIntermediateLinkEventCatchingNode();
        Node intermediateTimerEventCatchingNode = createIntermediateTimerEventCatchingNode();
        Node intermediateMessageEventCatchingNode = createIntermediateMessageEventCatchingNode();
        Node intermediateConditionalEventCatchingNode = createIntermediateConditionalEventCatchingNode();
        Node intermediateEscalationEventCatchingNode = createIntermediateEscalationEventCatchingNode();
        Node intermediateCompensationEventCatchingNode = createIntermediateCompensationEventCatchingNode();

        tested.toFlowElement(intermediateErrorEventCatchingNode);
        tested.toFlowElement(intermediateSignalEventCatchingNode);
        tested.toFlowElement(intermediateLinkEventCatchingNode);
        tested.toFlowElement(intermediateTimerEventCatchingNode);
        tested.toFlowElement(intermediateMessageEventCatchingNode);
        tested.toFlowElement(intermediateConditionalEventCatchingNode);
        tested.toFlowElement(intermediateEscalationEventCatchingNode);
        tested.toFlowElement(intermediateCompensationEventCatchingNode);

        verify(tested).errorEvent(intermediateErrorEventCatchingNode);
        verify(tested).signalEvent(intermediateSignalEventCatchingNode);
        verify(tested).linkEvent(intermediateLinkEventCatchingNode);
        verify(tested).timerEvent(intermediateTimerEventCatchingNode);
        verify(tested).messageEvent(intermediateMessageEventCatchingNode);
        verify(tested).conditionalEvent(intermediateConditionalEventCatchingNode);
        verify(tested).escalationEvent(intermediateEscalationEventCatchingNode);
        verify(tested).compensationEvent(intermediateCompensationEventCatchingNode);
    }

    @Test
    public void errorEvent() {
        Node node = createIntermediateErrorEventCatchingNode();

        PropertyWriter propertyWriter = tested.errorEvent(node);
        assertNotNull(propertyWriter);

        verify(boundaryEventPropertyWriter).setCancelActivity(CANCEL_ACTIVITY);
        verify(boundaryEventPropertyWriter).addSlaDueDate(slaDueDate);
        verify(boundaryEventPropertyWriter).addError(errorRef);
        verifyCommonProperties(node);
    }

    @Test
    public void signalEvent() {
        Node node = createIntermediateSignalEventCatchingNode();

        PropertyWriter propertyWriter = tested.signalEvent(node);
        assertNotNull(propertyWriter);

        verify(boundaryEventPropertyWriter).setCancelActivity(CANCEL_ACTIVITY);
        verify(boundaryEventPropertyWriter).addSlaDueDate(slaDueDate);
        verify(boundaryEventPropertyWriter).addSignal(signalRef);
        verifyCommonProperties(node);
    }

    @Test
    public void linkEvent() {
        Node node = createIntermediateLinkEventCatchingNode();

        PropertyWriter propertyWriter = tested.linkEvent(node);
        assertNotNull(propertyWriter);

        verify(boundaryEventPropertyWriter).addLink(linkRef);
        verifyCommonProperties(node);
    }

    @Test
    public void timerEvent() {
        Node node = createIntermediateTimerEventCatchingNode();

        PropertyWriter propertyWriter = tested.timerEvent(node);
        assertNotNull(propertyWriter);

        verify(boundaryEventPropertyWriter).setCancelActivity(CANCEL_ACTIVITY);
        verify(boundaryEventPropertyWriter).addSlaDueDate(slaDueDate);
        verify(boundaryEventPropertyWriter).addTimer(timerSettings);
        verifyCommonProperties(node);
    }

    @Test
    public void messageEvent() {
        Node node = createIntermediateMessageEventCatchingNode();

        PropertyWriter propertyWriter = tested.messageEvent(node);
        assertNotNull(propertyWriter);

        verify(boundaryEventPropertyWriter).setCancelActivity(CANCEL_ACTIVITY);
        verify(boundaryEventPropertyWriter).addSlaDueDate(slaDueDate);
        verify(boundaryEventPropertyWriter).addMessage(messageRef);
        verifyCommonProperties(node);
    }

    @Test
    public void conditionalEvent() {
        Node node = createIntermediateConditionalEventCatchingNode();

        PropertyWriter propertyWriter = tested.conditionalEvent(node);
        assertNotNull(propertyWriter);

        verify(boundaryEventPropertyWriter).setCancelActivity(CANCEL_ACTIVITY);
        verify(boundaryEventPropertyWriter).addSlaDueDate(slaDueDate);
        verify(boundaryEventPropertyWriter).addCondition(conditionExpression);
        verifyCommonProperties(node);
    }

    @Test
    public void escalationEvent() {
        Node node = createIntermediateEscalationEventCatchingNode();

        PropertyWriter propertyWriter = tested.escalationEvent(node);
        assertNotNull(propertyWriter);

        verify(boundaryEventPropertyWriter).setCancelActivity(CANCEL_ACTIVITY);
        verify(boundaryEventPropertyWriter).addSlaDueDate(slaDueDate);
        verify(boundaryEventPropertyWriter).addEscalation(escalationRef);
        verifyCommonProperties(node);
    }

    @Test
    public void compensationEvent() {
        Node node = createIntermediateCompensationEventCatchingNode();

        PropertyWriter propertyWriter = tested.compensationEvent(node);
        assertNotNull(propertyWriter);

        verify(boundaryEventPropertyWriter).addSlaDueDate(slaDueDate);
        verify(boundaryEventPropertyWriter).addCompensation();
        verifyCommonProperties(node);
    }

    @Test
    public void createCatchEventPropertyWriter() {
        Node node1 = mockNode();
        CatchEventPropertyWriter result1 = tested.createCatchEventPropertyWriter(node1);

        Node node2 = mockDockedNode(mock(Node.class), null);
        CatchEventPropertyWriter result2 = tested.createCatchEventPropertyWriter(node2);

        verify(propertyWriterFactory).of(any(BoundaryEvent.class));
        verify(propertyWriterFactory).of(any(IntermediateCatchEvent.class));

        assertFalse(result1 instanceof BoundaryEventPropertyWriter);
        assertTrue(result1 instanceof CatchEventPropertyWriter);
        assertTrue(result2 instanceof BoundaryEventPropertyWriter);
    }

    private void verifyCommonProperties(Node node) {
        verify(boundaryEventPropertyWriter).getFlowElement();
        verify(boundaryEventPropertyWriter).setAbsoluteBounds(node);
        verify(boundaryEventPropertyWriter.getFlowElement()).setId(UUID);
        verify(boundaryEventPropertyWriter).setName(NAME);
        verify(boundaryEventPropertyWriter).setDocumentation(DOCUMENTATION);
        verify(boundaryEventPropertyWriter).setAssignmentsInfo(assignmentsInfo);
    }

    private Node createIntermediateErrorEventCatchingNode() {
        CancellingErrorEventExecutionSet executionSet =
                new CancellingErrorEventExecutionSet(new CancelActivity(CANCEL_ACTIVITY),
                                                     slaDueDate,
                                                     errorRef);

        IntermediateErrorEventCatching eventCatching =
                new IntermediateErrorEventCatching(generalSet,
                                                   mock(BackgroundSet.class),
                                                   mock(FontSet.class),
                                                   mock(CircleDimensionSet.class),
                                                   dataIOSet,
                                                   advancedData,
                                                   executionSet);
        Node dockNode = mockNode();
        Node node = mockDockedNode(dockNode, eventCatching);
        return node;
    }

    private Node createIntermediateSignalEventCatchingNode() {
        CancellingSignalEventExecutionSet executionSet =
                new CancellingSignalEventExecutionSet(new CancelActivity(CANCEL_ACTIVITY),
                                                      slaDueDate,
                                                      signalRef);

        IntermediateSignalEventCatching eventCatching =
                new IntermediateSignalEventCatching(generalSet,
                                                    mock(BackgroundSet.class),
                                                    mock(FontSet.class),
                                                    mock(CircleDimensionSet.class),
                                                    dataIOSet,
                                                    advancedData,
                                                    executionSet);
        Node dockNode = mockNode();
        Node node = mockDockedNode(dockNode, eventCatching);
        return node;
    }

    private Node createIntermediateLinkEventCatchingNode() {
        LinkEventExecutionSet executionSet =
                new LinkEventExecutionSet(linkRef);

        IntermediateLinkEventCatching eventCatching =
                new IntermediateLinkEventCatching(generalSet,
                                                  mock(BackgroundSet.class),
                                                  mock(FontSet.class),
                                                  mock(CircleDimensionSet.class),
                                                  dataIOSet,
                                                  advancedData,
                                                  executionSet);
        Node dockNode = mockNode();
        Node node = mockDockedNode(dockNode, eventCatching);
        return node;
    }

    private Node createIntermediateTimerEventCatchingNode() {
        CancellingTimerEventExecutionSet executionSet =
                new CancellingTimerEventExecutionSet(new CancelActivity(CANCEL_ACTIVITY),
                                                     slaDueDate,
                                                     timerSettings);

        IntermediateTimerEvent eventCatching =
                new IntermediateTimerEvent(generalSet,
                                           mock(BackgroundSet.class),
                                           mock(FontSet.class),
                                           mock(CircleDimensionSet.class),
                                           dataIOSet,
                                           advancedData,
                                           executionSet);
        Node dockNode = mockNode();
        Node node = mockDockedNode(dockNode, eventCatching);
        return node;
    }

    private Node createIntermediateMessageEventCatchingNode() {
        CancellingMessageEventExecutionSet executionSet =
                new CancellingMessageEventExecutionSet(new CancelActivity(CANCEL_ACTIVITY),
                                                       slaDueDate,
                                                       messageRef);

        IntermediateMessageEventCatching eventCatching =
                new IntermediateMessageEventCatching(generalSet,
                                                     mock(BackgroundSet.class),
                                                     mock(FontSet.class),
                                                     mock(CircleDimensionSet.class),
                                                     dataIOSet,
                                                     advancedData,
                                                     correlationSet,
                                                     executionSet);
        Node dockNode = mockNode();
        Node node = mockDockedNode(dockNode, eventCatching);
        return node;
    }

    private Node createIntermediateConditionalEventCatchingNode() {
        CancellingConditionalEventExecutionSet executionSet =
                new CancellingConditionalEventExecutionSet(new CancelActivity(CANCEL_ACTIVITY),
                                                           slaDueDate,
                                                           conditionExpression);

        IntermediateConditionalEvent eventCatching =
                new IntermediateConditionalEvent(generalSet,
                                                 mock(BackgroundSet.class),
                                                 mock(FontSet.class),
                                                 mock(CircleDimensionSet.class),
                                                 dataIOSet,
                                                 advancedData,
                                                 executionSet);
        Node dockNode = mockNode();
        Node node = mockDockedNode(dockNode, eventCatching);
        return node;
    }

    private Node createIntermediateEscalationEventCatchingNode() {
        CancellingEscalationEventExecutionSet executionSet =
                new CancellingEscalationEventExecutionSet(new CancelActivity(CANCEL_ACTIVITY),
                                                          slaDueDate,
                                                          escalationRef);

        IntermediateEscalationEvent eventCatching =
                new IntermediateEscalationEvent(generalSet,
                                                mock(BackgroundSet.class),
                                                mock(FontSet.class),
                                                mock(CircleDimensionSet.class),
                                                dataIOSet,
                                                advancedData,
                                                executionSet);
        Node dockNode = mockNode();
        Node node = mockDockedNode(dockNode, eventCatching);
        return node;
    }

    private Node createIntermediateCompensationEventCatchingNode() {
        BaseCancellingEventExecutionSet executionSet =
                new BaseCancellingEventExecutionSet(new CancelActivity(CANCEL_ACTIVITY),
                                                    slaDueDate);

        IntermediateCompensationEvent eventCatching =
                new IntermediateCompensationEvent(generalSet,
                                                  mock(BackgroundSet.class),
                                                  mock(FontSet.class),
                                                  mock(CircleDimensionSet.class),
                                                  dataIOSet,
                                                  advancedData,
                                                  executionSet);
        Node dockNode = mockNode();
        Node node = mockDockedNode(dockNode, eventCatching);
        return node;
    }

    private static Node<View<BaseCatchingIntermediateEvent>, Edge> mockNode() {
        Node<View<BaseCatchingIntermediateEvent>, Edge> node = mock(Node.class);
        View view = mock(View.class);

        Bound ul = new Bound(0d, 100d);
        Bound lr = new Bound(200d, 0d);
        Bounds bounds = new Bounds(ul, lr);

        when(node.getContent()).thenReturn(view);

        when(view.getBounds()).thenReturn(bounds);

        return node;
    }

    private static Node<View<BaseCatchingIntermediateEvent>, Edge> mockDockedNode(Node dockSourceNode,
                                                                                  BaseCatchingIntermediateEvent event) {
        Dock dockContent = mock(Dock.class);
        Edge edge = mock(Edge.class);
        List<Edge> inEdges = Collections.singletonList(edge);
        Node<View<BaseCatchingIntermediateEvent>, Edge> node = mock(Node.class);
        View view = mock(View.class);

        Bound ul = new Bound(0d, 100d);
        Bound lr = new Bound(200d, 0d);
        Bounds bounds = new Bounds(ul, lr);

        when(node.getUUID()).thenReturn(UUID);
        when(node.getInEdges()).thenReturn(inEdges);
        when(node.getContent()).thenReturn(view);

        when(edge.getContent()).thenReturn(dockContent);
        when(edge.getSourceNode()).thenReturn(dockSourceNode);

        when(view.getBounds()).thenReturn(bounds);
        when(view.getDefinition()).thenReturn(event);

        return node;
    }
}