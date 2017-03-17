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
import org.kie.workbench.common.stunner.core.rule.impl.Occurrences;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CardinalityEvaluationHandlerTest {

    private final static String ROLE = "theRole";
    private final static Set<String> ROLES =
            new HashSet<String>(2) {{
                add(ROLE);
            }};

    private final static Occurrences RULE_NO_LIMIT = new Occurrences("c1",
                                                                     ROLE,
                                                                     0,
                                                                     -1);
    private final static Occurrences RULE_MIN_1 = new Occurrences("c2",
                                                                  ROLE,
                                                                  1,
                                                                  -1);
    private final static Occurrences RULE_MAX_1 = new Occurrences("c2",
                                                                  ROLE,
                                                                  0,
                                                                  1);

    @Mock
    CardinalityContext context;

    private CardinalityEvaluationHandler tested;

    @Before
    public void setup() throws Exception {
        when(context.getRoles()).thenReturn(ROLES);
        tested = new CardinalityEvaluationHandler();
    }

    @Test
    public void testEvaluateNoLimit() {
        when(context.getCandidateCount()).thenReturn(0);
        when(context.getOperation()).thenReturn(CardinalityContext.Operation.ADD);
        RuleViolations violations = tested.evaluate(RULE_NO_LIMIT,
                                                    context);
        assertNotNull(violations);
        assertFalse(violations.violations(RuleViolation.Type.ERROR).iterator().hasNext());

        when(context.getCandidateCount()).thenReturn(100);
        when(context.getOperation()).thenReturn(CardinalityContext.Operation.ADD);
        violations = tested.evaluate(RULE_NO_LIMIT,
                                     context);
        assertNotNull(violations);
        assertFalse(violations.violations(RuleViolation.Type.ERROR).iterator().hasNext());
    }

    @Test
    public void testMinOneSuccess() {
        when(context.getCandidateCount()).thenReturn(0);
        when(context.getOperation()).thenReturn(CardinalityContext.Operation.ADD);
        RuleViolations violations = tested.evaluate(RULE_MIN_1,
                                                    context);
        assertNotNull(violations);
        assertFalse(violations.violations(RuleViolation.Type.ERROR).iterator().hasNext());
    }

    @Test
    public void testMinOneFailed1() {
        when(context.getCandidateCount()).thenReturn(1);
        when(context.getOperation()).thenReturn(CardinalityContext.Operation.DELETE);
        RuleViolations violations = tested.evaluate(RULE_MIN_1,
                                                    context);
        assertNotNull(violations);
        assertTrue(violations.violations(RuleViolation.Type.ERROR).iterator().hasNext());
    }

    @Test
    public void testMaxOneSuccess() {
        when(context.getCandidateCount()).thenReturn(0);
        when(context.getOperation()).thenReturn(CardinalityContext.Operation.ADD);
        RuleViolations violations = tested.evaluate(RULE_MAX_1,
                                                    context);
        assertNotNull(violations);
        assertFalse(violations.violations(RuleViolation.Type.ERROR).iterator().hasNext());
    }

    @Test
    public void testMaxOneFailed1() {
        when(context.getCandidateCount()).thenReturn(1);
        when(context.getOperation()).thenReturn(CardinalityContext.Operation.ADD);
        RuleViolations violations = tested.evaluate(RULE_MAX_1,
                                                    context);
        assertNotNull(violations);
        assertTrue(violations.violations(RuleViolation.Type.ERROR).iterator().hasNext());
    }
}
