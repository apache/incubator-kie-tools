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

package org.uberfire.ext.security.management.keycloak;

import org.jboss.errai.security.shared.api.Group;
import org.jboss.errai.security.shared.api.Role;
import org.jboss.errai.security.shared.api.identity.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.keycloak.admin.client.resource.RoleMappingResource;
import org.keycloak.admin.client.resource.RoleScopeResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.mockito.ArgumentCaptor;
import org.mockito.Spy;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.uberfire.ext.security.management.api.AbstractEntityManager;
import org.uberfire.ext.security.management.api.Capability;
import org.uberfire.ext.security.management.api.CapabilityStatus;
import org.uberfire.ext.security.management.api.UserManager;
import org.uberfire.ext.security.management.api.exception.UserNotFoundException;

import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class KeyCloakUserManagerTest extends DefaultKeyCloakTest {
    
    @Spy
    private KeyCloakUserManager usersManager = new KeyCloakUserManager();
    
    @Before
    public void setup() throws Exception {
        super.setup();
        doReturn(keycloakMock).when(usersManager).getKeyCloakInstance();
        doReturn(realmResource).when(usersManager).getRealmResource();
        usersManager.initialize(userSystemManager);
    }

    @Test
    public void testCapabilities() {
        assertEquals(usersManager.getCapabilityStatus(Capability.CAN_SEARCH_USERS), CapabilityStatus.ENABLED);
        assertEquals(usersManager.getCapabilityStatus(Capability.CAN_READ_USER), CapabilityStatus.ENABLED);
        assertEquals(usersManager.getCapabilityStatus(Capability.CAN_UPDATE_USER), CapabilityStatus.ENABLED);
        assertEquals(usersManager.getCapabilityStatus(Capability.CAN_ADD_USER), CapabilityStatus.ENABLED);
        assertEquals(usersManager.getCapabilityStatus(Capability.CAN_DELETE_USER), CapabilityStatus.ENABLED);
        assertEquals(usersManager.getCapabilityStatus(Capability.CAN_MANAGE_ATTRIBUTES), CapabilityStatus.ENABLED);
        assertEquals(usersManager.getCapabilityStatus(Capability.CAN_ASSIGN_GROUPS), CapabilityStatus.ENABLED);
        assertEquals(usersManager.getCapabilityStatus(Capability.CAN_CHANGE_PASSWORD), CapabilityStatus.ENABLED);
        assertEquals(usersManager.getCapabilityStatus(Capability.CAN_ASSIGN_ROLES), CapabilityStatus.ENABLED);
    }
    
    @Test
    public void testAttributes() {
        final Collection<UserManager.UserAttribute> USER_ATTRIBUTES =
                Arrays.asList(BaseKeyCloakManager.USER_ID, BaseKeyCloakManager.USER_FIST_NAME, BaseKeyCloakManager.USER_LAST_NAME,
                        BaseKeyCloakManager.USER_ENABLED, BaseKeyCloakManager.USER_EMAIL, 
                        BaseKeyCloakManager.USER_EMAIL_VERIFIED);
        Collection<UserManager.UserAttribute> attributes = usersManager.getSettings().getSupportedAttributes();
        assertEquals(attributes,USER_ATTRIBUTES);
    }
    
    @Test
    public void testGetUser5() {
        String username = USERNAME + 5;
        User user = usersManager.get(username);
        assertUser(user, username);
    }

    @Test(expected = UserNotFoundException.class)
    public void testGetUser50() {
        String username = USERNAME + 50;
        User user = usersManager.get(username);
        assertNull(user);
    }

    @Test(expected = RuntimeException.class)
    public void testSearchPageZero() {
        AbstractEntityManager.SearchRequest request = buildSearchRequestMock("", 0, 5);
        AbstractEntityManager.SearchResponse<User> response = usersManager.search(request);
    }
    
    @Test
    public void testSearchAllFirstPage() {
        AbstractEntityManager.SearchRequest request = buildSearchRequestMock("", 1, 5);
        AbstractEntityManager.SearchResponse<User> response = usersManager.search(request);
        assertNotNull(response);
        List<User> users = response.getResults();
        int total = response.getTotal();
        boolean hasNextPage = response.hasNextPage();
        assertEquals(total, -1);
        assertTrue(hasNextPage);
        assertEquals(users.size(), 5);
        User user0  = users.get(0);
        assertUser(user0, USERNAME + 0);
        User user4 = users.get(4);
        assertUser(user4, USERNAME + 4);
    }

    @Test
    public void testSearchAllSecondPage() {
        AbstractEntityManager.SearchRequest request = buildSearchRequestMock("", 2, 5);
        AbstractEntityManager.SearchResponse<User> response = usersManager.search(request);
        assertNotNull(response);
        List<User> users = response.getResults();
        int total = response.getTotal();
        boolean hasNextPage = response.hasNextPage();
        assertEquals(total, -1);
        assertTrue(hasNextPage);
        assertEquals(users.size(), 5);
        User user5  = users.get(0);
        assertUser(user5, USERNAME + 5);
        User user9 = users.get(4);
        assertUser(user9, USERNAME + 9);
    }

    @Test
    public void testSearchAllThirdPage() {
        AbstractEntityManager.SearchRequest request = buildSearchRequestMock("", 3, 5);
        AbstractEntityManager.SearchResponse<User> response = usersManager.search(request);
        assertNotNull(response);
        List<User> users = response.getResults();
        int total = response.getTotal();
        boolean hasNextPage = response.hasNextPage();
        assertEquals(total, -1);
        assertTrue(hasNextPage);
        assertEquals(users.size(), 5);
        User user10  = users.get(0);
        assertUser(user10, USERNAME + 10);
        User user14 = users.get(4);
        assertUser(user14, USERNAME + 14);
    }

    @Test
    public void testSearchAllLastPage() {
        AbstractEntityManager.SearchRequest request = buildSearchRequestMock("", 4, 5);
        AbstractEntityManager.SearchResponse<User> response = usersManager.search(request);
        assertNotNull(response);
        List<User> users = response.getResults();
        int total = response.getTotal();
        boolean hasNextPage = response.hasNextPage();
        assertEquals(total, -1);
        assertTrue(hasNextPage);
        assertEquals(users.size(), 5);
        User user15  = users.get(0);
        assertUser(user15, USERNAME + 15);
        User user19 = users.get(4);
        assertUser(user19, USERNAME + 19);
    }

    @Test
    public void testCreateUser() {
        User user = mock(User.class);
        when(user.getIdentifier()).thenReturn("user0");
        User userCreated = usersManager.create(user);
        assertNotNull(userCreated);
        verify(usersResource, times(1)).create(any(UserRepresentation.class));
    }

    @Test
    public void testUpdateUser() {
        User user = mock(User.class);
        when(user.getIdentifier()).thenReturn("user0");
        UserResource user0Resource = userResources.get(0);
        User userUpdated = usersManager.update(user);
        assertNotNull(userUpdated);
        verify(user0Resource, times(1)).update(any(UserRepresentation.class));
    }

    @Test
    public void testDeleteUser() {
        UserResource user0Resource = userResources.get(0);
        usersManager.delete("user0");
        verify(user0Resource, times(1)).remove();
    }

    @Test
    public void testChangePassword() {
        UserResource user0Resource = userResources.get(0);
        usersManager.changePassword("user0", "newPassword");
        verify(user0Resource, times(1)).resetPassword(any(CredentialRepresentation.class));
    }
    
    @Test
    public void testAssignGroups() {
        final User user = mock(User.class);
        when(user.getIdentifier()).thenReturn("user0");
        when(user.getRoles()).thenReturn(new HashSet<Role>());
        UserManager userManagerMock = mock(UserManager.class);
        doAnswer(new Answer<User>() {
            @Override
            public User answer(InvocationOnMock invocationOnMock) throws Throwable {
                return user;
            }
        }).when(userManagerMock).get("user0");
        when(userSystemManager.users()).thenReturn(userManagerMock);
        final Collection<String> groups = new ArrayList<String>();
        groups.add("role1");
        groups.add("role2");
        UserResource user0Resource = userResources.get(0);
        RoleMappingResource roleMappingResource = user0Resource.roles();
        RoleScopeResource roleScopeResource = roleMappingResource.realmLevel();
        usersManager.assignGroups("user0", groups);
        ArgumentCaptor<List> rolesCaptor = ArgumentCaptor.forClass(List.class);
        verify(roleScopeResource, times(1)).add(rolesCaptor.capture());
        List rolesAdded = rolesCaptor.getValue();
        assertEquals(2, rolesAdded.size());
    }

    @Test
    public void testAssignRoles() {
        final User user = mock(User.class);
        when(user.getIdentifier()).thenReturn("user0");
        when(user.getGroups()).thenReturn(new HashSet<Group>());
        UserManager userManagerMock = mock(UserManager.class);
        doAnswer(new Answer<User>() {
            @Override
            public User answer(InvocationOnMock invocationOnMock) throws Throwable {
                return user;
            }
        }).when(userManagerMock).get("user0");
        when(userSystemManager.users()).thenReturn(userManagerMock);
        final Collection<String> roles = new ArrayList<String>();
        roles.add("role1");
        roles.add("role2");
        UserResource user0Resource = userResources.get(0);
        RoleMappingResource roleMappingResource = user0Resource.roles();
        RoleScopeResource roleScopeResource = roleMappingResource.realmLevel();
        usersManager.assignRoles("user0", roles);
        ArgumentCaptor<List> rolesCaptor = ArgumentCaptor.forClass(List.class);
        verify(roleScopeResource, times(1)).add(rolesCaptor.capture());
        List rolesAdded = rolesCaptor.getValue();
        assertEquals(2, rolesAdded.size());
    }

    private void assertUser(User user, String username) {
        assertNotNull(user);
        assertEquals(username, user.getIdentifier());
        Map<String, String> attributes = user.getProperties();
        assertNotNull(attributes);
        assertTrue(attributes.size() >= 4);
        final String id = attributes.get(BaseKeyCloakManager.ATTRIBUTE_USER_ID);
        assertNotNull(id);
        assertTrue(id.trim().length() > 0);
        final String firstName = attributes.get(BaseKeyCloakManager.ATTRIBUTE_USER_FIRST_NAME);
        assertNotNull(firstName);
        assertEquals(username.toUpperCase(), firstName);
        final String lastName = attributes.get(BaseKeyCloakManager.ATTRIBUTE_USER_LAST_NAME);
        assertNotNull(lastName);
        assertEquals(username.toUpperCase() + "Last", lastName);
        final String enabled = attributes.get(BaseKeyCloakManager.ATTRIBUTE_USER_ENABLED);
        assertNotNull(enabled);
        assertEquals(enabled, "true");
        final String email = attributes.get(BaseKeyCloakManager.ATTRIBUTE_USER_EMAIL);
        assertNotNull(email);
        assertEquals(email, username + "@jboss.org");
    }
    
}
