/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.guvnor.structure.backend.repositories;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.guvnor.structure.organizationalunit.OrganizationalUnitService;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.repositories.RepositoryService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.spaces.Space;

@RunWith(MockitoJUnitRunner.class)
public class RepositorySearchServiceTest {

    @Mock
    RepositoryService resourceService;

    @Mock
    Repository itemA;

    @Mock
    Repository itemB;

    @Mock
    OrganizationalUnitService orgUnitService;

    Space space;

    private RepositorySearchServiceImpl searchService;

    @Before
    public void setUp() throws Exception {
        space = new Space("test-realm");
        when(orgUnitService.getAllUserSpaces()).thenReturn(Collections.singletonList(space));
        when(itemA.getIdentifier()).thenReturn("itemA");
        when(itemB.getIdentifier()).thenReturn("itemB");
        when(itemA.getAlias()).thenReturn("Item A");
        when(itemB.getAlias()).thenReturn("Item B");
        when(resourceService.getAllRepositories(space)).thenReturn(Arrays.asList(itemA,
                                                                            itemB));
        searchService = new RepositorySearchServiceImpl(resourceService, orgUnitService);
    }

    @Test
    public void testSearchById() throws Exception {
        Collection<Repository> result = searchService.searchById(Arrays.asList("itemA"));
        assertEquals(result.size(),
                     1);
        assertEquals(result.iterator().next().getAlias(),
                     "Item A");
    }

    @Test
    public void testSearchByAlias() throws Exception {
        Collection<Repository> result = searchService.searchByAlias("Item",
                                                                    10,
                                                                    true);
        assertEquals(result.size(),
                     2);
    }

    @Test
    public void testSearchCaseSensitiveEmpty() throws Exception {
        Collection<Repository> result = searchService.searchByAlias("item",
                                                                    10,
                                                                    true);
        assertEquals(result.size(),
                     0);
    }

    @Test
    public void testSearchCaseUnsensitive() throws Exception {
        Collection<Repository> result = searchService.searchByAlias("item",
                                                                    10,
                                                                    false);
        assertEquals(result.size(),
                     2);
    }

    @Test
    public void testSearchMaxItems() throws Exception {
        Collection<Repository> result = searchService.searchByAlias("item",
                                                                    1,
                                                                    false);
        assertEquals(result.size(),
                     1);
    }
}