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

package org.kie.workbench.common.forms.jbpm.server.service.formGeneration.test;

import org.kie.workbench.common.forms.data.modeller.service.DataObjectFinderService;
import org.kie.workbench.common.forms.data.modeller.service.impl.DataModellerFieldGenerator;
import org.kie.workbench.common.forms.data.modeller.service.impl.DataObjectFormModelHandler;
import org.kie.workbench.common.forms.editor.service.backend.FormModelHandler;
import org.kie.workbench.common.forms.editor.service.backend.FormModelHandlerManager;
import org.kie.workbench.common.forms.jbpm.model.authoring.process.BusinessProcessFormModel;
import org.kie.workbench.common.forms.jbpm.model.authoring.task.TaskFormModel;
import org.kie.workbench.common.forms.jbpm.server.service.impl.BusinessProcessFormModelHandler;
import org.kie.workbench.common.forms.jbpm.server.service.impl.TaskFormModelHandler;
import org.kie.workbench.common.forms.model.FormModel;
import org.kie.workbench.common.forms.service.FieldManager;

public class TestFormModelHandlerManager implements FormModelHandlerManager {

    private FieldManager fieldManager;

    private DataObjectFinderService finderService;

    private DataModellerFieldGenerator dataModellerFieldGenerator;

    public TestFormModelHandlerManager(FieldManager fieldManager,
                                       DataObjectFinderService finderService,
                                       DataModellerFieldGenerator dataModellerFieldGenerator) {
        this.fieldManager = fieldManager;
        this.finderService = finderService;
        this.dataModellerFieldGenerator = dataModellerFieldGenerator;
    }

    @Override
    public FormModelHandler getFormModelHandler(Class<? extends FormModel> clazz) {
        if (BusinessProcessFormModel.class.equals(clazz)) {
            return new BusinessProcessFormModelHandler(fieldManager);
        }
        if (TaskFormModel.class.equals(clazz)) {
            return new TaskFormModelHandler(fieldManager);
        }
        return new DataObjectFormModelHandler(finderService,
                                              dataModellerFieldGenerator);
    }
}
