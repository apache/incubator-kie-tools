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
import java.util.Iterator;
import java.util.List;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.TestingGraphInstanceBuilder;
import org.kie.workbench.common.stunner.core.TestingGraphMockHandler;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.Canvas;
import org.kie.workbench.common.stunner.core.definition.adapter.AdapterManager;
import org.kie.workbench.common.stunner.core.definition.adapter.DefinitionSetRuleAdapter;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.definition.DefinitionSet;
import org.kie.workbench.common.stunner.core.graph.content.view.Point2D;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
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
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
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
    private Canvas canvas;

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

        Point2D canvasMin = new Point2D(0d,
                                        0d);
        Point2D canvasMax = new Point2D(1200d,
                                        1200d);
        when(canvasHandler.getCanvas().getHeightPx()).thenReturn((int) canvasMax.getY());
        when(canvasHandler.getCanvas().getWidthPx()).thenReturn((int) canvasMax.getX());

        canvasLayoutUtils = new CanvasLayoutUtils(graphBoundsIndexer);

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
        boolean isCanvasRoot = CanvasLayoutUtils.isCanvasRoot(diagram,
                                                              parentCanvasRoot);
        assertTrue(isCanvasRoot);
    }

    @Test
    public void isCanvasRootFalseTest() {
        when(diagram.getMetadata().getCanvasRootUUID()).thenReturn("test");
        boolean isCanvasRoot = CanvasLayoutUtils.isCanvasRoot(diagram,
                                                              parentNotCanvasRoot);
        assertFalse(isCanvasRoot);
    }

    @Test
    public void isCanvasRootWithUuidTrueTest() {
        when(diagram.getMetadata().getCanvasRootUUID()).thenReturn("canvas_root");
        boolean isCanvasRoot = CanvasLayoutUtils.isCanvasRoot(diagram,
                                                              "canvas_root");
        assertTrue(isCanvasRoot);
    }

    @Test
    public void isCanvasRootWithUuidFalseTest() {
        when(diagram.getMetadata().getCanvasRootUUID()).thenReturn("test");
        boolean isCanvasRoot = CanvasLayoutUtils.isCanvasRoot(diagram,
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
                                                 node2);

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
                                                 node);

        Node<View<?>, Edge> start = (Node<View<?>, Edge>) graphInstanceParent.startNode;
        double[] size = GraphUtils.getNodeSize(start.getContent());
        assertTrue(next.getX() == CanvasLayoutUtils.getPaddingX());
        assertTrue(next.getY() > size[1]);
    }

    // TODO (AlessioP & Roger):
    @Test
    @Ignore
    @SuppressWarnings("unchecked")
    public void getNextOutOfCanvas() {
        when(ruleManager.evaluate(eq(ruleSet),
                                  any(RuleEvaluationContext.class))).thenReturn(ruleViolations);

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
                                                 newNode);

        Node<View<?>, Edge> intermNode = (Node<View<?>, Edge>) graphInstance.intermNode;
        double[] size = GraphUtils.getNodeSize(intermNode.getContent());

        assertTrue(next.getX() > size[0]);
        assertTrue(next.getY() == CanvasLayoutUtils.getPaddingY());
    }

    // TODO (AlessioP & Roger):
    @Test
    @Ignore
    @SuppressWarnings("unchecked")
    public void getNextFromNewTaskWithNonEmptyPositionWithParent() {
        when(ruleManager.evaluate(eq(ruleSet),
                                  any(RuleEvaluationContext.class))).thenReturn(ruleViolations);

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
                                                 newNode);
        Node<View<?>, Edge> intermNode = (Node<View<?>, Edge>) graphInstanceParent.intermNode;
        double[] size = GraphUtils.getNodeSize(intermNode.getContent());
        assertTrue(next.getX() == CanvasLayoutUtils.getPaddingX());
        assertTrue(next.getY() > size[1]);
    }

    // TODO (AlessioP & Roger):
    @Test
    @Ignore
    @SuppressWarnings("unchecked")
    public void getNextNewTaskWithNonEmptyPosition() {

        when(ruleManager.evaluate(eq(ruleSet),
                                  any(RuleEvaluationContext.class))).thenReturn(ruleViolations);

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
                                                 newNode);

        Node<View<?>, Edge> startNode = (Node<View<?>, Edge>) graphInstance.startNode;
        double[] sizeStartNode = GraphUtils.getNodeSize(startNode.getContent());
        Node<View<?>, Edge> intermNode = (Node<View<?>, Edge>) graphInstance.intermNode;
        double[] sizeIntermNode = GraphUtils.getNodeSize(intermNode.getContent());
        assertTrue(next.getX() == sizeStartNode[0] + CanvasLayoutUtils.getPaddingX());
        assertTrue(next.getY() > sizeIntermNode[1]);
    }

    @Test
    public void testGetElementNullCanvasHandlerAndNullUUID() {
        when(abstractCanvasHandler.getGraphIndex()).thenReturn(graphIndex);

        assertNull(CanvasLayoutUtils.getElement(null, null));
    }

    @Test
    public void testGetElementNullCanvasHandler() {
        when(abstractCanvasHandler.getGraphIndex()).thenReturn(graphIndex);

        assertNull(CanvasLayoutUtils.getElement(null, NODE_UUID));
    }

    @Test
    public void testGetElementNullUUID() {
        when(abstractCanvasHandler.getGraphIndex()).thenReturn(graphIndex);

        assertNull(CanvasLayoutUtils.getElement(abstractCanvasHandler, null));
    }

    @Test
    public void testGetElementWithNullIndex() {
        assertNull(CanvasLayoutUtils.getElement(abstractCanvasHandler, NODE_UUID));
    }

    @Test
    public void testGetElement() {
        when(abstractCanvasHandler.getGraphIndex()).thenReturn(graphIndex);
        when(graphIndex.get(NODE_UUID)).thenReturn(nodeRoot);

        assertEquals(nodeRoot, CanvasLayoutUtils.getElement(abstractCanvasHandler, NODE_UUID));
    }
}
