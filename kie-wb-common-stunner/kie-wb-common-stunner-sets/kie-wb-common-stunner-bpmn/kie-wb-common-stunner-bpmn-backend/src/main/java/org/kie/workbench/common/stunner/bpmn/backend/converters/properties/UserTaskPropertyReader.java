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

package org.kie.workbench.common.stunner.bpmn.backend.converters.properties;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.eclipse.bpmn2.Assignment;
import org.eclipse.bpmn2.DataInput;
import org.eclipse.bpmn2.DataInputAssociation;
import org.eclipse.bpmn2.FormalExpression;
import org.eclipse.bpmn2.InputOutputSpecification;
import org.eclipse.bpmn2.PotentialOwner;
import org.eclipse.bpmn2.ResourceRole;
import org.eclipse.bpmn2.UserTask;
import org.eclipse.bpmn2.di.BPMNPlane;
import org.kie.workbench.common.stunner.bpmn.backend.converters.DefinitionResolver;
import org.kie.workbench.common.stunner.bpmn.definition.property.simulation.SimulationSet;

import static java.util.stream.Collectors.joining;

public class UserTaskPropertyReader extends TaskPropertyReader {

    private final UserTask task;

    public UserTaskPropertyReader(UserTask element, BPMNPlane plane, DefinitionResolver definitionResolver) {
        super(element, plane, definitionResolver);
        this.task = element;
    }

    public String getTaskName() {
        return optionalInput("TaskName").orElse("Task");
    }

    public String getActors() {
        // get the user task actors
        List<ResourceRole> roles = task.getResources();
        return roles.stream()
                .filter(role -> role instanceof PotentialOwner)
                .map(this::getRoleName)
                .collect(joining(","));
    }

    private String getRoleName(ResourceRole role) {
        return (
                (FormalExpression) role.getResourceAssignmentExpression().getExpression()
        ).getBody();
    }

    public String getGroupid() {
        return input("GroupId");
    }

    public String getAssignmentsInfo() {
        InputOutputSpecification ioSpecification = task.getIoSpecification();
        if (ioSpecification == null) {
            return (
                    AssignmentsInfos.makeString(
                            Collections.emptyList(),
                            Collections.emptyList(),
                            task.getDataInputAssociations(),
                            Collections.emptyList(),
                            Collections.emptyList(),
                            task.getDataOutputAssociations()
                    )
            );
        } else {
            return (
                    AssignmentsInfos.makeWrongString(
                            ioSpecification.getDataInputs(),
                            task.getDataInputAssociations(),
                            ioSpecification.getDataOutputs(),
                            task.getDataOutputAssociations()
                    )
            );
        }
    }

    public boolean isAsync() {
        return Boolean.parseBoolean(metaData("customAsync"));
    }

    public boolean isSkippable() {
        return Boolean.parseBoolean(input("Skippable"));
    }

    public String getPriority() {
        return input("Priority");
    }

    public String getSubject() {
        return input("Comment");
    }

    public String getDescription() {
        return input("Description");
    }

    public String getCreatedBy() {
        return input("CreatedBy");
    }

    public boolean isAdHocAutostart() {
        return Boolean.parseBoolean(metaData("customAutoStart"));
    }

    public String input(String name) {
        return optionalInput(name).orElse("");
    }

    private Optional<String> optionalInput(String name) {
        for (DataInputAssociation din : task.getDataInputAssociations()) {
            DataInput targetRef = (DataInput) (din.getTargetRef());
            if (targetRef.getName().equalsIgnoreCase(name)) {
                Assignment assignment = din.getAssignment().get(0);
                return Optional.of(evaluate(assignment).toString());
            }
        }
        return Optional.empty();
    }

    private static Object evaluate(Assignment assignment) {
        return ((FormalExpression) assignment.getFrom()).getMixed().getValue(0);
    }

    public SimulationSet getSimulationSet() {
        return definitionResolver.extractSimulationSet(task);
    }
}
