/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.ssh.service.backend.auth;

import java.security.PublicKey;

import org.jboss.errai.security.shared.api.identity.User;
import org.uberfire.ssh.service.backend.keystore.SSHKeyStore;

/**
 * Service that authenticates platform users based on a {@link PublicKey}
 */
public interface SSHKeyAuthenticator {

    /**
     * Authenticates the given user and {@link PublicKey}
     * @param userName The login of the user
     * @param key The {@link PublicKey} for the given user
     * @return A {@link User} instance if the userName exists on the platform and has a matching key registered on the
     * current {@link SSHKeyStore}. Null if the user doesn't exist or the key isn't valid.
     */
    User authenticate(String userName, PublicKey key);
}
