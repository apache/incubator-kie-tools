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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.enterprise.context.Dependent;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.bpmn2.Assignment;
import org.eclipse.bpmn2.DataInput;
import org.eclipse.bpmn2.DataInputAssociation;
import org.eclipse.bpmn2.DataOutput;
import org.eclipse.bpmn2.DataOutputAssociation;
import org.eclipse.bpmn2.Definitions;
import org.eclipse.bpmn2.FlowElement;
import org.eclipse.bpmn2.FlowElementsContainer;
import org.eclipse.bpmn2.FormalExpression;
import org.eclipse.bpmn2.ItemDefinition;
import org.eclipse.bpmn2.Process;
import org.eclipse.bpmn2.RootElement;
import org.eclipse.bpmn2.UserTask;
import org.kie.workbench.common.forms.jbpm.model.authoring.JBPMVariable;
import org.kie.workbench.common.forms.jbpm.model.authoring.process.BusinessProcessFormModel;
import org.kie.workbench.common.forms.jbpm.model.authoring.task.TaskFormModel;
import org.kie.workbench.common.forms.jbpm.server.service.BPMNFormModelGenerator;
import org.kie.workbench.common.forms.jbpm.service.bpmn.util.BPMNVariableUtils;

@Dependent
public class BPMNFormModelGeneratorImpl implements BPMNFormModelGenerator {

    @Override
    public BusinessProcessFormModel generateProcessFormModel(Definitions source) {

        Process process = getProcess(source);

        if (process != null) {

            List<JBPMVariable> variables = new ArrayList<>();

            process.getProperties().forEach(prop -> {
                String varName = prop.getId();
                String varType = getDefinitionType(prop.getItemSubjectRef());

                variables.add(new JBPMVariable(varName,
                                               varType));
            });

            return new BusinessProcessFormModel(process.getId(),
                                                process.getName(),
                                                variables);
        }

        return null;
    }

    @Override
    public List<TaskFormModel> generateTaskFormModels(Definitions source) {

        Process process = getProcess(source);

        List<TaskFormModel> models = new ArrayList<>();

        if (process != null) {

            generateTaskFormModels(process,
                                   models);
        }
        return models;
    }

    public void generateTaskFormModels(FlowElementsContainer container,
                                       List<TaskFormModel> models) {
        for (FlowElement fe : container.getFlowElements()) {
            if (fe instanceof UserTask) {
                models.add(getTaskFormModel((UserTask) fe,
                                            container));
            } else if (fe instanceof FlowElementsContainer) {
                generateTaskFormModels((FlowElementsContainer) fe,
                                       models);
            }
        }
    }

    @Override
    public TaskFormModel generateTaskFormModel(Definitions source,
                                               String taskId) {
        Process process = getProcess(source);

        if (process != null) {
            return generateTaskFormModel(taskId,
                                         process);
        }
        return null;
    }

    protected TaskFormModel generateTaskFormModel(String taskId,
                                                  FlowElementsContainer container) {
        for (FlowElement fe : container.getFlowElements()) {
            if (fe instanceof UserTask && fe.getId().equals(taskId)) {
                return getTaskFormModel((UserTask) fe,
                                        container);
            } else if (fe instanceof FlowElementsContainer) {
                TaskFormModel model = generateTaskFormModel(taskId,
                                                            (FlowElementsContainer) fe);
                if (model != null) {
                    return model;
                }
            }
        }
        return null;
    }

    protected TaskFormModel getTaskFormModel(UserTask userTask,
                                             FlowElementsContainer container) {

        Map<String, JBPMVariable> taskVariables = new HashMap<>();

        List<DataInputAssociation> dataInputAssociations = userTask.getDataInputAssociations();

        String taskFormName = "";

        if (dataInputAssociations != null) {

            for (DataInputAssociation inputAssociation : dataInputAssociations) {
                if (inputAssociation.getTargetRef() != null) {

                    String name = ((DataInput) inputAssociation.getTargetRef()).getName();

                    if (!BPMNVariableUtils.isValidInputName(name)) {
                        if (BPMNVariableUtils.TASK_FORM_VARIABLE.equals(name)) {
                            List<Assignment> assignments = inputAssociation.getAssignment();
                            for (Iterator<Assignment> it = assignments.iterator(); it.hasNext() && StringUtils.isEmpty(
                                    taskFormName); ) {
                                Assignment assignment = it.next();
                                if (assignment.getFrom() != null) {
                                    String taskName = ((FormalExpression) assignment.getFrom()).getBody();
                                    if (!StringUtils.isEmpty(taskName)) {
                                        taskFormName = taskName + BPMNVariableUtils.TASK_FORM_SUFFIX;
                                    }
                                }
                            }
                        }
                    } else {
                        String type = getDefinitionType(inputAssociation.getTargetRef().getItemSubjectRef());

                        taskVariables.put(name,
                                          new JBPMVariable(name,
                                                           type));
                    }
                }
            }
        }

        List<DataOutputAssociation> dataOutputAssociations = userTask.getDataOutputAssociations();

        if (dataOutputAssociations != null) {

            dataOutputAssociations.forEach(outputAssociation -> {
                if (outputAssociation.getSourceRef() != null && outputAssociation.getSourceRef().size() == 1) {

                    String name = ((DataOutput) outputAssociation.getSourceRef().get(0)).getName();

                    if (!taskVariables.containsKey(name)) {
                        String type = getDefinitionType(outputAssociation.getSourceRef().get(0).getItemSubjectRef());

                        taskVariables.put(name,
                                          new JBPMVariable(name,
                                                           type));
                    }
                }
            });
        }

        return new TaskFormModel(container.getId(),
                                 userTask.getId(),
                                 userTask.getName(),
                                 taskFormName,
                                 new ArrayList(taskVariables.values()));
    }

    protected Process getProcess(Definitions source) {
        for (RootElement re : source.getRootElements()) {
            if (re instanceof Process) {
                return (Process) re;
            }
        }
        return null;
    }

    private String getDefinitionType(ItemDefinition definition) {

        String type = null;

        if (definition != null) {
            type = definition.getStructureRef();
        }

        return BPMNVariableUtils.getRealTypeForInput(type);
    }
}
