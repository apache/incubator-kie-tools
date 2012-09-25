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

import java.util.Collection;
import java.util.List;

import org.uberfire.security.Role;
import org.uberfire.security.authz.RolesResource;

public class RolesResourceImpl implements RolesResource {

    private final List<Role> roles;

    public RolesResourceImpl(final List<Role> roles) {
        this.roles = roles;
    }

    @Override
    public Collection<Role> getRoles() {
        return roles;
    }

}
