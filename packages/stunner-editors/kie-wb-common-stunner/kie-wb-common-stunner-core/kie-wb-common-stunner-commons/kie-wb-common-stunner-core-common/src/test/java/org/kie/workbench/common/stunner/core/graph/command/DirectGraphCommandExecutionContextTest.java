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


package org.kie.workbench.common.stunner.core.graph.command;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.processing.index.Index;
import org.kie.workbench.common.stunner.core.rule.RuleViolations;
import org.kie.workbench.common.stunner.core.rule.context.NodeContainmentContext;
import org.kie.workbench.common.stunner.core.rule.context.impl.RuleEvaluationContextBuilder;
import org.kie.workbench.common.stunner.core.rule.violations.EmptyRuleViolations;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DirectGraphCommandExecutionContextTest {

    @Mock
    private DefinitionManager definitionManager;

    @Mock
    private FactoryManager factoryManager;

    @Mock
    private RuleEvaluationContextBuilder.StatelessGraphContextBuilder contextBuilder;

    @Mock
    private Index<?, ?> graphIndex;

    private DirectGraphCommandExecutionContext tested;

    @Before
    public void setUp() {
        tested = new DirectGraphCommandExecutionContext(definitionManager,
                                                        factoryManager,
                                                        contextBuilder,
                                                        graphIndex);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testEvaluate() {
        when(contextBuilder.containment(any(Element.class), any(Node.class))).thenReturn(mock(NodeContainmentContext.class));
        RuleViolations result = tested.evaluate(builder -> builder.containment(mock(Element.class), mock(Node.class)));
        assertEquals(EmptyRuleViolations.INSTANCE, result);
    }
}
