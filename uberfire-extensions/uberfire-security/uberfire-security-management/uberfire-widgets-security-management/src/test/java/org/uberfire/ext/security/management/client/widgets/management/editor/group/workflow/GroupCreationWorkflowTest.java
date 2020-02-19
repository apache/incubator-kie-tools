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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.security.shared.api.Group;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.uberfire.backend.authz.AuthorizationService;
import org.uberfire.ext.security.management.api.GroupManagerSettings;
import org.uberfire.ext.security.management.client.widgets.management.AbstractSecurityManagementTest;
import org.uberfire.ext.security.management.client.widgets.management.CreateEntity;
import org.uberfire.ext.security.management.client.widgets.management.editor.group.GroupUsersAssignment;
import org.uberfire.ext.security.management.client.widgets.management.editor.workflow.EntityWorkflowView;
import org.uberfire.ext.security.management.client.widgets.management.events.AddUsersToGroupEvent;
import org.uberfire.ext.security.management.client.widgets.management.events.CreateGroupEvent;
import org.uberfire.ext.security.management.client.widgets.management.events.OnErrorEvent;
import org.uberfire.ext.security.management.client.widgets.popup.ConfirmBox;
import org.uberfire.ext.security.management.client.widgets.popup.LoadingBox;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.mvp.Command;
import org.uberfire.security.authz.PermissionManager;
import org.uberfire.security.impl.authz.DefaultPermissionManager;
import org.uberfire.workbench.events.NotificationEvent;

import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class GroupCreationWorkflowTest extends AbstractSecurityManagementTest {

    @Mock
    AuthorizationService authorizationService;
    Caller<AuthorizationService> authorizationServiceCaller;
    @Mock
    EventSourceMock<OnErrorEvent> errorEvent;
    @Mock
    ConfirmBox confirmBox;
    @Mock
    LoadingBox loadingBox;
    @Mock
    CreateEntity createEntity;
    @Mock
    GroupUsersAssignment groupUsersAssignment;
    @Mock
    EventSourceMock<CreateGroupEvent> onCreateGroupEvent;
    @Mock
    EntityWorkflowView view;
    @Mock
    Group group;
    @Mock
    private GroupEditorWorkflow groupEditorWorkflow;

    private GroupCreationWorkflow tested;

    PermissionManager permissionManager;

    @Before
    public void setup() {
        super.setup();
        permissionManager = new DefaultPermissionManager();
        authorizationServiceCaller = new CallerMock<>(authorizationService);
        when(group.getName()).thenReturn("group1");
        when(view.setWidget(any(IsWidget.class))).thenReturn(view);
        when(view.clearNotifications()).thenReturn(view);
        when(view.setCallback(any(EntityWorkflowView.Callback.class))).thenReturn(view);
        when(view.setCancelButtonVisible(anyBoolean())).thenReturn(view);
        when(view.setSaveButtonEnabled(anyBoolean())).thenReturn(view);
        when(view.setSaveButtonVisible(anyBoolean())).thenReturn(view);
        when(view.setSaveButtonText(anyString())).thenReturn(view);
        when(view.showNotification(anyString())).thenReturn(view);
        when(groupsManagerService.get(anyString())).thenReturn(group);
        GroupManagerSettings settings = mock(GroupManagerSettings.class);
        when(settings.allowEmpty()).thenReturn(true);
        when(userSystemManager.getGroupManagerSettings()).thenReturn(settings);
        tested = new GroupCreationWorkflow(userSystemManager,
                                           authorizationServiceCaller,
                                           permissionManager,
                                           errorEvent,
                                           confirmBox,
                                           loadingBox,
                                           workbenchNotification,
                                           createEntity,
                                           groupUsersAssignment,
                                           onCreateGroupEvent,
                                           view);
    }

    @Test
    public void testClear() {
        tested.group = group;
        tested.clear();
        assertNull(tested.group);
        verify(createEntity,
               times(1)).clear();
        verify(groupUsersAssignment,
               times(1)).clear();
        verify(view,
               times(1)).clearNotifications();
        verify(view,
               times(0)).setCancelButtonVisible(anyBoolean());
        verify(view,
               times(0)).setCallback(any(EntityWorkflowView.Callback.class));
        verify(view,
               times(0)).setSaveButtonText(anyString());
        verify(view,
               times(0)).setWidget(any(IsWidget.class));
        verify(view,
               times(0)).setSaveButtonVisible(anyBoolean());
        verify(view,
               times(0)).setSaveButtonEnabled(anyBoolean());
        verify(view,
               times(0)).showNotification(anyString());
    }

    @Test
    public void testShowError() {
        tested.showErrorMessage("error1");
        verify(errorEvent,
               times(1)).fire(any(OnErrorEvent.class));
    }

    @Test
    public void testCreate() {
        tested.create();
        verify(createEntity,
               times(1)).show(anyString(),
                              anyString());
        verify(view,
               times(1)).setCancelButtonVisible(false);
        verify(view,
               times(1)).setCallback(any(EntityWorkflowView.Callback.class));
        verify(view,
               times(1)).setSaveButtonText(anyString());
        verify(view,
               times(1)).setWidget(any(IsWidget.class));
        verify(view,
               times(1)).setSaveButtonVisible(true);
        verify(view,
               times(1)).setSaveButtonEnabled(true);
        verify(view,
               times(1)).clearNotifications();
    }

    @Test
    public void testShowUsersAssignment() {
        tested.group = group;
        tested.showUsersAssignment(group.getName());
        verify(groupUsersAssignment,
               times(1)).show(anyString());
        verify(view,
               times(1)).setCancelButtonVisible(true);
        verify(view,
               times(1)).setCallback(any(EntityWorkflowView.Callback.class));
        verify(view,
               times(1)).setSaveButtonText(anyString());
        verify(view,
               times(1)).setWidget(any(IsWidget.class));
        verify(view,
               times(1)).setSaveButtonVisible(false);
        verify(view,
               times(1)).setSaveButtonEnabled(false);
        verify(view,
               times(0)).clearNotifications();
    }

    @Test
    public void testCheckCreateExisting() {
        when(groupsManagerService.get(anyString())).thenReturn(group);
        when(createEntity.getEntityIdentifier()).thenReturn("group1");
        tested.checkCreate();
        verify(loadingBox,
               times(1)).show();
        verify(loadingBox,
               times(1)).hide();
        verify(errorEvent,
               times(1)).fire(any(OnErrorEvent.class));
        verify(createEntity,
               times(1)).setErrorState();
    }

    @Test
    public void testCheckCreateConstrainedGroup() {
        Collection<String> cGroups = new ArrayList<String>(1);
        cGroups.add("admin");
        when(userSystemManager.getConstrainedGroups()).thenReturn(cGroups);
        when(createEntity.getEntityIdentifier()).thenReturn("admin");
        tested.checkCreate();
        verify(loadingBox,
               times(1)).show();
        verify(loadingBox,
               times(1)).hide();
        verify(errorEvent,
               times(1)).fire(any(OnErrorEvent.class));
        verify(createEntity,
               times(1)).setErrorState();
    }

    @Test
    public void testCreateGroup() {
        final CreateGroupEvent onCreateEvent = mock(CreateGroupEvent.class);
        when(onCreateEvent.getName()).thenReturn("group1");
        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocationOnMock) throws Throwable {
                Command noCommand = (Command) invocationOnMock.getArguments()[3];
                noCommand.execute();
                return null;
            }
        }).when(confirmBox).show(anyString(),
                                 anyString(),
                                 any(Command.class),
                                 any(Command.class));
        tested.createGroup("group1");
        tested.onCreateGroupEvent(onCreateEvent);
        verify(createEntity,
               times(2)).clear();
        verify(loadingBox,
               times(1)).show();
        verify(loadingBox,
               times(1)).hide();
        verify(confirmBox,
               times(1)).show(anyString(),
                              anyString(),
                              any(Command.class),
                              any(Command.class));
        verify(workbenchNotification,
               times(1)).fire(any(NotificationEvent.class));
        verify(onCreateGroupEvent,
               times(1)).fire(any(CreateGroupEvent.class));
    }

    @Test
    public void testOnAssignUsers() {
        final AddUsersToGroupEvent addUsersToGroupEvent = mock(AddUsersToGroupEvent.class);
        final Set<String> users = new HashSet<String>(1);
        users.add("user1");
        when(addUsersToGroupEvent.getContext()).thenReturn(groupUsersAssignment);
        when(addUsersToGroupEvent.getUsers()).thenReturn(users);
        tested.group = group;
        tested.onAssignUsers(addUsersToGroupEvent);
        verify(loadingBox,
               times(1)).show();
        verify(loadingBox,
               times(1)).hide();
        verify(workbenchNotification,
               times(1)).fire(any(NotificationEvent.class));
        verify(onCreateGroupEvent,
               times(1)).fire(any(CreateGroupEvent.class));
        verify(createEntity,
               times(1)).show(anyString(),
                              anyString());
        verify(view,
               times(1)).setCancelButtonVisible(false);
        verify(view,
               times(1)).setCallback(any(EntityWorkflowView.Callback.class));
        verify(view,
               times(1)).setSaveButtonText(anyString());
        verify(view,
               times(1)).setWidget(any(IsWidget.class));
        verify(view,
               times(1)).setSaveButtonVisible(true);
        verify(view,
               times(1)).setSaveButtonEnabled(true);
        verify(view,
               times(1)).clearNotifications();
    }
}
