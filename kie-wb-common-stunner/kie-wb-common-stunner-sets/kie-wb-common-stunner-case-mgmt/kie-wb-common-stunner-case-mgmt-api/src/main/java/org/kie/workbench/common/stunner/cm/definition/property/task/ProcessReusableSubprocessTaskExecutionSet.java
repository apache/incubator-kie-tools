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
package org.kie.workbench.common.stunner.cm.definition.property.task;

import javax.validation.Valid;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;
import org.kie.workbench.common.forms.adf.definitions.annotations.FieldParam;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormDefinition;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormField;
import org.kie.workbench.common.forms.adf.definitions.annotations.SkipFormField;
import org.kie.workbench.common.forms.adf.definitions.annotations.field.selector.SelectorDataProvider;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.listBox.type.ListBoxFieldType;
import org.kie.workbench.common.stunner.bpmn.definition.property.subProcess.IsCase;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.AdHocAutostart;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.BaseReusableSubprocessTaskExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.CalledElement;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.Independent;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.IsAsync;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.OnEntryAction;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.OnExitAction;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.ScriptTypeListValue;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.ScriptTypeValue;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.WaitForCompletion;
import org.kie.workbench.common.stunner.core.definition.annotation.Property;
import org.kie.workbench.common.stunner.core.definition.annotation.PropertySet;
import org.kie.workbench.common.stunner.core.util.HashUtil;

@Portable
@Bindable
@PropertySet
@FormDefinition(
        startElement = "calledElement"
)
public class ProcessReusableSubprocessTaskExecutionSet
        implements BaseReusableSubprocessTaskExecutionSet {

    @Property
    @SelectorDataProvider(
            type = SelectorDataProvider.ProviderType.REMOTE,
            className = "org.kie.workbench.common.stunner.bpmn.backend.dataproviders.CalledElementFormProvider")
    @FormField(
            type = ListBoxFieldType.class
    )
    @Valid
    protected CalledElement calledElement;

    @Property
    @SkipFormField
    @Valid
    protected IsCase isCase;

    @Property
    @FormField(
            afterElement = "calledElement"
    )
    @Valid
    protected Independent independent;

    @Property
    @FormField(
            afterElement = "independent"
    )
    @Valid
    protected WaitForCompletion waitForCompletion;

    @Property
    @FormField(
            afterElement = "waitForCompletion"
    )
    @Valid
    protected IsAsync isAsync;

    @Property
    @FormField(afterElement = "isAsync")
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

    public ProcessReusableSubprocessTaskExecutionSet() {
        this(new CalledElement(),
             new IsCase(false),
             new Independent(),
             new WaitForCompletion(),
             new IsAsync(),
             new AdHocAutostart(),
             new OnEntryAction(new ScriptTypeListValue().addValue(new ScriptTypeValue("java", ""))),
             new OnExitAction(new ScriptTypeListValue().addValue(new ScriptTypeValue("java", ""))));
    }

    public ProcessReusableSubprocessTaskExecutionSet(final @MapsTo("calledElement") CalledElement calledElement,
                                                     final @MapsTo("isCase") IsCase isCase,
                                                     final @MapsTo("independent") Independent independent,
                                                     final @MapsTo("waitForCompletion") WaitForCompletion waitForCompletion,
                                                     final @MapsTo("isAsync") IsAsync isAsync,
                                                     final @MapsTo("adHocAutostart") AdHocAutostart adHocAutostart,
                                                     final @MapsTo("onEntryAction") OnEntryAction onEntryAction,
                                                     final @MapsTo("onExitAction") OnExitAction onExitAction) {
        this.calledElement = calledElement;
        this.isCase = isCase;
        this.independent = independent;
        this.waitForCompletion = waitForCompletion;
        this.isAsync = isAsync;
        this.adHocAutostart = adHocAutostart;
        this.onEntryAction = onEntryAction;
        this.onExitAction = onExitAction;
    }

    @Override
    public CalledElement getCalledElement() {
        return calledElement;
    }

    @Override
    public void setCalledElement(final CalledElement calledElement) {
        this.calledElement = calledElement;
    }

    @Override
    public IsCase getIsCase() {
        return isCase;
    }

    @Override
    public void setIsCase(final IsCase isCase) {
        this.isCase = isCase;
    }

    @Override
    public Independent getIndependent() {
        return independent;
    }

    @Override
    public void setIndependent(final Independent independent) {
        this.independent = independent;
    }

    @Override
    public WaitForCompletion getWaitForCompletion() {
        return waitForCompletion;
    }

    @Override
    public void setWaitForCompletion(final WaitForCompletion waitForCompletion) {
        this.waitForCompletion = waitForCompletion;
    }

    @Override
    public IsAsync getIsAsync() {
        return isAsync;
    }

    @Override
    public void setIsAsync(IsAsync isAsync) {
        this.isAsync = isAsync;
    }

    @Override
    public AdHocAutostart getAdHocAutostart() {
        return adHocAutostart;
    }

    @Override
    public void setAdHocAutostart(AdHocAutostart adHocAutostart) {
        this.adHocAutostart = adHocAutostart;
    }

    @Override
    public OnEntryAction getOnEntryAction() {
        return onEntryAction;
    }

    @Override
    public void setOnEntryAction(final OnEntryAction onEntryAction) {
        this.onEntryAction = onEntryAction;
    }

    @Override
    public OnExitAction getOnExitAction() {
        return onExitAction;
    }

    @Override
    public void setOnExitAction(final OnExitAction onExitAction) {
        this.onExitAction = onExitAction;
    }

    @Override
    public int hashCode() {
        return HashUtil.combineHashCodes(calledElement.hashCode(),
                                         isCase.hashCode(),
                                         independent.hashCode(),
                                         waitForCompletion.hashCode(),
                                         isAsync.hashCode(),
                                         adHocAutostart.hashCode(),
                                         onEntryAction.hashCode(),
                                         onExitAction.hashCode());
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof ProcessReusableSubprocessTaskExecutionSet) {
            ProcessReusableSubprocessTaskExecutionSet other = (ProcessReusableSubprocessTaskExecutionSet) o;
            return calledElement.equals(other.calledElement) &&
                    isCase.equals(other.isCase) &&
                    independent.equals(other.independent) &&
                    waitForCompletion.equals(other.waitForCompletion) &&
                    isAsync.equals(other.isAsync) &&
                    adHocAutostart.equals(other.adHocAutostart) &&
                    onEntryAction.equals(other.onEntryAction) &&
                    onExitAction.equals(other.onExitAction);
        }
        return false;
    }
}
