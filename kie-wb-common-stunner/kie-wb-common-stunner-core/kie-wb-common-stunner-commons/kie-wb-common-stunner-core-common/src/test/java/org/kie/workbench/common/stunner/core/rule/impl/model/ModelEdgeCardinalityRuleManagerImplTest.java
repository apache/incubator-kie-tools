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
import org.kie.workbench.common.stunner.core.rule.EdgeCardinalityRule;
import org.kie.workbench.common.stunner.core.rule.RuleManager;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.kie.workbench.common.stunner.core.rule.RuleViolations;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith( MockitoJUnitRunner.class )
public class ModelEdgeCardinalityRuleManagerImplTest {

    private final static String OROLE1 = "oRole";
    private final static int OMIN1 = 1;
    private final static int OMAX1 = 2;
    private final static String IROLE1 = "iRole";
    private final static int IMIN1 = -1;
    private final static int IMAX1 = 1;

    @Mock EdgeCardinalityRule orule;
    @Mock EdgeCardinalityRule irule;

    private ModelEdgeCardinalityRuleManagerImpl tested;

    @Before
    public void setup() throws Exception {
        when( orule.getRole() ).thenReturn( OROLE1 );
        when( orule.getMinOccurrences() ).thenReturn( OMIN1 );
        when( orule.getMaxOccurrences() ).thenReturn( OMAX1 );
        when( orule.getType() ).thenReturn( EdgeCardinalityRule.Type.OUTGOING );
        when( irule.getRole() ).thenReturn( IROLE1 );
        when( irule.getMinOccurrences() ).thenReturn( IMIN1 );
        when( irule.getMaxOccurrences() ).thenReturn( IMAX1 );
        when( irule.getType() ).thenReturn( EdgeCardinalityRule.Type.INCOMING );
        tested = new ModelEdgeCardinalityRuleManagerImpl();
        tested.addRule( orule );
        tested.addRule( irule );
    }

    @Test
    public void testOutgoingMaxAccept() {
        final Set<String> labels = new HashSet<String>( 1 ) {{
            add( OROLE1 );
        }};
        final RuleViolations violations =
                tested.evaluate( OROLE1, labels, 1, EdgeCardinalityRule.Type.OUTGOING, RuleManager.Operation.ADD );
        assertNotNull( violations );
        assertFalse( violations.violations( RuleViolation.Type.ERROR ).iterator().hasNext() );
    }

    @Test
    public void testOutgoingMaxDeny() {
        final Set<String> labels = new HashSet<String>( 1 ) {{
            add( OROLE1 );
        }};
        final RuleViolations violations =
                tested.evaluate( OROLE1, labels, 2, EdgeCardinalityRule.Type.OUTGOING, RuleManager.Operation.ADD );
        assertNotNull( violations );
        assertTrue( violations.violations( RuleViolation.Type.ERROR ).iterator().hasNext() );
    }

    @Test
    public void testOutgoingMinAccept() {
        final Set<String> labels = new HashSet<String>( 1 ) {{
            add( OROLE1 );
        }};
        final RuleViolations violations =
                tested.evaluate( OROLE1, labels, 2, EdgeCardinalityRule.Type.OUTGOING, RuleManager.Operation.DELETE );
        assertNotNull( violations );
        assertFalse( violations.violations( RuleViolation.Type.ERROR ).iterator().hasNext() );
    }

    @Test
    public void testOutgoingMinDeny() {
        final Set<String> labels = new HashSet<String>( 1 ) {{
            add( OROLE1 );
        }};
        final RuleViolations violations =
                tested.evaluate( OROLE1, labels, 1, EdgeCardinalityRule.Type.OUTGOING, RuleManager.Operation.DELETE );
        assertNotNull( violations );
        assertTrue( violations.violations( RuleViolation.Type.ERROR ).iterator().hasNext() );
    }

    @Test
    public void testIncomingMaxAccept() {
        final Set<String> labels = new HashSet<String>( 1 ) {{
            add( IROLE1 );
        }};
        final RuleViolations violations =
                tested.evaluate( IROLE1, labels, 0, EdgeCardinalityRule.Type.INCOMING, RuleManager.Operation.ADD );
        assertNotNull( violations );
        assertFalse( violations.violations( RuleViolation.Type.ERROR ).iterator().hasNext() );
    }

    @Test
    public void testIncomingMaxDeny() {
        final Set<String> labels = new HashSet<String>( 1 ) {{
            add( IROLE1 );
        }};
        final RuleViolations violations =
                tested.evaluate( IROLE1, labels, 1, EdgeCardinalityRule.Type.INCOMING, RuleManager.Operation.ADD );
        assertNotNull( violations );
        assertTrue( violations.violations( RuleViolation.Type.ERROR ).iterator().hasNext() );
    }

    @Test
    public void testIncomingMinAccept() {
        final Set<String> labels = new HashSet<String>( 1 ) {{
            add( IROLE1 );
        }};
        final RuleViolations violations =
                tested.evaluate( IROLE1, labels, 1, EdgeCardinalityRule.Type.INCOMING, RuleManager.Operation.DELETE );
        assertNotNull( violations );
        assertFalse( violations.violations( RuleViolation.Type.ERROR ).iterator().hasNext() );
    }

    private final static String AROLE1 = "aRole";
    private final static int AMIN1 = 0;
    private final static int AMAX1 = -1;

    @Mock EdgeCardinalityRule anotherRule;

    @Test
    public void testMinNotNegative() {
        when( anotherRule.getRole() ).thenReturn( AROLE1 );
        when( anotherRule.getMinOccurrences() ).thenReturn( AMIN1 );
        when( anotherRule.getMaxOccurrences() ).thenReturn( AMAX1 );
        when( anotherRule.getType() ).thenReturn( EdgeCardinalityRule.Type.OUTGOING );
        tested.addRule( anotherRule );
        final Set<String> labels = new HashSet<String>( 1 ) {{
            add( AROLE1 );
        }};
        final RuleViolations violations =
                tested.evaluate( AROLE1, labels, 0, EdgeCardinalityRule.Type.OUTGOING, RuleManager.Operation.DELETE );
        assertNotNull( violations );
        assertFalse( violations.violations( RuleViolation.Type.ERROR ).iterator().hasNext() );
    }

}
