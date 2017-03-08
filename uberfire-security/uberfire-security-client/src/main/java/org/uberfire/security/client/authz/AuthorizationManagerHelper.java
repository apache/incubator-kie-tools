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

package org.uberfire.security.client.authz;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.container.IOC;
import org.jboss.errai.security.shared.api.identity.User;
import org.uberfire.security.Resource;
import org.uberfire.security.ResourceAction;
import org.uberfire.security.ResourceType;
import org.uberfire.security.authz.AuthorizationCheck;
import org.uberfire.security.authz.AuthorizationManager;
import org.uberfire.security.authz.Permission;
import org.uberfire.security.authz.VotingStrategy;
import org.uberfire.security.impl.authz.DefaultResourceAction;
import org.uberfire.security.impl.authz.DefaultResourceType;

@ApplicationScoped
public class AuthorizationManagerHelper {

    private AuthorizationManager authorizationManager;
    private User user;
    @Inject
    public AuthorizationManagerHelper(AuthorizationManager authorizationManager,
                                      User user) {
        this.authorizationManager = authorizationManager;
        this.user = user;
    }

    private static AuthorizationManagerHelper get() {
        return IOC.getBeanManager().lookupBean(AuthorizationManagerHelper.class).getInstance();
    }

    public static boolean authorize(Resource resource) {
        AuthorizationManagerHelper helper = get();
        return helper.getAuthorizationManager().authorize(resource,
                                                          helper.getUser());
    }

    public static boolean authorize(Resource resource,
                                    String action) {
        AuthorizationManagerHelper helper = get();
        return helper.getAuthorizationManager().authorize(resource,
                                                          new DefaultResourceAction(action),
                                                          helper.getUser());
    }

    public static boolean authorize(Resource resource,
                                    ResourceAction action) {
        AuthorizationManagerHelper helper = get();
        return helper.getAuthorizationManager().authorize(resource,
                                                          action,
                                                          helper.getUser());
    }

    public static boolean authorize(String resourceType,
                                    String action) {
        return authorize(new DefaultResourceType(resourceType),
                         new DefaultResourceAction(action));
    }

    public static boolean authorize(ResourceType resourceType,
                                    ResourceAction action) {
        AuthorizationManagerHelper helper = get();
        return helper.getAuthorizationManager().authorize(resourceType,
                                                          action,
                                                          helper.getUser());
    }

    public static boolean authorize(Resource resource,
                                    VotingStrategy votingStrategy) {
        AuthorizationManagerHelper helper = get();
        return helper.getAuthorizationManager().authorize(resource,
                                                          helper.getUser(),
                                                          votingStrategy);
    }

    public static boolean authorize(Resource resource,
                                    ResourceAction action,
                                    VotingStrategy votingStrategy) {
        AuthorizationManagerHelper helper = get();
        return helper.getAuthorizationManager().authorize(resource,
                                                          action,
                                                          helper.getUser(),
                                                          votingStrategy);
    }

    public static boolean authorize(ResourceType resourceType,
                                    ResourceAction action,
                                    VotingStrategy votingStrategy) {
        AuthorizationManagerHelper helper = get();
        return helper.getAuthorizationManager().authorize(resourceType,
                                                          action,
                                                          helper.getUser(),
                                                          votingStrategy);
    }

    public static boolean authorize(String permission) {
        AuthorizationManagerHelper helper = get();
        return helper.getAuthorizationManager().authorize(permission,
                                                          helper.getUser());
    }

    public static boolean authorize(Permission permission) {
        AuthorizationManagerHelper helper = get();
        return helper.getAuthorizationManager().authorize(permission,
                                                          helper.getUser());
    }

    public static boolean authorize(String permission,
                                    VotingStrategy votingStrategy) {
        AuthorizationManagerHelper helper = get();
        return helper.getAuthorizationManager().authorize(permission,
                                                          helper.getUser(),
                                                          votingStrategy);
    }

    public static boolean authorize(Permission permission,
                                    VotingStrategy votingStrategy) {
        AuthorizationManagerHelper helper = get();
        return helper.getAuthorizationManager().authorize(permission,
                                                          helper.getUser(),
                                                          votingStrategy);
    }

    public static AuthorizationCheck check(Resource target) {
        AuthorizationManagerHelper helper = get();
        return helper.getAuthorizationManager().check(target,
                                                      helper.getUser());
    }

    public static AuthorizationCheck check(Resource target,
                                           VotingStrategy votingStrategy) {
        AuthorizationManagerHelper helper = get();
        return helper.getAuthorizationManager().check(target,
                                                      helper.getUser(),
                                                      votingStrategy);
    }

    public static AuthorizationCheck check(String permission) {
        AuthorizationManagerHelper helper = get();
        return helper.getAuthorizationManager().check(permission,
                                                      helper.getUser());
    }

    public static AuthorizationCheck check(String permission,
                                           VotingStrategy votingStrategy) {
        AuthorizationManagerHelper helper = get();
        return helper.getAuthorizationManager().check(permission,
                                                      helper.getUser(),
                                                      votingStrategy);
    }

    public AuthorizationManager getAuthorizationManager() {
        return authorizationManager;
    }

    public User getUser() {
        return user;
    }
}
