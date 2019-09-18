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
package org.uberfire.backend.events;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.backend.authz.AuthorizationPolicyStorage;
import org.uberfire.commons.clusterapi.Clustered;
import org.uberfire.security.authz.AuthorizationPolicy;

/**
 * Event fired just after the security policy is saved. See
 * {@link AuthorizationPolicyStorage#savePolicy(AuthorizationPolicy)}
 */
@Portable
@Clustered
public class AuthorizationPolicySavedEvent {

    private AuthorizationPolicy policy;

    public AuthorizationPolicySavedEvent() {
    }

    public AuthorizationPolicySavedEvent(@MapsTo("policy") AuthorizationPolicy policy) {
        this.policy = policy;
    }

    public AuthorizationPolicy getPolicy() {
        return policy;
    }
}
