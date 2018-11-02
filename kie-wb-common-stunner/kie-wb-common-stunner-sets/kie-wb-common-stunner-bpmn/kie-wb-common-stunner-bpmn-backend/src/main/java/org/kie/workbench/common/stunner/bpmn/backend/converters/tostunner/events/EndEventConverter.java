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

import org.eclipse.bpmn2.CancelEventDefinition;
import org.eclipse.bpmn2.CompensateEventDefinition;
import org.eclipse.bpmn2.EndEvent;
import org.eclipse.bpmn2.ErrorEventDefinition;
import org.eclipse.bpmn2.EscalationEventDefinition;
import org.eclipse.bpmn2.EventDefinition;
import org.eclipse.bpmn2.MessageEventDefinition;
import org.eclipse.bpmn2.SignalEventDefinition;
import org.eclipse.bpmn2.TerminateEventDefinition;
import org.kie.workbench.common.stunner.bpmn.backend.converters.Match;
import org.kie.workbench.common.stunner.bpmn.backend.converters.TypedFactoryManager;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.BpmnNode;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.properties.EventDefinitionReader;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.properties.EventPropertyReader;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.properties.PropertyReaderFactory;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.properties.ThrowEventPropertyReader;
import org.kie.workbench.common.stunner.bpmn.definition.EndCompensationEvent;
import org.kie.workbench.common.stunner.bpmn.definition.EndErrorEvent;
import org.kie.workbench.common.stunner.bpmn.definition.EndEscalationEvent;
import org.kie.workbench.common.stunner.bpmn.definition.EndMessageEvent;
import org.kie.workbench.common.stunner.bpmn.definition.EndNoneEvent;
import org.kie.workbench.common.stunner.bpmn.definition.EndSignalEvent;
import org.kie.workbench.common.stunner.bpmn.definition.EndTerminateEvent;
import org.kie.workbench.common.stunner.bpmn.definition.property.dataio.DataIOSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.compensation.ActivityRef;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.compensation.CompensationEventExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.error.ErrorEventExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.error.ErrorRef;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.escalation.EscalationEventExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.escalation.EscalationRef;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.message.MessageEventExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.message.MessageRef;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.signal.ScopedSignalEventExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.signal.SignalRef;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.signal.SignalScope;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.BPMNGeneralSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.Documentation;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.Name;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

public class EndEventConverter {

    private final TypedFactoryManager factoryManager;
    private final PropertyReaderFactory propertyReaderFactory;

    public EndEventConverter(TypedFactoryManager factoryManager, PropertyReaderFactory propertyReaderFactory) {
        this.factoryManager = factoryManager;
        this.propertyReaderFactory = propertyReaderFactory;
    }

    public BpmnNode convert(EndEvent event) {
        ThrowEventPropertyReader p = propertyReaderFactory.of(event);
        List<EventDefinition> eventDefinitions = p.getEventDefinitions();
        switch (eventDefinitions.size()) {
            case 0:
                return endNoneEvent(event);
            case 1:
                return Match.of(EventDefinition.class, BpmnNode.class)
                        .when(TerminateEventDefinition.class, e -> terminateEndEvent(event, e))
                        .when(SignalEventDefinition.class, e -> signalEventDefinition(event, e))
                        .when(MessageEventDefinition.class, e -> messageEventDefinition(event, e))
                        .when(ErrorEventDefinition.class, e -> errorEventDefinition(event, e))
                        .when(EscalationEventDefinition.class, e -> escalationEventDefinition(event, e))
                        .when(CompensateEventDefinition.class, e -> compensationEventDefinition(event, e))
                        .missing(CancelEventDefinition.class)
                        .apply(eventDefinitions.get(0)).value();
            default:
                throw new UnsupportedOperationException("Multiple event definitions not supported for end event");
        }
    }

    private BpmnNode messageEventDefinition(EndEvent event, MessageEventDefinition e) {
        Node<View<EndMessageEvent>, Edge> node =
                factoryManager.newNode(event.getId(), EndMessageEvent.class);

        EndMessageEvent definition = node.getContent().getDefinition();
        EventPropertyReader p = propertyReaderFactory.of(event);

        definition.setGeneral(new BPMNGeneralSet(
                new Name(p.getName()),
                new Documentation(p.getDocumentation())
        ));

        definition.setDataIOSet(new DataIOSet(
                p.getAssignmentsInfo()
        ));

        definition.setExecutionSet(new MessageEventExecutionSet(
                new MessageRef(EventDefinitionReader.messageRefOf(e))
        ));

        node.getContent().setBounds(p.getBounds());

        definition.setDimensionsSet(p.getCircleDimensionSet());
        definition.setFontSet(p.getFontSet());
        definition.setBackgroundSet(p.getBackgroundSet());

        return BpmnNode.of(node);
    }

    private BpmnNode signalEventDefinition(EndEvent event, SignalEventDefinition nodeId) {
        Node<View<EndSignalEvent>, Edge> node =
                factoryManager.newNode(event.getId(), EndSignalEvent.class);

        EndSignalEvent definition = node.getContent().getDefinition();
        EventPropertyReader p = propertyReaderFactory.of(event);

        definition.setGeneral(new BPMNGeneralSet(
                new Name(p.getName()),
                new Documentation(p.getDocumentation())
        ));

        definition.setDataIOSet(new DataIOSet(
                p.getAssignmentsInfo()
        ));

        definition.setExecutionSet(new ScopedSignalEventExecutionSet(
                new SignalRef(p.getSignalRef()),
                new SignalScope(p.getSignalScope())
        ));

        node.getContent().setBounds(p.getBounds());

        definition.setDimensionsSet(p.getCircleDimensionSet());
        definition.setFontSet(p.getFontSet());
        definition.setBackgroundSet(p.getBackgroundSet());

        return BpmnNode.of(node);
    }

    private BpmnNode terminateEndEvent(EndEvent event, TerminateEventDefinition e) {
        Node<View<EndTerminateEvent>, Edge> node =
                factoryManager.newNode(event.getId(), EndTerminateEvent.class);

        EndTerminateEvent definition = node.getContent().getDefinition();
        EventPropertyReader p = propertyReaderFactory.of(event);

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

    private BpmnNode endNoneEvent(EndEvent event) {
        Node<View<EndNoneEvent>, Edge> node =
                factoryManager.newNode(event.getId(), EndNoneEvent.class);
        EndNoneEvent definition = node.getContent().getDefinition();
        EventPropertyReader p = propertyReaderFactory.of(event);

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

    private BpmnNode errorEventDefinition(EndEvent event, ErrorEventDefinition e) {
        Node<View<EndErrorEvent>, Edge> node =
                factoryManager.newNode(event.getId(), EndErrorEvent.class);

        EndErrorEvent definition = node.getContent().getDefinition();
        EventPropertyReader p = propertyReaderFactory.of(event);

        definition.setGeneral(new BPMNGeneralSet(
                new Name(p.getName()),
                new Documentation(p.getDocumentation())
        ));

        definition.setDataIOSet(new DataIOSet(
                p.getAssignmentsInfo()
        ));

        definition.setExecutionSet(new ErrorEventExecutionSet(
                new ErrorRef(EventDefinitionReader.errorRefOf(e))
        ));

        node.getContent().setBounds(p.getBounds());

        definition.setDimensionsSet(p.getCircleDimensionSet());
        definition.setFontSet(p.getFontSet());
        definition.setBackgroundSet(p.getBackgroundSet());

        return BpmnNode.of(node);
    }

    private BpmnNode escalationEventDefinition(EndEvent event,
                                               EscalationEventDefinition e) {
        Node<View<EndEscalationEvent>, Edge> node =
                factoryManager.newNode(event.getId(),
                                       EndEscalationEvent.class);

        EndEscalationEvent definition = node.getContent().getDefinition();
        EventPropertyReader p = propertyReaderFactory.of(event);

        definition.setGeneral(new BPMNGeneralSet(
                new Name(p.getName()),
                new Documentation(p.getDocumentation())
        ));

        definition.setDataIOSet(new DataIOSet(
                p.getAssignmentsInfo()
        ));

        definition.setExecutionSet(new EscalationEventExecutionSet(
                new EscalationRef(EventDefinitionReader.escalationRefOf(e))
        ));

        node.getContent().setBounds(p.getBounds());

        definition.setDimensionsSet(p.getCircleDimensionSet());
        definition.setFontSet(p.getFontSet());
        definition.setBackgroundSet(p.getBackgroundSet());

        return BpmnNode.of(node);
    }

    private BpmnNode compensationEventDefinition(EndEvent event,
                                                 CompensateEventDefinition eventDefinition) {
        Node<View<EndCompensationEvent>, Edge> node =
                factoryManager.newNode(event.getId(),
                                       EndCompensationEvent.class);

        EndCompensationEvent definition = node.getContent().getDefinition();
        EventPropertyReader p = propertyReaderFactory.of(event);

        definition.setGeneral(new BPMNGeneralSet(
                new Name(p.getName()),
                new Documentation(p.getDocumentation())
        ));

        definition.setExecutionSet(new CompensationEventExecutionSet(
                new ActivityRef(EventDefinitionReader.activityRefOf(eventDefinition))
        ));

        node.getContent().setBounds(p.getBounds());

        definition.setDimensionsSet(p.getCircleDimensionSet());
        definition.setFontSet(p.getFontSet());
        definition.setBackgroundSet(p.getBackgroundSet());

        return BpmnNode.of(node);
    }
}
