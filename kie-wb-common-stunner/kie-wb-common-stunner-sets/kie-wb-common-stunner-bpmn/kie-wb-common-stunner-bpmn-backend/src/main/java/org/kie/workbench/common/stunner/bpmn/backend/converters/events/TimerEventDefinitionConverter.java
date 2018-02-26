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

import org.eclipse.bpmn2.FormalExpression;
import org.eclipse.bpmn2.TimerEventDefinition;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.timer.TimerSettings;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.timer.TimerSettingsValue;

public class TimerEventDefinitionConverter {

    public static TimerSettingsValue convertTimerEventDefinition(TimerEventDefinition e) {
        TimerSettingsValue timerSettingsValue = new TimerSettings().getValue();
        FormalExpression timeCycle = (FormalExpression) e.getTimeCycle();
        if (timeCycle != null) {
            timerSettingsValue.setTimeCycle(timeCycle.getMixed().getValue(0).toString());
            timerSettingsValue.setTimeCycleLanguage(timeCycle.getLanguage());
        }

        FormalExpression timeDate = (FormalExpression) e.getTimeDate();
        if (timeDate != null) {
            timerSettingsValue.setTimeDate(timeDate.getMixed().getValue(0).toString());
        }

        FormalExpression timeDateDuration = (FormalExpression) e.getTimeDuration();
        if (timeDateDuration != null) {
            timerSettingsValue.setTimeDuration(timeDateDuration.getMixed().getValue(0).toString());
        }
        return (timerSettingsValue);
    }
}
