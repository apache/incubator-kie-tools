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
        if(!taskIdFormNameMap.containsKey(taskId)) {
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

    public void registerTaskFormVariables(String taskId, TaskFormVariables taskFormVariables) {
        taskIdFormNameMap.put(taskId, taskFormVariables.getTaskName());

        Optional<TaskFormVariables> optional = Optional.ofNullable(getTaskFormVariablesByFormName(taskFormVariables.getTaskName()));

        if (optional.isPresent()) {
            optional.get().merge(taskFormVariables);
        } else {
            taskFormVariables.setProcessId(processId);
            taskFormVariablesRegistry.put(taskFormVariables.getTaskName(), taskFormVariables);
        }
    }
}
