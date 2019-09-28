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

package org.kie.workbench.common.dmn.client.widgets.toolbar;

import java.util.Arrays;
import java.util.List;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.model.Decision;
import org.kie.workbench.common.dmn.api.definition.model.DecisionService;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class DMNGraphProcessorTest {

    @Test
    public void testGetNodes() {
        final DMNGraphProcessor processor = new DMNGraphProcessor();
        final Graph graph = mock(Graph.class);
        final Decision decision1 = mock(Decision.class);
        final Decision decision2 = mock(Decision.class);
        final Node node1 = createNode(decision1, "id1");
        final Node node2 = createNode(decision2, "id2");
        final List<Node> graphNodes = createGraphNodes(node1, node2);

        when(graph.nodes()).thenReturn(graphNodes);

        final Iterable<? extends Node> nodes = processor.getNodes(graph);
        for (final Node node : nodes) {
            assertTrue(graphNodes.contains(node));
            assertFalse(processor.isReplacedByAnotherNode(node.getUUID()));
        }
    }

    @Test
    public void testGetNodesWithDecisionServices() {
        final String childId1 = "id1";
        final String childId2 = "id2";
        final String dsId = "dsId";
        final String decisionNodeId = "decision1Node";
        final DMNGraphProcessor processor = spy(new DMNGraphProcessor());
        final Graph graph = mock(Graph.class);
        final Decision decision1 = mock(Decision.class);
        final Node decisionNode = createNode(decision1, decisionNodeId);
        final DecisionService ds = mock(DecisionService.class);
        final Node dsNode = createNode(ds, dsId);
        final Decision child1 = mock(Decision.class);
        final Node ch1 = createNode(child1, childId1);
        final Decision child2 = mock(Decision.class);
        final Node ch2 = createNode(child2, childId2);
        final List<Node> children = Arrays.asList(ch1, ch2);
        doReturn(children).when(processor).getChildNodes(dsNode);

        final List<Node> graphNodes = createGraphNodes(dsNode, decisionNode);
        when(graph.nodes()).thenReturn(graphNodes);

        final List<Node> nodes = (List<Node>) processor.getNodes(graph);

        assertEquals(2, nodes.size());
        assertTrue(nodes.contains(dsNode));
        assertTrue(nodes.contains(decisionNode));
        assertTrue(processor.isReplacedByAnotherNode(childId1));
        assertEquals(dsId, processor.getReplaceNodeId(childId1));
        assertTrue(processor.isReplacedByAnotherNode(childId2));
        assertEquals(dsId, processor.getReplaceNodeId(childId2));
    }

    private List<Node> createGraphNodes(final Node... nodes) {
        return Arrays.asList(nodes);
    }

    private Node createNode(final Object definition, final String nodeId) {
        final Node node = mock(Node.class);
        when(node.getUUID()).thenReturn(nodeId);
        final Definition content = mock(Definition.class);
        when(node.getContent()).thenReturn(content);
        when(content.getDefinition()).thenReturn(definition);

        return node;
    }
}