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
import org.uberfire.ext.security.management.client.widgets.management.editor.group.GroupViewer;
import org.uberfire.ext.security.management.client.widgets.management.editor.group.workflow.GroupCreationWorkflow;
import org.uberfire.ext.security.management.client.widgets.management.editor.group.workflow.GroupViewerWorkflow;
import org.uberfire.ext.security.management.client.widgets.management.events.DeleteGroupEvent;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.mvp.PlaceRequest;

import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class GroupEditorScreenTest {

    @Mock PlaceManager placeManager;
    @Mock EventSourceMock<ChangeTitleWidgetEvent> changeTitleNotification;
    @Mock ErrorPopupPresenter errorPopupPresenter;
    @Mock BaseScreen baseScreen;
    @Mock ClientUserSystemManager clientUserSystemManager;
    @Mock GroupViewerWorkflow groupViewerWorkflow;
    @Mock GroupCreationWorkflow groupCreationWorkflow;
    @Mock GroupViewer groupViewer;
    @InjectMocks GroupEditorScreen tested;

    @Before
    public void setup() {
        when(clientUserSystemManager.isUserCapabilityEnabled(any(Capability.class))).thenReturn(true);
    }
    
    @Test
    public void testOnStartupAddingGroup() {
        final PlaceRequest placeRequest = mock(PlaceRequest.class);
        when(placeRequest.getParameter(GroupEditorScreen.ADD_GROUP, "false")).thenReturn("true");
        tested.onStartup(placeRequest);
        verify(baseScreen, times(1)).init(groupCreationWorkflow);
        verify(groupCreationWorkflow, times(1)).create();
    }

    @Test
    public void testOnStartupShowingUser() {
        final PlaceRequest placeRequest = mock(PlaceRequest.class);
        when(placeRequest.getParameter(GroupEditorScreen.ADD_GROUP, "false")).thenReturn("false");
        when(placeRequest.getParameter(eq(GroupEditorScreen.GROUP_NAME), isNull(String.class))).thenReturn("group1");
        tested.onStartup(placeRequest);
        verify(baseScreen, times(1)).init(groupViewerWorkflow);
        verify(groupViewerWorkflow, times(1)).show("group1");
    }

    @Test
    public void testOnClose() {
        tested.groupName = "group1";
        tested.onClose();
        assertNull(tested.groupName);
        verify(groupViewerWorkflow, times(1)).clear();
        verify(groupCreationWorkflow, times(1)).clear();
    }

    @Test
    public void testShowError() {
        tested.showError("error");
        verify(errorPopupPresenter, times(1)).showMessage("error");
    }
    
    @Test
    public void testOnGroupDeleted() {
        final DeleteGroupEvent deleteGroupEvent = mock(DeleteGroupEvent.class);
        when(deleteGroupEvent.getName()).thenReturn("group1");
        tested.groupName = "group1";
        tested.onGroupDeleted(deleteGroupEvent);
        verify(placeManager, times(1)).closePlace(any(PlaceRequest.class));
    }
    
}
