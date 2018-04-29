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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.eclipse.bpmn2.UserTask;
import org.kie.workbench.common.forms.jbpm.model.authoring.task.TaskFormModel;
import org.kie.workbench.common.forms.model.ModelProperty;

public class TaskFormVariables {

    private String processId;

    private UserTask userTask;

    private String taskName;
    private Map<String, Variable> variables = new HashMap<>();

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

    public void addVariable(Variable variable) {
        addVariable(Optional.empty(),
                    variable);
    }

    public void addVariable(Optional<UserTask> userTask,
                            Variable variable) {

        Variable existingVariable = variables.get(variable.getName());
        if (existingVariable != null) {
            if (!existingVariable.getType().equals(variable.getType())) {
                valid = false;
                StringBuffer message = new StringBuffer("Type conflict on task variable '").append(variable.getName()).append("': The variable type defined by task '").append(this.userTask.getName()).append("' (").append(existingVariable.getType()).append(") doesn't match the ");
                if (userTask.isPresent()) {
                    message.append("variable type defined by task '").append(userTask.get().getName()).append("' ");
                } else {
                    message.append("variable type received ");
                }
                message.append("(").append(variable.getType()).append(").");
                errors.add(message.toString());
            } else {
                if (variable.isInput()) {
                    existingVariable.setInput(variable.isInput());
                }
                if (variable.isOutput()) {
                    existingVariable.setOutput(variable.isOutput());
                }
            }
        } else {
            variables.put(variable.getName(),
                          variable);
        }
    }

    public TaskFormModel toFormModel(Function<Variable, ModelProperty> converterFunction) {

        if (!isValid()) {
            return null;
        }

        List<ModelProperty> properties = variables.values().stream()
                .sorted((o1, o2) -> sort(o1, o2))
                .map(variable -> converterFunction.apply(variable))
                .filter(modelProperty -> modelProperty != null)
                .collect(Collectors.toList());

        return new TaskFormModel(processId, taskName, properties);
    }

    public void merge(TaskFormVariables other) {
        other.variables.values().forEach(taskVariable -> addVariable(Optional.of(other.userTask), taskVariable));
    }

    protected int sort(Variable variable1, Variable variable2) {
        boolean variable1OnlyInput = variable1.isInput() && !variable1.isOutput();
        boolean variable2OnlyInput = variable2.isInput() && !variable2.isOutput();

        if (variable1OnlyInput) {
            if (variable2OnlyInput) {
                return variable1.getName().compareToIgnoreCase(variable2.getName());
            } else {
                return -1;
            }
        }

        if (variable2OnlyInput) {
            return 1;
        }

        return variable1.getName().compareToIgnoreCase(variable2.getName());
    }
}
