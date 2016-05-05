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

public class CommandManagerTest extends AbstractBaseRuleTest {

    @Test
    public void testAddOneNodeToProcess() {
        final ProcessNode process = new ProcessNode();
        final RuleManager ruleManager = new DefaultRuleManagerImpl();

        for ( Rule rule : getContainmentRules() ) {
            ruleManager.addRule( rule );
        }
        for ( Rule rule : getCardinalityRules() ) {
            ruleManager.addRule( rule );
        }

        final CommandManager commandManager = new DefaultCommandManagerImpl();

        final StartProcessNode node1 = new StartProcessNode();

        //Add StartProcessNode to Graph
        final Results results1 = commandManager.execute( ruleManager,
                                                         new AddGraphNodeCommand( process,
                                                                                  node1 ) );
        assertEquals( node1,
                      process.getNode( node1.getId() ) );

        assertNotNull( results1 );
        assertEquals( 0,
                      results1.getMessages().size() );

        assertEquals( 1,
                      process.size() );
        assertProcessContainsNodes( process,
                                    node1 );
    }

    @Test
    public void testAddTwoNodesToProcess() {
        final ProcessNode process = new ProcessNode();
        final RuleManager ruleManager = new DefaultRuleManagerImpl();

        for ( Rule rule : getContainmentRules() ) {
            ruleManager.addRule( rule );
        }
        for ( Rule rule : getCardinalityRules() ) {
            ruleManager.addRule( rule );
        }

        final CommandManager commandManager = new DefaultCommandManagerImpl();

        final StartProcessNode node1 = new StartProcessNode();
        final EndProcessNode node2 = new EndProcessNode();

        //Add StartProcessNode to Graph
        final Results results1 = commandManager.execute( ruleManager,
                                                         new AddGraphNodeCommand( process,
                                                                                  node1 ) );
        assertEquals( node1,
                      process.getNode( node1.getId() ) );

        assertNotNull( results1 );
        assertEquals( 0,
                      results1.getMessages().size() );

        //Add EndProcessNode to Graph
        final Results results2 = commandManager.execute( ruleManager,
                                                         new AddGraphNodeCommand( process,
                                                                                  node2 ) );
        assertEquals( node2,
                      process.getNode( node2.getId() ) );

        assertNotNull( results2 );
        assertEquals( 0,
                      results2.getMessages().size() );

        assertEquals( 2,
                      process.size() );
        assertProcessContainsNodes( process,
                                    node1,
                                    node2 );
    }

    @Test
    public void testAddTwoNodesToProcessThenUndo() {
        final ProcessNode process = new ProcessNode();
        final RuleManager ruleManager = new DefaultRuleManagerImpl();

        for ( Rule rule : getContainmentRules() ) {
            ruleManager.addRule( rule );
        }
        for ( Rule rule : getCardinalityRules() ) {
            ruleManager.addRule( rule );
        }

        final CommandManager commandManager = new DefaultCommandManagerImpl();

        final StartProcessNode node1 = new StartProcessNode();
        final EndProcessNode node2 = new EndProcessNode();

        //Add StartProcessNode to Graph
        final Results results1 = commandManager.execute( ruleManager,
                                                         new AddGraphNodeCommand( process,
                                                                                  node1 ) );
        assertNotNull( results1 );
        assertEquals( 0,
                      results1.getMessages().size() );
        assertEquals( 1,
                      process.size() );
        assertProcessContainsNodes( process,
                                    node1 );

        //Add EndProcessNode to Graph
        final Results results2 = commandManager.execute( ruleManager,
                                                         new AddGraphNodeCommand( process,
                                                                                  node2 ) );
        assertNotNull( results2 );
        assertEquals( 0,
                      results2.getMessages().size() );
        assertEquals( 2,
                      process.size() );
        assertProcessContainsNodes( process,
                                    node1,
                                    node2 );

        //Undo last Command
        commandManager.undo( ruleManager );
        assertEquals( 1,
                      process.size() );
        assertProcessContainsNodes( process,
                                    node1 );
        assertProcessNotContainsNodes( process,
                                       node2 );

        //Undo last Command
        commandManager.undo( ruleManager );
        assertEquals( 0,
                      process.size() );
        assertProcessNotContainsNodes( process,
                                       node1,
                                       node2 );
    }

    @Test
    public void testAddNotPermittedNodesToProcess() {
        final ProcessNode process = new ProcessNode();
        final RuleManager ruleManager = new DefaultRuleManagerImpl();

        //Set a minimum of 0 and a maximum of 1 node
        ruleManager.addRule( new CardinalityRuleImpl( "TestDummyNode Cardinality Rule",
                                                      new DefaultRoleImpl( "dummy" ),
                                                      0,
                                                      1,
                                                      Collections.EMPTY_SET,
                                                      Collections.EMPTY_SET ) );

        final TestDummyNode node1 = new TestDummyNode();
        final TestDummyNode node2 = new TestDummyNode();

        final CommandManager commandManager = new DefaultCommandManagerImpl();

        //Add StartProcessNode to Graph
        final Results results1 = commandManager.execute( ruleManager,
                                                         new AddGraphNodeCommand( process,
                                                                                  node1 ) );

        assertNotNull( results1 );
        assertEquals( 0,
                      results1.getMessages().size() );

        assertEquals( 1,
                      process.size() );
        assertProcessContainsNodes( process,
                                    node1 );

        //Add EndProcessNode to Graph
        final Results results2 = commandManager.execute( ruleManager,
                                                         new AddGraphNodeCommand( process,
                                                                                  node2 ) );
        assertNotNull( results2 );
        assertEquals( 1,
                      results2.getMessages().size() );

        assertEquals( 1,
                      process.size() );
        assertProcessContainsNodes( process,
                                    node1 );
        assertProcessNotContainsNodes( process,
                                       node2 );

        //Undo last Command
        commandManager.undo( ruleManager );
        assertEquals( 0,
                      process.size() );
        assertProcessNotContainsNodes( process,
                                       node1,
                                       node2 );
    }

}
