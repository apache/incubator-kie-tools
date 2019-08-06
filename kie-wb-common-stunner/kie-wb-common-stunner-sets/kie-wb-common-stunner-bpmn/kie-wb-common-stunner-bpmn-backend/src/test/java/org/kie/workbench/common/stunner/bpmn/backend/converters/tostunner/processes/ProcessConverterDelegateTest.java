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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.eclipse.bpmn2.BaseElement;
import org.eclipse.bpmn2.Bpmn2Factory;
import org.eclipse.bpmn2.DataObject;
import org.eclipse.bpmn2.Definitions;
import org.eclipse.bpmn2.FlowElement;
import org.eclipse.bpmn2.FlowNode;
import org.eclipse.bpmn2.Lane;
import org.eclipse.bpmn2.LaneSet;
import org.eclipse.bpmn2.ManualTask;
import org.eclipse.bpmn2.RootElement;
import org.eclipse.bpmn2.SequenceFlow;
import org.eclipse.bpmn2.Task;
import org.eclipse.bpmn2.di.BPMNDiagram;
import org.eclipse.bpmn2.di.BPMNEdge;
import org.eclipse.bpmn2.di.BPMNPlane;
import org.eclipse.bpmn2.di.BPMNShape;
import org.eclipse.bpmn2.impl.LaneImpl;
import org.eclipse.dd.dc.Bounds;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.soup.commons.util.Maps;
import org.kie.workbench.common.stunner.bpmn.BPMNDefinitionSet;
import org.kie.workbench.common.stunner.bpmn.backend.BPMNTestDefinitionFactory;
import org.kie.workbench.common.stunner.bpmn.backend.converters.Result;
import org.kie.workbench.common.stunner.bpmn.backend.converters.TypedFactoryManager;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.BaseConverterFactory;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.BpmnNode;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.ConverterFactory;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.DefinitionResolver;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.properties.BasePropertyReader;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.properties.PropertyReaderFactory;
import org.kie.workbench.common.stunner.core.backend.StunnerTestingGraphBackendAPI;
import org.kie.workbench.common.stunner.core.graph.impl.NodeImpl;
import org.kie.workbench.common.stunner.core.marshaller.MarshallingMessage;
import org.kie.workbench.common.stunner.core.marshaller.MarshallingMessageKeys;
import org.kie.workbench.common.stunner.core.validation.Violation;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
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
        when(plane.getPlaneElement()).thenReturn(new ArrayList<>());
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
    public void testConvertEdges() {
        Task task1 = mockTask("1");
        Task task2 = mockTask("2");
        BpmnNode task1Node = mockTaskNode(task1);
        BpmnNode task2Node = mockTaskNode(task2);
        SequenceFlow sequenceFlow = mockSequenceFlow("seq1", task1, task2);
        List<BaseElement> elements = Arrays.asList(sequenceFlow, task1, task2);

        //ignored because there the tasks are not on the nodes map
        assertFalse(converterDelegate.convertEdges(parentNode, elements, new HashMap<>()).value());

        //convert with all nodes
        Map<String, BpmnNode> nodes = new Maps.Builder<String, BpmnNode>().put(task1.getId(), task1Node).put(task2.getId(), task2Node).build();
        assertTrue(converterDelegate.convertEdges(parentNode, elements, nodes).value());
    }

    private BpmnNode.Simple mockTaskNode(Task task) {
        BPMNShape shape = mock(BPMNShape.class);
        when(shape.getBounds()).thenReturn(mock(Bounds.class));
        plane.getPlaneElement().add(shape);
        when(shape.getBpmnElement()).thenReturn(task);
        return new BpmnNode.Simple(new NodeImpl<>(task.getId()), basePropertyReader);
    }

    private SequenceFlow mockSequenceFlow(String id, FlowNode source, FlowNode target) {
        SequenceFlow sequenceFlow = Bpmn2Factory.eINSTANCE.createSequenceFlow();
        sequenceFlow.setSourceRef(source);
        sequenceFlow.setTargetRef(target);
        sequenceFlow.setId(id);

        BPMNEdge shape = mock(BPMNEdge.class);
        when(shape.getWaypoint()).thenReturn(Collections.emptyList());
        plane.getPlaneElement().add(shape);
        when(shape.getBpmnElement()).thenReturn(sequenceFlow);
        return sequenceFlow;
    }

    @Test
    public void testConvertEdgesIgnoredNonEdgeElement() {
        Map<String, BpmnNode> nodes = new HashMap<>();
        List<BaseElement> elements = Arrays.asList(mockTask("1"));
        assertFalse(converterDelegate.convertEdges(parentNode, elements, nodes).value());
    }

    @Test
    public void testConvertUnsupportedChildNodes() {
        List<LaneSet> laneSets = Collections.emptyList();
        //adding 2 supported and 2 unsupported elements
        List<FlowElement> flowElements = Arrays.asList(mockTask("1"),
                                                       mockTask("2"),
                                                       mockUnsupportedTask("3"),
                                                       mockUnsupportedDataObject("4"));

        Result<Map<String, BpmnNode>> result = converterDelegate.convertChildNodes(parentNode, flowElements, laneSets);
        List<MarshallingMessage> messages = result.messages();
        //check one message per unsupported element
        assertEquals(2, messages.size());
        assertTrue(messages.stream().map(MarshallingMessage::getViolationType).allMatch(Violation.Type.WARNING::equals));
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

        Result<Map<String, BpmnNode>> result = converterDelegate.convertChildNodes(parentNode, flowElements, laneSets);
        Map<String, BpmnNode> nodes = result.value();
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

        //assert messages for converted lane sets
        List<MarshallingMessage> messages = result.messages();
        assertEquals(4, messages.size());
        assertTrue(messages.stream()
                           .map(MarshallingMessage::getMessageKey)
                           .allMatch(MarshallingMessageKeys.childLaneSetConverted::equals));
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

    private static Task mockUnsupportedTask(String id) {
        ManualTask task = Bpmn2Factory.eINSTANCE.createManualTask();
        task.setId(id);
        return task;
    }

    private static DataObject mockUnsupportedDataObject(String id) {
        DataObject element = Bpmn2Factory.eINSTANCE.createDataObject();
        element.setId(id);
        return element;
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
