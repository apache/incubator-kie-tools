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

package org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.properties;

import java.util.Optional;

import org.eclipse.bpmn2.Activity;
import org.eclipse.bpmn2.CompensateEventDefinition;
import org.eclipse.bpmn2.Error;
import org.eclipse.bpmn2.ErrorEventDefinition;
import org.eclipse.bpmn2.Escalation;
import org.eclipse.bpmn2.EscalationEventDefinition;
import org.eclipse.bpmn2.Message;
import org.eclipse.bpmn2.MessageEventDefinition;

public class EventDefinitionReader {

    public static String errorRefOf(ErrorEventDefinition e) {
        return Optional.ofNullable(e.getErrorRef())
                .map(Error::getErrorCode)
                .orElse("");
    }

    public static String messageRefOf(MessageEventDefinition e) {
        return Optional.ofNullable(e.getMessageRef())
                .map(Message::getName)
                .orElse("");
    }

    public static String escalationRefOf(EscalationEventDefinition e) {
        return Optional.ofNullable(e.getEscalationRef())
                .map(Escalation::getEscalationCode)
                .orElse("");
    }

    public static String activityRefOf(CompensateEventDefinition e) {
        return Optional.ofNullable(e.getActivityRef())
                .map(Activity::getId)
                .orElse(null);
    }
}
