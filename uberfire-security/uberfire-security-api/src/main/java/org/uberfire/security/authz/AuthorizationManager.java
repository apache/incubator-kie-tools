/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

import org.jboss.errai.security.shared.api.identity.User;
import org.uberfire.security.Resource;
import org.uberfire.security.ResourceAction;
import org.uberfire.security.ResourceType;

/**
 * Main entry interface for querying the authorization management subsystem about
 * user access to different system resources.
 * <p>
 * <p>It provides services for checking access to {@link Resource} instances
 * as well as services to check if a given permission has been granted to a user.
 */
public interface AuthorizationManager {

    /**
     * Check if the specified user can "access" a given resource. The term "access"
     * refers to the ability to be able to reach, read or view a resource. For instance,
     * read a file, view an item in the UI, etc.</p>
     * <p>
     * <p>Notice the resource may have dependencies ({@link Resource#getDependencies()}) to
     * other resources, in such case the resource is only accessible if and only if one of
     * its dependent references is accessible too.</p>
     * @param resource The resource
     * @param user The user instance
     * @param votingStrategy The voting strategy to use when voting is required
     * (users with more than one role and/or group assigned).
     * @return true if access is granted, false otherwise.
     */
    boolean authorize(Resource resource,
                      User user,
                      VotingStrategy votingStrategy);

    /**
     * Check if the given action can be performed over the specified resource or any of its
     * dependent resource references (see {@link Resource#getDependencies}).
     * @param resource The resource instance to check
     * @param action The action to check. If null then the {@link #authorize(Resource, User)} method is invoked.
     * @param user The user instance
     * @param votingStrategy The voting strategy to use when voting is required
     * (users with more than one role and/or group assigned).
     * @return true if the action is granted, false otherwise.
     */
    boolean authorize(Resource resource,
                      ResourceAction action,
                      User user,
                      VotingStrategy votingStrategy);

    /**
     * Check if the given action can be performed over the specified resource or any of its
     * dependent resource references (see {@link Resource#getDependencies}).
     * @param resourceType The resource type to check
     * @param action The action to check.
     * @param user The user instance
     * @param votingStrategy The voting strategy to use when voting is required
     * (users with more than one role and/or group assigned).
     * @return true if the action is granted, false otherwise.
     */
    boolean authorize(ResourceType resourceType,
                      ResourceAction action,
                      User user,
                      VotingStrategy votingStrategy);

    /**
     * Check of the given permission has been granted to the user.
     * @param permission The name of the permission to check
     * @param user The user instance
     * @param votingStrategy The voting strategy to use when voting is required
     * (users with more than one role and/or group assigned).
     * @return true if the permission is granted, false otherwise.
     */
    boolean authorize(String permission,
                      User user,
                      VotingStrategy votingStrategy);

    /**
     * Check of the given permission has been granted to the user.
     * @param permission The name of the permission to check
     * @param user The user instance
     * @param votingStrategy The voting strategy to use when voting is required
     * (users with more than one role and/or group assigned).
     * @return true if the permission is granted, false otherwise.
     */
    boolean authorize(Permission permission,
                      User user,
                      VotingStrategy votingStrategy);

    /**
     * Creates a brand new {@link ResourceCheck} instance which provides a fluent styled API for
     * the checking of restricted actions over {@link Resource} instances.
     * <p>
     * <p>ExampleUsage: </p>
     * <pre>
     * {@code User user;
     *   Resource resource;
     *   AuthorizationManager authzManager;
     *
     *   boolean result = authzManager.check(resource, user)
     *      .granted(() -> System.out.println("Access granted"))
     *      .denied(() -> System.out.println("Access denied"))
     *      .result();
     * }
     * </pre>
     * @param resource The resource to check
     * @param votingStrategy The voting strategy to use when voting is required
     * (users with more than one role and/or group assigned).
     * @return A {@link AuthorizationCheck} instance.
     */
    ResourceCheck check(Resource resource,
                        User user,
                        VotingStrategy votingStrategy);

    /**
     * Creates a brand new {@link ResourceCheck} instance which provides a fluent styled API for
     * the checking of restricted actions over a {@link ResourceType}.
     * <p>
     * <p>ExampleUsage: </p>
     * <pre>
     * {@code User user;
     *   AuthorizationManager authzManager;
     *
     *   boolean result = authzManager.check(ActivityResourceType.PERSPECTIVE, user)
     *      .granted(() -> System.out.println("Access granted"))
     *      .denied(() -> System.out.println("Access denied"))
     *      .result();
     * }
     * </pre>
     * @param resourceType The resource type to check
     * @param votingStrategy The voting strategy to use when voting is required
     * (users with more than one role and/or group assigned).
     * @return A {@link AuthorizationCheck} instance.
     */
    ResourceCheck check(ResourceType resourceType,
                        User user,
                        VotingStrategy votingStrategy);

    /**
     * Creates a brand new {@link PermissionCheck} instance which provides a
     * fluent styled API for checking permissions.
     * <p>
     * <p>ExampleUsage: </p>
     * <pre>
     * {@code User user;
     *   AuthorizationManager authzManager;
     *
     *   boolean result = authzManager.check("myfeature", user)
     *      .granted(() -> System.out.println("Access granted"))
     *      .denied(() -> System.out.println("Access denied"))
     *      .result();
     * }
     * </pre>
     * @param permission The name of the permission to check
     * @param votingStrategy The voting strategy to use when voting is required
     * (users with more than one role and/or group assigned).
     * @return A {@link AuthorizationCheck} instance.
     */
    PermissionCheck check(String permission,
                          User user,
                          VotingStrategy votingStrategy);

    /**
     * It redirects to {@link #authorize(Resource, User, VotingStrategy)}
     * using the default voting strategy defined at {@link PermissionManager}.
     */
    boolean authorize(Resource resource,
                      User user);

    /**
     * It redirects to {@link #authorize(Resource, ResourceAction, User)}
     * using the default voting strategy defined at {@link PermissionManager}.
     */
    boolean authorize(Resource resource,
                      ResourceAction action,
                      User user);

    /**
     * It redirects to {@link #authorize(ResourceType, ResourceAction, User, VotingStrategy)}
     * using the default voting strategy defined at {@link PermissionManager}.
     */
    boolean authorize(ResourceType resourceType,
                      ResourceAction action,
                      User user);

    /**
     * It redirects to {@link #authorize(String, User, VotingStrategy)}
     * using the default voting strategy defined at {@link PermissionManager}.
     */
    boolean authorize(String permission,
                      User user);

    /**
     * It redirects to {@link #authorize(Permission, User, VotingStrategy)}
     * using the default voting strategy defined at {@link PermissionManager}.
     */
    boolean authorize(Permission permission,
                      User user);

    /**
     * It redirects to {@link #check(Resource, User, VotingStrategy)}
     * using the default voting strategy defined at {@link PermissionManager}.
     */
    ResourceCheck check(Resource resource,
                        User user);

    /**
     * It redirects to {@link #check(ResourceType, User, VotingStrategy)}
     * using the default voting strategy defined at {@link PermissionManager}.
     */
    ResourceCheck check(ResourceType type,
                        User user);

    /**
     * It redirects to {@link #check(String, User, VotingStrategy)}
     * using the default voting strategy defined at {@link PermissionManager}.
     */
    PermissionCheck check(String permission,
                          User user);

    /**
     * Invalidate user related authorization data cached
     * @param user user to invalidate cache
     */
    void invalidate(final User user);
}
