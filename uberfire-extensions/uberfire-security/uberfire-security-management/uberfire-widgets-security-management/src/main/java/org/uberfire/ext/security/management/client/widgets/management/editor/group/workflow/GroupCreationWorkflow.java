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

package org.uberfire.ext.security.management.client.widgets.management.editor.group.workflow;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.security.shared.api.Group;
import org.uberfire.ext.security.management.api.exception.GroupNotFoundException;
import org.uberfire.ext.security.management.client.ClientUserSystemManager;
import org.uberfire.ext.security.management.client.resources.i18n.UsersManagementWidgetsConstants;
import org.uberfire.ext.security.management.client.resources.i18n.UsersManagementWidgetsMessages;
import org.uberfire.ext.security.management.client.widgets.management.CreateEntity;
import org.uberfire.ext.security.management.client.widgets.management.editor.group.GroupUsersAssignment;
import org.uberfire.ext.security.management.client.widgets.management.editor.workflow.EntityWorkflowView;
import org.uberfire.ext.security.management.client.widgets.management.events.AddUsersToGroupEvent;
import org.uberfire.ext.security.management.client.widgets.management.events.CreateGroupEvent;
import org.uberfire.ext.security.management.client.widgets.management.events.OnErrorEvent;
import org.uberfire.ext.security.management.client.widgets.popup.ConfirmBox;
import org.uberfire.ext.security.management.client.widgets.popup.LoadingBox;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.events.NotificationEvent;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.util.Collection;
import java.util.Set;

import static org.uberfire.workbench.events.NotificationEvent.NotificationType.INFO;

/**
 * <p>Main entry point for creating a group instance.</p>
 * 
 * @since 0.8.0
 */
@Dependent
public class GroupCreationWorkflow implements IsWidget {

    ClientUserSystemManager userSystemManager;
    Event<OnErrorEvent> errorEvent;
    ConfirmBox confirmBox;
    LoadingBox loadingBox;
    Event<NotificationEvent> workbenchNotification;
    CreateEntity createEntity;
    GroupUsersAssignment groupUsersAssignment;
    EntityWorkflowView view;
    Event<CreateGroupEvent> onCreateGroupEvent;

    Group group;
    
    @Inject
    public GroupCreationWorkflow(final ClientUserSystemManager userSystemManager,
                                 final Event<OnErrorEvent> errorEvent,
                                 final ConfirmBox confirmBox, 
                                 final LoadingBox loadingBox,
                                 final Event<NotificationEvent> workbenchNotification,
                                 final CreateEntity createEntity,
                                 final GroupUsersAssignment groupUsersAssignment,
                                 final Event<CreateGroupEvent> onCreateGroupEvent,
                                 final EntityWorkflowView view) {
        this.userSystemManager = userSystemManager;
        this.errorEvent = errorEvent;
        this.confirmBox = confirmBox;
        this.loadingBox = loadingBox;
        this.createEntity = createEntity;
        this.groupUsersAssignment = groupUsersAssignment;
        this.workbenchNotification = workbenchNotification;
        this.onCreateGroupEvent = onCreateGroupEvent;
        this.view = view;
    }

    @PostConstruct
    public void init() {
        
    }
    
     /*  ******************************************************************************************************
                                     PUBLIC PRESENTER API 
         ****************************************************************************************************** */

    public void create() {
        clear();
        
        // Configure the view with the create entity component first.
        view.setWidget(createEntity.asWidget())
                .setSaveButtonVisible(true)
                .setSaveButtonEnabled(true)
                .setSaveButtonText(UsersManagementWidgetsConstants.INSTANCE.saveChanges())
                .setCancelButtonVisible(false)
                .setCallback(new EntityWorkflowView.Callback() {
                    @Override
                    public void onSave() {
                        GroupCreationWorkflow.this.checkCreate();
                    }

                    @Override
                    public void onCancel() {
                        create();
                    }
                });
        
        createEntity.show(UsersManagementWidgetsConstants.INSTANCE.inputGroupName(),
                UsersManagementWidgetsConstants.INSTANCE.groupName() + "...");
    }
    
    
    public void clear() {
        view.clearNotification();
        createEntity.clear();
        groupUsersAssignment.clear();
        group = null;
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }
    
    /*  ******************************************************************************************************
                                 PRIVATE METHODS AND VALIDATORS
     ****************************************************************************************************** */
    
    protected void showUsersAssignment() {
        assert group != null;
        final String name = group.getName();
        
        // Configure the view with the group's users assignment component.
        view.setWidget(groupUsersAssignment.asWidget())
                .setSaveButtonVisible(false)
                .setSaveButtonEnabled(false)
                .setSaveButtonText("")
                .setCancelButtonVisible(true)
                .setCallback(new EntityWorkflowView.Callback() {
                    @Override
                    public void onSave() {
                        // Save button not present. Listen to the AddUsersToGroupEvent instance fired. 
                    }

                    @Override
                    public void onCancel() {
                        create();
                    }
                });

        groupUsersAssignment.show(UsersManagementWidgetsConstants.INSTANCE.assignUsersToGroupName() + " " + name);
        
    }
    
    
    protected void checkCreate() {
        final String identifier = createEntity.getEntityIdentifier();
        if (identifier != null) {
            loadingBox.show();
            
            // Check constrained groups, they cannot be created (such as registered roles).
            final Collection<String> constrainedGroups = userSystemManager.getConstrainedGroups();
            if ( null != constrainedGroups && constrainedGroups.contains(identifier)) {
                
                loadingBox.hide();

                // Registered role found with this identifier, so name is not valid.
                errorEvent.fire(new OnErrorEvent(GroupCreationWorkflow.this, UsersManagementWidgetsConstants.INSTANCE.alreadyExistRegisteredRole()));
                createEntity.setErrorState();
                
            } else {
                
                userSystemManager.groups(new RemoteCallback<Group>() {
                    @Override
                    public void callback(final Group o) {
                        loadingBox.hide();

                        // Group found, so name is not valid.
                        errorEvent.fire(new OnErrorEvent(GroupCreationWorkflow.this, UsersManagementWidgetsConstants.INSTANCE.groupAlreadyExists()));
                        createEntity.setErrorState();
                    }
                }, new ErrorCallback<Message>() {
                    @Override
                    public boolean error(final Message o, final Throwable throwable) {
                        loadingBox.hide();
                        if (throwable instanceof GroupNotFoundException) {
                            // Group not found, so name is valid.
                            createGroup(identifier);
                        } else {
                            showError(throwable);
                            create();
                        }
                        return false;
                    }
                }).get(identifier);
                
            }
            
        }
    }
    
    protected void createGroup(final String name) {
        final Group _group = userSystemManager.createGroup(name);
        createEntity.clear();
        loadingBox.show();
        userSystemManager.groups(new RemoteCallback<Group>() {
            @Override
            public void callback(Group group) {
                GroupCreationWorkflow.this.group = group;
                final boolean isEmptyUsersAllowed = userSystemManager.getGroupManagerSettings().allowEmpty();
                loadingBox.hide();
                if (!isEmptyUsersAllowed) {
                    showUsersAssignment();
                } else {
                    confirmBox.show(UsersManagementWidgetsConstants.INSTANCE.confirmAction(), UsersManagementWidgetsConstants.INSTANCE.assignUsersToGroupName() + " " + name,
                            new Command() {
                                @Override
                                public void execute() {
                                    showUsersAssignment();
                                }
                            }, new Command() {
                                @Override
                                public void execute() {
                                    fireGroupCreated(name);
                                    create();
                                }
                            });
                }
            }
        }, errorCallback).create(_group);
    }

    void onAssignUsers(@Observes final AddUsersToGroupEvent addUsersToGroupEvent) {
        final Object editor = addUsersToGroupEvent.getContext();
        if (editor != null && editor.equals(groupUsersAssignment)) {
            final Set<String> users = addUsersToGroupEvent.getUsers();
            assignUsers(users);
        }
    }

    protected void assignUsers(final Collection<String> users) {
        assert group != null;
        
        final boolean isEmptyUsersAllowed = userSystemManager.getGroupManagerSettings().allowEmpty();
        final boolean isEmpty = users == null || users.isEmpty();
        if (!isEmptyUsersAllowed && isEmpty) {
            showError(UsersManagementWidgetsConstants.INSTANCE.groupMustHaveAtLeastOneUser());
            showUsersAssignment();
        } else {
            final String name = group.getName();
            loadingBox.show();
            userSystemManager.groups(new RemoteCallback<Void>() {
                @Override
                public void callback(Void o) {
                    loadingBox.hide();
                    fireUsersAssigned(name);
                    create();
                }
            }, errorCallback).assignUsers(name, users);
        }
    }

    protected void fireGroupCreated(final String name) {
        workbenchNotification.fire(new NotificationEvent(UsersManagementWidgetsMessages.INSTANCE.groupCreated(name) + " " + name, INFO));
        onCreateGroupEvent.fire(new CreateGroupEvent(name));
    }

    protected void fireUsersAssigned(final String name) {
        workbenchNotification.fire(new NotificationEvent(UsersManagementWidgetsMessages.INSTANCE.usersAssigned(name), INFO));
        onCreateGroupEvent.fire(new CreateGroupEvent(name));
    }
    
    final ErrorCallback<Message> errorCallback = new ErrorCallback<Message>() {
        @Override
        public boolean error(final Message message, final Throwable throwable) {
            loadingBox.hide();
            showError(throwable);
            return false;
        }
    };

    protected void showError(final Throwable throwable) {
        final String msg = throwable != null ? throwable.getMessage() : UsersManagementWidgetsConstants.INSTANCE.genericError();
        showError(msg);
    }

    protected void showError(final String message) {
        errorEvent.fire(new OnErrorEvent(GroupCreationWorkflow.this, message));
    }
    
}
