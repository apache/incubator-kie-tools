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
package org.kie.workbench.common.forms.jbpm.server.service.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ProcessTaskFormsGenerationResult {

    private String processId;

    private Map<String, String> taskIdFormNameMap = new HashMap<>();

    private Map<String, TaskFormVariables> taskFormVariablesRegistry = new HashMap<>();

    public ProcessTaskFormsGenerationResult(String processId) {
        this.processId = processId;
    }

    public String getProcessId() {
        return processId;
    }

    public TaskFormVariables getTaskFormVariablesByTaskId(String taskId) {
        if (!taskIdFormNameMap.containsKey(taskId)) {
            return null;
        }
        return getTaskFormVariablesByFormName(taskIdFormNameMap.get(taskId));
    }

    public TaskFormVariables getTaskFormVariablesByFormName(String formName) {
        return taskFormVariablesRegistry.get(formName);
    }

    public Collection<TaskFormVariables> getAllTaskFormVariables() {
        return taskFormVariablesRegistry.values();
    }

    public void registerTaskFormVariables(String taskId,
                                          TaskFormVariables taskFormVariables) {
        taskIdFormNameMap.put(taskId,
                              taskFormVariables.getTaskName());

        Optional<TaskFormVariables> optional = Optional.ofNullable(getTaskFormVariablesByFormName(taskFormVariables.getTaskName()));

        if (optional.isPresent()) {
            optional.get().merge(taskFormVariables);
        } else {
            taskFormVariables.setProcessId(processId);
            taskFormVariablesRegistry.put(taskFormVariables.getTaskName(),
                                          taskFormVariables);
        }
    }
}
