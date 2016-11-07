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
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.graph.content.relationship.Dock;
import org.kie.workbench.common.stunner.core.graph.impl.EdgeImpl;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.kie.workbench.common.stunner.core.util.UUID;
import org.uberfire.commons.validation.PortablePreconditions;

import java.util.Collection;
import java.util.LinkedList;

/**
 * Creates a dock connection from the child node to the target node.
 */
@Portable
public final class AddDockEdgeCommand extends AbstractGraphCommand {

    private String parentUUID;
    private String candidateUUID;

    public AddDockEdgeCommand( @MapsTo( "parentUUID" ) String parentUUID,
                               @MapsTo( "candidate" ) String candidateUUID ) {
        this.parentUUID = PortablePreconditions.checkNotNull( "parentUUID",
                parentUUID );
        this.candidateUUID = PortablePreconditions.checkNotNull( "candidate",
                candidateUUID );
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
            final Node<?, Edge> parent = checkNodeNotNull( context, parentUUID );
            final Node<?, Edge> candidate = checkNodeNotNull( context, candidateUUID );
            // TODO: Create a DockEdgeFactory iface extending EdgeFactory using as content generics type Relationship
            final String uuid = UUID.uuid();
            final Edge<Dock, Node> edge = new EdgeImpl<>( uuid );
            edge.setContent( new Dock() );
            edge.setSourceNode( parent );
            edge.setTargetNode( candidate );
            parent.getOutEdges().add( edge );
            candidate.getInEdges().add( edge );
            getMutableIndex( context ).addEdge( edge );

        }
        return results;
    }

    @SuppressWarnings( "unchecked" )
    protected CommandResult<RuleViolation> doCheck( final GraphCommandExecutionContext context ) {
        final Node<?, Edge> parent = checkNodeNotNull( context, parentUUID );
        final Node<Definition<?>, Edge> candidate = ( Node<Definition<?>, Edge> ) checkNodeNotNull( context, candidateUUID );
        final Collection<RuleViolation> dockingRuleViolations =
                ( Collection<RuleViolation> ) context.getRulesManager().docking().evaluate( parent, candidate ).violations();
        final Collection<RuleViolation> violations = new LinkedList<RuleViolation>();
        violations.addAll( dockingRuleViolations );
        return new GraphCommandResultBuilder( violations ).build();
    }

    @Override
    public CommandResult<RuleViolation> undo( final GraphCommandExecutionContext context ) {
        final DeleteDockEdgeCommand undoCommand = new DeleteDockEdgeCommand( parentUUID, candidateUUID );
        return undoCommand.execute( context );
    }

    @Override
    public String toString() {
        return "AddDockEdgeCommand [parent=" + parentUUID + ", candidate=" + candidateUUID + "]";
    }
}
