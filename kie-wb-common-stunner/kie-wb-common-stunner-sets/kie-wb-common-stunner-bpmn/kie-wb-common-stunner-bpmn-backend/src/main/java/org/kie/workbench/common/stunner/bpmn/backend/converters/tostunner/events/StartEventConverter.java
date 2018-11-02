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

import org.eclipse.bpmn2.CompensateEventDefinition;
import org.eclipse.bpmn2.ConditionalEventDefinition;
import org.eclipse.bpmn2.ErrorEventDefinition;
import org.eclipse.bpmn2.EscalationEventDefinition;
import org.eclipse.bpmn2.EventDefinition;
import org.eclipse.bpmn2.MessageEventDefinition;
import org.eclipse.bpmn2.SignalEventDefinition;
import org.eclipse.bpmn2.StartEvent;
import org.eclipse.bpmn2.TimerEventDefinition;
import org.kie.workbench.common.stunner.bpmn.backend.converters.Match;
import org.kie.workbench.common.stunner.bpmn.backend.converters.TypedFactoryManager;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.BpmnNode;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.properties.CatchEventPropertyReader;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.properties.EventDefinitionReader;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.properties.EventPropertyReader;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.properties.PropertyReaderFactory;
import org.kie.workbench.common.stunner.bpmn.definition.StartCompensationEvent;
import org.kie.workbench.common.stunner.bpmn.definition.StartConditionalEvent;
import org.kie.workbench.common.stunner.bpmn.definition.StartErrorEvent;
import org.kie.workbench.common.stunner.bpmn.definition.StartEscalationEvent;
import org.kie.workbench.common.stunner.bpmn.definition.StartMessageEvent;
import org.kie.workbench.common.stunner.bpmn.definition.StartNoneEvent;
import org.kie.workbench.common.stunner.bpmn.definition.StartSignalEvent;
import org.kie.workbench.common.stunner.bpmn.definition.StartTimerEvent;
import org.kie.workbench.common.stunner.bpmn.definition.property.dataio.DataIOSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.IsInterrupting;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.conditional.InterruptingConditionalEventExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.error.ErrorRef;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.error.InterruptingErrorEventExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.escalation.EscalationRef;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.escalation.InterruptingEscalationEventExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.message.InterruptingMessageEventExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.message.MessageRef;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.signal.InterruptingSignalEventExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.signal.SignalRef;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.timer.InterruptingTimerEventExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.timer.TimerSettings;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.BPMNGeneralSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.Documentation;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.Name;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

public class StartEventConverter {

    private final TypedFactoryManager factoryManager;
    private final PropertyReaderFactory propertyReaderFactory;

    public StartEventConverter(TypedFactoryManager factoryManager, PropertyReaderFactory propertyReaderFactory) {
        this.factoryManager = factoryManager;
        this.propertyReaderFactory = propertyReaderFactory;
    }

    public BpmnNode convert(StartEvent event) {
        CatchEventPropertyReader p = propertyReaderFactory.of(event);
        List<EventDefinition> eventDefinitions = p.getEventDefinitions();
        switch (eventDefinitions.size()) {
            case 0:
                return noneEvent(event);
            case 1:
                return Match.of(EventDefinition.class, BpmnNode.class)
                        .when(SignalEventDefinition.class, e -> signalEvent(event, e))
                        .when(MessageEventDefinition.class, e -> messageEvent(event, e))
                        .when(TimerEventDefinition.class, e -> timerEvent(event, e))
                        .when(ErrorEventDefinition.class, e -> errorEvent(event, e))
                        .when(ConditionalEventDefinition.class, e -> conditionalEvent(event, e))
                        .when(EscalationEventDefinition.class, e -> escalationEvent(event, e))
                        .when(CompensateEventDefinition.class, e -> compensationEvent(event, e))
                        .apply(eventDefinitions.get(0)).value();
            default:
                throw new UnsupportedOperationException("Multiple event definitions not supported for start event");
        }
    }

    private BpmnNode errorEvent(
            StartEvent event,
            ErrorEventDefinition e) {
        Node<View<StartErrorEvent>, Edge> node =
                factoryManager.newNode(event.getId(), StartErrorEvent.class);

        StartErrorEvent definition = node.getContent().getDefinition();
        EventPropertyReader p = propertyReaderFactory.of(event);

        definition.setGeneral(new BPMNGeneralSet(
                new Name(p.getName()),
                new Documentation(p.getDocumentation())
        ));

        definition.setDataIOSet(new DataIOSet(
                p.getAssignmentsInfo()
        ));

        definition.setExecutionSet(new InterruptingErrorEventExecutionSet(
                new IsInterrupting(event.isIsInterrupting()),
                new ErrorRef(EventDefinitionReader.errorRefOf(e))
        ));

        definition.setSimulationSet(p.getSimulationSet());

        node.getContent().setBounds(p.getBounds());

        definition.setDimensionsSet(p.getCircleDimensionSet());
        definition.setFontSet(p.getFontSet());
        definition.setBackgroundSet(p.getBackgroundSet());

        return BpmnNode.of(node);
    }

    private BpmnNode timerEvent(
            StartEvent event,
            TimerEventDefinition e) {
        Node<View<StartTimerEvent>, Edge> node =
                factoryManager.newNode(event.getId(), StartTimerEvent.class);

        StartTimerEvent definition = node.getContent().getDefinition();
        EventPropertyReader p = propertyReaderFactory.of(event);

        definition.setGeneral(new BPMNGeneralSet(
                new Name(p.getName()),
                new Documentation(p.getDocumentation())
        ));

        definition.setExecutionSet(new InterruptingTimerEventExecutionSet(
                new IsInterrupting(event.isIsInterrupting()),
                new TimerSettings(p.getTimerSettings(e))
        ));

        definition.setSimulationSet(p.getSimulationSet());

        node.getContent().setBounds(p.getBounds());

        definition.setDimensionsSet(p.getCircleDimensionSet());
        definition.setFontSet(p.getFontSet());
        definition.setBackgroundSet(p.getBackgroundSet());

        return BpmnNode.of(node);
    }

    private BpmnNode messageEvent(
            StartEvent event,
            MessageEventDefinition e) {
        Node<View<StartMessageEvent>, Edge> node =
                factoryManager.newNode(event.getId(), StartMessageEvent.class);

        StartMessageEvent definition = node.getContent().getDefinition();
        EventPropertyReader p = propertyReaderFactory.of(event);

        definition.setGeneral(new BPMNGeneralSet(
                new Name(p.getName()),
                new Documentation(p.getDocumentation())
        ));

        definition.setDataIOSet(new DataIOSet(
                p.getAssignmentsInfo()
        ));

        definition.setExecutionSet(new InterruptingMessageEventExecutionSet(
                new IsInterrupting(event.isIsInterrupting()),
                new MessageRef(EventDefinitionReader.messageRefOf(e))
        ));

        definition.setSimulationSet(p.getSimulationSet());

        node.getContent().setBounds(p.getBounds());

        definition.setFontSet(p.getFontSet());
        definition.setBackgroundSet(p.getBackgroundSet());
        definition.setDimensionsSet(p.getCircleDimensionSet());

        return BpmnNode.of(node);
    }

    private BpmnNode signalEvent(
            StartEvent event,
            SignalEventDefinition e) {
        Node<View<StartSignalEvent>, Edge> node =
                factoryManager.newNode(event.getId(), StartSignalEvent.class);

        StartSignalEvent definition = node.getContent().getDefinition();
        EventPropertyReader p = propertyReaderFactory.of(event);

        definition.setGeneral(new BPMNGeneralSet(
                new Name(p.getName()),
                new Documentation(p.getDocumentation())
        ));

        definition.setDataIOSet(new DataIOSet(
                p.getAssignmentsInfo()
        ));

        definition.setExecutionSet(new InterruptingSignalEventExecutionSet(
                new IsInterrupting(event.isIsInterrupting()),
                new SignalRef(p.getSignalRef())
        ));

        definition.setSimulationSet(p.getSimulationSet());

        node.getContent().setBounds(p.getBounds());

        definition.setDimensionsSet(p.getCircleDimensionSet());
        definition.setFontSet(p.getFontSet());
        definition.setBackgroundSet(p.getBackgroundSet());

        return BpmnNode.of(node);
    }

    private BpmnNode noneEvent(StartEvent event) {
        Node<View<StartNoneEvent>, Edge> node =
                factoryManager.newNode(event.getId(), StartNoneEvent.class);
        StartNoneEvent definition = node.getContent().getDefinition();
        EventPropertyReader p = propertyReaderFactory.of(event);

        definition.setGeneral(new BPMNGeneralSet(
                new Name(p.getName()),
                new Documentation(p.getDocumentation())
        ));

        definition.setSimulationSet(p.getSimulationSet());
        definition.setIsInterrupting(new IsInterrupting(event.isIsInterrupting()));

        node.getContent().setBounds(p.getBounds());

        definition.setDimensionsSet(p.getCircleDimensionSet());
        definition.setFontSet(p.getFontSet());
        definition.setBackgroundSet(p.getBackgroundSet());

        return BpmnNode.of(node);
    }

    private BpmnNode conditionalEvent(
            StartEvent event,
            ConditionalEventDefinition e) {
        Node<View<StartConditionalEvent>, Edge> node =
                factoryManager.newNode(event.getId(), StartConditionalEvent.class);

        StartConditionalEvent definition = node.getContent().getDefinition();
        EventPropertyReader p = propertyReaderFactory.of(event);

        definition.setGeneral(new BPMNGeneralSet(
                new Name(p.getName()),
                new Documentation(p.getDocumentation())
        ));

        definition.setExecutionSet(new InterruptingConditionalEventExecutionSet(
                new IsInterrupting(event.isIsInterrupting()),
                p.getConditionExpression(e))
        );

        definition.setSimulationSet(p.getSimulationSet());

        node.getContent().setBounds(p.getBounds());

        definition.setDimensionsSet(p.getCircleDimensionSet());
        definition.setFontSet(p.getFontSet());
        definition.setBackgroundSet(p.getBackgroundSet());

        return BpmnNode.of(node);
    }

    private BpmnNode escalationEvent(
            StartEvent event,
            EscalationEventDefinition e) {
        Node<View<StartEscalationEvent>, Edge> node =
                factoryManager.newNode(event.getId(), StartEscalationEvent.class);

        StartEscalationEvent definition = node.getContent().getDefinition();
        EventPropertyReader p = propertyReaderFactory.of(event);

        definition.setGeneral(new BPMNGeneralSet(
                new Name(p.getName()),
                new Documentation(p.getDocumentation())
        ));

        definition.setDataIOSet(new DataIOSet(
                p.getAssignmentsInfo()
        ));

        definition.setExecutionSet(new InterruptingEscalationEventExecutionSet(
                new IsInterrupting(event.isIsInterrupting()),
                new EscalationRef(EventDefinitionReader.escalationRefOf(e)))
        );

        definition.setSimulationSet(p.getSimulationSet());

        node.getContent().setBounds(p.getBounds());

        definition.setDimensionsSet(p.getCircleDimensionSet());
        definition.setFontSet(p.getFontSet());
        definition.setBackgroundSet(p.getBackgroundSet());

        return BpmnNode.of(node);
    }

    private BpmnNode compensationEvent(
            StartEvent event,
            CompensateEventDefinition e) {
        Node<View<StartCompensationEvent>, Edge> node =
                factoryManager.newNode(event.getId(), StartCompensationEvent.class);

        StartCompensationEvent definition = node.getContent().getDefinition();
        EventPropertyReader p = propertyReaderFactory.of(event);

        definition.setGeneral(new BPMNGeneralSet(
                new Name(p.getName()),
                new Documentation(p.getDocumentation())
        ));

        definition.setSimulationSet(p.getSimulationSet());
        definition.setIsInterrupting(new IsInterrupting(false));

        node.getContent().setBounds(p.getBounds());

        definition.setDimensionsSet(p.getCircleDimensionSet());
        definition.setFontSet(p.getFontSet());
        definition.setBackgroundSet(p.getBackgroundSet());

        return BpmnNode.of(node);
    }
}
