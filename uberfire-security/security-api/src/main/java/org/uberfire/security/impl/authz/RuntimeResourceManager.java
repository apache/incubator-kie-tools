/*
 * Copyright 2012 JBoss Inc
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

import org.uberfire.security.Resource;
import org.uberfire.security.ResourceManager;
import org.uberfire.security.Role;
import org.uberfire.security.authz.RuntimeResource;

import static java.util.Collections.*;

public class RuntimeResourceManager implements ResourceManager {

    final Map<String, RuntimeRestriction> restrictions = new HashMap<String, RuntimeRestriction>();

    private RuntimeRestriction addResource(final RuntimeResource resource) {

        if (restrictions.containsKey(resource.getSignatureId())) {
            return null;
        }

        final RuntimeRestriction runtimeRestriction = new RuntimeRestriction(resource.getRoles(), resource.getTraits());
        restrictions.put(resource.getSignatureId(), runtimeRestriction);

        return runtimeRestriction;
    }

    public RuntimeRestriction getRestriction(final RuntimeResource resource) {
        return restrictions.get(resource.getSignatureId());
    }

    @Override
    public boolean supports(final Resource resource) {
        if (resource instanceof RuntimeResource) {
            return true;
        }
        return false;
    }

    @Override
    public boolean requiresAuthentication(final Resource resource) {
        if (!(resource instanceof RuntimeResource)) {
            throw new IllegalArgumentException("Parameter named 'resource' is not instance of clazz 'RuntimeResource'!");
        }

        final RuntimeResource runtimeResource = (RuntimeResource) resource;

        RuntimeRestriction restriction = restrictions.get(runtimeResource.getSignatureId());

        if (restriction == null) {
            restriction = addResource(runtimeResource);
        }

        if (restriction == null || restriction.isEmpty()) {
            return false;
        }

        return true;
    }

    public static class RuntimeRestriction {

        final Collection<Role> roles;
        final Collection<String> traits;

        public RuntimeRestriction(final Collection<String> roles, final Collection<String> traits) {
            if (roles != null) {
                final List<Role> tempRoles = new ArrayList<Role>(roles.size());
                for (final String tempRole : roles) {
                    tempRoles.add(new Role() {
                        @Override
                        public String getName() {
                            return tempRole;
                        }
                    });
                }

                this.roles = unmodifiableList(tempRoles);
            } else {
                this.roles = emptyList();
            }

            if (traits != null) {
                this.traits = traits;
            } else {
                this.traits = emptyList();
            }
        }

        public Collection<Role> getRoles() {
            return roles;
        }

        public Collection<String> getTraits() {
            return traits;
        }

        public boolean isEmpty() {
            return roles.isEmpty() && traits.isEmpty();
        }
    }
}
