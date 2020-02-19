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

import java.util.Collection;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.security.shared.api.Group;
import org.jboss.errai.security.shared.api.GroupImpl;
import org.uberfire.backend.authz.AuthorizationService;
import org.uberfire.ext.security.management.api.exception.GroupNotFoundException;
import org.uberfire.ext.security.management.api.exception.InvalidEntityIdentifierException;
import org.uberfire.ext.security.management.api.exception.SecurityManagementException;
import org.uberfire.ext.security.management.client.ClientUserSystemManager;
import org.uberfire.ext.security.management.client.resources.i18n.UsersManagementClientConstants;
import org.uberfire.ext.security.management.client.resources.i18n.UsersManagementWidgetsConstants;
import org.uberfire.ext.security.management.client.widgets.management.CreateEntity;
import org.uberfire.ext.security.management.client.widgets.management.editor.group.GroupUsersAssignment;
import org.uberfire.ext.security.management.client.widgets.management.editor.workflow.EntityWorkflowView;
import org.uberfire.ext.security.management.client.widgets.management.events.AddUsersToGroupEvent;
import org.uberfire.ext.security.management.client.widgets.management.events.CreateGroupEvent;
import org.uberfire.ext.security.management.client.widgets.management.events.OnErrorEvent;
import org.uberfire.ext.security.management.client.widgets.popup.ConfirmBox;
import org.uberfire.ext.security.management.client.widgets.popup.LoadingBox;
import org.uberfire.mvp.Command;
import org.uberfire.security.authz.AuthorizationPolicy;
import org.uberfire.security.authz.Permission;
import org.uberfire.security.authz.PermissionManager;
import org.uberfire.workbench.events.NotificationEvent;

import static org.uberfire.workbench.events.NotificationEvent.NotificationType.INFO;

/**
 * <p>Main entry point for creating a group instance.</p>
 *
 * @since 0.8.0
 */
@Dependent
public class GroupCreationWorkflow implements IsWidget {

    ClientUserSystemManager userSystemManager;
    Caller<AuthorizationService> authorizationService;
    PermissionManager permissionManager;
    Event<OnErrorEvent> errorEvent;
    ConfirmBox confirmBox;
    LoadingBox loadingBox;
    final ErrorCallback<Message> errorCallback = new ErrorCallback<Message>() {
        @Override
        public boolean error(final Message message,
                             final Throwable throwable) {
            loadingBox.hide();
            showError(throwable);
            return false;
        }
    };
    Event<NotificationEvent> workbenchNotification;
    CreateEntity createEntity;
    GroupUsersAssignment groupUsersAssignment;
    EntityWorkflowView view;
    Event<CreateGroupEvent> onCreateGroupEvent;
    Group group;

    static final String PERSPECTIVE = "perspective";
    static final String ACCESS = "read";

    @Inject
    public GroupCreationWorkflow(final ClientUserSystemManager userSystemManager,
                                 final Caller<AuthorizationService> authorizationService,
                                 final PermissionManager permissionManager,
                                 final Event<OnErrorEvent> errorEvent,
                                 final ConfirmBox confirmBox,
                                 final LoadingBox loadingBox,
                                 final Event<NotificationEvent> workbenchNotification,
                                 final CreateEntity createEntity,
                                 final GroupUsersAssignment groupUsersAssignment,
                                 final Event<CreateGroupEvent> onCreateGroupEvent,
                                 final EntityWorkflowView view) {
        this.userSystemManager = userSystemManager;
        this.authorizationService = authorizationService;
        this.permissionManager = permissionManager;
        this.errorEvent = errorEvent;
        this.confirmBox = confirmBox;
        this.loadingBox = loadingBox;
        this.createEntity = createEntity;
        this.groupUsersAssignment = groupUsersAssignment;
        this.workbenchNotification = workbenchNotification;
        this.onCreateGroupEvent = onCreateGroupEvent;
        this.view = view;
    }
    
     /*  ******************************************************************************************************
                                     PUBLIC PRESENTER API 
         ****************************************************************************************************** */

    @PostConstruct
    public void init() {

    }

    public void create() {
        clear();

        // Configure the view with the create entity component first.
        view.setWidget(createEntity.asWidget())
                .setSaveButtonVisible(true)
                .setSaveButtonEnabled(true)
                .setSaveButtonText(UsersManagementWidgetsConstants.INSTANCE.next())
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
        view.clearNotifications();
        createEntity.clear();
        groupUsersAssignment.clear();
        group = null;
    }
    
    /*  ******************************************************************************************************
                                 PRIVATE METHODS AND VALIDATORS
     ****************************************************************************************************** */

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    protected void showUsersAssignment(final String name) {
        assert group != null;

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
            if (null != constrainedGroups && constrainedGroups.contains(identifier)) {

                loadingBox.hide();

                // Registered role found with this identifier, so name is not valid.
                showErrorMessage(UsersManagementClientConstants.INSTANCE.roleAlreadyExists());
                createEntity.setErrorState();
            } else {

                userSystemManager.groups(o -> {
                                             loadingBox.hide();

                                             // Group found, so name is not valid.
                                             showErrorMessage(UsersManagementClientConstants.INSTANCE.groupAlreadyExists());
                                             createEntity.setErrorState();
                                         },
                                         new ErrorCallback<Message>() {
                                             @Override
                                             public boolean error(final Message o,
                                                                  final Throwable throwable) {
                                                 loadingBox.hide();
                                                 Throwable error = throwable;
                                                 if (throwable instanceof GroupNotFoundException) {
                                                     // Group not found, so name is valid.
                                                     createGroup(identifier);
                                                     error = null;
                                                 } else if (throwable instanceof InvalidEntityIdentifierException) {
                                                     error = new SecurityManagementException(getGroupIdentifierNotValidMessage((InvalidEntityIdentifierException) throwable),
                                                                                             throwable);
                                                 }
                                                 // On error,
                                                 if (null != error) {
                                                     showError(throwable);
                                                     create();
                                                 }
                                                 return false;
                                             }
                                         }).get(identifier);
            }
        }
    }

    private String getGroupIdentifierNotValidMessage(final InvalidEntityIdentifierException e) {
        return UsersManagementWidgetsConstants.INSTANCE.invalidGroupName() +
                " [" + e.getIdentifier() + "]. " +
                UsersManagementWidgetsConstants.INSTANCE.patternAlphanumericSymbols() +
                " [" + e.getSymbolsAccepted() + "]";
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
                                             showUsersAssignment(name);
                                         } else {
                                             confirmBox.show(UsersManagementWidgetsConstants.INSTANCE.confirmAction(),
                                                             UsersManagementWidgetsConstants.INSTANCE.assignUsersToGroupName() + " " + name,
                                                             new Command() {
                                                                 @Override
                                                                 public void execute() {
                                                                     showUsersAssignment(name);
                                                                 }
                                                             },
                                                             new Command() {
                                                                 @Override
                                                                 public void execute() {
                                                                     fireGroupCreated(name);
                                                                     create();
                                                                 }
                                                             });
                                         }
                                     }
                                 },
                                 errorCallback).create(_group);
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

        final String name = group.getName();
        final boolean isEmptyUsersAllowed = userSystemManager.getGroupManagerSettings().allowEmpty();
        final boolean isEmpty = users == null || users.isEmpty();
        if (!isEmptyUsersAllowed && isEmpty) {
            showErrorMessage(UsersManagementWidgetsConstants.INSTANCE.groupMustHaveAtLeastOneUser());
            showUsersAssignment(name);
        } else {
            loadingBox.show();
            userSystemManager.groups(new RemoteCallback<Void>() {
                                         @Override
                                         public void callback(Void o) {
                                             loadingBox.hide();
                                             fireUsersAssigned(name);
                                             create();
                                         }
                                     },
                                     errorCallback).assignUsers(name,
                                                                users);
        }
    }

    protected void fireGroupCreated(final String name) {
        workbenchNotification.fire(new NotificationEvent(UsersManagementWidgetsConstants.INSTANCE.groupCreated(name) + " " + name,
                                                         INFO));
        onCreateGroupEvent.fire(new CreateGroupEvent(name));
    }

    protected void fireUsersAssigned(final String name) {
        workbenchNotification.fire(new NotificationEvent(UsersManagementWidgetsConstants.INSTANCE.usersAssigned(name),
                                                         INFO));
        onCreateGroupEvent.fire(new CreateGroupEvent(name));
    }

    void showErrorMessage(final String message) {
        showError(new SecurityManagementException(message));
    }

    void showError(final Throwable throwable) {
        errorEvent.fire(new OnErrorEvent(GroupCreationWorkflow.this,
                                         throwable));
    }

    void onCreateGroupEvent(@Observes final CreateGroupEvent event) {
        AuthorizationPolicy authzPolicy = permissionManager.getAuthorizationPolicy();
        Group newGroup = new GroupImpl(event.getName());
        String permissionName = PERSPECTIVE + "." + ACCESS + "." + authzPolicy.getHomePerspective(newGroup);
        Permission permission = permissionManager.createPermission(permissionName, true);
        authzPolicy.addPermission(newGroup, permission);
        authorizationService.call(r -> {
        }, errorCallback).savePolicy(authzPolicy);
    }
}
