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

package org.uberfire.ext.security.management.client.editor.user;

import org.jboss.errai.security.shared.api.Group;
import org.jboss.errai.security.shared.api.Role;
import org.jboss.errai.security.shared.api.identity.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.uberfire.ext.security.management.api.UserManager;
import org.uberfire.ext.security.management.api.validation.EntityValidator;
import org.uberfire.ext.security.management.client.ClientUserSystemManager;

import javax.validation.ConstraintViolation;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class UserEditorDriverTest {

    @Mock
    ClientUserSystemManager userSystemManager;
            
    private UserEditorDriver tested;

    @Before
    public void setup() {
        tested = new UserEditorDriver(userSystemManager);    
    }
    
    @Test
    public void testCreateUser() {

        final User u = mock(User.class);
        final ArgumentCaptor<String> idCaptor =  ArgumentCaptor.forClass(String.class);
        doAnswer(new Answer<User>() {
            @Override
            public User answer(InvocationOnMock invocationOnMock) throws Throwable {
                final String idArgument = idCaptor.getValue();
                when(u.getIdentifier()).thenReturn(idArgument);
                return u;
            }
        }).when(userSystemManager).createUser(idCaptor.capture());

        doAnswer(new Answer<Collection<UserManager.UserAttribute>>() {
            @Override
            public Collection<UserManager.UserAttribute> answer(InvocationOnMock invocationOnMock) throws Throwable {
                Collection<UserManager.UserAttribute> attributes = new ArrayList<UserManager.UserAttribute>();
                UserManager.UserAttribute a1 = mockUserAttribute("a1", "v1", true, true);
                UserManager.UserAttribute a2 = mockUserAttribute("a2", "v2", false, true);
                UserManager.UserAttribute a3 = mockUserAttribute("a3", "v3", true, false);
                UserManager.UserAttribute a4 = mockUserAttribute("a4", "v4", false, false);
                attributes.add(a1);
                attributes.add(a2);
                attributes.add(a3);
                attributes.add(a4);
                return attributes;
            }
        }).when(userSystemManager).getUserSupportedAttributes();
        
        String id = "user1";
        User user = tested.createNewUser(id);
        assertEquals(user, u);
        assertEquals(user.getIdentifier(), u.getIdentifier());
        verify(user, times(1)).setProperty(anyString(), anyString());
        
    }
    
    @Test
    public void testShow() {
        final User user = mock(User.class);
        final UserEditor userEditor = mock(UserEditor.class);
        final UserAttributesEditor attributesEditor = mock(UserAttributesEditor.class);
        final UserAssignedGroupsExplorer groupsExplorer = mock(UserAssignedGroupsExplorer.class);
        final UserAssignedRolesExplorer rolesExplorer = mock(UserAssignedRolesExplorer.class);
        when(userEditor.attributesEditor()).thenReturn(attributesEditor);
        when(userEditor.groupsExplorer()).thenReturn(groupsExplorer);
        when(userEditor.rolesExplorer()).thenReturn(rolesExplorer);
        tested.isFlushed = true;
        tested.isEditMode = true;
        tested.show(user, userEditor);
        assertEquals(user, tested.user);
        assertEquals(userEditor, tested.userEditor);
        assertFalse(tested.isFlushed);
        assertFalse(tested.isEditMode);
        verify(userEditor, times(1)).show(user);
        verify(attributesEditor, times(1)).show(user);
        verify(groupsExplorer, times(1)).show(user);
        verify(rolesExplorer, times(1)).show(user);
        verify(userEditor, times(0)).edit(user);
        verify(attributesEditor, times(0)).edit(user);
        verify(groupsExplorer, times(0)).edit(user);
        verify(rolesExplorer, times(0)).edit(user);
        verify(userEditor, times(0)).flush();
        verify(attributesEditor, times(0)).flush();
        verify(groupsExplorer, times(0)).flush();
        verify(rolesExplorer, times(0)).flush();
    }

    @Test
    public void testEdit() {
        final User user = mock(User.class);
        final UserEditor userEditor = mock(UserEditor.class);
        final UserAttributesEditor attributesEditor = mock(UserAttributesEditor.class);
        final UserAssignedGroupsExplorer groupsExplorer = mock(UserAssignedGroupsExplorer.class);
        final UserAssignedRolesExplorer rolesExplorer = mock(UserAssignedRolesExplorer.class);
        when(userEditor.attributesEditor()).thenReturn(attributesEditor);
        when(userEditor.groupsExplorer()).thenReturn(groupsExplorer);
        when(userEditor.rolesExplorer()).thenReturn(rolesExplorer);
        tested.isFlushed = true;
        tested.isEditMode = false;
        tested.edit(user, userEditor);
        assertEquals(user, tested.user);
        assertEquals(userEditor, tested.userEditor);
        assertFalse(tested.isFlushed);
        assertTrue(tested.isEditMode);
        verify(userEditor, times(1)).edit(user);
        verify(attributesEditor, times(1)).edit(user);
        verify(groupsExplorer, times(1)).edit(user);
        verify(rolesExplorer, times(1)).edit(user);
        verify(userEditor, times(0)).show(user);
        verify(attributesEditor, times(0)).show(user);
        verify(groupsExplorer, times(0)).show(user);
        verify(rolesExplorer, times(0)).show(user);
        verify(userEditor, times(0)).flush();
        verify(attributesEditor, times(0)).flush();
        verify(groupsExplorer, times(0)).flush();
        verify(rolesExplorer, times(0)).flush();
    }

    @Test
    public void testFlush() {
        final User user = mock(User.class);
        final Set<Role> roles = new HashSet<Role>();
        when(user.getRoles()).thenReturn(roles);
        final UserEditor userEditor = mock(UserEditor.class);
        final UserAttributesEditor attributesEditor = mock(UserAttributesEditor.class);
        final UserAssignedGroupsExplorer groupsExplorer = mock(UserAssignedGroupsExplorer.class);
        final UserAssignedRolesExplorer rolesExplorer = mock(UserAssignedRolesExplorer.class);
        when(userEditor.identifier()).thenReturn("user1");
        when(userEditor.attributesEditor()).thenReturn(attributesEditor);
        final Map<String, String> attributes = new HashMap<String, String>();
        when(attributesEditor.getValue()).thenReturn(attributes);
        final Set<Group> groups = new HashSet<Group>();
        when(userEditor.groupsExplorer()).thenReturn(groupsExplorer);
        when(groupsExplorer.getValue()).thenReturn(groups);
        final Set<Role> _roles = new HashSet<Role>();
        when(userEditor.rolesExplorer()).thenReturn(rolesExplorer);
        when(rolesExplorer.getValue()).thenReturn(_roles);
        final EntityValidator<User> userEntityValidator = mock(EntityValidator.class);
        when(userSystemManager.usersValidator()).thenReturn(userEntityValidator);
        final Set<ConstraintViolation<User>> violations = mock(Set.class);
        when(violations.isEmpty()).thenReturn(true);
        when(userEntityValidator.validate(any(User.class))).thenReturn(violations);
        tested.user = user;
        tested.userEditor = userEditor;
        tested.isFlushed = false;
        tested.isEditMode = true;
        
        tested.flush();
        User result = tested.getValue();
        
        verify(userEditor, times(1)).flush();
        verify(attributesEditor, times(1)).flush();
        verify(groupsExplorer, times(1)).flush();
        verify(rolesExplorer, times(1)).flush();
        verify(attributesEditor, times(1)).getValue();
        verify(groupsExplorer, times(1)).getValue();
        verify(rolesExplorer, times(1)).getValue();
        verify(userEntityValidator, times(1)).validate(any(User.class));
        assertEquals("user1", result.getIdentifier());
        assertEquals(roles, result.getRoles());
        assertEquals(groups, result.getGroups());
        assertEquals(_roles, result.getRoles());
        assertEquals(attributes, result.getProperties());
    }
    
    private UserManager.UserAttribute mockUserAttribute(String name, String defaultValue,
                                                        boolean isMandatory,
                                                        boolean isEditable) {
        
        UserManager.UserAttribute attr = mock(UserManager.UserAttribute.class);
        when(attr.getName()).thenReturn(name);
        when(attr.getDefaultValue()).thenReturn(defaultValue);
        when(attr.isMandatory()).thenReturn(isMandatory);
        when(attr.isEditable()).thenReturn(isEditable);
        return attr;
    }
    
}
