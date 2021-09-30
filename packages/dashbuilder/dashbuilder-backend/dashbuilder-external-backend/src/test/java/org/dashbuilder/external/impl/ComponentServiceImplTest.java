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

package org.dashbuilder.external.impl;

import java.util.Collections;
import java.util.List;

import org.dashbuilder.external.model.ExternalComponent;
import org.dashbuilder.external.service.ComponentLoader;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ComponentServiceImplTest {

    private static final String C1_ID = "c1";
    private static final String C2_ID = "c2";

    @Mock
    ComponentLoader loader;

    @InjectMocks
    ComponentServiceImpl externalComponentServiceImpl;

    @Test
    public void testById() {
        ExternalComponent c1 = new ExternalComponent(C1_ID, "c1 name", "c1 icon", false, Collections.emptyList());
        ExternalComponent c2 = new ExternalComponent(C2_ID, "c2 name", "c2 icon", false, Collections.emptyList());

        Mockito.when(loader.loadExternal()).thenReturn(asList(c1, c2));

        assertTrue(externalComponentServiceImpl.byId(C1_ID).isPresent());
        assertTrue(externalComponentServiceImpl.byId(C2_ID).isPresent());
        assertFalse(externalComponentServiceImpl.byId("do not exist").isPresent());
    }

    @Test
    public void testByIdProvidedPriority() {
        String c1ProvidedName = "c1 provided";
        ExternalComponent c1_provided = new ExternalComponent(C1_ID, c1ProvidedName, "c1 icon", false, Collections.emptyList());
        ExternalComponent c1_external = new ExternalComponent(C1_ID, "c1 external", "c1 icon", false, Collections.emptyList());

        when(loader.loadProvided()).thenReturn(asList(c1_provided));
        when(loader.loadExternal()).thenReturn(asList(c1_external));

        assertEquals(c1ProvidedName, externalComponentServiceImpl.byId(C1_ID).get().getName());
    }

    @Test
    public void testListAllComponents() {
        String providedId = "c1";
        String externalId = "c2";
        ExternalComponent c1_provided = new ExternalComponent(providedId, "name", "icon", false, Collections.emptyList());
        ExternalComponent c1_external = new ExternalComponent(externalId, "name", "icon", false, Collections.emptyList());

        when(loader.loadProvided()).thenReturn(asList(c1_provided));
        when(loader.loadExternal()).thenReturn(asList(c1_external));

        List<ExternalComponent> comps = externalComponentServiceImpl.listAllComponents();
        ExternalComponent cp = comps.stream().filter(c -> providedId.equals(c.getId())).findAny().get();
        ExternalComponent ce = comps.stream().filter(c -> externalId.equals(c.getId())).findAny().get();
        assertTrue(cp.isProvided());
        assertFalse(ce.isProvided());
    }

}