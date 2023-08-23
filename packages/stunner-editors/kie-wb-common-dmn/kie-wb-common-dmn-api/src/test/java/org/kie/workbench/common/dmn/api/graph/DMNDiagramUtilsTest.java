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

package org.kie.workbench.common.dmn.api.graph;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.model.DMNDiagram;
import org.kie.workbench.common.dmn.api.definition.model.DRGElement;
import org.kie.workbench.common.dmn.api.definition.model.Decision;
import org.kie.workbench.common.dmn.api.definition.model.Definitions;
import org.kie.workbench.common.dmn.api.property.dmn.Text;
import org.kie.workbench.common.stunner.core.diagram.DiagramImpl;
import org.kie.workbench.common.stunner.core.diagram.MetadataImpl;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.graph.content.definition.DefinitionSet;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewImpl;
import org.kie.workbench.common.stunner.core.graph.impl.EdgeImpl;
import org.kie.workbench.common.stunner.core.graph.impl.GraphImpl;
import org.kie.workbench.common.stunner.core.graph.impl.NodeImpl;
import org.kie.workbench.common.stunner.core.graph.store.GraphNodeStoreImpl;
import org.kie.workbench.common.stunner.core.util.UUID;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DMNDiagramUtilsTest {

    private static final String NAME = "name";

    @Mock
    private Definition definition;

    @Mock
    private DMNDiagram dmnDiagram;

    @Mock
    private Definitions definitions;

    @Mock
    private MetadataImpl metadata;

    @Mock
    private Bounds bounds;

    @Mock
    private Node node;

    private DiagramImpl diagram;

    private GraphImpl<DefinitionSet> graph;

    private String namespace = "://namespace";

    private DMNDiagramUtils utils;

    @Before
    public void setup() {

        utils = new DMNDiagramUtils();
        graph = new GraphImpl<>(UUID.uuid(), new GraphNodeStoreImpl());
        diagram = new DiagramImpl(NAME, graph, metadata);

        graph.addNode(node);

        when(node.getContent()).thenReturn(definition);
    }

    @Test
    public void testGetNodes() {

        final DRGElement drgElement = mock(DRGElement.class);

        when(definition.getDefinition()).thenReturn(drgElement);

        final List<DRGElement> actualNodes = utils.getDRGElements(diagram);
        final List<DRGElement> expectedNodes = singletonList(drgElement);

        assertEquals(expectedNodes, actualNodes);
    }

    @Test
    public void testGetNamespaceByDiagram() {

        when(definition.getDefinition()).thenReturn(dmnDiagram);
        when(dmnDiagram.getDefinitions()).thenReturn(definitions);
        when(definitions.getNamespace()).thenReturn(new Text(namespace));

        final String actualNamespace = utils.getNamespace(diagram);

        assertEquals(namespace, actualNamespace);
    }

    @Test
    public void testGetDefinitionsWithRootNode() {

        final DMNDiagram definition = new DMNDiagram();

        graph.addNode(newNode(definition));

        final Definitions definitions = utils.getDefinitions(diagram);

        assertNotNull(definitions);
        assertEquals(definition.getDefinitions(), definitions);
    }

    @Test
    public void testGetDefinitionsWithMultipleRootNodes() {

        final Decision definition1 = new Decision();
        final DMNDiagram definition2 = new DMNDiagram();

        graph.addNode(newNode(definition1));
        graph.addNode(newNode(definition2));

        final Definitions definitions = utils.getDefinitions(diagram);

        assertNotNull(definitions);
        assertEquals(definition2.getDefinitions(), definitions);
    }

    @Test
    public void testGetDefinitionsWithConnectedNodes() {

        final Decision definition1 = new Decision();
        final DMNDiagram definition2 = new DMNDiagram();
        final Node<View, Edge> node1 = newNode(definition1);
        final Node<View, Edge> node2 = newNode(definition2);

        final Edge<View, Node> edge = new EdgeImpl<>(UUID.uuid());
        node1.getInEdges().add(edge);
        node2.getOutEdges().add(edge);
        edge.setSourceNode(node2);
        edge.setTargetNode(node1);

        graph.addNode(node1);
        graph.addNode(node2);

        final Definitions definitions = utils.getDefinitions(diagram);

        assertNotNull(definitions);
        assertEquals(definition2.getDefinitions(), definitions);
    }

    @Test
    public void testGetDefinitionsWithNoNodes() {
        assertNull(utils.getDefinitions(diagram));
    }

    private Node<View, Edge> newNode(final Object definition) {

        final Node<View, Edge> node = new NodeImpl<>(UUID.uuid());
        final View<Object> content = new ViewImpl<>(definition, bounds);

        node.setContent(content);

        return node;
    }
}
