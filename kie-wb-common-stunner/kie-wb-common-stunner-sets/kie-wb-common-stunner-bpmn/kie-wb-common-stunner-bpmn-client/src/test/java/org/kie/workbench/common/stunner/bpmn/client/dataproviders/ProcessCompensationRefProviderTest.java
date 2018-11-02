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

package org.kie.workbench.common.stunner.bpmn.client.dataproviders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.forms.dynamic.model.config.SelectorData;
import org.kie.workbench.common.forms.dynamic.model.config.SelectorDataProvider;
import org.kie.workbench.common.forms.dynamic.service.shared.FormRenderingContext;
import org.kie.workbench.common.stunner.bpmn.definition.BaseSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.EmbeddedSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.EndNoneEvent;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateCompensationEvent;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateConditionalEvent;
import org.kie.workbench.common.stunner.bpmn.definition.Lane;
import org.kie.workbench.common.stunner.bpmn.definition.StartNoneEvent;
import org.kie.workbench.common.stunner.bpmn.definition.UserTask;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.BPMNGeneralSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.Name;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.TaskGeneralSet;
import org.kie.workbench.common.stunner.core.TestingGraphInstanceBuilder2;
import org.kie.workbench.common.stunner.core.TestingGraphMockHandler;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.select.SelectionControl;
import org.kie.workbench.common.stunner.core.client.session.impl.EditorSession;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import static org.kie.workbench.common.stunner.core.TestingGraphInstanceBuilder2.NODES.LEVEL0_NODE1;
import static org.kie.workbench.common.stunner.core.TestingGraphInstanceBuilder2.NODES.LEVEL0_NODE2;
import static org.kie.workbench.common.stunner.core.TestingGraphInstanceBuilder2.NODES.LEVEL1_NODE1;
import static org.kie.workbench.common.stunner.core.TestingGraphInstanceBuilder2.NODES.LEVEL1_NODE2;
import static org.kie.workbench.common.stunner.core.TestingGraphInstanceBuilder2.NODES.LEVEL1_SUB_PROCESS1;
import static org.kie.workbench.common.stunner.core.TestingGraphInstanceBuilder2.NODES.LEVEL2_NODE1;
import static org.kie.workbench.common.stunner.core.TestingGraphInstanceBuilder2.NODES.LEVEL2_NODE2;
import static org.kie.workbench.common.stunner.core.TestingGraphInstanceBuilder2.NODES.LEVEL2_SUB_PROCESS1;
import static org.kie.workbench.common.stunner.core.TestingGraphInstanceBuilder2.ROOT_UUID;
import static org.kie.workbench.common.stunner.core.TestingGraphInstanceBuilder2.buildLevel0Graph;
import static org.kie.workbench.common.stunner.core.TestingGraphInstanceBuilder2.buildLevel1Graph;
import static org.kie.workbench.common.stunner.core.TestingGraphInstanceBuilder2.buildLevel2Graph;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ProcessCompensationRefProviderTest {

    @Mock
    private SessionManager sessionManager;

    @Mock
    private EditorSession session;

    @Mock
    private AbstractCanvasHandler canvasHandler;

    @Mock
    private Diagram diagram;

    @Mock
    private Metadata metadata;

    @Mock
    private Graph graph;

    @Mock
    private SelectionControl selectionControl;

    @Mock
    private Node node;

    @Mock
    private FormRenderingContext renderingContext;

    private TestingGraphMockHandler graphTestHandler;

    private SelectorDataProvider provider;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() throws Exception {
        when(sessionManager.getCurrentSession()).thenReturn(session);
        when(session.getCanvasHandler()).thenReturn(canvasHandler);
        when(canvasHandler.getDiagram()).thenReturn(diagram);
        when(diagram.getGraph()).thenReturn(graph);
        when(diagram.getMetadata()).thenReturn(metadata);
        when(metadata.getCanvasRootUUID()).thenReturn(ROOT_UUID);
        when(session.getSelectionControl()).thenReturn(selectionControl);
        provider = new ProcessCompensationRefProvider(sessionManager);
        graphTestHandler = new TestingGraphMockHandler();
    }

    @Test
    public void testGetSelectorDataWhenNoNodeSelected() {
        setSelectedNode(null);
        SelectorData result = provider.getSelectorData(renderingContext);
        assertResult(new ArrayList<>(),
                     result);
    }

    @Test
    public void testGetSelectorDataLevel0TwoTasks() {
        TestingGraphInstanceBuilder2.Level0Graph graph = buildLevel0Graph(graphTestHandler,
                                                                          new TestingGraphInstanceBuilder2.Level0Graph(),
                                                                          new StartNoneEvent(),
                                                                          mockTask(LEVEL0_NODE1.nodeName()),
                                                                          mockTask(LEVEL0_NODE2.nodeName()),
                                                                          new EndNoneEvent());
        testGetSelectorData(graph.level0Node1,
                            expectedNodes(LEVEL0_NODE1,
                                          LEVEL0_NODE2));
    }

    @Test
    public void testGetSelectorDataLevel0OneTask() {
        TestingGraphInstanceBuilder2.Level0Graph graph = buildLevel0Graph(graphTestHandler,
                                                                          new TestingGraphInstanceBuilder2.Level0Graph(),
                                                                          new StartNoneEvent(),
                                                                          mockTask(LEVEL0_NODE1.nodeName()),
                                                                          new IntermediateConditionalEvent(),
                                                                          new EndNoneEvent());
        testGetSelectorData(graph.level0Node2,
                            expectedNodes(LEVEL0_NODE1));
    }

    @Test
    public void testGetSelectorDataLevel0ZeroTasks() {
        TestingGraphInstanceBuilder2.Level0Graph graph = buildLevel0Graph(graphTestHandler,
                                                                          new TestingGraphInstanceBuilder2.Level0Graph(),
                                                                          new StartNoneEvent(),
                                                                          new IntermediateCompensationEvent(),
                                                                          new IntermediateConditionalEvent(),
                                                                          new EndNoneEvent());
        testGetSelectorData(graph.level0Node2,
                            expectedNodes());
    }

    @Test
    public void testGetSelectorDataLevel1FourTasks() {
        TestingGraphInstanceBuilder2.Level1Graph graph = buildLevel1Graph(graphTestHandler,
                                                                          new TestingGraphInstanceBuilder2.Level1Graph(),
                                                                          new StartNoneEvent(),
                                                                          mockTask(LEVEL0_NODE1.nodeName()),
                                                                          mockTask(LEVEL0_NODE2.nodeName()),
                                                                          new EndNoneEvent(),
                                                                          mockSubProcess(LEVEL1_SUB_PROCESS1.nodeName()),
                                                                          mockTask(LEVEL1_NODE1.nodeName()),
                                                                          mockTask(LEVEL1_NODE2.nodeName()));
        testGetSelectorData(graph.level1Node1,
                            expectedNodes(LEVEL0_NODE1,
                                          LEVEL0_NODE2,
                                          LEVEL1_SUB_PROCESS1,
                                          LEVEL1_NODE1,
                                          LEVEL1_NODE2));
    }

    @Test
    public void testGetSelectorDataLevel1ThreeTasks() {
        TestingGraphInstanceBuilder2.Level1Graph graph = buildLevel1Graph(graphTestHandler,
                                                                          new TestingGraphInstanceBuilder2.Level1Graph(),
                                                                          new StartNoneEvent(),
                                                                          new IntermediateConditionalEvent(),
                                                                          mockTask(LEVEL0_NODE2.nodeName()),
                                                                          new EndNoneEvent(),
                                                                          mockSubProcess(LEVEL1_SUB_PROCESS1.nodeName()),
                                                                          mockTask(LEVEL1_NODE1.nodeName()),
                                                                          mockTask(LEVEL1_NODE2.nodeName()));
        testGetSelectorData(graph.level1Node1,
                            expectedNodes(LEVEL0_NODE2,
                                          LEVEL1_SUB_PROCESS1,
                                          LEVEL1_NODE1,
                                          LEVEL1_NODE2));
    }

    @Test
    public void testGetSelectorDataLevel1FourTasksButNodeInLevel0IsSelected() {
        TestingGraphInstanceBuilder2.Level1Graph graph = buildLevel1Graph(graphTestHandler,
                                                                          new TestingGraphInstanceBuilder2.Level1Graph(),
                                                                          new StartNoneEvent(),
                                                                          mockTask(LEVEL0_NODE1.nodeName()),
                                                                          mockTask(LEVEL0_NODE2.nodeName()),
                                                                          new EndNoneEvent(),
                                                                          mockSubProcess(LEVEL1_SUB_PROCESS1.nodeName()),
                                                                          mockTask(LEVEL1_NODE1.nodeName()),
                                                                          mockTask(LEVEL1_NODE2.nodeName()));
        testGetSelectorData(graph.level0Node1,
                            expectedNodes(LEVEL0_NODE1,
                                          LEVEL0_NODE2,
                                          LEVEL1_SUB_PROCESS1));
    }

    @Test
    public void testGetSelectorDataLevel2SixTasks() {
        TestingGraphInstanceBuilder2.Level2Graph graph = buildLevel2Graph(graphTestHandler,
                                                                          new TestingGraphInstanceBuilder2.Level2Graph(),
                                                                          new StartNoneEvent(),
                                                                          mockTask(LEVEL0_NODE1.nodeName()),
                                                                          mockTask(LEVEL0_NODE2.nodeName()),
                                                                          new EndNoneEvent(),
                                                                          mockSubProcess(LEVEL1_SUB_PROCESS1.nodeName()),
                                                                          mockTask(LEVEL1_NODE1.nodeName()),
                                                                          mockTask(LEVEL1_NODE2.nodeName()),
                                                                          mockSubProcess(LEVEL2_SUB_PROCESS1.nodeName()),
                                                                          mockTask(LEVEL2_NODE1.nodeName()),
                                                                          mockTask(LEVEL2_NODE2.nodeName()));
        testGetSelectorData(graph.level2Node1,
                            expectedNodes(LEVEL2_SUB_PROCESS1,
                                          LEVEL1_NODE1,
                                          LEVEL1_NODE2,
                                          LEVEL2_NODE1,
                                          LEVEL2_NODE2));
    }

    @Test
    public void testGetSelectorDataLevel2SixTasksButNodeInLevel1IsSelected() {
        TestingGraphInstanceBuilder2.Level2Graph graph = buildLevel2Graph(graphTestHandler,
                                                                          new TestingGraphInstanceBuilder2.Level2Graph(),
                                                                          new StartNoneEvent(),
                                                                          mockTask(LEVEL0_NODE1.nodeName()),
                                                                          mockTask(LEVEL0_NODE2.nodeName()),
                                                                          new EndNoneEvent(),
                                                                          mockSubProcess(LEVEL1_SUB_PROCESS1.nodeName()),
                                                                          mockTask(LEVEL1_NODE1.nodeName()),
                                                                          mockTask(LEVEL1_NODE2.nodeName()),
                                                                          mockSubProcess(LEVEL2_SUB_PROCESS1.nodeName()),
                                                                          mockTask(LEVEL2_NODE1.nodeName()),
                                                                          mockTask(LEVEL2_NODE2.nodeName()));
        testGetSelectorData(graph.level1Node1,
                            expectedNodes(LEVEL0_NODE1,
                                          LEVEL0_NODE2,
                                          LEVEL1_SUB_PROCESS1,
                                          LEVEL1_NODE1,
                                          LEVEL1_NODE2,
                                          LEVEL2_SUB_PROCESS1));
    }

    @Test
    public void testGetSelectorDataLevel2SixTasksWithLanesNodeInLevel0Selected() {
        TestingGraphInstanceBuilder2.Level2Graph graph = buildLevel2Graph(graphTestHandler,
                                                                          new TestingGraphInstanceBuilder2.Level2Graph(),
                                                                          new StartNoneEvent(),
                                                                          mockTask(LEVEL0_NODE1.nodeName()),
                                                                          mockTask(LEVEL0_NODE2.nodeName()),
                                                                          new EndNoneEvent(),
                                                                          mockLane(LEVEL1_SUB_PROCESS1.nodeName()),
                                                                          mockTask(LEVEL1_NODE1.nodeName()),
                                                                          mockTask(LEVEL1_NODE2.nodeName()),
                                                                          mockLane(LEVEL2_SUB_PROCESS1.nodeName()),
                                                                          mockTask(LEVEL2_NODE1.nodeName()),
                                                                          mockTask(LEVEL2_NODE2.nodeName()));
        testGetSelectorData(graph.level0Node1,
                            expectedNodes(LEVEL0_NODE1,
                                          LEVEL0_NODE2,
                                          LEVEL1_NODE1,
                                          LEVEL1_NODE2,
                                          LEVEL2_NODE1,
                                          LEVEL2_NODE2));
    }

    @Test
    public void testGetSelectorDataLevel2SixTasksWithLanesNodeInLevel1Selected() {
        TestingGraphInstanceBuilder2.Level2Graph graph = buildLevel2Graph(graphTestHandler,
                                                                          new TestingGraphInstanceBuilder2.Level2Graph(),
                                                                          new StartNoneEvent(),
                                                                          mockTask(LEVEL0_NODE1.nodeName()),
                                                                          mockTask(LEVEL0_NODE2.nodeName()),
                                                                          new EndNoneEvent(),
                                                                          mockLane(LEVEL1_SUB_PROCESS1.nodeName()),
                                                                          mockTask(LEVEL1_NODE1.nodeName()),
                                                                          mockTask(LEVEL1_NODE2.nodeName()),
                                                                          mockLane(LEVEL2_SUB_PROCESS1.nodeName()),
                                                                          mockTask(LEVEL2_NODE1.nodeName()),
                                                                          mockTask(LEVEL2_NODE2.nodeName()));
        testGetSelectorData(graph.level1Node1,
                            expectedNodes(LEVEL0_NODE1,
                                          LEVEL0_NODE2,
                                          LEVEL1_NODE1,
                                          LEVEL1_NODE2,
                                          LEVEL2_NODE1,
                                          LEVEL2_NODE2));
    }

    @Test
    public void testGetSelectorDataLevel2SixTasksWithLanesNodeInLevel2Selected() {
        TestingGraphInstanceBuilder2.Level2Graph graph = buildLevel2Graph(graphTestHandler,
                                                                          new TestingGraphInstanceBuilder2.Level2Graph(),
                                                                          new StartNoneEvent(),
                                                                          mockTask(LEVEL0_NODE1.nodeName()),
                                                                          mockTask(LEVEL0_NODE2.nodeName()),
                                                                          new EndNoneEvent(),
                                                                          mockLane(LEVEL1_SUB_PROCESS1.nodeName()),
                                                                          mockTask(LEVEL1_NODE1.nodeName()),
                                                                          mockTask(LEVEL1_NODE2.nodeName()),
                                                                          mockLane(LEVEL2_SUB_PROCESS1.nodeName()),
                                                                          mockTask(LEVEL2_NODE1.nodeName()),
                                                                          mockTask(LEVEL2_NODE2.nodeName()));
        testGetSelectorData(graph.level2Node1,
                            expectedNodes(LEVEL0_NODE1,
                                          LEVEL0_NODE2,
                                          LEVEL1_NODE1,
                                          LEVEL1_NODE2,
                                          LEVEL2_NODE1,
                                          LEVEL2_NODE2));
    }

    private void testGetSelectorData(Node selectedNode,
                                     List<TestingGraphInstanceBuilder2.NODES> expectedNodes) {
        setSelectedNode(selectedNode);
        SelectorData data = provider.getSelectorData(renderingContext);
        assertResult(expectedNodes,
                     data);
    }

    private void setSelectedNode(Node node) {
        if (node != null) {
            when(selectionControl.getSelectedItems()).thenReturn(Collections.singletonList(node.getUUID()));
            when(graph.getNode(node.getUUID())).thenReturn(node);
        } else {
            when(selectionControl.getSelectedItems()).thenReturn(Collections.EMPTY_LIST);
        }
    }

    private List<TestingGraphInstanceBuilder2.NODES> expectedNodes(TestingGraphInstanceBuilder2.NODES... nodes) {
        return Arrays.asList(nodes);
    }

    private void assertResult(List<TestingGraphInstanceBuilder2.NODES> expectedNodes,
                              SelectorData selectorData) {
        assertNotNull(selectorData);
        assertEquals(expectedNodes.size(),
                     selectorData.getValues().size());
        for (TestingGraphInstanceBuilder2.NODES node : expectedNodes) {
            assertTrue(selectorData.getValues().containsKey(node.uuid()));
            assertEquals(node.nodeName(),
                         selectorData.getValues().get(node.uuid()));
        }
    }

    private UserTask mockTask(String taskName) {
        UserTask userTask = mock(UserTask.class);
        TaskGeneralSet generalSet = mock(TaskGeneralSet.class);
        Name name = mock(Name.class);
        when(userTask.getGeneral()).thenReturn(generalSet);
        when(generalSet.getName()).thenReturn(name);
        when(name.getValue()).thenReturn(taskName);
        return userTask;
    }

    private BaseSubprocess mockSubProcess(String subProcessName) {
        EmbeddedSubprocess subProcess = mock(EmbeddedSubprocess.class);
        BPMNGeneralSet generalSet = mockGeneralSet(subProcessName);
        when(subProcess.getGeneral()).thenReturn(generalSet);
        return subProcess;
    }

    private Lane mockLane(String laneName) {
        Lane lane = mock(Lane.class);
        BPMNGeneralSet generalSet = mockGeneralSet(laneName);
        when(lane.getGeneral()).thenReturn(generalSet);
        return lane;
    }

    private BPMNGeneralSet mockGeneralSet(String name) {
        BPMNGeneralSet generalSet = mock(BPMNGeneralSet.class);
        Name nameProperty = mock(Name.class);
        when(generalSet.getName()).thenReturn(nameProperty);
        when(nameProperty.getValue()).thenReturn(name);
        return generalSet;
    }
}
