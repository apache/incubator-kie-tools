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

package org.uberfire.ext.security.management.search;

import org.jboss.errai.security.shared.api.Group;
import org.junit.Test;
import org.uberfire.ext.security.management.api.AbstractEntityManager;
import org.uberfire.ext.security.management.impl.SearchRequestImpl;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GroupsRuntimeSearchEngineTest {

    GroupsRuntimeSearchEngine searchEngine = new GroupsRuntimeSearchEngine();

    @Test(expected = RuntimeException.class)
    public void testSearchPageZero() {
        List<Group> groups = new ArrayList<Group>(1);
        // First page cannot be 0.
        AbstractEntityManager.SearchRequest request = new SearchRequestImpl("", 0, 5);
        AbstractEntityManager.SearchResponse<Group> response = searchEngine.search(groups, request);
    }
    
    @Test
    public void testSearchAll() {
        List<Group> groups = createTestGroups("group", 20);

        // First page.
        AbstractEntityManager.SearchRequest request = new SearchRequestImpl("", 1, 5);
        AbstractEntityManager.SearchResponse<Group> response = searchEngine.search(groups, request);
        assertNotNull(response);
        int total = response.getTotal();
        assertEquals(total, 20);
        boolean hasNextPage = response.hasNextPage();
        assertEquals(hasNextPage, true);
        List<Group> results = response.getResults();
        assertEquals(results.size(), 5);
        Group g = results.get(0);
        assertEquals("group0", g.getName());
        Group g4 = results.get(4);
        assertEquals("group4", g4.getName());

        // Last page.
        request = new SearchRequestImpl("", 4, 5);
        response = searchEngine.search(groups, request);
        assertNotNull(response);
        total = response.getTotal();
        assertEquals(total, 20);
        hasNextPage = response.hasNextPage();
        assertEquals(hasNextPage, false);
        results = response.getResults();
        assertEquals(results.size(), 5);
        Group g15 = results.get(0);
        assertEquals("group15", g15.getName());
        Group g19 = results.get(4);
        assertEquals("group19", g19.getName());
    }
    
    @Test
    public void testSearchSingle() {
        List<Group> groups = createTestGroups("group", 20);
        AbstractEntityManager.SearchRequest request = new SearchRequestImpl("group18", 1, 5);
        AbstractEntityManager.SearchResponse<Group> response = searchEngine.search(groups, request);
        assertNotNull(response);
        int total = response.getTotal();
        assertEquals(total, 1);
        boolean hasNextPage = response.hasNextPage();
        assertEquals(hasNextPage, false);
        List<Group> results = response.getResults();
        assertEquals(results.size(), 1);
        Group g = results.get(0);
        assertEquals("group18", g.getName());
    }

    @Test
    public void testSearchMultiple() {
        List<Group> groups = createTestGroups("group", 20);
        // First page.
        AbstractEntityManager.SearchRequest request = new SearchRequestImpl("group1", 1, 5);
        AbstractEntityManager.SearchResponse<Group> response = searchEngine.search(groups, request);
        assertNotNull(response);
        int total = response.getTotal();
        assertEquals(total, 11);
        boolean hasNextPage = response.hasNextPage();
        assertEquals(hasNextPage, true);
        List<Group> results = response.getResults();
        assertEquals(results.size(), 5);
        Group g0 = results.get(0);
        assertEquals("group1", g0.getName());
        Group g10 = results.get(1);
        assertEquals("group10", g10.getName());
        Group g13 = results.get(4);
        assertEquals("group13", g13.getName());

        // Second page.
        request = new SearchRequestImpl("group1", 2, 5);
        response = searchEngine.search(groups, request);
        assertNotNull(response);
        total = response.getTotal();
        assertEquals(total, 11);
        hasNextPage = response.hasNextPage();
        assertEquals(hasNextPage, true);
        results = response.getResults();
        assertEquals(results.size(), 5);
        Group g14 = results.get(0);
        assertEquals("group14", g14.getName());
        Group g15 = results.get(1);
        assertEquals("group15", g15.getName());
        Group g18 = results.get(4);
        assertEquals("group18", g18.getName());

        // Third page.
        request = new SearchRequestImpl("group1", 3, 5);
        response = searchEngine.search(groups, request);
        assertNotNull(response);
        total = response.getTotal();
        assertEquals(total, 11);
        hasNextPage = response.hasNextPage();
        assertEquals(hasNextPage, false);
        results = response.getResults();
        assertEquals(results.size(), 1);
        Group g19 = results.get(0);
        assertEquals("group19", g19.getName());
        
    }
    
    private List<Group> createTestGroups(String namePrefix, int size) {
        List<Group> groups = new LinkedList<Group>();
        for (int x = 0; x < size; x++) {
            Group group = mock(Group.class);
            when(group.getName()).thenReturn(namePrefix + x);
            groups.add(group);
        }
        return groups;
    }
    
}
