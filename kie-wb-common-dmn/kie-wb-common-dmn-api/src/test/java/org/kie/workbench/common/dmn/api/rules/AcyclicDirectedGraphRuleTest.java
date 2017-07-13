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
package org.kie.workbench.common.dmn.api.rules;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewConnector;
import org.kie.workbench.common.stunner.core.graph.impl.EdgeImpl;
import org.kie.workbench.common.stunner.core.graph.impl.GraphImpl;
import org.kie.workbench.common.stunner.core.graph.impl.NodeImpl;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.tree.TreeTraverseCallback;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.tree.TreeWalkTraverseProcessor;
import org.kie.workbench.common.stunner.core.graph.store.GraphNodeStoreImpl;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.kie.workbench.common.stunner.core.rule.RuleViolations;
import org.kie.workbench.common.stunner.core.rule.context.GraphConnectionContext;
import org.kie.workbench.common.stunner.core.rule.ext.RuleExtension;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AcyclicDirectedGraphRuleTest {

    @Mock
    private RuleExtension rule;

    @Mock
    private GraphConnectionContext context;

    @Mock
    private EdgeImpl connector;

    @Mock
    private ViewConnector connectorView;

    @Mock
    private TreeWalkTraverseProcessor walker;

    private GraphImpl graph = new GraphImpl("uuid",
                                            new GraphNodeStoreImpl());

    private AcyclicDirectedGraphRule check;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() {
        this.check = spy(new AcyclicDirectedGraphRule());
        when(context.getGraph()).thenReturn(graph);
    }

    @Test
    public void assertExtensionType() {
        assertEquals(AcyclicDirectedGraphRule.class,
                     check.getExtensionType());
    }

    @Test
    public void assertContextType() {
        assertEquals(GraphConnectionContext.class,
                     check.getContextType());
    }

    @Test
    public void checkRuleAcceptsAnnotatedConnectorType() {
        mockConnectorDefinition(Definition.class,
                                new Definition());

        assertTrue(check.accepts(rule,
                                 context));
    }

    @Test
    public void checkRuleDoesNotAcceptDifferentAnnotatedConnectorType() {
        mockConnectorDefinition(String.class,
                                new Definition());

        assertFalse(check.accepts(rule,
                                  context));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void checkMissingConnectionNodesDoesNotTriggerGraphWalk() {
        when(context.getSource()).thenReturn(Optional.empty());
        when(context.getTarget()).thenReturn(Optional.empty());
        when(context.getConnector()).thenReturn(connector);
        when(check.getTreeWalker(any(Node.class),
                                 any(Node.class),
                                 any(Edge.class))).thenReturn(walker);

        final RuleViolations result = check.evaluate(rule,
                                                     context);
        assertNotNull(result);
        assertFalse(result.violations().iterator().hasNext());
        verify(walker,
               never()).traverse(any(Graph.class),
                                 any(TreeTraverseCallback.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void checkMissingConnectionTargetNodeDoesNotTriggerGraphWalk() {
        final Node source = mock(Node.class);
        when(context.getSource()).thenReturn(Optional.of(source));
        when(context.getTarget()).thenReturn(Optional.empty());
        when(context.getConnector()).thenReturn(connector);
        when(check.getTreeWalker(any(Node.class),
                                 any(Node.class),
                                 any(Edge.class))).thenReturn(walker);

        final RuleViolations result = check.evaluate(rule,
                                                     context);
        assertNotNull(result);
        assertFalse(result.violations().iterator().hasNext());
        verify(walker,
               never()).traverse(any(Graph.class),
                                 any(TreeTraverseCallback.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void checkCompleteConnectionDefinitionTriggersGraphWalk() {
        final Node source = mock(Node.class);
        final Node target = mock(Node.class);
        when(context.getSource()).thenReturn(Optional.of(source));
        when(context.getTarget()).thenReturn(Optional.of(target));
        when(context.getConnector()).thenReturn(connector);
        when(check.getTreeWalker(any(Node.class),
                                 any(Node.class),
                                 any(Edge.class))).thenReturn(walker);

        final RuleViolations result = check.evaluate(rule,
                                                     context);
        assertNotNull(result);
        assertFalse(result.violations().iterator().hasNext());
        verify(walker).traverse(eq(graph),
                                any(TreeTraverseCallback.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void checkNonCyclicalConnection() {
        final Node node1 = new NodeImpl<>("node1");
        final Node node2 = new NodeImpl<>("node2");
        graph.addNode(node1);
        graph.addNode(node2);
        when(context.getSource()).thenReturn(Optional.of(node1));
        when(context.getTarget()).thenReturn(Optional.of(node2));
        when(context.getConnector()).thenReturn(connector);

        final RuleViolations result = check.evaluate(rule,
                                                     context);
        assertNotNull(result);
        assertFalse(result.violations().iterator().hasNext());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void checkCyclicalConnection() {
        final Node node1 = new NodeImpl<>("node1");
        final Node node2 = new NodeImpl<>("node2");
        final Edge c1 = new EdgeImpl<>("edge1");

        node1.getOutEdges().add(c1);
        node2.getInEdges().add(c1);
        c1.setSourceNode(node1);
        c1.setTargetNode(node2);

        graph.addNode(node1);
        graph.addNode(node2);

        when(context.getSource()).thenReturn(Optional.of(node2));
        when(context.getTarget()).thenReturn(Optional.of(node1));
        when(context.getConnector()).thenReturn(connector);

        final RuleViolations result = check.evaluate(rule,
                                                     context);
        assertNotNull(result);
        assertTrue(result.violations().iterator().hasNext());
        final RuleViolation violation = result.violations().iterator().next();
        assertNotNull(violation);
        assertTrue(violation.getArguments().isPresent());
        assertEquals(1,
                     violation.getArguments().get().length);
        assertEquals(AcyclicDirectedGraphRule.ERROR_MESSAGE,
                     violation.getArguments().get()[0]);
    }

    @SuppressWarnings("unchecked")
    private void mockConnectorDefinition(final Class accepts,
                                         final Object definition) {
        when(rule.getTypeArguments()).thenReturn(new Class[]{accepts});
        when(context.getConnector()).thenReturn(connector);
        when(connector.getContent()).thenReturn(connectorView);
        when(connectorView.getDefinition()).thenReturn(definition);
    }

    private static class Definition {

    }
}
