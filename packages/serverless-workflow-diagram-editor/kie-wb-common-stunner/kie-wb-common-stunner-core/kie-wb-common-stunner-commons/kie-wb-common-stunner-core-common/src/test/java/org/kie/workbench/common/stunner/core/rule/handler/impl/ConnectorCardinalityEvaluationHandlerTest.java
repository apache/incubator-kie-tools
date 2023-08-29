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


package org.kie.workbench.common.stunner.core.rule.handler.impl;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.definition.adapter.DefinitionId;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewConnector;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.kie.workbench.common.stunner.core.rule.RuleViolations;
import org.kie.workbench.common.stunner.core.rule.context.CardinalityContext;
import org.kie.workbench.common.stunner.core.rule.context.ConnectorCardinalityContext;
import org.kie.workbench.common.stunner.core.rule.context.EdgeCardinalityContext;
import org.kie.workbench.common.stunner.core.rule.context.impl.StatefulGraphEvaluationState;
import org.kie.workbench.common.stunner.core.rule.impl.EdgeOccurrences;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class ConnectorCardinalityEvaluationHandlerTest extends AbstractGraphRuleHandlerTest {

    private final static String EDGE_ID = "edgeId1";

    private final static EdgeOccurrences RULE_IN_NO_LIMIT =
            new EdgeOccurrences("c1",
                                EDGE_ID,
                                CANDIDATE_ID,
                                EdgeCardinalityContext.Direction.INCOMING,
                                0,
                                -1);

    private final static EdgeOccurrences RULE_IN_MAX_1 =
            new EdgeOccurrences("c1",
                                EDGE_ID,
                                CANDIDATE_ID,
                                EdgeCardinalityContext.Direction.INCOMING,
                                0,
                                1);

    private final static EdgeOccurrences RULE_IN_MIN_1 =
            new EdgeOccurrences("c1",
                                EDGE_ID,
                                CANDIDATE_ID,
                                EdgeCardinalityContext.Direction.INCOMING,
                                1,
                                -1);

    @Mock
    ConnectorCardinalityContext context;

    @Mock
    Edge edge;

    @Mock
    ViewConnector edgeContent;

    @Mock
    Object edgeDefinition;

    private ConnectorCardinalityEvaluationHandler tested;
    private GraphEvaluationHandlerUtils evalUtils;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() throws Exception {
        super.setup();
        final Set<String> edgeLabels = Collections.singleton(EDGE_ID);
        evalUtils = spy(new GraphEvaluationHandlerUtils(definitionManager));
        StatefulGraphEvaluationState graphEvaluationState = new StatefulGraphEvaluationState(mock(Graph.class));
        when(context.getState()).thenReturn(graphEvaluationState);
        when(context.getCandidate()).thenReturn(candidate);
        when(context.getEdge()).thenReturn(edge);
        when(edge.getContent()).thenReturn(edgeContent);
        when(edge.getLabels()).thenReturn(edgeLabels);
        when(edgeContent.getDefinition()).thenReturn(edgeDefinition);
        when(definitionAdapter.getId(eq(edgeDefinition))).thenReturn(DefinitionId.build(EDGE_ID));
        tested = new ConnectorCardinalityEvaluationHandler(evalUtils,
                                                           new EdgeCardinalityEvaluationHandler());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testAccepts() {
        when(context.getDirection()).thenReturn(EdgeCardinalityContext.Direction.INCOMING);
        assertTrue(tested.accepts(RULE_IN_NO_LIMIT,
                                  context));
        when(context.getDirection()).thenReturn(EdgeCardinalityContext.Direction.OUTGOING);
        assertFalse(tested.accepts(RULE_IN_NO_LIMIT,
                                   context));
        when(context.getDirection()).thenReturn(EdgeCardinalityContext.Direction.INCOMING);
        when(definitionAdapter.getId(eq(edgeDefinition))).thenReturn(DefinitionId.build("anotherId"));
        assertFalse(tested.accepts(RULE_IN_NO_LIMIT,
                                   context));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testEvaluateInNoLimit() {
        when(evalUtils.countEdges(Mockito.<String>any(),
                                  Mockito.<List>any())).thenReturn(0);
        when(context.getDirection()).thenReturn(EdgeCardinalityContext.Direction.INCOMING);
        when(context.getOperation()).thenReturn(Optional.of(CardinalityContext.Operation.ADD));
        RuleViolations violations = tested.evaluate(RULE_IN_NO_LIMIT,
                                                    context);
        assertNotNull(violations);
        assertFalse(violations.violations(RuleViolation.Type.ERROR).iterator().hasNext());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testEvaluateInMaxOneSucess() {
        when(evalUtils.countEdges(Mockito.<String>any(),
                                  Mockito.<List>any())).thenReturn(0);
        when(context.getDirection()).thenReturn(EdgeCardinalityContext.Direction.INCOMING);
        when(context.getOperation()).thenReturn(Optional.of(CardinalityContext.Operation.ADD));
        RuleViolations violations = tested.evaluate(RULE_IN_MAX_1,
                                                    context);
        assertNotNull(violations);
        assertFalse(violations.violations(RuleViolation.Type.ERROR).iterator().hasNext());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testEvaluateInMaxOneFailed() {
        when(evalUtils.countEdges(Mockito.<String>any(),
                                  Mockito.<List>any())).thenReturn(1);
        when(context.getDirection()).thenReturn(EdgeCardinalityContext.Direction.INCOMING);
        when(context.getOperation()).thenReturn(Optional.of(CardinalityContext.Operation.ADD));
        RuleViolations violations = tested.evaluate(RULE_IN_MAX_1,
                                                    context);
        assertNotNull(violations);
        assertTrue(violations.violations(RuleViolation.Type.ERROR).iterator().hasNext());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testEvaluateInMinOneSuccess() {
        when(evalUtils.countEdges(Mockito.<String>any(),
                                  Mockito.<List>any())).thenReturn(0);
        when(context.getDirection()).thenReturn(EdgeCardinalityContext.Direction.INCOMING);
        when(context.getOperation()).thenReturn(Optional.of(CardinalityContext.Operation.ADD));
        RuleViolations violations = tested.evaluate(RULE_IN_MIN_1,
                                                    context);
        assertNotNull(violations);
        assertFalse(violations.violations(RuleViolation.Type.ERROR).iterator().hasNext());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testEvaluateInMinOneFailed() {
        when(evalUtils.countEdges(Mockito.<String>any(),
                                  Mockito.<List>any())).thenReturn(1);
        when(context.getDirection()).thenReturn(EdgeCardinalityContext.Direction.INCOMING);
        when(context.getOperation()).thenReturn(Optional.of(CardinalityContext.Operation.DELETE));
        RuleViolations violations = tested.evaluate(RULE_IN_MIN_1,
                                                    context);
        assertNotNull(violations);
        assertTrue(violations.violations(RuleViolation.Type.WARNING).iterator().hasNext());
    }
}
