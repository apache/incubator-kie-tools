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

package org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.properties;

import org.eclipse.bpmn2.CompensateEventDefinition;
import org.eclipse.bpmn2.ConditionalEventDefinition;
import org.eclipse.bpmn2.Error;
import org.eclipse.bpmn2.ErrorEventDefinition;
import org.eclipse.bpmn2.Escalation;
import org.eclipse.bpmn2.EscalationEventDefinition;
import org.eclipse.bpmn2.Event;
import org.eclipse.bpmn2.EventDefinition;
import org.eclipse.bpmn2.FormalExpression;
import org.eclipse.bpmn2.ItemDefinition;
import org.eclipse.bpmn2.Message;
import org.eclipse.bpmn2.MessageEventDefinition;
import org.eclipse.bpmn2.Signal;
import org.eclipse.bpmn2.SignalEventDefinition;
import org.eclipse.bpmn2.TerminateEventDefinition;
import org.eclipse.bpmn2.TimerEventDefinition;
import org.kie.workbench.common.stunner.bpmn.backend.converters.customproperties.CustomAttribute;
import org.kie.workbench.common.stunner.bpmn.backend.converters.customproperties.CustomElement;
import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.Ids;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.properties.Scripts;
import org.kie.workbench.common.stunner.bpmn.definition.property.common.ConditionExpression;
import org.kie.workbench.common.stunner.bpmn.definition.property.dataio.AssignmentsInfo;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.error.ErrorRef;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.escalation.EscalationRef;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.message.MessageRef;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.signal.SignalRef;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.signal.SignalScope;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.timer.TimerSettings;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.timer.TimerSettingsValue;

import static org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.Factories.bpmn2;
import static org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.properties.Scripts.asCData;

public abstract class EventPropertyWriter extends PropertyWriter {

    public EventPropertyWriter(Event event, VariableScope variableScope) {
        super(event, variableScope);
    }

    public abstract void setAssignmentsInfo(AssignmentsInfo assignmentsInfo);

    public void addMessage(MessageRef messageRef) {
        MessageEventDefinition messageEventDefinition =
                bpmn2.createMessageEventDefinition();
        addEventDefinition(messageEventDefinition);

        String name = messageRef.getValue();
        if (name == null || name.isEmpty()) {
            return;
        }

        ItemDefinition itemDefinition = bpmn2.createItemDefinition();
        itemDefinition.setId(Ids.messageItem(name));

        Message message = bpmn2.createMessage();
        message.setName(name);
        message.setItemRef(itemDefinition);
        messageEventDefinition.setMessageRef(message);
        CustomAttribute.msgref.of(messageEventDefinition).set(name);

        addItemDefinition(itemDefinition);
        addRootElement(message);
    }

    public void addSignal(SignalRef signalRef) {
        SignalEventDefinition signalEventDefinition =
                bpmn2.createSignalEventDefinition();
        addEventDefinition(signalEventDefinition);

        Signal signal = bpmn2.createSignal();
        String name = signalRef.getValue();
        if (name == null || name.isEmpty()) {
            return;
        }

        signal.setName(name);
        signal.setId(Ids.fromString(name));
        signalEventDefinition.setSignalRef(signal.getId());

        addRootElement(signal);
    }

    public void addSignalScope(SignalScope signalScope) {
        CustomElement.scope.of(flowElement).set(signalScope.getValue());
    }

    public void addError(ErrorRef errorRef) {
        Error error = bpmn2.createError();
        ErrorEventDefinition errorEventDefinition =
                bpmn2.createErrorEventDefinition();
        addEventDefinition(errorEventDefinition);

        String errorCode = errorRef.getValue();
        if (errorCode == null || errorCode.isEmpty()) {
            return;
        }

        error.setId(errorCode);
        error.setErrorCode(errorCode);
        errorEventDefinition.setErrorRef(error);

        CustomAttribute.errorName.of(errorEventDefinition).set(errorCode);
        addRootElement(error);
    }

    public void addTerminate() {
        TerminateEventDefinition terminateEventDefinition =
                bpmn2.createTerminateEventDefinition();
        addEventDefinition(terminateEventDefinition);
    }

    public void addTimer(TimerSettings timerSettings) {
        TimerEventDefinition eventDefinition =
                bpmn2.createTimerEventDefinition();

        TimerSettingsValue timerSettingsValue = timerSettings.getValue();

        String date = timerSettingsValue.getTimeDate();
        if (date != null) {
            FormalExpression timeDate = bpmn2.createFormalExpression();
            timeDate.setBody(date);
            eventDefinition.setTimeDate(timeDate);
        }

        String duration = timerSettingsValue.getTimeDuration();
        if (duration != null) {
            FormalExpression timeDuration = bpmn2.createFormalExpression();
            timeDuration.setBody(duration);
            eventDefinition.setTimeDuration(timeDuration);
        }

        String cycle = timerSettingsValue.getTimeCycle();
        String cycleLanguage = timerSettingsValue.getTimeCycleLanguage();
        if (cycle != null && cycleLanguage != null) {
            FormalExpression timeCycleExpression = bpmn2.createFormalExpression();
            timeCycleExpression.setBody(cycle);
            timeCycleExpression.setLanguage(cycleLanguage);
            eventDefinition.setTimeCycle(timeCycleExpression);
        }

        addEventDefinition(eventDefinition);
    }

    public void addCondition(ConditionExpression condition) {
        ConditionalEventDefinition conditionalEventDefinition = bpmn2.createConditionalEventDefinition();
        FormalExpression conditionExpression = bpmn2.createFormalExpression();

        String languageFormat = Scripts.scriptLanguageToUri(condition.getValue().getLanguage(),
                                                            Scripts.LANGUAGE.DROOLS.format());
        conditionExpression.setLanguage(languageFormat);

        String conditionScript = condition.getValue().getScript();
        if (conditionScript != null && !conditionScript.isEmpty()) {
            conditionExpression.setBody(asCData(conditionScript));
        }

        conditionalEventDefinition.setCondition(conditionExpression);
        addEventDefinition(conditionalEventDefinition);
    }

    public void addEscalation(EscalationRef escalationRef) {
        EscalationEventDefinition escalationEventDefinition =
                bpmn2.createEscalationEventDefinition();
        addEventDefinition(escalationEventDefinition);

        Escalation escalation = bpmn2.createEscalation();
        String escalationCode = escalationRef.getValue();
        if (escalationCode == null || escalationCode.isEmpty()) {
            return;
        }

        escalation.setId(Ids.fromString(escalationCode));
        escalation.setEscalationCode(escalationCode);
        escalationEventDefinition.setEscalationRef(escalation);

        CustomAttribute.esccode.of(escalationEventDefinition).set(escalationCode);
        addRootElement(escalation);
    }

    public void addCompensation() {
        CompensateEventDefinition compensationEventDefinition =
                bpmn2.createCompensateEventDefinition();
        addEventDefinition(compensationEventDefinition);
    }

    protected abstract void addEventDefinition(EventDefinition eventDefinition);
}
