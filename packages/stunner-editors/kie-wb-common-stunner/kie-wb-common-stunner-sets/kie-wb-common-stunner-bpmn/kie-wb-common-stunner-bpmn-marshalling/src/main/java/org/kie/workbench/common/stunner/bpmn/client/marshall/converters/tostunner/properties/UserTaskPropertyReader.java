/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.properties;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.bpmn2.FormalExpression;
import org.eclipse.bpmn2.PotentialOwner;
import org.eclipse.bpmn2.ResourceRole;
import org.eclipse.bpmn2.UserTask;
import org.eclipse.bpmn2.di.BPMNDiagram;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.customproperties.CustomElement;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.customproperties.CustomInput;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.DefinitionResolver;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.util.FormalExpressionBodyHandler;
import org.kie.workbench.common.stunner.bpmn.definition.property.assignee.Actors;
import org.kie.workbench.common.stunner.bpmn.definition.property.notification.NotificationsInfo;
import org.kie.workbench.common.stunner.bpmn.definition.property.reassignment.ReassignmentsInfo;

public class UserTaskPropertyReader extends MultipleInstanceActivityPropertyReader {

    private final UserTask task;

    public UserTaskPropertyReader(UserTask element, BPMNDiagram diagram, DefinitionResolver definitionResolver) {
        super(element, diagram, definitionResolver);
        this.task = element;
    }

    public Actors getActors() {
        // get the user task actors
        List<ResourceRole> roles = task.getResources();
        List<String> users = new ArrayList<>();
        for (ResourceRole role : roles) {
            if (role instanceof PotentialOwner) {
                FormalExpression fe = (FormalExpression)
                        role.getResourceAssignmentExpression()
                                .getExpression();
                users.add(FormalExpressionBodyHandler.of(fe).getBody());
            }
        }
        return new Actors(renderActors(users));
    }

    private String renderActors(final Collection<String> actors) {
        return actors.stream().collect(Collectors.joining(","));
    }

    public ReassignmentsInfo getReassignments() {
        return ReassignmentsInfos.of(task.getDataInputAssociations());
    }

    public NotificationsInfo getNotifications() {
        return NotificationsInfos.of(task.getDataInputAssociations());
    }

    public String getTaskName() {
        return CustomInput.taskName.of(task).get();
    }

    public String getGroupid() {
        return CustomInput.groupId.of(task).get();
    }

    public boolean isAsync() {
        return CustomElement.async.of(element).get();
    }

    public boolean isSkippable() {
        return CustomInput.skippable.of(task).get();
    }

    public String getPriority() {
        return CustomInput.priority.of(task).get();
    }

    public String getSubject() {
        return CustomInput.subject.of(task).get();
    }

    public String getDescription() {
        return CustomInput.description.of(task).get();
    }

    public String getCreatedBy() {
        return CustomInput.createdBy.of(task).get();
    }

    public boolean isAdHocAutostart() {
        return CustomElement.autoStart.of(element).get();
    }

    public String getContent() {
        return CustomInput.content.of(task).get();
    }

    public String getSLADueDate() {
        return CustomElement.slaDueDate.of(element).get();
    }
}
