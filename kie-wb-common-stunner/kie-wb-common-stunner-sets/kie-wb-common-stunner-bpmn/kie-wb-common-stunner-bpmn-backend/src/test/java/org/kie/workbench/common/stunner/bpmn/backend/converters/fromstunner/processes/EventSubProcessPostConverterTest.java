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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.bpmn2.SubProcess;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.properties.BasePropertyWriter;
import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.properties.ProcessPropertyWriter;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNViewDefinition;
import org.kie.workbench.common.stunner.bpmn.definition.EmbeddedSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.EndEscalationEvent;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateSignalEventThrowing;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateTimerEvent;
import org.kie.workbench.common.stunner.bpmn.definition.ScriptTask;
import org.kie.workbench.common.stunner.bpmn.definition.StartCompensationEvent;
import org.kie.workbench.common.stunner.bpmn.definition.UserTask;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.relationship.Child;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class EventSubProcessPostConverterTest {

    @Mock
    private ProcessPropertyWriter processWriter;

    @Mock
    private BasePropertyWriter nodeWriter;

    @Mock
    private Node<View<? extends BPMNViewDefinition>, Edge> eventSubprocessNode;

    @Mock
    private SubProcess subProcess;

    private List<Edge> outEdges;

    private EventSubProcessPostConverter converter;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() {
        when(nodeWriter.getElement()).thenReturn(subProcess);
        outEdges = new ArrayList<>();
        outEdges.add(mockEdge(mock(Node.class), mockNode(new IntermediateTimerEvent())));
        outEdges.add(mockEdge(mock(Node.class), mockNode(new UserTask())));
        outEdges.add(mockEdge(mock(Node.class), mockNode(new ScriptTask())));
        outEdges.add(mockEdge(mock(Node.class), mockNode(new IntermediateSignalEventThrowing())));
        outEdges.add(mockEdge(mock(Node.class), mockNode(new EmbeddedSubprocess())));
        outEdges.add(mockEdge(mock(Node.class), mockNode(new EndEscalationEvent())));

        when(eventSubprocessNode.getOutEdges()).thenReturn(outEdges);

        converter = new EventSubProcessPostConverter();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testProcessWhenIsForCompensation() {
        outEdges.add(mockEdge(mock(Node.class), mockNode(new StartCompensationEvent())));
        converter.process(processWriter, nodeWriter, eventSubprocessNode);
        verify(subProcess).setIsForCompensation(true);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testProcessWhenIsNotForCompensation() {
        converter.process(processWriter, nodeWriter, eventSubprocessNode);
        verify(subProcess, never()).setIsForCompensation(true);
    }

    private <T extends BPMNViewDefinition> Edge mockEdge(Node<View<T>, ?> sourceNode, Node<View<T>, ?> targetNode) {
        Edge edge = mock(Edge.class);
        when(edge.getContent()).thenReturn(mock(Child.class));
        when(edge.getSourceNode()).thenReturn(sourceNode);
        when(edge.getTargetNode()).thenReturn(targetNode);
        return edge;
    }

    @SuppressWarnings("unchecked")
    private <T extends BPMNViewDefinition> Node<View<T>, ?> mockNode(T definition) {
        Node<View<T>, ?> node = mock(Node.class);
        View<T> view = mock(View.class);
        when(node.getContent()).thenReturn(view);
        when(view.getDefinition()).thenReturn(definition);
        return node;
    }
}
