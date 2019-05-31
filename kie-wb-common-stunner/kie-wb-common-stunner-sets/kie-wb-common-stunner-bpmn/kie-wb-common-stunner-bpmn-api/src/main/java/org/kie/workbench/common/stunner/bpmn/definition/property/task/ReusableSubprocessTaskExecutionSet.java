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
package org.kie.workbench.common.stunner.bpmn.definition.property.task;

import java.util.Objects;

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
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textArea.type.TextAreaFieldType;
import org.kie.workbench.common.stunner.bpmn.definition.property.subProcess.IsCase;
import org.kie.workbench.common.stunner.bpmn.forms.model.MultipleInstanceVariableFieldType;
import org.kie.workbench.common.stunner.core.definition.annotation.Property;
import org.kie.workbench.common.stunner.core.definition.annotation.PropertySet;
import org.kie.workbench.common.stunner.core.util.HashUtil;

@Portable
@Bindable
@PropertySet
@FormDefinition(
        startElement = "calledElement"
)
public class ReusableSubprocessTaskExecutionSet implements BaseReusableSubprocessTaskExecutionSet {

    @Property
    @SelectorDataProvider(
            type = SelectorDataProvider.ProviderType.REMOTE,
            className = "org.kie.workbench.common.stunner.bpmn.backend.dataproviders.CalledElementFormProvider")
    @FormField(type = ListBoxFieldType.class
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
    private Independent independent;

    @Property
    @FormField(
            afterElement = "independent"
    )
    @Valid
    private WaitForCompletion waitForCompletion;

    @Property
    @FormField(
            afterElement = "waitForCompletion"
    )
    @Valid
    private IsAsync isAsync;

    @Property
    @SkipFormField
    @Valid
    private AdHocAutostart adHocAutostart;

    @Property
    @Valid
    @FormField(afterElement = "isAsync")
    private IsMultipleInstance isMultipleInstance;

    @Property
    @Valid
    @FormField(afterElement = "isMultipleInstance",
            type = ListBoxFieldType.class,
            settings = {@FieldParam(name = "addEmptyOption", value = "false")}
    )
    @SelectorDataProvider(
            type = SelectorDataProvider.ProviderType.CLIENT,
            className = "org.kie.workbench.common.stunner.bpmn.client.dataproviders.ExecutionOrderProvider")
    private MultipleInstanceExecutionMode multipleInstanceExecutionMode;

    @Property
    @FormField(type = ListBoxFieldType.class, afterElement = "multipleInstanceExecutionMode")
    @SelectorDataProvider(
            type = SelectorDataProvider.ProviderType.CLIENT,
            className = "org.kie.workbench.common.stunner.bpmn.client.dataproviders.VariablesProvider"
    )
    @Valid
    private MultipleInstanceCollectionInput multipleInstanceCollectionInput;

    @Property
    @FormField(
            type = MultipleInstanceVariableFieldType.class,
            afterElement = "multipleInstanceCollectionInput"
    )
    @Valid
    private MultipleInstanceDataInput multipleInstanceDataInput;

    @Property
    @FormField(
            type = ListBoxFieldType.class,
            afterElement = "multipleInstanceDataInput"
    )
    @SelectorDataProvider(
            type = SelectorDataProvider.ProviderType.CLIENT,
            className = "org.kie.workbench.common.stunner.bpmn.client.dataproviders.VariablesProvider"
    )
    @Valid
    private MultipleInstanceCollectionOutput multipleInstanceCollectionOutput;

    @Property
    @FormField(
            type = MultipleInstanceVariableFieldType.class,
            afterElement = "multipleInstanceCollectionOutput"
    )
    @Valid
    private MultipleInstanceDataOutput multipleInstanceDataOutput;

    @Property
    @FormField(
            type = TextAreaFieldType.class,
            afterElement = "multipleInstanceDataOutput",
            settings = {@FieldParam(name = "rows", value = "5")}
    )
    @Valid
    private MultipleInstanceCompletionCondition multipleInstanceCompletionCondition;

    @Property
    @FormField(afterElement = "multipleInstanceCompletionCondition",
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

    public ReusableSubprocessTaskExecutionSet() {
        this(new CalledElement(),
             new IsCase(),
             new Independent(),
             new WaitForCompletion(),
             new IsAsync(),
             new AdHocAutostart(),
             new IsMultipleInstance(false),
             new MultipleInstanceExecutionMode(false),
             new MultipleInstanceCollectionInput(),
             new MultipleInstanceDataInput(),
             new MultipleInstanceCollectionOutput(),
             new MultipleInstanceDataOutput(),
             new MultipleInstanceCompletionCondition(),
             new OnEntryAction(new ScriptTypeListValue().addValue(new ScriptTypeValue("java",
                                                                                      ""))),
             new OnExitAction(new ScriptTypeListValue().addValue(new ScriptTypeValue("java",
                                                                                     ""))));
    }

    public ReusableSubprocessTaskExecutionSet(final @MapsTo("calledElement") CalledElement calledElement,
                                              final @MapsTo("isCase") IsCase isCase,
                                              final @MapsTo("independent") Independent independent,
                                              final @MapsTo("waitForCompletion") WaitForCompletion waitForCompletion,
                                              final @MapsTo("isAsync") IsAsync isAsync,
                                              final @MapsTo("adHocAutostart") AdHocAutostart adHocAutostart,
                                              final @MapsTo("isMultipleInstance") IsMultipleInstance isMultipleInstance,
                                              final @MapsTo("multipleInstanceExecutionMode") MultipleInstanceExecutionMode multipleInstanceExecutionMode,
                                              final @MapsTo("multipleInstanceCollectionInput") MultipleInstanceCollectionInput multipleInstanceCollectionInput,
                                              final @MapsTo("multipleInstanceDataInput") MultipleInstanceDataInput multipleInstanceDataInput,
                                              final @MapsTo("multipleInstanceCollectionOutput") MultipleInstanceCollectionOutput multipleInstanceCollectionOutput,
                                              final @MapsTo("multipleInstanceDataOutput") MultipleInstanceDataOutput multipleInstanceDataOutput,
                                              final @MapsTo("multipleInstanceCompletionCondition") MultipleInstanceCompletionCondition multipleInstanceCompletionCondition,
                                              final @MapsTo("onEntryAction") OnEntryAction onEntryAction,
                                              final @MapsTo("onExitAction") OnExitAction onExitAction) {
        this.calledElement = calledElement;
        this.isCase = isCase;
        this.independent = independent;
        this.waitForCompletion = waitForCompletion;
        this.isAsync = isAsync;
        this.adHocAutostart = adHocAutostart;
        this.isMultipleInstance = isMultipleInstance;
        this.multipleInstanceExecutionMode = multipleInstanceExecutionMode;
        this.multipleInstanceCollectionInput = multipleInstanceCollectionInput;
        this.multipleInstanceDataInput = multipleInstanceDataInput;
        this.multipleInstanceCollectionOutput = multipleInstanceCollectionOutput;
        this.multipleInstanceDataOutput = multipleInstanceDataOutput;
        this.multipleInstanceCompletionCondition = multipleInstanceCompletionCondition;
        this.onEntryAction = onEntryAction;
        this.onExitAction = onExitAction;
    }

    @Override
    public CalledElement getCalledElement() {
        return calledElement;
    }

    @Override
    public Independent getIndependent() {
        return independent;
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
    public WaitForCompletion getWaitForCompletion() {
        return waitForCompletion;
    }

    @Override
    public void setCalledElement(final CalledElement calledElement) {
        this.calledElement = calledElement;
    }

    @Override
    public void setIndependent(final Independent independent) {
        this.independent = independent;
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
    public void setIsAsync(final IsAsync isAsync) {
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

    public IsMultipleInstance getIsMultipleInstance() {
        return isMultipleInstance;
    }

    public void setIsMultipleInstance(IsMultipleInstance isMultipleInstance) {
        this.isMultipleInstance = isMultipleInstance;
    }

    public MultipleInstanceExecutionMode getMultipleInstanceExecutionMode() {
        return multipleInstanceExecutionMode;
    }

    public void setMultipleInstanceExecutionMode(MultipleInstanceExecutionMode multipleInstanceExecutionMode) {
        this.multipleInstanceExecutionMode = multipleInstanceExecutionMode;
    }

    public MultipleInstanceCollectionInput getMultipleInstanceCollectionInput() {
        return multipleInstanceCollectionInput;
    }

    public void setMultipleInstanceCollectionInput(MultipleInstanceCollectionInput multipleInstanceCollectionInput) {
        this.multipleInstanceCollectionInput = multipleInstanceCollectionInput;
    }

    public MultipleInstanceDataInput getMultipleInstanceDataInput() {
        return multipleInstanceDataInput;
    }

    public void setMultipleInstanceDataInput(MultipleInstanceDataInput multipleInstanceDataInput) {
        this.multipleInstanceDataInput = multipleInstanceDataInput;
    }

    public MultipleInstanceCollectionOutput getMultipleInstanceCollectionOutput() {
        return multipleInstanceCollectionOutput;
    }

    public void setMultipleInstanceCollectionOutput(MultipleInstanceCollectionOutput multipleInstanceCollectionOutput) {
        this.multipleInstanceCollectionOutput = multipleInstanceCollectionOutput;
    }

    public MultipleInstanceDataOutput getMultipleInstanceDataOutput() {
        return multipleInstanceDataOutput;
    }

    public void setMultipleInstanceDataOutput(MultipleInstanceDataOutput multipleInstanceDataOutput) {
        this.multipleInstanceDataOutput = multipleInstanceDataOutput;
    }

    public MultipleInstanceCompletionCondition getMultipleInstanceCompletionCondition() {
        return multipleInstanceCompletionCondition;
    }

    public void setMultipleInstanceCompletionCondition(MultipleInstanceCompletionCondition multipleInstanceCompletionCondition) {
        this.multipleInstanceCompletionCondition = multipleInstanceCompletionCondition;
    }

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
        return HashUtil.combineHashCodes(Objects.hashCode(calledElement),
                                         Objects.hashCode(isCase),
                                         Objects.hashCode(independent),
                                         Objects.hashCode(waitForCompletion),
                                         Objects.hashCode(isAsync),
                                         Objects.hashCode(adHocAutostart),
                                         Objects.hashCode(isMultipleInstance),
                                         Objects.hashCode(multipleInstanceExecutionMode),
                                         Objects.hashCode(multipleInstanceCollectionInput),
                                         Objects.hashCode(multipleInstanceDataInput),
                                         Objects.hashCode(multipleInstanceCollectionOutput),
                                         Objects.hashCode(multipleInstanceDataOutput),
                                         Objects.hashCode(multipleInstanceCompletionCondition),
                                         Objects.hashCode(onEntryAction),
                                         Objects.hashCode(onExitAction));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof ReusableSubprocessTaskExecutionSet) {
            ReusableSubprocessTaskExecutionSet other = (ReusableSubprocessTaskExecutionSet) o;
            return Objects.equals(calledElement, other.calledElement) &&
                    Objects.equals(isCase, other.isCase) &&
                    Objects.equals(independent, other.independent) &&
                    Objects.equals(waitForCompletion, other.waitForCompletion) &&
                    Objects.equals(isAsync, other.isAsync) &&
                    Objects.equals(adHocAutostart, other.adHocAutostart) &&
                    Objects.equals(isMultipleInstance, other.isMultipleInstance) &&
                    Objects.equals(multipleInstanceExecutionMode, other.multipleInstanceExecutionMode) &&
                    Objects.equals(multipleInstanceCollectionInput, other.multipleInstanceCollectionInput) &&
                    Objects.equals(multipleInstanceDataInput, other.multipleInstanceDataInput) &&
                    Objects.equals(multipleInstanceCollectionOutput, other.multipleInstanceCollectionOutput) &&
                    Objects.equals(multipleInstanceDataOutput, other.multipleInstanceDataOutput) &&
                    Objects.equals(multipleInstanceCompletionCondition, other.multipleInstanceCompletionCondition) &&
                    Objects.equals(onEntryAction, other.onEntryAction) &&
                    Objects.equals(onExitAction, other.onExitAction);
        }
        return false;
    }
}
