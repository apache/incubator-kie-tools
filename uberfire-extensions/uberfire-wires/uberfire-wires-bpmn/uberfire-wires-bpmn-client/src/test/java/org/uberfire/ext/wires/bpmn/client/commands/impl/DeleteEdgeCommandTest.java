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
package org.uberfire.ext.wires.bpmn.client.commands.impl;

import java.util.Collections;
import java.util.HashSet;

import org.junit.Before;
import org.junit.Test;
import org.uberfire.ext.wires.bpmn.api.model.Role;
import org.uberfire.ext.wires.bpmn.api.model.impl.edges.BpmnEdgeImpl;
import org.uberfire.ext.wires.bpmn.api.model.impl.nodes.EndProcessNode;
import org.uberfire.ext.wires.bpmn.api.model.impl.nodes.ProcessNode;
import org.uberfire.ext.wires.bpmn.api.model.impl.nodes.StartProcessNode;
import org.uberfire.ext.wires.bpmn.api.model.impl.roles.DefaultRoleImpl;
import org.uberfire.ext.wires.bpmn.api.model.impl.rules.CardinalityRuleImpl;
import org.uberfire.ext.wires.bpmn.api.model.BpmnEdge;
import org.uberfire.ext.wires.bpmn.api.model.rules.CardinalityRule;
import org.uberfire.ext.wires.bpmn.api.model.rules.Rule;
import org.uberfire.ext.wires.bpmn.client.AbstractBaseRuleTest;
import org.uberfire.ext.wires.bpmn.client.TestDummyNode;
import org.uberfire.ext.wires.bpmn.client.commands.CommandManager;
import org.uberfire.ext.wires.bpmn.client.commands.ResultType;
import org.uberfire.ext.wires.bpmn.client.commands.Results;
import org.uberfire.ext.wires.bpmn.client.rules.RuleManager;
import org.uberfire.ext.wires.bpmn.client.rules.impl.DefaultRuleManagerImpl;

import static junit.framework.Assert.*;

public class DeleteEdgeCommandTest extends AbstractBaseRuleTest {

    private ProcessNode process;
    private RuleManager ruleManager;
    private StartProcessNode node1;
    private TestDummyNode node2;
    private TestDummyNode node3;
    private EndProcessNode node4;
    private BpmnEdge e1;
    private BpmnEdge e2;
    private BpmnEdge e3;
    private BpmnEdge e4;
    private CommandManager commandManager;

    @Before
    public void setupNodes() {
        //Dummy process for each test consists of 3 connected nodes
        //---------------------------------------------------------
        //
        // [StartNode]--e1-->[DummyNode]--e2-->[DummyNode]--e3-->[EndNode]
        //                   [         ]--e4-->[         ]
        //
        process = new ProcessNode();
        ruleManager = new DefaultRuleManagerImpl();
        node1 = new StartProcessNode();
        node2 = new TestDummyNode();
        node3 = new TestDummyNode();
        node4 = new EndProcessNode();
        commandManager = new DefaultCommandManagerImpl();

        for ( Rule rule : getConnectionRules() ) {
            ruleManager.addRule( rule );
        }
        for ( Rule rule : getCardinalityRules() ) {
            ruleManager.addRule( rule );
        }

        //Add StartProcessNode
        final Results results1 = commandManager.execute( ruleManager,
                                                         new AddGraphNodeCommand( process,
                                                                                  node1 ) );

        assertNotNull( results1 );
        assertEquals( 0,
                      results1.getMessages().size() );

        //Add TestDummyNode
        final Results results2 = commandManager.execute( ruleManager,
                                                         new AddGraphNodeCommand( process,
                                                                                  node2 ) );

        assertNotNull( results2 );
        assertEquals( 0,
                      results2.getMessages().size() );

        final Results results3 = commandManager.execute( ruleManager,
                                                         new AddGraphNodeCommand( process,
                                                                                  node3 ) );

        assertNotNull( results3 );
        assertEquals( 0,
                      results3.getMessages().size() );

        //Add EndProcessNode
        final Results results4 = commandManager.execute( ruleManager,
                                                         new AddGraphNodeCommand( process,
                                                                                  node4 ) );

        assertNotNull( results4 );
        assertEquals( 0,
                      results4.getMessages().size() );

        //Add Edge between StartNode and DummyNode1
        e1 = new BpmnEdgeImpl( new DefaultRoleImpl( "general_edge" ) );
        final Results results5 = commandManager.execute( ruleManager,
                                                         new AddEdgeCommand( node1,
                                                                             node2,
                                                                             e1 ) );

        assertNotNull( results5 );
        assertEquals( 0,
                      results5.getMessages().size() );

        //Add Edge between DummyNode1 and DummyNode2
        e2 = new BpmnEdgeImpl( new DefaultRoleImpl( "general_edge" ) );
        final Results results6 = commandManager.execute( ruleManager,
                                                         new AddEdgeCommand( node2,
                                                                             node3,
                                                                             e2 ) );

        assertNotNull( results6 );
        assertEquals( 0,
                      results6.getMessages().size() );

        //Add Edge between DummyNode2 and EndNode
        e3 = new BpmnEdgeImpl( new DefaultRoleImpl( "general_edge" ) );
        final Results results7 = commandManager.execute( ruleManager,
                                                         new AddEdgeCommand( node3,
                                                                             node4,
                                                                             e3 ) );

        assertNotNull( results7 );
        assertEquals( 0,
                      results7.getMessages().size() );

        //Add another Edge between DummyNode1 and DummyNode2
        e4 = new BpmnEdgeImpl( new DefaultRoleImpl( "general_edge" ) );
        final Results results8 = commandManager.execute( ruleManager,
                                                         new AddEdgeCommand( node2,
                                                                             node3,
                                                                             e4 ) );

        assertNotNull( results8 );
        assertEquals( 0,
                      results8.getMessages().size() );
    }

    @Test
    public void testDeleteEdgeBetweenStartNodeAndDummyNode() {
        //Delete edge between StartNode and DummyNode
        final Results results1 = commandManager.execute( ruleManager,
                                                         new DeleteEdgeCommand( node1,
                                                                                node2,
                                                                                e1 ) );

        assertNotNull( results1 );
        assertEquals( 0,
                      results1.getMessages().size() );

        assertEquals( 0,
                      node1.getInEdges().size() );
        assertEquals( 0,
                      node1.getOutEdges().size() );
        assertEquals( 0,
                      node2.getInEdges().size() );
        assertEquals( 2,
                      node2.getOutEdges().size() );

        //Try to delete the same edge between StartNode and DummyNode again
        final Results results2 = commandManager.execute( ruleManager,
                                                         new DeleteEdgeCommand( node1,
                                                                                node2,
                                                                                e1 ) );

        assertNotNull( results2 );
        assertEquals( 1,
                      results2.getMessages().size() );
        assertEquals( 1,
                      results2.getMessages( ResultType.WARNING ).size() );

        assertEquals( 0,
                      node1.getInEdges().size() );
        assertEquals( 0,
                      node1.getOutEdges().size() );
        assertEquals( 0,
                      node2.getInEdges().size() );
        assertEquals( 2,
                      node2.getOutEdges().size() );
    }

    @Test
    public void testDeleteEdgeBetweenDummyNodeAndEndNode() {
        //Delete edge between DummyNode and EndNode
        final Results results1 = commandManager.execute( ruleManager,
                                                         new DeleteEdgeCommand( node3,
                                                                                node4,
                                                                                e3 ) );

        assertNotNull( results1 );
        assertEquals( 0,
                      results1.getMessages().size() );

        assertEquals( 2,
                      node3.getInEdges().size() );
        assertEquals( 0,
                      node3.getOutEdges().size() );
        assertEquals( 0,
                      node4.getInEdges().size() );
        assertEquals( 0,
                      node4.getOutEdges().size() );

        //Try to delete the same edge between DummyNode and EndNode again
        final Results results2 = commandManager.execute( ruleManager,
                                                         new DeleteEdgeCommand( node3,
                                                                                node4,
                                                                                e3 ) );

        assertNotNull( results2 );
        assertEquals( 1,
                      results2.getMessages().size() );
        assertEquals( 1,
                      results2.getMessages( ResultType.WARNING ).size() );

        assertEquals( 2,
                      node3.getInEdges().size() );
        assertEquals( 0,
                      node3.getOutEdges().size() );
        assertEquals( 0,
                      node4.getInEdges().size() );
        assertEquals( 0,
                      node4.getOutEdges().size() );
    }

    @Test
    public void testDeleteOutgoingCardinalityOutgoingMinimum() {
        ruleManager.addRule( new CardinalityRuleImpl( "DummyNode Outgoing Cardinality Rule",
                                                      new DefaultRoleImpl( "dummy" ),
                                                      0,
                                                      2,
                                                      new HashSet<CardinalityRule.ConnectorRule>() {{
                                                          add( new CardinalityRule.ConnectorRule() {
                                                              @Override
                                                              public long getMinOccurrences() {
                                                                  return 1;
                                                              }

                                                              @Override
                                                              public long getMaxOccurrences() {
                                                                  return 2;
                                                              }

                                                              @Override
                                                              public Role getRole() {
                                                                  return new DefaultRoleImpl( "general_edge" );
                                                              }

                                                              @Override
                                                              public String getName() {
                                                                  return "End Node DummyNode Connector Rule 1";
                                                              }
                                                          } );
                                                      }},
                                                      Collections.EMPTY_SET ) );

        //Delete edge between DummyNode1 and DummyNode2
        final Results results1 = commandManager.execute( ruleManager,
                                                         new DeleteEdgeCommand( node2,
                                                                                node3,
                                                                                e2 ) );

        assertNotNull( results1 );
        assertEquals( 0,
                      results1.getMessages().size() );

        assertEquals( 1,
                      node2.getInEdges().size() );
        assertEquals( 1,
                      node2.getOutEdges().size() );
        assertEquals( 1,
                      node3.getInEdges().size() );
        assertEquals( 1,
                      node3.getOutEdges().size() );

        //Try to delete the other edge between DummyNode1 and DummyNode2 again. This should fail validation.
        final Results results2 = commandManager.execute( ruleManager,
                                                         new DeleteEdgeCommand( node2,
                                                                                node3,
                                                                                e4 ) );

        assertNotNull( results2 );
        assertEquals( 1,
                      results2.getMessages().size() );
        assertEquals( 1,
                      results2.getMessages( ResultType.ERROR ).size() );

        assertEquals( 1,
                      node2.getInEdges().size() );
        assertEquals( 1,
                      node2.getOutEdges().size() );
        assertEquals( 1,
                      node3.getInEdges().size() );
        assertEquals( 1,
                      node3.getOutEdges().size() );
    }

    @Test
    public void testDeleteOutgoingCardinalityIncomingMinimum() {
        ruleManager.addRule( new CardinalityRuleImpl( "DummyNode Incoming Cardinality Rule",
                                                      new DefaultRoleImpl( "dummy" ),
                                                      0,
                                                      2,
                                                      Collections.EMPTY_SET,
                                                      new HashSet<CardinalityRule.ConnectorRule>() {{
                                                          add( new CardinalityRule.ConnectorRule() {
                                                              @Override
                                                              public long getMinOccurrences() {
                                                                  return 1;
                                                              }

                                                              @Override
                                                              public long getMaxOccurrences() {
                                                                  return 2;
                                                              }

                                                              @Override
                                                              public Role getRole() {
                                                                  return new DefaultRoleImpl( "general_edge" );
                                                              }

                                                              @Override
                                                              public String getName() {
                                                                  return "End Node DummyNode Connector Rule 1";
                                                              }
                                                          } );
                                                      }} ) );

        //Delete edge between DummyNode1 and DummyNode2
        final Results results1 = commandManager.execute( ruleManager,
                                                         new DeleteEdgeCommand( node2,
                                                                                node3,
                                                                                e2 ) );

        assertNotNull( results1 );
        assertEquals( 0,
                      results1.getMessages().size() );

        assertEquals( 1,
                      node2.getInEdges().size() );
        assertEquals( 1,
                      node2.getOutEdges().size() );
        assertEquals( 1,
                      node3.getInEdges().size() );
        assertEquals( 1,
                      node3.getOutEdges().size() );

        //Try to delete the other edge between DummyNode1 and DummyNode2 again. This should fail validation.
        final Results results2 = commandManager.execute( ruleManager,
                                                         new DeleteEdgeCommand( node2,
                                                                                node3,
                                                                                e4 ) );

        assertNotNull( results2 );
        assertEquals( 1,
                      results2.getMessages().size() );
        assertEquals( 1,
                      results2.getMessages( ResultType.ERROR ).size() );

        assertEquals( 1,
                      node2.getInEdges().size() );
        assertEquals( 1,
                      node2.getOutEdges().size() );
        assertEquals( 1,
                      node3.getInEdges().size() );
        assertEquals( 1,
                      node3.getOutEdges().size() );
    }

}
