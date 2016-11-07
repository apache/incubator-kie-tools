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
package org.kie.workbench.common.stunner.core.graph.command.impl;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.command.exception.BadCommandArgumentsException;
import org.kie.workbench.common.stunner.core.command.impl.AbstractCompositeCommand;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandResultBuilder;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.rule.RuleManager;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.uberfire.commons.validation.PortablePreconditions;

import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A Command to delete a node from a graph.
 * Does not take care about node'edges neither children nodes.
 */
@Portable
public final class DeleteNodeCommand extends AbstractGraphCommand {

    private static Logger LOGGER = Logger.getLogger( DeleteNodeCommand.class.getName() );

    private final String uuid;
    private transient Node<?, Edge> removed;

    public DeleteNodeCommand( @MapsTo( "uuid" ) String uuid ) {
        this.uuid = PortablePreconditions.checkNotNull( "uuid",
                uuid );
        this.removed = null;
    }

    @Override
    public CommandResult<RuleViolation> allow( final GraphCommandExecutionContext context ) {
        return check( context );
    }

    @Override
    public CommandResult<RuleViolation> execute( final GraphCommandExecutionContext context ) {
        CommandResult<RuleViolation> results = check( context );
        if ( !results.getType().equals( CommandResult.Type.ERROR ) ) {
            LOGGER.log( Level.FINE, "Executing..." );
            final Graph<?, Node> graph = getGraph( context );
            final Node<?, Edge> candidate = getNode( context, uuid );
            this.removed = candidate;
            graph.removeNode( candidate.getUUID() );
            getMutableIndex( context ).removeNode( candidate );
            LOGGER.log( Level.FINE, "Node [" + uuid + " removed from strcture and index." );
        }
        return results;
    }

    @SuppressWarnings( "unchecked" )
    protected CommandResult<RuleViolation> doCheck( final GraphCommandExecutionContext context ) {
        // Check node exist on the index.
        checkNodeNotNull( context, uuid );
        // And check it really exist on the graph storage as well.
        final Graph<?, Node> graph = getGraph( context );
        final Node<View<?>, Edge> candidate = ( Node<View<?>, Edge> ) getNode( context, uuid );
        boolean isNodeInGraph = false;
        for ( Object node : graph.nodes() ) {
            if ( node.equals( candidate ) ) {
                isNodeInGraph = true;
                break;
            }
        }
        if ( isNodeInGraph ) {
            final GraphCommandResultBuilder builder = new GraphCommandResultBuilder();
            final Collection<RuleViolation> cardinalityRuleViolations =
                    ( Collection<RuleViolation> ) context.getRulesManager()
                            .cardinality()
                            .evaluate( graph,
                                    candidate,
                                    RuleManager.Operation.DELETE )
                            .violations();
            builder.addViolations( cardinalityRuleViolations );
            return builder.build();
        }

        throw new BadCommandArgumentsException( this, uuid, "No node found for UUID" );
    }

    @Override
    public CommandResult<RuleViolation> undo( GraphCommandExecutionContext context ) {
        final AddNodeCommand undoCommand = new AddNodeCommand( removed );
        return undoCommand.execute( context );
    }

    @Override
    public String toString() {
        return "DeleteNodeCommand [candidate=" + uuid + "]";
    }

}
