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
import org.jboss.errai.security.shared.api.Group;
import org.jboss.errai.security.shared.api.GroupImpl;
import org.uberfire.backend.authz.AuthorizationService;
import org.uberfire.client.authz.PerspectiveAction;
import org.uberfire.client.mvp.PerspectiveActivity;
import org.uberfire.ext.security.management.client.ClientUserSystemManager;
import org.uberfire.ext.security.management.client.editor.group.GroupEditorDriver;
import org.uberfire.ext.security.management.client.resources.i18n.UsersManagementWidgetsConstants;
import org.uberfire.ext.security.management.client.widgets.management.editor.group.GroupEditor;
import org.uberfire.ext.security.management.client.widgets.management.editor.workflow.EntityWorkflowView;
import org.uberfire.ext.security.management.client.widgets.management.events.ContextualEvent;
import org.uberfire.ext.security.management.client.widgets.management.events.CreateGroupEvent;
import org.uberfire.ext.security.management.client.widgets.management.events.DeleteGroupEvent;
import org.uberfire.ext.security.management.client.widgets.management.events.HomePerspectiveChangedEvent;
import org.uberfire.ext.security.management.client.widgets.management.events.OnDeleteEvent;
import org.uberfire.ext.security.management.client.widgets.management.events.OnEditEvent;
import org.uberfire.ext.security.management.client.widgets.management.events.OnErrorEvent;
import org.uberfire.ext.security.management.client.widgets.management.events.PermissionChangedEvent;
import org.uberfire.ext.security.management.client.widgets.management.events.PermissionNodeAddedEvent;
import org.uberfire.ext.security.management.client.widgets.management.events.PermissionNodeRemovedEvent;
import org.uberfire.ext.security.management.client.widgets.management.events.PriorityChangedEvent;
import org.uberfire.ext.security.management.client.widgets.management.events.SaveGroupEvent;
import org.uberfire.ext.security.management.client.widgets.popup.ConfirmBox;
import org.uberfire.ext.security.management.client.widgets.popup.LoadingBox;
import org.uberfire.mvp.Command;
import org.uberfire.security.authz.AuthorizationPolicy;
import org.uberfire.security.authz.AuthorizationResult;
import org.uberfire.security.authz.Permission;
import org.uberfire.security.authz.PermissionCollection;
import org.uberfire.security.authz.PermissionManager;
import org.uberfire.workbench.events.NotificationEvent;

import static org.uberfire.workbench.events.NotificationEvent.NotificationType.INFO;
import static org.uberfire.workbench.events.NotificationEvent.NotificationType.SUCCESS;

/**
 * <p>Main entry point for viewing a group instance.</p>
 *
 * @since 0.8.0
 */
@Dependent
public class GroupEditorWorkflow implements IsWidget {

    public EntityWorkflowView view;
    ClientUserSystemManager userSystemManager;
    Caller<AuthorizationService> authorizationService;
    PermissionManager permissionManager;
    Event<OnErrorEvent> errorEvent;
    protected final ErrorCallback<Message> errorCallback = (Message message, Throwable throwable) -> {
        hideLoadingBox();
        showError(throwable);
        return false;
    };
    Event<NotificationEvent> workbenchNotification;
    Event<SaveGroupEvent> saveGroupEvent;
    Event<DeleteGroupEvent> deleteGroupEvent;
    ConfirmBox confirmBox;
    LoadingBox loadingBox;
    GroupEditor groupEditor;
    GroupEditorDriver groupEditorDriver;
    Group group;
    boolean isDirty;
    PerspectiveActivity selectedHomePerspective = null;

    @Inject
    public GroupEditorWorkflow(final ClientUserSystemManager userSystemManager,
                               final Caller<AuthorizationService> authorizationService,
                               final PermissionManager permissionManager,
                               final Event<OnErrorEvent> errorEvent,
                               final ConfirmBox confirmBox,
                               final LoadingBox loadingBox,
                               final Event<NotificationEvent> workbenchNotification,
                               final Event<SaveGroupEvent> saveGroupEvent,
                               final Event<DeleteGroupEvent> deleteGroupEvent,
                               final GroupEditor groupEditor,
                               final GroupEditorDriver groupEditorDriver,
                               final EntityWorkflowView view) {
        this.userSystemManager = userSystemManager;
        this.authorizationService = authorizationService;
        this.permissionManager = permissionManager;
        this.errorEvent = errorEvent;
        this.confirmBox = confirmBox;
        this.workbenchNotification = workbenchNotification;
        this.saveGroupEvent = saveGroupEvent;
        this.deleteGroupEvent = deleteGroupEvent;
        this.groupEditor = groupEditor;
        this.view = view;
        this.groupEditorDriver = groupEditorDriver;
        this.loadingBox = loadingBox;
        this.isDirty = false;
    }

    @PostConstruct
    public void setup() {

    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    public GroupEditor getGroupEditor() {
        return groupEditor;
    }

    public void show(final String name) {
        doShow(name);
    }

    protected String getSaveButtonText() {
        return UsersManagementWidgetsConstants.INSTANCE.saveChanges();
    }

    protected void onSave() {
        doSave();
    }

    protected void onCancel() {
        doShow(group.getName());
    }

    public void clear() {
        groupEditor.clear();
        view.clearNotifications();
        group = null;
    }

    public boolean isDirty() {
        return isDirty;
    }

    /*  ******************************************************************************************************
                                 PROTECTED PRESENTER API
     ****************************************************************************************************** */

    void delete() {
        final String name = group.getName();
        userSystemManager.groups((Void v) -> {
                                     doDelete();
                                     clear();
                                 },
                                 errorCallback).delete(name);
    }

    protected void doDelete() {
        final String name = group.getName();
        AuthorizationPolicy authzPolicy = permissionManager.getAuthorizationPolicy();
        showLoadingBox();
        authorizationService.call(r -> {
                                      hideLoadingBox();
                                      deleteGroupEvent.fire(new DeleteGroupEvent(name));
                                      workbenchNotification.fire(new NotificationEvent(UsersManagementWidgetsConstants.INSTANCE.groupRemoved(name),
                                                                                       INFO));
                                  },
                                  errorCallback).deletePolicyByGroup(group, authzPolicy);
    }

    protected void doShow(final String groupName) {
        assert groupName != null;

        // Configure the view.
        doInitView();

        // Start the workflow's logic.
        checkDirty(() -> doLoad(groupName));
    }

    protected void doInitView() {
        // Configure the workflow view.
        view.setWidget(groupEditor.asWidget())
                .setCancelButtonVisible(true)
                .setSaveButtonVisible(true)
                .setSaveButtonEnabled(isDirty)
                .setSaveButtonText(getSaveButtonText())
                .setCallback(new EntityWorkflowView.Callback() {
                    @Override
                    public void onSave() {
                        GroupEditorWorkflow.this.onSave();
                    }

                    @Override
                    public void onCancel() {
                        GroupEditorWorkflow.this.onCancel();
                    }
                });
    }

    protected void doLoad(String name) {
        clear();

        // Call backend service.
        showLoadingBox();
        userSystemManager.groups((Group o) -> {
                                     hideLoadingBox();
                                     GroupEditorWorkflow.this.group = o;
                                     assert group != null;

                                     edit();
                                 },
                                 errorCallback).get(name);
    }

    protected void doSave() {
        assert group != null;

        final boolean isValid = groupEditorDriver.flush();
        this.group = groupEditorDriver.getValue();
        PermissionCollection groupPermissions = groupEditorDriver.getPermissions();
        PerspectiveActivity homePerspective = groupEditorDriver.getHomePerspective();
        int groupPriority = groupEditorDriver.getGroupPriority();

        if (isValid) {
            showLoadingBox();

            // Update the current active policy
            AuthorizationPolicy authzPolicy = permissionManager.getAuthorizationPolicy();
            authzPolicy.setHomePerspective(group, homePerspective.getIdentifier());
            authzPolicy.setPriority(group, groupPriority);
            authzPolicy.setPermissions(group, groupPermissions);

            // Save the policy in the backend
            authorizationService.call(r -> {

                                          hideLoadingBox();
                                          isDirty = false;
                                          workbenchNotification.fire(new NotificationEvent(UsersManagementWidgetsConstants.INSTANCE.groupSaved(group.getName()),
                                                                                           SUCCESS));
                                          saveGroupEvent.fire(new SaveGroupEvent(group.getName()));
                                          doShow(group.getName());
                                      },
                                      errorCallback).savePolicy(authzPolicy);
        } else {
            throw new RuntimeException("Group must be valid before updating it.");
        }
    }

    protected void showNotification(String message) {
        view.showNotification(message);
    }

    protected void setDirty(final boolean isDirty) {
        this.isDirty = isDirty;
        view.setSaveButtonVisible(isDirty);
        view.setSaveButtonEnabled(isDirty);
        view.setCancelButtonVisible(true);
        if (isDirty) {
            view.showNotification(UsersManagementWidgetsConstants.INSTANCE.groupModified(group.getName()));
        } else {
            view.clearNotifications();
        }
    }

    protected void checkDirty(final Command callback) {
        if (isDirty) {
            confirmBox.show(UsersManagementWidgetsConstants.INSTANCE.confirmAction(),
                            UsersManagementWidgetsConstants.INSTANCE.groupIsDirty(),
                            () -> {
                                GroupEditorWorkflow.this.isDirty = false;
                                callback.execute();
                            },
                            () -> {
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

    void showError(final Throwable throwable) {
        errorEvent.fire(new OnErrorEvent(GroupEditorWorkflow.this,
                                         throwable));
    }

    // Event observers

    protected void edit() {
        groupEditorDriver.edit(group,
                               groupEditor);
        view.setCancelButtonVisible(false);
        view.setSaveButtonVisible(false);

        selectedHomePerspective = groupEditor.getAclSettings().getHomePerspective();
        if (isPerspectiveReadDenied(selectedHomePerspective)) {
            showNotification(UsersManagementWidgetsConstants.INSTANCE.homePerspectiveReadDenied());
        }
    }

    void onEditGroupEvent(@Observes final OnEditEvent onEditEvent) {
        if (checkEventContext(onEditEvent,
                              groupEditor)) {
            edit();
        }
    }

    void onDeleteGroupEvent(@Observes final OnDeleteEvent onDeleteEvent) {
        if (checkEventContext(onDeleteEvent,
                              groupEditor)) {
            confirmBox.show(UsersManagementWidgetsConstants.INSTANCE.confirmAction(),
                            UsersManagementWidgetsConstants.INSTANCE.ensureRemoveGroup(),
                            this::delete,
                            () -> {
                            });
        }
    }

    void onHomePerspectiveChangedEvent(@Observes final HomePerspectiveChangedEvent event) {
        if (checkEventContext(event,
                              groupEditor.getAclSettings())) {
            selectedHomePerspective = event.getPerspective();
            checkStatus();
        }
    }

    void onPriorityChangedEvent(@Observes final PriorityChangedEvent event) {
        if (checkEventContext(event,
                              groupEditor.getAclSettings())) {
            checkStatus();
        }
    }

    void onPermissionChangedEvent(@Observes final PermissionChangedEvent event) {
        if (checkEventContext(event,
                              groupEditor.getAclEditor())) {
            checkStatus();
        }
    }

    void onPermissionAddedEvent(@Observes final PermissionNodeAddedEvent event) {
        if (checkEventContext(event,
                              groupEditor.getAclEditor())) {
            checkStatus();
        }
    }

    void onPermissionRemovedEvent(@Observes final PermissionNodeRemovedEvent event) {
        if (checkEventContext(event,
                              groupEditor.getAclEditor())) {
            checkStatus();
        }
    }

    protected boolean checkEventContext(final ContextualEvent contextualEvent,
                                        final Object context) {
        return contextualEvent != null && contextualEvent.getContext() != null && contextualEvent.getContext().equals(context);
    }

    protected void checkStatus() {
        boolean readDenied = isPerspectiveReadDenied(selectedHomePerspective);
        if (readDenied) {
            setDirty(false);
            showNotification(UsersManagementWidgetsConstants.INSTANCE.homePerspectiveReadDenied());
        } else {
            setDirty(true);
        }
    }

    protected boolean isPerspectiveReadDenied(PerspectiveActivity perspectiveActivity) {
        if (perspectiveActivity == null) {
            return false;
        }
        PermissionCollection permissionCollection = groupEditor.permissions();
        if (permissionCollection == null) {
            return false;
        }
        Permission p = permissionManager.createPermission(perspectiveActivity,
                                                          PerspectiveAction.READ,
                                                          false);
        Permission existing = permissionCollection.get(p.getName());
        if (existing != null) {
            return existing.getResult().equals(AuthorizationResult.ACCESS_DENIED);
        }
        return permissionCollection.implies(p);
    }
}
