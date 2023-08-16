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

import javax.validation.Valid;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormDefinition;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormField;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.BaseStartEventExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.IsInterrupting;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.SLADueDate;
import org.kie.workbench.common.stunner.core.definition.annotation.Property;
import org.kie.workbench.common.stunner.core.util.HashUtil;

@Portable
@Bindable
@FormDefinition(startElement = "isInterrupting")
public class InterruptingTimerEventExecutionSet extends BaseStartEventExecutionSet {

    @Property
    @FormField(afterElement = "isInterrupting")
    @Valid
    private TimerSettings timerSettings;

    public InterruptingTimerEventExecutionSet() {
        this(new IsInterrupting(true),
             new SLADueDate(),
             new TimerSettings());
    }

    public InterruptingTimerEventExecutionSet(final @MapsTo("isInterrupting") IsInterrupting isInterrupting,
                                              final @MapsTo("slaDueDate") SLADueDate slaDueDate,
                                              final @MapsTo("timerSettings") TimerSettings timerSettings) {
        super(isInterrupting, slaDueDate);
        this.timerSettings = timerSettings;
    }

    public TimerSettings getTimerSettings() {
        return timerSettings;
    }

    public void setTimerSettings(TimerSettings timerSettings) {
        this.timerSettings = timerSettings;
    }

    @Override
    public int hashCode() {
        return HashUtil.combineHashCodes(super.hashCode(),
                                         Objects.hashCode(timerSettings));
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof InterruptingTimerEventExecutionSet) {
            InterruptingTimerEventExecutionSet other = (InterruptingTimerEventExecutionSet) o;
            return super.equals(other) &&
                    Objects.equals(timerSettings, other.timerSettings);
        }
        return false;
    }
}
