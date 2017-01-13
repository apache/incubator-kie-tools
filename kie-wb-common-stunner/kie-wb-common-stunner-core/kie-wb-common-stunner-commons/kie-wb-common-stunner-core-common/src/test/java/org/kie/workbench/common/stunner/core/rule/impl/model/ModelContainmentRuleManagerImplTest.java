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
import org.kie.workbench.common.stunner.core.rule.ContainmentRule;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.kie.workbench.common.stunner.core.rule.RuleViolations;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ModelContainmentRuleManagerImplTest {

    private final static String PARENT_ID = "parentId";
    private final static Set<String> PARENT_ROLES =
            new HashSet<String>(2) {{
                add("role1");
                add("role2");
            }};
    private final static String PARENT_ID2 = "parentId2";
    private final static Set<String> PARENT_ROLES2 =
            new HashSet<String>(2) {{
                add("role3");
                add("role4");
            }};

    @Mock
    ContainmentRule rule;
    @Mock
    ContainmentRule rule2;

    private ModelContainmentRuleManagerImpl tested;

    @Before
    public void setup() throws Exception {
        when(rule.getId()).thenReturn(PARENT_ID);
        when(rule.getPermittedRoles()).thenReturn(PARENT_ROLES);
        when(rule2.getId()).thenReturn(PARENT_ID2);
        when(rule2.getPermittedRoles()).thenReturn(PARENT_ROLES2);
        tested = new ModelContainmentRuleManagerImpl();
        tested.addRule(rule);
        tested.addRule(rule2);
    }

    @Test
    public void test1Accept() {
        final Set<String> candidateRoles = new HashSet<String>(1) {{
            add("role2");
        }};
        final RuleViolations violations = tested.evaluate(PARENT_ID,
                                                          candidateRoles);
        assertNotNull(violations);
        assertFalse(violations.violations(RuleViolation.Type.ERROR).iterator().hasNext());
    }

    @Test
    public void test1Deny() {
        final Set<String> candidateRoles = new HashSet<String>(1) {{
            add("role5");
        }};
        final RuleViolations violations = tested.evaluate(PARENT_ID,
                                                          candidateRoles);
        assertNotNull(violations);
        assertTrue(violations.violations(RuleViolation.Type.ERROR).iterator().hasNext());
    }

    @Test
    public void test2Accept() {
        final Set<String> candidateRoles = new HashSet<String>(1) {{
            add("role4");
        }};
        final RuleViolations violations = tested.evaluate(PARENT_ID2,
                                                          candidateRoles);
        assertNotNull(violations);
        assertFalse(violations.violations(RuleViolation.Type.ERROR).iterator().hasNext());
    }

    @Test
    public void test2Deny() {
        final Set<String> candidateRoles = new HashSet<String>(1) {{
            add("role1");
        }};
        final RuleViolations violations = tested.evaluate(PARENT_ID2,
                                                          candidateRoles);
        assertNotNull(violations);
        assertTrue(violations.violations(RuleViolation.Type.ERROR).iterator().hasNext());
    }
}
