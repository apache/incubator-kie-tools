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

import java.util.HashSet;

import org.junit.Test;
import org.uberfire.ext.wires.bpmn.api.model.Role;
import org.uberfire.ext.wires.bpmn.api.model.impl.nodes.BPMNDiagramNode;
import org.uberfire.ext.wires.bpmn.api.model.impl.roles.DefaultRoleImpl;
import org.uberfire.ext.wires.bpmn.api.model.impl.rules.ContainmentRuleImpl;
import org.uberfire.ext.wires.bpmn.client.commands.CommandManager;
import org.uberfire.ext.wires.bpmn.client.commands.impl.DefaultCommandManagerImpl;
import org.uberfire.ext.wires.bpmn.client.rules.RuleManager;

public class ContainmentRulesTest {

    @Test
    public void testBpmnDiagramAddPermittedNode() {
        final BPMNDiagramNode graph = new BPMNDiagramNode();
        final RuleManager ruleManager = DefaultRuleManagerImpl.getInstance();
            ruleManager.addRule( new ContainmentRuleImpl( graph.getContent().getId(),
                                                          new HashSet<Role>() {{
                                                              add( new DefaultRoleImpl( "all" ) );
                                                          }} ) );

        final CommandManager commandManager = DefaultCommandManagerImpl.getInstance();
        //commandManager.execute()
        //TODO {manstis} Test is not implemented!
    }

}
