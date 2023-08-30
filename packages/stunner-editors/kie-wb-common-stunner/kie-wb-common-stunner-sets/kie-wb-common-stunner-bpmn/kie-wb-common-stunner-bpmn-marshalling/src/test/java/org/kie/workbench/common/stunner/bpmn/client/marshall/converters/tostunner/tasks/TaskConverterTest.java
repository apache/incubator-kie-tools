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

import org.eclipse.bpmn2.Bpmn2Factory;
import org.eclipse.bpmn2.Task;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.workbench.common.stunner.bpmn.client.marshall.MarshallingRequest;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.Result;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.BpmnNode;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.properties.TaskPropertyReader;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.properties.UserTaskPropertyReader;
import org.kie.workbench.common.stunner.bpmn.definition.NoneTask;
import org.kie.workbench.common.stunner.bpmn.definition.UserTask;
import org.kie.workbench.common.stunner.bpmn.definition.property.assignee.Actors;
import org.kie.workbench.common.stunner.bpmn.definition.property.background.BackgroundSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.dataio.AssignmentsInfo;
import org.kie.workbench.common.stunner.bpmn.definition.property.dimensions.RectangleDimensionsSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.font.FontSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.simulation.SimulationSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.ScriptTypeListValue;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

// TODO: Kogito - @RunWith(MockitoJUnitRunner.class)
@Ignore
public class TaskConverterTest extends BaseTaskConverterTest {

    private static final String NAME = "NAME";
    private static final String TASK_NAME = "TASK_NAME";
    private static final Actors ACTORS = new Actors();
    private static final String GROUP_ID = "GROUP_ID";
    private static final String DOCUMENTATION = "DOCUMENTATION";
    private static final Boolean IS_ASYNC = Boolean.TRUE;
    private static final Boolean SKIPPABLE = Boolean.TRUE;
    private static final String PRIORITY = "PRIORITY";
    private static final String SUBJECT = "SUBJECT";
    private static final String DESCRIPTION = "DESCRIPTION";
    private static final String CREATED_BY = "CREATED_BY";
    private static final Boolean IS_AUTOSTART = Boolean.TRUE;
    private static final Boolean SEQUENTIAL = Boolean.TRUE;
    private static final String COLLECTION_INPUT = "COLLECTION_INPUT";
    private static final String COLLECTION_OUTPUT = "COLLECTION_OUTPUT";
    private static final String DATA_INPUT = "DATA_INPUT";
    private static final String DATA_OUTPUT = "DATA_OUTPUT";
    private static final String COMPLETION_CONDITION = "COMPLETION_CONDITION";
    private static final ScriptTypeListValue ON_ENTRY_ACTION = new ScriptTypeListValue();
    private static final ScriptTypeListValue ON_EXIT_ACTION = new ScriptTypeListValue();
    private static final String CONTENT = "CONTENT";
    private static final String SLA_DUE_DATE = "SLA_DUE_DATE";
    private static final Bounds BOUNDS = Bounds.create();
    private static final SimulationSet SIMULATION_SET = new SimulationSet();
    private static final RectangleDimensionsSet RECTANGLE_DIMENSIONS_SET = new RectangleDimensionsSet();
    private static final FontSet FONT_SET = new FontSet();
    private static final BackgroundSet BACKGROUND_SET = new BackgroundSet();
    private static final AssignmentsInfo ASSIGNMENTS_INFO = new AssignmentsInfo();

    @Mock
    private org.eclipse.bpmn2.UserTask userTask;

    @Mock
    private Node<View<UserTask>, Edge> userTaskNode;

    @Mock
    private View<UserTask> userTaskContent;

    private UserTask userTaskDefinition;

    @Mock
    private UserTaskPropertyReader userTaskPropertyReader;

    @Override
    public void setUp() {
        super.setUp();
        userTaskDefinition = new UserTask();
        when(factoryManager.newNode(anyString(), eq(UserTask.class))).thenReturn(userTaskNode);
        when(userTaskNode.getContent()).thenReturn(userTaskContent);
        when(userTaskNode.getContent().getDefinition()).thenReturn(userTaskDefinition);
        when(propertyReaderFactory.of(userTask)).thenReturn(userTaskPropertyReader);

        when(userTaskPropertyReader.getName()).thenReturn(NAME);
        when(userTaskPropertyReader.getDocumentation()).thenReturn(DOCUMENTATION);
        when(userTaskPropertyReader.getSimulationSet()).thenReturn(SIMULATION_SET);
        when(userTaskPropertyReader.getBounds()).thenReturn(BOUNDS);
        when(userTaskPropertyReader.getRectangleDimensionsSet()).thenReturn(RECTANGLE_DIMENSIONS_SET);
        when(userTaskPropertyReader.getBackgroundSet()).thenReturn(BACKGROUND_SET);
        when(userTaskPropertyReader.getFontSet()).thenReturn(FONT_SET);
        when(userTaskPropertyReader.getTaskName()).thenReturn(TASK_NAME);
        when(userTaskPropertyReader.getActors()).thenReturn(ACTORS);
        when(userTaskPropertyReader.getGroupid()).thenReturn(GROUP_ID);
        when(userTaskPropertyReader.getAssignmentsInfo()).thenReturn(ASSIGNMENTS_INFO);
        when(userTaskPropertyReader.isAsync()).thenReturn(IS_ASYNC);
        when(userTaskPropertyReader.isSkippable()).thenReturn(SKIPPABLE);
        when(userTaskPropertyReader.getPriority()).thenReturn(PRIORITY);
        when(userTaskPropertyReader.getSubject()).thenReturn(SUBJECT);
        when(userTaskPropertyReader.getDescription()).thenReturn(DESCRIPTION);
        when(userTaskPropertyReader.getCreatedBy()).thenReturn(CREATED_BY);
        when(userTaskPropertyReader.isAdHocAutostart()).thenReturn(IS_AUTOSTART);
        when(userTaskPropertyReader.isSequential()).thenReturn(SEQUENTIAL);
        when(userTaskPropertyReader.getCollectionInput()).thenReturn(COLLECTION_INPUT);
        when(userTaskPropertyReader.getCollectionOutput()).thenReturn(COLLECTION_OUTPUT);
        when(userTaskPropertyReader.getDataInput()).thenReturn(DATA_INPUT);
        when(userTaskPropertyReader.getDataOutput()).thenReturn(DATA_OUTPUT);
        when(userTaskPropertyReader.getCompletionCondition()).thenReturn(COMPLETION_CONDITION);
        when(userTaskPropertyReader.getOnEntryAction()).thenReturn(ON_ENTRY_ACTION);
        when(userTaskPropertyReader.getOnExitAction()).thenReturn(ON_EXIT_ACTION);
        when(userTaskPropertyReader.getContent()).thenReturn(CONTENT);
        when(userTaskPropertyReader.getSLADueDate()).thenReturn(SLA_DUE_DATE);
    }

    @Override
    protected BaseTaskConverter createTaskConverter() {
        return new TaskConverter(factoryManager, propertyReaderFactory, MarshallingRequest.Mode.AUTO);
    }

    @Test
    public void testConvertUserTaskMI() {
        when(userTaskPropertyReader.isMultipleInstance()).thenReturn(true);
        BpmnNode node = (BpmnNode) tested.convert(userTask).value();
        UserTask result = (UserTask) node.value().getContent().getDefinition();
        assertCommonValues(result);
        assertTrue(result.getExecutionSet().getIsMultipleInstance().getValue());
        assertEquals(SEQUENTIAL, result.getExecutionSet().getMultipleInstanceExecutionMode().isSequential());
        assertEquals(COLLECTION_INPUT, result.getExecutionSet().getMultipleInstanceCollectionInput().getValue());
        assertEquals(COLLECTION_OUTPUT, result.getExecutionSet().getMultipleInstanceCollectionOutput().getValue());
        assertEquals(DATA_INPUT, result.getExecutionSet().getMultipleInstanceDataInput().getValue());
        assertEquals(DATA_OUTPUT, result.getExecutionSet().getMultipleInstanceDataOutput().getValue());
        assertEquals(COMPLETION_CONDITION, result.getExecutionSet().getMultipleInstanceCompletionCondition().getValue());
    }

    @Test
    public void testConvertUserTaskNonMI() {
        when(userTaskPropertyReader.isMultipleInstance()).thenReturn(false);
        when(userTaskPropertyReader.isSequential()).thenReturn(false);
        when(userTaskPropertyReader.getCollectionInput()).thenReturn(null);
        when(userTaskPropertyReader.getDataInput()).thenReturn(null);
        when(userTaskPropertyReader.getCollectionOutput()).thenReturn(null);
        when(userTaskPropertyReader.getDataOutput()).thenReturn(null);
        when(userTaskPropertyReader.getCompletionCondition()).thenReturn(null);
        BpmnNode node = (BpmnNode) tested.convert(userTask).value();
        UserTask result = (UserTask) node.value().getContent().getDefinition();
        assertCommonValues(result);
        assertFalse(result.getExecutionSet().getIsMultipleInstance().getValue());
        assertFalse(result.getExecutionSet().getMultipleInstanceExecutionMode().isSequential());
        assertNull(result.getExecutionSet().getMultipleInstanceCollectionInput().getValue());
        assertNull(result.getExecutionSet().getMultipleInstanceCollectionOutput().getValue());
        assertNull(result.getExecutionSet().getMultipleInstanceDataInput().getValue());
        assertNull(result.getExecutionSet().getMultipleInstanceDataOutput().getValue());
        assertNull(result.getExecutionSet().getMultipleInstanceCompletionCondition().getValue());
    }

    private void assertCommonValues(UserTask result) {
        assertEquals(NAME, result.getGeneral().getName().getValue());
        assertEquals(DOCUMENTATION, result.getGeneral().getDocumentation().getValue());
        assertEquals(SIMULATION_SET, result.getSimulationSet());
        verify(userTaskContent).setBounds(BOUNDS);
        assertEquals(ACTORS, result.getExecutionSet().getActors());
        assertEquals(GROUP_ID, result.getExecutionSet().getGroupid().getValue());
        assertEquals(ASSIGNMENTS_INFO, result.getExecutionSet().getAssignmentsinfo());
        assertEquals(IS_ASYNC, result.getExecutionSet().getIsAsync().getValue());
        assertEquals(SKIPPABLE, result.getExecutionSet().getSkippable().getValue());
        assertEquals(PRIORITY, result.getExecutionSet().getPriority().getValue());
        assertEquals(SUBJECT, result.getExecutionSet().getSubject().getValue());
        assertEquals(DESCRIPTION, result.getExecutionSet().getDescription().getValue());
        assertEquals(CREATED_BY, result.getExecutionSet().getCreatedBy().getValue());
        assertEquals(IS_AUTOSTART, result.getExecutionSet().getAdHocAutostart().getValue());
        assertEquals(ON_ENTRY_ACTION, result.getExecutionSet().getOnEntryAction().getValue());
        assertEquals(ON_EXIT_ACTION, result.getExecutionSet().getOnExitAction().getValue());
        assertEquals(CONTENT, result.getExecutionSet().getContent().getValue());
        assertEquals(SLA_DUE_DATE, result.getExecutionSet().getSlaDueDate().getValue());
    }

    // TODO: Kogito - @Test
    public void testConvertManualTask() {
        testConvertToDefaultTask(Bpmn2Factory.eINSTANCE.createManualTask());
    }

    @Test
    public void testConvertReceiveTask() {
        testConvertToDefaultTask(Bpmn2Factory.eINSTANCE.createReceiveTask());
    }

    @Test
    public void testConvertSendTask() {
        testConvertToDefaultTask(Bpmn2Factory.eINSTANCE.createSendTask());
    }

    @Test
    public void testConvertNoneTask() {
        testConvertToDefaultTask(Bpmn2Factory.eINSTANCE.createTask());
    }

    private void testConvertToDefaultTask(Task task) {
        TaskPropertyReader reader = mock(TaskPropertyReader.class);
        when(propertyReaderFactory.of(task)).thenReturn(reader);

        Node<View<NoneTask>, Edge> taskNode = mock(Node.class);
        when(factoryManager.newNode(anyString(), eq(NoneTask.class))).thenReturn(taskNode);

        View<NoneTask> taskContent = mock(View.class);
        when(taskNode.getContent()).thenReturn(taskContent);
        NoneTask taskDef = mock(NoneTask.class);
        when(taskContent.getDefinition()).thenReturn(taskDef);

        Result<BpmnNode> result = tested.convert(task);
        verify(factoryManager).newNode(anyString(), eq(NoneTask.class));
        assertTrue(result.value().value().getContent().getDefinition() instanceof NoneTask);
    }
}

