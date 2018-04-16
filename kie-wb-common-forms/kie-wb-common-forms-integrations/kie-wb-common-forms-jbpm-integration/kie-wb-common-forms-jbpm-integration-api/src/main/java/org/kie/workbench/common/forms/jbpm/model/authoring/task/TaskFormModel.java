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

package org.kie.workbench.common.forms.jbpm.model.authoring.task;

import java.util.List;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.workbench.common.forms.jbpm.model.authoring.AbstractJBPMFormModel;
import org.kie.workbench.common.forms.jbpm.service.bpmn.util.BPMNVariableUtils;
import org.kie.workbench.common.forms.model.ModelProperty;

@Portable
public class TaskFormModel extends AbstractJBPMFormModel {

    private String taskName;

    private TaskFormModel() {
        // Only for serialization purposes
    }

    public TaskFormModel(@MapsTo("processId") String processId,
                         @MapsTo("taskName") String taskName,
                         @MapsTo("properties") List<ModelProperty> properties) {
        super(processId, properties);

        this.taskName = taskName;
    }

    @Override
    public String getName() {
        return "task";
    }

    public String getTaskName() {
        return taskName;
    }

    @Override
    public String getFormName() {
        return taskName + BPMNVariableUtils.TASK_FORM_SUFFIX;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        TaskFormModel that = (TaskFormModel) o;

        if (!processId.equals(that.processId)) {
            return false;
        }

        return taskName.equals(that.taskName);
    }

    @Override
    public int hashCode() {
        int result = processId.hashCode();
        result = ~~result;
        result = 31 * result + taskName.hashCode();
        result = ~~result;
        return result;
    }
}
