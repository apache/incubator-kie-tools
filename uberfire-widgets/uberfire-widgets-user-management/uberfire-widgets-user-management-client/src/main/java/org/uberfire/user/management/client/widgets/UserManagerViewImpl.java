/*
 * Copyright 2013 JBoss Inc
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
package org.uberfire.user.management.client.widgets;

import javax.annotation.PostConstruct;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.CellTable;
import com.github.gwtbootstrap.client.ui.FluidContainer;
import com.github.gwtbootstrap.client.ui.Label;
import com.github.gwtbootstrap.client.ui.constants.ButtonType;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.github.gwtbootstrap.client.ui.resources.ButtonSize;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import org.uberfire.client.common.ButtonCell;
import org.uberfire.client.tables.ResizableHeader;
import org.uberfire.commons.validation.PortablePreconditions;
import org.uberfire.user.management.client.UserManagementPresenter;
import org.uberfire.user.management.client.resources.i18n.UserManagementConstants;
import org.uberfire.user.management.client.utils.UserManagementUtils;
import org.uberfire.user.management.model.UserInformation;
import org.uberfire.user.management.model.UserManagerContent;

public class UserManagerViewImpl extends Composite implements UserManagementView {

    interface UserManagerWidgetBinder
            extends
            UiBinder<Widget, UserManagerViewImpl> {

    }

    @UiField(provided = true)
    CellTable<UserInformation> table = new CellTable<UserInformation>();
    ListDataProvider<UserInformation> dataProvider;

    @UiField
    FluidContainer container;

    @UiField
    Button addUserButton;

    private boolean isReadOnly = false;
    private ButtonCell deleteUserButton;
    private ButtonCell editUserRolesButton;
    private ButtonCell editUserPasswordButton;

    private UserManagementPresenter presenter;

    private static UserManagerWidgetBinder uiBinder = GWT.create( UserManagerWidgetBinder.class );

    @PostConstruct
    public void init() {
        initWidget( uiBinder.createAndBindUi( this ) );

        //Setup table
        table.setEmptyTableWidget( new Label( UserManagementConstants.INSTANCE.noUsersDefined() ) );

        //Columns
        final TextColumn<UserInformation> userNameColumn = new TextColumn<UserInformation>() {

            @Override
            public String getValue( final UserInformation userInformation ) {
                return userInformation.getUserName();
            }
        };

        final TextColumn<UserInformation> userRolesColumn = new TextColumn<UserInformation>() {

            @Override
            public String getValue( final UserInformation userInformation ) {
                return UserManagementUtils.convertUserRoles( userInformation.getUserRoles() );
            }

        };

        editUserRolesButton = new ButtonCell( ButtonSize.SMALL );
        editUserRolesButton.setType( ButtonType.DEFAULT );
        editUserRolesButton.setIcon( IconType.EDIT );
        final Column<UserInformation, String> editUserRolesColumn = new Column<UserInformation, String>( editUserRolesButton ) {
            @Override
            public String getValue( final UserInformation userInformation ) {
                return UserManagementConstants.INSTANCE.editRoles();
            }
        };
        editUserRolesColumn.setFieldUpdater( new FieldUpdater<UserInformation, String>() {
            public void update( final int index,
                                final UserInformation userInformation,
                                final String value ) {
                if ( isReadOnly ) {
                    return;
                }
                presenter.editUserRoles( userInformation );
            }
        } );

        editUserPasswordButton = new ButtonCell( ButtonSize.SMALL );
        editUserPasswordButton.setType( ButtonType.DEFAULT );
        editUserPasswordButton.setIcon( IconType.EDIT );
        final Column<UserInformation, String> editUserPasswordColumn = new Column<UserInformation, String>( editUserPasswordButton ) {
            @Override
            public String getValue( final UserInformation userInformation ) {
                return UserManagementConstants.INSTANCE.editPassword();
            }
        };
        editUserPasswordColumn.setFieldUpdater( new FieldUpdater<UserInformation, String>() {
            public void update( final int index,
                                final UserInformation userInformation,
                                final String value ) {
                if ( isReadOnly ) {
                    return;
                }
                presenter.editUserPassword( userInformation );
            }
        } );

        deleteUserButton = new ButtonCell( ButtonSize.SMALL );
        deleteUserButton.setType( ButtonType.DANGER );
        deleteUserButton.setIcon( IconType.MINUS_SIGN );
        final Column<UserInformation, String> deleteUserColumn = new Column<UserInformation, String>( deleteUserButton ) {
            @Override
            public String getValue( final UserInformation userInformation ) {
                return UserManagementConstants.INSTANCE.remove();
            }
        };
        deleteUserColumn.setFieldUpdater( new FieldUpdater<UserInformation, String>() {
            public void update( final int index,
                                final UserInformation userInformation,
                                final String value ) {
                if ( isReadOnly ) {
                    return;
                }
                if ( Window.confirm( UserManagementConstants.INSTANCE.promptForRemovalOfUser0( userInformation.getUserName() ) ) ) {
                    presenter.deleteUser( userInformation );
                }
            }
        } );

        table.addColumn( userNameColumn,
                         new ResizableHeader( UserManagementConstants.INSTANCE.userName(),
                                              table,
                                              userNameColumn ) );
        table.addColumn( userRolesColumn,
                         new ResizableHeader( UserManagementConstants.INSTANCE.userRoles(),
                                              table,
                                              userRolesColumn ) );
        table.addColumn( editUserPasswordColumn,
                         UserManagementConstants.INSTANCE.editPassword() );
        table.addColumn( editUserRolesColumn,
                         UserManagementConstants.INSTANCE.editRoles() );
        table.addColumn( deleteUserColumn,
                         UserManagementConstants.INSTANCE.remove() );

        //Default to disabled, until we know what features are supported
        addUserButton.setEnabled( false );
        editUserRolesButton.setEnabled( false );
        editUserPasswordButton.setEnabled( false );
        deleteUserButton.setEnabled( false );
    }

    @Override
    public void init( final UserManagementPresenter presenter ) {
        this.presenter = PortablePreconditions.checkNotNull( "presenter",
                                                             presenter );
    }

    @Override
    public void setContent( final UserManagerContent content,
                            final boolean isReadOnly ) {
        this.isReadOnly = isReadOnly;
        this.dataProvider = new ListDataProvider<UserInformation>( content.getUserInformation() );
        this.dataProvider.addDataDisplay( table );
        final boolean isAddUserSupported = content.getCapabilities().isAddUserSupported();
        final boolean isUpdateUserRolesSupported = content.getCapabilities().isUpdateUserRolesSupported();
        final boolean isUpdateUserPasswordSupported = content.getCapabilities().isUpdateUserPasswordSupported();
        final boolean isDeleteUserSupported = content.getCapabilities().isDeleteUserSupported();
        addUserButton.setEnabled( !isReadOnly && isAddUserSupported );
        editUserRolesButton.setEnabled( !isReadOnly && isUpdateUserRolesSupported );
        editUserPasswordButton.setEnabled( !isReadOnly && isUpdateUserPasswordSupported );
        deleteUserButton.setEnabled( !isReadOnly && isDeleteUserSupported );
    }

    @Override
    public void addUser( final UserInformation userInformation ) {
        this.dataProvider.getList().add( userInformation );
    }

    @Override
    public void updateUser( final UserInformation oldUserInformation,
                            final UserInformation newUserInformation ) {
        final int idx = this.dataProvider.getList().indexOf( oldUserInformation );
        if ( idx < 0 ) {
            return;
        }
        this.dataProvider.getList().set( idx,
                                         newUserInformation );
    }

    @Override
    public void deleteUser( final UserInformation userInformation ) {
        this.dataProvider.getList().remove( userInformation );
    }

    @UiHandler(value = "addUserButton")
    public void onClickAddUserButton( final ClickEvent event ) {
        presenter.addUser();
    }

}
