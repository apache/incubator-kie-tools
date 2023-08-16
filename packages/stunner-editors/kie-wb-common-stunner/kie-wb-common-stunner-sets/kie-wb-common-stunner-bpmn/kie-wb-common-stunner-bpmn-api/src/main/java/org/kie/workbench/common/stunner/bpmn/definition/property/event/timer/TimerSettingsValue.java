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


package org.kie.workbench.common.stunner.bpmn.definition.property.event.timer;

import java.util.Objects;
import java.util.StringJoiner;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;
import org.kie.workbench.common.stunner.bpmn.forms.validation.timerEditor.ValidTimerSettingsValue;
import org.kie.workbench.common.stunner.core.util.HashUtil;

@ValidTimerSettingsValue
@Portable
@Bindable
public class TimerSettingsValue {

    private String timeDate;
    private String timeDuration;
    private String timeCycle;
    private String timeCycleLanguage;

    public TimerSettingsValue() {
    }

    public TimerSettingsValue(@MapsTo("timeDate") final String timeDate,
                              @MapsTo("timeDuration") final String timeDuration,
                              @MapsTo("timeCycle") final String timeCycle,
                              @MapsTo("timeCycleLanguage") final String timeCycleLanguage) {
        this.timeDate = timeDate;
        this.timeDuration = timeDuration;
        this.timeCycle = timeCycle;
        this.timeCycleLanguage = timeCycleLanguage;
    }

    public String getTimeDate() {
        return timeDate;
    }

    public void setTimeDate(String timeDate) {
        this.timeDate = timeDate;
    }

    public String getTimeDuration() {
        return timeDuration;
    }

    public void setTimeDuration(String timeDuration) {
        this.timeDuration = timeDuration;
    }

    public String getTimeCycle() {
        return timeCycle;
    }

    public void setTimeCycle(String timeCycle) {
        this.timeCycle = timeCycle;
    }

    public String getTimeCycleLanguage() {
        return timeCycleLanguage;
    }

    public void setTimeCycleLanguage(String timeCycleLanguage) {
        this.timeCycleLanguage = timeCycleLanguage;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof TimerSettingsValue) {
            TimerSettingsValue other = (TimerSettingsValue) o;
            return Objects.equals(timeDate, other.timeDate) &&
                    Objects.equals(timeDuration, other.timeDuration) &&
                    Objects.equals(timeCycle, other.timeCycle) &&
                    Objects.equals(timeCycleLanguage, other.timeCycleLanguage);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return HashUtil.combineHashCodes(Objects.hashCode(timeDate),
                                         Objects.hashCode(timeDuration),
                                         Objects.hashCode(timeCycle),
                                         Objects.hashCode(timeCycleLanguage));
    }

    @Override
    public String toString() {
        return new StringJoiner(" ")
                .add(Objects.nonNull(timeDate) ? timeDate : "")
                .add(Objects.nonNull(timeDuration) ? timeDuration : "")
                .add(Objects.nonNull(timeCycle) ? timeCycle : "")
                .add(Objects.nonNull(timeCycleLanguage) ? timeCycleLanguage : "")
                .toString()
                .trim();
    }
}