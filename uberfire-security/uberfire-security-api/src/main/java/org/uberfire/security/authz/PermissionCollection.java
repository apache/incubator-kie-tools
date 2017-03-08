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

import java.util.Collection;

/**
 * Interface representing a collection of Permission objects.
 * <p>
 * <p>With a PermissionCollection, you can:
 * <ul>
 * <li> add a permission to the collection using the {@code add} method.
 * <li> check to see if a particular permission is implied in the
 * collection, using the {@code implies} method.
 * <li> enumerate all the permissions, using the {@code elements} method.
 * </ul>
 * @see Permission
 */
public interface PermissionCollection {

    /**
     * Adds a permission object to the current collection of permission objects.
     * @param permissions the Permission objects to add.
     */
    PermissionCollection add(Permission... permissions);

    /**
     * Removes a permission object from the current collection of permission objects.
     * @param permissions the Permission objects to remove.
     */
    PermissionCollection remove(Permission... permissions);

    /**
     * Gets a permission object matching the specified name.
     * @param name The fully qualified name of the permission.
     * @return A Permission instance or null if not found.
     */
    Permission get(String name);

    /**
     * Returns all the Permission objects in the collection.
     * @return A collection of all the Permissions.
     */
    Collection<Permission> collection();

    /**
     * Checks to see if the specified permission is implied by
     * the collection of Permission objects held in this PermissionCollection.
     * @param permission the Permission object to compare.
     * @return true if "permission" is implied by the permissions in
     * the collection, false if not.
     */
    boolean implies(Permission permission);

    /**
     * It returns true provided this collection already contains a permission which implies by name
     * (See {@link Permission#impliesName(Permission)}) the permission passed as a parameter.
     * @param permission the permission to check
     * @return true if the permission name is implied by this collection, false otherwise.
     */
    boolean impliesName(Permission permission);

    /**
     * Get all the permissions from this collection and the given one and puts them
     * into a brand new collection instance. Any "redundant" permission instance
     * ("implied" by other permissions in the collection) are left out from the
     * outcome. Notice also, the {@code priority} parameter is taken into account in case
     * the same permission is present in both collections.
     * @param other the collection to merge.
     * @param priority integer indicating what to do if the same permission is found in both collections:
     * <ul>
     * <li>0 = same priority (GRANTED permissions win by default)</li>
     * <li>negative integer = this collection wins</li>
     * <li>positive integer = the other collection wins</li>
     * </ul>
     * @return A collection containing the merge result.
     */
    PermissionCollection merge(PermissionCollection other,
                               int priority);

    /**
     * Creates an exact copy of this instance.
     * @return A brand new Permission instance
     */
    PermissionCollection clone();
}
