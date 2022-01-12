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

package org.kie.workbench.common.stunner.bpmn.forms.service.adf.processing.processors.fields;

import javax.enterprise.context.Dependent;

import org.kie.workbench.common.forms.adf.engine.shared.formGeneration.FormGenerationContext;
import org.kie.workbench.common.forms.adf.engine.shared.formGeneration.processing.fields.FieldInitializer;
import org.kie.workbench.common.forms.adf.service.definitions.elements.FieldElement;
import org.kie.workbench.common.forms.model.FieldDefinition;
import org.kie.workbench.common.stunner.bpmn.forms.model.ScriptTypeFieldDefinition;
import org.kie.workbench.common.stunner.bpmn.forms.model.ScriptTypeMode;

@Dependent
public class ScriptTypeFieldInitializer implements FieldInitializer<ScriptTypeFieldDefinition> {

    @Override
    public boolean supports(FieldDefinition fieldDefinition) {
        return fieldDefinition instanceof ScriptTypeFieldDefinition;
    }

    @Override
    public void initialize(ScriptTypeFieldDefinition fieldDefinition,
                           FieldElement fieldElement,
                           FormGenerationContext context) {
        fieldDefinition.setMode(ScriptTypeMode.valueOf(fieldElement.getParams().getOrDefault("mode",
                                                                                             ScriptTypeMode.ACTION_SCRIPT.name())));
    }
}
