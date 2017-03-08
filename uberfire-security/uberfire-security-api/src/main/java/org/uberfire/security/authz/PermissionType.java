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

import org.uberfire.security.Resource;
import org.uberfire.security.ResourceAction;
import org.uberfire.security.ResourceType;

/**
 * A PermissionType provides factory services for the creation of permission instances
 * as well as services for controlling the access to Resource instances.
 */
public interface PermissionType {

    /**
     * An string identifier that acts as a unique identifier for the permission type.
     * @return The permission type unique identifier
     */
    String getType();

    /**
     * Checks if the given permission name is supported by this type. That means basically that
     * such name does follow a specific nomenclature for the formatting of its permission names.
     * @param name The permission name to check
     * @return true is such permission is supported or false otherwise.
     */
    boolean supportsPermission(String name);

    /**
     * Creates a permission instance.
     * @param name The name of the permission to create.
     * @param granted true=granted, false=denied
     * @return A permission instance
     */
    Permission createPermission(String name,
                                boolean granted);

    /**
     * Creates a permission instance representing a "global" action that can be applied to any resource instance
     * (for instance, "edit an invoice") or an action that is not related to any specific instance (for instance,
     * "create a new invoice")
     * @param resourceType The resource type
     * @param action The action to check. If null then an "access" permission is created.
     * The term access refers to the ability to reach, read, view ... the resource, depending on the resource type.
     * @return A permission instance
     */
    Permission createPermission(ResourceType resourceType,
                                ResourceAction action,
                                boolean granted);

    /**
     * Creates a permission instance representing an action on a given resource..
     * @param resource The resource instance
     * @param action The action to check. If null then an "access" permission is created.
     * The term access refers to the ability to reach, read, view ... the resource, depending on the resource type.
     * @return A permission instance
     */
    Permission createPermission(Resource resource,
                                ResourceAction action,
                                boolean granted);

    /**
     * Given a permission it tries to determine what is the resource the permission refers to.
     * <p>
     * <p>The resolution mechanism works only if the permission instance was created by a previous call
     * to {@link #createPermission(Resource, ResourceAction, boolean)}. In such case the identifier of the
     * {@link Resource} instance is the value returned.</p>
     * @param permission The permission which resource id. has to be inferred.
     * @return A resource id. or null if it can bot be inferred.
     */
    String resolveResourceId(Permission permission);
}