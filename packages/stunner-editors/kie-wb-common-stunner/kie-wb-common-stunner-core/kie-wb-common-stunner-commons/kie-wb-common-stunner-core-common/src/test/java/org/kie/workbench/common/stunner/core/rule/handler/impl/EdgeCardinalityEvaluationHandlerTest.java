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
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.kie.workbench.common.stunner.core.rule.RuleViolations;
import org.kie.workbench.common.stunner.core.rule.context.CardinalityContext;
import org.kie.workbench.common.stunner.core.rule.context.EdgeCardinalityContext;
import org.kie.workbench.common.stunner.core.rule.impl.EdgeOccurrences;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
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
                                EdgeCardinalityContext.Direction.INCOMING,
                                0,
                                -1);

    private final static EdgeOccurrences RULE_IN_MAX_1 =
            new EdgeOccurrences("c1",
                                EDGE_ID,
                                ROLE,
                                EdgeCardinalityContext.Direction.INCOMING,
                                0,
                                1);

    private final static EdgeOccurrences RULE_IN_MIN_1 =
            new EdgeOccurrences("c1",
                                EDGE_ID,
                                ROLE,
                                EdgeCardinalityContext.Direction.INCOMING,
                                1,
                                -1);

    @Mock
    EdgeCardinalityContext context;

    private EdgeCardinalityEvaluationHandler tested;

    @Before
    public void setup() throws Exception {
        when(context.getEdgeRole()).thenReturn(EDGE_ID);
        when(context.getRoles()).thenReturn(ROLES);
        tested = new EdgeCardinalityEvaluationHandler();
    }

    @Test
    public void testAccept() {
        when(context.getDirection()).thenReturn(EdgeCardinalityContext.Direction.OUTGOING);
        assertFalse(tested.accepts(RULE_IN_MAX_1,
                                   context));
        when(context.getDirection()).thenReturn(EdgeCardinalityContext.Direction.INCOMING);
        assertTrue(tested.accepts(RULE_IN_MAX_1,
                                  context));
        when(context.getEdgeRole()).thenReturn("some-edge");
        assertFalse(tested.accepts(RULE_IN_MAX_1,
                                   context));
        when(context.getEdgeRole()).thenReturn(EDGE_ID);
        assertTrue(tested.accepts(RULE_IN_MAX_1,
                                  context));
        when(context.getRoles()).thenReturn(Collections.singleton("some-role"));
        assertFalse(tested.accepts(RULE_IN_MAX_1,
                                   context));
    }

    @Test
    public void testEvaluateInNoLimit() {
        when(context.getCurrentCount()).thenReturn(0);
        when(context.getDirection()).thenReturn(EdgeCardinalityContext.Direction.INCOMING);
        when(context.getOperation()).thenReturn(Optional.of(CardinalityContext.Operation.ADD));
        RuleViolations violations = tested.evaluate(RULE_IN_NO_LIMIT,
                                                    context);
        assertNotNull(violations);
        assertFalse(violations.violations(RuleViolation.Type.ERROR).iterator().hasNext());

        when(context.getCurrentCount()).thenReturn(100);
        violations = tested.evaluate(RULE_IN_NO_LIMIT,
                                     context);
        assertNotNull(violations);
        assertFalse(violations.violations(RuleViolation.Type.ERROR).iterator().hasNext());
    }

    @Test
    public void testEvaluateInMaxOneSucess() {
        when(context.getCurrentCount()).thenReturn(0);
        when(context.getDirection()).thenReturn(EdgeCardinalityContext.Direction.INCOMING);
        when(context.getOperation()).thenReturn(Optional.of(CardinalityContext.Operation.ADD));
        RuleViolations violations = tested.evaluate(RULE_IN_MAX_1,
                                                    context);
        assertNotNull(violations);
        assertFalse(violations.violations(RuleViolation.Type.ERROR).iterator().hasNext());
    }

    @Test
    public void testEvaluateInMaxOneFailed() {
        when(context.getCurrentCount()).thenReturn(1);
        when(context.getDirection()).thenReturn(EdgeCardinalityContext.Direction.INCOMING);
        when(context.getOperation()).thenReturn(Optional.of(CardinalityContext.Operation.ADD));
        RuleViolations violations = tested.evaluate(RULE_IN_MAX_1,
                                                    context);
        assertNotNull(violations);
        assertTrue(violations.violations(RuleViolation.Type.ERROR).iterator().hasNext());
    }

    @Test
    public void testEvaluateInMinOneSucess() {
        when(context.getCurrentCount()).thenReturn(0);
        when(context.getDirection()).thenReturn(EdgeCardinalityContext.Direction.INCOMING);
        when(context.getOperation()).thenReturn(Optional.of(CardinalityContext.Operation.ADD));
        RuleViolations violations = tested.evaluate(RULE_IN_MIN_1,
                                                    context);
        assertNotNull(violations);
        assertFalse(violations.violations(RuleViolation.Type.ERROR).iterator().hasNext());
    }

    @Test
    public void testEvaluateInMinOneFailed() {
        when(context.getCurrentCount()).thenReturn(1);
        when(context.getDirection()).thenReturn(EdgeCardinalityContext.Direction.INCOMING);
        when(context.getOperation()).thenReturn(Optional.of(CardinalityContext.Operation.DELETE));
        RuleViolations violations = tested.evaluate(RULE_IN_MIN_1,
                                                    context);
        assertNotNull(violations);
        assertTrue(violations.violations(RuleViolation.Type.WARNING).iterator().hasNext());
    }
}
