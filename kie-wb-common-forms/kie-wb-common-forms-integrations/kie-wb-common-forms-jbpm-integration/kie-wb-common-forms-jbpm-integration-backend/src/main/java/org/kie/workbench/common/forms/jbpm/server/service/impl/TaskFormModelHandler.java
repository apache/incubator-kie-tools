/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.forms.jbpm.server.service.impl;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.kie.workbench.common.forms.jbpm.model.authoring.task.TaskFormModel;
import org.kie.workbench.common.forms.service.FieldManager;
import org.kie.workbench.common.forms.service.FormModelHandler;

@Dependent
public class TaskFormModelHandler extends AbstractJBPMFormModelHandler<TaskFormModel> {

    @Inject
    public TaskFormModelHandler( FieldManager fieldManager ) {
        super( fieldManager );
    }

    @Override
    public Class<TaskFormModel> getModelType() {
        return TaskFormModel.class;
    }

    @Override
    public FormModelHandler<TaskFormModel> newInstance() {
        return new TaskFormModelHandler( fieldManager );
    }
}
