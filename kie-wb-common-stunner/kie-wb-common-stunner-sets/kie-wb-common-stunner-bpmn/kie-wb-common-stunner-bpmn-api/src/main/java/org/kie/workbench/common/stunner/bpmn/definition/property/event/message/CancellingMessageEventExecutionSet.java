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
package org.kie.workbench.common.stunner.bpmn.definition.property.event.message;

import javax.validation.Valid;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormDefinition;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormField;
import org.kie.workbench.common.forms.adf.definitions.annotations.field.selector.SelectorDataProvider;
import org.kie.workbench.common.forms.adf.definitions.annotations.metaModel.FieldLabel;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNPropertySet;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.CancelActivity;
import org.kie.workbench.common.stunner.bpmn.forms.model.ComboBoxFieldType;
import org.kie.workbench.common.stunner.core.definition.annotation.Name;
import org.kie.workbench.common.stunner.core.definition.annotation.Property;
import org.kie.workbench.common.stunner.core.definition.annotation.PropertySet;
import org.kie.workbench.common.stunner.core.util.HashUtil;

@Portable
@Bindable
@PropertySet
@FormDefinition(startElement = "cancelActivity")
public class CancellingMessageEventExecutionSet implements BPMNPropertySet {

    @Name
    @FieldLabel
    public static final transient String propertySetName = "Implementation/Execution";

    @Property
    @FormField
    @Valid
    private CancelActivity cancelActivity;

    @Property
    @FormField(type = ComboBoxFieldType.class,
            afterElement = "cancelActivity")
    @SelectorDataProvider(
            type = SelectorDataProvider.ProviderType.CLIENT,
            className = "org.kie.workbench.common.stunner.bpmn.client.dataproviders.ProcessMessageRefProvider"
    )
    @Valid
    private MessageRef messageRef;

    public CancellingMessageEventExecutionSet() {
        this(new CancelActivity(),
             new MessageRef());
    }

    public CancellingMessageEventExecutionSet(final @MapsTo("cancelActivity") CancelActivity cancelActivity,
                                              final @MapsTo("messageRef") MessageRef messageRef) {
        this.cancelActivity = cancelActivity;
        this.messageRef = messageRef;
    }

    public String getPropertySetName() {
        return propertySetName;
    }

    public CancelActivity getCancelActivity() {
        return cancelActivity;
    }

    public void setCancelActivity(CancelActivity cancelActivity) {
        this.cancelActivity = cancelActivity;
    }

    public MessageRef getMessageRef() {
        return messageRef;
    }

    public void setMessageRef(final MessageRef messageRef) {
        this.messageRef = messageRef;
    }

    @Override
    public int hashCode() {
        return HashUtil.combineHashCodes(cancelActivity.hashCode(),
                                         messageRef.hashCode());
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof CancellingMessageEventExecutionSet) {
            CancellingMessageEventExecutionSet other = (CancellingMessageEventExecutionSet) o;
            return cancelActivity.equals(other.cancelActivity) &&
                    messageRef.equals(other.messageRef);
        }
        return false;
    }
}
