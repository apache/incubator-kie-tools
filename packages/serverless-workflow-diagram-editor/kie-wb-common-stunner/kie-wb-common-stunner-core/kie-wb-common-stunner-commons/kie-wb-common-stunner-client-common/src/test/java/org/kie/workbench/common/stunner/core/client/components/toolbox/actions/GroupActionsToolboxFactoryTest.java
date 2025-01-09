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


package org.kie.workbench.common.stunner.core.client.components.toolbox.actions;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.j2cl.tools.di.core.ManagedInstance;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.components.toolbox.Toolbox;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.lookup.domain.CommonDomainLookups;
import org.kie.workbench.common.stunner.core.profile.DomainProfileManager;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.uberfire.stubs.ManagedInstanceStub;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GroupActionsToolboxFactoryTest {

    private static final String DS_ID = "defSetId1";
    private static final String EDGE_ID = "edgeId1";
    private static final String NODE_ID = "nodeId1";
    private static final String NODE_ID2 = "nodeId2";
    private static final String E_UUID = "e1";

    @Mock
    private DefinitionUtils definitionUtils;

    @Mock
    private DomainProfileManager profileManager;

    @Mock
    private ToolboxDomainLookups toolboxLookups;

    @Mock
    private CommonDomainLookups domainLookups;

    @Mock
    private CreateConnectorToolboxAction createConnectorAction;
    private ManagedInstance<CreateConnectorToolboxAction> createConnectorActions;

    @Mock
    private CreateNodeToolboxAction createNodeAction;
    @Mock
    private CreateNodeToolboxAction createNodeAction2;
    private ManagedInstance<CreateNodeToolboxAction> createNodeActions;

    @Mock
    private ActionsToolboxView view;
    private ManagedInstance<ActionsToolboxView> views;

    @Mock
    private AbstractCanvasHandler canvasHandler;

    @Mock
    private Diagram diagram;

    @Mock
    private Metadata metadata;

    @Mock
    private Graph graph;

    @Mock
    private Node<View<Object>, Edge> element;

    private GroupActionsToolboxFactory tested;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() throws Exception {
        when(createConnectorAction.setEdgeId(EDGE_ID)).thenReturn(createConnectorAction);
        when(createNodeAction.setEdgeId(EDGE_ID)).thenReturn(createNodeAction);
        when(createNodeAction.setNodeId(NODE_ID)).thenReturn(createNodeAction);
        when(createNodeAction.setNodeId(NODE_ID2)).thenReturn(createNodeAction2);
        createConnectorActions = spy(new ManagedInstanceStub<>(createConnectorAction));
        createNodeActions = spy(new ManagedInstanceStub<>(createNodeAction, createNodeAction2));

        views = spy(new ManagedInstanceStub<>(view));
        when(canvasHandler.getDiagram()).thenReturn(diagram);
        when(diagram.getGraph()).thenReturn(graph);
        when(diagram.getMetadata()).thenReturn(metadata);
        when(metadata.getDefinitionSetId()).thenReturn(DS_ID);
        when(element.getUUID()).thenReturn(E_UUID);
        when(toolboxLookups.get(anyString())).thenReturn(domainLookups);
        when(domainLookups.lookupTargetConnectors(eq(element)))
                .thenReturn(Stream.of(EDGE_ID).collect(Collectors.toSet()));

        Set<String> targetRoles = new HashSet<>();
        targetRoles.add(NODE_ID);
        targetRoles.add(NODE_ID2);

        when(domainLookups.lookupTargetNodes(eq(graph),
                                             eq(element),
                                             eq(EDGE_ID),
                                             any(Predicate.class)))
                .thenReturn(targetRoles);

        this.tested = new GroupActionsToolboxFactory(definitionUtils,
                                                     toolboxLookups,
                                                     profileManager,
                                                     createConnectorActions,
                                                     createNodeActions,
                                                     views);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testBuildGroupedActionsToolbox() {
        Predicate<String> profileFilter = mock(Predicate.class);
        when(profileManager.isDefinitionIdAllowed(eq(metadata))).thenReturn(profileFilter);
        final Optional<Toolbox<?>> toolbox =
                tested.build(canvasHandler,
                             element);
        verify(profileManager, times(1)).isDefinitionIdAllowed(eq(metadata));
        verify(domainLookups, times(1)).lookupTargetNodes(eq(graph),
                                                          eq(element),
                                                          eq(EDGE_ID),
                                                          eq(profileFilter));
        assertTrue(toolbox.isPresent());
        assertTrue(toolbox.get() instanceof GroupedActionsToolbox);
        final GroupedActionsToolbox actionsToolbox = (GroupedActionsToolbox) toolbox.get();
        assertEquals(E_UUID,
                     actionsToolbox.getElementUUID());
        verify(createConnectorAction,
               times(1)).setEdgeId(eq(EDGE_ID));
        verify(createNodeAction,
               times(2)).setEdgeId(eq(EDGE_ID));
        verify(createNodeAction,
               times(1)).setNodeId(eq(NODE_ID));
        verify(createNodeAction,
               times(1)).setNodeId(eq(NODE_ID2));
        verify(view,
               times(1)).init(eq(actionsToolbox));

        // Total number of actions connectors + nodes
        assertEquals(3, actionsToolbox.size());

        // Number of connectors grouping actions
        assertEquals(1, actionsToolbox.getConnectorSize());

        // Number of actions grouped into the connector
        assertEquals(2, actionsToolbox.getNodeSize(EDGE_ID));

        // Check hierarchy
        assertEquals(EDGE_ID, actionsToolbox.getNodeActions().get(createNodeAction));
        assertEquals(EDGE_ID, actionsToolbox.getNodeActions().get(createNodeAction2));
    }

    @Test
    public void testDestroy() {
        tested.destroy();

        verify(createConnectorActions, times(1)).destroyAll();
        verify(createNodeActions, times(1)).destroyAll();
        verify(views, times(1)).destroyAll();
    }
}
