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
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandResultBuilder;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.graph.content.relationship.Child;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.util.SafeDeleteNodeProcessor;
import org.kie.workbench.common.stunner.core.rule.RuleManager;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.uberfire.commons.validation.PortablePreconditions;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Deletes a node taking into account its ingoing / outgoing edges and safe remove all node's children as well, if any.
 */
@Portable
public final class SafeDeleteNodeCommand extends AbstractGraphCompositeCommand {

    private Graph target;
    private Node candidate;

    public SafeDeleteNodeCommand( @MapsTo( "target" ) Graph target,
                                  @MapsTo( "candidate" ) Node candidate ) {
        this.target = PortablePreconditions.checkNotNull( "target",
                target );
        this.candidate = PortablePreconditions.checkNotNull( "candidate",
                candidate );
    }

    @Override
    @SuppressWarnings( "unchecked" )
    protected void initialize( final GraphCommandExecutionContext context ) {
        final List<Command<GraphCommandExecutionContext, RuleViolation>> commands = new LinkedList<>();
        // Delete & set incoming & outgoing edges for the node being deleted.
        new SafeDeleteNodeProcessor( candidate ).run( new SafeDeleteNodeProcessor.DeleteNodeCallback() {

            @Override
            public void deleteChildNode( final Node<Definition<?>, Edge> node ) {
                commands.add( new SafeDeleteNodeCommand( target, node ) );
            }

            @Override
            public void deleteInViewEdge( final Edge<View<?>, Node> edge ) {
                commands.add( new SetConnectionTargetNodeCommand( null, edge, 0 ) );

            }

            @Override
            public void deleteInChildEdge( final Node parent,
                                           final Edge<Child, Node> edge ) {
                commands.add( new DeleteChildEdgeCommand( parent, candidate ) );
            }

            @Override
            public void deleteOutEdge( final Edge<? extends View<?>, Node> edge ) {
                commands.add( new SetConnectionSourceNodeCommand( null, edge, 0 ) );

            }

            @Override
            public void deleteNode( final Node<Definition<?>, Edge> node ) {
                commands.add( new DeleteNodeCommand( target, candidate ) );
            }

        } );
        // Add the commands above as composited.
        for ( Command<GraphCommandExecutionContext, RuleViolation> command : commands ) {
            this.addCommand( command );
        }

    }

    @Override
    protected CommandResult<RuleViolation> doAllow( GraphCommandExecutionContext context, Command<GraphCommandExecutionContext, RuleViolation> command ) {
        return check( context );
    }

    @SuppressWarnings( "unchecked" )
    protected CommandResult<RuleViolation> doCheck( final GraphCommandExecutionContext context ) {
        // Check node exist on the store.
        boolean isNodeInGraph = false;
        for ( Object node : target.nodes() ) {
            if ( node.equals( candidate ) ) {
                isNodeInGraph = true;
                break;
            }
        }
        final GraphCommandResultBuilder builder = new GraphCommandResultBuilder();
        if ( isNodeInGraph ) {
            final Collection<RuleViolation> cardinalityRuleViolations =
                    ( Collection<RuleViolation> ) context.getRulesManager()
                            .cardinality().evaluate( target, candidate, RuleManager.Operation.DELETE ).violations();
            builder.addViolations( cardinalityRuleViolations );
            for ( final RuleViolation violation : cardinalityRuleViolations ) {
                if ( builder.isError( violation ) ) {
                    return builder.build();
                }
            }
            // Check nodes has no children.
            final List<Edge> outEdges = candidate.getOutEdges();
            if ( null != outEdges && !outEdges.isEmpty() ) {
                for ( final Edge outEdge : outEdges ) {
                    if ( outEdge.getContent() instanceof Child ) {
                        builder.setType( CommandResult.Type.ERROR );
                        builder.setMessage( "Node contains children nodes. It cannot be removed using this command [DeleteNodeCommand]." );
                    }
                }
            }

        } else {
            builder.setType( CommandResult.Type.ERROR );
            builder.setMessage( "Node was not present in Graph and hence was not deleted" );

        }
        return builder.build();
    }

    @Override
    public String toString() {
        return "SafeDeleteNodeCommand [target=" + target.getUUID() + ", candidate=" + candidate.getUUID() + "]";
    }

}
