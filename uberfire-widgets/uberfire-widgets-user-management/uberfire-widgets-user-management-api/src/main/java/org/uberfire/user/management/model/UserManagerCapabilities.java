/*
 * Copyright 2014 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.uberfire.user.management.model;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class UserManagerCapabilities {

    private boolean isAddUserSupported;
    private boolean isUpdateUserPasswordSupported;
    private boolean isUpdateUserRolesSupported;
    private boolean isDeleteUserSupported;

    public UserManagerCapabilities() {
        //Errai marshalling
    }

    public UserManagerCapabilities( final boolean isAddUserSupported,
                                    final boolean isUpdateUserPasswordSupported,
                                    final boolean isDeleteUserSupported,
                                    final boolean isUpdateUserRolesSupported ) {
        this.isAddUserSupported = isAddUserSupported;
        this.isUpdateUserPasswordSupported = isUpdateUserPasswordSupported;
        this.isDeleteUserSupported = isDeleteUserSupported;
        this.isUpdateUserRolesSupported = isUpdateUserRolesSupported;
    }

    public boolean isAddUserSupported() {
        return isAddUserSupported;
    }

    public boolean isUpdateUserPasswordSupported() {
        return isUpdateUserPasswordSupported;
    }

    public boolean isDeleteUserSupported() {
        return isDeleteUserSupported;
    }

    public boolean isUpdateUserRolesSupported() {
        return isUpdateUserRolesSupported;
    }

}
