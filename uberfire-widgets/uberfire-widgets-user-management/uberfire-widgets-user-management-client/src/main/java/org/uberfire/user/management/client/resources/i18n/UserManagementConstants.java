/*
 * Copyright 2014 JBoss Inc
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

package org.uberfire.user.management.client.resources.i18n;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.Messages;

/**
 * User Management Editor I18N constants
 */
public interface UserManagementConstants
        extends
        Messages {

    public static final UserManagementConstants INSTANCE = GWT.create( UserManagementConstants.class );

    String userManagementTitle();

    String noUsersDefined();

    String promptForRemovalOfUser0( String p0 );

    String userName();

    String userRoles();

    String add();

    String remove();

    String editRoles();

    String editPassword();

    String addUserPopupTitle();

    String userNameIsMandatory();

    String userPasswordIsMandatory();

    String userRolesIsMandatory();

    String userPasswordsDoNotMatch();

    String genericError();

    String noUserManagerInstalledMessage();

}
