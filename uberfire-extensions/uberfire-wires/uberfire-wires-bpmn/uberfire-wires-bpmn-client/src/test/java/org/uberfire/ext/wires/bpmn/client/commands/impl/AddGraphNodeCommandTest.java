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

import org.junit.Test;
import org.uberfire.ext.wires.bpmn.api.model.BpmnGraphNode;
import org.uberfire.ext.wires.bpmn.api.model.impl.nodes.EndProcessNode;
import org.uberfire.ext.wires.bpmn.api.model.impl.nodes.ProcessNode;
import org.uberfire.ext.wires.bpmn.api.model.impl.nodes.StartProcessNode;
import org.uberfire.ext.wires.bpmn.api.model.rules.Rule;
import org.uberfire.ext.wires.bpmn.client.AbstractBaseRuleTest;
import org.uberfire.ext.wires.bpmn.client.TestDummyNode;
import org.uberfire.ext.wires.bpmn.client.commands.CommandManager;
import org.uberfire.ext.wires.bpmn.client.commands.ResultType;
import org.uberfire.ext.wires.bpmn.client.commands.Results;
import org.uberfire.ext.wires.bpmn.client.rules.RuleManager;
import org.uberfire.ext.wires.bpmn.client.rules.impl.DefaultRuleManagerImpl;

import static junit.framework.Assert.*;

public class AddGraphNodeCommandTest extends AbstractBaseRuleTest {

    @Test
    public void testAddStartProcessNodeToProcess() {
        final ProcessNode process = new ProcessNode();
        final RuleManager ruleManager = new DefaultRuleManagerImpl();

        for ( Rule rule : getContainmentRules() ) {
            ruleManager.addRule( rule );
        }
        for ( Rule rule : getCardinalityRules() ) {
            ruleManager.addRule( rule );
        }

        final StartProcessNode node1 = new StartProcessNode();
        final StartProcessNode node2 = new StartProcessNode();

        final CommandManager commandManager = new DefaultCommandManagerImpl();

        //Add one Node
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

        //Try to add another Node (rules specify maximum as one)
        final Results results2 = commandManager.execute( ruleManager,
                                                         new AddGraphNodeCommand( process,
                                                                                  node2 ) );

        assertNotNull( results2 );
        assertEquals( 1,
                      results2.getMessages().size() );
        assertEquals( 1,
                      results2.getMessages( ResultType.ERROR ).size() );

        assertEquals( 1,
                      process.size() );
        assertProcessContainsNodes( process,
                                    node1 );
        assertProcessNotContainsNodes( process,
                                       node2 );
    }

    @Test
    public void testAddEndProcessNodeToProcess() {
        final ProcessNode process = new ProcessNode();
        final RuleManager ruleManager = new DefaultRuleManagerImpl();

        for ( Rule rule : getContainmentRules() ) {
            ruleManager.addRule( rule );
        }
        for ( Rule rule : getCardinalityRules() ) {
            ruleManager.addRule( rule );
        }

        final EndProcessNode node1 = new EndProcessNode();
        final EndProcessNode node2 = new EndProcessNode();

        final CommandManager commandManager = new DefaultCommandManagerImpl();

        //Add one Node
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

        //Try to add another Node (rules specify maximum as one)
        final Results results2 = commandManager.execute( ruleManager,
                                                         new AddGraphNodeCommand( process,
                                                                                  node2 ) );

        assertNotNull( results2 );
        assertEquals( 1,
                      results2.getMessages().size() );
        assertEquals( 1,
                      results2.getMessages( ResultType.ERROR ).size() );

        assertEquals( 1,
                      process.size() );
        assertProcessContainsNodes( process,
                                    node1 );
        assertProcessNotContainsNodes( process,
                                       node2 );
    }

    @Test
    public void testAddDummyNodeToProcess() {
        final ProcessNode process = new ProcessNode();
        final BpmnGraphNode node = new TestDummyNode();
        final RuleManager ruleManager = new DefaultRuleManagerImpl();

        for ( Rule rule : getContainmentRules() ) {
            ruleManager.addRule( rule );
        }
        for ( Rule rule : getCardinalityRules() ) {
            ruleManager.addRule( rule );
        }

        final CommandManager commandManager = new DefaultCommandManagerImpl();
        final Results results = commandManager.execute( ruleManager,
                                                        new AddGraphNodeCommand( process,
                                                                                 node ) );

        assertNotNull( results );
        assertEquals( 1,
                      results.getMessages().size() );
        assertEquals( 1,
                      results.getMessages( ResultType.ERROR ).size() );

        assertEquals( 0,
                      process.size() );
    }

}
