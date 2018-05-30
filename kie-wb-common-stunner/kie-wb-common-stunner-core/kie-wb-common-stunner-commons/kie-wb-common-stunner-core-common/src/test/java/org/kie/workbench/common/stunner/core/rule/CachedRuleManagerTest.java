/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.registry.RegistryFactory;
import org.kie.workbench.common.stunner.core.registry.rule.RuleHandlerRegistry;
import org.kie.workbench.common.stunner.core.rule.context.ConnectionContext;
import org.kie.workbench.common.stunner.core.rule.context.ContainmentContext;
import org.kie.workbench.common.stunner.core.rule.context.impl.RuleContextBuilder;
import org.kie.workbench.common.stunner.core.rule.handler.impl.ConnectionEvaluationHandler;
import org.kie.workbench.common.stunner.core.rule.handler.impl.ContainmentEvaluationHandler;
import org.kie.workbench.common.stunner.core.rule.impl.CanConnect;
import org.kie.workbench.common.stunner.core.rule.impl.CanContain;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CachedRuleManagerTest {

    private static final CanContain containmentRule = new CanContain("cont1",
                                                                     "role1",
                                                                     new HashSet<String>() {{
                                                                         add(("role1"));
                                                                         add(("role2"));
                                                                     }});

    private static final CanConnect connectionRule = new CanConnect("conn1",
                                                                    "role1",
                                                                    Arrays.asList(new CanConnect.PermittedConnection("role1",
                                                                                                                     "role2")));

    @Mock
    private RuleSet ruleSet;

    private ContainmentContext containmentContext = RuleContextBuilder.DomainContexts.containment(Collections.emptySet(),
                                                                                                  Collections.emptySet());

    @Mock
    private ContainmentEvaluationHandler containmentHandler;

    private ConnectionContext connectionContext = RuleContextBuilder.DomainContexts.connection("role1",
                                                                                               Optional.empty(),
                                                                                               Optional.empty());

    @Mock
    private ConnectionEvaluationHandler connectionHandler;

    @Mock
    private RuleHandlerRegistry ruleHandlerRegistry;

    private CachedRuleManager tested;

    @Before
    public void setup() throws Exception {
        RegistryFactory registryFactory = mock(RegistryFactory.class);
        when(registryFactory.newRuleHandlerRegistry()).thenReturn(ruleHandlerRegistry);
        when(ruleHandlerRegistry.getHandlersByContext(eq(ContainmentContext.class))).thenReturn(Arrays.asList(containmentHandler));
        when(ruleHandlerRegistry.getHandlersByContext(eq(ConnectionContext.class))).thenReturn(Arrays.asList(connectionHandler));
        when(connectionHandler.getRuleType()).thenReturn(CanConnect.class);
        when(connectionHandler.getContextType()).thenReturn(ConnectionContext.class);
        when(connectionHandler.accepts(any(CanConnect.class), any(ConnectionContext.class))).thenReturn(true);
        when(containmentHandler.accepts(any(CanContain.class), any(ContainmentContext.class))).thenReturn(true);
        when(containmentHandler.getRuleType()).thenReturn(CanContain.class);
        when(containmentHandler.getContextType()).thenReturn(ContainmentContext.class);
        RuleManagerImpl delegate = new RuleManagerImpl(registryFactory);
        when(ruleSet.getName()).thenReturn("testRuleSet");
        when(ruleSet.getRules()).thenReturn(Arrays.asList(containmentRule, connectionRule));
        tested = new CachedRuleManager(delegate);
        tested.init();
    }

    @Test
    public void testEvaluateContainmentContextOnce() {
        tested.evaluate(ruleSet,
                        containmentContext);
        tested.evaluate(ruleSet,
                        containmentContext);
        verify(ruleSet, times(1)).getRules();
        verify(containmentHandler, times(2)).evaluate(eq(containmentRule),
                                                      eq(containmentContext));
        verify(connectionHandler, never()).evaluate(any(CanConnect.class),
                                                    any(ConnectionContext.class));
    }

    @Test
    public void testEvaluateConnectionContextOnce() {
        tested.evaluate(ruleSet,
                        connectionContext);
        tested.evaluate(ruleSet,
                        connectionContext);
        verify(ruleSet, times(1)).getRules();
        verify(connectionHandler, times(2)).evaluate(eq(connectionRule),
                                                     eq(connectionContext));
        verify(containmentHandler, never()).evaluate(any(CanContain.class),
                                                     any(ContainmentContext.class));
    }
}
