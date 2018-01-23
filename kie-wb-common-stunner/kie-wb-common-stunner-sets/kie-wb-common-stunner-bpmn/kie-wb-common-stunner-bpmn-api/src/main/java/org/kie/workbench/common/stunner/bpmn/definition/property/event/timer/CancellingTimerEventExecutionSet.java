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

import javax.validation.Valid;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormDefinition;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormField;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNPropertySet;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.CancelActivity;
import org.kie.workbench.common.stunner.core.definition.annotation.Property;
import org.kie.workbench.common.stunner.core.definition.annotation.PropertySet;
import org.kie.workbench.common.stunner.core.util.HashUtil;

@Portable
@Bindable
@PropertySet
@FormDefinition(startElement = "cancelActivity")
public class CancellingTimerEventExecutionSet implements BPMNPropertySet {

    @Property
    @FormField
    private CancelActivity cancelActivity;

    @Property
    @FormField(afterElement = "cancelActivity")
    @Valid
    private TimerSettings timerSettings;

    public CancellingTimerEventExecutionSet() {
        this(new CancelActivity(),
             new TimerSettings());
    }

    public CancellingTimerEventExecutionSet(final @MapsTo("cancelActivity") CancelActivity cancelActivity,
                                            final @MapsTo("timerSettings") TimerSettings timerSettings) {
        this.cancelActivity = cancelActivity;
        this.timerSettings = timerSettings;
    }

    public CancelActivity getCancelActivity() {
        return cancelActivity;
    }

    public void setCancelActivity(CancelActivity cancelActivity) {
        this.cancelActivity = cancelActivity;
    }

    public TimerSettings getTimerSettings() {
        return timerSettings;
    }

    public void setTimerSettings(TimerSettings timerSettings) {
        this.timerSettings = timerSettings;
    }

    @Override
    public int hashCode() {
        return HashUtil.combineHashCodes(cancelActivity.hashCode(),
                                         timerSettings.hashCode());
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof CancellingTimerEventExecutionSet) {
            CancellingTimerEventExecutionSet other = (CancellingTimerEventExecutionSet) o;
            return cancelActivity.equals(other.cancelActivity) &&
                    timerSettings.equals(other.timerSettings);
        }
        return false;
    }
}
