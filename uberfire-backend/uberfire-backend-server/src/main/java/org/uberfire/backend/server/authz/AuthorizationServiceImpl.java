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
package org.uberfire.backend.server.authz;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.jboss.errai.bus.server.annotations.Service;
import org.jboss.errai.security.shared.api.Group;
import org.uberfire.backend.authz.AuthorizationPolicyStorage;
import org.uberfire.backend.authz.AuthorizationService;
import org.uberfire.backend.events.AuthorizationPolicySavedEvent;
import org.uberfire.security.authz.AuthorizationPolicy;
import org.uberfire.security.authz.PermissionManager;

@Service
@ApplicationScoped
public class AuthorizationServiceImpl implements AuthorizationService {

    private AuthorizationPolicyStorage storage;

    private PermissionManager permissionManager;

    private Event<AuthorizationPolicySavedEvent> savedEvent;

    @Inject
    public AuthorizationServiceImpl(final AuthorizationPolicyStorage storage, final PermissionManager permissionManager, final Event<AuthorizationPolicySavedEvent> savedEvent) {
        this.storage = storage;
        this.permissionManager = permissionManager;
        this.savedEvent = savedEvent;
    }

    @Override
    public AuthorizationPolicy loadPolicy() {
        return storage.loadPolicy();
    }

    @Override
    public void savePolicy(AuthorizationPolicy policy) {
        storage.savePolicy(policy);
        permissionManager.setAuthorizationPolicy(policy);
        savedEvent.fire(new AuthorizationPolicySavedEvent(policy));
    }

    @Override
    public void deletePolicyByGroup(Group group, AuthorizationPolicy policy) {
        storage.deletePolicyByGroup(group, policy);
        AuthorizationPolicy newPolicy = storage.loadPolicy();
        permissionManager.setAuthorizationPolicy(newPolicy);
        savedEvent.fire(new AuthorizationPolicySavedEvent(newPolicy));
    }
}
