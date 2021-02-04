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

import org.jboss.errai.security.shared.api.Role;
import org.jboss.errai.security.shared.api.RoleImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.uberfire.backend.server.security.RoleRegistry;
import org.uberfire.ext.security.management.api.AbstractEntityManager;
import org.uberfire.ext.security.management.api.UserSystemManager;
import org.uberfire.ext.security.management.impl.SearchRequestImpl;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class UberfireRoleManagerTest {

    @Mock
    UserSystemManager userSystemManager;
    private UberfireRoleManager tested;

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
        tested.create(mock(Role.class));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testUpdate() {
        tested.update(mock(Role.class));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testDelete() {
        tested.delete("regRole1");
    }
}
