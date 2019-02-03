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

package org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.tasks;

import org.kie.workbench.common.stunner.bpmn.backend.converters.TypedFactoryManager;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.properties.PropertyReaderFactory;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.properties.UserTaskPropertyReader;
import org.kie.workbench.common.stunner.bpmn.definition.UserTask;
import org.kie.workbench.common.stunner.bpmn.definition.property.assignee.Groupid;
import org.kie.workbench.common.stunner.bpmn.definition.property.connectors.Priority;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.AdHocAutostart;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.Content;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.CreatedBy;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.Description;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.IsAsync;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.OnEntryAction;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.OnExitAction;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.SLADueDate;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.Skippable;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.Subject;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.TaskName;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.UserTaskExecutionSet;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

public class TaskConverter extends BaseTaskConverter<UserTask, UserTaskExecutionSet> {

    public TaskConverter(TypedFactoryManager factoryManager, PropertyReaderFactory propertyReaderFactory) {
        super(factoryManager, propertyReaderFactory);
    }

    @Override
    protected Node<View<UserTask>, Edge> createNode(String id) {
        return factoryManager.newNode(id, UserTask.class);
    }

    @Override
    protected UserTaskExecutionSet createUserTaskExecutionSet(UserTaskPropertyReader p) {
        return new UserTaskExecutionSet(new TaskName(p.getTaskName()),
                                        p.getActors(),
                                        new Groupid(p.getGroupid()),
                                        p.getAssignmentsInfo(),
                                        new IsAsync(p.isAsync()),
                                        new Skippable(p.isSkippable()),
                                        new Priority(p.getPriority()),
                                        new Subject(p.getSubject()),
                                        new Description(p.getDescription()),
                                        new CreatedBy(p.getCreatedBy()),
                                        new AdHocAutostart(p.isAdHocAutostart()),
                                        new OnEntryAction(p.getOnEntryAction()),
                                        new OnExitAction(p.getOnExitAction()),
                                        new Content(p.getContent()),
                                        new SLADueDate(p.getSLADueDate()));
    }
}
