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

import org.uberfire.security.Resource;
import org.uberfire.security.ResourceAction;
import org.uberfire.security.ResourceType;
import org.uberfire.security.authz.Permission;
import org.uberfire.security.authz.PermissionType;

public class DotNamedPermissionType implements PermissionType {

    private String type = null;

    public DotNamedPermissionType(String type) {
        this.type = type;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public boolean supportsPermission(String name) {
        return name == null || name.startsWith(type);
    }

    @Override
    public Permission createPermission(String name,
                                       boolean granted) {
        if (!supportsPermission(name)) {
            throw new IllegalArgumentException("The permission is not supported: " + name);
        }
        return new DotNamedPermission(name,
                                      granted);
    }

    @Override
    public Permission createPermission(ResourceType resourceType,
                                       ResourceAction action,
                                       boolean granted) {
        ResourceAction _action = action != null ? action : ResourceAction.READ;
        String name = buildPermissionName(resourceType,
                                          _action.getName().toLowerCase(),
                                          null);
        return createPermission(name,
                                granted);
    }

    @Override
    public Permission createPermission(Resource resource,
                                       ResourceAction action,
                                       boolean granted) {
        ResourceAction _action = action != null ? action : ResourceAction.READ;
        ResourceType type = resource != null ? resource.getResourceType() : null;
        String id = resource != null ? resource.getIdentifier() : null;
        String name = buildPermissionName(type,
                                          _action.getName().toLowerCase(),
                                          id);
        return createPermission(name,
                                granted);
    }

    @Override
    public String resolveResourceId(Permission permission) {
        String name = permission != null ? permission.getName() : null;
        if (name != null) {
            String[] s = name.split("\\.");
            if (s.length > 2) {
                String prefix = s[0] + "." + s[1] + ".";
                return name.substring(prefix.length());
            }
        }
        return null;
    }

    protected String buildPermissionName(ResourceType type,
                                         String action,
                                         String resourceId) {
        String name = "";
        if (type != null && !type.getName().equalsIgnoreCase(ResourceType.UNKNOWN.getName())) {
            name += type.getName();
        }
        if (action != null && action.trim().length() > 0) {
            name += (name.length() > 0 ? "." : "") + action;
        }
        if (resourceId != null && resourceId.trim().length() > 0) {
            name += (name.length() > 0 ? "." : "") + resourceId;
        }
        return name;
    }
}
