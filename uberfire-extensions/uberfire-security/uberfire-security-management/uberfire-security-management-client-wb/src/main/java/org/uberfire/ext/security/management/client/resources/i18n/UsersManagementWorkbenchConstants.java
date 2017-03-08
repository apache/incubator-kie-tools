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
import com.google.gwt.i18n.client.Messages;

/**
 * <p>Users Management constants for workbench module.</p>
 * @since 0.8.0
 */
public interface UsersManagementWorkbenchConstants extends Messages {

    UsersManagementWorkbenchConstants INSTANCE = GWT.create(UsersManagementWorkbenchConstants.class);

    String securityManagement();

    String securityExplorer();

    String roles();

    String groups();

    String users();

    String usersManagement();

    String groupsManagement();

    String usersManagementHome();

    String usersExplorer();

    String userEditor();

    String groupsManagementHome();

    String groupsExplorer();

    String groupEditor();

    String rolesExplorer();

    String roleEditor();

    String home_createUser();

    String home_listSearchUsers();

    String home_clickOnUserInListToRead();

    String home_editAndDeleteUser();

    String home_createGroup();

    String home_listSearchGroups();

    String home_clickOnGroupInListToRead();

    String home_deleteGroup();

    String userEditorWelcomeText();

    String groupEditorWelcomeText();

    String showUser(String userId);

    String editUser(String userId);

    String showGroup(String groupName);

    String editGroup(String groupName);

    String createNewUser();

    String createNewGroup();

    String showRole(String roleName);

    String editRole(String roleName);
}
