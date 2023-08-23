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

package org.kie.workbench.common.dmn.client.docks.navigator.factories;

import java.util.ArrayList;
import java.util.List;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.model.DMNDiagram;
import org.kie.workbench.common.dmn.api.definition.model.DMNDiagramElement;
import org.kie.workbench.common.dmn.api.definition.model.DecisionService;
import org.kie.workbench.common.dmn.api.definition.model.Definitions;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.client.docks.navigator.DecisionNavigatorItem;
import org.kie.workbench.common.dmn.client.docks.navigator.drds.DMNDiagramSelected;
import org.kie.workbench.common.dmn.client.docks.navigator.drds.DMNDiagramTuple;
import org.kie.workbench.common.dmn.client.docks.navigator.drds.DMNDiagramsSession;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.uberfire.mocks.EventSourceMock;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.kie.workbench.common.dmn.client.docks.navigator.DecisionNavigatorItem.Type.DECISION_SERVICE;
import static org.kie.workbench.common.dmn.client.docks.navigator.DecisionNavigatorItem.Type.ITEM;
import static org.kie.workbench.common.dmn.client.docks.navigator.DecisionNavigatorItem.Type.ROOT;
import static org.kie.workbench.common.dmn.client.docks.navigator.DecisionNavigatorItem.Type.SEPARATOR;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class DecisionNavigatorItemFactoryTest {

    @Mock
    private DecisionNavigatorBaseItemFactory baseItemFactory;

    @Mock
    private EventSourceMock<DMNDiagramSelected> selectedEvent;

    @Mock
    private DMNDiagramsSession dmnDiagramsSession;

    @Mock
    private Node<View, Edge> node;

    @Mock
    private View content;

    @Mock
    private DecisionNavigatorItem item;

    @Captor
    private ArgumentCaptor<DMNDiagramSelected> diagramSelectedArgumentCaptor;

    private DecisionNavigatorItemFactory factory;

    @Before
    public void setup() {
        factory = spy(new DecisionNavigatorItemFactory(baseItemFactory, selectedEvent, dmnDiagramsSession));
    }

    @Test
    public void testMakeRootWhenDMNDiagramElementIsDRG() {

        final Diagram stunnerDiagram = mock(Diagram.class);
        final Graph graph = mock(Graph.class);
        final Node node = mock(Node.class);
        final Definition definition = mock(Definition.class);
        final String dmnDiagramId = "0000";
        final String dmnModelName = "diagram-name";
        final DMNDiagramElement dmnDiagramElement = new DMNDiagramElement(new Id(dmnDiagramId), new Name("DRG"));
        final DMNDiagramTuple diagramTuple = new DMNDiagramTuple(stunnerDiagram, dmnDiagramElement);

        when(stunnerDiagram.getGraph()).thenReturn(graph);
        when(graph.nodes()).thenReturn(singletonList(node));
        when(node.getContent()).thenReturn(definition);
        when(definition.getDefinition()).thenReturn(new DMNDiagram());
        when(baseItemFactory.getLabel(node)).thenReturn(dmnModelName);

        final DecisionNavigatorItem decisionNavigatorItem = factory.makeRoot(diagramTuple);

        assertEquals(dmnModelName, decisionNavigatorItem.getLabel());
        assertEquals(dmnDiagramId, decisionNavigatorItem.getUUID());
        assertEquals(ROOT, decisionNavigatorItem.getType());
        assertNull(decisionNavigatorItem.getParentUUID());
    }

    @Test
    public void testMakeRootWhenDMNDiagramElementIsNotDRG() {

        final Diagram stunnerDiagram = mock(Diagram.class);
        final Graph graph = mock(Graph.class);
        final Node node = mock(Node.class);
        final Definition definition = mock(Definition.class);
        final String drdName = "DRD 1";
        final String dmnDiagramId = "0000";
        final DMNDiagramElement dmnDiagramElement = new DMNDiagramElement(new Id(dmnDiagramId), new Name(drdName));
        final DMNDiagramTuple diagramTuple = new DMNDiagramTuple(stunnerDiagram, dmnDiagramElement);

        when(stunnerDiagram.getGraph()).thenReturn(graph);
        when(graph.nodes()).thenReturn(singletonList(node));
        when(node.getContent()).thenReturn(definition);
        when(definition.getDefinition()).thenReturn(new DMNDiagram());
        when(baseItemFactory.getLabel(node)).thenReturn("diagram-name");

        final DecisionNavigatorItem decisionNavigatorItem = factory.makeRoot(diagramTuple);

        assertEquals(drdName, decisionNavigatorItem.getLabel());
        assertEquals(dmnDiagramId, decisionNavigatorItem.getUUID());
        assertEquals(ROOT, decisionNavigatorItem.getType());
        assertNull(decisionNavigatorItem.getParentUUID());
    }

    @Test
    public void testMakeSeparator() {
        final String drds = "DRDs";
        final DecisionNavigatorItem drdSeparator = factory.makeSeparator(drds);

        assertEquals(drds, drdSeparator.getLabel());
        assertEquals(SEPARATOR, drdSeparator.getType());
        assertNotNull(drdSeparator.getUUID());
        assertNull(drdSeparator.getParentUUID());
    }

    @Test
    public void testGetOnClickAction() {
        final DMNDiagramElement dmnDiagramElement = mock(DMNDiagramElement.class);

        factory.getOnClickAction(dmnDiagramElement).execute();

        verify(selectedEvent).fire(diagramSelectedArgumentCaptor.capture());
        assertEquals(dmnDiagramElement, diagramSelectedArgumentCaptor.getValue().getDiagramElement());
    }

    @Test
    public void testGetOnUpdate() {
        final DMNDiagramElement dmnDiagramElement = mock(DMNDiagramElement.class);
        final DecisionNavigatorItem item = mock(DecisionNavigatorItem.class);
        final Name dmnDiagramElementName = new Name();
        final String newDiagramName = "New diagram name";

        when(item.getLabel()).thenReturn(newDiagramName);
        when(dmnDiagramElement.getName()).thenReturn(dmnDiagramElementName);

        factory.getOnUpdate(dmnDiagramElement).accept(item);

        verify(selectedEvent).fire(diagramSelectedArgumentCaptor.capture());

        final DMNDiagramElement actualDiagram = diagramSelectedArgumentCaptor.getValue().getDiagramElement();

        assertEquals(dmnDiagramElement, actualDiagram);
        assertEquals(newDiagramName, actualDiagram.getName().getValue());
    }

    @Test
    public void testGetOnRemove() {

        final DMNDiagramElement drgDiagramElement = new DMNDiagramElement();
        final DMNDiagramElement drd1DiagramElement = new DMNDiagramElement();
        final DMNDiagramElement drd2DiagramElement = new DMNDiagramElement();
        final DMNDiagramElement drgElement = mock(DMNDiagramElement.class);
        final Graph drgGraph = mock(Graph.class);
        final Diagram drgDiagram = mock(Diagram.class);
        final DecisionNavigatorItem item = mock(DecisionNavigatorItem.class);
        final Node node = mock(Node.class);
        final Definition definition = mock(Definition.class);
        final DMNDiagram dmnDiagram = mock(DMNDiagram.class);
        final Definitions dmnDefinitions = mock(Definitions.class);
        final List<DMNDiagramElement> diagramElements = new ArrayList<>(asList(drgDiagramElement, drd1DiagramElement, drd2DiagramElement));
        final Iterable nodes = singletonList(node);

        when(dmnDiagramsSession.getDRGDiagramElement()).thenReturn(drgElement);
        when(dmnDiagramsSession.getDRGDiagram()).thenReturn(drgDiagram);
        when(drgDiagram.getGraph()).thenReturn(drgGraph);
        when(drgGraph.nodes()).thenReturn(nodes);
        when(node.getContent()).thenReturn(definition);
        when(definition.getDefinition()).thenReturn(dmnDiagram);
        when(dmnDiagram.getDefinitions()).thenReturn(dmnDefinitions);
        when(dmnDefinitions.getDiagramElements()).thenReturn(diagramElements);

        factory.getOnRemove(drd1DiagramElement).accept(item);

        verify(dmnDiagramsSession).remove(drd1DiagramElement);
        verify(selectedEvent).fire(diagramSelectedArgumentCaptor.capture());
        assertEquals(drgElement, diagramSelectedArgumentCaptor.getValue().getDiagramElement());
        assertEquals(2, diagramElements.size());
        assertTrue(diagramElements.contains(drgDiagramElement));
        assertFalse(diagramElements.contains(drd1DiagramElement));
        assertTrue(diagramElements.contains(drd2DiagramElement));
    }

    @Test
    public void testMakeItem() {
        when(baseItemFactory.makeItem(node, ITEM)).thenReturn(item);
        assertEquals(item, factory.makeItem(node));
    }

    @Test
    public void testMakeItemWhenTypeIsDecisionService() {
        when(baseItemFactory.makeItem(node, DECISION_SERVICE)).thenReturn(item);
        when(node.getContent()).thenReturn(content);
        when(content.getDefinition()).thenReturn(new DecisionService());
        assertEquals(item, factory.makeItem(node));
    }

    @Test
    public void testMakeItemWhenTypeIsNotMapped() {
        when(baseItemFactory.makeItem(node, ITEM)).thenReturn(item);
        when(node.getContent()).thenReturn(content);
        when(content.getDefinition()).thenReturn(new Object());
        assertEquals(item, factory.makeItem(node));
    }
}
