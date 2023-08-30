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


package org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.events;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.bpmn2.CompensateEventDefinition;
import org.eclipse.bpmn2.EscalationEventDefinition;
import org.eclipse.bpmn2.EventDefinition;
import org.eclipse.bpmn2.IntermediateThrowEvent;
import org.eclipse.bpmn2.LinkEventDefinition;
import org.eclipse.bpmn2.MessageEventDefinition;
import org.eclipse.bpmn2.SignalEventDefinition;
import org.eclipse.bpmn2.ThrowEvent;
import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.stunner.bpmn.client.marshall.MarshallingRequest;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.TypedFactoryManager;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.properties.PropertyReaderFactory;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.properties.ThrowEventPropertyReader;
import org.kie.workbench.common.stunner.bpmn.definition.BaseThrowingIntermediateEvent;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateCompensationEventThrowing;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateEscalationEventThrowing;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateLinkEventThrowing;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateMessageEventThrowing;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateSignalEventThrowing;
import org.kie.workbench.common.stunner.bpmn.definition.property.background.BackgroundSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.dataio.DataIOSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.dimensions.CircleDimensionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.compensation.CompensationEventExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.escalation.EscalationEventExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.link.LinkEventExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.message.MessageEventExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.signal.ScopedSignalEventExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.font.FontSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.BPMNGeneralSet;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class IntermediateThrowEventConverterTest {

    private static final String UUID = "THROW EVENT UUID";

    private TypedFactoryManager factoryManager;
    private PropertyReaderFactory propertyReaderFactory;

    private ThrowEventPropertyReader propertyReader;
    private View view;
    private List<EventDefinition> eventDefinitions;

    private IntermediateThrowEventConverter tested;

    @Before
    public void setUp() {
        factoryManager = mock(TypedFactoryManager.class);
        propertyReaderFactory = mock(PropertyReaderFactory.class);
        propertyReader = mock(ThrowEventPropertyReader.class);
        eventDefinitions = new ArrayList<>();
        when(propertyReader.getEventDefinitions()).thenReturn(eventDefinitions);
        when(propertyReaderFactory.of(Mockito.<ThrowEvent>any())).thenReturn(propertyReader);

        view = mock(View.class);

        tested = spy(new IntermediateThrowEventConverter(factoryManager,
                                                         propertyReaderFactory,
                                                         MarshallingRequest.Mode.IGNORE));
    }

    @Test
    public void convert() {
        eventDefinitions.clear();
        try {
            tested.convert(Mockito.<IntermediateThrowEvent>any());
            fail("Exception expected");
        } catch (Exception exception) {
            assertTrue(exception instanceof UnsupportedOperationException);
            assertEquals(IntermediateThrowEventConverter.NO_DEFINITION, exception.getMessage());
        }

        verifyMessageEventConvert();
        verifySignalEventConvert();
        verifyLinkEventConvert();
        verifyEscalationEventConvert();
        verifyCompensationEventConvert();

        eventDefinitions.clear();
        eventDefinitions.add(mock(EventDefinition.class));
        eventDefinitions.add(mock(EventDefinition.class));
        try {
            tested.convert(Mockito.<IntermediateThrowEvent>any());
            fail("Exception expected");
        } catch (Exception exception) {
            assertTrue(exception instanceof UnsupportedOperationException);
            assertEquals(IntermediateThrowEventConverter.MULTIPLE_DEFINITIONS, exception.getMessage());
        }

        verify(propertyReader, times(7)).getEventDefinitions();
    }

    @Test
    public void constructor() {
        assertEquals(factoryManager, tested.factoryManager);
        assertEquals(propertyReaderFactory, tested.propertyReaderFactory);
    }

    @Test
    public void messageEvent() {
        IntermediateMessageEventThrowing definition = mock(IntermediateMessageEventThrowing.class);
        MessageEventDefinition eventDefinition = mock(MessageEventDefinition.class);
        IntermediateThrowEvent intermediateThrowEvent = mockIntermediateThrowEvent(definition);

        tested.messageEvent(intermediateThrowEvent, eventDefinition);

        verifyCommonProperties(definition);
        verify(eventDefinition, times(2)).getMessageRef();
        verify(definition).setExecutionSet(any(MessageEventExecutionSet.class));
    }

    @Test
    public void signalEvent() {
        IntermediateSignalEventThrowing definition = mock(IntermediateSignalEventThrowing.class);
        IntermediateThrowEvent intermediateThrowEvent = mockIntermediateThrowEvent(definition);

        tested.signalEvent(intermediateThrowEvent);

        verifyCommonProperties(definition);
        verify(propertyReader).getSignalRef();
        verify(propertyReader).getSignalScope();
        verify(definition).setExecutionSet(any(ScopedSignalEventExecutionSet.class));
    }

    @Test
    public void linkEvent() {
        IntermediateLinkEventThrowing definition = mock(IntermediateLinkEventThrowing.class);
        IntermediateThrowEvent intermediateThrowEvent = mockIntermediateThrowEvent(definition);

        tested.linkEvent(intermediateThrowEvent);

        verifyCommonProperties(definition);
        verify(propertyReader).getLinkRef();
        verify(definition).setExecutionSet(any(LinkEventExecutionSet.class));
    }

    @Test
    public void escalationEvent() {
        IntermediateEscalationEventThrowing definition = mock(IntermediateEscalationEventThrowing.class);
        EscalationEventDefinition eventDefinition = mock(EscalationEventDefinition.class);
        IntermediateThrowEvent intermediateThrowEvent = mockIntermediateThrowEvent(definition);

        tested.escalationEvent(intermediateThrowEvent, eventDefinition);

        verifyCommonProperties(definition);
        verify(eventDefinition).getEscalationRef();
        verify(definition).setExecutionSet(any(EscalationEventExecutionSet.class));
    }

    @Test
    public void compensationEvent() {
        IntermediateCompensationEventThrowing definition = mock(IntermediateCompensationEventThrowing.class);
        CompensateEventDefinition eventDefinition = mock(CompensateEventDefinition.class);
        IntermediateThrowEvent intermediateThrowEvent = mockIntermediateThrowEvent(definition);

        tested.compensationEvent(intermediateThrowEvent, eventDefinition);

        verifyCommonProperties(definition);
        verify(definition).setExecutionSet(any(CompensationEventExecutionSet.class));
    }

    private void verifyCommonProperties(BaseThrowingIntermediateEvent definition) {
        verify(propertyReader).getBounds();
        verify(view).setBounds(Mockito.<Bounds>any());

        verify(propertyReader).getName();
        verify(propertyReader).getDocumentation();
        verify(definition).setGeneral(Mockito.<BPMNGeneralSet>any());

        verify(propertyReader).getBackgroundSet();
        verify(definition).setBackgroundSet(Mockito.<BackgroundSet>any());

        verify(propertyReader).getFontSet();
        verify(definition).setFontSet(Mockito.<FontSet>any());

        verify(propertyReader).getCircleDimensionSet();
        verify(definition).setDimensionsSet(Mockito.<CircleDimensionSet>any());

        verify(propertyReader).getAssignmentsInfo();
        verify(definition).setDataIOSet(Mockito.<DataIOSet>any());
    }

    private void verifyMessageEventConvert() {
        IntermediateMessageEventThrowing definition = mock(IntermediateMessageEventThrowing.class);
        MessageEventDefinition eventDefinition = mock(MessageEventDefinition.class);
        IntermediateThrowEvent intermediateThrowEvent = mockIntermediateThrowEvent(definition);
        eventDefinitions.clear();
        eventDefinitions.add(eventDefinition);
        tested.convert(intermediateThrowEvent);
        verify(tested).messageEvent(intermediateThrowEvent, eventDefinition);
    }

    private void verifySignalEventConvert() {
        IntermediateSignalEventThrowing definition = mock(IntermediateSignalEventThrowing.class);
        SignalEventDefinition eventDefinition = mock(SignalEventDefinition.class);
        IntermediateThrowEvent intermediateThrowEvent = mockIntermediateThrowEvent(definition);
        eventDefinitions.clear();
        eventDefinitions.add(eventDefinition);
        tested.convert(intermediateThrowEvent);
        verify(tested).signalEvent(intermediateThrowEvent);
    }

    private void verifyLinkEventConvert() {
        IntermediateLinkEventThrowing definition = mock(IntermediateLinkEventThrowing.class);
        LinkEventDefinition eventDefinition = mock(LinkEventDefinition.class);
        IntermediateThrowEvent intermediateThrowEvent = mockIntermediateThrowEvent(definition);
        eventDefinitions.clear();
        eventDefinitions.add(eventDefinition);
        tested.convert(intermediateThrowEvent);
        verify(tested).linkEvent(intermediateThrowEvent);
    }

    private void verifyEscalationEventConvert() {
        IntermediateEscalationEventThrowing definition = mock(IntermediateEscalationEventThrowing.class);
        EscalationEventDefinition eventDefinition = mock(EscalationEventDefinition.class);
        IntermediateThrowEvent intermediateThrowEvent = mockIntermediateThrowEvent(definition);
        eventDefinitions.clear();
        eventDefinitions.add(eventDefinition);
        tested.convert(intermediateThrowEvent);
        verify(tested).escalationEvent(intermediateThrowEvent, eventDefinition);
    }

    private void verifyCompensationEventConvert() {
        IntermediateCompensationEventThrowing definition = mock(IntermediateCompensationEventThrowing.class);
        CompensateEventDefinition eventDefinition = mock(CompensateEventDefinition.class);
        IntermediateThrowEvent intermediateThrowEvent = mockIntermediateThrowEvent(definition);
        eventDefinitions.clear();
        eventDefinitions.add(eventDefinition);
        tested.convert(intermediateThrowEvent);
        verify(tested).compensationEvent(intermediateThrowEvent, eventDefinition);
    }

    private IntermediateThrowEvent mockIntermediateThrowEvent(BaseThrowingIntermediateEvent eventDefinition) {
        IntermediateThrowEvent intermediateThrowEvent = mock(IntermediateThrowEvent.class);
        when(intermediateThrowEvent.getId()).thenReturn(UUID);

        Node node = mock(Node.class);
        when(factoryManager.newNode(eq(UUID), any())).thenReturn(node);
        when(node.getContent()).thenReturn(view);
        when(view.getDefinition()).thenReturn(eventDefinition);

        return intermediateThrowEvent;
    }
}