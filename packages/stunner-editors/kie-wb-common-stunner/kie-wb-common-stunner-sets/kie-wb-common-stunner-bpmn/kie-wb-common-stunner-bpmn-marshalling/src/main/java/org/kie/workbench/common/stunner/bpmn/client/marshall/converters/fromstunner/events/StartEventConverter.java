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

import org.eclipse.bpmn2.Message;
import org.eclipse.bpmn2.StartEvent;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.properties.CatchEventPropertyWriter;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.properties.CorrelationPropertyWriter;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.properties.PropertyWriter;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.properties.PropertyWriterFactory;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.util.ConverterUtils;
import org.kie.workbench.common.stunner.bpmn.definition.BaseStartEvent;
import org.kie.workbench.common.stunner.bpmn.definition.StartCompensationEvent;
import org.kie.workbench.common.stunner.bpmn.definition.StartConditionalEvent;
import org.kie.workbench.common.stunner.bpmn.definition.StartErrorEvent;
import org.kie.workbench.common.stunner.bpmn.definition.StartEscalationEvent;
import org.kie.workbench.common.stunner.bpmn.definition.StartMessageEvent;
import org.kie.workbench.common.stunner.bpmn.definition.StartNoneEvent;
import org.kie.workbench.common.stunner.bpmn.definition.StartSignalEvent;
import org.kie.workbench.common.stunner.bpmn.definition.StartTimerEvent;
import org.kie.workbench.common.stunner.bpmn.definition.property.collaboration.events.CorrelationSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.BaseStartEventExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.conditional.InterruptingConditionalEventExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.error.InterruptingErrorEventExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.escalation.InterruptingEscalationEventExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.message.InterruptingMessageEventExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.message.MessageRef;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.signal.InterruptingSignalEventExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.timer.InterruptingTimerEventExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.BPMNGeneralSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.ScriptTypeValue;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

import static org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.Factories.bpmn2;
import static org.kie.workbench.common.stunner.bpmn.client.marshall.converters.util.ConverterUtils.cast;

public class StartEventConverter {

    private final PropertyWriterFactory propertyWriterFactory;

    public StartEventConverter(PropertyWriterFactory propertyWriterFactory) {
        this.propertyWriterFactory = propertyWriterFactory;
    }

    public PropertyWriter toFlowElement(Node<View<BaseStartEvent>, ?> node) {
        BaseStartEvent def = node.getContent().getDefinition();
        if (def instanceof StartNoneEvent) {
            return noneEvent(cast(node));
        }
        if (def instanceof StartSignalEvent) {
            return signalEvent(cast(node));
        }
        if (def instanceof StartTimerEvent) {
            return timerEvent(cast(node));
        }
        if (def instanceof StartErrorEvent) {
            return errorEvent(cast(node));
        }
        if (def instanceof StartMessageEvent) {
            return messageEvent(cast(node));
        }
        if (def instanceof StartConditionalEvent) {
            return conditionalEvent(cast(node));
        }
        if (def instanceof StartEscalationEvent) {
            return escalationEvent(cast(node));
        }
        if (def instanceof StartCompensationEvent) {
            return compensationEvent(cast(node));
        }
        return ConverterUtils.notSupported(def);
    }

    private PropertyWriter noneEvent(Node<View<StartNoneEvent>, ?> n) {
        StartEvent event = bpmn2.createStartEvent();
        event.setId(n.getUUID());

        StartNoneEvent definition = n.getContent().getDefinition();
        CatchEventPropertyWriter p = propertyWriterFactory.of(event);

        BPMNGeneralSet general = definition.getGeneral();
        p.setName(general.getName().getValue());
        p.setDocumentation(general.getDocumentation().getValue());
        p.setSimulationSet(definition.getSimulationSet());
        p.setMetaData(definition.getAdvancedData().getMetaDataAttributes());

        BaseStartEventExecutionSet executionSet = definition.getExecutionSet();
        event.setIsInterrupting(executionSet.getIsInterrupting().getValue());
        p.addSlaDueDate(executionSet.getSlaDueDate());

        p.setAbsoluteBounds(n);

        return p;
    }

    private PropertyWriter compensationEvent(Node<View<StartCompensationEvent>, ?> n) {
        StartEvent event = bpmn2.createStartEvent();
        event.setId(n.getUUID());

        StartCompensationEvent definition = n.getContent().getDefinition();
        CatchEventPropertyWriter p = propertyWriterFactory.of(event);

        BPMNGeneralSet general = definition.getGeneral();
        p.setName(general.getName().getValue());
        p.setDocumentation(general.getDocumentation().getValue());
        p.setSimulationSet(definition.getSimulationSet());
        p.setMetaData(definition.getAdvancedData().getMetaDataAttributes());

        BaseStartEventExecutionSet executionSet = definition.getExecutionSet();
        event.setIsInterrupting(executionSet.getIsInterrupting().getValue());
        p.addSlaDueDate(executionSet.getSlaDueDate());
        p.setAbsoluteBounds(n);

        p.addCompensation();

        return p;
    }

    private PropertyWriter signalEvent(Node<View<StartSignalEvent>, ?> n) {
        StartEvent event = bpmn2.createStartEvent();
        event.setId(n.getUUID());

        StartSignalEvent definition = n.getContent().getDefinition();
        CatchEventPropertyWriter p = propertyWriterFactory.of(event);

        BPMNGeneralSet general = definition.getGeneral();
        p.setName(general.getName().getValue());
        p.setDocumentation(general.getDocumentation().getValue());
        p.setAssignmentsInfo(definition.getDataIOSet().getAssignmentsinfo());
        p.setMetaData(definition.getAdvancedData().getMetaDataAttributes());

        InterruptingSignalEventExecutionSet executionSet = definition.getExecutionSet();
        event.setIsInterrupting(executionSet.getIsInterrupting().getValue());
        p.addSlaDueDate(executionSet.getSlaDueDate());
        p.setAbsoluteBounds(n);

        p.addSignal(executionSet.getSignalRef());

        return p;
    }

    private PropertyWriter timerEvent(Node<View<StartTimerEvent>, ?> n) {
        StartEvent event = bpmn2.createStartEvent();
        event.setId(n.getUUID());

        StartTimerEvent definition = n.getContent().getDefinition();
        CatchEventPropertyWriter p = propertyWriterFactory.of(event);

        BPMNGeneralSet general = definition.getGeneral();
        p.setName(general.getName().getValue());
        p.setDocumentation(general.getDocumentation().getValue());
        p.setSimulationSet(definition.getSimulationSet());
        p.setMetaData(definition.getAdvancedData().getMetaDataAttributes());

        InterruptingTimerEventExecutionSet executionSet = definition.getExecutionSet();
        event.setIsInterrupting(executionSet.getIsInterrupting().getValue());
        p.addSlaDueDate(executionSet.getSlaDueDate());
        p.setAbsoluteBounds(n);

        p.addTimer(executionSet.getTimerSettings());

        return p;
    }

    private PropertyWriter conditionalEvent(Node<View<StartConditionalEvent>, ?> n) {
        StartEvent event = bpmn2.createStartEvent();
        event.setId(n.getUUID());

        StartConditionalEvent definition = n.getContent().getDefinition();
        CatchEventPropertyWriter p = propertyWriterFactory.of(event);

        BPMNGeneralSet general = definition.getGeneral();
        p.setName(general.getName().getValue());
        p.setDocumentation(general.getDocumentation().getValue());
        p.setSimulationSet(definition.getSimulationSet());
        p.setMetaData(definition.getAdvancedData().getMetaDataAttributes());

        InterruptingConditionalEventExecutionSet executionSet = definition.getExecutionSet();
        event.setIsInterrupting(executionSet.getIsInterrupting().getValue());
        p.addSlaDueDate(executionSet.getSlaDueDate());
        p.setAbsoluteBounds(n);

        p.addCondition(executionSet.getConditionExpression());

        return p;
    }

    private PropertyWriter errorEvent(Node<View<StartErrorEvent>, ?> n) {
        StartEvent event = bpmn2.createStartEvent();
        event.setId(n.getUUID());

        StartErrorEvent definition = n.getContent().getDefinition();
        CatchEventPropertyWriter p = propertyWriterFactory.of(event);

        BPMNGeneralSet general = definition.getGeneral();
        p.setName(general.getName().getValue());
        p.setDocumentation(general.getDocumentation().getValue());
        p.setMetaData(definition.getAdvancedData().getMetaDataAttributes());

        p.setAssignmentsInfo(definition.getDataIOSet().getAssignmentsinfo());

        InterruptingErrorEventExecutionSet executionSet = definition.getExecutionSet();
        event.setIsInterrupting(executionSet.getIsInterrupting().getValue());
        p.addSlaDueDate(executionSet.getSlaDueDate());
        p.setAbsoluteBounds(n);

        p.addError(executionSet.getErrorRef());

        return p;
    }

    private PropertyWriter escalationEvent(Node<View<StartEscalationEvent>, ?> n) {
        StartEvent event = bpmn2.createStartEvent();
        event.setId(n.getUUID());

        StartEscalationEvent definition = n.getContent().getDefinition();
        CatchEventPropertyWriter p = propertyWriterFactory.of(event);

        BPMNGeneralSet general = definition.getGeneral();
        p.setName(general.getName().getValue());
        p.setDocumentation(general.getDocumentation().getValue());
        p.setSimulationSet(definition.getSimulationSet());
        p.setMetaData(definition.getAdvancedData().getMetaDataAttributes());

        p.setAssignmentsInfo(definition.getDataIOSet().getAssignmentsinfo());

        InterruptingEscalationEventExecutionSet executionSet = definition.getExecutionSet();
        event.setIsInterrupting(executionSet.getIsInterrupting().getValue());
        p.addSlaDueDate(executionSet.getSlaDueDate());
        p.setAbsoluteBounds(n);

        p.addEscalation(executionSet.getEscalationRef());

        return p;
    }

    private PropertyWriter messageEvent(Node<View<StartMessageEvent>, ?> n) {
        StartEvent event = bpmn2.createStartEvent();
        event.setId(n.getUUID());

        CatchEventPropertyWriter p = propertyWriterFactory.of(event);

        StartMessageEvent definition = n.getContent().getDefinition();
        CorrelationSet correlationSet = definition.getCorrelationSet();
        CorrelationPropertyWriter correlationPropertyWriter = propertyWriterFactory.of(p);

        BPMNGeneralSet general = definition.getGeneral();
        p.setName(general.getName().getValue());
        p.setDocumentation(general.getDocumentation().getValue());
        p.setMetaData(definition.getAdvancedData().getMetaDataAttributes());

        p.setAssignmentsInfo(definition.getDataIOSet().getAssignmentsinfo());

        InterruptingMessageEventExecutionSet executionSet = definition.getExecutionSet();
        event.setIsInterrupting(executionSet.getIsInterrupting().getValue());
        p.addSlaDueDate((executionSet.getSlaDueDate()));
        p.setAbsoluteBounds(n);

        MessageRef messageRef = executionSet.getMessageRef();
        Message message = p.addMessage(messageRef);

        ScriptTypeValue messageExpression = correlationSet.getMessageExpression().getValue();
        ScriptTypeValue dataExpression = correlationSet.getDataExpression().getValue();
        correlationPropertyWriter.setCorrelationData(correlationSet.getCorrelationProperty().getValue(),
                                                     message,
                                                     messageExpression.getScript(),
                                                     messageExpression.getLanguage(),
                                                     correlationSet.getMessageExpressionType().getValue(),
                                                     dataExpression.getScript(),
                                                     dataExpression.getLanguage(),
                                                     correlationSet.getDataExpressionType().getValue());

        return p;
    }
}
