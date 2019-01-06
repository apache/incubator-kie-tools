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

package org.kie.workbench.common.stunner.core;

import java.util.Optional;
import java.util.Set;

import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.definition.adapter.AdapterManager;
import org.kie.workbench.common.stunner.core.definition.adapter.DefinitionAdapter;
import org.kie.workbench.common.stunner.core.definition.adapter.DefinitionId;
import org.kie.workbench.common.stunner.core.definition.adapter.DefinitionSetRuleAdapter;
import org.kie.workbench.common.stunner.core.definition.adapter.PropertyAdapter;
import org.kie.workbench.common.stunner.core.factory.impl.EdgeFactoryImpl;
import org.kie.workbench.common.stunner.core.factory.impl.GraphFactoryImpl;
import org.kie.workbench.common.stunner.core.factory.impl.NodeFactoryImpl;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.command.impl.GraphCommandFactory;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.graph.content.definition.DefinitionSet;
import org.kie.workbench.common.stunner.core.graph.content.view.BoundImpl;
import org.kie.workbench.common.stunner.core.graph.content.view.BoundsImpl;
import org.kie.workbench.common.stunner.core.graph.content.view.MagnetConnection;
import org.kie.workbench.common.stunner.core.graph.processing.index.MutableIndex;
import org.kie.workbench.common.stunner.core.registry.definition.AdapterRegistry;
import org.kie.workbench.common.stunner.core.registry.definition.TypeDefinitionSetRegistry;
import org.kie.workbench.common.stunner.core.rule.RuleEvaluationContext;
import org.kie.workbench.common.stunner.core.rule.RuleManager;
import org.kie.workbench.common.stunner.core.rule.RuleSet;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.kie.workbench.common.stunner.core.rule.RuleViolations;
import org.kie.workbench.common.stunner.core.rule.violations.DefaultRuleViolations;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertNotEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.anyDouble;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * An utility class for testing scope that provides
 * different mock objects and public methods in order
 * to create graph structures with some nodes and connectors.
 */
public class TestingGraphMockHandler {

    public static final String DEF_SET_ID = "defSetId";
    public static final String GRAPH_UUID = "graphUUID";
    private final RuleViolations EMPTY_VIOLATIONS = new DefaultRuleViolations();

    @Mock
    public GraphCommandExecutionContext graphCommandExecutionContext;
    @Mock
    public DefinitionManager definitionManager;
    @Mock
    public DefinitionUtils definitionUtils;
    @Mock
    public TypeDefinitionSetRegistry definitionSetRegistry;
    @Mock
    public AdapterManager adapterManager;
    @Mock
    public AdapterRegistry adapterRegistry;
    @Mock
    public DefinitionAdapter<Object> definitionAdapter;
    @Mock
    public PropertyAdapter propertyAdapter;
    @Mock
    public DefinitionSetRuleAdapter ruleAdapter;
    @Mock
    public FactoryManager factoryManager;
    @Mock
    public RuleManager ruleManager;
    @Mock
    public RuleSet ruleSet;
    @Mock
    public MutableIndex graphIndex;

    public GraphFactoryImpl graphFactory;
    public NodeFactoryImpl nodeFactory;
    public EdgeFactoryImpl edgeFactory;
    public GraphCommandFactory commandFactory;
    public Graph<DefinitionSet, Node> graph;

    public TestingGraphMockHandler() {
        init();
    }

    @SuppressWarnings("unchecked")
    private TestingGraphMockHandler init() {
        MockitoAnnotations.initMocks(this);
        this.graphFactory = new GraphFactoryImpl(definitionManager);
        this.nodeFactory = new NodeFactoryImpl(definitionUtils);
        this.edgeFactory = new EdgeFactoryImpl(definitionManager);
        this.commandFactory = new GraphCommandFactory();
        this.graph = graphFactory.build(GRAPH_UUID,
                                        DEF_SET_ID);
        when(definitionUtils.getDefinitionManager()).thenReturn(definitionManager);
        when(definitionManager.definitionSets()).thenReturn(definitionSetRegistry);
        when(definitionManager.adapters()).thenReturn(adapterManager);
        when(adapterManager.registry()).thenReturn(adapterRegistry);
        when(adapterManager.forDefinition()).thenReturn(definitionAdapter);
        when(adapterManager.forProperty()).thenReturn(propertyAdapter);
        when(adapterManager.forRules()).thenReturn(ruleAdapter);
        when(adapterRegistry.getDefinitionAdapter(any(Class.class))).thenReturn(definitionAdapter);
        when(adapterRegistry.getPropertyAdapter(any(Class.class))).thenReturn(propertyAdapter);
        when(graphCommandExecutionContext.getDefinitionManager()).thenReturn(definitionManager);
        when(graphCommandExecutionContext.getFactoryManager()).thenReturn(factoryManager);
        when(graphCommandExecutionContext.getRuleManager()).thenReturn(ruleManager);
        when(graphCommandExecutionContext.getGraphIndex()).thenReturn(graphIndex);
        when(graphCommandExecutionContext.getRuleSet()).thenReturn(ruleSet);
        when(graphIndex.getGraph()).thenReturn(graph);
        when(ruleManager.evaluate(any(RuleSet.class),
                                  any(RuleEvaluationContext.class))).thenReturn(EMPTY_VIOLATIONS);
        return this;
    }

    public Object getDefIfPresent(final String id,
                                  final Optional<Object> actual) {
        Object definition = null;
        if (actual.isPresent()) {
            definition = actual.get();
            if (null == definitionAdapter.getId(definition)) {
                when(definitionAdapter.getId(eq(definition))).thenReturn(DefinitionId.build(id));
            }
        } else {
            definition = newDef("def-" + id,
                                Optional.empty());
        }
        return definition;
    }

    public Object newDef(final String id,
                         final Optional<Set<String>> labels) {
        final Object def = mock(Object.class);
        mockDefAttributes(def,
                          id,
                          labels);
        return def;
    }

    public void mockDefAttributes(final Object def,
                                  final String id,
                                  final Optional<Set<String>> labels) {
        when(definitionAdapter.getId(def)).thenReturn(DefinitionId.build(id));
        if (labels.isPresent()) {
            when(definitionAdapter.getLabels(def)).thenReturn(labels.get());
        }
    }

    public Node newNode(String uuid,
                        String id,
                        Optional<Set<String>> labels) {
        return newViewNode(uuid,
                           Optional.of(newDef(id,
                                              labels)),
                           0,
                           0,
                           100,
                           100);
    }

    public Node newNode(String uuid,
                        Optional<Object> def) {
        return newViewNode(uuid,
                           def,
                           0,
                           0,
                           100,
                           100);
    }

    public Node newViewNode(String uuid,
                            Optional<Object> def,
                            final double x,
                            final double y,
                            final double w,
                            final double h) {
        final Object definition = getDefIfPresent(uuid, def);
        when(definitionUtils.buildBounds(eq(definition),
                                         anyDouble(),
                                         anyDouble()))
                .thenReturn(new BoundsImpl(
                        new BoundImpl(x,
                                      y),
                        new BoundImpl(x + w,
                                      y + h)
                ));
        final Node<Definition<Object>, Edge> result = nodeFactory.build(uuid,
                                                                        definition);
        execute(commandFactory.addNode(result));
        when(graphIndex.getNode(eq(uuid))).thenReturn(result);
        when(graphIndex.get(eq(uuid))).thenReturn(result);
        return result;
    }

    public Edge newEdge(String uuid,
                        String id,
                        final Optional<Set<String>> labels) {
        final Object definition = newDef(id,
                                         labels);
        return newEdge(uuid, definition);
    }

    private Edge newEdge(String uuid, Object definition) {
        final Edge<Definition<Object>, Node> edge = edgeFactory.build(uuid,
                                                                      definition);
        when(graphIndex.getEdge(eq(uuid))).thenReturn(edge);
        when(graphIndex.get(eq(uuid))).thenReturn(edge);
        return edge;
    }

    public Edge newEdge(String uuid,
                        final Optional<Object> def) {
        final Object definition = getDefIfPresent(uuid, def);
        return newEdge(uuid, definition);
    }

    @SuppressWarnings("unchecked")
    public TestingGraphMockHandler setChild(final Node parent,
                                            final Node candidate) {
        return execute(commandFactory.setChildNode(parent,
                                                   candidate));
    }

    @SuppressWarnings("unchecked")
    public TestingGraphMockHandler removeChild(final Node parent,
                                               final Node candidate) {
        return execute(commandFactory.removeChild(parent,
                                                  candidate));
    }

    @SuppressWarnings("unchecked")
    public TestingGraphMockHandler dockTo(final Node parent,
                                          final Node candidate) {
        return execute(commandFactory.dockNode(parent,
                                               candidate));
    }

    @SuppressWarnings("unchecked")
    public TestingGraphMockHandler addEdge(final Edge edge,
                                           final Node source) {
        return execute(commandFactory.addConnector(source,
                                                   edge,
                                                   MagnetConnection.Builder.at(0d,
                                                                               0d)));
    }

    @SuppressWarnings("unchecked")
    public TestingGraphMockHandler connectTo(final Edge edge,
                                             final Node target) {
        return execute(commandFactory.setTargetNode(target,
                                                    edge,
                                                    MagnetConnection.Builder.at(0d,
                                                                                0d)));
    }

    @SuppressWarnings("unchecked")
    public TestingGraphMockHandler removeTargetConnection(final Edge edge) {
        return execute(commandFactory.setTargetNode(null,
                                                    edge,
                                                    MagnetConnection.Builder.at(0d,
                                                                                0d)));
    }

    private TestingGraphMockHandler execute(final
                                            Command<GraphCommandExecutionContext, RuleViolation> command) {
        assertCommandResult(command.execute(graphCommandExecutionContext));
        return this;
    }

    private static void assertCommandResult(CommandResult<RuleViolation> result) {
        assertNotEquals(CommandResult.Type.ERROR,
                        result.getType());
    }
}
