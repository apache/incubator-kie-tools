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

import org.kie.workbench.common.forms.editor.service.backend.FormModelHandler;
import org.kie.workbench.common.forms.editor.service.backend.FormModelHandlerManager;
import org.kie.workbench.common.forms.jbpm.model.authoring.process.BusinessProcessFormModel;
import org.kie.workbench.common.forms.jbpm.model.authoring.task.TaskFormModel;
import org.kie.workbench.common.forms.jbpm.server.service.impl.BusinessProcessFormModelHandler;
import org.kie.workbench.common.forms.jbpm.server.service.impl.TaskFormModelHandler;
import org.kie.workbench.common.forms.model.FormModel;
import org.kie.workbench.common.forms.service.shared.FieldManager;
import org.kie.workbench.common.services.backend.project.ModuleClassLoaderHelper;
import org.kie.workbench.common.services.shared.project.KieModuleService;

public class TestFormModelHandlerManager implements FormModelHandlerManager {

    private KieModuleService projectService;

    private ModuleClassLoaderHelper projectClassLoaderHelper;

    private FieldManager fieldManager;

    public TestFormModelHandlerManager(KieModuleService projectService,
                                       ModuleClassLoaderHelper projectClassLoaderHelper,
                                       FieldManager fieldManager) {
        this.projectService = projectService;
        this.projectClassLoaderHelper = projectClassLoaderHelper;
        this.fieldManager = fieldManager;
    }

    @Override
    public FormModelHandler getFormModelHandler(Class<? extends FormModel> clazz) {
        if (BusinessProcessFormModel.class.equals(clazz)) {
            return new BusinessProcessFormModelHandler(projectService,
                                                       projectClassLoaderHelper,
                                                       fieldManager,
                                                       null);
        }
        if (TaskFormModel.class.equals(clazz)) {
            return new TaskFormModelHandler(projectService,
                                            projectClassLoaderHelper,
                                            fieldManager,
                                            null);
        }
        return null;
    }
}
