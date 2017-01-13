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

package org.kie.workbench.common.stunner.core.rule.impl.model;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.rule.ConnectionRule;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.kie.workbench.common.stunner.core.rule.RuleViolations;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ModelConnectionRuleManagerImplTest {

    private final static String EDGE_ID = "edgeId";
    private final static String P1_S1 = "p1s1";
    private final static String P1_E1 = "p1e1";
    private final static String P2_S1 = "p2s1";
    private final static String P2_E1 = "p2e1";

    @Mock
    ConnectionRule rule;
    @Mock
    ConnectionRule.PermittedConnection p1;
    @Mock
    ConnectionRule.PermittedConnection p2;

    private ModelConnectionRuleManagerImpl tested;
    private Set<ConnectionRule.PermittedConnection> p = new HashSet<>(2);

    @Before
    public void setup() throws Exception {
        when(p1.getStartRole()).thenReturn(P1_S1);
        when(p1.getEndRole()).thenReturn(P1_E1);
        p.add(p1);
        when(p2.getStartRole()).thenReturn(P2_S1);
        when(p2.getEndRole()).thenReturn(P2_E1);
        p.add(p2);
        when(rule.getId()).thenReturn(EDGE_ID);
        when(rule.getPermittedConnections()).thenReturn(p);
        tested = new ModelConnectionRuleManagerImpl();
        tested.addRule(rule);
    }

    @Test
    public void testP1Accept() {
        final Set<String> i1 = new HashSet<String>(1) {{
            add(P1_E1);
        }};
        final Set<String> o1 = new HashSet<String>(1) {{
            add(P1_S1);
        }};
        final RuleViolations violations = tested.evaluate(EDGE_ID,
                                                          o1,
                                                          i1);
        assertNotNull(violations);
        assertFalse(violations.violations(RuleViolation.Type.ERROR).iterator().hasNext());
    }

    @Test
    public void testP1Deny() {
        final Set<String> i1 = new HashSet<String>(1) {{
            add(P1_E1);
        }};
        final Set<String> o1 = new HashSet<String>(1) {{
            add("p1s2");
        }};
        final RuleViolations violations = tested.evaluate(EDGE_ID,
                                                          o1,
                                                          i1);
        assertNotNull(violations);
        assertTrue(violations.violations(RuleViolation.Type.ERROR).iterator().hasNext());
    }

    @Test
    public void testP2Accept() {
        final Set<String> i1 = new HashSet<String>(1) {{
            add(P2_E1);
        }};
        final Set<String> o1 = new HashSet<String>(1) {{
            add(P2_S1);
        }};
        final RuleViolations violations = tested.evaluate(EDGE_ID,
                                                          o1,
                                                          i1);
        assertNotNull(violations);
        assertFalse(violations.violations(RuleViolation.Type.ERROR).iterator().hasNext());
    }

    @Test
    public void testP2Deny() {
        final Set<String> i1 = new HashSet<String>(1) {{
            add(P2_E1);
        }};
        final Set<String> o1 = new HashSet<String>(1) {{
            add("p2s2");
        }};
        final RuleViolations violations = tested.evaluate(EDGE_ID,
                                                          o1,
                                                          i1);
        assertNotNull(violations);
        assertTrue(violations.violations(RuleViolation.Type.ERROR).iterator().hasNext());
    }
}
