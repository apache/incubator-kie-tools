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

package org.kie.workbench.common.dmn.client.graph;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.v1_1.DMNDiagram;
import org.kie.workbench.common.dmn.api.definition.v1_1.Decision;
import org.kie.workbench.common.dmn.api.definition.v1_1.Definitions;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasHandler;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewImpl;
import org.kie.workbench.common.stunner.core.graph.impl.EdgeImpl;
import org.kie.workbench.common.stunner.core.graph.impl.GraphImpl;
import org.kie.workbench.common.stunner.core.graph.impl.NodeImpl;
import org.kie.workbench.common.stunner.core.graph.store.GraphNodeStoreImpl;
import org.kie.workbench.common.stunner.core.util.UUID;
import org.kie.workbench.common.stunner.project.diagram.ProjectMetadata;
import org.kie.workbench.common.stunner.project.diagram.impl.ProjectDiagramImpl;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DMNGraphUtilsTest {

    private static final String NAME = "name";

    @Mock
    private SessionManager sessionManager;

    @Mock
    private ClientSession clientSession;

    @Mock
    private CanvasHandler canvasHandler;

    @Mock
    private ProjectMetadata metadata;

    @Mock
    private Bounds bounds;

    private DMNGraphUtils utils;

    private ProjectDiagramImpl diagram;

    private GraphImpl graph;

    @Before
    public void setup() {
        this.utils = new DMNGraphUtils(sessionManager);
        this.graph = new GraphImpl(UUID.uuid(), new GraphNodeStoreImpl());
        this.diagram = new ProjectDiagramImpl(NAME, graph, metadata);
        when(sessionManager.getCurrentSession()).thenReturn(clientSession);
        when(clientSession.getCanvasHandler()).thenReturn(canvasHandler);
        when(canvasHandler.getDiagram()).thenReturn(diagram);
    }

    @Test
    public void testGetDefinitionsWithRootNode() {
        final DMNDiagram definition = new DMNDiagram();
        graph.addNode(newNode(definition));

        final Definitions definitions = utils.getDefinitions();
        assertNotNull(definitions);
        assertEquals(definition.getDefinitions(),
                     definitions);
    }

    @Test
    public void testGetDefinitionsWithMultipleRootNodes() {
        final Decision definition1 = new Decision();
        final DMNDiagram definition2 = new DMNDiagram();
        graph.addNode(newNode(definition1));
        graph.addNode(newNode(definition2));

        final Definitions definitions = utils.getDefinitions();
        assertNotNull(definitions);
        assertEquals(definition2.getDefinitions(),
                     definitions);
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

        final Definitions definitions = utils.getDefinitions();
        assertNotNull(definitions);
        assertEquals(definition2.getDefinitions(),
                     definitions);
    }

    @Test
    public void testGetDefinitionsWithNoNodes() {
        assertNull(utils.getDefinitions());
    }

    private Node<View, Edge> newNode(final Object definition) {
        final Node<View, Edge> node = new NodeImpl<>(UUID.uuid());
        final View<Object> content = new ViewImpl<>(definition, bounds);
        node.setContent(content);
        return node;
    }
}
