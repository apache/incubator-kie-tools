/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.screens.impl;

import java.util.Arrays;
import java.util.Collection;

import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.organizationalunit.OrganizationalUnitService;
import org.guvnor.structure.organizationalunit.impl.OrganizationalUnitImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.library.api.SpacesScreenService;
import org.kie.workbench.common.screens.library.api.preferences.LibraryInternalPreferencesPortableGeneratedImpl;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.preferences.shared.bean.PreferenceBeanServerStore;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

@RunWith(MockitoJUnitRunner.class)
public class SpacesScreenServiceImplTest {

    @Mock
    private OrganizationalUnitService organizationalUnitService;

    @Mock
    private PreferenceBeanServerStore preferenceBeanServerStore;

    private SpacesScreenServiceImpl spacesScreenService;

    @Before
    public void before() {
        organizationalUnitService = mock(OrganizationalUnitService.class);
        preferenceBeanServerStore = mock(PreferenceBeanServerStore.class);
        spacesScreenService = spy(new SpacesScreenServiceImpl(organizationalUnitService,
                                                              preferenceBeanServerStore));
    }

    @Test
    public void testGetSpaces() {
        final Collection<OrganizationalUnit> spaces = Arrays.asList(new OrganizationalUnitImpl("foo", "org.foo"), new OrganizationalUnitImpl("bar", "org.bar"));
        doReturn(spaces).when(organizationalUnitService).getOrganizationalUnits();
        assertEquals(spaces, spacesScreenService.getSpaces());
    }

    @Test
    public void testGetSpace() {
        final OrganizationalUnitImpl space = new OrganizationalUnitImpl("test", "org.test");
        doReturn(space).when(organizationalUnitService).getOrganizationalUnit("test");
        assertEquals(space, spacesScreenService.getSpace("test"));
    }

    @Test
    public void testSavePreference() {
        final LibraryInternalPreferencesPortableGeneratedImpl newPreference = new LibraryInternalPreferencesPortableGeneratedImpl();
        doNothing().when(preferenceBeanServerStore).save(newPreference);
        assertEquals(200, spacesScreenService.savePreference(newPreference).getStatus());
    }

    @Test
    public void testSaveSpace() {

        final SpacesScreenService.NewSpace space = new SpacesScreenService.NewSpace() {{
            name = "test";
            groupId = "com.test";
        }};

        doReturn(new OrganizationalUnitImpl("test", "com.test")).when(organizationalUnitService).createOrganizationalUnit("test", "com.test");
        assertEquals(201, spacesScreenService.postSpace(space).getStatus());
    }

    @Test
    public void testIsValidGroupId() {
        doReturn(true).when(organizationalUnitService).isValidGroupId("org.foo");
        assertTrue(spacesScreenService.isValidGroupId("org.foo"));
    }

    @Test
    public void testIsValidGroupIdFalse() {
        doReturn(false).when(organizationalUnitService).isValidGroupId("org.foo");
        assertFalse(spacesScreenService.isValidGroupId("org.foo"));
    }
}