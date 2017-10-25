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

package org.guvnor.ala.ui.backend.service.converter.impl;

import org.guvnor.ala.ui.backend.service.converter.ProviderConfigConverter;
import org.guvnor.ala.ui.backend.service.handler.BackendProviderHandler;
import org.guvnor.ala.ui.backend.service.handler.BackendProviderHandlerRegistry;
import org.guvnor.ala.ui.model.Provider;
import org.guvnor.ala.ui.model.ProviderConfiguration;
import org.guvnor.ala.ui.model.ProviderKey;
import org.guvnor.ala.ui.model.ProviderTypeKey;
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
public class ProviderConverterImplTest {

    private static final String PROVIDER_ID = "PROVIDER_ID";

    private static final String PROVIDER_TYPE_NAME = "PROVIDER_TYPE_NAME";

    private static final String PROVIDER_VERSION = "PROVIDER_VERSION";

    private static final String ERROR_MESSAGE = "ERROR_MESSAGE";

    @Mock
    private BackendProviderHandlerRegistry handlerRegistry;

    private ProviderConverterImpl converter;

    @Mock
    private BackendProviderHandler providerHandler;

    @Mock
    private ProviderConfigConverter providerConfigConverter;

    @Mock
    private org.guvnor.ala.runtime.providers.Provider provider;

    @Mock
    private org.guvnor.ala.runtime.providers.ProviderType providerType;

    @Mock
    private org.guvnor.ala.config.ProviderConfig providerConfig;

    private ProviderKey providerKey;

    private ProviderTypeKey providerTypeKey;

    @Mock
    private ProviderConfiguration providerConfiguration;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setUp() {
        when(providerType.getProviderTypeName()).thenReturn(PROVIDER_TYPE_NAME);
        when(providerType.getVersion()).thenReturn(PROVIDER_VERSION);
        when(provider.getId()).thenReturn(PROVIDER_ID);
        when(provider.getProviderType()).thenReturn(providerType);
        when(provider.getConfig()).thenReturn(providerConfig);

        providerTypeKey = new ProviderTypeKey(PROVIDER_TYPE_NAME,
                                              PROVIDER_VERSION);
        providerKey = new ProviderKey(providerTypeKey,
                                      PROVIDER_ID);

        converter = new ProviderConverterImpl(handlerRegistry);
    }

    @Test
    public void testToModelWhenHandlerNotConfigured() {
        when(handlerRegistry.ensureHandler(providerTypeKey)).thenThrow(new RuntimeException(ERROR_MESSAGE));
        expectedException.expectMessage(ERROR_MESSAGE);
        converter.toModel(provider);
    }

    @Test
    public void testToModelWhenHandlerIsConfigured() {
        when(handlerRegistry.ensureHandler(providerTypeKey)).thenReturn(providerHandler);
        when(providerHandler.getProviderConfigConverter()).thenReturn(providerConfigConverter);
        when(providerConfigConverter.toModel(providerConfig)).thenReturn(providerConfiguration);

        Provider result = converter.toModel(provider);

        assertNotNull(result);
        assertEquals(PROVIDER_ID,
                     result.getKey().getId());
        assertEquals(PROVIDER_TYPE_NAME,
                     result.getKey().getProviderTypeKey().getId());
        assertEquals(PROVIDER_VERSION,
                     result.getKey().getProviderTypeKey().getVersion());
        assertEquals(result.getConfiguration(),
                     providerConfiguration);
    }
}
