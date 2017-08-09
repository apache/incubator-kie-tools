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
package org.kie.workbench.common.dmn.client.canvas.controls.toolbox;

import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.DMNDefinitionSet;
import org.kie.workbench.common.dmn.api.definition.v1_1.Association;
import org.kie.workbench.common.dmn.api.definition.v1_1.Decision;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.toolbox.AbstractToolboxControlProvider;
import org.kie.workbench.common.stunner.core.client.canvas.controls.toolbox.command.ToolboxCommand;
import org.kie.workbench.common.stunner.core.client.canvas.controls.toolbox.command.builder.NewConnectorCommand;
import org.kie.workbench.common.stunner.core.client.canvas.controls.toolbox.command.builder.NewNodeCommand;
import org.kie.workbench.common.stunner.core.client.components.toolbox.ToolboxButtonGrid;
import org.kie.workbench.common.stunner.core.client.components.toolbox.builder.ToolboxBuilder;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DMNFlowActionsToolboxControlProviderTest extends BaseDMNToolboxControlProviderTest {

    @Override
    protected AbstractToolboxControlProvider getProvider() {
        return new DMNFlowActionsToolboxControlProvider(toolboxFactory,
                                                        toolboxCommandFactory,
                                                        definitionManager,
                                                        commonLookups);
    }

    @Override
    protected void doAssertion(final boolean supports) {
        assertTrue(supports);
    }

    @Test
    public void checkGridHasExpectedConfiguration() {
        final AbstractCanvasHandler context = mock(AbstractCanvasHandler.class);
        final Element item = mock(Element.class);

        final ToolboxButtonGrid grid = provider.getGrid(context,
                                                        item);
        assertNotNull(grid);
        assertEquals(5,
                     grid.getRows());
        assertEquals(2,
                     grid.getColumns());
        assertEquals(AbstractToolboxControlProvider.DEFAULT_ICON_SIZE,
                     grid.getButtonSize());
        assertEquals(AbstractToolboxControlProvider.DEFAULT_PADDING,
                     grid.getPadding());
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
        when(commonLookups.getAllowedConnectors(eq(DMNDefinitionSet.class.getName()),
                                                eq(node),
                                                anyInt(),
                                                anyInt())).thenReturn(Collections.singleton(Association.class.getName()));
        when(toolboxCommandFactory.newConnectorToolboxCommand()).thenReturn(newConnectorCommand);

        final List<ToolboxCommand<AbstractCanvasHandler, ?>> commands = provider.getCommands(context,
                                                                                             node);

        assertEquals(1,
                     commands.size());
        assertTrue(commands.contains(newConnectorCommand));
        verify(newConnectorCommand).setEdgeIdentifier(eq(Association.class.getName()));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void checkGetCommandsForAvailableConnectorAndFlowNode() {
        final AbstractCanvasHandler context = mock(AbstractCanvasHandler.class);
        setupDiagramMetaData(context);

        final Node node = mock(Node.class);
        final NewConnectorCommand newConnectorCommand = mock(NewConnectorCommand.class);
        when(commonLookups.getAllowedConnectors(eq(DMNDefinitionSet.class.getName()),
                                                eq(node),
                                                anyInt(),
                                                anyInt())).thenReturn(Collections.singleton(Association.class.getName()));
        when(toolboxCommandFactory.newConnectorToolboxCommand()).thenReturn(newConnectorCommand);

        final NewNodeCommand newNodeCommand = mock(NewNodeCommand.class);
        when(commonLookups.getAllowedTargetDefinitions(eq(DMNDefinitionSet.class.getName()),
                                                       any(Graph.class),
                                                       eq(node),
                                                       eq(Association.class.getName()),
                                                       anyInt(),
                                                       anyInt())).thenReturn(Collections.singleton(new Decision.DecisionBuilder().build()));
        when(toolboxCommandFactory.newNodeToolboxCommand()).thenReturn(newNodeCommand);

        final List<ToolboxCommand<AbstractCanvasHandler, ?>> commands = provider.getCommands(context,
                                                                                             node);

        assertEquals(2,
                     commands.size());
        assertTrue(commands.contains(newConnectorCommand));
        verify(newConnectorCommand).setEdgeIdentifier(eq(Association.class.getName()));
        assertTrue(commands.contains(newNodeCommand));
        verify(newNodeCommand).setEdgeIdentifier(eq(Association.class.getName()));
        verify(newNodeCommand).setDefinitionIdentifier(eq(Decision.class.getName()));
    }

    private void setupDiagramMetaData(final AbstractCanvasHandler context) {
        final Diagram diagram = mock(Diagram.class);
        final Metadata metadata = mock(Metadata.class);
        when(context.getDiagram()).thenReturn(diagram);
        when(diagram.getMetadata()).thenReturn(metadata);
        when(metadata.getDefinitionSetId()).thenReturn(DMNDefinitionSet.class.getName());
    }
}
