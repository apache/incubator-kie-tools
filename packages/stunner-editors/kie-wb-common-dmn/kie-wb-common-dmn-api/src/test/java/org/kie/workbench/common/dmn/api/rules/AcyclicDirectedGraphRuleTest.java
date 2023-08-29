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
package org.kie.workbench.common.dmn.api.rules;

import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.impl.EdgeImpl;
import org.kie.workbench.common.stunner.core.graph.impl.NodeImpl;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.tree.TreeTraverseCallback;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.tree.TreeWalkTraverseProcessor;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.kie.workbench.common.stunner.core.rule.RuleViolations;
import org.kie.workbench.common.stunner.core.rule.context.GraphConnectionContext;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class AcyclicDirectedGraphRuleTest extends BaseGraphRuleTest<AcyclicDirectedGraphRule> {

    @Mock
    private TreeWalkTraverseProcessor walker;

    @Override
    protected AcyclicDirectedGraphRule getRule() {
        return new AcyclicDirectedGraphRule();
    }

    @Override
    protected Class getExpectedExtensionType() {
        return AcyclicDirectedGraphRule.class;
    }

    @Override
    protected Class getExpectedContextType() {
        return GraphConnectionContext.class;
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
}
