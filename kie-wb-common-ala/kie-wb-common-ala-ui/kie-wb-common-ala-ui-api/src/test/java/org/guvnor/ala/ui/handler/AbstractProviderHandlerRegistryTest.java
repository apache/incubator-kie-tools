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

package org.guvnor.ala.ui.handler;

import java.util.ArrayList;
import java.util.List;
import javax.enterprise.inject.Instance;

import org.guvnor.ala.ui.model.ProviderTypeKey;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public abstract class AbstractProviderHandlerRegistryTest<T extends ProviderHandler> {

    protected static final int COUNT = 5;

    protected AbstractProviderHandlerRegistry<T> handlerRegistry;

    protected List<T> expectedHandlers;

    protected List<ProviderTypeKey> expectedKeys;

    @Mock
    protected Instance<T> handlerInstance;

    @Before
    public void setUp() {
        expectedKeys = createExpectedKeys();
        expectedHandlers = createExpectedHandlers();
        for (int i = 0; i < COUNT; i++) {
            when(expectedHandlers.get(i).acceptProviderType(expectedKeys.get(i))).thenReturn(true);
        }
        when(handlerInstance.iterator()).thenReturn(expectedHandlers.iterator());
        handlerRegistry = createHandlerRegistry(handlerInstance);
        verify(handlerInstance,
               times(1)).iterator();
    }

    @Test
    public void testProvidersInstalled() {
        expectedKeys.forEach(key -> assertTrue(handlerRegistry.isProviderInstalled(key)));
    }

    @Test
    public void testProviderNotInstalled() {
        ProviderTypeKey providerTypeKey = mock(ProviderTypeKey.class);
        assertFalse(handlerRegistry.isProviderInstalled(providerTypeKey));
    }

    @Test
    public void testGetProvidersHandler() {
        for (int i = 0; i < expectedKeys.size(); i++) {
            ProviderTypeKey key = expectedKeys.get(i);
            assertNotNull(handlerRegistry.isProviderInstalled(key));
            ProviderHandler handler = handlerRegistry.getProviderHandler(key);
            assertEquals(expectedHandlers.get(i),
                         handler);
        }
    }

    @Test
    public void testGetProviderHandlerMissing() {
        ProviderTypeKey providerTypeKey = mock(ProviderTypeKey.class);
        assertNull(handlerRegistry.getProviderHandler(providerTypeKey));
    }

    protected List<T> createExpectedHandlers() {
        List<T> handlers = new ArrayList<>();
        for (int i = 0; i < COUNT; i++) {
            T handler = mockHandler();
            when(handler.getPriority()).thenReturn(1);
            handlers.add(handler);
        }
        return handlers;
    }

    protected List<ProviderTypeKey> createExpectedKeys() {
        List<ProviderTypeKey> keys = new ArrayList<>();
        for (int i = 0; i < COUNT; i++) {
            ProviderTypeKey key = mock(ProviderTypeKey.class);
            keys.add(key);
        }
        return keys;
    }

    protected abstract AbstractProviderHandlerRegistry<T> createHandlerRegistry(Instance<T> handlerInstance);

    protected abstract T mockHandler();
}
