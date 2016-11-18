/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.core.client.canvas.controls.toolbox;

import com.google.gwt.logging.client.LogConfiguration;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.toolbox.command.ToolboxCommand;
import org.kie.workbench.common.stunner.core.client.canvas.controls.toolbox.command.ToolboxCommandFactory;
import org.kie.workbench.common.stunner.core.client.canvas.controls.toolbox.command.builder.NewConnectorCommand;
import org.kie.workbench.common.stunner.core.client.canvas.controls.toolbox.command.builder.NewNodeCommand;
import org.kie.workbench.common.stunner.core.client.components.toolbox.ToolboxButtonGrid;
import org.kie.workbench.common.stunner.core.client.components.toolbox.ToolboxFactory;
import org.kie.workbench.common.stunner.core.client.components.toolbox.builder.ToolboxBuilder;
import org.kie.workbench.common.stunner.core.client.components.toolbox.builder.ToolboxButtonGridBuilder;
import org.kie.workbench.common.stunner.core.definition.util.DefinitionUtils;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.lookup.util.CommonLookups;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

@Dependent
public class FlowActionsToolboxControlProvider extends AbstractToolboxControlProvider {

    private static Logger LOGGER = Logger.getLogger( FlowActionsToolboxControlProvider.class.getName() );

    private final DefinitionUtils definitionUtils;
    private final CommonLookups commonLookups;
    private final ToolboxCommandFactory defaultToolboxCommandFactory;

    protected FlowActionsToolboxControlProvider() {
        this( null, null, null, null );
    }

    @Inject
    public FlowActionsToolboxControlProvider( final ToolboxFactory toolboxFactory,
                                              final DefinitionUtils definitionUtils,
                                              final ToolboxCommandFactory defaultToolboxCommandFactory,
                                              final CommonLookups commonLookups ) {
        super( toolboxFactory );
        this.definitionUtils = definitionUtils;
        this.defaultToolboxCommandFactory = defaultToolboxCommandFactory;
        this.commonLookups = commonLookups;
    }

    @Override
    public boolean supports( final Object definition ) {
        return true;
    }

    @Override
    public ToolboxButtonGrid getGrid( final AbstractCanvasHandler context,
                                      final Element item ) {
        final ToolboxButtonGridBuilder buttonGridBuilder = toolboxFactory.toolboxGridBuilder();
        return buttonGridBuilder
                .setRows( 5 )
                .setColumns( 2 )
                .setIconSize( DEFAULT_ICON_SIZE )
                .setPadding( DEFAULT_PADDING )
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
    @SuppressWarnings( "unchecked" )
    public List<ToolboxCommand<?, ?>> getCommands( final AbstractCanvasHandler context,
                                                   final Element item ) {
        final Diagram diagram = context.getDiagram();
        final String defSetId = diagram.getMetadata().getDefinitionSetId();
        if ( item.getContent() instanceof Definition ) {
            final Definition definitionContent = ( Definition ) item.getContent();
            final List<ToolboxCommand<?, ?>> commands = new LinkedList<>();
            // TODO: Handle all response pages.
            final Set<String> allowedConnectorIds = commonLookups.getConnectionRulesAllowedEdges( defSetId, definitionContent.getDefinition(), 0, 10 );
            if ( null != allowedConnectorIds && !allowedConnectorIds.isEmpty() ) {
                for ( final String allowedConnectorId : allowedConnectorIds ) {
                    final NewConnectorCommand<?> newConnectorCommand = defaultToolboxCommandFactory.newConnectorCommand();
                    newConnectorCommand.setEdgeIdentifier( allowedConnectorId );
                    commands.add( newConnectorCommand );

                }

            }
            final String defaultConnectorId = definitionUtils.getDefaultConnectorId( defSetId );
            if ( null != defaultConnectorId ) {
                // TODO: Handle all response pages.
                final Set<String> allowedMorphDefaultDefinitionIds =
                        commonLookups.getAllowedMorphDefaultDefinitions(
                                defSetId,
                                diagram.getGraph(),
                                ( Node<? extends Definition<Object>, ? extends Edge> ) item,
                                defaultConnectorId,
                                0,
                                10
                        );
                if ( null != allowedMorphDefaultDefinitionIds && !allowedMorphDefaultDefinitionIds.isEmpty() ) {
                    for ( final String allowedDefId : allowedMorphDefaultDefinitionIds ) {
                        final NewNodeCommand newNodeCommand = defaultToolboxCommandFactory.newNodeCommand();
                        newNodeCommand.setDefinitionIdentifier( allowedDefId );
                        commands.add( newNodeCommand );

                    }

                }

            }
            return commands;

        }
        return null;

    }

    private void log( final String message ) {
        if ( LogConfiguration.loggingIsEnabled() ) {
            LOGGER.log( Level.SEVERE, "** FLOW-ACTIONS-TOOLBOX ** " + message );
        }
    }

}
