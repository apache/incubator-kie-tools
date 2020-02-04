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
package org.uberfire.client.auth;

import javax.inject.Singleton;

import org.jboss.errai.bus.server.annotations.ShadowService;
import org.jboss.errai.security.shared.api.Group;
import org.uberfire.backend.authz.AuthorizationService;
import org.uberfire.security.authz.AuthorizationPolicy;
import org.uberfire.security.impl.authz.DefaultAuthorizationPolicy;

/**
 * Local authorization service, shadowing the remote endpoint which doesn't
 * exist for this client-only application.
 */
@Singleton
@ShadowService
public class ShadowedAuthorizationService implements AuthorizationService {

    private AuthorizationPolicy policy;

    @Override
    public AuthorizationPolicy loadPolicy() {
        return (policy == null) ? new DefaultAuthorizationPolicy() : policy;
    }

    @Override
    public void savePolicy(AuthorizationPolicy policy) {
        this.policy = policy;
    }

    @Override
    public void deletePolicyByGroup(Group group , AuthorizationPolicy policy) {
        this.policy = policy;
    }
}
