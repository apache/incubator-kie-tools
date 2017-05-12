/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.forms.editor.backend.indexing;

import javax.enterprise.inject.Instance;

import org.kie.workbench.common.forms.editor.type.FormResourceTypeDefinition;
import org.kie.workbench.common.forms.model.FormModel;
import org.kie.workbench.common.forms.serialization.FormDefinitionSerializer;
import org.kie.workbench.common.services.refactoring.backend.server.TestIndexer;
import org.kie.workbench.common.services.shared.project.KieProjectService;
import org.uberfire.io.IOService;

public class TestFormDefinitionIndexer extends FormDefinitionIndexer implements TestIndexer<FormResourceTypeDefinition> {

    public TestFormDefinitionIndexer(FormResourceTypeDefinition formResourceTypeDefinition,
                                     FormDefinitionSerializer formDefinitionSerializer,
                                     Instance<FormModelVisitorProvider<? extends FormModel>> visitorProviderInstance) {
        super(formResourceTypeDefinition,
              formDefinitionSerializer,
              visitorProviderInstance);
    }

    @Override
    public void setIOService(IOService ioService) {
        this.ioService = ioService;
    }

    @Override
    public void setProjectService(KieProjectService projectService) {
        this.projectService = projectService;
    }

    @Override
    public void setResourceTypeDefinition(FormResourceTypeDefinition type) {
        this.formResourceTypeDefinition = type;
    }
}
