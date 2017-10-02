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

package org.kie.workbench.common.stunner.bpmn.backend.service.marshaller.json.builder;

import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.backend.marshall.json.builder.EdgeBuilderImpl;
import org.kie.workbench.common.stunner.bpmn.backend.marshall.json.builder.GraphObjectBuilder;
import org.kie.workbench.common.stunner.bpmn.backend.marshall.json.oryx.OryxIdMappings;
import org.kie.workbench.common.stunner.bpmn.backend.marshall.json.oryx.OryxManager;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNDefinition;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.definition.adapter.AdapterManager;
import org.kie.workbench.common.stunner.core.definition.adapter.DefinitionAdapter;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.command.impl.GraphCommandFactory;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.processing.index.MutableIndex;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class EdgeBuilderImplTest {

    private static String EDGE_ID = "edge";
    private static String DEFINITION_ID = "definition";

    @Mock
    private GraphObjectBuilder.BuilderContext context;

    @Mock
    private FactoryManager factoryManager;

    @Mock
    private MutableIndex index;

    @Mock
    private Edge edge;

    @Mock
    private OryxManager oryxManager;

    @Mock
    private OryxIdMappings oryxIdMappings;

    @Mock
    private BPMNDefinition definition;

    @Mock
    private DefinitionManager definitionManager;

    @Mock
    private AdapterManager adapters;

    @Mock
    private DefinitionAdapter forDefinition;

    @Mock
    private View view;

    @Mock
    private Set set;

    @Mock
    private CommandResult<RuleViolation> res;

    @Mock
    private GraphCommandFactory graphCommandFactory;

    private EdgeBuilderImpl edgeBuilder;

    @Before
    public void setup() throws Exception {
        when(context.getFactoryManager()).thenReturn(factoryManager);
        when(context.getIndex()).thenReturn(index);
        edgeBuilder = new EdgeBuilderImpl(Edge.class);
    }

    @Test
    public void testDoBuild() {
        when(context.getIndex().getNode(EDGE_ID)).thenReturn(null);
        when(context.getOryxManager()).thenReturn(oryxManager);
        when(context.getOryxManager().getMappingsManager()).thenReturn(oryxIdMappings);
        when(context.getOryxManager().getMappingsManager().getDefinitionId(any(Class.class))).thenReturn(DEFINITION_ID);
        when(factoryManager.newElement(null,
                                       DEFINITION_ID)).thenReturn(edge);
        when(edge.getContent()).thenReturn(view);
        when(view.getDefinition()).thenReturn(definition);
        when(context.getDefinitionManager()).thenReturn(definitionManager);
        when(context.getDefinitionManager().adapters()).thenReturn(adapters);
        when(context.getDefinitionManager().adapters().forDefinition()).thenReturn(forDefinition);
        when(context.getDefinitionManager().adapters().forDefinition().getProperties(definition)).thenReturn(set);
        when(context.execute(any())).thenReturn(res);
        when(context.getCommandFactory()).thenReturn(graphCommandFactory);

        Element edge1 = edgeBuilder.build(context);

        Assert.assertEquals(edge1,
                            edge);
    }

    @Test
    public void testDoBuildReturnNull() {
        when(context.getIndex().getEdge(EDGE_ID)).thenReturn(edge);

        edgeBuilder.nodeId(EDGE_ID);
        Element edge1 = edgeBuilder.build(context);
        Assert.assertEquals(edge1,
                            null);
    }
}