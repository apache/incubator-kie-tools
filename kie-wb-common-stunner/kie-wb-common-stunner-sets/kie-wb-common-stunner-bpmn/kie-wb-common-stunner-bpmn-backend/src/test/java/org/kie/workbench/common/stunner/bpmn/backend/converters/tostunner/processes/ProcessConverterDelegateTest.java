/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.processes;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.eclipse.bpmn2.Bpmn2Factory;
import org.eclipse.bpmn2.Definitions;
import org.eclipse.bpmn2.FlowElement;
import org.eclipse.bpmn2.FlowNode;
import org.eclipse.bpmn2.Lane;
import org.eclipse.bpmn2.LaneSet;
import org.eclipse.bpmn2.RootElement;
import org.eclipse.bpmn2.Task;
import org.eclipse.bpmn2.di.BPMNDiagram;
import org.eclipse.bpmn2.di.BPMNPlane;
import org.eclipse.bpmn2.impl.LaneImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.BPMNDefinitionSet;
import org.kie.workbench.common.stunner.bpmn.backend.BPMNTestDefinitionFactory;
import org.kie.workbench.common.stunner.bpmn.backend.converters.TypedFactoryManager;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.BaseConverterFactory;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.BpmnNode;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.ConverterFactory;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.DefinitionResolver;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.properties.BasePropertyReader;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.properties.PropertyReaderFactory;
import org.kie.workbench.common.stunner.core.backend.StunnerTestingGraphBackendAPI;
import org.kie.workbench.common.stunner.core.graph.impl.NodeImpl;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ProcessConverterDelegateTest {

    @Mock
    private PropertyReaderFactory propertyReaderFactory;

    private DefinitionResolver definitionResolver;

    private BaseConverterFactory factory;

    @Mock
    private BasePropertyReader basePropertyReader;

    @Mock
    private org.eclipse.bpmn2.Process process;

    @Mock
    private BPMNDiagram diagram;

    @Mock
    private BPMNPlane plane;

    private BpmnNode parentNode;

    private ProcessConverterDelegate converterDelegate;

    @Mock
    private Definitions definitions;

    @Before
    public void setUp() throws Exception {
        parentNode = new BpmnNode.Simple(new NodeImpl<>("ParentNode"), basePropertyReader);
        when(diagram.getPlane()).thenReturn(plane);
        List<RootElement> rootElements = Collections.singletonList(process);
        List<BPMNDiagram> diagrams = Collections.singletonList(diagram);
        when(definitions.getRootElements()).thenReturn(rootElements);
        when(definitions.getDiagrams()).thenReturn(diagrams);
        when(definitions.getRelationships()).thenReturn(Collections.emptyList());

        definitionResolver = new DefinitionResolver(definitions, Collections.emptyList());
        StunnerTestingGraphBackendAPI api = StunnerTestingGraphBackendAPI.build(BPMNDefinitionSet.class,
                                                                                new BPMNTestDefinitionFactory());
        TypedFactoryManager typedFactoryManager = new TypedFactoryManager(api.getFactoryManager());
        factory = new ConverterFactory(definitionResolver, typedFactoryManager);
        converterDelegate = new ProcessConverterDelegate(typedFactoryManager,
                                                         propertyReaderFactory,
                                                         definitionResolver,
                                                         factory);
    }

    @Test
    public void testConvertLanes() {
        Task task0_1 = mockTask("TASK0_1");
        Task task0_2 = mockTask("TASK0_2");
        Task task0_3 = mockTask("TASK0_3");

        Task task1_1 = mockTask("TASK1_1");
        Lane lane1 = mockLane("Lane1", "Lane1Name", task1_1);

        Task task2_1 = mockTask("TASK2_1");
        Lane lane2 = mockLane("Lane2", "Lane2Name", task2_1);
        LaneSet laneSet1 = mockLaneSet("LaneSet1", lane1, lane2);

        Task task3_2_1 = mockTask("TASK3_2_1");
        Task task3_2_2 = mockTask("TASK3_2_2");
        Lane lane3_2 = mockLane("Lane3_2", "Lane3_2Name", task3_2_1, task3_2_2);

        Task task3_1_2_1 = mockTask("TASK3_1_2_1");
        Lane lane3_1_2 = mockLane("Lane3_1_2", "Lane3_1_2Name", task3_1_2_1);

        Task task3_1_1_1_1 = mockTask("task3_1_1_1_1");
        Lane lane3_1_1_1 = mockLane("Lane3_1_1_1", "lane3_1_1_1Name", task3_1_1_1_1);

        Task task3_1_1_2_1 = mockTask("task3_1_1_2_1");
        Lane lane3_1_1_2 = mockLane("Lane3_1_1_2", "lane3_1_1_2Name", task3_1_1_2_1);

        Lane lane3_1_1 = mockLane("Lane3_1_1", "Lane3_1_1Name", mockLaneSet("laneSet3_1_1", lane3_1_1_1, lane3_1_1_2));

        Lane lane3_1 = mockLane("Lane3_1", "Lane3_1Name", mockLaneSet("laneSet3_1", lane3_1_1, lane3_1_2));

        Lane lane3 = mockLane("Lane3", "Lane3Name", mockLaneSet("LaneSet3", lane3_1, lane3_2));
        LaneSet laneSet2 = mockLaneSet("LaneSet2", lane3);

        List<FlowElement> flowElements = Arrays.asList(task0_1, task0_2, task0_3, task1_1, task2_1, task3_1_1_1_1,
                                                       task3_1_1_2_1, task3_1_2_1, task3_2_1, task3_2_2);
        List<LaneSet> laneSets = Arrays.asList(laneSet1, laneSet2);

        Map<String, BpmnNode> nodes = converterDelegate.convertChildNodes(parentNode, flowElements, laneSets);
        assertEquals(10, nodes.size());

        assertEquals(9, parentNode.getChildren().size());
        assertHasChildren(parentNode, task0_1.getId(), task0_2.getId(), task0_3.getId(), lane1.getId(), lane2.getId(),
                          lane3_2.getId(), lane3_1_2.getId(), lane3_1_1_1.getId(), lane3_1_1_2.getId());

        BpmnNode lane1Node = getChildById(parentNode, lane1.getId());
        assertNotNull(lane1Node);
        assertHasChildren(lane1Node, task1_1.getId());

        BpmnNode lane2Node = getChildById(parentNode, lane2.getId());
        assertNotNull(lane2Node);
        assertHasChildren(lane2Node, task2_1.getId());

        BpmnNode lane3_1_1_1Node = getChildById(parentNode, lane3_1_1_1.getId());
        assertNotNull(lane3_1_1_1Node);
        assertHasChildren(lane3_1_1_1Node, task3_1_1_1_1.getId());

        BpmnNode lane3_1_1_2Node = getChildById(parentNode, lane3_1_1_2.getId());
        assertNotNull(lane3_1_1_2Node);
        assertHasChildren(lane3_1_1_2Node, task3_1_1_2_1.getId());

        BpmnNode lane3_1_2Node = getChildById(parentNode, lane3_1_2.getId());
        assertNotNull(lane3_1_2Node);
        assertHasChildren(lane3_1_2Node, task3_1_2_1.getId());

        BpmnNode lane3_2Node = getChildById(parentNode, lane3_2.getId());
        assertHasChildren(lane3_2Node);
        assertHasChildren(lane3_2Node, task3_2_1.getId(), task3_2_2.getId());
    }

    private static void assertHasChildren(BpmnNode bpmnNode, String... nodeIds) {
        Arrays.stream(nodeIds).forEach(nodeId -> assertTrue(bpmnNode.getChildren().stream().anyMatch(child -> Objects.equals(child.value().getUUID(), nodeId))));
    }

    private static BpmnNode getChildById(BpmnNode bpmnNode, String nodeId) {
        return bpmnNode.getChildren().stream().filter(child -> Objects.equals(child.value().getUUID(), nodeId)).findFirst().orElse(null);
    }

    private static Task mockTask(String id) {
        Task task = Bpmn2Factory.eINSTANCE.createTask();
        task.setId(id);
        return task;
    }

    private static LaneSet mockLaneSet(String name, Lane... children) {
        LaneSet laneSet = mock(LaneSet.class);
        laneSet.setName(name);
        when(laneSet.getName()).thenReturn(name);
        when(laneSet.getLanes()).thenReturn(Arrays.asList(children));
        return laneSet;
    }

    private static Lane mockLane(String id, String name, FlowNode... children) {
        Lane lane = new LaneMock(Arrays.asList(children));
        lane.setId(id);
        lane.setName(name);
        return lane;
    }

    private static Lane mockLane(String id, String name, LaneSet childLineSet) {
        Lane lane = new LaneMock(childLineSet);
        lane.setId(id);
        lane.setName(name);
        return lane;
    }

    private static class LaneMock extends LaneImpl {

        List<FlowNode> children;
        LaneSet childLineSet;

        LaneMock(List<FlowNode> children) {
            this.children = children;
        }

        LaneMock(LaneSet childLineSet) {
            this.childLineSet = childLineSet;
        }

        @Override
        public List<FlowNode> getFlowNodeRefs() {
            return children;
        }

        @Override
        public LaneSet getChildLaneSet() {
            return childLineSet;
        }
    }
}
