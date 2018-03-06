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

package org.kie.workbench.common.stunner.forms.backend.service.impl;

import org.eclipse.bpmn2.Definitions;
import org.kie.workbench.common.forms.jbpm.server.service.BPMNFormModelGenerator;
import org.kie.workbench.common.forms.jbpm.server.service.formGeneration.BPMNFormGeneratorService;
import org.kie.workbench.common.forms.serialization.FormDefinitionSerializer;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.forms.backend.gen.FormGenerationModelProviders;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;

public class TestFormDefinitionGeneratorImpl extends FormDefinitionGeneratorImpl {

    private Definitions definitions;

    public TestFormDefinitionGeneratorImpl(FormGenerationModelProviders providers, IOService ioService, BPMNFormModelGenerator bpmnFormModelGenerator, FormDefinitionSerializer serializer, BPMNFormGeneratorService<Path> bpmnFormGeneratorService, Definitions definitions) {
        super(providers, ioService, bpmnFormModelGenerator, serializer, bpmnFormGeneratorService);
        this.definitions = definitions;
    }

    @Override
    protected Definitions toDefinitions(Diagram diagram) {
        return definitions;
    }
}
