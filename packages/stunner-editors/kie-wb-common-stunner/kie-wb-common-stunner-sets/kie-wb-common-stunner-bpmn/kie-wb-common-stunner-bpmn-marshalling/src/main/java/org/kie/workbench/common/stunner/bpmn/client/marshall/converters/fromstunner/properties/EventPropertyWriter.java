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


package org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.properties;

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
import org.eclipse.bpmn2.LinkEventDefinition;
import org.eclipse.bpmn2.Message;
import org.eclipse.bpmn2.MessageEventDefinition;
import org.eclipse.bpmn2.Signal;
import org.eclipse.bpmn2.SignalEventDefinition;
import org.eclipse.bpmn2.TerminateEventDefinition;
import org.eclipse.bpmn2.TimerEventDefinition;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.customproperties.CustomAttribute;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.customproperties.CustomElement;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.Ids;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.properties.Scripts;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.util.FormalExpressionBodyHandler;
import org.kie.workbench.common.stunner.bpmn.definition.property.common.ConditionExpression;
import org.kie.workbench.common.stunner.bpmn.definition.property.dataio.AssignmentsInfo;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.error.ErrorRef;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.escalation.EscalationRef;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.link.LinkRef;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.message.MessageRef;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.signal.SignalRef;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.signal.SignalScope;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.timer.TimerSettings;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.timer.TimerSettingsValue;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.SLADueDate;
import org.kie.workbench.common.stunner.core.util.StringUtils;
import org.kie.workbench.common.stunner.core.util.UUID;

import static org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.Factories.bpmn2;
import static org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.properties.Scripts.asCData;

public abstract class EventPropertyWriter extends PropertyWriter {

    public EventPropertyWriter(Event event, VariableScope variableScope) {
        super(event, variableScope);
    }

    public abstract void setAssignmentsInfo(AssignmentsInfo assignmentsInfo);

    public Message addMessage(MessageRef messageRef) {
        if (this.getItemDefinitions().isEmpty()) {
            messageRef.setStructure("");
        } else {
            messageRef.setStructure(this.getItemDefinitions().get(0).getStructureRef());
        }

        MessageEventDefinition messageEventDefinition =
                bpmn2.createMessageEventDefinition();
        addEventDefinition(messageEventDefinition);

        String name = messageRef.getValue();
        if (name == null || name.isEmpty()) {
            return null;
        }

        ItemDefinition itemDefinition = bpmn2.createItemDefinition();
        itemDefinition.setId(Ids.messageItem(name));
        itemDefinition.setStructureRef(messageRef.getStructure());

        Message message = bpmn2.createMessage();
        message.setName(name);
        message.setItemRef(itemDefinition);
        messageEventDefinition.setMessageRef(message);
        CustomAttribute.msgref.of(messageEventDefinition).set(name);

        addItemDefinition(itemDefinition);
        addRootElement(message);

        return message;
    }

    public void addLink(LinkRef linkRef) {
        LinkEventDefinition linkEventDefinition =
                bpmn2.createLinkEventDefinition();
        linkEventDefinition.setName(linkRef.getValue());

        addEventDefinition(linkEventDefinition);
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
        String errorId;
        if (StringUtils.nonEmpty(errorCode)) {
            error.setErrorCode(errorCode);
            CustomAttribute.errorName.of(errorEventDefinition).set(errorCode);
            errorId = errorCode;
        } else {
            errorId = UUID.uuid();
        }

        error.setId(errorId);
        errorEventDefinition.setErrorRef(error);

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
            FormalExpressionBodyHandler.of(timeDate).setBody(date);
            eventDefinition.setTimeDate(timeDate);
        }

        String duration = timerSettingsValue.getTimeDuration();
        if (duration != null) {
            FormalExpression timeDuration = bpmn2.createFormalExpression();
            FormalExpressionBodyHandler.of(timeDuration).setBody(duration);
            eventDefinition.setTimeDuration(timeDuration);
        }

        String cycle = timerSettingsValue.getTimeCycle();
        String cycleLanguage = timerSettingsValue.getTimeCycleLanguage();
        if (cycle != null && cycleLanguage != null) {
            FormalExpression timeCycleExpression = bpmn2.createFormalExpression();
            FormalExpressionBodyHandler.of(timeCycleExpression).setBody(cycle);
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
            FormalExpressionBodyHandler.of(conditionExpression).setBody(asCData(conditionScript));
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

    public void addSlaDueDate(SLADueDate slaDueDate) {
        CustomElement.slaDueDate.of(flowElement).set(slaDueDate.getValue());
    }

    protected abstract void addEventDefinition(EventDefinition eventDefinition);
}
