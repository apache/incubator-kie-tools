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

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

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
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.lookup.util.CommonLookups;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;

/**
 * A toolbox control provider implementation that provides buttons to create new elements
 * and update the graph structure.
 * <p/>
 * It provides buttons for:
 * - Creating a new connection from the source element.
 * It looks for the default connector type and creates a button for it.
 * - Creating new nodes after the source element.
 * As could be many target nodes that can be placed after the source one, as rules are evaluating and passing,
 * to avoid big amount of buttons on the toolbox, it just creates a button for each of
 * the base morph types that match all the given targets.
 */
@Dependent
public class FlowActionsToolboxControlProvider extends AbstractToolboxControlProvider {

    private static Logger LOGGER = Logger.getLogger( FlowActionsToolboxControlProvider.class.getName() );

    private final DefinitionUtils definitionUtils;
    private final CommonLookups commonLookups;
    private final ToolboxCommandFactory defaultToolboxCommandFactory;

    protected FlowActionsToolboxControlProvider() {
        this( null,
              null,
              null,
              null );
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
    public List<ToolboxCommand<AbstractCanvasHandler, ?>> getCommands( final AbstractCanvasHandler context,
                                                                       final Element item ) {
        try {
            final Node<Definition<Object>, Edge> node = ( Node<Definition<Object>, Edge> ) item;
            final Diagram diagram = context.getDiagram();
            final String defSetId = diagram.getMetadata().getDefinitionSetId();
            final List<ToolboxCommand<AbstractCanvasHandler, ?>> commands = new LinkedList<>();
            // Look for the default connector type and create a button for it.
            // TODO: Handle all response pages.
            final Set<String> allowedConnectorIds = commonLookups.getAllowedConnectors( context.getModelRulesManager(),
                                                                                        defSetId,
                                                                                        node,
                                                                                        0,
                                                                                        10 );
            if ( null != allowedConnectorIds && !allowedConnectorIds.isEmpty() ) {
                for ( final String allowedConnectorId : allowedConnectorIds ) {
                    final NewConnectorCommand<?> newConnectorCommand = defaultToolboxCommandFactory.newConnectorCommand();
                    newConnectorCommand.setEdgeIdentifier( allowedConnectorId );
                    commands.add( newConnectorCommand );
                }
            }
            // If default connector type is provided, new nodes can be created as well, so
            // look for the allowed nodes that can be placed as button but gropuing them by their
            // morph base type ( this avoid having large number of buttons on the toolbox ).
            final String defaultConnectorId = definitionUtils.getDefaultConnectorId( defSetId );
            if ( null != defaultConnectorId ) {
                // TODO: Handle all response pages.
                final Set<String> allowedMorphDefaultDefinitionIds = commonLookups.getAllowedMorphDefaultDefinitions( context.getModelRulesManager(),
                                                                                                                      defSetId,
                                                                                                                      diagram.getGraph(),
                                                                                                                      ( Node<? extends Definition<Object>, ? extends Edge> ) item,
                                                                                                                      defaultConnectorId,
                                                                                                                      0,
                                                                                                                      10 );
                if ( null != allowedMorphDefaultDefinitionIds && !allowedMorphDefaultDefinitionIds.isEmpty() ) {
                    for ( final String allowedDefId : allowedMorphDefaultDefinitionIds ) {
                        final NewNodeCommand newNodeCommand = defaultToolboxCommandFactory.newNodeCommand();
                        newNodeCommand.setDefinitionIdentifier( allowedDefId );
                        commands.add( newNodeCommand );
                    }
                }
            }
            return commands;
        } catch ( final Exception e ) {
            LOGGER.log( Level.FINEST,
                        "Discarded item [" + item.getUUID() + "] for flow action toolbox controls as it's not a node." );
        }
        return null;
    }

    private void log( final String message ) {
        if ( LogConfiguration.loggingIsEnabled() ) {
            LOGGER.log( Level.SEVERE,
                        "** FLOW-ACTIONS-TOOLBOX ** " + message );
        }
    }
}
