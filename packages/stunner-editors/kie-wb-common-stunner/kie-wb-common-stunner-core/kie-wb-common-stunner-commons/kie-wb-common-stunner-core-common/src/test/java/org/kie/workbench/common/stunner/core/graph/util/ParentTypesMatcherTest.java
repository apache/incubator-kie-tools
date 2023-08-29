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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.TestingGraphInstances;
import org.kie.workbench.common.stunner.core.TestingGraphMockHandler;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.Silent.class)
public class ParentTypesMatcherTest {

    private ParentTypesMatcher tested;
    private TestingGraphMockHandler graphTestHandler;
    private TestingGraphInstances.Level1Graph graph;

    @Before
    public void setUp() {
        graphTestHandler = new TestingGraphMockHandler();
        graph = TestingGraphInstances.newLevel1Graph(graphTestHandler);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testMatchParentTypeAsParentNodeBean() {
        ParentTypesMatcher tested = new ParentTypesMatcher(() -> graphTestHandler.getDefinitionManager(),
                                                           GraphUtils::getParent,
                                                           new Class[]{TestingGraphInstances.ParentNodeBean.class});
        assertTrue(tested.matcher().test(graph.startNode, graph.intermNode));
        assertTrue(tested.matcher().test(graph.intermNode, graph.endNode));
        assertTrue(tested.matcher().test(graph.startNode, graph.endNode));
        assertTrue(tested.matcher().test(graph.endNode, graph.nodeA));
        assertTrue(tested.matcher().test(graph.intermNode, graph.nodeA));
        assertTrue(tested.matcher().test(graph.startNode, graph.nodeA));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testMatchParentTypeAsContainerNodeBean() {
        ParentTypesMatcher tested = new ParentTypesMatcher(() -> graphTestHandler.getDefinitionManager(),
                                                           GraphUtils::getParent,
                                                           new Class[]{TestingGraphInstances.ContainerNodeBean.class});
        assertTrue(tested.matcher().test(graph.startNode, graph.intermNode));
        assertTrue(tested.matcher().test(graph.intermNode, graph.endNode));
        assertTrue(tested.matcher().test(graph.startNode, graph.endNode));
        assertFalse(tested.matcher().test(graph.endNode, graph.nodeA));
        assertFalse(tested.matcher().test(graph.intermNode, graph.nodeA));
        assertFalse(tested.matcher().test(graph.startNode, graph.nodeA));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testMatchParentTypeAsContainerNodeBeanButChangeIntermNode() {
        graphTestHandler
                .removeChild(graph.containerNode, graph.intermNode)
                .setChild(graph.parentNode, graph.intermNode);
        ParentTypesMatcher tested = new ParentTypesMatcher(() -> graphTestHandler.getDefinitionManager(),
                                                           GraphUtils::getParent,
                                                           new Class[]{TestingGraphInstances.ContainerNodeBean.class});
        assertFalse(tested.matcher().test(graph.startNode, graph.intermNode));
        assertFalse(tested.matcher().test(graph.intermNode, graph.endNode));
        assertTrue(tested.matcher().test(graph.startNode, graph.endNode));
        assertFalse(tested.matcher().test(graph.endNode, graph.nodeA));
        assertTrue(tested.matcher().test(graph.intermNode, graph.nodeA));
        assertFalse(tested.matcher().test(graph.startNode, graph.nodeA));
    }
}
