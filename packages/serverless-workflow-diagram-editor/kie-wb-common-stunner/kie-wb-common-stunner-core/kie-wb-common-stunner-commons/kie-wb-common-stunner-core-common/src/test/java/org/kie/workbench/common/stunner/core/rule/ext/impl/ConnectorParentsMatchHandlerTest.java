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


package org.kie.workbench.common.stunner.core.rule.ext.impl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.rule.RuleEvaluationContext;
import org.kie.workbench.common.stunner.core.rule.RuleViolations;
import org.kie.workbench.common.stunner.core.rule.ext.RuleExtension;
import org.kie.workbench.common.stunner.core.rule.ext.RuleExtensionMultiHandler;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class ConnectorParentsMatchHandlerTest {

    @Mock
    private ConnectorParentsMatchConnectionHandler connectionHandler;

    @Mock
    private ConnectorParentsMatchContainmentHandler containmentHandler;

    @Mock
    private RuleExtensionMultiHandler multiHandler;

    @Mock
    private RuleViolations ruleViolations;

    private ConnectorParentsMatchHandler tested;

    @Before
    public void setup() throws Exception {
        when(multiHandler.accepts(any(RuleExtension.class),
                                  any(RuleEvaluationContext.class))).thenReturn(true);
        when(multiHandler.evaluate(any(RuleExtension.class),
                                   any(RuleEvaluationContext.class))).thenReturn(ruleViolations);
        this.tested = new ConnectorParentsMatchHandler(connectionHandler,
                                                       containmentHandler,
                                                       multiHandler);
    }

    @Test
    public void testInit() {
        tested.init();
        verify(multiHandler,
               times(1)).addHandler(eq(connectionHandler));
        verify(multiHandler,
               times(1)).addHandler(eq(containmentHandler));
    }

    @Test
    public void testTypes() {
        assertEquals(ConnectorParentsMatchHandler.class,
                     tested.getExtensionType());
        assertEquals(RuleEvaluationContext.class,
                     tested.getContextType());
    }

    @Test
    public void testAccept() {
        final RuleExtension ruleExtension = mock(RuleExtension.class);
        when(ruleExtension.getArguments()).thenReturn(new String[]{"parentType"});
        final RuleEvaluationContext context = mock(RuleEvaluationContext.class);
        assertTrue(tested.accepts(ruleExtension,
                                  context));
        verify(multiHandler,
               times(1)).accepts(eq(ruleExtension),
                                 eq(context));
    }

    @Test
    public void testEvaluate() {
        final RuleExtension ruleExtension = mock(RuleExtension.class);
        when(ruleExtension.getArguments()).thenReturn(new String[]{"parentType"});
        final RuleEvaluationContext context = mock(RuleEvaluationContext.class);
        assertEquals(ruleViolations,
                     tested.evaluate(ruleExtension,
                                     context));
        verify(multiHandler,
               times(1)).evaluate(eq(ruleExtension),
                                  eq(context));
    }
}
