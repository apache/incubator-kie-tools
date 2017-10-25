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

package org.guvnor.ala.services.backend.impl;

import java.util.List;

import org.guvnor.ala.config.ProviderConfig;
import org.guvnor.ala.config.RuntimeConfig;
import org.guvnor.ala.runtime.Runtime;
import org.guvnor.ala.runtime.providers.Provider;
import org.guvnor.ala.runtime.providers.ProviderType;
import org.guvnor.ala.services.api.RuntimeProvisioningService;
import org.guvnor.ala.services.api.RuntimeQuery;
import org.guvnor.ala.services.api.RuntimeQueryResultItem;
import org.guvnor.ala.services.api.itemlist.ProviderList;
import org.guvnor.ala.services.api.itemlist.ProviderTypeList;
import org.guvnor.ala.services.api.itemlist.RuntimeList;
import org.guvnor.ala.services.api.itemlist.RuntimeQueryResultItemList;
import org.junit.Before;
import org.junit.Test;

import static org.guvnor.ala.AlaSPITestCommons.mockList;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class RuntimeProvisioningServiceBackendImplTest {

    //arbitrary random values
    private static final int ELEMENTS_COUNT = 10;
    private static final Integer PAGE = 1234;
    private static final Integer PAGE_SIZE = 10;
    private static final Boolean SORT_ORDER = Boolean.FALSE;
    private static final String SORT = "SORT";
    private static final String PROVIDER_NAME = "PROVIDER_NAME";
    private static final String RUNTIME_ID = "RUNTIME_ID";

    private RuntimeProvisioningService runtimeProvisioningService;

    private RuntimeProvisioningServiceBackendImpl runtimeProvisioningServiceBackend;

    @Before
    public void setUp() {
        runtimeProvisioningService = mock(RuntimeProvisioningService.class);
        runtimeProvisioningServiceBackend = new RuntimeProvisioningServiceBackendImpl(runtimeProvisioningService);
    }

    @Test
    public void testGetProviderTypes() {
        List<ProviderType> values = mockList(ProviderType.class,
                                             ELEMENTS_COUNT);
        ProviderTypeList list = new ProviderTypeList(values);
        when(runtimeProvisioningService.getProviderTypes(PAGE,
                                                         PAGE_SIZE,
                                                         SORT,
                                                         SORT_ORDER)).thenReturn(list);
        List<ProviderType> result = runtimeProvisioningServiceBackend.getProviderTypes(PAGE,
                                                                                       PAGE_SIZE,
                                                                                       SORT,
                                                                                       SORT_ORDER);
        verify(runtimeProvisioningService,
               times(1)).getProviderTypes(PAGE,
                                          PAGE_SIZE,
                                          SORT,
                                          SORT_ORDER);
        assertEquals(values,
                     result);
    }

    @Test
    public void testGetProviders() {
        List<Provider> values = mockList(Provider.class,
                                         ELEMENTS_COUNT);
        ProviderList list = new ProviderList(values);
        when(runtimeProvisioningService.getProviders(PAGE,
                                                     PAGE_SIZE,
                                                     SORT,
                                                     SORT_ORDER)).thenReturn(list);
        List<Provider> result = runtimeProvisioningServiceBackend.getProviders(PAGE,
                                                                               PAGE_SIZE,
                                                                               SORT,
                                                                               SORT_ORDER);
        verify(runtimeProvisioningService,
               times(1)).getProviders(PAGE,
                                      PAGE_SIZE,
                                      SORT,
                                      SORT_ORDER);
        assertEquals(values,
                     result);
    }

    @Test
    public void testGetRuntimes() {
        List<Runtime> values = mockList(Runtime.class,
                                        ELEMENTS_COUNT);
        RuntimeList list = new RuntimeList(values);

        when(runtimeProvisioningService.getRuntimes(PAGE,
                                                    PAGE_SIZE,
                                                    SORT,
                                                    SORT_ORDER)).thenReturn(list);

        List<Runtime> result = runtimeProvisioningServiceBackend.getRuntimes(PAGE,
                                                                             PAGE_SIZE,
                                                                             SORT,
                                                                             SORT_ORDER);

        verify(runtimeProvisioningService,
               times(1)).getRuntimes(PAGE,
                                     PAGE_SIZE,
                                     SORT,
                                     SORT_ORDER);
        assertEquals(values,
                     result);
    }

    @Test
    public void testRegisterProvider() {
        ProviderConfig providerConfig = mock(ProviderConfig.class);
        runtimeProvisioningServiceBackend.registerProvider(providerConfig);
        verify(runtimeProvisioningService,
               times(1)).registerProvider(providerConfig);
    }

    @Test
    public void unregisterProvider() {
        runtimeProvisioningServiceBackend.unregisterProvider(PROVIDER_NAME);
        verify(runtimeProvisioningService,
               times(1)).deregisterProvider(PROVIDER_NAME);
    }

    @Test
    public void testNewRuntime() {
        RuntimeConfig runtimeConfig = mock(RuntimeConfig.class);
        when(runtimeProvisioningService.newRuntime(runtimeConfig)).thenReturn(RUNTIME_ID);

        String result = runtimeProvisioningServiceBackend.newRuntime(runtimeConfig);
        verify(runtimeProvisioningService,
               times(1)).newRuntime(runtimeConfig);
        assertEquals(RUNTIME_ID,
                     result);
    }

    @Test
    public void testDestroyRuntimeNotForced() {
        runtimeProvisioningServiceBackend.destroyRuntime(RUNTIME_ID,
                                                         false);
        verify(runtimeProvisioningService,
               times(1)).destroyRuntime(RUNTIME_ID,
                                        false);
    }

    @Test
    public void testDestroyRuntimeForced() {
        runtimeProvisioningServiceBackend.destroyRuntime(RUNTIME_ID,
                                                         true);
        verify(runtimeProvisioningService,
               times(1)).destroyRuntime(RUNTIME_ID,
                                        true);
    }

    @Test
    public void testStartRuntime() {
        runtimeProvisioningServiceBackend.startRuntime(RUNTIME_ID);
        verify(runtimeProvisioningService).startRuntime(RUNTIME_ID);
    }

    @Test
    public void testStopRuntime() {
        runtimeProvisioningServiceBackend.stopRuntime(RUNTIME_ID);
        verify(runtimeProvisioningService).stopRuntime(RUNTIME_ID);
    }

    @Test
    public void testRestartRuntime() {
        runtimeProvisioningServiceBackend.restartRuntime(RUNTIME_ID);
        verify(runtimeProvisioningService).restartRuntime(RUNTIME_ID);
    }

    @Test
    public void testExecuteQuery() {
        RuntimeQuery query = mock(RuntimeQuery.class);
        List<RuntimeQueryResultItem> values = mockList(RuntimeQueryResultItem.class,
                                                       ELEMENTS_COUNT);
        RuntimeQueryResultItemList list = new RuntimeQueryResultItemList(values);
        when(runtimeProvisioningService.executeQuery(query)).thenReturn(list);

        List<RuntimeQueryResultItem> result = runtimeProvisioningServiceBackend.executeQuery(query);
        verify(runtimeProvisioningService,
               times(1)).executeQuery(query);
        assertEquals(values,
                     result);
    }
}
