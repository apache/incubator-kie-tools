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

package org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.tasks;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.properties.PropertyWriterFactory;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.properties.UserTaskPropertyWriter;
import org.kie.workbench.common.stunner.bpmn.definition.BaseUserTask;
import org.kie.workbench.common.stunner.bpmn.definition.property.assignee.Actors;
import org.kie.workbench.common.stunner.bpmn.definition.property.assignee.Groupid;
import org.kie.workbench.common.stunner.bpmn.definition.property.dataio.AssignmentsInfo;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.MetaDataAttributes;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.Documentation;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.Name;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.SLADueDate;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.TaskGeneralSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.notification.NotificationsInfo;
import org.kie.workbench.common.stunner.bpmn.definition.property.reassignment.ReassignmentsInfo;
import org.kie.workbench.common.stunner.bpmn.definition.property.simulation.SimulationSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.AdHocAutostart;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.BaseUserTaskExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.Content;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.CreatedBy;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.Description;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.IsAsync;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.IsMultipleInstance;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.OnEntryAction;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.OnExitAction;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.ScriptTypeListValue;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.ScriptTypeValue;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.Skippable;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.Subject;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.TaskName;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.TaskPriority;
import org.kie.workbench.common.stunner.bpmn.definition.property.variables.AdvancedData;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.mockito.Mock;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class TaskConverterPriorityTest {

    @Mock
    TaskConverter taskConverter;

    PropertyWriterFactory propertyWriterFactory;

    @Mock
    TaskGeneralSet general;

    @Mock
    Node<View<BaseUserTask>, ?> n;

    @Mock
    View<BaseUserTask> view;

    @Mock
    BaseUserTask definition;

    @Mock
    BaseUserTaskExecutionSet baseUserTaskExecutionSet;

    @Test
    public void setPriorityMvel() {
        propertyWriterFactory = new PropertyWriterFactory();
        taskConverter = spy(new TaskConverter(propertyWriterFactory));
        when(n.getContent()).thenReturn(view);
        when(view.getBounds()).thenReturn(Bounds.create(0, 0, 10, 10));
        when(view.getDefinition()).thenReturn(definition);

        when(general.getName()).thenReturn(new Name("Name"));
        when(general.getDocumentation()).thenReturn(new Documentation());
        when(definition.getGeneral()).thenReturn(general);
        when(definition.getSimulationSet()).thenReturn(new SimulationSet());
        when(definition.getExecutionSet()).thenReturn(baseUserTaskExecutionSet);

        when(baseUserTaskExecutionSet.getTaskName()).thenReturn(new TaskName("taskName"));
        when(baseUserTaskExecutionSet.getActors()).thenReturn(new Actors());
        when(baseUserTaskExecutionSet.getAssignmentsinfo()).thenReturn(new AssignmentsInfo());
        when(baseUserTaskExecutionSet.getReassignmentsInfo()).thenReturn(new ReassignmentsInfo());
        when(baseUserTaskExecutionSet.getNotificationsInfo()).thenReturn(new NotificationsInfo());
        when(baseUserTaskExecutionSet.getSkippable()).thenReturn(new Skippable(true));
        when(baseUserTaskExecutionSet.getGroupid()).thenReturn(new Groupid("groupId"));
        when(baseUserTaskExecutionSet.getSubject()).thenReturn(new Subject());
        when(baseUserTaskExecutionSet.getDescription()).thenReturn(new Description());
        when(baseUserTaskExecutionSet.getPriority()).thenReturn(new TaskPriority("#{varOne}"));
        when(baseUserTaskExecutionSet.getIsAsync()).thenReturn(new IsAsync(true));
        when(baseUserTaskExecutionSet.getCreatedBy()).thenReturn(new CreatedBy());
        when(baseUserTaskExecutionSet.getAdHocAutostart()).thenReturn(new AdHocAutostart());
        when(baseUserTaskExecutionSet.getIsMultipleInstance()).thenReturn(new IsMultipleInstance(false));
        when(baseUserTaskExecutionSet.getOnEntryAction()).thenReturn(new OnEntryAction(new ScriptTypeListValue().addValue(new ScriptTypeValue("java",
                                                                                                                                              ""))));
        when(baseUserTaskExecutionSet.getOnExitAction()).thenReturn(new OnExitAction(new ScriptTypeListValue().addValue(new ScriptTypeValue("java", ""))));
        when(baseUserTaskExecutionSet.getContent()).thenReturn(new Content());
        when(baseUserTaskExecutionSet.getSlaDueDate()).thenReturn(new SLADueDate());
        when(definition.getAdvancedData()).thenReturn(new AdvancedData(new MetaDataAttributes()));

        doCallRealMethod().when(taskConverter).userTask(any());
        final UserTaskPropertyWriter propertyWriter = (UserTaskPropertyWriter) taskConverter.userTask(n);
        assertTrue(propertyWriter.getPriority().equals("<![CDATA[#{varOne}]]>"));
        System.out.println("Priority: " + propertyWriter.getPriority());
    }

    @Test
    public void setPriorityEscapeSpecialChars() {
        propertyWriterFactory = new PropertyWriterFactory();
        taskConverter = spy(new TaskConverter(propertyWriterFactory));
        when(n.getContent()).thenReturn(view);
        when(view.getBounds()).thenReturn(Bounds.create(0, 0, 10, 10));
        when(view.getDefinition()).thenReturn(definition);

        when(general.getName()).thenReturn(new Name("Name"));
        when(general.getDocumentation()).thenReturn(new Documentation());
        when(definition.getGeneral()).thenReturn(general);
        when(definition.getSimulationSet()).thenReturn(new SimulationSet());
        when(definition.getExecutionSet()).thenReturn(baseUserTaskExecutionSet);

        when(baseUserTaskExecutionSet.getTaskName()).thenReturn(new TaskName("taskName"));
        when(baseUserTaskExecutionSet.getActors()).thenReturn(new Actors());
        when(baseUserTaskExecutionSet.getAssignmentsinfo()).thenReturn(new AssignmentsInfo());
        when(baseUserTaskExecutionSet.getReassignmentsInfo()).thenReturn(new ReassignmentsInfo());
        when(baseUserTaskExecutionSet.getNotificationsInfo()).thenReturn(new NotificationsInfo());
        when(baseUserTaskExecutionSet.getSkippable()).thenReturn(new Skippable(true));
        when(baseUserTaskExecutionSet.getGroupid()).thenReturn(new Groupid("groupId"));
        when(baseUserTaskExecutionSet.getSubject()).thenReturn(new Subject());
        when(baseUserTaskExecutionSet.getDescription()).thenReturn(new Description());
        when(baseUserTaskExecutionSet.getPriority()).thenReturn(new TaskPriority("#{varOne<>&\"}"));
        when(baseUserTaskExecutionSet.getIsAsync()).thenReturn(new IsAsync(true));
        when(baseUserTaskExecutionSet.getCreatedBy()).thenReturn(new CreatedBy());
        when(baseUserTaskExecutionSet.getAdHocAutostart()).thenReturn(new AdHocAutostart());
        when(baseUserTaskExecutionSet.getIsMultipleInstance()).thenReturn(new IsMultipleInstance(false));
        when(baseUserTaskExecutionSet.getOnEntryAction()).thenReturn(new OnEntryAction(new ScriptTypeListValue().addValue(new ScriptTypeValue("java",
                                                                                                                                              ""))));
        when(baseUserTaskExecutionSet.getOnExitAction()).thenReturn(new OnExitAction(new ScriptTypeListValue().addValue(new ScriptTypeValue("java",
                                                                                                                                            ""))));
        when(baseUserTaskExecutionSet.getContent()).thenReturn(new Content());
        when(baseUserTaskExecutionSet.getSlaDueDate()).thenReturn(new SLADueDate());
        when(definition.getAdvancedData()).thenReturn(new AdvancedData(new MetaDataAttributes()));

        doCallRealMethod().when(taskConverter).userTask(any());
        final UserTaskPropertyWriter propertyWriter = (UserTaskPropertyWriter) taskConverter.userTask(n);
        assertTrue(propertyWriter.getPriority().equals("<![CDATA[#{varOne&lt;&gt;&amp;&quot;}]]>"));
    }
}