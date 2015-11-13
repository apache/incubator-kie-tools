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
package org.uberfire.ext.wires.bpmn.api.model.impl.content;

import java.util.HashSet;
import java.util.Set;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.commons.validation.PortablePreconditions;
import org.uberfire.ext.wires.bpmn.api.model.Content;
import org.uberfire.ext.wires.bpmn.api.model.Property;
import org.uberfire.ext.wires.bpmn.api.model.Role;

/**
 * Content of a Bpmn GraphNode
 */
@Portable
public class DefaultContentImpl implements Content {

    private String id;
    private String title;
    private String description;
    private Set<Role> roles;
    private Set<Property> properties;

    public DefaultContentImpl( @MapsTo("id") final String id,
                               @MapsTo("title") final String title,
                               @MapsTo("description") final String description,
                               @MapsTo("roles") final Set<Role> roles,
                               @MapsTo("properties") final Set<Property> properties ) {
        this.id = PortablePreconditions.checkNotNull( "id",
                                                      id );
        this.title = PortablePreconditions.checkNotNull( "title",
                                                         title );
        this.description = PortablePreconditions.checkNotNull( "description",
                                                               description );
        this.roles = PortablePreconditions.checkNotNull( "roles",
                                                         roles );
        this.properties = PortablePreconditions.checkNotNull( "properties",
                                                              properties );
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public Set<Role> getRoles() {
        return roles;
    }

    @Override
    public Set<Property> getProperties() {
        return properties;
    }

    @Override
    public Content copy() {
        final Content copy = new DefaultContentImpl( this.id,
                                                     this.title,
                                                     this.description,
                                                     copyRoles( this.roles ),
                                                     copyProperties( this.properties ) );
        return copy;
    }

    private Set<Role> copyRoles( final Set<Role> roles ) {
        final Set<Role> copy = new HashSet<Role>();
        for ( Role role : roles ) {
            copy.add( role.copy() );
        }
        return copy;
    }

    private Set<Property> copyProperties( final Set<Property> properties ) {
        final Set<Property> copy = new HashSet<Property>();
        for ( Property property : properties ) {
            copy.add( property.copy() );
        }
        return copy;
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( !( o instanceof DefaultContentImpl ) ) {
            return false;
        }

        DefaultContentImpl that = (DefaultContentImpl) o;

        if ( !description.equals( that.description ) ) {
            return false;
        }
        if ( !id.equals( that.id ) ) {
            return false;
        }
        if ( !properties.equals( that.properties ) ) {
            return false;
        }
        if ( !roles.equals( that.roles ) ) {
            return false;
        }
        if ( !title.equals( that.title ) ) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = ~~result;
        result = 31 * result + title.hashCode();
        result = ~~result;
        result = 31 * result + description.hashCode();
        result = ~~result;
        result = 31 * result + roles.hashCode();
        result = ~~result;
        result = 31 * result + properties.hashCode();
        result = ~~result;
        return result;
    }

    @Override
    public String toString() {
        return "DefaultContentImpl{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", roles=" + roles +
                ", properties=" + properties +
                '}';
    }

}

