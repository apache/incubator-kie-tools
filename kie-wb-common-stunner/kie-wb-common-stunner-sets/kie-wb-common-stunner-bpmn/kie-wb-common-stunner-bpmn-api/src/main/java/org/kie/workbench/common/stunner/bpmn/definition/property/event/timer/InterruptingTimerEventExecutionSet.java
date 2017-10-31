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
import org.kie.workbench.common.forms.adf.definitions.annotations.field.selector.SelectorDataProvider;
import org.kie.workbench.common.forms.adf.definitions.annotations.metaModel.FieldLabel;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.checkBox.type.CheckBoxFieldType;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.listBox.type.ListBoxFieldType;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNPropertySet;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.IsInterrupting;
import org.kie.workbench.common.stunner.core.definition.annotation.Name;
import org.kie.workbench.common.stunner.core.definition.annotation.Property;
import org.kie.workbench.common.stunner.core.definition.annotation.PropertySet;
import org.kie.workbench.common.stunner.core.util.HashUtil;

@Portable
@Bindable
@PropertySet
@FormDefinition(startElement = "isInterrupting")
public class InterruptingTimerEventExecutionSet implements BPMNPropertySet {

    @Name
    @FieldLabel
    public static final transient String propertySetName = "Implementation/Execution";

    @Property
    @FormField(type = CheckBoxFieldType.class)
    @Valid
    private IsInterrupting isInterrupting;

    @Property
    @FormField(afterElement = "isInterrupting")
    @Valid
    private TimeCycle timeCycle;

    @Property
    @FormField(
            type = ListBoxFieldType.class,
            afterElement = "timeCycle"
    )
    @SelectorDataProvider(
            type = SelectorDataProvider.ProviderType.REMOTE,
            className = "org.kie.workbench.common.stunner.bpmn.backend.dataproviders.TimeCycleLanguageProvider")
    @Valid
    private TimeCycleLanguage timeCycleLanguage;

    @Property
    @FormField(afterElement = "timeCycleLanguage")
    @Valid
    private TimeDate timeDate;

    @Property
    @FormField(afterElement = "timeDate")
    @Valid
    private TimeDuration timeDuration;

    public InterruptingTimerEventExecutionSet() {
        this(new IsInterrupting(true),
             new TimeCycle(),
             new TimeCycleLanguage(),
             new TimeDate(),
             new TimeDuration());
    }

    public InterruptingTimerEventExecutionSet(final @MapsTo("isInterrupting") IsInterrupting isInterrupting,
                                              final @MapsTo("timeCycle") TimeCycle timeCycle,
                                              final @MapsTo("timeCycleLanguage") TimeCycleLanguage timeCycleLanguage,
                                              final @MapsTo("timeDate") TimeDate timeDate,
                                              final @MapsTo("timeDuration") TimeDuration timeDuration) {
        this.isInterrupting = isInterrupting;
        this.timeCycle = timeCycle;
        this.timeCycleLanguage = timeCycleLanguage;
        this.timeDate = timeDate;
        this.timeDuration = timeDuration;
    }

    public String getPropertySetName() {
        return propertySetName;
    }

    public IsInterrupting getIsInterrupting() {
        return isInterrupting;
    }

    public void setIsInterrupting(IsInterrupting isInterrupting) {
        this.isInterrupting = isInterrupting;
    }

    public TimeCycle getTimeCycle() {
        return timeCycle;
    }

    public void setTimeCycle(final TimeCycle timeCycle) {
        this.timeCycle = timeCycle;
    }

    public TimeCycleLanguage getTimeCycleLanguage() {
        return timeCycleLanguage;
    }

    public void setTimeCycleLanguage(final TimeCycleLanguage timeCycleLanguage) {
        this.timeCycleLanguage = timeCycleLanguage;
    }

    public TimeDate getTimeDate() {
        return timeDate;
    }

    public void setTimeDate(final TimeDate timeDate) {
        this.timeDate = timeDate;
    }

    public TimeDuration getTimeDuration() {
        return timeDuration;
    }

    public void setTimeDuration(final TimeDuration timeDuration) {
        this.timeDuration = timeDuration;
    }

    @Override
    public int hashCode() {
        return HashUtil.combineHashCodes(isInterrupting.hashCode(),
                                         timeCycle.hashCode(),
                                         timeCycleLanguage.hashCode(),
                                         timeDate.hashCode(),
                                         timeDuration.hashCode());
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof InterruptingTimerEventExecutionSet) {
            InterruptingTimerEventExecutionSet other = (InterruptingTimerEventExecutionSet) o;
            return isInterrupting.equals(other.isInterrupting) &&
                    timeCycle.equals(other.timeCycle) &&
                    timeCycleLanguage.equals(other.timeCycleLanguage) &&
                    timeDate.equals(other.timeDate) &&
                    timeDuration.equals(other.timeDuration);
        }
        return false;
    }
}
