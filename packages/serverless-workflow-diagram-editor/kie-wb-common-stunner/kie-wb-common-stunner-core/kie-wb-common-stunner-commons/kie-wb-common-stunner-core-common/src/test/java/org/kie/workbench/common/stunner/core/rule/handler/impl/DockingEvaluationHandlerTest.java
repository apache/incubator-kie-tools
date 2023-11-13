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
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.kie.workbench.common.stunner.core.rule.RuleViolations;
import org.kie.workbench.common.stunner.core.rule.context.DockingContext;
import org.kie.workbench.common.stunner.core.rule.impl.CanDock;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class DockingEvaluationHandlerTest {

    private final static String PARENT_ID = "parentId";
    private final static Set<String> PARENT_ROLES =
            new HashSet<String>(2) {{
                add(PARENT_ID);
                add("drole1");
                add("drole2");
            }};
    private final static CanDock RULE = new CanDock("r1",
                                                    PARENT_ID,
                                                    PARENT_ROLES);

    @Mock
    DockingContext context;

    private DockingEvaluationHandler tested;

    @Before
    public void setup() throws Exception {
        tested = new DockingEvaluationHandler();
    }

    @Test
    public void testAccepts() {
        Set<String> candidateRoles = Collections.singleton("role2");
        Set<String> parentRoles = Collections.singleton(PARENT_ID);
        when(context.getParentRoles()).thenReturn(parentRoles);
        when(context.getCandidateRoles()).thenReturn(candidateRoles);
        assertTrue(tested.accepts(RULE,
                                  context));
        parentRoles = Collections.singleton("anotherParent");
        when(context.getParentRoles()).thenReturn(parentRoles);
        assertFalse(tested.accepts(RULE,
                                   context));
    }

    @Test
    public void testEvaluateSuccess() {
        final Set<String> candidateRoles = new HashSet<String>(1) {{
            add("drole2");
        }};
        when(context.getParentRoles()).thenReturn(PARENT_ROLES);
        when(context.getCandidateRoles()).thenReturn(candidateRoles);
        final RuleViolations violations = tested.evaluate(RULE,
                                                          context);
        assertNotNull(violations);
        assertFalse(violations.violations(RuleViolation.Type.ERROR).iterator().hasNext());
    }

    @Test
    public void testEvaluateFailed() {
        final Set<String> candidateRoles = new HashSet<String>(1) {{
            add("drole4");
        }};
        when(context.getParentRoles()).thenReturn(PARENT_ROLES);
        when(context.getCandidateRoles()).thenReturn(candidateRoles);
        final RuleViolations violations = tested.evaluate(RULE,
                                                          context);
        assertNotNull(violations);
        assertTrue(violations.violations(RuleViolation.Type.ERROR).iterator().hasNext());
    }
}
