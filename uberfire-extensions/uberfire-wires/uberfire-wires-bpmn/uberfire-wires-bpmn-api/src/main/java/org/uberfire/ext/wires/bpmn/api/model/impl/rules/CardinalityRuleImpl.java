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
package org.uberfire.ext.wires.bpmn.api.model.impl.rules;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.commons.validation.PortablePreconditions;
import org.uberfire.ext.wires.bpmn.api.model.Role;
import org.uberfire.ext.wires.bpmn.api.model.rules.CardinalityRule;

@Portable
public class CardinalityRuleImpl implements CardinalityRule {

    private String name;
    private Role role;
    private long minOccurrences = 0;
    private long maxOccurrences = 0;
    private Set<ConnectorRule> incomingConnectionRules = new HashSet<ConnectorRule>();
    private Set<ConnectorRule> outgoingConnectionRules = new HashSet<ConnectorRule>();

    public CardinalityRuleImpl( @MapsTo("name") final String name,
                                @MapsTo("role") Role role,
                                @MapsTo("minOccurrences") long minOccurrences,
                                @MapsTo("maxOccurrences") long maxOccurrences ) {
        this( name,
              role,
              minOccurrences,
              maxOccurrences,
              Collections.EMPTY_SET,
              Collections.EMPTY_SET );
    }

    public CardinalityRuleImpl( @MapsTo("name") final String name,
                                @MapsTo("role") Role role,
                                @MapsTo("minOccurrences") long minOccurrences,
                                @MapsTo("maxOccurrences") long maxOccurrences,
                                @MapsTo("incomingConnectionRules") Set<ConnectorRule> incomingConnectionRules,
                                @MapsTo("outgoingConnectionRules") Set<ConnectorRule> outgoingConnectionRules ) {
        this.name = PortablePreconditions.checkNotNull( "name",
                                                        name );
        this.role = PortablePreconditions.checkNotNull( "role",
                                                        role );
        this.minOccurrences = minOccurrences;
        this.maxOccurrences = maxOccurrences;
        this.incomingConnectionRules = PortablePreconditions.checkNotNull( "incomingConnectionRules",
                                                                           incomingConnectionRules );
        this.outgoingConnectionRules = PortablePreconditions.checkNotNull( "outgoingConnectionRules",
                                                                           outgoingConnectionRules );
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Role getRole() {
        return role;
    }

    @Override
    public long getMinOccurrences() {
        return minOccurrences;
    }

    @Override
    public long getMaxOccurrences() {
        return maxOccurrences;
    }

    @Override
    public Set<ConnectorRule> getIncomingConnectionRules() {
        return incomingConnectionRules;
    }

    @Override
    public Set<ConnectorRule> getOutgoingConnectionRules() {
        return outgoingConnectionRules;
    }

}
