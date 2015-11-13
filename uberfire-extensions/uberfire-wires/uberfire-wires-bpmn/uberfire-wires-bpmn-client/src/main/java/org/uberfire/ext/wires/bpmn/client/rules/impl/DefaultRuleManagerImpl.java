/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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

import org.uberfire.commons.data.Pair;
import org.uberfire.commons.validation.PortablePreconditions;
import org.uberfire.ext.wires.bpmn.api.model.BpmnEdge;
import org.uberfire.ext.wires.bpmn.api.model.BpmnGraph;
import org.uberfire.ext.wires.bpmn.api.model.BpmnGraphNode;
import org.uberfire.ext.wires.bpmn.api.model.Role;
import org.uberfire.ext.wires.bpmn.api.model.rules.CardinalityRule;
import org.uberfire.ext.wires.bpmn.api.model.rules.ConnectionRule;
import org.uberfire.ext.wires.bpmn.api.model.rules.ContainmentRule;
import org.uberfire.ext.wires.bpmn.api.model.rules.Rule;
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
    public Results checkContainment( final BpmnGraph target,
                                     final BpmnGraphNode candidate ) {
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
    public Results checkCardinality( final BpmnGraph target,
                                     final BpmnGraphNode candidate,
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
                for ( BpmnGraphNode node : target ) {
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

    @Override
    public Results checkConnectionRules( final BpmnGraphNode outgoingNode,
                                         final BpmnGraphNode incomingNode,
                                         final BpmnEdge edge ) {
        final Results results = new DefaultResultsImpl();
        if ( connectionRules.isEmpty() ) {
            return results;
        }

        final Set<Pair<String, String>> couples = new HashSet<Pair<String, String>>();
        for ( ConnectionRule rule : connectionRules ) {
            if ( edge.getRole().equals( rule.getRole() ) ) {
                for ( ConnectionRule.PermittedConnection pc : rule.getPermittedConnections() ) {
                    couples.add( new Pair( pc.getStartRole().getName(),
                                           pc.getEndRole().getName() ) );
                    if ( outgoingNode.getContent().getRoles().contains( pc.getStartRole() ) ) {
                        if ( incomingNode.getContent().getRoles().contains( pc.getEndRole() ) ) {
                            return results;
                        }
                    }
                }
            }
        }

        results.addMessage( new DefaultResultImpl( ResultType.ERROR,
                                                   "Edge does not emanate from a GraphNode with a permitted Role nor terminate at GraphNode with a permitted Role. Permitted Connections are: " + couples.toString() ) );
        return results;
    }

    @Override
    public Results checkCardinality( final BpmnGraphNode outgoingNode,
                                     final BpmnGraphNode incomingNode,
                                     final BpmnEdge edge,
                                     final Operation operation ) {
        final Results results = new DefaultResultsImpl();
        if ( cardinalityRules.isEmpty() ) {
            return results;
        }

        for ( CardinalityRule rule : cardinalityRules ) {
            //Check outgoing connections
            if ( outgoingNode.getContent().getRoles().contains( rule.getRole() ) ) {
                for ( CardinalityRule.ConnectorRule cr : rule.getOutgoingConnectionRules() ) {
                    if ( cr.getRole().equals( edge.getRole() ) ) {
                        final long minOccurrences = cr.getMinOccurrences();
                        final long maxOccurrences = cr.getMaxOccurrences();
                        long count = ( operation == Operation.ADD ? 1 : -1 );
                        for ( BpmnEdge e : outgoingNode.getOutEdges() ) {
                            if ( e instanceof BpmnEdge ) {
                                final BpmnEdge be = (BpmnEdge) e;
                                if ( be.getRole().equals( edge.getRole() ) ) {
                                    count++;
                                }
                            }
                        }

                        if ( count < minOccurrences ) {
                            results.addMessage( new DefaultResultImpl( ResultType.ERROR,
                                                                       "'" + outgoingNode.getContent().getId() + "' needs a minimum '" + minOccurrences + "' of '" + cr.getRole() + "' edges. Found '" + count + "'." ) );
                        } else if ( count > maxOccurrences ) {
                            results.addMessage( new DefaultResultImpl( ResultType.ERROR,
                                                                       "'" + outgoingNode.getContent().getId() + "' can have a maximum  '" + maxOccurrences + "' of '" + cr.getRole() + "' edges. Found '" + count + "'." ) );
                        }
                    }
                }
            }

            //Check incoming connections
            if ( incomingNode.getContent().getRoles().contains( rule.getRole() ) ) {
                for ( CardinalityRule.ConnectorRule cr : rule.getIncomingConnectionRules() ) {
                    if ( cr.getRole().equals( edge.getRole() ) ) {
                        final long minOccurrences = cr.getMinOccurrences();
                        final long maxOccurrences = cr.getMaxOccurrences();
                        long count = ( operation == Operation.ADD ? 1 : -1 );
                        for ( BpmnEdge e : incomingNode.getInEdges() ) {
                            if ( e instanceof BpmnEdge ) {
                                final BpmnEdge be = (BpmnEdge) e;
                                if ( be.getRole().equals( edge.getRole() ) ) {
                                    count++;
                                }
                            }
                        }

                        if ( count < minOccurrences ) {
                            results.addMessage( new DefaultResultImpl( ResultType.ERROR,
                                                                       "'" + incomingNode.getContent().getId() + "' needs a minimum '" + minOccurrences + "' of '" + cr.getRole() + "' edges. Found '" + count + "'." ) );
                        } else if ( count > maxOccurrences ) {
                            results.addMessage( new DefaultResultImpl( ResultType.ERROR,
                                                                       "'" + incomingNode.getContent().getId() + "' can have a maximum  '" + maxOccurrences + "' of '" + cr.getRole() + "' edges. Found '" + count + "'." ) );
                        }
                    }
                }
            }
        }
        return results;
    }

}
