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
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandResultBuilder;
import org.kie.workbench.common.stunner.core.graph.content.relationship.Child;
import org.kie.workbench.common.stunner.core.graph.content.relationship.Parent;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.uberfire.commons.validation.PortablePreconditions;

import java.util.List;

/**
 * Deletes a child edge ( the outgoing edge from the parent that targets the given candidate ).
 */
@Portable
public final class DeleteChildEdgeCommand extends AbstractGraphCommand {

    private Node parent;
    private Node candidate;

    public DeleteChildEdgeCommand( @MapsTo( "parent" ) Node parent,
                                   @MapsTo( "candidate" ) Node candidate ) {
        this.parent = PortablePreconditions.checkNotNull( "parent",
                parent );
        this.candidate = PortablePreconditions.checkNotNull( "candidate",
                candidate );
    }

    @Override
    public CommandResult<RuleViolation> allow( final GraphCommandExecutionContext context ) {
        return check( context );
    }

    @Override
    public CommandResult<RuleViolation> execute( final GraphCommandExecutionContext context ) {
        final CommandResult<RuleViolation> results = check( context );
        if ( !results.getType().equals( CommandResult.Type.ERROR ) ) {
            final Edge<Parent, Node> edge = getEdgeForTarget();
            if ( null != edge ) {
                edge.setSourceNode( null );
                edge.setTargetNode( null );
                parent.getOutEdges().remove( edge );
                candidate.getInEdges().remove( edge );
            }
        }
        return results;
    }

    @SuppressWarnings( "unchecked" )
    private Edge<Parent, Node> getEdgeForTarget() {
        final List<Edge<?, Node>> outEdges = parent.getOutEdges();
        if ( null != outEdges && !outEdges.isEmpty() ) {
            for ( Edge<?, Node> outEdge : outEdges ) {
                if ( outEdge.getContent() instanceof Child ) {
                    final Node targetNode = outEdge.getTargetNode();
                    if ( null != targetNode && targetNode.equals( candidate ) ) {
                        return ( Edge<Parent, Node> ) outEdge;
                    }
                }
            }
        }
        return null;
    }

    protected CommandResult<RuleViolation> doCheck( final GraphCommandExecutionContext context ) {
        return GraphCommandResultBuilder.RESULT_OK;
    }

    @Override
    public CommandResult<RuleViolation> undo( final GraphCommandExecutionContext context ) {
        final AddChildEdgeCommand undoCommand = new AddChildEdgeCommand( parent, candidate );
        return undoCommand.execute( context );
    }

    @Override
    public String toString() {
        return "DeleteChildEdgeCommand [parent=" + parent.getUUID() + ", candidate=" + candidate.getUUID() + "]";
    }
}
