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

package org.kie.workbench.common.stunner.core.rule.impl.graph;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.kie.workbench.common.stunner.core.rule.RuleViolations;
import org.kie.workbench.common.stunner.core.rule.model.ModelDockingRuleManager;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class GraphDockingRuleManagerImplTest extends AbstractGraphRuleManagerTest {

    @Mock
    ModelDockingRuleManager modelDockingRuleManager;

    private GraphDockingRuleManagerImpl tested;

    @Before
    public void setup() {
        super.setup();
        this.tested = new GraphDockingRuleManagerImpl(definitionManager,
                                                      modelDockingRuleManager);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testEvaluateAccept() {
        RuleViolations violations = mockNoViolations();
        when(modelDockingRuleManager.evaluate(eq(DEFINITION_ID),
                                              eq(CANDIDATE_LABELS))).thenReturn(violations);
        final RuleViolations result = tested.evaluate(element,
                                                      candidate);
        assertNotNull(result);
        assertFalse(result.violations(RuleViolation.Type.ERROR).iterator().hasNext());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testEvaluateDeny() {
        RuleViolations violations = mockWithViolations();
        when(modelDockingRuleManager.evaluate(eq(DEFINITION_ID),
                                              eq(CANDIDATE_LABELS))).thenReturn(violations);
        final RuleViolations result = tested.evaluate(element,
                                                      candidate);
        assertNotNull(result);
        assertTrue(result.violations(RuleViolation.Type.ERROR).iterator().hasNext());
    }
}
