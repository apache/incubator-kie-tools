/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.guvnor.ala.ui.backend.service;

import java.util.Collection;

import org.guvnor.ala.ui.model.Provider;
import org.guvnor.ala.ui.model.ProviderKey;
import org.guvnor.ala.ui.model.ProviderType;
import org.guvnor.ala.ui.model.ProviderTypeKey;
import org.guvnor.ala.ui.model.ProvidersInfo;
import org.guvnor.ala.ui.model.RuntimeListItem;
import org.guvnor.ala.ui.model.RuntimesInfo;
import org.guvnor.ala.ui.service.ProviderService;
import org.guvnor.ala.ui.service.ProviderTypeService;
import org.guvnor.ala.ui.service.RuntimeService;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ProvisioningScreensServiceImplTest {

    @Mock
    private ProviderTypeService providerTypeService;

    @Mock
    private ProviderService providerService;

    @Mock
    private RuntimeService runtimeService;

    private ProvisioningScreensServiceImpl service;

    @Mock
    private ProviderTypeKey providerTypeKey;

    @Mock
    private ProviderType providerType;

    @Mock
    private ProviderKey providerKey;

    @Mock
    private Provider provider;

    @Mock
    private Collection<ProviderKey> providerKeys;

    @Mock
    private Collection<RuntimeListItem> runtimeListItems;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setUp() {
        service = new ProvisioningScreensServiceImpl(providerTypeService,
                                                     providerService,
                                                     runtimeService);
    }

    @Test
    public void testGetProvidersInfoProviderTypeExisting() {
        //the provider type exists.
        when(providerTypeService.getProviderType(providerTypeKey)).thenReturn(providerType);
        when(providerService.getProvidersKey(providerType)).thenReturn(providerKeys);

        ProvidersInfo providersInfo = service.getProvidersInfo(providerTypeKey);

        assertNotNull(providersInfo);
        assertEquals(providerType,
                     providersInfo.getProviderType());
        assertEquals(providerKeys,
                     providersInfo.getProvidersKey());

        verify(providerTypeService,
               times(1)).getProviderType(providerTypeKey);
        verify(providerService,
               times(1)).getProvidersKey(providerType);
    }

    @Test
    public void testGetProvidersInfoProviderTypeNotExisting() {
        //the provider type not exists.
        when(providerTypeService.getProviderType(providerTypeKey)).thenReturn(null);

        ProvidersInfo providersInfo = service.getProvidersInfo(providerTypeKey);

        assertNull(providersInfo);

        verify(providerTypeService,
               times(1)).getProviderType(providerTypeKey);
        verify(providerService,
               never()).getProvidersKey(providerType);
    }

    @Test
    public void testGetRuntimesInfoProviderExisting() {
        //the provider exists.
        when(providerService.getProvider(providerKey)).thenReturn(provider);
        when(runtimeService.getRuntimeItems(providerKey)).thenReturn(runtimeListItems);

        RuntimesInfo info = service.getRuntimesInfo(providerKey);

        assertNotNull(info);
        assertEquals(provider,
                     info.getProvider());
        assertEquals(runtimeListItems,
                     info.getRuntimeItems());

        verify(providerService,
               times(1)).getProvider(providerKey);
        verify(runtimeService,
               times(1)).getRuntimeItems(providerKey);
    }

    @Test
    public void testGetRuntimesInfoProviderNotExisting() {
        //the provider not exists.
        when(providerService.getProvider(providerKey)).thenReturn(null);
        RuntimesInfo info = service.getRuntimesInfo(providerKey);
        assertNull(info);
        verify(providerService,
               times(1)).getProvider(providerKey);
        verify(runtimeService,
               never()).getRuntimeItems(providerKey);
    }

    @Test
    public void testHasRuntimesTrue() {
        Collection<RuntimeListItem> runtimes = mock(Collection.class);
        when(runtimeService.getRuntimeItems(providerKey)).thenReturn(runtimes);

        when(runtimes.isEmpty()).thenReturn(false);
        assertTrue(service.hasRuntimes(providerKey));
        verify(runtimeService,
               times(1)).getRuntimeItems(providerKey);
    }

    @Test
    public void testHasRuntimesFalse() {
        Collection<RuntimeListItem> runtimes = mock(Collection.class);
        when(runtimeService.getRuntimeItems(providerKey)).thenReturn(runtimes);

        when(runtimes.isEmpty()).thenReturn(true);
        assertFalse(service.hasRuntimes(providerKey));
        verify(runtimeService,
               times(1)).getRuntimeItems(providerKey);
    }
}
