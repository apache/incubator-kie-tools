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
import org.kie.workbench.common.stunner.bpmn.definition.property.event.BaseCancellingEventExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.CancelActivity;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.SLADueDate;
import org.kie.workbench.common.stunner.bpmn.forms.model.ComboBoxFieldType;
import org.kie.workbench.common.stunner.core.definition.annotation.Property;
import org.kie.workbench.common.stunner.core.util.HashUtil;

@Portable
@Bindable
@FormDefinition(startElement = "cancelActivity",
        policy = FieldPolicy.ONLY_MARKED)
public class CancellingEscalationEventExecutionSet extends BaseCancellingEventExecutionSet {

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
             new SLADueDate(),
             new EscalationRef());
    }

    public CancellingEscalationEventExecutionSet(final @MapsTo("cancelActivity") CancelActivity cancelActivity,
                                                 final @MapsTo("slaDueDate") SLADueDate slaDueDate,
                                                 final @MapsTo("escalationRef") EscalationRef escalationRef) {
        super(cancelActivity, slaDueDate);
        this.escalationRef = escalationRef;
    }

    public EscalationRef getEscalationRef() {
        return escalationRef;
    }

    public void setEscalationRef(EscalationRef escalationRef) {
        this.escalationRef = escalationRef;
    }

    @Override
    public int hashCode() {
        return HashUtil.combineHashCodes(super.hashCode(),
                                         Objects.hashCode(escalationRef));
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof CancellingEscalationEventExecutionSet) {
            CancellingEscalationEventExecutionSet other = (CancellingEscalationEventExecutionSet) o;
            return super.equals(other) &&
                    Objects.equals(escalationRef, other.escalationRef);
        }
        return false;
    }
}
