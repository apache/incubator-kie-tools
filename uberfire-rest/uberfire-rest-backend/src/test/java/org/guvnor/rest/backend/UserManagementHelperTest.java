/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.guvnor.rest.backend;

import java.util.Arrays;

import javax.jws.soap.SOAPBinding;
import javax.ws.rs.core.Response;

import org.guvnor.common.services.project.service.WorkspaceProjectService;
import org.guvnor.rest.client.NewUser;
import org.guvnor.rest.client.PermissionResponse;
import org.guvnor.rest.client.UberfireRestResponse;
import org.guvnor.rest.client.UpdateSettingRequest;
import org.guvnor.structure.organizationalunit.OrganizationalUnitService;
import org.jboss.errai.security.shared.api.Group;
import org.jboss.errai.security.shared.api.GroupImpl;
import org.jboss.errai.security.shared.api.Role;
import org.jboss.errai.security.shared.api.RoleImpl;
import org.jboss.errai.security.shared.api.identity.User;
import org.jboss.errai.security.shared.api.identity.UserImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.soup.commons.util.Lists;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.authz.AuthorizationService;
import org.uberfire.ext.security.management.api.exception.GroupNotFoundException;
import org.uberfire.ext.security.management.api.exception.SecurityManagementException;
import org.uberfire.ext.security.management.api.exception.UserNotFoundException;
import org.uberfire.ext.security.management.api.service.GroupManagerService;
import org.uberfire.ext.security.management.api.service.RoleManagerService;
import org.uberfire.ext.security.management.api.service.UserManagerService;
import org.uberfire.security.authz.AuthorizationPolicy;
import org.uberfire.security.authz.PermissionCollection;
import org.uberfire.security.authz.PermissionManager;
import org.uberfire.security.authz.VotingStrategy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.isNotNull;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UserManagementHelperTest {

    @InjectMocks
    private UserManagementResourceHelper helper;
    @Mock
    private GroupManagerService groupManagerService;
    @Mock
    private RoleManagerService roleManagerService;
    @Mock
    private UserManagerService userManagerService;
    @Mock
    private OrganizationalUnitService organizationalUnitService;
    @Mock
    private WorkspaceProjectService projectService;
    @Mock
    private ResourceTypePermissionValidator permissionValidator;
    @Mock
    private AuthorizationService authorizationService;
    @Mock
    private PermissionManager permissionManager;

    @Test
    public void testGroupNotFoundWhenUpdateGroupPermission() {
        doThrow(GroupNotFoundException.class).when(groupManagerService).get("testGroup");
        UberfireRestResponse response = helper.updateGroupPermissions("testGroup", mock(UpdateSettingRequest.class));

        assertEquals(Response.Status.BAD_REQUEST,
                     response.getStatus());
    }

    @Test
    public void testCreateGroup() {
        doThrow(GroupNotFoundException.class).when(groupManagerService).get("testGroup");
        when(groupManagerService.create(new GroupImpl("testGroup"))).thenReturn(mock(Group.class));
        when(userManagerService.get("testUser")).thenReturn(mock(User.class));

        UberfireRestResponse response = helper.createGroup("testGroup", Arrays.asList("testUser"));

        assertEquals(Response.Status.OK,
                     response.getStatus());
    }

    @Test
    public void testCreateGroupWithInvalidUser() {
        doThrow(GroupNotFoundException.class).when(groupManagerService).get("testGroup");
        doThrow(UserNotFoundException.class).when(userManagerService).get("testUser");

        UberfireRestResponse response = helper.createGroup("testGroup", Arrays.asList("testUser"));

        assertEquals(Response.Status.BAD_REQUEST,
                     response.getStatus());
    }

    @Test
    public void testCreateUser() {
        doThrow(UserNotFoundException.class).when(userManagerService).get("testUser");
        when(userManagerService.create(new UserImpl("testUser"))).thenReturn(mock(User.class));
        NewUser newUser = new NewUser();
        newUser.setName("testUser");
        UberfireRestResponse response = helper.createUser(newUser);

        assertEquals(Response.Status.OK,
                     response.getStatus());
    }

    @Test
    public void testChangePassword() {
        doThrow(SecurityManagementException.class).when(userManagerService).changePassword("testUser", "testpassword");
        UberfireRestResponse response = helper.changePassword("testUser", "testpassword");

        assertEquals(Response.Status.INTERNAL_SERVER_ERROR,
                     response.getStatus());
    }

    @Test
    public void testCreateUserWithInvalidGroup() {
        doThrow(UserNotFoundException.class).when(userManagerService).get("testUser");
        doThrow(GroupNotFoundException.class).when(groupManagerService).get("testGroup");
        when(userManagerService.create(new UserImpl("testUser"))).thenReturn(mock(User.class));
        NewUser newUser = new NewUser();
        newUser.setName("testUser");
        newUser.setGroups(new Lists.Builder<String>().add("testGroup").build());
        UberfireRestResponse response = helper.createUser(newUser);

        assertEquals(Response.Status.BAD_REQUEST,
                     response.getStatus());
    }
    @Test
    public void testRemoveGroup() {
        UberfireRestResponse response = helper.removeGroup("testGroup");

        assertEquals(Response.Status.OK,
                     response.getStatus());
    }

    @Test
    public void testRemoveUser() {
        UberfireRestResponse response = helper.removeUser("testUser");

        assertEquals(Response.Status.OK,
                     response.getStatus());
    }

    @Test
    public void testAssignGroupsToUser() {
        when(groupManagerService.get("testGroup")).thenReturn(mock(Group.class));
        UberfireRestResponse response = helper.assignGroupsToUser("testUser", new Lists.Builder<String>().add("testGroup").build());

        assertEquals(Response.Status.OK,
                     response.getStatus());
    }

    @Test
    public void testAssignInvalidGroupsToUser() {
        doThrow(GroupNotFoundException.class).when(groupManagerService).get("testGroup");
        UberfireRestResponse response = helper.assignGroupsToUser("testUser", new Lists.Builder<String>().add("testGroup").build());

        assertEquals(Response.Status.BAD_REQUEST,
                     response.getStatus());
    }

    @Test
    public void testAssignRolesToUser() {
        when(roleManagerService.get("testRole")).thenReturn(mock(Role.class));
        UberfireRestResponse response = helper.assignRolesToUser("testUser", new Lists.Builder<String>().add("testRole").build());

        assertEquals(Response.Status.OK,
                     response.getStatus());
    }

    @Test
    public void testUpdateRolePermission() {
        when(permissionManager.getAuthorizationPolicy()).thenReturn(mock(AuthorizationPolicy.class));
        when(roleManagerService.get("testRole")).thenReturn(new RoleImpl("testRole"));
        UpdateSettingRequest request = mock(UpdateSettingRequest.class);
        UberfireRestResponse response = helper.updateRolePermissions("testRole", request);

        assertEquals(Response.Status.OK,
                     response.getStatus());
    }

    @Test
    public void testUpdateGroupPermission() {
        when(permissionManager.getAuthorizationPolicy()).thenReturn(mock(AuthorizationPolicy.class));
        when(groupManagerService.get("testGroup")).thenReturn(new GroupImpl("testGroup"));
        UpdateSettingRequest request = mock(UpdateSettingRequest.class);
        UberfireRestResponse response = helper.updateGroupPermissions("testGroup", request);
        assertEquals(Response.Status.OK,
                     response.getStatus());
    }

    @Test
    public void testGetGroupPermission() {
        Group group = mock(Group.class);
        when(group.getName()).thenReturn("testGroup");
        AuthorizationPolicy authz = mock(AuthorizationPolicy.class);
        when(authz.getHomePerspective(group)).thenReturn("Home");
        when(authz.getPriority(group)).thenReturn(10);
        when(authz.getPermissions(group)).thenReturn(mock(PermissionCollection.class));
        when(permissionManager.getAuthorizationPolicy()).thenReturn(authz);
        when(groupManagerService.get("testGroup")).thenReturn(group);
        PermissionResponse response = helper.getGroupPermissions("testGroup");

        assertNotNull(response);
        assertEquals("Home", response.getHomePage());
        assertEquals(new Integer(10), response.getPriority());

    }

    @Test
    public void testGetRolePermission() {
        Role role = mock(Role.class);
        when(role.getName()).thenReturn("testRole");
        AuthorizationPolicy authz = mock(AuthorizationPolicy.class);
        when(authz.getHomePerspective(role)).thenReturn("Home");
        when(authz.getPriority(role)).thenReturn(10);
        when(authz.getPermissions(role)).thenReturn(mock(PermissionCollection.class));
        when(permissionManager.getAuthorizationPolicy()).thenReturn(authz);
        when(roleManagerService.get("testRole")).thenReturn(role);
        PermissionResponse response = helper.getRolePermissions("testRole");

        assertNotNull(response);
        assertEquals("Home", response.getHomePage());
        assertEquals(new Integer(10), response.getPriority());
    }

    @Test
    public void testGetUserPermission() {
        User user = mock(User.class);
        AuthorizationPolicy authz = mock(AuthorizationPolicy.class);
        when(permissionManager.resolvePermissions(user, VotingStrategy.PRIORITY)).thenReturn(mock(PermissionCollection.class));
        when(userManagerService.get("testUser")).thenReturn(user);
        PermissionResponse response = helper.getUserPermissions("testUser");

        assertNotNull(response);
    }
}
