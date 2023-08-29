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

import org.eclipse.bpmn2.CompensateEventDefinition;
import org.eclipse.bpmn2.ConditionalEventDefinition;
import org.eclipse.bpmn2.ErrorEventDefinition;
import org.eclipse.bpmn2.EscalationEventDefinition;
import org.eclipse.bpmn2.EventDefinition;
import org.eclipse.bpmn2.MessageEventDefinition;
import org.eclipse.bpmn2.SignalEventDefinition;
import org.eclipse.bpmn2.StartEvent;
import org.eclipse.bpmn2.TimerEventDefinition;
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
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.properties.EventPropertyReader;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.properties.PropertyReaderFactory;
import org.kie.workbench.common.stunner.bpmn.definition.StartCompensationEvent;
import org.kie.workbench.common.stunner.bpmn.definition.StartConditionalEvent;
import org.kie.workbench.common.stunner.bpmn.definition.StartErrorEvent;
import org.kie.workbench.common.stunner.bpmn.definition.StartEscalationEvent;
import org.kie.workbench.common.stunner.bpmn.definition.StartMessageEvent;
import org.kie.workbench.common.stunner.bpmn.definition.StartNoneEvent;
import org.kie.workbench.common.stunner.bpmn.definition.StartSignalEvent;
import org.kie.workbench.common.stunner.bpmn.definition.StartTimerEvent;
import org.kie.workbench.common.stunner.bpmn.definition.property.dataio.DataIOSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.BaseStartEventExecutionSet;
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
import org.kie.workbench.common.stunner.bpmn.definition.property.general.SLADueDate;
import org.kie.workbench.common.stunner.bpmn.definition.property.variables.AdvancedData;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

public class StartEventConverter extends AbstractConverter implements NodeConverter<StartEvent> {

    private final TypedFactoryManager factoryManager;
    private final PropertyReaderFactory propertyReaderFactory;

    public StartEventConverter(TypedFactoryManager factoryManager, PropertyReaderFactory propertyReaderFactory,
                               Mode mode) {
        super(mode);
        this.factoryManager = factoryManager;
        this.propertyReaderFactory = propertyReaderFactory;
    }

    public Result<BpmnNode> convert(StartEvent event) {
        CatchEventPropertyReader p = propertyReaderFactory.of(event);
        List<EventDefinition> eventDefinitions = p.getEventDefinitions();
        switch (eventDefinitions.size()) {
            case 0:
                return Result.success(noneEvent(event));
            case 1:
                return Match.<EventDefinition, BpmnNode>of()
                        .<SignalEventDefinition>when(e -> e instanceof SignalEventDefinition, e -> signalEvent(event, e))
                        .<MessageEventDefinition>when(e -> e instanceof MessageEventDefinition, e -> messageEvent(event, e))
                        .<TimerEventDefinition>when(e -> e instanceof TimerEventDefinition, e -> timerEvent(event, e))
                        .<ErrorEventDefinition>when(e -> e instanceof ErrorEventDefinition, e -> errorEvent(event, e))
                        .<ConditionalEventDefinition>when(e -> e instanceof ConditionalEventDefinition, e -> conditionalEvent(event, e))
                        .<EscalationEventDefinition>when(e -> e instanceof EscalationEventDefinition, e -> escalationEvent(event, e))
                        .<CompensateEventDefinition>when(e -> e instanceof CompensateEventDefinition, e -> compensationEvent(event, e))
                        .mode(getMode())
                        .apply(eventDefinitions.get(0));
            default:
                throw new UnsupportedOperationException("Multiple event definitions not supported for start event");
        }
    }

    private BpmnNode noneEvent(StartEvent event) {
        Node<View<StartNoneEvent>, Edge> node = factoryManager.newNode(event.getId(), StartNoneEvent.class);

        StartNoneEvent definition = node.getContent().getDefinition();
        EventPropertyReader p = propertyReaderFactory.of(event);

        definition.setGeneral(new BPMNGeneralSet(new Name(p.getName()), new Documentation(p.getDocumentation())));
        definition.setBackgroundSet(p.getBackgroundSet());
        definition.setFontSet(p.getFontSet());
        definition.setDimensionsSet(p.getCircleDimensionSet());
        definition.setSimulationSet(p.getSimulationSet());
        definition.setAdvancedData(new AdvancedData(p.getMetaDataAttributes()));

        IsInterrupting isInterrupting = new IsInterrupting(event.isIsInterrupting());
        SLADueDate slaDueDate = new SLADueDate(p.getSlaDueDate());
        BaseStartEventExecutionSet baseStartEventExecutionSet =
                new BaseStartEventExecutionSet(isInterrupting, slaDueDate);
        definition.setExecutionSet(baseStartEventExecutionSet);

        node.getContent().setBounds(p.getBounds());

        return BpmnNode.of(node, p);
    }

    private BpmnNode compensationEvent(StartEvent event, CompensateEventDefinition e) {
        Node<View<StartCompensationEvent>, Edge> node =
                factoryManager.newNode(event.getId(), StartCompensationEvent.class);

        StartCompensationEvent definition = node.getContent().getDefinition();
        EventPropertyReader p = propertyReaderFactory.of(event);

        definition.setGeneral(new BPMNGeneralSet(new Name(p.getName()), new Documentation(p.getDocumentation())));
        definition.setBackgroundSet(p.getBackgroundSet());
        definition.setFontSet(p.getFontSet());
        definition.setDimensionsSet(p.getCircleDimensionSet());
        definition.setSimulationSet(p.getSimulationSet());
        definition.setAdvancedData(new AdvancedData(p.getMetaDataAttributes()));

        IsInterrupting isInterrupting = new IsInterrupting(event.isIsInterrupting());
        SLADueDate slaDueDate = new SLADueDate(p.getSlaDueDate());
        BaseStartEventExecutionSet baseStartEventExecutionSet =
                new BaseStartEventExecutionSet(isInterrupting, slaDueDate);
        definition.setExecutionSet(baseStartEventExecutionSet);

        node.getContent().setBounds(p.getBounds());

        return BpmnNode.of(node, p);
    }

    private BpmnNode signalEvent(StartEvent event, SignalEventDefinition e) {
        Node<View<StartSignalEvent>, Edge> node = factoryManager.newNode(event.getId(), StartSignalEvent.class);

        StartSignalEvent definition = node.getContent().getDefinition();
        EventPropertyReader p = propertyReaderFactory.of(event);

        definition.setGeneral(new BPMNGeneralSet(new Name(p.getName()), new Documentation(p.getDocumentation())));
        definition.setBackgroundSet(p.getBackgroundSet());
        definition.setFontSet(p.getFontSet());
        definition.setDimensionsSet(p.getCircleDimensionSet());
        definition.setSimulationSet(p.getSimulationSet());
        definition.setAdvancedData(new AdvancedData(p.getMetaDataAttributes()));
        definition.setDataIOSet(new DataIOSet(p.getAssignmentsInfo()));

        IsInterrupting isInterrupting = new IsInterrupting(event.isIsInterrupting());
        SLADueDate slaDueDate = new SLADueDate(p.getSlaDueDate());
        SignalRef signalRef = new SignalRef(p.getSignalRef());
        InterruptingSignalEventExecutionSet executionSet =
                new InterruptingSignalEventExecutionSet(isInterrupting, slaDueDate, signalRef);
        definition.setExecutionSet(executionSet);

        node.getContent().setBounds(p.getBounds());

        return BpmnNode.of(node, p);
    }

    private BpmnNode timerEvent(StartEvent event, TimerEventDefinition e) {
        Node<View<StartTimerEvent>, Edge> node = factoryManager.newNode(event.getId(), StartTimerEvent.class);

        StartTimerEvent definition = node.getContent().getDefinition();
        EventPropertyReader p = propertyReaderFactory.of(event);

        definition.setGeneral(new BPMNGeneralSet(new Name(p.getName()), new Documentation(p.getDocumentation())));
        definition.setBackgroundSet(p.getBackgroundSet());
        definition.setFontSet(p.getFontSet());
        definition.setDimensionsSet(p.getCircleDimensionSet());
        definition.setSimulationSet(p.getSimulationSet());
        definition.setAdvancedData(new AdvancedData(p.getMetaDataAttributes()));

        IsInterrupting isInterrupting = new IsInterrupting(event.isIsInterrupting());
        SLADueDate slaDueDate = new SLADueDate(p.getSlaDueDate());
        TimerSettings timerSettings = new TimerSettings(p.getTimerSettings(e));
        InterruptingTimerEventExecutionSet executionSet =
                new InterruptingTimerEventExecutionSet(isInterrupting, slaDueDate, timerSettings);
        definition.setExecutionSet(executionSet);

        node.getContent().setBounds(p.getBounds());

        return BpmnNode.of(node, p);
    }

    private BpmnNode conditionalEvent(StartEvent event, ConditionalEventDefinition e) {
        Node<View<StartConditionalEvent>, Edge> node = factoryManager.newNode(event.getId(), StartConditionalEvent.class);

        StartConditionalEvent definition = node.getContent().getDefinition();
        EventPropertyReader p = propertyReaderFactory.of(event);

        definition.setGeneral(new BPMNGeneralSet(new Name(p.getName()), new Documentation(p.getDocumentation())));
        definition.setBackgroundSet(p.getBackgroundSet());
        definition.setFontSet(p.getFontSet());
        definition.setDimensionsSet(p.getCircleDimensionSet());
        definition.setSimulationSet(p.getSimulationSet());
        definition.setAdvancedData(new AdvancedData(p.getMetaDataAttributes()));

        IsInterrupting isInterrupting = new IsInterrupting(event.isIsInterrupting());
        SLADueDate slaDueDate = new SLADueDate(p.getSlaDueDate());
        InterruptingConditionalEventExecutionSet executionSet =
                new InterruptingConditionalEventExecutionSet(isInterrupting, slaDueDate, p.getConditionExpression(e));
        definition.setExecutionSet(executionSet);

        node.getContent().setBounds(p.getBounds());

        return BpmnNode.of(node, p);
    }

    private BpmnNode errorEvent(StartEvent event, ErrorEventDefinition e) {
        Node<View<StartErrorEvent>, Edge> node = factoryManager.newNode(event.getId(), StartErrorEvent.class);

        StartErrorEvent definition = node.getContent().getDefinition();
        EventPropertyReader p = propertyReaderFactory.of(event);

        definition.setGeneral(new BPMNGeneralSet(new Name(p.getName()), new Documentation(p.getDocumentation())));
        definition.setBackgroundSet(p.getBackgroundSet());
        definition.setFontSet(p.getFontSet());
        definition.setDimensionsSet(p.getCircleDimensionSet());
        definition.setSimulationSet(p.getSimulationSet());
        definition.setAdvancedData(new AdvancedData(p.getMetaDataAttributes()));
        definition.setDataIOSet(new DataIOSet(p.getAssignmentsInfo()));

        IsInterrupting isInterrupting = new IsInterrupting(event.isIsInterrupting());
        SLADueDate slaDueDate = new SLADueDate(p.getSlaDueDate());
        ErrorRef errorRef = new ErrorRef(EventDefinitionReader.errorRefOf(e));
        InterruptingErrorEventExecutionSet executionSet =
                new InterruptingErrorEventExecutionSet(isInterrupting, slaDueDate, errorRef);
        definition.setExecutionSet(executionSet);

        node.getContent().setBounds(p.getBounds());

        return BpmnNode.of(node, p);
    }

    private BpmnNode escalationEvent(StartEvent event, EscalationEventDefinition e) {
        Node<View<StartEscalationEvent>, Edge> node = factoryManager.newNode(event.getId(), StartEscalationEvent.class);

        StartEscalationEvent definition = node.getContent().getDefinition();
        EventPropertyReader p = propertyReaderFactory.of(event);

        definition.setGeneral(new BPMNGeneralSet(new Name(p.getName()), new Documentation(p.getDocumentation())));
        definition.setBackgroundSet(p.getBackgroundSet());
        definition.setFontSet(p.getFontSet());
        definition.setDimensionsSet(p.getCircleDimensionSet());
        definition.setSimulationSet(p.getSimulationSet());
        definition.setAdvancedData(new AdvancedData(p.getMetaDataAttributes()));
        definition.setDataIOSet(new DataIOSet(p.getAssignmentsInfo()));

        IsInterrupting isInterrupting = new IsInterrupting(event.isIsInterrupting());
        SLADueDate slaDueDate = new SLADueDate(p.getSlaDueDate());
        EscalationRef escalationRef = new EscalationRef(EventDefinitionReader.escalationRefOf(e));
        InterruptingEscalationEventExecutionSet executionSet =
                new InterruptingEscalationEventExecutionSet(isInterrupting, slaDueDate, escalationRef);
        definition.setExecutionSet(executionSet);

        node.getContent().setBounds(p.getBounds());

        return BpmnNode.of(node, p);
    }

    private BpmnNode messageEvent(StartEvent event, MessageEventDefinition e) {
        Node<View<StartMessageEvent>, Edge> node = factoryManager.newNode(event.getId(), StartMessageEvent.class);

        StartMessageEvent definition = node.getContent().getDefinition();
        EventPropertyReader p = propertyReaderFactory.of(event);
        CorrelationPropertyReader correlationPropertyReader = propertyReaderFactory.of(definition);

        definition.setGeneral(new BPMNGeneralSet(new Name(p.getName()), new Documentation(p.getDocumentation())));
        definition.setBackgroundSet(p.getBackgroundSet());
        definition.setFontSet(p.getFontSet());
        definition.setDimensionsSet(p.getCircleDimensionSet());
        definition.setSimulationSet(p.getSimulationSet());
        definition.setAdvancedData(new AdvancedData(p.getMetaDataAttributes()));
        definition.setDataIOSet(new DataIOSet(p.getAssignmentsInfo()));

        IsInterrupting isInterrupting = new IsInterrupting(event.isIsInterrupting());
        SLADueDate slaDueDate = new SLADueDate(p.getSlaDueDate());
        MessageRef messageRef = new MessageRef(EventDefinitionReader.messageRefOf(e),
                                               EventDefinitionReader.messageRefStructureOf(e));
        InterruptingMessageEventExecutionSet executionSet =
                new InterruptingMessageEventExecutionSet(isInterrupting, slaDueDate, messageRef);
        definition.setExecutionSet(executionSet);

        definition.setCorrelationSet(correlationPropertyReader.getCorrelationSet(messageRef));

        node.getContent().setBounds(p.getBounds());

        return BpmnNode.of(node, p);
    }
}
