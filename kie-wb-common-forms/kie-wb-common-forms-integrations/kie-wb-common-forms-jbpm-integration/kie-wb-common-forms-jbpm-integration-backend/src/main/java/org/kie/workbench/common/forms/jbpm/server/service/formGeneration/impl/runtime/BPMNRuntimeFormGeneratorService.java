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

package org.kie.workbench.common.forms.jbpm.server.service.formGeneration.impl.runtime;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.kie.workbench.common.forms.data.modeller.service.ext.ModelReaderService;
import org.kie.workbench.common.forms.jbpm.server.service.formGeneration.impl.AbstractBPMNFormGeneratorService;
import org.kie.workbench.common.forms.jbpm.server.service.formGeneration.impl.GenerationContext;
import org.kie.workbench.common.forms.jbpm.service.bpmn.util.BPMNVariableUtils;
import org.kie.workbench.common.forms.model.FieldDefinition;
import org.kie.workbench.common.forms.model.FormDefinition;
import org.kie.workbench.common.forms.service.shared.FieldManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Runtime
@Dependent
public class BPMNRuntimeFormGeneratorService extends AbstractBPMNFormGeneratorService<ClassLoader> {

    private static final Logger logger = LoggerFactory.getLogger(BPMNRuntimeFormGeneratorService.class);

    @Inject
    public BPMNRuntimeFormGeneratorService(final ModelReaderService<ClassLoader> modelReaderService,
                                           final FieldManager fieldManager) {
        super(modelReaderService, fieldManager);
    }

    @Override
    protected FormDefinition createRootFormDefinition(GenerationContext<ClassLoader> context) {
        FormDefinition form = new FormDefinition(context.getFormModel());

        form.setId(context.getFormModel().getFormName());
        form.setName(context.getFormModel().getFormName());

        context.getFormModel().getProperties().forEach(property -> {

            if (!BPMNVariableUtils.isValidInputName(property.getName())) {
                return;
            }

            FieldDefinition field = fieldManager.getDefinitionByModelProperty(property);

            if (field != null) {
                form.getFields().add(field);
            }
        });

        return form;
    }

    @Override
    protected boolean supportsEmptyNestedForms() {
        return false;
    }

    @Override
    protected void log(String message, Exception ex) {
        logger.warn(message, ex);
    }
}
