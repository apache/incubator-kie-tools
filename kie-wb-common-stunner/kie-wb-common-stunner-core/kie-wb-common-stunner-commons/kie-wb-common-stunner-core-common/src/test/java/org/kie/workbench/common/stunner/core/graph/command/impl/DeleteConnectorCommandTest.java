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

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewConnector;
import org.kie.workbench.common.stunner.core.rule.DefaultRuleViolations;
import org.kie.workbench.common.stunner.core.rule.EdgeCardinalityRule;
import org.kie.workbench.common.stunner.core.rule.RuleManager;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.kie.workbench.common.stunner.core.rule.RuleViolations;
import org.kie.workbench.common.stunner.core.rule.impl.violations.ContainmentRuleViolation;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DeleteConnectorCommandTest extends AbstractGraphCommandTest {

    private static final String SOURCE_UUID = "sourceUUID";
    private static final String TARGET_UUID = "targetUUID";
    private static final String UUID = "edgeUUID";

    private Node source;
    private Node target;
    private Edge edge;
    @Mock
    private ViewConnector connContent;
    private DeleteConnectorCommand tested;

    @Before
    public void setup() throws Exception {
        super.init(500,
                   500);
        source = mockNode(SOURCE_UUID);
        target = mockNode(TARGET_UUID);
        edge = mockEdge(UUID);
        when(graphIndex.getNode(eq(SOURCE_UUID))).thenReturn(source);
        when(graphIndex.getNode(eq(TARGET_UUID))).thenReturn(target);
        when(graphIndex.getEdge(eq(UUID))).thenReturn(edge);
        when(edge.getContent()).thenReturn(connContent);
        when(edge.getSourceNode()).thenReturn(source);
        when(edge.getTargetNode()).thenReturn(target);
        when(connContent.getSourceMagnetIndex()).thenReturn(0);
        when(connContent.getTargetMagnetIndex()).thenReturn(1);
        this.tested = new DeleteConnectorCommand(UUID);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testAllow() {
        CommandResult<RuleViolation> result = tested.allow(graphCommandExecutionContext);
        assertEquals(CommandResult.Type.INFO,
                     result.getType());
        verify(connectionRuleManager,
               times(1)).evaluate(eq(edge),
                                  eq(source),
                                  eq(null));
        verify(connectionRuleManager,
               times(1)).evaluate(eq(edge),
                                  eq(null),
                                  eq(target));
        verify(edgeCardinalityRuleManager,
               times(1)).evaluate(eq(edge),
                                  eq(source),
                                  any(List.class),
                                  eq(EdgeCardinalityRule.Type.OUTGOING),
                                  eq(RuleManager.Operation.DELETE));
        verify(edgeCardinalityRuleManager,
               times(1)).evaluate(eq(edge),
                                  eq(target),
                                  any(List.class),
                                  eq(EdgeCardinalityRule.Type.INCOMING),
                                  eq(RuleManager.Operation.DELETE));
        verify(containmentRuleManager,
               times(0)).evaluate(any(Element.class),
                                  any(Element.class));
        verify(cardinalityRuleManager,
               times(0)).evaluate(any(Graph.class),
                                  any(Node.class),
                                  any(RuleManager.Operation.class));
        verify(dockingRuleManager,
               times(0)).evaluate(any(Element.class),
                                  any(Element.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testAllowNoRules() {
        when(graphCommandExecutionContext.getRulesManager()).thenReturn(null);
        CommandResult<RuleViolation> result = tested.allow(graphCommandExecutionContext);
        assertEquals(CommandResult.Type.INFO,
                     result.getType());
        verify(connectionRuleManager,
               times(0)).evaluate(eq(edge),
                                  eq(source),
                                  eq(null));
        verify(connectionRuleManager,
               times(0)).evaluate(eq(edge),
                                  eq(null),
                                  eq(target));
        verify(edgeCardinalityRuleManager,
               times(0)).evaluate(eq(edge),
                                  eq(source),
                                  any(List.class),
                                  eq(EdgeCardinalityRule.Type.OUTGOING),
                                  eq(RuleManager.Operation.DELETE));
        verify(edgeCardinalityRuleManager,
               times(0)).evaluate(eq(edge),
                                  eq(target),
                                  any(List.class),
                                  eq(EdgeCardinalityRule.Type.INCOMING),
                                  eq(RuleManager.Operation.DELETE));
        verify(containmentRuleManager,
               times(0)).evaluate(any(Element.class),
                                  any(Element.class));
        verify(cardinalityRuleManager,
               times(0)).evaluate(any(Graph.class),
                                  any(Node.class),
                                  any(RuleManager.Operation.class));
        verify(dockingRuleManager,
               times(0)).evaluate(any(Element.class),
                                  any(Element.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testNotAllowed() {
        final RuleViolations FAILED_VIOLATIONS = new DefaultRuleViolations()
                .addViolation(new ContainmentRuleViolation(graph.getUUID(),
                                                           UUID));
        when(edgeCardinalityRuleManager.evaluate(any(Edge.class),
                                                 any(Node.class),
                                                 any(List.class),
                                                 any(EdgeCardinalityRule.Type.class),
                                                 any(RuleManager.Operation.class))).thenReturn(FAILED_VIOLATIONS);
        CommandResult<RuleViolation> result = tested.allow(graphCommandExecutionContext);
        assertEquals(CommandResult.Type.ERROR,
                     result.getType());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testExecute() {
        final List sourceOutEdges = mock(List.class);
        final List targetInEdges = mock(List.class);
        when(source.getOutEdges()).thenReturn(sourceOutEdges);
        when(target.getInEdges()).thenReturn(targetInEdges);
        CommandResult<RuleViolation> result = tested.execute(graphCommandExecutionContext);
        assertEquals(CommandResult.Type.INFO,
                     result.getType());
        verify(sourceOutEdges,
               times(1)).remove(eq(edge));
        verify(targetInEdges,
               times(1)).remove(eq(edge));
        verify(graphIndex,
               times(1)).removeEdge(eq(edge));
        verify(graphIndex,
               times(0)).addEdge(any(Edge.class));
        verify(graphIndex,
               times(0)).addNode(any(Node.class));
        verify(graphIndex,
               times(0)).removeNode(any(Node.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testExecuteCheckFailed() {
        final RuleViolations FAILED_VIOLATIONS = new DefaultRuleViolations()
                .addViolation(new ContainmentRuleViolation(graph.getUUID(),
                                                           UUID));
        when(edgeCardinalityRuleManager.evaluate(any(Edge.class),
                                                 any(Node.class),
                                                 any(List.class),
                                                 any(EdgeCardinalityRule.Type.class),
                                                 any(RuleManager.Operation.class))).thenReturn(FAILED_VIOLATIONS);
        final List sourceOutEdges = mock(List.class);
        final List targetInEdges = mock(List.class);
        when(source.getOutEdges()).thenReturn(sourceOutEdges);
        when(target.getInEdges()).thenReturn(targetInEdges);
        CommandResult<RuleViolation> result = tested.execute(graphCommandExecutionContext);
        assertEquals(CommandResult.Type.ERROR,
                     result.getType());
        verify(sourceOutEdges,
               times(0)).remove(any(Edge.class));
        verify(targetInEdges,
               times(0)).remove(any(Edge.class));
        verify(graphIndex,
               times(0)).removeNode(any(Node.class));
        verify(graphIndex,
               times(0)).removeEdge(any(Edge.class));
        verify(graphIndex,
               times(0)).addEdge(any(Edge.class));
        verify(graphIndex,
               times(0)).addNode(any(Node.class));
    }
}
