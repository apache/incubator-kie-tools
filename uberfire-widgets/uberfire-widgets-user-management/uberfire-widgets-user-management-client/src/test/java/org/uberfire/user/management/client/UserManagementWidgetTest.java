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

import java.util.Collections;
import java.util.List;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.CellTable;
import com.github.gwtbootstrap.client.ui.FluidContainer;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.uberfire.client.common.ButtonCell;
import org.uberfire.client.tables.ResizableHeader;
import org.uberfire.user.management.model.UserInformation;
import org.uberfire.user.management.model.UserManagerCapabilities;
import org.uberfire.user.management.model.UserManagerContent;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class UserManagementWidgetTest {

    @GwtMock
    private CellTable<UserInformation> table;

    @Mock
    private ListDataProvider dataProvider;

    @GwtMock
    private FluidContainer container;

    @GwtMock
    private Button addUserButton;

    @Mock
    private ButtonCell editUserRolesButton;

    @Mock
    private TextColumn<UserInformation> editUserRolesColumn;

    @Mock
    private ButtonCell editUserPasswordButton;

    @Mock
    private TextColumn<UserInformation> editUserPasswordColumn;

    @Mock
    private ButtonCell deleteUserButton;

    @Mock
    private Column<UserInformation, String> deleteUserColumn;

    private UserManagementWidgetUnitTestWrapper widget;

    @Before
    public void setup() {
        //Workaround for gwt mock works (apparently it doesn't allow to @gwtmock for same type and init mocks only works on that way)
        MockitoAnnotations.initMocks( this );
        table = GWT.create( CellTable.class );
        container = GWT.create( FluidContainer.class );
        addUserButton = GWT.create( Button.class );

        widget = new UserManagementWidgetUnitTestWrapper().setupMocks( table,
                                                                       dataProvider,
                                                                       container,
                                                                       addUserButton,
                                                                       editUserRolesButton,
                                                                       editUserRolesColumn,
                                                                       editUserPasswordButton,
                                                                       editUserPasswordColumn,
                                                                       deleteUserButton,
                                                                       deleteUserColumn );
    }

    @Test
    public void verifyNewInstanceCreationSequenceHappyCase() {
        verify( table,
                times( 2 ) ).addColumn( any( Column.class ),
                                        any( ResizableHeader.class ) );
        verify( table,
                times( 3 ) ).addColumn( any( Column.class ),
                                        any( String.class ) );

        verify( addUserButton ).setEnabled( false );
        verify( editUserRolesButton ).setEnabled( false );
        verify( editUserPasswordButton ).setEnabled( false );
        verify( deleteUserButton ).setEnabled( false );

        verify( editUserRolesColumn ).setFieldUpdater( any( FieldUpdater.class ) );
        verify( editUserPasswordColumn ).setFieldUpdater( any( FieldUpdater.class ) );
        verify( deleteUserColumn ).setFieldUpdater( any( FieldUpdater.class ) );
    }

    @Test
    public void verifySetContent() {
        final List<UserInformation> userInformation = Collections.EMPTY_LIST;
        final UserManagerCapabilities capabilities = new UserManagerCapabilities( true,
                                                                                  true,
                                                                                  true,
                                                                                  true );
        final UserManagerContent content = new UserManagerContent( userInformation,
                                                                   capabilities );

        widget.setContent( content,
                           false );
        verify( dataProvider ).setList( userInformation );
        verify( dataProvider ).addDataDisplay( table );
    }

    @Test
    public void verifySetContentNotReadOnly() {
        final List<UserInformation> userInformation = Collections.EMPTY_LIST;
        final UserManagerCapabilities capabilities = new UserManagerCapabilities( true,
                                                                                  true,
                                                                                  true,
                                                                                  true );
        final UserManagerContent content = new UserManagerContent( userInformation,
                                                                   capabilities );

        verify( addUserButton ).setEnabled( false );
        verify( editUserRolesButton ).setEnabled( false );
        verify( editUserPasswordButton ).setEnabled( false );
        verify( deleteUserButton ).setEnabled( false );

        widget.setContent( content,
                           false );

        verify( addUserButton ).setEnabled( true );
        verify( editUserRolesButton ).setEnabled( true );
        verify( editUserPasswordButton ).setEnabled( true );
        verify( deleteUserButton ).setEnabled( true );
    }

    @Test
    public void verifySetContentReadOnly() {
        final List<UserInformation> userInformation = Collections.EMPTY_LIST;
        final UserManagerCapabilities capabilities = new UserManagerCapabilities( true,
                                                                                  true,
                                                                                  true,
                                                                                  true );
        final UserManagerContent content = new UserManagerContent( userInformation,
                                                                   capabilities );

        verify( addUserButton ).setEnabled( false );
        verify( editUserRolesButton ).setEnabled( false );
        verify( editUserPasswordButton ).setEnabled( false );
        verify( deleteUserButton ).setEnabled( false );

        widget.setContent( content,
                           true );

        verify( addUserButton,
                times( 2 ) ).setEnabled( false );
        verify( editUserRolesButton,
                times( 2 ) ).setEnabled( false );
        verify( editUserPasswordButton,
                times( 2 ) ).setEnabled( false );
        verify( deleteUserButton,
                times( 2 ) ).setEnabled( false );
    }

    @Test
    public void verifySetContentIsAddUserSupportedIsFalse() {
        final List<UserInformation> userInformation = Collections.EMPTY_LIST;
        final UserManagerCapabilities capabilities = new UserManagerCapabilities( false,
                                                                                  true,
                                                                                  true,
                                                                                  true );
        final UserManagerContent content = new UserManagerContent( userInformation,
                                                                   capabilities );

        verify( addUserButton ).setEnabled( false );
        verify( editUserRolesButton ).setEnabled( false );
        verify( editUserPasswordButton ).setEnabled( false );
        verify( deleteUserButton ).setEnabled( false );

        widget.setContent( content,
                           false );

        verify( addUserButton,
                times( 2 ) ).setEnabled( false );
        verify( editUserRolesButton ).setEnabled( true );
        verify( editUserPasswordButton ).setEnabled( true );
        verify( deleteUserButton ).setEnabled( true );
    }

    @Test
    public void verifySetContentIsUpdateUserPasswordSupportedIsFalse() {
        final List<UserInformation> userInformation = Collections.EMPTY_LIST;
        final UserManagerCapabilities capabilities = new UserManagerCapabilities( true,
                                                                                  false,
                                                                                  true,
                                                                                  true );
        final UserManagerContent content = new UserManagerContent( userInformation,
                                                                   capabilities );

        verify( addUserButton ).setEnabled( false );
        verify( editUserRolesButton ).setEnabled( false );
        verify( editUserPasswordButton ).setEnabled( false );
        verify( deleteUserButton ).setEnabled( false );

        widget.setContent( content,
                           false );

        verify( addUserButton ).setEnabled( true );
        verify( editUserRolesButton ).setEnabled( true );
        verify( editUserPasswordButton,
                times( 2 ) ).setEnabled( false );
        verify( deleteUserButton ).setEnabled( true );
    }

    @Test
    public void verifySetContentIsDeleteUserSupportedIsFalse() {
        final List<UserInformation> userInformation = Collections.EMPTY_LIST;
        final UserManagerCapabilities capabilities = new UserManagerCapabilities( true,
                                                                                  true,
                                                                                  false,
                                                                                  true );
        final UserManagerContent content = new UserManagerContent( userInformation,
                                                                   capabilities );

        verify( addUserButton ).setEnabled( false );
        verify( editUserRolesButton ).setEnabled( false );
        verify( editUserPasswordButton ).setEnabled( false );
        verify( deleteUserButton ).setEnabled( false );

        widget.setContent( content,
                           false );

        verify( addUserButton ).setEnabled( true );
        verify( editUserRolesButton ).setEnabled( true );
        verify( editUserPasswordButton ).setEnabled( true );
        verify( deleteUserButton,
                times( 2 ) ).setEnabled( false );
    }

    @Test
    public void verifySetContentIsUpdateUserRolesSupportedIsFalse() {
        final List<UserInformation> userInformation = Collections.EMPTY_LIST;
        final UserManagerCapabilities capabilities = new UserManagerCapabilities( true,
                                                                                  true,
                                                                                  true,
                                                                                  false );
        final UserManagerContent content = new UserManagerContent( userInformation,
                                                                   capabilities );

        verify( addUserButton ).setEnabled( false );
        verify( editUserRolesButton ).setEnabled( false );
        verify( editUserPasswordButton ).setEnabled( false );
        verify( deleteUserButton ).setEnabled( false );

        widget.setContent( content,
                           false );

        verify( addUserButton ).setEnabled( true );
        verify( editUserRolesButton,
                times( 2 ) ).setEnabled( false );
        verify( editUserPasswordButton ).setEnabled( true );
        verify( deleteUserButton ).setEnabled( true );
    }

}
