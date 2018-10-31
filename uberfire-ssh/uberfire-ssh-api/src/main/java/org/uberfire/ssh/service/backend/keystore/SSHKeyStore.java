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

package org.uberfire.ssh.service.backend.keystore;

import java.util.Collection;

import org.uberfire.ssh.service.backend.keystore.model.SSHPublicKey;

/**
 * API to administrate user {@link SSHPublicKey}
 */
public interface SSHKeyStore {

    /**
     * Registers a {@link SSHPublicKey} to a given platform user
     * @param userName The user's login
     * @param key The {@link SSHPublicKey} to register
     */
    void addUserKey(String userName, SSHPublicKey key);

    /**
     * Unregisters a {@link SSHPublicKey} for a given user
     * @param userName The user's login
     * @param key The {@link SSHPublicKey} to unregister
     */
    void removeUserKey(String userName, SSHPublicKey key);

    /**
     * Updates the a platform user {@link SSHPublicKey}
     * @param userName The user's login
     * @param key The {@link SSHPublicKey} to update
     */
    void updateUserKey(String userName, SSHPublicKey key);

    /**
     * Lists all the {@link SSHPublicKey} for a given platform user
     * @param userName The user's login
     * @return A Collection containing all the user's {@link SSHPublicKey}
     */
    Collection<SSHPublicKey> getUserKeys(String userName);
}
