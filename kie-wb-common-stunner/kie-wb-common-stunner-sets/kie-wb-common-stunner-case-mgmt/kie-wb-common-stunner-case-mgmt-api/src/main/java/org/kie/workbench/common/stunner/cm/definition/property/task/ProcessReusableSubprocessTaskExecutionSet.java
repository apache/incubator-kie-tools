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
import org.kie.workbench.common.stunner.bpmn.definition.property.general.SLADueDate;
import org.kie.workbench.common.stunner.bpmn.definition.property.subProcess.IsCase;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.AbortParent;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.AdHocAutostart;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.BaseReusableSubprocessTaskExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.BaseSubprocessTaskExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.CalledElement;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.Independent;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.IsAsync;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.IsMultipleInstance;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.MultipleInstanceCollectionInput;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.MultipleInstanceCollectionOutput;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.MultipleInstanceCompletionCondition;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.MultipleInstanceDataInput;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.MultipleInstanceDataOutput;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.MultipleInstanceExecutionMode;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.OnEntryAction;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.OnExitAction;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.ScriptTypeListValue;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.ScriptTypeValue;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.WaitForCompletion;
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
public class ProcessReusableSubprocessTaskExecutionSet
        extends BaseSubprocessTaskExecutionSet
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
    protected AbortParent abortParent;

    @Property
    @FormField(
            afterElement = "abortParent"
    )
    @Valid
    protected WaitForCompletion waitForCompletion;

    @Property
    @FormField(afterElement = "isAsync")
    @Valid
    private AdHocAutostart adHocAutostart;

    @SkipFormField
    @Property
    @Valid
    @FormField(afterElement = "adHocAutostart")
    private IsMultipleInstance isMultipleInstance;

    @SkipFormField
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

    @SkipFormField
    @Property
    @FormField(type = ListBoxFieldType.class, afterElement = "multipleInstanceExecutionMode")
    @SelectorDataProvider(
            type = SelectorDataProvider.ProviderType.CLIENT,
            className = "org.kie.workbench.common.stunner.bpmn.client.dataproviders.VariablesProvider"
    )
    @Valid
    private MultipleInstanceCollectionInput multipleInstanceCollectionInput;

    @SkipFormField
    @Property
    @FormField(
            type = MultipleInstanceVariableFieldType.class,
            afterElement = "multipleInstanceCollectionInput"
    )
    @Valid
    private MultipleInstanceDataInput multipleInstanceDataInput;

    @SkipFormField
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

    @SkipFormField
    @Property
    @FormField(
            type = MultipleInstanceVariableFieldType.class,
            afterElement = "multipleInstanceCollectionOutput"
    )
    @Valid
    private MultipleInstanceDataOutput multipleInstanceDataOutput;

    @SkipFormField
    @Property
    @FormField(
            type = TextAreaFieldType.class,
            afterElement = "multipleInstanceDataOutput",
            settings = {@FieldParam(name = "rows", value = "5")}
    )
    @Valid
    private MultipleInstanceCompletionCondition multipleInstanceCompletionCondition;

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
             new AbortParent(),
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
             new OnEntryAction(new ScriptTypeListValue().addValue(new ScriptTypeValue("java", ""))),
             new OnExitAction(new ScriptTypeListValue().addValue(new ScriptTypeValue("java", ""))),
             new SLADueDate());
    }

    public ProcessReusableSubprocessTaskExecutionSet(final @MapsTo("calledElement") CalledElement calledElement,
                                                     final @MapsTo("isCase") IsCase isCase,
                                                     final @MapsTo("independent") Independent independent,
                                                     final @MapsTo("abortParent") AbortParent abortParent,
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
                                                     final @MapsTo("onExitAction") OnExitAction onExitAction,
                                                     final @MapsTo("slaDueDate") SLADueDate slaDueDate) {
        super(isAsync, slaDueDate);
        this.calledElement = calledElement;
        this.isCase = isCase;
        this.independent = independent;
        this.abortParent = abortParent;
        this.waitForCompletion = waitForCompletion;
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
    public AbortParent getAbortParent() {
        return abortParent;
    }

    @Override
    public void setAbortParent(final AbortParent abortParent) {
        this.abortParent = abortParent;
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
    public AdHocAutostart getAdHocAutostart() {
        return adHocAutostart;
    }

    @Override
    public void setAdHocAutostart(AdHocAutostart adHocAutostart) {
        this.adHocAutostart = adHocAutostart;
    }

    @Override
    public IsMultipleInstance getIsMultipleInstance() {
        return isMultipleInstance;
    }

    @Override
    public void setIsMultipleInstance(IsMultipleInstance isMultipleInstance) {
        this.isMultipleInstance = isMultipleInstance;
    }

    @Override
    public MultipleInstanceExecutionMode getMultipleInstanceExecutionMode() {
        return multipleInstanceExecutionMode;
    }

    @Override
    public void setMultipleInstanceExecutionMode(MultipleInstanceExecutionMode multipleInstanceExecutionMode) {
        this.multipleInstanceExecutionMode = multipleInstanceExecutionMode;
    }

    @Override
    public MultipleInstanceCollectionInput getMultipleInstanceCollectionInput() {
        return multipleInstanceCollectionInput;
    }

    @Override
    public void setMultipleInstanceCollectionInput(MultipleInstanceCollectionInput multipleInstanceCollectionInput) {
        this.multipleInstanceCollectionInput = multipleInstanceCollectionInput;
    }

    @Override
    public MultipleInstanceDataInput getMultipleInstanceDataInput() {
        return multipleInstanceDataInput;
    }

    @Override
    public void setMultipleInstanceDataInput(MultipleInstanceDataInput multipleInstanceDataInput) {
        this.multipleInstanceDataInput = multipleInstanceDataInput;
    }

    @Override
    public MultipleInstanceCollectionOutput getMultipleInstanceCollectionOutput() {
        return multipleInstanceCollectionOutput;
    }

    @Override
    public void setMultipleInstanceCollectionOutput(MultipleInstanceCollectionOutput multipleInstanceCollectionOutput) {
        this.multipleInstanceCollectionOutput = multipleInstanceCollectionOutput;
    }

    @Override
    public MultipleInstanceDataOutput getMultipleInstanceDataOutput() {
        return multipleInstanceDataOutput;
    }

    @Override
    public void setMultipleInstanceDataOutput(MultipleInstanceDataOutput multipleInstanceDataOutput) {
        this.multipleInstanceDataOutput = multipleInstanceDataOutput;
    }

    @Override
    public MultipleInstanceCompletionCondition getMultipleInstanceCompletionCondition() {
        return multipleInstanceCompletionCondition;
    }

    @Override
    public void setMultipleInstanceCompletionCondition(MultipleInstanceCompletionCondition multipleInstanceCompletionCondition) {
        this.multipleInstanceCompletionCondition = multipleInstanceCompletionCondition;
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
        return HashUtil.combineHashCodes(super.hashCode(),
                                         Objects.hashCode(calledElement),
                                         Objects.hashCode(isCase),
                                         Objects.hashCode(independent),
                                         Objects.hashCode(abortParent),
                                         Objects.hashCode(waitForCompletion),
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
        if (o instanceof ProcessReusableSubprocessTaskExecutionSet) {
            ProcessReusableSubprocessTaskExecutionSet other = (ProcessReusableSubprocessTaskExecutionSet) o;
            return super.equals(other) &&
                    Objects.equals(calledElement, other.calledElement) &&
                    Objects.equals(isCase, other.isCase) &&
                    Objects.equals(independent, other.independent) &&
                    Objects.equals(abortParent, other.abortParent) &&
                    Objects.equals(waitForCompletion, other.waitForCompletion) &&
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