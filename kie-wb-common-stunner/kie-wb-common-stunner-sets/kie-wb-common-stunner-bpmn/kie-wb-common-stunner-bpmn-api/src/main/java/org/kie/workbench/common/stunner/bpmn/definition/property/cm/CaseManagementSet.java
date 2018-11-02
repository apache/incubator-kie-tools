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

package org.kie.workbench.common.stunner.bpmn.definition.property.cm;

import java.util.Objects;

import javax.validation.Valid;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormDefinition;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormField;
import org.kie.workbench.common.forms.adf.definitions.settings.FieldPolicy;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNPropertySet;
import org.kie.workbench.common.stunner.bpmn.forms.model.VariablesEditorFieldType;
import org.kie.workbench.common.stunner.bpmn.forms.model.cm.RolesEditorFieldType;
import org.kie.workbench.common.stunner.core.definition.annotation.Property;
import org.kie.workbench.common.stunner.core.definition.annotation.PropertySet;
import org.kie.workbench.common.stunner.core.util.HashUtil;

@Portable
@Bindable
@PropertySet
@FormDefinition(
        policy = FieldPolicy.ONLY_MARKED,
        startElement = "caseRoles"
)
public class CaseManagementSet implements BPMNPropertySet {

    @Property
    @FormField(
            type = RolesEditorFieldType.class
    )
    @Valid
    private CaseRoles caseRoles;

    @Property
    @FormField(
            type = VariablesEditorFieldType.class
    )
    @Valid
    private CaseFileVariables caseFileVariables;

    public CaseManagementSet() {
        this(new CaseRoles(), new CaseFileVariables());
    }

    public CaseManagementSet(final @MapsTo("caseRoles") CaseRoles caseRoles,
                             final @MapsTo("caseFileVariables") CaseFileVariables caseFileVariables) {
        this.caseRoles = caseRoles;
        this.caseFileVariables = caseFileVariables;
    }

    public CaseRoles getCaseRoles() {
        return caseRoles;
    }

    public void setCaseRoles(CaseRoles caseRoles) {
        this.caseRoles = caseRoles;
    }

    public CaseFileVariables getCaseFileVariables() {
        return caseFileVariables;
    }

    public void setCaseFileVariables(CaseFileVariables caseFileVariables) {
        this.caseFileVariables = caseFileVariables;
    }

    @Override
    public int hashCode() {
        return HashUtil.combineHashCodes(caseRoles.hashCode(),
                                         caseFileVariables.hashCode());
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof CaseManagementSet) {
            CaseManagementSet other = (CaseManagementSet) o;
            return Objects.equals(caseRoles, other.caseRoles) &&
                    Objects.equals(caseFileVariables, other.caseFileVariables);

        }
        return false;
    }
}
