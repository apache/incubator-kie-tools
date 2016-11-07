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
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandResultBuilder;
import org.kie.workbench.common.stunner.core.rule.RuleManager;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.uberfire.commons.validation.PortablePreconditions;

import java.util.Collection;
import java.util.LinkedList;

/**
 * A Command to add a node into a graph
 */
@Portable
public final class AddNodeCommand extends AbstractGraphCommand {

    private final Node candidate;

    public AddNodeCommand( @MapsTo( "candidate" ) Node candidate ) {
        this.candidate = PortablePreconditions.checkNotNull( "candidate",
                candidate );
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
            final Graph<?, Node> graph = getGraph( context );
            graph.addNode( candidate );
            getMutableIndex( context ).addNode( candidate );
        }
        return results;
    }

    @SuppressWarnings( "unchecked" )
    protected CommandResult<RuleViolation> doCheck( final GraphCommandExecutionContext context ) {
        final Graph<?, Node> graph = getGraph( context );
        final Collection<RuleViolation> containmentRuleViolations =
                ( Collection<RuleViolation> ) context.getRulesManager().containment().evaluate( graph, candidate ).violations();
        final Collection<RuleViolation> cardinalityRuleViolations =
                ( Collection<RuleViolation> ) context.getRulesManager().cardinality().evaluate( graph, candidate, RuleManager.Operation.ADD ).violations();
        final Collection<RuleViolation> violations = new LinkedList<RuleViolation>();
        violations.addAll( containmentRuleViolations );
        violations.addAll( cardinalityRuleViolations );
        return new GraphCommandResultBuilder( violations ).build();
    }

    @Override
    public CommandResult<RuleViolation> undo( GraphCommandExecutionContext context ) {
        final SafeDeleteNodeCommand undoCommand = new SafeDeleteNodeCommand( candidate.getUUID() );
        return undoCommand.execute( context );
    }

    @Override
    public String toString() {
        return "AddNodeCommand [candidate=" + candidate.getUUID() + "]";
    }
}
