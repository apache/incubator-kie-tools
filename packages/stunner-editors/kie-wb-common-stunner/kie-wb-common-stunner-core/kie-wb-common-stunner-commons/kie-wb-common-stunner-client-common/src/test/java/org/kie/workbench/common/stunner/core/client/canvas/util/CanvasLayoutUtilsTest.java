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


package org.kie.workbench.common.stunner.core.client.canvas.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.TestingGraphInstanceBuilder;
import org.kie.workbench.common.stunner.core.TestingGraphMockHandler;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.definition.adapter.AdapterManager;
import org.kie.workbench.common.stunner.core.definition.adapter.DefinitionSetRuleAdapter;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.Bound;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.graph.content.definition.DefinitionSet;
import org.kie.workbench.common.stunner.core.graph.content.view.Point2D;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewConnector;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewConnectorImpl;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewImpl;
import org.kie.workbench.common.stunner.core.graph.impl.EdgeImpl;
import org.kie.workbench.common.stunner.core.graph.impl.NodeImpl;
import org.kie.workbench.common.stunner.core.graph.processing.index.Index;
import org.kie.workbench.common.stunner.core.graph.processing.index.bounds.GraphBoundsIndexer;
import org.kie.workbench.common.stunner.core.graph.util.GraphUtils;
import org.kie.workbench.common.stunner.core.registry.definition.TypeDefinitionSetRegistry;
import org.kie.workbench.common.stunner.core.rule.RuleEvaluationContext;
import org.kie.workbench.common.stunner.core.rule.RuleManager;
import org.kie.workbench.common.stunner.core.rule.RuleSet;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.kie.workbench.common.stunner.core.rule.RuleViolations;
import org.kie.workbench.common.stunner.core.validation.Violation;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.kie.workbench.common.stunner.core.client.canvas.util.CanvasLayoutUtils.DEFAULT_NEW_NODE_ORIENTATION;
import static org.kie.workbench.common.stunner.core.client.canvas.util.CanvasLayoutUtils.Orientation;
import static org.kie.workbench.common.stunner.core.client.canvas.util.CanvasLayoutUtils.PADDING_X;
import static org.kie.workbench.common.stunner.core.client.canvas.util.CanvasLayoutUtils.PADDING_Y;
import static org.kie.workbench.common.stunner.core.client.canvas.util.CanvasLayoutUtils.getElement;
import static org.kie.workbench.common.stunner.core.client.canvas.util.CanvasLayoutUtils.getPaddingX;
import static org.kie.workbench.common.stunner.core.client.canvas.util.CanvasLayoutUtils.getPaddingY;
import static org.kie.workbench.common.stunner.core.client.canvas.util.CanvasLayoutUtils.isCanvasRoot;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class CanvasLayoutUtilsTest {

    private final static String NODE_UUID = "uuid";

    private final static double NEW_NODE_HEIGHT = 100;

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
    private AbstractCanvasHandler canvasHandler;

    @Mock
    private AbstractCanvasHandler abstractCanvasHandler;

    @Mock
    private Graph<DefinitionSet, Node> graph;

    @Mock
    private Index graphIndex;

    @Mock
    private AbstractCanvas canvas;

    @Mock
    private GraphBoundsIndexer graphBoundsIndexer;

    @Mock
    private RuleManager ruleManager;

    @Mock
    private RuleSet ruleSet;

    @Mock
    private TypeDefinitionSetRegistry typeDefinitionSetRegistry;

    @Mock
    private Object defSet;

    @Mock
    private AdapterManager adapterManager;

    @Mock
    private Iterable<RuleViolation> ruleViolationIterable;

    @Mock
    private Iterator ruleViolationIterator;

    @Mock
    private DefinitionSetRuleAdapter definitionSetRuleAdapter;

    @Mock
    private RuleViolations ruleViolations;

    @Mock
    private DefinitionManager definitionManager;

    @Mock
    private Node<DefinitionSet, ?> nodeRoot;

    private CanvasLayoutUtils canvasLayoutUtils;
    private TestingGraphMockHandler graphTestHandler;
    private TestingGraphInstanceBuilder.TestGraph1 graphInstance;

    private TestingGraphMockHandler graphTestHandlerParent;
    private TestingGraphInstanceBuilder.TestGraph2 graphInstanceParent;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() throws Exception {
        when(diagram.getMetadata()).thenReturn(metadata);
        when(canvasHandler.getDiagram()).thenReturn(diagram);
        when(canvasHandler.getDiagram().getGraph()).thenReturn(graph);
        when(canvasHandler.getDiagram().getGraph().getNode("canvas_root")).thenReturn(nodeRoot);

        when(graph.getContent()).thenReturn(definitionSet);

        when(canvasHandler.getDiagram().getGraph().getNode("canvas_root")).thenReturn(nodeRoot);
        when(parentCanvasRoot.getUUID()).thenReturn("canvas_root");
        when(parentNotCanvasRoot.getUUID()).thenReturn("canvas_not_root");

        when(canvasHandler.getCanvas()).thenReturn(canvas);

        Point2D canvasMax = new Point2D(1200d,
                                        1200d);
        when(canvasHandler.getCanvas().getHeightPx()).thenReturn((int) canvasMax.getY());
        when(canvasHandler.getCanvas().getWidthPx()).thenReturn((int) canvasMax.getX());

        canvasLayoutUtils = spy(new CanvasLayoutUtils(graphBoundsIndexer));

        when(metadata.getDefinitionSetId()).thenReturn("definitionSetId");
        when(definitionManager.definitionSets()).thenReturn(typeDefinitionSetRegistry);

        when(typeDefinitionSetRegistry.getDefinitionSetById(eq("definitionSetId"))).thenReturn(defSet);
        when(definitionManager.adapters()).thenReturn(adapterManager);
        when(adapterManager.forRules()).thenReturn(definitionSetRuleAdapter);

        when(definitionSetRuleAdapter.getRuleSet(defSet)).thenReturn(ruleSet);
    }

    @Test
    public void isCanvasRootTrueTest() {
        when(diagram.getMetadata().getCanvasRootUUID()).thenReturn("canvas_root");
        boolean isCanvasRoot = isCanvasRoot(diagram,
                                            parentCanvasRoot);
        assertTrue(isCanvasRoot);
    }

    @Test
    public void isCanvasRootFalseTest() {
        when(diagram.getMetadata().getCanvasRootUUID()).thenReturn("test");
        boolean isCanvasRoot = isCanvasRoot(diagram,
                                            parentNotCanvasRoot);
        assertFalse(isCanvasRoot);
    }

    @Test
    public void isCanvasRootWithUuidTrueTest() {
        when(diagram.getMetadata().getCanvasRootUUID()).thenReturn("canvas_root");
        boolean isCanvasRoot = isCanvasRoot(diagram,
                                            "canvas_root");
        assertTrue(isCanvasRoot);
    }

    @Test
    public void isCanvasRootWithUuidFalseTest() {
        when(diagram.getMetadata().getCanvasRootUUID()).thenReturn("test");
        boolean isCanvasRoot = isCanvasRoot(diagram,
                                            "canvas_root");
        assertFalse(isCanvasRoot);
    }

    // TODO (AlessioP & Roger):
    @Test
    @Ignore
    @SuppressWarnings("unchecked")
    public void getNextFromRoot() {
        Node node1 = mock(Node.class);
        Bounds boundsNode1 = Bounds.create(100d, 100d, 300d, 200d);
        View viewNode1 = mock(View.class);
        when(node1.getContent()).thenReturn(viewNode1);
        when(viewNode1.getBounds()).thenReturn(boundsNode1);
        Node node2 = mock(Node.class);
        Bounds boundsNode2 = Bounds.create(100d, 100d, 300d, 200d);
        View viewNode2 = mock(View.class);
        when(node2.getContent()).thenReturn(viewNode2);
        when(viewNode2.getBounds()).thenReturn(boundsNode2);
        Node nodeRoot = mock(Node.class);
        double rootWidth = 40d;
        double rootHeight = 40d;
        Bounds rootBounds = Bounds.create(0d, 0d, rootWidth, rootHeight);

        View rootView = mock(View.class);
        when(nodeRoot.getContent()).thenReturn(rootView);
        when(rootView.getBounds()).thenReturn(rootBounds);
        List<Node> nodes = new ArrayList<>();
        List<Edge> edges = new ArrayList<>();

        Edge edge = mock(Edge.class);
        edges.add(edge);
        when(nodeRoot.getOutEdges()).thenReturn(edges);
        when(edge.getTargetNode()).thenReturn(node1);
        when(nodeRoot.getContent()).thenReturn(rootView);
        nodes.add(nodeRoot);
        when(graph.nodes()).thenReturn(nodes);
        when(node2.getInEdges()).thenReturn(edges);
        when(nodeRoot.asNode()).thenReturn(nodeRoot);

        Point2D next = canvasLayoutUtils.getNext(canvasHandler,
                                                 nodeRoot,
                                                 node2,
                                                 DEFAULT_NEW_NODE_ORIENTATION);

        assertTrue(next.getX() > rootWidth);
        assertTrue(next.getY() > NEW_NODE_HEIGHT);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void getNextFromRootWithParent() {
        this.graphTestHandlerParent = new TestingGraphMockHandler();
        graphInstanceParent = TestingGraphInstanceBuilder.newGraph2(graphTestHandlerParent);
        Node node = mock(Node.class);
        Bounds boundsNode = Bounds.create(100d, 100d, 300d, 200d);
        View viewNode = mock(View.class);
        when(node.getContent()).thenReturn(viewNode);
        when(viewNode.getBounds()).thenReturn(boundsNode);
        when(canvasHandler.getDiagram().getGraph()).thenReturn(graphInstanceParent.graph);

        Point2D next = canvasLayoutUtils.getNext(canvasHandler,
                                                 graphInstanceParent.startNode,
                                                 node,
                                                 DEFAULT_NEW_NODE_ORIENTATION);

        Node<View<?>, Edge> start = (Node<View<?>, Edge>) graphInstanceParent.startNode;
        double[] size = GraphUtils.getNodeSize(start.getContent());
        assertTrue(next.getX() == getPaddingX());
        assertTrue(next.getY() > size[1]);
    }

    // TODO (AlessioP & Roger):
    @Test
    @Ignore
    @SuppressWarnings("unchecked")
    public void getNextOutOfCanvas() {
        when(ruleManager.evaluate(eq(ruleSet),
                                  Mockito.<RuleEvaluationContext>any())).thenReturn(ruleViolations);

        when(ruleViolations.violations(Violation.Type.ERROR)).thenReturn(ruleViolationIterable);
        when(ruleViolations.violations(Violation.Type.ERROR).iterator()).thenReturn(ruleViolationIterator);
        when(ruleViolations.violations(Violation.Type.ERROR).iterator().hasNext()).thenReturn(true);

        this.graphTestHandler = new TestingGraphMockHandler();
        graphInstance = TestingGraphInstanceBuilder.newGraph1(graphTestHandler);

        Node node = mock(Node.class);

        Bounds boundsNode = Bounds.create(100d, 0d, 300d, 1400d);
        View viewNode = mock(View.class);
        when(node.getContent()).thenReturn(viewNode);
        when(viewNode.getBounds()).thenReturn(boundsNode);

        Node newNode = mock(Node.class);

        Bounds boundsNewNode = Bounds.create(100d, 200d, 300d, 300d);
        View viewNewNode = mock(View.class);

        when(newNode.getContent()).thenReturn(viewNewNode);
        when(viewNewNode.getBounds()).thenReturn(boundsNewNode);
        when(canvasHandler.getDiagram().getGraph()).thenReturn(graphInstance.graph);

        when(graphBoundsIndexer.getAt(140.0,
                                      0.0,
                                      200.0,
                                      100.0,
                                      null)).thenReturn(node);

        graphInstance.startNode.getOutEdges().clear();

        Point2D next = canvasLayoutUtils.getNext(canvasHandler,
                                                 graphInstance.startNode,
                                                 newNode,
                                                 DEFAULT_NEW_NODE_ORIENTATION);

        Node<View<?>, Edge> intermNode = (Node<View<?>, Edge>) graphInstance.intermNode;
        double[] size = GraphUtils.getNodeSize(intermNode.getContent());

        assertTrue(next.getX() > size[0]);
        assertTrue(next.getY() == getPaddingY());
    }

    // TODO (AlessioP & Roger):
    @Test
    @Ignore
    @SuppressWarnings("unchecked")
    public void getNextFromNewTaskWithNonEmptyPositionWithParent() {
        when(ruleManager.evaluate(eq(ruleSet),
                                  Mockito.<RuleEvaluationContext>any())).thenReturn(ruleViolations);

        when(ruleViolations.violations(Violation.Type.ERROR)).thenReturn(ruleViolationIterable);
        when(ruleViolations.violations(Violation.Type.ERROR).iterator()).thenReturn(ruleViolationIterator);
        when(ruleViolations.violations(Violation.Type.ERROR).iterator().hasNext()).thenReturn(false);
        this.graphTestHandlerParent = new TestingGraphMockHandler();
        graphInstanceParent = TestingGraphInstanceBuilder.newGraph2(graphTestHandlerParent);

        Node newNode = mock(Node.class);

        Bounds boundsNewNode = Bounds.create(100d, 200d, 300d, 300d);
        View viewNewNode = mock(View.class);

        when(newNode.getContent()).thenReturn(viewNewNode);
        when(viewNewNode.getBounds()).thenReturn(boundsNewNode);
        when(canvasHandler.getDiagram().getGraph()).thenReturn(graphInstanceParent.graph);
        when(graphBoundsIndexer.getAt(280.0,
                                      100.0,
                                      100.0,
                                      100.0,
                                      graphInstanceParent.parentNode)).thenReturn(graphInstanceParent.intermNode);

        graphInstanceParent.startNode.getOutEdges().clear();

        Point2D next = canvasLayoutUtils.getNext(canvasHandler,
                                                 graphInstanceParent.startNode,
                                                 newNode,
                                                 DEFAULT_NEW_NODE_ORIENTATION);
        Node<View<?>, Edge> intermNode = (Node<View<?>, Edge>) graphInstanceParent.intermNode;
        double[] size = GraphUtils.getNodeSize(intermNode.getContent());
        assertTrue(next.getX() == getPaddingX());
        assertTrue(next.getY() > size[1]);
    }

    // TODO (AlessioP & Roger):
    @Test
    @Ignore
    @SuppressWarnings("unchecked")
    public void getNextNewTaskWithNonEmptyPosition() {

        when(ruleManager.evaluate(eq(ruleSet),
                                  Mockito.<RuleEvaluationContext>any())).thenReturn(ruleViolations);

        when(ruleViolations.violations(Violation.Type.ERROR)).thenReturn(ruleViolationIterable);
        when(ruleViolations.violations(Violation.Type.ERROR).iterator()).thenReturn(ruleViolationIterator);
        when(ruleViolations.violations(Violation.Type.ERROR).iterator().hasNext()).thenReturn(true);

        this.graphTestHandler = new TestingGraphMockHandler();
        graphInstance = TestingGraphInstanceBuilder.newGraph1(graphTestHandler);

        Node newNode = mock(Node.class);

        Bounds boundsNewNode = Bounds.create(200d, 300d, 300d, 400d);
        View viewNewNode = mock(View.class);

        when(newNode.getContent()).thenReturn(viewNewNode);
        when(viewNewNode.getBounds()).thenReturn(boundsNewNode);

        when(canvasHandler.getDiagram().getGraph()).thenReturn(graphInstance.graph);

        when(graphBoundsIndexer.getAt(140.0,
                                      0.0,
                                      100.0,
                                      100.0,
                                      null)).thenReturn(graphInstance.intermNode);

        graphInstance.startNode.getOutEdges().clear();

        Point2D next = canvasLayoutUtils.getNext(canvasHandler,
                                                 graphInstance.startNode,
                                                 newNode,
                                                 DEFAULT_NEW_NODE_ORIENTATION);

        Node<View<?>, Edge> startNode = (Node<View<?>, Edge>) graphInstance.startNode;
        double[] sizeStartNode = GraphUtils.getNodeSize(startNode.getContent());
        Node<View<?>, Edge> intermNode = (Node<View<?>, Edge>) graphInstance.intermNode;
        double[] sizeIntermNode = GraphUtils.getNodeSize(intermNode.getContent());
        assertTrue(next.getX() == sizeStartNode[0] + getPaddingX());
        assertTrue(next.getY() > sizeIntermNode[1]);
    }

    @Test
    public void testGetElementNullCanvasHandlerAndNullUUID() {
        when(abstractCanvasHandler.getGraphIndex()).thenReturn(graphIndex);

        assertNull(getElement(null, null));
    }

    @Test
    public void testGetElementNullCanvasHandler() {
        when(abstractCanvasHandler.getGraphIndex()).thenReturn(graphIndex);

        assertNull(getElement(null, NODE_UUID));
    }

    @Test
    public void testGetElementNullUUID() {
        when(abstractCanvasHandler.getGraphIndex()).thenReturn(graphIndex);

        assertNull(getElement(abstractCanvasHandler, null));
    }

    @Test
    public void testGetElementWithNullIndex() {
        assertNull(getElement(abstractCanvasHandler, NODE_UUID));
    }

    @Test
    public void testGetElement() {
        when(abstractCanvasHandler.getGraphIndex()).thenReturn(graphIndex);
        when(graphIndex.get(NODE_UUID)).thenReturn(nodeRoot);

        assertEquals(nodeRoot, getElement(abstractCanvasHandler, NODE_UUID));
    }

    @Test
    public void testDefaultNewNodeOrientation() {
        assertEquals(Orientation.RightBottom, DEFAULT_NEW_NODE_ORIENTATION);
    }

    @Test
    public void testCalculateOffsetForMultipleEdges_UpRight_WhenNodePositionIsNotBeyondMaxPosition() {

        final double xMaxPosition = 10.0d;
        final double yMaxPosition = 10.0d;
        final double xNodePosition = 5.0d;
        final double yNodePosition = 5.0d;
        final Node<View<?>, Edge> node = new NodeImpl("uuid");
        final Point2D offset = new Point2D(0, 0);
        final Point2D maxNodePosition = new Point2D(xMaxPosition, yMaxPosition);
        final Point2D nodePos = new Point2D(xNodePosition, yNodePosition);
        final Point2D rootPos = new Point2D(0, 0);

        node.setContent(new ViewImpl<>(new EdgeImpl("uuid"), Bounds.createEmpty()));

        canvasLayoutUtils.setOrientation(Orientation.UpRight);

        canvasLayoutUtils.calculateOffsetForMultipleEdges(offset,
                                                          maxNodePosition,
                                                          node,
                                                          nodePos,
                                                          rootPos);

        assertEquals(0.0d, offset.getX(), 0.01d);
        assertEquals(0.0d, offset.getY(), 0.01d);

        assertEquals(xMaxPosition, maxNodePosition.getX(), 0.01d);
        assertEquals(yMaxPosition, maxNodePosition.getY(), 0.01d);
    }

    @Test
    public void testCalculateOffsetForMultipleEdges_UpRight_WhenNodePositionIsBeyondMaxPosition() {

        final double xMaxPosition = 10.0d;
        final double yMaxPosition = 10.0d;
        final double xNodePosition = 12.0d;
        final double yNodePosition = 5.0d;
        final double xRootPosition = 1.0d;
        final double yRootPosition = 1.0d;
        final Node<View<?>, Edge> node = new NodeImpl("uuid");
        final Point2D offset = new Point2D(0, 0);
        final Point2D maxNodePosition = new Point2D(xMaxPosition, yMaxPosition);
        final Point2D nodePos = new Point2D(xNodePosition, yNodePosition);
        final Point2D rootPos = new Point2D(xRootPosition, yRootPosition);

        node.setContent(new ViewImpl<>(new EdgeImpl("uuid"), Bounds.createEmpty()));

        canvasLayoutUtils.setOrientation(Orientation.UpRight);

        canvasLayoutUtils.calculateOffsetForMultipleEdges(offset,
                                                          maxNodePosition,
                                                          node,
                                                          nodePos,
                                                          rootPos);

        assertEquals(xNodePosition - xRootPosition, offset.getX(), 0.01d);
        assertEquals(0.0d, offset.getY(), 0.01d);

        assertEquals(xNodePosition, maxNodePosition.getX(), 0.01d);
        assertEquals(yMaxPosition, maxNodePosition.getY(), 0.01d);
    }

    @Test
    public void testCalculateOffsetForMultipleEdges_RightBottom_WhenNodePositionIsNotBeyondMaxPosition() {

        final double xMaxPosition = 9.0d;
        final double yMaxPosition = 8.0d;
        final double xNodePosition = 15.0d;
        final double yNodePosition = 5.0d;
        final Node<View<?>, Edge> node = new NodeImpl("uuid");
        final Point2D offset = new Point2D(0, 0);
        final Point2D maxNodePosition = new Point2D(xMaxPosition, yMaxPosition);
        final Point2D nodePos = new Point2D(xNodePosition, yNodePosition);
        final Point2D rootPos = new Point2D(0, 0);

        node.setContent(new ViewImpl<>(new EdgeImpl("uuid"), Bounds.createEmpty()));

        canvasLayoutUtils.setOrientation(Orientation.RightBottom);

        canvasLayoutUtils.calculateOffsetForMultipleEdges(offset,
                                                          maxNodePosition,
                                                          node,
                                                          nodePos,
                                                          rootPos);

        assertEquals(0.0d, offset.getX(), 0.01d);
        assertEquals(0.0d, offset.getY(), 0.01d);

        assertEquals(xMaxPosition, maxNodePosition.getX(), 0.01d);
        assertEquals(yMaxPosition, maxNodePosition.getY(), 0.01d);
    }

    @Test
    public void testCalculateOffsetForMultipleEdges_RightBottom_WhenNodePositionIsBeyondMaxPosition() {

        final double xMaxPosition = 10.0d;
        final double yMaxPosition = 10.0d;
        final double xNodePosition = 12.0d;
        final double yNodePosition = 15.0d;
        final double xRootPosition = 1.0d;
        final double yRootPosition = 1.0d;
        final Node<View<?>, Edge> node = new NodeImpl("uuid");
        final Point2D offset = new Point2D(0, 0);
        final Point2D maxNodePosition = new Point2D(xMaxPosition, yMaxPosition);
        final Point2D nodePos = new Point2D(xNodePosition, yNodePosition);
        final Point2D rootPos = new Point2D(xRootPosition, yRootPosition);
        final double nodeSizeHeight = 50.0d;
        final double nodeSizeWidth = 100.0d;
        final Bounds bounds = Bounds.create();
        bounds.setUpperLeft(Bound.create(0, 0));
        bounds.setLowerRight(Bound.create(nodeSizeWidth, nodeSizeHeight));
        node.setContent(new ViewImpl<>(new EdgeImpl("uuid"), bounds));

        canvasLayoutUtils.setOrientation(Orientation.RightBottom);

        canvasLayoutUtils.calculateOffsetForMultipleEdges(offset,
                                                          maxNodePosition,
                                                          node,
                                                          nodePos,
                                                          rootPos);

        final double expectedOffsetY = yNodePosition + nodeSizeHeight - yRootPosition;
        assertEquals(0.0d, offset.getX(), 0.01d);
        assertEquals(expectedOffsetY, offset.getY(), 0.01d);

        assertEquals(xMaxPosition, maxNodePosition.getX(), 0.01d);
        assertEquals(yNodePosition, maxNodePosition.getY(), 0.01d);
    }

    @Test
    public void testCalculatePaddingToFirstNode_RightBottom() {

        final double[] rootSize = new double[]{100.0d, 200.0d};
        final double[] newNodeSize = new double[]{50.0d, 100.0d};
        final Point2D offset = new Point2D(0, 0);

        canvasLayoutUtils.calculatePaddingToFirstNode(rootSize, newNodeSize, offset);

        final double expectedX = 0.0d;
        final double expectedY = PADDING_Y;

        assertEquals(expectedX, offset.getX(), 0.01d);
        assertEquals(expectedY, offset.getY(), 0.01d);
    }

    @Test
    public void testCalculatePaddingToFirstNode_UpRight() {

        final double rootNodeWidth = 100.0d;
        final double newNodeWidth = 50.0d;
        final double[] rootSize = new double[]{rootNodeWidth, 200.0d};
        final double[] newNodeSize = new double[]{newNodeWidth, 100.0d};
        final Point2D offset = new Point2D(0, 0);

        canvasLayoutUtils.setOrientation(Orientation.UpRight);
        canvasLayoutUtils.calculatePaddingToFirstNode(rootSize, newNodeSize, offset);

        final double expectedX = PADDING_X + newNodeWidth - rootNodeWidth;
        final double expectedY = 0.0d;

        assertEquals(expectedX, offset.getX(), 0.01d);
        assertEquals(expectedY, offset.getY(), 0.01d);
    }

    @Test
    public void testCalculateOffsetForSingleEdge_UpRight() {

        final double rootNodeWidth = 100.0d;
        final double rootNodeHeight = 35.0d;
        final double newNodeWidth = 50.0d;
        final double newNodeHeight = 80.0d;

        final double[] rootSize = new double[]{rootNodeWidth, rootNodeHeight};
        final double[] newNodeSize = new double[]{newNodeWidth, newNodeHeight};
        final Point2D offset = new Point2D(0, 0);
        canvasLayoutUtils.setOrientation(Orientation.UpRight);

        canvasLayoutUtils.calculateOffsetForSingleEdge(rootSize,
                                                       newNodeSize,
                                                       offset);

        final double expectedX = -rootNodeWidth;
        final double expectedY = 0.0d;

        assertEquals(expectedX, offset.getX(), 0.01d);
        assertEquals(expectedY, offset.getY(), 0.01d);
    }

    @Test
    public void testCalculateOffsetForSingleEdge_RightBottom() {

        final double rootNodeWidth = 100.0d;
        final double rootNodeHeight = 35.0d;
        final double newNodeWidth = 50.0d;
        final double newNodeHeight = 80.0d;

        final double[] rootSize = new double[]{rootNodeWidth, rootNodeHeight};
        final double[] newNodeSize = new double[]{newNodeWidth, newNodeHeight};
        final Point2D offset = new Point2D(0, 0);
        canvasLayoutUtils.setOrientation(Orientation.RightBottom);

        canvasLayoutUtils.calculateOffsetForSingleEdge(rootSize,
                                                       newNodeSize,
                                                       offset);

        final double expectedX = 0.0d;
        final double expectedY = -(newNodeHeight - rootNodeHeight) / 2;

        assertEquals(expectedX, offset.getX(), 0.01d);
        assertEquals(expectedY, offset.getY(), 0.01d);
    }

    @Test
    public void testCalculatePadding_UpRight() {

        final double newNodeWidth = 50.0d;
        final double newNodeHeight = 80.0d;
        final double[] newNodeSize = new double[]{newNodeWidth, newNodeHeight};
        final Point2D offset = new Point2D(0, 0);
        canvasLayoutUtils.setOrientation(Orientation.UpRight);

        canvasLayoutUtils.calculatePadding(newNodeSize, offset);

        final double expectedX = 0.0d;
        final double expectedY = -PADDING_Y - newNodeHeight;

        assertEquals(expectedX, offset.getX(), 0.01d);
        assertEquals(expectedY, offset.getY(), 0.01d);
    }

    @Test
    public void testCalculatePadding_RightBottom() {

        final double newNodeWidth = 50.0d;
        final double newNodeHeight = 80.0d;
        final double[] newNodeSize = new double[]{newNodeWidth, newNodeHeight};
        final Point2D offset = new Point2D(0, 0);

        canvasLayoutUtils.calculatePadding(newNodeSize, offset);

        final double expectedX = PADDING_X;
        final double expectedY = 0;

        assertEquals(expectedX, offset.getX(), 0.01d);
        assertEquals(expectedY, offset.getY(), 0.01d);
    }

    @Test
    public void testGetStartingOffset_UpRight() {

        final double expectedX = 0.0d;
        final double expectedY = -PADDING_Y;
        canvasLayoutUtils.setOrientation(Orientation.UpRight);

        final Point2D startingOffset = canvasLayoutUtils.getStartingOffset();

        assertEquals(expectedX, startingOffset.getX(), 0.01d);
        assertEquals(expectedY, startingOffset.getY(), 0.01d);
    }

    @Test
    public void testGetStartingOffset_RightBottom() {

        final double expectedX = PADDING_X;
        final double expectedY = 0.0d;

        final Point2D startingOffset = canvasLayoutUtils.getStartingOffset();

        assertEquals(expectedX, startingOffset.getX(), 0.01d);
        assertEquals(expectedY, startingOffset.getY(), 0.01d);
    }

    @Test
    public void testGetNext_WhenThereIsNotMultipleEdges() {

        final ArgumentCaptor<double[]> captor = ArgumentCaptor.forClass(double[].class);
        final ArgumentCaptor<Point2D> pointCaptor = ArgumentCaptor.forClass(Point2D.class);
        final AbstractCanvasHandler canvasHandler = mock(AbstractCanvasHandler.class);
        final double rootNodeWidth = 155.0d;
        final double rootNodeHeight = 74.0d;
        final double newNodeWidth = 99.0d;
        final double newNodeHeight = 49.0d;
        final Node<View<?>, Edge> root = createNodeWithSize(rootNodeWidth, rootNodeHeight);
        final Node<View<?>, Edge> newNode = createNodeWithSize(newNodeWidth, newNodeHeight);
        final Orientation orientation = Orientation.UpRight;
        final double rootX = 9.0d;
        final double rootY = 2.0d;
        final Point2D returnedPoint = new Point2D(0, 0);
        final Point2D startingOffset = new Point2D(77.0d, 77.0d);
        final double[] rootBounds = new double[]{rootX, rootY};

        doReturn(rootBounds).when(canvasLayoutUtils).getBoundCoordinates(root);
        doReturn(startingOffset).when(canvasLayoutUtils).getStartingOffset();
        doReturn(returnedPoint).when(canvasLayoutUtils).getNext(eq(canvasHandler),
                                                                eq(root),
                                                                eq(rootNodeWidth),
                                                                eq(rootNodeHeight),
                                                                eq(newNodeWidth),
                                                                eq(newNodeHeight),
                                                                any(Point2D.class),
                                                                any(Point2D.class));
        canvasLayoutUtils.getNext(canvasHandler,
                                  root,
                                  newNode,
                                  orientation);

        verify(canvasLayoutUtils).setOrientation(orientation);
        verify(canvasLayoutUtils).calculateOffsetForSingleEdge(captor.capture(),
                                                               captor.capture(),
                                                               eq(startingOffset));

        final List<double[]> capturedValues = captor.getAllValues();
        final double[] rootSize = capturedValues.get(0);
        final double[] newNodeSize = capturedValues.get(1);

        verify(canvasLayoutUtils).calculatePadding(eq(newNodeSize),
                                                   eq(startingOffset));

        verify(canvasLayoutUtils).getNext(eq(canvasHandler),
                                          eq(root),
                                          eq(rootNodeWidth),
                                          eq(rootNodeHeight),
                                          eq(newNodeWidth),
                                          eq(newNodeHeight),
                                          pointCaptor.capture(),
                                          pointCaptor.capture());

        final Point2D offsetCoordinates = pointCaptor.getAllValues().get(0);
        final Point2D rootNodeCoordinates = pointCaptor.getAllValues().get(1);
        assertEquals(rootX, rootNodeCoordinates.getX(), 0.01d);
        assertEquals(rootY, rootNodeCoordinates.getY(), 0.01d);
        assertEquals(startingOffset.getX(), offsetCoordinates.getX(), 0.01d);
        assertEquals(startingOffset.getY(), offsetCoordinates.getY(), 0.01d);
        assertEquals(rootSize[0], rootNodeWidth, 0.01d);
        assertEquals(rootSize[1], rootNodeHeight, 0.01d);
        assertEquals(newNodeSize[0], newNodeWidth, 0.01d);
        assertEquals(newNodeSize[1], newNodeHeight, 0.01d);
    }

    @Test
    public void testGetNext_WhenThereIsMultipleEdges() {

        final ArgumentCaptor<double[]> captor = ArgumentCaptor.forClass(double[].class);
        final ArgumentCaptor<Point2D> pointCaptor = ArgumentCaptor.forClass(Point2D.class);
        final AbstractCanvasHandler canvasHandler = mock(AbstractCanvasHandler.class);
        final double rootNodeWidth = 155.0d;
        final double rootNodeHeight = 74.0d;
        final double newNodeWidth = 99.0d;
        final double newNodeHeight = 49.0d;
        final Node<View<?>, Edge> root = createNodeWithSize(rootNodeWidth, rootNodeHeight);
        final Node<View<?>, Edge> newNode = createNodeWithSize(newNodeWidth, newNodeHeight);
        final Orientation orientation = Orientation.UpRight;
        final double rootX = 9.0d;
        final double rootY = 2.0d;
        final Point2D returnedPoint = new Point2D(0, 0);
        final Point2D startingOffset = new Point2D(77.0d, 77.0d);
        final double[] rootBounds = new double[]{rootX, rootY};

        final Node<View<?>, Edge> edge1 = createNodeWithSize(10, 20);
        final Node<View<?>, Edge> edge2 = createNodeWithSize(10, 20);
        final Node<View<?>, Edge> edge3 = createNodeWithSize(10, 20);

        root.getOutEdges().add(createEdgeToTargetNode(edge1));
        root.getOutEdges().add(createEdgeToTargetNode(edge2));
        root.getOutEdges().add(createEdgeToTargetNode(edge3));
        root.getOutEdges().add(createEdgeToTargetNode(newNode));
        root.getOutEdges().add(createEdgeToTargetNode(null));

        doReturn(rootBounds).when(canvasLayoutUtils).getBoundCoordinates(root);
        doReturn(startingOffset).when(canvasLayoutUtils).getStartingOffset();
        doReturn(returnedPoint).when(canvasLayoutUtils).getNext(eq(canvasHandler),
                                                                eq(root),
                                                                eq(rootNodeWidth),
                                                                eq(rootNodeHeight),
                                                                eq(newNodeWidth),
                                                                eq(newNodeHeight),
                                                                any(Point2D.class),
                                                                any(Point2D.class));
        canvasLayoutUtils.getNext(canvasHandler,
                                  root,
                                  newNode,
                                  orientation);

        verify(canvasLayoutUtils).setOrientation(orientation);

        verify(canvasLayoutUtils).calculateOffsetForMultipleEdges(eq(startingOffset),
                                                                  any(Point2D.class),
                                                                  eq(edge1),
                                                                  any(Point2D.class),
                                                                  any(Point2D.class));

        verify(canvasLayoutUtils).calculateOffsetForMultipleEdges(eq(startingOffset),
                                                                  any(Point2D.class),
                                                                  eq(edge2),
                                                                  any(Point2D.class),
                                                                  any(Point2D.class));

        verify(canvasLayoutUtils).calculateOffsetForMultipleEdges(eq(startingOffset),
                                                                  any(Point2D.class),
                                                                  eq(edge3),
                                                                  any(Point2D.class),
                                                                  any(Point2D.class));

        verify(canvasLayoutUtils, never()).calculateOffsetForMultipleEdges(eq(startingOffset),
                                                                           any(Point2D.class),
                                                                           eq(newNode),
                                                                           any(Point2D.class),
                                                                           any(Point2D.class));

        verify(canvasLayoutUtils, never()).calculateOffsetForMultipleEdges(eq(startingOffset),
                                                                           any(Point2D.class),
                                                                           eq(null),
                                                                           any(Point2D.class),
                                                                           any(Point2D.class));

        verify(canvasLayoutUtils, never()).calculateOffsetForSingleEdge(any(double[].class),
                                                                        any(double[].class),
                                                                        eq(startingOffset));

        verify(canvasLayoutUtils).calculatePaddingToFirstNode(captor.capture(),
                                                              captor.capture(),
                                                              eq(startingOffset));

        final List<double[]> capturedValues = captor.getAllValues();
        final double[] rootSize = capturedValues.get(0);
        final double[] newNodeSize = capturedValues.get(1);

        verify(canvasLayoutUtils).calculatePadding(eq(newNodeSize),
                                                   eq(startingOffset));

        verify(canvasLayoutUtils).getNext(eq(canvasHandler),
                                          eq(root),
                                          eq(rootNodeWidth),
                                          eq(rootNodeHeight),
                                          eq(newNodeWidth),
                                          eq(newNodeHeight),
                                          pointCaptor.capture(),
                                          pointCaptor.capture());

        final Point2D offsetCoordinates = pointCaptor.getAllValues().get(0);
        final Point2D rootNodeCoordinates = pointCaptor.getAllValues().get(1);
        assertEquals(rootX, rootNodeCoordinates.getX(), 0.01d);
        assertEquals(rootY, rootNodeCoordinates.getY(), 0.01d);
        assertEquals(startingOffset.getX(), offsetCoordinates.getX(), 0.01d);
        assertEquals(startingOffset.getY(), offsetCoordinates.getY(), 0.01d);
        assertEquals(rootSize[0], rootNodeWidth, 0.01d);
        assertEquals(rootSize[1], rootNodeHeight, 0.01d);
        assertEquals(newNodeSize[0], newNodeWidth, 0.01d);
        assertEquals(newNodeSize[1], newNodeHeight, 0.01d);
    }

    private Node<View<?>, Edge> createNodeWithSize(final double width, final double height) {

        final Node<View<?>, Edge> node = new NodeImpl<>(UUID.randomUUID().toString());
        final Bounds bounds = Bounds.create();
        bounds.setUpperLeft(Bound.create(0, 0));
        bounds.setLowerRight(Bound.create(width, height));
        final ViewImpl<EdgeImpl> viewImpl = new ViewImpl<>(new EdgeImpl("uuid"), bounds);
        node.setContent(viewImpl);

        return node;
    }

    private Edge createEdgeToTargetNode(final Node node) {

        final ViewConnector connector = new ViewConnectorImpl(mock(Definition.class), Bounds.createEmpty());
        final Edge edge = new EdgeImpl("uuid");
        edge.setTargetNode(node);
        edge.setContent(connector);
        return edge;
    }
}
