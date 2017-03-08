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

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jboss.errai.security.shared.api.identity.User;
import org.uberfire.ext.security.management.client.ClientUserSystemManager;
import org.uberfire.ext.security.management.client.editor.user.UserEditorDriver;
import org.uberfire.ext.security.management.client.widgets.management.ChangePassword;
import org.uberfire.ext.security.management.client.widgets.management.editor.user.UserEditor;
import org.uberfire.ext.security.management.client.widgets.management.editor.workflow.EntityWorkflowView;
import org.uberfire.ext.security.management.client.widgets.management.events.CreateUserAttributeEvent;
import org.uberfire.ext.security.management.client.widgets.management.events.DeleteUserAttributeEvent;
import org.uberfire.ext.security.management.client.widgets.management.events.DeleteUserEvent;
import org.uberfire.ext.security.management.client.widgets.management.events.OnChangePasswordEvent;
import org.uberfire.ext.security.management.client.widgets.management.events.OnDeleteEvent;
import org.uberfire.ext.security.management.client.widgets.management.events.OnEditEvent;
import org.uberfire.ext.security.management.client.widgets.management.events.OnErrorEvent;
import org.uberfire.ext.security.management.client.widgets.management.events.OnRemoveUserGroupEvent;
import org.uberfire.ext.security.management.client.widgets.management.events.OnRemoveUserRoleEvent;
import org.uberfire.ext.security.management.client.widgets.management.events.OnUpdateUserGroupsEvent;
import org.uberfire.ext.security.management.client.widgets.management.events.OnUpdateUserRolesEvent;
import org.uberfire.ext.security.management.client.widgets.management.events.SaveGroupEvent;
import org.uberfire.ext.security.management.client.widgets.management.events.SaveRoleEvent;
import org.uberfire.ext.security.management.client.widgets.management.events.SaveUserEvent;
import org.uberfire.ext.security.management.client.widgets.management.events.UpdateUserAttributeEvent;
import org.uberfire.ext.security.management.client.widgets.popup.ConfirmBox;
import org.uberfire.ext.security.management.client.widgets.popup.LoadingBox;
import org.uberfire.workbench.events.NotificationEvent;

/**
 * <p>The workflow for editing a user.</p>
 * <p>It links the editor & sub-editors components with the editor driver and the remote user services.</p>
 * @since 0.8.0
 */
@Dependent
public class UserEditorWorkflow extends BaseUserEditorWorkflow {

    @Inject
    public UserEditorWorkflow(final ClientUserSystemManager userSystemManager,
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

        super(userSystemManager,
              errorEvent,
              workbenchNotification,
              deleteUserEvent,
              saveUserEvent,
              confirmBox,
              userEditor,
              userEditorDriver,
              changePassword,
              loadingBox,
              view);
    }

    @PostConstruct
    public void init() {
    }

    public void show(final String userId) {
        doShow(userId);
    }

    void onEditUserEvent(@Observes final OnEditEvent onEditEvent) {
        if (checkEventContext(onEditEvent,
                              userEditor)) {
            edit();
        }
    }

    void onDeleteUserEvent(@Observes final OnDeleteEvent onDeleteEvent) {
        if (checkEventContext(onDeleteEvent,
                              userEditor)) {
            doDelete();
        }
    }

    void onChangeUserPasswordEvent(@Observes final OnChangePasswordEvent onChangePasswordEvent) {
        if (checkEventContext(onChangePasswordEvent,
                              userEditor)) {
            doChangePassword();
        }
    }

    void onAttributeCreated(@Observes final CreateUserAttributeEvent createUserAttributeEvent) {
        if (checkEventContext(createUserAttributeEvent,
                              getUserEditor().attributesEditor())) {
            setDirty(true);
        }
    }

    void onAttributeDeleted(@Observes final DeleteUserAttributeEvent deleteUserAttributeEvent) {
        if (checkEventContext(deleteUserAttributeEvent,
                              getUserEditor().attributesEditor())) {
            setDirty(true);
        }
    }

    void onAttributeUpdated(@Observes final UpdateUserAttributeEvent updateUserAttributeEvent) {
        if (checkEventContext(updateUserAttributeEvent,
                              getUserEditor().attributesEditor())) {
            setDirty(true);
        }
    }

    void onOnRemoveUserGroupEvent(@Observes final OnRemoveUserGroupEvent onRemoveUserGroupEvent) {
        if (checkEventContext(onRemoveUserGroupEvent,
                              getUserEditor().groupsExplorer())) {
            setDirty(true);
        }
    }

    void onOnUserGroupsUpdatedEvent(@Observes final OnUpdateUserGroupsEvent onUpdateUserGroupsEvent) {
        if (checkEventContext(onUpdateUserGroupsEvent,
                              getUserEditor().groupsEditor())) {
            setDirty(true);
            refreshPermissions(true);
        }
    }

    void onOnRemoveUserRoleEvent(@Observes final OnRemoveUserRoleEvent onRemoveUserRoleEvent) {
        if (checkEventContext(onRemoveUserRoleEvent,
                              getUserEditor().rolesExplorer())) {
            setDirty(true);
            refreshPermissions(true);
        }
    }

    void onOnUserRolesUpdatedEvent(@Observes final OnUpdateUserRolesEvent onUpdateUserRolesEvent) {
        if (checkEventContext(onUpdateUserRolesEvent,
                              getUserEditor().rolesEditor())) {
            setDirty(true);
            refreshPermissions(true);
        }
    }

    void onRoleSavedEvent(@Observes SaveRoleEvent event) {
        refreshPermissions(false);
    }

    void onGroupSavedEvent(@Observes SaveGroupEvent event) {
        refreshPermissions(false);
    }

    void refreshPermissions(boolean flush) {
        if (flush) {
            userEditorDriver.flush();
            User user = userEditorDriver.getValue();
            userEditor.getACLViewer().show(user);
        } else {
            userEditor.getACLViewer().show(user);
        }
    }
}
