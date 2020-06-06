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

import java.util.List;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Variant;

import org.guvnor.rest.client.NewGroup;
import org.guvnor.rest.client.NewUser;
import org.guvnor.rest.client.UberfireRestResponse;
import org.guvnor.rest.client.UpdateSettingRequest;
import org.jboss.errai.security.shared.api.Group;
import org.jboss.errai.security.shared.api.GroupImpl;
import org.jboss.errai.security.shared.api.Role;
import org.jboss.errai.security.shared.api.RoleImpl;
import org.jboss.errai.security.shared.api.identity.User;
import org.jboss.errai.security.shared.api.identity.UserImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.soup.commons.util.Lists;
import org.kie.soup.commons.util.Sets;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.ext.security.management.api.service.GroupManagerService;
import org.uberfire.ext.security.management.api.service.RoleManagerService;
import org.uberfire.ext.security.management.api.service.UserManagerService;
import org.uberfire.workbench.model.AppFormerActivities;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UserManagementResourceJobTest {

    @Mock
    private GroupManagerService groupManagerService;

    @Mock
    private RoleManagerService roleManagerService;

    @Mock
    private UserManagerService userManagerService;

    @Mock
    private UserManagementResourceHelper jobRequestHelper;

    @Mock
    private AppFormerActivities appFormerActivities;

    @InjectMocks
    UserManagementResource userManagementResource = new UserManagementResource() {
        protected void assertObjectExists(final Object o,
                                          final String objectInfo,
                                          final String objectName) {

        }

        protected Variant getDefaultVariant() {
            return null;
        }

        protected Response createResponse(final UberfireRestResponse restResponse) {
            return null;
        }
    };

    @Test
    public void getAllGroupsTest() throws Exception {
        when(groupManagerService.getAll()).thenReturn(new Lists.Builder<Group>().add(new GroupImpl("testGroup")).build());
        assertThat(userManagementResource.getGroups()).isNotNull();
    }

    @Test
    public void getAllRolesTest() throws Exception {
        when(roleManagerService.getAll()).thenReturn(new Lists.Builder<Role>().add(new RoleImpl("testRole")).build());
        assertThat(userManagementResource.getRoles().size()).isOne();
    }

    @Test
    public void getAllUsersTest() throws Exception {
        when(userManagerService.getAll()).thenReturn(new Lists.Builder<User>().add(new UserImpl("testuser")).build());
        assertThat(userManagementResource.getUsers().size()).isOne();
    }

    @Test
    public void getAllEditorsTest() throws Exception {
        List<String> perspectives = new Lists.Builder<String>()
                .add("perpective1")
                .add("perpective2")
                .build();
        when(appFormerActivities.getAllPerpectivesIds()).thenReturn(perspectives);
        assertThat(userManagementResource.getPerpectives().size()).isEqualTo(2);
    }

    @Test
    public void getAllPerspectivesTest() throws Exception {
        List<String> editors = new Lists.Builder<String>()
                .add("editor1")
                .add("editor2")
                .add("editor3")
                .build();
        when(appFormerActivities.getAllEditorIds()).thenReturn(editors);
        assertThat(userManagementResource.getEditors().size()).isEqualTo(3);
    }

    @Test
    public void getUserGroupsTest() throws Exception {
        User user = mock(User.class);
        when(user.getGroups()).thenReturn(new Sets.Builder<Group>().add(new GroupImpl("testgroup")).build());
        when(userManagerService.getUser("testUser")).thenReturn(user);
        assertThat(userManagementResource.getUserGroups("testUser").size()).isOne();
    }

    @Test
    public void getUserRolesTest() throws Exception {
        User user = mock(User.class);
        when(user.getRoles()).thenReturn(new Sets.Builder<Role>().add(new RoleImpl("testRole")).build());
        when(userManagerService.getUser("testUser")).thenReturn(user);
        assertThat(userManagementResource.getUserRoles("testUser").size()).isOne();
    }

    @Test
    public void createGroupTest() throws Exception {
        NewGroup newGroup = new NewGroup();
        userManagementResource.createGroup(newGroup);
        verify(jobRequestHelper).createGroup(newGroup.getName(), newGroup.getUsers());
    }

    @Test
    public void createUserTest() throws Exception {
        NewUser newUser = new NewUser();
        userManagementResource.createUser(newUser);
        verify(jobRequestHelper).createUser(newUser);
    }

    @Test
    public void removeGroupTest() throws Exception {
        userManagementResource.deleteGroup("testGroup");
        verify(jobRequestHelper).removeGroup("testGroup");
    }

    @Test
    public void removeUserTest() throws Exception {
        userManagementResource.deleteUser("testUser");
        verify(jobRequestHelper).removeUser("testUser");
    }

    @Test
    public void assignGroupToUserTest() throws Exception {
        List<String> roles = new Lists.Builder<String>().add("testRole").build();
        userManagementResource.assignRolesToUser("testUser", roles);
        verify(jobRequestHelper).assignRolesToUser("testUser", roles);
    }

    @Test
    public void assignRolesToUserTest() throws Exception {
        List<String> groups = new Lists.Builder<String>().add("testGroup").build();
        userManagementResource.assignGroupsToUser("testUser", groups);
        verify(jobRequestHelper).assignGroupsToUser("testUser", groups);
    }

    @Test
    public void updateGroupSettingTest() throws Exception {
        UpdateSettingRequest settingRequest = new UpdateSettingRequest();
        userManagementResource.updateGroupPermissions("groupName", settingRequest);
        verify(jobRequestHelper).updateGroupPermissions("groupName", settingRequest);
    }

    @Test
    public void updateRoleSettingTest() throws Exception {
        UpdateSettingRequest settingRequest = new UpdateSettingRequest();
        userManagementResource.updateRolePermissions("roleName", settingRequest);
        verify(jobRequestHelper).updateRolePermissions("roleName", settingRequest);
    }

    @Test
    public void getRolePermisssionTest() throws Exception {
        userManagementResource.getRolePermissions("roleName");
        verify(jobRequestHelper).getRolePermissions("roleName");
    }

    @Test
    public void getGroupPermisssionTest() throws Exception {
        userManagementResource.getGroupPermissions("groupname");
        verify(jobRequestHelper).getGroupPermissions("groupname");
    }

    @Test
    public void getUserPermisssionTest() throws Exception {
        userManagementResource.getUserPermissions("userName");
        verify(jobRequestHelper).getUserPermissions("userName");
    }

    @Test
    public void getchangePasswordTest() throws Exception {
        userManagementResource.changePassword("user", "newPassword");
        verify(jobRequestHelper).changePassword("user", "newPassword");
    }

}
