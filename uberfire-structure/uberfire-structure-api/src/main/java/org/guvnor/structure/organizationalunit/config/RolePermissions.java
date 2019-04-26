/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.guvnor.structure.organizationalunit.config;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class RolePermissions {

    private String roleName;

    private boolean canRead;

    private boolean canWrite;

    private boolean canDelete;

    private boolean canDeploy;

    public RolePermissions() {
    }

    public RolePermissions(@MapsTo("roleName") final String roleName,
                           @MapsTo("canRead") final boolean canRead,
                           @MapsTo("canWrite") final boolean canWrite,
                           @MapsTo("canDelete") final boolean canDelete,
                           @MapsTo("canDeploy") final boolean canDeploy) {
        this.roleName = roleName;
        this.canRead = canRead;
        this.canWrite = canWrite;
        this.canDelete = canDelete;
        this.canDeploy = canDeploy;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(final String roleName) {
        this.roleName = roleName;
    }

    public boolean canRead() {
        return canRead;
    }

    public void setCanRead(final boolean canRead) {
        this.canRead = canRead;
    }

    public boolean canWrite() {
        return canWrite;
    }

    public void setCanWrite(final boolean canWrite) {
        this.canWrite = canWrite;
    }

    public boolean canDelete() {
        return canDelete;
    }

    public void setCanDelete(final boolean canDelete) {
        this.canDelete = canDelete;
    }

    public boolean canDeploy() {
        return canDeploy;
    }

    public void setCanDeploy(boolean canDeploy) {
        this.canDeploy = canDeploy;
    }

    @Override
    public int hashCode() {
        int result = roleName != null ? roleName.hashCode() : 0;
        result = ~~result;
        result = 31 * result + Boolean.valueOf(canRead).hashCode();
        result = ~~result;
        result = 31 * result + Boolean.valueOf(canWrite).hashCode();
        result = ~~result;
        result = 31 * result + Boolean.valueOf(canDelete).hashCode();
        result = ~~result;
        result = 31 * result + Boolean.valueOf(canDeploy).hashCode();
        result = ~~result;
        return result;
    }
}
