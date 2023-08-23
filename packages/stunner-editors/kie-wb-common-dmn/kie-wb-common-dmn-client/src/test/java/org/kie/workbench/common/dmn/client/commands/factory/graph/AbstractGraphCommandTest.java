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
package org.kie.workbench.common.dmn.client.commands.factory.graph;

import java.util.LinkedList;

import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.definition.adapter.AdapterManager;
import org.kie.workbench.common.stunner.core.definition.adapter.DefinitionAdapter;
import org.kie.workbench.common.stunner.core.definition.adapter.PropertyAdapter;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.command.DirectGraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.content.Bound;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.definition.DefinitionSet;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.processing.index.MutableIndex;
import org.kie.workbench.common.stunner.core.registry.definition.AdapterRegistry;
import org.kie.workbench.common.stunner.core.rule.RuleManager;
import org.kie.workbench.common.stunner.core.rule.RuleSet;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public abstract class AbstractGraphCommandTest {

    protected TestingGraphMockHandler testingGraphMockHandler;
    protected DefinitionManager definitionManager;
    protected AdapterManager adapterManager;
    protected AdapterRegistry adapterRegistry;
    protected DefinitionAdapter definitionAdapter;
    protected PropertyAdapter propertyAdapter;
    protected FactoryManager factoryManager;
    protected RuleManager ruleManager;
    protected MutableIndex graphIndex;
    protected Graph<DefinitionSet, Node> graph;
    protected RuleSet ruleSet;
    protected DefinitionSet graphContent;
    protected GraphCommandExecutionContext graphCommandExecutionContext;

    @SuppressWarnings("unchecked")
    public void init() {
        testingGraphMockHandler = new TestingGraphMockHandler();
        definitionManager = testingGraphMockHandler.getDefinitionManager();
        adapterManager = testingGraphMockHandler.getAdapterManager();
        adapterRegistry = testingGraphMockHandler.getAdapterRegistry();
        definitionAdapter = testingGraphMockHandler.getDefinitionAdapter();
        propertyAdapter = testingGraphMockHandler.getPropertyAdapter();
        factoryManager = testingGraphMockHandler.getFactoryManager();
        ruleManager = testingGraphMockHandler.getRuleManager();
        graphIndex = testingGraphMockHandler.graphIndex;
        graph = testingGraphMockHandler.graph;
        ruleSet = testingGraphMockHandler.ruleSet;
        graphContent = testingGraphMockHandler.graph.getContent();
        graphCommandExecutionContext = testingGraphMockHandler.graphCommandExecutionContext;
    }

    protected DirectGraphCommandExecutionContext createAllowedExecutionContext() {
        return new DirectGraphCommandExecutionContext(definitionManager, factoryManager, graphIndex);
    }

    protected void useAllowedExecutionContext() {
        graphCommandExecutionContext = createAllowedExecutionContext();
    }

    public static Node mockNode(String uuid) {
        Node node = mock(Node.class);
        when(node.getUUID()).thenReturn(uuid);
        when(node.asNode()).thenReturn(node);
        when(node.getInEdges()).thenReturn(new LinkedList());
        when(node.getOutEdges()).thenReturn(new LinkedList());
        return node;
    }

    public static Edge mockEdge(String uuid) {
        Edge edge = mock(Edge.class);
        when(edge.getUUID()).thenReturn(uuid);
        when(edge.asEdge()).thenReturn(edge);
        return edge;
    }

    public static View mockView(final double x,
                                final double y,
                                final double w,
                                final double h) {
        View view = mock(View.class);
        Bounds bounds = mockBounds(x,
                                   y,
                                   w,
                                   h);
        when(view.getBounds()).thenReturn(bounds);
        return view;
    }

    public static Bounds mockBounds(final double x,
                                    final double y,
                                    final double w,
                                    final double h) {
        Bounds bounds = mock(Bounds.class);
        Bound boundUL = mock(Bound.class);
        Bound boundLR = mock(Bound.class);
        when(boundUL.getX()).thenReturn(x);
        when(boundUL.getY()).thenReturn(y);
        when(boundLR.getX()).thenReturn(x + w);
        when(boundLR.getY()).thenReturn(y + h);
        when(bounds.getLowerRight()).thenReturn(boundLR);
        when(bounds.getUpperLeft()).thenReturn(boundUL);
        return bounds;
    }
}
