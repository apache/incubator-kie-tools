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

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.kie.workbench.common.dmn.api.DMNDefinitionSet;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.toolbox.AbstractToolboxControlProvider;
import org.kie.workbench.common.stunner.core.client.canvas.controls.toolbox.command.ToolboxCommand;
import org.kie.workbench.common.stunner.core.client.canvas.controls.toolbox.command.ToolboxCommandFactory;
import org.kie.workbench.common.stunner.core.client.canvas.controls.toolbox.command.builder.NewConnectorCommand;
import org.kie.workbench.common.stunner.core.client.canvas.controls.toolbox.command.builder.NewNodeCommand;
import org.kie.workbench.common.stunner.core.client.components.toolbox.ToolboxButtonGrid;
import org.kie.workbench.common.stunner.core.client.components.toolbox.ToolboxFactory;
import org.kie.workbench.common.stunner.core.client.components.toolbox.builder.ToolboxBuilder;
import org.kie.workbench.common.stunner.core.client.components.toolbox.builder.ToolboxButtonGridBuilder;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.lookup.util.CommonLookups;

/**
 * A DMN-specific implementation of {@see FlowActionsToolboxControlProvider}
 * that provides specific behaviour for DMN DefinitionSets.
 */
@Dependent
public class DMNFlowActionsToolboxControlProvider extends AbstractToolboxControlProvider {

    private static Logger LOGGER = Logger.getLogger(DMNFlowActionsToolboxControlProvider.class.getName());

    private final DefinitionManager definitionManager;
    private final ToolboxCommandFactory defaultToolboxCommandFactory;
    private final CommonLookups commonLookups;

    private final Set<String> dmnDefinitionIds = new HashSet<>();

    protected DMNFlowActionsToolboxControlProvider() {
        this(null,
             null,
             null,
             null);
    }

    @Inject
    public DMNFlowActionsToolboxControlProvider(final ToolboxFactory toolboxFactory,
                                                final DefinitionManager definitionManager,
                                                final ToolboxCommandFactory defaultToolboxCommandFactory,
                                                final CommonLookups commonLookups) {
        super(toolboxFactory);
        this.definitionManager = definitionManager;
        this.defaultToolboxCommandFactory = defaultToolboxCommandFactory;
        this.commonLookups = commonLookups;

        final DMNDefinitionSet definitionSet = (DMNDefinitionSet) definitionManager.definitionSets().getDefinitionSetByType(DMNDefinitionSet.class);
        this.dmnDefinitionIds.addAll(definitionManager.adapters().forDefinitionSet().getDefinitions(definitionSet));
    }

    @Override
    public boolean supports(final Object definition) {
        final String definitionId = definitionManager.adapters().forDefinition().getId(definition);
        return dmnDefinitionIds.contains(definitionId);
    }

    @Override
    public ToolboxButtonGrid getGrid(final AbstractCanvasHandler context,
                                     final Element item) {
        final ToolboxButtonGridBuilder buttonGridBuilder = toolboxFactory.toolboxGridBuilder();
        return buttonGridBuilder
                .setRows(5)
                .setColumns(2)
                .setIconSize(DEFAULT_ICON_SIZE)
                .setPadding(DEFAULT_PADDING)
                .build();
    }

    @Override
    public ToolboxBuilder.Direction getOn() {
        return ToolboxBuilder.Direction.NORTH_EAST;
    }

    @Override
    public ToolboxBuilder.Direction getTowards() {
        return ToolboxBuilder.Direction.SOUTH_EAST;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<ToolboxCommand<AbstractCanvasHandler, ?>> getCommands(final AbstractCanvasHandler context,
                                                                      final Element item) {
        try {
            final Node<Definition<Object>, Edge> node = (Node<Definition<Object>, Edge>) item;
            final Diagram diagram = context.getDiagram();
            final String defSetId = diagram.getMetadata().getDefinitionSetId();
            final List<ToolboxCommand<AbstractCanvasHandler, ?>> commands = new LinkedList<>();

            // Look for the default connector type and create a button for it.
            final Set<String> allowedConnectorIds = commonLookups.getAllowedConnectors(defSetId,
                                                                                       node,
                                                                                       0,
                                                                                       10);
            if (null != allowedConnectorIds && !allowedConnectorIds.isEmpty()) {
                for (final String allowedConnectorId : allowedConnectorIds) {
                    final NewConnectorCommand<?> newConnectorCommand = defaultToolboxCommandFactory.newConnectorToolboxCommand();
                    newConnectorCommand.setEdgeIdentifier(allowedConnectorId);
                    commands.add(newConnectorCommand);

                    final Set<Object> allowedTargetDefinitions = commonLookups.getAllowedTargetDefinitions(defSetId,
                                                                                                           diagram.getGraph(),
                                                                                                           (Node<? extends Definition<Object>, ? extends Edge>) item,
                                                                                                           allowedConnectorId,
                                                                                                           0,
                                                                                                           10);
                    if (null != allowedTargetDefinitions && !allowedTargetDefinitions.isEmpty()) {
                        for (final Object allowedTargetDefinition : allowedTargetDefinitions) {
                            final String allowedTargetDefinitionId = definitionManager.adapters().forDefinition().getId(allowedTargetDefinition);
                            final NewNodeCommand newNodeCommand = defaultToolboxCommandFactory.newNodeToolboxCommand();
                            newNodeCommand.setEdgeIdentifier(allowedConnectorId);
                            newNodeCommand.setDefinitionIdentifier(allowedTargetDefinitionId);
                            commands.add(newNodeCommand);
                        }
                    }
                }
            }

            return commands;
        } catch (final Exception e) {
            LOGGER.log(Level.FINEST,
                       "Discarded item [" + item.getUUID() + "] for flow action toolbox controls as it's not a node.");
        }
        return null;
    }
}
