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

package org.kie.workbench.common.dmn.client.docks.navigator;

import java.util.List;
import java.util.stream.Stream;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.model.BusinessKnowledgeModel;
import org.kie.workbench.common.dmn.api.definition.model.DMNDiagramElement;
import org.kie.workbench.common.dmn.api.definition.model.Decision;
import org.kie.workbench.common.dmn.api.definition.model.InputData;
import org.kie.workbench.common.dmn.api.definition.model.KnowledgeSource;
import org.kie.workbench.common.dmn.api.definition.model.TextAnnotation;
import org.kie.workbench.common.dmn.api.graph.DMNDiagramUtils;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.client.docks.navigator.drds.DMNDiagramTuple;
import org.kie.workbench.common.dmn.client.docks.navigator.drds.DMNDiagramsSession;
import org.kie.workbench.common.dmn.client.docks.navigator.factories.DecisionNavigatorItemFactory;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.mockito.Mock;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.kie.workbench.common.dmn.client.docks.navigator.DecisionNavigatorItemsProvider.DRDs;
import static org.kie.workbench.common.dmn.client.docks.navigator.DecisionNavigatorItemsProvider.DRG;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class DecisionNavigatorItemsProviderTest {

    @Mock
    private DecisionNavigatorItemFactory itemFactory;

    @Mock
    private DMNDiagramsSession dmnDiagramsSession;

    @Mock
    private DMNDiagramUtils dmnDiagramUtils;

    private DecisionNavigatorItemsProvider itemsProvider;

    @Before
    public void setup() {
        itemsProvider = new DecisionNavigatorItemsProvider(itemFactory, dmnDiagramsSession, dmnDiagramUtils);
    }

    @Test
    public void testGetItems() {

        final Diagram stunnerDiagram1 = mock(Diagram.class);
        final Diagram stunnerDiagram2 = mock(Diagram.class);
        final DMNDiagramElement dmnDiagramElement1 = mock(DMNDiagramElement.class);
        final DMNDiagramElement dmnDiagramElement2 = mock(DMNDiagramElement.class);
        final DMNDiagramTuple dmnDiagramTuple1 = new DMNDiagramTuple(stunnerDiagram1, dmnDiagramElement1);
        final DMNDiagramTuple dmnDiagramTuple2 = new DMNDiagramTuple(stunnerDiagram2, dmnDiagramElement2);
        final DecisionNavigatorItem decisionNavigatorItem1 = mock(DecisionNavigatorItem.class);
        final DecisionNavigatorItem decisionNavigatorItem2 = mock(DecisionNavigatorItem.class);
        final DecisionNavigatorItem decisionNavigatorItem3 = mock(DecisionNavigatorItem.class);
        final DecisionNavigatorItem decisionNavigatorItem4 = mock(DecisionNavigatorItem.class);
        final DecisionNavigatorItem decisionNavigatorItem5 = mock(DecisionNavigatorItem.class);
        final DecisionNavigatorItem decisionNavigatorItem6 = mock(DecisionNavigatorItem.class);
        final DecisionNavigatorItem decisionNavigatorItem7 = mock(DecisionNavigatorItem.class);
        final DecisionNavigatorItem decisionNavigatorItem8 = mock(DecisionNavigatorItem.class);
        final DecisionNavigatorItem decisionNavigatorItem9 = mock(DecisionNavigatorItem.class);
        final DecisionNavigatorItem decisionNavigatorItem10 = mock(DecisionNavigatorItem.class);
        final List<DMNDiagramTuple> diagramTuples = asList(dmnDiagramTuple1, dmnDiagramTuple2);
        final Node node1 = mockNode(new Decision());
        final Node node2 = mockNode(new InputData());
        final Node node3 = mockNode(new TextAnnotation());
        final Node node4 = mockNode(new BusinessKnowledgeModel());
        final Node node5 = mockNode(new KnowledgeSource());
        final Node node6 = mockNode(new TextAnnotation());
        final Stream<Node> stunnerNodes1 = Stream.of(node1, node2, node3);
        final Stream<Node> stunnerNodes2 = Stream.of(node4, node5, node6);

        when(dmnDiagramElement1.getName()).thenReturn(new Name("DRD"));
        when(dmnDiagramElement2.getName()).thenReturn(new Name("DRG"));
        when(dmnDiagramsSession.getDMNDiagrams()).thenReturn(diagramTuples);
        when(itemFactory.makeRoot(dmnDiagramTuple1)).thenReturn(decisionNavigatorItem1);
        when(itemFactory.makeRoot(dmnDiagramTuple2)).thenReturn(decisionNavigatorItem2);
        when(itemFactory.makeItem(node1)).thenReturn(decisionNavigatorItem5);
        when(itemFactory.makeItem(node2)).thenReturn(decisionNavigatorItem6);
        when(itemFactory.makeItem(node3)).thenReturn(decisionNavigatorItem7);
        when(itemFactory.makeItem(node4)).thenReturn(decisionNavigatorItem8);
        when(itemFactory.makeItem(node5)).thenReturn(decisionNavigatorItem9);
        when(itemFactory.makeItem(node6)).thenReturn(decisionNavigatorItem10);
        when(itemFactory.makeSeparator(DRG)).thenReturn(decisionNavigatorItem3);
        when(itemFactory.makeSeparator(DRDs)).thenReturn(decisionNavigatorItem4);
        when(dmnDiagramUtils.getNodeStream(stunnerDiagram1)).thenReturn(stunnerNodes1);
        when(dmnDiagramUtils.getNodeStream(stunnerDiagram2)).thenReturn(stunnerNodes2);

        final List<DecisionNavigatorItem> actualItems = itemsProvider.getItems();
        final List<DecisionNavigatorItem> expectedItems = asList(decisionNavigatorItem3, decisionNavigatorItem2, decisionNavigatorItem4, decisionNavigatorItem1);

        assertEquals(expectedItems, actualItems);
        verify(decisionNavigatorItem1).addChild(decisionNavigatorItem5);
        verify(decisionNavigatorItem1).addChild(decisionNavigatorItem6);
        verify(decisionNavigatorItem1).addChild(decisionNavigatorItem7);
        verify(decisionNavigatorItem2).addChild(decisionNavigatorItem8);
        verify(decisionNavigatorItem2).addChild(decisionNavigatorItem9);
        verify(decisionNavigatorItem2).addChild(decisionNavigatorItem10);
    }

    private Node mockNode(final Object definition) {

        final Node node = mock(Node.class);
        final Definition content = mock(Definition.class);

        when(node.getContent()).thenReturn(content);
        when(content.getDefinition()).thenReturn(definition);

        return node;
    }
}
