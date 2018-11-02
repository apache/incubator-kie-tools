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

package org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.processes;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.ConverterFactory;
import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.DefinitionsBuildingContext;
import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.FlowElementPostConverter;
import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.properties.ProcessPropertyWriter;
import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.properties.PropertyWriterFactory;
import org.kie.workbench.common.stunner.bpmn.definition.AdHocSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNDiagramImpl;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNViewDefinition;
import org.kie.workbench.common.stunner.bpmn.definition.BusinessRuleTask;
import org.kie.workbench.common.stunner.bpmn.definition.EmbeddedSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.EndCompensationEvent;
import org.kie.workbench.common.stunner.bpmn.definition.EndNoneEvent;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateErrorEventCatching;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateSignalEventThrowing;
import org.kie.workbench.common.stunner.bpmn.definition.ScriptTask;
import org.kie.workbench.common.stunner.bpmn.definition.StartNoneEvent;
import org.kie.workbench.common.stunner.bpmn.definition.UserTask;
import org.kie.workbench.common.stunner.core.TestingGraphInstanceBuilder2;
import org.kie.workbench.common.stunner.core.TestingGraphMockHandler;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.relationship.Dock;
import org.kie.workbench.common.stunner.core.graph.content.view.BoundsImpl;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewImpl;
import org.kie.workbench.common.stunner.core.graph.impl.EdgeImpl;
import org.kie.workbench.common.stunner.core.graph.impl.GraphImpl;
import org.kie.workbench.common.stunner.core.graph.impl.NodeImpl;
import org.kie.workbench.common.stunner.core.graph.store.GraphNodeStoreImpl;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.runners.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.Factories.bpmn2;
import static org.kie.workbench.common.stunner.core.TestingGraphInstanceBuilder2.NODES.LEVEL0_END_NODE;
import static org.kie.workbench.common.stunner.core.TestingGraphInstanceBuilder2.NODES.LEVEL0_NODE1;
import static org.kie.workbench.common.stunner.core.TestingGraphInstanceBuilder2.NODES.LEVEL0_NODE2;
import static org.kie.workbench.common.stunner.core.TestingGraphInstanceBuilder2.NODES.LEVEL0_START_NODE;
import static org.kie.workbench.common.stunner.core.TestingGraphInstanceBuilder2.NODES.LEVEL1_NODE1;
import static org.kie.workbench.common.stunner.core.TestingGraphInstanceBuilder2.NODES.LEVEL1_NODE2;
import static org.kie.workbench.common.stunner.core.TestingGraphInstanceBuilder2.NODES.LEVEL1_SUB_PROCESS1;
import static org.kie.workbench.common.stunner.core.TestingGraphInstanceBuilder2.NODES.LEVEL2_NODE1;
import static org.kie.workbench.common.stunner.core.TestingGraphInstanceBuilder2.NODES.LEVEL2_NODE2;
import static org.kie.workbench.common.stunner.core.TestingGraphInstanceBuilder2.NODES.LEVEL2_SUB_PROCESS1;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ProcessConverterDelegateTest {

    @Captor
    private ArgumentCaptor<Node<View<? extends BPMNViewDefinition>, ?>> nodeCaptor;

    @Test
    public void convertDockedEdges() {
        GraphNodeStoreImpl nodes = new GraphNodeStoreImpl();
        GraphImpl g = new GraphImpl("g", nodes);

        NodeImpl<Object> root = new NodeImpl<>("root");
        BPMNDiagramImpl bpmnDiagram = new BPMNDiagramImpl();
        root.setContent(new ViewImpl<>(bpmnDiagram, BoundsImpl.build()));
        g.addNode(root);

        NodeImpl<Object> n = new NodeImpl<>("n");
        EmbeddedSubprocess subProcessNode = new EmbeddedSubprocess();
        n.setContent(new ViewImpl<>(subProcessNode, BoundsImpl.build()));
        g.addNode(n);

        NodeImpl<Object> e = new NodeImpl<>("e");
        IntermediateErrorEventCatching intermediateErrorEventCatching = new IntermediateErrorEventCatching();
        e.setContent(new ViewImpl<>(intermediateErrorEventCatching, BoundsImpl.build()));
        g.addNode(e);

        EdgeImpl<Object> edge = new EdgeImpl<>("edge");
        Dock dock = new Dock();
        edge.setContent(dock);

        n.getOutEdges().add(edge);
        edge.setSourceNode(n);
        e.getInEdges().add(edge);
        edge.setTargetNode(e);

        DefinitionsBuildingContext ctx = new DefinitionsBuildingContext(g);
        PropertyWriterFactory pwFactory = new PropertyWriterFactory();
        ConverterFactory factory = new ConverterFactory(ctx, pwFactory);

        MyProcessConverter abstractProcessConverter =
                new MyProcessConverter(factory);

        ProcessPropertyWriter p = pwFactory.of(bpmn2.createProcess());

        assertThatCode(() -> {
            abstractProcessConverter.convertChildNodes(p, ctx);
            abstractProcessConverter.convertEdges(p, ctx);
        }).doesNotThrowAnyException();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testPostConvertNodes() {
        TestingGraphMockHandler graphTestHandler = new TestingGraphMockHandler();
        BPMNDiagramImpl bpmnDiagram = new BPMNDiagramImpl();
        StartNoneEvent level0StartNode = new StartNoneEvent();
        EndNoneEvent level0EndNode = new EndNoneEvent();
        UserTask level0Node1 = new UserTask();
        UserTask level0Node2 = new UserTask();
        EmbeddedSubprocess level1SubProcess1 = new EmbeddedSubprocess();
        ScriptTask level1Node1 = new ScriptTask();
        IntermediateSignalEventThrowing level1Node2 = new IntermediateSignalEventThrowing();
        AdHocSubprocess level2SubProcess1 = new AdHocSubprocess();
        BusinessRuleTask level2Node1 = new BusinessRuleTask();
        EndCompensationEvent level2Node2 = new EndCompensationEvent();

        TestingGraphInstanceBuilder2.Level2Graph level2Graph = TestingGraphInstanceBuilder2.buildLevel2Graph(graphTestHandler,
                                                                                                             bpmnDiagram,
                                                                                                             level0StartNode,
                                                                                                             level0Node1,
                                                                                                             level0Node2,
                                                                                                             level0EndNode,
                                                                                                             level1SubProcess1,
                                                                                                             level1Node1,
                                                                                                             level1Node2,
                                                                                                             level2SubProcess1,
                                                                                                             level2Node1,
                                                                                                             level2Node2);
        DefinitionsBuildingContext ctx = new DefinitionsBuildingContext(level2Graph.graph);
        PropertyWriterFactory writerFactory = new PropertyWriterFactory();
        ConverterFactory factory = spy(new ConverterFactory(ctx, writerFactory));
        FlowElementPostConverter flowElementPostConverter = mock(FlowElementPostConverter.class);
        when(factory.flowElementPostConverter()).thenReturn(flowElementPostConverter);
        MyProcessConverter abstractProcessConverter = new MyProcessConverter(factory);
        ProcessPropertyWriter processWriter = writerFactory.of(bpmn2.createProcess());

        abstractProcessConverter.postConvertChildNodes(processWriter, ctx);

        verify(flowElementPostConverter, times(10)).postConvert(anyObject(), anyObject(), nodeCaptor.capture());
        Map<String, BPMNViewDefinition> nodes = new HashMap<>();
        nodes.put(LEVEL0_START_NODE.uuid(), level0StartNode);
        nodes.put(LEVEL0_NODE1.uuid(), level0Node1);
        nodes.put(LEVEL0_NODE2.uuid(), level0Node2);
        nodes.put(LEVEL0_END_NODE.uuid(), level0EndNode);
        nodes.put(LEVEL1_SUB_PROCESS1.uuid(), level1SubProcess1);
        nodes.put(LEVEL1_NODE1.uuid(), level1Node1);
        nodes.put(LEVEL1_NODE2.uuid(), level1Node2);
        nodes.put(LEVEL2_SUB_PROCESS1.uuid(), level2SubProcess1);
        nodes.put(LEVEL2_NODE1.uuid(), level2Node1);
        nodes.put(LEVEL2_NODE2.uuid(), level2Node2);
        assertEquals(nodes.size(), nodeCaptor.getAllValues().size());

        nodes.entrySet().forEach(entry -> {
            Optional<Node<View<? extends BPMNViewDefinition>, ?>> processed =
            nodeCaptor.getAllValues()
                    .stream()
                    .filter(captured -> entry.getKey().equals(captured.getUUID()))
                    .findFirst();
            assertTrue("Node: " + entry.getKey() + " was not present in result", processed.isPresent());
            assertEquals(entry.getValue(), processed.get().getContent().getDefinition());
        });
    }

    static class MyProcessConverter extends ProcessConverterDelegate {

        public MyProcessConverter(ConverterFactory converterFactory) {
            super(converterFactory);
        }
    }
}