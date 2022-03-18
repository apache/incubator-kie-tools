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

package org.kie.workbench.common.stunner.core.lookup.domain;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.TestingGraphInstanceBuilder;
import org.kie.workbench.common.stunner.core.TestingGraphMockHandler;
import org.kie.workbench.common.stunner.core.lookup.domain.DomainLookupFunctions.FilterConnectionTargetDefinitions;
import org.kie.workbench.common.stunner.core.lookup.domain.DomainLookupFunctions.LookupAllowedDefinitionsByLabels;
import org.kie.workbench.common.stunner.core.lookup.domain.DomainLookupFunctions.LookupConnectionTargetRoles;
import org.kie.workbench.common.stunner.core.lookup.domain.DomainLookupFunctions.LookupDefinitionsByLabels;
import org.kie.workbench.common.stunner.core.lookup.domain.DomainLookupFunctions.LookupTargetConnectors;
import org.kie.workbench.common.stunner.core.registry.impl.DefinitionsCacheRegistry;
import org.kie.workbench.common.stunner.core.rule.RuleEvaluationContext;
import org.kie.workbench.common.stunner.core.rule.RuleManager;
import org.kie.workbench.common.stunner.core.rule.RuleSet;
import org.kie.workbench.common.stunner.core.rule.RuleSetImpl;
import org.kie.workbench.common.stunner.core.rule.context.CardinalityContext;
import org.kie.workbench.common.stunner.core.rule.context.EdgeCardinalityContext;
import org.kie.workbench.common.stunner.core.rule.impl.CanConnect;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.kie.workbench.common.stunner.core.lookup.domain.DomainLookupFunctions.isSourceConnectionAllowed;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class DomainLookupFunctionsTest {

    private static final String DEF_ID1 = "defId1";
    private static final String DEF_ID2 = "defId2";
    private static final String DEF_ID3 = "defId3";
    private static final String ROLE1 = "role1";
    private static final String ROLE2 = "role2";
    private static final String ROLE3 = "role3";
    private static final CanConnect permittedConnections = new CanConnect("PermittedConnections",
                                                                          DEF_ID2,
                                                                          Arrays.asList(new CanConnect.PermittedConnection(ROLE1,
                                                                                                                           ROLE2),
                                                                                        new CanConnect.PermittedConnection(ROLE2,
                                                                                                                           ROLE3)));

    private static final RuleSet RULE_SET = new RuleSetImpl("ruleSet1",
                                                            Collections.singleton(permittedConnections));

    @Mock
    private DomainLookupContext context;

    @Mock
    private DomainLookupsCache cache;

    @Mock
    private DefinitionsCacheRegistry definitionsCache;

    @Mock
    private RuleManager ruleManager;

    private TestingGraphInstanceBuilder.TestGraph1 graph1Instance;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() throws Exception {
        when(context.getRuleManager()).thenReturn(ruleManager);
        when(context.getCache()).thenReturn(cache);
        when(cache.getRuleSet()).thenReturn(RULE_SET);
        when(cache.getConnectionRules()).thenReturn(Collections.singletonList(permittedConnections));
        when(context.getDefinitionsRegistry()).thenReturn(definitionsCache);
        TestingGraphMockHandler graphTestHandler = new TestingGraphMockHandler();
        when(context.getDefinitionManager()).thenReturn(graphTestHandler.getDefinitionManager());
        graph1Instance = TestingGraphInstanceBuilder.newGraph1(graphTestHandler);

        when(definitionsCache.getLabels(eq(DEF_ID1))).thenReturn(Collections.singleton(ROLE1));
        when(definitionsCache.getLabels(eq(DEF_ID2))).thenReturn(Collections.singleton(ROLE2));
        when(definitionsCache.getLabels(eq(DEF_ID3))).thenReturn(Collections.singleton(ROLE3));
    }

    @Test
    public void testIsSourceConnectionAllowed() {
        assertTrue(isSourceConnectionAllowed(permittedConnections,
                                             new HashSet<String>(1) {{
                                                 add(ROLE1);
                                             }}));
        assertTrue(isSourceConnectionAllowed(permittedConnections,
                                             new HashSet<String>(1) {{
                                                 add(ROLE2);
                                             }}));
        assertFalse(isSourceConnectionAllowed(permittedConnections,
                                              new HashSet<String>(1) {{
                                                  add(ROLE3);
                                              }}));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testLookupConnectionTargetRoles() {
        LookupConnectionTargetRoles function = new LookupConnectionTargetRoles(DEF_ID2,
                                                                               DEF_ID1);
        Set<String> result = function.execute(context);
        assertEquals(1, result.size());
        assertTrue(result.contains(ROLE2));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testLookupDefinitionsByLabels() {
        Set<String> expected = new HashSet<String>(1) {{
            add(DEF_ID1);
        }};
        when(cache.getDefinitions(eq(ROLE2))).thenReturn(expected);
        LookupDefinitionsByLabels function = new LookupDefinitionsByLabels(new HashSet<String>(1) {{
            add(ROLE2);
        }},
                                                                           id -> true);
        assertTrue(function.execute(context).contains(DEF_ID1));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testLookupDefinitionsByLabelsWithFiltering() {
        Set<String> expected = new HashSet<String>(1) {{
            add(DEF_ID1);
        }};
        when(cache.getDefinitions(eq(ROLE2))).thenReturn(expected);
        LookupDefinitionsByLabels function = new LookupDefinitionsByLabels(new HashSet<String>(1) {{
            add(ROLE2);
        }},
                                                                           DEF_ID2::equals);
        assertFalse(function.execute(context).contains(DEF_ID1));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testLookupAllowedDefinitionsByLabels() {
        when(cache.getDefinitions(eq("label1"))).thenReturn(Collections.singleton(TestingGraphInstanceBuilder.DEF1_ID));
        when(definitionsCache.getLabels(eq(TestingGraphInstanceBuilder.DEF1_ID))).thenReturn(new HashSet<>(Arrays.asList(TestingGraphInstanceBuilder.DEF1_LABELS)));
        LookupAllowedDefinitionsByLabels function = new LookupAllowedDefinitionsByLabels(graph1Instance.graph,
                                                                                         new HashSet<String>(1) {{
                                                                                             add("label1");
                                                                                         }},
                                                                                         id -> true);
        Set<String> result = function.execute(context);
        assertTrue(result.contains(TestingGraphInstanceBuilder.DEF1_ID));
        ArgumentCaptor<RuleEvaluationContext> ruleEvaluationContextCaptor = ArgumentCaptor.forClass(RuleEvaluationContext.class);
        verify(ruleManager, atLeastOnce()).evaluate(eq(RULE_SET),
                                                    ruleEvaluationContextCaptor.capture());
        RuleEvaluationContext evaluationContext = ruleEvaluationContextCaptor.getValue();
        assertTrue(evaluationContext instanceof CardinalityContext);
        CardinalityContext cardinalityContext = (CardinalityContext) evaluationContext;
        assertEquals(1, cardinalityContext.getCurrentCount());
        assertEquals(CardinalityContext.Operation.ADD, cardinalityContext.getOperation().get());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testLookupAllowedDefinitionsByLabelsWithFiltering() {
        when(cache.getDefinitions(eq("label1"))).thenReturn(Collections.singleton(TestingGraphInstanceBuilder.DEF1_ID));
        when(definitionsCache.getLabels(eq(TestingGraphInstanceBuilder.DEF1_ID))).thenReturn(new HashSet<>(Arrays.asList(TestingGraphInstanceBuilder.DEF1_LABELS)));
        LookupAllowedDefinitionsByLabels function = new LookupAllowedDefinitionsByLabels(graph1Instance.graph,
                                                                                         new HashSet<String>(1) {{
                                                                                             add("label1");
                                                                                         }},
                                                                                         DEF_ID2::equals);
        Set<String> result = function.execute(context);
        assertTrue(result.isEmpty());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testLookupTargetConnectors() {
        CanConnect frmom3To1 = new CanConnect("cc1",
                                              TestingGraphInstanceBuilder.EDGE1_ID,
                                              Collections.singletonList(new CanConnect.PermittedConnection(TestingGraphInstanceBuilder.DEF3_LABELS[0],
                                                                                                           TestingGraphInstanceBuilder.DEF1_LABELS[0])));
        when(cache.getConnectionRules()).thenReturn(Collections.singletonList(frmom3To1));
        LookupTargetConnectors function = new LookupTargetConnectors(graph1Instance.endNode);
        Set<String> result = function.execute(context);
        assertTrue(result.contains(TestingGraphInstanceBuilder.EDGE1_ID));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testFilterConnectionTargetDefinitions() {
        FilterConnectionTargetDefinitions function = new FilterConnectionTargetDefinitions(DEF_ID2,
                                                                                           Collections.singleton(DEF_ID1));
        Set<String> result = function.execute(context);
        assertTrue(result.contains(DEF_ID1));
        ArgumentCaptor<RuleEvaluationContext> ruleEvaluationContextCaptor = ArgumentCaptor.forClass(RuleEvaluationContext.class);
        verify(ruleManager, times(1)).evaluate(eq(RULE_SET),
                                               ruleEvaluationContextCaptor.capture());
        RuleEvaluationContext evaluationContext = ruleEvaluationContextCaptor.getValue();
        assertTrue(evaluationContext instanceof EdgeCardinalityContext);
        EdgeCardinalityContext cardinalityContext = (EdgeCardinalityContext) evaluationContext;
        assertEquals(DEF_ID2, cardinalityContext.getEdgeRole());
        assertEquals(0, cardinalityContext.getCurrentCount());
        assertTrue(cardinalityContext.getRoles().contains(ROLE1));
        assertEquals(EdgeCardinalityContext.Direction.INCOMING, cardinalityContext.getDirection());
        assertEquals(CardinalityContext.Operation.ADD, cardinalityContext.getOperation().get());
    }
}
