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

package org.uberfire.security.impl.authz;

import org.jboss.errai.security.shared.api.identity.User;
import org.uberfire.mvp.Command;
import org.uberfire.security.Resource;
import org.uberfire.security.ResourceAction;
import org.uberfire.security.ResourceType;
import org.uberfire.security.authz.AuthorizationManager;
import org.uberfire.security.authz.ResourceCheck;
import org.uberfire.security.authz.VotingStrategy;

public class ResourceCheckImpl<C extends ResourceCheckImpl> implements ResourceCheck<C> {

    protected AuthorizationManager authorizationManager;
    protected Resource resource;
    protected ResourceAction resourceAction = ResourceAction.READ;
    protected ResourceType resourceType;
    protected User user;
    protected VotingStrategy votingStrategy;
    protected Boolean result = null;

    public ResourceCheckImpl(AuthorizationManager authorizationManager,
                             Resource resource,
                             User user) {
        this.authorizationManager = authorizationManager;
        this.resource = resource;
        this.user = user;
    }

    public ResourceCheckImpl(AuthorizationManager authorizationManager,
                             ResourceType resourceType,
                             User user) {
        this.authorizationManager = authorizationManager;
        this.resourceType = resourceType;
        this.user = user;
    }

    public ResourceCheckImpl(AuthorizationManager authorizationManager,
                             Resource resource,
                             User user,
                             VotingStrategy votingStrategy) {
        this.authorizationManager = authorizationManager;
        this.resource = resource;
        this.user = user;
        this.votingStrategy = votingStrategy;
    }

    public ResourceCheckImpl(AuthorizationManager authorizationManager,
                             ResourceType resourceType,
                             User user,
                             VotingStrategy votingStrategy) {
        this.authorizationManager = authorizationManager;
        this.resourceType = resourceType;
        this.user = user;
        this.votingStrategy = votingStrategy;
    }

    protected void check(ResourceAction action) {
        if (votingStrategy == null) {
            if (resource == null) {
                result = authorizationManager.authorize(resourceType,
                                                        action,
                                                        user);
            } else {
                result = authorizationManager.authorize(resource,
                                                        action,
                                                        user);
            }
        } else {
            if (resource == null) {
                result = authorizationManager.authorize(resourceType,
                                                        action,
                                                        user,
                                                        votingStrategy);
            } else {
                result = authorizationManager.authorize(resource,
                                                        action,
                                                        user,
                                                        votingStrategy);
            }
        }
    }

    @Override
    public C action(ResourceAction action) {
        resourceAction = action;
        return (C) this;
    }

    @Override
    public C granted(Command onGranted) {
        if (result()) {
            onGranted.execute();
        }
        return (C) this;
    }

    @Override
    public C denied(Command onDenied) {
        if (!result()) {
            onDenied.execute();
        }
        return (C) this;
    }

    @Override
    public boolean result() {
        if (result == null) {
            check(resourceAction);
        }
        return result;
    }
}