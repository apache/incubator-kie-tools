/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *    http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.uberfire.ext.security.management.client.resources.i18n;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.ConstantsWithLookup;

/**
 * <p>Users Management constants for widgets module.</p>
 *
 * @since 0.8.0 
 */
public interface UsersManagementWidgetsConstants extends ConstantsWithLookup {

    UsersManagementWidgetsConstants INSTANCE = GWT.create(UsersManagementWidgetsConstants.class);

    String loading();
    String emptyEntities();
    String firstPage();
    String previousPage();
    String nextPage();
    String lastPage();
    String next();
    String userHasNoAttributes();
    String attributeIsMandatory();
    String userHasNoGroups();
    String memberOfGroups();
    String groupSelectionFor();
    String userHasNoRoles();
    String memberOfRoles();
    String roleSelectionFor();
    String userAttributes();
    String name();
    String value();
    String search();
    String searchFor();
    String searchResultsFor();
    String edit();
    String delete();
    String cancel();
    String create();
    String save();
    String saveChanges();
    String addToGroups();
    String addToRoles();
    String addAttribute();
    String add();
    String userAlreadyExists();
    String groupAlreadyExists();
    String alreadyExistRegisteredRole();
    String confirmAction();
    String ensureRemoveUser();
    String ensureRemoveGroup();
    String genericError();
    String remove();
    String ensureRemoveAttribute();
    String ensureRemoveGroupFromUser();
    String ensureRemoveRoleFromUser();
    String nameIsMandatory();
    String valueIsMandatory();
    String attributeAlreadyExists();
    String addToSelectedGroups();
    String addToSelectedRoles();
    String addUsersToGroup();
    String assignUsersToGroupName();
    String groupMustHaveAtLeastOneUser();
    String doesNotHavePrivileges();
    String username();
    String inputUserName();
    String groupName();
    String inputGroupName();
    String userIsDirty();
    String changePassword();
    String changePasswordFor();
    String newPassword();
    String repeatNewPassword();
    String passwordCannotBeEmpty();
    String passwordsNotMatch();
    String passwordUpdatedSuccessfully();
    String doSetPasswordNow();
    String clear();
    String clearSearch();
    String change();
    String user();
    String users();
    String group();
    String groups();
    String groupsAssigned();
    String rolesAssigned();
    String role();
    String roles();
    String noUsers();
    String noGroups();
    String noRoles();
    String refresh();
    String total();
    String all();
}
