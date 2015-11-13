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

public class AddEdgeCommandTest extends AbstractBaseRuleTest {

    private ProcessNode process;
    private RuleManager ruleManager;
    private StartProcessNode node1;
    private TestDummyNode node2;
    private EndProcessNode node3;
    private CommandManager commandManager;

    @Before
    public void setupNodes() {
        //Dummy process for each test consists of 3 unconnected nodes
        //-----------------------------------------------------------
        //
        // [StartNode]       [DummyNode]       [EndNode]
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
    }

    @Test
    public void testAddEdgeBetweenStartNodeAndDummyNode() {
        final BpmnEdge e1 = new BpmnEdgeImpl( new DefaultRoleImpl( "general_edge" ) );

        //An Edge with role "general_edge" is permitted between StartNode and DummyNode
        final Results results1 = commandManager.execute( ruleManager,
                                                         new AddEdgeCommand( node1,
                                                                             node2,
                                                                             e1 ) );

        assertNotNull( results1 );
        assertEquals( 0,
                      results1.getMessages().size() );

        assertEquals( 0,
                      node1.getInEdges().size() );
        assertEquals( 1,
                      node1.getOutEdges().size() );
        assertEquals( 1,
                      node2.getInEdges().size() );
        assertEquals( 0,
                      node2.getOutEdges().size() );

        assertEquals( e1,
                      node1.getOutEdges().toArray()[ 0 ] );
        assertEquals( e1,
                      node2.getInEdges().toArray()[ 0 ] );
    }

    @Test
    public void testAddEdgeBetweenDummyNodeAndEndNode() {
        final BpmnEdge e1 = new BpmnEdgeImpl( new DefaultRoleImpl( "general_edge" ) );

        //An Edge with role "general_edge" is permitted between DummyNode and EndNode
        final Results results1 = commandManager.execute( ruleManager,
                                                         new AddEdgeCommand( node2,
                                                                             node3,
                                                                             e1 ) );

        assertNotNull( results1 );
        assertEquals( 0,
                      results1.getMessages().size() );

        assertEquals( 0,
                      node2.getInEdges().size() );
        assertEquals( 1,
                      node2.getOutEdges().size() );
        assertEquals( 1,
                      node3.getInEdges().size() );
        assertEquals( 0,
                      node3.getOutEdges().size() );

        assertEquals( e1,
                      node2.getOutEdges().toArray()[ 0 ] );
        assertEquals( e1,
                      node3.getInEdges().toArray()[ 0 ] );
    }

    @Test
    public void testAddEdgeBetweenStartNodeAndEndNode() {
        final BpmnEdge e1 = new BpmnEdgeImpl( new DefaultRoleImpl( "general_edge" ) );

        //An Edge with role "general_edge" is NOT permitted between StartNode and EndNode
        final Results results1 = commandManager.execute( ruleManager,
                                                         new AddEdgeCommand( node1,
                                                                             node3,
                                                                             e1 ) );

        assertNotNull( results1 );
        assertEquals( 1,
                      results1.getMessages().size() );
        assertEquals( 1,
                      results1.getMessages( ResultType.ERROR ).size() );

        assertEquals( 0,
                      node1.getInEdges().size() );
        assertEquals( 0,
                      node1.getOutEdges().size() );
        assertEquals( 0,
                      node3.getInEdges().size() );
        assertEquals( 0,
                      node3.getOutEdges().size() );
    }

    @Test
    public void testStartNodeOutgoingCardinalityAndDummyNode() {
        final BpmnEdge e1 = new BpmnEdgeImpl( new DefaultRoleImpl( "general_edge" ) );

        //An Edge with role "general_edge" is permitted between StartNode and DummyNode
        final Results results1 = commandManager.execute( ruleManager,
                                                         new AddEdgeCommand( node1,
                                                                             node2,
                                                                             e1 ) );

        assertNotNull( results1 );
        assertEquals( 0,
                      results1.getMessages().size() );

        assertEquals( 0,
                      node1.getInEdges().size() );
        assertEquals( 1,
                      node1.getOutEdges().size() );
        assertEquals( 1,
                      node2.getInEdges().size() );
        assertEquals( 0,
                      node2.getOutEdges().size() );

        assertEquals( e1,
                      node1.getOutEdges().toArray()[ 0 ] );
        assertEquals( e1,
                      node2.getInEdges().toArray()[ 0 ] );

        //Try to add another Edge with role "general_edge" between StartNode and DummyNode. This should not be allowed.
        final Results results2 = commandManager.execute( ruleManager,
                                                         new AddEdgeCommand( node1,
                                                                             node2,
                                                                             e1 ) );

        assertNotNull( results2 );
        assertEquals( 1,
                      results2.getMessages().size() );
        assertEquals( 1,
                      results2.getMessages( ResultType.ERROR ).size() );

        assertEquals( 0,
                      node1.getInEdges().size() );
        assertEquals( 1,
                      node1.getOutEdges().size() );
        assertEquals( 1,
                      node2.getInEdges().size() );
        assertEquals( 0,
                      node2.getOutEdges().size() );

        assertEquals( e1,
                      node1.getOutEdges().toArray()[ 0 ] );
        assertEquals( e1,
                      node2.getInEdges().toArray()[ 0 ] );
    }

    @Test
    public void testDummyNodeAndEndNodeIncomingCardinality() {
        final BpmnEdge e1 = new BpmnEdgeImpl( new DefaultRoleImpl( "general_edge" ) );

        //An Edge with role "general_edge" is permitted between DummyNode and EndNode
        final Results results1 = commandManager.execute( ruleManager,
                                                         new AddEdgeCommand( node2,
                                                                             node3,
                                                                             e1 ) );

        assertNotNull( results1 );
        assertEquals( 0,
                      results1.getMessages().size() );

        assertEquals( 0,
                      node2.getInEdges().size() );
        assertEquals( 1,
                      node2.getOutEdges().size() );
        assertEquals( 1,
                      node3.getInEdges().size() );
        assertEquals( 0,
                      node3.getOutEdges().size() );

        assertEquals( e1,
                      node2.getOutEdges().toArray()[ 0 ] );
        assertEquals( e1,
                      node3.getInEdges().toArray()[ 0 ] );

        //Try to add another Edge with role "general_edge" between DummyNode and EndNode. This should not be allowed.
        final Results results2 = commandManager.execute( ruleManager,
                                                         new AddEdgeCommand( node2,
                                                                             node3,
                                                                             e1 ) );

        assertNotNull( results2 );
        assertEquals( 1,
                      results2.getMessages().size() );
        assertEquals( 1,
                      results2.getMessages( ResultType.ERROR ).size() );

        assertEquals( 0,
                      node2.getInEdges().size() );
        assertEquals( 1,
                      node2.getOutEdges().size() );
        assertEquals( 1,
                      node3.getInEdges().size() );
        assertEquals( 0,
                      node3.getOutEdges().size() );

        assertEquals( e1,
                      node2.getOutEdges().toArray()[ 0 ] );
        assertEquals( e1,
                      node3.getInEdges().toArray()[ 0 ] );
    }

}
