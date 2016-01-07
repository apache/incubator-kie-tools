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

package org.uberfire.ext.security.management.client.widgets.management.editor.group;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.ext.security.management.client.widgets.management.editor.AssignedEntitiesEditor;
import org.uberfire.ext.security.management.client.widgets.management.events.AddUsersToGroupEvent;
import org.uberfire.ext.security.management.client.widgets.management.explorer.EntitiesExplorerView;
import org.uberfire.ext.security.management.client.widgets.management.explorer.ExplorerViewContext;
import org.uberfire.ext.security.management.client.widgets.management.explorer.UsersExplorer;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.mvp.Command;

import java.util.HashSet;
import java.util.Set;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class GroupUsersAssignmentTest {

    @Mock AssignedEntitiesEditor<GroupUsersAssignment> view;
    @Mock UsersExplorer usersExplorer;
    @Mock EventSourceMock<AddUsersToGroupEvent> addUsersToGroupEvent;
    
    private GroupUsersAssignment tested;

    @Before
    public void setup() {
        tested = new GroupUsersAssignment(view, usersExplorer, addUsersToGroupEvent);
    }

    @Test
    public void testInit() {
        tested.init();
        verify(view, times(1)).init(tested);
        verify(view, times(1)).configure(any(EntitiesExplorerView.class));
        verify(view, times(1)).configureSave(anyString(), any(Command.class));
        verify(view, times(0)).configureClose(anyString(), any(Command.class));
        verify(view, times(0)).show(anyString());
        verify(view, times(0)).hide();
    }

    @Test
    public void testShow() {
        final String header = "header";
        tested.show(header);
        verify(usersExplorer, times(1)).clear();
        verify(usersExplorer, times(1)).show(any(ExplorerViewContext.class));
        verify(view, times(1)).show(anyString());
        verify(view, times(0)).init(tested);
        verify(view, times(0)).configure(any(EntitiesExplorerView.class));
        verify(view, times(0)).configureSave(anyString(), any(Command.class));
        verify(view, times(0)).configureClose(anyString(), any(Command.class));
        verify(view, times(0)).hide();
    }

    @Test
    public void testHide() {
        tested.hide();
        verify(view, times(1)).hide();
        verify(usersExplorer, times(0)).clear();
        verify(usersExplorer, times(0)).show(any(ExplorerViewContext.class));
        verify(view, times(0)).show(anyString());
        verify(view, times(0)).init(tested);
        verify(view, times(0)).configure(any(EntitiesExplorerView.class));
        verify(view, times(0)).configureSave(anyString(), any(Command.class));
        verify(view, times(0)).configureClose(anyString(), any(Command.class));
    }

    @Test
    public void testSaveEditorCallback() {
        final Set<String> selectedUsers = new HashSet<String>(1);
        selectedUsers.add("user1");
        when(usersExplorer.getSelectedEntities()).thenReturn(selectedUsers);
        tested.saveEditorCallback.execute();
        verify(view, times(1)).hide();
        verify(addUsersToGroupEvent, times(1)).fire(any(AddUsersToGroupEvent.class));
        verify(usersExplorer, times(0)).clear();
        verify(view, times(0)).show(anyString());
        verify(view, times(0)).init(tested);
        verify(view, times(0)).configure(any(EntitiesExplorerView.class));
        verify(view, times(0)).configureSave(anyString(), any(Command.class));
        verify(view, times(0)).configureClose(anyString(), any(Command.class));
        verify(usersExplorer, times(0)).show(any(ExplorerViewContext.class));
    }
}
