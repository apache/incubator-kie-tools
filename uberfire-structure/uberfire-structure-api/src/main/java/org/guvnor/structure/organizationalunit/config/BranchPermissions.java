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

import java.util.Map;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class BranchPermissions {

    private String branchName;

    private Map<String, RolePermissions> permissionsByRole;

    public BranchPermissions() {
    }

    public BranchPermissions(@MapsTo("branchName") final String branchName,
                             @MapsTo("permissionsByRole") final Map<String, RolePermissions> permissionsByRole) {
        this.branchName = branchName;
        this.permissionsByRole = permissionsByRole;
    }

    public String getBranchName() {
        return branchName;
    }

    public Map<String, RolePermissions> getPermissionsByRole() {
        return permissionsByRole;
    }

    @Override
    public int hashCode() {
        int result = branchName != null ? branchName.hashCode() : 0;
        result = ~~result;
        result = 31 * result + ( permissionsByRole != null ? permissionsByRole.hashCode() : 0 );
        result = ~~result;
        return result;
    }
}
