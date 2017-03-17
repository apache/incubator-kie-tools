/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *     http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.core.graph.command.impl;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Optional;

import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.definition.adapter.AdapterManager;
import org.kie.workbench.common.stunner.core.definition.adapter.DefinitionAdapter;
import org.kie.workbench.common.stunner.core.definition.adapter.PropertyAdapter;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.graph.content.definition.DefinitionSet;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.processing.index.MutableIndex;
import org.kie.workbench.common.stunner.core.registry.definition.AdapterRegistry;
import org.kie.workbench.common.stunner.core.rule.RuleEvaluationContext;
import org.kie.workbench.common.stunner.core.rule.RuleManager;
import org.kie.workbench.common.stunner.core.rule.RuleSet;
import org.kie.workbench.common.stunner.core.rule.RuleViolations;
import org.kie.workbench.common.stunner.core.rule.context.CardinalityContext;
import org.kie.workbench.common.stunner.core.rule.context.ConnectorCardinalityContext;
import org.kie.workbench.common.stunner.core.rule.context.ElementCardinalityContext;
import org.kie.workbench.common.stunner.core.rule.context.GraphConnectionContext;
import org.kie.workbench.common.stunner.core.rule.context.NodeContainmentContext;
import org.kie.workbench.common.stunner.core.rule.context.NodeDockingContext;
import org.kie.workbench.common.stunner.core.rule.violations.DefaultRuleViolations;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public abstract class AbstractGraphCommandTest {

    private static final String GRAPH_UUID = "graphUUID";
    protected final RuleViolations EMPTY_VIOLATIONS = new DefaultRuleViolations();

    @Mock
    protected GraphCommandExecutionContext graphCommandExecutionContext;
    @Mock
    protected DefinitionManager definitionManager;
    @Mock
    protected AdapterManager adapterManager;
    @Mock
    protected AdapterRegistry adapterRegistry;
    @Mock
    protected DefinitionAdapter definitionAdapter;
    @Mock
    protected PropertyAdapter propertyAdapter;
    @Mock
    protected FactoryManager factoryManager;
    @Mock
    protected RuleManager ruleManager;
    @Mock
    protected MutableIndex graphIndex;
    @Mock
    protected Graph<DefinitionSet, Node> graph;
    @Mock
    protected RuleSet ruleSet;
    @Mock
    protected DefinitionSet graphContent;

    protected Collection<Node> graphNodes = new LinkedList<>();

    @SuppressWarnings("unchecked")
    public void init(final double width,
                     final double height) {
        MockitoAnnotations.initMocks(this);
        Bounds bounds = mockBounds(0,
                                   0,
                                   width,
                                   height);
        when(graphContent.getBounds()).thenReturn(bounds);
        when(graph.getUUID()).thenReturn(GRAPH_UUID);
        when(graph.getContent()).thenReturn(graphContent);
        when(graph.nodes()).thenReturn(graphNodes);
        when(definitionManager.adapters()).thenReturn(adapterManager);
        when(adapterManager.registry()).thenReturn(adapterRegistry);
        when(adapterManager.forDefinition()).thenReturn(definitionAdapter);
        when(adapterManager.forProperty()).thenReturn(propertyAdapter);
        when(adapterRegistry.getDefinitionAdapter(any(Class.class))).thenReturn(definitionAdapter);
        when(adapterRegistry.getPropertyAdapter(any(Class.class))).thenReturn(propertyAdapter);
        when(graphCommandExecutionContext.getDefinitionManager()).thenReturn(definitionManager);
        when(graphCommandExecutionContext.getFactoryManager()).thenReturn(factoryManager);
        when(graphCommandExecutionContext.getRuleManager()).thenReturn(ruleManager);
        when(graphCommandExecutionContext.getGraphIndex()).thenReturn(graphIndex);
        when(graphCommandExecutionContext.getRuleSet()).thenReturn(ruleSet);
        when(graphIndex.getGraph()).thenReturn(graph);
        when(ruleManager.evaluate(any(RuleSet.class),
                                  any(RuleEvaluationContext.class))).thenReturn(EMPTY_VIOLATIONS);
    }

    public static Node mockNode(String uuid) {
        Node node = mock(Node.class);
        when(node.getUUID()).thenReturn(uuid);
        when(node.getInEdges()).thenReturn(new LinkedList());
        when(node.getOutEdges()).thenReturn(new LinkedList());
        return node;
    }

    public static Edge mockEdge(String uuid) {
        Edge edge = mock(Edge.class);
        when(edge.getUUID()).thenReturn(uuid);
        return edge;
    }

    public static View mockView(final double x,
                                final double y,
                                final double w,
                                final double h) {
        View view = mock(View.class);
        Bounds bounds = mockBounds(x,
                                   y,
                                   w,
                                   h);
        when(view.getBounds()).thenReturn(bounds);
        return view;
    }

    public static Bounds mockBounds(final double x,
                                    final double y,
                                    final double w,
                                    final double h) {
        Bounds bounds = mock(Bounds.class);
        Bounds.Bound boundUL = mock(Bounds.Bound.class);
        Bounds.Bound boundLR = mock(Bounds.Bound.class);
        when(boundUL.getX()).thenReturn(x);
        when(boundUL.getY()).thenReturn(y);
        when(boundLR.getX()).thenReturn(x + w);
        when(boundLR.getY()).thenReturn(y + h);
        when(bounds.getLowerRight()).thenReturn(boundLR);
        when(bounds.getUpperLeft()).thenReturn(boundUL);
        return bounds;
    }

    protected static void verifyContainment(final NodeContainmentContext containmentContext,
                                            final Element<? extends Definition<?>> parent,
                                            final Node<? extends Definition<?>, ? extends Edge> candidate) {
        assertNotNull(containmentContext);
        final Optional<Element<? extends Definition<?>>> source = containmentContext.getParent();
        final Node<? extends Definition<?>, ? extends Edge> target = containmentContext.getCandidate();
        assertTrue(source.isPresent());
        assertNotNull(target);
        assertEquals(parent,
                     source.get());
        assertEquals(candidate,
                     target);
    }

    protected static void verifyDocking(final NodeDockingContext context,
                                        final Element<? extends Definition<?>> parent,
                                        final Node<? extends Definition<?>, ? extends Edge> candidate) {
        assertNotNull(context);
        final Optional<Element<? extends Definition<?>>> source = context.getParent();
        final Node<? extends Definition<?>, ? extends Edge> target = context.getCandidate();
        assertTrue(source.isPresent());
        assertNotNull(target);
        assertEquals(parent,
                     source.get());
        assertEquals(candidate,
                     target);
    }

    protected static void verifyConnection(final GraphConnectionContext context,
                                           final Edge<? extends View<?>, ? extends Node> connector,
                                           final Node<? extends View<?>, ? extends Edge> sourceNode,
                                           final Node<? extends View<?>, ? extends Edge> targetNode) {
        assertNotNull(context);
        final Edge<? extends View<?>, ? extends Node> connector1 = context.getConnector();
        final Optional<Node<? extends View<?>, ? extends Edge>> source = context.getSource();
        final Optional<Node<? extends View<?>, ? extends Edge>> target = context.getTarget();
        assertNotNull(connector1);
        assertEquals(connector,
                     connector1);
        if (null != sourceNode) {
            assertEquals(sourceNode,
                         source.get());
        }
        if (null != targetNode) {
            assertEquals(targetNode,
                         target.get());
        }
    }

    protected static void verifyCardinality(final ElementCardinalityContext context,
                                            final Graph graph,
                                            final Element<? extends View<?>> candidate,
                                            final CardinalityContext.Operation operation) {
        assertNotNull(context);
        final Graph graph1 = context.getGraph();
        final Element<? extends View<?>> candidate1 = context.getCandidate();
        final CardinalityContext.Operation operation1 = context.getOperation();
        assertNotNull(graph1);
        assertNotNull(candidate1);
        assertNotNull(operation1);
        assertEquals(graph,
                     graph1);
        assertEquals(candidate,
                     candidate1);
        assertEquals(operation,
                     operation1);
    }

    protected static void verifyConnectorCardinality(final ConnectorCardinalityContext context,
                                                     final Graph graph,
                                                     final Element<? extends View<?>> candidate,
                                                     final Edge<? extends View<?>, Node> edge,
                                                     final ConnectorCardinalityContext.Direction direction,
                                                     final CardinalityContext.Operation operation) {
        assertNotNull(context);
        final ConnectorCardinalityContext.Direction direction1 = context.getDirection();
        final Edge<? extends View<?>, Node> edge1 = context.getEdge();
        final Element<? extends View<?>> candidate1 = context.getCandidate();
        final Graph graph1 = context.getGraph();
        final CardinalityContext.Operation operation1 = context.getOperation();
        assertNotNull(direction1);
        assertNotNull(edge1);
        assertNotNull(candidate1);
        assertNotNull(graph1);
        assertNotNull(operation1);
        assertEquals(direction,
                     direction1);
        assertEquals(edge,
                     edge1);
        assertEquals(operation,
                     operation1);
        assertEquals(candidate,
                     candidate1);
        assertEquals(graph,
                     graph1);
        assertEquals(operation,
                     operation1);
    }
}
