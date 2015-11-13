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
package org.uberfire.ext.wires.bpmn.client;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.uberfire.ext.wires.bpmn.api.model.BpmnEdge;
import org.uberfire.ext.wires.bpmn.api.model.BpmnGraph;
import org.uberfire.ext.wires.bpmn.api.model.BpmnGraphNode;
import org.uberfire.ext.wires.bpmn.api.model.Role;
import org.uberfire.ext.wires.bpmn.api.model.impl.nodes.ProcessNode;
import org.uberfire.ext.wires.bpmn.api.model.impl.roles.DefaultRoleImpl;
import org.uberfire.ext.wires.bpmn.api.model.impl.rules.CardinalityRuleImpl;
import org.uberfire.ext.wires.bpmn.api.model.impl.rules.ConnectionRuleImpl;
import org.uberfire.ext.wires.bpmn.api.model.impl.rules.ContainmentRuleImpl;
import org.uberfire.ext.wires.bpmn.api.model.rules.CardinalityRule;
import org.uberfire.ext.wires.bpmn.api.model.rules.ConnectionRule;
import org.uberfire.ext.wires.bpmn.api.model.rules.Rule;

import static org.junit.Assert.*;

/**
 * Base for Rule related tests
 */
public abstract class AbstractBaseRuleTest {

    protected Set<Rule> getContainmentRules() {
        final Set<Rule> rules = new HashSet<Rule>();
        rules.add( new ContainmentRuleImpl( "Process Node Containment Rule",
                                            new ProcessNode().getContent().getId(),
                                            new HashSet<Role>() {{
                                                add( new DefaultRoleImpl( "all" ) );
                                            }} ) );
        return rules;
    }

    protected Set<Rule> getCardinalityRules() {
        final Set<Rule> rules = new HashSet<Rule>();
        rules.add( new CardinalityRuleImpl( "Start Node Cardinality Rule",
                                            new DefaultRoleImpl( "sequence_start" ),
                                            0,
                                            1,
                                            Collections.EMPTY_SET,
                                            new HashSet<CardinalityRule.ConnectorRule>() {{
                                                add( new CardinalityRule.ConnectorRule() {
                                                    @Override
                                                    public long getMinOccurrences() {
                                                        return 0;
                                                    }

                                                    @Override
                                                    public long getMaxOccurrences() {
                                                        return 1;
                                                    }

                                                    @Override
                                                    public Role getRole() {
                                                        return new DefaultRoleImpl( "general_edge" );
                                                    }

                                                    @Override
                                                    public String getName() {
                                                        return "Start Node Outgoing Connector Rule 1";
                                                    }
                                                } );
                                            }} ) );
        rules.add( new CardinalityRuleImpl( "End Node Cardinality Rule",
                                            new DefaultRoleImpl( "sequence_end" ),
                                            0,
                                            1,
                                            new HashSet<CardinalityRule.ConnectorRule>() {{
                                                add( new CardinalityRule.ConnectorRule() {
                                                    @Override
                                                    public long getMinOccurrences() {
                                                        return 0;
                                                    }

                                                    @Override
                                                    public long getMaxOccurrences() {
                                                        return 1;
                                                    }

                                                    @Override
                                                    public Role getRole() {
                                                        return new DefaultRoleImpl( "general_edge" );
                                                    }

                                                    @Override
                                                    public String getName() {
                                                        return "End Node Incoming Connector Rule 1";
                                                    }
                                                } );
                                            }},
                                            Collections.EMPTY_SET ) );
        return rules;
    }

    protected Set<Rule> getConnectionRules() {
        final Set<Rule> rules = new HashSet<Rule>();
        rules.add( new ConnectionRuleImpl( "StartNode to TestDummyNode Connector Rule",
                                           new DefaultRoleImpl( "general_edge" ),
                                           new HashSet<ConnectionRule.PermittedConnection>() {{
                                               add( new ConnectionRule.PermittedConnection() {
                                                   @Override
                                                   public Role getStartRole() {
                                                       return new DefaultRoleImpl( "sequence_start" );
                                                   }

                                                   @Override
                                                   public Role getEndRole() {
                                                       return new DefaultRoleImpl( "dummy" );
                                                   }
                                               } );
                                               add( new ConnectionRule.PermittedConnection() {
                                                   @Override
                                                   public Role getStartRole() {
                                                       return new DefaultRoleImpl( "dummy" );
                                                   }

                                                   @Override
                                                   public Role getEndRole() {
                                                       return new DefaultRoleImpl( "sequence_end" );
                                                   }
                                               } );
                                               add( new ConnectionRule.PermittedConnection() {
                                                   @Override
                                                   public Role getStartRole() {
                                                       return new DefaultRoleImpl( "dummy" );
                                                   }

                                                   @Override
                                                   public Role getEndRole() {
                                                       return new DefaultRoleImpl( "dummy" );
                                                   }
                                               } );

                                           }} ) );
        return rules;
    }

    protected void assertProcessContainsNodes( final BpmnGraph graph,
                                               final BpmnGraphNode... nodes ) {
        final Set<BpmnGraphNode> nodesToExist = new HashSet<BpmnGraphNode>();
        for ( BpmnGraphNode node : nodes ) {
            nodesToExist.add( node );
        }
        for ( BpmnGraphNode gn : graph ) {
            for ( BpmnGraphNode node : nodes ) {
                if ( gn.equals( node ) ) {
                    nodesToExist.remove( node );
                }
            }
        }
        if ( !nodesToExist.isEmpty() ) {
            final StringBuffer sb = new StringBuffer( "Not all GraphNodes were present in Graph.\n" );
            for ( BpmnGraphNode node : nodesToExist ) {
                sb.append( "--> Not present: GraphNode [" + node.toString() + "].\n" );
            }
            fail( sb.toString() );
        }
    }

    protected void assertProcessNotContainsNodes( final BpmnGraph graph,
                                                  final BpmnGraphNode... nodes ) {
        final Set<BpmnGraphNode> nodesToNotExist = new HashSet<BpmnGraphNode>();
        for ( BpmnGraphNode gn : graph ) {
            for ( BpmnGraphNode node : nodes ) {
                if ( gn.equals( node ) ) {
                    nodesToNotExist.add( node );
                }
            }
        }
        if ( !nodesToNotExist.isEmpty() ) {
            final StringBuffer sb = new StringBuffer( "One or more GraphNodes were present in Graph.\n" );
            for ( BpmnGraphNode node : nodesToNotExist ) {
                sb.append( "--> Present: GraphNode [" + node.toString() + "].\n" );
            }
            fail( sb.toString() );
        }
    }

    protected void assertNodeContainsOutgoingEdges( final BpmnGraphNode node,
                                                    final BpmnEdge... edges ) {
        final Set<BpmnEdge> edgesToExist = new HashSet<BpmnEdge>();
        for ( BpmnEdge edge : edges ) {
            edgesToExist.add( edge );
        }
        for ( BpmnEdge edge : node.getOutEdges() ) {
            for ( BpmnEdge be : edges ) {
                if ( be.equals( edge ) ) {
                    edgesToExist.remove( edge );
                }
            }
        }
        if ( !edgesToExist.isEmpty() ) {
            final StringBuffer sb = new StringBuffer( "Not all Edges were present in GraphNode Outgoing connections.\n" );
            for ( BpmnEdge edge : edgesToExist ) {
                sb.append( "--> Not present: Edge [" + edge.toString() + "].\n" );
            }
            fail( sb.toString() );
        }
    }

    protected void assertNodeContainsIncomingEdges( final BpmnGraphNode node,
                                                    final BpmnEdge... edges ) {
        final Set<BpmnEdge> edgesToExist = new HashSet<BpmnEdge>();
        for ( BpmnEdge edge : edges ) {
            edgesToExist.add( edge );
        }
        for ( BpmnEdge edge : node.getInEdges() ) {
            for ( BpmnEdge be : edges ) {
                if ( be.equals( edge ) ) {
                    edgesToExist.remove( edge );
                }
            }
        }
        if ( !edgesToExist.isEmpty() ) {
            final StringBuffer sb = new StringBuffer( "Not all Edges were present in GraphNode Incoming connections.\n" );
            for ( BpmnEdge edge : edgesToExist ) {
                sb.append( "--> Not present: Edge [" + edge.toString() + "].\n" );
            }
            fail( sb.toString() );
        }
    }

}
