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


package org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.tasks;

import org.kie.workbench.common.stunner.bpmn.client.marshall.MarshallingRequest.Mode;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.TypedFactoryManager;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.properties.PropertyReaderFactory;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.properties.UserTaskPropertyReader;
import org.kie.workbench.common.stunner.bpmn.definition.UserTask;
import org.kie.workbench.common.stunner.bpmn.definition.property.assignee.Groupid;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.SLADueDate;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.AdHocAutostart;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.Content;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.CreatedBy;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.Description;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.IsAsync;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.IsMultipleInstance;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.MultipleInstanceCollectionInput;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.MultipleInstanceCollectionOutput;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.MultipleInstanceCompletionCondition;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.MultipleInstanceDataInput;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.MultipleInstanceDataOutput;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.MultipleInstanceExecutionMode;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.OnEntryAction;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.OnExitAction;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.Skippable;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.Subject;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.TaskName;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.TaskPriority;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.UserTaskExecutionSet;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.util.StringUtils;

public class TaskConverter extends BaseTaskConverter<UserTask, UserTaskExecutionSet> {

    public TaskConverter(TypedFactoryManager factoryManager, PropertyReaderFactory propertyReaderFactory,
                         Mode mode) {
        super(factoryManager, propertyReaderFactory, mode);
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
                                        p.getNotifications(),
                                        p.getReassignments(),
                                        new IsAsync(p.isAsync()),
                                        new Skippable(p.isSkippable()),
                                        new TaskPriority(StringUtils.revertIllegalCharsAttribute(p.getPriority())),
                                        new Subject(p.getSubject()),
                                        new Description(p.getDescription()),
                                        new CreatedBy(p.getCreatedBy()),
                                        new AdHocAutostart(p.isAdHocAutostart()),
                                        new IsMultipleInstance(p.isMultipleInstance()),
                                        new MultipleInstanceExecutionMode(p.isSequential()),
                                        new MultipleInstanceCollectionInput(p.getCollectionInput()),
                                        new MultipleInstanceDataInput(p.getDataInput()),
                                        new MultipleInstanceCollectionOutput(p.getCollectionOutput()),
                                        new MultipleInstanceDataOutput(p.getDataOutput()),
                                        new MultipleInstanceCompletionCondition(p.getCompletionCondition()),
                                        new OnEntryAction(p.getOnEntryAction()),
                                        new OnExitAction(p.getOnExitAction()),
                                        new Content(p.getContent()),
                                        new SLADueDate(p.getSLADueDate()));
    }
}
