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

package org.kie.workbench.common.stunner.core.rule;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.registry.RegistryFactory;
import org.kie.workbench.common.stunner.core.registry.rule.RuleHandlerRegistry;
import org.kie.workbench.common.stunner.core.rule.context.ContainmentContext;
import org.kie.workbench.common.stunner.core.rule.context.impl.RuleContextBuilder;
import org.kie.workbench.common.stunner.core.rule.ext.RuleExtension;
import org.kie.workbench.common.stunner.core.rule.ext.RuleExtensionHandler;
import org.kie.workbench.common.stunner.core.rule.impl.CanContain;
import org.kie.workbench.common.stunner.core.rule.violations.ContextOperationNotAllowedViolation;
import org.kie.workbench.common.stunner.core.rule.violations.DefaultRuleViolations;
import org.kie.workbench.common.stunner.core.rule.violations.RuleViolationImpl;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RuleManagerImplTest {

    private final List<RuleEvaluationHandler> handlers = new LinkedList<>();

    @Mock
    RegistryFactory registryFactory;

    @Mock
    RuleHandlerRegistry registry;

    @Mock
    RuleEvaluationHandler handler1;

    @Mock
    RuleEvaluationHandler handler2;

    @Mock
    RuleExtensionHandler extensionHandler;

    private RuleManagerImpl tested;
    private RuleSet ruleSet;
    private Rule rule1;
    private Rule rule2;
    private RuleViolation ruleViolation1;
    private RuleViolation ruleViolation2;
    private RuleViolation ruleViolation3;
    private RuleViolations ruleViolations1;
    private RuleViolations ruleViolations2;
    private RuleViolations ruleViolations3;
    private RuleEvaluationContext ruleEvaluationContext;
    private RuleExtension ruleExtension;

    @Before
    public void setup() throws Exception {
        rule1 = new CanContain("r1",
                               "r1-cId",
                               new HashSet<String>(2) {{
                                   add("role1");
                                   add("role2");
                               }});
        rule2 = new CanContain("r2",
                               "r2-cId",
                               new HashSet<String>(2) {{
                                   add("role1");
                                   add("role2");
                               }});
        ruleEvaluationContext = RuleContextBuilder.DomainContexts.containment(Collections.singleton("r1-cId"),
                                                                              new HashSet<String>(1) {{
                                                                                  add("r2-cId");
                                                                              }});
        ruleViolation1 = new RuleViolationImpl("error - v1");
        ruleViolation2 = new RuleViolationImpl("error - v2");
        ruleViolation3 = new RuleViolationImpl("error - v3");
        ruleViolations1 = new DefaultRuleViolations().addViolation(ruleViolation1);
        ruleViolations2 = new DefaultRuleViolations().addViolation(ruleViolation2);
        ruleViolations3 = new DefaultRuleViolations().addViolation(ruleViolation3);
        ruleExtension = new RuleExtension("re1",
                                          "cId").setHandlerType(RuleExtensionHandler.class);
        ruleSet = new RuleSetImpl("rs1",
                                  new ArrayList<Rule>(2) {{
                                      add(rule1);
                                      add(rule2);
                                      add(ruleExtension);
                                  }});
        handlers.add(handler1);
        handlers.add(handler2);
        when(registryFactory.newRuleHandlerRegistry()).thenReturn(registry);
        when(registry.getHandlersByContext(any(Class.class))).thenReturn(handlers);
        when(registry.getExtensionHandler(eq(RuleExtensionHandler.class))).thenReturn(extensionHandler);
        when(handler1.getRuleType()).thenReturn(CanContain.class);
        when(handler1.getContextType()).thenReturn(ContainmentContext.class);
        when(handler2.getRuleType()).thenReturn(CanContain.class);
        when(handler2.getContextType()).thenReturn(ContainmentContext.class);
        when(extensionHandler.getRuleType()).thenReturn(RuleExtension.class);
        when(extensionHandler.getContextType()).thenReturn(ContainmentContext.class);
        tested = new RuleManagerImpl(registryFactory);
    }

    @Test
    public void testEmptyRules() {
        ruleSet = new EmptyRuleSet();
        final RuleViolations result = tested.evaluate(ruleSet,
                                                      ruleEvaluationContext);
        assertNotNull(result);
        final Collection<RuleViolation> violations = (Collection<RuleViolation>) result.violations();
        assertTrue(violations.isEmpty());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testDefaultPolicyAccept() {
        when(registry.getExtensionHandler(any(Class.class))).thenReturn(null);
        when(handler1.accepts(eq(rule1),
                              eq(ruleEvaluationContext))).thenReturn(true);
        final RuleViolations result = tested.evaluate(ruleSet,
                                                      ruleEvaluationContext);
        assertNotNull(result);
        final Collection<RuleViolation> violations = (Collection<RuleViolation>) result.violations();
        assertTrue(violations.isEmpty());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testDefaultPolicyDeny() {
        when(registry.getExtensionHandler(any(Class.class))).thenReturn(null);
        when(handler1.accepts(eq(rule1),
                              eq(ruleEvaluationContext))).thenReturn(false);
        final RuleViolations result = tested.evaluate(ruleSet,
                                                      ruleEvaluationContext);
        assertNotNull(result);
        final Collection<RuleViolation> violations = (Collection<RuleViolation>) result.violations();
        assertEquals(1,
                     violations.size());
        assertEquals(ContextOperationNotAllowedViolation.class,
                     violations.stream().findFirst().get().getClass());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testEvaluateAllNoExtensions() {
        when(registry.getExtensionHandler(any(Class.class))).thenReturn(null);
        when(handler1.accepts(eq(rule1),
                              eq(ruleEvaluationContext))).thenReturn(true);
        when(handler2.accepts(eq(rule2),
                              eq(ruleEvaluationContext))).thenReturn(true);
        when(handler1.evaluate(eq(rule1),
                               eq(ruleEvaluationContext))).thenReturn(ruleViolations1);
        when(handler2.evaluate(eq(rule2),
                               eq(ruleEvaluationContext))).thenReturn(ruleViolations2);
        final RuleViolations result = tested.evaluate(ruleSet,
                                                      ruleEvaluationContext);
        assertNotNull(result);
        final Collection<RuleViolation> violations = (Collection<RuleViolation>) result.violations();
        assertTrue(violations.size() == 2);
        assertTrue(violations.contains(ruleViolation1));
        assertTrue(violations.contains(ruleViolation2));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testEvaluateOnlyOne() {
        when(registry.getExtensionHandler(any(Class.class))).thenReturn(null);
        when(handler1.accepts(eq(rule1),
                              eq(ruleEvaluationContext))).thenReturn(true);
        when(handler2.accepts(eq(rule2),
                              eq(ruleEvaluationContext))).thenReturn(false);
        when(handler1.evaluate(eq(rule1),
                               eq(ruleEvaluationContext))).thenReturn(ruleViolations1);
        when(handler2.evaluate(eq(rule2),
                               eq(ruleEvaluationContext))).thenReturn(ruleViolations2);
        final RuleViolations result = tested.evaluate(ruleSet,
                                                      ruleEvaluationContext);
        assertNotNull(result);
        final Collection<RuleViolation> violations = (Collection<RuleViolation>) result.violations();
        assertTrue(violations.size() == 1);
        assertTrue(violations.contains(ruleViolation1));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testEvaluateOnlyTwo() {
        when(registry.getExtensionHandler(any(Class.class))).thenReturn(null);
        when(handler1.accepts(eq(rule1),
                              eq(ruleEvaluationContext))).thenReturn(false);
        when(handler2.accepts(eq(rule2),
                              eq(ruleEvaluationContext))).thenReturn(true);
        when(handler1.evaluate(eq(rule1),
                               eq(ruleEvaluationContext))).thenReturn(ruleViolations1);
        when(handler2.evaluate(eq(rule2),
                               eq(ruleEvaluationContext))).thenReturn(ruleViolations2);
        final RuleViolations result = tested.evaluate(ruleSet,
                                                      ruleEvaluationContext);
        assertNotNull(result);
        final Collection<RuleViolation> violations = (Collection<RuleViolation>) result.violations();
        assertTrue(violations.size() == 1);
        assertTrue(violations.contains(ruleViolation2));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testEvaluateExtensionAndOne() {
        when(handler1.accepts(eq(rule1),
                              eq(ruleEvaluationContext))).thenReturn(true);
        when(handler2.accepts(eq(rule2),
                              eq(ruleEvaluationContext))).thenReturn(false);
        when(extensionHandler.accepts(eq(ruleExtension),
                                      eq(ruleEvaluationContext))).thenReturn(true);
        when(handler1.evaluate(eq(rule1),
                               eq(ruleEvaluationContext))).thenReturn(ruleViolations1);
        when(handler2.evaluate(eq(rule2),
                               eq(ruleEvaluationContext))).thenReturn(ruleViolations2);
        when(extensionHandler.evaluate(eq(ruleExtension),
                                       eq(ruleEvaluationContext))).thenReturn(ruleViolations3);
        final RuleViolations result = tested.evaluate(ruleSet,
                                                      ruleEvaluationContext);
        assertNotNull(result);
        final Collection<RuleViolation> violations = (Collection<RuleViolation>) result.violations();
        assertTrue(violations.size() == 2);
        assertTrue(violations.contains(ruleViolation1));
        assertTrue(violations.contains(ruleViolation3));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testEvaluateExtensionAndTwo() {
        when(handler1.accepts(eq(rule1),
                              eq(ruleEvaluationContext))).thenReturn(false);
        when(handler2.accepts(eq(rule2),
                              eq(ruleEvaluationContext))).thenReturn(true);
        when(extensionHandler.accepts(eq(ruleExtension),
                                      eq(ruleEvaluationContext))).thenReturn(true);
        when(handler1.evaluate(eq(rule1),
                               eq(ruleEvaluationContext))).thenReturn(ruleViolations1);
        when(handler2.evaluate(eq(rule2),
                               eq(ruleEvaluationContext))).thenReturn(ruleViolations2);
        when(extensionHandler.evaluate(eq(ruleExtension),
                                       eq(ruleEvaluationContext))).thenReturn(ruleViolations3);
        final RuleViolations result = tested.evaluate(ruleSet,
                                                      ruleEvaluationContext);
        assertNotNull(result);
        final Collection<RuleViolation> violations = (Collection<RuleViolation>) result.violations();
        assertTrue(violations.size() == 2);
        assertTrue(violations.contains(ruleViolation2));
        assertTrue(violations.contains(ruleViolation3));
    }
}
