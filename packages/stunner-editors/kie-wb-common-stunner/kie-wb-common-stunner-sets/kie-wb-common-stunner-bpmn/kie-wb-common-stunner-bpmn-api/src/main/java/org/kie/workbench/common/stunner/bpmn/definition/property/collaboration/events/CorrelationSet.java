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


package org.kie.workbench.common.stunner.bpmn.definition.property.collaboration.events;

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
import org.kie.workbench.common.stunner.bpmn.definition.property.task.ScriptTypeValue;
import org.kie.workbench.common.stunner.bpmn.forms.model.ComboBoxFieldType;
import org.kie.workbench.common.stunner.core.definition.annotation.Property;
import org.kie.workbench.common.stunner.core.util.HashUtil;

@Portable
@Bindable
@FormDefinition(startElement = "correlationProperty")
public class CorrelationSet implements BaseCorrelationSet {

    @Property
    @FormField(
            type = ListBoxFieldType.class
    )
    @SelectorDataProvider(
            type = SelectorDataProvider.ProviderType.CLIENT,
            className = "org.kie.workbench.common.stunner.bpmn.client.dataproviders.CorrelationsProvider"
    )
    @Valid
    private CorrelationProperty correlationProperty;

    @Property
    @FormField(
            settings = {@FieldParam(name = "mode", value = "ACTION_SCRIPT")},
            afterElement = "correlationProperty"
    )

    @Valid
    private MessageExpression messageExpression;

    @Property
    @FormField(
            type = ComboBoxFieldType.class,
            settings = {@FieldParam(name = "addEmptyOption", value = "false")},
            afterElement = "messageExpression"
    )
    @SelectorDataProvider(
            type = SelectorDataProvider.ProviderType.CLIENT,
            className = "org.kie.workbench.common.stunner.bpmn.client.dataproviders.DataTypeProvider"
    )
    @Valid
    private MessageExpressionType messageExpressionType;

    @Property
    @FormField(
            settings = {@FieldParam(name = "mode", value = "ACTION_SCRIPT")},
            afterElement = "messageExpressionType"
    )
    @Valid
    private DataExpression dataExpression;

    @Property
    @FormField(
            type = ComboBoxFieldType.class,
            settings = {@FieldParam(name = "addEmptyOption", value = "false")},
            afterElement = "dataExpression"
    )
    @SelectorDataProvider(
            type = SelectorDataProvider.ProviderType.CLIENT,
            className = "org.kie.workbench.common.stunner.bpmn.client.dataproviders.DataTypeProvider"
    )
    @Valid
    private DataExpressionType dataExpressionType;

    public CorrelationSet() {
        this(new CorrelationProperty(""),
             new MessageExpression(new ScriptTypeValue("java", "")),
             new MessageExpressionType(),
             new DataExpression(new ScriptTypeValue("java", "")),
             new DataExpressionType());
    }

    public CorrelationSet(final @MapsTo("correlationProperty") CorrelationProperty correlationProperty,
                          final @MapsTo("messageExpression") MessageExpression messageExpression,
                          final @MapsTo("messageExpressionType") MessageExpressionType messageExpressionType,
                          final @MapsTo("dataExpression") DataExpression dataExpression,
                          final @MapsTo("dataExpressionType") DataExpressionType dataExpressionType) {
        this.correlationProperty = correlationProperty;
        this.messageExpression = messageExpression;
        this.messageExpressionType = messageExpressionType;
        this.dataExpression = dataExpression;
        this.dataExpressionType = dataExpressionType;
    }

    @Override
    public CorrelationProperty getCorrelationProperty() {
        return correlationProperty;
    }

    public void setCorrelationProperty(final CorrelationProperty correlationProperty) {
        this.correlationProperty = correlationProperty;
    }

    @Override
    public MessageExpression getMessageExpression() {
        return messageExpression;
    }

    public void setMessageExpression(final MessageExpression messageExpression) {
        this.messageExpression = messageExpression;
    }

    @Override
    public MessageExpressionType getMessageExpressionType() {
        return messageExpressionType;
    }

    public void setMessageExpressionType(final MessageExpressionType messageExpressionType) {
        this.messageExpressionType = messageExpressionType;
    }

    @Override
    public DataExpression getDataExpression() {
        return dataExpression;
    }

    public void setDataExpression(final DataExpression dataExpression) {
        this.dataExpression = dataExpression;
    }

    @Override
    public DataExpressionType getDataExpressionType() {
        return dataExpressionType;
    }

    public void setDataExpressionType(final DataExpressionType dataExpressionType) {
        this.dataExpressionType = dataExpressionType;
    }

    @Override
    public int hashCode() {
        return HashUtil.combineHashCodes(Objects.hashCode(correlationProperty),
                                         Objects.hashCode(messageExpression),
                                         Objects.hashCode(messageExpressionType),
                                         Objects.hashCode(dataExpression),
                                         Objects.hashCode(dataExpressionType));
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof CorrelationSet) {
            CorrelationSet other = (CorrelationSet) o;
            return Objects.equals(correlationProperty, other.correlationProperty) &&
                    Objects.equals(messageExpression, other.messageExpression) &&
                    Objects.equals(messageExpressionType, other.messageExpressionType) &&
                    Objects.equals(dataExpression, other.dataExpression) &&
                    Objects.equals(dataExpressionType, other.dataExpressionType);
        }
        return false;
    }
}
