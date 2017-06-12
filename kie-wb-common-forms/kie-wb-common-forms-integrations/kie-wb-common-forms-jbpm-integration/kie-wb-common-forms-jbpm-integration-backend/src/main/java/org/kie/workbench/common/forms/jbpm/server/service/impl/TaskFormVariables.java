package org.kie.workbench.common.forms.jbpm.server.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.eclipse.bpmn2.UserTask;
import org.kie.workbench.common.forms.jbpm.model.authoring.JBPMVariable;
import org.kie.workbench.common.forms.jbpm.model.authoring.task.TaskFormModel;

public class TaskFormVariables {

    private String processId;

    private UserTask userTask;

    private String taskName;
    private Map<String, String> variables = new HashMap<>();

    private boolean valid = true;
    private List<String> errors = new ArrayList<>();

    public TaskFormVariables(UserTask userTask) {
        this.userTask = userTask;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getTaskName() {
        return taskName;
    }

    public boolean isValid() {
        return valid;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void addVariable(String variable,
                            String type) {
        addVariable(Optional.empty(),
                    variable,
                    type);
    }

    private void addVariable(Optional<UserTask> userTask,
                             String variable,
                             String type) {
        if (variables.containsKey(variable)) {
            String existingType = variables.get(variable);
            if (!existingType.equals(type)) {
                valid = false;
                StringBuffer message = new StringBuffer("Type conflict on task variable '").append(variable).append("': The variable type defined by task '").append(this.userTask.getName()).append("' (").append(existingType).append(") doesn't match the ");
                if (userTask.isPresent()) {
                    message.append("variable type defined by task '").append(userTask.get().getName()).append("' ");
                } else {
                    message.append("variable type received ");
                }
                message.append("(").append(type).append(").");
                errors.add(message.toString());
            }
        } else {
            variables.put(variable,
                          type);
        }
    }

    public TaskFormModel toFormModel() {

        if (!isValid()) {
            return null;
        }

        List<JBPMVariable> jbpmVariables = new ArrayList<>();
        variables.forEach((variable, type) -> jbpmVariables.add(new JBPMVariable(variable,
                                                                                 type)));
        return new TaskFormModel(processId,
                                 taskName,
                                 jbpmVariables);
    }

    public void merge(TaskFormVariables other) {
        other.variables.forEach((variable, type) -> addVariable(Optional.of(other.userTask),
                                                                variable,
                                                                type));
    }
}
