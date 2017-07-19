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

import java.util.List;

import org.kie.workbench.common.forms.jbpm.model.authoring.JBPMFormModel;
import org.kie.workbench.common.forms.model.FormDefinition;

/**
 * Result of the {@link BPMNFormGeneratorService}. It contains all the generated forms for a given {@link JBPMFormModel}
 */
public class FormGenerationResult {

    private FormDefinition rootForm;

    private List<FormDefinition> nestedForms;

    public FormGenerationResult(FormDefinition rootForm,
                                List<FormDefinition> nestedForms) {
        this.rootForm = rootForm;
        this.nestedForms = nestedForms;
    }

    public FormDefinition getRootForm() {
        return rootForm;
    }

    public List<FormDefinition> getNestedForms() {
        return nestedForms;
    }
}
