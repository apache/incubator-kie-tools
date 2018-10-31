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

package org.uberfire.ssh.service.shared.editor;

import java.security.PublicKey;
import java.util.Collection;

import org.jboss.errai.bus.server.annotations.Remote;
import org.uberfire.ssh.service.backend.keystore.SSHKeyStore;

/**
 * Remote service for the Key service
 */
@Remote
public interface SSHKeyEditorService {

    /**
     * Retrieves all the {@link PortableSSHPublicKey} for the current user.
     * @return A Collection containing the current user's keys
     */
    Collection<PortableSSHPublicKey> getUserKeys();

    /**
     * Deletes the given {@link PortableSSHPublicKey} for the current user
     * @param key The {@link PortableSSHPublicKey} to add
     */
    void deleteKey(PortableSSHPublicKey key);

    /**
     * Adds a new SSh Key into the {@link SSHKeyStore} for the current user.
     * @param name The name of the key
     * @param keyContent The {@link PublicKey} as String format.
     */
    void addKey(String name, String keyContent);
}
