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

import org.junit.Before;
import org.junit.runner.RunWith;
import org.keycloak.admin.client.resource.RoleMappingResource;
import org.keycloak.admin.client.resource.RoleResource;
import org.keycloak.admin.client.resource.RoleScopeResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * <p>It provides a default set of users and roles for mocking a keycloak service.</p>
 */
@RunWith(MockitoJUnitRunner.class)
public abstract class DefaultKeyCloakTest extends BaseKeyCloakTest {

    public static final String USERNAME = "user";
    public static final String ROLE = "role";
    public static final int rolesCount = 50;
    public static final int usersCount = 30;

    protected final List<RoleResource> roleResources = new ArrayList<RoleResource>();
    protected final List<RoleRepresentation> roleRepresentations = new ArrayList<RoleRepresentation>();
    protected final  List<UserResource> userResources = new ArrayList<UserResource>();
    protected final List<UserRepresentation> userRepresentations = new ArrayList<UserRepresentation>();

    @Before
    public void setup() throws Exception {
        super.setup();
        // Roles.
        for (int x = 0; x < rolesCount; x++) {
            String name = ROLE + x;
            RoleResource roleResource = mock(RoleResource.class);
            mockRoleResource(roleResource, name);
            roleResources.add(roleResource);
            roleRepresentations.add(roleResource.toRepresentation());
        }
        when(rolesResource.get(anyString())).thenAnswer(new Answer<RoleResource>() {
            @Override
            public RoleResource answer(InvocationOnMock invocationOnMock) throws Throwable {
                String name = (String) invocationOnMock.getArguments()[0];
                return getRole(roleResources, name);
            }
        });
        when(rolesResource.list()).thenReturn(roleRepresentations);
        
        // Users.
        for (int x = 0; x < usersCount; x++) {
            String username = USERNAME + x;
            UserResource userResource = mock(UserResource.class);
            mockUserResource(userResource, username);
            userResources.add(userResource);
            userRepresentations.add(userResource.toRepresentation());
        }
        when(usersResource.get(anyString())).thenAnswer(new Answer<UserResource>() {
            @Override
            public UserResource answer(InvocationOnMock invocationOnMock) throws Throwable {
                String id = (String) invocationOnMock.getArguments()[0];
                return getUser(userResources, id);
            }
        });
        when(usersResource.search(anyString(), anyInt(), anyInt())).thenAnswer(new Answer<List<UserRepresentation>>() {
            @Override
            public List<UserRepresentation> answer(InvocationOnMock invocationOnMock) throws Throwable {
                String pattern = (String) invocationOnMock.getArguments()[0];
                Integer start = (Integer) invocationOnMock.getArguments()[1];
                Integer size = (Integer) invocationOnMock.getArguments()[2];
                List<UserRepresentation> result  = getUserRepresentations(pattern, start, size);
                return result;
            }
        });
        when(usersResource.search(anyString(), anyString(), anyString(), anyString(), anyInt(), anyInt())).thenAnswer(new Answer<List<UserRepresentation>>() {
            @Override
            public List<UserRepresentation> answer(InvocationOnMock invocationOnMock) throws Throwable {
                String pattern = (String) invocationOnMock.getArguments()[0];
                Integer start = (Integer) invocationOnMock.getArguments()[4];
                Integer size = (Integer) invocationOnMock.getArguments()[5];
                List<UserRepresentation> result  = getUserRepresentations(pattern, start, size);
                return result;
            }
        });
        Response response = mock(Response.class);
        when(response.getStatus()).thenReturn(200);
        when(usersResource.create(any(UserRepresentation.class))).thenReturn(response);
    
    }
    
    private List<UserRepresentation> getUserRepresentations(String pattern, int start, int size) {
        List<UserRepresentation> result  = null;
        if (isEmpty(pattern)) {
            result = userRepresentations.subList(start, start + size);
        } else {
            UserResource userResource = getUser(userResources, pattern);
            if (userResource != null) {
                result = new ArrayList<UserRepresentation>(1);
                result.add(userResource.toRepresentation());
            }
        }
        return result;
    }
    
    private boolean isEmpty(String str) {
        return str == null || str.trim().length() == 0;
    }

    private void mockUserResource(UserResource userResource, String username) {
        UserRepresentation userRepresentation = mock(UserRepresentation.class);
        when(userResource.toRepresentation()).thenReturn(userRepresentation);
        mockUserRepresentation(userRepresentation, username);
        RoleMappingResource roleMappingResource = mock(RoleMappingResource.class);
        when(userResource.roles()).thenReturn(roleMappingResource);
        RoleScopeResource roleScopeResource = mock(RoleScopeResource.class);
        when(roleMappingResource.realmLevel()).thenReturn(roleScopeResource);
        mockRoleMappingResource(roleMappingResource);
    }

    
    private void mockUserRepresentation(UserRepresentation userRepresentation, String username) {
        when(userRepresentation.getUsername()).thenReturn(username);
        String id = username;
        String fName = username.toUpperCase();
        String lName = fName + "Last";
        String mail = username + "@jboss.org";
        when(userRepresentation.getId()).thenReturn(id);
        when(userRepresentation.getFirstName()).thenReturn(fName);
        when(userRepresentation.getLastName()).thenReturn(lName);
        when(userRepresentation.getEmail()).thenReturn(mail);
        when(userRepresentation.isEmailVerified()).thenReturn(true);
        when(userRepresentation.isEnabled()).thenReturn(true);
        Map<String, Object> attributes = new HashMap<String, Object>(6);
        attributes.put(BaseKeyCloakManager.ATTRIBUTE_USER_ID, id);
        attributes.put(BaseKeyCloakManager.ATTRIBUTE_USER_FIRST_NAME, fName);
        attributes.put(BaseKeyCloakManager.ATTRIBUTE_USER_LAST_NAME, lName);
        attributes.put(BaseKeyCloakManager.ATTRIBUTE_USER_ENABLED, "true");
        attributes.put(BaseKeyCloakManager.ATTRIBUTE_USER_EMAIL, mail);
        attributes.put(BaseKeyCloakManager.ATTRIBUTE_USER_EMAIL_VERIFIED, "true");
        when(userRepresentation.getAttributes()).thenReturn(attributes);
    }
    
    
    private void mockRoleMappingResource(RoleMappingResource roleMappingResource) {
        RoleScopeResource roleScopeResource = mock(RoleScopeResource.class);
        when(roleMappingResource.realmLevel()).thenReturn(roleScopeResource);
        mockRoleScopeResource(roleScopeResource);
    }

    private void mockRoleScopeResource(RoleScopeResource roleScopeResource) {
        when(roleScopeResource.listEffective()).thenReturn(roleRepresentations);
    }

    private void mockRoleResource(RoleResource roleResource, String name) {
        RoleRepresentation roleRepresentation = mock(RoleRepresentation.class);
        when(roleResource.toRepresentation()).thenReturn(roleRepresentation);
        mockRoleRepresentation(roleRepresentation, name);
    }
    
    private void mockRoleRepresentation(RoleRepresentation roleRepresentation, String name) {
        String id = Integer.toString(name.hashCode());
        String description = "Role " + name;
        when(roleRepresentation.getId()).thenReturn(id);
        when(roleRepresentation.getName()).thenReturn(name);
        when(roleRepresentation.getDescription()).thenReturn(description);
        when(roleRepresentation.isComposite()).thenReturn(false);
    }
    
    private RoleResource getRole(List<RoleResource> roleResources, String name) {
        for (RoleResource roleResource : roleResources) {
            if (roleResource.toRepresentation().getName().equals(name)) return roleResource;
        }
        return null;
    }

    private UserResource getUser(List<UserResource> userResources, String id) {
        for (UserResource userResource : userResources) {
            if (userResource.toRepresentation().getId().equals(id)) return userResource;
        }
        return null;
    }
    
}
