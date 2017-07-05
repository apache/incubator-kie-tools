/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.workbench.common.stunner.core.client.canvas.controls.toolbox;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.toolbox.command.ToolboxCommand;
import org.kie.workbench.common.stunner.core.client.canvas.controls.toolbox.command.ToolboxCommandFactory;
import org.kie.workbench.common.stunner.core.client.canvas.controls.toolbox.command.builder.NewConnectorCommand;
import org.kie.workbench.common.stunner.core.client.canvas.controls.toolbox.command.builder.NewNodeCommand;
import org.kie.workbench.common.stunner.core.client.components.toolbox.ToolboxFactory;
import org.kie.workbench.common.stunner.core.client.components.toolbox.builder.ToolboxBuilder;
import org.kie.workbench.common.stunner.core.client.components.toolbox.builder.ToolboxButtonGridBuilder;
import org.kie.workbench.common.stunner.core.definition.adapter.AdapterManager;
import org.kie.workbench.common.stunner.core.definition.adapter.DefinitionAdapter;
import org.kie.workbench.common.stunner.core.definition.adapter.DefinitionSetAdapter;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.lookup.util.CommonLookups;
import org.kie.workbench.common.stunner.core.registry.definition.TypeDefinitionSetRegistry;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class FlowActionsToolboxControlProviderTest {

    @Mock
    private DefinitionUtils definitionUtils;

    @Mock
    private ToolboxFactory toolboxFactory;

    @Mock
    private ToolboxButtonGridBuilder toolboxButtonGridBuilder;

    @Mock
    private DefinitionManager definitionManager;

    @Mock
    private FactoryManager factoryManager;

    @Mock
    private ToolboxCommandFactory defaultToolboxCommandFactory;

    @Mock
    private CommonLookups commonLookups;

    @Mock
    private TypeDefinitionSetRegistry typeDefinitionSetRegistry;

    @Mock
    private AdapterManager adapterManager;

    @Mock
    private DefinitionSetAdapter definitionSetAdapter;

    @Mock
    private DefinitionAdapter definitionAdapter;

    private AbstractToolboxControlProvider provider;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() {
        when(definitionUtils.getDefinitionManager()).thenReturn(definitionManager);
        when(toolboxFactory.toolboxGridBuilder()).thenReturn(toolboxButtonGridBuilder);
        when(toolboxButtonGridBuilder.setRows(anyInt())).thenReturn(toolboxButtonGridBuilder);
        when(toolboxButtonGridBuilder.setColumns(anyInt())).thenReturn(toolboxButtonGridBuilder);
        when(toolboxButtonGridBuilder.setIconSize(anyInt())).thenReturn(toolboxButtonGridBuilder);
        when(toolboxButtonGridBuilder.setPadding(anyInt())).thenReturn(toolboxButtonGridBuilder);
        when(definitionManager.definitionSets()).thenReturn(typeDefinitionSetRegistry);
        when(typeDefinitionSetRegistry.getDefinitionSetByType(eq(MockDefinitionSet.class))).thenReturn(new MockDefinitionSet());
        when(definitionManager.adapters()).thenReturn(adapterManager);
        when(adapterManager.forDefinitionSet()).thenReturn(definitionSetAdapter);
        when(adapterManager.forDefinition()).thenReturn(definitionAdapter);
        when(definitionSetAdapter.getDefinitions(any(MockDefinitionSet.class))).thenReturn(new HashSet<String>() {{
            add(MockNode.class.getName());
            add(MockConnector.class.getName());
        }});
        when(definitionAdapter.getId(anyObject())).thenAnswer((o) -> o.getArguments()[0].getClass().getName());

        this.provider = new FlowActionsToolboxControlProvider(toolboxFactory,
                                                              definitionUtils,
                                                              defaultToolboxCommandFactory,
                                                              commonLookups);
    }

    @Test
    public void checkGridHasExpectedConfiguration() {
        final AbstractCanvasHandler context = mock(AbstractCanvasHandler.class);
        final Element item = mock(Element.class);

        provider.getGrid(context,
                         item);

        verify(toolboxButtonGridBuilder).setRows(5);
        verify(toolboxButtonGridBuilder).setColumns(2);
        verify(toolboxButtonGridBuilder).setIconSize(AbstractToolboxControlProvider.DEFAULT_ICON_SIZE);
        verify(toolboxButtonGridBuilder).setPadding(AbstractToolboxControlProvider.DEFAULT_PADDING);
        verify(toolboxButtonGridBuilder).build();
    }

    @Test
    public void checkGetOnIsNorthEast() {
        assertEquals(ToolboxBuilder.Direction.NORTH_EAST,
                     provider.getOn());
    }

    @Test
    public void checkGetTowardsIsSouthEast() {
        assertEquals(ToolboxBuilder.Direction.SOUTH_EAST,
                     provider.getTowards());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void checkGetCommandsForAvailableConnectors() {
        final AbstractCanvasHandler context = mock(AbstractCanvasHandler.class);
        setupDiagramMetaData(context);

        final Node node = mock(Node.class);
        final NewConnectorCommand newConnectorCommand = mock(NewConnectorCommand.class);

        when(commonLookups.getAllowedConnectors(eq(MockDefinitionSet.class.getName()),
                                                eq(node),
                                                anyInt(),
                                                anyInt())).thenReturn(Collections.singleton(MockConnector.class.getName()));
        when(defaultToolboxCommandFactory.newConnectorToolboxCommand()).thenReturn(newConnectorCommand);

        final List<ToolboxCommand<AbstractCanvasHandler, ?>> commands = provider.getCommands(context,
                                                                                             node);

        assertEquals(1,
                     commands.size());
        assertTrue(commands.contains(newConnectorCommand));
        verify(newConnectorCommand).setEdgeIdentifier(eq(MockConnector.class.getName()));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void checkGetCommandsForAvailableConnectorAndFlowNode() {
        final AbstractCanvasHandler context = mock(AbstractCanvasHandler.class);
        setupDiagramMetaData(context);

        final Node node = mock(Node.class);
        final NewConnectorCommand newConnectorCommand = mock(NewConnectorCommand.class);
        when(commonLookups.getAllowedConnectors(eq(MockDefinitionSet.class.getName()),
                                                eq(node),
                                                anyInt(),
                                                anyInt())).thenReturn(Collections.singleton(MockConnector.class.getName()));
        when(defaultToolboxCommandFactory.newConnectorToolboxCommand()).thenReturn(newConnectorCommand);

        when(definitionUtils.getDefaultConnectorId(eq(MockDefinitionSet.class.getName()))).thenReturn(MockConnector.class.getName());

        final NewNodeCommand newNodeCommand = mock(NewNodeCommand.class);
        when(commonLookups.getAllowedMorphDefaultDefinitions(eq(MockDefinitionSet.class.getName()),
                                                             any(Graph.class),
                                                             eq(node),
                                                             eq(MockConnector.class.getName()),
                                                             anyInt(),
                                                             anyInt())).thenReturn(Collections.singleton(MockNode.class.getName()));
        when(defaultToolboxCommandFactory.newNodeToolboxCommand()).thenReturn(newNodeCommand);

        final List<ToolboxCommand<AbstractCanvasHandler, ?>> commands = provider.getCommands(context,
                                                                                             node);

        assertEquals(2,
                     commands.size());
        assertTrue(commands.contains(newConnectorCommand));
        verify(newConnectorCommand).setEdgeIdentifier(eq(MockConnector.class.getName()));
        assertTrue(commands.contains(newNodeCommand));
        verify(newNodeCommand).setEdgeIdentifier(eq(MockConnector.class.getName()));
        verify(newNodeCommand).setDefinitionIdentifier(eq(MockNode.class.getName()));
    }

    private void setupDiagramMetaData(final AbstractCanvasHandler context) {
        final Diagram diagram = mock(Diagram.class);
        final Metadata metadata = mock(Metadata.class);
        when(context.getDiagram()).thenReturn(diagram);
        when(diagram.getMetadata()).thenReturn(metadata);
        when(metadata.getDefinitionSetId()).thenReturn(MockDefinitionSet.class.getName());
    }

    private static class MockDefinitionSet {

    }

    private static class MockNode {

    }

    private static class MockConnector {

    }
}
