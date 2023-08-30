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


package org.kie.workbench.common.stunner.core.lookup.domain;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.TestingGraphInstanceBuilder;
import org.kie.workbench.common.stunner.core.TestingGraphMockHandler;
import org.kie.workbench.common.stunner.core.registry.impl.DefinitionsCacheRegistry;
import org.kie.workbench.common.stunner.core.rule.Rule;
import org.kie.workbench.common.stunner.core.rule.impl.CanConnect;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class CommonDomainTargetNodeLookupTest {

    @Mock
    private DefinitionsCacheRegistry definitionsRegistry;

    @Mock
    private Function<String, DomainLookupsCache> cacheBuilder;

    @Mock
    private DomainLookupsCache cache;

    private CommonDomainLookups tested;
    private TestingGraphInstanceBuilder.TestGraph1 graph;

    @Before
    public void setup() throws Exception {
        final TestingGraphMockHandler graphTestHandler = new TestingGraphMockHandler();
        graph = TestingGraphInstanceBuilder.newGraph1(graphTestHandler);
        TestingGraphInstanceBuilder.createDefaultRulesForGraph1(graphTestHandler.ruleSet);

        when(cacheBuilder.apply(anyString())).thenReturn(cache);
        when(cache.getRuleSet()).thenReturn(graphTestHandler.ruleSet);
        CanConnect connectionRule = (CanConnect) ((List<Rule>) graphTestHandler.ruleSet.getRules()).get(0);
        when(cache.getConnectionRules()).thenReturn(Collections.singletonList(connectionRule));
        when(cache.getDefinitions(contains("label1"))).thenReturn(Stream.of(TestingGraphInstanceBuilder.DEF1_ID).collect(Collectors.toSet()));
        when(cache.getDefinitions(contains("label2"))).thenReturn(Stream.of(TestingGraphInstanceBuilder.DEF2_ID).collect(Collectors.toSet()));
        when(definitionsRegistry.getLabels(eq(TestingGraphInstanceBuilder.DEF1_ID))).thenReturn(new HashSet<>(Arrays.asList(TestingGraphInstanceBuilder.DEF1_LABELS)));
        when(definitionsRegistry.getLabels(eq(TestingGraphInstanceBuilder.DEF2_ID))).thenReturn(new HashSet<>(Arrays.asList(TestingGraphInstanceBuilder.DEF2_LABELS)));

        tested = new CommonDomainLookups(graphTestHandler.getDefinitionUtils(),
                                         definitionsRegistry,
                                         graphTestHandler.getRuleManager(),
                                         cacheBuilder)
                .setDomain("ds1");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testLookupTargetNodes() {
        Set<String> result = tested.lookupTargetNodes(graph.graph,
                                                      graph.startNode,
                                                      TestingGraphInstanceBuilder.EDGE1_ID);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(TestingGraphInstanceBuilder.DEF2_ID, result.iterator().next());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testLookupTargetNodesWithPredicate() {
        Predicate<String> filter = TestingGraphInstanceBuilder.DEF1_ID::equals;
        Set<String> result = tested.lookupTargetNodes(graph.graph,
                                                      graph.startNode,
                                                      TestingGraphInstanceBuilder.EDGE1_ID,
                                                      filter);
        assertTrue(result.isEmpty());
    }
}
