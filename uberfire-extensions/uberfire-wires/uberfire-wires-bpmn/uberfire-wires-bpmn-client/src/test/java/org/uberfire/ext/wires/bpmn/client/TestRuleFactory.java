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
package org.uberfire.ext.wires.bpmn.client;

import java.util.HashSet;
import java.util.Set;

import org.uberfire.ext.wires.bpmn.api.model.Role;
import org.uberfire.ext.wires.bpmn.api.model.impl.nodes.ProcessNode;
import org.uberfire.ext.wires.bpmn.api.model.impl.roles.DefaultRoleImpl;
import org.uberfire.ext.wires.bpmn.api.model.impl.rules.CardinalityRuleImpl;
import org.uberfire.ext.wires.bpmn.api.model.impl.rules.ContainmentRuleImpl;
import org.uberfire.ext.wires.bpmn.api.model.rules.Rule;

/**
 * A Factory for Rules for testing
 */
public class TestRuleFactory {

    public static Set<Rule> getContainmentRules() {
        final Set<Rule> rules = new HashSet<Rule>();
        rules.add( new ContainmentRuleImpl( "Process Node Containment Rule",
                                            new ProcessNode().getContent().getId(),
                                            new HashSet<Role>() {{
                                                add( new DefaultRoleImpl( "all" ) );
                                            }} ) );
        return rules;
    }

    public static Set<Rule> getCardinalityRules() {
        final Set<Rule> rules = new HashSet<Rule>();
        rules.add( new CardinalityRuleImpl( "Start Node Cardinality Rule",
                                            new DefaultRoleImpl( "sequence_start" ),
                                            0,
                                            1 ) );
        rules.add( new CardinalityRuleImpl( "End Node Cardinality Rule",
                                            new DefaultRoleImpl( "sequence_end" ),
                                            0,
                                            1 ) );
        return rules;
    }

}
