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
import org.uberfire.ext.wires.bpmn.api.model.rules.ContainmentRule;

@Portable
public class ContainmentRuleImpl implements ContainmentRule {

    private String name;
    private String id;
    private Set<Role> permittedRoles;

    public ContainmentRuleImpl( @MapsTo("name") final String name,
                                @MapsTo("id") final String id,
                                @MapsTo("permittedRoles") final Set<Role> permittedRoles ) {
        this.name = PortablePreconditions.checkNotNull( "name",
                                                        name );
        this.id = PortablePreconditions.checkNotNull( "id",
                                                      id );
        this.permittedRoles = PortablePreconditions.checkNotNull( "permittedRoles",
                                                                  permittedRoles );
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public Set<Role> getPermittedRoles() {
        return permittedRoles;
    }

}
