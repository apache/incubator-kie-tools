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

package org.kie.workbench.common.stunner.core.graph.command.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.Connection;
import org.kie.workbench.common.stunner.core.graph.content.view.MagnetConnection;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewConnector;
import org.kie.workbench.common.stunner.core.rule.RuleEvaluationContext;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.kie.workbench.common.stunner.core.rule.context.CardinalityContext;
import org.kie.workbench.common.stunner.core.rule.context.ConnectorCardinalityContext;
import org.kie.workbench.common.stunner.core.rule.context.EdgeCardinalityContext;
import org.kie.workbench.common.stunner.core.rule.context.GraphConnectionContext;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.kie.workbench.common.stunner.core.TestingGraphUtils.verifyConnection;
import static org.kie.workbench.common.stunner.core.TestingGraphUtils.verifyConnectorCardinality;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class SetConnectionSourceNodeCommandTest extends AbstractGraphCommandTest {

    private static final String NODE_UUID = "nodeUUID";
    private static final String LAST_SOURCE_NODE_UUID = "lastSourceNodeUUID";
    private static final String TARGET_UUID = "nodeTargetUUID";
    private static final String EDGE_UUID = "edgeUUID";
    private static final Double MAGNETX = 15d;
    private static final Double MAGNETY = 0d;

    private Node node;
    private Node lastSourceNode;
    private Node target;
    private Edge edge;
    private ViewConnector edgeContent;
    private Optional<Connection> sourceMagnet;
    private SetConnectionSourceNodeCommand tested;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() throws Exception {
        super.init();
        node = mockNode(NODE_UUID);
        lastSourceNode = mockNode(LAST_SOURCE_NODE_UUID);
        target = mockNode(TARGET_UUID);
        edge = mockEdge(EDGE_UUID);
        edgeContent = mock(ViewConnector.class);
        sourceMagnet = Optional.of(MagnetConnection.Builder.at(0d,
                                                               0d));
        graph.addNode(node);
        when(graphIndex.getNode(eq(NODE_UUID))).thenReturn(node);
        when(graphIndex.getNode(eq(LAST_SOURCE_NODE_UUID))).thenReturn(lastSourceNode);
        when(graphIndex.getNode(eq(TARGET_UUID))).thenReturn(target);
        when(graphIndex.getEdge(eq(EDGE_UUID))).thenReturn(edge);
        when(edge.getContent()).thenReturn(edgeContent);
        when(edge.getSourceNode()).thenReturn(lastSourceNode);
        when(edge.getTargetNode()).thenReturn(target);
        when(edgeContent.getSourceConnection()).thenReturn(sourceMagnet);
        this.tested = new SetConnectionSourceNodeCommand(node,
                                                         edge,
                                                         MagnetConnection.Builder.at(MAGNETX,
                                                                                     MAGNETY));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testAllow() {
        CommandResult<RuleViolation> result = tested.allow(graphCommandExecutionContext);
        assertEquals(CommandResult.Type.INFO,
                     result.getType());
        final ArgumentCaptor<RuleEvaluationContext> contextCaptor = ArgumentCaptor.forClass(RuleEvaluationContext.class);
        verify(ruleManager,
               times(3)).evaluate(eq(ruleSet),
                                  contextCaptor.capture());
        final List<RuleEvaluationContext> contexts = contextCaptor.getAllValues();
        assertEquals(3,
                     contexts.size());
        verifyConnection((GraphConnectionContext) contexts.get(0),
                         edge,
                         node,
                         target);

        verifyConnectorCardinality((ConnectorCardinalityContext) contexts.get(1),
                                   graph,
                                   lastSourceNode,
                                   edge,
                                   EdgeCardinalityContext.Direction.OUTGOING,
                                   Optional.of(CardinalityContext.Operation.DELETE));
        verifyConnectorCardinality((ConnectorCardinalityContext) contexts.get(2),
                                   graph,
                                   node,
                                   edge,
                                   EdgeCardinalityContext.Direction.OUTGOING,
                                   Optional.of(CardinalityContext.Operation.ADD));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testAllowNoRules() {
        useAllowedExecutionContext();
        CommandResult<RuleViolation> result = tested.allow(graphCommandExecutionContext);
        assertEquals(CommandResult.Type.INFO,
                     result.getType());
        verify(ruleManager,
               times(0)).evaluate(eq(ruleSet),
                                  any(RuleEvaluationContext.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSkipRulesForSameSourceNodeAsBefore() {
        when(edge.getSourceNode()).thenReturn(node);
        CommandResult<RuleViolation> result = tested.allow(graphCommandExecutionContext);
        assertEquals(CommandResult.Type.INFO,
                     result.getType());
        verify(ruleManager,
               times(0)).evaluate(eq(ruleSet),
                                  any(RuleEvaluationContext.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testAllowNoSourceConnection() {
        this.tested = new SetConnectionSourceNodeCommand(null,
                                                         edge,
                                                         MagnetConnection.Builder.at(MAGNETX,
                                                                                     MAGNETY));
        CommandResult<RuleViolation> result = tested.allow(graphCommandExecutionContext);
        assertEquals(CommandResult.Type.INFO,
                     result.getType());
        final ArgumentCaptor<RuleEvaluationContext> contextCaptor = ArgumentCaptor.forClass(RuleEvaluationContext.class);
        verify(ruleManager,
               times(2)).evaluate(eq(ruleSet),
                                  contextCaptor.capture());
        final List<RuleEvaluationContext> contexts = contextCaptor.getAllValues();
        assertEquals(2,
                     contexts.size());
        verifyConnection((GraphConnectionContext) contexts.get(0),
                         edge,
                         null,
                         null);

        verifyConnectorCardinality((ConnectorCardinalityContext) contexts.get(1),
                                   graph,
                                   lastSourceNode,
                                   edge,
                                   EdgeCardinalityContext.Direction.OUTGOING,
                                   Optional.of(CardinalityContext.Operation.DELETE));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testExecute() {
        final List lastSourceOutEdges = spy(new ArrayList());
        final List sourceOutEdges = spy(new ArrayList());
        final List targetInEdges = spy(new ArrayList());
        when(node.getOutEdges()).thenReturn(sourceOutEdges);
        when(lastSourceNode.getOutEdges()).thenReturn(lastSourceOutEdges);
        when(target.getInEdges()).thenReturn(targetInEdges);
        CommandResult<RuleViolation> result = tested.execute(graphCommandExecutionContext);
        final ArgumentCaptor<RuleEvaluationContext> contextCaptor = ArgumentCaptor.forClass(RuleEvaluationContext.class);
        verify(ruleManager,
               times(3)).evaluate(eq(ruleSet),
                                  contextCaptor.capture());
        final List<RuleEvaluationContext> contexts = contextCaptor.getAllValues();
        assertEquals(3,
                     contexts.size());
        verifyConnection((GraphConnectionContext) contexts.get(0),
                         edge,
                         node,
                         target);

        verifyConnectorCardinality((ConnectorCardinalityContext) contexts.get(1),
                                   graph,
                                   lastSourceNode,
                                   edge,
                                   EdgeCardinalityContext.Direction.OUTGOING,
                                   Optional.of(CardinalityContext.Operation.DELETE));
        verifyConnectorCardinality((ConnectorCardinalityContext) contexts.get(2),
                                   graph,
                                   node,
                                   edge,
                                   EdgeCardinalityContext.Direction.OUTGOING,
                                   Optional.of(CardinalityContext.Operation.ADD));
        assertEquals(CommandResult.Type.INFO,
                     result.getType());
        verify(lastSourceOutEdges,
               times(1)).remove(eq(edge));
        verify(sourceOutEdges,
               times(1)).add(eq(edge));
        verify(edgeContent,
               times(1)).setSourceConnection(any(Connection.class));
        verify(edge,
               times(1)).setSourceNode(eq(node));
        verify(targetInEdges,
               times(0)).remove(any(Edge.class));
        verify(targetInEdges,
               times(0)).add(any(Edge.class));
        verify(graphIndex,
               times(0)).removeEdge(any(Edge.class));
        verify(graphIndex,
               times(0)).addEdge(any(Edge.class));
        verify(graphIndex,
               times(0)).addNode(any(Node.class));
        verify(graphIndex,
               times(0)).removeNode(any(Node.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testExecuteOnlyConnectionsHasBeenChanged() {
        when(edge.getSourceNode()).thenReturn(node);
        MagnetConnection connection = MagnetConnection.Builder.at(MAGNETX,
                                                                  MAGNETY);
        tested = new SetConnectionSourceNodeCommand(node,
                                                    edge,
                                                    connection);
        CommandResult<RuleViolation> result = tested.execute(graphCommandExecutionContext);
        assertEquals(CommandResult.Type.INFO,
                     result.getType());
        verify(ruleManager,
               never()).evaluate(eq(ruleSet),
                                 any(RuleEvaluationContext.class));
        verify(edgeContent,
               times(1)).setSourceConnection(eq(connection));
        verify(edgeContent,
               never()).setTargetConnection(any(Connection.class));
        assertEquals(sourceMagnet.get(),
                     tested.lastConnection);
    }
}
