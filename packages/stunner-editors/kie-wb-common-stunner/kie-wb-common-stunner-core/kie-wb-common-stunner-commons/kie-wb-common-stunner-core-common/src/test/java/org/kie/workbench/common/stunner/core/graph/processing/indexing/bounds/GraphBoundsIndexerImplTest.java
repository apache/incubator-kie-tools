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


package org.kie.workbench.common.stunner.core.graph.processing.indexing.bounds;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.TestingGraphInstanceBuilder;
import org.kie.workbench.common.stunner.core.TestingGraphMockHandler;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.Point2D;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.processing.index.bounds.GraphBoundsIndexerImpl;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.content.ChildrenTraverseProcessor;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.content.ChildrenTraverseProcessorImpl;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.tree.TreeWalkTraverseProcessorImpl;
import org.kie.workbench.common.stunner.core.graph.util.GraphUtils;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

@RunWith(MockitoJUnitRunner.Silent.class)
public class GraphBoundsIndexerImplTest {

    private TestingGraphMockHandler graphTestHandlerParent;
    private TestingGraphInstanceBuilder.TestGraph2 graphInstanceParent;

    private GraphBoundsIndexerImpl graphBoundsIndexerImpl;

    @Before
    public void setup() {
        this.graphTestHandlerParent = new TestingGraphMockHandler();
        graphInstanceParent = TestingGraphInstanceBuilder.newGraph2(graphTestHandlerParent);

        ChildrenTraverseProcessor childrenTraverseProcessor = new ChildrenTraverseProcessorImpl(new TreeWalkTraverseProcessorImpl());
        graphBoundsIndexerImpl = new GraphBoundsIndexerImpl(childrenTraverseProcessor);
        graphBoundsIndexerImpl.build(graphInstanceParent.graph);
    }

    @Test
    public void testGetAt() {

        Point2D position = GraphUtils.getPosition((View) graphInstanceParent.startNode.getContent());
        double[] size = GraphUtils.getNodeSize((View) graphInstanceParent.startNode.getContent());
        double getAtX = position.getX() + (size[0] / 2);
        double getAtY = position.getY() + (size[1] / 2);
        Node<View<?>, Edge> node = graphBoundsIndexerImpl.getAt(getAtX,
                                                                getAtY);
        assertNotNull(node);
    }

    @Test
    public void testGetAreaAt() {

        Point2D position = GraphUtils.getPosition((View) graphInstanceParent.startNode.getContent());
        double[] size = GraphUtils.getNodeSize((View) graphInstanceParent.startNode.getContent());
        double getAtX = position.getX() + (size[0] / 2);
        double getAtY = position.getY() + (size[1] / 2);

        Node<View<?>, Edge> node = graphBoundsIndexerImpl.getAt(getAtX,
                                                                getAtY,
                                                                size[0],
                                                                size[1],
                                                                graphInstanceParent.parentNode);
        assertNotNull(node);
        Node<View<?>, Edge> nodeFreePosition = graphBoundsIndexerImpl.getAt(getAtX,
                                                                            getAtY + 400,
                                                                            size[0],
                                                                            size[1],
                                                                            graphInstanceParent.parentNode);
        assertNull(nodeFreePosition);
    }

    @Test
    public void testGetAreaAtWithParent() {
        Point2D position = GraphUtils.getPosition((View) graphInstanceParent.startNode.getContent());
        double[] size = GraphUtils.getNodeSize((View) graphInstanceParent.startNode.getContent());
        double getAtX = position.getX() + (size[0] / 2);
        double getAtY = position.getY() + (size[1] / 2);
        Node<View<?>, Edge> node = graphBoundsIndexerImpl.getAt(getAtX,
                                                                getAtY,
                                                                size[0],
                                                                size[1],
                                                                graphInstanceParent.parentNode);

        assertNotNull(node);
        Node<View<?>, Edge> nodeAtFreePosition = graphBoundsIndexerImpl.getAt(getAtX,
                                                                              getAtY + 200,
                                                                              size[0],
                                                                              size[1],
                                                                              graphInstanceParent.parentNode);
        assertNull(nodeAtFreePosition);
    }

    @Test
    public void testGetTrimmedBounds() {
        Point2D position = GraphUtils.getPosition((View) graphInstanceParent.startNode.getContent());
        double[] size = GraphUtils.getNodeSize((View) graphInstanceParent.startNode.getContent());
        double[] trimmedBounds = graphBoundsIndexerImpl.getTrimmedBounds();
        assertEquals(trimmedBounds[0],
                     position.getX(),
                     0.001);
        assertEquals(trimmedBounds[1],
                     position.getY(),
                     0.001);
        assertEquals(trimmedBounds[2],
                     size[0],
                     0.001);
        assertEquals(trimmedBounds[3],
                     size[1],
                     0.001);
    }
}