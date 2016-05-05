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

package org.uberfire.ext.security.management.tomcat;

import org.apache.commons.io.FileUtils;
import org.jboss.errai.security.shared.api.Group;
import org.junit.*;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.commons.config.ConfigProperties;
import org.uberfire.ext.security.management.BaseTest;
import org.uberfire.ext.security.management.api.AbstractEntityManager;
import org.uberfire.ext.security.management.api.Capability;
import org.uberfire.ext.security.management.api.CapabilityStatus;
import org.uberfire.ext.security.management.api.UserSystemManager;
import org.uberfire.ext.security.management.api.exception.GroupNotFoundException;
import org.uberfire.ext.security.management.api.exception.UnsupportedServiceCapabilityException;
import org.uberfire.ext.security.management.util.SecurityManagementUtils;

import java.io.File;
import java.net.URL;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * This tests create temporary working copy of the "tomcat-users.xml" file as the tests are run using the real tomcat admin api for realm management. 
 */
@RunWith(MockitoJUnitRunner.class)
public class TomcatGroupManagerTest extends BaseTest {

    protected static final String USERS_FILE_PATH = "org/uberfire/ext/security/management/tomcat/";
    protected static final String USERS_FILE_NAME = "tomcat-users.xml";

    @ClassRule
    public static TemporaryFolder tempFolder = new TemporaryFolder();

    private static File elHome;
    
    @Spy
    private TomcatGroupManager groupsManager = new TomcatGroupManager();

    @BeforeClass
    public static void initWorkspace() throws Exception {
        elHome = tempFolder.newFolder("uf-extensions-security-management-tomcat");
    }

    @Before
    public void setup() throws Exception {
        URL templateURL = Thread.currentThread().getContextClassLoader().getResource(USERS_FILE_PATH + USERS_FILE_NAME);
        File templateFile = new File(templateURL.getFile());
        FileUtils.cleanDirectory(elHome);
        FileUtils.copyFileToDirectory(templateFile, elHome);
        String full = new File(elHome, templateFile.getName()).getAbsolutePath();
        String path = full.substring(0, full.lastIndexOf(File.separator));
        String name = full.substring(full.lastIndexOf(File.separator) + 1, full.length());
        Map<String, String> props = new HashMap<String, String>(1);
        props.put("org.uberfire.ext.security.management.tomcat.catalina-base", path);
        props.put("org.uberfire.ext.security.management.tomcat.users-file", name);
        System.setProperty(BaseTomcatManager.CATALINA_BASE_PROPERTY, "");
        groupsManager.loadConfig(new ConfigProperties(props));
        groupsManager.initialize(userSystemManager);
    }

    @After
    public void finishIt() throws Exception {
        groupsManager.destroy();
    }

    @Test
    public void testCapabilities() {
        assertEquals(groupsManager.getCapabilityStatus(Capability.CAN_SEARCH_GROUPS), CapabilityStatus.ENABLED);
        assertEquals(groupsManager.getCapabilityStatus(Capability.CAN_READ_GROUP), CapabilityStatus.ENABLED);
        assertEquals(groupsManager.getCapabilityStatus(Capability.CAN_ADD_GROUP), CapabilityStatus.ENABLED);
        assertEquals(groupsManager.getCapabilityStatus(Capability.CAN_DELETE_GROUP), CapabilityStatus.ENABLED);
        assertEquals(groupsManager.getCapabilityStatus(Capability.CAN_UPDATE_GROUP), CapabilityStatus.UNSUPPORTED);
    }

    @Test
    public void testAllowsEmpty() {
        assertTrue(groupsManager.getSettings().allowEmpty());
    }

    @Test(expected = RuntimeException.class)
    public void testSearchPageZero() {
        AbstractEntityManager.SearchRequest request = buildSearchRequestMock("", 0, 5);
        AbstractEntityManager.SearchResponse<Group> response = groupsManager.search(request);
    }
    
    @Test
    public void testSearchAll() {
        AbstractEntityManager.SearchRequest request = buildSearchRequestMock("", 1, 5);
        AbstractEntityManager.SearchResponse<Group> response = groupsManager.search(request);
        assertNotNull(response);
        List<Group> groups = response.getResults();
        int total = response.getTotal();
        boolean hasNextPage = response.hasNextPage();
        assertEquals(total, 4);
        assertTrue(!hasNextPage);
        assertEquals(groups.size(), 4);
        List<Group> expectedGroups = createGroupList(UserSystemManager.ADMIN, "role3", "role2", "role1");
        assertEquals(new HashSet<Group>(expectedGroups), new HashSet<Group>(groups));
    }

    @Test
    public void testGet() {
        assertGet(UserSystemManager.ADMIN);
        assertGet("role1");
        assertGet("role2");
        assertGet("role3");
    }

    @Test
    public void testCreateGroup() {
        Group group = mock(Group.class);
        when(group.getName()).thenReturn("role10");
        groupsManager.create(group);
        Group created = groupsManager.get("role10");
        assertNotNull(created);
        assertEquals("role10", created.getName());
    }
    
    @Test(expected = UnsupportedServiceCapabilityException.class)
    public void testUpdateGroup() {
        Group group = mock(Group.class);
        when(group.getName()).thenReturn("role10");
        groupsManager.update(group);
    }

    @Test(expected = GroupNotFoundException.class)
    public void testDeleteGroup() {
        groupsManager.delete("role3");
        groupsManager.get("role3");
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
        Group group = groupsManager.get(name);
        assertNotNull(group);
        assertEquals(group.getName(), name);
    }

}
