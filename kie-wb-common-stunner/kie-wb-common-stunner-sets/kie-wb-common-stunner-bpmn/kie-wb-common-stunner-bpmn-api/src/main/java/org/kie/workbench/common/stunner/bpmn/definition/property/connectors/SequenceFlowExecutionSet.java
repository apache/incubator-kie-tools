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
package org.kie.workbench.common.stunner.bpmn.definition.property.connectors;

import javax.validation.Valid;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;
import org.kie.workbench.common.forms.adf.definitions.annotations.FieldParam;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormDefinition;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormField;
import org.kie.workbench.common.forms.adf.definitions.annotations.field.selector.SelectorDataProvider;
import org.kie.workbench.common.forms.adf.definitions.annotations.metaModel.FieldLabel;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textArea.type.TextAreaFieldType;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNPropertySet;
import org.kie.workbench.common.stunner.bpmn.forms.model.ConditionalComboBoxFieldType;
import org.kie.workbench.common.stunner.core.definition.annotation.Name;
import org.kie.workbench.common.stunner.core.definition.annotation.Property;
import org.kie.workbench.common.stunner.core.definition.annotation.PropertySet;
import org.kie.workbench.common.stunner.core.util.HashUtil;

@Portable
@Bindable
@PropertySet
@FormDefinition(
        startElement = "priority"
)
public class SequenceFlowExecutionSet implements BPMNPropertySet {

    @Name
    @FieldLabel
    public static final transient String propertySetName = "Implementation/Execution";

    @Property
    @FormField
    @Valid
    private Priority priority;

    @Property
    @FormField(
            type = TextAreaFieldType.class,
            afterElement = "priority",
            settings = {@FieldParam(name = "rows", value = "5")}
    )
    @Valid
    private ConditionExpression conditionExpression;

    @Property
    @FormField(
        type = ConditionalComboBoxFieldType.class,
        afterElement = "conditionExpression",
        settings = {
            @FieldParam(name = "relatedField", value = "executionSet.conditionExpression"),
            @FieldParam(name = "allowCustomValue", value = "false")
        }
    )
    @SelectorDataProvider(
        type = SelectorDataProvider.ProviderType.REMOTE,
        className = "org.kie.workbench.common.stunner.bpmn.backend.dataproviders.ConditionLanguageFormProvider")
    @Valid
    protected ConditionExpressionLanguage conditionExpressionLanguage;

    public SequenceFlowExecutionSet() {
        this(new Priority(""),
             new ConditionExpression(""),
             new ConditionExpressionLanguage("")
        );
    }

    public SequenceFlowExecutionSet(final @MapsTo("priority") Priority priority,
                                    final @MapsTo("conditionExpression") ConditionExpression conditionExpression,
                                    final @MapsTo("conditionExpressionLanguage") ConditionExpressionLanguage conditionExpressionLanguage) {
        this.priority = priority;
        this.conditionExpression = conditionExpression;
        this.conditionExpressionLanguage = conditionExpressionLanguage;
    }

    public String getPropertySetName() {
        return propertySetName;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(final Priority priority) {
        this.priority = priority;
    }

    public ConditionExpression getConditionExpression() {
        return conditionExpression;
    }

    public void setConditionExpression(final ConditionExpression conditionExpression) {
        this.conditionExpression = conditionExpression;
    }

    public ConditionExpressionLanguage getConditionExpressionLanguage() {
        return conditionExpressionLanguage;
    }

    public void setConditionExpressionLanguage(final ConditionExpressionLanguage conditionExpressionLanguage) {
        this.conditionExpressionLanguage = conditionExpressionLanguage;
    }

    @Override
    public int hashCode() {
        return HashUtil.combineHashCodes(priority.hashCode(),
                                         conditionExpression.hashCode(),
                                         conditionExpressionLanguage.hashCode());
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof SequenceFlowExecutionSet) {
            SequenceFlowExecutionSet other = (SequenceFlowExecutionSet) o;
            return priority.equals(other.priority) &&
                    conditionExpression.equals(other.conditionExpression) &&
                    conditionExpressionLanguage.equals(other.conditionExpressionLanguage);
        }
        return false;
    }
}
