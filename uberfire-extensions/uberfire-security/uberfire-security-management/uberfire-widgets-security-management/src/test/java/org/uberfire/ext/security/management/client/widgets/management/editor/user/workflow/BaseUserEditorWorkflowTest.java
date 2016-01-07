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
import org.uberfire.ext.security.management.client.widgets.management.editor.user.UserEditor;
import org.uberfire.ext.security.management.client.widgets.management.editor.workflow.EntityWorkflowView;
import org.uberfire.ext.security.management.client.widgets.management.events.DeleteUserEvent;
import org.uberfire.ext.security.management.client.widgets.management.events.OnErrorEvent;
import org.uberfire.ext.security.management.client.widgets.management.events.SaveUserEvent;
import org.uberfire.ext.security.management.client.widgets.popup.ConfirmBox;
import org.uberfire.ext.security.management.client.widgets.popup.LoadingBox;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.events.NotificationEvent;

import java.util.HashSet;
import java.util.Set;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class BaseUserEditorWorkflowTest extends AbstractSecurityManagementTest {

    @Mock EventSourceMock<OnErrorEvent> errorEvent;
    @Mock EventSourceMock<DeleteUserEvent> deleteUserEvent;
    @Mock EventSourceMock<SaveUserEvent> saveUserEvent;
    @Mock ConfirmBox confirmBox;
    @Mock UserEditor userEditor;
    @Mock UserEditorDriver userEditorDriver;
    @Mock ChangePassword changePassword;
    @Mock LoadingBox loadingBox;
    @Mock EntityWorkflowView view;

    private BaseUserEditorWorkflow tested;
    @Mock User user;
    
    @Before
    public void setup() {
        super.setup();
        when(view.setWidget(any(IsWidget.class))).thenReturn(view);
        when(view.clearNotification()).thenReturn(view);
        when(view.setCallback(any(EntityWorkflowView.Callback.class))).thenReturn(view);
        when(view.setCancelButtonVisible(anyBoolean())).thenReturn(view);
        when(view.setSaveButtonEnabled(anyBoolean())).thenReturn(view);
        when(view.setSaveButtonVisible(anyBoolean())).thenReturn(view);
        when(view.setSaveButtonText(anyString())).thenReturn(view);
        when(view.showNotification(anyString())).thenReturn(view);
        final Set<Group> groups = new HashSet<Group>();
        final Group group1 = mock(Group.class);
        when(group1.getName()).thenReturn("group1");
        groups.add(group1);
        when(user.getIdentifier()).thenReturn("user1");
        when(user.getGroups()).thenReturn(groups);
        doAnswer(new Answer<User>() {
            @Override
            public User answer(InvocationOnMock invocationOnMock) throws Throwable {
                return user;
            }
        }).when(userManagerService).get(anyString());
        doAnswer(new Answer<User>() {
            @Override
            public User answer(InvocationOnMock invocationOnMock) throws Throwable {
                return user;
            }
        }).when(userManagerService).update(any(User.class));

        tested = new BaseUserEditorWorkflow(userSystemManager, errorEvent, workbenchNotification, deleteUserEvent, saveUserEvent,
                confirmBox, userEditor, userEditorDriver, changePassword, loadingBox, view) {
        };
        
    }

    @Test
    public void testDoShow() {
        final String userId = "user1";
        tested.isDirty = false;
        tested.doShow(userId);
        verify(userManagerService, times(1)).get(anyString());
        verify(view, times(1)).setCancelButtonVisible(true);
        verify(view, times(1)).setCallback(any(EntityWorkflowView.Callback.class));
        verify(view, times(1)).setSaveButtonText(anyString());
        verify(view, times(1)).setWidget(any(IsWidget.class));
        verify(view, times(1)).setSaveButtonVisible(true);
        verify(view, times(2)).setSaveButtonEnabled(false);
        verify(view, times(2)).clearNotification();
        verify(userEditor, times(1)).clear();
        verify(loadingBox, times(1)).show();
        verify(loadingBox, times(1)).hide();
        verify(userEditorDriver, times(1)).show(user, userEditor);
        verify(userEditorDriver, times(0)).edit(user, userEditor);
        verify(view, times(1)).setCancelButtonVisible(false);
        verify(view, times(1)).setSaveButtonVisible(false);
    }

    @Test
    public void testCheckDirtyFalse() {
        final Command command = mock(Command.class);
        tested.isDirty = false;
        tested.checkDirty(command);
        verify(command, times(1)).execute();
        assertNoViewCalls();
    }

    @Test
    public void testCheckDirtyTrue() {
        final Command command = mock(Command.class);
        tested.isDirty = true;
        tested.checkDirty(command);
        verify(confirmBox, times(1)).show(anyString(), anyString(), any(Command.class));
        verify(command, times(0)).execute();
        assertNoViewCalls();
    }

    @Test
    public void testSetDirtyTrue() {
        tested.user = user;
        tested.setDirty(true);
        verify(view, times(0)).setCancelButtonVisible(anyBoolean());
        verify(view, times(0)).setCallback(any(EntityWorkflowView.Callback.class));
        verify(view, times(0)).setSaveButtonText(anyString());
        verify(view, times(0)).setWidget(any(IsWidget.class));
        verify(view, times(0)).setSaveButtonVisible(anyBoolean());
        verify(view, times(1)).setSaveButtonEnabled(true);
        verify(view, times(1)).showNotification(anyString());
        verify(view, times(0)).clearNotification();
        verify(loadingBox, times(0)).show();
        verify(loadingBox, times(0)).hide();
    }

    @Test
    public void testSetDirtyFalse() {
        tested.setDirty(false);
        verify(view, times(0)).setCancelButtonVisible(anyBoolean());
        verify(view, times(0)).setCallback(any(EntityWorkflowView.Callback.class));
        verify(view, times(0)).setSaveButtonText(anyString());
        verify(view, times(0)).setWidget(any(IsWidget.class));
        verify(view, times(0)).setSaveButtonVisible(anyBoolean());
        verify(view, times(1)).setSaveButtonEnabled(false);
        verify(view, times(0)).showNotification(anyString());
        verify(view, times(1)).clearNotification();
        verify(loadingBox, times(0)).show();
        verify(loadingBox, times(0)).hide();
    }

    @Test
    public void testEdit() {
        tested.user = user;
        tested.edit();
        verify(userEditorDriver, times(1)).edit(user, userEditor);
        verify(userEditorDriver, times(0)).show(user, userEditor);
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
    public void testDoSaveWithoutGroupAssignment() {
        when(userEditorDriver.flush()).thenReturn(true);
        when(userEditorDriver.getValue()).thenReturn(user);
        when(userEditor.getValue()).thenReturn(user);
        when(userEditor.canAssignGroups()).thenReturn(false);
        when(userEditor.canAssignRoles()).thenReturn(false);
        tested.user = user;
        tested.doSave();
        verify(userManagerService, times(1)).update(any(User.class));
        verify(loadingBox, times(2)).show();
        verify(loadingBox, times(2)).hide();
    }

    @Test
    public void testDoShowChangePassword() {
        tested.user = user;
        tested.doChangePassword();
        verify(changePassword, times(1)).show(anyString(), any(ChangePassword.ChangePasswordCallback.class));
        assertNoViewCalls();
        
    }

    @Test
    public void testDoDelete() {
        tested.user = user;
        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocationOnMock) throws Throwable {
                final Command callback = (Command) invocationOnMock.getArguments()[2];
                callback.execute();
                return null;
            }
        }).when(confirmBox).show(anyString(), anyString(), any(Command.class));
        tested.doDelete();
        verify(confirmBox, times(1)).show(anyString(), anyString(), any(Command.class));
        verify(userManagerService, times(1)).delete(anyString());
        verify(loadingBox, times(1)).show();
        verify(loadingBox, times(1)).hide();
        verify(deleteUserEvent, times(1)).fire(any(DeleteUserEvent.class));
        verify(workbenchNotification, times(1)).fire(any(NotificationEvent.class));
        verify(view, times(0)).setCancelButtonVisible(anyBoolean());
        verify(view, times(0)).setCallback(any(EntityWorkflowView.Callback.class));
        verify(view, times(0)).setSaveButtonText(anyString());
        verify(view, times(0)).setWidget(any(IsWidget.class));
        verify(view, times(0)).setSaveButtonVisible(anyBoolean());
        verify(view, times(1)).setSaveButtonEnabled(false);
        verify(view, times(0)).showNotification(anyString());
        verify(view, times(2)).clearNotification();
    }
    
    
    private void assertNoViewCalls() {
        verify(view, times(0)).setCancelButtonVisible(anyBoolean());
        verify(view, times(0)).setCallback(any(EntityWorkflowView.Callback.class));
        verify(view, times(0)).setSaveButtonText(anyString());
        verify(view, times(0)).setWidget(any(IsWidget.class));
        verify(view, times(0)).setSaveButtonVisible(anyBoolean());
        verify(view, times(0)).setSaveButtonEnabled(anyBoolean());
        verify(view, times(0)).showNotification(anyString());
        verify(view, times(0)).clearNotification();
        verify(loadingBox, times(0)).show();
        verify(loadingBox, times(0)).hide();
    }
    
    
}

