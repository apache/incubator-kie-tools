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

import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.uberfire.security.authz.PermissionType;
import org.uberfire.security.authz.PermissionTypeRegistry;

@ApplicationScoped
public class DefaultPermissionTypeRegistry implements PermissionTypeRegistry {

    @Inject
    Instance<PermissionType> permissionTypeBeans;

    private Map<String, PermissionType> permissionTypes = new HashMap<>();
    private PermissionType defaultPermissionType = new DotNamedPermissionType("");

    @PostConstruct
    private void init() {
        for (PermissionType permissionType : permissionTypeBeans) {
            register(permissionType);
        }
    }

    @Override
    public void register(PermissionType instance) {
        if (permissionTypes.containsKey(instance.getType())) {
            throw new IllegalStateException("PermissionType already exists: " + instance.getType());
        }
        permissionTypes.put(instance.getType(),
                            instance);
    }

    @Override
    public PermissionType get(String type) {
        if (permissionTypes.containsKey(type)) {
            return permissionTypes.get(type);
        }
        return defaultPermissionType;
    }

    @Override
    public PermissionType resolve(String name) {
        for (PermissionType permissionType : permissionTypes.values()) {
            if (permissionType.supportsPermission(name)) {
                return permissionType;
            }
        }
        return defaultPermissionType;
    }
}
