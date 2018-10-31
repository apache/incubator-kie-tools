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

import java.util.Date;

/**
 * Metadata for a {@link SSHPublicKey}
 */
public class KeyMetaData {

    private String name;
    private Date creationDate;
    private Date lastTimeUsed;

    public KeyMetaData() {
        this.name = "";
        this.creationDate = new Date();
    }

    public KeyMetaData(String name, Date creationDate) {
        this.name = name;
        this.creationDate = creationDate;
    }

    /**
     * Gets the key name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the creation date of the {@link SSHPublicKey}
     */
    public Date getCreationDate() {
        return creationDate;
    }

    /**
     * Gets the last {@link Date} the {@link SSHPublicKey} was used
     */
    public Date getLastTimeUsed() {
        return lastTimeUsed;
    }

    /**
     * Sets the last {@link Date} the {@link SSHPublicKey} was used
     */
    public void setLastTimeUsed(Date lastTimeUsed) {
        this.lastTimeUsed = lastTimeUsed;
    }
}
