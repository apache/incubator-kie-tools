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


package org.kie.workbench.common.stunner.core.rule.violations;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.kie.workbench.common.stunner.core.validation.Violation;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DefaultRuleViolationsTest {

    @Mock
    private RuleViolation violationErr;

    @Mock
    private RuleViolation violationWarn;

    @Mock
    private RuleViolation violationInfo;

    @Before
    public void setup() throws Exception {
        when(violationErr.getViolationType()).thenReturn(Violation.Type.ERROR);
        when(violationWarn.getViolationType()).thenReturn(Violation.Type.WARNING);
        when(violationInfo.getViolationType()).thenReturn(Violation.Type.INFO);
    }

    @Test
    public void testGetResultingViolationsForType() {
        final DefaultRuleViolations violations =
                new DefaultRuleViolations()
                        .addViolation(violationWarn)
                        .addViolation(violationInfo)
                        .addViolation(violationErr);
        assertNotNull(violations);
        assertFalse(violations.isEmpty());
        final Iterable<RuleViolation> errViolations = violations.violations(Violation.Type.ERROR);
        assertNotNull(errViolations);
        assertEquals(violationErr,
                     errViolations.iterator().next());
        final Iterable<RuleViolation> warnViolations = violations.violations(Violation.Type.WARNING);
        assertNotNull(warnViolations);
        assertEquals(violationWarn,
                     warnViolations.iterator().next());
        final Iterable<RuleViolation> infoViolations = violations.violations(Violation.Type.INFO);
        assertNotNull(infoViolations);
        assertEquals(violationInfo,
                     infoViolations.iterator().next());
    }

    @Test
    public void testClear() {
        final DefaultRuleViolations violations =
                new DefaultRuleViolations()
                        .addViolation(violationWarn)
                        .addViolation(violationInfo)
                        .addViolation(violationErr);
        violations.clear();
        assertNotNull(violations);
        assertTrue(violations.isEmpty());
    }
}
