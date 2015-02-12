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
package org.uberfire.ext.wires.bpmn.client.rules.impl;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import org.junit.Test;
import org.uberfire.ext.wires.bpmn.api.model.Content;
import org.uberfire.ext.wires.bpmn.api.model.Role;
import org.uberfire.ext.wires.bpmn.api.model.impl.content.DefaultContentImpl;
import org.uberfire.ext.wires.bpmn.api.model.impl.nodes.ProcessNode;
import org.uberfire.ext.wires.bpmn.api.model.impl.nodes.StartProcessNode;
import org.uberfire.ext.wires.bpmn.api.model.impl.rules.ContainmentRuleImpl;
import org.uberfire.ext.wires.bpmn.beliefs.graph.Edge;
import org.uberfire.ext.wires.bpmn.beliefs.graph.GraphNode;
import org.uberfire.ext.wires.bpmn.client.commands.CommandManager;
import org.uberfire.ext.wires.bpmn.client.commands.ResultType;
import org.uberfire.ext.wires.bpmn.client.commands.Results;
import org.uberfire.ext.wires.bpmn.client.commands.impl.AddGraphNodeCommand;
import org.uberfire.ext.wires.bpmn.client.commands.impl.DefaultCommandManagerImpl;
import org.uberfire.ext.wires.bpmn.client.rules.RuleManager;

import static junit.framework.Assert.*;

public class ContainmentRulesTest {

    @Test
    public void testAddStartProcessNodeToProcess() {
        final ProcessNode process = new ProcessNode();
        final StartProcessNode node = new StartProcessNode();
        final RuleManager ruleManager = DefaultRuleManagerImpl.getInstance();

        for ( final Role role : node.getContent().getRoles() ) {
            ruleManager.addRule( new ContainmentRuleImpl( process.getContent().getId(),
                                                          new HashSet<Role>() {{
                                                              add( role );
                                                          }} ) );
        }

        final CommandManager commandManager = DefaultCommandManagerImpl.getInstance();
        final Results results = commandManager.execute( new AddGraphNodeCommand( process,
                                                                                 node ) );

        assertNotNull( results );
        assertEquals( 0,
                      results.getMessages().size() );

        assertEquals( 1,
                      process.size() );
        assertEquals( node,
                      process.getNode( node.getId() ) );
    }

    @Test
    public void testAddEndProcessNodeToProcess() {
        final ProcessNode process = new ProcessNode();
        final StartProcessNode node = new StartProcessNode();
        final RuleManager ruleManager = DefaultRuleManagerImpl.getInstance();

        for ( final Role role : node.getContent().getRoles() ) {
            ruleManager.addRule( new ContainmentRuleImpl( process.getContent().getId(),
                                                          new HashSet<Role>() {{
                                                              add( role );
                                                          }} ) );
        }

        final CommandManager commandManager = DefaultCommandManagerImpl.getInstance();
        final Results results = commandManager.execute( new AddGraphNodeCommand( process,
                                                                                 node ) );

        assertNotNull( results );
        assertEquals( 0,
                      results.getMessages().size() );

        assertEquals( 1,
                      process.size() );
        assertEquals( node,
                      process.getNode( node.getId() ) );
    }

    @Test
    public void testAddDummyNodeToProcess() {
        final ProcessNode process = new ProcessNode();
        final GraphNode<Content> node = new DummyNode();
        final RuleManager ruleManager = DefaultRuleManagerImpl.getInstance();

        for ( final Role role : node.getContent().getRoles() ) {
            ruleManager.addRule( new ContainmentRuleImpl( process.getContent().getId(),
                                                          new HashSet<Role>() {{
                                                              add( role );
                                                          }} ) );
        }

        final CommandManager commandManager = DefaultCommandManagerImpl.getInstance();
        final Results results = commandManager.execute( new AddGraphNodeCommand( process,
                                                                                 node ) );

        assertNotNull( results );
        assertEquals( 1,
                      results.getMessages().size() );
        assertEquals( 1,
                      results.getMessages( ResultType.ERROR ).size() );

        assertEquals( 0,
                      process.size() );
    }

    private static class DummyNode implements GraphNode<Content> {

        private final Content content = new DefaultContentImpl( "dummy",
                                                                "dummy",
                                                                "dummy",
                                                                Collections.EMPTY_SET,
                                                                Collections.EMPTY_SET );

        @Override
        public int getId() {
            return 0;
        }

        @Override
        public void setId( int id ) {
            //Stub
        }

        @Override
        public List<Edge> getInEdges() {
            return Collections.EMPTY_LIST;
        }

        @Override
        public List<Edge> getOutEdges() {
            return Collections.EMPTY_LIST;
        }

        @Override
        public Content getContent() {
            return content;
        }

        @Override
        public void setContent( Content content ) {
            //Stub
        }
    }

}
