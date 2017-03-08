/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.uberfire.security.impl.authz;

import java.util.HashMap;
import java.util.Map;

import org.jboss.errai.security.shared.api.Group;
import org.jboss.errai.security.shared.api.GroupImpl;
import org.jboss.errai.security.shared.api.Role;
import org.jboss.errai.security.shared.api.RoleImpl;
import org.uberfire.security.authz.AuthorizationPolicy;
import org.uberfire.security.authz.Permission;
import org.uberfire.security.authz.PermissionType;
import org.uberfire.security.authz.PermissionTypeRegistry;

/**
 * Fluent API for the creation of AuthorizationPolicy instances. Example:
 *
 * <pre>
 * AuthorizationPolicy policy = permissionManager.newAuthorizationPolicy()
 * .role("role1", 3).permission("resource.read", true)
 * .role("role2", 2).permission("resource.read", false)
 * .role("role3", 1).permission("resource.read.1", true)
 * .build();
 * </pre>
 */
public class AuthorizationPolicyBuilder {

    private PermissionTypeRegistry permissionTypeRegistry;
    private DefaultAuthorizationPolicy policy = new DefaultAuthorizationPolicy();
    private Map<String, Role> roles = new HashMap<>();
    private Map<String, Group> groups = new HashMap<>();

    private transient Role _currentRole = null;
    private transient Group _currentGroup = null;

    public AuthorizationPolicyBuilder(PermissionTypeRegistry permissionTypeRegistry) {
        this.permissionTypeRegistry = permissionTypeRegistry;
    }

    public AuthorizationPolicyBuilder bydefault() {
        _currentRole = null;
        _currentGroup = null;
        return this;
    }

    public AuthorizationPolicyBuilder role(String role) {
        _currentGroup = null;
        _currentRole = roles.get(role);
        if (_currentRole == null) {
            roles.put(role,
                      _currentRole = new RoleImpl(role));
        }
        return this;
    }

    public AuthorizationPolicyBuilder role(String role,
                                           String home) {
        role(role);
        home(home);
        return this;
    }

    public AuthorizationPolicyBuilder role(String role,
                                           int priority) {
        role(role);
        priority(priority);
        return this;
    }

    public AuthorizationPolicyBuilder role(String role,
                                           int priority,
                                           String home) {
        role(role);
        priority(priority);
        home(home);
        return this;
    }

    public AuthorizationPolicyBuilder group(String group) {
        _currentRole = null;
        _currentGroup = groups.get(group);
        if (_currentGroup == null) {
            groups.put(group,
                       _currentGroup = new GroupImpl(group));
        }
        return this;
    }

    public AuthorizationPolicyBuilder group(String group,
                                            int priority,
                                            String home) {
        group(group);
        priority(priority);
        home(home);
        return this;
    }

    public AuthorizationPolicyBuilder group(String group,
                                            int priority) {
        group(group);
        priority(priority);
        return this;
    }

    public AuthorizationPolicyBuilder group(String group,
                                            String home) {
        group(group);
        home(home);
        return this;
    }

    public AuthorizationPolicyBuilder permission(String name,
                                                 Boolean granted) {
        PermissionType type = permissionTypeRegistry.resolve(name);
        Permission permission = type.createPermission(name,
                                                      granted);
        if (_currentRole != null) {
            policy.addPermission(_currentRole,
                                 permission);
        } else if (_currentGroup != null) {
            policy.addPermission(_currentGroup,
                                 permission);
        } else {
            policy.addPermission(permission);
        }
        return this;
    }

    public AuthorizationPolicyBuilder description(String description) {
        if (_currentRole != null) {
            policy.setRoleDescription(_currentRole,
                                      description);
        } else if (_currentGroup != null) {
            policy.setGroupDescription(_currentGroup,
                                       description);
        } else {
            throw new IllegalStateException("Invoke role() or group() first");
        }
        return this;
    }

    public AuthorizationPolicyBuilder priority(int priority) {
        if (_currentRole != null) {
            policy.setPriority(_currentRole,
                               priority);
        } else if (_currentGroup != null) {
            policy.setPriority(_currentGroup,
                               priority);
        } else {
            throw new IllegalStateException("Invoke role() or group() first");
        }
        return this;
    }

    public AuthorizationPolicyBuilder home(String homePerspective) {
        if (_currentRole != null) {
            policy.setHomePerspective(_currentRole,
                                      homePerspective);
        } else if (_currentGroup != null) {
            policy.setHomePerspective(_currentGroup,
                                      homePerspective);
        } else {
            policy.setHomePerspective(homePerspective);
        }
        return this;
    }

    public AuthorizationPolicy build() {
        return policy;
    }
}
