/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.uberfire.ext.wires.bpmn.client.rules.impl;

import java.util.HashSet;
import java.util.Set;
import javax.enterprise.context.ApplicationScoped;

import org.uberfire.commons.validation.PortablePreconditions;
import org.uberfire.ext.wires.bpmn.api.model.Content;
import org.uberfire.ext.wires.bpmn.api.model.Role;
import org.uberfire.ext.wires.bpmn.api.model.rules.CardinalityRule;
import org.uberfire.ext.wires.bpmn.api.model.rules.ConnectionRule;
import org.uberfire.ext.wires.bpmn.api.model.rules.ContainmentRule;
import org.uberfire.ext.wires.bpmn.api.model.rules.Rule;
import org.uberfire.ext.wires.bpmn.beliefs.graph.Graph;
import org.uberfire.ext.wires.bpmn.beliefs.graph.GraphNode;
import org.uberfire.ext.wires.bpmn.client.commands.ResultType;
import org.uberfire.ext.wires.bpmn.client.commands.Results;
import org.uberfire.ext.wires.bpmn.client.commands.impl.DefaultResultImpl;
import org.uberfire.ext.wires.bpmn.client.commands.impl.DefaultResultsImpl;
import org.uberfire.ext.wires.bpmn.client.rules.RuleManager;

/**
 * Default implementation of Rule Manager
 */
@ApplicationScoped
public class DefaultRuleManagerImpl implements RuleManager {

    private final Set<ContainmentRule> containmentRules = new HashSet<ContainmentRule>();
    private final Set<CardinalityRule> cardinalityRules = new HashSet<CardinalityRule>();
    private final Set<ConnectionRule> connectionRules = new HashSet<ConnectionRule>();

    @Override
    public void addRule( final Rule rule ) {
        PortablePreconditions.checkNotNull( "rule",
                                            rule );
        // Filter Rules upon insertion as different types of validation use different rules
        // It's quicker to filter once here than every time the Rules are needed.
        if ( rule instanceof ContainmentRule ) {
            containmentRules.add( (ContainmentRule) rule );
        } else if ( rule instanceof CardinalityRule ) {
            cardinalityRules.add( (CardinalityRule) rule );
        } else if ( rule instanceof ConnectionRule ) {
            connectionRules.add( (ConnectionRule) rule );
        }
    }

    @Override
    public Results checkContainment( final Graph<Content> target,
                                     final GraphNode<Content> candidate ) {
        final Results results = new DefaultResultsImpl();
        if ( containmentRules.isEmpty() ) {
            return results;
        }

        for ( ContainmentRule rule : containmentRules ) {
            if ( rule.getId().equals( target.getContent().getId() ) ) {
                final Set<Role> permittedRoles = new HashSet( rule.getPermittedRoles() );
                permittedRoles.retainAll( candidate.getContent().getRoles() );
                if ( permittedRoles.size() > 0 ) {
                    return results;
                }
            }
        }
        results.addMessage( new DefaultResultImpl( ResultType.ERROR,
                                                   "'" + target.getContent().getId() + "' cannot contain '" + candidate.getContent().getId() + "'." ) );
        return results;
    }

    @Override
    public Results checkCardinality( final Graph<Content> target,
                                     final GraphNode<Content> candidate,
                                     final Operation operation ) {
        final Results results = new DefaultResultsImpl();
        if ( cardinalityRules.isEmpty() ) {
            return results;
        }

        for ( CardinalityRule rule : cardinalityRules ) {
            if ( candidate.getContent().getRoles().contains( rule.getRole() ) ) {
                final long minOccurrences = rule.getMinOccurrences();
                final long maxOccurrences = rule.getMaxOccurrences();
                long count = ( operation == Operation.ADD ? 1 : -1 );
                for ( GraphNode<Content> node : target ) {
                    if ( node.getContent().getId().equals( candidate.getContent().getId() ) ) {
                        count++;
                    }
                }
                if ( count < minOccurrences ) {
                    results.addMessage( new DefaultResultImpl( ResultType.ERROR,
                                                               "'" + target.getContent().getId() + "' needs a minimum '" + minOccurrences + "' of '" + candidate.getContent().getId() + "' nodes. Found '" + count + "'." ) );
                } else if ( count > maxOccurrences ) {
                    results.addMessage( new DefaultResultImpl( ResultType.ERROR,
                                                               "'" + target.getContent().getId() + "' can have a maximum  '" + maxOccurrences + "' of '" + candidate.getContent().getId() + "' nodes. Found '" + count + "'." ) );
                }
            }
        }
        return results;
    }
}
