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


package org.kie.workbench.common.stunner.core.validation.impl;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.TestingGraphInstanceBuilder;
import org.kie.workbench.common.stunner.core.TestingGraphMockHandler;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.DefinitionSet;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.tree.TreeWalkTraverseProcessorImpl;
import org.kie.workbench.common.stunner.core.rule.RuleEvaluationContext;
import org.kie.workbench.common.stunner.core.rule.RuleManager;
import org.kie.workbench.common.stunner.core.rule.RuleSet;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.kie.workbench.common.stunner.core.rule.context.ConnectorCardinalityContext;
import org.kie.workbench.common.stunner.core.rule.context.EdgeCardinalityContext;
import org.kie.workbench.common.stunner.core.rule.context.ElementCardinalityContext;
import org.kie.workbench.common.stunner.core.rule.context.GraphConnectionContext;
import org.kie.workbench.common.stunner.core.rule.context.NodeContainmentContext;
import org.kie.workbench.common.stunner.core.rule.violations.EmptyConnectionViolation;
import org.kie.workbench.common.stunner.core.validation.Violation;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.kie.workbench.common.stunner.core.TestingGraphUtils.verifyCardinality;
import static org.kie.workbench.common.stunner.core.TestingGraphUtils.verifyConnection;
import static org.kie.workbench.common.stunner.core.TestingGraphUtils.verifyConnectorCardinality;
import static org.kie.workbench.common.stunner.core.TestingGraphUtils.verifyContainment;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class GraphValidatorImplTest {

    private final static String DEF_SET_ID = "ds1";

    @Mock
    private Object defSetBean;

    private GraphValidatorImpl tested;
    private TestingGraphMockHandler graphTestHandler;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() throws Exception {
        this.graphTestHandler = new TestingGraphMockHandler();
        when(graphTestHandler.getDefinitionSetRegistry().getDefinitionSetById(eq(DEF_SET_ID))).thenReturn(defSetBean);
        when(graphTestHandler.getRuleAdapter().getRuleSet(eq(defSetBean))).thenReturn(graphTestHandler.ruleSet);
        this.tested = new GraphValidatorImpl(graphTestHandler.getDefinitionManager(),
                                             graphTestHandler.getRuleManager(),
                                             new TreeWalkTraverseProcessorImpl());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testValidateGraph1() {
        final RuleManager ruleManager = graphTestHandler.getRuleManager();
        final RuleSet ruleSet = graphTestHandler.ruleSet;
        final Graph<DefinitionSet, Node> graph = graphTestHandler.graph;
        final TestingGraphInstanceBuilder.TestGraph1 testGraph1 = TestingGraphInstanceBuilder.newGraph1(graphTestHandler);
        tested.validate(graph,
                        ruleSet,
                        this::assertNoError);
        final int evalCount = testGraph1.evaluationsCount + 10;
        final ArgumentCaptor<RuleEvaluationContext> contextCaptor = ArgumentCaptor.forClass(RuleEvaluationContext.class);
        verify(ruleManager,
               times(evalCount)).evaluate(eq(ruleSet),
                                          contextCaptor.capture());
        final List<RuleEvaluationContext> contexts = contextCaptor.getAllValues();
        assertEquals(evalCount,
                     contexts.size());
        int cindex = testGraph1.evaluationsCount;
        verifyCardinality((ElementCardinalityContext) contexts.get(cindex++),
                          graph);
        verifyContainment((NodeContainmentContext) contexts.get(cindex++),
                          graph,
                          testGraph1.startNode);
        verifyConnection((GraphConnectionContext) contexts.get(cindex++),
                         testGraph1.edge1,
                         testGraph1.startNode,
                         testGraph1.intermNode);
        verifyConnectorCardinality((ConnectorCardinalityContext) contexts.get(cindex++),
                                   graph,
                                   testGraph1.intermNode,
                                   testGraph1.edge1,
                                   EdgeCardinalityContext.Direction.INCOMING,
                                   Optional.empty());
        verifyConnectorCardinality((ConnectorCardinalityContext) contexts.get(cindex++),
                                   graph,
                                   testGraph1.startNode,
                                   testGraph1.edge1,
                                   EdgeCardinalityContext.Direction.OUTGOING,
                                   Optional.empty());
        verifyContainment((NodeContainmentContext) contexts.get(cindex++),
                          graph,
                          testGraph1.intermNode);
        verifyConnection((GraphConnectionContext) contexts.get(cindex++),
                         testGraph1.edge2,
                         testGraph1.intermNode,
                         testGraph1.endNode);
        verifyConnectorCardinality((ConnectorCardinalityContext) contexts.get(cindex++),
                                   graph,
                                   testGraph1.endNode,
                                   testGraph1.edge2,
                                   EdgeCardinalityContext.Direction.INCOMING,
                                   Optional.empty());
        verifyConnectorCardinality((ConnectorCardinalityContext) contexts.get(cindex++),
                                   graph,
                                   testGraph1.intermNode,
                                   testGraph1.edge2,
                                   EdgeCardinalityContext.Direction.OUTGOING,
                                   Optional.empty());
        verifyContainment((NodeContainmentContext) contexts.get(cindex++),
                          graph,
                          testGraph1.endNode);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testValidateGraph2() {
        final RuleManager ruleManager = graphTestHandler.getRuleManager();
        final RuleSet ruleSet = graphTestHandler.ruleSet;
        final Graph<DefinitionSet, Node> graph = graphTestHandler.graph;
        final TestingGraphInstanceBuilder.TestGraph2 testGraph2 = TestingGraphInstanceBuilder.newGraph2(graphTestHandler);
        tested.validate(getGraph(),
                        graphTestHandler.ruleSet,
                        this::assertNoError);
        final int evalCount = testGraph2.evaluationsCount + 11;
        final ArgumentCaptor<RuleEvaluationContext> contextCaptor = ArgumentCaptor.forClass(RuleEvaluationContext.class);
        verify(ruleManager,
               times(evalCount)).evaluate(eq(ruleSet),
                                          contextCaptor.capture());
        final List<RuleEvaluationContext> contexts = contextCaptor.getAllValues();
        assertEquals(evalCount,
                     contexts.size());
        int cindex = testGraph2.evaluationsCount;
        verifyCardinality((ElementCardinalityContext) contexts.get(cindex++),
                          graph);
        verifyContainment((NodeContainmentContext) contexts.get(cindex++),
                          graph,
                          testGraph2.parentNode);
        verifyContainment((NodeContainmentContext) contexts.get(cindex++),
                          testGraph2.parentNode,
                          testGraph2.startNode);
        verifyConnection((GraphConnectionContext) contexts.get(cindex++),
                         testGraph2.edge1,
                         testGraph2.startNode,
                         testGraph2.intermNode);
        verifyConnectorCardinality((ConnectorCardinalityContext) contexts.get(cindex++),
                                   graph,
                                   testGraph2.intermNode,
                                   testGraph2.edge1,
                                   EdgeCardinalityContext.Direction.INCOMING,
                                   Optional.empty());
        verifyConnectorCardinality((ConnectorCardinalityContext) contexts.get(cindex++),
                                   graph,
                                   testGraph2.startNode,
                                   testGraph2.edge1,
                                   EdgeCardinalityContext.Direction.OUTGOING,
                                   Optional.empty());
        verifyContainment((NodeContainmentContext) contexts.get(cindex++),
                          testGraph2.parentNode,
                          testGraph2.intermNode);
        verifyConnection((GraphConnectionContext) contexts.get(cindex++),
                         testGraph2.edge2,
                         testGraph2.intermNode,
                         testGraph2.endNode);
        verifyConnectorCardinality((ConnectorCardinalityContext) contexts.get(cindex++),
                                   graph,
                                   testGraph2.endNode,
                                   testGraph2.edge2,
                                   EdgeCardinalityContext.Direction.INCOMING,
                                   Optional.empty());
        verifyConnectorCardinality((ConnectorCardinalityContext) contexts.get(cindex++),
                                   graph,
                                   testGraph2.intermNode,
                                   testGraph2.edge2,
                                   EdgeCardinalityContext.Direction.OUTGOING,
                                   Optional.empty());
        verifyContainment((NodeContainmentContext) contexts.get(cindex++),
                          testGraph2.parentNode,
                          testGraph2.endNode);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testValidateEmptyViewConnectorNodes() {
        final RuleSet ruleSet = graphTestHandler.ruleSet;
        final Graph<DefinitionSet, Node> graph = graphTestHandler.graph;
        final TestingGraphInstanceBuilder.TestGraph1 testGraph1 = TestingGraphInstanceBuilder.newGraph1(graphTestHandler);
        // Update the edge2 and remove the connection's target node.
        // From this point, a validation error is expected.
        graphTestHandler.removeTargetConnection(testGraph1.edge2);
        tested.validate(graph,
                        ruleSet,
                        ruleViolations -> {
                            assertEquals(1,
                                         ruleViolations.size());
                            final RuleViolation violation = ruleViolations.iterator().next();
                            assertNotNull(violation);
                            assertTrue(violation instanceof EmptyConnectionViolation);
                            EmptyConnectionViolation v = (EmptyConnectionViolation) violation;
                            final Optional<Object[]> arguments = v.getArguments();
                            assertTrue(arguments.isPresent());
                            assertEquals(testGraph1.edge2.getUUID(),
                                         arguments.get()[0]);
                            assertEquals(testGraph1.intermNode.getUUID(),
                                         arguments.get()[1]);
                            assertNull(arguments.get()[2]);
                        });
    }

    private void assertNoError(final Collection<RuleViolation> violations) {
        assertFalse(violations.stream()
                            .filter(v -> Violation.Type.ERROR.equals(v.getViolationType()))
                            .findAny()
                            .isPresent());
    }

    @SuppressWarnings("unchecked")
    private Graph<?, Node<?, Edge>> getGraph() {
        return (Graph) graphTestHandler.graph;
    }
}
