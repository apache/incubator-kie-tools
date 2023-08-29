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


package org.kie.workbench.common.stunner.core.graph.util;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.TestingGraphInstanceBuilder;
import org.kie.workbench.common.stunner.core.TestingGraphMockHandler;
import org.kie.workbench.common.stunner.core.diagram.GraphsProvider;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.HasContentDefinitionId;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.content.ChildrenTraverseProcessorImpl;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.tree.AbstractTreeTraverseCallback;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.tree.TreeWalkTraverseProcessor;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.tree.TreeWalkTraverseProcessorImpl;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class SafeDeleteNodeProcessorTest {

    @Mock
    private SafeDeleteNodeProcessor.Callback callback;

    private TestingGraphInstanceBuilder.TestGraph2 graphHolder;
    private TestingGraphInstanceBuilder.TestGraph3 graphHolderContainer;
    private TestingGraphInstanceBuilder.TestGraph4 graphHolderDocked;

    private SafeDeleteNodeProcessor tested;

    @Before
    public void setup() throws Exception {
        this.graphHolder = TestingGraphInstanceBuilder.newGraph2(new TestingGraphMockHandler());
        this.graphHolderContainer = TestingGraphInstanceBuilder.newGraph3(new TestingGraphMockHandler());
        this.graphHolderDocked = TestingGraphInstanceBuilder.newGraph4(new TestingGraphMockHandler());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testDeleteStartNode() {
        this.tested = new SafeDeleteNodeProcessor(new ChildrenTraverseProcessorImpl(new TreeWalkTraverseProcessorImpl()),
                                                  graphHolder.graph,
                                                  graphHolder.startNode);
        tested.run(callback);
        verify(callback,
               times(1)).deleteCandidateConnector(eq(graphHolder.edge1));
        verify(callback,
               times(1)).removeChild(eq(graphHolder.parentNode),
                                     eq(graphHolder.startNode));
        verify(callback,
               times(1)).deleteCandidateNode(eq(graphHolder.startNode));
        verify(callback,
               never()).removeDock(any(Node.class),
                                   any(Node.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testDeleteIntermediateNode() {
        this.tested = new SafeDeleteNodeProcessor(new ChildrenTraverseProcessorImpl(new TreeWalkTraverseProcessorImpl()),
                                                  graphHolder.graph,
                                                  graphHolder.intermNode);
        tested.run(callback);
        verify(callback,
               times(1)).deleteCandidateConnector(eq(graphHolder.edge1));
        verify(callback,
               times(1)).deleteCandidateConnector(eq(graphHolder.edge2));
        verify(callback,
               times(1)).removeChild(eq(graphHolder.parentNode),
                                     eq(graphHolder.intermNode));
        verify(callback,
               times(1)).deleteCandidateNode(eq(graphHolder.intermNode));
        verify(callback,
               never()).removeDock(any(Node.class),
                                   any(Node.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testDeleteEndNode() {
        this.tested = new SafeDeleteNodeProcessor(new ChildrenTraverseProcessorImpl(new TreeWalkTraverseProcessorImpl()),
                                                  graphHolder.graph,
                                                  graphHolder.endNode);
        tested.run(callback);
        verify(callback,
               times(1)).deleteCandidateConnector(eq(graphHolder.edge2));
        verify(callback,
               times(1)).removeChild(eq(graphHolder.parentNode),
                                     eq(graphHolder.endNode));
        verify(callback,
               times(1)).deleteCandidateNode(eq(graphHolder.endNode));
        verify(callback,
               never()).removeDock(any(Node.class),
                                   any(Node.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testDeleteParentNode() {
        this.tested = new SafeDeleteNodeProcessor(new ChildrenTraverseProcessorImpl(new TreeWalkTraverseProcessorImpl()),
                                                  graphHolder.graph,
                                                  graphHolder.parentNode);
        tested.run(callback);
        verify(callback,
               times(1)).deleteConnector(eq(graphHolder.edge2));
        verify(callback,
               times(1)).removeChild(eq(graphHolder.parentNode),
                                     eq(graphHolder.endNode));
        verify(callback,
               times(1)).deleteNode(eq(graphHolder.endNode));
        verify(callback,
               times(1)).deleteConnector(eq(graphHolder.edge1));
        verify(callback,
               times(1)).deleteConnector(eq(graphHolder.edge2));
        verify(callback,
               times(1)).removeChild(eq(graphHolder.parentNode),
                                     eq(graphHolder.intermNode));
        verify(callback,
               times(1)).deleteNode(eq(graphHolder.intermNode));
        verify(callback,
               times(1)).deleteConnector(eq(graphHolder.edge1));
        verify(callback,
               times(1)).removeChild(eq(graphHolder.parentNode),
                                     eq(graphHolder.startNode));
        verify(callback,
               times(1)).deleteNode(eq(graphHolder.startNode));
        verify(callback,
               times(1)).deleteCandidateNode(eq(graphHolder.parentNode));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testDeleteParentWithNonEmptyContainerInside() {
        this.tested = new SafeDeleteNodeProcessor(new ChildrenTraverseProcessorImpl(new TreeWalkTraverseProcessorImpl()),
                                                  graphHolderContainer.graph,
                                                  graphHolderContainer.parentNode);
        tested.run(callback);

        InOrder inOrder = inOrder(callback);

        inOrder.verify(callback,
                       times(1)).deleteConnector(eq(graphHolderContainer.edge2));

        inOrder.verify(callback,
                       times(1)).removeChild(eq(graphHolderContainer.containerNode),
                                             eq(graphHolderContainer.endNode));
        inOrder.verify(callback,
                       times(1)).deleteNode(eq(graphHolderContainer.endNode));

        inOrder.verify(callback,
                       times(1)).deleteConnector(eq(graphHolderContainer.edge1));

        inOrder.verify(callback,
                       times(1)).removeChild(eq(graphHolderContainer.containerNode),
                                             eq(graphHolderContainer.intermNode));
        inOrder.verify(callback,
                       times(1)).deleteNode(eq(graphHolderContainer.intermNode));

        inOrder.verify(callback,
                       times(1)).removeChild(eq(graphHolderContainer.containerNode),
                                             eq(graphHolderContainer.startNode));
        inOrder.verify(callback,
                       times(1)).deleteNode(eq(graphHolderContainer.startNode));

        inOrder.verify(callback,
                       times(1)).deleteNode(eq(graphHolderContainer.containerNode));

        inOrder.verify(callback,
                       times(1)).deleteCandidateNode(eq(graphHolderContainer.parentNode));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testDeleteDockParent() {
        this.tested = new SafeDeleteNodeProcessor(new ChildrenTraverseProcessorImpl(new TreeWalkTraverseProcessorImpl()),
                                                  graphHolderDocked.graph,
                                                  graphHolderDocked.intermNode);
        tested.run(callback);
        verify(callback, times(1)).deleteCandidateConnector(eq(graphHolderDocked.edge1));
        verify(callback, times(1)).deleteCandidateConnector(eq(graphHolderDocked.edge2));
        verify(callback, times(1)).removeChild(eq(graphHolderDocked.parentNode), eq(graphHolderDocked.intermNode));
        verify(callback, times(1)).deleteCandidateNode(eq(graphHolderDocked.intermNode));
        verify(callback, times(1)).removeDock(graphHolderDocked.intermNode, graphHolderDocked.dockedNode);
        verify(callback, times(1)).deleteNode(graphHolderDocked.dockedNode);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testDeleteDocked() {
        this.tested = new SafeDeleteNodeProcessor(new ChildrenTraverseProcessorImpl(new TreeWalkTraverseProcessorImpl()),
                                                  graphHolderDocked.graph,
                                                  graphHolderDocked.dockedNode);
        tested.run(callback);
        verify(callback, never()).deleteCandidateConnector(any(Edge.class));
        verify(callback, times(1)).removeDock(graphHolderDocked.intermNode, graphHolderDocked.dockedNode);
        verify(callback, times(1)).deleteCandidateNode(graphHolderDocked.dockedNode);
    }

    @Test
    public void testRun() {
        final Graph graph = mock(Graph.class);
        final Node node = mock(Node.class);
        this.tested = spy(new SafeDeleteNodeProcessor(new ChildrenTraverseProcessorImpl(new TreeWalkTraverseProcessorImpl()),
                                                      graph,
                                                      node));
        final ArrayDeque nodes = mock(ArrayDeque.class);
        doReturn(nodes).when(tested).createNodesDequeue();
        doNothing().when(tested).deleteChildren(callback, nodes);
        doNothing().when(tested).processNode(node, callback, true);

        tested.run(callback);

        verify(tested).deleteChildren(callback, nodes);
        verify(tested, never()).deleteGlobalGraphNodes(callback, nodes);
    }

    @Test
    public void testInit() {
        final Graph graph = mock(Graph.class);
        final Node node = mock(Node.class);
        final Definition definition = mock(Definition.class);
        final HasContentDefinitionId hasContentDefinitionId = mock(HasContentDefinitionId.class);
        final String definitionId = "definition id";
        final String dmnDiagramId = "dmn diagram id";

        when(node.getContent()).thenReturn(definition);
        when(definition.getDefinition()).thenReturn(hasContentDefinitionId);
        when(hasContentDefinitionId.getContentDefinitionId()).thenReturn(definitionId);
        when(hasContentDefinitionId.getDiagramId()).thenReturn(dmnDiagramId);

        this.tested = new SafeDeleteNodeProcessor(new ChildrenTraverseProcessorImpl(new TreeWalkTraverseProcessorImpl()),
                                                  graph,
                                                  node);

        tested.init();

        final String candidateContentDefinitionId = tested.getCandidateContentDefinitionId();
        final String candidateDmnDiagramId = tested.getCandidateDiagramId();

        assertEquals(definitionId, candidateContentDefinitionId);
        assertEquals(dmnDiagramId, candidateDmnDiagramId);
    }

    @Test
    public void testRunKeepChildren() {
        final Graph graph = mock(Graph.class);
        final Node node = mock(Node.class);
        final GraphsProvider graphsProvider = mock(GraphsProvider.class);
        when(graphsProvider.isGlobalGraphSelected()).thenReturn(false);

        this.tested = spy(new SafeDeleteNodeProcessor(new ChildrenTraverseProcessorImpl(new TreeWalkTraverseProcessorImpl()),
                                                      graph,
                                                      node,
                                                      true,
                                                      new TreeWalkTraverseProcessorImpl(),
                                                      graphsProvider));
        final ArrayDeque nodes = mock(ArrayDeque.class);
        doReturn(nodes).when(tested).createNodesDequeue();
        doNothing().when(tested).deleteChildren(callback, nodes);
        doNothing().when(tested).processNode(node, callback, true);

        tested.run(callback);

        verify(tested, never()).deleteChildren(callback, nodes);
        verify(tested, never()).deleteGlobalGraphNodes(callback, nodes);
    }

    @Test
    public void testRunKeepChildrenAndIsGlobalGraph() {
        final Graph graph = mock(Graph.class);
        final Node node = mock(Node.class);
        final GraphsProvider graphsProvider = mock(GraphsProvider.class);
        when(graphsProvider.isGlobalGraphSelected()).thenReturn(true);

        this.tested = spy(new SafeDeleteNodeProcessor(new ChildrenTraverseProcessorImpl(new TreeWalkTraverseProcessorImpl()),
                                                      graph,
                                                      node,
                                                      true,
                                                      new TreeWalkTraverseProcessorImpl(),
                                                      graphsProvider));
        final ArrayDeque nodes = mock(ArrayDeque.class);
        doReturn(nodes).when(tested).createNodesDequeue();
        doNothing().when(tested).deleteChildren(callback, nodes);
        doNothing().when(tested).processNode(node, callback, true);
        doNothing().when(tested).deleteGlobalGraphNodes(callback, nodes);

        tested.run(callback);

        verify(tested, never()).deleteChildren(callback, nodes);
        verify(tested).deleteGlobalGraphNodes(callback, nodes);
    }

    @Test
    public void testDeleteGlobalGraphNodes() {
        final Node node = mock(Node.class);
        final GraphsProvider graphsProvider = mock(GraphsProvider.class);
        final Deque nodes = new ArrayDeque();
        final TreeWalkTraverseProcessor treeWalk = mock(TreeWalkTraverseProcessor.class);
        final List<Graph> graphs = Arrays.asList(graphHolder.graph,
                                                 graphHolderContainer.graph,
                                                 graphHolderDocked.graph);

        when(graphsProvider.isGlobalGraphSelected()).thenReturn(false);

        this.tested = spy(new SafeDeleteNodeProcessor(new ChildrenTraverseProcessorImpl(new TreeWalkTraverseProcessorImpl()),
                                                      graphHolder.graph,
                                                      node,
                                                      true,
                                                      treeWalk,
                                                      graphsProvider));

        when(graphsProvider.getGraphs()).thenReturn(graphs);
        doReturn(nodes).when(tested).createNodesDequeue();
        doReturn(true).when(tested).processGlobalNodeForDeletion(any(Node.class), eq(nodes));

        tested.deleteGlobalGraphNodes(callback, nodes);

        verify(treeWalk).traverse(eq(graphHolder.graph), any(AbstractTreeTraverseCallback.class));
        verify(treeWalk).traverse(eq(graphHolderContainer.graph), any(AbstractTreeTraverseCallback.class));
        verify(treeWalk).traverse(eq(graphHolderDocked.graph), any(AbstractTreeTraverseCallback.class));
    }

    @Test
    public void testProcessGlobalNodeForDeletion() {

        final Deque deque = mock(Deque.class);
        final String nodeId = "id";
        final Node node = createNode(nodeId, "diagramId");

        this.tested = mock(SafeDeleteNodeProcessor.class);

        when(tested.getCandidateContentDefinitionId()).thenReturn(nodeId);
        when(tested.isDuplicatedOnTheCurrentDiagram(eq(node), anyString(), anyString())).thenReturn(false);
        when(tested.processGlobalNodeForDeletion(node, deque)).thenCallRealMethod();

        final boolean shouldDeleteDuplicatedDRGNodes = tested.processGlobalNodeForDeletion(node, deque);

        verify(deque).add(node);

        assertTrue(shouldDeleteDuplicatedDRGNodes);
    }

    @Test
    public void testProcessDuplicatedGlobalNodeForDeletion() {

        final String nodeId = "id";
        final String diagramId = "diagramId";
        final Node node = createNode(nodeId, diagramId);
        final Deque deque = mock(Deque.class);

        this.tested = mock(SafeDeleteNodeProcessor.class);

        when(tested.getCandidateContentDefinitionId()).thenReturn(nodeId);
        when(tested.isDuplicatedOnTheCurrentDiagram(node, nodeId, diagramId)).thenReturn(true);
        when(tested.processGlobalNodeForDeletion(node, deque)).thenCallRealMethod();

        final boolean shouldDeleteDuplicatedDRGNodes = tested.processGlobalNodeForDeletion(node, deque);

        verify(deque).clear();
        verify(deque, never()).add(node);

        assertFalse(shouldDeleteDuplicatedDRGNodes);
    }

    private Node createNode(final String contentDefinitionId,
                            final String diagramId) {

        final Node node = mock(Node.class);
        final Definition content = mock(Definition.class);
        final HasContentDefinitionId hasContentDefinitionId = mock(HasContentDefinitionId.class);
        when(hasContentDefinitionId.getContentDefinitionId()).thenReturn(contentDefinitionId);
        when(hasContentDefinitionId.getDiagramId()).thenReturn(diagramId);
        when(node.getContent()).thenReturn(content);
        when(content.getDefinition()).thenReturn(hasContentDefinitionId);

        return node;
    }

    @Test
    public void testIsDuplicatedOnTheCurrentDiagramWhenIs() {

        final Node node = mock(Node.class);
        final String nodeId = "id";
        final String diagramId = "diagramId";

        this.tested = mock(SafeDeleteNodeProcessor.class);
        doCallRealMethod().when(tested).isDuplicatedOnTheCurrentDiagram(node, nodeId, diagramId);
        doReturn(nodeId).when(tested).getCandidateContentDefinitionId();
        doReturn(diagramId).when(tested).getCandidateDiagramId();

        final boolean actual = tested.isDuplicatedOnTheCurrentDiagram(node, nodeId, diagramId);

        assertTrue(actual);
    }

    @Test
    public void testIsDuplicatedOnTheCurrentDiagramWhenIsNot() {

        final Node node = mock(Node.class);
        final String nodeId = "id";
        final String diagramId = "diagramId";
        final String anotherDiagramId = "anotherDiagramId";

        this.tested = mock(SafeDeleteNodeProcessor.class);
        doCallRealMethod().when(tested).isDuplicatedOnTheCurrentDiagram(node, nodeId, diagramId);
        doReturn(nodeId).when(tested).getCandidateContentDefinitionId();
        doReturn(anotherDiagramId).when(tested).getCandidateDiagramId();

        final boolean actual = tested.isDuplicatedOnTheCurrentDiagram(node, nodeId, diagramId);

        assertFalse(actual);
    }
}
