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

package org.kie.workbench.common.stunner.bpmn.backend.converters.events;

import java.util.List;

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
import org.kie.workbench.common.stunner.bpmn.backend.converters.properties.PropertyReaderFactory;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNViewDefinition;
import org.kie.workbench.common.stunner.bpmn.definition.BaseCatchingIntermediateEvent;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

public class IntermediateCatchEventConverter {

    private final IntermediateSignalEventCatchingConverter intermediateSignalEventCatchingConverter;
    private final IntermediateMessageEventCatchingConverter intermediateMessageEventCatchingConverter;
    private final IntermediateTimerEventConverter intermediateTimerEventConverter;
    private final IntermediateErrorEventCatchingConverter intermediateErrorEventCatchingConverter;

    public IntermediateCatchEventConverter(TypedFactoryManager factoryManager, PropertyReaderFactory propertyReaderFactory) {
        this.intermediateSignalEventCatchingConverter = new IntermediateSignalEventCatchingConverter(factoryManager, propertyReaderFactory);
        this.intermediateMessageEventCatchingConverter = new IntermediateMessageEventCatchingConverter(factoryManager, propertyReaderFactory);
        this.intermediateTimerEventConverter = new IntermediateTimerEventConverter(factoryManager, propertyReaderFactory);
        this.intermediateErrorEventCatchingConverter = new IntermediateErrorEventCatchingConverter(factoryManager, propertyReaderFactory);
    }

    public Node<? extends View<? extends BPMNViewDefinition>, ?> convert(IntermediateCatchEvent event) {
        List<EventDefinition> eventDefinitions = event.getEventDefinitions();
        switch (eventDefinitions.size()) {
            case 0:
                throw new UnsupportedOperationException("An intermediate catch event should contain exactly one definition");
            case 1:
                return Match.ofNode(EventDefinition.class, BaseCatchingIntermediateEvent.class)
                        .when(TimerEventDefinition.class, e -> intermediateTimerEventConverter.convert(event, e))
                        .when(SignalEventDefinition.class, e -> intermediateSignalEventCatchingConverter.convert(event, e))
                        .when(MessageEventDefinition.class, e -> intermediateMessageEventCatchingConverter.convert(event, e))
                        .when(ErrorEventDefinition.class, e -> intermediateErrorEventCatchingConverter.convert(event, e))
                        .missing(EscalationEventDefinition.class)
                        .missing(CompensateEventDefinition.class)
                        .missing(ConditionalEventDefinition.class)
                        .apply(eventDefinitions.get(0)).asSuccess().value();
            default:
                throw new UnsupportedOperationException("Multiple definitions not supported for intermediate catch event");
        }
    }
}
