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

import org.junit.Before;
import org.junit.Test;
import org.uberfire.ext.wires.bpmn.api.model.impl.edges.BpmnEdgeImpl;
import org.uberfire.ext.wires.bpmn.api.model.impl.nodes.EndProcessNode;
import org.uberfire.ext.wires.bpmn.api.model.impl.nodes.ProcessNode;
import org.uberfire.ext.wires.bpmn.api.model.impl.nodes.StartProcessNode;
import org.uberfire.ext.wires.bpmn.api.model.impl.roles.DefaultRoleImpl;
import org.uberfire.ext.wires.bpmn.api.model.BpmnEdge;
import org.uberfire.ext.wires.bpmn.api.model.rules.Rule;
import org.uberfire.ext.wires.bpmn.client.AbstractBaseRuleTest;
import org.uberfire.ext.wires.bpmn.client.TestDummyNode;
import org.uberfire.ext.wires.bpmn.client.commands.CommandManager;
import org.uberfire.ext.wires.bpmn.client.commands.ResultType;
import org.uberfire.ext.wires.bpmn.client.commands.Results;
import org.uberfire.ext.wires.bpmn.client.rules.RuleManager;
import org.uberfire.ext.wires.bpmn.client.rules.impl.DefaultRuleManagerImpl;

import static junit.framework.Assert.*;

public class BatchCommandTest extends AbstractBaseRuleTest {

    private ProcessNode process;
    private RuleManager ruleManager;
    private StartProcessNode node1;
    private TestDummyNode node2;
    private EndProcessNode node3;
    private BpmnEdge e1;
    private BpmnEdge e2;
    private CommandManager commandManager;

    @Before
    public void setupNodes() {
        //Dummy process for each test consists of 3 connected nodes
        //---------------------------------------------------------
        //
        // [StartNode]--e1-->[DummyNode]--e2-->[EndNode]
        //
        process = new ProcessNode();
        ruleManager = new DefaultRuleManagerImpl();
        node1 = new StartProcessNode();
        node2 = new TestDummyNode();
        node3 = new EndProcessNode();
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

        //Add EndProcessNode
        final Results results3 = commandManager.execute( ruleManager,
                                                         new AddGraphNodeCommand( process,
                                                                                  node3 ) );

        assertNotNull( results3 );
        assertEquals( 0,
                      results3.getMessages().size() );

        //Add Edge between StartNode and DummyNode1
        e1 = new BpmnEdgeImpl( new DefaultRoleImpl( "general_edge" ) );
        final Results results4 = commandManager.execute( ruleManager,
                                                         new AddEdgeCommand( node1,
                                                                             node2,
                                                                             e1 ) );

        assertNotNull( results4 );
        assertEquals( 0,
                      results4.getMessages().size() );

        //Add Edge between DummyNode2 and EndNode
        e2 = new BpmnEdgeImpl( new DefaultRoleImpl( "general_edge" ) );
        final Results results5 = commandManager.execute( ruleManager,
                                                         new AddEdgeCommand( node2,
                                                                             node3,
                                                                             e2 ) );

        assertNotNull( results5 );
        assertEquals( 0,
                      results5.getMessages().size() );
    }

    @Test
    public void testBatchAddValidState() {
        // Add two more TestDummyNodes and related Edges to the existing graph. This is valid.
        final TestDummyNode testNode1 = new TestDummyNode();
        final TestDummyNode testNode2 = new TestDummyNode();
        final BpmnEdge testEdge1 = new BpmnEdgeImpl( new DefaultRoleImpl( "general_edge" ) );
        final BpmnEdge testEdge2 = new BpmnEdgeImpl( new DefaultRoleImpl( "general_edge" ) );
        final AddGraphNodeCommand testCmd1 = new AddGraphNodeCommand( process,
                                                                      testNode1 );
        final AddGraphNodeCommand testCmd2 = new AddGraphNodeCommand( process,
                                                                      testNode2 );
        final AddEdgeCommand testCmd3 = new AddEdgeCommand( node2,
                                                            testNode1,
                                                            testEdge1 );
        final AddEdgeCommand testCmd4 = new AddEdgeCommand( testNode1,
                                                            testNode2,
                                                            testEdge2 );
        final Results results1 = commandManager.execute( ruleManager,
                                                         new BatchCommand( testCmd1,
                                                                           testCmd2,
                                                                           testCmd3,
                                                                           testCmd4 ) );

        assertNotNull( results1 );
        assertEquals( 0,
                      results1.getMessages().size() );

        assertEquals( 5,
                      process.size() );
        assertProcessContainsNodes( process,
                                    testNode1,
                                    testNode2 );
        assertEquals( 2,
                      node2.getOutEdges().size() );
        assertNodeContainsOutgoingEdges( node2,
                                         e2,
                                         testEdge1 );

        assertEquals( 1,
                      testNode1.getInEdges().size() );
        assertNodeContainsIncomingEdges( testNode1,
                                         testEdge1 );
        assertEquals( 1,
                      testNode1.getOutEdges().size() );
        assertNodeContainsOutgoingEdges( testNode1,
                                         testEdge2 );

        assertEquals( 1,
                      testNode2.getInEdges().size() );
        assertNodeContainsIncomingEdges( testNode2,
                                         testEdge2 );
    }

    @Test
    public void testBatchAddInvalidState1() {
        // Add one more TestDummyNode and another EndProcessNode plus related Edges to the existing graph.
        // This is invalid as a Process can only contain one EndProcessNode and hence the batch should fail.
        final TestDummyNode testNode1 = new TestDummyNode();
        final EndProcessNode testNode2 = new EndProcessNode();
        final BpmnEdge testEdge1 = new BpmnEdgeImpl( new DefaultRoleImpl( "general_edge" ) );
        final BpmnEdge testEdge2 = new BpmnEdgeImpl( new DefaultRoleImpl( "general_edge" ) );
        final AddGraphNodeCommand testCmd1 = new AddGraphNodeCommand( process,
                                                                      testNode1 );
        final AddGraphNodeCommand testCmd2 = new AddGraphNodeCommand( process,
                                                                      testNode2 );
        final AddEdgeCommand testCmd3 = new AddEdgeCommand( node2,
                                                            testNode1,
                                                            testEdge1 );
        final AddEdgeCommand testCmd4 = new AddEdgeCommand( testNode1,
                                                            testNode2,
                                                            testEdge2 );
        final Results results1 = commandManager.execute( ruleManager,
                                                         new BatchCommand( testCmd1,
                                                                           testCmd2,
                                                                           testCmd3,
                                                                           testCmd4 ) );

        assertNotNull( results1 );
        assertEquals( 1,
                      results1.getMessages().size() );
        assertEquals( 1,
                      results1.getMessages( ResultType.ERROR ).size() );

        assertEquals( 3,
                      process.size() );
        assertProcessNotContainsNodes( process,
                                       testNode1,
                                       testNode2 );
        assertEquals( 1,
                      node2.getOutEdges().size() );
        assertNodeContainsOutgoingEdges( node2,
                                         e2 );

        assertEquals( 0,
                      testNode1.getInEdges().size() );
        assertEquals( 0,
                      testNode1.getOutEdges().size() );
        assertEquals( 0,
                      testNode2.getInEdges().size() );
    }

    @Test
    public void testBatchAddInvalidState2() {
        // Add one more TestDummyNode and related Edges to the existing graph. However we attach the TestDummyNode
        // to the existing StartProcessNode. This is invalid as a StartProcessNode can only contain one outgoing Edge
        // and hence the batch should fail.
        final TestDummyNode testNode1 = new TestDummyNode();
        final BpmnEdge testEdge1 = new BpmnEdgeImpl( new DefaultRoleImpl( "general_edge" ) );
        final AddGraphNodeCommand testCmd1 = new AddGraphNodeCommand( process,
                                                                      testNode1 );
        final AddEdgeCommand testCmd2 = new AddEdgeCommand( node1,
                                                            testNode1,
                                                            testEdge1 );
        final Results results1 = commandManager.execute( ruleManager,
                                                         new BatchCommand( testCmd1,
                                                                           testCmd2 ) );

        assertNotNull( results1 );
        assertEquals( 1,
                      results1.getMessages().size() );
        assertEquals( 1,
                      results1.getMessages( ResultType.ERROR ).size() );

        assertEquals( 3,
                      process.size() );
        assertProcessNotContainsNodes( process,
                                       testNode1 );
        assertEquals( 1,
                      node2.getOutEdges().size() );
        assertNodeContainsOutgoingEdges( node2,
                                         e2 );

        assertEquals( 0,
                      testNode1.getInEdges().size() );
        assertEquals( 0,
                      testNode1.getOutEdges().size() );
    }

}
