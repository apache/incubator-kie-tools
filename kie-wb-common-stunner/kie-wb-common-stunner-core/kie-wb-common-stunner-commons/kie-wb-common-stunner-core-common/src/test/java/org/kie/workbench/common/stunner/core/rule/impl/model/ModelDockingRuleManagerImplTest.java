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
import org.kie.workbench.common.stunner.core.rule.DockingRule;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.kie.workbench.common.stunner.core.rule.RuleViolations;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ModelDockingRuleManagerImplTest {

    private final static String DEF_ID = "defId";
    private final static String ROLE1 = "role1";
    private final static String ROLE2 = "role2";
    private final static Set<String> PERMITTED_ROLES = new HashSet<String>(2) {{
        add(ROLE1);
        add(ROLE2);
    }};
    private final static String DEF_ID2 = "defId2";
    private final static String ROLE3 = "role3";
    private final static String ROLE4 = "role4";
    private final static Set<String> PERMITTED_ROLES2 = new HashSet<String>(2) {{
        add(ROLE3);
        add(ROLE4);
    }};

    @Mock
    DockingRule rule;
    @Mock
    DockingRule rule2;

    private ModelDockingRuleManagerImpl tested;

    @Before
    public void setup() throws Exception {
        when(rule.getId()).thenReturn(DEF_ID);
        when(rule.getPermittedRoles()).thenReturn(PERMITTED_ROLES);
        when(rule2.getId()).thenReturn(DEF_ID2);
        when(rule2.getPermittedRoles()).thenReturn(PERMITTED_ROLES2);
        tested = new ModelDockingRuleManagerImpl();
        tested.addRule(rule);
        tested.addRule(rule2);
    }

    @Test
    public void test1Role1Accept() {
        final Set<String> roles = new HashSet<String>(2) {{
            add(ROLE1);
            add("role3");
        }};
        final RuleViolations violations = tested.evaluate(DEF_ID,
                                                          roles);
        assertNotNull(violations);
        assertFalse(violations.violations(RuleViolation.Type.ERROR).iterator().hasNext());
    }

    @Test
    public void test1Role2Accept() {
        final Set<String> roles = new HashSet<String>(2) {{
            add(ROLE2);
            add("role3");
        }};
        final RuleViolations violations = tested.evaluate(DEF_ID,
                                                          roles);
        assertNotNull(violations);
        assertFalse(violations.violations(RuleViolation.Type.ERROR).iterator().hasNext());
    }

    @Test
    public void test1Deny() {
        final Set<String> roles = new HashSet<String>(2) {{
            add("role3");
            add("role4");
        }};
        final RuleViolations violations = tested.evaluate(DEF_ID,
                                                          roles);
        assertNotNull(violations);
        assertTrue(violations.violations(RuleViolation.Type.ERROR).iterator().hasNext());
    }

    @Test
    public void test2Role3Accept() {
        final Set<String> roles = new HashSet<String>(2) {{
            add(ROLE3);
            add("role6");
        }};
        final RuleViolations violations = tested.evaluate(DEF_ID2,
                                                          roles);
        assertNotNull(violations);
        assertFalse(violations.violations(RuleViolation.Type.ERROR).iterator().hasNext());
    }

    @Test
    public void test2Role4Accept() {
        final Set<String> roles = new HashSet<String>(2) {{
            add(ROLE4);
            add("role1");
        }};
        final RuleViolations violations = tested.evaluate(DEF_ID2,
                                                          roles);
        assertNotNull(violations);
        assertFalse(violations.violations(RuleViolation.Type.ERROR).iterator().hasNext());
    }

    @Test
    public void test2Deny() {
        final Set<String> roles = new HashSet<String>(2) {{
            add(ROLE1);
            add("role6");
        }};
        final RuleViolations violations = tested.evaluate(DEF_ID2,
                                                          roles);
        assertNotNull(violations);
        assertTrue(violations.violations(RuleViolation.Type.ERROR).iterator().hasNext());
    }
}
