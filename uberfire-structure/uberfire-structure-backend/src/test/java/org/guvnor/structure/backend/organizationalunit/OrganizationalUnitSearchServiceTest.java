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

package org.guvnor.structure.backend.organizationalunit;

import java.util.Arrays;
import java.util.Collection;

import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.organizationalunit.OrganizationalUnitService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class OrganizationalUnitSearchServiceTest {

    @Mock
    OrganizationalUnitService resourceService;

    @Mock
    OrganizationalUnit itemA;

    @Mock
    OrganizationalUnit itemB;

    private OrganizationalUnitSearchServiceImpl searchService;

    @Before
    public void setUp() throws Exception {
        when(itemA.getIdentifier()).thenReturn("itemA");
        when(itemB.getIdentifier()).thenReturn("itemB");
        when(itemA.getName()).thenReturn("Item A");
        when(itemB.getName()).thenReturn("Item B");
        when(resourceService.getAllOrganizationalUnits()).thenReturn(Arrays.asList(itemA,
                                                                                   itemB));
        searchService = new OrganizationalUnitSearchServiceImpl(resourceService);
    }

    @Test
    public void testSearchById() throws Exception {
        Collection<OrganizationalUnit> result = searchService.searchById(Arrays.asList("itemA"));
        assertEquals(result.size(),
                     1);
        assertEquals(result.iterator().next().getName(),
                     "Item A");
    }

    @Test
    public void testSearchByAlias() throws Exception {
        Collection<OrganizationalUnit> result = searchService.searchByName("Item",
                                                                           10,
                                                                           true);
        assertEquals(result.size(),
                     2);
    }

    @Test
    public void testSearchCaseSensitiveEmpty() throws Exception {
        Collection<OrganizationalUnit> result = searchService.searchByName("item",
                                                                           10,
                                                                           true);
        assertEquals(result.size(),
                     0);
    }

    @Test
    public void testSearchCaseUnsensitive() throws Exception {
        Collection<OrganizationalUnit> result = searchService.searchByName("item",
                                                                           10,
                                                                           false);
        assertEquals(result.size(),
                     2);
    }

    @Test
    public void testSearchMaxItems() throws Exception {
        Collection<OrganizationalUnit> result = searchService.searchByName("item",
                                                                           1,
                                                                           false);
        assertEquals(result.size(),
                     1);
    }
}