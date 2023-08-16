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

import java.util.List;
import java.util.Objects;
import java.util.Optional;

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
import org.eclipse.bpmn2.impl.EventDefinitionImpl;
import org.kie.workbench.common.stunner.bpmn.client.marshall.MarshallingRequest.Mode;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.Match;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.Result;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.TypedFactoryManager;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.AbstractConverter;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.BpmnNode;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.NodeConverter;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.properties.CatchEventPropertyReader;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.properties.CorrelationPropertyReader;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.properties.EventDefinitionReader;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.properties.PropertyReaderFactory;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateCompensationEvent;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateConditionalEvent;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateErrorEventCatching;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateEscalationEvent;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateLinkEventCatching;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateMessageEventCatching;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateSignalEventCatching;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateTimerEvent;
import org.kie.workbench.common.stunner.bpmn.definition.property.dataio.DataIOSet;
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
import org.kie.workbench.common.stunner.bpmn.definition.property.general.BPMNGeneralSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.Documentation;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.Name;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.SLADueDate;
import org.kie.workbench.common.stunner.bpmn.definition.property.variables.AdvancedData;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

public class IntermediateCatchEventConverter extends AbstractConverter implements NodeConverter<IntermediateCatchEvent> {

    protected static final String NO_DEFINITION = "An intermediate catch event should contain exactly one definition";
    protected static final String MULTIPLE_DEFINITIONS = "Multiple definitions not supported for intermediate catch event";

    protected static final String BOUNDARY_NO_DEFINITION = "A boundary event should contain exactly one definition";
    protected static final String BOUNDARY_MULTIPLE_DEFINITIONS = ("Multiple definitions not supported for boundary event");

    protected final TypedFactoryManager factoryManager;
    protected final PropertyReaderFactory propertyReaderFactory;

    public IntermediateCatchEventConverter(TypedFactoryManager factoryManager,
                                           PropertyReaderFactory propertyReaderFactory,
                                           Mode mode) {
        super(mode);
        this.factoryManager = factoryManager;
        this.propertyReaderFactory = propertyReaderFactory;
    }

    public Result<BpmnNode> convert(IntermediateCatchEvent event) {
        CatchEventPropertyReader p = propertyReaderFactory.of(event);
        List<EventDefinition> eventDefinitions = p.getEventDefinitions();
        switch (eventDefinitions.size()) {
            case 0:
                throw new UnsupportedOperationException(NO_DEFINITION);
            case 1:
                return Match.<EventDefinition, Result<BpmnNode>>of()
                        .<TimerEventDefinition>when(e -> e instanceof TimerEventDefinition, e -> timerEvent(event, e))
                        .<SignalEventDefinition>when(e -> e instanceof SignalEventDefinition, e -> signalEvent(event))
                        .<LinkEventDefinition>when(e -> e instanceof LinkEventDefinition, e -> linkEvent(event))
                        .<MessageEventDefinition>when(e -> e instanceof MessageEventDefinition, e -> messageEvent(event, e))
                        .<ErrorEventDefinition>when(e -> e instanceof ErrorEventDefinition, e -> errorEvent(event, e))
                        .<ConditionalEventDefinition>when(e -> e instanceof ConditionalEventDefinition, e -> conditionalEvent(event, e))
                        .<EscalationEventDefinition>when(e -> e instanceof EscalationEventDefinition, e -> escalationEvent(event, e))
                        .<CompensateEventDefinition>when(e -> e instanceof CompensateEventDefinition,
                                                         e -> compensationEvent(event))
                        .defaultValue(Result.ignored("Ignored IntermediateCatchEvent", getNotFoundMessage(event)))
                        .mode(getMode())
                        .apply(eventDefinitions.get(0))
                        .value();
            default:
                throw new UnsupportedOperationException(MULTIPLE_DEFINITIONS);
        }
    }

    public Result<BpmnNode> convertBoundaryEvent(BoundaryEvent event) {
        CatchEventPropertyReader p = propertyReaderFactory.of(event);
        List<EventDefinition> eventDefinitions = p.getEventDefinitions();
        switch (eventDefinitions.size()) {
            case 0:
                throw new UnsupportedOperationException(BOUNDARY_NO_DEFINITION);
            case 1:
                Result<BpmnNode> result = Match.<EventDefinition, Result<BpmnNode>>of()
                        .<SignalEventDefinition>when(e -> e instanceof SignalEventDefinition, e -> signalEvent(event))
                        .<TimerEventDefinition>when(e -> e instanceof TimerEventDefinition, e -> timerEvent(event, e))
                        .<MessageEventDefinition>when(e -> e instanceof MessageEventDefinition, e -> messageEvent(event, e))
                        .<ErrorEventDefinition>when(e -> e instanceof ErrorEventDefinition, e -> errorEvent(event, e))
                        .<ConditionalEventDefinition>when(e -> e instanceof ConditionalEventDefinition, e -> conditionalEvent(event, e))
                        .<EscalationEventDefinition>when(e -> e instanceof EscalationEventDefinition, e -> escalationEvent(event, e))
                        .<CompensateEventDefinition>when(e -> e instanceof CompensateEventDefinition, e -> compensationEvent(event))
                        //TODO:kogito verify this ignore
                        //.<BoundaryEventImpl>ignore(e -> e instanceof BoundaryEventImpl,BoundaryEventImpl.class)
                        .<EventDefinitionImpl>ignore(e -> event.getClass().equals(EventDefinitionImpl.class),
                                                     EventDefinitionImpl.class)
                        .defaultValue(Result.ignored("BoundaryEvent ignored", getNotFoundMessage(event)))
                        .mode(getMode())
                        .apply(eventDefinitions.get(0))
                        .value();
                return Optional.of(result)
                        .map(Result::value)
                        .filter(Objects::nonNull)
                        .map(BpmnNode::docked)
                        .map(node -> Result.success(node))
                        .orElse(result);

            default:
                throw new UnsupportedOperationException(BOUNDARY_MULTIPLE_DEFINITIONS);
        }
    }

    protected Result<BpmnNode> errorEvent(CatchEvent event, ErrorEventDefinition e) {
        String nodeId = event.getId();
        Node<View<IntermediateErrorEventCatching>, Edge> node = factoryManager.newNode(nodeId, IntermediateErrorEventCatching.class);

        IntermediateErrorEventCatching definition = node.getContent().getDefinition();

        CatchEventPropertyReader p = propertyReaderFactory.of(event);

        node.getContent().setBounds(p.getBounds());

        definition.setGeneral(
                new BPMNGeneralSet(
                        new Name(p.getName()),
                        new Documentation(p.getDocumentation())
                )
        );

        definition.setBackgroundSet(p.getBackgroundSet());
        definition.setFontSet(p.getFontSet());
        definition.setDimensionsSet(p.getCircleDimensionSet());

        definition.setDataIOSet(new DataIOSet(p.getAssignmentsInfo()));
        definition.setAdvancedData(new AdvancedData(p.getMetaDataAttributes()));

        definition.setExecutionSet(
                new CancellingErrorEventExecutionSet(
                        new CancelActivity(p.isCancelActivity()),
                        new SLADueDate(p.getSlaDueDate()),
                        new ErrorRef(EventDefinitionReader.errorRefOf(e))
                )
        );

        return Result.success(BpmnNode.of(node, p));
    }

    protected Result<BpmnNode> linkEvent(CatchEvent event) {
        String nodeId = event.getId();
        Node<View<IntermediateLinkEventCatching>, Edge> node = factoryManager.newNode(nodeId, IntermediateLinkEventCatching.class);

        IntermediateLinkEventCatching definition = node.getContent().getDefinition();

        CatchEventPropertyReader p = propertyReaderFactory.of(event);

        node.getContent().setBounds(p.getBounds());

        definition.setGeneral(
                new BPMNGeneralSet(
                        new Name(p.getName()),
                        new Documentation(p.getDocumentation())
                )
        );
        definition.setBackgroundSet(p.getBackgroundSet());
        definition.setFontSet(p.getFontSet());
        definition.setDimensionsSet(p.getCircleDimensionSet());

        definition.setDataIOSet(new DataIOSet(p.getAssignmentsInfo()));
        definition.setAdvancedData(new AdvancedData(p.getMetaDataAttributes()));

        definition.setExecutionSet(new LinkEventExecutionSet(new LinkRef(p.getLinkRef())));

        return Result.success(BpmnNode.of(node, p));
    }

    protected Result<BpmnNode> signalEvent(CatchEvent event) {
        String nodeId = event.getId();
        Node<View<IntermediateSignalEventCatching>, Edge> node = factoryManager.newNode(nodeId, IntermediateSignalEventCatching.class);

        IntermediateSignalEventCatching definition = node.getContent().getDefinition();

        CatchEventPropertyReader p = propertyReaderFactory.of(event);

        node.getContent().setBounds(p.getBounds());

        definition.setGeneral(
                new BPMNGeneralSet(
                        new Name(p.getName()),
                        new Documentation(p.getDocumentation())
                )
        );
        definition.setBackgroundSet(p.getBackgroundSet());
        definition.setFontSet(p.getFontSet());
        definition.setDimensionsSet(p.getCircleDimensionSet());

        definition.setDataIOSet(new DataIOSet(p.getAssignmentsInfo()));
        definition.setAdvancedData(new AdvancedData(p.getMetaDataAttributes()));

        definition.setExecutionSet(
                new CancellingSignalEventExecutionSet(
                        new CancelActivity(p.isCancelActivity()),
                        new SLADueDate(p.getSlaDueDate()),
                        new SignalRef(p.getSignalRef())
                )
        );

        return Result.success(BpmnNode.of(node, p));
    }

    protected Result<BpmnNode> timerEvent(CatchEvent event, TimerEventDefinition e) {
        String nodeId = event.getId();
        Node<View<IntermediateTimerEvent>, Edge> node = factoryManager.newNode(nodeId, IntermediateTimerEvent.class);

        IntermediateTimerEvent definition = node.getContent().getDefinition();

        CatchEventPropertyReader p = propertyReaderFactory.of(event);

        node.getContent().setBounds(p.getBounds());

        definition.setGeneral(
                new BPMNGeneralSet(
                        new Name(p.getName()),
                        new Documentation(p.getDocumentation())
                )
        );

        definition.setBackgroundSet(p.getBackgroundSet());
        definition.setFontSet(p.getFontSet());
        definition.setDimensionsSet(p.getCircleDimensionSet());

        definition.setDataIOSet(new DataIOSet(p.getAssignmentsInfo()));
        definition.setAdvancedData(new AdvancedData(p.getMetaDataAttributes()));

        definition.setExecutionSet(
                new CancellingTimerEventExecutionSet(
                        new CancelActivity(p.isCancelActivity()),
                        new SLADueDate(p.getSlaDueDate()),
                        new TimerSettings(p.getTimerSettings(e))
                )
        );

        return Result.success(BpmnNode.of(node, p));
    }

    protected Result<BpmnNode> messageEvent(CatchEvent event, MessageEventDefinition e) {
        String nodeId = event.getId();
        Node<View<IntermediateMessageEventCatching>, Edge> node = factoryManager.newNode(nodeId, IntermediateMessageEventCatching.class);

        IntermediateMessageEventCatching definition = node.getContent().getDefinition();

        CatchEventPropertyReader p = propertyReaderFactory.of(event);
        CorrelationPropertyReader correlationPropertyReader = propertyReaderFactory.of(definition);

        node.getContent().setBounds(p.getBounds());

        definition.setGeneral(
                new BPMNGeneralSet(
                        new Name(p.getName()),
                        new Documentation(p.getDocumentation())
                )
        );

        MessageRef messageRef = new MessageRef(EventDefinitionReader.messageRefOf(e),
                                               EventDefinitionReader.messageRefStructureOf(e));

        definition.setBackgroundSet(p.getBackgroundSet());
        definition.setFontSet(p.getFontSet());
        definition.setDimensionsSet(p.getCircleDimensionSet());
        definition.setDataIOSet(new DataIOSet(p.getAssignmentsInfo()));
        definition.setAdvancedData(new AdvancedData(p.getMetaDataAttributes()));
        definition.setCorrelationSet(correlationPropertyReader.getCorrelationSet(messageRef));

        definition.setExecutionSet(
                new CancellingMessageEventExecutionSet(
                        new CancelActivity(p.isCancelActivity()),
                        new SLADueDate(p.getSlaDueDate()),
                        messageRef)
        );

        return Result.success(BpmnNode.of(node, p));
    }

    protected Result<BpmnNode> conditionalEvent(CatchEvent event, ConditionalEventDefinition e) {
        String nodeId = event.getId();
        Node<View<IntermediateConditionalEvent>, Edge> node = factoryManager.newNode(nodeId, IntermediateConditionalEvent.class);

        IntermediateConditionalEvent definition = node.getContent().getDefinition();
        CatchEventPropertyReader p = propertyReaderFactory.of(event);

        node.getContent().setBounds(p.getBounds());

        definition.setGeneral(
                new BPMNGeneralSet(
                        new Name(p.getName()),
                        new Documentation(p.getDocumentation())
                )
        );

        definition.setBackgroundSet(p.getBackgroundSet());
        definition.setFontSet(p.getFontSet());
        definition.setDimensionsSet(p.getCircleDimensionSet());

        definition.setDataIOSet(new DataIOSet(p.getAssignmentsInfo()));
        definition.setAdvancedData(new AdvancedData(p.getMetaDataAttributes()));

        definition.setExecutionSet(
                new CancellingConditionalEventExecutionSet(
                        new CancelActivity(p.isCancelActivity()),
                        new SLADueDate(p.getSlaDueDate()),
                        p.getConditionExpression(e)
                )
        );

        return Result.success(BpmnNode.of(node, p));
    }

    protected Result<BpmnNode> escalationEvent(CatchEvent event, EscalationEventDefinition e) {
        String nodeId = event.getId();
        Node<View<IntermediateEscalationEvent>, Edge> node = factoryManager.newNode(nodeId,
                                                                                    IntermediateEscalationEvent.class);

        IntermediateEscalationEvent definition = node.getContent().getDefinition();

        CatchEventPropertyReader p = propertyReaderFactory.of(event);

        node.getContent().setBounds(p.getBounds());

        definition.setGeneral(
                new BPMNGeneralSet(
                        new Name(p.getName()),
                        new Documentation(p.getDocumentation())
                )
        );

        definition.setBackgroundSet(p.getBackgroundSet());
        definition.setFontSet(p.getFontSet());
        definition.setDimensionsSet(p.getCircleDimensionSet());

        definition.setDataIOSet(new DataIOSet(p.getAssignmentsInfo()));
        definition.setAdvancedData(new AdvancedData(p.getMetaDataAttributes()));

        definition.setExecutionSet(
                new CancellingEscalationEventExecutionSet(
                        new CancelActivity(p.isCancelActivity()),
                        new SLADueDate(p.getSlaDueDate()),
                        new EscalationRef(EventDefinitionReader.escalationRefOf(e))
                )
        );

        return Result.success(BpmnNode.of(node, p));
    }

    protected Result<BpmnNode> compensationEvent(CatchEvent event) {
        String nodeId = event.getId();
        Node<View<IntermediateCompensationEvent>, Edge> node = factoryManager.newNode(nodeId,
                                                                                      IntermediateCompensationEvent.class);

        IntermediateCompensationEvent definition = node.getContent().getDefinition();

        CatchEventPropertyReader p = propertyReaderFactory.of(event);

        node.getContent().setBounds(p.getBounds());

        definition.setGeneral(
                new BPMNGeneralSet(
                        new Name(p.getName()),
                        new Documentation(p.getDocumentation())
                )
        );

        definition.setBackgroundSet(p.getBackgroundSet());
        definition.setFontSet(p.getFontSet());
        definition.setDimensionsSet(p.getCircleDimensionSet());

        definition.setDataIOSet(new DataIOSet(p.getAssignmentsInfo()));
        definition.setAdvancedData(new AdvancedData(p.getMetaDataAttributes()));

        CancelActivity cancelActivity = new CancelActivity(false);
        SLADueDate slaDueDate = new SLADueDate(p.getSlaDueDate());

        BaseCancellingEventExecutionSet executionSet = new BaseCancellingEventExecutionSet(cancelActivity, slaDueDate);
        definition.setExecutionSet(executionSet);

        return Result.success(BpmnNode.of(node, p));
    }
}