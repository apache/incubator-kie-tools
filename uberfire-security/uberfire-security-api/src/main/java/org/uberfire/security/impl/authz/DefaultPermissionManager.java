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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.security.shared.api.Group;
import org.jboss.errai.security.shared.api.Role;
import org.jboss.errai.security.shared.api.identity.User;
import org.uberfire.security.Resource;
import org.uberfire.security.ResourceAction;
import org.uberfire.security.ResourceType;
import org.uberfire.security.authz.AuthorizationPolicy;
import org.uberfire.security.authz.AuthorizationResult;
import org.uberfire.security.authz.Permission;
import org.uberfire.security.authz.PermissionCollection;
import org.uberfire.security.authz.PermissionManager;
import org.uberfire.security.authz.PermissionType;
import org.uberfire.security.authz.PermissionTypeRegistry;
import org.uberfire.security.authz.VotingAlgorithm;
import org.uberfire.security.authz.VotingStrategy;

import static org.uberfire.security.authz.AuthorizationResult.ACCESS_ABSTAIN;
import static org.uberfire.security.authz.AuthorizationResult.ACCESS_DENIED;
import static org.uberfire.security.authz.AuthorizationResult.ACCESS_GRANTED;

@ApplicationScoped
public class DefaultPermissionManager implements PermissionManager {

    private PermissionTypeRegistry permissionTypeRegistry;
    private AuthorizationPolicy authorizationPolicy = new DefaultAuthorizationPolicy();
    private DefaultAuthzResultCache cache;
    private VotingStrategy defaultVotingStrategy = VotingStrategy.PRIORITY;
    private Map<VotingStrategy, VotingAlgorithm> votingAlgorithmMap = new HashMap<>();
    private Map<String, PermissionCollection> permissionCollectionCache = new HashMap<>();

    @Inject
    public DefaultPermissionManager(PermissionTypeRegistry permissionTypeRegistry) {
        this(permissionTypeRegistry,
             new DefaultAuthzResultCache());
    }

    public DefaultPermissionManager() {
        this(new DefaultPermissionTypeRegistry(),
             new DefaultAuthzResultCache());
    }

    public DefaultPermissionManager(PermissionTypeRegistry permissionTypeRegistry,
                                    DefaultAuthzResultCache cache) {
        this.permissionTypeRegistry = permissionTypeRegistry;
        this.cache = cache;
        setVotingAlgorithm(VotingStrategy.AFFIRMATIVE,
                           new AffirmativeBasedVoter());
        setVotingAlgorithm(VotingStrategy.CONSENSUS,
                           new ConsensusBasedVoter());
        setVotingAlgorithm(VotingStrategy.UNANIMOUS,
                           new UnanimousBasedVoter());
    }

    public AuthorizationPolicy getAuthorizationPolicy() {
        return authorizationPolicy;
    }

    public void setAuthorizationPolicy(AuthorizationPolicy authorizationPolicy) {
        this.authorizationPolicy = authorizationPolicy != null ? authorizationPolicy : new DefaultAuthorizationPolicy();
        this.cache.clear();
        this.permissionCollectionCache.clear();
    }

    @Override
    public AuthorizationPolicyBuilder newAuthorizationPolicy() {
        return new AuthorizationPolicyBuilder(permissionTypeRegistry);
    }

    @Override
    public VotingStrategy getDefaultVotingStrategy() {
        return defaultVotingStrategy;
    }

    @Override
    public void setDefaultVotingStrategy(VotingStrategy votingStrategy) {
        defaultVotingStrategy = votingStrategy;
    }

    public VotingAlgorithm getVotingAlgorithm(VotingStrategy votingStrategy) {
        return votingAlgorithmMap.get(votingStrategy);
    }

    public void setVotingAlgorithm(VotingStrategy votingStrategy,
                                   VotingAlgorithm votingAlgorithm) {
        votingAlgorithmMap.put(votingStrategy,
                               votingAlgorithm);
    }

    @Override
    public Permission createPermission(String name,
                                       boolean granted) {
        PermissionType permissionType = permissionTypeRegistry.resolve(name);
        return permissionType.createPermission(name,
                                               granted);
    }

    @Override
    public Permission createPermission(Resource resource,
                                       ResourceAction action,
                                       boolean granted) {

        // Does the resource have a type?

        // YES => check the resource action f.i: "project.read.myprojectid"
        if (resource.getResourceType() != null && !resource.isType(ResourceType.UNKNOWN.getName())) {
            PermissionType permissionType = permissionTypeRegistry.resolve(resource.getResourceType().getName());
            return permissionType.createPermission(resource,
                                                   action,
                                                   granted);
        }
        // NO => just check the resource identifier
        return createPermission(resource.getIdentifier(),
                                granted);
    }

    @Override
    public Permission createPermission(ResourceType resourceType,
                                       ResourceAction action,
                                       boolean granted) {
        PermissionType permissionType = permissionTypeRegistry.resolve(resourceType.getName());
        return permissionType.createPermission(resourceType,
                                               action,
                                               granted);
    }

    @Override
    public AuthorizationResult checkPermission(Permission permission,
                                               User user) {
        return checkPermission(permission,
                               user,
                               defaultVotingStrategy);
    }

    @Override
    public AuthorizationResult checkPermission(Permission permission,
                                               User user,
                                               VotingStrategy votingStrategy) {

        if (authorizationPolicy == null || permission == null) {
            return ACCESS_ABSTAIN;
        }
        AuthorizationResult result = cache.get(user,
                                               permission);
        if (result == null) {
            result = _checkPermission(permission,
                                      user,
                                      votingStrategy == null ? defaultVotingStrategy : votingStrategy);
            cache.put(user,
                      permission,
                      result);
        }
        return result;
    }

    protected AuthorizationResult _checkPermission(Permission permission,
                                                   User user,
                                                   VotingStrategy votingStrategy) {

        if (VotingStrategy.PRIORITY.equals(votingStrategy)) {
            PermissionCollection userPermissions = resolvePermissions(user,
                                                                      VotingStrategy.PRIORITY);
            return _checkPermission(permission,
                                    userPermissions);
        } else {
            List<AuthorizationResult> permList = _checkRoleAndGroupPermissions(permission,
                                                                               user);
            VotingAlgorithm votingAlgorithm = votingAlgorithmMap.get(votingStrategy);
            return votingAlgorithm.vote(permList);
        }
    }

    protected List<AuthorizationResult> _checkRoleAndGroupPermissions(Permission permission,
                                                                      User user) {
        List<AuthorizationResult> result = new ArrayList<>();
        if (user.getRoles() != null) {
            for (Role role : user.getRoles()) {
                PermissionCollection collection = authorizationPolicy.getPermissions(role);
                AuthorizationResult _partialResult = _checkPermission(permission,
                                                                      collection);
                result.add(_partialResult);
            }
        }
        if (user.getGroups() != null) {
            for (Group group : user.getGroups()) {
                PermissionCollection collection = authorizationPolicy.getPermissions(group);
                AuthorizationResult _partialResult = _checkPermission(permission,
                                                                      collection);
                result.add(_partialResult);
            }
        }
        return result;
    }

    protected AuthorizationResult _checkPermission(Permission permission,
                                                   PermissionCollection collection) {
        if (collection == null) {
            return ACCESS_ABSTAIN;
        }
        Permission existing = collection.get(permission.getName());
        if (existing != null) {
            return existing.getResult().equals(permission.getResult()) ? ACCESS_GRANTED : ACCESS_DENIED;
        }
        if (collection.implies(permission)) {
            return ACCESS_GRANTED;
        }
        Permission inverted = permission.clone();
        inverted.setResult(inverted.getResult().invert());
        if (collection.implies(inverted)) {
            return ACCESS_DENIED;
        }
        return ACCESS_ABSTAIN;
    }

    @Override
    public String resolveResourceId(Permission permission) {
        PermissionType permissionType = permissionTypeRegistry.resolve(permission.getName());
        return permissionType.resolveResourceId(permission);
    }

    @Override
    public PermissionCollection resolvePermissions(User user,
                                                   VotingStrategy votingStrategy) {
        if (user == null) {
            return new DefaultPermissionCollection();
        }
        switch (votingStrategy) {
            case AFFIRMATIVE:
                return resolvePermissionsAffirmative(user);
            case CONSENSUS:
                return resolvePermissionsConsensus(user);
            case UNANIMOUS:
                return resolvePermissionsUnanimous(user);
            default:
                return resolvePermissionsPriority(user);
        }
    }

    @Override
    public void invalidate(final User user) {
        cache.invalidate(user);
    }

    private PermissionCollection resolvePermissionsAffirmative(User user) {
        // TODO
        PermissionCollection result = new DefaultPermissionCollection();
        return result;
    }

    private PermissionCollection resolvePermissionsConsensus(User user) {
        // TODO
        PermissionCollection result = new DefaultPermissionCollection();
        return result;
    }

    private PermissionCollection resolvePermissionsUnanimous(User user) {
        // TODO
        PermissionCollection result = new DefaultPermissionCollection();
        return result;
    }

    /**
     * Get all the permissions assigned to any of the user's roles/groups plus the default permissions
     * ({@link AuthorizationPolicy#getPermissions()}) and it creates a single permission collection where
     * the permission are added by priority.
     *
     * @param user The target user
     * @return An unified permission collection
     */
    private PermissionCollection resolvePermissionsPriority(User user) {
        if (authorizationPolicy == null) {
            return null;
        }

        if (permissionCollectionCache.containsKey(user.getIdentifier())) {
            return permissionCollectionCache.get(user.getIdentifier());
        }
        // Get the default permissions as lowest priority
        PermissionCollection result = authorizationPolicy.getPermissions();
        int[] priority = new int[]{Integer.MIN_VALUE};

        // Overwrite the default permissions with those defined for the user's roles & groups
        result = mergeRolePermissions(user,
                                      result,
                                      priority);
        result = mergeGroupPermissions(user,
                                       result,
                                       priority);
        permissionCollectionCache.put(user.getIdentifier(), result);
        return result;
    }

    /**
     * Merge the target collection with the permissions assigned to the given user's roles
     */
    private PermissionCollection mergeRolePermissions(User user,
                                                      PermissionCollection target,
                                                      int[] lastPriority) {
        PermissionCollection result = target;
        if (user.getRoles() != null) {
            for (Role role : user.getRoles()) {
                PermissionCollection collection = authorizationPolicy.getPermissions(role);
                int priority = authorizationPolicy.getPriority(role);
                int comparator = resolve(priority,
                                         lastPriority[0]);
                result = result.merge(collection,
                                      comparator);
                if (priority > lastPriority[0]) {
                    lastPriority[0] = priority;
                }
            }
        }
        return result;
    }

    /**
     * Merge the target collection with the permissions assigned to the given user's groups
     */
    private PermissionCollection mergeGroupPermissions(User user,
                                                       PermissionCollection target,
                                                       int[] lastPriority) {
        PermissionCollection result = target;
        if (user.getGroups() != null) {
            for (Group group : user.getGroups()) {
                PermissionCollection collection = authorizationPolicy.getPermissions(group);
                int priority = authorizationPolicy.getPriority(group);
                int comparator = resolve(priority,
                                         lastPriority[0]);
                result = result.merge(collection,
                                      comparator);
                if (priority > lastPriority[0]) {
                    lastPriority[0] = priority;
                }
            }
        }
        return result;
    }

    private int resolve(int p1,
                        int p2) {
        if (p1 == p2) {
            return 0;
        }
        if (p1 > p2) {
            return 1;
        }
        return -1;
    }
}
