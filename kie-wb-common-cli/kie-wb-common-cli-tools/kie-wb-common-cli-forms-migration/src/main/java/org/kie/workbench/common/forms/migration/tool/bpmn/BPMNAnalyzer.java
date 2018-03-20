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

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Consumer;

import org.eclipse.bpmn2.DataInput;
import org.eclipse.bpmn2.DataInputAssociation;
import org.eclipse.bpmn2.Definitions;
import org.eclipse.bpmn2.FlowElementsContainer;
import org.eclipse.bpmn2.FormalExpression;
import org.eclipse.bpmn2.Process;
import org.eclipse.bpmn2.UserTask;
import org.jbpm.simulation.util.BPMN2Utils;
import org.jsoup.parser.Parser;
import org.kie.workbench.common.forms.jbpm.model.authoring.process.BusinessProcessFormModel;
import org.kie.workbench.common.forms.jbpm.model.authoring.task.TaskFormModel;
import org.kie.workbench.common.forms.migration.tool.util.FormsMigrationConstants;

public class BPMNAnalyzer {

    public BPMNProcess read(InputStream inputStream) {
        Definitions definitions = BPMN2Utils.getDefinitions(inputStream);

        Optional<Process> processOptional = findProcess(definitions);

        if(!processOptional.isPresent()) {
            throw new RuntimeException("Cannot find Process on definitions");
        }

        Process process = processOptional.get();

        BusinessProcessFormModel formModel = new BusinessProcessFormModel(process.getId(), process.getName(), new ArrayList<>());

        BPMNProcess bpmmProcess = new BPMNProcess(formModel);

        readContainerUserTasks(process, bpmmProcess::addTaskFormModel);

        return bpmmProcess;
    }

    private void readContainerUserTasks(FlowElementsContainer container, Consumer<TaskFormModel> consumer) {

        container.getFlowElements()
                .stream()
                .filter(flowElement -> flowElement instanceof UserTask)
                .map(flowElement -> (UserTask) flowElement).forEach(userTask -> readUserTask(userTask,
                                                                                                                                                                                consumer));

        container.getFlowElements()
                .stream()
                .filter(flowElement -> flowElement instanceof FlowElementsContainer).map(flowElement -> (FlowElementsContainer) flowElement)
                .forEach(flowElementsContainer -> readContainerUserTasks(flowElementsContainer, consumer));
    }

    private void readUserTask(UserTask userTask, Consumer<TaskFormModel> consumer) {

        userTask.getDataInputAssociations()
                .stream()
                .filter(inputAssociation -> inputAssociation.getTargetRef() != null && FormsMigrationConstants.TASK_FORM_VARIABLE.equals(((DataInput)inputAssociation.getTargetRef()).getName()))
                .findAny()
                .ifPresent(inputAssociation -> consumer.accept(new TaskFormModel("", readTaskFormName(inputAssociation), new ArrayList<>())));
    }

    private String readTaskFormName(DataInputAssociation inputAssociation) {

        Optional<FormalExpression> optional = inputAssociation.getAssignment()
                .stream()
                .filter(assignment -> assignment.getFrom() != null && assignment.getFrom() instanceof FormalExpression)
                .map(assignment -> (FormalExpression)assignment.getFrom())
                .findAny();

        if(optional.isPresent()) {
            return Parser.xmlParser().parseInput(optional.get().getBody(), "").toString();
        }

        return "";
    }

    private Optional<Process> findProcess(Definitions definitions) {
        return definitions.getRootElements()
                .stream()
                .filter(rootElement -> rootElement instanceof Process)
                .map(rootElement -> (Process)rootElement)
                .findAny();
    }
}
