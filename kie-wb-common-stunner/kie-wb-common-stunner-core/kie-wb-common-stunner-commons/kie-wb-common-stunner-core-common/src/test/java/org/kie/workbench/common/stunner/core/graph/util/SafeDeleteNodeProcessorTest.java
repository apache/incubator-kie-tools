/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
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
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.content.ChildrenTraverseProcessorImpl;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.tree.TreeWalkTraverseProcessorImpl;
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
    private TestingGraphInstanceBuilder.TestGraph2 graphHolder;
    private SafeDeleteNodeProcessor tested;

    @Before
    public void setup() throws Exception {
        this.graphTestHandler = new TestingGraphMockHandler();
        this.graphHolder = TestingGraphInstanceBuilder.newGraph2(graphTestHandler);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testDeleteStartNode() {
        this.tested = new SafeDeleteNodeProcessor(new ChildrenTraverseProcessorImpl(new TreeWalkTraverseProcessorImpl()),
                                                  graphHolder.graph,
                                                  graphHolder.startNode);
        tested.run(callback);
        verifyDeleteStartNode();
        verify(callback,
               never()).deleteIncomingConnection(any(Edge.class));
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
        verifyDeleteIntermediateNode();
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
        verifyDeleteEndNode();
        verify(callback,
               never()).deleteOutgoingConnection(any(Edge.class));
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
        verifyDeleteEndNode();
        verifyDeleteIntermediateNode();
        verifyDeleteStartNode();
        verifyDeleteParentNode();
    }

    @SuppressWarnings("unchecked")
    private void verifyDeleteStartNode() {
        verify(callback,
               times(1)).deleteOutgoingConnection(eq(graphHolder.edge1));
        verify(callback,
               times(1)).removeChild(eq(graphHolder.parentNode),
                                     eq(graphHolder.startNode));
        verify(callback,
               times(1)).deleteNode(eq(graphHolder.startNode));
    }

    @SuppressWarnings("unchecked")
    private void verifyDeleteIntermediateNode() {
        verify(callback,
               times(1)).deleteIncomingConnection(eq(graphHolder.edge1));
        verify(callback,
               times(1)).deleteOutgoingConnection(eq(graphHolder.edge2));
        verify(callback,
               times(1)).removeChild(eq(graphHolder.parentNode),
                                     eq(graphHolder.intermNode));
        verify(callback,
               times(1)).deleteNode(eq(graphHolder.intermNode));
    }

    @SuppressWarnings("unchecked")
    private void verifyDeleteEndNode() {
        verify(callback,
               times(1)).deleteIncomingConnection(eq(graphHolder.edge2));
        verify(callback,
               times(1)).removeChild(eq(graphHolder.parentNode),
                                     eq(graphHolder.endNode));
        verify(callback,
               times(1)).deleteNode(eq(graphHolder.endNode));
    }

    @SuppressWarnings("unchecked")
    private void verifyDeleteParentNode() {
        verify(callback,
               times(1)).deleteNode(eq(graphHolder.parentNode));
    }
}
