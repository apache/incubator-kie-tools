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
 * Interface for representing access to a system resource.
 * All permissions have a name (whose interpretation depends on the subclass),
 * as well as some functions for defining the semantics of the
 * particular Permission subclass.
 */
public interface Permission {

    /**
     * A string literal that identifies a resource or set of resources this permission object relates to.
     * <p>It's up to every Permission implementation to define what's the set the name's format.</p>
     */
    String getName();

    /**
     * The authorization result or permission status.
     * @return One of the available results: GRANT / DENY / ABSTAIN
     */
    AuthorizationResult getResult();

    /**
     * Change the authorzation result.
     * @param result GRANT / DENY / ABSTAIN
     */
    void setResult(AuthorizationResult result);

    /**
     * Basically, "permission p1 implies permission p2" means that
     * if one is granted permission p1, one is naturally granted permission p2.
     * Thus, this is not an equality test, but rather more of a
     * subset test.
     * <p>
     * <p>Both calls to {@link #impliesName(Permission)} & {@link #impliesResult(Permission)} return true.</p>
     * @param other the permission to check against.
     * @return true if the specified permission is implied by this object, false if not.
     */
    boolean implies(Permission other);

    /**
     * If "permission p1 impliesName permission p2" means that the feature represented by p1 is a superset of p2.
     * @param other the permission to check against.
     * @return true if the specified permission name is implied by this object, false if not.
     */
    boolean impliesName(Permission other);

    /**
     * If "permission p1 impliesResult permission p2" means that both permissions give the same result.
     * @param other the permission to check against.
     * @return true if the specified permission result is implied by this object, false if not.
     */
    boolean impliesResult(Permission other);

    /**
     * Creates an exact copy of this instance.
     * @return A brand new Permission instance
     */
    Permission clone();
}
