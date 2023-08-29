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

import org.junit.Test;
import org.kie.workbench.common.stunner.core.TestingGraphInstanceBuilder;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.tree.TreeWalkTraverseProcessorImpl;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class ViewTraverseProcessorImplTest extends ContentTraverseProcessorBaseTest<ViewTraverseProcessor, ContentTraverseCallback> {

    @Override
    protected ViewTraverseProcessor newTraverseProcessor() {
        return new ViewTraverseProcessorImpl(new TreeWalkTraverseProcessorImpl());
    }

    @Override
    protected ContentTraverseCallback newTraverseCallback() {
        return mock(ContentTraverseCallback.class);
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

        verify(callback,
               times(1)).startEdgeTraversal(eq(result.edge1));
        verify(callback,
               times(1)).startEdgeTraversal(eq(result.edge2));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testTraverseGraph2() {
        final TestingGraphInstanceBuilder.TestGraph2 result =
                TestingGraphInstanceBuilder.newGraph2(graphTestHandler);
        tested.traverse(result.graph,
                        callback);
        verify(callback,
               times(1)).startNodeTraversal(eq(result.parentNode));
        verify(callback,
               times(1)).startNodeTraversal(eq(result.startNode));
        verify(callback,
               times(1)).startNodeTraversal(eq(result.intermNode));

        verify(callback,
               times(1)).startEdgeTraversal(eq(result.edge1));
        verify(callback,
               times(1)).startEdgeTraversal(eq(result.edge2));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testTraverseGraph5() {
        final TestingGraphInstanceBuilder.TestGraph5 result =
                TestingGraphInstanceBuilder.newGraph5(graphTestHandler);

        tested.traverse(result.graph,
                        callback);
        verify(callback,
               times(1)).startNodeTraversal(eq(result.startNode));
        verify(callback,
               times(1)).startNodeTraversal(eq(result.intermNode));
        verify(callback,
               times(1)).startEdgeTraversal(eq(result.edge1));
        verify(callback,
               times(1)).startEdgeTraversal(eq(result.edge2));
        verify(callback,
               times(1)).startNodeTraversal(result.endNode);
    }
}
