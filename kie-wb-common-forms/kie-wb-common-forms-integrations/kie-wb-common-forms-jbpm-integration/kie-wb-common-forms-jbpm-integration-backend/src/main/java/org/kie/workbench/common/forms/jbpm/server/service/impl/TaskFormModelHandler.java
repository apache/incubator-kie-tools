/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.forms.jbpm.server.service.impl;

import java.util.Optional;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.kie.workbench.common.forms.editor.service.backend.FormModelHandler;
import org.kie.workbench.common.forms.editor.service.backend.SourceFormModelNotFoundException;
import org.kie.workbench.common.forms.jbpm.model.authoring.JBPMProcessModel;
import org.kie.workbench.common.forms.jbpm.model.authoring.task.TaskFormModel;
import org.kie.workbench.common.forms.jbpm.server.service.util.JBPMFormsIntegrationBackendConstants;
import org.kie.workbench.common.forms.jbpm.service.shared.BPMFinderService;
import org.kie.workbench.common.forms.service.shared.FieldManager;
import org.kie.workbench.common.services.backend.project.ModuleClassLoaderHelper;
import org.kie.workbench.common.services.shared.project.KieModuleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Dependent
public class TaskFormModelHandler extends AbstractJBPMFormModelHandler<TaskFormModel> {

    private static final Logger logger = LoggerFactory.getLogger(BusinessProcessFormModelHandler.class);

    @Inject
    public TaskFormModelHandler(KieModuleService projectService,
                                ModuleClassLoaderHelper classLoaderHelper,
                                FieldManager fieldManager,
                                BPMFinderService bpmFinderService) {
        super(projectService,
              classLoaderHelper,
              fieldManager,
              bpmFinderService);
    }

    @Override
    public Class<TaskFormModel> getModelType() {
        return TaskFormModel.class;
    }

    @Override
    public void checkSourceModel() throws SourceFormModelNotFoundException {
        JBPMProcessModel processModel = bpmFinderService.getModelForProcess(formModel.getProcessId(), path);

        if (processModel != null) {
            Optional<TaskFormModel> optional = processModel.getTaskFormModels().stream()
                    .filter(taskFormModel -> taskFormModel.getFormName().equals(formModel.getFormName()))
                    .findAny();

            if (!optional.isPresent()) {
                String[] params = new String[]{formModel.getTaskName(), formModel.getProcessId()};
                throwException(JBPMFormsIntegrationBackendConstants.BUNDLE,
                               JBPMFormsIntegrationBackendConstants.MISSING_TASK_SHORT_KEY, params,
                               JBPMFormsIntegrationBackendConstants.MISSING_TASK_FULL_KEY, params,
                               JBPMFormsIntegrationBackendConstants.PROCESS_KEY);
            }
        } else {
            String[] params = new String[]{formModel.getProcessId()};
            throwException(JBPMFormsIntegrationBackendConstants.BUNDLE,
                           JBPMFormsIntegrationBackendConstants.MISSING_PROCESS_SHORT_KEY, params,
                           JBPMFormsIntegrationBackendConstants.MISSING_PROCESS_FULL_KEY, params,
                           JBPMFormsIntegrationBackendConstants.PROCESS_KEY);
        }
    }

    @Override
    public FormModelHandler<TaskFormModel> newInstance() {
        return new TaskFormModelHandler(moduleService,
                                        classLoaderHelper,
                                        fieldManager,
                                        bpmFinderService);
    }

    protected TaskFormModel getSourceModel() {
        JBPMProcessModel processModel = bpmFinderService.getModelForProcess(formModel.getProcessId(), path);

        if (processModel != null) {
            Optional<TaskFormModel> optional = processModel.getTaskFormModels().stream()
                    .filter(taskFormModel -> taskFormModel.getFormName().equals(formModel.getFormName()))
                    .findAny();

            if (optional.isPresent()) {
                return optional.get();
            }
        }

        return null;
    }

    @Override
    protected void log(String message,
                       Exception e) {
        logger.warn(message, e);
    }
}
