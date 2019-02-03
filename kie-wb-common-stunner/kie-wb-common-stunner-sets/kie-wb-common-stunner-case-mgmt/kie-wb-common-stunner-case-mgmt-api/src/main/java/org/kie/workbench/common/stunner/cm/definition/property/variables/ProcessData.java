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

package org.kie.workbench.common.stunner.cm.definition.property.variables;

import javax.validation.Valid;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormDefinition;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormField;
import org.kie.workbench.common.stunner.bpmn.definition.property.variables.BaseProcessData;
import org.kie.workbench.common.stunner.bpmn.forms.model.VariablesEditorFieldType;
import org.kie.workbench.common.stunner.core.definition.annotation.Property;
import org.kie.workbench.common.stunner.core.definition.annotation.PropertySet;

@Portable
@Bindable
@PropertySet
@FormDefinition
public class ProcessData implements BaseProcessData {

    @Property
    @FormField(
            type = VariablesEditorFieldType.class
    )
    @Valid
    private ProcessVariables processVariables;

    public ProcessData() {
        this(new ProcessVariables());
    }

    public ProcessData(final @MapsTo("processVariables") ProcessVariables processVariables) {
        this.processVariables = processVariables;
    }

    public ProcessData(final String processVariables) {
        this.processVariables = new ProcessVariables(processVariables);
    }

    @Override
    public ProcessVariables getProcessVariables() {
        return processVariables;
    }

    public void setProcessVariables(final ProcessVariables processVariables) {
        this.processVariables = processVariables;
    }

    @Override
    public int hashCode() {
        return processVariables.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof ProcessData) {
            ProcessData other = (ProcessData) o;
            return processVariables.equals(other.processVariables);
        }
        return false;
    }
}
