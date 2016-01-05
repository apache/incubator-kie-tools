/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.security.impl.authz;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.errai.security.shared.api.Group;
import org.jboss.errai.security.shared.api.GroupImpl;
import org.jboss.errai.security.shared.api.Role;
import org.jboss.errai.security.shared.api.RoleImpl;
import org.uberfire.commons.data.Cacheable;
import org.uberfire.security.Resource;
import org.uberfire.security.ResourceManager;
import org.uberfire.security.authz.RuntimeContentResource;
import org.uberfire.security.authz.RuntimeFeatureResource;
import org.uberfire.security.authz.RuntimeResource;

import static java.util.Collections.*;

public class RuntimeResourceManager implements ResourceManager {

    final Map<String, RuntimeRestriction> restrictions = new HashMap<String, RuntimeRestriction>();

    private RuntimeRestriction addResource( final RuntimeResource resource ) {

        RuntimeRestriction runtimeRestriction = null;
        if ( resource instanceof RuntimeFeatureResource ) {
            runtimeRestriction = new FeatureRestriction( ( (RuntimeFeatureResource) resource ).getRoles(), resource.getTraits() );
        } else if ( resource instanceof RuntimeContentResource ) {
            runtimeRestriction = new ContentRestriction( ( (RuntimeContentResource) resource ).getGroups(), resource.getTraits() );
        }

        restrictions.put( resource.getSignatureId(), runtimeRestriction );

        return runtimeRestriction;
    }

    public RuntimeRestriction getRestriction( final RuntimeResource resource ) {
        return restrictions.get( resource.getSignatureId() );
    }

    @Override
    public boolean supports( final Resource resource ) {
        if ( resource instanceof RuntimeResource ) {
            return true;
        }
        return false;
    }

    @Override
    public boolean requiresAuthentication( final Resource resource ) {
        if ( !( resource instanceof RuntimeResource ) ) {
            throw new IllegalArgumentException( "Parameter named 'resource' is not instance of clazz 'RuntimeResource'!" );
        }

        boolean refreshCache = false;
        if ( resource instanceof Cacheable ) {
            refreshCache = ( (Cacheable) resource ).requiresRefresh();
        }

        final RuntimeResource runtimeResource = (RuntimeResource) resource;

        RuntimeRestriction restriction = restrictions.get( runtimeResource.getSignatureId() );

        if ( restriction == null || refreshCache ) {
            restriction = addResource( runtimeResource );
        }

        if ( restriction == null || restriction.isEmpty() ) {
            return false;
        }

        return true;
    }

    public static abstract class RuntimeRestriction {

        final Collection<String> traits;

        public RuntimeRestriction( final Collection<String> traits ) {
            if ( traits != null ) {
                this.traits = traits;
            } else {
                this.traits = emptyList();
            }
        }

        public Collection<String> getTraits() {
            return traits;
        }

        public boolean isEmpty() {
            return traits.isEmpty();
        }
    }

    public static class FeatureRestriction extends RuntimeRestriction {

        final Collection<Role> roles;

        public FeatureRestriction( final Collection<String> roles,
                                   final Collection<String> traits ) {
            super( traits );
            if ( roles != null ) {
                final List<Role> tempRoles = new ArrayList<Role>( roles.size() );
                for ( final String tempRole : roles ) {
                    tempRoles.add( new RoleImpl( tempRole ) );
                }

                this.roles = unmodifiableList( tempRoles );
            } else {
                this.roles = emptyList();
            }
        }

        public Collection<Role> getRoles() {
            return roles;
        }

        public boolean isEmpty() {
            return roles.isEmpty() && super.isEmpty();
        }
    }

    public static class ContentRestriction extends RuntimeRestriction {

        final Collection<Group> groups;

        public ContentRestriction( final Collection<String> groups,
                                   final Collection<String> traits ) {
            super( traits );
            if ( groups != null ) {
                final List<Group> tempGroups = new ArrayList<Group>( groups.size() );
                for ( final String tempGroup : groups ) {
                    tempGroups.add( new GroupImpl( tempGroup ) );
                }

                this.groups = unmodifiableList( tempGroups );
            } else {
                this.groups = emptyList();
            }
        }

        public Collection<Group> getGroups() {
            return groups;
        }

        public boolean isEmpty() {
            return groups.isEmpty() && super.isEmpty();
        }
    }
}
