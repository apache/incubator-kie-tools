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

import java.util.HashSet;
import java.util.Set;

import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class UserEditorWorkflowTest extends AbstractSecurityManagementTest  {

    @Mock EventSourceMock<OnErrorEvent> errorEvent;
    @Mock EventSourceMock<DeleteUserEvent> deleteUserEvent;
    @Mock EventSourceMock<SaveUserEvent> saveUserEvent;
    @Mock ConfirmBox confirmBox;
    @Mock UserEditor userEditor;
    @Mock UserEditorDriver userEditorDriver;
    @Mock ChangePassword changePassword;
    @Mock LoadingBox loadingBox;
    @Mock EntityWorkflowView view;
    
    private UserEditorWorkflow tested;
    @Mock UserAttributesEditor userAttributesEditor;
    @Mock UserAssignedGroupsExplorer userAssignedGroupsExplorer;
    @Mock UserAssignedGroupsEditor userAssignedGroupsEditor;
    @Mock User user;
    
    @Before
    public void setup() {
        super.setup();
        when(userEditor.attributesEditor()).thenReturn(userAttributesEditor);
        when(userEditor.groupsExplorer()).thenReturn(userAssignedGroupsExplorer);
        when(userEditor.groupsEditor()).thenReturn(userAssignedGroupsEditor);
        final Set<Group> groups = new HashSet<Group>();
        final Group group1 = mock(Group.class);
        when(group1.getName()).thenReturn("group1");
        groups.add(group1);
        when(user.getIdentifier()).thenReturn("user1");
        when(user.getGroups()).thenReturn(groups);
        tested = new UserEditorWorkflow(userSystemManager, errorEvent, workbenchNotification, deleteUserEvent, saveUserEvent,
                confirmBox, userEditor, userEditorDriver, changePassword, loadingBox, view);
    }
    
    @Test
    public void testOnEditUserEvent() {
        final OnEditEvent onEditEvent = mock(OnEditEvent.class);
        when(onEditEvent.getContext()).thenReturn(userEditor);
        tested.user = user;
        tested.onEditUserEvent(onEditEvent);
        verify(userEditorDriver, times(1)).edit(user, userEditor);
        verify(view, times(1)).setCancelButtonVisible(true);
        verify(view, times(0)).setCallback(any(EntityWorkflowView.Callback.class));
        verify(view, times(0)).setSaveButtonText(anyString());
        verify(view, times(0)).setWidget(any(IsWidget.class));
        verify(view, times(1)).setSaveButtonVisible(true);
        verify(view, times(1)).setSaveButtonEnabled(false);
        verify(view, times(0)).showNotification(anyString());
        verify(view, times(0)).clearNotification();
        verify(loadingBox, times(0)).show();
        verify(loadingBox, times(0)).hide();
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
