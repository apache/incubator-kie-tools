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
public class ProviderConverterFactoryImplTest {

    private static final String ERROR_MESSAGE = "ERROR_MESSAGE";

    @Mock
    private BackendProviderHandlerRegistry handlerRegistry;

    @Mock
    private BackendProviderHandler providerHandler;

    @Mock
    private ProviderConfigConverter configConverter;

    private ProviderConverterFactoryImpl converterFactory;

    @Mock
    private ProviderTypeKey providerTypeKey;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setUp() {
        converterFactory = new ProviderConverterFactoryImpl(handlerRegistry);
    }

    @Test
    public void testGetProviderConfigConverterWhenHandlerConfigured() {
        when(handlerRegistry.ensureHandler(providerTypeKey)).thenReturn(providerHandler);
        when(providerHandler.getProviderConfigConverter()).thenReturn(configConverter);

        assertEquals(configConverter,
                     converterFactory.getProviderConfigConverter(providerTypeKey));
    }

    @Test
    public void testGetProviderConfigConverterWhenHandlerNotConfigured() {
        when(handlerRegistry.ensureHandler(providerTypeKey)).thenThrow(new RuntimeException(ERROR_MESSAGE));

        expectedException.expectMessage(ERROR_MESSAGE);
        converterFactory.getProviderConfigConverter(providerTypeKey);
    }

    @Test
    public void testGetProviderConverter() {
        assertNotNull(converterFactory.getProviderConverter());
    }
}
