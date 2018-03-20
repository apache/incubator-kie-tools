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

package org.kie.workbench.common.forms.migration.tool.bpmn;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.kie.workbench.common.forms.jbpm.model.authoring.JBPMFormModel;
import org.kie.workbench.common.forms.jbpm.model.authoring.process.BusinessProcessFormModel;
import org.kie.workbench.common.forms.jbpm.model.authoring.task.TaskFormModel;

public class BPMNProcess {

    private String processId;

    private List<JBPMFormModel> formModels = new ArrayList<>();

    public BPMNProcess(BusinessProcessFormModel formModel) {
        this.processId = formModel.getProcessId();
        this.formModels.add(formModel);
    }

    public List<JBPMFormModel> getFormModels() {
        return formModels;
    }

    public void addTaskFormModel(TaskFormModel taskFormModel) {
        taskFormModel.setProcessId(processId);

        Optional<JBPMFormModel> optional = formModels.stream()
                .filter(taskModel -> taskFormModel.getFormName().equals(taskModel.getFormName()))
                .findAny();

        if(!optional.isPresent()) {
            formModels.add(taskFormModel);
        }
    }
}
