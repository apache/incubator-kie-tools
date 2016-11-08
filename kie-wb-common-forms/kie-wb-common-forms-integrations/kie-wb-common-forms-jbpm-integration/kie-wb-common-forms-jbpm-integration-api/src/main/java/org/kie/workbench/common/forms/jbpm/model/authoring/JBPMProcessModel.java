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

package org.kie.workbench.common.forms.jbpm.model.authoring;

import java.util.List;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.workbench.common.forms.jbpm.model.authoring.process.BusinessProcessFormModel;
import org.kie.workbench.common.forms.jbpm.model.authoring.task.TaskFormModel;

@Portable
public class JBPMProcessModel {

    protected BusinessProcessFormModel processFormModel;

    protected List<TaskFormModel> taskFormModels;

    public JBPMProcessModel( @MapsTo( "processFormModel" ) BusinessProcessFormModel processFormModel,
                             @MapsTo( "taskFormModels" ) List<TaskFormModel> taskFormModels ) {
        this.processFormModel = processFormModel;
        this.taskFormModels = taskFormModels;
    }

    public BusinessProcessFormModel getProcessFormModel() {
        return processFormModel;
    }

    public void setProcessFormModel( BusinessProcessFormModel processFormModel ) {
        this.processFormModel = processFormModel;
    }

    public List<TaskFormModel> getTaskFormModels() {
        return taskFormModels;
    }

    public void setTaskFormModels( List<TaskFormModel> taskFormModels ) {
        this.taskFormModels = taskFormModels;
    }
}
