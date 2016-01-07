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
import org.jboss.errai.security.shared.api.GroupImpl;
import org.junit.*;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.ext.security.management.BaseTest;
import org.uberfire.ext.security.management.api.AbstractEntityManager;
import org.uberfire.ext.security.management.api.Capability;
import org.uberfire.ext.security.management.api.CapabilityStatus;
import org.uberfire.ext.security.management.api.UserSystemManager;
import org.uberfire.ext.security.management.api.exception.GroupNotFoundException;
import org.uberfire.ext.security.management.api.exception.UnsupportedServiceCapabilityException;
import org.uberfire.ext.security.management.util.SecurityManagementUtils;
import org.uberfire.ext.security.server.RolesRegistry;

import java.io.File;
import java.net.URL;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class WildflyGroupsPropertiesManagerTest extends BaseTest {

    protected static final String GROUPS_FILE = "org/uberfire/ext/security/management/wildfly/application-roles.properties";
    protected String groupsFilePath;
    
    @Spy
    private WildflyGroupPropertiesManager groupsPropertiesManager = new WildflyGroupPropertiesManager();

    private static File elHome;

    @ClassRule
    public static TemporaryFolder tempFolder = new TemporaryFolder();

    @BeforeClass
    public static void initWorkspace() throws Exception {
        elHome = tempFolder.newFolder("uf-extensions-security-management-wildfly");
        RolesRegistry.get().clear();
    }
    
    @Before
    public void setup() throws Exception {
        URL templateURL = Thread.currentThread().getContextClassLoader().getResource(GROUPS_FILE);
        File templateFile = new File(templateURL.getFile());
        FileUtils.cleanDirectory(elHome);
        FileUtils.copyFileToDirectory(templateFile, elHome);
        this.groupsFilePath = new File(elHome, templateFile.getName()).getAbsolutePath();
        doReturn(groupsFilePath).when(groupsPropertiesManager).getGroupsFilePath();
        groupsPropertiesManager.initialize(userSystemManager);
    }

    @After
    public void finishIt() throws Exception {
        groupsPropertiesManager.destroy();
    }

    @Test
    public void testCapabilities() {
        assertEquals(groupsPropertiesManager.getCapabilityStatus(Capability.CAN_SEARCH_GROUPS), CapabilityStatus.ENABLED);
        assertEquals(groupsPropertiesManager.getCapabilityStatus(Capability.CAN_READ_GROUP), CapabilityStatus.ENABLED);
        assertEquals(groupsPropertiesManager.getCapabilityStatus(Capability.CAN_ADD_GROUP), CapabilityStatus.ENABLED);
        assertEquals(groupsPropertiesManager.getCapabilityStatus(Capability.CAN_DELETE_GROUP), CapabilityStatus.ENABLED);
        assertEquals(groupsPropertiesManager.getCapabilityStatus(Capability.CAN_UPDATE_GROUP), CapabilityStatus.UNSUPPORTED);
    }

    @Test
    public void testAllowsEmpty() {
        assertFalse(groupsPropertiesManager.getSettings().allowEmpty());
    }

    @Test(expected = RuntimeException.class)
    public void testSearchPageZero() {
        AbstractEntityManager.SearchRequest request = buildSearchRequestMock("", 0, 5);
        AbstractEntityManager.SearchResponse<Group> response = groupsPropertiesManager.search(request);
    }
    
    @Test
    public void testSearchAll() {
        AbstractEntityManager.SearchRequest request = buildSearchRequestMock("", 1, 5);
        AbstractEntityManager.SearchResponse<Group> response = groupsPropertiesManager.search(request);
        assertNotNull(response);
        List<Group> groups = response.getResults();
        int total = response.getTotal();
        boolean hasNextPage = response.hasNextPage();
        assertEquals(total, 5);
        assertTrue(!hasNextPage);
        assertEquals(groups.size(), 5);
        List<Group> expectedGroups = createGroupList("ADMIN", UserSystemManager.ADMIN, "role3", "role2", "role1");
        assertEquals(new HashSet<Group>(expectedGroups), new HashSet<Group>(groups));
    }
    
    @Test
    public void testGroupsForUser() {
        Set<Group> groups = groupsPropertiesManager.getGroupsAndRolesForUser(UserSystemManager.ADMIN)[0];
        assertGroupsForUser(groups, new String[]{"ADMIN"});
        groups = groupsPropertiesManager.getGroupsAndRolesForUser("user1")[0];
        assertGroupsForUser(groups, new String[]{"role1"});
        groups = groupsPropertiesManager.getGroupsAndRolesForUser("user2")[0];
        assertGroupsForUser(groups, new String[]{"role1", "role2"});
        groups = groupsPropertiesManager.getGroupsAndRolesForUser("user3")[0];
        assertGroupsForUser(groups, new String[]{"role3"});
    }

    @Test
    public void testGet() {
        assertGet(UserSystemManager.ADMIN);
        assertGet("role1");
        assertGet("role2");
        assertGet("role3");
        assertGet("ADMIN");
    }

    @Test
    public void testCreateGroup() {
        Collection<String> users = new HashSet<String>();
        users.add("user10");
        groupsPropertiesManager.assignUsers("role10", users);
        Group created = groupsPropertiesManager.get("role10");
        Set<Group> groups = groupsPropertiesManager.getGroupsAndRolesForUser("user10")[0];
        assertNotNull(created);
        assertGroupsForUser(groups, new String[]{"role10"});
    }

    @Test(expected = UnsupportedServiceCapabilityException.class)
    public void testUpdateGroup() {
        Group group = mock(Group.class);
        when(group.getName()).thenReturn("role10");
        groupsPropertiesManager.update(group);
    }

    @Test(expected = GroupNotFoundException.class)
    public void testDeleteGroup() {
        groupsPropertiesManager.delete("role3");
        groupsPropertiesManager.get("role3");
    }

    private List<Group> createGroupList(String... names) {
        if (names != null) {
            List<Group> result = new ArrayList<Group>(names.length);
            for (int x = 0; x < names.length; x++) {
                String name = names[x];
                Group g = SecurityManagementUtils.createGroup(name);
                result.add(g);
            }
            return result;
        }
        return null;
    }
    
    private void assertGet(String name) {
        Group group = groupsPropertiesManager.get(name);
        assertNotNull(group);
        assertEquals(group.getName(), name);
    }

    private void assertGroupsForUser(Set<Group> groupsSet, String[] groups) {
        assertNotNull(groupsSet);
        assertEquals(groupsSet.size(), groups.length);
        int x = 0;
        for (Group g : groupsSet) {
            String gName = groups[x];
            assertTrue(groupsSet.contains(new GroupImpl(gName)));
            x++;
        }
    }
    
    
    
}
