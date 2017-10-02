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
import org.kie.workbench.common.stunner.bpmn.backend.marshall.json.builder.GraphObjectBuilder;
import org.kie.workbench.common.stunner.bpmn.backend.marshall.json.builder.NodeBuilderImpl;
import org.kie.workbench.common.stunner.bpmn.backend.marshall.json.oryx.OryxIdMappings;
import org.kie.workbench.common.stunner.bpmn.backend.marshall.json.oryx.OryxManager;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNDefinition;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.definition.adapter.AdapterManager;
import org.kie.workbench.common.stunner.core.definition.adapter.DefinitionAdapter;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.command.impl.GraphCommandFactory;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.processing.index.Index;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class NodeBuilderImplTest {

    private static String NODE_ID = "node";
    private static String DEFINITION_ID = "definition";

    @Mock
    private GraphObjectBuilder.BuilderContext context;

    @Mock
    private FactoryManager factoryManager;

    @Mock
    private Index index;

    @Mock
    private Node node;

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

    NodeBuilderImpl nodeBuilder;

    @Before
    public void setup() throws Exception {
        when(context.getFactoryManager()).thenReturn(factoryManager);
        when(context.getIndex()).thenReturn(index);

        nodeBuilder = new NodeBuilderImpl(Node.class);
    }

    @Test
    public void testDoBuildExistingNode() {
        when(context.getIndex().getNode(NODE_ID)).thenReturn(node);

        nodeBuilder.nodeId(NODE_ID);
        Element node1 = nodeBuilder.build(context);
        Assert.assertEquals(node1,
                            node);
    }

    @Test
    public void testDoBuildNewNode() {
        when(context.getIndex().getNode(NODE_ID)).thenReturn(null);
        when(context.getOryxManager()).thenReturn(oryxManager);
        when(context.getOryxManager().getMappingsManager()).thenReturn(oryxIdMappings);
        when(context.getOryxManager().getMappingsManager().getDefinitionId(any(Class.class))).thenReturn(DEFINITION_ID);
        when(factoryManager.newElement(null,
                                       DEFINITION_ID)).thenReturn(node);
        when(node.getContent()).thenReturn(view);
        when(view.getDefinition()).thenReturn(definition);
        when(context.getDefinitionManager()).thenReturn(definitionManager);
        when(context.getDefinitionManager().adapters()).thenReturn(adapters);
        when(context.getDefinitionManager().adapters().forDefinition()).thenReturn(forDefinition);
        when(context.getDefinitionManager().adapters().forDefinition().getProperties(definition)).thenReturn(set);
        when(context.execute(any())).thenReturn(res);
        when(context.getCommandFactory()).thenReturn(graphCommandFactory);

        Element node1 = nodeBuilder.build(context);
        Assert.assertEquals(node1,
                            node);
    }
}
