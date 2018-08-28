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

package org.kie.workbench.common.stunner.bpmn.definition.property.event.escalation;

import java.util.Objects;

import javax.validation.Valid;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormDefinition;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormField;
import org.kie.workbench.common.forms.adf.definitions.annotations.field.selector.SelectorDataProvider;
import org.kie.workbench.common.forms.adf.definitions.settings.FieldPolicy;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNPropertySet;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.CancelActivity;

import org.kie.workbench.common.stunner.bpmn.forms.model.ComboBoxFieldType;
import org.kie.workbench.common.stunner.core.definition.annotation.Property;
import org.kie.workbench.common.stunner.core.definition.annotation.PropertySet;
import org.kie.workbench.common.stunner.core.util.HashUtil;

@Portable
@Bindable
@PropertySet
@FormDefinition(startElement = "cancelActivity",
        policy = FieldPolicy.ONLY_MARKED)
public class CancellingEscalationEventExecutionSet implements BPMNPropertySet {

    @Property
    @FormField
    @Valid
    private CancelActivity cancelActivity;

    @Property
    @FormField(type = ComboBoxFieldType.class,
            afterElement = "cancelActivity"
    )
    @SelectorDataProvider(
            type = SelectorDataProvider.ProviderType.CLIENT,
            className = "org.kie.workbench.common.stunner.bpmn.client.dataproviders.ProcessEscalationRefProvider"
    )
    @Valid
    private EscalationRef escalationRef;

    public CancellingEscalationEventExecutionSet() {
        this(new CancelActivity(true),
             new EscalationRef());
    }

    public CancellingEscalationEventExecutionSet(final @MapsTo("cancelActivity") CancelActivity cancelActivity,
                                                 final @MapsTo("escalationRef") EscalationRef escalationRef) {
        this.cancelActivity = cancelActivity;
        this.escalationRef = escalationRef;
    }

    public CancelActivity getCancelActivity() {
        return cancelActivity;
    }

    public void setCancelActivity(CancelActivity cancelActivity) {
        this.cancelActivity = cancelActivity;
    }

    public EscalationRef getEscalationRef() {
        return escalationRef;
    }

    public void setEscalationRef(EscalationRef escalationRef) {
        this.escalationRef = escalationRef;
    }

    @Override
    public int hashCode() {
        return HashUtil.combineHashCodes(Objects.hashCode(cancelActivity),
                                         Objects.hashCode(escalationRef));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof CancellingEscalationEventExecutionSet) {
            CancellingEscalationEventExecutionSet other = (CancellingEscalationEventExecutionSet) o;
            return Objects.equals(cancelActivity, other.cancelActivity) &&
                    Objects.equals(escalationRef, other.escalationRef);
        }
        return false;
    }
}
