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
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.user.management.client.popups.AddUserPopup;
import org.uberfire.user.management.client.popups.EditUserPasswordPopup;
import org.uberfire.user.management.client.popups.EditUserRolesPopup;
import org.uberfire.user.management.client.resources.i18n.UserManagementConstants;
import org.uberfire.user.management.client.utils.UserManagementUtils;
import org.uberfire.user.management.client.widgets.UserManagementViewController;
import org.uberfire.user.management.model.UserInformation;
import org.uberfire.user.management.model.UserManagerContent;
import org.uberfire.user.management.service.UserManagementService;

@WorkbenchScreen(identifier = "UserManagementPresenter")
public class UserManagementPresenter {

    @Inject
    private UserManagementViewController view;

    @Inject
    private AddUserPopup addUserPopup;

    @Inject
    private EditUserRolesPopup editUserRolesPopup;

    @Inject
    private EditUserPasswordPopup editUserPasswordPopup;

    @Inject
    private Caller<UserManagementService> userManagementService;

    private PlaceRequest place;
    private boolean isReadOnly;

    @OnStartup
    public void onStartup( final PlaceRequest place ) {
        this.place = place;
        this.isReadOnly = place.getParameter( "readOnly", null ) == null ? false : true;

        userManagementService.call( new RemoteCallback<Boolean>() {
            @Override
            public void callback( final Boolean result ) {
                final boolean isUserManagerInstalled = Boolean.TRUE.equals( result );
                view.setUserManagerInstalled( isUserManagerInstalled );
            }
        } ).isUserManagerInstalled();
    }

    public void loadContent() {
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

    @WorkbenchPartTitle
    public String getTitle() {
        return UserManagementConstants.INSTANCE.userManagementTitle();
    }

    @WorkbenchPartView
    public UberView<UserManagementPresenter> getView() {
        return view;
    }

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

    public void editUserRoles( final UserInformation oldUserInformation ) {
        editUserRolesPopup.setCallbackCommand( new Command() {

            @Override
            public void execute() {
                final String userName = oldUserInformation.getUserName();
                final Set<String> userRoles = UserManagementUtils.convertUserRoles( editUserRolesPopup.getUserRoles() );

                final UserInformation newUserInformation = new UserInformation( userName,
                                                                                userRoles );
                userManagementService.call( new RemoteCallback<Void>() {
                                                @Override
                                                public void callback( final Void o ) {
                                                    view.updateUser( oldUserInformation,
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
        editUserRolesPopup.setUserInformation( oldUserInformation );
        editUserRolesPopup.show();
    }

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
