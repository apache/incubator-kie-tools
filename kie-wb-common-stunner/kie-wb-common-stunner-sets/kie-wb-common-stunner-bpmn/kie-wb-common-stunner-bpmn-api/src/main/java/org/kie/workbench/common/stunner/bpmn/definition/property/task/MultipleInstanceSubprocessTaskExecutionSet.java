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
import org.kie.workbench.common.stunner.bpmn.definition.BPMNPropertySet;
import org.kie.workbench.common.stunner.core.definition.annotation.Property;
import org.kie.workbench.common.stunner.core.definition.annotation.PropertySet;
import org.kie.workbench.common.stunner.core.util.HashUtil;

@Portable
@Bindable
@PropertySet
@FormDefinition(
        startElement = "multipleInstanceCollectionInput"
)
public class MultipleInstanceSubprocessTaskExecutionSet implements BPMNPropertySet {

    @Property
    @FormField(type = ListBoxFieldType.class)
    @SelectorDataProvider(
            type = SelectorDataProvider.ProviderType.CLIENT,
            className = "org.kie.workbench.common.stunner.bpmn.client.dataproviders.VariablesProvider"
    )
    @Valid
    private MultipleInstanceCollectionInput multipleInstanceCollectionInput;

    @Property
    @FormField(
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

    @Property
    @FormField(
            afterElement = "onExitAction"
    )
    @Valid
    private IsAsync isAsync;

    @Property
    @SkipFormField
    private MITrigger miTrigger;

    public MultipleInstanceSubprocessTaskExecutionSet() {
        this(new MultipleInstanceCollectionInput(),
             new MultipleInstanceCollectionOutput(),
             new MultipleInstanceDataInput(),
             new MultipleInstanceDataOutput(),
             new MultipleInstanceCompletionCondition(),
             new OnEntryAction(new ScriptTypeListValue().addValue(new ScriptTypeValue("java",
                                                                                      ""))),
             new OnExitAction(new ScriptTypeListValue().addValue(new ScriptTypeValue("java",
                                                                                     ""))),
             new MITrigger("true"),
             new IsAsync());
    }

    public MultipleInstanceSubprocessTaskExecutionSet(final @MapsTo("multipleInstanceCollectionInput") MultipleInstanceCollectionInput multipleInstanceCollectionInput,
                                                      final @MapsTo("multipleInstanceCollectionOutput") MultipleInstanceCollectionOutput multipleInstanceCollectionOutput,
                                                      final @MapsTo("multipleInstanceDataInput") MultipleInstanceDataInput multipleInstanceDataInput,
                                                      final @MapsTo("multipleInstanceDataOutput") MultipleInstanceDataOutput multipleInstanceDataOutput,
                                                      final @MapsTo("multipleInstanceCompletionCondition") MultipleInstanceCompletionCondition multipleInstanceCompletionCondition,
                                                      final @MapsTo("onEntryAction") OnEntryAction onEntryAction,
                                                      final @MapsTo("onExitAction") OnExitAction onExitAction,
                                                      final @MapsTo("miTrigger") MITrigger miTrigger,
                                                      final @MapsTo("isAsync") IsAsync isAsync

    ) {
        this.multipleInstanceCollectionInput = multipleInstanceCollectionInput;
        this.multipleInstanceCollectionOutput = multipleInstanceCollectionOutput;
        this.multipleInstanceDataInput = multipleInstanceDataInput;
        this.multipleInstanceDataOutput = multipleInstanceDataOutput;
        this.multipleInstanceCompletionCondition = multipleInstanceCompletionCondition;
        this.onEntryAction = onEntryAction;
        this.onExitAction = onExitAction;
        this.miTrigger = miTrigger;
        this.isAsync = isAsync;
    }

    public MultipleInstanceCollectionInput getMultipleInstanceCollectionInput() {
        return multipleInstanceCollectionInput;
    }

    public void setMultipleInstanceCollectionInput(MultipleInstanceCollectionInput multipleInstanceCollectionInput) {
        this.multipleInstanceCollectionInput = multipleInstanceCollectionInput;
    }

    public MultipleInstanceCollectionOutput getMultipleInstanceCollectionOutput() {
        return multipleInstanceCollectionOutput;
    }

    public void setMultipleInstanceCollectionOutput(MultipleInstanceCollectionOutput multipleInstanceCollectionOutput) {
        this.multipleInstanceCollectionOutput = multipleInstanceCollectionOutput;
    }

    public MultipleInstanceDataInput getMultipleInstanceDataInput() {
        return multipleInstanceDataInput;
    }

    public void setMultipleInstanceDataInput(MultipleInstanceDataInput multipleInstanceDataInput) {
        this.multipleInstanceDataInput = multipleInstanceDataInput;
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

    public MITrigger getMiTrigger() {
        return miTrigger;
    }

    public void setMiTrigger(MITrigger miTrigger) {
        this.miTrigger = miTrigger;
    }

    public OnEntryAction getOnEntryAction() {
        return onEntryAction;
    }

    public void setOnEntryAction(final OnEntryAction onEntryAction) {
        this.onEntryAction = onEntryAction;
    }

    public OnExitAction getOnExitAction() {
        return onExitAction;
    }

    public void setOnExitAction(final OnExitAction onExitAction) {
        this.onExitAction = onExitAction;
    }

    public IsAsync getIsAsync() {
        return isAsync;
    }

    public void setIsAsync(final IsAsync isAsync) {
        this.isAsync = isAsync;
    }

    @Override
    public int hashCode() {
        return HashUtil.combineHashCodes(Objects.hashCode(multipleInstanceCollectionInput),
                                         Objects.hashCode(multipleInstanceCollectionOutput),
                                         Objects.hashCode(multipleInstanceDataInput),
                                         Objects.hashCode(multipleInstanceDataOutput),
                                         Objects.hashCode(multipleInstanceCompletionCondition),
                                         Objects.hashCode(onEntryAction),
                                         Objects.hashCode(onExitAction),
                                         Objects.hashCode(miTrigger),
                                         Objects.hashCode(isAsync));
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof MultipleInstanceSubprocessTaskExecutionSet) {
            MultipleInstanceSubprocessTaskExecutionSet other = (MultipleInstanceSubprocessTaskExecutionSet) o;
            return Objects.equals(multipleInstanceCollectionInput, other.multipleInstanceCollectionInput) &&
                    Objects.equals(multipleInstanceCollectionOutput, other.multipleInstanceCollectionOutput) &&
                    Objects.equals(multipleInstanceDataInput, other.multipleInstanceDataInput) &&
                    Objects.equals(multipleInstanceDataOutput, other.multipleInstanceDataOutput) &&
                    Objects.equals(multipleInstanceCompletionCondition, other.multipleInstanceCompletionCondition) &&
                    Objects.equals(onEntryAction, other.onEntryAction) &&
                    Objects.equals(onExitAction, other.onExitAction) &&
                    Objects.equals(miTrigger, other.miTrigger) &&
                    Objects.equals(isAsync, other.isAsync);
        }
        return false;
    }
}
