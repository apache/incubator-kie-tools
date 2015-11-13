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
import org.uberfire.ext.wires.bpmn.client.commands.ResultType;
import org.uberfire.ext.wires.bpmn.client.commands.Results;
import org.uberfire.ext.wires.bpmn.client.rules.RuleManager;

import static junit.framework.Assert.*;

public class ConnectionRulesTest extends AbstractBaseRuleTest {

    private ProcessNode process;
    private RuleManager ruleManager;
    private StartProcessNode node1;
    private TestDummyNode node2;
    private EndProcessNode node3;

    @Before
    public void setupNodes() {
        process = new ProcessNode();
        ruleManager = new DefaultRuleManagerImpl();
        node1 = new StartProcessNode();
        node2 = new TestDummyNode();
        node3 = new EndProcessNode();

        for ( Rule rule : getConnectionRules() ) {
            ruleManager.addRule( rule );
        }

        //Add StartProcessNode
        process.addNode( node1 );
        process.addNode( node2 );
        process.addNode( node3 );
    }

    @Test
    public void testAddEdgeBetweenStartNodeAndDummyNode() {
        final BpmnEdge e1 = new BpmnEdgeImpl( new DefaultRoleImpl( "general_edge" ) );

        final Results results1 = ruleManager.checkConnectionRules( node1,
                                                                   node2,
                                                                   e1 );

        //An Edge with role "general_edge" is permitted between StartNode and DummyNode
        assertNotNull( results1 );
        assertEquals( 0,
                      results1.getMessages().size() );
    }

    @Test
    public void testAddEdgeBetweenDummyNodeAndEndNode() {
        final BpmnEdge e1 = new BpmnEdgeImpl( new DefaultRoleImpl( "general_edge" ) );

        final Results results1 = ruleManager.checkConnectionRules( node2,
                                                                   node3,
                                                                   e1 );

        //An Edge with role "general_edge" is permitted between DummyNode and EndNode
        assertNotNull( results1 );
        assertEquals( 0,
                      results1.getMessages().size() );
    }

    @Test
    public void testAddEdgeBetweenStartNodeAndEndNode() {
        final BpmnEdge e1 = new BpmnEdgeImpl( new DefaultRoleImpl( "general_edge" ) );

        final Results results1 = ruleManager.checkConnectionRules( node1,
                                                                   node3,
                                                                   e1 );

        //An Edge with role "general_edge" is NOT permitted between StartNode and EndNode
        assertNotNull( results1 );
        assertEquals( 1,
                      results1.getMessages().size() );
        assertEquals( 1,
                      results1.getMessages( ResultType.ERROR ).size() );
    }

}
