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

package org.kie.workbench.common.stunner.core.rule.handler.impl;

import java.util.HashSet;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.kie.workbench.common.stunner.core.rule.RuleViolations;
import org.kie.workbench.common.stunner.core.rule.context.NodeDockingContext;
import org.kie.workbench.common.stunner.core.rule.impl.CanDock;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class NodeDockingEvaluationHandlerTest extends AbstractGraphRuleHandlerTest {

    private final static CanDock RULE = new CanDock("r1",
                                                    PARENT_ID,
                                                    CANDIDATE_LABELS);
    private final static CanDock RULE_INVALID = new CanDock("r2",
                                                            PARENT_ID,
                                                            new HashSet<String>(1) {{
                                                                add("notExists");
                                                            }});
    private final DockingEvaluationHandler HANDLER = new DockingEvaluationHandler();

    @Mock
    NodeDockingContext context;

    private NodeDockingEvaluationHandler tested;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() throws Exception {
        super.setup();
        when(context.getParent()).thenReturn(parent);
        tested = new NodeDockingEvaluationHandler(definitionManager,
                                                  HANDLER);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testAcceptSuccess() {
        when(context.getParent()).thenReturn(parent);
        when(context.getCandidate()).thenReturn(candidate);
        final boolean accepts = tested.accepts(RULE,
                                               context);
        assertTrue(accepts);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testAcceptFailed() {
        when(context.getParent()).thenReturn(element);
        when(context.getCandidate()).thenReturn(candidate);
        final boolean accepts = tested.accepts(RULE,
                                               context);
        assertFalse(accepts);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testEvaluateSuccess() {
        when(context.getCandidate()).thenReturn(candidate);
        final RuleViolations violations = tested.evaluate(RULE,
                                                          context);
        assertNotNull(violations);
        assertFalse(violations.violations(RuleViolation.Type.ERROR).iterator().hasNext());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testEvaluateFailed() {
        when(context.getCandidate()).thenReturn(candidate);
        final RuleViolations violations = tested.evaluate(RULE_INVALID,
                                                          context);
        assertNotNull(violations);
        assertTrue(violations.violations(RuleViolation.Type.ERROR).iterator().hasNext());
    }
}
