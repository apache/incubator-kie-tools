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
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.uberfire.commons.validation.PortablePreconditions;

/**
 * A Command to add an edge into a graph
 */
@Portable
public final class AddEdgeCommand extends AbstractGraphCommand {

    private final String nodeUUID;
    private final Edge edge;

    public AddEdgeCommand( @MapsTo( "nodeUUID" ) String nodeUUID,
                           @MapsTo( "edge" ) Edge edge ) {
        this.nodeUUID = PortablePreconditions.checkNotNull( "nodeUUID",
                nodeUUID );
        this.edge = PortablePreconditions.checkNotNull( "edge",
                edge );
        ;
    }

    @Override
    public CommandResult<RuleViolation> allow( final GraphCommandExecutionContext context ) {
        return check( context );
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public CommandResult<RuleViolation> execute( final GraphCommandExecutionContext context ) {
        final CommandResult<RuleViolation> results = check( context );
        if ( !results.getType().equals( CommandResult.Type.ERROR ) ) {
            final Node<?, Edge> target = checkNodeNotNull( context, nodeUUID );
            target.getOutEdges().add( edge );
            getMutableIndex( context ).addEdge( edge );
        }
        return results;
    }

    protected CommandResult<RuleViolation> doCheck( final GraphCommandExecutionContext context ) {
        checkNodeNotNull( context, nodeUUID );
        return GraphCommandResultBuilder.SUCCESS;
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public CommandResult<RuleViolation> undo( GraphCommandExecutionContext context ) {
        final DeleteEdgeCommand undoCommand = new DeleteEdgeCommand( edge.getUUID() );
        return undoCommand.execute( context );
    }

    @Override
    public String toString() {
        return "AddEdgeCommand [target=" + nodeUUID + ", edge=" + edge.getUUID() + "]";
    }
}
