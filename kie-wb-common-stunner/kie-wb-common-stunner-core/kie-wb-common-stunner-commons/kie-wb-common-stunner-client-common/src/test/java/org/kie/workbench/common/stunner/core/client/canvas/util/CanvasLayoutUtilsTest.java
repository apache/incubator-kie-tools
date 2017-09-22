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

package org.kie.workbench.common.stunner.core.client.canvas.util;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.TestingGraphInstanceBuilder;
import org.kie.workbench.common.stunner.core.TestingGraphMockHandler;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasHandler;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.definition.DefinitionSet;
import org.kie.workbench.common.stunner.core.graph.content.view.BoundImpl;
import org.kie.workbench.common.stunner.core.graph.content.view.BoundsImpl;
import org.kie.workbench.common.stunner.core.graph.content.view.Point2D;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.util.GraphUtils;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CanvasLayoutUtilsTest {

    private final static double NEW_NODE_WIDTH = 100;
    private final static double NEW_NODE_HEIGHT = 100;
    private Point2D canvasMin;
    private Point2D canvasMax;
    private Point2D offset;

    @Mock
    private Diagram diagram;
    @Mock
    private Element parentCanvasRoot;
    @Mock
    private Element parentNotCanvasRoot;
    @Mock
    private DefinitionSet definitionSet;
    @Mock
    private Metadata metadata;
    @Mock
    private CanvasHandler canvasHandler;
    @Mock
    private Graph<DefinitionSet, Node> graph;
    @Mock
    private Bounds canvasBounds;
    @Mock
    private Bounds.Bound minCanvasBound;
    @Mock
    private Bounds.Bound maxCanvasBound;
    @Mock
    private Node<DefinitionSet, ?> nodeRoot;
    private CanvasLayoutUtils canvasLayoutUtils;
    private TestingGraphMockHandler graphTestHandler;
    private TestingGraphInstanceBuilder.TestGraph2 graphInstance;

    @Before
    public void setup() throws Exception {
        when(diagram.getMetadata()).thenReturn(metadata);
        when(canvasHandler.getDiagram()).thenReturn(diagram);
        when(canvasHandler.getDiagram().getGraph()).thenReturn(graph);
        when(canvasHandler.getDiagram().getGraph().getNode("canvas_root")).thenReturn(nodeRoot);
        when(graph.getContent()).thenReturn(definitionSet);
        when(graph.getContent().getBounds()).thenReturn(canvasBounds);
        when(canvasHandler.getDiagram().getGraph().getNode("canvas_root")).thenReturn(nodeRoot);
        when(parentCanvasRoot.getUUID()).thenReturn("canvas_root");
        when(parentNotCanvasRoot.getUUID()).thenReturn("canvas_not_root");
        when(canvasBounds.getUpperLeft()).thenReturn(minCanvasBound);
        when(canvasBounds.getLowerRight()).thenReturn(maxCanvasBound);
        canvasMin = new Point2D(0d,
                                0d);
        when(minCanvasBound.getX()).thenReturn(canvasMin.getX());
        when(minCanvasBound.getY()).thenReturn(canvasMin.getY());
        canvasMax = new Point2D(1200d,
                                1200d);
        when(maxCanvasBound.getX()).thenReturn(canvasMax.getX());
        when(maxCanvasBound.getY()).thenReturn(canvasMax.getY());
        offset = new Point2D(100d,
                             100d);
        canvasLayoutUtils = new CanvasLayoutUtils();
    }

    @Test
    public void isCanvasRootTrueTest() {
        when(diagram.getMetadata().getCanvasRootUUID()).thenReturn("canvas_root");
        boolean isCanvasRoot = canvasLayoutUtils.isCanvasRoot(diagram,
                                                              parentCanvasRoot);
        assertTrue(isCanvasRoot);
    }

    @Test
    public void isCanvasRootFalseTest() {
        when(diagram.getMetadata().getCanvasRootUUID()).thenReturn("test");
        boolean isCanvasRoot = canvasLayoutUtils.isCanvasRoot(diagram,
                                                              parentNotCanvasRoot);
        assertFalse(isCanvasRoot);
    }

    @Test
    public void isCanvasRootWithUuidTrueTest() {
        when(diagram.getMetadata().getCanvasRootUUID()).thenReturn("canvas_root");
        boolean isCanvasRoot = canvasLayoutUtils.isCanvasRoot(diagram,
                                                              "canvas_root");
        assertTrue(isCanvasRoot);
    }

    @Test
    public void isCanvasRootWithUuidFalseTest() {
        when(diagram.getMetadata().getCanvasRootUUID()).thenReturn("test");
        boolean isCanvasRoot = canvasLayoutUtils.isCanvasRoot(diagram,
                                                              "canvas_root");
        assertFalse(isCanvasRoot);
    }

    @Test
    public void getNextWidthHeightTest() {
        Node node = mock(Node.class);
        Bounds bounds = new BoundsImpl(new BoundImpl(100d,
                                                     100d),
                                       new BoundImpl(300d,
                                                     200d));
        List<Node> nodes = new ArrayList<Node>();
        View view = mock(View.class);
        when(node.getContent()).thenReturn(view);
        when(view.getBounds()).thenReturn(bounds);
        nodes.add(node);
        when(graph.nodes()).thenReturn(nodes);
        double[] next = canvasLayoutUtils.getNext(canvasHandler,
                                                  NEW_NODE_HEIGHT);
        double x = next[0];
        double y = next[1];
        assertTrue(x > NEW_NODE_WIDTH);
        assertTrue(y > NEW_NODE_HEIGHT);
    }

    @Test
    public void getNextOffsetTest() {
        Node node = mock(Node.class);
        Bounds bounds = new BoundsImpl(new BoundImpl(100d,
                                                     100d),
                                       new BoundImpl(300d,
                                                     200d));
        List<Node> nodes = new ArrayList<Node>();
        View view = mock(View.class);
        when(node.getContent()).thenReturn(view);
        when(view.getBounds()).thenReturn(bounds);
        nodes.add(node);
        when(graph.nodes()).thenReturn(nodes);

        double[] next = canvasLayoutUtils.getNext(canvasHandler,
                                                  NEW_NODE_HEIGHT,
                                                  offset
        );
        double nextX = next[0];
        double nextY = next[1];
        assertTrue(nextX > (NEW_NODE_WIDTH + offset.getX()));
        assertTrue(nextY > (NEW_NODE_HEIGHT + offset.getY()));
    }

    @Test
    public void getNextOffsetAndMin() {
        Node node = mock(Node.class);
        Bounds bounds = new BoundsImpl(new BoundImpl(100d,
                                                     100d),
                                       new BoundImpl(300d,
                                                     200d));
        List<Node> nodes = new ArrayList<Node>();
        View view = mock(View.class);
        when(node.getContent()).thenReturn(view);
        when(view.getBounds()).thenReturn(bounds);
        nodes.add(node);
        when(graph.nodes()).thenReturn(nodes);
        final Point2D min = new Point2D(canvasMin.getX(),
                                        canvasMin.getY());
        double[] next = canvasLayoutUtils.getNext(canvasHandler,
                                                  NEW_NODE_HEIGHT,
                                                  offset,
                                                  min);
        double nextX = next[0];
        double nextY = next[1];
        assertTrue(nextX > (NEW_NODE_WIDTH + offset.getX()));
        assertTrue(nextY > (NEW_NODE_HEIGHT + offset.getY()));
    }

    @Test
    public void getNextFromRoot() {
        Node node1 = mock(Node.class);
        Bounds boundsNode1 = new BoundsImpl(new BoundImpl(100d,
                                                          100d),
                                            new BoundImpl(300d,
                                                          200d));
        View viewNode1 = mock(View.class);
        when(node1.getContent()).thenReturn(viewNode1);
        when(viewNode1.getBounds()).thenReturn(boundsNode1);
        Node node2 = mock(Node.class);
        Bounds boundsNode2 = new BoundsImpl(new BoundImpl(100d,
                                                          100d),
                                            new BoundImpl(300d,
                                                          200d));
        View viewNode2 = mock(View.class);
        when(node2.getContent()).thenReturn(viewNode2);
        when(viewNode2.getBounds()).thenReturn(boundsNode2);
        Node nodeRoot = mock(Node.class);
        double rootWidth = 40d;
        double rootHeight = 40d;
        Bounds rootBounds = new BoundsImpl(new BoundImpl(0d,
                                                         0d),
                                           new BoundImpl(rootWidth,
                                                         rootHeight));

        View rootView = mock(View.class);
        when(nodeRoot.getContent()).thenReturn(rootView);
        when(rootView.getBounds()).thenReturn(rootBounds);
        List<Node> nodes = new ArrayList<Node>();
        List<Edge> edges = new ArrayList<Edge>();
        Edge edge = mock(Edge.class);
        edges.add(edge);
        when(nodeRoot.getOutEdges()).thenReturn(edges);
        when(edge.getTargetNode()).thenReturn(node1);
        when(nodeRoot.getContent()).thenReturn(rootView);
        nodes.add(nodeRoot);
        when(graph.nodes()).thenReturn(nodes);
        double[] next = canvasLayoutUtils.getNext(canvasHandler,
                                                  nodeRoot,
                                                  node2);
        double nextX = next[0];
        double nextY = next[1];
        assertTrue(nextX > rootWidth);
        assertTrue(nextY > NEW_NODE_HEIGHT);
    }

    @Test
    public void getNextFromRootWithParent() {
        this.graphTestHandler = new TestingGraphMockHandler();
        graphInstance = TestingGraphInstanceBuilder.newGraph2(graphTestHandler);
        Node node = mock(Node.class);
        Bounds boundsNode = new BoundsImpl(new BoundImpl(100d,
                                                         100d),
                                           new BoundImpl(300d,
                                                         200d));
        View viewNode = mock(View.class);
        when(node.getContent()).thenReturn(viewNode);
        when(viewNode.getBounds()).thenReturn(boundsNode);
        when(canvasHandler.getDiagram().getGraph()).thenReturn(graphInstance.graph);
        double[] next = canvasLayoutUtils.getNext(canvasHandler,
                                                  graphInstance.startNode,
                                                  node);
        Node<View<?>, Edge> start = (Node<View<?>, Edge>) graphInstance.startNode;
        final double[] size = GraphUtils.getNodeSize((View) start.getContent());
        double nextX = next[0];
        double nextY = next[1];
        assertTrue(nextX > size[0]);
        assertTrue(nextY > size[1]);
    }
}
