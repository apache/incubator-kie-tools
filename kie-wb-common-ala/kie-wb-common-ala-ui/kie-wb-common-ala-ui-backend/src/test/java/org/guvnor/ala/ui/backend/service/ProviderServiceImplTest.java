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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.guvnor.ala.config.ProviderConfig;
import org.guvnor.ala.services.api.backend.RuntimeProvisioningServiceBackend;
import org.guvnor.ala.ui.backend.service.converter.ProviderConfigConverter;
import org.guvnor.ala.ui.backend.service.converter.ProviderConverter;
import org.guvnor.ala.ui.backend.service.converter.ProviderConverterFactory;
import org.guvnor.ala.ui.model.Provider;
import org.guvnor.ala.ui.model.ProviderConfiguration;
import org.guvnor.ala.ui.model.ProviderKey;
import org.guvnor.ala.ui.model.ProviderType;
import org.guvnor.ala.ui.model.ProviderTypeKey;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.guvnor.ala.AlaSPITestCommons.mockProviderListSPI;
import static org.guvnor.ala.AlaSPITestCommons.mockProviderTypeSPI;
import static org.guvnor.ala.ui.ProvisioningManagementTestCommons.PROVIDER_ID;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ProviderServiceImplTest {

    private static final int PROVIDER_COUNT = 5;

    @Mock
    private RuntimeProvisioningServiceBackend runtimeProvisioningService;

    @Mock
    private ProviderConverterFactory providerConverterFactory;

    @Mock
    private ProviderConverter providerConverter;

    @Mock
    private ProviderConfigConverter providerConfigConverter;

    private ProviderServiceImpl service;

    private List<org.guvnor.ala.runtime.providers.Provider> providersSpi;

    private org.guvnor.ala.runtime.providers.ProviderType providerTypeSpi;

    private List<Provider> providers;

    private List<ProviderKey> providerKeys;

    private ProviderTypeKey providerTypeKey;

    private ProviderType providerType;

    private ProviderConfiguration providerConfiguration;

    private ProviderConfig providerConfig;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setUp() {

        providerTypeSpi = mockProviderTypeSPI("0");
        providersSpi = mockProviderListSPI(providerTypeSpi,
                                           PROVIDER_COUNT);

        when(runtimeProvisioningService.getProviders(anyInt(),
                                                     anyInt(),
                                                     anyString(),
                                                     anyBoolean())).thenReturn(providersSpi);

        providerTypeKey = new ProviderTypeKey(providerTypeSpi.getProviderTypeName(),
                                              providerTypeSpi.getVersion());

        providerType = new ProviderType(providerTypeKey,
                                        null);

        when(providerConverterFactory.getProviderConverter()).thenReturn(providerConverter);
        when(providerConverterFactory.getProviderConfigConverter(providerTypeKey)).thenReturn(providerConfigConverter);

        providers = new ArrayList<>();
        providerKeys = new ArrayList<>();
        for (int i = 0; i < PROVIDER_COUNT; i++) {
            Provider provider = mock(Provider.class);
            ProviderTypeKey providerTypeKey = new ProviderTypeKey(providersSpi.get(i).getProviderType().getProviderTypeName(),
                                                                  providersSpi.get(i).getProviderType().getVersion());
            ProviderKey providerKey = new ProviderKey(providerTypeKey,
                                                      providersSpi.get(i).getId());

            when(provider.getKey()).thenReturn(providerKey);
            providers.add(provider);
            providerKeys.add(providerKey);
            when(providerConverter.toModel(providersSpi.get(i))).thenReturn(provider);
        }

        service = new ProviderServiceImpl(runtimeProvisioningService,
                                          providerConverterFactory);
    }

    @Test
    public void testGetProviders() {
        Collection<Provider> result = service.getProviders(providerType);
        assertEquals(providers,
                     result);
    }

    @Test
    public void testGetProvidersKey() {
        Collection<ProviderKey> result = service.getProvidersKey(providerType);
        assertEquals(providerKeys,
                     result);
    }

    @Test
    public void testCreateProvider() {

        prepareConfigurationForCreate();
        //PROVIDER_ID don't exists by construction.
        when(providerConfiguration.getId()).thenReturn(PROVIDER_ID);

        service.createProvider(providerType,
                               providerConfiguration);

        verify(runtimeProvisioningService,
               times(1)).registerProvider(providerConfig);
    }

    @Test
    public void testCreateProviderExisting() {

        prepareConfigurationForCreate();

        //emulate that one of the existing providers has the same id by picking an arbitrary existing id.
        String existingId = providers.get(1).getKey().getId();
        when(providerConfiguration.getId()).thenReturn(existingId);

        expectedException.expectMessage("A provider with the given name already exists: " + existingId);
        service.createProvider(providerType,
                               providerConfiguration);

        verify(runtimeProvisioningService,
               never()).registerProvider(any(ProviderConfig.class));
    }

    @Test
    public void testDeleteProvider() {
        ProviderKey providerKey = mock(ProviderKey.class);
        when(providerKey.getId()).thenReturn(PROVIDER_ID);

        service.deleteProvider(providerKey);

        verify(runtimeProvisioningService,
               times(1)).unregisterProvider(PROVIDER_ID);
    }

    @Test
    public void testGetProviderExisting() {

        //pick one of the existing providers.
        org.guvnor.ala.runtime.providers.Provider providerSpi = providersSpi.get(2);
        //create a key for finding it.
        ProviderTypeKey providerTypeKey = new ProviderTypeKey(providerSpi.getProviderType().getProviderTypeName(),
                                                              providerSpi.getProviderType().getVersion());
        ProviderKey providerKey = new ProviderKey(providerTypeKey,
                                                  providerSpi.getId());

        Provider provider = service.getProvider(providerKey);
        assertNotNull(provider);
        assertEquals(providers.get(2).getKey(),
                     provider.getKey());
    }

    @Test
    public void testGetProviderNotExisting() {
        //create an arbitrary not existing key.
        ProviderTypeKey providerTypeKey = new ProviderTypeKey("not exist",
                                                              "not exist");
        ProviderKey providerKey = new ProviderKey(providerTypeKey,
                                                  "not exist");

        Provider provider = service.getProvider(providerKey);
        assertNull(provider);
    }

    private void prepareConfigurationForCreate() {
        providerConfiguration = mock(ProviderConfiguration.class);
        Map values = mock(Map.class);
        when(values.size()).thenReturn(5);
        when(providerConfiguration.getValues()).thenReturn(values);

        providerConfig = mock(ProviderConfig.class);

        when(providerConfigConverter.toDomain(providerConfiguration)).thenReturn(providerConfig);
    }
}
