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

package org.kie.workbench.common.stunner.bpmn.definition.property.event.none;

import javax.validation.Valid;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormDefinition;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormField;
import org.kie.workbench.common.forms.adf.definitions.annotations.metaModel.FieldLabel;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.checkBox.type.CheckBoxFieldType;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNPropertySet;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.IsInterrupting;
import org.kie.workbench.common.stunner.core.definition.annotation.Name;
import org.kie.workbench.common.stunner.core.definition.annotation.Property;
import org.kie.workbench.common.stunner.core.definition.annotation.PropertySet;
import org.kie.workbench.common.stunner.core.util.HashUtil;

@Portable
@Bindable
@PropertySet
@FormDefinition(startElement = "isInterrupting")
public class InterruptingNoneEventExecutionSet
        implements BPMNPropertySet {

    @Name
    @FieldLabel
    public static final transient String propertySetName = "Implementation/Execution";

    @Property
    @FormField(type = CheckBoxFieldType.class)
    @Valid
    private IsInterrupting isInterrupting;

    public InterruptingNoneEventExecutionSet() {
        this(new IsInterrupting(true));
    }

    public InterruptingNoneEventExecutionSet(final @MapsTo("isInterrupting") IsInterrupting isInterrupting) {
        this.isInterrupting = isInterrupting;
    }

    public String getPropertySetName() {
        return propertySetName;
    }

    public IsInterrupting getIsInterrupting() {
        return isInterrupting;
    }

    public void setIsInterrupting(IsInterrupting isInterrupting) {
        this.isInterrupting = isInterrupting;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof InterruptingNoneEventExecutionSet)) {
            return false;
        }

        InterruptingNoneEventExecutionSet that = (InterruptingNoneEventExecutionSet) o;

        return isInterrupting.equals(that.isInterrupting);
    }

    @Override
    public int hashCode() {
        return HashUtil.combineHashCodes(isInterrupting.hashCode());
    }
}
