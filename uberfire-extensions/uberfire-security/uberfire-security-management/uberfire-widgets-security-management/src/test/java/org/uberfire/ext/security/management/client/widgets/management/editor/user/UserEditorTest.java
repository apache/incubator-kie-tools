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

import org.jboss.errai.security.shared.api.Group;
import org.jboss.errai.security.shared.api.GroupImpl;
import org.jboss.errai.security.shared.api.Role;
import org.jboss.errai.security.shared.api.RoleImpl;
import org.jboss.errai.security.shared.api.identity.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.ext.security.management.api.Capability;
import org.uberfire.ext.security.management.client.ClientUserSystemManager;
import org.uberfire.ext.security.management.client.widgets.management.editor.AssignedEntitiesEditor;
import org.uberfire.ext.security.management.client.widgets.management.editor.AssignedEntitiesExplorer;
import org.uberfire.ext.security.management.client.widgets.management.events.*;
import org.uberfire.mocks.EventSourceMock;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class UserEditorTest {

    @Mock ClientUserSystemManager userSystemManager;
    @Mock UserAttributesEditor userAttributesEditor;
    @Mock UserAssignedGroupsExplorer userAssignedGroupsExplorer;
    @Mock UserAssignedRolesExplorer userAssignedRolesExplorer;
    @Mock UserAssignedGroupsEditor userAssignedGroupsEditor;
    @Mock UserAssignedRolesEditor userAssignedRolesEditor;
    @Mock EventSourceMock<OnEditEvent> onEditEvent;
    @Mock EventSourceMock<OnShowEvent> onShowEvent;
    @Mock EventSourceMock<OnDeleteEvent> onDeleteEvent;
    @Mock EventSourceMock<OnChangePasswordEvent> onChangePasswordEvent;
    @Mock UserEditor.View view;

    private UserEditor presenter;
    @Mock User user;

    @Before
    public void setup() {
        Map<String, String> userAttributes = new HashMap<String, String>(1);
        userAttributes.put("attr1", "value1");
        when(user.getIdentifier()).thenReturn("user1");
        when(user.getProperties()).thenReturn(userAttributes);
        when(userSystemManager.isUserCapabilityEnabled(any(Capability.class))).thenReturn(true);
        presenter = new UserEditor(userSystemManager, userAttributesEditor, userAssignedGroupsExplorer,
                userAssignedGroupsEditor, userAssignedRolesExplorer, userAssignedRolesEditor, onEditEvent, onShowEvent, onDeleteEvent, onChangePasswordEvent, view);
    }

    @Test
    public void testInit() {
        presenter.init();
        verify(view, times(1)).init(presenter);
        verify(view, times(1)).initWidgets(any(UserAttributesEditor.View.class), any(AssignedEntitiesExplorer.class),
                any(AssignedEntitiesEditor.class), any(AssignedEntitiesExplorer.class), any(AssignedEntitiesEditor.class));
        verify(view, times(0)).setAddToGroupsButtonVisible(anyBoolean());
        verify(view, times(0)).setAttributesEditorVisible(anyBoolean());
        verify(view, times(0)).setChangePasswordButtonVisible(anyBoolean());
        verify(view, times(0)).setDeleteButtonVisible(anyBoolean());
        verify(view, times(0)).setEditButtonVisible(anyBoolean());
        verify(view, times(0)).setUsername(anyString());
    }

    @Test
    public void testClear() {
        presenter.isEditMode = true;
        presenter.user = user;
        presenter.clear();
        assertNull(presenter.user);
        assertFalse(presenter.isEditMode);
        verify(userAttributesEditor, times(1)).clear();
        verify(userAssignedGroupsExplorer, times(1)).clear();
        verify(userAssignedGroupsEditor, times(1)).clear();
        assertNoViewCalls();
    }

    @Test
    public void testIdentifier() {
        presenter.user = user;
        String id = presenter.identifier();
        assertEquals("user1", id);
        assertNoViewCalls();
    }

    @Test
    public void testAttributesEditor() {
        assertEquals(userAttributesEditor, presenter.attributesEditor());
        assertNoViewCalls();
    }

    @Test
    public void testGroupsExplorer() {
        assertEquals(userAssignedGroupsExplorer, presenter.groupsExplorer());
        assertNoViewCalls();
    }

    @Test
    public void testGroupsEditor() {
        assertEquals(userAssignedGroupsEditor, presenter.groupsEditor());
        assertNoViewCalls();
    }

    @Test
    public void testRolesExplorer() {
        assertEquals(userAssignedRolesExplorer, presenter.rolesExplorer());
        assertNoViewCalls();
    }

    @Test
    public void testRolesEditor() {
        assertEquals(userAssignedRolesEditor, presenter.rolesEditor());
        assertNoViewCalls();
    }

    @Test
    public void testShow() {
        presenter.show(user);
        assertFalse(presenter.isEditMode);
        verify(userAttributesEditor, times(1)).clear();
        verify(userAssignedGroupsExplorer, times(1)).clear();
        verify(userAssignedRolesExplorer, times(1)).clear();
        verify(userAssignedGroupsEditor, times(1)).clear();
        verify(onShowEvent, times(1)).fire(any(OnShowEvent.class));
        verify(view, times(0)).init(any(UserEditor.class));
        verify(view, times(0)).initWidgets(any(UserAttributesEditor.View.class), any(AssignedEntitiesExplorer.class),
                any(AssignedEntitiesEditor.class), any(AssignedEntitiesExplorer.class), any(AssignedEntitiesEditor.class));
        verify(view, times(1)).setAddToGroupsButtonVisible(false);
        verify(view, times(1)).setAttributesEditorVisible(true);
        verify(view, times(1)).setChangePasswordButtonVisible(false);
        verify(view, times(1)).setDeleteButtonVisible(false);
        verify(view, times(1)).setEditButtonVisible(true);
        verify(view, times(1)).setUsername("user1");
    }

    @Test
    public void testSetEditButtonVisible() {
        presenter.setEditButtonVisible(true);
        verify(view, times(1)).setEditButtonVisible(true);
        verify(view, times(0)).setAddToGroupsButtonVisible(anyBoolean());
        verify(view, times(0)).setAttributesEditorVisible(anyBoolean());
        verify(view, times(0)).setChangePasswordButtonVisible(anyBoolean());
        verify(view, times(0)).setDeleteButtonVisible(anyBoolean());
        verify(view, times(0)).setUsername(anyString());

    }

    @Test
    public void testSetDeleteButtonVisible() {
        presenter.setDeleteButtonVisible(true);
        verify(view, times(1)).setDeleteButtonVisible(true);
        verify(view, times(0)).setAddToGroupsButtonVisible(anyBoolean());
        verify(view, times(0)).setAttributesEditorVisible(anyBoolean());
        verify(view, times(0)).setChangePasswordButtonVisible(anyBoolean());
        verify(view, times(0)).setEditButtonVisible(anyBoolean());
        verify(view, times(0)).setUsername(anyString());
    }

    @Test
    public void testSetChangePasswordButtonVisible() {
        presenter.setChangePasswordButtonVisible(true);
        verify(view, times(1)).setChangePasswordButtonVisible(true);
        verify(view, times(0)).setAddToGroupsButtonVisible(anyBoolean());
        verify(view, times(0)).setAttributesEditorVisible(anyBoolean());
        verify(view, times(0)).setDeleteButtonVisible(anyBoolean());
        verify(view, times(0)).setEditButtonVisible(anyBoolean());
        verify(view, times(0)).setUsername(anyString());
    }

    @Test
    public void testSetAttributesEditorVisible() {
        presenter.setAttributesEditorVisible(true);
        verify(view, times(1)).setAttributesEditorVisible(true);
        verify(view, times(0)).setAddToGroupsButtonVisible(anyBoolean());
        verify(view, times(0)).setChangePasswordButtonVisible(anyBoolean());
        verify(view, times(0)).setDeleteButtonVisible(anyBoolean());
        verify(view, times(0)).setEditButtonVisible(anyBoolean());
        verify(view, times(0)).setUsername(anyString());
    }

    @Test
    public void testOnEdit() {
        presenter.onEdit();
        verify(onEditEvent, times(1)).fire(any(OnEditEvent.class));
        assertNoViewCalls();
    }

    @Test
    public void testOnDelete() {
        presenter.onDelete();
        verify(onDeleteEvent, times(1)).fire(any(OnDeleteEvent.class));
        assertNoViewCalls();
    }

    @Test
    public void testOnChangePassword() {
        presenter.onChangePassword();
        verify(onChangePasswordEvent, times(1)).fire(any(OnChangePasswordEvent.class));
        assertNoViewCalls();
    }

    @Test
    public void testOnAssignGroupsInReadMode() {
        presenter.user = user;
        presenter.isEditMode = false;
        presenter.onAssignGroups();
        verify(userAssignedGroupsEditor, times(1)).show(any(User.class));
        verify(userAssignedGroupsEditor, times(0)).edit(any(User.class));
        assertNoViewCalls();
    }

    @Test
    public void testOnAssignGroupsInEditMode() {
        presenter.user = user;
        presenter.isEditMode = true;
        presenter.onAssignGroups();
        verify(userAssignedGroupsEditor, times(0)).show(any(User.class));
        verify(userAssignedGroupsEditor, times(1)).edit(any(User.class));
        assertNoViewCalls();

    }

    @Test
    public void testOnOnUserGroupsUpdatedEvent() {
        OnUpdateUserGroupsEvent onUpdateUserGroupsEvent = mock(OnUpdateUserGroupsEvent.class);
        when(onUpdateUserGroupsEvent.getContext()).thenReturn(userAssignedGroupsEditor);
        final Set<Group> explorerGroups = new HashSet<Group>();
        when(userAssignedGroupsExplorer.getValue()).thenReturn(explorerGroups);
        final Set<Group> groups = new HashSet<Group>();
        groups.add(new GroupImpl("group1"));
        when(userAssignedGroupsEditor.getValue()).thenReturn(groups);
        presenter.onOnUserGroupsUpdatedEvent(onUpdateUserGroupsEvent);
        assertEquals(groups, userAssignedGroupsExplorer.getValue());
        verify(userAssignedGroupsEditor, times(1)).flush();
        verify(userAssignedGroupsExplorer, times(1)).doShow();
        assertNoViewCalls();
    }


    @Test
    public void testOnAssignRolesInReadMode() {
        presenter.user = user;
        presenter.isEditMode = false;
        presenter.onAssignRoles();
        verify(userAssignedRolesEditor, times(1)).show(any(User.class));
        verify(userAssignedRolesEditor, times(0)).edit(any(User.class));
        assertNoViewCalls();
    }

    @Test
    public void testOnAssignRolesInEditMode() {
        presenter.user = user;
        presenter.isEditMode = true;
        presenter.onAssignRoles();
        verify(userAssignedRolesEditor, times(0)).show(any(User.class));
        verify(userAssignedRolesEditor, times(1)).edit(any(User.class));
        assertNoViewCalls();

    }

    @Test
    public void testOnOnUserRolesUpdatedEvent() {
        OnUpdateUserRolesEvent onUpdateUserRolesEvent = mock(OnUpdateUserRolesEvent.class);
        when(onUpdateUserRolesEvent.getContext()).thenReturn(userAssignedRolesEditor);
        final Set<Role> explorerRoles = new HashSet<Role>();
        when(userAssignedRolesExplorer.getValue()).thenReturn(explorerRoles);
        final Set<Role> roles = new HashSet<Role>();
        roles.add(new RoleImpl("role1"));
        when(userAssignedRolesEditor.getValue()).thenReturn(roles);
        presenter.onOnUserRolesUpdatedEvent(onUpdateUserRolesEvent);
        assertEquals(roles, userAssignedRolesExplorer.getValue());
        verify(userAssignedRolesEditor, times(1)).flush();
        verify(userAssignedRolesExplorer, times(1)).doShow();
        assertNoViewCalls();
    }


    private void assertNoViewCalls() {
        verify(view, times(0)).init(any(UserEditor.class));
        verify(view, times(0)).initWidgets(any(UserAttributesEditor.View.class), any(AssignedEntitiesExplorer.class),
                any(AssignedEntitiesEditor.class), any(AssignedEntitiesExplorer.class), any(AssignedEntitiesEditor.class));
        verify(view, times(0)).setAddToGroupsButtonVisible(anyBoolean());
        verify(view, times(0)).setAttributesEditorVisible(anyBoolean());
        verify(view, times(0)).setChangePasswordButtonVisible(anyBoolean());
        verify(view, times(0)).setDeleteButtonVisible(anyBoolean());
        verify(view, times(0)).setEditButtonVisible(anyBoolean());
        verify(view, times(0)).setUsername(anyString());
    }

}
