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


package org.kie.workbench.common.stunner.core.client.rule;

import io.crysknife.client.ManagedInstance;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.registry.rule.RuleHandlerRegistry;
import org.kie.workbench.common.stunner.core.rule.CachedRuleManager;
import org.kie.workbench.common.stunner.core.rule.RuleEvaluationContext;
import org.kie.workbench.common.stunner.core.rule.RuleEvaluationHandler;
import org.kie.workbench.common.stunner.core.rule.RuleSet;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.uberfire.stubs.ManagedInstanceStub;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ClientRuleManagerTest {

    @Mock
    private CachedRuleManager ruleManager;

    @Mock
    private RuleHandlerRegistry registry;

    @Mock
    private RuleEvaluationHandler handler;
    private ManagedInstance<RuleEvaluationHandler> handlerInstances;

    private ClientRuleManager tested;

    @Before
    public void setUp() {
        handlerInstances = new ManagedInstanceStub<>(handler);
        when(ruleManager.registry()).thenReturn(registry);
        tested = new ClientRuleManager(ruleManager,
                                       handlerInstances);
    }

    @Test
    public void testRegisterHandlers() {
        tested.init();
        verify(registry, times(1)).register(eq(handler));
    }

    @Test
    public void testGetRegistry() {
        assertEquals(registry, tested.registry());
    }

    @Test
    public void testEvaluate() {
        RuleSet ruleSet = mock(RuleSet.class);
        RuleEvaluationContext context = mock(RuleEvaluationContext.class);
        tested.evaluate(ruleSet, context);
        verify(ruleManager, times(1)).evaluate(eq(ruleSet),
                                               eq(context));
    }
}
