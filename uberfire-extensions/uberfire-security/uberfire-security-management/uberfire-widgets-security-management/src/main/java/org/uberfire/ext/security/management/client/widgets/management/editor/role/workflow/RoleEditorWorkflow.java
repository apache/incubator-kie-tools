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

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import org.jboss.errai.common.client.api.Caller;
import org.uberfire.backend.authz.AuthorizationService;
import org.uberfire.ext.security.management.client.ClientUserSystemManager;
import org.uberfire.ext.security.management.client.editor.role.RoleEditorDriver;
import org.uberfire.ext.security.management.client.widgets.management.editor.role.RoleEditor;
import org.uberfire.ext.security.management.client.widgets.management.editor.workflow.EntityWorkflowView;
import org.uberfire.ext.security.management.client.widgets.management.events.HomePerspectiveChangedEvent;
import org.uberfire.ext.security.management.client.widgets.management.events.OnEditEvent;
import org.uberfire.ext.security.management.client.widgets.management.events.OnErrorEvent;
import org.uberfire.ext.security.management.client.widgets.management.events.PermissionChangedEvent;
import org.uberfire.ext.security.management.client.widgets.management.events.PermissionNodeAddedEvent;
import org.uberfire.ext.security.management.client.widgets.management.events.PermissionNodeRemovedEvent;
import org.uberfire.ext.security.management.client.widgets.management.events.PriorityChangedEvent;
import org.uberfire.ext.security.management.client.widgets.management.events.SaveRoleEvent;
import org.uberfire.ext.security.management.client.widgets.popup.ConfirmBox;
import org.uberfire.ext.security.management.client.widgets.popup.LoadingBox;
import org.uberfire.security.authz.PermissionManager;
import org.uberfire.workbench.events.NotificationEvent;

/**
 * <p>The workflow for editing a role.</p>
 * <p>It links the editor & sub-editors components with the editor driver and the remote user services.</p>
 * 
 * @since 0.9.0
 */
@Dependent
public class RoleEditorWorkflow extends BaseRoleEditorWorkflow {

    @Inject
    public RoleEditorWorkflow(final ClientUserSystemManager userSystemManager,
                              final Caller<AuthorizationService> authorizationService,
                              final PermissionManager permissionManager,
                              final Event<OnErrorEvent> errorEvent,
                              final Event<NotificationEvent> workbenchNotification,
                              final Event<SaveRoleEvent> saveUserEvent,
                              final ConfirmBox confirmBox,
                              final RoleEditor roleEditor,
                              final RoleEditorDriver roleEditorDriver,
                              final LoadingBox loadingBox,
                              final EntityWorkflowView view) {
        
        super(userSystemManager, authorizationService, permissionManager, errorEvent, workbenchNotification,
                saveUserEvent, confirmBox, roleEditor, roleEditorDriver, loadingBox, view);
    }

    public void show(final String roleName) {
        doShow(roleName);
    }
    
    void onEditRoleEvent(@Observes final OnEditEvent onEditEvent) {
        if (checkEventContext(onEditEvent, roleEditor)) {
            edit();
        }
    }

    void onHomePerspectiveChangedEvent(@Observes final HomePerspectiveChangedEvent event) {
        if (checkEventContext(event, roleEditor.getAclSettings())) {
            setDirty(true);
            GWT.log("Home perspective: " + event.getPerspective().getIdentifier());
        }
    }

    void onPriorityChangedEvent(@Observes final PriorityChangedEvent event) {
        if (checkEventContext(event, roleEditor.getAclSettings())) {
            setDirty(true);
            GWT.log("Role priority : " + event.getPriority());
        }
    }

    void onPermissionChangedEvent(@Observes final PermissionChangedEvent event) {
        if (checkEventContext(event, roleEditor.getAclEditor())) {
            setDirty(true);
            if (event.isGranted()) {
                GWT.log("Permission granted: " + event.getPermission().getName());
            } else {
                GWT.log("Permission denied: " + event.getPermission().getName());
            }
        }
    }

    void onPermissionAddedEvent(@Observes final PermissionNodeAddedEvent event) {
        if (checkEventContext(event, roleEditor.getAclEditor())) {
            setDirty(true);
            GWT.log("New permission node: " + event.getChildNode().getNodeName());
        }
    }

    void onPermissionRemovedEvent(@Observes final PermissionNodeRemovedEvent event) {
        if (checkEventContext(event, roleEditor.getAclEditor())) {
            setDirty(true);
            GWT.log("Permission node removed: " + event.getChildNode().getNodeName());
        }
    }
}
