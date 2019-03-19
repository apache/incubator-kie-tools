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

package org.kie.workbench.common.forms.jbpm.server.service;

import java.util.List;

import org.eclipse.bpmn2.Definitions;
import org.eclipse.bpmn2.Process;
import org.kie.workbench.common.forms.jbpm.model.authoring.process.BusinessProcessFormModel;
import org.kie.workbench.common.forms.jbpm.model.authoring.task.TaskFormModel;
import org.uberfire.backend.vfs.Path;

public interface BPMNFormModelGenerator {

    /**
     * Generates a {@link BusinessProcessFormModel} for the given Definitions
     */
    BusinessProcessFormModel generateProcessFormModel(Definitions source, Path path);

    /**
     * Generates a List with all the {@link TaskFormModel} on the given Definitions
     */
    List<TaskFormModel> generateTaskFormModels(Definitions source, Path path);

    /**
     * Generates the {@link TaskFormModel} on the Definitions for a given taskId
     */
    TaskFormModel generateTaskFormModel(Definitions source, String taskId, Path path);

    /**
     * Gets the Process from the Definitions
     */
    Process getProcess(Definitions source);
}
