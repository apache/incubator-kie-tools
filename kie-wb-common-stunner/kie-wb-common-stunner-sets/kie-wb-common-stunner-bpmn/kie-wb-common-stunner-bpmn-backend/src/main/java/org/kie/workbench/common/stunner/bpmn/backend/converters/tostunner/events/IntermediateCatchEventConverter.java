/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

import java.util.List;

import org.eclipse.bpmn2.BoundaryEvent;
import org.eclipse.bpmn2.CatchEvent;
import org.eclipse.bpmn2.CompensateEventDefinition;
import org.eclipse.bpmn2.ConditionalEventDefinition;
import org.eclipse.bpmn2.ErrorEventDefinition;
import org.eclipse.bpmn2.EscalationEventDefinition;
import org.eclipse.bpmn2.EventDefinition;
import org.eclipse.bpmn2.IntermediateCatchEvent;
import org.eclipse.bpmn2.MessageEventDefinition;
import org.eclipse.bpmn2.SignalEventDefinition;
import org.eclipse.bpmn2.TimerEventDefinition;
import org.kie.workbench.common.stunner.bpmn.backend.converters.Match;
import org.kie.workbench.common.stunner.bpmn.backend.converters.TypedFactoryManager;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.BpmnNode;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.properties.CatchEventPropertyReader;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.properties.EventDefinitionReader;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.properties.PropertyReaderFactory;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateCompensationEvent;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateConditionalEvent;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateErrorEventCatching;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateEscalationEvent;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateMessageEventCatching;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateSignalEventCatching;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateTimerEvent;
import org.kie.workbench.common.stunner.bpmn.definition.property.dataio.DataIOSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.CancelActivity;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.conditional.CancellingConditionalEventExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.error.CancellingErrorEventExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.error.ErrorRef;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.escalation.CancellingEscalationEventExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.escalation.EscalationRef;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.message.CancellingMessageEventExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.message.MessageRef;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.signal.CancellingSignalEventExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.signal.SignalRef;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.timer.CancellingTimerEventExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.timer.TimerSettings;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.BPMNGeneralSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.Documentation;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.Name;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

public class IntermediateCatchEventConverter {

    private final TypedFactoryManager factoryManager;
    private final PropertyReaderFactory propertyReaderFactory;

    public IntermediateCatchEventConverter(TypedFactoryManager factoryManager, PropertyReaderFactory propertyReaderFactory) {
        this.factoryManager = factoryManager;
        this.propertyReaderFactory = propertyReaderFactory;
    }

    public BpmnNode convertIntermediateCatchEvent(IntermediateCatchEvent event) {
        CatchEventPropertyReader p = propertyReaderFactory.of(event);
        List<EventDefinition> eventDefinitions = p.getEventDefinitions();
        switch (eventDefinitions.size()) {
            case 0:
                throw new UnsupportedOperationException("An intermediate catch event should contain exactly one definition");
            case 1:
                return Match.of(EventDefinition.class, BpmnNode.class)
                        .when(TimerEventDefinition.class, e -> timerEvent(event, e))
                        .when(SignalEventDefinition.class, e -> signalEvent(event, e))
                        .when(MessageEventDefinition.class, e -> messageEvent(event, e))
                        .when(ErrorEventDefinition.class, e -> errorEvent(event, e))
                        .when(ConditionalEventDefinition.class, e -> conditionalEvent(event, e))
                        .when(EscalationEventDefinition.class, e -> escalationEvent(event, e))
                        .when(CompensateEventDefinition.class, e -> compensationEvent(event, e))
                        .apply(eventDefinitions.get(0)).value();
            default:
                throw new UnsupportedOperationException("Multiple definitions not supported for intermediate catch event");
        }
    }

    public BpmnNode convertBoundaryEvent(BoundaryEvent event) {
        CatchEventPropertyReader p = propertyReaderFactory.of(event);
        List<EventDefinition> eventDefinitions = p.getEventDefinitions();
        switch (eventDefinitions.size()) {
            case 0:
                throw new UnsupportedOperationException("A boundary event should contain exactly one definition");
            case 1:
                return Match.of(EventDefinition.class, BpmnNode.class)
                        .when(SignalEventDefinition.class, e -> signalEvent(event, e))
                        .when(TimerEventDefinition.class, e -> timerEvent(event, e))
                        .when(MessageEventDefinition.class, e -> messageEvent(event, e))
                        .when(ErrorEventDefinition.class, e -> errorEvent(event, e))
                        .when(ConditionalEventDefinition.class, e -> conditionalEvent(event, e))
                        .when(EscalationEventDefinition.class, e -> escalationEvent(event, e))
                        .when(CompensateEventDefinition.class, e -> compensationEvent(event, e))
                        .apply(eventDefinitions.get(0)).value()
                        .docked();
            default:
                throw new UnsupportedOperationException("Multiple definitions not supported for boundary event");
        }
    }

    private BpmnNode errorEvent(CatchEvent event, ErrorEventDefinition e) {
        String nodeId = event.getId();
        Node<View<IntermediateErrorEventCatching>, Edge> node = factoryManager.newNode(nodeId, IntermediateErrorEventCatching.class);

        IntermediateErrorEventCatching definition = node.getContent().getDefinition();
        CatchEventPropertyReader p = propertyReaderFactory.of(event);

        definition.setGeneral(new BPMNGeneralSet(
                new Name(p.getName()),
                new Documentation(p.getDocumentation())
        ));

        definition.setDataIOSet(
                new DataIOSet(
                        p.getAssignmentsInfo()));

        definition.setExecutionSet(new CancellingErrorEventExecutionSet(
                new CancelActivity(p.isCancelActivity()),
                new ErrorRef(EventDefinitionReader.errorRefOf(e))
        ));

        node.getContent().setBounds(p.getBounds());

        definition.setDimensionsSet(p.getCircleDimensionSet());
        definition.setFontSet(p.getFontSet());
        definition.setBackgroundSet(p.getBackgroundSet());

        return BpmnNode.of(node);
    }

    private BpmnNode signalEvent(CatchEvent event, SignalEventDefinition e) {
        String nodeId = event.getId();
        Node<View<IntermediateSignalEventCatching>, Edge> node = factoryManager.newNode(nodeId, IntermediateSignalEventCatching.class);

        IntermediateSignalEventCatching definition = node.getContent().getDefinition();
        CatchEventPropertyReader p = propertyReaderFactory.of(event);

        definition.setGeneral(new BPMNGeneralSet(
                new Name(p.getName()),
                new Documentation(p.getDocumentation())
        ));

        definition.setDataIOSet(new DataIOSet(
                p.getAssignmentsInfo()
        ));

        definition.setExecutionSet(new CancellingSignalEventExecutionSet(
                new CancelActivity(p.isCancelActivity()),
                new SignalRef(p.getSignalRef())
        ));

        node.getContent().setBounds(p.getBounds());

        definition.setDimensionsSet(p.getCircleDimensionSet());
        definition.setFontSet(p.getFontSet());
        definition.setBackgroundSet(p.getBackgroundSet());

        return BpmnNode.of(node);
    }

    private BpmnNode timerEvent(CatchEvent event, TimerEventDefinition e) {
        String nodeId = event.getId();
        Node<View<IntermediateTimerEvent>, Edge> node = factoryManager.newNode(nodeId, IntermediateTimerEvent.class);

        IntermediateTimerEvent definition = node.getContent().getDefinition();
        CatchEventPropertyReader p = propertyReaderFactory.of(event);

        definition.setGeneral(new BPMNGeneralSet(
                new Name(p.getName()),
                new Documentation(p.getDocumentation())
        ));

        definition.setExecutionSet(new CancellingTimerEventExecutionSet(
                new CancelActivity(p.isCancelActivity()),
                new TimerSettings(p.getTimerSettings(e))
        ));

        node.getContent().setBounds(p.getBounds());

        definition.setDimensionsSet(p.getCircleDimensionSet());
        definition.setFontSet(p.getFontSet());
        definition.setBackgroundSet(p.getBackgroundSet());

        return BpmnNode.of(node);
    }

    private BpmnNode messageEvent(CatchEvent event, MessageEventDefinition e) {
        String nodeId = event.getId();
        Node<View<IntermediateMessageEventCatching>, Edge> node = factoryManager.newNode(nodeId, IntermediateMessageEventCatching.class);

        IntermediateMessageEventCatching definition = node.getContent().getDefinition();
        CatchEventPropertyReader p = propertyReaderFactory.of(event);

        definition.setGeneral(new BPMNGeneralSet(
                new Name(p.getName()),
                new Documentation(p.getDocumentation())
        ));

        definition.setDataIOSet(new DataIOSet(
                p.getAssignmentsInfo()
        ));

        definition.setExecutionSet(new CancellingMessageEventExecutionSet(
                new CancelActivity(p.isCancelActivity()),
                new MessageRef(EventDefinitionReader.messageRefOf(e))
        ));

        node.getContent().setBounds(p.getBounds());

        definition.setDimensionsSet(p.getCircleDimensionSet());
        definition.setFontSet(p.getFontSet());
        definition.setBackgroundSet(p.getBackgroundSet());

        return BpmnNode.of(node);
    }

    private BpmnNode conditionalEvent(CatchEvent event, ConditionalEventDefinition e) {
        String nodeId = event.getId();
        Node<View<IntermediateConditionalEvent>, Edge> node = factoryManager.newNode(nodeId, IntermediateConditionalEvent.class);

        IntermediateConditionalEvent definition = node.getContent().getDefinition();
        CatchEventPropertyReader p = propertyReaderFactory.of(event);

        definition.setGeneral(new BPMNGeneralSet(
                new Name(p.getName()),
                new Documentation(p.getDocumentation())
        ));

        definition.setExecutionSet(new CancellingConditionalEventExecutionSet(
                new CancelActivity(p.isCancelActivity()),
                p.getConditionExpression(e)
        ));

        node.getContent().setBounds(p.getBounds());

        definition.setDimensionsSet(p.getCircleDimensionSet());
        definition.setFontSet(p.getFontSet());
        definition.setBackgroundSet(p.getBackgroundSet());

        return BpmnNode.of(node);
    }

    private BpmnNode escalationEvent(CatchEvent event,
                                     EscalationEventDefinition e) {
        String nodeId = event.getId();
        Node<View<IntermediateEscalationEvent>, Edge> node = factoryManager.newNode(nodeId,
                                                                                    IntermediateEscalationEvent.class);

        IntermediateEscalationEvent definition = node.getContent().getDefinition();
        CatchEventPropertyReader p = propertyReaderFactory.of(event);

        definition.setGeneral(new BPMNGeneralSet(
                new Name(p.getName()),
                new Documentation(p.getDocumentation())
        ));

        definition.setDataIOSet(
                new DataIOSet(p.getAssignmentsInfo()));

        definition.setExecutionSet(new CancellingEscalationEventExecutionSet(
                new CancelActivity(p.isCancelActivity()),
                new EscalationRef(EventDefinitionReader.escalationRefOf(e))
        ));

        node.getContent().setBounds(p.getBounds());

        definition.setDimensionsSet(p.getCircleDimensionSet());
        definition.setFontSet(p.getFontSet());
        definition.setBackgroundSet(p.getBackgroundSet());

        return BpmnNode.of(node);
    }

    private BpmnNode compensationEvent(CatchEvent event,
                                       CompensateEventDefinition e) {
        String nodeId = event.getId();
        Node<View<IntermediateCompensationEvent>, Edge> node = factoryManager.newNode(nodeId,
                                                                                      IntermediateCompensationEvent.class);

        IntermediateCompensationEvent definition = node.getContent().getDefinition();
        CatchEventPropertyReader p = propertyReaderFactory.of(event);

        definition.setGeneral(new BPMNGeneralSet(
                new Name(p.getName()),
                new Documentation(p.getDocumentation())
        ));

        node.getContent().setBounds(p.getBounds());

        definition.setDimensionsSet(p.getCircleDimensionSet());
        definition.setFontSet(p.getFontSet());
        definition.setBackgroundSet(p.getBackgroundSet());

        return BpmnNode.of(node);
    }

}
