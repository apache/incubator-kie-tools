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

package org.uberfire.ext.security.management.client.widgets.management.editor.user;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.errai.security.shared.api.Group;
import org.jboss.errai.security.shared.api.Role;
import org.jboss.errai.security.shared.api.identity.User;
import org.jboss.errai.security.shared.api.identity.UserImpl;
import org.uberfire.client.mvp.UberView;
import org.uberfire.ext.security.management.api.Capability;
import org.uberfire.ext.security.management.client.ClientUserSystemManager;
import org.uberfire.ext.security.management.client.widgets.management.editor.AssignedEntitiesEditor;
import org.uberfire.ext.security.management.client.widgets.management.editor.AssignedEntitiesExplorer;
import org.uberfire.ext.security.management.client.widgets.management.events.*;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import java.util.Set;

/**
 * <p>The user editor presenter.</p>
 * <p>User's groups are edited using the UserAssignedGroupsExplorer editor component. So the UserAssignedGroupsEditor works with a dummy user instance.</p>
 * 
 * @since 0.8.0
 */
@Dependent
public class UserEditor implements IsWidget, org.uberfire.ext.security.management.client.editor.user.UserEditor {

    public interface View extends UberView<UserEditor> {
        View initWidgets(UserAttributesEditor.View userAttributesEditorView,
                         AssignedEntitiesExplorer userAssignedGroupsExplorerView,
                         AssignedEntitiesEditor userAssignedGroupsEditorView,
                         AssignedEntitiesExplorer userAssignedRolesExplorerView,
                         AssignedEntitiesEditor userAssignedRolesEditorView);
        View setUsername(String username);
        View setEditButtonVisible(boolean isVisible);
        View setDeleteButtonVisible(boolean isVisible);
        View setChangePasswordButtonVisible(boolean isVisible);
        View setAddToGroupsButtonVisible(boolean isVisible);
        View setAddToRolesButtonVisible(boolean isVisible);
        View setAttributesEditorVisible(boolean isVisible);
    }

    ClientUserSystemManager userSystemManager;
    UserAttributesEditor userAttributesEditor;
    UserAssignedGroupsExplorer userAssignedGroupsExplorer;
    UserAssignedGroupsEditor userAssignedGroupsEditor;
    UserAssignedRolesExplorer userAssignedRolesExplorer;
    UserAssignedRolesEditor userAssignedRolesEditor;
    Event<OnEditEvent> onEditEvent;
    Event<OnShowEvent> onShowEvent;
    Event<OnDeleteEvent> onDeleteEvent;
    Event<OnChangePasswordEvent> onChangePasswordEvent;
    public View view;
    User user;
    boolean isEditMode;

    @Inject
    public UserEditor(final ClientUserSystemManager userSystemManager,
                      final UserAttributesEditor userAttributesEditor, 
                      final UserAssignedGroupsExplorer userAssignedGroupsExplorer,
                      final UserAssignedGroupsEditor userAssignedGroupsEditor,
                      final UserAssignedRolesExplorer userAssignedRolesExplorer,
                      final UserAssignedRolesEditor userAssignedRolesEditor,
                      final Event<OnEditEvent> onEditEvent,
                      final Event<OnShowEvent> onShowEvent,
                      final Event<OnDeleteEvent> onDeleteEvent,
                      final Event<OnChangePasswordEvent> onChangePasswordEvent,
                      final View view) {
        
        this.userSystemManager = userSystemManager;
        this.userAttributesEditor = userAttributesEditor;
        this.userAssignedGroupsExplorer = userAssignedGroupsExplorer;
        this.userAssignedGroupsEditor = userAssignedGroupsEditor;
        this.userAssignedRolesExplorer = userAssignedRolesExplorer;
        this.userAssignedRolesEditor = userAssignedRolesEditor;
        this.onEditEvent = onEditEvent;
        this.onShowEvent = onShowEvent;
        this.onDeleteEvent = onDeleteEvent;
        this.onChangePasswordEvent = onChangePasswordEvent;
        this.view = view;
    }

    @PostConstruct
    public void init() {
        view.init(this);
        view.initWidgets(userAttributesEditor.view, 
                userAssignedGroupsExplorer.view,
                userAssignedGroupsEditor.view,
                userAssignedRolesExplorer.view,
                userAssignedRolesEditor.view);
    }

    /*  ******************************************************************************************************
                                     PUBLIC PRESENTER API 
         ****************************************************************************************************** */
    
    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    @Override
    public String identifier() {
        // Identifier is not editable, no need for any editor.
        return user.getIdentifier();
    }

    @Override
    public UserAttributesEditor attributesEditor() {
        return userAttributesEditor;
    }

    @Override
    public UserAssignedGroupsExplorer groupsExplorer() {
        return userAssignedGroupsExplorer;
    }

    @Override
    public org.uberfire.ext.security.management.client.editor.user.UserAssignedRolesExplorer rolesExplorer() {
        return userAssignedRolesExplorer;
    }

    public UserAssignedGroupsEditor groupsEditor() {
        return userAssignedGroupsEditor;
    }

    public UserAssignedRolesEditor rolesEditor() {
        return userAssignedRolesEditor;
    }

    @Override
    public void show(final User user) {
        clear();
        this.isEditMode = false;
        open(user);
        onShowEvent.fire(new OnShowEvent(UserEditor.this, user));
    }

    @Override
    public void edit(final User user) {
        clear();
        this.isEditMode = true;
        open(user);
    }

    @Override
    public void flush() {
        assert user != null;
        assert isEditMode;
        // No additional flush logic to perform here.
    }

    @Override
    public User getValue() {
        return user;
    }

    @Override
    public void setViolations(final Set<ConstraintViolation<User>> violations) {
        //  Currently no violations expected.
    }
    
    public void clear() {
        isEditMode = false;
        user = null;
        userAttributesEditor.clear();
        userAssignedGroupsExplorer.clear();
        userAssignedGroupsEditor.clear();
        userAssignedRolesExplorer.clear();
    }

    public UserEditor setEditButtonVisible(boolean isVisible) {
        view.setEditButtonVisible(isVisible);
        return this;
    }
    
    public UserEditor setDeleteButtonVisible(boolean isVisible) {
        view.setDeleteButtonVisible(isVisible);
        return this;
    }

    public UserEditor setChangePasswordButtonVisible(boolean isVisible) {
        view.setChangePasswordButtonVisible(isVisible);
        return this;
    }

    public UserEditor setAttributesEditorVisible(boolean isVisible) {
        view.setAttributesEditorVisible(isVisible);
        return this;
    }
    
    public UserEditor setAddToGroupsButtonVisible(boolean isVisible) {
        view.setAddToGroupsButtonVisible(isVisible);
        return this;
    }
    
    /*  ******************************************************************************************************
                                 VIEW CALLBACKS 
     ****************************************************************************************************** */

    void onEdit() {
        onEditEvent.fire(new OnEditEvent(UserEditor.this, user));
    }
    
    void onDelete() {
        onDeleteEvent.fire(new OnDeleteEvent(UserEditor.this, user));
    }
    
    void onChangePassword() {
        onChangePasswordEvent.fire(new OnChangePasswordEvent(UserEditor.this, user));
    }
    
    void onAssignGroups() {

        final User dummyUser = new UserImpl(user.getIdentifier(),
                userAssignedRolesExplorer.getValue(), userAssignedGroupsExplorer.getValue(), user.getProperties());
        
        if (isEditMode) {
            userAssignedGroupsEditor.edit(dummyUser);
        } else {
            userAssignedGroupsEditor.show(dummyUser);
        }
        
    }

    void onAssignRoles() {

        final User dummyUser = new UserImpl(user.getIdentifier(),
                userAssignedRolesExplorer.getValue(), userAssignedGroupsExplorer.getValue(), user.getProperties());

        if (isEditMode) {
            userAssignedRolesEditor.edit(dummyUser);
        } else {
            userAssignedRolesEditor.show(dummyUser);
        }

    }
    
     /*  ******************************************************************************************************
                                 PRIVATE METHODS AND VALIDATORS
     ****************************************************************************************************** */
    
    protected void open(final User user) {
        assert user != null;
        this.user = user;
        
        // User identifier.
        final String id = user.getIdentifier();
        view.setUsername(id);

        // Edit mode & Capabilities. 
        final boolean canUpdate = canUpdate();
        final boolean canDelete = canDelete();
        final boolean canManageAttributes = canManageAttributes();
        final boolean canChangePwd = canChangePassword();
        final boolean canAssignGroups = canAssignGroups();
        final boolean canAssignRoles = canAssignRoles();
        final boolean hasAttributes = user.getProperties() != null && !user.getProperties().isEmpty();
        final boolean shouldHideAttributesEditor = !canManageAttributes && !hasAttributes;
        view.setEditButtonVisible(!isEditMode && canUpdate);
        view.setDeleteButtonVisible(isEditMode && canDelete);
        view.setChangePasswordButtonVisible(isEditMode && canChangePwd);
        view.setAddToGroupsButtonVisible(isEditMode && canAssignGroups);
        view.setAddToRolesButtonVisible(isEditMode && canAssignRoles);
        view.setAttributesEditorVisible(!shouldHideAttributesEditor);
    }

    boolean canUpdate() {
        final boolean canUpdate = userSystemManager.isUserCapabilityEnabled(Capability.CAN_UPDATE_USER);
        return canUpdate;
    }

    boolean canDelete() {
        final boolean canDelete = userSystemManager.isUserCapabilityEnabled(Capability.CAN_DELETE_USER);
        return canDelete;
    }

    public boolean canAssignGroups() {
        final boolean canAssignGroups = userSystemManager.isUserCapabilityEnabled(Capability.CAN_ASSIGN_GROUPS);
        return canAssignGroups;
    }

    public boolean canAssignRoles() {
        final boolean canAssignRoles = userSystemManager.isUserCapabilityEnabled(Capability.CAN_ASSIGN_ROLES);
        return canAssignRoles;
    }

    boolean canChangePassword() {
        final boolean canChangePassword = userSystemManager.isUserCapabilityEnabled(Capability.CAN_CHANGE_PASSWORD);
        return canChangePassword;
    }

    boolean canManageAttributes() {
        final boolean canManageAttributes = userSystemManager.isUserCapabilityEnabled(Capability.CAN_MANAGE_ATTRIBUTES);
        return canManageAttributes;
    }

    void onOnUserGroupsUpdatedEvent(@Observes final OnUpdateUserGroupsEvent onUpdateUserGroupsEvent) {
        if (checkEventContext(onUpdateUserGroupsEvent, userAssignedGroupsEditor)) {
            userAssignedGroupsEditor.flush();
            final Set<Group> groups = userAssignedGroupsEditor.getValue();
            userAssignedGroupsExplorer.getValue().clear();
            userAssignedGroupsExplorer.getValue().addAll(groups);
            userAssignedGroupsExplorer.doShow();
        }
    }

    void onOnUserRolesUpdatedEvent(@Observes final OnUpdateUserRolesEvent onUpdateUserRolesEvent) {
        if (checkEventContext(onUpdateUserRolesEvent, userAssignedRolesEditor)) {
            userAssignedRolesEditor.flush();
            final Set<Role> roles = userAssignedRolesEditor.getValue();
            userAssignedRolesExplorer.getValue().clear();
            userAssignedRolesExplorer.getValue().addAll(roles);
            userAssignedRolesExplorer.doShow();
        }
    }

    
    private boolean checkEventContext(final ContextualEvent contextualEvent, final Object context) {
        return contextualEvent != null && contextualEvent.getContext() != null && contextualEvent.getContext().equals(context);
    }
}
