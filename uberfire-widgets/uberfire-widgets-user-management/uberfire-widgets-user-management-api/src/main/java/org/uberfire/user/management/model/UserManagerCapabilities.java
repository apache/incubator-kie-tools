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

/**
 * Data Transfer Object defining the capabilities of the installed User Manager.
 * This is used to align the operations available in the UI to those supported by the User Manager.
 */
@Portable
public class UserManagerCapabilities {

    private boolean isAddUserSupported;
    private boolean isUpdateUserPasswordSupported;
    private boolean isUpdateUserRolesSupported;
    private boolean isDeleteUserSupported;

    public UserManagerCapabilities() {
        //Errai marshalling
    }

    /**
     * Constructor
     * @param isAddUserSupported True if the User Manager supports creation of new Users
     * @param isUpdateUserPasswordSupported True if the User Manager supports updates to a User's password
     * @param isDeleteUserSupported True if the User Manager supports deletion of Users
     * @param isUpdateUserRolesSupported True if the User Manager supports changes to a Users' Roles
     */
    public UserManagerCapabilities( final boolean isAddUserSupported,
                                    final boolean isUpdateUserPasswordSupported,
                                    final boolean isDeleteUserSupported,
                                    final boolean isUpdateUserRolesSupported ) {
        this.isAddUserSupported = isAddUserSupported;
        this.isUpdateUserPasswordSupported = isUpdateUserPasswordSupported;
        this.isDeleteUserSupported = isDeleteUserSupported;
        this.isUpdateUserRolesSupported = isUpdateUserRolesSupported;
    }

    /**
     * @return True if the User Manager supports creation of new Users
     */
    public boolean isAddUserSupported() {
        return isAddUserSupported;
    }

    /**
     * @return True if the User Manager supports updates to a User's password
     */
    public boolean isUpdateUserPasswordSupported() {
        return isUpdateUserPasswordSupported;
    }

    /**
     * @return True if the User Manager supports deletion of Users
     */
    public boolean isDeleteUserSupported() {
        return isDeleteUserSupported;
    }

    /**
     * @return True if the User Manager supports changes to a Users' Roles
     */
    public boolean isUpdateUserRolesSupported() {
        return isUpdateUserRolesSupported;
    }

}
