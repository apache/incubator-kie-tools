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

import java.util.Set;
import javax.inject.Inject;

import com.google.gwt.user.client.Command;
import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.common.popups.errors.ErrorPopup;
import org.uberfire.client.mvp.UberView;
import org.uberfire.client.mvp.WorkbenchScreenActivity;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.user.management.client.popups.AddUserPopup;
import org.uberfire.user.management.client.popups.EditUserPasswordPopup;
import org.uberfire.user.management.client.popups.EditUserRolesPopup;
import org.uberfire.user.management.client.resources.i18n.UserManagementConstants;
import org.uberfire.user.management.client.utils.UserManagementUtils;
import org.uberfire.user.management.model.UserInformation;
import org.uberfire.user.management.model.UserManagerContent;
import org.uberfire.user.management.service.UserManagementService;

/**
 * Uberfire compliant Editor to manage Users
 */
@WorkbenchScreen(identifier = "UserManagementPresenter")
public class UserManagementPresenter {

    @Inject
    private UserManagementView view;

    @Inject
    private AddUserPopup addUserPopup;

    @Inject
    private EditUserRolesPopup editUserRolesPopup;

    @Inject
    private EditUserPasswordPopup editUserPasswordPopup;

    @Inject
    private Caller<UserManagementService> userManagementService;

    private boolean isReadOnly;

    /**
     * Launch the Editor
     * @param place
     * @see WorkbenchScreenActivity#onStartup(PlaceRequest)
     */
    @OnStartup
    public void onStartup( final PlaceRequest place ) {
        this.isReadOnly = place.getParameter( "readOnly", null ) == null ? false : true;

        userManagementService.call( new RemoteCallback<UserManagerContent>() {
                                        @Override
                                        public void callback( final UserManagerContent content ) {
                                            view.setContent( content,
                                                             isReadOnly );
                                        }
                                    },
                                    new ErrorCallback<Message>() {
                                        @Override
                                        public boolean error( final Message message,
                                                              final Throwable throwable ) {
                                            ErrorPopup.showMessage( UserManagementConstants.INSTANCE.genericError() + "\n" + throwable.getMessage() );

                                            return false;
                                        }
                                    }
                                  ).loadContent();
    }

    /**
     * Provide a title for the Editor
     * @return
     * @see WorkbenchScreenActivity#getTitle()
     */
    @WorkbenchPartTitle
    public String getTitle() {
        return UserManagementConstants.INSTANCE.userManagementTitle();
    }

    /**
     * Provide the Widget for the Editor that Uberfire inserts into the Workbench
     * @return
     * @see WorkbenchScreenActivity#getWidget()
     */
    @WorkbenchPartView
    public UberView<UserManagementPresenter> getView() {
        return view;
    }

    /**
     * Request to add a new User, called when the user clicks on the "Add" (user) button in the UI
     * This implementation launches a popup to capture the User's User Name and Roles.
     */
    public void addUser() {
        addUserPopup.setCallbackCommand( new Command() {

            @Override
            public void execute() {
                final String userName = addUserPopup.getUserName();
                final String userPassword = addUserPopup.getUserPassword();
                final Set<String> userRoles = UserManagementUtils.convertUserRoles( addUserPopup.getUserRoles() );
                final UserInformation userInformation = new UserInformation( userName,
                                                                             userRoles );
                userManagementService.call( new RemoteCallback<Void>() {
                                                @Override
                                                public void callback( final Void o ) {
                                                    view.addUser( userInformation );
                                                }
                                            },
                                            new ErrorCallback<Message>() {
                                                @Override
                                                public boolean error( final Message message,
                                                                      final Throwable throwable ) {
                                                    ErrorPopup.showMessage( UserManagementConstants.INSTANCE.genericError() + "\n" + throwable.getMessage() );

                                                    return false;
                                                }
                                            }
                                          ).addUser( userInformation,
                                                     userPassword );
            }
        } );
        addUserPopup.show();
    }

    /**
     * Request to delete a User, called when the user clicks on the "Delete" (user) button in the UI
     * This implementation prompts for confirmation before removal of the User from the underlying Identity Manager.
     * @param userInformation User information representing the User to delete. Cannot be null.
     */
    public void deleteUser( final UserInformation userInformation ) {
        userManagementService.call( new RemoteCallback<Void>() {
                                        @Override
                                        public void callback( final Void o ) {
                                            view.deleteUser( userInformation );
                                        }
                                    },
                                    new ErrorCallback<Message>() {
                                        @Override
                                        public boolean error( final Message message,
                                                              final Throwable throwable ) {
                                            ErrorPopup.showMessage( UserManagementConstants.INSTANCE.genericError() + "\n" + throwable.getMessage() );

                                            return false;
                                        }
                                    }
                                  ).deleteUser( userInformation );
    }

    /**
     * Request to update a User's Roles, called when the user clicks on the "Edit" (user) button in the UI
     * This implementation launches a popup to capture the User's Roles.
     * @param userInformation User information representing the User to edit. Cannot be null.
     */
    public void editUserRoles( final UserInformation userInformation ) {
        editUserRolesPopup.setCallbackCommand( new Command() {

            @Override
            public void execute() {
                final String userName = userInformation.getUserName();
                final Set<String> userRoles = UserManagementUtils.convertUserRoles( editUserRolesPopup.getUserRoles() );

                final UserInformation newUserInformation = new UserInformation( userName,
                                                                                userRoles );
                userManagementService.call( new RemoteCallback<Void>() {
                                                @Override
                                                public void callback( final Void o ) {
                                                    view.updateUser( userInformation,
                                                                     newUserInformation );
                                                }
                                            },
                                            new ErrorCallback<Message>() {
                                                @Override
                                                public boolean error( final Message message,
                                                                      final Throwable throwable ) {
                                                    ErrorPopup.showMessage( UserManagementConstants.INSTANCE.genericError() + "\n" + throwable.getMessage() );

                                                    return false;
                                                }
                                            }
                                          ).updateUser( newUserInformation );
            }

        } );
        editUserRolesPopup.setUserInformation( userInformation );
        editUserRolesPopup.show();
    }

    /**
     * Request to update a User's Password, called when the user clicks on the "Password" button in the UI
     * This implementation launches a popup to capture the User's new password.
     * @param userInformation User information representing the User to edit. Cannot be null.
     */
    public void editUserPassword( final UserInformation userInformation ) {
        editUserPasswordPopup.setCallbackCommand( new Command() {

            @Override
            public void execute() {
                final String userPassword = editUserPasswordPopup.getUserPassword();
                userManagementService.call( new RemoteCallback<Void>() {
                                                @Override
                                                public void callback( final Void o ) {
                                                    //Do nothing. Passwords are not presented in the UI
                                                }
                                            },
                                            new ErrorCallback<Message>() {
                                                @Override
                                                public boolean error( final Message message,
                                                                      final Throwable throwable ) {
                                                    ErrorPopup.showMessage( UserManagementConstants.INSTANCE.genericError() + "\n" + throwable.getMessage() );

                                                    return false;
                                                }
                                            }
                                          ).updateUser( userInformation,
                                                        userPassword );
            }

        } );
        editUserPasswordPopup.setUserInformation( userInformation );
        editUserPasswordPopup.show();
    }

}
