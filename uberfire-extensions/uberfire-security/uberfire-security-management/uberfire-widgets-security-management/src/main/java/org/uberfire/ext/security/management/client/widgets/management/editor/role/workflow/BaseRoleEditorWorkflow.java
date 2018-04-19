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

package org.uberfire.ext.security.management.client.widgets.management.editor.role.workflow;

import java.util.Collection;
import javax.enterprise.event.Event;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.security.shared.api.Role;
import org.uberfire.backend.authz.AuthorizationService;
import org.uberfire.client.mvp.PerspectiveActivity;
import org.uberfire.ext.security.management.client.ClientUserSystemManager;
import org.uberfire.ext.security.management.client.editor.role.RoleEditorDriver;
import org.uberfire.ext.security.management.client.resources.i18n.UsersManagementWidgetsConstants;
import org.uberfire.ext.security.management.client.widgets.management.editor.role.RoleEditor;
import org.uberfire.ext.security.management.client.widgets.management.editor.workflow.EntityWorkflowView;
import org.uberfire.ext.security.management.client.widgets.management.events.ContextualEvent;
import org.uberfire.ext.security.management.client.widgets.management.events.OnErrorEvent;
import org.uberfire.ext.security.management.client.widgets.management.events.SaveRoleEvent;
import org.uberfire.ext.security.management.client.widgets.popup.ConfirmBox;
import org.uberfire.ext.security.management.client.widgets.popup.LoadingBox;
import org.uberfire.mvp.Command;
import org.uberfire.security.authz.AuthorizationPolicy;
import org.uberfire.security.authz.Permission;
import org.uberfire.security.authz.PermissionCollection;
import org.uberfire.security.authz.PermissionManager;
import org.uberfire.workbench.events.NotificationEvent;

import static org.uberfire.workbench.events.NotificationEvent.NotificationType.SUCCESS;

/**
 * <p>The workflow for editing a role.</p>
 * <p>It links the editor & sub-editors components with the editor driver and the remote user services.</p>
 * @since 0.9.0
 */
public abstract class BaseRoleEditorWorkflow implements IsWidget {

    public EntityWorkflowView view;
    protected ClientUserSystemManager userSystemManager;
    protected Caller<AuthorizationService> authorizationService;
    protected PermissionManager permissionManager;
    protected Event<OnErrorEvent> errorEvent;
    protected Event<NotificationEvent> workbenchNotification;
    protected Event<SaveRoleEvent> saveRoleEvent;
    protected ConfirmBox confirmBox;
    protected RoleEditor roleEditor;
    protected RoleEditorDriver roleEditorDriver;
    protected LoadingBox loadingBox;
    protected final ErrorCallback<Message> errorCallback = (message, throwable) -> {
        hideLoadingBox();
        showError(throwable);
        return false;
    };
    protected Role role;
    protected boolean isDirty;

    public BaseRoleEditorWorkflow(final ClientUserSystemManager userSystemManager,
                                  final Caller<AuthorizationService> authorizationService,
                                  final PermissionManager permissionManager,
                                  final Event<OnErrorEvent> errorEvent,
                                  final Event<NotificationEvent> workbenchNotification,
                                  final Event<SaveRoleEvent> saveRoleEvent,
                                  final ConfirmBox confirmBox,
                                  final RoleEditor roleEditor,
                                  final RoleEditorDriver roleEditorDriver,
                                  final LoadingBox loadingBox,
                                  final EntityWorkflowView view) {

        this.userSystemManager = userSystemManager;
        this.authorizationService = authorizationService;
        this.permissionManager = permissionManager;
        this.errorEvent = errorEvent;
        this.workbenchNotification = workbenchNotification;
        this.saveRoleEvent = saveRoleEvent;
        this.confirmBox = confirmBox;
        this.roleEditor = roleEditor;
        this.roleEditorDriver = roleEditorDriver;
        this.loadingBox = loadingBox;
        this.view = view;
        this.isDirty = false;
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    public RoleEditor getRoleEditor() {
        return roleEditor;
    }

    /*  ******************************************************************************************************
                                     PROTECTED PRESENTER API
         ****************************************************************************************************** */

    public void clear() {
        view.clearNotifications();
        roleEditor.clear();
        isDirty = false;
        role = null;
    }

    public boolean isDirty() {
        return isDirty;
    }

    protected void doShow(final String roleName) {
        assert roleName != null;

        // Configure the view.
        doInitView();

        // Start the workflow's logic.
        checkDirty(() -> doLoad(roleName));
    }

    protected void doLoad(String roleName) {
        checkDirty(this::clear);

        // Call backend service.
        showLoadingBox();
        userSystemManager.roles((Role o) -> {
                                    hideLoadingBox();
                                    BaseRoleEditorWorkflow.this.role = o;
                                    assert role != null;

                                    edit();
                                },
                                errorCallback).get(roleName);
    }

    protected void onSave() {
        doSave();
    }

    protected void onCancel() {
        doShow(BaseRoleEditorWorkflow.this.role.getName());
    }

    protected void doInitView() {
        view.setWidget(roleEditor.view)
                .setCancelButtonVisible(true)
                .setSaveButtonVisible(true)
                .setSaveButtonEnabled(isDirty)
                .setSaveButtonText(getSaveButtonText())
                .setCallback(new EntityWorkflowView.Callback() {
                    @Override
                    public void onSave() {
                        BaseRoleEditorWorkflow.this.onSave();
                    }

                    @Override
                    public void onCancel() {
                        BaseRoleEditorWorkflow.this.onCancel();
                    }
                });
    }

    protected String getSaveButtonText() {
        return UsersManagementWidgetsConstants.INSTANCE.saveChanges();
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
            view.showNotification(UsersManagementWidgetsConstants.INSTANCE.roleModified(BaseRoleEditorWorkflow.this.role.getName()));
        } else {
            view.clearNotifications();
        }
    }

    protected void edit() {
        roleEditorDriver.edit(role,
                              roleEditor);
        view.setCancelButtonVisible(false);
        view.setSaveButtonVisible(false);
    }

    protected void doSave() {
        assert role != null;

        final boolean isValid = roleEditorDriver.flush();
        this.role = roleEditorDriver.getValue();
        PermissionCollection rolePermissions = roleEditorDriver.getPermissions();
        PerspectiveActivity homePerspective = roleEditorDriver.getHomePerspective();
        int rolePriority = roleEditorDriver.getRolePriority();

        if (isValid) {
            showLoadingBox();

            // Update the current active policy
            AuthorizationPolicy authzPolicy = permissionManager.getAuthorizationPolicy();
            authzPolicy.setHomePerspective(role, homePerspective.getIdentifier());
            authzPolicy.setPriority(role, rolePriority);
            authzPolicy.setPermissions(role, rolePermissions);

            // Save the policy in the backend
            authorizationService.call(r -> {

                                          hideLoadingBox();
                                          isDirty = false;
                                          workbenchNotification.fire(new NotificationEvent(UsersManagementWidgetsConstants.INSTANCE.roleSaved(role.getName()),
                                                                                           SUCCESS));
                                          saveRoleEvent.fire(new SaveRoleEvent(role.getName()));
                                          doShow(role.getName());
                                      },
                                      errorCallback).savePolicy(authzPolicy);
        } else {
            throw new RuntimeException("Role must be valid before updating it.");
        }
    }

    protected boolean checkEventContext(final ContextualEvent contextualEvent,
                                        final Object context) {
        return contextualEvent != null && contextualEvent.getContext() != null && contextualEvent.getContext().equals(context);
    }

    void showError(final Throwable throwable) {
        errorEvent.fire(new OnErrorEvent(BaseRoleEditorWorkflow.this,
                                         throwable));
    }

    protected void checkDirty(final Command callback) {
        if (isDirty) {
            confirmBox.show(UsersManagementWidgetsConstants.INSTANCE.confirmAction(),
                            UsersManagementWidgetsConstants.INSTANCE.roleIsDirty(),
                            () -> {
                                BaseRoleEditorWorkflow.this.isDirty = false;
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
}
