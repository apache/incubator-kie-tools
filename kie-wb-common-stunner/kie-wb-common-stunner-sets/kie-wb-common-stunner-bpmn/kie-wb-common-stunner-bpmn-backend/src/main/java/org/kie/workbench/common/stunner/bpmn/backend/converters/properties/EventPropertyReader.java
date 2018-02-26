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

package org.kie.workbench.common.stunner.bpmn.backend.converters.properties;

import java.util.List;
import java.util.Objects;

import org.eclipse.bpmn2.BoundaryEvent;
import org.eclipse.bpmn2.CatchEvent;
import org.eclipse.bpmn2.Event;
import org.eclipse.bpmn2.EventDefinition;
import org.eclipse.bpmn2.SignalEventDefinition;
import org.eclipse.bpmn2.ThrowEvent;
import org.eclipse.bpmn2.TimerEventDefinition;
import org.eclipse.bpmn2.di.BPMNPlane;
import org.kie.workbench.common.stunner.bpmn.backend.converters.DefinitionResolver;
import org.kie.workbench.common.stunner.bpmn.backend.converters.events.TimerEventDefinitionConverter;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.timer.TimerSettingsValue;
import org.kie.workbench.common.stunner.bpmn.definition.property.simulation.SimulationAttributeSet;

public abstract class EventPropertyReader extends FlowElementPropertyReader {

    private final DefinitionResolver definitionResolver;
    private String signalRefId;

    public static EventPropertyReader of(Event el, BPMNPlane plane, DefinitionResolver definitionResolver) {
        if (el instanceof BoundaryEvent) {
            return new BoundaryEventPropertyReader((BoundaryEvent) el, plane, definitionResolver);
        } else if (el instanceof CatchEvent) {
            CatchEvent catchEvent = (CatchEvent) el;
            return new CatchEventPropertyReader(catchEvent, plane, definitionResolver);
        } else if (el instanceof ThrowEvent) {
            ThrowEvent throwEvent = (ThrowEvent) el;
            return new ThrowEventPropertyReader(throwEvent, plane, definitionResolver);
        } else {
            throw new IllegalArgumentException(el.toString());
        }
    }

    static String getSignalRefId(List<EventDefinition> eventDefinitions) {
        if (eventDefinitions.size() == 1 && eventDefinitions.get(0) instanceof SignalEventDefinition) {
            return ((SignalEventDefinition) eventDefinitions.get(0)).getSignalRef();
        }
        return null;
    }

    EventPropertyReader(Event element, BPMNPlane plane, DefinitionResolver definitionResolver, String eventDefinition) {
        super(element, plane);
        this.definitionResolver = definitionResolver;
        this.signalRefId = eventDefinition;
    }

    public String getSignalScope() {
        return metaData("customScope");
    }

    public abstract String getAssignmentsInfo();

    public boolean isCancelActivity() {
        return optionalAttribute("boundaryca")
                .filter(s -> !s.isEmpty())
                .map(Boolean::parseBoolean)
                .orElse(true);
    }

    public TimerSettingsValue getTimerSettings(TimerEventDefinition eventDefinition) {
        return TimerEventDefinitionConverter.convertTimerEventDefinition(eventDefinition);
    }

    public String getSignalRef() {
        Objects.requireNonNull(signalRefId);
        return definitionResolver.resolveSignalName(signalRefId);
    }

    public SimulationAttributeSet getSimulationSet() {
        return definitionResolver.extractSimulationAttributeSet(element.getId());
    }
}
