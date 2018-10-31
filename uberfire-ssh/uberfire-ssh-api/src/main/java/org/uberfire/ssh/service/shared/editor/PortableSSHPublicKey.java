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

import java.util.Date;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class PortableSSHPublicKey {

    private String id;
    private String name;
    private String keyContent;
    private Date creationDate;
    private Date lastTimeUsed;

    public PortableSSHPublicKey(@MapsTo("id") String id, @MapsTo("name") String name, @MapsTo("keyContent") String keyContent, @MapsTo("creationDate") Date creationDate, @MapsTo("lastTimeUsed") Date lastTimeUsed) {
        this.id = id;
        this.name = name;
        this.keyContent = keyContent;
        this.creationDate = creationDate;
        this.lastTimeUsed = lastTimeUsed;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getKeyContent() {
        return keyContent;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public Date getLastTimeUsed() {
        return lastTimeUsed;
    }
}
