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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.rule.CardinalityRule;
import org.kie.workbench.common.stunner.core.rule.RuleManager;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.kie.workbench.common.stunner.core.rule.RuleViolations;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith( MockitoJUnitRunner.class )
public class ModelCardinalityRuleManagerImplTest {

    private final static String ROLE1 = "role1";
    private final static int MIN1 = 1;
    private final static int MAX1 = 2;
    private final static String ROLE2 = "role2";
    private final static int MIN2 = 0;
    private final static int MAX2 = 1;

    @Mock CardinalityRule rule;
    @Mock CardinalityRule rule2;

    private ModelCardinalityRuleManagerImpl tested;

    @Before
    public void setup() throws Exception {
        when( rule.getRole() ).thenReturn( ROLE1 );
        when( rule.getMinOccurrences() ).thenReturn( MIN1 );
        when( rule.getMaxOccurrences() ).thenReturn( MAX1 );
        when( rule2.getRole() ).thenReturn( ROLE2 );
        when( rule2.getMinOccurrences() ).thenReturn( MIN2 );
        when( rule2.getMaxOccurrences() ).thenReturn( MAX2 );
        tested = new ModelCardinalityRuleManagerImpl();
        tested.addRule( rule );
        tested.addRule( rule2 );
    }

    @Test
    public void testMax1Accept() {
        final RuleViolations violations = tested.evaluate( ROLE1, 1, RuleManager.Operation.ADD );
        assertNotNull( violations );
        assertFalse( violations.violations( RuleViolation.Type.ERROR ).iterator().hasNext() );
    }

    @Test
    public void testMax1Deny() {
        final RuleViolations violations = tested.evaluate( ROLE1, 2, RuleManager.Operation.ADD );
        assertNotNull( violations );
        assertTrue( violations.violations( RuleViolation.Type.ERROR ).iterator().hasNext() );
    }

    @Test
    public void testMin1Accept() {
        final RuleViolations violations = tested.evaluate( ROLE1, 1, RuleManager.Operation.ADD );
        assertNotNull( violations );
        assertFalse( violations.violations( RuleViolation.Type.ERROR ).iterator().hasNext() );
    }

    @Test
    public void testMin1Deny() {
        final RuleViolations violations = tested.evaluate( ROLE1, 1, RuleManager.Operation.DELETE );
        assertNotNull( violations );
        assertTrue( violations.violations( RuleViolation.Type.ERROR ).iterator().hasNext() );
    }

    @Test
    public void testMax2Accept() {
        final RuleViolations violations = tested.evaluate( ROLE2, 0, RuleManager.Operation.ADD );
        assertNotNull( violations );
        assertFalse( violations.violations( RuleViolation.Type.ERROR ).iterator().hasNext() );
    }

    @Test
    public void testMax2Deny() {
        final RuleViolations violations = tested.evaluate( ROLE2, 1, RuleManager.Operation.ADD );
        assertNotNull( violations );
        assertTrue( violations.violations( RuleViolation.Type.ERROR ).iterator().hasNext() );
    }

    @Test
    public void testMin2Accept() {
        final RuleViolations violations = tested.evaluate( ROLE2, 2, RuleManager.Operation.DELETE );
        assertNotNull( violations );
        assertFalse( violations.violations( RuleViolation.Type.ERROR ).iterator().hasNext() );
    }

}
