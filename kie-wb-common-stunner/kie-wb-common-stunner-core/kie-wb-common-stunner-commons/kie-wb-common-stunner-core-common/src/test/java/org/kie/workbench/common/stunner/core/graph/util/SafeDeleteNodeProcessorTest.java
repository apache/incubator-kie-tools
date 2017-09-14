/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.core.graph.util;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.TestingGraphInstanceBuilder;
import org.kie.workbench.common.stunner.core.TestingGraphMockHandler;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.content.ChildrenTraverseProcessorImpl;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.tree.TreeWalkTraverseProcessorImpl;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class SafeDeleteNodeProcessorTest {

    @Mock
    private SafeDeleteNodeProcessor.Callback callback;

    private TestingGraphMockHandler graphTestHandler;
    private TestingGraphMockHandler graphTestHandlerContainer;
    private TestingGraphInstanceBuilder.TestGraph2 graphHolder;
    private TestingGraphInstanceBuilder.TestGraph3 graphHolderContainer;

    private SafeDeleteNodeProcessor tested;

    @Before
    public void setup() throws Exception {
        this.graphTestHandler = new TestingGraphMockHandler();
        this.graphTestHandlerContainer = new TestingGraphMockHandler();
        this.graphHolder = TestingGraphInstanceBuilder.newGraph2(graphTestHandler);
        this.graphHolderContainer = TestingGraphInstanceBuilder.newGraph3(graphTestHandlerContainer);
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
}
