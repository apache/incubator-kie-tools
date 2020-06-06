/*
 *
 *   Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */

package org.guvnor.rest.client;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class ResourcePermission {

    private PermissionType read;
    private PermissionType create;
    private PermissionType update;
    private PermissionType delete;
    private PermissionType build;

    public PermissionType getRead() {
        return read;
    }

    public void setRead(PermissionType read) {
        this.read = read;
    }

    public PermissionType getCreate() {
        return create;
    }

    public void setCreate(PermissionType create) {
        this.create = create;
    }

    public PermissionType getUpdate() {
        return update;
    }

    public void setUpdate(PermissionType update) {
        this.update = update;
    }

    public PermissionType getDelete() {
        return delete;
    }

    public void setDelete(PermissionType delete) {
        this.delete = delete;
    }

    public PermissionType getBuild() {
        return build;
    }

    public void setBuild(PermissionType build) {
        this.build = build;
    }
}
