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
package org.uberfire.security.authz;

/**
 * A registry of permission types. Every type is uniquely identified in the registry.
 */
public interface PermissionTypeRegistry {

    /**
     * Register a permission type. The {@link PermissionType#getType()} is used as a unique key within the registry.
     */
    void register(PermissionType type);

    /**
     * Get a previously registed permission type given its unique identifier.
     */
    PermissionType get(String type);

    /**
     * Get the first permission type that matches the given name.
     * <p>
     * <p>It's always up to every permission type to define what are the valid format for its permission names.</p>
     * @see PermissionType#supportsPermission(String)
     */
    PermissionType resolve(String name);
}
