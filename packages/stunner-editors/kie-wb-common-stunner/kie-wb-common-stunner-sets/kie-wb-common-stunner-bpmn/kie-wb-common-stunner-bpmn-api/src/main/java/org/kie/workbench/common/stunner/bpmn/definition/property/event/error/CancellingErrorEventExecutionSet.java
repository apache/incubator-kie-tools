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
package org.kie.workbench.common.stunner.bpmn.definition.property.event.error;

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
@FormDefinition(startElement = "errorRef",
        policy = FieldPolicy.ONLY_MARKED)
public class CancellingErrorEventExecutionSet extends BaseCancellingEventExecutionSet {

    @Property
    @FormField(type = ComboBoxFieldType.class)
    @SelectorDataProvider(
            type = SelectorDataProvider.ProviderType.CLIENT,
            className = "org.kie.workbench.common.stunner.bpmn.client.dataproviders.ProcessErrorRefProvider"
    )
    @Valid
    private ErrorRef errorRef;

    public CancellingErrorEventExecutionSet() {
        this(new CancelActivity(),
             new SLADueDate(),
             new ErrorRef());
    }

    public CancellingErrorEventExecutionSet(final @MapsTo("cancelActivity") CancelActivity cancelActivity,
                                            final @MapsTo("slaDueDate") SLADueDate slaDueDate,
                                            final @MapsTo("errorRef") ErrorRef errorRef) {
        super(cancelActivity, slaDueDate);
        this.errorRef = errorRef;
    }

    public ErrorRef getErrorRef() {
        return errorRef;
    }

    public void setErrorRef(ErrorRef errorRef) {
        this.errorRef = errorRef;
    }

    @Override
    public int hashCode() {
        return HashUtil.combineHashCodes(super.hashCode(),
                                         Objects.hashCode(errorRef));
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof CancellingErrorEventExecutionSet) {
            CancellingErrorEventExecutionSet other = (CancellingErrorEventExecutionSet) o;
            return super.equals(other) &&
                    Objects.equals(errorRef, other.errorRef);
        }
        return false;
    }
}
