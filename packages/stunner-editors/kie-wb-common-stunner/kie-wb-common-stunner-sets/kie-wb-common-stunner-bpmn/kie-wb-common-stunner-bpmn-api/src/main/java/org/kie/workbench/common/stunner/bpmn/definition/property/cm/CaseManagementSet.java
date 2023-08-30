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


package org.kie.workbench.common.stunner.bpmn.definition.property.cm;

import java.util.Objects;

import javax.validation.Valid;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;
import org.kie.workbench.common.forms.adf.definitions.annotations.FieldParam;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormDefinition;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormField;
import org.kie.workbench.common.forms.adf.definitions.settings.FieldPolicy;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textBox.type.TextBoxFieldType;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNPropertySet;
import org.kie.workbench.common.stunner.bpmn.forms.model.VariablesEditorFieldType;
import org.kie.workbench.common.stunner.bpmn.forms.model.cm.RolesEditorFieldType;
import org.kie.workbench.common.stunner.core.definition.annotation.Property;
import org.kie.workbench.common.stunner.core.util.HashUtil;

@Portable
@Bindable
@FormDefinition(
        policy = FieldPolicy.ONLY_MARKED,
        startElement = "caseIdPrefix"
)
public class CaseManagementSet implements BPMNPropertySet {

    @Property
    @FormField(type = TextBoxFieldType.class)
    private CaseIdPrefix caseIdPrefix;

    @Property
    @FormField(
            type = RolesEditorFieldType.class,
            afterElement = "caseIdPrefix"
    )
    @Valid
    private CaseRoles caseRoles;

    @Property
    @FormField(
            type = VariablesEditorFieldType.class,
            settings = {@FieldParam(name = "caseFileVariable", value = "true")}
    )
    @Valid
    private CaseFileVariables caseFileVariables;

    public CaseManagementSet() {
        this(new CaseIdPrefix(), new CaseRoles(), new CaseFileVariables());
    }

    public CaseManagementSet(final @MapsTo("caseIdPrefix") CaseIdPrefix caseIdPrefix,
                             final @MapsTo("caseRoles") CaseRoles caseRoles,
                             final @MapsTo("caseFileVariables") CaseFileVariables caseFileVariables) {
        this.caseRoles = caseRoles;
        this.caseFileVariables = caseFileVariables;
        this.caseIdPrefix = caseIdPrefix;
    }

    public CaseIdPrefix getCaseIdPrefix() {
        return caseIdPrefix;
    }

    public void setCaseIdPrefix(CaseIdPrefix caseIdPrefix) {
        this.caseIdPrefix = caseIdPrefix;
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
        return HashUtil.combineHashCodes(caseIdPrefix.hashCode(), caseRoles.hashCode(),
                                         caseFileVariables.hashCode());
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof CaseManagementSet)) {
            return false;
        }

        CaseManagementSet other = (CaseManagementSet) o;
        return Objects.equals(caseRoles, other.caseRoles) &&
                Objects.equals(getCaseIdPrefix(), other.caseIdPrefix) &&
                Objects.equals(caseFileVariables, other.caseFileVariables);
    }
}
