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

package org.kie.workbench.common.forms.jbpm.server.service.formGeneration.impl;

import java.util.HashMap;
import java.util.Map;

import org.kie.workbench.common.forms.data.modeller.service.ext.ModelReader;
import org.kie.workbench.common.forms.jbpm.model.authoring.JBPMFormModel;
import org.kie.workbench.common.forms.model.FormDefinition;

public class GenerationContext<SOURCE> {

    private JBPMFormModel formModel;

    private Map<String, FormDefinition> contextForms = new HashMap<>();

    private FormDefinition rootForm;

    private SOURCE source;

    private ModelReader modelReader;

    public GenerationContext(JBPMFormModel formModel,
                             SOURCE source,
                             ModelReader modelReader) {
        this.formModel = formModel;
        this.source = source;
        this.modelReader = modelReader;
    }

    public JBPMFormModel getFormModel() {
        return formModel;
    }

    public SOURCE getSource() {
        return source;
    }

    public ModelReader getModelReader() {
        return modelReader;
    }

    public Map<String, FormDefinition> getContextForms() {
        return contextForms;
    }

    public FormDefinition getRootForm() {
        return rootForm;
    }

    public void setRootForm(FormDefinition rootForm) {
        this.rootForm = rootForm;
    }
}
