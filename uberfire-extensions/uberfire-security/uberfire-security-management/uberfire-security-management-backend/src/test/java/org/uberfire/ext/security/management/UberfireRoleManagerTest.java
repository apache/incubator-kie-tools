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

package org.uberfire.ext.security.management;

import java.util.List;

import org.jboss.errai.security.shared.api.Group;
import org.jboss.errai.security.shared.api.Role;
import org.jboss.errai.security.shared.api.RoleImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.uberfire.backend.server.security.RoleRegistry;
import org.uberfire.ext.security.management.api.AbstractEntityManager;
import org.uberfire.ext.security.management.api.GroupManager;
import org.uberfire.ext.security.management.api.UserSystemManager;
import org.uberfire.ext.security.management.api.exception.GroupNotFoundException;
import org.uberfire.ext.security.management.impl.SearchRequestImpl;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class UberfireRoleManagerTest {

    @Mock
    UserSystemManager userSystemManager;
    private UberfireRoleManager tested;

    public static Role mockRole(String name) {
        Role role = mock(Role.class);
        when(role.getName()).thenReturn(name);
        return role;
    }

    @Before
    public void setup() throws Exception {
        RoleRegistry.get().clear();
        RoleRegistry.get().registerRole("regRole1");
        RoleRegistry.get().registerRole("regRole2");
        RoleRegistry.get().registerRole("regRole3");
        tested = new UberfireRoleManager();
        tested.initialize(userSystemManager);
    }

    @Test
    public void testSearch() {
        GroupManager groupManager = mock(GroupManager.class);
        doAnswer(new Answer<Group>() {
            @Override
            public Group answer(InvocationOnMock invocationOnMock) throws Throwable {
                String name = (String) invocationOnMock.getArguments()[0];
                Group g = mock(Group.class);
                when(g.getName()).thenReturn(name);
                return g;
            }
        }).when(groupManager).get(anyString());
        when(userSystemManager.groups()).thenReturn(groupManager);
        AbstractEntityManager.SearchResponse<Role> response = tested.search(new SearchRequestImpl("",
                                                                                                  1,
                                                                                                  10));
        assertNotNull(response);
        int total = response.getTotal();
        assertEquals(total,
                     3);
        boolean hasNextPage = response.hasNextPage();
        assertEquals(hasNextPage,
                     false);
        List<Role> results = response.getResults();
        assertEquals(results.size(),
                     3);
    }

    @Test
    public void testSearchRoleNotExists() {
        GroupManager groupManager = mock(GroupManager.class);
        doAnswer(new Answer<Group>() {
            @Override
            public Group answer(InvocationOnMock invocationOnMock) throws Throwable {
                String name = (String) invocationOnMock.getArguments()[0];
                Group g = mock(Group.class);
                when(g.getName()).thenReturn(name);
                return g;
            }
        }).when(groupManager).get(anyString());
        doThrow(new GroupNotFoundException("admin")).when(groupManager).get("admin");
        when(userSystemManager.groups()).thenReturn(groupManager);
        AbstractEntityManager.SearchResponse<Role> response = tested.search(new SearchRequestImpl("",
                                                                                                  1,
                                                                                                  10));
        assertNotNull(response);
        int total = response.getTotal();
        assertEquals(total,
                     3);
        boolean hasNextPage = response.hasNextPage();
        assertEquals(hasNextPage,
                     false);
        List<Role> results = response.getResults();
        assertEquals(results.size(),
                     3);
    }

    @Test
    public void testGetAllRoles() {
        List<Role> results = tested.getAll();
        assertEquals(results.size(),
                     3);
    }

    public void testGet() {
        Role role = tested.get("regRole1");
        assertEquals(role,
                     new RoleImpl("regRole1"));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testCreate() {
        tested.create(mockRole("regRole1"));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testUpdate() {
        tested.update(mockRole("regRole1"));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testDelete() {
        tested.delete("regRole1");
    }
}
