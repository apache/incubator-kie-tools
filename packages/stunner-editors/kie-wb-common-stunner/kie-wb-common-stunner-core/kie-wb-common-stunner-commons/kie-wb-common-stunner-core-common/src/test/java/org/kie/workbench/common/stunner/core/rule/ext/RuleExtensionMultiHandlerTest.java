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


package org.kie.workbench.common.stunner.core.rule.ext;

import java.util.Collection;
import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.rule.RuleEvaluationContext;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.kie.workbench.common.stunner.core.rule.RuleViolations;
import org.kie.workbench.common.stunner.core.rule.context.ContainmentContext;
import org.kie.workbench.common.stunner.core.rule.context.impl.RuleEvaluationContextBuilder;
import org.kie.workbench.common.stunner.core.rule.violations.DefaultRuleViolations;
import org.kie.workbench.common.stunner.core.rule.violations.RuleViolationImpl;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class RuleExtensionMultiHandlerTest {

    @Mock
    RuleExtensionHandler handler1;

    @Mock
    RuleExtensionHandler handler2;

    private RuleExtensionMultiHandler tested;
    private RuleExtension rule1 = new RuleExtension("r1",
                                                    "c1");
    private RuleEvaluationContext context;
    private RuleViolation violation1;
    private RuleViolations violations1;

    @Before
    public void setup() throws Exception {
        context = RuleEvaluationContextBuilder.DomainContexts.containment(Collections.singleton("id1"),
                                                                          Collections.emptySet());
        violation1 = new RuleViolationImpl("v1");
        violations1 = new DefaultRuleViolations().addViolation(violation1);
        when(handler1.getRuleType()).thenReturn(RuleExtension.class);
        when(handler1.getContextType()).thenReturn(ContainmentContext.class);
        when(handler2.getRuleType()).thenReturn(RuleExtension.class);
        when(handler2.getContextType()).thenReturn(ContainmentContext.class);
        tested = new RuleExtensionMultiHandler();
        tested.addHandler(handler1);
        tested.addHandler(handler2);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testAcceptAll() {
        when(handler1.accepts(eq(rule1),
                              eq(context))).thenReturn(true);
        when(handler2.accepts(eq(rule1),
                              eq(context))).thenReturn(true);
        final boolean accepts = tested.accepts(rule1,
                                               context);
        assertTrue(accepts);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testAcceptAllBut1() {
        when(handler1.accepts(eq(rule1),
                              eq(context))).thenReturn(true);
        when(handler2.accepts(eq(rule1),
                              eq(context))).thenReturn(false);
        final boolean accepts = tested.accepts(rule1,
                                               context);
        assertTrue(accepts);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testAcceptAllBut2() {
        when(handler1.accepts(eq(rule1),
                              eq(context))).thenReturn(false);
        when(handler2.accepts(eq(rule1),
                              eq(context))).thenReturn(true);
        final boolean accepts = tested.accepts(rule1,
                                               context);
        assertTrue(accepts);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testNotAccept() {
        when(handler1.accepts(eq(rule1),
                              eq(context))).thenReturn(false);
        when(handler2.accepts(eq(rule1),
                              eq(context))).thenReturn(false);
        final boolean accepts = tested.accepts(rule1,
                                               context);
        assertFalse(accepts);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testEvaluateAll() {
        when(handler1.accepts(eq(rule1),
                              eq(context))).thenReturn(true);
        when(handler2.accepts(eq(rule1),
                              eq(context))).thenReturn(true);
        when(handler1.evaluate(eq(rule1),
                               eq(context))).thenReturn(violations1);
        when(handler2.evaluate(eq(rule1),
                               eq(context))).thenReturn(violations1);
        final RuleViolations result = tested.evaluate(rule1,
                                                      context);
        assertNotNull(result);
        final Collection<RuleViolation> violations = (Collection<RuleViolation>) result.violations();
        assertTrue(violations.size() == 2);
        assertTrue(violations.contains(violation1));
        assertTrue(violations.contains(violation1));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testEvaluateOnlyOne() {
        when(handler1.accepts(eq(rule1),
                              eq(context))).thenReturn(true);
        when(handler2.accepts(eq(rule1),
                              eq(context))).thenReturn(false);
        when(handler1.evaluate(eq(rule1),
                               eq(context))).thenReturn(violations1);
        when(handler2.evaluate(eq(rule1),
                               eq(context))).thenReturn(violations1);
        final RuleViolations result = tested.evaluate(rule1,
                                                      context);
        assertNotNull(result);
        final Collection<RuleViolation> violations = (Collection<RuleViolation>) result.violations();
        assertTrue(violations.size() == 1);
        assertTrue(violations.contains(violation1));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testEvaluateOnlyTwo() {
        when(handler1.accepts(eq(rule1),
                              eq(context))).thenReturn(false);
        when(handler2.accepts(eq(rule1),
                              eq(context))).thenReturn(true);
        when(handler1.evaluate(eq(rule1),
                               eq(context))).thenReturn(violations1);
        when(handler2.evaluate(eq(rule1),
                               eq(context))).thenReturn(violations1);
        final RuleViolations result = tested.evaluate(rule1,
                                                      context);
        assertNotNull(result);
        final Collection<RuleViolation> violations = (Collection<RuleViolation>) result.violations();
        assertTrue(violations.size() == 1);
        assertTrue(violations.contains(violation1));
    }
}
