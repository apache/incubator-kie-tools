/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.stunner.core.client.command;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.impl.NodeImpl;
import org.kie.workbench.common.stunner.core.graph.processing.index.Index;
import org.kie.workbench.common.stunner.core.rule.RuleManager;
import org.kie.workbench.common.stunner.core.rule.RuleSet;
import org.kie.workbench.common.stunner.core.rule.context.NodeContainmentContext;
import org.kie.workbench.common.stunner.core.rule.context.impl.RuleEvaluationContextBuilder;
import org.kie.workbench.common.stunner.core.rule.context.impl.StatefulGraphEvaluationState;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class QueueGraphExecutionContextTest {

    @Mock
    private DefinitionManager definitionManager;

    @Mock
    private FactoryManager factoryManager;

    @Mock
    private RuleManager ruleManager;

    @Mock
    private StatefulGraphEvaluationState state;

    @Mock
    private Index<?, ?> graphIndex;

    @Mock
    private RuleSet ruleSet;

    private QueueGraphExecutionContext tested;
    private RuleEvaluationContextBuilder.StatefulGraphContextBuilder contextBuilder;

    @Before
    public void setUp() {
        contextBuilder = new RuleEvaluationContextBuilder.StatefulGraphContextBuilder(state);
        tested = new QueueGraphExecutionContext(definitionManager,
                                                factoryManager,
                                                ruleManager,
                                                contextBuilder,
                                                graphIndex,
                                                ruleSet);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testEvaluate() {
        StatefulGraphEvaluationState.StatefulContainmentState containmentState = new StatefulGraphEvaluationState.StatefulContainmentState();
        when(state.getContainmentState()).thenReturn(containmentState);
        Element parent = new NodeImpl<>("parent");
        Node child = new NodeImpl<>("child");
        final NodeContainmentContext[] containmentContext = new NodeContainmentContext[1];
        tested.evaluate(builder -> containmentContext[0] = builder.containment(parent, child));
        verify(ruleManager, times(1)).evaluate(eq(ruleSet), eq(containmentContext[0]));
        assertEquals(parent, containmentState.getParent(child));
    }

    @Test
    public void testClear() {
        tested.clear();
        verify(state, times(1)).clear();
    }

    @Test
    public void testAddElement() {
        Element node1 = new NodeImpl<>("parent");
        tested.addElement(node1);
        assertEquals("Should be One Element in Updated Elements", 1, tested.getUpdatedElements().size());
    }

    @Test
    public void testGetUpdatedElements() {
        Element node1 = new NodeImpl<>("parent");
        tested.addElement(node1);
        assertEquals("node 1 should be returned from Updated Elements", node1, tested.getUpdatedElements().get(0));
    }

    @Test
    public void testResetUpdatedElements() {
        Element parent = new NodeImpl<>("parent");

        tested.addElement(parent);
        assertEquals("Should be One Element in Updated Elements", 1, tested.getUpdatedElements().size());
        tested.resetUpdatedElements();
        assertEquals("Updated Elements should be empty", 0, tested.getUpdatedElements().size());
    }
}
