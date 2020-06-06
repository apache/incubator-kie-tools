/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.guvnor.rest.client;

import java.util.List;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class Permission {

    private Boolean read;
    private Boolean create;
    private Boolean update;
    private Boolean delete;
    private Boolean build;

    private List<PermissionException> exceptions;

    public Permission() {
    }

    public Permission(@MapsTo("read") Boolean read, @MapsTo("create") Boolean create,
                      @MapsTo("update") Boolean update, @MapsTo("delete") Boolean delete,
                      @MapsTo("build") Boolean build, @MapsTo("exceptions") List<PermissionException> exceptions) {
        this.read = read;
        this.create = create;
        this.update = update;
        this.delete = delete;
        this.build = build;
        this.exceptions = exceptions;
    }

    public Boolean isRead() {
        return read;
    }

    public Boolean isCreate() {
        return create;
    }

    public Boolean isUpdate() {
        return update;
    }

    public Boolean isDelete() {
        return delete;
    }

    public Boolean isBuild() {
        return build;
    }

    public List<PermissionException> getExceptions() {
        return exceptions;
    }

    public void setRead(Boolean read) {
        this.read = read;
    }

    public void setCreate(Boolean create) {
        this.create = create;
    }

    public void setUpdate(Boolean update) {
        this.update = update;
    }

    public void setDelete(Boolean delete) {
        this.delete = delete;
    }

    public void setBuild(Boolean build) {
        this.build = build;
    }

    public void setExceptions(List<PermissionException> exceptions) {
        this.exceptions = exceptions;
    }
}
