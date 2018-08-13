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

import org.jboss.errai.security.shared.api.Group;
import org.jboss.errai.security.shared.api.Role;
import org.jboss.errai.security.shared.api.identity.User;
import org.uberfire.security.Resource;
import org.uberfire.security.ResourceAction;
import org.uberfire.security.ResourceType;
import org.uberfire.security.impl.authz.AuthorizationPolicyBuilder;

/**
 * This is the main interface for checking permissions against User instances.
 * <p>
 * <p>This interface is backed by an {@code AuthorizationPolicy} instance which
 * holds all the permissions declarations.</p>
 * <p>
 * <p>Example:
 * <pre>
 *     &#064;Inject
 *     PermissionManager permissionManager;
 *
 *     &#064;Inject
 *     User user;
 *
 *     public boolean checkPermission() {
 *         Permission permission = permissionManager.createPermission("perspective.read.Home", true);
 *         return permissionManager.checkPermission(permission, user);
 *     }
 * </pre>
 * </p>
 */
public interface PermissionManager {

    /**
     * Gets a builder reference in order to initialize a brand new AuthorizationPolicy instance.
     */
    AuthorizationPolicyBuilder newAuthorizationPolicy();

    /**
     * Gets the current authorization policy instance set.
     */
    AuthorizationPolicy getAuthorizationPolicy();

    /**
     * Changes the current authorization policy instance.
     */
    void setAuthorizationPolicy(AuthorizationPolicy authorizationPolicy);

    /**
     * Gets the default voting strategy.
     * @return A {@link VotingStrategy} instance
     */
    VotingStrategy getDefaultVotingStrategy();

    /**
     * Set the default voting strategy to apply when checking permissions for users who have
     * more than one role and/or group assigned.
     * @param votingStrategy The voting strategy to apply when calling to
     * {@link #checkPermission(Permission, User)}
     */
    void setDefaultVotingStrategy(VotingStrategy votingStrategy);

    /**
     * Gets the {@link VotingAlgorithm} implementation associated with the specified {@link VotingStrategy}.
     * @param votingStrategy The voting strategy
     * @return The voting algorithm instance
     */
    VotingAlgorithm getVotingAlgorithm(VotingStrategy votingStrategy);

    /**
     * Sets the {@link VotingAlgorithm} implementation to be used every time the given {@link VotingStrategy} is applied.
     * @param votingStrategy The voting strategy
     * @param votingAlgorithm The voting algorithm to apply when calling to {@link #checkPermission(Permission, User, VotingStrategy)}
     * with the proper voting strategy.
     */
    void setVotingAlgorithm(VotingStrategy votingStrategy,
                            VotingAlgorithm votingAlgorithm);

    /**
     * Creates a permission instance.
     * @param name The name of the permission to create
     * @param granted true=granted, false=denied
     * @return A brand new permission instance
     */
    Permission createPermission(String name,
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
     * Creates a permission instance representing an action on a given resource..
     * @param resourceType The resource type
     * @param action The action to check. If null then an "access" permission is created.
     * The term access refers to the ability to reach, read, view ... the resource, depending on the resource type.
     * @return A permission instance
     */
    Permission createPermission(ResourceType resourceType,
                                ResourceAction action,
                                boolean granted);

    /**
     * Check if the given permission is granted to the specified user.
     * <p>
     * <p>NOTE: If voting is required (users with more than one role and/or group assigned) then
     * the default voting strategy is used</p>
     * @param permission The permission to check
     * @param user The user instance
     * @return The authorization result: GRANTED / DENIED / ABSTAIN
     * @see AuthorizationResult
     */
    AuthorizationResult checkPermission(Permission permission,
                                        User user);

    /**
     * Check if the given permission is granted to the specified user.
     * @param permission The permission to check
     * @param user The user instance
     * @param votingStrategy The voting strategy to use when voting is required
     * (users with more than one role and/or group assigned).
     * If null then the default voting strategy is used.
     * @return The authorization result: GRANTED / DENIED / ABSTAIN
     */
    AuthorizationResult checkPermission(Permission permission,
                                        User user,
                                        VotingStrategy votingStrategy);

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

    /**
     * Get the permissions assigned to a given user.
     * <p>
     * <p>Usually, the user's permissions is obtained by mixing all the permissions assigned
     * to each role and group instance the user belongs to.</p>
     * <p>
     * <p>Every interface implementation must take into account the voting strategy specified,
     * which is used to resolve permission collision.</p>
     * @param user The user instance
     * @param votingStrategy The voting strategy
     * @return The permission collection
     * @see AuthorizationPolicy#getPriority(Role)
     * @see AuthorizationPolicy#getPriority(Group)
     */
    PermissionCollection resolvePermissions(User user,
                                            VotingStrategy votingStrategy);

    /**
     * Invalidate user related authorization data cached
     * @param user user to invalidate cache
     */
    void invalidate(final User user);
}
