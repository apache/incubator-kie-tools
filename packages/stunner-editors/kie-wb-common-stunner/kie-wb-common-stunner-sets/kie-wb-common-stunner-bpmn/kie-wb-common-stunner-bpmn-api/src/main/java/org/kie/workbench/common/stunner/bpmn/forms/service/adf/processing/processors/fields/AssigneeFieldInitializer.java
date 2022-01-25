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

package org.kie.workbench.common.stunner.bpmn.forms.service.adf.processing.processors.fields;

import javax.enterprise.context.Dependent;

import org.kie.workbench.common.forms.adf.engine.shared.formGeneration.FormGenerationContext;
import org.kie.workbench.common.forms.adf.engine.shared.formGeneration.processing.fields.FieldInitializer;
import org.kie.workbench.common.forms.adf.service.definitions.elements.FieldElement;
import org.kie.workbench.common.forms.model.FieldDefinition;
import org.kie.workbench.common.stunner.bpmn.forms.model.AssigneeEditorFieldDefinition;
import org.kie.workbench.common.stunner.bpmn.forms.model.AssigneeType;

@Dependent
public class AssigneeFieldInitializer implements FieldInitializer<AssigneeEditorFieldDefinition> {

    @Override
    public boolean supports(FieldDefinition fieldDefinition) {
        return fieldDefinition instanceof AssigneeEditorFieldDefinition;
    }

    @Override
    public void initialize(AssigneeEditorFieldDefinition field,
                           FieldElement fieldElement,
                           FormGenerationContext context) {
        field.setDefaultValue(fieldElement.getParams().getOrDefault("defaultValue",
                                                                    ""));
        field.setType(AssigneeType.valueOf(fieldElement.getParams().getOrDefault("type",
                                                                                 "USER")));
        field.setMax(Integer.valueOf(fieldElement.getParams().getOrDefault("max",
                                                                           "-1")));
    }
}
