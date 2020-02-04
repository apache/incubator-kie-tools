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
package org.uberfire.backend.authz;

import org.jboss.errai.bus.server.annotations.Remote;
import org.jboss.errai.security.shared.api.Group;
import org.uberfire.security.authz.AuthorizationPolicy;

/**
 * Provides methods to manipulate the AuthorizationPolicy instance stored in the Uberfire's virtual file system.
 */
@Remote
public interface AuthorizationService {

    /**
     * Retrieves the current {@link AuthorizationPolicy} instance from the backend storage.
     * @return The stored {@link AuthorizationPolicy} instance
     */
    AuthorizationPolicy loadPolicy();

    /**
     * Overwrites the content of the {@link AuthorizationPolicy} instance stored in the backend
     * by the contents of the instance passed as a parameter.
     * @param policy The authorization policy to store
     */
    void savePolicy(AuthorizationPolicy policy);

    /**
     * Deletes the group from {@link AuthorizationPolicy} instance stored in the backend
     * @param policy The authorization policy to store
     * @param group Group instance
     */
    void deletePolicyByGroup(Group group , AuthorizationPolicy policy);
}
