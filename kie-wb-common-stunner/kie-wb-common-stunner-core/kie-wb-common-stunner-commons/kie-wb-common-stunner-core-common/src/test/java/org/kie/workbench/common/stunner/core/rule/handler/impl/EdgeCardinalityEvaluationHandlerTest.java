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

package org.kie.workbench.common.stunner.core.rule.handler.impl;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.kie.workbench.common.stunner.core.rule.RuleViolations;
import org.kie.workbench.common.stunner.core.rule.context.CardinalityContext;
import org.kie.workbench.common.stunner.core.rule.context.ConnectorCardinalityContext;
import org.kie.workbench.common.stunner.core.rule.context.EdgeCardinalityContext;
import org.kie.workbench.common.stunner.core.rule.impl.EdgeOccurrences;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class EdgeCardinalityEvaluationHandlerTest {

    private final static String ROLE = "theRole";
    private final static String EDGE_ID = "edge1";
    private final static Set<String> ROLES =
            new HashSet<String>(2) {{
                add(ROLE);
            }};

    private final static EdgeOccurrences RULE_IN_NO_LIMIT =
            new EdgeOccurrences("c1",
                                EDGE_ID,
                                ROLE,
                                ConnectorCardinalityContext.Direction.INCOMING,
                                0,
                                -1);

    private final static EdgeOccurrences RULE_IN_MAX_1 =
            new EdgeOccurrences("c1",
                                EDGE_ID,
                                ROLE,
                                ConnectorCardinalityContext.Direction.INCOMING,
                                0,
                                1);

    private final static EdgeOccurrences RULE_IN_MIN_1 =
            new EdgeOccurrences("c1",
                                EDGE_ID,
                                ROLE,
                                ConnectorCardinalityContext.Direction.INCOMING,
                                1,
                                -1);

    @Mock
    EdgeCardinalityContext context;

    private EdgeCardinalityEvaluationHandler tested;

    @Before
    public void setup() throws Exception {
        when(context.getEdgeId()).thenReturn(EDGE_ID);
        when(context.getRoles()).thenReturn(ROLES);
        tested = new EdgeCardinalityEvaluationHandler();
    }

    @Test
    public void testEvaluateInNoLimit() {
        when(context.getCandidateCount()).thenReturn(0);
        when(context.getDirection()).thenReturn(ConnectorCardinalityContext.Direction.INCOMING);
        when(context.getOperation()).thenReturn(CardinalityContext.Operation.ADD);
        RuleViolations violations = tested.evaluate(RULE_IN_NO_LIMIT,
                                                    context);
        assertNotNull(violations);
        assertFalse(violations.violations(RuleViolation.Type.ERROR).iterator().hasNext());

        when(context.getCandidateCount()).thenReturn(100);
        violations = tested.evaluate(RULE_IN_NO_LIMIT,
                                     context);
        assertNotNull(violations);
        assertFalse(violations.violations(RuleViolation.Type.ERROR).iterator().hasNext());
    }

    @Test
    public void testEvaluateInMaxOneSucess() {
        when(context.getCandidateCount()).thenReturn(0);
        when(context.getDirection()).thenReturn(ConnectorCardinalityContext.Direction.INCOMING);
        when(context.getOperation()).thenReturn(CardinalityContext.Operation.ADD);
        RuleViolations violations = tested.evaluate(RULE_IN_MAX_1,
                                                    context);
        assertNotNull(violations);
        assertFalse(violations.violations(RuleViolation.Type.ERROR).iterator().hasNext());
    }

    @Test
    public void testEvaluateInMaxOneFailed() {
        when(context.getCandidateCount()).thenReturn(1);
        when(context.getDirection()).thenReturn(ConnectorCardinalityContext.Direction.INCOMING);
        when(context.getOperation()).thenReturn(CardinalityContext.Operation.ADD);
        RuleViolations violations = tested.evaluate(RULE_IN_MAX_1,
                                                    context);
        assertNotNull(violations);
        assertTrue(violations.violations(RuleViolation.Type.ERROR).iterator().hasNext());
    }

    @Test
    public void testEvaluateInMinOneSucess() {
        when(context.getCandidateCount()).thenReturn(0);
        when(context.getDirection()).thenReturn(ConnectorCardinalityContext.Direction.INCOMING);
        when(context.getOperation()).thenReturn(CardinalityContext.Operation.ADD);
        RuleViolations violations = tested.evaluate(RULE_IN_MIN_1,
                                                    context);
        assertNotNull(violations);
        assertFalse(violations.violations(RuleViolation.Type.ERROR).iterator().hasNext());
    }

    @Test
    public void testEvaluateInMinOneFailed() {
        when(context.getCandidateCount()).thenReturn(1);
        when(context.getDirection()).thenReturn(ConnectorCardinalityContext.Direction.INCOMING);
        when(context.getOperation()).thenReturn(CardinalityContext.Operation.DELETE);
        RuleViolations violations = tested.evaluate(RULE_IN_MIN_1,
                                                    context);
        assertNotNull(violations);
        assertTrue(violations.violations(RuleViolation.Type.ERROR).iterator().hasNext());
    }
}
