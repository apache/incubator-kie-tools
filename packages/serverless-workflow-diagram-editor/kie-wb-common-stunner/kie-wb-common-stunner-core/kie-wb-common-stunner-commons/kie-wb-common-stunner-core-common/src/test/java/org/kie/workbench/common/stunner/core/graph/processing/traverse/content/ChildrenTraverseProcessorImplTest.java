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


package org.kie.workbench.common.stunner.core.graph.processing.traverse.content;

import java.util.List;

import org.junit.Test;
import org.kie.workbench.common.stunner.core.TestingGraphInstanceBuilder;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.tree.TreeWalkTraverseProcessorImpl;
import org.mockito.ArgumentCaptor;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class ChildrenTraverseProcessorImplTest extends ContentTraverseProcessorBaseTest<ChildrenTraverseProcessor, ChildrenTraverseCallback> {

    @Override
    protected ChildrenTraverseProcessorImpl newTraverseProcessor() {
        return new ChildrenTraverseProcessorImpl(new TreeWalkTraverseProcessorImpl());
    }

    @Override
    protected ChildrenTraverseCallback newTraverseCallback() {
        return mock(ChildrenTraverseCallback.class);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testTraverseGraph1() {
        final TestingGraphInstanceBuilder.TestGraph1 result =
                TestingGraphInstanceBuilder.newGraph1(graphTestHandler);
        tested.traverse(result.graph,
                        callback);
        verify(callback,
               times(1)).startNodeTraversal(eq(result.startNode));
        verify(callback,
               times(1)).startNodeTraversal(eq(result.intermNode));
        verify(callback,
               times(1)).startNodeTraversal(eq(result.endNode));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testTraverseGraph2() {
        final TestingGraphInstanceBuilder.TestGraph2 result =
                TestingGraphInstanceBuilder.newGraph2(graphTestHandler);
        final ArgumentCaptor<List> cStartNodeParents = ArgumentCaptor.forClass(List.class);
        final ArgumentCaptor<List> cIntermNodeParents = ArgumentCaptor.forClass(List.class);
        final ArgumentCaptor<List> cEndNodeParents = ArgumentCaptor.forClass(List.class);
        tested.traverse(result.graph,
                        callback);
        verify(callback,
               never()).startGraphTraversal(eq(result.graph));
        verify(callback,
               times(1)).startNodeTraversal(eq(result.parentNode));
        verify(callback,
               times(1)).startNodeTraversal(cStartNodeParents.capture(),
                                            eq(result.startNode));
        verify(callback,
               times(1)).startNodeTraversal(cIntermNodeParents.capture(),
                                            eq(result.intermNode));
        verify(callback,
               times(1)).startNodeTraversal(cEndNodeParents.capture(),
                                            eq(result.endNode));
        final List<Node> startNodeParents = cStartNodeParents.getValue();
        final List<Node> intermNodeParents = cStartNodeParents.getValue();
        final List<Node> endNodeParents = cStartNodeParents.getValue();
        assertEquals(1,
                     startNodeParents.size());
        assertEquals(result.parentNode,
                     startNodeParents.get(0));
        assertEquals(1,
                     intermNodeParents.size());
        assertEquals(result.parentNode,
                     intermNodeParents.get(0));
        assertEquals(1,
                     endNodeParents.size());
        assertEquals(result.parentNode,
                     endNodeParents.get(0));
    }
}
