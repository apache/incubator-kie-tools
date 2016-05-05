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

package org.uberfire.ext.security.management.client.screens.editor;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jboss.errai.security.shared.api.identity.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.events.ChangeTitleWidgetEvent;
import org.uberfire.client.workbench.widgets.common.ErrorPopupPresenter;
import org.uberfire.ext.security.management.api.Capability;
import org.uberfire.ext.security.management.client.ClientUserSystemManager;
import org.uberfire.ext.security.management.client.screens.BaseScreen;
import org.uberfire.ext.security.management.client.widgets.management.editor.user.UserEditor;
import org.uberfire.ext.security.management.client.widgets.management.editor.user.workflow.UserCreationWorkflow;
import org.uberfire.ext.security.management.client.widgets.management.editor.user.workflow.UserEditorWorkflow;
import org.uberfire.ext.security.management.client.widgets.management.events.DeleteUserEvent;
import org.uberfire.ext.security.management.client.widgets.management.events.OnEditEvent;
import org.uberfire.ext.security.management.client.widgets.management.events.OnShowEvent;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.mvp.PlaceRequest;

import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class UserEditorScreenTest {

    @Mock PlaceManager placeManager;
    @Mock EventSourceMock<ChangeTitleWidgetEvent> changeTitleNotification;
    @Mock ErrorPopupPresenter errorPopupPresenter;
    @Mock BaseScreen baseScreen;
    @Mock ClientUserSystemManager clientUserSystemManager;
    @Mock UserEditorWorkflow userEditorWorkflow;
    @Mock UserCreationWorkflow userCreationWorkflow;
    @Mock UserEditor userEditor;
    @InjectMocks UserEditorScreen tested;
    @Mock User  user;

    @Before
    public void setup() {
        when(user.getIdentifier()).thenReturn("user1");
        when(clientUserSystemManager.isUserCapabilityEnabled(any(Capability.class))).thenReturn(true);
        when(userEditorWorkflow.getUserEditor()).thenReturn(userEditor);
    }
    
    @Test
    public void testOnStartupAddingUser() {
        final PlaceRequest placeRequest = mock(PlaceRequest.class);
        when(placeRequest.getParameter(UserEditorScreen.ADD_USER, "false")).thenReturn("true");
        tested.onStartup(placeRequest);
        verify(baseScreen, times(1)).init(userCreationWorkflow);
        verify(userCreationWorkflow, times(1)).create();
    }

    @Test
    public void testOnStartupShowingUser() {
        final PlaceRequest placeRequest = mock(PlaceRequest.class);
        when(placeRequest.getParameter(UserEditorScreen.ADD_USER, "false")).thenReturn("false");
        when(placeRequest.getParameter(eq(UserEditorScreen.USER_ID), isNull(String.class))).thenReturn("user1");
        tested.onStartup(placeRequest);
        verify(baseScreen, times(1)).init(userEditorWorkflow);
        verify(userEditorWorkflow, times(1)).show("user1");
    }

    @Test
    public void testOnClose() {
        tested.userId = "user1";
        tested.onClose();
        assertNull(tested.userId);
        verify(userEditorWorkflow, times(1)).clear();
        verify(userCreationWorkflow, times(1)).clear();
    }

    @Test
    public void testShowError() {
        tested.showError("error");
        verify(errorPopupPresenter, times(1)).showMessage("error");
    }
    
    @Test
    public void testOnEditUserEvent() {
        final OnEditEvent onEditEvent = mock(OnEditEvent.class);
        when(onEditEvent.getContext()).thenReturn(userEditor);
        when(onEditEvent.getInstance()).thenReturn(user);
        tested.onEditUserEvent(onEditEvent);
        verify(changeTitleNotification, times(1)).fire(any(ChangeTitleWidgetEvent.class));
    }

    @Test
    public void testOnShowUserEvent() {
        final OnShowEvent onShowEvent = mock(OnShowEvent.class);
        when(onShowEvent.getContext()).thenReturn(userEditor);
        when(onShowEvent.getInstance()).thenReturn(user);
        tested.onShowUserEvent(onShowEvent);
        verify(changeTitleNotification, times(1)).fire(any(ChangeTitleWidgetEvent.class));
    }

    @Test
    public void testOnUserDeleted() {
        final DeleteUserEvent deleteUserEvent = mock(DeleteUserEvent.class);
        when(deleteUserEvent.getIdentifier()).thenReturn("user1");
        tested.userId = "user1";
        tested.onUserDeleted(deleteUserEvent);
        verify(placeManager, times(1)).closePlace(any(PlaceRequest.class));
    }
    
}
