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

package org.kie.workbench.common.stunner.core.graph.command.impl;

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
import org.kie.workbench.common.stunner.core.rule.RuleSet;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.kie.workbench.common.stunner.core.rule.context.CardinalityContext;
import org.kie.workbench.common.stunner.core.rule.context.ConnectorCardinalityContext;
import org.kie.workbench.common.stunner.core.rule.context.EdgeCardinalityContext;
import org.kie.workbench.common.stunner.core.rule.context.GraphConnectionContext;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.kie.workbench.common.stunner.core.TestingGraphUtils.verifyConnection;
import static org.kie.workbench.common.stunner.core.TestingGraphUtils.verifyConnectorCardinality;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class AddConnectorCommandTest extends AbstractGraphCommandTest {

    private static final String NODE_UUID = "nodeUUID";
    private static final String EDGE_UUID = "edgeUUID";

    private Node node;
    @Mock
    private Edge edge;
    @Mock
    private ViewConnector connContent;

    private Optional<Connection> sourceMagnet;
    private Optional<Connection> targetMagnet;

    private AddConnectorCommand tested;

    @Before
    public void setup() throws Exception {
        super.init();
        node = mockNode(NODE_UUID);
        sourceMagnet = Optional.of(MagnetConnection.Builder.at(0d,
                                                               0d));
        targetMagnet = Optional.of(MagnetConnection.Builder.at(0d,
                                                               0d));
        when(edge.getUUID()).thenReturn(EDGE_UUID);
        when(edge.getContent()).thenReturn(connContent);
        when(connContent.getSourceConnection()).thenReturn(sourceMagnet);
        when(connContent.getTargetConnection()).thenReturn(targetMagnet);
        when(graphIndex.getNode(eq(NODE_UUID))).thenReturn(node);
        this.tested = new AddConnectorCommand(NODE_UUID,
                                              edge,
                                              MagnetConnection.Builder.at(0d,
                                                                          0d));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testAllow() {
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
                         node,
                         null);
        verifyConnectorCardinality((ConnectorCardinalityContext) contexts.get(1),
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
               times(0)).evaluate(any(RuleSet.class),
                                  any(RuleEvaluationContext.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testExecute() {
        CommandResult<RuleViolation> result = tested.execute(graphCommandExecutionContext);
        assertEquals(CommandResult.Type.INFO,
                     result.getType());
        assertTrue(node.getOutEdges().size() == 1);
        assertEquals(edge,
                     node.getOutEdges().get(0));
        verify(graphIndex,
               times(1)).addEdge(eq(edge));
        verify(graphIndex,
               times(0)).addNode(any(Node.class));
    }
}
