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

package org.uberfire.ext.security.management.client.widgets.management.editor.user.workflow;

import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.security.shared.api.identity.User;
import org.uberfire.ext.security.management.api.exception.EntityAlreadyExistsException;
import org.uberfire.ext.security.management.api.exception.SecurityManagementException;
import org.uberfire.ext.security.management.api.exception.UserNotFoundException;
import org.uberfire.ext.security.management.client.ClientUserSystemManager;
import org.uberfire.ext.security.management.client.editor.user.UserEditorDriver;
import org.uberfire.ext.security.management.client.resources.i18n.UsersManagementWidgetsConstants;
import org.uberfire.ext.security.management.client.resources.i18n.UsersManagementWidgetsMessages;
import org.uberfire.ext.security.management.client.widgets.management.ChangePassword;
import org.uberfire.ext.security.management.client.widgets.management.CreateEntity;
import org.uberfire.ext.security.management.client.widgets.management.editor.user.UserEditor;
import org.uberfire.ext.security.management.client.widgets.management.editor.workflow.EntityWorkflowView;
import org.uberfire.ext.security.management.client.widgets.management.events.*;
import org.uberfire.ext.security.management.client.widgets.popup.ConfirmBox;
import org.uberfire.ext.security.management.client.widgets.popup.LoadingBox;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.events.NotificationEvent;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import static org.uberfire.workbench.events.NotificationEvent.NotificationType.SUCCESS;

/**
 * <p>The workflow for creating a new user.</p>
 * <p>It links the editor & sub-editors components with the editor driver and the remote user services.</p>
 * 
 * @since 0.8.0
 */
@Dependent
public class UserCreationWorkflow extends BaseUserEditorWorkflow {

    CreateEntity createEntity;
    Event<CreateUserEvent> createUserEvent;
    
    @Inject
    public UserCreationWorkflow(final ClientUserSystemManager userSystemManager,
                                final Event<OnErrorEvent> errorEvent,
                                final Event<NotificationEvent> workbenchNotification,
                                final Event<DeleteUserEvent> deleteUserEvent,
                                final Event<SaveUserEvent> saveUserEvent,
                                final Event<CreateUserEvent> createUserEvent,
                                final ConfirmBox confirmBox,
                                final CreateEntity createEntity,
                                final UserEditor userEditor,
                                final UserEditorDriver userEditorDriver,
                                final ChangePassword changePassword,
                                final LoadingBox loadingBox,
                                final EntityWorkflowView view) {
        
        super(userSystemManager, errorEvent, workbenchNotification, 
                deleteUserEvent, saveUserEvent, confirmBox, userEditor, userEditorDriver, 
                changePassword, loadingBox, view);
        
        this.createUserEvent = createUserEvent;
        this.createEntity = createEntity;
    }

    @PostConstruct
    public void init() {
    }

    /*  ******************************************************************************************************
                                     PUBLIC PRESENTER API 
         ****************************************************************************************************** */
    
    public void create() {
        clear();

        // Show the create entity component for user instance types.
        createEntity.show(UsersManagementWidgetsConstants.INSTANCE.inputUserName(),
                UsersManagementWidgetsConstants.INSTANCE.username() + "...");
        
        // Show the create entity view.
        view.setWidget(createEntity.asWidget())
                .setCancelButtonVisible(false)
                .setSaveButtonVisible(true)
                .setSaveButtonEnabled(true)
                .setSaveButtonText(UsersManagementWidgetsConstants.INSTANCE.next())
                .setCallback(new EntityWorkflowView.Callback() {
            @Override
            public void onSave() {
                UserCreationWorkflow.this.onCreateEntityClick();
            }

            @Override
            public void onCancel() {
                
            }
        });
        
    }

    /*  ******************************************************************************************************
                                 VIEW CALLBACKS 
     ****************************************************************************************************** */
    
    void onCreateEntityClick() {
        final String identifier = createEntity.getEntityIdentifier();
        if (identifier != null) {
            checkCreate(identifier, new CheckCreateCallback() {
                @Override
                public void valid() {
                    createEntity.clear();
                    UserCreationWorkflow.this.user = userEditorDriver.createNewUser(identifier);
                    doEdit();
                }

                @Override
                public void invalid(final SecurityManagementException exception) {
                    showError(exception.getMessage());
                    createEntity.setErrorState();
                }

                @Override
                public void error(final Throwable error) {
                    showError(error);
                }
            });
        }
    }

    
    /*  ******************************************************************************************************
                                 PRIVATE METHODS AND VALIDATORS
     ****************************************************************************************************** */

    protected void doEdit() {
        
        // Configure the workflow's view.
        doShowEditorView();
        
        // Edit the instance using the user editor's driver.
        edit();
        
        // Enable the create button by default, not as the default edition behavior.
        view
            .setSaveButtonText(UsersManagementWidgetsConstants.INSTANCE.create())
            .setSaveButtonEnabled(true)
            .setSaveButtonVisible(true)
            .setCancelButtonVisible(true);
        
        // When creating, the edit, change password and delete buttons must not be available.
        getUserEditor()
                .setEditButtonVisible(false)
                .setChangePasswordButtonVisible(false)
                .setDeleteButtonVisible(false);
        
    }
    
    @Override
    protected void doSaveRemoteServiceCall(RemoteCallback<User> callback) {
        userSystemManager.users(callback, errorCallback).create(user);
    }

    @Override
    protected void afterSave(String id) {
        // Ask for password input at user creation time.
        confirmBox.show(UsersManagementWidgetsConstants.INSTANCE.confirmAction(), UsersManagementWidgetsConstants.INSTANCE.doSetPasswordNow(),
                new Command() {
                    @Override
                    public void execute() {
                        // Set a password for the new user.
                        showPasswordPopup(new ChangePassword.ChangePasswordCallback() {
                            @Override
                            public void onPasswordUpdated() {
                                doAfterSave(user.getIdentifier());
                            }

                            @Override
                            public void onError(Throwable throwable) {
                                showError(throwable);
                                onCancel();
                            }
                        });
                    }
                }, new Command() {
                    @Override
                    public void execute() {
                        // Do not set a password for the new user.
                        doAfterSave(user.getIdentifier());
                    }
                });
    }

    private void doAfterSave(final String id) {
        workbenchNotification.fire(new NotificationEvent(UsersManagementWidgetsMessages.INSTANCE.userCreated(id), SUCCESS));
        createUserEvent.fire(new CreateUserEvent(id));
        create(); // Go to home workflow step.
    }

    @Override
    protected String getSaveButtonText() {
        return UsersManagementWidgetsConstants.INSTANCE.saveChanges();
    }

    @Override
    protected void onCancel() {
        create();
    }

    private interface CheckCreateCallback {
        void valid();
        void invalid(final SecurityManagementException exception);
        void error(final Throwable error);
    }

    private void checkCreate(final String identifier, final CheckCreateCallback callback) {
        showLoadingBox();
        userSystemManager.users(new RemoteCallback<User>() {
            @Override
            public void callback(User o) {
                // User found, so identifier is not valid.
                hideLoadingBox();
                callback.invalid(new EntityAlreadyExistsException(identifier,
                        UsersManagementWidgetsConstants.INSTANCE.userAlreadyExists()));
            }
        }, new ErrorCallback<Message>() {
            @Override
            public boolean error(Message o, Throwable throwable) {
                hideLoadingBox();
                if (throwable instanceof UserNotFoundException) {
                    // User not found, so identifier is valid.
                    callback.valid();
                } else {
                    callback.error(throwable);
                }
                return false;
            }
        }).get(identifier);
    }
    
    @Override
    protected void doClear() {
        super.doClear();
        createEntity.clear();
    }

    void onEditUserEvent(@Observes final OnEditEvent onEditEvent) {
        if (checkEventContext(onEditEvent, getUserEditor())) {
            edit();
        }
    }

    void onDeleteUserEvent(@Observes final OnDeleteEvent onDeleteEvent) {
        if (checkEventContext(onDeleteEvent, getUserEditor())) {
            doDelete();
        }
    }

    void onChangeUserPasswordEvent(@Observes final OnChangePasswordEvent onChangePasswordEvent) {
        if (checkEventContext(onChangePasswordEvent, getUserEditor())) {
            doChangePassword();
        }
    }


    void onAttributeCreated(@Observes final CreateUserAttributeEvent createUserAttributeEvent) {
        if (checkEventContext(createUserAttributeEvent, getUserEditor().attributesEditor())) {
            setDirty(true);
        }
    }

    void onAttributeDeleted(@Observes final DeleteUserAttributeEvent deleteUserAttributeEvent) {
        if (checkEventContext(deleteUserAttributeEvent, getUserEditor().attributesEditor())) {
            setDirty(true);
        }
    }

    void onAttributeUpdated(@Observes final UpdateUserAttributeEvent updateUserAttributeEvent) {
        if (checkEventContext(updateUserAttributeEvent, getUserEditor().attributesEditor())) {
            setDirty(true);
        }
    }

    void onOnRemoveUserGroupEvent(@Observes final OnRemoveUserGroupEvent onRemoveUserGroupEvent) {
        if (checkEventContext(onRemoveUserGroupEvent, getUserEditor().groupsExplorer())) {
            setDirty(true);
        }
    }

    void onOnUserGroupsUpdatedEvent(@Observes final OnUpdateUserGroupsEvent onUpdateUserGroupsEvent) {
        if (checkEventContext(onUpdateUserGroupsEvent, getUserEditor().groupsEditor())) {
            setDirty(true);
        }
    }
    
    void onOnRemoveUserRoleEvent(@Observes final OnRemoveUserRoleEvent onRemoveUserRoleEvent) {
        if (checkEventContext(onRemoveUserRoleEvent, getUserEditor().rolesExplorer())) {
            setDirty(true);
        }
    }

    void onOnUserRolesUpdatedEvent(@Observes final OnUpdateUserRolesEvent onUpdateUserRolesEvent) {
        if (checkEventContext(onUpdateUserRolesEvent, getUserEditor().rolesEditor())) {
            setDirty(true);
        }
    }

}
