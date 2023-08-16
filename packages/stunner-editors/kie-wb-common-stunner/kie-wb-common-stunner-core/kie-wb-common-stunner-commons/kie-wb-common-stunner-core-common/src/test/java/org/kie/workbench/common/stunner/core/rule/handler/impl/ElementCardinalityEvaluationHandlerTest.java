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

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.kie.workbench.common.stunner.core.rule.RuleViolations;
import org.kie.workbench.common.stunner.core.rule.context.CardinalityContext;
import org.kie.workbench.common.stunner.core.rule.context.ElementCardinalityContext;
import org.kie.workbench.common.stunner.core.rule.context.GraphEvaluationState;
import org.kie.workbench.common.stunner.core.rule.impl.Occurrences;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class ElementCardinalityEvaluationHandlerTest extends AbstractGraphRuleHandlerTest {

    private final static Occurrences RULE_NO_LIMIT = new Occurrences("r1",
                                                                     CANDIDATE_ROLE1,
                                                                     0,
                                                                     -1);
    private final static Occurrences RULE_MIN_1 = new Occurrences("r2",
                                                                  CANDIDATE_ROLE1,
                                                                  1,
                                                                  -1);
    private final static Occurrences RULE_MAX_1 = new Occurrences("r3",
                                                                  CANDIDATE_ROLE1,
                                                                  0,
                                                                  1);
    private final static Occurrences RULE_MAX_0 = new Occurrences("r3",
                                                                  CANDIDATE_ROLE1,
                                                                  0,
                                                                  0);

    private final CardinalityEvaluationHandler HANDLER = new CardinalityEvaluationHandler();

    @Mock
    ElementCardinalityContext context;

    private ElementCardinalityEvaluationHandler tested;
    private Collection candidates;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() throws Exception {
        super.setup();
        candidates = Collections.singleton(candidate);
        tested = spy(new ElementCardinalityEvaluationHandler(definitionManager,
                                                             HANDLER));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testAcceptsNoOp() {
        when(context.getCandidates()).thenReturn(Collections.emptyList());
        assertTrue(tested.accepts(RULE_NO_LIMIT,
                                  context));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testAccepts() {
        when(context.getOperation()).thenReturn(Optional.of(CardinalityContext.Operation.ADD));
        when(context.getCandidates()).thenReturn(candidates);
        assertTrue(tested.accepts(RULE_NO_LIMIT,
                                  context));
        when(context.getCandidates()).thenReturn(Collections.singleton(parent));
        assertFalse(tested.accepts(RULE_NO_LIMIT,
                                   context));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testEvaluateSuccess() {
        final Map<String, Integer> count = new HashMap<String, Integer>(2) {{
            put(CANDIDATE_ROLE1,
                0);
            put(CANDIDATE_ROLE2,
                0);
        }};
        doReturn(count).when(tested).countLabels(Mockito.<GraphEvaluationState>any(),
                                                 Mockito.<Set>any());
        when(context.getCandidates()).thenReturn(candidates);
        when(context.getOperation()).thenReturn(Optional.of(CardinalityContext.Operation.ADD));
        final RuleViolations violations = tested.evaluate(RULE_NO_LIMIT,
                                                          context);
        assertNotNull(violations);
        assertFalse(violations.violations(RuleViolation.Type.ERROR).iterator().hasNext());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testEvaluateSuccessAgain() {
        final Map<String, Integer> count = new HashMap<String, Integer>(2) {{
            put(CANDIDATE_ROLE1,
                100);
            put(CANDIDATE_ROLE2,
                0);
        }};
        doReturn(count).when(tested).countLabels(Mockito.<GraphEvaluationState>any(),
                                                 Mockito.<Set>any());
        when(context.getCandidates()).thenReturn(candidates);
        when(context.getOperation()).thenReturn(Optional.of(CardinalityContext.Operation.ADD));
        final RuleViolations violations = tested.evaluate(RULE_NO_LIMIT,
                                                          context);
        assertNotNull(violations);
        assertFalse(violations.violations(RuleViolation.Type.ERROR).iterator().hasNext());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testMin1EvaluateFailed() {
        final Map<String, Integer> count = new HashMap<String, Integer>(1) {{
            put(CANDIDATE_ROLE1,
                0);
        }};
        doReturn(count).when(tested).countLabels(Mockito.<GraphEvaluationState>any(),
                                                 Mockito.<Set>any());
        when(context.getCandidates()).thenReturn(Collections.emptyList());
        when(context.getOperation()).thenReturn(Optional.empty());
        final RuleViolations violations = tested.evaluate(RULE_MIN_1,
                                                          context);
        assertNotNull(violations);
        assertTrue(violations.violations(RuleViolation.Type.WARNING).iterator().hasNext());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testMin1AddOk() {
        final Map<String, Integer> count = new HashMap<String, Integer>(1) {{
            put(CANDIDATE_ROLE1,
                0);
        }};
        doReturn(count).when(tested).countLabels(Mockito.<GraphEvaluationState>any(),
                                                 Mockito.<Set>any());
        when(context.getCandidates()).thenReturn(candidates);
        when(context.getOperation()).thenReturn(Optional.of(CardinalityContext.Operation.ADD));
        final RuleViolations violations = tested.evaluate(RULE_MIN_1,
                                                          context);
        assertNotNull(violations);
        assertFalse(violations.violations(RuleViolation.Type.ERROR).iterator().hasNext());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testMin1DeleteOk() {
        final Map<String, Integer> count = new HashMap<String, Integer>(1) {{
            put(CANDIDATE_ROLE1,
                2);
        }};
        doReturn(count).when(tested).countLabels(Mockito.<GraphEvaluationState>any(),
                                                 Mockito.<Set>any());
        when(context.getCandidates()).thenReturn(candidates);
        when(context.getOperation()).thenReturn(Optional.of(CardinalityContext.Operation.DELETE));
        final RuleViolations violations = tested.evaluate(RULE_MIN_1,
                                                          context);
        assertNotNull(violations);
        assertFalse(violations.violations(RuleViolation.Type.ERROR).iterator().hasNext());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testMin1DeleteFailed() {
        final Map<String, Integer> count = new HashMap<String, Integer>(2) {{
            put(CANDIDATE_ROLE1,
                1);
            put(CANDIDATE_ROLE2,
                0);
        }};
        doReturn(count).when(tested).countLabels(Mockito.<GraphEvaluationState>any(),
                                                 Mockito.<Set>any());
        when(context.getCandidates()).thenReturn(candidates);
        when(context.getOperation()).thenReturn(Optional.of(CardinalityContext.Operation.DELETE));
        final RuleViolations violations = tested.evaluate(RULE_MIN_1,
                                                          context);
        assertNotNull(violations);
        assertTrue(violations.violations(RuleViolation.Type.WARNING).iterator().hasNext());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testMax1Ok() {
        final Map<String, Integer> count = new HashMap<String, Integer>(2) {{
            put(CANDIDATE_ROLE1,
                0);
            put(CANDIDATE_ROLE2,
                0);
        }};
        doReturn(count).when(tested).countLabels(Mockito.<GraphEvaluationState>any(),
                                                 Mockito.<Set>any());
        when(context.getCandidates()).thenReturn(candidates);
        when(context.getOperation()).thenReturn(Optional.of(CardinalityContext.Operation.ADD));
        final RuleViolations violations = tested.evaluate(RULE_MAX_1,
                                                          context);
        assertNotNull(violations);
        assertFalse(violations.violations(RuleViolation.Type.ERROR).iterator().hasNext());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testMax1Failed() {
        final Map<String, Integer> count = new HashMap<String, Integer>(2) {{
            put(CANDIDATE_ROLE1,
                1);
            put(CANDIDATE_ROLE2,
                0);
        }};
        doReturn(count).when(tested).countLabels(Mockito.<GraphEvaluationState>any(),
                                                 Mockito.<Set>any());
        when(context.getCandidates()).thenReturn(candidates);
        when(context.getOperation()).thenReturn(Optional.of(CardinalityContext.Operation.ADD));
        final RuleViolations violations = tested.evaluate(RULE_MAX_1,
                                                          context);
        assertNotNull(violations);
        assertTrue(violations.violations(RuleViolation.Type.ERROR).iterator().hasNext());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testMax0Failed() {
        final Map<String, Integer> count = new HashMap<String, Integer>(1) {{
            put(CANDIDATE_ROLE1,
                0);
        }};
        doReturn(count).when(tested).countLabels(Mockito.<GraphEvaluationState>any(),
                                                 Mockito.<Set>any());
        when(context.getCandidates()).thenReturn(candidates);
        when(context.getOperation()).thenReturn(Optional.of(CardinalityContext.Operation.ADD));
        final RuleViolations violations = tested.evaluate(RULE_MAX_0,
                                                          context);
        assertNotNull(violations);
        assertTrue(violations.violations(RuleViolation.Type.ERROR).iterator().hasNext());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testMax0EvaluateSuccess() {
        final Map<String, Integer> count = new HashMap<String, Integer>(1) {{
            put(CANDIDATE_ROLE1,
                0);
        }};
        doReturn(count).when(tested).countLabels(Mockito.<GraphEvaluationState>any(),
                                                 Mockito.<Set>any());
        when(context.getCandidates()).thenReturn(Collections.emptyList());
        when(context.getOperation()).thenReturn(Optional.empty());
        final RuleViolations violations = tested.evaluate(RULE_MAX_0,
                                                          context);
        assertNotNull(violations);
        assertFalse(violations.violations(RuleViolation.Type.ERROR).iterator().hasNext());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testMax0EvaluateFailed() {
        final Map<String, Integer> count = new HashMap<String, Integer>(1) {{
            put(CANDIDATE_ROLE1,
                1);
        }};
        doReturn(count).when(tested).countLabels(Mockito.<GraphEvaluationState>any(),
                                                 Mockito.<Set>any());
        when(context.getCandidates()).thenReturn(Collections.emptyList());
        when(context.getOperation()).thenReturn(Optional.empty());
        final RuleViolations violations = tested.evaluate(RULE_MAX_0,
                                                          context);
        assertNotNull(violations);
        assertTrue(violations.violations(RuleViolation.Type.WARNING).iterator().hasNext());
    }
}
