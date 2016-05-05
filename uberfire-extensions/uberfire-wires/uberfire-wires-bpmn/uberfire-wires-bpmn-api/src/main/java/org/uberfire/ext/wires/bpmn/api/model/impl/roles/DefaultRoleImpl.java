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
package org.uberfire.ext.wires.bpmn.api.model.impl.roles;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.commons.validation.PortablePreconditions;
import org.uberfire.ext.wires.bpmn.api.model.Role;

@Portable
public class DefaultRoleImpl implements Role {

    private String name;

    public DefaultRoleImpl( @MapsTo("name") final String name ) {
        this.name = PortablePreconditions.checkNotNull( "name",
                                                        name );
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Role copy() {
        return new DefaultRoleImpl( this.name );
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( !( o instanceof DefaultRoleImpl ) ) {
            return false;
        }

        DefaultRoleImpl that = (DefaultRoleImpl) o;

        if ( !name.equals( that.name ) ) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        return "DefaultRoleImpl{" +
                "name='" + name + '\'' +
                '}';
    }

}
