/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.events;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.bpmn2.BoundaryEvent;
import org.eclipse.bpmn2.CatchEvent;
import org.eclipse.bpmn2.CompensateEventDefinition;
import org.eclipse.bpmn2.ConditionalEventDefinition;
import org.eclipse.bpmn2.ErrorEventDefinition;
import org.eclipse.bpmn2.EscalationEventDefinition;
import org.eclipse.bpmn2.EventDefinition;
import org.eclipse.bpmn2.IntermediateCatchEvent;
import org.eclipse.bpmn2.LinkEventDefinition;
import org.eclipse.bpmn2.MessageEventDefinition;
import org.eclipse.bpmn2.SignalEventDefinition;
import org.eclipse.bpmn2.TimerEventDefinition;
import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.stunner.bpmn.backend.converters.TypedFactoryManager;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.properties.CatchEventPropertyReader;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.properties.PropertyReaderFactory;
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
import org.kie.workbench.common.stunner.bpmn.definition.property.dataio.DataIOSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.dimensions.CircleDimensionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.BaseCancellingEventExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.conditional.CancellingConditionalEventExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.error.CancellingErrorEventExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.escalation.CancellingEscalationEventExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.link.LinkEventExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.message.CancellingMessageEventExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.signal.CancellingSignalEventExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.timer.CancellingTimerEventExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.font.FontSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.BPMNGeneralSet;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.marshaller.MarshallingRequest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class IntermediateCatchEventConverterTest {

    private static final String UUID = "CATCH EVENT UUID";

    private TypedFactoryManager factoryManager;
    private PropertyReaderFactory propertyReaderFactory;

    private CatchEventPropertyReader propertyReader;
    private View view;
    private List<EventDefinition> eventDefinitions;

    private IntermediateCatchEventConverter tested;

    @Before
    public void setUp() {
        factoryManager = mock(TypedFactoryManager.class);
        propertyReaderFactory = mock(PropertyReaderFactory.class);
        propertyReader = mock(CatchEventPropertyReader.class);
        eventDefinitions = new ArrayList<>();
        when(propertyReader.getEventDefinitions()).thenReturn(eventDefinitions);
        when(propertyReaderFactory.of(any(CatchEvent.class))).thenReturn(propertyReader);

        view = mock(View.class);

        tested = spy(new IntermediateCatchEventConverter(factoryManager,
                                                         propertyReaderFactory,
                                                         MarshallingRequest.Mode.IGNORE));
    }

    @Test
    public void constructor() {
        assertEquals(factoryManager, tested.factoryManager);
        assertEquals(propertyReaderFactory, tested.propertyReaderFactory);
    }

    @Test
    public void convert() {
        eventDefinitions.clear();
        try {
            tested.convert(any(IntermediateCatchEvent.class));
            fail("Exception expected");
        } catch (Exception exception) {
            assertTrue(exception instanceof UnsupportedOperationException);
            assertEquals(IntermediateCatchEventConverter.NO_DEFINITION, exception.getMessage());
        }

        verifyErrorEventConvert();
        verifySignalEventConvert();
        verifyLinkEventConvert();
        verifyTimerEventConvert();
        verifyMessageEventConvert();
        verifyConditionalEventConvert();
        verifyEscalationEventConvert();
        verifyCompensationEventConvert();

        eventDefinitions.clear();
        eventDefinitions.add(mock(EventDefinition.class));
        eventDefinitions.add(mock(EventDefinition.class));
        try {
            tested.convert(any(IntermediateCatchEvent.class));
            fail("Exception expected");
        } catch (Exception exception) {
            assertTrue(exception instanceof UnsupportedOperationException);
            assertEquals(IntermediateCatchEventConverter.MULTIPLE_DEFINITIONS, exception.getMessage());
        }

        verify(propertyReader, times(10)).getEventDefinitions();
    }

    @Test
    public void convertBoundaryEvent() {
        eventDefinitions.clear();
        try {
            tested.convertBoundaryEvent(any(BoundaryEvent.class));
            fail("Exception expected");
        } catch (Exception exception) {
            assertTrue(exception instanceof UnsupportedOperationException);
            assertEquals(IntermediateCatchEventConverter.BOUNDARY_NO_DEFINITION, exception.getMessage());
        }

        verifyBoundaryErrorEventConvert();
        verifyBoundarySignalEventConvert();
        verifyBoundaryTimerEventConvert();
        verifyBoundaryMessageEventConvert();
        verifyBoundaryConditionalEventConvert();
        verifyBoundaryEscalationEventConvert();
        verifyBoundaryCompensationEventConvert();

        eventDefinitions.clear();
        eventDefinitions.add(mock(EventDefinition.class));
        eventDefinitions.add(mock(EventDefinition.class));
        try {
            tested.convertBoundaryEvent(any(BoundaryEvent.class));
            fail("Exception expected");
        } catch (Exception exception) {
            assertTrue(exception instanceof UnsupportedOperationException);
            assertEquals(IntermediateCatchEventConverter.BOUNDARY_MULTIPLE_DEFINITIONS, exception.getMessage());
        }

        verify(propertyReader, times(9)).getEventDefinitions();
    }

    @Test
    public void errorEvent() {
        IntermediateErrorEventCatching definition = mock(IntermediateErrorEventCatching.class);
        ErrorEventDefinition eventDefinition = mock(ErrorEventDefinition.class);
        CatchEvent catchEvent = mockIntermediateCatchEvent(definition);

        tested.errorEvent(catchEvent, eventDefinition);

        verifyCommonProperties(definition);
        verify(propertyReader).isCancelActivity();
        verify(propertyReader).getSlaDueDate();
        verify(eventDefinition).getErrorRef();
        verify(definition).setExecutionSet(any(CancellingErrorEventExecutionSet.class));
    }

    @Test
    public void signalEvent() {
        IntermediateSignalEventCatching definition = mock(IntermediateSignalEventCatching.class);
        SignalEventDefinition eventDefinition = mock(SignalEventDefinition.class);
        CatchEvent catchEvent = mockIntermediateCatchEvent(definition);

        tested.signalEvent(catchEvent);

        verifyCommonProperties(definition);
        verify(propertyReader).isCancelActivity();
        verify(propertyReader).getSlaDueDate();
        verify(propertyReader).getSignalRef();
        verify(definition).setExecutionSet(any(CancellingSignalEventExecutionSet.class));
    }

    @Test
    public void linkEvent() {
        IntermediateLinkEventCatching definition = mock(IntermediateLinkEventCatching.class);
        LinkEventDefinition eventDefinition = mock(LinkEventDefinition.class);
        CatchEvent catchEvent = mockIntermediateCatchEvent(definition);

        tested.linkEvent(catchEvent);

        verifyCommonProperties(definition);
        verify(propertyReader).getLinkRef();
        verify(definition).setExecutionSet(any(LinkEventExecutionSet.class));
    }

    @Test
    public void timerEvent() {
        IntermediateTimerEvent definition = mock(IntermediateTimerEvent.class);
        TimerEventDefinition eventDefinition = mock(TimerEventDefinition.class);
        CatchEvent catchEvent = mockIntermediateCatchEvent(definition);

        tested.timerEvent(catchEvent, eventDefinition);

        verifyCommonProperties(definition);
        verify(propertyReader).isCancelActivity();
        verify(propertyReader).getSlaDueDate();
        verify(eventDefinition).getTimeCycle();
        verify(definition).setExecutionSet(any(CancellingTimerEventExecutionSet.class));
    }

    @Test
    public void messageEvent() {
        IntermediateMessageEventCatching definition = mock(IntermediateMessageEventCatching.class);
        MessageEventDefinition eventDefinition = mock(MessageEventDefinition.class);
        CatchEvent catchEvent = mockIntermediateCatchEvent(definition);

        tested.messageEvent(catchEvent, eventDefinition);

        verifyCommonProperties(definition);
        verify(propertyReader).isCancelActivity();
        verify(propertyReader).getSlaDueDate();
        verify(eventDefinition, times(2)).getMessageRef();
        verify(definition).setExecutionSet(any(CancellingMessageEventExecutionSet.class));
    }

    @Test
    public void conditionalEvent() {
        IntermediateConditionalEvent definition = mock(IntermediateConditionalEvent.class);
        ConditionalEventDefinition eventDefinition = mock(ConditionalEventDefinition.class);
        CatchEvent catchEvent = mockIntermediateCatchEvent(definition);

        tested.conditionalEvent(catchEvent, eventDefinition);

        verifyCommonProperties(definition);
        verify(propertyReader).isCancelActivity();
        verify(propertyReader).getSlaDueDate();
        verify(eventDefinition).getCondition();
        verify(definition).setExecutionSet(any(CancellingConditionalEventExecutionSet.class));
    }

    @Test
    public void escalationEvent() {
        IntermediateEscalationEvent definition = mock(IntermediateEscalationEvent.class);
        EscalationEventDefinition eventDefinition = mock(EscalationEventDefinition.class);
        IntermediateCatchEvent catchEvent = mockIntermediateCatchEvent(definition);

        tested.escalationEvent(catchEvent, eventDefinition);

        verifyCommonProperties(definition);
        verify(propertyReader).isCancelActivity();
        verify(propertyReader).getSlaDueDate();
        verify(eventDefinition).getEscalationRef();
        verify(definition).setExecutionSet(any(CancellingEscalationEventExecutionSet.class));
    }

    @Test
    public void compensationEvent() {
        IntermediateCompensationEvent definition = mock(IntermediateCompensationEvent.class);
        CompensateEventDefinition eventDefinition = mock(CompensateEventDefinition.class);
        CatchEvent catchEvent = mockIntermediateCatchEvent(definition);

        tested.compensationEvent(catchEvent);

        verifyCommonProperties(definition);
        verify(propertyReader).getSlaDueDate();
        verify(definition).setExecutionSet(any(BaseCancellingEventExecutionSet.class));
    }

    private void verifyErrorEventConvert() {
        IntermediateErrorEventCatching definition = mock(IntermediateErrorEventCatching.class);
        ErrorEventDefinition eventDefinition = mock(ErrorEventDefinition.class);
        IntermediateCatchEvent intermediateCatchEvent = mockIntermediateCatchEvent(definition);
        eventDefinitions.clear();
        eventDefinitions.add(eventDefinition);
        tested.convert(intermediateCatchEvent);
        verify(tested).errorEvent(intermediateCatchEvent, eventDefinition);
    }

    private void verifySignalEventConvert() {
        IntermediateSignalEventCatching definition = mock(IntermediateSignalEventCatching.class);
        SignalEventDefinition eventDefinition = mock(SignalEventDefinition.class);
        IntermediateCatchEvent intermediateCatchEvent = mockIntermediateCatchEvent(definition);
        eventDefinitions.clear();
        eventDefinitions.add(eventDefinition);
        tested.convert(intermediateCatchEvent);
        verify(tested).signalEvent(intermediateCatchEvent);
    }

    private void verifyLinkEventConvert() {
        IntermediateLinkEventCatching definition = mock(IntermediateLinkEventCatching.class);
        LinkEventDefinition eventDefinition = mock(LinkEventDefinition.class);
        IntermediateCatchEvent intermediateCatchEvent = mockIntermediateCatchEvent(definition);
        eventDefinitions.clear();
        eventDefinitions.add(eventDefinition);
        tested.convert(intermediateCatchEvent);
        verify(tested).linkEvent(intermediateCatchEvent);
    }

    private void verifyTimerEventConvert() {
        IntermediateTimerEvent definition = mock(IntermediateTimerEvent.class);
        TimerEventDefinition eventDefinition = mock(TimerEventDefinition.class);
        IntermediateCatchEvent intermediateCatchEvent = mockIntermediateCatchEvent(definition);
        eventDefinitions.clear();
        eventDefinitions.add(eventDefinition);
        tested.convert(intermediateCatchEvent);
        verify(tested).timerEvent(intermediateCatchEvent, eventDefinition);
    }

    private void verifyMessageEventConvert() {
        IntermediateMessageEventCatching definition = mock(IntermediateMessageEventCatching.class);
        MessageEventDefinition eventDefinition = mock(MessageEventDefinition.class);
        IntermediateCatchEvent intermediateCatchEvent = mockIntermediateCatchEvent(definition);
        eventDefinitions.clear();
        eventDefinitions.add(eventDefinition);
        tested.convert(intermediateCatchEvent);
        verify(tested).messageEvent(intermediateCatchEvent, eventDefinition);
    }

    private void verifyConditionalEventConvert() {
        IntermediateConditionalEvent definition = mock(IntermediateConditionalEvent.class);
        ConditionalEventDefinition eventDefinition = mock(ConditionalEventDefinition.class);
        IntermediateCatchEvent intermediateCatchEvent = mockIntermediateCatchEvent(definition);
        eventDefinitions.clear();
        eventDefinitions.add(eventDefinition);
        tested.convert(intermediateCatchEvent);
        verify(tested).conditionalEvent(intermediateCatchEvent, eventDefinition);
    }

    private void verifyEscalationEventConvert() {
        IntermediateEscalationEvent definition = mock(IntermediateEscalationEvent.class);
        EscalationEventDefinition eventDefinition = mock(EscalationEventDefinition.class);
        IntermediateCatchEvent intermediateCatchEvent = mockIntermediateCatchEvent(definition);
        eventDefinitions.clear();
        eventDefinitions.add(eventDefinition);
        tested.convert(intermediateCatchEvent);
        verify(tested).escalationEvent(intermediateCatchEvent, eventDefinition);
    }

    private void verifyCompensationEventConvert() {
        IntermediateCompensationEvent definition = mock(IntermediateCompensationEvent.class);
        CompensateEventDefinition eventDefinition = mock(CompensateEventDefinition.class);
        IntermediateCatchEvent intermediateCatchEvent = mockIntermediateCatchEvent(definition);
        eventDefinitions.clear();
        eventDefinitions.add(eventDefinition);
        tested.convert(intermediateCatchEvent);
        verify(tested).compensationEvent(intermediateCatchEvent);
    }

    private void verifyBoundaryErrorEventConvert() {
        IntermediateErrorEventCatching definition = mock(IntermediateErrorEventCatching.class);
        ErrorEventDefinition eventDefinition = mock(ErrorEventDefinition.class);
        BoundaryEvent boundaryEvent = mockBoundaryEvent(definition);
        eventDefinitions.clear();
        eventDefinitions.add(eventDefinition);
        tested.convertBoundaryEvent(boundaryEvent);
        verify(tested).errorEvent(boundaryEvent, eventDefinition);
    }

    private void verifyBoundarySignalEventConvert() {
        IntermediateSignalEventCatching definition = mock(IntermediateSignalEventCatching.class);
        SignalEventDefinition eventDefinition = mock(SignalEventDefinition.class);
        BoundaryEvent boundaryEvent = mockBoundaryEvent(definition);
        eventDefinitions.clear();
        eventDefinitions.add(eventDefinition);
        tested.convertBoundaryEvent(boundaryEvent);
        verify(tested).signalEvent(boundaryEvent);
    }

    private void verifyBoundaryTimerEventConvert() {
        IntermediateTimerEvent definition = mock(IntermediateTimerEvent.class);
        TimerEventDefinition eventDefinition = mock(TimerEventDefinition.class);
        BoundaryEvent boundaryEvent = mockBoundaryEvent(definition);
        eventDefinitions.clear();
        eventDefinitions.add(eventDefinition);
        tested.convertBoundaryEvent(boundaryEvent);
        verify(tested).timerEvent(boundaryEvent, eventDefinition);
    }

    private void verifyBoundaryMessageEventConvert() {
        IntermediateMessageEventCatching definition = mock(IntermediateMessageEventCatching.class);
        MessageEventDefinition eventDefinition = mock(MessageEventDefinition.class);
        BoundaryEvent boundaryEvent = mockBoundaryEvent(definition);
        eventDefinitions.clear();
        eventDefinitions.add(eventDefinition);
        tested.convertBoundaryEvent(boundaryEvent);
        verify(tested).messageEvent(boundaryEvent, eventDefinition);
    }

    private void verifyBoundaryConditionalEventConvert() {
        IntermediateConditionalEvent definition = mock(IntermediateConditionalEvent.class);
        ConditionalEventDefinition eventDefinition = mock(ConditionalEventDefinition.class);
        BoundaryEvent boundaryEvent = mockBoundaryEvent(definition);
        eventDefinitions.clear();
        eventDefinitions.add(eventDefinition);
        tested.convertBoundaryEvent(boundaryEvent);
        verify(tested).conditionalEvent(boundaryEvent, eventDefinition);
    }

    private void verifyBoundaryEscalationEventConvert() {
        IntermediateEscalationEvent definition = mock(IntermediateEscalationEvent.class);
        EscalationEventDefinition eventDefinition = mock(EscalationEventDefinition.class);
        BoundaryEvent boundaryEvent = mockBoundaryEvent(definition);
        eventDefinitions.clear();
        eventDefinitions.add(eventDefinition);
        tested.convertBoundaryEvent(boundaryEvent);
        verify(tested).escalationEvent(boundaryEvent, eventDefinition);
    }

    private void verifyBoundaryCompensationEventConvert() {
        IntermediateCompensationEvent definition = mock(IntermediateCompensationEvent.class);
        CompensateEventDefinition eventDefinition = mock(CompensateEventDefinition.class);
        BoundaryEvent boundaryEvent = mockBoundaryEvent(definition);
        eventDefinitions.clear();
        eventDefinitions.add(eventDefinition);
        tested.convertBoundaryEvent(boundaryEvent);
        verify(tested).compensationEvent(boundaryEvent);
    }

    private void verifyCommonProperties(BaseCatchingIntermediateEvent definition) {
        verify(propertyReader).getBounds();
        verify(view).setBounds(any(Bounds.class));

        verify(propertyReader).getName();
        verify(propertyReader).getDocumentation();
        verify(definition).setGeneral(any(BPMNGeneralSet.class));

        verify(propertyReader).getBackgroundSet();
        verify(definition).setBackgroundSet(any(BackgroundSet.class));

        verify(propertyReader).getFontSet();
        verify(definition).setFontSet(any(FontSet.class));

        verify(propertyReader).getCircleDimensionSet();
        verify(definition).setDimensionsSet(any(CircleDimensionSet.class));

        verify(propertyReader).getAssignmentsInfo();
        verify(definition).setDataIOSet(any(DataIOSet.class));
    }

    private IntermediateCatchEvent mockIntermediateCatchEvent(BaseCatchingIntermediateEvent eventDefinition) {
        IntermediateCatchEvent intermediateCatchEvent = mock(IntermediateCatchEvent.class);
        when(intermediateCatchEvent.getId()).thenReturn(UUID);

        Node node = mock(Node.class);
        when(factoryManager.newNode(eq(UUID), any())).thenReturn(node);
        when(node.getContent()).thenReturn(view);
        when(view.getDefinition()).thenReturn(eventDefinition);

        return intermediateCatchEvent;
    }

    private BoundaryEvent mockBoundaryEvent(BaseCatchingIntermediateEvent eventDefinition) {
        BoundaryEvent boundaryEvent = mock(BoundaryEvent.class);
        when(boundaryEvent.getId()).thenReturn(UUID);

        Node node = mock(Node.class);
        when(factoryManager.newNode(eq(UUID), any())).thenReturn(node);
        when(node.getContent()).thenReturn(view);
        when(view.getDefinition()).thenReturn(eventDefinition);

        return boundaryEvent;
    }
}