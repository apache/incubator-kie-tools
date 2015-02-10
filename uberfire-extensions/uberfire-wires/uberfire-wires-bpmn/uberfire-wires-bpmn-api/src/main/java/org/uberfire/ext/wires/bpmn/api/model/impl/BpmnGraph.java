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
package org.uberfire.ext.wires.bpmn.api.model.impl;

import java.util.HashSet;
import java.util.Set;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.ext.wires.bpmn.api.model.Content;
import org.uberfire.ext.wires.bpmn.api.model.HasProperties;
import org.uberfire.ext.wires.bpmn.api.model.HasRoles;
import org.uberfire.ext.wires.bpmn.api.model.Property;
import org.uberfire.ext.wires.bpmn.api.model.Role;
import org.uberfire.ext.wires.bpmn.api.model.impl.properties.DefaultPropertyImpl;
import org.uberfire.ext.wires.bpmn.api.model.impl.roles.CanContainArtifactsRole;
import org.uberfire.ext.wires.bpmn.api.model.impl.types.StringType;
import org.uberfire.ext.wires.bpmn.beliefs.graph.impl.GraphImpl;
import org.uberfire.ext.wires.bpmn.beliefs.graph.impl.MapGraphStore;

@Portable
public class BpmnGraph extends GraphImpl<Content> implements HasRoles,
                                                             HasProperties {

    private Set<Role> roles = new HashSet<Role>() {{
        add( new CanContainArtifactsRole() );
    }};

    private Set<Property> properties = new HashSet<Property>() {{
        add( new DefaultPropertyImpl( "processn",
                                      StringType.INSTANCE,
                                      "Process Name",
                                      "Process Name",
                                      false,
                                      false ) );
        add( new DefaultPropertyImpl( "documentation",
                                      StringType.INSTANCE,
                                      "Documentation",
                                      "This attribute is used to annotate the BPMN element, such as descriptions and other documentation.",
                                      false,
                                      true ) );
    }};

    public BpmnGraph() {
        super( new MapGraphStore<Content>() );
    }

    @Override
    public Set<Role> getRoles() {
        return roles;
    }

    @Override
    public Set<Property> getProperties() {
        return properties;
    }

}
