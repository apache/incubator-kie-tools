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

package org.kie.workbench.common.dmn.client.commands.util;

import java.util.Optional;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.diagram.GraphsProvider;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.HasContentDefinitionId;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.kie.workbench.common.dmn.client.commands.util.ContentDefinitionIdUtils.belongsToCurrentGraph;
import static org.kie.workbench.common.dmn.client.commands.util.ContentDefinitionIdUtils.getDiagramId;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class ContentDefinitionIdUtilsTest {

    @Test
    public void testNodeBelongsToCurrentGraph() {

        final String currentDiagramId = "currentDiagramId";
        final Node node = createNode(currentDiagramId);
        final GraphsProvider graphsProvider = createGraphsProvider(currentDiagramId);

        assertTrue(belongsToCurrentGraph(node, graphsProvider));
    }

    @Test
    public void testNodeBelongsToCurrentGraphWhenDoesNot() {

        final String nodeDiagramId = "nodeDiagramId";
        final String currentDiagramId = "currentDiagramId";
        final Node node = createNode(nodeDiagramId);
        final GraphsProvider graphsProvider = createGraphsProvider(currentDiagramId);

        assertFalse(belongsToCurrentGraph(node, graphsProvider));
    }

    @Test
    public void testEdgeBelongsToCurrentGraph() {

        final String currentDiagramId = "currentDiagramId";
        final Node node = createNode(currentDiagramId);
        final Edge edge = createEdge(node, null);
        final GraphsProvider graphsProvider = createGraphsProvider(currentDiagramId);

        assertTrue(belongsToCurrentGraph(edge, graphsProvider));
    }

    @Test
    public void testEdgeBelongsToCurrentGraphWhenDoesNot() {

        final String nodeDiagramId = "nodeDiagramId";
        final String currentDiagramId = "currentDiagramId";
        final Node node = createNode(nodeDiagramId);
        final Edge edge = createEdge(node, null);
        final GraphsProvider graphsProvider = createGraphsProvider(currentDiagramId);

        assertFalse(belongsToCurrentGraph(edge, graphsProvider));
    }

    @Test
    public void testGetDiagramIdFromEdge() {

        final String targetNodeDiagramId = "targetNodeId";
        final String sourceNodeDiagramId = "sourceNodeId";
        final Node target = createNode(targetNodeDiagramId);
        final Node source = createNode(sourceNodeDiagramId);
        final Edge edge = createEdge(source, target);

        final Optional<String> actualId = getDiagramId(edge);

        assertTrue(actualId.isPresent());
        assertEquals(sourceNodeDiagramId, actualId.get());
    }

    @Test
    public void testGetDiagramIdFromEdgeSourceNode() {

        final String sourceNodeDiagramId = "diagram id";
        final Node source = createNode(sourceNodeDiagramId);
        final Edge edge = createEdge(source, null);

        final Optional<String> actualId = getDiagramId(edge);

        assertTrue(actualId.isPresent());
        assertEquals(sourceNodeDiagramId, actualId.get());
    }

    @Test
    public void testGetDiagramIdFromEdgeTargetNode() {

        final String targetNodeDiagramId = "diagram id";
        final Node target = createNode(targetNodeDiagramId);
        final Edge edge = createEdge(null, target);

        final Optional<String> actualId = getDiagramId(edge);

        assertTrue(actualId.isPresent());
        assertEquals(targetNodeDiagramId, actualId.get());
    }

    @Test
    public void testGetDiagramIdFromNode() {

        final String diagramId = "diagram Id";
        final Node node = createNode(diagramId);

        final Optional<String> actual = getDiagramId(node);

        assertTrue(actual.isPresent());
        assertEquals(diagramId, actual.get());
    }

    @Test
    public void testGetDiagramIdFromNodeWhenNodeIsNull() {

        final Optional<String> diagramId = getDiagramId((Node) null);
        assertFalse(diagramId.isPresent());
        assertNotNull(diagramId);
    }

    private Edge createEdge(final Node sourceNode, final Node targetNode) {

        final Edge edge = mock(Edge.class);
        when(edge.getSourceNode()).thenReturn(sourceNode);
        when(edge.getTargetNode()).thenReturn(targetNode);
        return edge;
    }

    private Node createNode(final String diagramId) {

        final Node node = mock(Node.class);
        final Definition definition = mock(Definition.class);
        final HasContentDefinitionId hasContentDefinitionId = mock(HasContentDefinitionId.class);

        when(node.getContent()).thenReturn(definition);
        when(hasContentDefinitionId.getDiagramId()).thenReturn(diagramId);
        when(definition.getDefinition()).thenReturn(hasContentDefinitionId);

        return node;
    }

    private GraphsProvider createGraphsProvider(final String currentDiagramId) {

        final GraphsProvider graphsProvider = mock(GraphsProvider.class);
        when(graphsProvider.getCurrentDiagramId()).thenReturn(currentDiagramId);
        return graphsProvider;
    }
}