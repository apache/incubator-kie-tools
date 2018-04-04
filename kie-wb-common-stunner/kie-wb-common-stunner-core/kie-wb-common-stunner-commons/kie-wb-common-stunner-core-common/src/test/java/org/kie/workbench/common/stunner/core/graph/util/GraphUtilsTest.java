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

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.TestingGraphInstanceBuilder;
import org.kie.workbench.common.stunner.core.TestingGraphMockHandler;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.view.BoundImpl;
import org.kie.workbench.common.stunner.core.graph.content.view.BoundsImpl;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class GraphUtilsTest {

    private TestingGraphMockHandler graphTestHandler;
    private TestingGraphInstanceBuilder.TestGraph4 graphInstance;

    @Before
    public void setup() {
        this.graphTestHandler = new TestingGraphMockHandler();
        graphInstance = TestingGraphInstanceBuilder.newGraph4(graphTestHandler);
    }

    @Test
    public void hasChildrenTest() {
        boolean hasChildren = GraphUtils.hasChildren(graphInstance.parentNode);
        assertTrue(hasChildren);
    }

    @Test
    public void notHasChildrenTest() {
        boolean hasChildren = GraphUtils.hasChildren(graphInstance.startNode);
        assertFalse(hasChildren);
    }

    @Test
    public void countChildrenTest() {
        Long countChildren = GraphUtils.countChildren(graphInstance.parentNode);
        assertEquals(Long.valueOf(3),
                     countChildren);
    }

    @Test
    public void checkBoundsExceededTest() {
        Bounds parentBounds = new BoundsImpl(new BoundImpl(50d, 50d), new BoundImpl(200d, 200d));

        Bounds childBounds = new BoundsImpl(new BoundImpl(51d, 51d), new BoundImpl(199d, 199d));
        assertTrue(GraphUtils.checkBoundsExceeded(parentBounds, childBounds));

        childBounds = new BoundsImpl(new BoundImpl(51d, 51d), new BoundImpl(200d, 200d));
        assertTrue(GraphUtils.checkBoundsExceeded(parentBounds, childBounds));

        childBounds = new BoundsImpl(new BoundImpl(50d, 50d), new BoundImpl(199d, 199d));
        assertTrue(GraphUtils.checkBoundsExceeded(parentBounds, childBounds));

        childBounds = new BoundsImpl(new BoundImpl(50d, 50d), new BoundImpl(200d, 200d));
        assertTrue(GraphUtils.checkBoundsExceeded(parentBounds, childBounds));

        childBounds = new BoundsImpl(new BoundImpl(49d, 49d), new BoundImpl(201d, 201d));
        assertFalse(GraphUtils.checkBoundsExceeded(parentBounds, childBounds));

        childBounds = new BoundsImpl(new BoundImpl(49d, 49d), new BoundImpl(200d, 200d));
        assertFalse(GraphUtils.checkBoundsExceeded(parentBounds, childBounds));

        childBounds = new BoundsImpl(new BoundImpl(49d, 49d), new BoundImpl(199d, 199d));
        assertFalse(GraphUtils.checkBoundsExceeded(parentBounds, childBounds));

        childBounds = new BoundsImpl(new BoundImpl(49d, 49d), new BoundImpl(201d, 201d));
        assertFalse(GraphUtils.checkBoundsExceeded(parentBounds, childBounds));

        childBounds = new BoundsImpl(new BoundImpl(50d, 49d), new BoundImpl(201d, 201d));
        assertFalse(GraphUtils.checkBoundsExceeded(parentBounds, childBounds));

        childBounds = new BoundsImpl(new BoundImpl(51d, 49d), new BoundImpl(201d, 201d));
        assertFalse(GraphUtils.checkBoundsExceeded(parentBounds, childBounds));
    }

    @Test
    public void isDockedNodeTest(){
        assertTrue(GraphUtils.isDockedNode(graphInstance.dockedNode));
        assertFalse(GraphUtils.isDockedNode(graphInstance.startNode));
        assertFalse(GraphUtils.isDockedNode(graphInstance.intermNode));
        assertFalse(GraphUtils.isDockedNode(graphInstance.endNode));
    }

    @Test
    public void getDockedNodesTest(){
        List<Node> dockedNodes = GraphUtils.getDockedNodes(graphInstance.intermNode);
        assertEquals(dockedNodes.size(), 1);
        assertEquals(dockedNodes.get(0), graphInstance.dockedNode);
    }
}
