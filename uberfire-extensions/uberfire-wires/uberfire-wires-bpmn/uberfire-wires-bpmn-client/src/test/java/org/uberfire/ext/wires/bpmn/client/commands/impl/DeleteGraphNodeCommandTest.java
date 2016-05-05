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

import org.junit.Test;
import org.uberfire.ext.wires.bpmn.api.model.BpmnGraphNode;
import org.uberfire.ext.wires.bpmn.api.model.impl.nodes.EndProcessNode;
import org.uberfire.ext.wires.bpmn.api.model.impl.nodes.ProcessNode;
import org.uberfire.ext.wires.bpmn.api.model.impl.nodes.StartProcessNode;
import org.uberfire.ext.wires.bpmn.api.model.impl.roles.DefaultRoleImpl;
import org.uberfire.ext.wires.bpmn.api.model.impl.rules.CardinalityRuleImpl;
import org.uberfire.ext.wires.bpmn.api.model.rules.Rule;
import org.uberfire.ext.wires.bpmn.client.AbstractBaseRuleTest;
import org.uberfire.ext.wires.bpmn.client.TestDummyNode;
import org.uberfire.ext.wires.bpmn.client.commands.CommandManager;
import org.uberfire.ext.wires.bpmn.client.commands.Results;
import org.uberfire.ext.wires.bpmn.client.rules.RuleManager;
import org.uberfire.ext.wires.bpmn.client.rules.impl.DefaultRuleManagerImpl;

import static junit.framework.Assert.*;

public class DeleteGraphNodeCommandTest extends AbstractBaseRuleTest {

    @Test
    public void testDeleteStartProcessNodeFromProcess() {
        final ProcessNode process = new ProcessNode();
        final StartProcessNode node = new StartProcessNode();
        final RuleManager ruleManager = new DefaultRuleManagerImpl();

        //Set a minimum of 1 node
        ruleManager.addRule( new CardinalityRuleImpl( "Start Node Cardinality Rule",
                                                      new DefaultRoleImpl( "sequence_start" ),
                                                      1,
                                                      1,
                                                      Collections.EMPTY_SET,
                                                      Collections.EMPTY_SET ) );

        process.addNode( node );

        final CommandManager commandManager = new DefaultCommandManagerImpl();
        final Results results = commandManager.execute( ruleManager,
                                                        new DeleteGraphNodeCommand( process,
                                                                                    node ) );

        assertNotNull( results );
        assertEquals( 1,
                      results.getMessages().size() );

        assertEquals( 1,
                      process.size() );
        assertEquals( node,
                      process.getNode( node.getId() ) );
    }

    @Test
    public void testDeleteEndProcessNodeFromProcess() {
        final ProcessNode process = new ProcessNode();
        final EndProcessNode node = new EndProcessNode();
        final RuleManager ruleManager = new DefaultRuleManagerImpl();

        //Set a minimum of 1 node
        ruleManager.addRule( new CardinalityRuleImpl( "End Node Cardinality Rule",
                                                      new DefaultRoleImpl( "sequence_end" ),
                                                      1,
                                                      1,
                                                      Collections.EMPTY_SET,
                                                      Collections.EMPTY_SET ) );

        process.addNode( node );

        final CommandManager commandManager = new DefaultCommandManagerImpl();
        final Results results = commandManager.execute( ruleManager,
                                                        new DeleteGraphNodeCommand( process,
                                                                                    node ) );

        assertNotNull( results );
        assertEquals( 1,
                      results.getMessages().size() );

        assertEquals( 1,
                      process.size() );
        assertEquals( node,
                      process.getNode( node.getId() ) );
    }

    @Test
    public void testDeleteDummyNodeFromProcess() {
        final ProcessNode process = new ProcessNode();
        final BpmnGraphNode node = new TestDummyNode();
        final RuleManager ruleManager = new DefaultRuleManagerImpl();

        for ( Rule rule : getContainmentRules() ) {
            ruleManager.addRule( rule );
        }

        process.addNode( node );

        final CommandManager commandManager = new DefaultCommandManagerImpl();
        final Results results = commandManager.execute( ruleManager,
                                                        new DeleteGraphNodeCommand( process,
                                                                                    node ) );

        assertNotNull( results );
        assertEquals( 0,
                      results.getMessages().size() );

        assertEquals( 0,
                      process.size() );
    }

}
