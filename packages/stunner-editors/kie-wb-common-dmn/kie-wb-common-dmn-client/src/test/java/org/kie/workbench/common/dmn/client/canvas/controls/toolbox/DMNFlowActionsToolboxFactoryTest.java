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

package org.kie.workbench.common.dmn.client.canvas.controls.toolbox;

import java.util.Collections;
import java.util.Iterator;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.components.toolbox.Toolbox;
import org.kie.workbench.common.stunner.core.client.components.toolbox.actions.ActionsToolbox;
import org.kie.workbench.common.stunner.core.client.components.toolbox.actions.ActionsToolboxView;
import org.kie.workbench.common.stunner.core.client.components.toolbox.actions.CreateConnectorToolboxAction;
import org.kie.workbench.common.stunner.core.client.components.toolbox.actions.CreateNodeToolboxAction;
import org.kie.workbench.common.stunner.core.client.components.toolbox.actions.ToolboxDomainLookups;
import org.kie.workbench.common.stunner.core.definition.adapter.AdapterManager;
import org.kie.workbench.common.stunner.core.definition.adapter.DefinitionAdapter;
import org.kie.workbench.common.stunner.core.definition.adapter.DefinitionId;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.lookup.domain.CommonDomainLookups;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.uberfire.stubs.ManagedInstanceStub;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class DMNFlowActionsToolboxFactoryTest {

    private static final String DS_ID = "defSetId1";
    private static final String EDGE_ID = "edgeDefId1";
    private static final String NODE_ID = "nodeDefId1";
    private static final String E_UUID = "e1";

    @Mock
    private DefinitionManager definitionManager;

    @Mock
    private AdapterManager adapters;

    @Mock
    private DefinitionAdapter definitionAdapter;

    @Mock
    private ToolboxDomainLookups toolboxLookups;

    @Mock
    private CommonDomainLookups domainLookups;

    @Mock
    private CreateConnectorToolboxAction createConnectorActionInstance;
    private ManagedInstanceStub<CreateConnectorToolboxAction> createConnectorAction;

    @Mock
    private CreateNodeToolboxAction createNodeActionInstance;
    private ManagedInstanceStub<CreateNodeToolboxAction> createNodeAction;

    @Mock
    private ActionsToolboxView viewInstance;
    private ManagedInstanceStub<ActionsToolboxView> view;

    @Mock
    private AbstractCanvasHandler canvasHandler;

    @Mock
    private Diagram diagram;

    @Mock
    private Metadata metadata;

    @Mock
    private Graph graph;

    @Mock
    private Object allowedNodeDefinition;

    @Mock
    private Node<View<Object>, Edge> element;

    private DMNFlowActionsToolboxFactory tested;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() throws Exception {
        when(definitionManager.adapters()).thenReturn(adapters);
        when(adapters.forDefinition()).thenReturn(definitionAdapter);
        when(definitionAdapter.getId(eq(allowedNodeDefinition))).thenReturn(DefinitionId.build(NODE_ID));
        createConnectorAction = new ManagedInstanceStub<>(createConnectorActionInstance);
        when(createConnectorActionInstance.setEdgeId(Mockito.<String>any())).thenReturn(createConnectorActionInstance);
        createNodeAction = new ManagedInstanceStub<>(createNodeActionInstance);
        when(createNodeActionInstance.setEdgeId(Mockito.<String>any())).thenReturn(createNodeActionInstance);
        when(createNodeActionInstance.setNodeId(Mockito.<String>any())).thenReturn(createNodeActionInstance);
        when(canvasHandler.getDiagram()).thenReturn(diagram);
        when(diagram.getGraph()).thenReturn(graph);
        when(diagram.getMetadata()).thenReturn(metadata);
        when(metadata.getDefinitionSetId()).thenReturn(DS_ID);
        when(element.getUUID()).thenReturn(E_UUID);
        when(element.asNode()).thenReturn(element);
        when(toolboxLookups.get(Mockito.<String>any())).thenReturn(domainLookups);
        when(domainLookups.lookupTargetConnectors(eq(element)))
                .thenReturn(Collections.singleton(EDGE_ID));
        when(domainLookups.lookupTargetNodes(eq(graph),
                                             eq(element),
                                             eq(EDGE_ID)))
                .thenReturn(Collections.singleton(NODE_ID));
        view = new ManagedInstanceStub<>(viewInstance);
        this.tested = new DMNFlowActionsToolboxFactory(toolboxLookups,
                                                       createConnectorAction,
                                                       createNodeAction,
                                                       view);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testBuildToolbox() {
        final Optional<Toolbox<?>> toolbox =
                tested.build(canvasHandler,
                             element);
        assertTrue(toolbox.isPresent());
        assertTrue(toolbox.get() instanceof ActionsToolbox);
        final ActionsToolbox actionsToolbox = (ActionsToolbox) toolbox.get();
        assertEquals(E_UUID,
                     actionsToolbox.getElementUUID());
        assertEquals(2,
                     actionsToolbox.size());
        final Iterator actionIt = actionsToolbox.iterator();
        assertEquals(createConnectorActionInstance,
                     actionIt.next());
        assertEquals(createNodeActionInstance,
                     actionIt.next());
        verify(createConnectorActionInstance,
               times(1)).setEdgeId(eq(EDGE_ID));
        verify(createNodeActionInstance,
               times(1)).setEdgeId(eq(EDGE_ID));
        verify(createNodeActionInstance,
               times(1)).setNodeId(eq(NODE_ID));
        verify(viewInstance,
               times(1)).init(eq(actionsToolbox));
    }
}
