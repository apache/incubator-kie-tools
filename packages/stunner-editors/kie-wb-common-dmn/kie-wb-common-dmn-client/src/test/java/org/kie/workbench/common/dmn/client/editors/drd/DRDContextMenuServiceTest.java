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

package org.kie.workbench.common.dmn.client.editors.drd;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.enterprise.event.Event;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.model.DMNDiagram;
import org.kie.workbench.common.dmn.api.definition.model.DMNDiagramElement;
import org.kie.workbench.common.dmn.api.definition.model.Definitions;
import org.kie.workbench.common.dmn.api.definition.model.InputData;
import org.kie.workbench.common.dmn.api.graph.DMNDiagramUtils;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.client.commands.clone.DMNDeepCloneProcess;
import org.kie.workbench.common.dmn.client.docks.navigator.drds.DMNDiagramSelected;
import org.kie.workbench.common.dmn.client.docks.navigator.drds.DMNDiagramTuple;
import org.kie.workbench.common.dmn.client.docks.navigator.drds.DMNDiagramsSession;
import org.kie.workbench.common.dmn.client.marshaller.unmarshall.DMNUnmarshaller;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.Bound;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import static java.util.Collections.singleton;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DRDContextMenuServiceTest {

    private DRDContextMenuService drdContextMenuService;

    @Mock
    private DMNDiagramsSession dmnDiagramsSession;

    @Mock
    private FactoryManager factoryManager;

    @Mock
    private Event<DMNDiagramSelected> selectedEvent;

    @Mock
    private DMNDiagramUtils dmnDiagramUtils;

    @Mock
    private DMNDeepCloneProcess dmnDeepCloneProcess;

    @Mock
    private Graph graph;

    @Mock
    private DMNUnmarshaller dmnUnmarshaller;

    @Before
    public void setUp() {
        drdContextMenuService = new DRDContextMenuService(dmnDiagramsSession,
                                                          factoryManager,
                                                          selectedEvent,
                                                          dmnDiagramUtils,
                                                          dmnDeepCloneProcess,
                                                          dmnUnmarshaller);
    }

    @Test
    public void testGetDiagrams() {
        final DMNDiagramTuple dmnDiagramTuple = mock(DMNDiagramTuple.class);
        when(dmnDiagramsSession.getDMNDiagrams()).thenReturn(Collections.singletonList(dmnDiagramTuple));

        final List<DMNDiagramTuple> diagrams = drdContextMenuService.getDiagrams();
        assertThat(diagrams).isNotEmpty();
        assertThat(diagrams).hasSize(1);
        assertThat(diagrams).contains(dmnDiagramTuple);
    }

    @Test
    public void testAddToNewDRD() {
        final Collection<Node<? extends Definition<?>, Edge>> nodes = mock(Collection.class);
        final Definitions definitions = mock(Definitions.class);
        final Diagram drgDiagram = mock(Diagram.class);
        final List<DMNDiagramElement> diagramElements = mock(List.class);
        when(dmnDiagramsSession.getDRGDiagram()).thenReturn(drgDiagram);
        when(dmnDiagramUtils.getDefinitions(drgDiagram)).thenReturn(definitions);
        when(definitions.getDiagramElements()).thenReturn(diagramElements);

        drdContextMenuService.addToNewDRD(nodes);

        verify(diagramElements, times(1)).add(Mockito.<DMNDiagramElement>any());
        verify(dmnDiagramsSession, times(1)).add(Mockito.<DMNDiagramElement>any(), Mockito.<Diagram>any());
        verify(selectedEvent, times(1)).fire(Mockito.<DMNDiagramSelected>any());
    }

    @Test
    public void testAddToExistingDRD() {

        final DMNDiagramTuple diagramTuple = mockDmnDiagramTuple();
        final Diagram diagram = mock(Diagram.class);
        final Node graphNode = mock(Node.class);
        final View graphNodeDefinition = mock(View.class);
        final DMNDiagram dmnDiagram = spy(new DMNDiagram());
        final Collection<Node<? extends Definition<?>, Edge>> selectedNodes = mockNodes();
        selectedNodes.add(graphNode);

        when(diagramTuple.getStunnerDiagram()).thenReturn(diagram);
        when(diagram.getGraph()).thenReturn(graph);
        when(graph.nodes()).thenReturn(selectedNodes);
        when(graphNode.getContent()).thenReturn(graphNodeDefinition);
        when(graphNodeDefinition.getDefinition()).thenReturn(dmnDiagram);

        drdContextMenuService.addToExistingDRD(diagramTuple, selectedNodes);

        verify(graph).addNode(Mockito.<Node>any());
        verify(selectedEvent, times(1)).fire(Mockito.<DMNDiagramSelected>any());
    }

    @Test
    public void testRemoveFromCurrentDRD() {
        final String nodeUUID = "UUID";
        final Node node = mock(Node.class);
        final Diagram diagram = mock(Diagram.class);
        final DMNDiagramElement dmnDiagram = mock(DMNDiagramElement.class);
        when(node.getUUID()).thenReturn(nodeUUID);
        when(dmnDiagramsSession.getCurrentDiagram()).thenReturn(Optional.of(diagram));
        when(dmnDiagramsSession.getCurrentDMNDiagramElement()).thenReturn(Optional.of(dmnDiagram));
        when(diagram.getGraph()).thenReturn(graph);

        drdContextMenuService.removeFromCurrentDRD(singleton(node));

        verify(graph, times(1)).removeNode(nodeUUID);
        verify(selectedEvent, times(1)).fire(Mockito.<DMNDiagramSelected>any());
    }

    private Collection<Node<? extends Definition<?>, Edge>> mockNodes() {

        final Node node = mock(Node.class);
        final Node clonedNode = mock(Node.class);
        final View content = mock(View.class);
        final View clonedContent = mock(View.class);
        final Bounds bounds = mock(Bounds.class);
        final Bound upperLeft = mock(Bound.class);
        final Bound lowerRight = mock(Bound.class);
        final InputData inputData = spy(new InputData());
        final InputData clonedInputData = spy(new InputData());
        final Collection<Node<? extends Definition<?>, Edge>> nodes = new ArrayList<>();

        when(node.getContent()).thenReturn(content);
        when(content.getDefinition()).thenReturn(inputData);
        when(content.getBounds()).thenReturn(bounds);
        when(bounds.getUpperLeft()).thenReturn(upperLeft);
        when(bounds.getLowerRight()).thenReturn(lowerRight);
        when(factoryManager.newElement(Mockito.<String>any(), Mockito.<String>any())).thenReturn(clonedNode);
        when(clonedNode.asNode()).thenReturn(clonedNode);
        when(clonedNode.getContent()).thenReturn(clonedContent);
        when(clonedContent.getDefinition()).thenReturn(clonedInputData);
        when(dmnDeepCloneProcess.clone(eq(inputData))).thenReturn(clonedInputData);

        nodes.add(node);
        return nodes;
    }

    private DMNDiagramTuple mockDmnDiagramTuple() {
        final DMNDiagramTuple dmnDiagramTuple = mock(DMNDiagramTuple.class);
        final Diagram diagram = mock(Diagram.class);
        final DMNDiagramElement dmnDiagram = mock(DMNDiagramElement.class);
        final Id diagramId = new Id("DIAGRAM_ID");
        when(dmnDiagramTuple.getStunnerDiagram()).thenReturn(diagram);
        when(dmnDiagramTuple.getDMNDiagram()).thenReturn(dmnDiagram);
        when(dmnDiagram.getId()).thenReturn(diagramId);
        return dmnDiagramTuple;
    }
}
