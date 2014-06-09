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
package org.uberfire.user.management.client;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.CellTable;
import com.github.gwtbootstrap.client.ui.FluidContainer;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.view.client.ListDataProvider;
import org.uberfire.client.common.ButtonCell;
import org.uberfire.user.management.model.UserInformation;

/**
 * Wrapper for Widget under test to provide mocks
 */
public class UserManagementWidgetUnitTestWrapper extends UserManagerViewImpl {

    public UserManagementWidgetUnitTestWrapper() {
    }

    public UserManagementWidgetUnitTestWrapper setupMocks( final CellTable<UserInformation> table,
                                                           final ListDataProvider<UserInformation> dataProvider,
                                                           final FluidContainer container,
                                                           final Button addUserButton,
                                                           final ButtonCell editUserRolesButton,
                                                           final Column<UserInformation, String> editUserRolesColumn,
                                                           final ButtonCell editUserPasswordButton,
                                                           final Column<UserInformation, String> editUserPasswordColumn,
                                                           final ButtonCell deleteUserButton,
                                                           final Column<UserInformation, String> deleteUserColumn ) {
        this.table = table;
        this.dataProvider = dataProvider;
        this.container = container;
        this.addUserButton = addUserButton;
        this.editUserRolesButton = editUserRolesButton;
        this.editUserRolesColumn = editUserRolesColumn;
        this.editUserPasswordButton = editUserPasswordButton;
        this.editUserPasswordColumn = editUserPasswordColumn;
        this.deleteUserButton = deleteUserButton;
        this.deleteUserColumn = deleteUserColumn;

        init();

        return this;
    }

}
