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

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jboss.errai.security.shared.api.Role;
import org.jboss.errai.security.shared.api.identity.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.uberfire.ext.security.management.client.ClientUserSystemManager;
import org.uberfire.ext.security.management.client.widgets.management.editor.AssignedEntitiesExplorer;
import org.uberfire.ext.security.management.client.widgets.management.events.OnRemoveUserRoleEvent;
import org.uberfire.ext.security.management.client.widgets.management.list.EntitiesList;
import org.uberfire.ext.security.management.client.widgets.management.list.RolesList;
import org.uberfire.ext.security.management.client.widgets.popup.ConfirmBox;
import org.uberfire.mocks.EventSourceMock;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.anySet;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class UserAssignedRolesExplorerTest {

    @Mock ClientUserSystemManager userSystemManager;
    @Mock EventSourceMock<OnRemoveUserRoleEvent> removeUserRoleEventEvent;
    @Mock ConfirmBox confirmBox;
    @Mock RolesList rolesList;
    @Mock AssignedEntitiesExplorer view;

    private UserAssignedRolesExplorer tested;
    @Mock User user;

    @Before
    public void setup() {
        Map<String, String> userAttributes = new HashMap<String, String>(1);
        userAttributes.put("attr1", "value1");
        when(user.getIdentifier()).thenReturn("user1");
        when(user.getProperties()).thenReturn(userAttributes);
        final Set<Role> roles = new HashSet<Role>();
        final Role role = mock(Role.class);
        when(role.getName()).thenReturn("role1");
        roles.add(role);
        when(user.getRoles()).thenReturn(roles);
        doAnswer(new Answer<Role>() {
            @Override
            public Role answer(InvocationOnMock invocationOnMock) throws Throwable {
                Role _role = mock(Role.class);
                return _role;
            }
        }).when(userSystemManager).createRole(anyString());
        tested = new UserAssignedRolesExplorer(userSystemManager, confirmBox, rolesList, view, removeUserRoleEventEvent);
    }

    @Test
    public void testInit() {
        tested.init();
        verify(rolesList, times(1)).setPageSize(anyInt());
        verify(rolesList, times(1)).setEmptyEntitiesText(anyString());
        verify(view, times(1)).configure(anyString(), any(EntitiesList.View.class));
        verify(view, times(0)).clear();
    }

    @Test
    public void testClear() {
        tested.clear();
        verify(view, times(0)).configure(anyString(), any(EntitiesList.View.class));
        verify(view, times(1)).clear();
        verify(rolesList, times(1)).clear();
        assertTrue(tested.entities.isEmpty());
        assertFalse(tested.isEditMode);
    }

    @Test
    public void testShow() {
        tested.show(user);
        assertFalse(tested.isEditMode);
        assertTrue(tested.entities.size() == 1);
        verify(view, times(0)).configure(anyString(), any(EntitiesList.View.class));
        verify(view, times(1)).clear();
        verify(rolesList, times(1)).clear();
        verify(rolesList, times(1)).show(anySet(), any(EntitiesList.Callback.class));
    }


    @Test
    public void testRemoveRole() {
        Role r = mock(Role.class);
        when(r.getName()).thenReturn("role1");
        tested.entities.add(r);
        tested.removeEntity("role1");
        assertFalse(tested.isEditMode);
        assertTrue(tested.entities.size() == 0);
        verify(rolesList, times(1)).show(anySet(), any(EntitiesList.Callback.class));
        verify(removeUserRoleEventEvent, times(1)).fire(any(OnRemoveUserRoleEvent.class));
        verify(view, times(0)).configure(anyString(), any(EntitiesList.View.class));
        verify(view, times(0)).clear();
        verify(rolesList, times(0)).clear();
    }

}
