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

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.security.shared.api.Group;
import org.jboss.errai.security.shared.api.Role;
import org.jboss.errai.security.shared.api.identity.User;
import org.uberfire.ext.security.management.client.ClientUserSystemManager;
import org.uberfire.ext.security.management.client.editor.user.UserEditorDriver;
import org.uberfire.ext.security.management.client.resources.i18n.UsersManagementWidgetsConstants;
import org.uberfire.ext.security.management.client.resources.i18n.UsersManagementWidgetsMessages;
import org.uberfire.ext.security.management.client.widgets.management.ChangePassword;
import org.uberfire.ext.security.management.client.widgets.management.editor.user.UserEditor;
import org.uberfire.ext.security.management.client.widgets.management.editor.workflow.EntityWorkflowView;
import org.uberfire.ext.security.management.client.widgets.management.events.ContextualEvent;
import org.uberfire.ext.security.management.client.widgets.management.events.DeleteUserEvent;
import org.uberfire.ext.security.management.client.widgets.management.events.OnErrorEvent;
import org.uberfire.ext.security.management.client.widgets.management.events.SaveUserEvent;
import org.uberfire.ext.security.management.client.widgets.popup.ConfirmBox;
import org.uberfire.ext.security.management.client.widgets.popup.LoadingBox;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.events.NotificationEvent;

import javax.enterprise.event.Event;
import java.util.LinkedHashSet;
import java.util.Set;

import static org.uberfire.workbench.events.NotificationEvent.NotificationType.SUCCESS;

/**
 * <p>The workflow for editing a user.</p>
 * <p>It links the editor & sub-editors components with the editor driver and the remote user services.</p>
 * 
 * @since 0.8.0
 */
public abstract class BaseUserEditorWorkflow implements IsWidget {

    protected ClientUserSystemManager userSystemManager;
    protected Event<OnErrorEvent> errorEvent;
    protected Event<NotificationEvent> workbenchNotification;
    protected Event<DeleteUserEvent> deleteUserEvent;
    protected Event<SaveUserEvent> saveUserEvent;
    protected ConfirmBox confirmBox;
    protected UserEditor userEditor;
    protected UserEditorDriver userEditorDriver;
    protected ChangePassword changePassword;
    protected LoadingBox loadingBox;
    public EntityWorkflowView view;

    protected User user;
    protected boolean isDirty;
    
    public BaseUserEditorWorkflow(final ClientUserSystemManager userSystemManager,
                                  final Event<OnErrorEvent> errorEvent,
                                  final Event<NotificationEvent> workbenchNotification,
                                  final Event<DeleteUserEvent> deleteUserEvent,
                                  final Event<SaveUserEvent> saveUserEvent,
                                  final ConfirmBox confirmBox,
                                  final UserEditor userEditor,
                                  final UserEditorDriver userEditorDriver,
                                  final ChangePassword changePassword,
                                  final LoadingBox loadingBox,
                                  final EntityWorkflowView view) {
        
        this.userSystemManager = userSystemManager;
        this.errorEvent = errorEvent;
        this.workbenchNotification = workbenchNotification;
        this.deleteUserEvent = deleteUserEvent;
        this.saveUserEvent = saveUserEvent;
        this.confirmBox = confirmBox;
        this.userEditor = userEditor;
        this.userEditorDriver = userEditorDriver;
        this.changePassword = changePassword;
        this.loadingBox = loadingBox;
        this.view = view;
        this.isDirty = false;
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    /*  ******************************************************************************************************
                                     PROTECTED PRESENTER API 
         ****************************************************************************************************** */
    
    protected void doShow(final String userId) {
        assert userId != null;
        
        // Configure the view.
        doShowEditorView();
        
        // Start the show workflow's logic.
        checkDirty(new Command() {
            @Override
            public void execute() {
                clear();
                // Call backend service.
                showLoadingBox();
                userSystemManager.users(new RemoteCallback<User>() {
                    @Override
                    public void callback(User o) {
                        hideLoadingBox();
                        BaseUserEditorWorkflow.this.user = o;
                        assert user != null;
                        
                        userEditorDriver.show(user, userEditor);
                        view.setCancelButtonVisible(false);
                        view.setSaveButtonVisible(false);
                    }
                }, errorCallback).get(userId);
            }
        });
        
    }
    
    public void clear() {
        checkDirty(new Command() {
            @Override
            public void execute() {
                doClear();
            }
        });
    }

    public UserEditor getUserEditor() {
        return userEditor;
    }

    protected void onSave() {
        doSave();
    }
    
    protected void onCancel() {
        doShow(BaseUserEditorWorkflow.this.user.getIdentifier());
    }

    protected void doShowEditorView() {
        view.setWidget(userEditor.view)
                .setCancelButtonVisible(true)
                .setSaveButtonVisible(true)
                .setSaveButtonEnabled(isDirty)
                .setSaveButtonText(getSaveButtonText())
                .setCallback(new EntityWorkflowView.Callback() {
                    @Override
                    public void onSave() {
                        BaseUserEditorWorkflow.this.onSave();
                    }

                    @Override
                    public void onCancel() {
                        BaseUserEditorWorkflow.this.onCancel();
                    }
                });
    }

    protected String getSaveButtonText() {
        return UsersManagementWidgetsConstants.INSTANCE.saveChanges();
    }

    protected  void setDirty(final boolean isDirty) {
        this.isDirty = isDirty;
        view.setSaveButtonEnabled(isDirty);
        if (isDirty) {
            view.showNotification(UsersManagementWidgetsMessages.INSTANCE.userModified(BaseUserEditorWorkflow.this.user.getIdentifier()));
        } else {
            view.clearNotification();
        }
    }
    
    protected void edit() {
        userEditorDriver.edit(user, userEditor);
        view.setCancelButtonVisible(true);
        view.setSaveButtonEnabled(false);
        view.setSaveButtonVisible(true);
    }
    
    protected void doSave() {
        assert user != null;

        final boolean isValid = userEditorDriver.flush();
        this.user = userEditorDriver.getValue();

        if (isValid) {
            final RemoteCallback<User> assignGroupsCallback = new RemoteCallback<User>() {
                @Override
                public void callback(final User user) {
                doAssignGroups(new Command() {
                    @Override
                    public void execute() {
                        doAssignRoles(new Command() {
                            @Override
                            public void execute() {
                                hideLoadingBox();
                                BaseUserEditorWorkflow.this.isDirty = false;
                                // Ask for the user's password if user is just created.
                                final String id = user.getIdentifier();
                                afterSave(id);
                            }
                        });
                    }
                });                    
                }
            };
            
            // Update the wrapped user instance from the modifiable one and assign updated groups if update op is successful.
            showLoadingBox();
            doSaveRemoteServiceCall(assignGroupsCallback);
            
        } else {
            throw new RuntimeException("User must be valid before updating it.");
        }
    }
    
    protected void doAssignGroups(final Command callback) {
        if (userEditor.canAssignGroups()) {
            userSystemManager.users(new RemoteCallback<Void>() {
                @Override
                public void callback(Void aVoid) {
                    callback.execute();
                }
            }, errorCallback).assignGroups(user.getIdentifier(), getGroupNames());
        } else {
            callback.execute();
        }
    }

    protected void doAssignRoles(final Command callback) {
        if (userEditor.canAssignRoles()) {
            userSystemManager.users(new RemoteCallback<Void>() {
                @Override
                public void callback(Void aVoid) {
                    callback.execute();
                }
            }, errorCallback).assignRoles(user.getIdentifier(), getRoleNames());
        } else {
            callback.execute();
        }
    }
    
    protected void doSaveRemoteServiceCall(final RemoteCallback<User> callback) {
        userSystemManager.users(callback, errorCallback).update(user);
    }
    
    protected void afterSave(final String id) {
        workbenchNotification.fire(new NotificationEvent(UsersManagementWidgetsMessages.INSTANCE.userSaved(id), SUCCESS));
        saveUserEvent.fire(new SaveUserEvent(id));
        doShow(user.getIdentifier());
    }

    protected void doDelete() {
        if (user != null && user.getIdentifier() != null) {
            confirmBox.show(UsersManagementWidgetsConstants.INSTANCE.confirmAction(), UsersManagementWidgetsConstants.INSTANCE.ensureRemoveUser(),
                    new Command() {
                        @Override
                        public void execute() {
                            final String id = user.getIdentifier();
                            showLoadingBox();
                            userSystemManager.users(new RemoteCallback<Void>() {
                                @Override
                                public void callback(Void o) {
                                    hideLoadingBox();
                                    final String id = user.getIdentifier();
                                    deleteUserEvent.fire(new DeleteUserEvent(id));
                                    workbenchNotification.fire(new NotificationEvent(UsersManagementWidgetsMessages.INSTANCE.userRemoved(id), SUCCESS));
                                    clear();
                                }
                            }, errorCallback).delete(id);
                        }
                    });
        }
    }

    protected void doChangePassword() {
        showPasswordPopup(null);
    }

    protected void showPasswordPopup(final ChangePassword.ChangePasswordCallback callback) {
        changePassword.show(user.getIdentifier(), callback);
    }
    
    protected Set<String> getGroupNames() {
        final Set<String> result = new LinkedHashSet<String>(user.getGroups().size());
        for (final Group group : user.getGroups()) {
            result.add(group.getName());
        }
        return result;
    }

    protected Set<String> getRoleNames() {
        final Set<String> result = new LinkedHashSet<String>(user.getRoles().size());
        for (final Role role : user.getRoles()) {
            result.add(role.getName());
        }
        return result;
    }

    protected void doClear() {
        view.clearNotification();
        userEditor.clear();
        user = null;
        setDirty(false);
    }

    protected boolean checkEventContext(final ContextualEvent contextualEvent, final Object context) {
        return contextualEvent != null && contextualEvent.getContext() != null && contextualEvent.getContext().equals(context);
    }

    protected final ErrorCallback<Message> errorCallback = new ErrorCallback<Message>() {
        @Override
        public boolean error(final Message message, final Throwable throwable) {
            hideLoadingBox();
            showError(throwable);
            return false;
        }
    };

    protected void showError(final Throwable throwable) {
        final String msg = throwable != null ? throwable.getMessage() : UsersManagementWidgetsConstants.INSTANCE.genericError();
        showError(msg);
    }

    protected void showError(final String message) {
        errorEvent.fire(new OnErrorEvent(BaseUserEditorWorkflow.this, message));
    }

    protected void checkDirty(final Command callback) {
        if (isDirty) {
            confirmBox.show(UsersManagementWidgetsConstants.INSTANCE.confirmAction(),
                    UsersManagementWidgetsConstants.INSTANCE.userIsDirty(),
                    new Command() {
                        @Override
                        public void execute() {
                            BaseUserEditorWorkflow.this.isDirty = false;
                            callback.execute();
                        }
                    });
        } else {
            callback.execute();
        }
    }

    protected void showLoadingBox() {
        loadingBox.show();
    }
    
    protected void hideLoadingBox() {
        loadingBox.hide();
    }
}
