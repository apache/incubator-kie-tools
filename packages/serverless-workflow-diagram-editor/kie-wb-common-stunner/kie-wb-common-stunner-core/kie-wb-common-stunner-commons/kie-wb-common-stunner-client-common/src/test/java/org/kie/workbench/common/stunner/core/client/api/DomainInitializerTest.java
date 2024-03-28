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


package org.kie.workbench.common.stunner.core.client.api;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;

import jakarta.inject.Qualifier;
import jsinterop.base.JsPropertyMap;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.j2cl.tools.di.core.BeanManager;
import org.kie.j2cl.tools.di.core.ManagedInstance;
import org.kie.j2cl.tools.di.ui.translation.client.TranslationService;
import org.kie.workbench.common.stunner.core.api.JsDefinitionManager;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
import org.kie.workbench.common.stunner.core.client.registry.impl.ClientRegistryFactoryImpl;
import org.kie.workbench.common.stunner.core.definition.adapter.AdapterManagerImpl;
import org.kie.workbench.common.stunner.core.definition.adapter.bootstrap.BootstrapAdapterFactory;
import org.kie.workbench.common.stunner.core.definition.clone.CloneManager;
import org.kie.workbench.common.stunner.core.definition.jsadapter.JsDefinitionAdapter;
import org.kie.workbench.common.stunner.core.definition.jsadapter.JsDefinitionProperty;
import org.kie.workbench.common.stunner.core.definition.jsadapter.JsDefinitionSetAdapter;
import org.kie.workbench.common.stunner.core.definition.jsadapter.JsPropertyAdapter;
import org.kie.workbench.common.stunner.core.definition.jsadapter.JsRuleAdapter;
import org.kie.workbench.common.stunner.core.factory.graph.NodeFactory;
import org.kie.workbench.common.stunner.core.i18n.StunnerTranslationService;
import org.kie.workbench.common.stunner.core.registry.DynamicRegistry;
import org.kie.workbench.common.stunner.core.registry.definition.TypeDefinitionSetRegistry;
import org.kie.workbench.common.stunner.core.rule.Rule;
import org.kie.workbench.common.stunner.core.rule.RuleSetImpl;
import org.kie.workbench.common.stunner.core.rule.context.EdgeCardinalityContext;
import org.kie.workbench.common.stunner.core.rule.impl.CanConnect;
import org.kie.workbench.common.stunner.core.rule.impl.CanContain;
import org.kie.workbench.common.stunner.core.rule.impl.CanDock;
import org.kie.workbench.common.stunner.core.rule.impl.EdgeOccurrences;
import org.kie.workbench.common.stunner.core.rule.impl.Occurrences;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.kie.workbench.common.stunner.core.validation.DiagramElementNameProvider;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DomainInitializerTest {

    @Mock
    private TranslationService translationService;

    @Mock
    private ManagedInstance<DiagramElementNameProvider> elementNameProviders;

    @Mock
    private SessionManager sessionManager;

    @Mock
    private DefinitionUtils definitionUtils;

    @Mock
    private ClientRegistryFactoryImpl registryFactory;

    @Mock
    private CloneManager cloneManager;

    private JsDefinitionAdapter jsDefinitionAdapter;
    private JsDefinitionSetAdapter jsDefinitionSetAdapter;
    private TypeDefinitionSetRegistry<Object> registry;
    private DomainInitializer tested;

    @Before
    public void setup() {
        when(registryFactory.newDefinitionSetRegistry()).thenCallRealMethod();
        doCallRealMethod().when(registryFactory).setAdapterManager(any());

        BeanManager beanManager = mock(BeanManager.class);
        BootstrapAdapterFactory bootstrapAdapterFactory = spy(new BootstrapAdapterFactory());
        AdapterManagerImpl adapterManager = spy(new AdapterManagerImpl(bootstrapAdapterFactory));
        registryFactory.setAdapterManager(adapterManager);
        registry = spy(registryFactory.newDefinitionSetRegistry());
        ClientDefinitionManager definitionManager = spy(new ClientDefinitionManager(beanManager, registryFactory, adapterManager, cloneManager));
        jsDefinitionAdapter = spy(new JsDefinitionAdapter());
        JsPropertyAdapter jsPropertyAdapter = spy(new JsPropertyAdapter());
        StunnerTranslationService stunnerTranslationService = new ClientTranslationService(translationService, elementNameProviders, sessionManager, definitionUtils);
        jsDefinitionSetAdapter = spy(new JsDefinitionSetAdapter());
        JsRuleAdapter jsRuleAdapter = spy(new JsRuleAdapter());
        tested = spy(new DomainInitializer());
        JsDefinitionManager jsDefinitionManager = spy(new JsDefinitionManager(registry,
                                                                              jsDefinitionSetAdapter,
                                                                              jsDefinitionAdapter,
                                                                              jsPropertyAdapter,
                                                                              jsRuleAdapter));

        tested.definitionManager = definitionManager;
        tested.jsDefinitionAdapter = jsDefinitionAdapter;
        tested.jsPropertyAdapter = jsPropertyAdapter;
        tested.translationService = stunnerTranslationService;
        tested.jsDefinitionSetAdapter = jsDefinitionSetAdapter;
        tested.rules = new HashSet<>();

        JsWindow.setEditor(new JsStunnerEditor());
        JsWindow.getEditor().setDefinitions(jsDefinitionManager);

        when(adapterManager.forDefinitionSet()).thenReturn(new JsDefinitionSetAdapter());
    }

    @SuppressWarnings("all")
    @Test
    public void testInitializeDefinitionSet() {
        final DefinitionSetTest definitionSetTest = new DefinitionSetTest();

        tested.initializeDefinitionSet(definitionSetTest);

        verify(JsWindow.getEditor().getDefinitions(), times(1)).initializeDefinitionSet(definitionSetTest);
        verify(((DynamicRegistry) registry), times(1)).register(definitionSetTest);
    }

    @Test
    public void testInitializeDefinitionsField() {
        final String definitionsField = "definitions";
        final JsPropertyMap<Object> pojo = new JsPropertyMap<Object>() {
            @Override
            public Object get(String propertyName) {
                return definitionsField;
            }
        };

        tested.initializeDefinitionsField(definitionsField);

        verify(JsWindow.getEditor().getDefinitions(), times(1)).initializeDefinitionsField(definitionsField);
        assertTrue(jsDefinitionSetAdapter.getDefinitions(pojo).contains(definitionsField));
    }

    @Test
    public void testInitializeDomainQualifier() {
        final Object pojo = new Object();
        final TestEditor testEditor = new TestEditor() {
            @Override
            public Class<? extends Annotation> annotationType() {
                return TestEditor.class;
            }
        };

        tested.initializeDomainQualifier(testEditor);

        verify(JsWindow.getEditor().getDefinitions(), times(1)).initializeDomainQualifier(testEditor);
        assertEquals(jsDefinitionSetAdapter.getQualifier(pojo), testEditor);
    }

    @Test
    public void testInitializeCategory() {
        final Start pojo = new Start();
        final Class<? extends Start> type = pojo.getClass();
        final String category = "States";

        tested.initializeCategory(type, category);

        verify(JsWindow.getEditor().getDefinitions(), times(1)).initializeCategory(type.getName(), category);
        assertEquals(jsDefinitionAdapter.getCategory(pojo), category);
        assertEquals(jsDefinitionAdapter.getId(pojo).type(), DomainInitializerTest.class.getName() + "$Start");
    }

    @Test
    public void testInitializeElementFactory() {
        final State pojo = new State();
        final Class<? extends State> type = pojo.getClass();
        final String category = "States";

        tested.initializeCategory(type, category);
        tested.initializeElementFactory(NodeFactory.class, category);

        assertEquals(jsDefinitionAdapter.getElementFactory(pojo), NodeFactory.class);
    }

    @Test
    public void testInitializeLabels() {
        final Start pojo = new Start();
        final Class<? extends Start> type = pojo.getClass();
        final String[] labels = {"rootNode", "start"};

        tested.initializeLabels(Start.class, "rootNode", "start");

        verify(JsWindow.getEditor().getDefinitions(), times(1)).initializeLabels(type.getName(), labels);
        assertArrayEquals(jsDefinitionAdapter.getLabels(pojo), labels);
    }

    @Test
    public void testInitializeDefinitionNameField() {
        final Start pojo = new Start();
        final Class<? extends Start> type = pojo.getClass();
        final String field = "nameField";

        tested.initializeDefinitionNameField(Start.class, field);
        final Optional<?> property = jsDefinitionAdapter.getProperty(pojo, field);

        verify(JsWindow.getEditor().getDefinitions(), times(1)).initializeDefinitionNameField(type.getName(), field);
        assertTrue(property.isPresent());
        JsDefinitionProperty name = (JsDefinitionProperty) property.get();
        assertEquals(pojo, name.getPojo());
        assertEquals(field, name.getField());
    }

    @Test
    public void testInitializeRules() {
        final Workflow workflow = new Workflow();
        final Class<? extends Workflow> workflowType = workflow.getClass();

        tested.setContainmentRule(workflowType, "rootNode")
                .setConnectionRule(Transition.class, new String[]{"state", "end"})
                .setDockingRule(Start.class, "timeout")
                .setOccurrences("start", 0, 1)
                .setEdgeOccurrences(Transition.class, "state", true, 0, -1)
                .setEdgeOccurrences(Transition.class, "state", false, 0, 0)
                .initializeRules();

        // Containment
        final CanContain canContain = (CanContain) getRule("CAN_CONTAIN0", tested.rules);
        assertEquals("CAN_CONTAIN0", canContain.getName());
        assertEquals(DomainInitializerTest.class.getName() + "$Workflow", canContain.getRole());
        assertArrayEquals(new String[]{"rootNode"}, canContain.getAllowedRoles().toArray());

        // Connection
        final CanConnect canConnect = (CanConnect) getRule("CAN_CONNECT1", tested.rules);
        assertEquals("CAN_CONNECT1", canConnect.getName());
        assertEquals(DomainInitializerTest.class.getName() + "$Transition", canConnect.getRole());
        assertEquals(1, canConnect.getPermittedConnections().size());
        assertEquals("state", canConnect.getPermittedConnections().get(0).getStartRole());
        assertEquals("end", canConnect.getPermittedConnections().get(0).getEndRole());

        // Docking
        final CanDock canDock = (CanDock) getRule("CAN_DOCK2", tested.rules);
        assertEquals("CAN_DOCK2", canDock.getName());
        assertEquals(DomainInitializerTest.class.getName() + "$Start", canDock.getRole());
        assertEquals(1, canDock.getAllowedRoles().size());
        assertTrue(canDock.getAllowedRoles().contains("timeout"));

        // Occurrences
        final Occurrences occurrences = (Occurrences) getRule("OCCURRENCES3", tested.rules);
        assertEquals("OCCURRENCES3", occurrences.getName());
        assertEquals("start", occurrences.getRole());
        assertEquals(0, occurrences.getMinOccurrences());
        assertEquals(1, occurrences.getMaxOccurrences());

        // Edge Occurrences
        final EdgeOccurrences edgeOccurrences1 = (EdgeOccurrences) getRule("EDGE_OCCURRENCES4", tested.rules);
        assertEquals("EDGE_OCCURRENCES4", edgeOccurrences1.getName());
        assertEquals(DomainInitializerTest.class.getName() + "$Transition", edgeOccurrences1.getConnectorRole());
        assertEquals("state", edgeOccurrences1.getRole());
        assertEquals(EdgeCardinalityContext.Direction.INCOMING, edgeOccurrences1.getDirection());
        assertEquals(0, edgeOccurrences1.getMinOccurrences());
        assertEquals(-1, edgeOccurrences1.getMaxOccurrences());

        final EdgeOccurrences edgeOccurrences2 = (EdgeOccurrences) getRule("EDGE_OCCURRENCES5", tested.rules);
        assertEquals("EDGE_OCCURRENCES5", edgeOccurrences2.getName());
        assertEquals(DomainInitializerTest.class.getName() + "$Transition", edgeOccurrences2.getConnectorRole());
        assertEquals("state", edgeOccurrences2.getRole());
        assertEquals(EdgeCardinalityContext.Direction.OUTGOING, edgeOccurrences2.getDirection());
        assertEquals(0, edgeOccurrences2.getMinOccurrences());
        assertEquals(0, edgeOccurrences2.getMaxOccurrences());

        // Initialization
        verify(JsWindow.getEditor().getDefinitions(), times(1)).initializeRules(any(RuleSetImpl.class));
    }

    private Rule getRule(final String ruleName, final Collection<Rule> rules) {
        for (Rule rule : rules) {
            if (ruleName.equals(rule.getName())) {
                return rule;
            }
        }
        return null;
    }

    class DefinitionSetTest {

        public String definitions;

        public DefinitionSetTest() {
            definitions = "org.kie.workbench.common.stunner.core.definition.adapter.DefinitionSetAdapter";
        }
    }

    @Qualifier
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
    @interface TestEditor {

    }

    @SuppressWarnings("all")
    class Start {

    }

    @SuppressWarnings("all")
    class State {

    }

    @SuppressWarnings("all")
    class Workflow {

    }

    @SuppressWarnings("all")
    class Transition {

    }
}
