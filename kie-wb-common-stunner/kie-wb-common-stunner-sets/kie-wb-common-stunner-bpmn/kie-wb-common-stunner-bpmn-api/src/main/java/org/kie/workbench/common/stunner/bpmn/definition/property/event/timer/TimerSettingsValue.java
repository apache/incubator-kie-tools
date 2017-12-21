/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.definition.property.event.timer;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;
import org.kie.workbench.common.stunner.bpmn.forms.validation.ValidTimerSettingsValue;
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
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        TimerSettingsValue that = (TimerSettingsValue) o;

        if (timeDate != null ? !timeDate.equals(that.timeDate) : that.timeDate != null) {
            return false;
        }
        if (timeDuration != null ? !timeDuration.equals(that.timeDuration) : that.timeDuration != null) {
            return false;
        }
        if (timeCycle != null ? !timeCycle.equals(that.timeCycle) : that.timeCycle != null) {
            return false;
        }
        return timeCycleLanguage != null ? timeCycleLanguage.equals(that.timeCycleLanguage) : that.timeCycleLanguage == null;
    }

    @Override
    public int hashCode() {
        return HashUtil.combineHashCodes(timeDate != null ? timeDate.hashCode() : 0,
                                         timeDuration != null ? timeDuration.hashCode() : 0,
                                         timeCycle != null ? timeCycle.hashCode() : 0,
                                         timeCycleLanguage != null ? timeCycleLanguage.hashCode() : 0);
    }
}
