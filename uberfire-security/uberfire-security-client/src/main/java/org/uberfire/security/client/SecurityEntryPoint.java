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

package org.uberfire.security.client;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.ioc.client.api.EntryPoint;
import org.uberfire.backend.authz.AuthorizationService;
import org.uberfire.backend.events.AuthorizationPolicySavedEvent;
import org.uberfire.security.authz.AuthorizationPolicy;
import org.uberfire.security.authz.PermissionManager;

@EntryPoint
@ApplicationScoped
public class SecurityEntryPoint {

    @Inject
    private Caller<AuthorizationService> authorizationService;

    @Inject
    private PermissionManager permissionManager;

    @PostConstruct
    public void init() {
        authorizationService.call(
                (AuthorizationPolicy p) -> {
                    permissionManager.setAuthorizationPolicy(p);
                }
        ).loadPolicy();
    }

    public void onPolicySaved(@Observes AuthorizationPolicySavedEvent event) {
        AuthorizationPolicy policy = event.getPolicy();
        permissionManager.setAuthorizationPolicy(policy);
    }
}
