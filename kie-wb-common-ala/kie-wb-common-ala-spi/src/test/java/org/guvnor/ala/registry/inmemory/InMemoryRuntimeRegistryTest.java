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

package org.guvnor.ala.registry.inmemory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.guvnor.ala.config.ProviderConfig;
import org.guvnor.ala.registry.RuntimeRegistry;
import org.guvnor.ala.runtime.Runtime;
import org.guvnor.ala.runtime.RuntimeId;
import org.guvnor.ala.runtime.providers.Provider;
import org.guvnor.ala.runtime.providers.ProviderId;
import org.guvnor.ala.runtime.providers.ProviderType;
import org.junit.Before;
import org.junit.Test;

import static org.guvnor.ala.AlaSPITestCommons.mockProviderListSPI;
import static org.guvnor.ala.AlaSPITestCommons.mockProviderSPI;
import static org.guvnor.ala.AlaSPITestCommons.mockProviderTypeListSPI;
import static org.guvnor.ala.AlaSPITestCommons.mockProviderTypeSPI;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class InMemoryRuntimeRegistryTest {

    protected static final int ELEMENTS_COUNT = 10;

    protected static final String PROVIDER_ID = "PROVIDER_ID";

    protected InMemoryRuntimeRegistry runtimeRegistry;

    protected ProviderType providerType;

    protected Provider provider;

    protected ProviderId providerId;

    protected Runtime runtime;

    @Before
    public void setUp() {
        providerType = mockProviderTypeSPI("");
        provider = mockProviderSPI(providerType,
                                   "");
        providerId = mock(ProviderId.class);
        when(providerId.getProviderType()).thenReturn(providerType);
        when(providerId.getId()).thenReturn(PROVIDER_ID);

        runtime = mockRuntime(providerId,
                              "");

        runtimeRegistry = new InMemoryRuntimeRegistry();
    }

    @Test
    public void testRegisterProviderType() {
        runtimeRegistry.registerProviderType(providerType);
        verifyProviderTypeIsRegistered(providerType);
    }

    @Test
    public void testDeregisterProviderType() {
        runtimeRegistry.registerProviderType(providerType);
        verifyProviderTypeIsRegistered(providerType);
        runtimeRegistry.deregisterProviderType(providerType);
        verifyProviderTypeIsNotRegistered(providerType);
    }

    @Test
    public void testGetProviderTypes() {
        List<ProviderType> result = runtimeRegistry.getProviderTypes(0,
                                                                     1000,
                                                                     RuntimeRegistry.PROVIDER_TYPE_NAME_SORT,
                                                                     true);
        assertTrue(result.isEmpty());

        List<ProviderType> providerTypes = mockProviderTypeListSPI(ELEMENTS_COUNT);
        providerTypes.forEach(providerType -> runtimeRegistry.registerProviderType(providerType));

        result = runtimeRegistry.getProviderTypes(0,
                                                  1000,
                                                  RuntimeRegistry.PROVIDER_TYPE_NAME_SORT,
                                                  true);

        for (ProviderType providerType : providerTypes) {
            assertTrue(result.contains(providerType));
        }
    }

    private void verifyProviderTypeIsRegistered(ProviderType providerType) {
        List<ProviderType> result = runtimeRegistry.getProviderTypes(0,
                                                                     1000,
                                                                     RuntimeRegistry.PROVIDER_TYPE_NAME_SORT,
                                                                     true);
        assertTrue(result.contains(providerType));
    }

    private void verifyProviderTypeIsNotRegistered(ProviderType providerType) {
        List<ProviderType> result = runtimeRegistry.getProviderTypes(0,
                                                                     1000,
                                                                     RuntimeRegistry.PROVIDER_TYPE_NAME_SORT,
                                                                     true);
        assertFalse(result.contains(providerType));
    }

    @Test
    public void testRegisterProvider() {
        runtimeRegistry.registerProvider(provider);
        verifyProviderIsRegistered(provider);
    }

    @Test
    public void testDeregisterProvider() {
        runtimeRegistry.registerProvider(provider);
        verifyProviderIsRegistered(provider);
        runtimeRegistry.deregisterProvider(provider);
        verifyProviderIsNotRegistered(provider);
    }

    @Test
    public void testDeregisterProviderById() {
        runtimeRegistry.registerProvider(provider);
        verifyProviderIsRegistered(provider);
        runtimeRegistry.deregisterProvider(provider.getId());
        verifyProviderIsNotRegistered(provider);
    }

    @Test
    public void testGetProviders() {
        List<Provider> result = runtimeRegistry.getProviders(0,
                                                             1000,
                                                             RuntimeRegistry.PROVIDER_ID_SORT,
                                                             true);
        assertTrue(result.isEmpty());

        List<Provider> providers = mockProviderListSPI(providerType,
                                                       ELEMENTS_COUNT);
        providers.forEach(provider -> runtimeRegistry.registerProvider(provider));

        result = runtimeRegistry.getProviders(0,
                                              1000,
                                              RuntimeRegistry.PROVIDER_ID_SORT,
                                              true);

        for (Provider provider : providers) {
            assertTrue(result.contains(provider));
        }
    }

    @Test
    public void testGetProvider() {
        Provider result = runtimeRegistry.getProvider(provider.getId());
        assertNull(result);

        runtimeRegistry.registerProvider(provider);
        result = runtimeRegistry.getProvider(provider.getId());
        assertEquals(provider,
                     result);
    }

    @Test
    public void testGetProvidersByType() {
        ProviderType providerType1 = mockProviderTypeSPI("ProviderType1");
        ProviderType providerType2 = mockProviderTypeSPI("ProviderType2");
        List<Provider> providers = mockProviderListSPI(providerType,
                                                       "1",
                                                       ELEMENTS_COUNT);
        List<Provider> providersForType1 = mockProviderListSPI(providerType1,
                                                               "2",
                                                               ELEMENTS_COUNT);
        List<Provider> providersForType2 = mockProviderListSPI(providerType2,
                                                               ELEMENTS_COUNT);

        List<Provider> allProviders = new ArrayList<>();
        allProviders.addAll(providers);
        allProviders.addAll(providersForType1);
        allProviders.addAll(providersForType2);

        allProviders.forEach(provider -> runtimeRegistry.registerProvider(provider));

        List<Provider> result = runtimeRegistry.getProvidersByType(providerType);
        assertEquals(providers.size(),
                     result.size());
        for (Provider provider : providers) {
            assertTrue(result.contains(provider));
        }
    }

    protected void verifyProviderIsRegistered(Provider provider) {
        List<Provider> result = runtimeRegistry.getProviders(0,
                                                             1000,
                                                             RuntimeRegistry.PROVIDER_ID_SORT,
                                                             true);
        assertTrue(result.contains(provider));
    }

    protected void verifyProviderIsNotRegistered(Provider provider) {
        List<Provider> result = runtimeRegistry.getProviders(0,
                                                             1000,
                                                             RuntimeRegistry.PROVIDER_ID_SORT,
                                                             true);
        assertFalse(result.contains(provider));
    }

    @Test
    public void testRegisterRuntime() {
        runtimeRegistry.registerRuntime(runtime);
        verifyRuntimeIsRegistered(runtime);
    }

    @Test
    public void testGetRuntimes() {
        List<Runtime> result = runtimeRegistry.getRuntimes(0,
                                                           1000,
                                                           RuntimeRegistry.RUNTIME_ID_SORT,
                                                           true);
        assertTrue(result.isEmpty());

        List<Runtime> runtimes = mockRuntimeList(providerId,
                                                 "",
                                                 ELEMENTS_COUNT);

        runtimes.forEach(runtime -> runtimeRegistry.registerRuntime(runtime));

        result = runtimeRegistry.getRuntimes(0,
                                             1000,
                                             RuntimeRegistry.RUNTIME_ID_SORT,
                                             true);

        for (Runtime runtime : runtimes) {
            assertTrue(result.contains(runtime));
        }
    }

    @Test
    public void testGetRuntimeById() {
        String id = runtime.getId();
        runtimeRegistry.registerRuntime(runtime);
        Runtime result = runtimeRegistry.getRuntimeById(id);
        assertEquals(runtime,
                     result);
    }

    @Test
    public void testDeregisterRuntime() {
        String id = runtime.getId();
        RuntimeId runtimeId = mock(RuntimeId.class);
        when(runtimeId.getId()).thenReturn(id);
        runtimeRegistry.registerRuntime(runtime);

        verifyRuntimeIsRegistered(runtime);
        runtimeRegistry.deregisterRuntime(runtimeId);
        verifyRuntimeIsNotRegistered(runtime);
    }

    @Test
    public void getProvider() {
        MockProvider mockProvider = new MockProvider(PROVIDER_ID,
                                                     providerType);

        ProviderId providerId = mock(ProviderId.class);
        when(providerId.getId()).thenReturn(PROVIDER_ID);
        when(providerId.getProviderType()).thenReturn(providerType);

        runtimeRegistry.registerProvider(mockProvider);

        Optional<MockProvider> result = runtimeRegistry.getProvider(providerId,
                                                                    MockProvider.class);
        assertTrue(result.isPresent());
        assertEquals(mockProvider,
                     result.get());
    }

    protected void verifyRuntimeIsRegistered(Runtime runtime) {
        List<Runtime> result = runtimeRegistry.getRuntimes(0,
                                                           1000,
                                                           RuntimeRegistry.RUNTIME_ID_SORT,
                                                           true);
        assertTrue(result.contains(runtime));
    }

    protected void verifyRuntimeIsNotRegistered(Runtime runtime) {
        List<Runtime> result = runtimeRegistry.getRuntimes(0,
                                                           1000,
                                                           RuntimeRegistry.RUNTIME_ID_SORT,
                                                           true);
        assertFalse(result.contains(runtime));
    }

    protected Runtime mockRuntime(ProviderId providerId,
                                  String suffix) {
        Runtime runtime = mock(Runtime.class);
        when(runtime.getId()).thenReturn("Runtime.id." + suffix);
        when(runtime.getName()).thenReturn("Runtime.name." + suffix);
        when(runtime.getProviderId()).thenReturn(providerId);
        return runtime;
    }

    protected List<Runtime> mockRuntimeList(ProviderId providerId,
                                            String suffix,
                                            int count) {
        List<Runtime> runtimes = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            runtimes.add(mockRuntime(providerId,
                                     suffix + Integer.toString(i)));
        }
        return runtimes;
    }

    static class MockProviderConfig implements ProviderConfig {

    }

    static class MockProvider implements Provider<MockProviderConfig> {

        private String id;
        private ProviderType providerType;

        public MockProvider(String id,
                            ProviderType providerType) {
            this.id = id;
            this.providerType = providerType;
        }

        @Override
        public String getId() {
            return id;
        }

        @Override
        public ProviderType getProviderType() {
            return providerType;
        }

        @Override
        public MockProviderConfig getConfig() {
            return mock(MockProviderConfig.class);
        }
    }
}
