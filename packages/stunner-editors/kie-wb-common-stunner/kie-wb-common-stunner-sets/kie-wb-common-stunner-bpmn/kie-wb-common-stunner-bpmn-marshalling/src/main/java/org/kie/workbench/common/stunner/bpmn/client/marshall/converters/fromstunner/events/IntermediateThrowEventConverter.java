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

import org.eclipse.bpmn2.IntermediateThrowEvent;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.properties.PropertyWriter;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.properties.PropertyWriterFactory;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.properties.ThrowEventPropertyWriter;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.util.ConverterUtils;
import org.kie.workbench.common.stunner.bpmn.definition.BaseThrowingIntermediateEvent;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateCompensationEventThrowing;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateEscalationEventThrowing;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateLinkEventThrowing;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateMessageEventThrowing;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateSignalEventThrowing;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.message.MessageEventExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.BPMNGeneralSet;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

import static org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.Factories.bpmn2;
import static org.kie.workbench.common.stunner.bpmn.client.marshall.converters.util.ConverterUtils.cast;

public class IntermediateThrowEventConverter {

    protected final PropertyWriterFactory propertyWriterFactory;

    public IntermediateThrowEventConverter(PropertyWriterFactory propertyWriterFactory) {
        this.propertyWriterFactory = propertyWriterFactory;
    }

    public PropertyWriter toFlowElement(Node<View<BaseThrowingIntermediateEvent>, ?> node) {
        BaseThrowingIntermediateEvent def = node.getContent().getDefinition();
        if (def instanceof IntermediateMessageEventThrowing) {
            return messageEvent(cast(node));
        }
        if (def instanceof IntermediateSignalEventThrowing) {
            return signalEvent(cast(node));
        }
        if (def instanceof IntermediateLinkEventThrowing) {
            return linkEvent(cast(node));
        }
        if (def instanceof IntermediateEscalationEventThrowing) {
            return escalationEvent(cast(node));
        }
        if (def instanceof IntermediateCompensationEventThrowing) {
            return compensationEvent(cast(node));
        }
        return ConverterUtils.notSupported(def);
    }

    protected PropertyWriter signalEvent(Node<View<IntermediateSignalEventThrowing>, ?> n) {
        IntermediateThrowEvent event = bpmn2.createIntermediateThrowEvent();
        event.setId(n.getUUID());

        IntermediateSignalEventThrowing definition = n.getContent().getDefinition();

        ThrowEventPropertyWriter p = propertyWriterFactory.of(event);

        p.setAbsoluteBounds(n);

        BPMNGeneralSet general = definition.getGeneral();
        p.setName(general.getName().getValue());
        p.setDocumentation(general.getDocumentation().getValue());
        p.setMetaData(definition.getAdvancedData().getMetaDataAttributes());

        p.setAssignmentsInfo(definition.getDataIOSet().getAssignmentsinfo());

        p.addSignal(definition.getExecutionSet().getSignalRef());
        p.addSignalScope(definition.getExecutionSet().getSignalScope());

        return p;
    }

    protected PropertyWriter linkEvent(Node<View<IntermediateLinkEventThrowing>, ?> n) {
        IntermediateThrowEvent event = bpmn2.createIntermediateThrowEvent();
        event.setId(n.getUUID());

        IntermediateLinkEventThrowing definition = n.getContent().getDefinition();
        ThrowEventPropertyWriter p = propertyWriterFactory.of(event);

        p.setAbsoluteBounds(n);

        BPMNGeneralSet general = definition.getGeneral();
        p.setName(general.getName().getValue());
        p.setDocumentation(general.getDocumentation().getValue());
        p.setMetaData(definition.getAdvancedData().getMetaDataAttributes());

        p.setAssignmentsInfo(definition.getDataIOSet().getAssignmentsinfo());

        p.addLink(definition.getExecutionSet().getLinkRef());

        return p;
    }

    protected PropertyWriter messageEvent(Node<View<IntermediateMessageEventThrowing>, ?> n) {
        IntermediateThrowEvent event = bpmn2.createIntermediateThrowEvent();
        event.setId(n.getUUID());

        IntermediateMessageEventThrowing definition = n.getContent().getDefinition();

        ThrowEventPropertyWriter p = propertyWriterFactory.of(event);

        p.setAbsoluteBounds(n);

        BPMNGeneralSet general = definition.getGeneral();
        p.setName(general.getName().getValue());
        p.setDocumentation(general.getDocumentation().getValue());
        p.setMetaData(definition.getAdvancedData().getMetaDataAttributes());

        p.setAssignmentsInfo(definition.getDataIOSet().getAssignmentsinfo());

        MessageEventExecutionSet executionSet = definition.getExecutionSet();

        p.addMessage(executionSet.getMessageRef());

        return p;
    }

    protected PropertyWriter escalationEvent(Node<View<IntermediateEscalationEventThrowing>, ?> n) {
        IntermediateThrowEvent event = bpmn2.createIntermediateThrowEvent();
        event.setId(n.getUUID());

        IntermediateEscalationEventThrowing definition = n.getContent().getDefinition();

        ThrowEventPropertyWriter p = propertyWriterFactory.of(event);

        p.setAbsoluteBounds(n);

        BPMNGeneralSet general = definition.getGeneral();
        p.setName(general.getName().getValue());
        p.setDocumentation(general.getDocumentation().getValue());
        p.setMetaData(definition.getAdvancedData().getMetaDataAttributes());

        p.setAssignmentsInfo(definition.getDataIOSet().getAssignmentsinfo());

        p.addEscalation(definition.getExecutionSet().getEscalationRef());

        return p;
    }

    protected PropertyWriter compensationEvent(Node<View<IntermediateCompensationEventThrowing>, ?> n) {
        IntermediateThrowEvent event = bpmn2.createIntermediateThrowEvent();
        event.setId(n.getUUID());

        IntermediateCompensationEventThrowing definition = n.getContent().getDefinition();

        ThrowEventPropertyWriter p = propertyWriterFactory.of(event);

        p.setAbsoluteBounds(n);

        BPMNGeneralSet general = definition.getGeneral();
        p.setName(general.getName().getValue());
        p.setDocumentation(general.getDocumentation().getValue());
        p.setMetaData(definition.getAdvancedData().getMetaDataAttributes());

        p.setAssignmentsInfo(definition.getDataIOSet().getAssignmentsinfo());

        p.addCompensation();

        return p;
    }
}
