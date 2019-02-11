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

package org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.properties;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.eclipse.bpmn2.FormalExpression;
import org.eclipse.bpmn2.PotentialOwner;
import org.eclipse.bpmn2.ResourceAssignmentExpression;
import org.eclipse.bpmn2.UserTask;
import org.kie.workbench.common.stunner.bpmn.backend.converters.customproperties.CustomElement;
import org.kie.workbench.common.stunner.bpmn.backend.converters.customproperties.CustomInput;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.properties.Scripts;
import org.kie.workbench.common.stunner.bpmn.definition.property.assignee.Actors;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.OnEntryAction;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.OnExitAction;

import static org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.Factories.bpmn2;

public class UserTaskPropertyWriter extends MultipleInstanceActivityPropertyWriter {

    private final UserTask task;
    private final CustomInput<String> description;
    private final CustomInput<String> createdBy;
    private final CustomInput<String> taskName;
    private final CustomInput<String> groupId;
    private final CustomInput<Boolean> skippable;
    private final CustomInput<String> priority;
    private final CustomInput<String> subject;
    private final CustomInput<String> content;

    public UserTaskPropertyWriter(UserTask task, VariableScope variableScope) {
        super(task, variableScope);
        this.task = task;

        this.skippable = CustomInput.skippable.of(task);
        this.addItemDefinition(this.skippable.typeDef());

        this.priority = CustomInput.priority.of(task);
        this.addItemDefinition(this.priority.typeDef());

        this.subject = CustomInput.subject.of(task);
        this.addItemDefinition(this.subject.typeDef());

        this.description = CustomInput.description.of(task);
        this.addItemDefinition(this.description.typeDef());

        this.createdBy = CustomInput.createdBy.of(task);
        this.addItemDefinition(this.createdBy.typeDef());

        this.taskName = CustomInput.taskName.of(task);
        this.addItemDefinition(this.taskName.typeDef());

        this.groupId = CustomInput.groupId.of(task);
        this.addItemDefinition(this.groupId.typeDef());

        this.content = CustomInput.content.of(task);
        this.addItemDefinition(this.content.typeDef());
    }

    public void setAsync(boolean async) {
        CustomElement.async.of(task).set(async);
    }

    public void setSkippable(boolean skippable) {
        this.skippable.set(skippable);
    }

    public void setPriority(String priority) {
        this.priority.set(priority);
    }

    public void setSubject(String subject) {
        this.subject.set(subject);
    }

    public void setDescription(String description) {
        this.description.set(description);
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy.set(createdBy);
    }

    public void setAdHocAutostart(boolean autoStart) {
        CustomElement.autoStart.of(task).set(autoStart);
    }

    public void setTaskName(String taskName) {
        this.taskName.set(taskName);
    }

    public void setActors(Actors actors) {
        for (String actor : fromActorString(actors.getValue())) {
            PotentialOwner potentialOwner = bpmn2.createPotentialOwner();
            potentialOwner.setId(UUID.randomUUID().toString());

            FormalExpression formalExpression = bpmn2.createFormalExpression();
            formalExpression.setBody(actor);

            ResourceAssignmentExpression resourceAssignmentExpression =
                    bpmn2.createResourceAssignmentExpression();
            resourceAssignmentExpression.setExpression(formalExpression);

            potentialOwner.setResourceAssignmentExpression(resourceAssignmentExpression);

            task.getResources().add(potentialOwner);
        }
    }

    private List<String> fromActorString(String delimitedActors) {
        String[] split = delimitedActors.split(",");
        if (split.length == 1 && split[0].isEmpty()) {
            return Collections.emptyList();
        } else {
            return Arrays.asList(split);
        }
    }

    public void setGroupId(String value) {
        groupId.set(value);
    }

    public void setOnEntryAction(OnEntryAction onEntryAction) {
        Scripts.setOnEntryAction(task, onEntryAction);
    }

    public void setOnExitAction(OnExitAction onExitAction) {
        Scripts.setOnExitAction(task, onExitAction);
    }

    public void setContent(String content) {
        this.content.set(content);
    }

    public void setSLADueDate(String slaDueDate) {
        CustomElement.slaDueDate.of(task).set(slaDueDate);
    }
}
