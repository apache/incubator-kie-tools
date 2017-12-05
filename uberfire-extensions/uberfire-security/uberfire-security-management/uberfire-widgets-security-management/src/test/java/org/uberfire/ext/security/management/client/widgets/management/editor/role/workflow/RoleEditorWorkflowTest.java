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

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.security.shared.api.Role;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.backend.authz.AuthorizationService;
import org.uberfire.client.authz.PerspectiveAction;
import org.uberfire.client.mvp.PerspectiveActivity;
import org.uberfire.ext.security.management.client.editor.role.RoleEditorDriver;
import org.uberfire.ext.security.management.client.widgets.management.AbstractSecurityManagementTest;
import org.uberfire.ext.security.management.client.widgets.management.editor.acl.ACLSettings;
import org.uberfire.ext.security.management.client.widgets.management.editor.role.RoleEditor;
import org.uberfire.ext.security.management.client.widgets.management.editor.workflow.EntityWorkflowView;
import org.uberfire.ext.security.management.client.widgets.management.events.OnErrorEvent;
import org.uberfire.ext.security.management.client.widgets.management.events.SaveRoleEvent;
import org.uberfire.ext.security.management.client.widgets.popup.ConfirmBox;
import org.uberfire.ext.security.management.client.widgets.popup.LoadingBox;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.security.authz.PermissionCollection;
import org.uberfire.security.authz.PermissionManager;
import org.uberfire.security.impl.authz.DefaultPermissionCollection;
import org.uberfire.security.impl.authz.DefaultPermissionManager;
import org.uberfire.workbench.model.ActivityResourceType;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class RoleEditorWorkflowTest extends AbstractSecurityManagementTest {

    @Mock
    Caller<AuthorizationService> authorizationService;
    @Mock
    EventSourceMock<OnErrorEvent> errorEvent;
    @Mock
    ConfirmBox confirmBox;
    @Mock
    LoadingBox loadingBox;
    @Mock
    EventSourceMock<SaveRoleEvent> saveRoleEvent;
    @Mock
    RoleEditor roleEditor;
    @Mock
    RoleEditorDriver roleEditorDriver;
    @Mock
    EntityWorkflowView view;
    @Mock
    ACLSettings aclSettings;
    @Mock
    PerspectiveActivity homePerspective;
    @Mock
    Role role;

    PermissionManager permissionManager;
    PermissionCollection permissionCollection;
    RoleEditorWorkflow tested;

    @Before
    public void setup() {
        super.setup();
        permissionCollection = new DefaultPermissionCollection();
        permissionManager = new DefaultPermissionManager();

        when(homePerspective.getIdentifier()).thenReturn("home");
        when(homePerspective.getResourceType()).thenReturn(ActivityResourceType.PERSPECTIVE);
        when(roleEditor.permissions()).thenReturn(permissionCollection);
        when(aclSettings.getHomePerspective()).thenReturn(homePerspective);
        when(roleEditor.getAclSettings()).thenReturn(aclSettings);
        when(role.getName()).thenReturn("role1");
        when(view.setWidget(any(IsWidget.class))).thenReturn(view);
        when(view.clearNotifications()).thenReturn(view);
        when(view.setCallback(any(EntityWorkflowView.Callback.class))).thenReturn(view);
        when(view.setCancelButtonVisible(anyBoolean())).thenReturn(view);
        when(view.setSaveButtonEnabled(anyBoolean())).thenReturn(view);
        when(view.setSaveButtonVisible(anyBoolean())).thenReturn(view);
        when(view.setSaveButtonText(anyString())).thenReturn(view);
        when(view.showNotification(anyString())).thenReturn(view);
        when(rolesManagerService.get(anyString())).thenReturn(role);

        tested = spy(new RoleEditorWorkflow(userSystemManager,
                                            authorizationService,
                                            permissionManager,
                                            errorEvent,
                                            workbenchNotification,
                                            saveRoleEvent,
                                            confirmBox,
                                            roleEditor,
                                            roleEditorDriver,
                                            loadingBox,
                                            view));
    }

    @Test
    public void testClear() {
        tested.role = role;
        tested.clear();
        assertNull(tested.role);
        verify(roleEditor).clear();
        verify(roleEditor,
               never()).show(any(Role.class));
        verify(view,
               atLeastOnce()).clearNotifications();
        verify(view,
               never()).setCancelButtonVisible(true);
        verify(view,
               never()).setCallback(any(EntityWorkflowView.Callback.class));
        verify(view,
               never()).setSaveButtonText(anyString());
        verify(view,
               never()).setWidget(any(IsWidget.class));
        verify(view,
               never()).setSaveButtonVisible(anyBoolean());
        verify(view,
               never()).setSaveButtonEnabled(anyBoolean());
        verify(view,
               never()).showNotification(anyString());
    }

    @Test
    public void testShow() {
        final String name = "role1";
        tested.show(name);
        verify(view).setCancelButtonVisible(false);
        verify(view).setCallback(any(EntityWorkflowView.Callback.class));
        verify(view).setSaveButtonText(anyString());
        verify(view).setWidget(any(IsWidget.class));
        verify(view).setSaveButtonVisible(false);
        verify(view).setSaveButtonEnabled(false);
        verify(view,
               never()).showNotification(anyString());
        verify(view).clearNotifications();
        verify(roleEditorDriver).edit(role,
                                      roleEditor);
        verify(roleEditor).clear();
    }

    @Test
    public void testShowError() {
        Throwable error = mock(Throwable.class);
        when(error.getMessage()).thenReturn("error1");
        tested.showError(error);
        verify(errorEvent).fire(any(OnErrorEvent.class));
    }

    @Test
    public void testHomePerspectiveGranted() {
        permissionCollection.add(permissionManager.createPermission(homePerspective,
                                                                    PerspectiveAction.READ,
                                                                    true));
        tested.edit();
        verify(tested,
               never()).showNotification(anyString());
    }

    @Test
    public void testHomePerspectiveDenied() {
        permissionCollection.add(permissionManager.createPermission(homePerspective,
                                                                    PerspectiveAction.READ,
                                                                    false));
        tested.edit();
        verify(tested).showNotification(anyString());
    }
}
