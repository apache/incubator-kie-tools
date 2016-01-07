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

package org.uberfire.ext.security.management.wildfly.properties;

import org.apache.commons.io.FileUtils;
import org.jboss.errai.security.shared.api.Group;
import org.jboss.errai.security.shared.api.Role;
import org.jboss.errai.security.shared.api.identity.User;
import org.jboss.errai.security.shared.api.identity.UserImpl;
import org.junit.*;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.uberfire.ext.security.management.BaseTest;
import org.uberfire.ext.security.management.api.*;
import org.uberfire.ext.security.management.api.exception.UserNotFoundException;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

/**
 * This tests create temporary working copy of the "application-users.properties" file as the tests are run using the real wildfly admin api for realm management. 
 */
@RunWith(MockitoJUnitRunner.class)
public class WildflyUsersPropertiesManagerTest extends BaseTest {

    protected static final String USERS_FILE = "org/uberfire/ext/security/management/wildfly/application-users.properties";
    protected String usersFilePath;

    @Spy
    private WildflyUserPropertiesManager usersPropertiesManager = new WildflyUserPropertiesManager();

    @Mock private WildflyGroupPropertiesManager groupPropertiesManager;
    
    private static File elHome;
    
    @ClassRule
    public static TemporaryFolder tempFolder = new TemporaryFolder();

    @BeforeClass
    public static void initWorkspace() throws Exception {
        elHome = tempFolder.newFolder("uf-extensions-security-management-wildfly");
    }

    @Before
    public void setup() throws Exception {
        URL templateURL = Thread.currentThread().getContextClassLoader().getResource(USERS_FILE);
        File templateFile = new File(templateURL.getFile());
        FileUtils.cleanDirectory(elHome);
        FileUtils.copyFileToDirectory(templateFile, elHome);
        this.usersFilePath = new File(elHome, templateFile.getName()).getAbsolutePath();
        doReturn(usersFilePath).when(usersPropertiesManager).getUsersFilePath();
        usersPropertiesManager.initialize(userSystemManager);
        doReturn(groupPropertiesManager).when(usersPropertiesManager).getGroupsPropertiesManager();
    }
    
    @After
    public void finishIt() throws Exception {
        usersPropertiesManager.destroy();
    }

    @Test
    public void testCapabilities() {
        assertEquals(usersPropertiesManager.getCapabilityStatus(Capability.CAN_SEARCH_USERS), CapabilityStatus.ENABLED);
        assertEquals(usersPropertiesManager.getCapabilityStatus(Capability.CAN_READ_USER), CapabilityStatus.ENABLED);
        assertEquals(usersPropertiesManager.getCapabilityStatus(Capability.CAN_UPDATE_USER), CapabilityStatus.ENABLED);
        assertEquals(usersPropertiesManager.getCapabilityStatus(Capability.CAN_ADD_USER), CapabilityStatus.ENABLED);
        assertEquals(usersPropertiesManager.getCapabilityStatus(Capability.CAN_DELETE_USER), CapabilityStatus.ENABLED);
        assertEquals(usersPropertiesManager.getCapabilityStatus(Capability.CAN_MANAGE_ATTRIBUTES), CapabilityStatus.UNSUPPORTED);
        assertEquals(usersPropertiesManager.getCapabilityStatus(Capability.CAN_ASSIGN_GROUPS), CapabilityStatus.ENABLED);
        assertEquals(usersPropertiesManager.getCapabilityStatus(Capability.CAN_CHANGE_PASSWORD), CapabilityStatus.ENABLED);
        assertEquals(usersPropertiesManager.getCapabilityStatus(Capability.CAN_ASSIGN_ROLES), CapabilityStatus.ENABLED);
    }

    @Test
    public void testAttributes() {
        assertNull(usersPropertiesManager.getSettings().getSupportedAttributes());
    }

    @Test(expected = RuntimeException.class)
    public void testSearchPageZero() {
        AbstractEntityManager.SearchRequest request = buildSearchRequestMock("", 0, 5);
        AbstractEntityManager.SearchResponse<User> response = usersPropertiesManager.search(request);
    }
    
    @Test
    public void testSearchAll() {
        AbstractEntityManager.SearchRequest request = buildSearchRequestMock("", 1, 5);
        AbstractEntityManager.SearchResponse<User> response = usersPropertiesManager.search(request);
        assertNotNull(response);
        List<User> users = response.getResults();
        int total = response.getTotal();
        boolean hasNextPage = response.hasNextPage();
        assertEquals(total, 4);
        assertTrue(!hasNextPage);
        assertEquals(users.size(), 4);
        Set<User> expectedUsers = new HashSet<User>(4);
        expectedUsers.add(create(UserSystemManager.ADMIN));
        expectedUsers.add(create("user1"));
        expectedUsers.add(create("user2"));
        expectedUsers.add(create("user3"));
        assertThat(new HashSet<User>(users), is(expectedUsers));
    }

    @Test
    public void testGetAdmin() {
        User user = usersPropertiesManager.get(UserSystemManager.ADMIN);
        assertUser(user, UserSystemManager.ADMIN);
    }

    @Test
    public void testGetUser1() {
        User user = usersPropertiesManager.get("user1");
        assertUser(user, "user1");
    }

    @Test
    public void testGetUser2() {
        User user = usersPropertiesManager.get("user2");
        assertUser(user, "user2");
    }

    @Test
    public void testGetUser3() {
        User user = usersPropertiesManager.get("user3");
        assertUser(user, "user3");
    }

    @Test
    public void testCreateUser() {
        User user = mock(User.class);
        when(user.getIdentifier()).thenReturn("user4");
        User userCreated = usersPropertiesManager.create(user);
        assertUser(userCreated, "user4");
    }


    @Test( expected = UserNotFoundException.class )
    public void testDeleteUser() {
        usersPropertiesManager.delete("user1");
        usersPropertiesManager.get("user1");
        try {
            verify(groupPropertiesManager, times(1)).removeEntry("user1");
        } catch (IOException e) {
            fail();
        }
    }

    @Test
    public void testAssignGroups() {
        final User user = mock(User.class);
        when(user.getIdentifier()).thenReturn("user1");
        when(user.getRoles()).thenReturn(new HashSet<Role>());
        UserManager userManagerMock = mock(UserManager.class);
        doAnswer(new Answer<User>() {
            @Override
            public User answer(InvocationOnMock invocationOnMock) throws Throwable {
                return user;
            }
        }).when(userManagerMock).get("user1");
        when(userSystemManager.users()).thenReturn(userManagerMock);
        Collection<String> groups = new ArrayList<String>(2);
        groups.add( "group1" );
        groups.add( "group2" );
        usersPropertiesManager.assignGroups("user1", groups);
        ArgumentCaptor<Collection> groupsCaptor = ArgumentCaptor.forClass(Collection.class);
        verify(groupPropertiesManager, times(1)).setGroupsForUser(eq("user1"), groupsCaptor.capture());
        Collection<String> groupsCaptured = groupsCaptor.getValue();
        assertTrue(groupsCaptured.size() == 2);
        assertTrue(groupsCaptured.contains("group1"));
        assertTrue(groupsCaptured.contains("group2"));
    }

    @Test
    public void testAssignRoles() {
        final User user = mock(User.class);
        when(user.getIdentifier()).thenReturn("user1");
        when(user.getGroups()).thenReturn(new HashSet<Group>());
        UserManager userManagerMock = mock(UserManager.class);
        doAnswer(new Answer<User>() {
            @Override
            public User answer(InvocationOnMock invocationOnMock) throws Throwable {
                return user;
            }
        }).when(userManagerMock).get("user1");
        when(userSystemManager.users()).thenReturn(userManagerMock);
        Collection<String> roles = new ArrayList<String>(2);
        roles.add( "group1" );
        roles.add( "group2" );
        usersPropertiesManager.assignRoles("user1", roles);
        ArgumentCaptor<Collection> groupsCaptor = ArgumentCaptor.forClass(Collection.class);
        verify(groupPropertiesManager, times(1)).setGroupsForUser(eq("user1"), groupsCaptor.capture());
        Collection<String> groupsCaptured = groupsCaptor.getValue();
        assertTrue(groupsCaptured.size() == 2);
        assertTrue(groupsCaptured.contains("group1"));
        assertTrue(groupsCaptured.contains("group2"));
    }

    @Test
    public void testChangePassword() throws Exception {
        String oldHash = usersPropertiesManager.usersFileLoader.getProperties().getProperty("user1");
        usersPropertiesManager.changePassword("user1", "newUser1Password");
        String currentHash = usersPropertiesManager.usersFileLoader.getProperties().getProperty("user1");
        assertNotEquals(oldHash, currentHash);
    }

    private User create(String username) {
        return new UserImpl(username);
    }
    
    private void assertUser(User user, String username) {
        assertNotNull(user);
        assertEquals(user.getIdentifier(), username);
    }
    
}
