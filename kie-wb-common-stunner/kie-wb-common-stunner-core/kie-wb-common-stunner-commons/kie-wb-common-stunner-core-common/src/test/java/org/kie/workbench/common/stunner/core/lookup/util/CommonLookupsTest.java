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
package org.kie.workbench.common.stunner.core.lookup.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.definition.adapter.AdapterManager;
import org.kie.workbench.common.stunner.core.definition.adapter.DefinitionAdapter;
import org.kie.workbench.common.stunner.core.definition.adapter.DefinitionSetAdapter;
import org.kie.workbench.common.stunner.core.definition.adapter.DefinitionSetRuleAdapter;
import org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableAdapterUtils;
import org.kie.workbench.common.stunner.core.factory.graph.EdgeFactory;
import org.kie.workbench.common.stunner.core.factory.graph.NodeFactory;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.lookup.definition.DefinitionLookupManager;
import org.kie.workbench.common.stunner.core.lookup.definition.DefinitionLookupManagerImpl;
import org.kie.workbench.common.stunner.core.lookup.rule.RuleLookupManager;
import org.kie.workbench.common.stunner.core.lookup.rule.RuleLookupManagerImpl;
import org.kie.workbench.common.stunner.core.registry.definition.AdapterRegistry;
import org.kie.workbench.common.stunner.core.registry.definition.TypeDefinitionRegistry;
import org.kie.workbench.common.stunner.core.registry.definition.TypeDefinitionSetRegistry;
import org.kie.workbench.common.stunner.core.registry.impl.DefinitionsCacheRegistry;
import org.kie.workbench.common.stunner.core.rule.EmptyRuleSet;
import org.kie.workbench.common.stunner.core.rule.Rule;
import org.kie.workbench.common.stunner.core.rule.RuleManager;
import org.kie.workbench.common.stunner.core.rule.RuleSet;
import org.kie.workbench.common.stunner.core.rule.RuleSetImpl;
import org.kie.workbench.common.stunner.core.rule.impl.CanConnect;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CommonLookupsTest {

    @Mock
    private DefinitionUtils definitionUtils;

    @Mock
    private DefinitionManager definitionManager;

    @Mock
    private TypeDefinitionSetRegistry typeDefinitionSetRegistry;

    @Mock
    private AdapterManager adapterManager;

    @Mock
    private AdapterRegistry adapterRegistry;

    @Mock
    private DefinitionAdapter definitionAdapter;

    @Mock
    private DefinitionSetAdapter definitionSetAdapter;

    @Mock
    private DefinitionSetRuleAdapter definitionSetRuleAdapter;

    @Mock
    private RuleManager ruleManager;

    @Mock
    private FactoryManager factoryManager;

    @Mock
    private DefinitionsCacheRegistry registry;

    @Mock
    private TypeDefinitionRegistry typeDefinitionRegistry;

    @Mock
    private DefinitionAdapter mockDefinitionAdaptor;

    @Mock
    private DefinitionAdapter mockConnectionAdaptor;

    @Mock
    private Graph graph;

    @Mock
    private Node node;

    @Mock
    private MockNodeContent nodeContent;

    private RuleSet ruleSet;

    private RuleLookupManager ruleLookupManager;

    private DefinitionLookupManager definitionLookupManager;

    private CommonLookups lookups;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() {
        when(typeDefinitionRegistry.getDefinitionById(eq(MockDefinition.class.getName()))).thenReturn(new MockDefinition());
        when(typeDefinitionRegistry.getDefinitionById(eq(MockConnector.class.getName()))).thenReturn(new MockConnector());

        this.ruleSet = new EmptyRuleSet();
        this.ruleLookupManager = new RuleLookupManagerImpl(definitionManager);
        this.definitionLookupManager = new DefinitionLookupManagerImpl(definitionManager,
                                                                       factoryManager,
                                                                       registry);
        this.lookups = new CommonLookups(definitionUtils,
                                         ruleManager,
                                         definitionLookupManager,
                                         ruleLookupManager,
                                         registry);

        when(node.getContent()).thenReturn(nodeContent);
        when(nodeContent.getDefinition()).thenReturn(new MockDefinition());
        when(definitionUtils.getDefinitionManager()).thenReturn(definitionManager);
        when(definitionManager.definitionSets()).thenReturn(typeDefinitionSetRegistry);
        when(typeDefinitionSetRegistry.getDefinitionSetById(eq(MockDefinitionSet.class.getName()))).thenReturn(new MockDefinitionSet());
        when(definitionManager.adapters()).thenReturn(adapterManager);
        when(adapterManager.registry()).thenReturn(adapterRegistry);
        when(adapterRegistry.getDefinitionSetRuleAdapter(eq(MockDefinitionSet.class))).thenReturn(definitionSetRuleAdapter);
        when(adapterManager.forDefinition()).thenReturn(definitionAdapter);
        when(adapterManager.forRules()).thenReturn(definitionSetRuleAdapter);
        when(adapterManager.forDefinitionSet()).thenReturn(definitionSetAdapter);
        when(definitionSetAdapter.getDefinitions(any(MockDefinitionSet.class))).thenReturn(new HashSet<String>() {{
            add(MockDefinition.class.getName());
            add(MockConnector.class.getName());
        }});

        when(adapterRegistry.getDefinitionAdapter(eq(MockDefinition.class))).thenReturn(mockDefinitionAdaptor);
        when(adapterRegistry.getDefinitionAdapter(eq(MockConnector.class))).thenReturn(mockConnectionAdaptor);
        when(mockDefinitionAdaptor.getLabels(any(MockDefinition.class))).thenReturn(Collections.singleton("definition-role"));
        when(mockConnectionAdaptor.getLabels(any(MockConnector.class))).thenReturn(Collections.singleton("connector-role"));
        when(mockDefinitionAdaptor.getGraphFactoryType(any(MockDefinition.class))).thenReturn(NodeFactory.class);
        when(mockConnectionAdaptor.getGraphFactoryType(any(MockConnector.class))).thenReturn(EdgeFactory.class);

        when(graph.nodes()).thenReturn(Collections.emptyList());

        when(registry.getDefinitionById(eq(BindableAdapterUtils.getDefinitionId(MockDefinition.class))))
                .thenReturn(new MockDefinition());
        when(registry.getDefinitionById(eq(BindableAdapterUtils.getDefinitionId(MockConnector.class))))
                .thenReturn(new MockConnector());
        when(factoryManager.newDefinition(eq(MockDefinition.class.getName()))).thenReturn(new MockDefinition());
        when(factoryManager.newDefinition(eq(MockConnector.class.getName()))).thenReturn(new MockConnector());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void checkAllowedTargetDefinitionsWithPermittedConnectionRules() {
        this.ruleSet = new RuleSetImpl("connection-rules",
                                       new ArrayList<Rule>() {{
                                           add(new CanConnect(MockConnector.class.getName(),
                                                              MockConnector.class.getName(),
                                                              new ArrayList<CanConnect.PermittedConnection>() {{
                                                                  add(new CanConnect.PermittedConnection("definition-role",
                                                                                                         "definition-role"));
                                                              }}));
                                       }});

        when(definitionAdapter.getLabels(any(MockDefinition.class))).thenReturn(Collections.singleton("definition-role"));
        when(definitionSetRuleAdapter.getRuleSet(any(MockDefinitionSet.class))).thenReturn(ruleSet);

        final Set<Object> targetDefinitions = lookups.getAllowedTargetDefinitions(MockDefinitionSet.class.getName(),
                                                                                  graph,
                                                                                  node,
                                                                                  MockConnector.class.getName(),
                                                                                  0,
                                                                                  10);
        assertEquals(1,
                     targetDefinitions.size());
        assertTrue(targetDefinitions.iterator().next() instanceof MockDefinition);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void checkAllowedTargetDefinitionsWithNotPermittedStartConnectionRules() {
        this.ruleSet = new RuleSetImpl("connection-rules",
                                       new ArrayList<Rule>() {{
                                           add(new CanConnect(MockConnector.class.getName(),
                                                              MockConnector.class.getName(),
                                                              new ArrayList<CanConnect.PermittedConnection>() {{
                                                                  add(new CanConnect.PermittedConnection("not-permitted",
                                                                                                         "definition-role"));
                                                              }}));
                                       }});

        when(definitionAdapter.getLabels(any(MockDefinition.class))).thenReturn(Collections.singleton("definition-role"));
        when(definitionSetRuleAdapter.getRuleSet(any(MockDefinitionSet.class))).thenReturn(ruleSet);

        final Set<Object> targetDefinitions = lookups.getAllowedTargetDefinitions(MockDefinitionSet.class.getName(),
                                                                                  graph,
                                                                                  node,
                                                                                  MockConnector.class.getName(),
                                                                                  0,
                                                                                  10);
        assertTrue(targetDefinitions.isEmpty());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void checkAllowedTargetDefinitionsWithNotPermittedEndConnectionRules() {
        this.ruleSet = new RuleSetImpl("connection-rules",
                                       new ArrayList<Rule>() {{
                                           add(new CanConnect(MockConnector.class.getName(),
                                                              MockConnector.class.getName(),
                                                              new ArrayList<CanConnect.PermittedConnection>() {{
                                                                  add(new CanConnect.PermittedConnection("definition-role",
                                                                                                         "not-permitted"));
                                                              }}));
                                       }});

        when(definitionAdapter.getLabels(any(MockDefinition.class))).thenReturn(Collections.singleton("definition-role"));
        when(definitionSetRuleAdapter.getRuleSet(any(MockDefinitionSet.class))).thenReturn(ruleSet);

        final Set<Object> targetDefinitions = lookups.getAllowedTargetDefinitions(MockDefinitionSet.class.getName(),
                                                                                  graph,
                                                                                  node,
                                                                                  MockConnector.class.getName(),
                                                                                  0,
                                                                                  10);
        assertTrue(targetDefinitions.isEmpty());
    }

    private static class MockDefinitionSet {

    }

    private static class MockDefinition {

    }

    private static class MockNodeContent implements Definition<MockDefinition> {

        private MockDefinition definition;

        @Override
        public MockDefinition getDefinition() {
            return definition;
        }

        @Override
        public void setDefinition(final MockDefinition definition) {
            this.definition = definition;
        }
    }

    private static class MockConnector {

    }
}
