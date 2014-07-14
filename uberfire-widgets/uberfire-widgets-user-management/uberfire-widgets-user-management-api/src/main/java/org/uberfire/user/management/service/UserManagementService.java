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
package org.uberfire.user.management.service;

import org.jboss.errai.bus.server.annotations.Remote;
import org.uberfire.user.management.model.UserInformation;
import org.uberfire.user.management.model.UserManagerContent;

/**
 * Service definition used by the UI
 */
@Remote
public interface UserManagementService {

    /**
     * The UI requires User information and details of the capabilities supported by the User Manager.
     * To minimize network traffic this method should return everything needed by the UI in a single call.
     * @return A non-null Data Transfer Object containing all information needed to initialise the UI
     */
    UserManagerContent loadContent();

    /**
     * Request to create a new user
     * @param userInformation Basic user information. Cannot be null.
     * @param userPassword User's password. Cannot be null.
     */
    void addUser( final UserInformation userInformation,
                  final String userPassword );

    /**
     * Request to update a User's basic information
     * @param userInformation Basic user information. Cannot be null.
     */
    void updateUser( final UserInformation userInformation );

    /**
     * Request to update a User's basic information and password
     * @param userInformation Basic user information. Cannot be null.
     * @param userPassword User's password. Cannot be null.
     */
    void updateUser( final UserInformation userInformation,
                     final String userPassword );

    /**
     * Request to delete a User.
     * @param userInformation Basic user information. Cannot be null.
     */
    void deleteUser( final UserInformation userInformation );

}
