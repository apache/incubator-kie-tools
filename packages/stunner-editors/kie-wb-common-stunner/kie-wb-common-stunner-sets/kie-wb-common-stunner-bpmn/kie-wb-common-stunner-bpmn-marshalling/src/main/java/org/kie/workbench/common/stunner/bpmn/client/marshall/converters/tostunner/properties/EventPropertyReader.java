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


package org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.properties;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.bpmn2.ConditionalEventDefinition;
import org.eclipse.bpmn2.Event;
import org.eclipse.bpmn2.EventDefinition;
import org.eclipse.bpmn2.Expression;
import org.eclipse.bpmn2.FormalExpression;
import org.eclipse.bpmn2.LinkEventDefinition;
import org.eclipse.bpmn2.SignalEventDefinition;
import org.eclipse.bpmn2.TimerEventDefinition;
import org.eclipse.bpmn2.di.BPMNDiagram;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.customproperties.CustomElement;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.DefinitionResolver;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.util.FormalExpressionBodyHandler;
import org.kie.workbench.common.stunner.bpmn.definition.property.common.ConditionExpression;
import org.kie.workbench.common.stunner.bpmn.definition.property.dataio.AssignmentsInfo;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.timer.TimerSettingsValue;
import org.kie.workbench.common.stunner.bpmn.definition.property.simulation.SimulationAttributeSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.ScriptTypeValue;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;

public abstract class EventPropertyReader extends FlowElementPropertyReader {

    // These values are present in the SVG declaration for the event shape.
    static final double WIDTH = 56d;
    static final double HEIGHT = 56d;

    protected final DefinitionResolver definitionResolver;

    EventPropertyReader(Event element, BPMNDiagram diagram, DefinitionResolver definitionResolver) {
        super(element, diagram, definitionResolver.getShape(element.getId()), definitionResolver.getResolutionFactor());
        this.definitionResolver = definitionResolver;
    }

    @Override
    protected Bounds computeBounds(final org.eclipse.dd.dc.Bounds bounds) {
        final double x = bounds.getX() * resolutionFactor;
        final double y = bounds.getY() * resolutionFactor;
        return Bounds.create(x, y, x + WIDTH, y + HEIGHT);
    }

    public String getSignalScope() {
        return CustomElement.scope.of(element).get();
    }

    public abstract List<EventDefinition> getEventDefinitions();

    public abstract AssignmentsInfo getAssignmentsInfo();

    public static TimerSettingsValue getTimerSettings(TimerEventDefinition eventDefinition) {
        TimerSettingsValue timerSettingsValue = new TimerSettingsValue();
        toFormalExpression(eventDefinition.getTimeCycle()).ifPresent(timeCycle -> {
            timerSettingsValue.setTimeCycle(FormalExpressionBodyHandler.of(timeCycle).getBody());
            timerSettingsValue.setTimeCycleLanguage(timeCycle.getLanguage());
        });

        toFormalExpression(eventDefinition.getTimeDate()).ifPresent(timeDate -> {
            timerSettingsValue.setTimeDate(FormalExpressionBodyHandler.of(timeDate).getBody());
        });

        toFormalExpression(eventDefinition.getTimeDuration()).ifPresent(timeDateDuration -> {
            timerSettingsValue.setTimeDuration(FormalExpressionBodyHandler.of(timeDateDuration).getBody());
        });
        return timerSettingsValue;
    }

    private static Optional<FormalExpression> toFormalExpression(Expression e) {
        if (e instanceof FormalExpression) {
            return Optional.of((FormalExpression) e);
        } else {
            return Optional.empty();
        }
    }

    public String getSignalRef() {
        List<EventDefinition> eventDefinitions = getEventDefinitions();
        if (eventDefinitions.size() == 1 && eventDefinitions.get(0) instanceof SignalEventDefinition) {
            String signalRefId = ((SignalEventDefinition) eventDefinitions.get(0)).getSignalRef();
            return signalRefId != null ? definitionResolver.resolveSignalName(signalRefId) : "";
        }
        return "";
    }

    public String getLinkRef() {
        List<EventDefinition> eventDefinitions = getEventDefinitions();
        if (eventDefinitions.size() == 1 && eventDefinitions.get(0) instanceof LinkEventDefinition) {
            String linkRef = ((LinkEventDefinition) eventDefinitions.get(0)).getName();
            return linkRef != null ? linkRef : "";
        }
        return "";
    }

    public SimulationAttributeSet getSimulationSet() {
        return definitionResolver.resolveSimulationParameters(element.getId())
                .map(SimulationAttributeSets::of)
                .orElse(new SimulationAttributeSet());
    }

    public String getSlaDueDate() {
        return CustomElement.slaDueDate.of(element).get();
    }

    public static ConditionExpression getConditionExpression(ConditionalEventDefinition conditionalEvent) {
        if (conditionalEvent.getCondition() instanceof FormalExpression) {
            FormalExpression formalExpression = (FormalExpression) conditionalEvent.getCondition();
            String language = Scripts.scriptLanguageFromUri(formalExpression.getLanguage(), Scripts.LANGUAGE.DROOLS.language());
            String script = FormalExpressionBodyHandler.of(formalExpression).getBody();
            return new ConditionExpression(new ScriptTypeValue(language, script));
        } else {
            return new ConditionExpression(new ScriptTypeValue(Scripts.LANGUAGE.DROOLS.language(), ""));
        }
    }

    protected static List<EventDefinition> combineEventDefinitions(List<EventDefinition> eventDefinitions, List<EventDefinition> eventDefinitionRefs) {
        //combine the event definitions by filtering the eventDefinitionRefs that points to nowhere for avoiding edge
        //cases detected when importing bpmn files generated in ARIS (https://issues.jboss.org/browse/JBPM-6758).
        //Stunner doesn't generate eventDefinitionRefs so this filtering can't introduce issues in Stunner.
        return Stream.concat(eventDefinitions.stream(),
                             eventDefinitionRefs.stream()
                                     .filter(Objects::nonNull)
                                     .filter(eventDefinition -> Objects.nonNull(eventDefinition.getId())))
                .collect(Collectors.toList());
    }
}
