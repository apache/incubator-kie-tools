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
package org.uberfire.ext.wires.bpmn.api.model.impl.rules;

import java.util.Set;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.commons.validation.PortablePreconditions;
import org.uberfire.ext.wires.bpmn.api.model.Role;
import org.uberfire.ext.wires.bpmn.api.model.rules.ConnectionRule;

@Portable
public class ConnectionRuleImpl implements ConnectionRule {

    private String name;
    private Role role;
    private Set<PermittedConnection> permittedRoles;

    public ConnectionRuleImpl( @MapsTo("name") final String name,
                               @MapsTo("role") final Role role,
                               @MapsTo("permittedRoles") final Set<PermittedConnection> permittedRoles ) {
        this.name = PortablePreconditions.checkNotNull( "name",
                                                        name );
        this.role = PortablePreconditions.checkNotNull( "role",
                                                        role );
        this.permittedRoles = PortablePreconditions.checkNotNull( "permittedRoles",
                                                                  permittedRoles );
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
    public Set<PermittedConnection> getPermittedConnections() {
        return permittedRoles;
    }

}
