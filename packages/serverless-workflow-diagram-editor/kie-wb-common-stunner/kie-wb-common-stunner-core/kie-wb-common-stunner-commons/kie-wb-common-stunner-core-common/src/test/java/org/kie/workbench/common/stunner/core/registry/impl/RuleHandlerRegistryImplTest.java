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


package org.kie.workbench.common.stunner.core.registry.impl;

import java.util.Collection;

import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.stunner.core.rule.RuleEvaluationContext;
import org.kie.workbench.common.stunner.core.rule.RuleEvaluationHandler;
import org.kie.workbench.common.stunner.core.rule.RuleViolations;
import org.kie.workbench.common.stunner.core.rule.context.ConnectionContext;
import org.kie.workbench.common.stunner.core.rule.context.ContainmentContext;
import org.kie.workbench.common.stunner.core.rule.ext.RuleExtension;
import org.kie.workbench.common.stunner.core.rule.ext.RuleExtensionHandler;
import org.kie.workbench.common.stunner.core.rule.impl.CanContain;
import org.kie.workbench.common.stunner.core.rule.violations.DefaultRuleViolations;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class RuleHandlerRegistryImplTest {

    private RuleHandlerRegistryImpl tested;

    private static final class RuleHandlerStub<C extends RuleEvaluationContext> implements RuleEvaluationHandler<CanContain, C> {

        private final Class<C> contextType;

        private RuleHandlerStub(final Class<C> contextType) {
            this.contextType = contextType;
        }

        @Override
        public Class<CanContain> getRuleType() {
            return CanContain.class;
        }

        @Override
        public Class<C> getContextType() {
            return contextType;
        }

        @Override
        public boolean accepts(final CanContain rule,
                               final C context) {
            return true;
        }

        @Override
        public RuleViolations evaluate(final CanContain rule,
                                       final C context) {
            return new DefaultRuleViolations();
        }
    }

    private static final class RuleExtensionHandlerStub extends RuleExtensionHandler<RuleExtensionHandlerStub, ContainmentContext> {

        @Override
        public Class<RuleExtensionHandlerStub> getExtensionType() {
            return RuleExtensionHandlerStub.class;
        }

        @Override
        public Class<ContainmentContext> getContextType() {
            return ContainmentContext.class;
        }

        @Override
        public boolean accepts(final RuleExtension rule,
                               final ContainmentContext context) {
            return true;
        }

        @Override
        public RuleViolations evaluate(final RuleExtension rule,
                                       final ContainmentContext context) {
            return new DefaultRuleViolations();
        }
    }

    private RuleHandlerStub<ContainmentContext> handler1;
    private RuleHandlerStub<ConnectionContext> handler2;
    private RuleExtensionHandlerStub extensionHandler;

    @Before
    public void setup() throws Exception {
        handler1 = new RuleHandlerStub<ContainmentContext>(ContainmentContext.class);
        handler2 = new RuleHandlerStub<ConnectionContext>(ConnectionContext.class);
        extensionHandler = new RuleExtensionHandlerStub();
        tested = new RuleHandlerRegistryImpl();
        tested.register(handler1);
        tested.register(handler2);
        tested.register(extensionHandler);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testGetHandler1() {
        final Collection<RuleEvaluationHandler> handlersByContext = tested.getHandlersByContext(ContainmentContext.class);
        assertNotNull(handlersByContext);
        assertTrue(handlersByContext.size() == 1);
        assertTrue(handlersByContext.contains(handler1));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testGetHandler2() {
        final Collection<RuleEvaluationHandler> handlersByContext = tested.getHandlersByContext(ConnectionContext.class);
        assertNotNull(handlersByContext);
        assertTrue(handlersByContext.size() == 1);
        assertTrue(handlersByContext.contains(handler2));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testGetExtension() {
        final RuleExtensionHandlerStub _extensionHandler = tested.getExtensionHandler(RuleExtensionHandlerStub.class);
        assertNotNull(_extensionHandler);
        assertEquals(extensionHandler,
                     _extensionHandler);
    }
}
