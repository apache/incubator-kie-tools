/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.security.shared.api.Group;
import org.jboss.errai.security.shared.api.Role;
import org.uberfire.security.authz.PermissionCollection;

@Portable
public class DefaultAuthorizationEntry {

    public static final int DEFAULT_PRIORITY = -100;
    private String description = null;
    private Role role = null;
    private Group group = null;
    private Integer priority = DEFAULT_PRIORITY;
    private String homePerspective = null;
    private PermissionCollection permissions = new DefaultPermissionCollection();

    public DefaultAuthorizationEntry() {
    }

    public DefaultAuthorizationEntry(Role role) {
        this.role = role;
    }

    public DefaultAuthorizationEntry(Group group) {
        this.group = group;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getHomePerspective() {
        return homePerspective;
    }

    public void setHomePerspective(String homePerspective) {
        this.homePerspective = homePerspective;
    }

    public PermissionCollection getPermissions() {
        return permissions;
    }

    public void setPermissions(PermissionCollection permissions) {
        this.permissions = permissions;
    }

    public DefaultAuthorizationEntry cloneInstance() {
        DefaultAuthorizationEntry clone = new DefaultAuthorizationEntry();
        clone.description = description;
        clone.role = role;
        clone.group = group;
        clone.priority = priority;
        clone.homePerspective = homePerspective;
        clone.permissions = permissions.clone();
        return clone;
    }

    @Override
    public String toString() {
        StringBuilder out = new StringBuilder();
        if (role != null) {
            out.append(role.getName()).append(" ");
        }
        if (group != null) {
            out.append(group.getName()).append(" ");
        }
        if (priority != 0) {
            out.append(priority).append(" ");
        }
        if (homePerspective != null) {
            out.append(homePerspective).append(" ");
        }
        out.append("\n");
        out.append(permissions);
        return out.toString();
    }
}
