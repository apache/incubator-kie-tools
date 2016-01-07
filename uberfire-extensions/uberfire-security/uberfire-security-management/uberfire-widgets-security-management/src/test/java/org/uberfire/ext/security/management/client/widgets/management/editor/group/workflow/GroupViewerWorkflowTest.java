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

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jboss.errai.security.shared.api.Group;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.uberfire.ext.security.management.client.widgets.management.AbstractSecurityManagementTest;
import org.uberfire.ext.security.management.client.widgets.management.editor.group.GroupViewer;
import org.uberfire.ext.security.management.client.widgets.management.editor.workflow.EntityWorkflowView;
import org.uberfire.ext.security.management.client.widgets.management.events.DeleteGroupEvent;
import org.uberfire.ext.security.management.client.widgets.management.events.OnDeleteEvent;
import org.uberfire.ext.security.management.client.widgets.management.events.OnErrorEvent;
import org.uberfire.ext.security.management.client.widgets.popup.ConfirmBox;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.events.NotificationEvent;

import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class GroupViewerWorkflowTest extends AbstractSecurityManagementTest {

    @Mock EventSourceMock<OnErrorEvent> errorEvent;
    @Mock ConfirmBox confirmBox;
    @Mock EventSourceMock<DeleteGroupEvent> deleteGroupEvent;
    @Mock GroupViewer groupViewer;
    @Mock EntityWorkflowView view;
    
    private GroupViewerWorkflow tested;
    @Mock Group group; 

    @Before
    public void setup() {
        super.setup();
        when(group.getName()).thenReturn("group1");
        when(view.setWidget(any(IsWidget.class))).thenReturn(view);
        when(view.clearNotification()).thenReturn(view);
        when(view.setCallback(any(EntityWorkflowView.Callback.class))).thenReturn(view);
        when(view.setCancelButtonVisible(anyBoolean())).thenReturn(view);
        when(view.setSaveButtonEnabled(anyBoolean())).thenReturn(view);
        when(view.setSaveButtonVisible(anyBoolean())).thenReturn(view);
        when(view.setSaveButtonText(anyString())).thenReturn(view);
        when(view.showNotification(anyString())).thenReturn(view);
        when(groupsManagerService.get(anyString())).thenReturn(group);
        tested = new GroupViewerWorkflow(userSystemManager, errorEvent, confirmBox, workbenchNotification, deleteGroupEvent,
            groupViewer, view);
    }

    @Test
    public void testClear() {
        tested.group = group;
        tested.clear();
        assertNull(tested.group);
        verify(groupViewer, times(1)).clear();
        verify(groupViewer, times(0)).show(any(Group.class));
        verify(view, times(1)).clearNotification();
        verify(view, times(0)).setCancelButtonVisible(anyBoolean());
        verify(view, times(0)).setCallback(any(EntityWorkflowView.Callback.class));
        verify(view, times(0)).setSaveButtonText(anyString());
        verify(view, times(0)).setWidget(any(IsWidget.class));
        verify(view, times(0)).setSaveButtonVisible(anyBoolean());
        verify(view, times(0)).setSaveButtonEnabled(anyBoolean());
        verify(view, times(0)).showNotification(anyString());
    }
    
    @Test
    public void testShow() {
        final String name = "group1";
        tested.show(name);
        verify(view, times(1)).setCancelButtonVisible(false);
        verify(view, times(1)).setCallback(isNull(EntityWorkflowView.Callback.class));
        verify(view, times(0)).setSaveButtonText(anyString());
        verify(view, times(1)).setWidget(any(IsWidget.class));
        verify(view, times(1)).setSaveButtonVisible(false);
        verify(view, times(1)).setSaveButtonEnabled(false);
        verify(view, times(0)).showNotification(anyString());
        verify(view, times(1)).clearNotification();
        verify(groupViewer, times(1)).show(group);
        verify(groupViewer, times(1)).clear();
    }

    @Test
    public void testOnDeleteUserEvent() {
        final OnDeleteEvent onDeleteEvent = mock(OnDeleteEvent.class);
        when(onDeleteEvent.getContext()).thenReturn(groupViewer);
        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocationOnMock) throws Throwable {
                final Command callback = (Command) invocationOnMock.getArguments()[2];
                callback.execute();
                return null;
            }
        }).when(confirmBox).show(anyString(), anyString(), any(Command.class));
        tested.group = group;
        tested.onDeleteUserEvent(onDeleteEvent);
        verify(confirmBox, times(1)).show(anyString(), anyString(), any(Command.class));
        verify(deleteGroupEvent, times(1)).fire(any(DeleteGroupEvent.class));
        verify(workbenchNotification, times(1)).fire(any(NotificationEvent.class));
    }

    @Test
    public void testShowError() {
        Throwable error = mock(Throwable.class);
        when(error.getMessage()).thenReturn("error1");
        tested.showError(error);
        verify(errorEvent, times(1)).fire(any(OnErrorEvent.class));
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
    }
    
}
