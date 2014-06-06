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
package org.uberfire.user.management.client;

import org.uberfire.client.mvp.UberView;
import org.uberfire.user.management.model.UserInformation;
import org.uberfire.user.management.model.UserManagerContent;

/**
 * Definition of the User Management Editor's view
 */
public interface UserManagementView extends UberView<UserManagementPresenter> {

    /**
     * The UI requires User information and details of the capabilities supported by the User Manager. To minimize network
     * traffic the View is provided with a Data Transfer Object containing all information needed to initialise the UI
     */
    void setContent( final UserManagerContent content,
                     final boolean isReadOnly );

    /**
     * Request to update the View following creation of a new user
     * @param userInformation Basic user information of new user. Cannot be null.
     */
    void addUser( final UserInformation userInformation );

    /**
     * Request to update the View following a change to a User's basic information or Roles
     * @param oldUserInformation Original user information. Cannot be null.
     * @param newUserInformation Updated user information. Cannot be null.
     */
    void updateUser( final UserInformation oldUserInformation,
                     final UserInformation newUserInformation );

    /**
     * Request to update the View following deletion of a User.
     * @param userInformation Basic user information or user to be removed. Cannot be null.
     */
    void deleteUser( final UserInformation userInformation );

}
