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
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jboss.errai.security.shared.api.Group;
import org.jboss.errai.security.shared.api.identity.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.uberfire.ext.security.management.client.editor.user.UserEditorDriver;
import org.uberfire.ext.security.management.client.widgets.management.AbstractSecurityManagementTest;
import org.uberfire.ext.security.management.client.widgets.management.ChangePassword;
import org.uberfire.ext.security.management.client.widgets.management.CreateEntity;
import org.uberfire.ext.security.management.client.widgets.management.editor.user.UserAssignedGroupsEditor;
import org.uberfire.ext.security.management.client.widgets.management.editor.user.UserAssignedGroupsExplorer;
import org.uberfire.ext.security.management.client.widgets.management.editor.user.UserAttributesEditor;
import org.uberfire.ext.security.management.client.widgets.management.editor.user.UserEditor;
import org.uberfire.ext.security.management.client.widgets.management.editor.workflow.EntityWorkflowView;
import org.uberfire.ext.security.management.client.widgets.management.events.*;
import org.uberfire.ext.security.management.client.widgets.popup.ConfirmBox;
import org.uberfire.ext.security.management.client.widgets.popup.LoadingBox;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.events.NotificationEvent;

import java.util.HashSet;
import java.util.Set;

import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class UserCreationWorkflowTest extends AbstractSecurityManagementTest  {

    @Mock EventSourceMock<OnErrorEvent> errorEvent;
    @Mock EventSourceMock<DeleteUserEvent> deleteUserEvent;
    @Mock EventSourceMock<SaveUserEvent> saveUserEvent;
    @Mock ConfirmBox confirmBox;
    @Mock UserEditor userEditor;
    @Mock UserEditorDriver userEditorDriver;
    @Mock ChangePassword changePassword;
    @Mock LoadingBox loadingBox;
    @Mock CreateEntity createEntity;
    @Mock EventSourceMock<CreateUserEvent> createUserEvent;
    @Mock EntityWorkflowView view;

    private UserCreationWorkflow tested;
    @Mock UserAttributesEditor userAttributesEditor;
    @Mock UserAssignedGroupsExplorer userAssignedGroupsExplorer;
    @Mock UserAssignedGroupsEditor userAssignedGroupsEditor;
    @Mock User user;

    @Before
    public void setup() {
        super.setup();
        final Set<Group> groups = new HashSet<Group>();
        final Group group1 = mock(Group.class);
        when(group1.getName()).thenReturn("group1");
        groups.add(group1);
        when(user.getIdentifier()).thenReturn("user1");
        when(user.getGroups()).thenReturn(groups);
        when(view.setWidget(any(IsWidget.class))).thenReturn(view);
        when(view.clearNotification()).thenReturn(view);
        when(view.setCallback(any(EntityWorkflowView.Callback.class))).thenReturn(view);
        when(view.setCancelButtonVisible(anyBoolean())).thenReturn(view);
        when(view.setSaveButtonEnabled(anyBoolean())).thenReturn(view);
        when(view.setSaveButtonVisible(anyBoolean())).thenReturn(view);
        when(view.setSaveButtonText(anyString())).thenReturn(view);
        when(view.showNotification(anyString())).thenReturn(view);
        when(userEditor.setEditButtonVisible(anyBoolean())).thenReturn(userEditor);
        when(userEditor.setChangePasswordButtonVisible(anyBoolean())).thenReturn(userEditor);
        when(userEditor.setDeleteButtonVisible(anyBoolean())).thenReturn(userEditor);
        when(userEditor.attributesEditor()).thenReturn(userAttributesEditor);
        when(userEditor.groupsExplorer()).thenReturn(userAssignedGroupsExplorer);
        when(userEditor.groupsEditor()).thenReturn(userAssignedGroupsEditor);
        tested = new UserCreationWorkflow(userSystemManager, errorEvent, workbenchNotification, deleteUserEvent, saveUserEvent,
                createUserEvent, confirmBox, createEntity, userEditor, userEditorDriver, changePassword, loadingBox, view);
    }
    
    @Test
    public void testCreate() {
        tested.create();
        verify(userEditor, times(1)).clear();
        verify(createEntity, times(1)).show(anyString(), anyString());
        verify(view, times(1)).setCancelButtonVisible(false);
        verify(view, times(1)).setCallback(any(EntityWorkflowView.Callback.class));
        verify(view, times(1)).setSaveButtonText(anyString());
        verify(view, times(1)).setWidget(any(IsWidget.class));
        verify(view, times(1)).setSaveButtonVisible(true);
        verify(view, times(1)).setSaveButtonEnabled(true);
        verify(view, times(2)).clearNotification();
    }
    
    
    @Test
    public void testOnCreateEntityAlreadyExisting() {
        when(userManagerService.get(anyString())).thenReturn(user);
        when(createEntity.getEntityIdentifier()).thenReturn("user1");
        tested.onCreateEntityClick();
        verify(loadingBox, times(1)).show();
        verify(loadingBox, times(1)).hide();
        verify(createEntity, times(1)).setErrorState();
    }
    
    @Test
    public void testDoEdit() {
        tested.isDirty = false;
        tested.user = user;
        tested.doEdit();
        verify(userManagerService, times(0)).get(anyString());
        verify(view, times(3)).setCancelButtonVisible(true);
        verify(view, times(1)).setCallback(any(EntityWorkflowView.Callback.class));
        verify(view, times(2)).setSaveButtonText(anyString());
        verify(view, times(1)).setWidget(any(IsWidget.class));
        verify(view, times(3)).setSaveButtonVisible(true);
        verify(view, times(2)).setSaveButtonEnabled(false);
        verify(view, times(1)).setSaveButtonEnabled(true);
        verify(view, times(0)).clearNotification();
        verify(userEditor, times(0)).clear();
        verify(userEditorDriver, times(0)).show(user, userEditor);
        verify(userEditorDriver, times(1)).edit(user, userEditor);
        verify(userEditor, times(1)).setEditButtonVisible(false);
        verify(userEditor, times(1)).setChangePasswordButtonVisible(false);
        verify(userEditor, times(1)).setDeleteButtonVisible(false);
    }
    
    @Test
    public void testAfterSaveSetPassword() {
        tested.user = user;
        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocationOnMock) throws Throwable {
                final Command callback = (Command) invocationOnMock.getArguments()[2];
                callback.execute();
                return null;
            }
        }).when(confirmBox).show(anyString(), anyString(), any(Command.class), any(Command.class));
        final String id = "user1";
        tested.afterSave(id);
        verify(confirmBox, times(1)).show(anyString(), anyString(), any(Command.class), any(Command.class));
        verify(changePassword, times(1)).show(anyString(), any(ChangePassword.ChangePasswordCallback.class));
    }

    @Test
    public void testAfterSaveSkipSetPassword() {
        tested.user = user;
        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocationOnMock) throws Throwable {
                final Command callback = (Command) invocationOnMock.getArguments()[3];
                callback.execute();
                return null;
            }
        }).when(confirmBox).show(anyString(), anyString(), any(Command.class), any(Command.class));
        final String id = "user1";
        tested.afterSave(id);
        verify(confirmBox, times(1)).show(anyString(), anyString(), any(Command.class), any(Command.class));
        verify(changePassword, times(0)).show(anyString(), any(ChangePassword.ChangePasswordCallback.class));
        verify(workbenchNotification, times(1)).fire(any(NotificationEvent.class));
        verify(createUserEvent, times(1)).fire(any(CreateUserEvent.class));
        verify(createEntity, times(1)).show(anyString(), anyString());
    }

    @Test
    public void testOnDeleteUserEvent() {
        final OnDeleteEvent onDeleteEvent = mock(OnDeleteEvent.class);
        when(onDeleteEvent.getContext()).thenReturn(userEditor);
        tested.user = user;
        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocationOnMock) throws Throwable {
                final Command callback = (Command) invocationOnMock.getArguments()[2];
                callback.execute();
                return null;
            }
        }).when(confirmBox).show(anyString(), anyString(), any(Command.class));
        tested.onDeleteUserEvent(onDeleteEvent);
        verify(confirmBox, times(1)).show(anyString(), anyString(), any(Command.class));
        verify(userManagerService, times(1)).delete(anyString());
    }

    @Test
    public void testOnChangeUserPasswordEvent() {
        final OnChangePasswordEvent onChangePasswordEvent = mock(OnChangePasswordEvent.class);
        when(onChangePasswordEvent.getContext()).thenReturn(userEditor);
        tested.user = user;
        tested.onChangeUserPasswordEvent(onChangePasswordEvent);
        verify(changePassword, times(1)).show(anyString(), any(ChangePassword.ChangePasswordCallback.class));
    }

    @Test
    public void testOnAttributeCreated() {
        final CreateUserAttributeEvent createUserAttributeEvent = mock(CreateUserAttributeEvent.class);
        when(createUserAttributeEvent.getContext()).thenReturn(userAttributesEditor);
        tested.user = user;
        tested.onAttributeCreated(createUserAttributeEvent);
        assertSetDirty();
    }

    @Test
    public void testOnAttributeDeleted() {
        final DeleteUserAttributeEvent deleteUserAttributeEvent = mock(DeleteUserAttributeEvent.class);
        when(deleteUserAttributeEvent.getContext()).thenReturn(userAttributesEditor);
        tested.user = user;
        tested.onAttributeDeleted(deleteUserAttributeEvent);
        assertSetDirty();
    }

    @Test
    public void testOnAttributeUpdated() {
        final UpdateUserAttributeEvent updateUserAttributeEvent = mock(UpdateUserAttributeEvent.class);
        when(updateUserAttributeEvent.getContext()).thenReturn(userAttributesEditor);
        tested.user = user;
        tested.onAttributeUpdated(updateUserAttributeEvent);
        assertSetDirty();
    }

    @Test
    public void testOnRemoveUserGroupEvent() {
        final OnRemoveUserGroupEvent onRemoveUserGroupEvent = mock(OnRemoveUserGroupEvent.class);
        when(onRemoveUserGroupEvent.getContext()).thenReturn(userAssignedGroupsExplorer);
        tested.user = user;
        tested.onOnRemoveUserGroupEvent(onRemoveUserGroupEvent);
        assertSetDirty();
    }

    @Test
    public void testOnUserGroupsUpdatedEvent() {
        final OnUpdateUserGroupsEvent onUpdateUserGroupsEvent = mock(OnUpdateUserGroupsEvent.class);
        when(onUpdateUserGroupsEvent.getContext()).thenReturn(userAssignedGroupsEditor);
        tested.user = user;
        tested.onOnUserGroupsUpdatedEvent(onUpdateUserGroupsEvent);
        assertSetDirty();
    }

    private void assertSetDirty() {
        verify(view, times(1)).setSaveButtonEnabled(true);
        verify(view, times(1)).showNotification(anyString());
    }
}
