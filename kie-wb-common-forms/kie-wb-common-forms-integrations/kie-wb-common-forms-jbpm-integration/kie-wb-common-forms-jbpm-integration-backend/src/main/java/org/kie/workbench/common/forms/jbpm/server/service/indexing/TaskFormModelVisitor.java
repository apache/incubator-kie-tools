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

package org.kie.workbench.common.forms.jbpm.server.service.indexing;

import javax.enterprise.context.Dependent;

import org.kie.workbench.common.forms.editor.backend.indexing.FormModelVisitor;
import org.kie.workbench.common.forms.editor.backend.indexing.FormModelVisitorProvider;
import org.kie.workbench.common.forms.jbpm.model.authoring.task.TaskFormModel;
import org.kie.workbench.common.forms.model.FormDefinition;
import org.kie.workbench.common.services.refactoring.ResourceReference;
import org.kie.workbench.common.services.refactoring.backend.server.impact.ResourceReferenceCollector;
import org.kie.workbench.common.services.refactoring.service.PartType;
import org.kie.workbench.common.services.refactoring.service.ResourceType;

@Dependent
public class TaskFormModelVisitor extends FormModelVisitor<TaskFormModel> implements FormModelVisitorProvider<TaskFormModel> {

    @Override
    public Class<TaskFormModel> getModelType() {
        return TaskFormModel.class;
    }

    @Override
    public FormModelVisitor<TaskFormModel> getVisitor() {
        return new TaskFormModelVisitor();
    }

    @Override
    public void index(FormDefinition formDefinition, TaskFormModel formModel) {
        addResourceReference(formModel.getProcessId(),
                             ResourceType.BPMN2);
        formModel.getProperties().forEach(property -> addResourceReference(property.getTypeInfo().getClassName(),
                                                                           ResourceType.JAVA));
    }
}
