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

package org.uberfire.ssh.service.backend.keystore.model;

import java.security.PublicKey;

/**
 * Definition of a Public SSH key for a platform user
 */
public class SSHPublicKey {

    private String id;
    private KeyMetaData metaData;
    private PublicKey key;

    public SSHPublicKey(String id, PublicKey key) {
        this.id = id;
        this.key = key;
        this.metaData = new KeyMetaData();
    }

    public SSHPublicKey(String id, PublicKey key, KeyMetaData metaData) {
        this.id = id;
        this.key = key;
        this.metaData = metaData;
    }

    /**
     * Gets the logic id of the {@link SSHPublicKey}
     */
    public String getId() {
        return id;
    }

    /**
     * Gets the real {@link PublicKey}
     */
    public PublicKey getKey() {
        return key;
    }

    /**
     * Gets the {@link KeyMetaData} of the {@link SSHPublicKey}
     */
    public KeyMetaData getMetaData() {
        return metaData;
    }
}
