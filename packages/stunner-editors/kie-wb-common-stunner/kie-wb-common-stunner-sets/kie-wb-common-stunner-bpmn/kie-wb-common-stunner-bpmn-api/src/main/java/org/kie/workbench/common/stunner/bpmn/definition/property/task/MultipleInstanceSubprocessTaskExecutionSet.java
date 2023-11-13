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
import org.kie.workbench.common.forms.adf.definitions.annotations.SkipFormField;
import org.kie.workbench.common.forms.adf.definitions.annotations.field.selector.SelectorDataProvider;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.listBox.type.ListBoxFieldType;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textArea.type.TextAreaFieldType;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.SLADueDate;
import org.kie.workbench.common.stunner.bpmn.forms.model.MultipleInstanceVariableFieldType;
import org.kie.workbench.common.stunner.core.definition.annotation.Property;
import org.kie.workbench.common.stunner.core.util.HashUtil;

@Portable
@Bindable
@FormDefinition(
        startElement = "multipleInstanceExecutionMode"
)
public class MultipleInstanceSubprocessTaskExecutionSet extends BaseSubprocessTaskExecutionSet {

    @Property
    @Valid
    @FormField(
            type = ListBoxFieldType.class,
            settings = {@FieldParam(name = "addEmptyOption", value = "false")}
    )
    @SelectorDataProvider(
            type = SelectorDataProvider.ProviderType.CLIENT,
            className = "org.kie.workbench.common.stunner.bpmn.client.dataproviders.ExecutionOrderProvider")
    private MultipleInstanceExecutionMode multipleInstanceExecutionMode;

    @Property
    @FormField(
            afterElement = "multipleInstanceExecutionMode",
            type = ListBoxFieldType.class)
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

    @Property
    @SkipFormField
    private IsMultipleInstance isMultipleInstance;

    public MultipleInstanceSubprocessTaskExecutionSet() {
        this(new MultipleInstanceExecutionMode(false),
             new MultipleInstanceCollectionInput(),
             new MultipleInstanceCollectionOutput(),
             new MultipleInstanceDataInput(),
             new MultipleInstanceDataOutput(),
             new MultipleInstanceCompletionCondition(),
             new OnEntryAction(new ScriptTypeListValue().addValue(new ScriptTypeValue("java",
                                                                                      ""))),
             new OnExitAction(new ScriptTypeListValue().addValue(new ScriptTypeValue("java",
                                                                                     ""))),
             new IsMultipleInstance(true),
             new IsAsync(),
             new SLADueDate());
    }

    public MultipleInstanceSubprocessTaskExecutionSet(final @MapsTo("multipleInstanceExecutionMode") MultipleInstanceExecutionMode multipleInstanceExecutionMode,
                                                      final @MapsTo("multipleInstanceCollectionInput") MultipleInstanceCollectionInput multipleInstanceCollectionInput,
                                                      final @MapsTo("multipleInstanceCollectionOutput") MultipleInstanceCollectionOutput multipleInstanceCollectionOutput,
                                                      final @MapsTo("multipleInstanceDataInput") MultipleInstanceDataInput multipleInstanceDataInput,
                                                      final @MapsTo("multipleInstanceDataOutput") MultipleInstanceDataOutput multipleInstanceDataOutput,
                                                      final @MapsTo("multipleInstanceCompletionCondition") MultipleInstanceCompletionCondition multipleInstanceCompletionCondition,
                                                      final @MapsTo("onEntryAction") OnEntryAction onEntryAction,
                                                      final @MapsTo("onExitAction") OnExitAction onExitAction,
                                                      final @MapsTo("isMultipleInstance") IsMultipleInstance isMultipleInstance,
                                                      final @MapsTo("isAsync") IsAsync isAsync,
                                                      final @MapsTo("slaDueDate") SLADueDate slaDueDate) {
        super(isAsync, slaDueDate);
        this.multipleInstanceExecutionMode = multipleInstanceExecutionMode;
        this.multipleInstanceCollectionInput = multipleInstanceCollectionInput;
        this.multipleInstanceCollectionOutput = multipleInstanceCollectionOutput;
        this.multipleInstanceDataInput = multipleInstanceDataInput;
        this.multipleInstanceDataOutput = multipleInstanceDataOutput;
        this.multipleInstanceCompletionCondition = multipleInstanceCompletionCondition;
        this.onEntryAction = onEntryAction;
        this.onExitAction = onExitAction;
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

    public IsMultipleInstance getIsMultipleInstance() {
        return isMultipleInstance;
    }

    public void setIsMultipleInstance(IsMultipleInstance isMultipleInstance) {
        this.isMultipleInstance = isMultipleInstance;
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

    @Override
    public int hashCode() {
        return HashUtil.combineHashCodes(super.hashCode(),
                                         Objects.hashCode(multipleInstanceExecutionMode),
                                         Objects.hashCode(multipleInstanceCollectionInput),
                                         Objects.hashCode(multipleInstanceCollectionOutput),
                                         Objects.hashCode(multipleInstanceDataInput),
                                         Objects.hashCode(multipleInstanceDataOutput),
                                         Objects.hashCode(multipleInstanceCompletionCondition),
                                         Objects.hashCode(onEntryAction),
                                         Objects.hashCode(onExitAction),
                                         Objects.hashCode(isMultipleInstance));
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof MultipleInstanceSubprocessTaskExecutionSet) {
            MultipleInstanceSubprocessTaskExecutionSet other = (MultipleInstanceSubprocessTaskExecutionSet) o;
            return super.equals(other) &&
                    Objects.equals(multipleInstanceExecutionMode, other.multipleInstanceExecutionMode) &&
                    Objects.equals(multipleInstanceCollectionInput, other.multipleInstanceCollectionInput) &&
                    Objects.equals(multipleInstanceCollectionOutput, other.multipleInstanceCollectionOutput) &&
                    Objects.equals(multipleInstanceDataInput, other.multipleInstanceDataInput) &&
                    Objects.equals(multipleInstanceDataOutput, other.multipleInstanceDataOutput) &&
                    Objects.equals(multipleInstanceCompletionCondition, other.multipleInstanceCompletionCondition) &&
                    Objects.equals(onEntryAction, other.onEntryAction) &&
                    Objects.equals(onExitAction, other.onExitAction) &&
                    Objects.equals(isMultipleInstance, other.isMultipleInstance);
        }
        return false;
    }
}
