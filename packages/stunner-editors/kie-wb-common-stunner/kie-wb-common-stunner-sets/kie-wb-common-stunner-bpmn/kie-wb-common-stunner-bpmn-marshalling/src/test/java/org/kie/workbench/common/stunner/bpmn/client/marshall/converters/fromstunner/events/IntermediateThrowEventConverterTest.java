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

import org.eclipse.bpmn2.Event;
import org.eclipse.bpmn2.ThrowEvent;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.properties.FlatVariableScope;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.properties.PropertyWriter;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.properties.PropertyWriterFactory;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.properties.ThrowEventPropertyWriter;
import org.kie.workbench.common.stunner.bpmn.definition.BaseThrowingIntermediateEvent;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateCompensationEventThrowing;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateEscalationEventThrowing;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateLinkEventThrowing;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateMessageEventThrowing;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateSignalEventThrowing;
import org.kie.workbench.common.stunner.bpmn.definition.property.background.BackgroundSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.dataio.AssignmentsInfo;
import org.kie.workbench.common.stunner.bpmn.definition.property.dataio.DataIOSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.dimensions.CircleDimensionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.compensation.CompensationEventExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.escalation.EscalationEventExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.escalation.EscalationRef;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.link.LinkEventExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.link.LinkRef;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.message.MessageEventExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.message.MessageRef;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.signal.ScopedSignalEventExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.signal.SignalRef;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.signal.SignalScope;
import org.kie.workbench.common.stunner.bpmn.definition.property.font.FontSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.BPMNGeneralSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.variables.AdvancedData;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.Bound;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.relationship.Dock;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.Factories.bpmn2;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class IntermediateThrowEventConverterTest {

    private static final String UUID = "THROWING EVENT UUID";
    private static final String NAME = "THROWING EVENT NAME";
    private static final String DOCUMENTATION = "THROWING EVENT DOCUMENTATION";
    private static final String ASSIGNMENTS_INFO = "Years of Service:Integer||Data Test:Boolean||[din]Years of Service->35,[dout]Data Test->BooleanTest";

    private ThrowEventPropertyWriter throwEventPropertyWriter;

    private PropertyWriterFactory propertyWriterFactory;

    private BPMNGeneralSet generalSet;
    private AssignmentsInfo assignmentsInfo;
    private DataIOSet dataIOSet;
    private AdvancedData advancedData;

    private SignalRef signalRef;
    private SignalScope signalScope;
    private LinkRef linkRef;
    private MessageRef messageRef;
    private EscalationRef escalationRef;

    private IntermediateThrowEventConverter tested;

    @Before
    public void setUp() {
        Event throwEvent = bpmn2.createIntermediateThrowEvent();
        throwEvent.setId("throwEventID");
        throwEventPropertyWriter = spy(new ThrowEventPropertyWriter((ThrowEvent) throwEvent,
                                                                    new FlatVariableScope(),
                                                                    new HashSet<>()));

        propertyWriterFactory = mock(PropertyWriterFactory.class);
        when(propertyWriterFactory.of(Mockito.<ThrowEvent>any())).thenReturn(throwEventPropertyWriter);

        generalSet = new BPMNGeneralSet(NAME, DOCUMENTATION);

        assignmentsInfo = new AssignmentsInfo(ASSIGNMENTS_INFO);
        dataIOSet = new DataIOSet(assignmentsInfo);
        advancedData = new AdvancedData();

        signalRef = mock(SignalRef.class);
        signalScope = mock(SignalScope.class);
        linkRef = mock(LinkRef.class);
        messageRef = new MessageRef();
        escalationRef = mock(EscalationRef.class);

        tested = spy(new IntermediateThrowEventConverter(propertyWriterFactory));
    }

    @Test
    public void constructor() {
        assertEquals(propertyWriterFactory, tested.propertyWriterFactory);
    }

    @Test
    public void toFlowElement() {
        Node intermediateSignalEventThrowingNode = createIntermediateSignalEventThrowingNode();
        Node intermediateLinkEventThrowingNode = createIntermediateLinkEventThrowingNode();
        Node intermediateMessageEventThrowingNode = createIntermediateMessageEventThrowingNode();
        Node intermediateEscalationEventThrowingNode = createIntermediateEscalationEventThrowingNode();
        Node intermediateCompensationEventThrowingNode = createIntermediateCompensationEventThrowingNode();

        tested.toFlowElement(intermediateSignalEventThrowingNode);
        tested.toFlowElement(intermediateLinkEventThrowingNode);
        tested.toFlowElement(intermediateMessageEventThrowingNode);
        tested.toFlowElement(intermediateEscalationEventThrowingNode);
        tested.toFlowElement(intermediateCompensationEventThrowingNode);

        verify(tested).signalEvent(intermediateSignalEventThrowingNode);
        verify(tested).linkEvent(intermediateLinkEventThrowingNode);
        verify(tested).messageEvent(intermediateMessageEventThrowingNode);
        verify(tested).escalationEvent(intermediateEscalationEventThrowingNode);
        verify(tested).compensationEvent(intermediateCompensationEventThrowingNode);
    }

    @Test
    public void signalEvent() {
        Node node = createIntermediateSignalEventThrowingNode();

        PropertyWriter propertyWriter = tested.signalEvent(node);
        assertNotNull(propertyWriter);

        verifyCommonProperties(node);
        verify(throwEventPropertyWriter).addSignal(signalRef);
        verify(throwEventPropertyWriter).addSignalScope(signalScope);
    }

    @Test
    public void linkEvent() {
        Node node = createIntermediateLinkEventThrowingNode();

        PropertyWriter propertyWriter = tested.linkEvent(node);
        assertNotNull(propertyWriter);

        verifyCommonProperties(node);
        verify(throwEventPropertyWriter).addLink(linkRef);
    }

    @Test
    public void messageEvent() {
        Node node = createIntermediateMessageEventThrowingNode();

        PropertyWriter propertyWriter = tested.messageEvent(node);
        assertNotNull(propertyWriter);

        verifyCommonProperties(node);
        verify(throwEventPropertyWriter).addMessage(messageRef);
    }

    @Test
    public void escalationEvent() {
        Node node = createIntermediateEscalationEventThrowingNode();

        PropertyWriter propertyWriter = tested.escalationEvent(node);
        assertNotNull(propertyWriter);

        verifyCommonProperties(node);
        verify(throwEventPropertyWriter).addEscalation(escalationRef);
    }

    @Test
    public void compensationEvent() {
        Node node = createIntermediateCompensationEventThrowingNode();

        PropertyWriter propertyWriter = tested.compensationEvent(node);
        assertNotNull(propertyWriter);

        verifyCommonProperties(node);
        verify(throwEventPropertyWriter).addCompensation();
    }

    private void verifyCommonProperties(Node node) {
        verify(throwEventPropertyWriter).setAbsoluteBounds(node);
        verify(throwEventPropertyWriter).setName(NAME);
        verify(throwEventPropertyWriter).setDocumentation(DOCUMENTATION);
        verify(throwEventPropertyWriter).setAssignmentsInfo(assignmentsInfo);
    }

    private Node createIntermediateSignalEventThrowingNode() {
        ScopedSignalEventExecutionSet executionSet =
                new ScopedSignalEventExecutionSet(signalRef,
                                                  signalScope);

        IntermediateSignalEventThrowing eventThrowing =
                new IntermediateSignalEventThrowing(generalSet,
                                                    mock(BackgroundSet.class),
                                                    mock(FontSet.class),
                                                    mock(CircleDimensionSet.class),
                                                    dataIOSet,
                                                    advancedData,
                                                    executionSet);
        Node dockNode = mockNode();
        Node node = mockDockedNode(dockNode, eventThrowing);

        return node;
    }

    private Node createIntermediateLinkEventThrowingNode() {
        LinkEventExecutionSet executionSet =
                new LinkEventExecutionSet(linkRef);

        IntermediateLinkEventThrowing eventThrowing =
                new IntermediateLinkEventThrowing(generalSet,
                                                  mock(BackgroundSet.class),
                                                  mock(FontSet.class),
                                                  mock(CircleDimensionSet.class),
                                                  dataIOSet,
                                                  advancedData,
                                                  executionSet);
        Node dockNode = mockNode();
        Node node = mockDockedNode(dockNode, eventThrowing);
        return node;
    }

    private Node createIntermediateMessageEventThrowingNode() {
        MessageEventExecutionSet executionSet =
                new MessageEventExecutionSet(messageRef);

        IntermediateMessageEventThrowing eventThrowing =
                new IntermediateMessageEventThrowing(generalSet,
                                                     mock(BackgroundSet.class),
                                                     mock(FontSet.class),
                                                     mock(CircleDimensionSet.class),
                                                     dataIOSet,
                                                     advancedData,
                                                     executionSet);
        Node dockNode = mockNode();
        Node node = mockDockedNode(dockNode, eventThrowing);
        return node;
    }

    private Node createIntermediateEscalationEventThrowingNode() {
        EscalationEventExecutionSet executionSet =
                new EscalationEventExecutionSet(escalationRef);

        IntermediateEscalationEventThrowing eventThrowing =
                new IntermediateEscalationEventThrowing(generalSet,
                                                        mock(BackgroundSet.class),
                                                        mock(FontSet.class),
                                                        mock(CircleDimensionSet.class),
                                                        dataIOSet,
                                                        advancedData,
                                                        executionSet);
        Node dockNode = mockNode();
        Node node = mockDockedNode(dockNode, eventThrowing);
        return node;
    }

    private Node createIntermediateCompensationEventThrowingNode() {
        CompensationEventExecutionSet executionSet =
                new CompensationEventExecutionSet();

        IntermediateCompensationEventThrowing eventThrowing =
                new IntermediateCompensationEventThrowing(generalSet,
                                                          mock(BackgroundSet.class),
                                                          mock(FontSet.class),
                                                          mock(CircleDimensionSet.class),
                                                          dataIOSet,
                                                          advancedData,
                                                          executionSet);
        Node dockNode = mockNode();
        Node node = mockDockedNode(dockNode, eventThrowing);
        return node;
    }

    private static Node<View<BaseThrowingIntermediateEvent>, Edge> mockNode() {
        Node<View<BaseThrowingIntermediateEvent>, Edge> node = mock(Node.class);
        View view = mock(View.class);

        Bound ul = new Bound(0d, 100d);
        Bound lr = new Bound(200d, 0d);
        Bounds bounds = new Bounds(ul, lr);

        when(node.getContent()).thenReturn(view);

        when(view.getBounds()).thenReturn(bounds);

        return node;
    }

    private static Node<View<BaseThrowingIntermediateEvent>, Edge> mockDockedNode(Node dockSourceNode,
                                                                                  BaseThrowingIntermediateEvent event) {
        Dock dockContent = mock(Dock.class);
        Edge edge = mock(Edge.class);
        List<Edge> inEdges = Collections.singletonList(edge);
        Node<View<BaseThrowingIntermediateEvent>, Edge> node = mock(Node.class);
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