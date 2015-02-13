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
package org.uberfire.ext.wires.bpmn.client;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.uberfire.ext.wires.bpmn.api.model.Content;
import org.uberfire.ext.wires.bpmn.api.model.Role;
import org.uberfire.ext.wires.bpmn.api.model.impl.nodes.ProcessNode;
import org.uberfire.ext.wires.bpmn.api.model.impl.roles.DefaultRoleImpl;
import org.uberfire.ext.wires.bpmn.api.model.impl.rules.CardinalityRuleImpl;
import org.uberfire.ext.wires.bpmn.api.model.impl.rules.ConnectionRuleImpl;
import org.uberfire.ext.wires.bpmn.api.model.impl.rules.ContainmentRuleImpl;
import org.uberfire.ext.wires.bpmn.api.model.rules.ConnectionRule;
import org.uberfire.ext.wires.bpmn.api.model.rules.Rule;
import org.uberfire.ext.wires.bpmn.beliefs.graph.Graph;
import org.uberfire.ext.wires.bpmn.beliefs.graph.GraphNode;

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
                                            Collections.EMPTY_SET ) );
        rules.add( new CardinalityRuleImpl( "End Node Cardinality Rule",
                                            new DefaultRoleImpl( "sequence_end" ),
                                            0,
                                            1,
                                            Collections.EMPTY_SET,
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

                                           }} ) );
        return rules;
    }

    protected void assertProcessContainsNodes( final Graph<Content> graph,
                                               final GraphNode<Content>... nodes ) {
        final Set<GraphNode<Content>> nodesToExist = new HashSet<GraphNode<Content>>();
        for ( GraphNode<Content> node : nodes ) {
            nodesToExist.add( node );
        }
        for ( GraphNode<Content> gn : graph ) {
            for ( GraphNode<Content> node : nodes ) {
                if ( gn.equals( node ) ) {
                    nodesToExist.remove( node );
                }
            }
        }
        if ( !nodesToExist.isEmpty() ) {
            final StringBuffer sb = new StringBuffer( "Not all GraphNodes were present in Graph.\n" );
            for ( GraphNode<Content> node : nodesToExist ) {
                sb.append( "--> Not present: GraphNode [" + node.toString() + "].\n" );
            }
            fail( sb.toString() );
        }
    }

    protected void assertProcessNotContainsNodes( final Graph<Content> graph,
                                                  final GraphNode<Content>... nodes ) {
        final Set<GraphNode<Content>> nodesToNotExist = new HashSet<GraphNode<Content>>();
        for ( GraphNode<Content> gn : graph ) {
            for ( GraphNode<Content> node : nodes ) {
                if ( gn.equals( node ) ) {
                    nodesToNotExist.add( node );
                }
            }
        }
        if ( !nodesToNotExist.isEmpty() ) {
            final StringBuffer sb = new StringBuffer( "One or more GraphNodes were present in Graph.\n" );
            for ( GraphNode<Content> node : nodesToNotExist ) {
                sb.append( "--> Present: GraphNode [" + node.toString() + "].\n" );
            }
            fail( sb.toString() );
        }
    }

}
