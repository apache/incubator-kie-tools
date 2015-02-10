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
import java.util.Set;

import org.junit.Test;
import org.uberfire.ext.wires.bpmn.api.model.Role;
import org.uberfire.ext.wires.bpmn.api.model.impl.BpmnGraph;
import org.uberfire.ext.wires.bpmn.api.model.impl.rules.ContainmentRuleImpl;
import org.uberfire.ext.wires.bpmn.client.rules.RuleManager;

import static org.junit.Assert.*;

public class ContainmentRulesTest {

    @Test
    public void testBpmnDiagramAddPermittedNode() {
        final BpmnGraph graph = new BpmnGraph();
        final RuleManager ruleManager = DefaultRuleManagerImpl.getInstance();
        ruleManager.addRule( new ContainmentRuleImpl( getFirstRole( graph.getRoles() ),
                                                      new HashSet<Role>() {{
                                                          add( new PermittedRole() );
                                                      }} ) );
        //TODO {manstis} Test is not implemented!
    }

    private Role getFirstRole( final Set<Role> roles ) {
        if ( roles == null || roles.isEmpty() ) {
            fail( "Roles have not been set" );
        }
        return roles.iterator().next();
    }

}
