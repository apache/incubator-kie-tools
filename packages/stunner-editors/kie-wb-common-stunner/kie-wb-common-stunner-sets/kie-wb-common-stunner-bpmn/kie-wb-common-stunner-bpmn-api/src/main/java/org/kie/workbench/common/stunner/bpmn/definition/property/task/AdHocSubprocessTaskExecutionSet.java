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


package org.kie.workbench.common.stunner.bpmn.definition.property.task;

import java.util.Objects;

import javax.validation.Valid;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;
import org.kie.workbench.common.forms.adf.definitions.annotations.FieldParam;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormDefinition;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormField;
import org.kie.workbench.common.forms.adf.definitions.annotations.field.selector.SelectorDataProvider;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.listBox.type.ListBoxFieldType;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textArea.type.TextAreaFieldType;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.SLADueDate;
import org.kie.workbench.common.stunner.core.definition.annotation.Property;
import org.kie.workbench.common.stunner.core.util.HashUtil;

@Portable
@Bindable
@FormDefinition(startElement = "adHocActivationCondition")
public class AdHocSubprocessTaskExecutionSet
        extends BaseSubprocessTaskExecutionSet
        implements BaseAdHocSubprocessTaskExecutionSet {

    @Property
    @FormField(type = TextAreaFieldType.class,
            settings = {@FieldParam(name = "rows", value = "5")})
    @Valid
    private AdHocActivationCondition adHocActivationCondition;

    @Property
    @FormField(afterElement = "adHocActivationCondition",
            settings = {@FieldParam(name = "mode", value = "COMPLETION_CONDITION")})
    @Valid
    private AdHocCompletionCondition adHocCompletionCondition;

    @Property
    @FormField(afterElement = "adHocCompletionCondition",
            type = ListBoxFieldType.class,
            settings = {@FieldParam(name = "addEmptyOption", value = "false")}
    )
    @SelectorDataProvider(
            type = SelectorDataProvider.ProviderType.CLIENT,
            className = "org.kie.workbench.common.stunner.bpmn.client.dataproviders.ExecutionOrderProvider")
    @Valid
    private AdHocOrdering adHocOrdering;

    @Property
    @FormField(afterElement = "adHocOrdering")
    @Valid
    private AdHocAutostart adHocAutostart;

    @Property
    @FormField(afterElement = "adHocAutostart",
            settings = {@FieldParam(name = "mode", value = "ACTION_SCRIPT")}
    )
    @Valid
    private OnEntryAction onEntryAction;

    @Property
    @FormField(afterElement = "onEntryAction",
            settings = {@FieldParam(name = "mode", value = "ACTION_SCRIPT")}
    )
    @Valid
    private OnExitAction onExitAction;

    public AdHocSubprocessTaskExecutionSet() {
        this(new AdHocActivationCondition(),
             new AdHocCompletionCondition(new ScriptTypeValue("mvel", "autocomplete")),
             new AdHocOrdering("Sequential"),
             new AdHocAutostart(),
             new OnEntryAction(new ScriptTypeListValue().addValue(new ScriptTypeValue("java", ""))),
             new OnExitAction(new ScriptTypeListValue().addValue(new ScriptTypeValue("java", ""))),
             new IsAsync(),
             new SLADueDate());
    }

    public AdHocSubprocessTaskExecutionSet(final @MapsTo("adHocActivationCondition") AdHocActivationCondition adHocActivationCondition,
                                           final @MapsTo("adHocCompletionCondition") AdHocCompletionCondition adHocCompletionCondition,
                                           final @MapsTo("adHocOrdering") AdHocOrdering adHocOrdering,
                                           final @MapsTo("adHocAutostart") AdHocAutostart adHocAutostart,
                                           final @MapsTo("onEntryAction") OnEntryAction onEntryAction,
                                           final @MapsTo("onExitAction") OnExitAction onExitAction,
                                           final @MapsTo("isAsync") IsAsync isAsync,
                                           final @MapsTo("slaDueDate") SLADueDate slaDueDate) {
        super(isAsync, slaDueDate);
        this.adHocActivationCondition = adHocActivationCondition;
        this.adHocCompletionCondition = adHocCompletionCondition;
        this.adHocOrdering = adHocOrdering;
        this.adHocAutostart = adHocAutostart;
        this.onEntryAction = onEntryAction;
        this.onEntryAction = onEntryAction;
        this.onExitAction = onExitAction;
    }

    @Override
    public AdHocActivationCondition getAdHocActivationCondition() {
        return adHocActivationCondition;
    }

    public void setAdHocActivationCondition(AdHocActivationCondition adHocActivationCondition) {
        this.adHocActivationCondition = adHocActivationCondition;
    }

    @Override
    public AdHocCompletionCondition getAdHocCompletionCondition() {
        return adHocCompletionCondition;
    }

    public void setAdHocCompletionCondition(AdHocCompletionCondition adHocCompletionCondition) {
        this.adHocCompletionCondition = adHocCompletionCondition;
    }

    @Override
    public AdHocOrdering getAdHocOrdering() {
        return adHocOrdering;
    }

    public void setAdHocOrdering(AdHocOrdering adHocOrdering) {
        this.adHocOrdering = adHocOrdering;
    }

    @Override
    public AdHocAutostart getAdHocAutostart() {
        return adHocAutostart;
    }

    public void setAdHocAutostart(AdHocAutostart adHocAutostart) {
        this.adHocAutostart = adHocAutostart;
    }

    @Override
    public OnEntryAction getOnEntryAction() {
        return onEntryAction;
    }

    public void setOnEntryAction(OnEntryAction onEntryAction) {
        this.onEntryAction = onEntryAction;
    }

    @Override
    public OnExitAction getOnExitAction() {
        return onExitAction;
    }

    public void setOnExitAction(OnExitAction onExitAction) {
        this.onExitAction = onExitAction;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof AdHocSubprocessTaskExecutionSet) {
            AdHocSubprocessTaskExecutionSet other = (AdHocSubprocessTaskExecutionSet) o;
            return super.equals(other) &&
                    Objects.equals(adHocActivationCondition, other.adHocActivationCondition) &&
                    Objects.equals(adHocCompletionCondition, other.adHocCompletionCondition) &&
                    Objects.equals(adHocOrdering, other.adHocOrdering) &&
                    Objects.equals(adHocAutostart, other.adHocAutostart) &&
                    Objects.equals(onEntryAction, other.onEntryAction) &&
                    Objects.equals(onExitAction, other.onExitAction);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return HashUtil.combineHashCodes(super.hashCode(),
                                         Objects.hashCode(adHocActivationCondition),
                                         Objects.hashCode(adHocCompletionCondition),
                                         Objects.hashCode(adHocOrdering),
                                         Objects.hashCode(adHocAutostart),
                                         Objects.hashCode(onEntryAction),
                                         Objects.hashCode(onExitAction));
    }
}
