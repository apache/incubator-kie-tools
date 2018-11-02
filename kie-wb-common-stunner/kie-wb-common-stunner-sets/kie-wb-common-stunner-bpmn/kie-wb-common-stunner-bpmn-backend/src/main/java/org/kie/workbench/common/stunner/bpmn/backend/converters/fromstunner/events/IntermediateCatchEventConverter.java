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

package org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.events;

import org.kie.workbench.common.stunner.bpmn.backend.converters.NodeMatch;
import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.properties.CatchEventPropertyWriter;
import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.properties.PropertyWriter;
import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.properties.PropertyWriterFactory;
import org.kie.workbench.common.stunner.bpmn.definition.BaseCatchingIntermediateEvent;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateCompensationEvent;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateConditionalEvent;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateErrorEventCatching;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateEscalationEvent;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateMessageEventCatching;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateSignalEventCatching;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateTimerEvent;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.conditional.CancellingConditionalEventExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.error.CancellingErrorEventExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.escalation.CancellingEscalationEventExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.message.CancellingMessageEventExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.signal.CancellingSignalEventExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.timer.CancellingTimerEventExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.BPMNGeneralSet;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.relationship.Dock;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

import static org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.Factories.bpmn2;

public class IntermediateCatchEventConverter {

    private final PropertyWriterFactory propertyWriterFactory;

    public IntermediateCatchEventConverter(PropertyWriterFactory propertyWriterFactory) {
        this.propertyWriterFactory = propertyWriterFactory;
    }

    public PropertyWriter toFlowElement(Node<View<BaseCatchingIntermediateEvent>, ?> node) {
        return NodeMatch.fromNode(BaseCatchingIntermediateEvent.class, PropertyWriter.class)
                .when(IntermediateMessageEventCatching.class, this::messageEvent)
                .when(IntermediateSignalEventCatching.class, this::signalEvent)
                .when(IntermediateErrorEventCatching.class, this::errorEvent)
                .when(IntermediateTimerEvent.class, this::timerEvent)
                .when(IntermediateConditionalEvent.class, this::conditionalEvent)
                .when(IntermediateEscalationEvent.class, this::escalationEvent)
                .when(IntermediateCompensationEvent.class, this::compensationEvent)

                .apply(node).value();
    }

    private PropertyWriter errorEvent(Node<View<IntermediateErrorEventCatching>, ?> n) {
        CatchEventPropertyWriter p = createCatchEventPropertyWriter(n);
        p.getFlowElement().setId(n.getUUID());

        IntermediateErrorEventCatching definition = n.getContent().getDefinition();

        BPMNGeneralSet general = definition.getGeneral();
        p.setName(general.getName().getValue());
        p.setDocumentation(general.getDocumentation().getValue());

        p.setAssignmentsInfo(
                definition.getDataIOSet().getAssignmentsinfo());

        CancellingErrorEventExecutionSet executionSet = definition.getExecutionSet();
        p.setCancelActivity(executionSet.getCancelActivity().getValue());
        p.addError(executionSet.getErrorRef());

        p.setBounds(n.getContent().getBounds());
        return p;
    }

    private PropertyWriter signalEvent(Node<View<IntermediateSignalEventCatching>, ?> n) {
        CatchEventPropertyWriter p = createCatchEventPropertyWriter(n);
        p.getFlowElement().setId(n.getUUID());

        IntermediateSignalEventCatching definition = n.getContent().getDefinition();

        BPMNGeneralSet general = definition.getGeneral();
        p.setName(general.getName().getValue());
        p.setDocumentation(general.getDocumentation().getValue());

        p.setAssignmentsInfo(
                definition.getDataIOSet().getAssignmentsinfo());

        CancellingSignalEventExecutionSet executionSet = definition.getExecutionSet();
        p.setCancelActivity(executionSet.getCancelActivity().getValue());
        p.addSignal(definition.getExecutionSet().getSignalRef());

        p.setBounds(n.getContent().getBounds());
        return p;
    }

    private PropertyWriter timerEvent(Node<View<IntermediateTimerEvent>, ?> n) {
        CatchEventPropertyWriter p = createCatchEventPropertyWriter(n);
        p.getFlowElement().setId(n.getUUID());

        IntermediateTimerEvent definition = n.getContent().getDefinition();

        BPMNGeneralSet general = definition.getGeneral();
        p.setName(general.getName().getValue());
        p.setDocumentation(general.getDocumentation().getValue());

        CancellingTimerEventExecutionSet executionSet = definition.getExecutionSet();
        p.setCancelActivity(executionSet.getCancelActivity().getValue());
        p.addTimer(executionSet.getTimerSettings());

        p.setBounds(n.getContent().getBounds());
        return p;
    }

    private PropertyWriter messageEvent(Node<View<IntermediateMessageEventCatching>, ?> n) {
        CatchEventPropertyWriter p = createCatchEventPropertyWriter(n);
        p.getFlowElement().setId(n.getUUID());

        IntermediateMessageEventCatching definition = n.getContent().getDefinition();

        BPMNGeneralSet general = definition.getGeneral();
        p.setName(general.getName().getValue());
        p.setDocumentation(general.getDocumentation().getValue());

        p.setAssignmentsInfo(
                definition.getDataIOSet().getAssignmentsinfo());

        CancellingMessageEventExecutionSet executionSet = definition.getExecutionSet();
        p.setCancelActivity(executionSet.getCancelActivity().getValue());
        p.addMessage(executionSet.getMessageRef());

        p.setBounds(n.getContent().getBounds());
        return p;
    }

    private PropertyWriter conditionalEvent(Node<View<IntermediateConditionalEvent>, ?> n) {
        CatchEventPropertyWriter p = createCatchEventPropertyWriter(n);
        p.getFlowElement().setId(n.getUUID());

        IntermediateConditionalEvent definition = n.getContent().getDefinition();

        BPMNGeneralSet general = definition.getGeneral();
        p.setName(general.getName().getValue());
        p.setDocumentation(general.getDocumentation().getValue());

        CancellingConditionalEventExecutionSet executionSet = definition.getExecutionSet();
        p.setCancelActivity(executionSet.getCancelActivity().getValue());
        p.addCondition(executionSet.getConditionExpression());

        p.setBounds(n.getContent().getBounds());
        return p;
    }

    private PropertyWriter escalationEvent(Node<View<IntermediateEscalationEvent>, ?> n) {
        CatchEventPropertyWriter p = createCatchEventPropertyWriter(n);
        p.getFlowElement().setId(n.getUUID());

        IntermediateEscalationEvent definition = n.getContent().getDefinition();

        BPMNGeneralSet general = definition.getGeneral();
        p.setName(general.getName().getValue());
        p.setDocumentation(general.getDocumentation().getValue());

        p.setAssignmentsInfo(
                definition.getDataIOSet().getAssignmentsinfo());

        CancellingEscalationEventExecutionSet executionSet = definition.getExecutionSet();
        p.setCancelActivity(executionSet.getCancelActivity().getValue());
        p.addEscalation(executionSet.getEscalationRef());

        p.setBounds(n.getContent().getBounds());
        return p;
    }

    private PropertyWriter compensationEvent(Node<View<IntermediateCompensationEvent>, ?> n) {
        CatchEventPropertyWriter p = createCatchEventPropertyWriter(n);
        p.getFlowElement().setId(n.getUUID());

        IntermediateCompensationEvent definition = n.getContent().getDefinition();

        BPMNGeneralSet general = definition.getGeneral();
        p.setName(general.getName().getValue());
        p.setDocumentation(general.getDocumentation().getValue());

        p.addCompensation();

        p.setBounds(n.getContent().getBounds());
        return p;
    }

    private CatchEventPropertyWriter createCatchEventPropertyWriter(Node n) {
        return isDocked(n) ?
                propertyWriterFactory.of(bpmn2.createBoundaryEvent()) :
                propertyWriterFactory.of(bpmn2.createIntermediateCatchEvent());
    }

    private boolean isDocked(Node node) {
        return null != getDockSourceNode(node);
    }

    @SuppressWarnings("unchecked")
    private Node<View, Edge> getDockSourceNode(final Node<View, Edge> node) {
        return (Node<View, Edge>) node
                .getInEdges()
                .stream()
                .filter(this::isDockEdge)
                .map(Edge::getSourceNode)
                .findFirst()
                .orElse(null);
    }

    private boolean isDockEdge(final Edge edge) {
        return edge.getContent() instanceof Dock;
    }
}
