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

package org.kie.workbench.common.forms.jbpm.server.service.formGeneration;

import org.kie.workbench.common.forms.jbpm.model.authoring.JBPMFormModel;
import org.kie.workbench.common.forms.model.FormDefinition;
import org.kie.workbench.common.forms.model.FormModel;

/**
 * Service to generate the required FormDefinitions (process / task form and nested ones) for a BPMN FormModel
 * @param <SOURCE> Any source relevant to the service to gather information about existing {@link FormDefinition},
 * models...
 */
public interface BPMNFormGeneratorService<SOURCE> {

    /**
     * Generates the forms for a given {@link JBPMFormModel} and a given Source
     * @param formModel A {@link FormModel} containing relevant info about a BPMN process / task
     * @param source A source to be used to obtain information about existing {@link FormDefinition} or Data Models.
     * @return A {@link FormGenerationResult} containing the {@link FormDefinition} for the formModel and it's nested
     * forms if needed.
     */
    FormGenerationResult generateForms(JBPMFormModel formModel, SOURCE source);
}
